package com.starfish_studios.hamsters.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.starfish_studios.hamsters.Hamsters;
import com.starfish_studios.hamsters.entity.HamsterNew;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@Environment(EnvType.CLIENT)
public class HamsterCollarLayer extends GeoRenderLayer<HamsterNew> {

    public HamsterCollarLayer(GeoRenderer<HamsterNew> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, HamsterNew animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        ResourceLocation COLLAR = new ResourceLocation(Hamsters.MOD_ID, "textures/entity/hamster/collar.png");
        ResourceLocation COLLAR_TAG = new ResourceLocation(Hamsters.MOD_ID, "textures/entity/hamster/collar_tag.png");


        if (animatable.isTame() && !animatable.isInvisible()) {
            RenderType renderType1 = RenderType.entityCutout(COLLAR);
            RenderType renderType2 = RenderType.entityCutout(COLLAR_TAG);

            float[] fs = animatable.getCollarColor().getTextureDiffuseColors();
            this.getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, renderType1,
                    bufferSource.getBuffer(renderType1), partialTick, packedLight, packedOverlay,
                    fs[0], fs[1], fs[2], 1);
            this.getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, renderType2,
                    bufferSource.getBuffer(renderType2), partialTick, packedLight, packedOverlay,
                    1, 1, 1, 1);
        }


    }
}
