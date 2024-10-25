package com.starfish_studios.hamsters.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.starfish_studios.hamsters.client.model.HamsterModel;
import com.starfish_studios.hamsters.client.renderer.layers.HamsterMarkingLayer;
import com.starfish_studios.hamsters.entity.Hamster;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

@Environment(EnvType.CLIENT)
public class HamsterRenderer extends GeoEntityRenderer<Hamster> {

    private static final String HELMET = "armorBipedHead";

    public HamsterRenderer(EntityRendererProvider.Context context) {

        super(context, new HamsterModel());
        this.shadowRadius = 0.3F;

        this.addRenderLayer(new ItemArmorGeoLayer<>(this) {

            @Nullable @Override
            protected ItemStack getArmorItemForBone(GeoBone bone, Hamster animatable) {
                if (bone.getName().equals(HELMET)) return this.helmetStack;
                return null;
            }

            @Override
            protected @NotNull EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, Hamster animatable) {
                if (bone.getName().equals(HELMET)) return EquipmentSlot.HEAD;
                return super.getEquipmentSlotForBone(bone, stack, animatable);
            }

            @NotNull @Override
            protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, Hamster animatable, HumanoidModel<?> baseModel) {
                if (bone.getName().equals(HELMET)) return baseModel.head;
                return super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
            }
        });

        this.addRenderLayer(new HamsterMarkingLayer(this));
    }

    @Override
    public float getMotionAnimThreshold(Hamster animatable) {
        return 0.001f;
    }

    @Override
    public void render(@NotNull Hamster animatable, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1F, 1F, 1F);
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}