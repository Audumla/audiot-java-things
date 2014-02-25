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
    return 1;
};


JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIBBB(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jbyte localAddress, jbyte value, jbyte mask) {
    uint32_t lenTr;
    uint8_t reason;
    bcm2835_i2c_setSlaveAddress(deviceAddress);
    bcm2835_i2c_write((char *)&localAddress,1,bus, &lenTr);
    if (mask != 0xFF) {
        char data;
        reason = bcm2835_i2c_read(&data,1,bus, &lenTr);
        data = (value & mask) | (data & ~mask);
        reason = bcm2835_i2c_write(&data,1,bus, &lenTr);
    }
    else {
        reason = bcm2835_i2c_write((char *)&value,1,bus, &lenTr);
    }
    return lenTr;
};

JNIEXPORT jbyte JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__IIB(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jbyte localAddress) {
    uint8_t data;
    uint8_t reason;
    uint32_t lenTr;
    bcm2835_i2c_setSlaveAddress(deviceAddress);
    reason = bcm2835_i2c_write((char *)&localAddress,1,bus, &lenTr);
    reason = bcm2835_i2c_read((char *)&data,1,bus, &lenTr);
    return data;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIBB(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress,  jbyte value , jbyte mask) {
    uint32_t lenTr;
    uint8_t reason;
    bcm2835_i2c_setSlaveAddress(deviceAddress);
    if (mask != 0xFF) {
        uint8_t data ;
        reason = bcm2835_i2c_read((char *)&data,1,bus, &lenTr);
        data = (value & mask) | (data & ~mask);
        reason = bcm2835_i2c_write((char *)&data,1,bus, &lenTr);
    }
    else {
        reason = bcm2835_i2c_write((char *)&value,1,bus, &lenTr);
    }
    return lenTr;
};

JNIEXPORT jbyte JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__II(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress ) {
    uint32_t lenTr;
    uint8_t data;
    bcm2835_i2c_setSlaveAddress(deviceAddress);
    bcm2835_i2c_read((char *)&data,1,bus, &lenTr);
    return data;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIII_3BB(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jint offset, jint writeCount, jbyteArray values, jbyte mask) {
    uint32_t lenTr;
    uint32_t totalLenTr = 0;
    uint8_t reason;
    jbyte *body = (jbyte *)env->GetPrimitiveArrayCritical( values, 0);
    bcm2835_i2c_setSlaveAddress(deviceAddress);
    if (mask != 0xFF) {
        uint32_t i;
        uint8_t olddata;
        uint8_t data;
        reason = bcm2835_i2c_read((char *)&olddata,1,bus, &lenTr);
        olddata = olddata & ~mask;
        for (i = 0; i < writeCount; ++i) {
            data = (body[i+offset] & mask) | olddata;
            reason = bcm2835_i2c_write((char *)&data,1,bus, &lenTr);
            totalLenTr += lenTr;
        }
    }
    else {
        reason = bcm2835_i2c_write((char *)body,writeCount,bus, &lenTr);
        totalLenTr += lenTr;
    }
    env->ReleasePrimitiveArrayCritical( values, body, 0);
    return totalLenTr;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_write__IIBIII_3B_3B(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jbyte localAddress, jint offset, jint width, jint writeCount, jbyteArray data, jbyteArray mask) {
    uint32_t lenTr;
    uint32_t totalLenTr = 0;
    uint8_t reason;
    uint32_t i;
    jbyte *body = (jbyte *)env->GetPrimitiveArrayCritical( data, 0);
    bcm2835_i2c_setSlaveAddress(deviceAddress);
    uint8_t dataBlock[width+1];
    dataBlock[0] = localAddress;
    if (mask != NULL) {
        jbyte *maskBody = (jbyte*)env->GetPrimitiveArrayCritical( mask, 0);
        uint8_t currentData[width];
        reason = bcm2835_i2c_write((char *)&localAddress,1,bus, &lenTr);
        reason = bcm2835_i2c_read((char *)&currentData,width,bus, &lenTr);
        uint32_t ni;
        for (i = 0; i < writeCount; ++i) {
            for (ni = 0; ni < width; ++ni) {
                dataBlock[ni+1] = (body[(i*width)+ni+(offset*width)] & maskBody[ni]) | (currentData[ni] & ~maskBody[ni]);
            }
            reason = bcm2835_i2c_write((char *)dataBlock,width+1,bus, &lenTr);
            totalLenTr += lenTr;
        }
        env->ReleasePrimitiveArrayCritical( mask, maskBody, 0);
    }
    else {
        for (i = 0; i < writeCount; ++i) {
            memcpy(dataBlock+1,(body + (i*width) + (offset*width)),width);
            reason = bcm2835_i2c_write((char *)dataBlock,width+1,bus, &lenTr);
            totalLenTr += lenTr;
        }
    }
    env->ReleasePrimitiveArrayCritical( data, body, 0);
    return totalLenTr;
}

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__IIII_3BB(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jint offset, jint readCount, jbyteArray data, jbyte mask) {
    uint32_t lenTr;
    jbyte *body = (jbyte *)env->GetPrimitiveArrayCritical( data, 0);
    bcm2835_i2c_setSlaveAddress(deviceAddress);
    uint8_t reason = bcm2835_i2c_read((char *)body,readCount,bus, &lenTr);
    env->ReleasePrimitiveArrayCritical( data, body, 0);
    return lenTr;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_read__IIBIII_3B_3B(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jbyte localAddress, jint offset, jint width, jint readCount, jbyteArray data, jbyteArray mask) {
    uint32_t totalLenTr = 0;
    uint32_t i;
    uint32_t lenTr;
    uint8_t reason;
    jbyte *body = (jbyte *)env->GetPrimitiveArrayCritical( data, 0);
    bcm2835_i2c_setSlaveAddress(deviceAddress);
    for (i = 0; i < readCount; ++i) {
        reason = bcm2835_i2c_write((char *)&localAddress,1,bus, &lenTr);
        reason = bcm2835_i2c_read((char *)(body + (i*width) + (offset*width)),width,bus, &lenTr);
        totalLenTr += lenTr;
    }
    if (mask != NULL) {
        uint32_t ni;
        jbyte *maskBody = (jbyte*)env->GetPrimitiveArrayCritical( mask, 0);
        for (i = 0; i < readCount; ++i) {
            for (ni = 0; ni < width; ++i) {
                int index = (i*width)+ni+(offset*width);
                body[index] = body[index] & maskBody[ni];
            }
        }
        env->ReleasePrimitiveArrayCritical( mask, maskBody, 0);
    }
    env->ReleasePrimitiveArrayCritical( data, body, 0);
    return totalLenTr;
};

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_setClock
  (JNIEnv *env, jclass clazz, jint bus, jint freq) {
  uint32_t of = bcm2835_i2c_get_baudrate(bus);
  bcm2835_i2c_set_baudrate(freq,bus);
  return of;
}

JNIEXPORT jint JNICALL Java_net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_getClock
  (JNIEnv *env, jclass clazz, jint bus) {
  return bcm2835_i2c_get_baudrate(bus);
}
