package com.starfish_studios.hamsters.client.model;

import com.starfish_studios.hamsters.Hamsters;
import com.starfish_studios.hamsters.block.entity.HamsterWheelBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class HamsterWheelModel extends DefaultedBlockGeoModel<HamsterWheelBlockEntity> {

    public HamsterWheelModel() {
        super(Hamsters.id("hamster_wheel"));
    }

    @Override
    public ResourceLocation getAnimationResource(HamsterWheelBlockEntity hamsterWheel) {
        return Hamsters.id("animations/hamster_wheel.animation.json");
    }

    @Override
    public RenderType getRenderType(HamsterWheelBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutout(getTextureResource(animatable));
    }
}