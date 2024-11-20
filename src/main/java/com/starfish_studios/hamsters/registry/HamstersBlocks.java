package com.starfish_studios.hamsters.registry;

import com.starfish_studios.hamsters.Hamsters;
import com.starfish_studios.hamsters.blocks.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.PushReaction;

public class HamstersBlocks {

    // region Common Properties

    public static Block cagePanelBlock(DyeColor dyeColor) {
        return new CagePanelBlock(FabricBlockSettings.create().strength(0.3F).noOcclusion().isSuffocating(Blocks::never).mapColor(dyeColor));
    }

    public static Block hamsterBowlBlock(DyeColor dyeColor) {
        return new HamsterBowlBlock(FabricBlockSettings.create().strength(0.6F).noOcclusion().isSuffocating(Blocks::never).mapColor(dyeColor).pushReaction(PushReaction.DESTROY));
    }

    public static Block hamsterBottleBlock(DyeColor dyeColor) {
        return new HamsterBottleBlock(FabricBlockSettings.create().strength(0.6F).noOcclusion().isSuffocating(Blocks::never).mapColor(dyeColor).pushReaction(PushReaction.DESTROY));
    }

    // endregion

    // region Block Registries

    @SuppressWarnings("unused")
    public static final Block TUNNEL = registerBlock("tunnel", new TunnelBlock(FabricBlockSettings.create().strength(0.6F).noOcclusion().isSuffocating((state, world, pos) -> false).pushReaction(PushReaction.IGNORE)));

    public static final Block HAMSTER_WHEEL = registerBlockWithoutBlockItem("hamster_wheel", new HamsterWheelBlock(FabricBlockSettings.create().strength(0.6F).noOcclusion().isSuffocating((state, world, pos) -> false).pushReaction(PushReaction.IGNORE)));

    public static final Block CAGE_PANEL = registerBlock("cage_panel", cagePanelBlock(DyeColor.WHITE));
    public static final Block RED_CAGE_PANEL = registerBlock("red_cage_panel", cagePanelBlock(DyeColor.RED));
    public static final Block ORANGE_CAGE_PANEL = registerBlock("orange_cage_panel", cagePanelBlock(DyeColor.ORANGE));
    public static final Block YELLOW_CAGE_PANEL = registerBlock("yellow_cage_panel", cagePanelBlock(DyeColor.YELLOW));
    public static final Block LIME_CAGE_PANEL = registerBlock("lime_cage_panel", cagePanelBlock(DyeColor.LIME));
    public static final Block GREEN_CAGE_PANEL = registerBlock("green_cage_panel", cagePanelBlock(DyeColor.GREEN));
    public static final Block CYAN_CAGE_PANEL = registerBlock("cyan_cage_panel", cagePanelBlock(DyeColor.CYAN));
    public static final Block BLUE_CAGE_PANEL = registerBlock("blue_cage_panel", cagePanelBlock(DyeColor.BLUE));
    public static final Block LIGHT_BLUE_CAGE_PANEL = registerBlock("light_blue_cage_panel", cagePanelBlock(DyeColor.LIGHT_BLUE));
    public static final Block PINK_CAGE_PANEL = registerBlock("pink_cage_panel", cagePanelBlock(DyeColor.PINK));
    public static final Block MAGENTA_CAGE_PANEL = registerBlock("magenta_cage_panel", cagePanelBlock(DyeColor.MAGENTA));
    public static final Block PURPLE_CAGE_PANEL = registerBlock("purple_cage_panel", cagePanelBlock(DyeColor.PURPLE));
    public static final Block WHITE_CAGE_PANEL = registerBlock("white_cage_panel", cagePanelBlock(DyeColor.WHITE));
    public static final Block LIGHT_GRAY_CAGE_PANEL = registerBlock("light_gray_cage_panel", cagePanelBlock(DyeColor.LIGHT_GRAY));
    public static final Block GRAY_CAGE_PANEL = registerBlock("gray_cage_panel", cagePanelBlock(DyeColor.GRAY));
    public static final Block BLACK_CAGE_PANEL = registerBlock("black_cage_panel", cagePanelBlock(DyeColor.BLACK));
    public static final Block BROWN_CAGE_PANEL = registerBlock("brown_cage_panel", cagePanelBlock(DyeColor.BROWN));

    public static final Block RED_HAMSTER_BOWL = registerBlock("red_hamster_bowl", hamsterBowlBlock(DyeColor.RED));
    public static final Block ORANGE_HAMSTER_BOWL = registerBlock("orange_hamster_bowl", hamsterBowlBlock(DyeColor.ORANGE));
    public static final Block YELLOW_HAMSTER_BOWL = registerBlock("yellow_hamster_bowl", hamsterBowlBlock(DyeColor.YELLOW));
    public static final Block LIME_HAMSTER_BOWL = registerBlock("lime_hamster_bowl", hamsterBowlBlock(DyeColor.LIME));
    public static final Block GREEN_HAMSTER_BOWL = registerBlock("green_hamster_bowl", hamsterBowlBlock(DyeColor.GREEN));
    public static final Block CYAN_HAMSTER_BOWL = registerBlock("cyan_hamster_bowl", hamsterBowlBlock(DyeColor.CYAN));
    public static final Block BLUE_HAMSTER_BOWL = registerBlock("blue_hamster_bowl", hamsterBowlBlock(DyeColor.BLUE));
    public static final Block LIGHT_BLUE_HAMSTER_BOWL = registerBlock("light_blue_hamster_bowl", hamsterBowlBlock(DyeColor.LIGHT_BLUE));
    public static final Block PINK_HAMSTER_BOWL = registerBlock("pink_hamster_bowl", hamsterBowlBlock(DyeColor.PINK));
    public static final Block MAGENTA_HAMSTER_BOWL = registerBlock("magenta_hamster_bowl", hamsterBowlBlock(DyeColor.MAGENTA));
    public static final Block PURPLE_HAMSTER_BOWL = registerBlock("purple_hamster_bowl", hamsterBowlBlock(DyeColor.PURPLE));
    public static final Block WHITE_HAMSTER_BOWL = registerBlock("white_hamster_bowl", hamsterBowlBlock(DyeColor.WHITE));
    public static final Block LIGHT_GRAY_HAMSTER_BOWL = registerBlock("light_gray_hamster_bowl", hamsterBowlBlock(DyeColor.LIGHT_GRAY));
    public static final Block GRAY_HAMSTER_BOWL = registerBlock("gray_hamster_bowl", hamsterBowlBlock(DyeColor.GRAY));
    public static final Block BLACK_HAMSTER_BOWL = registerBlock("black_hamster_bowl", hamsterBowlBlock(DyeColor.BLACK));
    public static final Block BROWN_HAMSTER_BOWL = registerBlock("brown_hamster_bowl", hamsterBowlBlock(DyeColor.BROWN));

    public static final Block RED_HAMSTER_BOTTLE = registerBlock("red_hamster_bottle", hamsterBottleBlock(DyeColor.RED));
    public static final Block ORANGE_HAMSTER_BOTTLE = registerBlock("orange_hamster_bottle", hamsterBottleBlock(DyeColor.ORANGE));
    public static final Block YELLOW_HAMSTER_BOTTLE = registerBlock("yellow_hamster_bottle", hamsterBottleBlock(DyeColor.YELLOW));
    public static final Block LIME_HAMSTER_BOTTLE = registerBlock("lime_hamster_bottle", hamsterBottleBlock(DyeColor.LIME));
    public static final Block GREEN_HAMSTER_BOTTLE = registerBlock("green_hamster_bottle", hamsterBottleBlock(DyeColor.GREEN));
    public static final Block CYAN_HAMSTER_BOTTLE = registerBlock("cyan_hamster_bottle", hamsterBottleBlock(DyeColor.CYAN));
    public static final Block BLUE_HAMSTER_BOTTLE = registerBlock("blue_hamster_bottle", hamsterBottleBlock(DyeColor.BLUE));
    public static final Block LIGHT_BLUE_HAMSTER_BOTTLE = registerBlock("light_blue_hamster_bottle", hamsterBottleBlock(DyeColor.LIGHT_BLUE));
    public static final Block PINK_HAMSTER_BOTTLE = registerBlock("pink_hamster_bottle", hamsterBottleBlock(DyeColor.PINK));
    public static final Block MAGENTA_HAMSTER_BOTTLE = registerBlock("magenta_hamster_bottle", hamsterBottleBlock(DyeColor.MAGENTA));
    public static final Block PURPLE_HAMSTER_BOTTLE = registerBlock("purple_hamster_bottle", hamsterBottleBlock(DyeColor.PURPLE));
    public static final Block WHITE_HAMSTER_BOTTLE = registerBlock("white_hamster_bottle", hamsterBottleBlock(DyeColor.WHITE));
    public static final Block LIGHT_GRAY_HAMSTER_BOTTLE = registerBlock("light_gray_hamster_bottle", hamsterBottleBlock(DyeColor.LIGHT_GRAY));
    public static final Block GRAY_HAMSTER_BOTTLE = registerBlock("gray_hamster_bottle", hamsterBottleBlock(DyeColor.GRAY));
    public static final Block BLACK_HAMSTER_BOTTLE = registerBlock("black_hamster_bottle", hamsterBottleBlock(DyeColor.BLACK));
    public static final Block BROWN_HAMSTER_BOTTLE = registerBlock("brown_hamster_bottle", hamsterBottleBlock(DyeColor.BROWN));

    // endregion

    @SuppressWarnings("all")
    private static Block registerBlockWithoutBlockItem(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, Hamsters.id(name), block);
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(BuiltInRegistries.BLOCK, Hamsters.id(name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(BuiltInRegistries.ITEM, Hamsters.id(name), new BlockItem(block, new FabricItemSettings()));
    }
}