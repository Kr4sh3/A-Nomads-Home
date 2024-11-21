package com.kr4she.nomadhome.common.biomes;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

public class HomeBiome extends Biome {

	public HomeBiome() {
		super(new Biome.Builder().category(Category.NONE).depth(0).scale(0).temperature(0.8f).downfall(0f)
				.precipitation(RainType.NONE).waterColor(4159204).waterFogColor(329011)
				.surfaceBuilder(SurfaceBuilder.NOPE, SurfaceBuilder.CORASE_DIRT_DIRT_GRAVEL_CONFIG)
				.parent((String) null));
	}
}