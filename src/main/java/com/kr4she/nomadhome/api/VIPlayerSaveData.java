package com.kr4she.nomadhome.api;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.dimension.DimensionType;

public interface VIPlayerSaveData {
    static void writeData(CompoundNBT nbt, ServerPlayerEntity player, DimensionType dimension, String modid) {
        PlayerSaveData saveData = PlayerSaveData.get(player, dimension, modid);
        saveData.data = nbt;
        saveData.markDirty();
    }

    static CompoundNBT readData(ServerPlayerEntity player, DimensionType dimension, String modid) {
        PlayerSaveData saveData = PlayerSaveData.get(player, dimension, modid);
        return saveData.data;
    }
}
