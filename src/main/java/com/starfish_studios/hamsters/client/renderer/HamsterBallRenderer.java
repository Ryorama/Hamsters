package com.starfish_studios.hamsters.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.starfish_studios.hamsters.client.model.HamsterBallModel;
import com.starfish_studios.hamsters.client.renderer.layers.HamsterInBallLayer;
import com.starfish_studios.hamsters.entities.HamsterBall;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public class HamsterBallRenderer extends GeoEntityRenderer<HamsterBall> {

    private final EntityRendererProvider.Context renderManager;

    public HamsterBallRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HamsterBallModel());
        this.renderManager = renderManager;
        this.addRenderLayer(new HamsterInBallLayer(this, renderManager));
    }

    @Override
    protected void applyRotations(HamsterBall hamsterBall, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        float hitRotation = (float) (hamsterBall.level().getGameTime() - hamsterBall.lastHit) + partialTick;
        if (hitRotation < 5.0F) poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(hitRotation / 1.5F * (float) Math.PI) * 5.0F).normalize());
        super.applyRotations(hamsterBall, poseStack, ageInTicks, rotationYaw, partialTick);
    }

    @Override
    public boolean shouldShowName(@NotNull HamsterBall animatable) {
        return false;
    }
}