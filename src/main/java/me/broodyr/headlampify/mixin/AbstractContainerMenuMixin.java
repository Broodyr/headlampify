package me.broodyr.headlampify.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import me.broodyr.headlampify.HeadlampSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Keeps the headlamp slot out of inventory packets sent to vanilla clients. Their inventory menu has no slot 46, so
 * receiving one would crash them.
 */
@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {
	@WrapOperation(
		method = "sendAllDataToRemote",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ContainerSynchronizer;sendInitialData(Lnet/minecraft/world/inventory/AbstractContainerMenu;Ljava/util/List;Lnet/minecraft/world/item/ItemStack;[I)V")
	)
	private void headlampify$trimInitialData(
		ContainerSynchronizer synchronizer, AbstractContainerMenu menu, List<ItemStack> items, ItemStack carried, int[] dataSlots, Operation<Void> original
	) {
		if (HeadlampSlot.isHiddenIn(menu) && items.size() > HeadlampSlot.MENU_INDEX) {
			items = items.subList(0, HeadlampSlot.MENU_INDEX);
		}

		original.call(synchronizer, menu, items, carried, dataSlots);
	}

	@WrapOperation(
		method = "*",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ContainerSynchronizer;sendSlotChange(Lnet/minecraft/world/inventory/AbstractContainerMenu;ILnet/minecraft/world/item/ItemStack;)V")
	)
	private void headlampify$skipSlotChange(
		ContainerSynchronizer synchronizer, AbstractContainerMenu menu, int slot, ItemStack stack, Operation<Void> original
	) {
		if (slot != HeadlampSlot.MENU_INDEX || !HeadlampSlot.isHiddenIn(menu)) {
			original.call(synchronizer, menu, slot, stack);
		}
	}
}
