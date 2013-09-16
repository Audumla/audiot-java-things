package net.audumla.spacetime;

public interface Geolocation {
    double getLatitude();

    void setLatitude(Double latitude);

    double getLongitude();

    void setLongitude(Double longitude);

    double getElevation();

    void setElevation(Double elevation);
}
