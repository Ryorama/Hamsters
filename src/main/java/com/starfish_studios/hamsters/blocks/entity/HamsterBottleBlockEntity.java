package com.starfish_studios.hamsters.blocks.entity;

import com.starfish_studios.hamsters.registry.HamstersBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HamsterBottleBlockEntity extends BlockEntity {

    public HamsterBottleBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(HamstersBlockEntities.HAMSTER_BOTTLE, blockPos, blockState);
    }
}