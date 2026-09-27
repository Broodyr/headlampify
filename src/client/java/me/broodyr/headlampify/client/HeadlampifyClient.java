package me.broodyr.headlampify.client;

import me.broodyr.headlampify.HeadlampifyNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class HeadlampifyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(HeadlampLights::tick);
		// Registering the receiver is what tells the server this client has the mod; nothing is ever sent on it.
		ClientPlayNetworking.registerGlobalReceiver(HeadlampifyNetworking.PresencePayload.TYPE, (payload, context) -> {
		});
	}
}
