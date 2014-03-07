package net.audumla.perio.jni;

/*
 * *********************************************************************
 *  ORGANIZATION : audumla.net
 *  More information about this project can be found at the following locations:
 *  http://www.audumla.net/
 *  http://audumla.googlecode.com/
 * *********************************************************************
 *  Copyright (C) 2012 - 2013 Audumla.net
 *  Licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *  You may not use this file except in compliance with the License located at http://creativecommons.org/licenses/by-nc-nd/3.0/
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 *  "AS IS BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

import net.audumla.perio.NativePeripheralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultErrorHandler implements ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultErrorHandler.class);
    private NativePeripheralException exception;

    @Override
    public void handleError(int errorNo, String message, String nativeMethodName) {
        exception = new NativePeripheralException(errorNo, message, nativeMethodName);
    }

    @Override
    public int getErrorCode() {
        return exception.getErrorCode();
    }

    @Override
    public String getErrorMessage() {
        return exception.getNativeMessage();
    }

    @Override
    public String getNativeMethod() {
        return exception.getNativeMethod();
    }

    @Override
    public void logError() {
        if (hasError()) {
            logger.error(exception.getMessage());
            exception = null;
        }
    }

    @Override
    public NativePeripheralException getException() {
        NativePeripheralException ex = exception;
        exception = null;
        return ex;
    }

    @Override
    public boolean hasError() {
        return exception != null;
    }

    @Override
    public void failOnError() throws NativePeripheralException {
        if (exception != null) {
            NativePeripheralException ex = exception;
            exception = null;
            throw ex;
        }
    }
}
