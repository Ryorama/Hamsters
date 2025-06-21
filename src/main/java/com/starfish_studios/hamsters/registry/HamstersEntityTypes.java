package com.starfish_studios.hamsters.registry;

import com.starfish_studios.hamsters.Hamsters;
import com.starfish_studios.hamsters.entities.*;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.function.Supplier;

public class HamstersEntityTypes {

    public static final EntityType<Hamster> HAMSTER = register(
        "hamster",
        FabricEntityTypeBuilder.createMob()
            .entityFactory(Hamster::new)
            .defaultAttributes(Hamster::createAttributes)
            .spawnGroup(MobCategory.CREATURE)
            .spawnRestriction(SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, Hamster::checkHamsterSpawnRules)
            .dimensions(EntityDimensions.scalable(0.5F, 0.5F))
            .trackRangeChunks(10)
    );

//    public static final EntityType<HamsterBall> HAMSTER_BALL = register(
//        "hamster_ball",
//        FabricEntityTypeBuilder.createLiving()
//            .entityFactory(HamsterBall::new)
//            .defaultAttributes(HamsterBall::createAttributes)
//            .spawnGroup(MobCategory.MISC)
//            .dimensions(EntityDimensions.scalable(1.0F, 1.0F))
//            .trackRangeChunks(10)
//    );

    public static final Supplier<EntityType<SeatEntity>> SEAT = registerEntityType("seat", (type, world) -> new SeatEntity(world), MobCategory.MISC, 0.0F, 0.0F);

    static {
        BiomeModifications.addSpawn(BiomeSelectors.tag(HamstersTags.HAS_HAMSTER), MobCategory.CREATURE, HamstersEntityTypes.HAMSTER, 10, 1, 2);
    }

    public static <T extends Entity> Supplier<EntityType<T>> registerEntityType(String name, EntityType.EntityFactory<T> factory, MobCategory category, float width, float height) {
        var registry = Registry.register(BuiltInRegistries.ENTITY_TYPE, Hamsters.id(name), FabricEntityTypeBuilder.create(category, factory).dimensions(EntityDimensions.fixed(width, height)).build());
        return () -> registry;
    }

    private static <T extends Entity> EntityType<T> register(String name, FabricEntityTypeBuilder<T> entityType) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, Hamsters.id(name), entityType.build());
    }
}