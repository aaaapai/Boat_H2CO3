#include "h2co3Launcher_internal.h"

#include <android/native_window_jni.h>
#include <jni.h>
#include <android/log.h>
#include <assert.h>

struct H2CO3LauncherInternal *h2co3Launcher = NULL;

__attribute__((constructor)) void env_init() {
    char *strptr_env = getenv("H2CO3LAUNCHER_ENVIRON");
    if (strptr_env == NULL) {
        __android_log_print(ANDROID_LOG_INFO, "Environ", "No environ found, creating...");
        h2co3Launcher = (struct H2CO3LauncherInternal *)malloc(sizeof(struct H2CO3LauncherInternal));
        if (h2co3Launcher == NULL) {
            abort();
        }
        memset(h2co3Launcher, 0, sizeof(struct H2CO3LauncherInternal));
        char str_h2co3Launcher[20];
        sprintf(str_h2co3Launcher, "%p", h2co3Launcher);
        setenv("H2CO3LAUNCHER_ENVIRON", str_h2co3Launcher, 1);
    } else {
        __android_log_print(ANDROID_LOG_INFO, "Environ", "Found existing environ: %s", strptr_env);
        h2co3Launcher = (struct H2CO3LauncherInternal *)strtoull(strptr_env, NULL, 16);
    }
    __android_log_print(ANDROID_LOG_INFO, "Environ", "%p", h2co3Launcher);
}

ANativeWindow *h2co3LauncherGetNativeWindow() {
    if (h2co3Launcher == NULL) {
        return NULL;
    }
    return h2co3Launcher->window;
}


void h2co3LauncherSetPrimaryClipString(const char *string) {
    PrepareH2CO3LauncherBridgeJNI();
    CallH2CO3LauncherBridgeJNIFunc(, Void, setPrimaryClipString, "(Ljava/lang/String;)V",
                                     (*env)->NewStringUTF(env, string));
}

const char *h2co3LauncherGetPrimaryClipString() {
    if (h2co3Launcher->clipboard_string != NULL) {
        return h2co3Launcher->clipboard_string;
    }

    PrepareH2CO3LauncherBridgeJNI();

    CallH2CO3LauncherBridgeJNIFunc(jstring clipstr =, Object, getPrimaryClipString,
                                   "()Ljava/lang/String;");
    const char *string = NULL;
    if (clipstr != NULL) {
        string = (*env)->GetStringUTFChars(env, clipstr, NULL);
        if (string != NULL) {
            h2co3Launcher->clipboard_string = strdup(string);
            (*env)->ReleaseStringUTFChars(env, clipstr, string);
        }
    }
    return h2co3Launcher->clipboard_string;
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_core_h2co3launcher_utils_H2CO3LauncherBridge_setH2CO3LauncherNativeWindow(
        JNIEnv *env, jclass clazz, jobject surface) {
    h2co3Launcher->window = ANativeWindow_fromSurface(env, surface);
    H2CO3_INTERNAL_LOG("setH2CO3LauncherNativeWindow : %p, size : %dx%d", h2co3Launcher->window,
                       ANativeWindow_getWidth(h2co3Launcher->window),
                       ANativeWindow_getHeight(h2co3Launcher->window));
}

JNIEXPORT void JNICALL
Java_org_koishi_launcher_h2co3_core_h2co3launcher_utils_H2CO3LauncherBridge_setH2CO3LauncherBridge(
        JNIEnv *env, jobject thiz, jobject h2co3Launcher_bridge) {
    h2co3Launcher->object_H2CO3LauncherBridge = (jclass) (*env)->NewGlobalRef(env, thiz);
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    env_init();
    if (vm == NULL) {
        H2CO3_INTERNAL_LOG("JavaVM is NULL.");
        return JNI_ERR;
    }

    JNIEnv *env = NULL;
    jint result = (*vm)->AttachCurrentThread(vm, &env, NULL);
    if (result != JNI_OK || env == NULL) {
        H2CO3_INTERNAL_LOG("Failed to attach thread to JavaVM.");
        return JNI_ERR;
    }

    jclass class_H2CO3LauncherBridge = (*env)->FindClass(env,
                                                         "org/koishi/launcher/h2co3/core/h2co3launcher/utils/H2CO3LauncherBridge");
    if (class_H2CO3LauncherBridge == NULL) {
        H2CO3_INTERNAL_LOG(
                "Failed to find class: org/koishi/launcher/h2co3/core/h2co3launcher/utils/H2CO3LauncherBridge.");
        return JNI_ERR;
    }

    h2co3Launcher->android_jvm = vm;
    h2co3Launcher->class_H2CO3LauncherBridge = (*env)->NewGlobalRef(env, class_H2CO3LauncherBridge);

    return JNI_VERSION_1_2;
}
