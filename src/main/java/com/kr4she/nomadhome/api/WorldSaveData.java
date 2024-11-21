package com.kr4she.nomadhome.api;

import java.util.function.Supplier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

public class WorldSaveData extends WorldSavedData implements Supplier<WorldSaveData> {

	public CompoundNBT data = new CompoundNBT();

	public WorldSaveData(String name) {
		super(name);
	}

	public static WorldSaveData get(MinecraftServer server, DimensionType dim, String modid) {
		DimensionSavedDataManager storage =  server.getWorld(dim).getSavedData();
		String dataFileName = modid + "_" + dim.getId();
		Supplier<WorldSaveData> sup = new WorldSaveData(dataFileName);

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
	public WorldSaveData get() {
		return this;
	}
}
