#include <poll.h>


void closeGPIO(int pin) {
    close(fName, O_RDWR);
}

int openGPIO(int pin) {
    int fd;
    char fName   [64] ;
    sprintf (fName, "/sys/class/gpio/gpio%d/value", bcmGpioPin) ;
    if ((fd = open(fName, O_RDWR)) < 0) {
        return -1;
    }
    return fd;
}

/*
 * waitForInterrupt:
 *	This has been heavily based on the wiringpi library.
 *	Wait for Interrupt on a GPIO pin.
 *	This is actually done via the /sys/class/gpio interface.
 *  Maybe sometime it might get a better way for a bit more efficiency.
 */

int waitForGPIOtrigger(int fd, int mS)
{
    struct pollfd polls ;
    // Setup poll structure
    polls.fd     = fd ;
    polls.events = POLLPRI ;	// Urgent data!
    int x = poll (&polls, 1, mS) ;
    // Do a dummy read to clear the interrupt, a one character read appars to be enough.
    uint8_t c ;
    read(fd, &c, 1);
    return x;
}

