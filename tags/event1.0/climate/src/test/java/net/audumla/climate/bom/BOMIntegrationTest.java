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

import net.audumla.climate.ClimateDataSource;
import net.audumla.climate.ClimateDataSourceFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

public class BOMIntegrationTest {
    @Test
    public void Validate_BOM_Catalogue() {
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setLatitude(-37.84);
        source.setLongitude(144.98);
        try {
            Assert.assertTrue(new BOMClimateObserverCatalogue().validateBOMConnection(source));
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void Validate_BOM_Observation() {
        BOMClimateDataSource source = ClimateDataSourceFactory.newInstance(BOMClimateDataSource.class);
        source.setId("086351");
        try {
            Assert.assertTrue(new BOMPeriodicClimateObserver(source).validateBOMConnection());
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void Validate_FTP_Access() throws IOException {
        InetAddress ftp = Inet4Address.getByName(BOMDataLoader.BOMFTP);
        Assert.assertNotNull(ftp);
        Assert.assertNotNull(ftp.getAddress());
        byte[] addr = ftp.getAddress();
        Assert.assertTrue(addr[0] != 0);
    }

    @Test
    public void Validate_WEB_Access() throws IOException {
        InetAddress www = Inet4Address.getByName(BOMDataLoader.BOMHTTP);
        Assert.assertNotNull(www);
        Assert.assertNotNull(www.getAddress());
        Assert.assertTrue(www.getAddress()[0] != 0);
    }
}
