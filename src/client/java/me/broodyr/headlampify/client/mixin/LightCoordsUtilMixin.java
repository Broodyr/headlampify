package me.broodyr.headlampify.client.mixin;

import me.broodyr.headlampify.client.HeadlampLights;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Block, fluid, block entity and particle lighting all funnel through this method. */
@Mixin(LightCoordsUtil.class)
public abstract class LightCoordsUtilMixin {
	@Inject(
		method = "getLightCoords(Lnet/minecraft/util/LightCoordsUtil$BrightnessGetter;Lnet/minecraft/world/level/BlockAndLightGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I",
		at = @At("RETURN"),
		cancellable = true
	)
	private static void headlampify$addHeadlampLight(
		LightCoordsUtil.BrightnessGetter brightnessGetter, BlockAndLightGetter level, BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir
	) {
		int light = HeadlampLights.bakedLightAt(pos);
		if (light > 0) {
			cir.setReturnValue(HeadlampLights.withLight(cir.getReturnValueI(), light));
		}
	}
}
