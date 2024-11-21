package com.kr4she.nomadhome.common.home;

import com.kr4she.nomadhome.NomadHome;
import com.kr4she.nomadhome.api.VIPlayerSaveData;
import com.kr4she.nomadhome.api.VIWorldSaveData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;

@Mod.EventBusSubscriber
public class HomeGenerator {
    private static final int SPACING = 256;
    private static final ResourceLocation default_home_structure = new ResourceLocation(NomadHome.MOD_ID,
            "default_home_structure");

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER && event.getTo() == DimensionType.byName(NomadHome.HOME_DIMENSION_TYPE)) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            CompoundNBT playerdata = VIPlayerSaveData.readData(player, DimensionType.OVERWORLD, NomadHome.MOD_ID);

            if (!playerdata.contains("hasGeneratedPlatform")) {

                String playerListKey = "playerList";
                CompoundNBT playerlistnbt = VIWorldSaveData.readData(player.getServer(), DimensionType.OVERWORLD,
                        NomadHome.MOD_ID + playerListKey);
                DimensionType home_dimension = DimensionType.byName(NomadHome.HOME_DIMENSION_TYPE);
                String playerHomeIDKey = player.getUniqueID() + "ID";
                int[] pos = HomeGenerator.positionFromIndex(playerlistnbt.getInt(playerHomeIDKey));
                MinecraftServer server = player.getServer();
                assert server != null;
                assert home_dimension != null;
                TemplateManager templateManager = server.getWorld(home_dimension).getStructureTemplateManager();
                int offsetX = 16;
                int offsetY = 16;
                int offsetZ = 16;
                BlockPos templatePos = new BlockPos(pos[0] - offsetX, 64 - offsetY, pos[1] - offsetZ);
                Template template = templateManager.getTemplate(default_home_structure);
                template.addBlocksToWorld(server.getWorld(home_dimension), templatePos, new PlacementSettings());
            }
        }
    }

    public static int[] positionFromIndex(int n) {
        int r = (int) (Math.floor((Math.sqrt(n + 1) - 1) / 2) + 1);
        int p = (8 * r * (r - 1)) / 2;
        int en = r * 2;
        int a = (1 + n - p) % (r * 8);
        int[] pos = new int[]{0, 0};
        switch ((int) Math.floor(a / (r * 2))) {
            case 0: {
                pos[0] = a - r;
                pos[1] = -r;
            }
            break;
            case 1: {
                pos[0] = r;
                pos[1] = (a % en) - r;

            }
            break;
            case 2: {
                pos[0] = r - (a % en);
                pos[1] = r;
            }
            break;
            case 3: {
                pos[0] = -r;
                pos[1] = r - (a % en);
            }
            break;
        }
        int spacing = SPACING;
        pos[0] *= spacing;
        pos[1] *= spacing;
        return pos;

    }
}
