package com.dousiyo.onlyzombiesspawn.mixin;

import com.dousiyo.onlyzombiesspawn.SpawnReasonTracked;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class EntityMixin implements SpawnReasonTracked {
    @Unique
    private EntitySpawnReason onlyzombiesspawn$spawnReason;

    @Override
    public EntitySpawnReason onlyzombiesspawn$getSpawnReason() {
        return this.onlyzombiesspawn$spawnReason;
    }

    @Override
    public void onlyzombiesspawn$setSpawnReason(EntitySpawnReason spawnReason) {
        this.onlyzombiesspawn$spawnReason = spawnReason;
    }
}
