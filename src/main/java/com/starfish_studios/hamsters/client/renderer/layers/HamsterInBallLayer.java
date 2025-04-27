package com.starfish_studios.hamsters.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.starfish_studios.hamsters.entities.Hamster;
import com.starfish_studios.hamsters.entities.HamsterBall;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class HamsterInBallLayer extends GeoRenderLayer<HamsterBall> {
    private final EntityRendererProvider.Context renderManager;

    public HamsterInBallLayer(GeoRenderer<HamsterBall> entityRendererIn, EntityRendererProvider.Context renderManager) {
        super(entityRendererIn);
        this.renderManager = renderManager;
    }

    @Override
    public void preRender(PoseStack poseStack, HamsterBall animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.preRender(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

        if (animatable.getFirstPassenger() instanceof Hamster hamster) {
            poseStack.pushPose();

            GeoEntityRenderer<? super Hamster> renderer = (GeoEntityRenderer<? super Hamster>) renderManager.getEntityRenderDispatcher().getRenderer(hamster);
            renderer.render(hamster, 0, partialTick, poseStack, bufferSource, packedLight);

            poseStack.popPose();

            bufferSource.getBuffer(renderType);
        }
    }
}