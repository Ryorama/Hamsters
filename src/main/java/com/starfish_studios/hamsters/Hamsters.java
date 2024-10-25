package com.starfish_studios.hamsters;

import com.google.common.reflect.Reflection;
import com.starfish_studios.hamsters.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

public class Hamsters implements ModInitializer {

	public static final String MOD_ID = "hamsters";

	public static ResourceLocation id(String name) {
		return new ResourceLocation(MOD_ID, name);
	}

	@Override
	public void onInitialize() {

		FabricLoader.getInstance().getModContainer("create"	).ifPresent(modContainer -> CreateCompat.setup());

		Reflection.initialize(
			HamstersItems.class,
			HamstersBlocks.class,
			HamstersEntityType.class,
			HamstersBlockEntities.class,
			HamstersSoundEvents.class,
			HamstersCreativeModeTab.class
		);
		HamstersVanillaIntegration.serverInit();
	}
}