package net.audumla.irrigation;

public interface Crop {

    String getName();
    int getGrowthStage();
    Container getContainer();
}
