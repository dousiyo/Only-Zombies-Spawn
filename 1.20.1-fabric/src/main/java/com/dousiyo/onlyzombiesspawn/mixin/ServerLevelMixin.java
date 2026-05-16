package com.dousiyo.onlyzombiesspawn.mixin;

import com.dousiyo.onlyzombiesspawn.OnlyZombiesSpawnLogic;
import com.dousiyo.onlyzombiesspawn.SpawnWhitelistConfig;
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
        cir.setReturnValue(this.onlyzombiesspawn$addReplacements(level, entity, replacement, false));
    }

    @Inject(method = "tryAddFreshEntityWithPassengers", at = @At("HEAD"), cancellable = true)
    private void onlyzombiesspawn$replaceFreshEntityWithPassengers(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        ServerLevel level = (ServerLevel) (Object) this;
        Entity replacement = OnlyZombiesSpawnLogic.createReplacementIfNeeded(level, entity);
        if (replacement == entity) {
            return;
        }
        cir.setReturnValue(this.onlyzombiesspawn$addReplacements(level, entity, replacement, true));
    }

    private boolean onlyzombiesspawn$addReplacements(ServerLevel level, Entity original, Entity firstReplacement, boolean withPassengers) {
        if (firstReplacement == null) {
            return false;
        }

        boolean added = withPassengers ? level.tryAddFreshEntityWithPassengers(firstReplacement) : level.addFreshEntity(firstReplacement);
        for (int i = 1; i < SpawnWhitelistConfig.zombieSpawnMultiplier(); i++) {
            Entity replacement = OnlyZombiesSpawnLogic.createReplacementIfNeeded(level, original);
            if (replacement != null && replacement != original) {
                if (withPassengers) {
                    level.tryAddFreshEntityWithPassengers(replacement);
                } else {
                    level.addFreshEntity(replacement);
                }
            }
        }
        return added;
    }
}
