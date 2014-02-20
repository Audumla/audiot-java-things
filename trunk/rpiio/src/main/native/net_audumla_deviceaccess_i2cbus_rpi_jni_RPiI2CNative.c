#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdint.h>
#include <errno.h>
#include <string.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include "net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative.h"
/*
 * Class:     net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative
 * Method:    open
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_open
  (JNIEnv *env, jclass clazz, jstring deviceName, jint address) {
   char device[256];
   int len = (*env)->GetStringLength(env, deviceName);
   (*env)->GetStringUTFRegion(env, deviceName, 0, len, device);

   int fd ;

   if ((fd = open (device, O_RDWR)) < 0)
        return -1;

   if (ioctl (fd, I2C_SLAVE, address) < 0)
        return -2;

   return fd ;
 };

/*
 * Class:     net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative
 * Method:    close
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_close
  (JNIEnv *env, jclass clazz, jint fd) {
    return close(fd);
};

/*
 * Class:     net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative
 * Method:    write
 * Signature: (III[BB)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__III_3BB
  (JNIEnv *env, jclass clazz, jint fd, jint offset, jint writeCount, jbyteArray data, jbyte mask) {
    ssize_t returnValue;
    jbyte *body = (*env)->GetPrimitiveArrayCritical(env, bytes, 0);
    if (mask != 0xff) {
        uint8_t currentData;
        if (returnValue = read(fd,&currentData,1)) {
            uint8_t maskedData[writeCount];
            for (int i =0; i < writeCount; ++i) maskedData[i] = (data[i+offset] & mask) | (currentData & ~mask);
            returnValue = write(fd,maskedData,writeCount);
        }
    }
    else {
        returnValue = write(fd,data+offset,writeCount);
    }
    (*env)->ReleasePrimitiveArrayCritical(env, bytes, body, 0);
    return returnValue;
};

/*
 * Class:     net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative
 * Method:    write
 * Signature: (IIIII[B[B)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIIII_3B_3B
  (JNIEnv *env, jclass clazz, jint fd, jint localAddress, jint offset, jint width, jint readCount, jbyteArray data, jbyteArray mask) {
};

/*
 * Class:     net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative
 * Method:    read
 * Signature: (III[BB)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__III_3BB
  (JNIEnv *env, jclass clazz, jint fd, jint offset, jint readCount, jbyteArray data, jbyte mask) {
};

/*
 * Class:     net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative
 * Method:    read
 * Signature: (IIIII[B[B)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__IIIII_3B_3B
  (JNIEnv *env, jclass clazz, jint fd, jint localAddress, jint offset, jint width, jint readCount, jbyteArray data, jbyteArray mask) {
};

