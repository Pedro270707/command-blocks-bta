package net.pedroricardo.commandblocks.content;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.pedroricardo.commandblocks.CommandBlocksMod;
import turniplabs.halplibe.helper.BlockBuilder;

import java.util.Random;

public class CBBlocks {
	public static Block COMMAND_BLOCK;
	public static Block REPEATING_COMMAND_BLOCK;
	public static Block CHAIN_COMMAND_BLOCK;

	public static void init() {
		COMMAND_BLOCK = new BlockBuilder(CommandBlocksMod.MOD_ID).addTags(BlockTags.NOT_IN_CREATIVE_MENU).setTickOnLoad().setUnbreakable().setResistance(Float.POSITIVE_INFINITY).build(new CommandBlock("command_block", 3130));
		REPEATING_COMMAND_BLOCK = new BlockBuilder(CommandBlocksMod.MOD_ID).addTags(BlockTags.NOT_IN_CREATIVE_MENU).setTickOnLoad().setUnbreakable().setResistance(Float.POSITIVE_INFINITY).build(new CommandBlock("repeating_command_block", 3131) {
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
		CHAIN_COMMAND_BLOCK = new BlockBuilder(CommandBlocksMod.MOD_ID).addTags(BlockTags.NOT_IN_CREATIVE_MENU).setUnbreakable().setResistance(Float.POSITIVE_INFINITY).build(new CommandBlock("chain_command_block", 3132) {
			@Override
			public void updateTick(World world, int x, int y, int z, Random rand) {
			}
		});
	}
}
