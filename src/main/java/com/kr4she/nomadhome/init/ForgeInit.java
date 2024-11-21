package com.kr4she.nomadhome.init;

import com.kr4she.nomadhome.NomadHome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NomadHome.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeInit {
	// Dimension Register

	@SubscribeEvent
	public static void registerDimensions(final RegisterDimensionsEvent event) {
		if (DimensionType.byName(NomadHome.HOME_DIMENSION_TYPE) == null) {
			DimensionManager.registerDimension(NomadHome.HOME_DIMENSION_TYPE, ModInit.HOME_DIMENSION.get(), null, true);
		}
	}
}
