package net.audumla.utils.jni;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LibraryLoader {
    private static final Logger logger = LoggerFactory.getLogger(LibraryLoader.class);

    private static List<String> loadedLibraries = null;
//    private static FileHandler fileHandler;
//    private static ConsoleHandler consoleHandler;
//    private static boolean initialized = false;

    // private constructor
    private LibraryLoader() {
        // forbid object construction
    }

    public static synchronized void load(String libraryName) {
        load(libraryName, null);
    }

    public static synchronized void load(String libraryName, String fileName) {
        if (fileName == null || fileName.length() == 0) {
            logger.debug("Load library [" + libraryName + "] (no embedded file provided)");
        } else {
            logger.debug("Load library [" + libraryName + "] (embedded file: " + fileName + ")");
        }
        // create instance if null
        if (loadedLibraries == null) {
            loadedLibraries = Collections.synchronizedList(new ArrayList<String>());
        }
        // first, make sure that this library has not already been previously loaded
        if (loadedLibraries.contains(libraryName)) {
            // debug
            logger.debug("Library [" + libraryName + "] has already been loaded");
        } else {
            // ---------------------------------------------
            // ATTEMPT LOAD FROM SYSTEM LIBS
            // ---------------------------------------------

            // assume library loaded successfully, add to tracking collection
            loadedLibraries.add(libraryName);

            try {
                // attempt to load the native library from the system classpath loader
                System.loadLibrary(libraryName);
                logger.debug("Library [" + libraryName + "] loaded from default System.loadLibrary");
            } catch (UnsatisfiedLinkError e) {
                if (fileName == null) {
                    // debug
                    logger.error("Library [" + libraryName + "] could not be located and no embedded file path was provided as auxillary lookup");
                    // library load failed, remove from tracking collection
                    loadedLibraries.remove(libraryName);
                    throw e;
                }
                // ---------------------------------------------
                // ATTEMPT LOAD BASED ON EDUCATED GUESS OF ABI
                // ---------------------------------------------

                // check for system properties
                boolean armhf_force = false;
                if (System.getProperty("pi4j.armhf") != null)
                    armhf_force = true;
                boolean armel_force = false;
                if (System.getProperty("pi4j.armel") != null)
                    armel_force = true;

                URL resourceUrl = LibraryLoader.class.getResource("/lib/" + fileName);

                // first attempt to determine if we are running on a hard float (armhf) based system
                if (armhf_force) {
                    // attempt to get the native library from the JAR file in the 'lib/hard-float' directory
                    resourceUrl = LibraryLoader.class.getResource("/lib/hard-float/" + fileName);
                } else if (armel_force) {
                    // attempt to get the native library from the JAR file in the 'lib/soft-float' directory
                    resourceUrl = LibraryLoader.class.getResource("/lib/soft-float/" + fileName);
                } else {
//                    logger.trace("AUTO-DETECTED HARD-FLOAT ABI" + SystemInfo.isHardFloatAbi());
//                    if (SystemInfo.isHardFloatAbi()) {
//                        // attempt to get the native library from the JAR file in the 'lib/hard-float' directory
//                        resourceUrl = NativeLibraryLoader.class.getResource("/lib/hard-float/" + fileName);
//                    } else {
//                        // attempt to get the native library from the JAR file in the 'lib/soft-float' directory
//                        resourceUrl = NativeLibraryLoader.class.getResource("/lib/soft-float/" + fileName);
//                    }
                }

                try {
                    // load library file from embedded resource
                    loadLibraryFromResource(resourceUrl, libraryName, fileName);
                    logger.trace("Library [" + libraryName + "] loaded successfully using embedded resource file: [" + resourceUrl.toString() + "]");
                } catch (Exception | UnsatisfiedLinkError ex) {
                    // ---------------------------------------------
                    // ATTEMPT LOAD BASED USING HARD-FLOAT (armhf)
                    // ---------------------------------------------

                    // attempt to get the native library from the JAR file in the 'lib/hard-float' directory
                    URL resourceUrlHardFloat = LibraryLoader.class.getResource("/lib/hard-float/" + fileName);

                    try {
                        // load library file from embedded resource
                        loadLibraryFromResource(resourceUrlHardFloat, libraryName, fileName);
                        // debug
                        logger.info("Library [" + libraryName + "] loaded successfully using embedded resource file: [" + resourceUrlHardFloat.toString() + "] (ARMHF)");
                    } catch (UnsatisfiedLinkError ule_hard_float) {
                        // debug
                        logger.trace("Failed to load library [" + libraryName + "] using the System.load(file) method using embedded resource file: [" + resourceUrlHardFloat.toString() + "]");

                        // ---------------------------------------------
                        // ATTEMPT LOAD BASED USING SOFT-FLOAT (armel)
                        // ---------------------------------------------

                        // attempt to get the native library from the JAR file in the 'lib/soft-float' directory
                        URL resourceUrlSoftFloat = LibraryLoader.class.getResource("/lib/soft-float/" + fileName);

                        try {
                            // load library file from embedded resource
                            loadLibraryFromResource(resourceUrlSoftFloat, libraryName, fileName);

                            // debug
                            logger.info("Library [" + libraryName + "] loaded successfully using embedded resource file: [" + resourceUrlSoftFloat.toString() + "] (ARMEL)");
                        } catch (Throwable err) {
                            // debug
                            logger.error("Failed to load library [" + libraryName + "] using System.load(file) as embedded resource file: [" + resourceUrlSoftFloat.toString() + "]", err);

                            // library load failed, remove from tracking collection
                            loadedLibraries.remove(libraryName);

                            logger.error("ERROR:  The native library ["
                                    + libraryName
                                    + " : "
                                    + fileName
                                    + "] could not be found in the JVM library path nor could it be loaded from the embedded JAR resource file; you may need to explicitly define the library path '-Djava.library.path' where this native library can be found.");
                        }
                    } catch (Exception ex_hard_float) {
                        // debug
                        logger.error("Failed to load library [" + libraryName + "] using System.load(file) with embedded resource file: [" + resourceUrlHardFloat.toString() + "]", ex_hard_float);

                        // library load failed, remove from tracking collection
                        loadedLibraries.remove(libraryName);

                        logger.error("ERROR:  The native library ["
                                + libraryName
                                + " : "
                                + fileName
                                + "] could not be found in the JVM library path nor could it be loaded from the embedded JAR resource file; you may need to explicitly define the library path '-Djava.library.path' where this native library can be found.");
                    }
                }
            }
        }
    }

    private static void loadLibraryFromResource(URL resourceUrl, String libraryName, String fileName) throws UnsatisfiedLinkError, Exception {
        // create a 1Kb read buffer
        byte[] buffer = new byte[1024];
        int byteCount = 0;

        // debug
        logger.error("Attempting to load library [" + libraryName + "] using the System.load(file) method using embedded resource file: [" + resourceUrl.toString() + "]");

        // open the resource file stream
        InputStream inputStream = resourceUrl.openStream();

        // get the system temporary directory path
        File tempDirectory = new File(System.getProperty("java.io.tmpdir"));

        // check to see if the temporary path exists
        if (!tempDirectory.exists()) {
            // debug
            logger.warn("The Java system temporary path [" + tempDirectory.getAbsolutePath() + "] does not exist.");

            // instead of the system defined temporary path, let just use the application path
            tempDirectory = new File("");
        }

        // create a temporary file to copy the native library content to
        File tempFile = new File(tempDirectory.getAbsolutePath() + "/" + fileName);

        // make sure that this temporary file does not exist; if it does then delete it
        if (tempFile.exists()) {
            // debug
            logger.warn("The temporary file already exists [" + tempFile.getAbsolutePath() + "]; attempting to delete it now.");

            // delete file immediately
            tempFile.delete();
        }

        // create output stream object
        OutputStream outputStream = null;

        try {
            // create the new file
            outputStream = new FileOutputStream(tempFile);
        } catch (FileNotFoundException fnfe) {
            // error
            logger.error("The temporary file [" + tempFile.getAbsolutePath() + "] cannot be created; it is a directory, not a file.");
            throw (fnfe);
        } catch (SecurityException se) {
            // error
            logger.error("The temporary file [" + tempFile.getAbsolutePath() + "] cannot be created; a security exception was detected. " + se.getMessage());
            throw (se);
        }

        try {
            // copy the library file content
            while ((byteCount = inputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, byteCount);
            }

            // flush all write data from stream
            outputStream.flush();

            // close the output stream
            outputStream.close();
        } catch (IOException ioe) {
            // error
            logger.error("The temporary file [" + tempFile.getAbsolutePath() + "] could not be written to; an IO exception was detected. " + ioe.getMessage());
            throw (ioe);
        }

        // close the input stream
        inputStream.close();

        try {
            // attempt to load the new temporary library file
            System.load(tempFile.getAbsolutePath());

            try {
                // ensure that this temporary file is removed when the program exits
                tempFile.deleteOnExit();
            } catch (SecurityException dse) {
                // warning
                logger.warn("The temporary file [" + tempFile.getAbsolutePath() + "] cannot be flagged for removal on program termination; a security exception was detected. " + dse.getMessage());
            }
        } catch (UnsatisfiedLinkError ule) {
            // if unable to load the library and the temporary file
            // exists; then delete the temporary file immediately
            if (tempFile.exists())
                tempFile.delete();

            throw (ule);
        } catch (Exception ex) {
            // if unable to load the library and the temporary file
            // exists; then delete the temporary file immediately
            if (tempFile.exists()) {
                tempFile.delete();
            }

            throw (ex);
        }
    }
}

