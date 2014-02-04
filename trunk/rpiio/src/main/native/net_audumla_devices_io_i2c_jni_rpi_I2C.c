#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <errno.h>
#include <string.h>
#include <fcntl.h>
#include <sys/ioctl.h>

#include "net_audumla_devices_io_i2c_jni_rpi_I2C.h"

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
} ;

struct i2c_smbus_ioctl_data
{
  char read_write ;
  uint8_t command ;
  int size ;
  union i2c_smbus_data *data ;
} ;

static inline int i2c_smbus_access (int fd, char rw, uint8_t command, int size, union i2c_smbus_data *data)
{
  struct i2c_smbus_ioctl_data args ;

  args.read_write = rw ;
  args.command    = command ;
  args.size       = size ;
  args.data       = data ;
  return ioctl (fd, I2C_SMBUS, &args) ;
}


/*
 * Class:     net_audumla_devices_io_i2c_jni_rpi_I2C
 * Method:    open
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_devices_io_i2c_jni_rpi_I2C_open
  (JNIEnv *env, jclass clazz, jstring str, jint devId) {
    char device[256];
    int len = (*env)->GetStringLength(env, str);
    (*env)->GetStringUTFRegion(env, str, 0, len, device);

    int fd ;

    if ((fd = open (device, O_RDWR)) < 0)
    return -1;

    if (ioctl (fd, I2C_SLAVE, devId) < 0)
    return -2;

    return fd ;
}

/*
 * Class:     net_audumla_devices_io_i2c_jni_rpi_I2C
 * Method:    close
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_devices_io_i2c_jni_rpi_I2C_close
  (JNIEnv *env, jclass clazz, jint fd){
  	return close(fd);
}

/*
 * Class:     net_audumla_devices_io_i2c_jni_rpi_I2C
 * Method:    writeByteDirect
 * Signature: (IIB)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_devices_io_i2c_jni_rpi_I2C_writeByteDirect
  (JNIEnv *env, jclass clazz, jint fd, jbyte data) {
      return i2c_smbus_access (fd, I2C_SMBUS_WRITE, data, I2C_SMBUS_BYTE, NULL);
}

/*
 * Class:     net_audumla_devices_io_i2c_jni_rpi_I2C
 * Method:    writeBytesDirect
 * Signature: (IIII[B)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_devices_io_i2c_jni_rpi_I2C_writeBytesDirect
  (JNIEnv *env, jclass clazz, jint fd, jint size, jint offset, jbyteArray bytes) {

    struct i2c_smbus_ioctl_data args ;
    args.read_write = I2C_SMBUS_WRITE;
    args.size       = I2C_SMBUS_BYTE;
    args.data       = NULL;

    jbyte *body = (*env)->GetPrimitiveArrayCritical(env, bytes, 0);
    for (i = 0; i < size; i++) {
        args.command = body[i + offset];
        ioctl(fd, I2C_SMBUS, &args);
    }
    (*env)->ReleasePrimitiveArrayCritical(env, bytes, body, 0);
    return 0;
}

/*
 * Class:     net_audumla_devices_io_i2c_jni_rpi_I2C
 * Method:    writeByte
 * Signature: (IIIB)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_devices_io_i2c_jni_rpi_I2C_writeByte
  (JNIEnv *env, jclass clazz, jint fd, jint reg, jbyte value) {
    union i2c_smbus_data data ;

    data.byte = value ;
    return i2c_smbus_access (fd, I2C_SMBUS_WRITE, reg, I2C_SMBUS_BYTE_DATA, &data) ;
}

/*
 * Class:     net_audumla_devices_io_i2c_jni_rpi_I2C
 * Method:    writeWord
 * Signature: (IIIC)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_devices_io_i2c_jni_rpi_I2C_writeWord
  (JNIEnv *env, jclass clazz, jint fd, jint reg, jchar value) {
    union i2c_smbus_data data ;

    data.word = value ;
    return i2c_smbus_access (fd, I2C_SMBUS_WRITE, reg, I2C_SMBUS_WORD_DATA, &data) ;
}

/*
 * Class:     net_audumla_devices_io_i2c_jni_rpi_I2C
 * Method:    writeBytes
 * Signature: (IIIII[B)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_devices_io_i2c_jni_rpi_I2C_writeBytes
  (JNIEnv *env, jclass clazz, jint fd, jint reg, jint size, jint offset, jbyteArray bytes) {

    union i2c_smbus_data data ;
    struct i2c_smbus_ioctl_data args ;
    args.read_write = I2C_SMBUS_WRITE;
    args.size       = I2C_SMBUS_BYTE_DATA;
    args.data       = data;
    args.command    = reg;

    jbyte *body = (*env)->GetPrimitiveArrayCritical(env, bytes, 0);
    for (i = 0; i < size; i++) {
        data.word = body[i + offset];
        ioctl(fd, I2C_SMBUS, &args);
    }
    (*env)->ReleasePrimitiveArrayCritical(env, bytes, body, 0);
    return 0;
}

/*
 * Class:     net_audumla_devices_io_i2c_jni_rpi_I2C
 * Method:    writeWords
 * Signature: (IIIII[C)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_devices_io_i2c_jni_rpi_I2C_writeWords
  (JNIEnv *env, jclass clazz, jint fd, jint reg, jint size, jint offset, jcharArray bytes){
    union i2c_smbus_data data ;
    struct i2c_smbus_ioctl_data args ;
    args.read_write = I2C_SMBUS_WRITE;
    args.size       = I2C_SMBUS_WORD_DATA;
    args.data       = data;
    args.command    = reg;

    jchar *body = (jchar*)(*env)->GetPrimitiveArrayCritical(env, bytes, 0);
    for (i = 0; i < size; i++) {
        data.word = body[i + offset];
        ioctl(fd, I2C_SMBUS, &args);
    }
    (*env)->ReleasePrimitiveArrayCritical(env, bytes, body, 0);
    return 0;
}

/*
 * Class:     net_audumla_devices_io_i2c_jni_rpi_I2C
 * Method:    readByteDirect
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_devices_io_i2c_jni_rpi_I2C_readByteDirect
  (JNIEnv *env, jclass clazz, jint fd) {
    union i2c_smbus_data data ;

    if (i2c_smbus_access (fd, I2C_SMBUS_READ, 0, I2C_SMBUS_BYTE, &data))
        return -1 ;
    else
        return data.byte & 0xFF ;
}

/*
 * Class:     net_audumla_devices_io_i2c_jni_rpi_I2C
 * Method:    readByte
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_devices_io_i2c_jni_rpi_I2C_readByte
  (JNIEnv *env, jclass clazz, jint fd, jint reg){
    union i2c_smbus_data data;

    if (i2c_smbus_access (fd, I2C_SMBUS_READ, reg, I2C_SMBUS_BYTE_DATA, &data))
       return -1 ;
    else
        return data.byte & 0xFF ;
 }


/*
 * Class:     net_audumla_devices_io_i2c_jni_rpi_I2C
 * Method:    readWord
 * Signature: (III)I
 */
JNIEXPORT jint JNICALL Java_net_audumla_devices_io_i2c_jni_rpi_I2C_readWord
  (JNIEnv *env, jclass clazz, jint fd, jint reg){
    union i2c_smbus_data data;

    if (i2c_smbus_access (fd, I2C_SMBUS_READ, reg, I2C_SMBUS_WORD_DATA, &data))
        return -1 ;
    else
        return data.word & 0xFFFF ;
}
