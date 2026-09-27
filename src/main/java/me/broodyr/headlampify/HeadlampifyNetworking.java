package me.broodyr.headlampify;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/**
 * Detects whether the other side of a connection has Headlampify installed, so modded servers stay compatible with
 * vanilla clients and modded clients stay compatible with servers without the mod (vanilla, Paper, ...).
 *
 * <p>Both sides register a receiver for {@link PresencePayload}; Fabric exchanges those channel lists during
 * configuration, before the player's inventory is first sent. The payload itself is never sent.</p>
 */
public final class HeadlampifyNetworking {
	public record PresencePayload() implements CustomPacketPayload {
		public static final PresencePayload INSTANCE = new PresencePayload();
		public static final CustomPacketPayload.Type<PresencePayload> TYPE = new CustomPacketPayload.Type<>(Headlampify.id("presence"));
		public static final StreamCodec<RegistryFriendlyByteBuf, PresencePayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

		@Override
		public Type<PresencePayload> type() {
			return TYPE;
		}
	}

	private HeadlampifyNetworking() {
	}

	static void init() {
		PayloadTypeRegistry.clientboundPlay().register(PresencePayload.TYPE, PresencePayload.STREAM_CODEC);
		PayloadTypeRegistry.serverboundPlay().register(PresencePayload.TYPE, PresencePayload.STREAM_CODEC);
		// Registering the receiver is what tells clients this server has the mod; nothing is ever sent on it.
		ServerPlayNetworking.registerGlobalReceiver(PresencePayload.TYPE, (payload, context) -> {
		});
	}

	/** Whether this player's client has Headlampify. Players without a network connection (e.g. fake players) count as modded. */
	public static boolean hasMod(ServerPlayer player) {
		return player.connection == null || ServerPlayNetworking.canSend(player, PresencePayload.TYPE);
	}
}
