#
# Makefile:
#	audumlaRPi - Java (JNI) library wrapper for Raspberry Pi io interfaces
#

TARGET=audumlaRPiI2C_ioctl.so

#DEBUG	= -g -O0
DEBUG	= -O3
CC	= gcc
INCLUDE	= -I. -I/$(JAVA_HOME)/include -I/usr/local/include -I. -I/$(JAVA_HOME)/include/linux -I/usr/local/include/linux
CFLAGS	= $(DEBUG) -Wall $(INCLUDE) -Winline -pipe $(LIBS) 

# Should not alter anything below this line
###############################################################################

SRC	=   net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_ioctl.c

OBJ	=   net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative_ioctl.o

all:		$(TARGET)

$(TARGET):	$(OBJ)
	@echo [LINK] $(OBJ)
	@$(CC) $(OBJ) -shared -o $(TARGET) $(INCLUDE) $(LIBS)  
	@size   $(TARGET)

.c.o:
	@echo [COMPILE] "$(CC) -c $(CFLAGS)" $<
	@$(CC) -c $(CFLAGS) $< -o $@

clean:
	rm -f $(OBJ) $(TARGET) *~ core tags Makefile.bak

tags:	$(SRC)
	@echo [ctags]
	@ctags $(SRC)

depend:
	makedepend -Y $(SRC)

install:	$(TARGET)
	@echo [install]
	install -m 0755 -d /usr/local/lib
	install -m 0755 -d /usr/local/include
	install -m 0644 $(TARGET)	/usr/local/lib
	install -m 0644 $(TARGET)   ../../../../../libraries/lib

uninstall:
	@echo [uninstall]
	rm -f /usr/local/lib/$(TARGET)


# DO NOT DELETE
net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative.o: net_audumla_deviceaccess_i2cbus_rpi_jni_RPiI2CNative.h