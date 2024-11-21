package com.kr4she.nomadhome.api;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;

public interface VIWorldSaveData {
    static void writeData(CompoundNBT nbt, MinecraftServer server, DimensionType dimension, String modid) {
        WorldSaveData saveData = WorldSaveData.get(server, dimension, modid);
        saveData.data = nbt;
        saveData.markDirty();
    }

    static CompoundNBT readData(MinecraftServer server, DimensionType dimension, String modid) {
        WorldSaveData saveData = WorldSaveData.get(server, dimension, modid);
        return saveData.data;
    }
}
