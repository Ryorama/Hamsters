package com.starfish_studios.hamsters.entities.util;

import software.bernie.geckolib.animatable.GeoEntity;

public interface HamstersGeoEntity extends GeoEntity {

    @Override
    default double getBoneResetTime() {
        return 5;
    }
}