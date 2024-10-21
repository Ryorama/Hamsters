package com.starfish_studios.hamsters.registry;

import com.starfish_studios.hamsters.Hamsters;
import com.starfish_studios.hamsters.entity.Hamster;
import com.starfish_studios.hamsters.item.HamsterBallItem;
import com.starfish_studios.hamsters.item.HamsterItem;
import com.starfish_studios.hamsters.item.HamsterWheelItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Supplier;

public class HamstersItems {

    public static final Item HAMSTER_SPAWN_EGG = register("hamster_spawn_egg", new SpawnEggItem(HamstersEntityType.HAMSTER, 16747824, 16775119, new FabricItemSettings()));
    public static final Item HAMSTER_NEW_SPAWN_EGG = register("hamster_new_spawn_egg", new SpawnEggItem(HamstersEntityType.HAMSTER_NEW, 16747824, 16775119, new FabricItemSettings()));
    public static final Item TUNNEL = register("tunnel", new BlockItem(HamstersBlocks.TUNNEL, new FabricItemSettings()));

    public static final Item HAMSTER = register("hamster", new HamsterItem(new FabricItemSettings().stacksTo(1)));

    public static final BlockItem BLUE_HAMSTER_BOWL = (BlockItem) register("blue_hamster_bowl", new BlockItem(HamstersBlocks.BLUE_HAMSTER_BOWL, new FabricItemSettings()));
    public static final BlockItem BLUE_HAMSTER_BOTTLE = (BlockItem) register("blue_hamster_bottle", new BlockItem(HamstersBlocks.BLUE_HAMSTER_BOTTLE, new FabricItemSettings()));
    public static final Item HAMSTER_BALL = register("hamster_ball", new HamsterBallItem(new FabricItemSettings().stacksTo(1)));

    public static final BlockItem HAMSTER_WHEEL = (BlockItem) register("hamster_wheel", new HamsterWheelItem(HamstersBlocks.HAMSTER_WHEEL, new FabricItemSettings()));


    //public static Supplier<Item> registerCaughtMobItem(String name, EntityType entitySupplier, Supplier<? extends Fluid> fluidSupplier, SoundEvent soundSupplier, int variantAmount) {
    //    return registerItem(name, () ->  new HamsterItem(entitySupplier, fluidSupplier.get(), soundSupplier, variantAmount, new Item.Properties().stacksTo(1)));
    //}

    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> item) {
        T registry = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Hamsters.MOD_ID, name), item.get());
        return () -> registry;
    }

    private static Item register(String id, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Hamsters.MOD_ID, id), item);
    }
}
