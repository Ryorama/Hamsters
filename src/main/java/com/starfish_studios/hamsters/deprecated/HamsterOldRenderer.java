package com.starfish_studios.hamsters.deprecated;

import com.mojang.blaze3d.vertex.PoseStack;
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
public class HamsterOldRenderer extends GeoEntityRenderer<HamsterOld> {

    private static final String HELMET = "armorBipedHead";

    public HamsterOldRenderer(EntityRendererProvider.Context context) {

        super(context, new HamsterOldModel());
        this.shadowRadius = 0.3F;
        this.addRenderLayer(new ItemArmorGeoLayer<>(this) {

            @Nullable @Override
            protected ItemStack getArmorItemForBone(GeoBone bone, HamsterOld animatable) {
                if (bone.getName().equals(HELMET)) return this.helmetStack;
                return null;
            }

            @Override
            protected @NotNull EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, HamsterOld animatable) {
                if (bone.getName().equals(HELMET)) return EquipmentSlot.HEAD;
                return super.getEquipmentSlotForBone(bone, stack, animatable);
            }

            @NotNull @Override
            protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, HamsterOld animatable, HumanoidModel<?> baseModel) {
                if (bone.getName().equals(HELMET)) return baseModel.head;
                return super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
            }
        });

        this.addRenderLayer(new HamsterOldMarkingLayer(this));
    }

    @Override
    public float getMotionAnimThreshold(HamsterOld animatable) {
        return 0.001f;
    }

    @Override
    public void render(@NotNull HamsterOld animatable, float entityYaw, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1F, 1F, 1F);
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}