package com.starfish_studios.hamsters.compat;

import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.starfish_studios.hamsters.Hamsters;

public class CreateCompat {

    public static void setup(){
        BlockStressDefaults.DEFAULT_CAPACITIES.put(Hamsters.id("hamster_wheel"), 16.0D);
    }
}