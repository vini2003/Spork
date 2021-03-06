package com.github.vini2003.polyester.api.event.type.block;

import com.github.vini2003.polyester.api.block.BlockInformation;
import com.github.vini2003.polyester.api.event.EventResult;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An event dispatched when a
 * block is added.
 */
public class BlockAddEvent {
	interface Listener {
		EventResult receive(World world, BlockInformation data, boolean moved);
	}

	private static final Set<Listener> LISTENERS = new HashSet<>();

	public static void register(Listener listener) {
		LISTENERS.add(listener);
	}

	public static void unregister(Listener listener) {
		LISTENERS.remove(listener);
	}

	public static EventResult dispatch(World world, BlockInformation data, boolean moved) {
		if (world.isClient()) return EventResult.CONTINUE;

		AtomicReference<EventResult> result = new AtomicReference<>(EventResult.CONTINUE);

		LISTENERS.forEach(listener -> {
			if (listener.receive(world, data, moved).isCancelled()) {
				result.set(EventResult.CANCEL);
			}
		});

		return result.get();
	}
}
