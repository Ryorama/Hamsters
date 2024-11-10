package com.starfish_studios.hamsters.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.starfish_studios.hamsters.client.model.HamsterBallModel;
import com.starfish_studios.hamsters.entity.HamsterBall;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public class HamsterBallRenderer extends GeoEntityRenderer<HamsterBall> {

    public HamsterBallRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HamsterBallModel());
    }

    @Override
    public void preRender(PoseStack poseStack, HamsterBall animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
    }

    @Override
    public void render(@NotNull HamsterBall animatable, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        super.render(animatable, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    protected void applyRotations(HamsterBall animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - rotationYaw));
        float hitRotation = (float) (animatable.level().getGameTime() - animatable.lastHit) + partialTick;
        if (hitRotation < 5.0F) poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(hitRotation / 1.5F * (float) Math.PI) * 5.0F).normalize());
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
    }

    @Override
    public boolean shouldShowName(@NotNull HamsterBall animatable) {
        return false;
    }
}