package com.dousiyo.onlyzombiesspawn.mixin;

import com.dousiyo.onlyzombiesspawn.SpawnReasonTracked;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class EntityMixin implements SpawnReasonTracked {
    @Unique
    private MobSpawnType onlyzombiesspawn$spawnType;

    @Override
    public MobSpawnType onlyzombiesspawn$getSpawnType() {
        return this.onlyzombiesspawn$spawnType;
    }

    @Override
    public void onlyzombiesspawn$setSpawnType(MobSpawnType spawnType) {
        this.onlyzombiesspawn$spawnType = spawnType;
    }
}
