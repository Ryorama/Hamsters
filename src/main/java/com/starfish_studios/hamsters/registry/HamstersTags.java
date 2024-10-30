package com.starfish_studios.hamsters.registry;

import com.starfish_studios.hamsters.Hamsters;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("unused")
public class HamstersTags {

    public static final TagKey<Block> HAMSTER_BLOCKS = TagKey.create(Registries.BLOCK, Hamsters.id("hamster_blocks"));
    public static final TagKey<Block> HAMSTER_WHEELS = TagKey.create(Registries.BLOCK, Hamsters.id("hamster_wheels"));

    public static final TagKey<Block> HAMSTERS_SPAWNABLE_ON = TagKey.create(Registries.BLOCK, Hamsters.id("hamsters_spawnable_on"));

    public static final TagKey<Item> CAGE_PANELS = TagKey.create(Registries.ITEM, Hamsters.id("cage_panels"));
    public static final TagKey<Item> HAMSTER_FOOD = TagKey.create(Registries.ITEM, Hamsters.id("hamster_food"));

    public static final TagKey<EntityType<?>> HAMSTER_AVOIDED = TagKey.create(Registries.ENTITY_TYPE, Hamsters.id("hamster_avoided"));

    public static final TagKey<Biome> HAS_HAMSTER = TagKey.create(Registries.BIOME, Hamsters.id("has_hamster"));
}