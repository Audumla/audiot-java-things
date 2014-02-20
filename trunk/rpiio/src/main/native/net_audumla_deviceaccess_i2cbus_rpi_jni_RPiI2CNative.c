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

/*
 * Class:     net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative
 * Method:    open
 * Signature: (Ljava/lang/String;I)I
 */
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

/*
 * Class:     net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative
 * Method:    close
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_close(JNIEnv *env, jclass clazz, jint fd) {
    return close(fd);
};

/*
 * Class:     net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative
 * Method:    write
 * Signature: (III[BB)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__III_3BB(JNIEnv *env, jclass clazz, jint fd, jint offset, jint writeCount, jbyteArray data, jbyte mask) {
    ssize_t returnValue;
    jbyte *body = (*env)->GetPrimitiveArrayCritical(env, data, 0);
    if (mask != 0xff) {
        uint8_t currentData;
        if ((returnValue = read(fd,&currentData,1))) {
            uint8_t maskedData[writeCount];
            int i;
            for (i = 0; i < writeCount; ++i) {
                maskedData[i] = (body[i+offset] & mask) | (currentData & ~mask);
            }
            returnValue = write(fd,maskedData,writeCount);
        }
    }
    else {
        returnValue = write(fd,body+offset,writeCount);
    }
    (*env)->ReleasePrimitiveArrayCritical(env, data, body, 0);
    return returnValue;
};

/*
 * Class:     net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative
 * Method:    write
 * Signature: (IIIII[B[B)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIIII_3B_3B(JNIEnv *env, jclass clazz, jint fd, jint localAddress, jint offset, jint width, jint writeCount, jbyteArray data, jbyteArray mask) {

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

/*
 * Class:     net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative
 * Method:    read
 * Signature: (III[BB)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__III_3BB
  (JNIEnv *env, jclass clazz, jint fd, jint offset, jint readCount, jbyteArray data, jbyte mask) {
  return 0;
};

/*
 * Class:     net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative
 * Method:    read
 * Signature: (IIIII[B[B)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__IIIII_3B_3B
  (JNIEnv *env, jclass clazz, jint fd, jint localAddress, jint offset, jint width, jint readCount, jbyteArray data, jbyteArray mask) {
  return 0;
};

