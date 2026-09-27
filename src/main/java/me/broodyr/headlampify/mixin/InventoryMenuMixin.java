package me.broodyr.headlampify.mixin;

import me.broodyr.headlampify.HeadlampSlot;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends AbstractCraftingMenu {
	private InventoryMenuMixin(MenuType<?> menuType, int containerId, int width, int height) {
		super(menuType, containerId, width, height);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void headlampify$addSlot(Inventory inventory, boolean active, Player owner, CallbackInfo ci) {
		this.addSlot(new HeadlampSlot(owner));
	}

	/** Shift-clicking a light source that isn't wearable equipment moves it from the inventory into an empty headlamp slot. */
	@Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
	private void headlampify$quickMoveIntoSlot(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
		if (slotIndex < InventoryMenu.INV_SLOT_START || slotIndex >= InventoryMenu.USE_ROW_SLOT_END) {
			return;
		}

		Slot from = this.slots.get(slotIndex);
		Slot headlamp = this.slots.get(HeadlampSlot.MENU_INDEX);
		ItemStack stack = from.getItem();
		if (headlamp.hasItem() || !headlamp.mayPlace(stack) || player.getEquipmentSlotForItem(stack) != EquipmentSlot.MAINHAND) {
			return;
		}

		ItemStack clicked = stack.copy();
		if (!this.moveItemStackTo(stack, HeadlampSlot.MENU_INDEX, HeadlampSlot.MENU_INDEX + 1, false)) {
			return;
		}

		if (stack.isEmpty()) {
			from.setByPlayer(ItemStack.EMPTY, clicked);
		} else {
			from.setChanged();
		}

		cir.setReturnValue(ItemStack.EMPTY);
	}
}
