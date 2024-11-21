package com.kr4she.nomadhome.common.dimension;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.OverworldGenSettings;

public class HomeGenerationSettings extends OverworldGenSettings {
	@Override
	public BlockState getDefaultBlock() {
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public BlockState getDefaultFluid() {
		return Blocks.AIR.getDefaultState();
	}
}
