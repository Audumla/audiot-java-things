package net.audumla.climate;

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

import net.audumla.util.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ClimateCalculations {

    protected static boolean sunBelowHorizon(double w, double ws) {
        return w < -1 * ws || w > ws;
    }

    protected static double w(ClimateData data, Date now, int hours) {
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
        double b = ((2 * Math.PI) * (dayOfYear - 81)) / 364;
        Date mid = new Date((now.getTime() + subHoursFromDate(now, hours).getTime()) / 2);
        c.setTime(mid);
        double Lm = data.getDataSource().getLongitude();
        double Lz = Lm;
        double Sc = 0.1645 * Math.sin(2 * b) - 0.1255 * Math.cos(b) - 0.025 * Math.sin(b);
        double midTime = c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE) / 60.0;
        double w = (Math.PI / 12) * ((midTime + 0.06667 * (Lz - Lm) + Sc) - 12);
        return w;

    }

    protected static double Ra(ClimateData data, Date now, int hours) {
        // Extraterrestrial radiation
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
        double Gsc = 0.0820f;
        double dr = (double) (1 + 0.033 * Math.cos(((2 * Math.PI) / 365) * dayOfYear));
        double d = d(now); // decimation
        double j = j(data);
        double ws = ws(data, now);

        if (hours < 24) {
            // formula 28
            // Extraterrestrial radiation for hourly or shorter periods (Ra)
            double w = w(data, now, hours);
            if (sunBelowHorizon(w, ws)) {
                return 0;
            } else {
                double w1 = w - (Math.PI * hours) / 24;
                double w2 = w + (Math.PI * hours) / 24;
                return (12 * 60) / Math.PI * Gsc * dr * ((w2 - w1) * Math.sin(j) * Math.sin(d) + Math.cos(j * Math.cos(d) * (Math.sin(w2) - Math.sin(21))));
            }
        } else {
            // formula 21
            // Extraterrestrial radiation for daily periods (Ra)
            return (double) (((24 * 60) / Math.PI) * Gsc * dr * ((ws * Math.sin(j) * Math.sin(d)) + (Math.cos(j) * Math.cos(d) * Math.sin(ws))));
        }
    }

    protected static double d(Date now) {
        // formula 24
        // solar decimation
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        return (0.409 * Math.sin(((2 * Math.PI * c.get(Calendar.DAY_OF_YEAR)) / 365) - 1.39)); // solar

    }

    protected static double j(ClimateData data) {
        return (Math.PI / 180) * data.getDataSource().getLatitude(); // latitude radians
    }

    protected static double ws(ClimateData data, Date now) {
        return Math.acos(-1 * Math.tan(j(data)) * Math.tan(d(now)));
    }

    protected static double N(ClimateData data, Date now) {
        // formula 34
        // Daylight hours (N)
        return 24 / Math.PI * ws(data, now);
    }

    public static double getSolarRadiationFromTemperature(ClimateData data, Date now, int durationLength) {
        double k = 0.16;
        return k * Math.sqrt(data.getMaximumTemperature() - data.getMinimumTemperature()) * Ra(data, now, durationLength);
    }


    public static double getSolarRadiationFromSunlightHours(ClimateData data, Date now, int durationLength) {
        // this calculation could be moved to the derived observer.
        double n = data.getSunshineHours();
        double as = 0.25d;
        double bs = 0.5d;
        double N = N(data, now);
        double Ra = Ra(data, now, durationLength);
        double Rs1 = (as + (bs * (n / N)));
        double Rs = Rs1 * Ra;
        return Rs;

    }

    protected double Rs(ClimateData data, Date now, int hours) {
        double Rs = 0.0;
        // formula 35
        // Solar radiation (Rs)
        try {
            // if we have measured the solar radiation then use it instead of calculation.
            Rs = data.getSolarRadiation();
        } catch (Exception e3) {
            try {
                Rs = getSolarRadiationFromSunlightHours(data, now, hours);
            } catch (Exception e1) {
                try {
                    Rs = getSolarRadiationFromTemperature(data, now, hours);
                } catch (Exception e2) {
                }
            }
        }
        if (hours < 24) {
            if (sunBelowHorizon(w(data, now, hours), ws(data, now))) {
                return 0.0;
            }
            Rs = Rs / N(data, now);
//			Rs = 0.0;
        }

        return Rs;
    }

    protected double getMeanDailyTemperature(ClimateData data) {
        ClimateObservation obs = data.getObservation(Time.getDayAndYear(data.getTime()), ClimateData.ObservationMatch.CLOSEST);
        ArrayList<Double> temps = new ArrayList<Double>();
        temps.add(data.getMaximumTemperature());
        temps.add(data.getMinimumTemperature());
        while (obs != null) {
            temps.add(obs.getTemperature());
            obs = obs.getNextObservation();
        }
        double total = 0;
        for (double t : temps) {
            total += t;
        }
        return total / temps.size();
    }

    protected double getMeanMonthlyTemperature(ClimateData data) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(data.getTime());
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.MONTH, c1.get(Calendar.MONTH));
        return 0;
    }

    protected double Rso(ClimateData data, Date now, int hours) {
        double Ra = Ra(data, now, hours);
        double Rso = (double) ((0.75 + (0.00002 * data.getDataSource().getElevation())) * Ra);
        return Rso;
    }

    protected double RsRso(double Rs, double Rso, double w, double ws) {
        if (sunBelowHorizon(w, ws)) {
            return 0.8;
        } else {
            return RsRso(Rs, Rso);
        }

    }

    protected double RsRso(double Rs, double Rso) {
        double RsRso = Rs / Rso;
        if (RsRso > 1.0) {
            RsRso = 1.0;
        }
        if (Double.isNaN(RsRso)) {
            RsRso = 1.0;
        }
        return RsRso;
    }

    protected double Rnl(ClimateData data, Date now, int hours) {
        // formula 39
        // Net longwave radiation (Rnl)
        double o = 0.000000004903;
        double oT = 0;
        double Rs = Rs(data, now, hours); // is the global solar exposure (MJm-2day-1), and is the clear-sky solar radiation (MJm-2day-1), which may be
        // calculated
        double Rso = Rso(data, now, hours);
        // Note that the Rs/Rso term in Equation 39 must be limited so that Rs/Rso <= 1.0.
        double RsRso = 0.0;
        if (hours < 24) {
            o = o / 24;// (24 / hours);
            oT = o * Math.pow(T(data, now, hours) + 273.16, 4);
            RsRso = RsRso(Rs, Rso, w(data, now, hours), ws(data, now));
        } else {
            oT = o * ((Math.pow(data.getMaximumTemperature() + 273.16, 4) + Math.pow(data.getMinimumTemperature() + 273.16, 4)) / 2);
            RsRso = RsRso(Rs, Rso);
        }
        // double Tmax = maxTemp; // is the max absolute temperature in the 24-hour period (K=°C+273.16),
        // double Tmin = minTemp; // is the min absolute temperature in the 24-hour period (K=°C+273.16),
        double ea = ea(data, now, hours); // ea is the actual vapour pressure (kPa),
        // The term (0.34-0.14 ea) expresses the correction for air humidity, and will be smaller if the humidity increases
        double eaAdj = (double) (0.34 - (0.14 * Math.sqrt(ea)));
        // The effect of cloudiness is expressed by (1.35 Rs/Rso - 0.35)
        double RsRsoAdj = (double) ((1.35 * RsRso - 0.35));

        // double Rnl = (double) (o * (0.34 - (0.139 * Math.sqrt(ea))) * ((1.35 * (Rs / Rso) - 0.35)) * (temperature));
        double Rnl = oT * eaAdj * RsRsoAdj;
        return Rnl;
    }

    protected double Rns(ClimateData data, Date now, int hours) {
        // formula 38
        // Albedo (a) and net solar radiation (Rns)
        // Net solar or net shortwave radiation (Rns)
        // a albedo or canopy reflection coefficient, which is 0.23 for the hypothetical grass reference crop
        double a = 0.23;
        double Rs = Rs(data, now, hours);
        return (double) ((1 - a) * Rs);
    }

    protected double Rn(ClimateData data, Date now, int hours) {
        // Equation 40
        double Rns = Rns(data, now, hours);
        double Rn1 = Rnl(data, now, hours);
        double Rn = Rns - Rn1;
        return Rn;
    }

    protected double ea(ClimateData data, Date now, int hours) {
        // formula 54
        // actual vapour pressure [kPa]
        double eaHumidity = 0;
        try {
            if (hours < 24) {
                // average hourly actual vapour pressure [kPa]
                ClimateObservation obs = data.getObservation(now, ClimateData.ObservationMatch.CLOSEST);
                Date timelimit = subHoursFromDate(now, hours);
                double t = 0;
                int c = 0;
                while (obs != null && obs.getTime().after(timelimit)) {
                    t += getSaturationVapourPressure(obs.getTemperature()) * obs.getHumidity() / 100;
                    obs = obs.getPreviousObservation();
                    ++c;
                }
                return t / c;
            } else {
                double total = getSaturationVapourPressure(data.getMinimumTemperature()) * (data.getMaximumHumidity() / 100)
                        + getSaturationVapourPressure(data.getMaximumTemperature()) * (data.getMinimumHumidity() / 100);
                int count = 2;
                /*
                 * ClimateObservation obs = data.getObservation(ClimateUtils.getDayAndYear(data.getTime())); while (obs != null) { total +=
				 * obs.getVapourPressure(); obs = obs.getNextObservation(); ++count; }
				 */
                eaHumidity = total / count;
            }
        } catch (UnsupportedOperationException ex) {
            eaHumidity = eaMinTemp(data, now, hours);
        }
        return eaHumidity;
    }

    protected double eaDew(ClimateObservation data) {
        // return (double) (0.6108 * Math.exp((17.27 * data.getDewPoint()) / (data.getDewPoint() + 237.3)));
        return getSaturationVapourPressure(data.getDewPoint());
    }

    protected double eaMinTemp(ClimateData data, Date now, int hours) {
        return getSaturationVapourPressure(data.getMinimumTemperature());
    }

    static public double getSaturationVapourPressure(double t) {
        return (double) (0.6108 * Math.exp((17.27 * t) / (t + 237.3)));
        // return 6.11 * Math.pow(10, ((7.5*t) / (237.7) + t));
    }

    static public double getAtmosphericPressure(double elevation) {
        return (double) (101.3 * Math.pow(((293 - (0.0065 * elevation)) / 293), 5.26));
    }

    static public double getSolarRadiationFromEvaporation(double evaporation) {
        return evaporation / 0.408;
    }

    protected double G(ClimateData data, Date now, int hours) {
        if (hours < 24) {
            if (sunBelowHorizon(w(data, now, hours), ws(data, now))) {
                return 0.5 * Rn(data, now, hours);
            } else {
                return 0.1 * Rn(data, now, hours);
            }
        } else {
            return 0;
        }

    }

    protected double u2(ClimateData data, Date now, int hours) {
        double speed = 0;
        double height = 2;
        if (hours < 24) {
            ClimateObservation obs = data.getObservation(now, ClimateData.ObservationMatch.CLOSEST);
            Date timelimit = subHoursFromDate(now, hours);
            double t = 0;
            int c = 0;
            while (obs != null && obs.getTime().after(timelimit)) {
                height = obs.getWindSpeedHeight();
                t += obs.getWindSpeed();
                obs = obs.getPreviousObservation();
                ++c;
            }
            speed = t / c;
        } else {
            try {
                speed = data.getAverageWindSpeed();
                height = data.getWindSpeedHeight();
            } catch (Exception ex) {
                // need to determine a default
            }
        }

        if (height == 2.0) {
            return speed;
        } else {
            return (double) (speed * (4.87 / (Math.log((67.8 * height) - 5.42))));
        }
    }

    protected static Date subHoursFromDate(Date now, int hours) {
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.add(Calendar.HOUR, hours * -1);
        return c.getTime();
    }

    protected double T(ClimateData data, Date now, int hours) {
        if (hours < 24) {
            ClimateObservation obs = data.getObservation(now, ClimateData.ObservationMatch.CLOSEST);
            Date timelimit = subHoursFromDate(now, hours);
            double t = 0;
            int c = 0;
            while (obs != null && obs.getTime().after(timelimit)) {
                t += obs.getTemperature();
                obs = obs.getPreviousObservation();
                ++c;
            }
            return t / c;
        } else {
            return (data.getMaximumTemperature() + data.getMinimumTemperature()) / 2;
        }
    }

    protected double es(ClimateData data, Date now, int hours) {
        if (hours < 24) {
            ClimateObservation obs = data.getObservation(now, ClimateData.ObservationMatch.CLOSEST);
            Date timelimit = subHoursFromDate(now, hours);
            double t = 0;
            int c = 0;
            while (obs != null && obs.getTime().after(timelimit)) {
                t += getSaturationVapourPressure(obs.getTemperature());
                obs = obs.getPreviousObservation();
                ++c;
            }
            return t / c;
        } else {
            return (getSaturationVapourPressure(data.getMaximumTemperature()) + getSaturationVapourPressure(data.getMinimumTemperature())) / 2;
        }
    }

    protected double D(ClimateData data, Date now, int hours) {
        double T = T(data, now, hours);
        return (4098 * (0.6108 * Math.exp((17.27 * T) / (T + 237.3)))) / Math.pow(T + 237.3, 2);
    }

    protected double g(ClimateData data, Date now, int hours) {
        double P = getAtmosphericPressure(data.getDataSource().getElevation());
        return 0.000665 * P;
    }

    public double ETo(ClimateData data, Date now, int hours) {
        double Rn = Rn(data, now, hours);
        double G = G(data, now, hours);
        double P = getAtmosphericPressure(data.getDataSource().getElevation());
        double T = T(data, now, hours);
        double u2 = u2(data, now, hours);
        double ea = ea(data, now, hours);
        double es = es(data, now, hours);
        double D = D(data, now, hours);
        double g = 0.000665 * P;

        if (hours < 24) {
            return (double) (((0.408 * (D)) * (Rn - G) + (g * (37 / (T + 273)) * u2 * (es - ea))) / (D + (g * (1 + (0.34 * u2)))));
        } else {
            double rRnG = 0.408 * (Rn - G);
            double ru2 = (1 + 0.34 * u2);
            double rD = D / (D + g * ru2);
            double ETo1 = rRnG * rD;
            double ETo2 = (900 / (T + 273)) * u2 * (es - ea) * g / (D + (g * (1 + 0.34 * u2)));
            return ETo1 + ETo2;
            // return (double) (((0.408*(D))*(Rn - G)+(g*(900/(T+273))*u2*(es-ea)))/(D+(g*(1+(0.34*u2)))));
        }

    }

    public double calculateEvapotranspiration(ClimateObserver station, Date now, int hours) {
        // Calendar.HOUR, Calendar.DAY_OF_YEAR, Calendar.MONTH

		/*
         * P atmospheric presssure [kPa] D slope vapour pressure curve [kPa °C-1] Rn net radiation at the crop surface [MJ m-2 day-1] G soil heat flux density
		 * [MJ m-2 day-1] g psychrometric constant [kPa °C-1] ETo reference evapotranspiration [mm day-1], T mean daily air temperature at 2 m height [°C], u2
		 * wind speed at 2 m height [m s-1], es saturation vapour pressure [kPa], ea actual vapour pressure [kPa], es - ea saturation vapour pressure deficit
		 * [kPa], Twet saturation vapour pressure at wet bulb temperature [kPa] Tdew dewpoint temperature is the temperature to which the air needs to be cooled
		 * to make the air saturated
		 */

        return ETo(station.getClimateData(now), now, hours);

    }

}
