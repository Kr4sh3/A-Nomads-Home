package com.kr4she.nomadhome.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import com.kr4she.nomadhome.Configs;
import com.kr4she.nomadhome.NomadHome;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;

@EventBusSubscriber
public class VITeleporter {

    // Used to identify an instance of a teleporter
    public UUID IDENTIFIER;
    // Used to decrease the duration of invulnerability
    protected int invulnerabilityCounter = WAIT_INTERVAL;
    // Used to determine whether a player is currently waiting to teleport
    protected boolean isTeleporting = false;
    // Used to decrease the time till teleport
    protected int teleportCounter = 0;
    // Used to hold the player the teleporter should attempt to teleport
    protected ServerPlayerEntity teleporterPlayer = null;
    // Holds the position the player is teleporting to
    protected Vec3d teleporterPosition = new Vec3d(0, 100, 0);
    // Holds the dimension the player is teleporting to
    protected DimensionType teleporterDimension;
    // Holds whether we are invulnerable or not
    protected boolean invulnerable = false;

    // Static

    // A list of all instances of DimensionTeleporter
    protected static List<VITeleporter> INSTANCES = new ArrayList<>();
    // Used to convert seconds to ticks
    protected static final int WAIT_INTERVAL = 40;

    // Constructor
    protected VITeleporter(UUID identifier) {
        INSTANCES.add(this);
        IDENTIFIER = identifier;
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Get DimensionTeleporter for serverPlayer, return new one if one does not
    // exist
    protected static VITeleporter get(ServerPlayerEntity serverPlayer) {
        UUID identifier = serverPlayer.getUniqueID();
        if (!INSTANCES.isEmpty()) {
            for (VITeleporter teleporter : INSTANCES) {
                if (teleporter.IDENTIFIER == identifier)
                    return teleporter;
            }
        }
        return new VITeleporter(identifier);
    }

    // Attempts to teleport the player, can be called from anywhere provided the
    // thread is serverside
    public static void teleportRequest(ServerPlayerEntity serverPlayer, Vec3d pos, DimensionType dest) {
        // Check for correct thread
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
            safeTeleport(serverPlayer, pos, dest);
        } else {
            NomadHome.LOGGER.error("A teleport request was sent from a non server-side thread!");
        }
    }

    public static void teleportRequest(ServerPlayerEntity serverPlayer, Vec3d pos) {
        teleportRequest(serverPlayer, pos, serverPlayer.dimension);
    }

    protected static void safeTeleport(ServerPlayerEntity serverPlayer, Vec3d pos, DimensionType dest) {
        // Use safe teleporting if enabled
        if (Configs.CommonConfig.USE_SAFE_TELEPORTS.get() && !serverPlayer.isCreative()) {
            Pair<Boolean, String> isTeleportSafe = teleportSafe(serverPlayer);
            if (isTeleportSafe.getFirst()) {
                timedTeleport(serverPlayer, pos, dest);
            } else {
                serverPlayer.sendStatusMessage(new TranslationTextComponent(isTeleportSafe.getSecond()), true);
            }
        } else {
            timedTeleport(serverPlayer, pos, dest);
        }
    }

    protected static Pair<Boolean, String> teleportSafe(ServerPlayerEntity serverPlayer) {
        Vec3d at = serverPlayer.getPositionVec();
        // Return false if monsters are nearby
        if (serverPlayer.getAttackingEntity() != null) {
            return new Pair<>(false, "teleport.nomadhome.incombat");
        }
        Vec3d vec3d = new Vec3d(at.getX() + 0.5D, at.getY(), at.getZ() + 0.5D);
        List<MonsterEntity> list = serverPlayer.world
                .getEntitiesWithinAABB(
                        MonsterEntity.class, new AxisAlignedBB(vec3d.getX() - 8.0D, vec3d.getY() - 5.0D,
                                vec3d.getZ() - 8.0D, vec3d.getX() + 8.0D, vec3d.getY() + 5.0D, vec3d.getZ() + 8.0D),
                        (p_213820_1_) -> p_213820_1_.isPreventingPlayerRest(serverPlayer));
        if (!list.isEmpty()) {
            return new Pair<>(false, "teleport.nomadhome.notsafe");
        }
        return new Pair<>(true, "");
    }

    protected static void timedTeleport(ServerPlayerEntity serverPlayer, Vec3d pos, DimensionType dest) {
        VITeleporter teleporter = VITeleporter.get(serverPlayer);
        if (Configs.CommonConfig.USE_TIMED_TELEPORTS.get() && teleporter.isTeleporting) {
            teleporter.cancelTeleport(); // Will also set isTeleporting to false
        } else {
            if (Configs.CommonConfig.USE_TIMED_TELEPORTS.get() && !serverPlayer.isCreative() && !serverPlayer.isSpectator())
                teleporter.beginTeleport(serverPlayer, pos, dest);
            else
                teleporter.teleportPlayer(serverPlayer, pos, dest);
        }
    }

    protected void beginTeleport(ServerPlayerEntity serverPlayer, Vec3d pos, DimensionType dest) {
        serverPlayer.sendStatusMessage(new TranslationTextComponent("teleport.nomadhome.teleporting"), true);
        // Setup our timed teleport
        this.isTeleporting = true;
        this.teleportCounter = Math.round(Configs.CommonConfig.TELEPORT_DURATION.get() * WAIT_INTERVAL);
        this.teleporterPlayer = serverPlayer;
        this.teleporterPosition = pos;
        this.teleporterDimension = dest;
        if (Configs.CommonConfig.GLOW_WHILE_TELEPORTING.get())
            serverPlayer.setGlowing(true);
    }

    protected void teleportPlayer(ServerPlayerEntity serverPlayer, Vec3d pos, DimensionType dest) {
        // Change dimension if we have a dimension dest
        if (dest != serverPlayer.dimension) {
            serverPlayer.changeDimension(dest, new ITeleporter() {
                // Disable portal block placement
                @Override
                public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw,
                                          Function<Boolean, Entity> repositionEntity) {
                    Entity repositionedEntity = repositionEntity.apply(false);
                    repositionedEntity.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
                    return repositionedEntity;
                }
            });
        } else {
            // Just change position if no dimension was given
            serverPlayer.setPosition(pos.getX(), pos.getY(), pos.getZ());
        }

        // Set invulnerability
        if (!serverPlayer.isCreative() && !serverPlayer.isSpectator()) {
            serverPlayer.setInvulnerable(true);
            invulnerable = true;
            invulnerabilityCounter = Math.round(Configs.CommonConfig.INVULNERABILITY_DURATION.get() * WAIT_INTERVAL);
        }
        if (serverPlayer.isGlowing())
            serverPlayer.setGlowing(false);
        isTeleporting = false;
    }

    @SubscribeEvent
    protected void onTickEvent(TickEvent.ServerTickEvent event) {
        if (teleporterPlayer != null) {
            // Invulnerability Timer
            if (!this.teleporterPlayer.isCreative() && !this.teleporterPlayer.isSpectator() && this.invulnerable
                    && this.invulnerabilityCounter <= 0) {
                this.invulnerable = false;
                this.teleporterPlayer.setInvulnerable(false);
                cancelTeleport();
            } else {
                if (this.teleporterPlayer.isAddedToWorld())
                    this.invulnerabilityCounter--;
            }
            // Teleport Timer
            if (this.teleportCounter <= 0 && this.isTeleporting) {
                DimensionType dest = this.teleporterDimension;
                teleportPlayer(this.teleporterPlayer, this.teleporterPosition, dest);
            } else {
                if (this.teleporterPlayer.isAddedToWorld()) {
                    this.teleportCounter--;
                    if (this.teleportCounter == 140) {
                        teleporterPlayer.connection.sendPacket(new SPlaySoundEffectPacket(SoundEvents.BLOCK_PORTAL_TRIGGER,
                                SoundCategory.AMBIENT, teleporterPlayer.getPosX(), teleporterPlayer.getPosY(),
                                teleporterPlayer.getPosZ(), 1, 1));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    protected void onDamageTaken(LivingDamageEvent event) {
        if (event.getEntity() == teleporterPlayer && Configs.CommonConfig.USE_SAFE_TELEPORTS.get())
            cancelTeleport();
    }

    @SubscribeEvent
    protected void onCloseTeleporter(PlayerLoggedOutEvent event) {
        if (event.getPlayer().isInvulnerable()) {
            if (!event.getPlayer().isCreative())
                event.getPlayer().setInvulnerable(false);
        }
        if (this.isTeleporting)
            cancelTeleport();
    }

    public void cancelTeleport() {
        if (isTeleporting) {
            teleporterPlayer.sendStatusMessage(new TranslationTextComponent("teleport.nomadhome.cancel"), true);
            isTeleporting = false;
            teleportCounter = 0;
            teleporterPlayer.setGlowing(false);
        }
    }
}
