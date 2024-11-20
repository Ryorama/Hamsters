package com.starfish_studios.hamsters.client.renderer;

import com.starfish_studios.hamsters.blocks.entity.HamsterWheelBlockEntity;
import com.starfish_studios.hamsters.client.model.HamsterWheelModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

@Environment(EnvType.CLIENT)
public class HamsterWheelRenderer extends GeoBlockRenderer<HamsterWheelBlockEntity> {

    public HamsterWheelRenderer() {
        super(new HamsterWheelModel());
    }
}