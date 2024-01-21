package net.pedroricardo.commandblocks.content;

import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.WorldSource;
import useless.dragonfly.model.blockstates.processed.MetaStateInterpreter;

import java.util.HashMap;
import java.util.Locale;

public class CommandBlockStateInterpreter extends MetaStateInterpreter {
	@Override
	public HashMap<String, String> getStateMap(WorldSource worldSource, int x, int y, int z, Block block, int meta) {
		HashMap<String, String> map = new HashMap<>();
		map.put("facing", Direction.getDirectionById(meta & 7).name().toLowerCase(Locale.ROOT));
		map.put("type", CommandBlock.isFallback(worldSource, x, y, z) ? "fallback" : CommandBlock.isConditional(worldSource, x, y, z) ? "conditional" : "impulse");
		return map;
	}
}
