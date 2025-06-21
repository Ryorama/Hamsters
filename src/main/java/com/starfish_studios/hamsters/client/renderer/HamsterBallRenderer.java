//<<<<<<< Updated upstream
//package com.starfish_studios.hamsters.client.renderer;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//import com.mojang.math.Axis;
//import com.starfish_studios.hamsters.client.model.HamsterBallModel;
//import com.starfish_studios.hamsters.entities.Hamster;
//import com.starfish_studios.hamsters.entities.HamsterBall;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraft.client.renderer.entity.EntityRendererProvider;
//import net.minecraft.util.Mth;
//import org.jetbrains.annotations.NotNull;
//import software.bernie.geckolib.cache.object.BakedGeoModel;
//import software.bernie.geckolib.renderer.GeoEntityRenderer;
//
//@Environment(EnvType.CLIENT)
//public class HamsterBallRenderer extends GeoEntityRenderer<HamsterBall> {
//
//    private final EntityRendererProvider.Context renderManager;
//
//    public HamsterBallRenderer(EntityRendererProvider.Context renderManager) {
//        super(renderManager, new HamsterBallModel());
//        this.renderManager = renderManager;
//    }
//
//    @Override
//    protected void applyRotations(HamsterBall hamsterBall, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
//        float hitRotation = (float) (hamsterBall.level().getGameTime() - hamsterBall.lastHit) + partialTick;
//        if (hitRotation < 5.0F) poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(hitRotation / 1.5F * (float) Math.PI) * 5.0F).normalize());
//        super.applyRotations(hamsterBall, poseStack, ageInTicks, rotationYaw, partialTick);
//    }
//
//    @Override
//    public boolean shouldShowName(@NotNull HamsterBall animatable) {
//        return false;
//    }
//
//    @Override
//    public void actuallyRender(PoseStack poseStack, HamsterBall animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
//        if (animatable.getFirstPassenger() instanceof Hamster hamster) {
//            poseStack.pushPose();
//
//            GeoEntityRenderer<? super Hamster> renderer = (GeoEntityRenderer<? super Hamster>) renderManager.getEntityRenderDispatcher().getRenderer(hamster);
//            poseStack.translate(0.0,0.3,0.0);
//            renderer.render(hamster, hamster.getYRot(), partialTick, poseStack, bufferSource, packedLight);
//
//            poseStack.popPose();
//
//            bufferSource.getBuffer(renderType); //refresh, keep this
//        }
//
//        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
//    }
//}
//=======
////package com.starfish_studios.hamsters.client.renderer;
////
////import com.mojang.blaze3d.vertex.PoseStack;
////import com.mojang.math.Axis;
////import com.starfish_studios.hamsters.client.model.HamsterBallModel;
////import com.starfish_studios.hamsters.entities.HamsterBall;
////import net.fabricmc.api.EnvType;
////import net.fabricmc.api.Environment;
////import net.minecraft.client.renderer.entity.EntityRendererProvider;
////import net.minecraft.util.Mth;
////import org.jetbrains.annotations.NotNull;
////import software.bernie.geckolib.renderer.GeoEntityRenderer;
////
////@Environment(EnvType.CLIENT)
////public class HamsterBallRenderer extends GeoEntityRenderer<HamsterBall> {
////
////    public HamsterBallRenderer(EntityRendererProvider.Context renderManager) {
////        super(renderManager, new HamsterBallModel());
////    }
////
////    @Override
////    protected void applyRotations(HamsterBall hamsterBall, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
////        float hitRotation = (float) (hamsterBall.level().getGameTime() - hamsterBall.lastHit) + partialTick;
////        if (hitRotation < 5.0F) poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(hitRotation / 1.5F * (float) Math.PI) * 5.0F).normalize());
////        super.applyRotations(hamsterBall, poseStack, ageInTicks, rotationYaw, partialTick);
////    }
////
////    @Override
////    public boolean shouldShowName(@NotNull HamsterBall animatable) {
////        return false;
////    }
////}
//>>>>>>> Stashed changes
