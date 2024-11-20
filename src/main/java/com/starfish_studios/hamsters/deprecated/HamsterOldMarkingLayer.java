package com.starfish_studios.hamsters.deprecated;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.starfish_studios.hamsters.Hamsters;
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
public class HamsterOldMarkingLayer extends GeoRenderLayer<HamsterOld> {

    public HamsterOldMarkingLayer(GeoRenderer<HamsterOld> entityRendererIn) {
        super(entityRendererIn);
    }

    private static final Map<HamsterOld.Marking, ResourceLocation> TEXTURES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(HamsterOld.Marking.BLANK, Hamsters.id("textures/entity/hamster/blank.png"));
        hashMap.put(HamsterOld.Marking.BANDED, Hamsters.id("textures/entity/hamster/banded.png"));
        hashMap.put(HamsterOld.Marking.DOMINANT_SPOTS, Hamsters.id("textures/entity/hamster/spotted.png"));
        hashMap.put(HamsterOld.Marking.ROAN, Hamsters.id("textures/entity/hamster/roan.png"));
        hashMap.put(HamsterOld.Marking.BELLY, Hamsters.id("textures/entity/hamster/whitebelly.png"));
    });

    @Override
    public ResourceLocation getTextureResource(HamsterOld animatable) {
        if (HamsterOld.Marking.byId(animatable.getMarking()) != HamsterOld.Marking.BLANK) return TEXTURES.get(HamsterOld.Marking.byId(animatable.getMarking()));
        return Hamsters.id("textures/entity/hamster/blank.png");
    }

    @Override
    public void render(PoseStack poseStack, HamsterOld animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (HamsterOld.Marking.byId(animatable.getMarking()) != HamsterOld.Marking.BLANK && !animatable.isBaby()) {
            RenderType entityTranslucent = RenderType.entityTranslucent(getTextureResource(animatable));
            this.getRenderer().actuallyRender(poseStack, animatable, bakedModel, renderType, bufferSource, bufferSource.getBuffer(entityTranslucent), true, partialTick, packedLight, packedOverlay, 1f, 1f, 1f, 1f);
        }

        super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }
}