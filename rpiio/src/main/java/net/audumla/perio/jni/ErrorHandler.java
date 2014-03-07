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

/**
 * The ErrorHandler interface allows callers of native methods to capture errors that represent the underlying native issue.
 */
public interface ErrorHandler {
    /**
     *
     * @param errorNo the raw error code of the underlying issue
     * @param message a readable message explaing the error
     * @param nativeMethodName the native method name that caused the error
     */
    void handleError(int errorNo, String message, String nativeMethodName);

    /**
     *
     * @return the native error code
     */
    int getErrorCode();

    /**
     *
      * @return the readable message describing the error code
     */
    String getErrorMessage();

    /**
     *
     * @return the name of the native method that caused the error
     */
    String getNativeMethod();

    /**
     * logs the error to the implementations logger and clears any stored error
     */
    void logError();

    /**
     * This call clears any stored error data
     *
     * @return the an exception that represents the native method error
     */
    NativePeripheralException getException();

    /**
     *
     * @return true if an error has been captured otherwise false
     */
    boolean hasError();

    /**
     * This call clears any stored error data
     *
     * @throws NativePeripheralException only if an error has been logged
     */
    void failOnError() throws NativePeripheralException;
}
