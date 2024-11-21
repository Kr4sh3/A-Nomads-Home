package com.kr4she.nomadhome;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.kr4she.nomadhome.client.gui.HomeButton;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.io.File;

@Mod.EventBusSubscriber(modid = NomadHome.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Configs {
    private static final ForgeConfigSpec.Builder common_builder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec common_config;

    private static final ForgeConfigSpec.Builder client_builder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec client_config;

    static {
        ClientConfig.init(client_builder);
        CommonConfig.init(common_builder);

        common_config = common_builder.build();
        client_config = client_builder.build();
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        NomadHome.LOGGER.info("Config loading from " + path);
        final CommentedFileConfig file =
                CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        NomadHome.LOGGER.info("Built config at " + path);
        file.load();
        NomadHome.LOGGER.info("Loaded config at " + path);
        config.setConfig(file);
    }

    public static class ClientConfig {
        public static ForgeConfigSpec.IntValue SURVIVAL_BUTTON_POS_X;
        public static ForgeConfigSpec.IntValue SURVIVAL_BUTTON_POS_Y;
        public static ForgeConfigSpec.IntValue CREATIVE_BUTTON_POS_X;
        public static ForgeConfigSpec.IntValue CREATIVE_BUTTON_POS_Y;
        public static ForgeConfigSpec.EnumValue<HomeButton.HomeButtonTexture> SURVIVAL_HOME_BUTTON_TEXTURE;
        public static ForgeConfigSpec.EnumValue<HomeButton.HomeButtonTexture> CREATIVE_HOME_BUTTON_TEXTURE;


        public static void init(ForgeConfigSpec.Builder client) {
            client.comment("Client Configurations");
            SURVIVAL_BUTTON_POS_X =
                    client.comment("\nX Position of the home button in survival mode\nDefault Value: 134").defineInRange(
                            "homebutton.survival_button_x", 134, -10000, 10000);
            SURVIVAL_BUTTON_POS_Y = client.comment("\nY Position of the home button in survival mode\n Default Value:" +
                    " " +
                    "22").defineInRange("homebutton.survival_button_y", 22, -10000, 10000);
            SURVIVAL_HOME_BUTTON_TEXTURE = client.comment("\nThe button texture of the home button in survival " +
                    "mode\nDefault Value: BUTTON").defineEnum("homebutton.survival_button_texture",
                    HomeButton.HomeButtonTexture.BUTTON);
            CREATIVE_BUTTON_POS_X = client.comment("\nX Position of the home button in creative mode\nDefault Value: " +
                    "134").defineInRange("homebutton.creative_button_x", 134, -10000, 10000);
            CREATIVE_BUTTON_POS_Y = client.comment("\nY Position of the home button in creative mode\nDefault Value: " +
                    "50").defineInRange("homebutton.creative_button_y", 50, -10000, 10000);
            CREATIVE_HOME_BUTTON_TEXTURE =
                    client.comment("\nThe button texture of the home button in creative mode\nDefault Value: BUTTON")
                            .defineEnum("homebutton.creative_button_texture", HomeButton.HomeButtonTexture.BUTTON);
        }
    }

    public static class CommonConfig {
        public static ForgeConfigSpec.BooleanValue USE_TIMED_TELEPORTS;
        public static ForgeConfigSpec.BooleanValue USE_SAFE_TELEPORTS;
        public static ForgeConfigSpec.BooleanValue GLOW_WHILE_TELEPORTING;
        public static ForgeConfigSpec.IntValue INVULNERABILITY_DURATION;
        public static ForgeConfigSpec.IntValue TELEPORT_DURATION;

        public static void init(ForgeConfigSpec.Builder common) {
            common.comment("Server Configurations");
            INVULNERABILITY_DURATION =
                    common.comment("\nDuration of the invulnerability gained after teleporting\nDefault Value: 1").defineInRange("teleporting.invulnerability_duration", 1, 0, 10000);
            USE_SAFE_TELEPORTS = common.comment("Should players need to be in a safe area to teleport, and cancel " +
                    "teleporting when hit?").define("teleporting.safe_teleporting", true);
            TELEPORT_DURATION =
                    common.comment("\nHow long a player needs to wait to teleport after clicking the teleport " +
                            "button\nDefault Value: 4").defineInRange("teleporting.teleport_duration", 4, 0, 10000);
            USE_TIMED_TELEPORTS =
                    common.comment("\nShould players need to wait a duration before teleporting?").define(
                            "teleporting" +
                            ".timed_teleporting", true);
            GLOW_WHILE_TELEPORTING = common.comment("\nShould players glow while teleporting?").define("teleporting" +
                    ".glow_while_teleporting", true);
        }
    }
}
