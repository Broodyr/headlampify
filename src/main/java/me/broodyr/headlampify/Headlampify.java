package me.broodyr.headlampify;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.BlockState;

public class Headlampify implements ModInitializer {
	public static final String MOD_ID = "headlampify";

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	/**
	 * The block light an item emits when worn as a headlamp: the light emission of the block it places,
	 * respecting any block state stored on the stack (e.g. the level of a {@code minecraft:light} item).
	 */
	public static int lightOf(ItemStack stack) {
		if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem blockItem)) {
			return 0;
		}

		BlockState state = blockItem.getBlock().defaultBlockState();
		BlockItemStateProperties properties = stack.get(DataComponents.BLOCK_STATE);
		if (properties != null) {
			state = properties.apply(state);
		}

		return state.getLightEmission();
	}

	@Override
	public void onInitialize() {
		HeadlampAttachments.init();
		HeadlampifyNetworking.init();
	}
}
