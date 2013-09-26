package com.ftdichip.ftd2xx;

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

import org.apache.log4j.Logger;

public class Localizer {
    private static final Logger logger = Logger.getLogger(Localizer.class);

    public static String getLocalizedMessage(Class<?> clazz, String s) {
        return s;
    }

    public static String getLocalizedMessage(Class<?> clazz, String s, long length, long offset) {
        return s;
    }

    public static String getLocalizedMessage(Class<?> clazz, String s, String message, String message1) {
        return s + message + message1;
    }

    public static String getLocalizedMessage(Class<?> clazz, String s, String message) {
        return s + message;
    }

    public static String getLocalizedMessage(Class<?> clazz, String s, long maxHeaderLength) {
        return s;
    }
}
