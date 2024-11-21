package com.kr4she.nomadhome.common.home;

import com.kr4she.nomadhome.NomadHome;
import com.kr4she.nomadhome.api.VIPlayerSaveData;
import com.kr4she.nomadhome.api.VITeleporter;
import com.kr4she.nomadhome.api.VIWorldSaveData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class DimensionTeleporter {

    public static void teleport(ServerPlayerEntity serverPlayer) {
        DimensionType home_dimension = DimensionType.byName(NomadHome.HOME_DIMENSION_TYPE);
        // Get HomeData NBT
        CompoundNBT playernbt = VIPlayerSaveData.readData(serverPlayer, DimensionType.OVERWORLD, NomadHome.MOD_ID);
        String dimKey = "lastDimensionID";
        String posXKey = "lastXPos";
        String posYKey = "lastYPos";
        String posZKey = "lastZPos";
        String playerListKey = "playerList";
        String playerHomeIDKey = serverPlayer.getUniqueID() + "ID";

        // Teleport to Last Dimension
        if (serverPlayer.getServerWorld().getDimension().getType() == home_dimension) {
            // Get data for last location
            int dimID = playernbt.getInt(dimKey);
            double posX = playernbt.getDouble(posXKey);
            double posY = playernbt.getDouble(posYKey);
            double posZ = playernbt.getDouble(posZKey);
            DimensionType dest = DimensionType.getById(dimID);
            if (dest == home_dimension) // Rewrite destination if they somehow saved the homedimension as their last
                dest = DimensionType.OVERWORLD;// position
            Vec3d pos = new Vec3d(posX, posY, posZ);

            // Teleport
            VITeleporter.teleportRequest(serverPlayer, pos, dest);
        }
        // Teleport to HomeDimension
        else {
            // Write Data
            playernbt.putInt(dimKey, serverPlayer.dimension.getId());
            playernbt.putDouble(posXKey, serverPlayer.getPosX());
            playernbt.putDouble(posYKey, serverPlayer.getPosY());
            playernbt.putDouble(posZKey, serverPlayer.getPosZ());
            VIPlayerSaveData.writeData(playernbt, serverPlayer, DimensionType.OVERWORLD, NomadHome.MOD_ID);

            // Get player's home position
            CompoundNBT playerlistnbt = VIWorldSaveData.readData(serverPlayer.getServer(), DimensionType.OVERWORLD,
                    NomadHome.MOD_ID + playerListKey);
            // Add their index to the list if they're not in there
            if (!playerlistnbt.contains(playerHomeIDKey)) {
                // Write new player index to list of homes
                playerlistnbt.putInt(playerHomeIDKey, playerlistnbt.size());
                // Write data
                VIWorldSaveData.writeData(playerlistnbt, serverPlayer.getServer(), DimensionType.OVERWORLD,
                        NomadHome.MOD_ID + playerListKey);
            }
            int[] pos = HomeGenerator.positionFromIndex(playerlistnbt.getInt(playerHomeIDKey));
            // Teleport
            VITeleporter.teleportRequest(serverPlayer, new Vec3d(pos[0], 66, pos[1]), home_dimension);
        }
    }
}
