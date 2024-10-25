package com.starfish_studios.hamsters;

import com.simibubi.create.content.kinetics.BlockStressDefaults;

public class CreateCompat {

    public static void setup(){
        BlockStressDefaults.DEFAULT_CAPACITIES.put(Hamsters.id("hamster_wheel"), 16.0);
    }
}