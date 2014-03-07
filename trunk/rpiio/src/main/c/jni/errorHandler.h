#ifndef errorHandler.h
#define errorHandler.h
#ifdef __cplusplus
extern "C" {
#endif

void handleError(JNIEnv *env, jobject *handler, int errorCode, char *message, char* nativeMethod);

#ifdef __cplusplus
}
#endif
#endif
