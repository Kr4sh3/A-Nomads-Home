package com.kr4she.nomadhome.init;

import com.kr4she.nomadhome.NomadHome;
import com.kr4she.nomadhome.common.biomes.HomeBiome;
import com.kr4she.nomadhome.common.dimension.ModHomeDimension;
import com.kr4she.nomadhome.util.NomadHomePacketHandler;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = NomadHome.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModInit {

    // Mod Dimension Register
    public static final DeferredRegister<ModDimension> MOD_DIMENSIONS = new DeferredRegister<>(
            ForgeRegistries.MOD_DIMENSIONS, NomadHome.MOD_ID);
    public static final RegistryObject<ModDimension> HOME_DIMENSION = MOD_DIMENSIONS.register("mod_home_dimension",
            ModHomeDimension::new);

    // Biome Register
    public static final DeferredRegister<Biome> BIOMES = new DeferredRegister<>(ForgeRegistries.BIOMES,
            NomadHome.MOD_ID);
    public static final RegistryObject<Biome> HOME_BIOME = BIOMES.register("home_biome", HomeBiome::new);

    public static void registerBiomes() {
        registerBiome(HOME_BIOME.get(), Type.VOID);
    }

    private static void registerBiome(Biome biome, Type... types) {
        BiomeDictionary.addTypes(biome, types);
        BiomeManager.addSpawnBiome(biome);

    }

    @SubscribeEvent
    public static void onRegisterBiomes(final RegistryEvent.Register<Biome> event) {
        ModInit.registerBiomes();
    }

    // Packet Handler Register
    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event) {
        NomadHomePacketHandler.register();
    }
}
