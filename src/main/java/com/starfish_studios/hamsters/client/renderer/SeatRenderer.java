package com.starfish_studios.hamsters.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.starfish_studios.hamsters.entities.SeatEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class SeatRenderer extends EntityRenderer<SeatEntity> {

    public SeatRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @SuppressWarnings("all")
    @Override
    public ResourceLocation getTextureLocation(@NotNull SeatEntity seatEntity) {
        return null;
    }

    @Override
    protected void renderNameTag(@NotNull SeatEntity seatEntity, @NotNull Component component, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int light) {}
}