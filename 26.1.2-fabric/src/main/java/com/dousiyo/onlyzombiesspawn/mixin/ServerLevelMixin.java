package com.dousiyo.onlyzombiesspawn.mixin;

import com.dousiyo.onlyzombiesspawn.OnlyZombiesSpawnLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(method = "addFreshEntity", at = @At("HEAD"), cancellable = true)
    private void onlyzombiesspawn$replaceFreshEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        ServerLevel level = (ServerLevel) (Object) this;
        Entity replacement = OnlyZombiesSpawnLogic.createReplacementIfNeeded(level, entity);
        if (replacement == entity) {
            return;
        }

        if (replacement != null) {
            cir.setReturnValue(level.addFreshEntity(replacement));
            return;
        }

        cir.setReturnValue(false);
    }

    @Inject(method = "tryAddFreshEntityWithPassengers", at = @At("HEAD"), cancellable = true)
    private void onlyzombiesspawn$replaceFreshEntityWithPassengers(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        ServerLevel level = (ServerLevel) (Object) this;
        Entity replacement = OnlyZombiesSpawnLogic.createReplacementIfNeeded(level, entity);
        if (replacement == entity) {
            return;
        }

        if (replacement != null) {
            cir.setReturnValue(level.tryAddFreshEntityWithPassengers(replacement));
            return;
        }

        cir.setReturnValue(false);
    }
}
