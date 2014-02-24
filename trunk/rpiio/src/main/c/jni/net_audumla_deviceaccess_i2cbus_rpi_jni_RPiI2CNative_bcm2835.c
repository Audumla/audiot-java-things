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
#include "bcm2835.h"


JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_open(JNIEnv *env, jclass clazz, jint bus, jint address) {
    int retVal;
    if ((retVal = bcm2835_init())) {
        bcm2835_i2c_begin(bus);
    };
    return retVal;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_close(JNIEnv *env, jclass clazz, jint bus) {
    bcm2835_i2c_end(bus);
};


JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIBBB(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jbyte localAddress, jbyte value, jbyte mask) {
    bcm2835_i2c_setSlaveAddress(bus,deviceAddress);
    bcm2835_i2c_write(bus,localAddress,1);
    if (mask != 0xFF) {
        uint8_t data;
        bcm2835_i2c_read(bus,&data,1);
        data = (value & mask) | (data & ~mask);
        bcm2835_i2c_write(bus,&data,1);
    }
    else {
        bcm2835_i2c_write(bus,&value,1);
    }
    return 1;
};

JNIEXPORT jbyte JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__IIB(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jbyte localAddress) {
    uint8_t data;
    bcm2835_i2c_setSlaveAddress(bus,deviceAddress);
    bcm2835_i2c_write(bus,localAddress,1);
    bcm2835_i2c_read(bus,&data,1);
    return data;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIBB(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress,  jbyte value , jbyte mask) {
    bcm2835_i2c_setSlaveAddress(bus,deviceAddress);
    if (mask != 0xFF) {
        uint8_t data ;
        bcm2835_i2c_read(bus,&data,1);
        data = (value & mask) | (data & ~mask);
        bcm2835_i2c_write(bus,&data,1);
    }
    else {
        bcm2835_i2c_write(bus,&value,1);
    }
    return 1;
};

JNIEXPORT jbyte JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__II(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress ) {
    uint8_t data;
    bcm2835_i2c_setSlaveAddress(bus,deviceAddress);
    bcm2835_i2c_read(bus,&data,1);
    return data;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIII_3BB(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jint offset, jint writeCount, jbyteArray values, jbyte mask) {
    jbyte *body = (*env)->GetPrimitiveArrayCritical(env, values, 0);
    uint32_t i;
    bcm2835_i2c_setSlaveAddress(bus,deviceAddress);
    if (mask != 0xFF) {
        uint8_t olddata;
        uint8_t data;
        bcm2835_i2c_read(bus,&olddata,1);
        olddata = olddata & ~mask;
        for (i = 0; i < writeCount; ++i) {
            data = (body[i+offset] & mask) | olddata;
            bcm2835_i2c_write(bus,&data,1);
        }
    }
    else {
        bcm2835_i2c_write(bus,body,writeCount);
    }
    (*env)->ReleasePrimitiveArrayCritical(env, data, body, 0);
    return 1;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIBIII_3B_3B(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jbyte localAddress, jint offset, jint width, jint writeCount, jbyteArray data, jbyteArray mask) {
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
        ioctl(bus, I2C_SMBUS, &args);
        args.data = dataBlock;
        args.read_write = I2C_SMBUS_WRITE;
        uint32_t ni;
        for (i = 0; i < writeCount; ++i) {
            for (ni = 0; ni < width; ++ni) {
                dataBlock[ni] = (body[(i*width)+ni+(offset*width)] & maskBody[ni]) | (currentData[ni] & ~maskBody[ni]);
            }
            ioctl(bus, I2C_SMBUS, &args);
        }
        (*env)->ReleasePrimitiveArrayCritical(env, mask, maskBody, 0);
    }
    else {
        args.read_write = I2C_SMBUS_WRITE;
        for (i = 0; i < writeCount; ++i) {
            args.data = (uint8_t*) (body + (i*width) + (offset*width));
            ioctl(bus, I2C_SMBUS, &args);
        }
    }
    (*env)->ReleasePrimitiveArrayCritical(env, data, body, 0);
    return 1;
}

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__IIII_3BB
  (JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jint offset, jint readCount, jbyteArray data, jbyte mask) {
    struct i2c_smbus_ioctl_data args ;
    jbyte *body = (*env)->GetPrimitiveArrayCritical(env, data, 0);
    uint8_t dataBlock;
    uint32_t i;
    args.read_write = I2C_SMBUS_READ;
    args.command    = 0;
    args.size       = 1;
    args.data       = &dataBlock;
    for (i = 0; i < readCount; ++i) {
        ioctl(bus, I2C_SMBUS, &args);
        body[i+offset] = dataBlock & mask;
    }
    (*env)->ReleasePrimitiveArrayCritical(env, data, body, 0);
    return 1;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__IIBIII_3B_3B
  (JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jbyte localAddress, jint offset, jint width, jint readCount, jbyteArray data, jbyteArray mask) {
    struct i2c_smbus_ioctl_data args ;
    jbyte *body = (*env)->GetPrimitiveArrayCritical(env, data, 0);
    args.read_write = I2C_SMBUS_READ;
    args.command    = localAddress;
    args.size       = width+1;
    uint32_t i;
    for (i = 0; i < readCount; ++i) {
        args.data = (uint8_t*) (body + (i*width) + (offset*width));
        ioctl(bus, I2C_SMBUS, &args);
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

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_setClock
  (JNIEnv *env, jclass clazz, jint bus, jint freq) {
  return 100000;
}

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_getClock
  (JNIEnv *env, jclass clazz, jint bus) {
  return 100000;
}
