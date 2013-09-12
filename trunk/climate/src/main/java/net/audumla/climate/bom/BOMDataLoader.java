package net.audumla.climate.bom;

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
 *  "AS I BASIS", WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

import net.audumla.collections.ExpiringMap;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class BOMDataLoader {
    private static final Logger LOG = Logger.getLogger(BOMDataLoader.class);
    public static String BOMHTTP = "www.bom.gov.au";
    public static String HTTP = "http://";
    public static String FTP = "ftp://";
    public static String BOMFTP = "ftp2.bom.gov.au";
    public static String BOMBaseFTPDir = "/anon/gen/";
    public static String FWO = "fwo/";
    public static String CLIMATE = "clim_data/";
    private static String localBaseDir = "audumla.net/";
    private static BOMDataLoader instance = new BOMDataLoader();
    //	private FTPClient ftpClient;
    private Map<String, FTPClient> ftpClients = new HashMap<String, FTPClient>();
    private ExpiringMap cache = new ExpiringMap();

    private BOMDataLoader() {
        try {
            PropertiesConfiguration config = new PropertiesConfiguration("bomdatacache.properties");
            cache.loadProperties(config);
        } catch (Exception e) {
            LOG.error("Cannot load BOM data loader config", e);
        }

    }

    public static BOMDataLoader instance() {
        return instance;
    }

    public boolean hasDataExpired(String conntype, String host, String location) {
        return cache.hasDataExpired(generateLocalFileName(conntype, host + "_" + location));
    }

    private String generateLocalFileName(String conntype, String location) {
        return (localBaseDir + conntype + location).replaceAll("/|\\&|\\?|\\:", "_");
    }

    synchronized public BufferedReader getData(String conntype, String host, String location) {
        String tmpFileName = generateLocalFileName(conntype, host + "_" + location);
        boolean loadedRemote = false;
        if (cache.hasDataExpired(tmpFileName)) {
            try {
                File file = new File(new File(System.getProperty("java.io.tmpdir")), tmpFileName);

                if (conntype.equals(FTP)) {
                    storeFTPFile(host, location, file);
                } else {
                    storeHTTPFile(host, location, file);
                }
                cache.add(tmpFileName);
                loadedRemote = true;
            } catch (Exception ex) {
                throw new UnsupportedOperationException("Cannot load data from - " + location, ex);
            }
        }
        try {
            File file = new File(new File(System.getProperty("java.io.tmpdir")), tmpFileName);
            file.setReadOnly();
            FileReader fr = new FileReader(file);
            if (!loadedRemote) {
                LOG.info("Retrieved cached content - " + tmpFileName);
            }
            return new BufferedReader(fr);
        } catch (FileNotFoundException e) {
            throw new UnsupportedOperationException("Cannot load file - " + tmpFileName);
        }
    }

    private boolean storeHTTPFile(String host, String location, File of) throws MalformedURLException, IOException {
        try {
            InputStream is = new URL(HTTP + host + "/" + location).openStream();
            if (of.exists()) {
                of.delete();
            }
            FileOutputStream fos = new FileOutputStream(of);
            of.createNewFile();
            IOUtils.copy(new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8"))), fos);
            LOG.info("Retrieved remote HTTP content - " + host + "/" + location);
        } catch (FileNotFoundException ex) {
            return false;
        }
        return true;
    }

    public FTPFile getFTPFile(String host, String location) throws IOException {
        FTPClient ftp = getFTPClient(host);
        try {
            synchronized (ftp) {
                FTPFile[] files = ftp.listFiles(location);
                if (files.length > 0) {
                    return files[0];
                } else {
                    return null;
                }
            }
        } catch (IOException e) {
            try {
                ftp.logout();
                ftp.disconnect();
            } catch (Exception ex) {
                LOG.error("Failure to close connection", ex);
            }
            throw new UnsupportedOperationException("Error locating File " + host + location + " FTP Code -> " + ftp.getReplyCode(), e);
        } finally {
/*            if (!ftp.completePendingCommand()) {
                ftp.logout();
                ftp.disconnect();
            }
            ftp.disconnect();
*/
        }
    }

    private synchronized FTPClient getFTPClient(String host) {
        FTPClient ftp = ftpClients.get(host);
        if (ftp == null || !ftp.isAvailable() || !ftp.isConnected()) {
            ftp = new FTPClient();
            FTPClientConfig config = new FTPClientConfig();
            ftp.configure(config);
            try {
                ftp.setControlKeepAliveTimeout(30);
                ftp.setControlKeepAliveReplyTimeout(5);
                ftp.setDataTimeout(3000);
                ftp.setDefaultTimeout(1000);
                int reply;
                ftp.connect(host);
                LOG.debug("Connected to " + host);
                reply = ftp.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftp.disconnect();
                    LOG.error("FTP server '" + host + "' refused connection.");
                } else {
                    if (!ftp.login("anonymous", "guest")) {
                        LOG.error("Unable to login to server " + host);
                    }
                    ftp.setSoTimeout(60000);
                    ftp.enterLocalPassiveMode();
                    ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

                }
            } catch (IOException e) {
                LOG.error("Unable to connect to " + host, e);
            }
            ftpClients.put(host, ftp);
        }
        if (!ftp.isConnected() || !ftp.isAvailable()) {
            throw new UnsupportedOperationException("Cannot connect to " + host);
        }
        return ftp;
    }

    private boolean storeFTPFile(String host, String location, File of) throws Exception {
        if (of.exists()) {
            of.delete();
        }
        FileOutputStream fos = new FileOutputStream(of);
        of.createNewFile();
        if (!storeFTPFile(host, location, fos)) {
            of.delete();
            return false;
        }
        return true;
    }

    private boolean storeFTPFile(String host, String location, OutputStream os) {
        FTPClient ftp = getFTPClient(host);
        try {
            if (getFTPFile(host, location) != null) {
                synchronized (ftp) {
                    IOUtils.copy(ftp.retrieveFileStream(location), os);
                }
            } else {
                return false;
            }
        } catch (IOException ex) {
            LOG.warn("Unable to load file " + location + " FTP Reply -> " + ftp.getReplyCode(), ex);
            return false;
        } finally {
            try {
                os.close();
            } catch (Exception ex) {
                LOG.error("Unknown error encountered storing file " + location, ex);
            }
        }
        LOG.info("Retrieved remote FTP content - " + "ftp://" + host + location);
        try {
            if (!ftp.completePendingCommand()) {
                ftp.logout();
                ftp.disconnect();
            }
        } catch (Exception ex) {
            LOG.error("Unknown error encountered storing file " + location, ex);
        }
        return true;
    }
}
