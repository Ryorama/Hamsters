package com.starfish_studios.hamsters.mixin;

import com.starfish_studios.hamsters.client.renderer.layers.player.PlayerHamsterOnLeftShoulderLayer;
import com.starfish_studios.hamsters.client.renderer.layers.player.PlayerHamsterOnRightShoulderLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> entityModel, float shadowRadius) {
        super(context, entityModel, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void hamsters$onPlayerRendererInit(EntityRendererProvider.Context context, boolean usesSlimModel, CallbackInfo info) {
        this.addLayer(new PlayerHamsterOnLeftShoulderLayer<>(this, context.getModelSet()));
        this.addLayer(new PlayerHamsterOnRightShoulderLayer<>(this, context.getModelSet()));
    }
}