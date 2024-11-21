package com.kr4she.nomadhome.common.biomes;

import java.util.Set;
import com.google.common.collect.ImmutableSet;
import com.kr4she.nomadhome.init.ModInit;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;

public class HomeBiomeProvider extends BiomeProvider {

	public HomeBiomeProvider() {
		super(biomeList);
	}

	private static final Set<Biome> biomeList = ImmutableSet.of(ModInit.HOME_BIOME.get());

	@Override
	public Biome getNoiseBiome(int x, int y, int z) {
		return ModInit.HOME_BIOME.get();
	}

}
