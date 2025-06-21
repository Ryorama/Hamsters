package com.starfish_studios.hamsters;

import com.starfish_studios.hamsters.client.renderer.*;
import com.starfish_studios.hamsters.registry.HamstersBlocks;
import com.starfish_studios.hamsters.registry.HamstersEntityTypes;
import com.starfish_studios.hamsters.registry.HamstersItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import java.util.function.Supplier;

public class HamstersVanillaIntegration {

    public static void serverInit() {}

    @Environment(EnvType.CLIENT)
    public static class Client {

        public static void clientInit() {
            registerEntityModelLayers();
            registerEntityRenderers();
            registerBlockRenderLayers();
            registerItemModelPredicates();
        }

        private static void registerEntityModelLayers() {
            EntityRendererRegistry.register(HamstersEntityTypes.HAMSTER, HamsterRenderer::new);
//            EntityRendererRegistry.register(HamstersEntityTypes.HAMSTER_BALL, HamsterBallRenderer::new);
        }

        private static void registerEntityRenderers() {
            createEntityRenderer(HamstersEntityTypes.SEAT, SeatRenderer::new);
        }

        private static void registerBlockRenderLayers() {
            BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),

                HamstersBlocks.CAGE_PANEL,
                HamstersBlocks.RED_CAGE_PANEL,
                HamstersBlocks.ORANGE_CAGE_PANEL,
                HamstersBlocks.YELLOW_CAGE_PANEL,
                HamstersBlocks.LIME_CAGE_PANEL,
                HamstersBlocks.GREEN_CAGE_PANEL,
                HamstersBlocks.CYAN_CAGE_PANEL,
                HamstersBlocks.BLUE_CAGE_PANEL,
                HamstersBlocks.LIGHT_BLUE_CAGE_PANEL,
                HamstersBlocks.PINK_CAGE_PANEL,
                HamstersBlocks.MAGENTA_CAGE_PANEL,
                HamstersBlocks.PURPLE_CAGE_PANEL,
                HamstersBlocks.WHITE_CAGE_PANEL,
                HamstersBlocks.LIGHT_GRAY_CAGE_PANEL,
                HamstersBlocks.GRAY_CAGE_PANEL,
                HamstersBlocks.BLACK_CAGE_PANEL,
                HamstersBlocks.BROWN_CAGE_PANEL,

                HamstersBlocks.RED_HAMSTER_BOWL,
                HamstersBlocks.ORANGE_HAMSTER_BOWL,
                HamstersBlocks.YELLOW_HAMSTER_BOWL,
                HamstersBlocks.LIME_HAMSTER_BOWL,
                HamstersBlocks.GREEN_HAMSTER_BOWL,
                HamstersBlocks.CYAN_HAMSTER_BOWL,
                HamstersBlocks.BLUE_HAMSTER_BOWL,
                HamstersBlocks.LIGHT_BLUE_HAMSTER_BOWL,
                HamstersBlocks.PINK_HAMSTER_BOWL,
                HamstersBlocks.MAGENTA_HAMSTER_BOWL,
                HamstersBlocks.PURPLE_HAMSTER_BOWL,
                HamstersBlocks.WHITE_HAMSTER_BOWL,
                HamstersBlocks.LIGHT_GRAY_HAMSTER_BOWL,
                HamstersBlocks.GRAY_HAMSTER_BOWL,
                HamstersBlocks.BLACK_HAMSTER_BOWL,
                HamstersBlocks.BROWN_HAMSTER_BOWL
            );
        }

        private static void registerItemModelPredicates() {

            for (int layers = 0; layers < 8; layers++) {
                ItemProperties.register(HamstersItems.HAMSTER, new ResourceLocation("variant"), (itemStack, level, entity, value) -> {
                    CompoundTag compoundTag = itemStack.getTag();
                    String variantTag = "Variant";
                    if (compoundTag != null && compoundTag.contains(variantTag)) return (float) compoundTag.getInt(variantTag) / 7;
                    return 0;
                });
            }

            for (int layers = 0; layers < 5; layers++) {
                ItemProperties.register(HamstersItems.HAMSTER, new ResourceLocation("marking"), (itemStack, level, entity, value) -> {
                    CompoundTag compoundTag = itemStack.getTag();
                    String markingTag = "Marking";
                    if (compoundTag != null && compoundTag.contains(markingTag)) return (float) compoundTag.getInt(markingTag) / 4;
                    return 0;
                });
            }
        }
    }

    @SuppressWarnings("all")
    private static <T extends Entity> void createEntityRenderer(Supplier<EntityType<T>> type, EntityRendererProvider<T> renderProvider) {
        EntityRendererRegistry.register(type.get(), renderProvider);
    }
}