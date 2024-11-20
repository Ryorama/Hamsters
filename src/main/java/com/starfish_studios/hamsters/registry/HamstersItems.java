package com.starfish_studios.hamsters.registry;

import com.starfish_studios.hamsters.Hamsters;
import com.starfish_studios.hamsters.items.ChocolateHamsterItem;
import com.starfish_studios.hamsters.items.HamsterBallItem;
import com.starfish_studios.hamsters.items.HamsterItem;
import com.starfish_studios.hamsters.items.HamsterWheelItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public class HamstersItems {

    // region Common Properties

    public static Item hamsterBallItem() {
        return new HamsterBallItem(new FabricItemSettings().stacksTo(16));
    }

    // endregion

    // region Item Registries

    public static final Item HAMSTER_SPAWN_EGG = register("hamster_spawn_egg", new SpawnEggItem(HamstersEntityTypes.HAMSTER, 16747824, 16775119, new FabricItemSettings()));
    // public static final Item TUNNEL = register("tunnel", new BlockItem(HamstersBlocks.TUNNEL, new FabricItemSettings()));

    public static final Item SEED_MIX = register("seed_mix", new Item(new FabricItemSettings()));

    public static final Item HAMSTER = register("hamster", new HamsterItem(new FabricItemSettings().stacksTo(1)));
    public static final Item CHOCOLATE_HAMSTER = register("chocolate_hamster", new ChocolateHamsterItem(new FabricItemSettings().stacksTo(1)
    .food(new FoodProperties.Builder().nutrition(2).saturationMod(0.1F).build())));

    public static final Item RED_HAMSTER_BALL = register("red_hamster_ball", hamsterBallItem());
    public static final Item ORANGE_HAMSTER_BALL = register("orange_hamster_ball", hamsterBallItem());
    public static final Item YELLOW_HAMSTER_BALL = register("yellow_hamster_ball", hamsterBallItem());
    public static final Item LIME_HAMSTER_BALL = register("lime_hamster_ball", hamsterBallItem());
    public static final Item GREEN_HAMSTER_BALL = register("green_hamster_ball", hamsterBallItem());
    public static final Item CYAN_HAMSTER_BALL = register("cyan_hamster_ball", hamsterBallItem());
    public static final Item BLUE_HAMSTER_BALL = register("blue_hamster_ball", hamsterBallItem());
    public static final Item LIGHT_BLUE_HAMSTER_BALL = register("light_blue_hamster_ball", hamsterBallItem());
    public static final Item PINK_HAMSTER_BALL = register("pink_hamster_ball", hamsterBallItem());
    public static final Item MAGENTA_HAMSTER_BALL = register("magenta_hamster_ball", hamsterBallItem());
    public static final Item PURPLE_HAMSTER_BALL = register("purple_hamster_ball", hamsterBallItem());
    public static final Item WHITE_HAMSTER_BALL = register("white_hamster_ball", hamsterBallItem());
    public static final Item LIGHT_GRAY_HAMSTER_BALL = register("light_gray_hamster_ball", hamsterBallItem());
    public static final Item GRAY_HAMSTER_BALL = register("gray_hamster_ball", hamsterBallItem());
    public static final Item BLACK_HAMSTER_BALL = register("black_hamster_ball", hamsterBallItem());
    public static final Item BROWN_HAMSTER_BALL = register("brown_hamster_ball", hamsterBallItem());

    public static final Item HAMSTER_WHEEL = register("hamster_wheel", new HamsterWheelItem(HamstersBlocks.HAMSTER_WHEEL, new FabricItemSettings()));

    // endregion

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, Hamsters.id(name), item);
    }
}