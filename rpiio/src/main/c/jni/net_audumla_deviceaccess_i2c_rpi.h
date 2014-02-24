
#ifndef _I2C_H_
#define _I2C_H_

#include "rpiGpio.h"
#include <stdarg.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <unistd.h>
#include <errno.h>
#include <stdio.h>
#include <time.h>

/** @brief The size the I2C mapping is required to be. */
#define I2C_MAP_SIZE                BSC_DEL_OFFSET

/** @brief Default I2C clock frequency (Hertz) */
#define I2C_DEFAULT_FREQ_HZ         100000

/** @brief nano seconds in a second */
#define NSEC_IN_SEC                 1000000000

/** @brief Clock pulses per I2C byte - 8 bits + ACK */
#define CLOCKS_PER_BYTE             9

/** @brief BSC_C register */
#define I2C_C                       *(gI2cMap + BSC_C_OFFSET / sizeof(uint32_t))
/** @brief BSC_DIV register */
#define I2C_DIV                     *(gI2cMap + BSC_DIV_OFFSET / sizeof(uint32_t))
/** @brief BSC_A register */
#define I2C_A                       *(gI2cMap + BSC_A_OFFSET / sizeof(uint32_t))
/** @brief BSC_DLEN register */
#define I2C_DLEN                    *(gI2cMap + BSC_DLEN_OFFSET / sizeof(uint32_t))
/** @brief BSC_S register */
#define I2C_S                       *(gI2cMap + BSC_S_OFFSET / sizeof(uint32_t))
/** @brief BSC_FIFO register */
#define I2C_FIFO                    *(gI2cMap + BSC_FIFO_OFFSET / sizeof(uint32_t))


#endif /*_I2C_H_*/