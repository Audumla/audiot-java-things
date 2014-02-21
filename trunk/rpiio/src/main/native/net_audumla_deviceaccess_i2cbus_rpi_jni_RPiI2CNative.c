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

// I2C definitions

#define I2C_SLAVE	0x0703
#define I2C_SMBUS	0x0720	/* SMBus-level access */

#define I2C_SMBUS_READ	1
#define I2C_SMBUS_WRITE	0

// SMBus transaction types

#define I2C_SMBUS_QUICK		    0
#define I2C_SMBUS_BYTE		    1
#define I2C_SMBUS_BYTE_DATA	    2
#define I2C_SMBUS_WORD_DATA	    3
#define I2C_SMBUS_PROC_CALL	    4
#define I2C_SMBUS_BLOCK_DATA	    5
#define I2C_SMBUS_I2C_BLOCK_BROKEN  6
#define I2C_SMBUS_BLOCK_PROC_CALL   7		/* SMBus 2.0 */
#define I2C_SMBUS_I2C_BLOCK_DATA    8

// SMBus messages

#define I2C_SMBUS_BLOCK_MAX	32	/* As specified in SMBus standard */
#define I2C_SMBUS_I2C_BLOCK_MAX	32	/* Not specified but we use same structure */

// Structures used in the ioctl() calls

union i2c_smbus_data
{
    uint8_t  byte ;
    uint16_t word ;
    uint8_t  block [I2C_SMBUS_BLOCK_MAX + 2] ;	// block [0] is used for length + one more for PEC
};

struct i2c_smbus_ioctl_data
{
    char read_write ;
    uint8_t command ;
    int size ;
    union i2c_smbus_data *data ;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_open(JNIEnv *env, jclass clazz, jstring deviceName, jint address) {
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

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_close(JNIEnv *env, jclass clazz, jint fd) {
    return close(fd);
};


JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIBBB(JNIEnv *env, jclass clazz, jint fd, jint devId, jbyte localAddress, jbyte value, jbyte mask) {
    ssize_t returnValue = 1;
        char values[2];
        values[0] = localAddress;
        if (mask != 0xFF) {
            uint8_t currentData;
            if ((returnValue = read(fd,&currentData,1))) {
                values[1] = (value & mask) | (currentData & ~mask);
            }
        }
        else {
            values[1] = value;
        }
        returnValue = write(fd,values,2);
    return returnValue;
};

JNIEXPORT jbyte JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__IIB(JNIEnv *env, jclass clazz, jint fd, jint devId, jbyte localAddress) {
    ssize_t returnValue = 0;
        if ((returnValue = write(fd,&localAddress,1))) {
            uint8_t value;
            if ((returnValue = read(fd,&value,1))) {
                return value;
            }
        }
    return returnValue;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIBB(JNIEnv *env, jclass clazz, jint fd, jint devId,  jbyte value , jbyte mask) {
    ssize_t returnValue = 0;
        if (mask != 0xFF) {
            uint8_t currentData;
            if ((returnValue = read(fd,&currentData,1))) {
                value = (value & mask) | (currentData & ~mask);
            }
        }
        else {
            returnValue = write(fd,&value,1);
        }
    return returnValue;
};

JNIEXPORT jbyte JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__II(JNIEnv *env, jclass clazz, jint fd, jint devId ) {
    ssize_t returnValue = 0;
        uint8_t value;
        if ((returnValue = read(fd,&value,1))) {
            return value;
        }
    return returnValue;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIIII_3BB(JNIEnv *env, jclass clazz, jint fd, jint devId, jint offset, jint width, jint writeCount, jbyteArray data, jbyteArray mask) {
    ssize_t returnValue = 1;
    uint8_t dataBlock[width];
    jbyte *body = (*env)->GetPrimitiveArrayCritical(env, data, 0);
    int i;
    int ni;

    if (mask != NULL) {
        jbyte *maskBody = (*env)->GetPrimitiveArrayCritical(env, mask, 0);
        uint8_t currentData[width];
        if ((returnValue = read(fd,currentData,width))) {
            for (i = 0; i < writeCount; ++i) {
                for (ni = 0; ni < width; ++ni) {
                    dataBlock[ni] = (body[(i*width)+ni+(offset*width)] & maskBody[ni]) | (currentData[ni] & ~maskBody[ni]);
                }
                returnValue = write(fd,dataBlock,width);
            }
        }
        (*env)->ReleasePrimitiveArrayCritical(env, mask, maskBody, 0);
    }
    else {
        returnValue = write(fd,body+offset,writeCount);
    }
    (*env)->ReleasePrimitiveArrayCritical(env, data, body, 0);
    return returnValue;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIBIII_3B_3B(JNIEnv *env, jclass clazz, jint fd, jint devId, jbyte localAddress, jint offset, jint width, jint writeCount, jbyteArray data, jbyteArray mask) {

    ssize_t returnValue = 1;
    uint8_t dataBlock[width+1];
    dataBlock[0] = localAddress;
    int i;
    int ni;

    jbyte *body = (*env)->GetPrimitiveArrayCritical(env, data, 0);
    if (mask != NULL) {
        jbyte *maskBody = (*env)->GetPrimitiveArrayCritical(env, mask, 0);
        uint8_t currentData[width];
        if ((returnValue = write(fd,&localAddress,1)) && (returnValue = read(fd,&currentData,width))) {
            for (i = 0; i < writeCount; ++i) {
                for (ni = 0; ni < width; ++ni) {
                    dataBlock[ni+1] = (body[(i*width)+ni+(offset*width)] & maskBody[ni]) | (currentData[ni] & ~maskBody[ni]);
                }
                returnValue = write(fd,dataBlock,width+1);
            }
        }
        (*env)->ReleasePrimitiveArrayCritical(env, mask, maskBody, 0);
    }
    else {
        for (i = 0; i < writeCount; ++i) {
            memcpy(&dataBlock+1,body+(i*width)+(offset*width),width);
            returnValue = write(fd,&dataBlock,width+1);
        }
    }
    (*env)->ReleasePrimitiveArrayCritical(env, data, body, 0);
    return returnValue;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__IIIII_3BB
  (JNIEnv *env, jclass clazz, jint fd, jint devId, jint offset, jint width, jint readCount, jbyteArray data, jbyteArray mask) {
  return 0;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__IIBIII_3B_3B
  (JNIEnv *env, jclass clazz, jint fd, jint devId, jbyte localAddress, jint offset, jint width, jint readCount, jbyteArray data, jbyteArray mask) {
  return 0;
};

