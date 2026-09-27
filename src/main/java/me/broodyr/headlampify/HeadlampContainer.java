package me.broodyr.headlampify;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** A single-slot container backed by the player's {@link HeadlampAttachments#ITEM} attachment. */
public class HeadlampContainer implements Container {
	private final Player player;

	public HeadlampContainer(Player player) {
		this.player = player;
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return this.getItem(0).isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return this.player.getAttachedOrElse(HeadlampAttachments.ITEM, ItemStack.EMPTY);
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		ItemStack stack = this.getItem(slot);
		if (stack.isEmpty() || count <= 0) {
			return ItemStack.EMPTY;
		}

		ItemStack removed = stack.split(count);
		this.setChanged();
		return removed;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = this.getItem(slot);
		this.setItem(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.player.setAttached(HeadlampAttachments.ITEM, stack.isEmpty() ? null : stack);
		this.setChanged();
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public void setChanged() {
		ItemStack stack = this.getItem(0);
		if (stack.isEmpty() && this.player.hasAttached(HeadlampAttachments.ITEM)) {
			this.player.removeAttached(HeadlampAttachments.ITEM);
		}

		if (!this.player.level().isClientSide()) {
			int light = Headlampify.lightOf(stack);
			if (this.player.getAttachedOrElse(HeadlampAttachments.LIGHT, 0) != light) {
				this.player.setAttached(HeadlampAttachments.LIGHT, light > 0 ? light : null);
			}
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void clearContent() {
		this.setItem(0, ItemStack.EMPTY);
	}
}
