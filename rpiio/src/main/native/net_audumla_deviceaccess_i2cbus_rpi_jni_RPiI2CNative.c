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


struct i2c_smbus_ioctl_data
{
    char read_write;
    uint8_t command;
    int size;
    uint8_t *data;
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
    uint8_t data;
    struct i2c_smbus_ioctl_data args ;

    args.command    = localAddress;
    args.size       = 2;
    args.data       = &data;
    if (mask != 0xFF) {
        args.read_write = I2C_SMBUS_READ;
        ioctl(fd, I2C_SMBUS, &args);
        data = (value & mask) | (data & ~mask);
    }
    else {
        data = value ;
    }
    args.read_write = I2C_SMBUS_WRITE;
    return ioctl(fd, I2C_SMBUS, &args);
};

JNIEXPORT jbyte JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__IIB(JNIEnv *env, jclass clazz, jint fd, jint devId, jbyte localAddress) {
    uint8_t data;
    struct i2c_smbus_ioctl_data args ;

    args.command    = localAddress;
    args.size       = 2;
    args.data       = &data;
    args.read_write = I2C_SMBUS_READ;
    ioctl(fd, I2C_SMBUS, &args);
    return data;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIBB(JNIEnv *env, jclass clazz, jint fd, jint devId,  jbyte value , jbyte mask) {
    uint8_t data ;
    struct i2c_smbus_ioctl_data args ;

    args.size = 1;
    if (mask != 0xFF) {
        args.command    = 0;
        args.data = &data;
        args.read_write = I2C_SMBUS_READ;
        ioctl(fd, I2C_SMBUS, &args);
        args.command = (value & mask) | (data & ~mask);
    }
    else {
        args.command = value;
    }
    args.data = NULL;
    args.read_write = I2C_SMBUS_WRITE;
    return ioctl(fd, I2C_SMBUS, &args);
};

JNIEXPORT jbyte JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__II(JNIEnv *env, jclass clazz, jint fd, jint devId ) {
    uint8_t data ;
    struct i2c_smbus_ioctl_data args ;

    args.read_write = I2C_SMBUS_READ;
    args.command    = 0;
    args.size       = 1;
    args.data       = &data;
    ioctl(fd, I2C_SMBUS, &args);
    return data;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIII_3BB(JNIEnv *env, jclass clazz, jint fd, jint devId, jint offset, jint writeCount, jbyteArray data, jbyte mask) {
    struct i2c_smbus_ioctl_data args ;
    jbyte *body = (*env)->GetPrimitiveArrayCritical(env, data, 0);
    uint32_t i;
    args.size       = 1;
    if (mask != 0xFF) {
        uint8_t data;
        args.command = 0;
        args.data = &data;
        args.read_write = I2C_SMBUS_READ;
        ioctl(fd, I2C_SMBUS, &args);
        args.data = NULL;
        args.read_write = I2C_SMBUS_WRITE;
        for (i = 0; i < writeCount; ++i) {
            args.command = (body[i+offset] & mask) | (data & ~mask);
            ioctl(fd, I2C_SMBUS, &args);
        }
    }
    else {
        args.data = NULL;
        args.read_write = I2C_SMBUS_WRITE;
        for (i = 0; i < writeCount; ++i) {
            args.command = body[i+offset];
            ioctl(fd, I2C_SMBUS, &args);
        }
    }
    (*env)->ReleasePrimitiveArrayCritical(env, data, body, 0);
    return 1;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIBIII_3B_3B(JNIEnv *env, jclass clazz, jint fd, jint devId, jbyte localAddress, jint offset, jint width, jint writeCount, jbyteArray data, jbyteArray mask) {
    struct i2c_smbus_ioctl_data args ;
    jbyte *body = (*env)->GetPrimitiveArrayCritical(env, data, 0);
    uint32_t i;
    args.command = localAddress;
    args.size    = width+1;
    if (mask != NULL) {
        uint8_t dataBlock[width];
        jbyte *maskBody = (*env)->GetPrimitiveArrayCritical(env, mask, 0);
        uint8_t currentData[width];
        args.data = currentData;
        args.read_write = I2C_SMBUS_READ;
        ioctl(fd, I2C_SMBUS, &args);
        args.data = dataBlock;
        args.read_write = I2C_SMBUS_WRITE;
        uint32_t ni;
        for (i = 0; i < writeCount; ++i) {
            for (ni = 0; ni < width; ++ni) {
                dataBlock[ni] = (body[(i*width)+ni+(offset*width)] & maskBody[ni]) | (currentData[ni] & ~maskBody[ni]);
            }
            ioctl(fd, I2C_SMBUS, &args);
        }
        (*env)->ReleasePrimitiveArrayCritical(env, mask, maskBody, 0);
    }
    else {
        args.read_write = I2C_SMBUS_WRITE;
        for (i = 0; i < writeCount; ++i) {
            args.data = body + (i*width) + (offset*width);
            ioctl(fd, I2C_SMBUS, &args);
        }
    }
    (*env)->ReleasePrimitiveArrayCritical(env, data, body, 0);
    return 1;
}

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__IIII_3BB
  (JNIEnv *env, jclass clazz, jint fd, jint devId, jint offset, jint readCount, jbyteArray data, jbyte mask) {
    struct i2c_smbus_ioctl_data args ;
    jbyte *body = (*env)->GetPrimitiveArrayCritical(env, data, 0);
    uint8_t dataBlock;
    uint32_t i;
    args.read_write = I2C_SMBUS_READ;
    args.command    = 0;
    args.size       = 1;
    args.data       = &dataBlock;
    for (i = 0; i < readCount; ++i) {
        ioctl(fd, I2C_SMBUS, &args);
        body[i+offset] = dataBlock & mask;
    }
    (*env)->ReleasePrimitiveArrayCritical(env, data, body, 0);
    return 1;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__IIBIII_3B_3B
  (JNIEnv *env, jclass clazz, jint fd, jint devId, jbyte localAddress, jint offset, jint width, jint readCount, jbyteArray data, jbyteArray mask) {
    struct i2c_smbus_ioctl_data args ;
    jbyte *body = (*env)->GetPrimitiveArrayCritical(env, data, 0);
    args.read_write = I2C_SMBUS_READ;
    args.command    = localAddress;
    args.size       = width+1;
    uint32_t i;
    for (i = 0; i < readCount; ++i) {
        args.data = body + (i*width) + (offset*width);
        ioctl(fd, I2C_SMBUS, &args);
    }
    if (mask != NULL) {
        uint32_t ni;
        jbyte *maskBody = (*env)->GetPrimitiveArrayCritical(env, mask, 0);
        for (i = 0; i < readCount; ++i) {
            for (ni = 0; ni < width; ++i) {
                int index = (i*width)+ni+(offset*width);
                body[index] = body[index] & maskBody[ni];
            }
        }
        (*env)->ReleasePrimitiveArrayCritical(env, mask, maskBody, 0);
    }
    (*env)->ReleasePrimitiveArrayCritical(env, data, body, 0);
    return 1;
};

