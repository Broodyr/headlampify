package me.broodyr.headlampify;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;

public final class HeadlampAttachments {
	/** The item in the player's headlamp slot. Server-authoritative; reaches the owner's client through the inventory menu. */
	public static final AttachmentType<ItemStack> ITEM = AttachmentRegistry.create(
		Headlampify.id("item"),
		builder -> builder.persistent(ItemStack.OPTIONAL_CODEC).copyOnDeath()
	);

	/** The light level the player's headlamp emits, synced to every client that can see the player. */
	public static final AttachmentType<Integer> LIGHT = AttachmentRegistry.create(
		Headlampify.id("light"),
		builder -> builder.persistent(Codec.INT).copyOnDeath().syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.all())
	);

	private HeadlampAttachments() {
	}

	static void init() {
	}
}
