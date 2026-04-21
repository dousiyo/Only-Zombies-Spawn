package com.dousiyo.onlyzombiesspawn.mixin;

import com.dousiyo.onlyzombiesspawn.SpawnReasonTracked;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {
	@Inject(method = "finalizeSpawn", at = @At("HEAD"))
	private void onlyzombiesspawn$trackSpawnReason(
		ServerLevelAccessor level,
		DifficultyInstance difficulty,
		EntitySpawnReason spawnReason,
		SpawnGroupData spawnGroupData,
		CallbackInfoReturnable<SpawnGroupData> cir
	) {
		((SpawnReasonTracked) this).onlyzombiesspawn$setSpawnReason(spawnReason);
	}
}
