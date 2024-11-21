package com.kr4she.nomadhome;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kr4she.nomadhome.client.gui.HomeButton;
import com.kr4she.nomadhome.init.ModInit;
import com.kr4she.nomadhome.util.NomadHomePacketHandler;

@Mod.EventBusSubscriber(modid = NomadHome.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(NomadHome.MOD_ID)
public class NomadHome {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "nomadhome";
    public static final ResourceLocation HOME_DIMENSION_TYPE = new ResourceLocation(MOD_ID, "home_dimension");

    public NomadHome() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configs.client_config);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configs.common_config);

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::doClientStuff);

        ModInit.BIOMES.register(modEventBus);
        ModInit.MOD_DIMENSIONS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        NomadHomePacketHandler.register();

        Configs.loadConfig(Configs.client_config,
                FMLPaths.CONFIGDIR.get().resolve(NomadHome.MOD_ID + "-client.toml").toString());
        Configs.loadConfig(Configs.common_config,
                FMLPaths.CONFIGDIR.get().resolve(NomadHome.MOD_ID + "-common.toml").toString());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(HomeButton.class);
    }
}
