package com.starfish_studios.hamsters.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.starfish_studios.hamsters.Hamsters;
import com.starfish_studios.hamsters.entities.Hamster;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@Environment(EnvType.CLIENT)
public class HamsterCollarLayer extends GeoRenderLayer<Hamster> {

    public HamsterCollarLayer(GeoRenderer<Hamster> geoRenderer) {
        super(geoRenderer);
    }

    @Override
    public void render(PoseStack poseStack, Hamster hamster, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (hamster.isTame() && !hamster.isInvisible()) {

            RenderType collarRenderer = RenderType.entityCutout(Hamsters.id("textures/entity/hamster/collar.png"));
            RenderType collarTagRenderer = RenderType.entityCutout(Hamsters.id("textures/entity/hamster/collar_tag.png"));
            float[] color = hamster.getCollarColor().getTextureDiffuseColors();

            this.getRenderer().reRender(this.getDefaultBakedModel(hamster), poseStack, bufferSource, hamster, collarRenderer,
            bufferSource.getBuffer(collarRenderer), partialTick, packedLight, packedOverlay,
            color[0], color[1], color[2], 1);

            this.getRenderer().reRender(this.getDefaultBakedModel(hamster), poseStack, bufferSource, hamster, collarTagRenderer,
            bufferSource.getBuffer(collarTagRenderer), partialTick, packedLight, packedOverlay,
            1, 1, 1, 1);
        }

        super.render(poseStack, hamster, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }
}