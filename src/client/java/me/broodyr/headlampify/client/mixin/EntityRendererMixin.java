package me.broodyr.headlampify.client.mixin;

import me.broodyr.headlampify.client.HeadlampLights;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
	@Inject(method = "getPackedLightCoords", at = @At("RETURN"), cancellable = true)
	private void headlampify$addHeadlampLight(Entity entity, float partialTickTime, CallbackInfoReturnable<Integer> cir) {
		int light = HeadlampLights.liveLightAt(entity.getLightProbePosition(partialTickTime), partialTickTime);
		if (light > 0) {
			cir.setReturnValue(HeadlampLights.withLight(cir.getReturnValueI(), light));
		}
	}
}
