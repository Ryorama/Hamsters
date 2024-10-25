package com.starfish_studios.hamsters;

import com.starfish_studios.hamsters.client.renderer.HamsterWheelRenderer;
import com.starfish_studios.hamsters.registry.HamstersBlockEntities;
import com.starfish_studios.hamsters.registry.HamstersBlocks;
import com.starfish_studios.hamsters.registry.HamstersItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.GeckoLib;

@Environment(EnvType.CLIENT)
public class HamstersClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		HamstersVanillaIntegration.Client.clientInit();
		registerRenderers();
		GeckoLib.initialize();

		ItemProperties.register(HamstersItems.HAMSTER, new ResourceLocation("variant"), (stack, world, entity, num) -> {
			CompoundTag compoundTag = stack.getTag();
			String variantString = "Variant";
			if (compoundTag != null && compoundTag.contains(variantString)) return (float) compoundTag.getInt(variantString) / 7;
			return 0.333F;
		});
	}

	@SuppressWarnings("all")
	public static void registerRenderers() {
		BlockEntityRenderers.register(HamstersBlockEntities.HAMSTER_WHEEL, context -> new HamsterWheelRenderer());
		BlockRenderLayerMapImpl.INSTANCE.putBlock(HamstersBlocks.HAMSTER_WHEEL, RenderType.cutout());
	}
}