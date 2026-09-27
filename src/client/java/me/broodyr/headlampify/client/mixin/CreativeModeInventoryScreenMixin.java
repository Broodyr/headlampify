package me.broodyr.headlampify.client.mixin;

import me.broodyr.headlampify.HeadlampSlot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Places the headlamp slot in the creative inventory tab, mirroring the offhand slot on the other side of the player. */
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
	@Unique
	private static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");
	@Unique
	private static final int CREATIVE_X = 127;
	@Unique
	private static final int CREATIVE_Y = 20;
	@Unique
	private static final String SLOT_WRAPPER_INIT =
		"Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$SlotWrapper;<init>(Lnet/minecraft/world/inventory/Slot;III)V";

	private CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Shadow
	public abstract boolean isInventoryOpen();

	@ModifyArg(method = "selectTab", at = @At(value = "INVOKE", target = SLOT_WRAPPER_INIT), index = 2)
	private int headlampify$slotX(Slot target, int index, int x, int y) {
		return index == HeadlampSlot.MENU_INDEX ? CREATIVE_X : x;
	}

	@ModifyArg(method = "selectTab", at = @At(value = "INVOKE", target = SLOT_WRAPPER_INIT), index = 3)
	private int headlampify$slotY(Slot target, int index, int x, int y) {
		return index == HeadlampSlot.MENU_INDEX ? CREATIVE_Y : y;
	}

	@Inject(method = "extractBackground", at = @At("TAIL"))
	private void headlampify$drawSlot(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		if (this.isInventoryOpen() && this.minecraft.player.inventoryMenu.getSlot(HeadlampSlot.MENU_INDEX).isActive()) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, this.leftPos + CREATIVE_X - 1, this.topPos + CREATIVE_Y - 1, 18, 18);
		}
	}
}
