package me.broodyr.headlampify.client.mixin.sodium;

import me.broodyr.headlampify.client.HeadlampLights;
import net.caffeinemc.mods.sodium.client.model.light.data.QuadLightData;
import net.caffeinemc.mods.sodium.client.model.light.flat.FlatLightPipeline;
import net.caffeinemc.mods.sodium.client.model.light.smooth.SmoothLightPipeline;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Sodium stores block light in whole levels (4 bits) while meshing, which throws away the sub-level precision the
 * headlamp relies on to fade smoothly. Instead, raise each vertex's final light using the exact distance from the
 * headlamp to that vertex, sampled half a block out from the face like vanilla samples the neighbouring block.
 */
@Mixin({SmoothLightPipeline.class, FlatLightPipeline.class})
public abstract class SodiumLightPipelineMixin {
	@Inject(method = "calculate", at = @At("TAIL"))
	private void headlampify$addHeadlampLight(
		ModelQuadView quad,
		BlockPos pos,
		QuadLightData out,
		Direction cullFace,
		Direction lightFace,
		Direction shadeDirectionOverride,
		boolean enhanced,
		CallbackInfo ci
	) {
		double ox = pos.getX();
		double oy = pos.getY();
		double oz = pos.getZ();
		if (lightFace != null) {
			ox += lightFace.getStepX() * 0.5;
			oy += lightFace.getStepY() * 0.5;
			oz += lightFace.getStepZ() * 0.5;
		}

		for (int i = 0; i < 4; i++) {
			int light = HeadlampLights.bakedLightAt(ox + quad.getX(i), oy + quad.getY(i), oz + quad.getZ(i));
			if (light > 0) {
				out.lm[i] = HeadlampLights.withLight(out.lm[i], light);
			}
		}
	}
}
