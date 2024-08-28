#include <android/log.h>
#include <dlfcn.h>
#include <errno.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <string.h>

#include "h2co3Launcher_internal.h"

#define FULL_VERSION "1.8.0-internal"
#define DOT_VERSION "1.8"

__attribute__((unused)) static const char* const_progname = "java";
__attribute__((unused)) static const char* const_launcher = "openjdk";
static const char** const_jargs = NULL;
__attribute__((unused)) static const char** const_appclasspath = NULL;
static const jboolean const_javaw = JNI_FALSE;
static const jboolean const_cpwildcard = JNI_TRUE;
static const jint const_ergo_class = 0;
static struct sigaction old_sa[NSIG];

void (*__old_sa)(int signal, siginfo_t *info, void *reserved);
int (*JVM_handle_linux_signal)(int signo, siginfo_t* siginfo, void* ucontext, int abort_if_unrecognized);

void android_sigaction(int signal, siginfo_t *info, void *reserved) {
    printf("process killed with signal %d code %p addr %p\n", signal, info->si_code, info->si_addr);
    if (JVM_handle_linux_signal == NULL) {
        __old_sa = old_sa[signal].sa_sigaction;
        __old_sa(signal, info, reserved);
        exit(1);
    } else {
        int orig_errno = errno;
        JVM_handle_linux_signal(signal, info, reserved, true);
        errno = orig_errno;
    }
}

__attribute__((unused)) typedef jint JNI_CreateJavaVM_func(JavaVM **pvm, void **penv, void *args);
typedef jint JLI_Launch_func(int argc, char ** argv, int jargc, const char** jargv, int appclassc, const char** appclassv, const char* fullversion, const char* dotversion, const char* pname, const char* lname, jboolean javaargs, jboolean cpwildcard, jboolean javaw, jint ergo);

static jint launchJVM(int margc, char** margv) {
    void* libjli = dlopen("libjli.so", RTLD_LAZY | RTLD_GLOBAL);
    if (libjli == NULL) {
        H2CO3_INTERNAL_LOG("JLI lib = NULL: %s", dlerror());
        return -1;
    }
    H2CO3_INTERNAL_LOG("Found JLI lib");

    JLI_Launch_func *pJLI_Launch = (JLI_Launch_func *)dlsym(libjli, "JLI_Launch");
    if (pJLI_Launch == NULL) {
        H2CO3_INTERNAL_LOG("JLI_Launch = NULL");
        dlclose(libjli);
        return -1;
    }

    H2CO3_INTERNAL_LOG("Calling JLI_Launch");
    return pJLI_Launch(margc, margv, 0, NULL, 0, NULL, FULL_VERSION, DOT_VERSION, margv[0], margv[0], (const_jargs != NULL) ? JNI_TRUE : JNI_FALSE, const_cpwildcard, const_javaw, const_ergo_class);
}

char** convert_to_char_array(JNIEnv *env, jobjectArray jstringArray) {
    int num_rows = (*env)->GetArrayLength(env, jstringArray);
    char **cArray = (char **) malloc(num_rows * sizeof(char*));
    if (cArray == NULL) return NULL; // Check malloc result

    for (int i = 0; i < num_rows; i++) {
        jstring row = (jstring)(*env)->GetObjectArrayElement(env, jstringArray, i);
        if (row == NULL) {
            free(cArray); // Free allocated memory on error
            return NULL;
        }
        cArray[i] = (char*)(*env)->GetStringUTFChars(env, row, 0);
        (*env)->DeleteLocalRef(env, row); // Clean up local reference
        if (cArray[i] == NULL) {
            free(cArray); // Free allocated memory on error
            return NULL;
        }
    }

    return cArray;
}

void free_char_array(JNIEnv *env, jobjectArray jstringArray, char **charArray) {
    if (charArray == NULL) return; // Check for NULL
    int num_rows = (*env)->GetArrayLength(env, jstringArray);

    for (int i = 0; i < num_rows; i++) {
        jstring row = (jstring)(*env)->GetObjectArrayElement(env, jstringArray, i);
        if (row != NULL) {
            (*env)->ReleaseStringUTFChars(env, row, charArray[i]);
            (*env)->DeleteLocalRef(env, row); // Clean up local reference
        }
    }
    free(charArray); // Free the allocated char array
}

void setup_signal_handler(int signal, struct sigaction *catcher) {
    if (sigaction(signal, catcher, &old_sa[signal]) != 0) {
        perror("sigaction failed");
    }
}

jint JNICALL Java_org_koishi_launcher_h2co3_core_launch_H2CO3JVMLauncher_launchJVM(JNIEnv *env, jclass clazz, jobjectArray argsArray) {
#ifdef TRY_SIG2JVM
    void* libjvm = dlopen("libjvm.so", RTLD_LAZY | RTLD_GLOBAL);
    if (libjvm == NULL) {
        LOGE("JVM lib = NULL: %s", dlerror());
        return -1;
    }
    JVM_handle_linux_signal = dlsym(libjvm, "JVM_handle_linux_signal");
#endif

    jint res = 0;
    struct sigaction catcher;
    memset(&catcher, 0, sizeof(sigaction));
    catcher.sa_sigaction = android_sigaction;
    catcher.sa_flags = SA_SIGINFO | SA_RESTART;

    setup_signal_handler(SIGILL, &catcher);
    setup_signal_handler(SIGBUS, &catcher);
    setup_signal_handler(SIGFPE, &catcher);
#ifdef TRY_SIG2JVM
    setup_signal_handler(SIGSEGV, &catcher);
#endif
    setup_signal_handler(SIGSTKFLT, &catcher);
    setup_signal_handler(SIGPIPE, &catcher);
    setup_signal_handler(SIGXFSZ, &catcher);

    if (argsArray == NULL) {
        H2CO3_INTERNAL_LOG("Args array null, returning");
        return 0;
    }

    int argc = (*env)->GetArrayLength(env, argsArray);
    char **argv = convert_to_char_array(env, argsArray);
    if (argv == NULL) {
        H2CO3_INTERNAL_LOG("Failed to convert args");
        return -1; // Handle conversion error
    }

    H2CO3_INTERNAL_LOG("Done processing args");

    res = launchJVM(argc, argv);

    H2CO3_INTERNAL_LOG("Going to free args");
    free_char_array(env, argsArray, argv);

    H2CO3_INTERNAL_LOG("Free done");

    return res;
}