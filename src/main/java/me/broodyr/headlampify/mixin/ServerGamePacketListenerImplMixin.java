package me.broodyr.headlampify.mixin;

import me.broodyr.headlampify.HeadlampSlot;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
	@Shadow
	public ServerPlayer player;

	/**
	 * The creative inventory edits slots client-side and syncs them with {@link ServerboundSetCreativeModeSlotPacket},
	 * but vanilla only accepts slot numbers 1-45 there. Also accept the headlamp slot, for items it can hold.
	 */
	@ModifyVariable(method = "handleSetCreativeModeSlot", at = @At("STORE"), ordinal = 1)
	private boolean headlampify$acceptHeadlampSlot(boolean validSlot, ServerboundSetCreativeModeSlotPacket packet) {
		if (validSlot || packet.slotNum() != HeadlampSlot.MENU_INDEX) {
			return validSlot;
		}

		ItemStack stack = packet.itemStack();
		return stack.isEmpty() || stack.getCount() == 1 && this.player.inventoryMenu.getSlot(HeadlampSlot.MENU_INDEX).mayPlace(stack);
	}
}
