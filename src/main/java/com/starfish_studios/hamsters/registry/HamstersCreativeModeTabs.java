package com.starfish_studios.hamsters.registry;

import com.starfish_studios.hamsters.Hamsters;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import static com.starfish_studios.hamsters.registry.HamstersBlocks.*;
import static com.starfish_studios.hamsters.registry.HamstersItems.*;

public class HamstersCreativeModeTabs {

    @SuppressWarnings("unused")
    public static final CreativeModeTab ITEM_GROUP = register("item_group", FabricItemGroup.builder().icon(HAMSTER::getDefaultInstance).title(Component.translatable("mod.hamsters")).displayItems((featureFlagSet, output) -> {

        output.accept(HAMSTER);
        output.accept(SEED_MIX);
        output.accept(HAMSTER_SPAWN_EGG);

        output.accept(HamstersItems.HAMSTER_WHEEL);

        output.accept(CAGE_PANEL);
        output.accept(WHITE_CAGE_PANEL);
        output.accept(LIGHT_GRAY_CAGE_PANEL);
        output.accept(GRAY_CAGE_PANEL);
        output.accept(BLACK_CAGE_PANEL);
        output.accept(BROWN_CAGE_PANEL);
        output.accept(RED_CAGE_PANEL);
        output.accept(ORANGE_CAGE_PANEL);
        output.accept(YELLOW_CAGE_PANEL);
        output.accept(LIME_CAGE_PANEL);
        output.accept(GREEN_CAGE_PANEL);
        output.accept(CYAN_CAGE_PANEL);
        output.accept(LIGHT_BLUE_CAGE_PANEL);
        output.accept(BLUE_CAGE_PANEL);
        output.accept(PURPLE_CAGE_PANEL);
        output.accept(MAGENTA_CAGE_PANEL);
        output.accept(PINK_CAGE_PANEL);

        output.accept(WHITE_HAMSTER_BOWL);
        output.accept(LIGHT_GRAY_HAMSTER_BOWL);
        output.accept(GRAY_HAMSTER_BOWL);
        output.accept(BLACK_HAMSTER_BOWL);
        output.accept(BROWN_HAMSTER_BOWL);
        output.accept(RED_HAMSTER_BOWL);
        output.accept(ORANGE_HAMSTER_BOWL);
        output.accept(YELLOW_HAMSTER_BOWL);
        output.accept(LIME_HAMSTER_BOWL);
        output.accept(GREEN_HAMSTER_BOWL);
        output.accept(CYAN_HAMSTER_BOWL);
        output.accept(LIGHT_BLUE_HAMSTER_BOWL);
        output.accept(BLUE_HAMSTER_BOWL);
        output.accept(PURPLE_HAMSTER_BOWL);
        output.accept(MAGENTA_HAMSTER_BOWL);
        output.accept(PINK_HAMSTER_BOWL);

        output.accept(WHITE_HAMSTER_BOTTLE);
        output.accept(LIGHT_GRAY_HAMSTER_BOTTLE);
        output.accept(GRAY_HAMSTER_BOTTLE);
        output.accept(BLACK_HAMSTER_BOTTLE);
        output.accept(BROWN_HAMSTER_BOTTLE);
        output.accept(RED_HAMSTER_BOTTLE);
        output.accept(ORANGE_HAMSTER_BOTTLE);
        output.accept(YELLOW_HAMSTER_BOTTLE);
        output.accept(LIME_HAMSTER_BOTTLE);
        output.accept(GREEN_HAMSTER_BOTTLE);
        output.accept(CYAN_HAMSTER_BOTTLE);
        output.accept(LIGHT_BLUE_HAMSTER_BOTTLE);
        output.accept(BLUE_HAMSTER_BOTTLE);
        output.accept(PURPLE_HAMSTER_BOTTLE);
        output.accept(MAGENTA_HAMSTER_BOTTLE);
        output.accept(PINK_HAMSTER_BOTTLE);

        output.accept(WHITE_HAMSTER_BALL);
        output.accept(LIGHT_GRAY_HAMSTER_BALL);
        output.accept(GRAY_HAMSTER_BALL);
        output.accept(BLACK_HAMSTER_BALL);
        output.accept(BROWN_HAMSTER_BALL);
        output.accept(RED_HAMSTER_BALL);
        output.accept(ORANGE_HAMSTER_BALL);
        output.accept(YELLOW_HAMSTER_BALL);
        output.accept(LIME_HAMSTER_BALL);
        output.accept(GREEN_HAMSTER_BALL);
        output.accept(CYAN_HAMSTER_BALL);
        output.accept(LIGHT_BLUE_HAMSTER_BALL);
        output.accept(BLUE_HAMSTER_BALL);
        output.accept(PURPLE_HAMSTER_BALL);
        output.accept(MAGENTA_HAMSTER_BALL);
        output.accept(PINK_HAMSTER_BALL);

        }).build()
    );

    public static void addToVanillaCreativeTabs() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COLORED_BLOCKS).register(entries ->
            entries.addAfter(Items.PINK_BANNER,

            CAGE_PANEL, WHITE_CAGE_PANEL, LIGHT_GRAY_CAGE_PANEL, GRAY_CAGE_PANEL, BLACK_CAGE_PANEL, BROWN_CAGE_PANEL,
            RED_CAGE_PANEL, ORANGE_CAGE_PANEL, YELLOW_CAGE_PANEL, LIME_CAGE_PANEL, GREEN_CAGE_PANEL, CYAN_CAGE_PANEL,
            LIGHT_BLUE_CAGE_PANEL, BLUE_CAGE_PANEL, PURPLE_CAGE_PANEL, MAGENTA_CAGE_PANEL, PINK_CAGE_PANEL,

            WHITE_HAMSTER_BOWL, LIGHT_GRAY_HAMSTER_BOWL, GRAY_HAMSTER_BOWL, BLACK_HAMSTER_BOWL, BROWN_HAMSTER_BOWL,
            RED_HAMSTER_BOWL, ORANGE_HAMSTER_BOWL, YELLOW_HAMSTER_BOWL, LIME_HAMSTER_BOWL, GREEN_HAMSTER_BOWL, CYAN_HAMSTER_BOWL,
            LIGHT_BLUE_HAMSTER_BOWL, BLUE_HAMSTER_BOWL, PURPLE_HAMSTER_BOWL, MAGENTA_HAMSTER_BOWL, PINK_HAMSTER_BOWL,

            WHITE_HAMSTER_BOTTLE, LIGHT_GRAY_HAMSTER_BOTTLE, GRAY_HAMSTER_BOTTLE, BLACK_HAMSTER_BOTTLE, BROWN_HAMSTER_BOTTLE,
            RED_HAMSTER_BOTTLE, ORANGE_HAMSTER_BOTTLE, YELLOW_HAMSTER_BOTTLE, LIME_HAMSTER_BOTTLE, GREEN_HAMSTER_BOTTLE, CYAN_HAMSTER_BOTTLE,
            LIGHT_BLUE_HAMSTER_BOTTLE, BLUE_HAMSTER_BOTTLE, PURPLE_HAMSTER_BOTTLE, MAGENTA_HAMSTER_BOTTLE, PINK_HAMSTER_BOTTLE
            )
        );

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {

            entries.addAfter(Items.LIGHTNING_ROD, HamstersItems.HAMSTER_WHEEL);

            entries.addBefore(Items.SKELETON_SKULL,

                WHITE_HAMSTER_BOWL, LIGHT_GRAY_HAMSTER_BOWL, GRAY_HAMSTER_BOWL, BLACK_HAMSTER_BOWL, BROWN_HAMSTER_BOWL,
                RED_HAMSTER_BOWL, ORANGE_HAMSTER_BOWL, YELLOW_HAMSTER_BOWL, LIME_HAMSTER_BOWL, GREEN_HAMSTER_BOWL, CYAN_HAMSTER_BOWL,
                LIGHT_BLUE_HAMSTER_BOWL, BLUE_HAMSTER_BOWL, PURPLE_HAMSTER_BOWL, MAGENTA_HAMSTER_BOWL, PINK_HAMSTER_BOWL,

                WHITE_HAMSTER_BOTTLE, LIGHT_GRAY_HAMSTER_BOTTLE, GRAY_HAMSTER_BOTTLE, BLACK_HAMSTER_BOTTLE, BROWN_HAMSTER_BOTTLE,
                RED_HAMSTER_BOTTLE, ORANGE_HAMSTER_BOTTLE, YELLOW_HAMSTER_BOTTLE, LIME_HAMSTER_BOTTLE, GREEN_HAMSTER_BOTTLE, CYAN_HAMSTER_BOTTLE,
                LIGHT_BLUE_HAMSTER_BOTTLE, BLUE_HAMSTER_BOTTLE, PURPLE_HAMSTER_BOTTLE, MAGENTA_HAMSTER_BOTTLE, PINK_HAMSTER_BOTTLE);
        });

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(entries ->
            entries.addAfter(Items.LIGHTNING_ROD, HamstersItems.HAMSTER_WHEEL)
        );

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries ->
            entries.addAfter(Items.TNT_MINECART,

            WHITE_HAMSTER_BALL, LIGHT_GRAY_HAMSTER_BALL, GRAY_HAMSTER_BALL, BLACK_HAMSTER_BALL, BROWN_HAMSTER_BALL,
            RED_HAMSTER_BALL, ORANGE_HAMSTER_BALL, YELLOW_HAMSTER_BALL, LIME_HAMSTER_BALL, GREEN_HAMSTER_BALL, CYAN_HAMSTER_BALL,
            LIGHT_BLUE_HAMSTER_BALL, BLUE_HAMSTER_BALL, PURPLE_HAMSTER_BALL, MAGENTA_HAMSTER_BALL, PINK_HAMSTER_BALL)
        );

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries ->
            entries.addAfter(Items.WHEAT, SEED_MIX)
        );

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries ->
            entries.addBefore(Items.HOGLIN_SPAWN_EGG, HAMSTER_SPAWN_EGG)
        );
    }

    @SuppressWarnings("all")
    private static CreativeModeTab register(String id, CreativeModeTab tab) {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Hamsters.id(id), tab);
    }
}