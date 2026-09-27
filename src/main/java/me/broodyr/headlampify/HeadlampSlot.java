package me.broodyr.headlampify;

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

	private final Player player;

	public HeadlampSlot(Player player) {
		super(new HeadlampContainer(player), 0, X, Y);
		this.player = player;
	}

	/**
	 * Whether the player's client knows about this slot. Always true client-side; on the server, false for players on
	 * vanilla clients, who never see the slot and can't put anything into it.
	 */
	public boolean isAvailable() {
		return !(this.player instanceof ServerPlayer serverPlayer) || HeadlampifyNetworking.hasMod(serverPlayer);
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
