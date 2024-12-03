package com.starfish_studios.hamsters.client.model;

import com.starfish_studios.hamsters.Hamsters;
import com.starfish_studios.hamsters.entities.Hamster;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import static com.starfish_studios.hamsters.HamstersConfig.*;

@Environment(EnvType.CLIENT)
public class HamsterModel extends DefaultedEntityGeoModel<Hamster> {

    public HamsterModel() {
        super(Hamsters.id("hamster"), true);
    }

    @Override
    public ResourceLocation getModelResource(Hamster hamster) {
        // return hamster.isBaby() ? Hamsters.id("geo/entity/pinkie.geo.json") : Hamsters.id("geo/entity/hamster.geo.json");
        return Hamsters.id("geo/entity/hamster.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Hamster hamster) {

        // if (hamster.isBaby()) return Hamsters.id("textures/entity/hamster/pinkie.png");

        return switch (hamster.getVariant()) {
            case 0 -> Hamsters.id("textures/entity/hamster/white.png");
            case 1 -> Hamsters.id("textures/entity/hamster/cream.png");
            case 2 -> Hamsters.id("textures/entity/hamster/champagne.png");
            case 3 -> Hamsters.id("textures/entity/hamster/silver_dove.png");
            case 4 -> Hamsters.id("textures/entity/hamster/dove.png");
            case 5 -> Hamsters.id("textures/entity/hamster/chocolate.png");
            case 6 -> Hamsters.id("textures/entity/hamster/black.png");
            default -> throw new IllegalStateException("Unexpected value: " + hamster.getVariant());
        };
    }

    @Override
    public ResourceLocation getAnimationResource(Hamster hamster) {
        return Hamsters.id("animations/hamster.animation.json");
    }

    @Override
    public void setCustomAnimations(Hamster hamster, long instanceId, AnimationState<Hamster> animationState) {

        super.setCustomAnimations(hamster, instanceId, animationState);
        if (animationState == null) return;

        CoreGeoBone root = this.getAnimationProcessor().getBone("root");
        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        CoreGeoBone sleep = this.getAnimationProcessor().getBone("sleep");
        CoreGeoBone cheeks = this.getAnimationProcessor().getBone("cheeks");
        CoreGeoBone leftCheek = this.getAnimationProcessor().getBone("leftCheek");
        CoreGeoBone rightCheek = this.getAnimationProcessor().getBone("rightCheek");

        cheeks.setHidden(hamster.getMainHandItem().isEmpty());
        float cheekDefaultScale = 1.0F;

        if (hamster.getCheekLevel() > 0) {

            cheeks.setHidden(false);
            float cheekScale = cheekDefaultScale + (hamster.getCheekLevel() * 0.2F);

            leftCheek.setScaleX(cheekScale);
            leftCheek.setScaleY(cheekScale);
            leftCheek.setScaleZ(cheekScale);

            rightCheek.setScaleX(cheekScale);
            rightCheek.setScaleY(cheekScale);
            rightCheek.setScaleZ(cheekScale);

        } else {
            cheeks.setScaleX(cheekDefaultScale);
            cheeks.setScaleY(cheekDefaultScale);
            cheeks.setScaleZ(cheekDefaultScale);
        }

        if (hamstersBurst && hamster.getCheekLevel() > 1) root.setRotZ((float) Math.sin(System.currentTimeMillis() * 0.05D) * 0.1F * (hamster.getCheekLevel() * 0.05F));

        // Ensures there are no strange eye glitches when the hamster is sleeping or awake.

        if (!hamster.isBaby()) sleep.setHidden(!hamster.isSleeping());

        float headScale = hamster.isBaby() ? 1.4F : 1.0F;
        if (hamster.isBaby()) head.setPosY(0.0F);

        head.setScaleX(headScale);
        head.setScaleY(headScale);
        head.setScaleZ(headScale);
    }
}