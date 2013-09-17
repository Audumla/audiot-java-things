package net.audumla.spacetime.astrological;

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

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import net.audumla.spacetime.Geolocation;
import net.audumla.spacetime.Location;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class SunriseSunsetDataTest extends BaseTestCase {
    private static CSVTestDriver driver;
    private static String[] dataSetNames; // The lat/long will be encoded in the filename.

    @BeforeClass
    public static void setupAllTests() {
        driver = new CSVTestDriver("src" + File.separator + "test"
                + File.separator + "resources" + File.separator + "testdata");
        dataSetNames = driver.getFileNames();
    }

    @AfterClass
    public static void tearDownAllTests() {
//        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
    }

    @Test
    public void testRiseAndSetTimes() {
        for (String dataSetName : dataSetNames) {
            List<String[]> data = driver.getData(dataSetName);
            String[] dataSetNameParts = dataSetName.split("\\#");
            String timeZoneName = dataSetNameParts[1].split("\\.")[0].replace('-', '/');
            location = createLocation(dataSetNameParts[0]);

            for (String[] line : data) {
                String date = line[0];
                Calendar calendar = createCalendar(date.split("\\/"));
                SunriseSunsetCalculator calc = new SunriseSunsetCalculator(location, timeZoneName);

                assertTimeEquals(line[1], calc.getAstronomicalSunriseForDate(calendar), date);
                assertTimeEquals(line[8], calc.getAstronomicalSunsetForDate(calendar), date);
                assertTimeEquals(line[2], calc.getNauticalSunriseForDate(calendar), date);
                assertTimeEquals(line[7], calc.getNauticalSunsetForDate(calendar), date);
                assertTimeEquals(line[4], calc.getOfficialSunriseForDate(calendar), date);
                assertTimeEquals(line[5], calc.getOfficialSunsetForDate(calendar), date);
                assertTimeEquals(line[3], calc.getCivilSunriseForDate(calendar), date);
                assertTimeEquals(line[6], calc.getCivilSunsetForDate(calendar), date);
            }
        }
    }

    private Calendar createCalendar(String[] dateParts) {
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.valueOf(dateParts[2]), Integer.valueOf(dateParts[0]) - 1, Integer.valueOf(dateParts[1]));
        return cal;
    }

    private Geolocation createLocation(String fileName) {
        String[] latlong = fileName.split("\\-");
        String latitude = latlong[0].replace('_', '.');
        String longitude = latlong[1].replace('_', '.');

        if (latitude.endsWith("S")) {
            latitude = "-" + latitude;
        }
        if (longitude.endsWith("W")) {
            longitude = "-" + longitude;
        }
        latitude = latitude.substring(0, latitude.length() - 1);
        longitude = longitude.substring(0, longitude.length() - 1);
        return new Location(Double.parseDouble(latitude), Double.parseDouble(longitude),0.0);
    }
}
