package com.starfish_studios.hamsters.client.model;

import com.starfish_studios.hamsters.Hamsters;
import com.starfish_studios.hamsters.entity.HamsterBall;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

@Environment(EnvType.CLIENT)
public class HamsterBallModel extends DefaultedEntityGeoModel<HamsterBall> {

    public HamsterBallModel() {
        super(Hamsters.id("hamster_ball"), true);
    }

    @Override
    public ResourceLocation getModelResource(HamsterBall animatable) {
        return Hamsters.id("geo/entity/hamster_ball.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HamsterBall animatable) {
        return Hamsters.id("textures/entity/ball/blue.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HamsterBall animatable) {
        return Hamsters.id("animations/hamster_ball.animation.json");
    }

    @Override
    public RenderType getRenderType(HamsterBall animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }

    @SuppressWarnings("all")
    @Override
    public void setCustomAnimations(HamsterBall animatable, long instanceId, AnimationState<HamsterBall> animationState) {

        super.setCustomAnimations(animatable, instanceId, animationState);

        if (animationState == null) return;
        CoreGeoBone root = this.getAnimationProcessor().getBone("root");
    }
}