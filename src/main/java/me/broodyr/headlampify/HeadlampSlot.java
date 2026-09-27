package me.broodyr.headlampify;

import java.util.function.BooleanSupplier;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class HeadlampSlot extends Slot {
	/** Index of the headlamp slot in the player's {@link InventoryMenu}, right after the offhand slot. */
	public static final int MENU_INDEX = 46;
	public static final int X = 77;
	public static final int Y = 8;
	public static final Identifier EMPTY_ICON = Headlampify.id("container/slot/headlamp");

	/** Set by the client entrypoint: whether the server the client is connected to has Headlampify. */
	private static BooleanSupplier serverHasMod = () -> true;

	private final Player player;

	public HeadlampSlot(Player player) {
		super(new HeadlampContainer(player), 0, X, Y);
		this.player = player;
	}

	public static void setServerHasMod(BooleanSupplier serverHasMod) {
		HeadlampSlot.serverHasMod = serverHasMod;
	}

	/**
	 * Whether both sides of the connection know about this slot. On the server, false for players on vanilla clients;
	 * on the client, false when connected to a server without Headlampify. Either way the slot is hidden and can't hold
	 * anything, since the other side would reject or crash on it.
	 */
	public boolean isAvailable() {
		if (this.player instanceof ServerPlayer serverPlayer) {
			return HeadlampifyNetworking.hasMod(serverPlayer);
		}

		return !this.player.level().isClientSide() || serverHasMod.getAsBoolean();
	}

	/** Whether {@code menu} is a player inventory whose headlamp slot must not be synced to the client. */
	public static boolean isHiddenIn(AbstractContainerMenu menu) {
		return menu instanceof InventoryMenu
			&& menu.slots.size() > MENU_INDEX
			&& menu.slots.get(MENU_INDEX) instanceof HeadlampSlot slot
			&& !slot.isAvailable();
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return Headlampify.lightOf(stack) > 0 && this.isAvailable();
	}

	@Override
	public boolean isActive() {
		return this.isAvailable();
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public Identifier getNoItemIcon() {
		return EMPTY_ICON;
	}
}
