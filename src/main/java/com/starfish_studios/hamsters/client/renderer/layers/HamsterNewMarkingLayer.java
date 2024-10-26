package com.starfish_studios.hamsters.client.renderer.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.starfish_studios.hamsters.Hamsters;
import com.starfish_studios.hamsters.entity.HamsterNew;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class HamsterNewMarkingLayer extends GeoRenderLayer<HamsterNew> {

    public HamsterNewMarkingLayer(GeoRenderer<HamsterNew> entityRendererIn) {
        super(entityRendererIn);
    }

    private static final Map<HamsterNew.Marking, ResourceLocation> TEXTURES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(HamsterNew.Marking.BLANK, Hamsters.id("textures/entity/hamster/blank.png"));
        hashMap.put(HamsterNew.Marking.BANDED, Hamsters.id("textures/entity/hamster/banded.png"));
        hashMap.put(HamsterNew.Marking.DOMINANT_SPOTS, Hamsters.id("textures/entity/hamster/dominant_spots.png"));
        hashMap.put(HamsterNew.Marking.ROAN, Hamsters.id("textures/entity/hamster/roan.png"));
        hashMap.put(HamsterNew.Marking.BELLY, Hamsters.id("textures/entity/hamster/belly.png"));
    });

    @Override
    public ResourceLocation getTextureResource(HamsterNew animatable) {
        if (HamsterNew.Marking.BY_ID[animatable.getMarking()] != HamsterNew.Marking.BLANK) return TEXTURES.get(HamsterNew.Marking.BY_ID[animatable.getMarking()]);
        return Hamsters.id("textures/entity/hamster/blank.png");
    }

    @Override
    public void render(PoseStack poseStack, HamsterNew animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (HamsterNew.Marking.BY_ID[animatable.getMarking()] != HamsterNew.Marking.BLANK) {
            RenderType entityTranslucent = RenderType.entityTranslucent(getTextureResource(animatable));
            this.getRenderer().actuallyRender(poseStack, animatable, bakedModel, renderType, bufferSource, bufferSource.getBuffer(entityTranslucent), true, partialTick, packedLight, packedOverlay, 1f, 1f, 1f, 1f);
        }

        super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }
}