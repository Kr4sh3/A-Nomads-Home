package com.kr4she.nomadhome.util;

import net.minecraft.network.PacketBuffer;

public class TeleportRequestPacket {

	public TeleportRequestPacket() {
	}

	public static void encode(TeleportRequestPacket msg, PacketBuffer buf) {
	}

	public static TeleportRequestPacket decode(PacketBuffer buf) {
		return new TeleportRequestPacket();
	}
}
