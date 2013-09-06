package net.audumla.climate;
/**
 * User: audumla
 * Date: 23/07/13
 * Time: 3:08 PM
 */

import net.audumla.bean.SupportedFunction;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class CallTest {
    private static final Logger logger = LogManager.getLogger(CallTest.class);

    @Test
    public void testDataSourceCoodinates() {
        ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setLatitude(-37.84);
        source.setLongitude(144.98);
        ClimateObserver observer = ClimateObserverCatalogue.getInstance().getClimateObserver(source);

        Assert.assertEquals(observer.getSource().getLatitude(), -37.84, 0.01);
        Assert.assertEquals(observer.getSource().getLongitude(), 144.98, 0.01);
    }

    @Test
    public void recursiveTest() {


        final ClimateDataSource source = ClimateDataSourceFactory.getInstance().newInstance();
        source.setLatitude(-37.84);
        source.setLongitude(144.98);

        ClimateObserver co = new ClimateObserver() {
            boolean recursive = false;

            @Override
            public ClimateData getClimateData(Date date) {
                return new ClimateDataAdaptor(date, getSource()) {
                    @Override
                    @SupportedFunction(supported = true)
                    public double getRainfall() {
                        if (!recursive) {
                            recursive = true;
                            double rain = getProxy().getRainfall();
                            return 1;
                        }
                        Assert.fail();
                        return 0;
                    }
                };
            }

            @Override
            public ClimateDataSource getSource() {
                return source;
            }

            @Override
            public boolean supportsDate(Date date) {
                return true;
            }
        };

        ClimateObserverFactoryListener listener = ClimateObserverCatalogue.getInstance().addClimateObserverFactoryListener(new ClimateObserverFactoryListener() {
            @Override
            public void climateObserverCreated(ClimateObserver observer) {
                ClimateObserverCollection ao = (ClimateObserverCollection) observer;
                ao.addClimateObserverTop(co);
            }
        });
        ClimateObserver observer = ClimateObserverCatalogue.getInstance().getClimateObserver(source);

        double rain = observer.getClimateData(new Date()).getRainfall();
        ClimateObserverCatalogue.getInstance().removeClimateObserverFactoryListener(listener);
        Assert.assertEquals(rain, 1, 0.1);
    }
}
