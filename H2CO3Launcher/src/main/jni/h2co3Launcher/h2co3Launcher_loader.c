#include <fcntl.h>
#include <unistd.h>
#include <jni.h>
#include <dlfcn.h>
#include <stdlib.h>
#include <android/log.h>
#include <xhook.h>
#include <string.h>
#include <sys/mman.h>
#include <pthread.h>
#include "h2co3Launcher_internal.h"

#define FULL_VERSION "1.8.0-internal"
#define DOT_VERSION "1.8"
#define PROGNAME "java"
#define LAUNCHER_NAME "openjdk"

static char *const_progname = PROGNAME;
static const char *const_launcher = LAUNCHER_NAME;
static const char **const_jargs = NULL;
static const char **const_appclasspath = NULL;
static const jboolean const_cpwildcard = JNI_TRUE;
static const jboolean const_javaw = JNI_FALSE;
static const jint const_ergo_class = 0;    //DEFAULT_POLICY

typedef void (*android_update_LD_LIBRARY_PATH_t)(const char *);
static volatile jobject exitTrap_bridge;
static volatile jmethodID exitTrap_method;
static JavaVM *exitTrap_jvm;
static volatile jmethodID log_method;
static JavaVM *log_pipe_jvm;
static int h2co3LauncherFd[2];
static pthread_t logger;

void correctUtfBytes(char *bytes) {
    char *currentByte = bytes;
    while (*currentByte != '\0') {
        unsigned char utf8 = *(currentByte++);
        // Switch on the high four bits.
        switch (utf8 >> 4) {
            case 0x00:
            case 0x01:
            case 0x02:
            case 0x03:
            case 0x04:
            case 0x05:
            case 0x06:
            case 0x07:
                // Bit pattern 0xxx. No need for any extra bytes.
                break;
            case 0x08:
            case 0x09:
            case 0x0a:
            case 0x0b:
            case 0x0f:
                /*
                 * Bit pattern 10xx or 1111, which are illegal start bytes.
                 * Note: 1111 is valid for normal UTF-8, but not the
                 * modified UTF-8 used here.
                 */
                *(currentByte - 1) = '?';
                break;
            case 0x0e:
                // Bit pattern 1110, so there are two additional bytes.
                utf8 = *(currentByte++);
                if ((utf8 & 0xc0) != 0x80) {
                    --currentByte;
                    *(currentByte - 1) = '?';
                    break;
                }
                // Fall through to take care of the final byte.
            case 0x0c:
            case 0x0d:
                // Bit pattern 110x, so there is one additional byte.
                utf8 = *(currentByte++);
                if ((utf8 & 0xc0) != 0x80) {
                    --currentByte;
                    *(currentByte - 1) = '?';
                }
                break;
        }
    }
}


static void *logger_thread() {
    JNIEnv *env;
    JavaVM *vm = h2co3Launcher->android_jvm;
    (*vm)->AttachCurrentThread(vm, &env, NULL);
    char buffer[2048];
    ssize_t _s;
    jstring str = NULL;
    while (1) {
        memset(buffer, '\0', sizeof(buffer));
        _s = read(h2co3LauncherFd[0], buffer, sizeof(buffer) - 1);
        if (_s < 0) {
            __android_log_print(ANDROID_LOG_ERROR, "H2CO3", "Failed to read log!");
            close(h2co3LauncherFd[0]);
            close(h2co3LauncherFd[1]);
            (*vm)->DetachCurrentThread(vm);
            return NULL;
        } else if (_s > 0) {
            buffer[_s] = '\0';
            correctUtfBytes(buffer);
            if (str != NULL) {
                (*env)->DeleteLocalRef(env, str);
            }
            str = (*env)->NewStringUTF(env, buffer);
            (*env)->CallVoidMethod(env, h2co3Launcher->object_H2CO3LauncherBridge, log_method, str);
        }
    }
}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3_core_h2co3launcher_utils_H2CO3LauncherBridge_redirectStdio(
        JNIEnv *env, jobject jobject, jstring path) {
    setvbuf(stdout, 0, _IOLBF, 0);
    setvbuf(stderr, 0, _IONBF, 0);

    if (pipe(h2co3LauncherFd) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "H2CO3", "Failed to create log pipe!");
        return 1;
    }

    if (dup2(h2co3LauncherFd[1], STDOUT_FILENO) != STDOUT_FILENO ||
        dup2(h2co3LauncherFd[1], STDERR_FILENO) != STDERR_FILENO) {
        __android_log_print(ANDROID_LOG_ERROR, "H2CO3", "failed to redirect stdio!");
        return 2;
    }

    jclass bridge = (*env)->FindClass(env, "org/koishi/launcher/h2co3/core/h2co3launcher/utils/H2CO3LauncherBridge");
    log_method = (*env)->GetMethodID(env, bridge, "receiveLog", "(Ljava/lang/String;)V");
    if (!log_method) {
        __android_log_print(ANDROID_LOG_ERROR, "H2CO3", "Failed to find receive method!");
        return 4;
    }

    h2co3Launcher->logFile = fdopen(h2co3LauncherFd[1], "a");
    H2CO3_INTERNAL_LOG("Log pipe ready.");

    (*env)->GetJavaVM(env, &log_pipe_jvm);

    int result = pthread_create(&logger, NULL, logger_thread, NULL);
    if (result != 0) {
        return 5;
    }

    pthread_detach(logger);

    return 0;
}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3_core_h2co3launcher_utils_H2CO3LauncherBridge_chdir(JNIEnv *env,
                                                                        jobject jobject,
                                                                        jstring path) {
    const char *dir = (*env)->GetStringUTFChars(env, path, 0);
    if (dir == NULL) {
        return -1;
    }

    int b = chdir(dir);

    (*env)->ReleaseStringUTFChars(env, path, dir);
    return b;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_core_h2co3launcher_utils_H2CO3LauncherBridge_setenv(JNIEnv *env,
                                                                         jobject jobject,
                                                                         jstring str1,
                                                                         jstring str2) {
    const char *name = (*env)->GetStringUTFChars(env, str1, 0);
    const char *value = (*env)->GetStringUTFChars(env, str2, 0);

    setenv(name, value, 1);

    (*env)->ReleaseStringUTFChars(env, str1, name);
    (*env)->ReleaseStringUTFChars(env, str2, value);
}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3_core_h2co3launcher_utils_H2CO3LauncherBridge_dlopen(JNIEnv *env,
                                                                         jobject jobject,
                                                                         jstring str) {
    int ret = 0;
    char const *lib_name = (*env)->GetStringUTFChars(env, str, 0);

    void *handle = dlopen(lib_name, RTLD_GLOBAL | RTLD_LAZY);
    char *error = dlerror();

    __android_log_print(error == NULL ? ANDROID_LOG_INFO : ANDROID_LOG_ERROR, "H2CO3",
                        "loading %s (error = %s)", lib_name, error);

    if (handle == NULL) {
        ret = -1;
    }

    (*env)->ReleaseStringUTFChars(env, str, lib_name);
    return ret;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_core_h2co3launcher_utils_H2CO3LauncherBridge_setLdLibraryPath(
        JNIEnv *env,
                                                                                   jobject jobject,
                                                                                   jstring ldLibraryPath) {
    static android_update_LD_LIBRARY_PATH_t android_update_LD_LIBRARY_PATH = NULL;
    static void *libdl_handle = NULL;

    if (libdl_handle == NULL) {
        libdl_handle = dlopen("libdl.so", RTLD_LAZY);
        if (libdl_handle == NULL) {
            return;
        }
    }

    if (android_update_LD_LIBRARY_PATH == NULL) {
        void *updateLdLibPath = dlsym(libdl_handle, "android_update_LD_LIBRARY_PATH");
        if (updateLdLibPath == NULL) {
            updateLdLibPath = dlsym(libdl_handle, "__loader_android_update_LD_LIBRARY_PATH");
            char *error = dlerror();
            __android_log_print(error == NULL ? ANDROID_LOG_INFO : ANDROID_LOG_ERROR, "H2CO3",
                                "loading %s (error = %s)", "libdl.so", error);
            if (updateLdLibPath == NULL) {
                return;
            }
        }
        android_update_LD_LIBRARY_PATH = (android_update_LD_LIBRARY_PATH_t) updateLdLibPath;
    }

    const char *ldLibPathUtf = (*env)->GetStringUTFChars(env, ldLibraryPath, 0);
    android_update_LD_LIBRARY_PATH(ldLibPathUtf);
    (*env)->ReleaseStringUTFChars(env, ldLibraryPath, ldLibPathUtf);
}

void (*old_exit)(int code);
void custom_exit(int code) {
    __android_log_print(code == 0 ? ANDROID_LOG_INFO : ANDROID_LOG_ERROR, "H2CO3",
                        "JVM exit with code %d.", code);
    JNIEnv *env;
    (*exitTrap_jvm)->AttachCurrentThread(exitTrap_jvm, &env, NULL);
    (*env)->CallVoidMethod(env, exitTrap_bridge, exitTrap_method, code);
    (*env)->DeleteGlobalRef(env, exitTrap_bridge);
    (*exitTrap_jvm)->DetachCurrentThread(exitTrap_jvm);
    old_exit(code);
}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3_core_h2co3launcher_utils_H2CO3LauncherBridge_setupExitTrap(
        JNIEnv *env,
                                                                                jobject jobject1,
                                                                                jobject bridge) {
    exitTrap_bridge = (*env)->NewGlobalRef(env, bridge);
    (*env)->GetJavaVM(env, &exitTrap_jvm);
    jclass exitTrap_exitClass = (*env)->NewGlobalRef(env, (*env)->FindClass(env,
                                                                            "org/koishi/launcher/h2co3/core/h2co3launcher/utils/H2CO3LauncherBridge"));
    exitTrap_method = (*env)->GetMethodID(env, exitTrap_exitClass, "onExit", "(I)V");
    (*env)->DeleteGlobalRef(env, exitTrap_exitClass);
    // Enable xhook debug mode here
    // xhook_enable_debug(1);
    xhook_register(".*\\.so$", "exit", custom_exit, (void **) &old_exit);
    return xhook_refresh(1);
}

int
(*JLI_Launch)(int argc, char **argv,              /* main argc, argc */
              int jargc, const char **jargv,          /* java args */
              int appclassc, const char **appclassv,  /* app classpath */
              const char *fullversion,                /* full version defined */
              const char *dotversion,                 /* dot version defined */
              const char *pname,                      /* program name */
              const char *lname,                      /* launcher name */
              jboolean javaargs,                      /* JAVA_ARGS */
              jboolean cpwildcard,                    /* classpath wildcard */
              jboolean javaw,                         /* windows-only javaw */
              jint ergo_class                     /* ergnomics policy */
);

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_core_h2co3launcher_utils_H2CO3LauncherBridge_setupJLI(JNIEnv *env,
                                                                           jobject jobject) {

    void *handle;
    handle = dlopen("libjli.so", RTLD_LAZY | RTLD_GLOBAL);
    JLI_Launch = (int (*)(int, char **, int, const char **, int, const char **, const char *,
                          const char *, const char *, const char *, jboolean, jboolean, jboolean,
                          jint)) dlsym(handle, "JLI_Launch");

}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3_core_h2co3launcher_utils_H2CO3LauncherBridge_jliLaunch(JNIEnv *env,
                                                                            jobject jobject,
                                                                            jobjectArray argsArray) {
    int argc = (*env)->GetArrayLength(env, argsArray);
    char **argv = (char **) malloc(argc * sizeof(char *));
    for (int i = 0; i < argc; i++) {
        jstring str = (*env)->GetObjectArrayElement(env, argsArray, i);
        const char *utfString = (*env)->GetStringUTFChars(env, str, NULL);
        int len = strlen(utfString);
        argv[i] = (char *) malloc(len + 1);
        strcpy(argv[i], utfString);
        (*env)->ReleaseStringUTFChars(env, str, utfString);
    }

    return JLI_Launch(argc, argv,
                      sizeof(const_jargs) / sizeof(char *), const_jargs,
                      sizeof(const_appclasspath) / sizeof(char *), const_appclasspath,
                      FULL_VERSION,
                      DOT_VERSION,
                      (const_progname != NULL) ? const_progname : *argv,
                      (const_launcher != NULL) ? const_launcher : *argv,
                      (const_jargs != NULL) ? JNI_TRUE : JNI_FALSE,
                      const_cpwildcard, const_javaw, const_ergo_class);

}