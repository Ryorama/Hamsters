package com.starfish_studios.hamsters.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.starfish_studios.hamsters.Hamsters;
import com.starfish_studios.hamsters.client.model.HamsterBallModel;
import com.starfish_studios.hamsters.entities.Hamster;
import com.starfish_studios.hamsters.entities.HamsterBall;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public class HamsterBallRenderer extends GeoEntityRenderer<HamsterBall> {

    private final ResourceLocation HAMSTER_MODEL = Hamsters.id("geo/entity/hamster.geo.json");

    private final EntityRendererProvider.Context renderManager;

    public HamsterBallRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HamsterBallModel());
        this.renderManager = renderManager;
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

    @Override
    public void actuallyRender(PoseStack poseStack, HamsterBall animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (animatable.getFirstPassenger() instanceof Hamster hamster) {
            poseStack.pushPose();

            GeoEntityRenderer<? super Hamster> renderer = (GeoEntityRenderer<? super Hamster>) renderManager.getEntityRenderDispatcher().getRenderer(hamster);

            var type = renderer.getRenderType(hamster, renderer.getTextureLocation(hamster), bufferSource, partialTick);
            var consumer = bufferSource.getBuffer(type);

            renderer.reRender(renderer.getGeoModel().getBakedModel(HAMSTER_MODEL), poseStack, bufferSource, hamster, type, consumer, partialTick, packedLight, packedOverlay, 1f,1f, 1f, 1f);

            poseStack.popPose();

            bufferSource.getBuffer(renderType); //refresh, keep this
        }

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}