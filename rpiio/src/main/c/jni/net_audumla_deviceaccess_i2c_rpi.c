
/** @brief Pointer which will be mmap'd to the I2C memory in /dev/mem */
static volatile uint32_t * gI2cMap = NULL;

errStatus net_audumla_i2c_setup(void)
    if (gI2cMap != NULL)
    {
        dbgPrint(DBG_INFO, "gpioI2cSetup was already called.");
        rtn = ERROR_ALREADY_INITIALISED;
    }
    else if ((mem_fd = open("/dev/mem", O_RDWR)) < 0)
    {
        dbgPrint(DBG_INFO, "open() failed for /dev/mem. errno: %s.",
                 strerror(errno));
        rtn = ERROR_EXTERNAL;
    }
    else if ((gI2cMap = (volatile uint32_t *)mmap(NULL,
                                                  I2C_MAP_SIZE,
                                                  PROT_READ|PROT_WRITE,
                                                  MAP_SHARED,
                                                  mem_fd,
                                                  bscBase)) == MAP_FAILED)
    {
        dbgPrint(DBG_INFO, "mmap() failed. errno: %s.", strerror(errno));
        rtn = ERROR_EXTERNAL;
    }

    /* Close the fd, we have now mapped it */
    else if (close(mem_fd) != OK)
    {
        dbgPrint(DBG_INFO, "close() failed. errno: %s.", strerror(errno));
        rtn = ERROR_EXTERNAL;
    }