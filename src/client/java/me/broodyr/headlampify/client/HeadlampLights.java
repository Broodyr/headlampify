package me.broodyr.headlampify.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import me.broodyr.headlampify.HeadlampAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Client-side dynamic lighting for players wearing headlamps.
 *
 * <p>Light is computed at sub-level precision: packed light coords store block light as 0-240 (16 steps per level),
 * which is also what smooth lighting interpolates in. Using that full range instead of whole levels lets the light
 * fade continuously as the wearer moves rather than stepping a level at a time.</p>
 *
 * <p>Two snapshots are kept. {@link #bakedSources} is what chunk sections are meshed with; it only changes when the
 * affected sections are marked for rebuild, so every section in a rebuild agrees on where the light is (sections are
 * compiled asynchronously, and meshing them against a per-tick position made neighbouring sections disagree and
 * flicker). {@link #liveSources} updates every tick and lights entities, which are re-lit every frame anyway.</p>
 */
public final class HeadlampLights {
	/**
	 * How far (in blocks) a light has to move before the sections around it are rebuilt. Light falls off by one level
	 * per block, so 1/16 of a block is one sub-level step: small enough that each rebuild is imperceptible even at
	 * sneaking speed. Rebuilds still happen at most once per tick, so this costs no more than running already does.
	 */
	private static final double REBUILD_DISTANCE = 1.0 / 16.0;
	private static final int MAX_SMOOTH_LIGHT = 240;

	private record Source(double x, double y, double z, int level) {
	}

	private static final Source[] NONE = new Source[0];
	private static volatile Source[] bakedSources = NONE;
	private static volatile Source[] liveSources = NONE;
	/** The source state the currently baked sections were last rebuilt for, keyed by entity id. */
	private static final Int2ObjectMap<Source> rebuiltSources = new Int2ObjectOpenHashMap<>();
	private static ClientLevel trackedLevel;

	private HeadlampLights() {
	}

	/** Headlamp block light (0-240) baked into the block at {@code pos}. Safe to call from section-compile threads. */
	public static int bakedLightAt(BlockPos pos) {
		return bakedLightAt(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}

	/** Headlamp block light (0-240) baked into terrain at an exact position. Safe to call from section-compile threads. */
	public static int bakedLightAt(double x, double y, double z) {
		return lightAt(bakedSources, x, y, z);
	}

	/** Headlamp block light (0-240) at an exact position, for lighting entities. */
	public static int liveLightAt(Vec3 pos) {
		return lightAt(liveSources, pos.x, pos.y, pos.z);
	}

	/** Raises the block light of packed light coords to {@code light} (0-240) if that's brighter. */
	public static int withLight(int coords, int light) {
		return light > (coords & 0xFF) ? (coords & 0xFFFF0000) | light : coords;
	}

	private static int lightAt(Source[] sources, double x, double y, double z) {
		int best = 0;
		for (Source source : sources) {
			double dx = x - source.x;
			double dy = y - source.y;
			double dz = z - source.z;
			double distSq = dx * dx + dy * dy + dz * dz;
			if (distSq >= source.level * source.level) {
				continue;
			}

			int light = (int) ((source.level - Math.sqrt(distSq)) * 16.0);
			if (light > best) {
				best = Math.min(light, MAX_SMOOTH_LIGHT);
			}
		}

		return best;
	}

	static void tick(Minecraft minecraft) {
		ClientLevel level = minecraft.level;
		if (level != trackedLevel) {
			trackedLevel = level;
			rebuiltSources.clear();
			bakedSources = NONE;
			liveSources = NONE;
		}

		if (level == null) {
			return;
		}

		Int2ObjectMap<Source> seen = new Int2ObjectOpenHashMap<>();
		for (AbstractClientPlayer player : level.players()) {
			int light = player.getAttachedOrElse(HeadlampAttachments.LIGHT, 0);
			if (light <= 0 || player.isSpectator() || player.isRemoved()) {
				continue;
			}

			Vec3 eye = player.getEyePosition();
			seen.put(player.getId(), new Source(eye.x, eye.y, eye.z, Math.min(light, 15)));
		}

		liveSources = seen.values().toArray(NONE);

		boolean changed = false;
		for (Int2ObjectMap.Entry<Source> entry : seen.int2ObjectEntrySet()) {
			Source now = entry.getValue();
			Source before = rebuiltSources.get(entry.getIntKey());
			if (before == null || before.level != now.level || distanceSq(before, now) > REBUILD_DISTANCE * REBUILD_DISTANCE) {
				if (before != null) {
					markDirty(level, before);
				}

				markDirty(level, now);
				rebuiltSources.put(entry.getIntKey(), now);
				changed = true;
			}
		}

		changed |= rebuiltSources.int2ObjectEntrySet().removeIf(entry -> {
			if (seen.containsKey(entry.getIntKey())) {
				return false;
			}

			markDirty(level, entry.getValue());
			return true;
		});

		if (changed) {
			List<Source> baked = new ArrayList<>(rebuiltSources.values());
			bakedSources = baked.toArray(NONE);
		}
	}

	private static double distanceSq(Source a, Source b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		double dz = a.z - b.z;
		return dx * dx + dy * dy + dz * dz;
	}

	private static void markDirty(ClientLevel level, Source source) {
		// One extra block of margin: smooth lighting on a section's edge samples light from the neighbouring section.
		double reach = source.level + 1;
		level.setSectionRangeDirty(
			SectionPos.blockToSectionCoord(Mth.floor(source.x - reach)),
			SectionPos.blockToSectionCoord(Mth.floor(source.y - reach)),
			SectionPos.blockToSectionCoord(Mth.floor(source.z - reach)),
			SectionPos.blockToSectionCoord(Mth.floor(source.x + reach)),
			SectionPos.blockToSectionCoord(Mth.floor(source.y + reach)),
			SectionPos.blockToSectionCoord(Mth.floor(source.z + reach))
		);
	}
}
