package net.audumla.climate.bom;

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
