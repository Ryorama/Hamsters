package com.starfish_studios.hamsters.items;

import com.starfish_studios.hamsters.Hamsters;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HamsterWheelItem extends BlockItem implements GeoItem {

    public HamsterWheelItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {

        consumer.accept(new RenderProvider() {

            private GeoItemRenderer<HamsterWheelItem> renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new GeoItemRenderer<>(new DefaultedBlockGeoModel<>(Hamsters.id("hamster_wheel")));
                return this.renderer;
            }
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return GeckoLibUtil.createInstanceCache(this);
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return GeoItem.makeRenderer(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
}