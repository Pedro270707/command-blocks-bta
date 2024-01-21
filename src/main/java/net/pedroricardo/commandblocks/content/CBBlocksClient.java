package net.pedroricardo.commandblocks.content;

import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.pedroricardo.commandblocks.CommandBlocksMod;
import turniplabs.halplibe.helper.BlockBuilder;
import useless.dragonfly.helper.ModelHelper;
import useless.dragonfly.model.block.BlockModelDragonFly;
import useless.dragonfly.model.blockstates.processed.MetaStateInterpreter;

import java.util.Random;

public class CBBlocksClient {
	public static void init() {
		CBBlocks.COMMAND_BLOCK = new BlockBuilder(CommandBlocksMod.MOD_ID).setBlockModel(getModel("command_block", new CommandBlockStateInterpreter())).addTags(BlockTags.NOT_IN_CREATIVE_MENU).setUnbreakable().setResistance(Float.POSITIVE_INFINITY).build(new CommandBlock("command_block", 3130));
		CBBlocks.REPEATING_COMMAND_BLOCK = new BlockBuilder(CommandBlocksMod.MOD_ID).setBlockModel(getModel("repeating_command_block", new CommandBlockStateInterpreter())).addTags(BlockTags.NOT_IN_CREATIVE_MENU).setUnbreakable().setResistance(Float.POSITIVE_INFINITY).build(new CommandBlock("repeating_command_block", 3131) {
			@Override
			public void updateTick(World world, int x, int y, int z, Random rand) {
				CommandBlockEntity blockEntity = (CommandBlockEntity) world.getBlockTileEntity(x, y, z);
				if (blockEntity == null) return;
				if (blockEntity.auto || world.isBlockGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y, z)) {
					blockEntity.tryExecuteCommand();
				}
				world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
			}
		});
		CBBlocks.CHAIN_COMMAND_BLOCK = new BlockBuilder(CommandBlocksMod.MOD_ID).setBlockModel(getModel("chain_command_block", new CommandBlockStateInterpreter())).addTags(BlockTags.NOT_IN_CREATIVE_MENU).setUnbreakable().setResistance(Float.POSITIVE_INFINITY).build(new CommandBlock("chain_command_block", 3132) {
			@Override
			public void updateTick(World world, int x, int y, int z, Random rand) {
			}
		});
	}

	private static BlockModelDragonFly getModel(String blockID, MetaStateInterpreter stateInterpreter) {
		return new BlockModelDragonFly(ModelHelper.getOrCreateBlockModel(CommandBlocksMod.MOD_ID, blockID + "_up.json"), ModelHelper.getOrCreateBlockState(CommandBlocksMod.MOD_ID, blockID + ".json"), stateInterpreter, true);
	}
}
