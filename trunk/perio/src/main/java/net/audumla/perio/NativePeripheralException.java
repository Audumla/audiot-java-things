package net.audumla.perio;

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

public class NativePeripheralException extends PeripheralException {
    private final String nativeMethod;
    private final int errorCode;
    private final String message;

    public NativePeripheralException(int errorNo, String message, String nativeMethodName) {
        super("Error in native code [" + nativeMethodName + "][" + errorNo + ":" + message + "]");
        this.message = message;
        this.errorCode = errorNo;
        this.nativeMethod = nativeMethodName;
    }

    /**
     * @return the raw error message returned by the native method
     */
    public String getNativeMessage() {
        return message;
    }

    /**
     * @return the native method error code
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * @return the name of the native method that caused the error
     */
    public String getNativeMethod() {
        return nativeMethod;
    }
}
