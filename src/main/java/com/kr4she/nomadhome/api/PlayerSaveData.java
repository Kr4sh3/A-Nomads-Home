package com.kr4she.nomadhome.api;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

public class PlayerSaveData extends WorldSavedData implements Supplier<PlayerSaveData> {

    public CompoundNBT data = new CompoundNBT();

    public PlayerSaveData(String name) {
        super(name);
    }

    public static PlayerSaveData get(ServerPlayerEntity player, DimensionType dim, String modid) {
        MinecraftServer server = player.getServer();
        assert server != null;
        DimensionSavedDataManager storage = server.getWorld(dim).getSavedData();
        String dataFileName = modid + "_" + player.getUniqueID();
        Supplier<PlayerSaveData> sup = new PlayerSaveData(dataFileName);

        return storage.getOrCreate(sup, dataFileName);
    }

    @Override
    public void read(CompoundNBT nbt) {
        data = nbt.getCompound("HomeData");
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("HomeData", data);
        return nbt;
    }

    @Override
    public PlayerSaveData get() {
        return this;
    }
}
