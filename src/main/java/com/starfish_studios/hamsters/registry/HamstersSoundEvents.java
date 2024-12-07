package com.starfish_studios.hamsters.registry;

import com.starfish_studios.hamsters.Hamsters;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

@SuppressWarnings("unused")
public class HamstersSoundEvents {

    public static final SoundEvent HAMSTER_AMBIENT = registerSoundEvent("entity.hamster.ambient");
    public static final SoundEvent HAMSTER_HURT = registerSoundEvent("entity.hamster.hurt");
    public static final SoundEvent HAMSTER_DEATH = registerSoundEvent("entity.hamster.death");
    public static final SoundEvent HAMSTER_BEG = registerSoundEvent("entity.hamster.beg");
    public static final SoundEvent HAMSTER_EAT = registerSoundEvent("entity.hamster.eat");
    public static final SoundEvent HAMSTER_EXPLODE = registerSoundEvent("entity.hamster.explode");
    public static final SoundEvent HAMSTER_SLEEP = registerSoundEvent("entity.hamster.sleep");
    public static final SoundEvent HAMSTER_SQUISH = registerSoundEvent("entity.hamster.squish");
    public static final SoundEvent HAMSTER_UNSQUISH = registerSoundEvent("entity.hamster.unsquish");
    public static final SoundEvent HAMSTER_PICK_UP = registerSoundEvent("entity.hamster.pick_up");
    public static final SoundEvent HAMSTER_PLACE = registerSoundEvent("entity.hamster.place");

    private static SoundType registerBlockSoundType(String name, float volume, float pitch) {

        String blockString = "block.";

        return new SoundType(volume, pitch,
        registerSoundEvent(blockString + name + ".break"),
        registerSoundEvent(blockString + name + ".step"),
        registerSoundEvent(blockString + name + ".place"),
        registerSoundEvent(blockString + name + ".hit"),
        registerSoundEvent(blockString + name + ".fall"));
    }

    private static SoundEvent registerSoundEvent(String name) {
        ResourceLocation resourceLocation = Hamsters.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, resourceLocation, SoundEvent.createVariableRangeEvent(resourceLocation));
    }
}