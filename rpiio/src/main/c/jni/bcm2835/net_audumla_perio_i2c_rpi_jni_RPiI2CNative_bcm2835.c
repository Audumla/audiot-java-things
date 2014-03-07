#include <jni.h>
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdint.h>
#include <errno.h>
#include <string.h>
#include <fcntl.h>
#include <sys/ioctl.h>
#include "net_audumla_perio_i2c_rpi_jni_RPiI2CNative.h"
#include "bcm2835.h"
#include "errorHandler.h"


const char *getI2CErrorMessage(int code) {
    switch (code) {
        case BCM2835_I2C_REASON_OK : return "I2C Success";
        case BCM2835_I2C_REASON_ERROR_NACK : return "I2C Received a NACK";
        case BCM2835_I2C_REASON_ERROR_CLKT : return "I2C Received Clock Stretch Timeout";
        case BCM2835_I2C_REASON_ERROR_DATA : return "I2C Not all data is sent / received";
    }
    return "Unknown I2C Error";
}

JNIEXPORT jint JNICALL JNIEXPORT jint JNICALL Java_net_audumla_perio_i2c_rpi_jni_RPiI2CNative_open(JNIEnv *env, jclass clazz, jint bus, jint address, jobject errorHandler) {
    int retVal;
    if ((retVal = bcm2835_init())) {
        bcm2835_i2c_begin(bus);
    }
    else {
        handleError(env,errorHandler,errno,strerror(errno),__PRETTY_FUNCTION__);
    }

    return retVal;
};

JNIEXPORT void JNICALL Java_net_audumla_perio_i2c_rpi_jni_RPiI2CNative_close(JNIEnv *env, jclass clazz, jint bus, jobject errorHandler) {
    bcm2835_i2c_end(bus);
};


JNIEXPORT jint JNICALL Java_net_audumla_perio_i2c_rpi_jni_RPiI2CNative_write__IIBBBLnet_audumla_perio_jni_ErrorHandler_2(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jbyte localAddress, jbyte value, jbyte mask, jobject errorHandler) {
    uint32_t lenTr = 0;
    uint8_t reason;
    bcm2835_i2c_setSlaveAddress(deviceAddress,bus);
    if ((reason = bcm2835_i2c_write((char *) &localAddress,1,bus, &lenTr))) {}
        if (mask != 0xFF) {
            char data;
            if ((reason = bcm2835_i2c_read(&data,1,bus, &lenTr))) {
                data = (value & mask) | (data & ~mask);
                if (!(reason = bcm2835_i2c_write(&data,1,bus, &lenTr))) {
                    handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
                }
            }
            else {
                handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
            }
        }
        else {
            if (!(reason = bcm2835_i2c_write((char *)&value,1,bus, &lenTr))) {
                handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
            }
        }
    }
    else {
        handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
    }
    return lenTr;
};

JNIEXPORT jbyte JNICALL Java_net_audumla_perio_i2c_rpi_jni_RPiI2CNative_read__IIBLnet_audumla_perio_jni_ErrorHandler_2(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jbyte localAddress, jobject errorHandler) {
    uint8_t data = 0;
    uint8_t reason;
    uint32_t lenTr;
    bcm2835_i2c_setSlaveAddress(deviceAddress,bus);
    if ((reason = bcm2835_i2c_write((char *)&localAddress,1,bus, &lenTr))) {
        if (!(reason = bcm2835_i2c_read((char *)&data,1,bus, &lenTr)) {
            handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
        }
    }
    else {
        handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
    }
    return data;
};

JNIEXPORT jint JNICALL Java_net_audumla_perio_i2c_rpi_jni_RPiI2CNative_write__IIBBLnet_audumla_perio_jni_ErrorHandler_2(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress,  jbyte value , jbyte mask, jobject errorHandler) {
    uint32_t lenTr = 0;
    uint8_t reason;
    bcm2835_i2c_setSlaveAddress(deviceAddress,bus);
    if (mask != 0xFF) {
        uint8_t data ;
        if ((reason = bcm2835_i2c_read((char *)&data,1,bus, &lenTr))) {
            data = (value & mask) | (data & ~mask);
            if (!(reason = bcm2835_i2c_write((char *)&data,1,bus, &lenTr))) {
                handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
            }
        }
        else {
            handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
        }
    }
    else {
        if (!(reason = bcm2835_i2c_write((char *)&value,1,bus, &lenTr))) {
            handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
        }
    }
    return lenTr;
};

JNIEXPORT jbyte JNICALL Java_net_audumla_perio_i2c_rpi_jni_RPiI2CNative_read__IILnet_audumla_perio_jni_ErrorHandler_2(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress , jobject errorHandler) {
    uint32_t lenTr;
    uint8_t data =0;
    bcm2835_i2c_setSlaveAddress(deviceAddress,bus);
    if (!(uint8_t reason = bcm2835_i2c_read((char *)&data,1,bus, &lenTr))) {
        handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
    }
    return data;
};

JNIEXPORT jint JNICALL Java_net_audumla_perio_i2c_rpi_jni_RPiI2CNative_write__IIII_3BBLnet_audumla_perio_jni_ErrorHandler_2(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jint offset, jint writeCount, jbyteArray values, jbyte mask, jobject errorHandler) {
    uint32_t lenTr = 0;
    uint32_t totalLenTr = 0;
    uint8_t reason;
    jbyte *body = (jbyte *)(*env)->GetPrimitiveArrayCritical( env,values, 0);
    bcm2835_i2c_setSlaveAddress(deviceAddress,bus);
    if (mask != 0xFF) {
        long i;
        uint8_t olddata;
        uint8_t data;
        if ((reason = bcm2835_i2c_read((char *)&olddata,1,bus, &lenTr)) {
            olddata = olddata & ~mask;
            for (i = 0; i < writeCount; ++i) {
                lenTr = 0;
                data = (body[i+offset] & mask) | olddata;
                if (!(reason = bcm2835_i2c_write((char *)&data,1,bus, &lenTr))) {
                    handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
                }
                totalLenTr += lenTr;
            }
        }
        else {
            handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
        }
    }
    else {
        if (!(reason = bcm2835_i2c_write((char *)body,writeCount,bus, &lenTr))) {
            handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
        }
        totalLenTr += lenTr;
    }
    (*env)->ReleasePrimitiveArrayCritical( env,values, body, 0);
    return totalLenTr;
};

JNIEXPORT jint JNICALL Java_net_audumla_perio_i2c_rpi_jni_RPiI2CNative_write__IIBIII_3B_3BLnet_audumla_perio_jni_ErrorHandler_2(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jbyte localAddress, jint offset, jint width, jint writeCount, jbyteArray data, jbyteArray mask, jobject errorHandler) {
    uint32_t lenTr;
    uint32_t totalLenTr = 0;
    uint8_t reason;
    long i;
    jbyte *body = (jbyte *)(*env)->GetPrimitiveArrayCritical( env,data, 0);
    bcm2835_i2c_setSlaveAddress(deviceAddress,bus);
    uint8_t dataBlock[width+1];
    dataBlock[0] = localAddress;
    if (mask != NULL) {
        jbyte *maskBody = (jbyte*)(*env)->GetPrimitiveArrayCritical( env,mask, 0);
        uint8_t currentData[width];
        if ((reason = bcm2835_i2c_write((char *)&localAddress,1,bus, &lenTr))) {}
            if ((reason = bcm2835_i2c_read((char *)&currentData,width,bus, &lenTr)) {
                long ni;
                for (i = 0; i < writeCount; ++i) {
                    lenTr = 1;
                    for (ni = 0; ni < width; ++ni) {
                        dataBlock[ni+1] = (body[(i*width)+ni+(offset*width)] & maskBody[ni]) | (currentData[ni] & ~maskBody[ni]);
                    }
                    if (!(reason = bcm2835_i2c_write((char *)dataBlock,width+1,bus, &lenTr))) {
                        handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
                    }
                    totalLenTr += (lenTr -1);
                }
                (*env)->ReleasePrimitiveArrayCritical( env,mask, maskBody, 0);
            }
            else {
                handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
            }
        }
        else {
            handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
        }
    }
    else {
        for (i = 0; i < writeCount; ++i) {
            memcpy(dataBlock+1,(body + (i*width) + (offset*width)),width);
            if (!(reason = bcm2835_i2c_write((char *)dataBlock,width+1,bus, &lenTr))) {
                handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
            }
            totalLenTr += (lenTr-1);
        }
    }
    (*env)->ReleasePrimitiveArrayCritical( env,data, body, 0);
    return totalLenTr;
}

JNIEXPORT jint JNICALL Java_net_audumla_perio_i2c_rpi_jni_RPiI2CNative_read__IIII_3BBLnet_audumla_perio_jni_ErrorHandler_2(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jint offset, jint readCount, jbyteArray data, jbyte mask, jobject errorHandler) {
    uint32_t lenTr = 0;
    uint8_t reason;
    jbyte *body = (jbyte *)(*env)->GetPrimitiveArrayCritical( env,data, 0);
    bcm2835_i2c_setSlaveAddress(deviceAddress,bus);
    if (!(reason = bcm2835_i2c_read((char *)(body+offset),readCount,bus, &lenTr))) {
        handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
    }
    (*env)->ReleasePrimitiveArrayCritical( env,data, body, 0);
    return lenTr;
};

JNIEXPORT jint JNICALL Java_net_audumla_perio_i2c_rpi_jni_RPiI2CNative_read__IIBIII_3B_3BLnet_audumla_perio_jni_ErrorHandler_2(JNIEnv *env, jclass clazz, jint bus, jint deviceAddress, jbyte localAddress, jint offset, jint width, jint readCount, jbyteArray data, jbyteArray mask, jobject errorHandler) {
    uint32_t totalLenTr = 0;
    long i;
    uint8_t reason;
    jbyte *body = (jbyte *)(*env)->GetPrimitiveArrayCritical( env,data, 0);
    bcm2835_i2c_setSlaveAddress(deviceAddress,bus);
    for (i = 0; i < readCount; ++i) {
        uint32_t lenTr = 0;
        if ((reason = bcm2835_i2c_write((char *)&localAddress,1,bus, &lenTr))) {
            if (!(reason = bcm2835_i2c_read((char *)(body + (i*width) + (offset*width)),width,bus, &lenTr))) {
                handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
            }
            totalLenTr += lenTr;
        }
        else {
            handleError(env,errorHandler,errno,getI2CErrorMessage(reason),__PRETTY_FUNCTION__);
        }
    }
    if (mask != NULL) {
        long ni;
        jbyte *maskBody = (jbyte*)(*env)->GetPrimitiveArrayCritical( env,mask, 0);
        for (i = 0; i < readCount; ++i) {
            for (ni = 0; ni < width; ++i) {
                int index = (i*width)+ni+(offset*width);
                body[index] = body[index] & maskBody[ni];
            }
        }
        (*env)->ReleasePrimitiveArrayCritical( env,mask, maskBody, 0);
    }
    (*env)->ReleasePrimitiveArrayCritical( env,data, body, 0);
    return totalLenTr;
};

JNIEXPORT jint JNICALL Java_net_audumla_perio_i2c_rpi_jni_RPiI2CNative_setClock(JNIEnv *env, jclass clazz, jint bus, jint freq, jobject errorHandler) {
  uint32_t of = bcm2835_i2c_get_baudrate(bus);
  bcm2835_i2c_set_baudrate(freq,bus);
  return of;
}

JNIEXPORT jint JNICALL Java_net_audumla_perio_i2c_rpi_jni_RPiI2CNative_getClock(JNIEnv *env, jclass clazz, jint bus, jobject errorHandler) {
  return bcm2835_i2c_get_baudrate(bus);
}

