package com.starfish_studios.hamsters.client.model;

import com.starfish_studios.hamsters.Hamsters;
import com.starfish_studios.hamsters.entity.Hamster;
import com.starfish_studios.hamsters.entity.HamsterNew;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

@Environment(EnvType.CLIENT)
public class HamsterModel extends DefaultedEntityGeoModel<Hamster> {

    public HamsterModel() {
        super(Hamsters.id("hamster"), true);
    }

    @Override
    public ResourceLocation getModelResource(Hamster animatable) {
        // return animatable.isBaby() ? new ResourceLocation(MOD_ID, "geo/entity/pinkie.geo.json") : new ResourceLocation(MOD_ID, "geo/entity/hamster.geo.json");
        return Hamsters.id("geo/entity/hamster.geo.json");
    }

    public static ResourceLocation getVariantTexture2(HamsterNew.Variant variant) {

        ResourceLocation resourceLocation;

        switch (variant) {
            case WHITE -> resourceLocation = Hamsters.id("textures/entity/hamster/white.png");
            case CREAM -> resourceLocation = Hamsters.id("textures/entity/hamster/cream.png");
            case CHAMPAGNE -> resourceLocation = Hamsters.id("textures/entity/hamster/champagne.png");
            case SILVER_DOVE -> resourceLocation = Hamsters.id("textures/entity/hamster/silver_dove.png");
            case DOVE -> resourceLocation = Hamsters.id("textures/entity/hamster/dove.png");
            case CHOCOLATE -> resourceLocation = Hamsters.id("textures/entity/hamster/chocolate.png");
            case BLACK -> resourceLocation = Hamsters.id("textures/entity/hamster/black.png");
            case WILD -> resourceLocation = Hamsters.id("textures/entity/hamster/wild.png");
            default -> throw new IllegalStateException("Unexpected value: " + variant);
        }

        return resourceLocation;
    }

    @SuppressWarnings("unused")
    public static ResourceLocation getVariantTexture(HamsterNew.Variant variant, HamsterNew.Marking marking) {

        ResourceLocation resourceLocation;

        switch (variant) {
            case WHITE -> resourceLocation = Hamsters.id("textures/entity/hamster/white.png");
            case CREAM -> resourceLocation = Hamsters.id("textures/entity/hamster/cream.png");
            case CHAMPAGNE -> resourceLocation = Hamsters.id("textures/entity/hamster/champagne.png");
            case SILVER_DOVE -> resourceLocation = Hamsters.id("textures/entity/hamster/silver_dove.png");
            case DOVE -> resourceLocation = Hamsters.id("textures/entity/hamster/dove.png");
            case CHOCOLATE -> resourceLocation = Hamsters.id("textures/entity/hamster/chocolate.png");
            case BLACK -> resourceLocation = Hamsters.id("textures/entity/hamster/black.png");
            case WILD -> resourceLocation = Hamsters.id("textures/entity/hamster/wild.png");
            default -> throw new IllegalStateException("Unexpected value: " + variant);
        }

        switch (marking) {
            case BLANK -> resourceLocation = Hamsters.id("textures/entity/hamster/blank.png");
            case BANDED -> resourceLocation = Hamsters.id("textures/entity/hamster/banded.png");
            case SPOTS -> resourceLocation = Hamsters.id("textures/entity/hamster/dominant_spots.png");
            case ROAN -> resourceLocation = Hamsters.id("textures/entity/hamster/roan.png");
            case BELLY -> resourceLocation = Hamsters.id("textures/entity/hamster/belly.png");
        }

        return resourceLocation;
    }

    @Override
    public ResourceLocation getTextureResource(Hamster animatable) {

        if (animatable.isBaby()) return Hamsters.id("textures/entity/hamster/pinkie.png");

        return switch (HamsterNew.Variant.getTypeById(animatable.getVariant())) {
            case WHITE -> Hamsters.id("textures/entity/hamster/white.png");
            case CREAM -> Hamsters.id("textures/entity/hamster/cream.png");
            case CHAMPAGNE -> Hamsters.id("textures/entity/hamster/champagne.png");
            case SILVER_DOVE -> Hamsters.id("textures/entity/hamster/silver_dove.png");
            case DOVE -> Hamsters.id("textures/entity/hamster/dove.png");
            case CHOCOLATE -> Hamsters.id("textures/entity/hamster/chocolate.png");
            case BLACK -> Hamsters.id("textures/entity/hamster/black.png");
            default -> Hamsters.id("textures/entity/hamster/wild.png");
        };
    }

    @Override
    public ResourceLocation getAnimationResource(Hamster animatable) {
        return Hamsters.id("animations/hamster.animation.json");
    }

    @Override
    public void setCustomAnimations(Hamster animatable, long instanceId, AnimationState<Hamster> animationState) {

        super.setCustomAnimations(animatable, instanceId, animationState);
        if (animationState == null) return;

        CoreGeoBone head = this.getAnimationProcessor().getBone("head");
        CoreGeoBone sleep = this.getAnimationProcessor().getBone("sleep");
        CoreGeoBone cheeks = this.getAnimationProcessor().getBone("cheeks");

        cheeks.setHidden(animatable.getMainHandItem().isEmpty());

        // Ensures there are no strange eye glitches when the hamster is sleeping or awake.

        if (animatable.isSleeping() && !animatable.isBaby()) sleep.setHidden(false);
        else if (!animatable.isSleeping() && !animatable.isBaby()) sleep.setHidden(true);

        if (animatable.isBaby()) {
            head.setScaleX(1.4F);
            head.setScaleY(1.4F);
            head.setScaleZ(1.4F);
        } else {
            // Setting values to 1.0F here prevents conflicts with GeckoLib and optimization mods.
            // Without this, adult heads will also size up. It's a bug :(
            head.setPosY(0F);
            head.setScaleX(1.0F);
            head.setScaleY(1.0F);
            head.setScaleZ(1.0F);
        }
    }
}