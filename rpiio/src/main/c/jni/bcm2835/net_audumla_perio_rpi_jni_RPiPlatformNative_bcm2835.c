#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdint.h>
#include <errno.h>
#include <string.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include "net_audumla_perio_rpi_jni_RPiPlatformNative.h"
#include "bcm2835.h"

JNIEXPORT jint JNICALL Java_net_audumla_perio_rpi_jni_RPiPlatformNative_init(JNIEnv *env, jclass clazz) {
    return bcm2835_init();
}

JNIEXPORT void JNICALL Java_net_audumla_perio_rpi_jni_RPiPlatformNative_shutdown(JNIEnv *env, jclass clazz) {
    bcm2835_close();
}

JNIEXPORT jint JNICALL Java_net_audumla_perio_rpi_jni_RPiPlatformNative_getRevision(JNIEnv *env, jclass clazz) {
    return getHardwareRevision();
}
