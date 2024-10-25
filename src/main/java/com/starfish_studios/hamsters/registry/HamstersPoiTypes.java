package com.starfish_studios.hamsters.registry;

import com.starfish_studios.hamsters.Hamsters;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;

@SuppressWarnings("unused")
public class HamstersPoiTypes {

    private HamstersPoiTypes() {}

    public static final TagKey<PoiType> HAMSTER_WHEEL = registerPoiTag("hamster_wheel");

    @SuppressWarnings("all")
    private static TagKey<PoiType> registerPoiTag(String id) {
        return TagKey.create(Registries.POINT_OF_INTEREST_TYPE, Hamsters.id(id));
    }
}