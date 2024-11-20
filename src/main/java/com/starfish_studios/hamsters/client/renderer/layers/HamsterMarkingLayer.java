package com.starfish_studios.hamsters.client.renderer.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.starfish_studios.hamsters.Hamsters;
import com.starfish_studios.hamsters.entities.Hamster;
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
public class HamsterMarkingLayer extends GeoRenderLayer<Hamster> {

    public HamsterMarkingLayer(GeoRenderer<Hamster> geoRenderer) {
        super(geoRenderer);
    }

    private static final Map<Hamster.Marking, ResourceLocation> TEXTURES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(Hamster.Marking.BANDED, Hamsters.id("textures/entity/hamster/banded.png"));
        hashMap.put(Hamster.Marking.SPOTTED, Hamsters.id("textures/entity/hamster/spotted.png"));
        hashMap.put(Hamster.Marking.ROAN, Hamsters.id("textures/entity/hamster/roan.png"));
        hashMap.put(Hamster.Marking.WHITEBELLY, Hamsters.id("textures/entity/hamster/whitebelly.png"));
    });

    @Override
    public ResourceLocation getTextureResource(Hamster hamster) {
        if (Hamster.Marking.BY_ID[hamster.getMarking()] != Hamster.Marking.BLANK) return TEXTURES.get(Hamster.Marking.BY_ID[hamster.getMarking()]);
        return null;
    }

    @Override
    public void render(PoseStack poseStack, Hamster hamster, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.render(poseStack, hamster, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        if (Hamster.Marking.BY_ID[hamster.getMarking()] == Hamster.Marking.BLANK) return;
        this.getRenderer().actuallyRender(poseStack, hamster, bakedModel, renderType, bufferSource, bufferSource.getBuffer(RenderType.entityTranslucent(this.getTextureResource(hamster))), true, partialTick, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}