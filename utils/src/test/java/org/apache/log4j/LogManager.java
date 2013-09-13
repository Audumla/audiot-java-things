package org.apache.log4j;

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

import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import java.util.Enumeration;

public class LogManager {

    public static Logger exists(String name) {
        return org.apache.log4j.Logger.exists(name);
    }

    public static Enumeration getCurrentLoggers() {
        return org.apache.log4j.Logger.getCurrentCategories();
    }

    public static Logger getLogger(Class clazz) {
        return org.apache.log4j.Logger.getLogger(clazz);
    }

    public static Logger getLogger(String name) {
        return org.apache.log4j.Logger.getLogger(name);
    }

    public static Logger getLogger(String name, LoggerFactory factory) {
        return org.apache.log4j.Logger.getLogger(name, factory);
    }

    public static LoggerRepository getLoggerRepository() {
        return org.apache.log4j.Logger.getDefaultHierarchy();
    }

    public static Logger getRootLogger() {
        return org.apache.log4j.Logger.getRootLogger();
    }

    public static void resetConfiguration() {
        org.apache.log4j.Logger.getDefaultHierarchy().resetConfiguration();
    }

    public static void setRepositorySelector(RepositorySelector selector, Object guard) {
    }

    public static void shutdown() {
    }
}
