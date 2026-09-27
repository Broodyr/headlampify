package me.broodyr.headlampify.client.mixin;

import me.broodyr.headlampify.HeadlampSlot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** The vanilla inventory texture has no slot where the headlamp goes, so draw one. */
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractRecipeBookScreen<InventoryMenu> {
	@Unique
	private static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");

	private InventoryScreenMixin(InventoryMenu menu, RecipeBookComponent<?> recipeBookComponent, Inventory inventory, Component title) {
		super(menu, recipeBookComponent, inventory, title);
	}

	@Inject(method = "extractBackground", at = @At("TAIL"))
	private void headlampify$drawSlot(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		if (this.menu.getSlot(HeadlampSlot.MENU_INDEX).isActive()) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, this.leftPos + HeadlampSlot.X - 1, this.topPos + HeadlampSlot.Y - 1, 18, 18);
		}
	}
}
