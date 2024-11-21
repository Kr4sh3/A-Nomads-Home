package com.kr4she.nomadhome.util;

import java.util.function.Supplier;
import com.kr4she.nomadhome.NomadHome;
import com.kr4she.nomadhome.common.home.DimensionTeleporter;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class NomadHomePacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(NomadHome.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);

	public static void register() {
		int id = 0;
		INSTANCE.registerMessage(id++, TeleportRequestPacket.class, TeleportRequestPacket::encode, TeleportRequestPacket::decode,
				NomadHomePacketHandler::handle);
	}

	public static void handle(TeleportRequestPacket packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity player = ctx.get().getSender();
			DimensionTeleporter.teleport(player);
		});
		ctx.get().setPacketHandled(true);
	}
}
