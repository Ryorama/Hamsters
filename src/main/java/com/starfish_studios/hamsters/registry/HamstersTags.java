package com.starfish_studios.hamsters.registry;

import com.starfish_studios.hamsters.Hamsters;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("unused")
public class HamstersTags {

    // region Block Tags

    public static final TagKey<Block> CAGE_PANELS = TagKey.create(Registries.BLOCK, Hamsters.id("cage_panels"));
    public static final TagKey<Block> HAMSTER_BLOCKS = TagKey.create(Registries.BLOCK, Hamsters.id("hamster_blocks"));
    public static final TagKey<Block> HAMSTER_BOTTLES = TagKey.create(Registries.BLOCK, Hamsters.id("hamster_bottles"));
    public static final TagKey<Block> HAMSTER_BOWLS = TagKey.create(Registries.BLOCK, Hamsters.id("hamster_bowls"));
    public static final TagKey<Block> HAMSTER_WHEELS = TagKey.create(Registries.BLOCK, Hamsters.id("hamster_wheels"));

    public static final TagKey<Block> HAMSTERS_SPAWNABLE_ON = TagKey.create(Registries.BLOCK, Hamsters.id("hamsters_spawnable_on"));

    // endregion

    // region Item Tags

    public static final TagKey<Item> CAGE_PANEL_ITEMS = TagKey.create(Registries.ITEM, Hamsters.id("cage_panels"));
    public static final TagKey<Item> HAMSTER_BLOCK_ITEMS = TagKey.create(Registries.ITEM, Hamsters.id("hamster_blocks"));
    public static final TagKey<Item> HAMSTER_BOTTLE_ITEMS = TagKey.create(Registries.ITEM, Hamsters.id("hamster_bottles"));
    public static final TagKey<Item> HAMSTER_BOWL_ITEMS = TagKey.create(Registries.ITEM, Hamsters.id("hamster_bowls"));
    public static final TagKey<Item> HAMSTER_WHEEL_ITEMS = TagKey.create(Registries.ITEM, Hamsters.id("hamster_wheels"));

    public static final TagKey<Item> HAMSTER_FOOD = TagKey.create(Registries.ITEM, Hamsters.id("hamster_food"));
    public static final TagKey<Item> HAMSTER_BREEDING_FOOD = TagKey.create(Registries.ITEM, Hamsters.id("hamster_breeding_food"));

    // endregion

    // region Entity Type Tags

    public static final TagKey<EntityType<?>> HAMSTER_AVOIDED = TagKey.create(Registries.ENTITY_TYPE, Hamsters.id("hamster_avoided"));

    // endregion

    // region Damage Type Tags

    public static final TagKey<DamageType> SQUISHES_HAMSTERS = TagKey.create(Registries.DAMAGE_TYPE, Hamsters.id("squishes_hamsters"));

    // endregion

    // region Biome Tags

    public static final TagKey<Biome> HAS_HAMSTER = TagKey.create(Registries.BIOME, Hamsters.id("has_hamster"));

    // endregion
}