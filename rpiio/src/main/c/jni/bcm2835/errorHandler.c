#include <jni.h>
#include "errorHAndler.h"

void handleError(JNIEnv *env, jobject *handler, int errorCode, char *message, char* nativeMethod) {
    jclass cls = (*env)->GetObjectClass(env, obj);
    jmethodID mid = (*env)->GetMethodID(env, cls, "handleError", "(ILjava/lang/String;Ljava/lang/String;)V");
    if (mid == 0)
        return;
    jstring ec = env->NewStringUTF(errorCode);
    jstring msg = env->NewStringUTF(message);
    jstring nm = env->NewStringUTF(nativeMethod);
    (*env)->CallVoidMethod(env, obj, mid, ec, msg,nm);
    env->DeleteLocalRef(ec);
    env->DeleteLocalRef(msg);
    env->DeleteLocalRef(nm);
}

