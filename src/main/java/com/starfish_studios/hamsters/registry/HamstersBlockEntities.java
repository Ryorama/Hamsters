package com.starfish_studios.hamsters.registry;

import com.starfish_studios.hamsters.Hamsters;
import com.starfish_studios.hamsters.blocks.entity.HamsterBottleBlockEntity;
import com.starfish_studios.hamsters.blocks.entity.HamsterWheelBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class HamstersBlockEntities {

    public static final BlockEntityType<HamsterWheelBlockEntity> HAMSTER_WHEEL = register("hamster_wheel", FabricBlockEntityTypeBuilder.create(HamsterWheelBlockEntity::new, HamstersBlocks.HAMSTER_WHEEL).build(null));
    public static final BlockEntityType<HamsterBottleBlockEntity> HAMSTER_BOTTLE = register("hamster_bottle", FabricBlockEntityTypeBuilder.create(HamsterBottleBlockEntity::new, HamstersBlocks.BLUE_HAMSTER_BOTTLE).build(null));

    public static <T extends BlockEntityType<?>> T register(String name, T blockEntityType) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Hamsters.id(name), blockEntityType);
    }
}