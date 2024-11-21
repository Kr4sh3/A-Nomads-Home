package com.kr4she.nomadhome.common.dimension;

import java.util.Random;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;

public class HomeChunkGenerator extends NoiseChunkGenerator<OverworldGenSettings> {

	public HomeChunkGenerator(IWorld worldIn, BiomeProvider provider, OverworldGenSettings settingsIn) {
		super(worldIn, provider, 4, 8, 256, settingsIn, true);
	}

	@Override
	protected void makeBedrock(IChunk chunkIn, Random rand) {

	}

	@Override
	protected double[] getBiomeNoiseColumn(int noiseX, int noiseZ) {
		return null;
	}

	@Override
	protected double func_222545_a(double p_222545_1_, double p_222545_3_, int p_222545_5_) {
		return 0;
	}

	@Override
	protected void fillNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ) {
	}

	@Override
	public int getGroundHeight() {
		return 0;
	}
}
