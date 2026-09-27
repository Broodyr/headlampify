package me.broodyr.headlampify.mixin;

import me.broodyr.headlampify.HeadlampAttachments;
import me.broodyr.headlampify.HeadlampSlot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {
	/** Drop the headlamp on death like the rest of the inventory; otherwise copyOnDeath carries it over to the respawned player. */
	@Inject(method = "dropEquipment", at = @At("TAIL"))
	private void headlampify$dropHeadlamp(ServerLevel level, CallbackInfo ci) {
		if (level.getGameRules().get(GameRules.KEEP_INVENTORY)) {
			return;
		}

		Player self = (Player) (Object) this;
		ItemStack stack = self.getAttachedOrElse(HeadlampAttachments.ITEM, ItemStack.EMPTY);
		if (!stack.isEmpty() && !EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
			ItemEntity drop = self.createItemStackToDrop(stack, true, false);
			if (drop != null) {
				level.addFreshEntity(drop);
			}
		}

		self.inventoryMenu.getSlot(HeadlampSlot.MENU_INDEX).set(ItemStack.EMPTY);
	}
}
