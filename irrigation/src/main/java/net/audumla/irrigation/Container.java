package net.audumla.irrigation;

public interface Container {
    static int POTTED = 1;
    static int GARDEN_BED = 2;

    int getType();

    double getDepth();

    double getArea();
}
