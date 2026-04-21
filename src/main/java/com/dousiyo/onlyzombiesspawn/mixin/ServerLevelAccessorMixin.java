package com.dousiyo.onlyzombiesspawn.mixin;

import com.dousiyo.onlyzombiesspawn.OnlyZombiesSpawnLogic;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevelAccessor.class)
public interface ServerLevelAccessorMixin {
	@Inject(method = "addFreshEntityWithPassengers", at = @At("HEAD"), cancellable = true)
	private void onlyzombiesspawn$replaceEntityWithPassengers(Entity entity, CallbackInfo ci) {
		ServerLevelAccessor accessor = (ServerLevelAccessor) this;
		Entity replacement = OnlyZombiesSpawnLogic.createReplacementIfNeeded(accessor.getLevel(), entity);
		if (replacement == entity) {
			return;
		}

		if (replacement != null) {
			accessor.addFreshEntityWithPassengers(replacement);
		}

		ci.cancel();
	}
}
