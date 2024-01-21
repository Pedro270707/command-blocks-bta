package net.pedroricardo.commandblocks.content;

import net.minecraft.client.Minecraft;
import net.minecraft.core.block.BlockTileEntityRotatable;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.collection.Pair;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.pedroricardo.commandblocks.duck.PlayerWhichCanOpenCommandBlockGui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandBlock extends BlockTileEntityRotatable {
	public static final List<Pair<Integer, String>> COMMAND_BLOCKS = new ArrayList<>();

	public CommandBlock(String key, int id) {
		super(key, id, Material.metal);
		COMMAND_BLOCKS.add(Pair.of(id, key));
		this.setTicking(true);
	}

	@Override
	public int tickRate() {
		return 1;
	}

	@Override
	public void onBlockPlaced(World world, int x, int y, int z, Side side, EntityLiving entity, double sideHeight) {
		Direction placementDirection = entity.getPlacementDirection(side).getOpposite();
		world.setBlockMetadataWithNotify(x, y, z, placementDirection.getId());
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
	}

	@Override
	protected TileEntity getNewBlockEntity() {
		return new CommandBlockEntity();
	}

	@Override
	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int x, int y, int z, int meta, TileEntity tileEntity) {
		if (dropCause == EnumDropCause.PICK_BLOCK) return new ItemStack[]{new ItemStack(this)};
		return null;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		if (!player.isSneaking()) {
			this.blockActivated(world, x, y, z, player);
		}
	}

	@Override
	public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
		if (player.getGamemode().isPlayerInvulnerable()) {
			if (!world.isClientSide) {
				((PlayerWhichCanOpenCommandBlockGui) player).displayGUICommandBlock((CommandBlockEntity) world.getBlockTileEntity(x, y, z));
			} else {
				Minecraft.getMinecraft(Minecraft.class).getSendQueue().addToSendQueue(new RequestCommandBlockEntityPacket(x, y, z));
			}
			return true;
		}
		return false;
	}

	public static int getMetadataBasedOnCondition(int metadata, boolean conditional, boolean fallback) {
		return fallback ? metadata | 16 : conditional ? (metadata | 8) & ~16 : metadata & ~24;
	}

	public static void setConditional(World world, int x, int y, int z, boolean conditional) {
		int previousMetadata = world.getBlockMetadata(x, y, z);
		world.setBlockMetadata(x, y, z, conditional ? previousMetadata | 8 : previousMetadata & ~8);
	}

	public static void setFallback(World world, int x, int y, int z, boolean fallback) {
		int previousMetadata = world.getBlockMetadata(x, y, z);
		world.setBlockMetadata(x, y, z, fallback ? previousMetadata | 16 : previousMetadata & ~16);
	}

	public static boolean isConditional(WorldSource world, int x, int y, int z) {
		return !isFallback(world, x, y, z) && (world.getBlockMetadata(x, y, z) >> 3 & 1) == 1;
	}

	public static boolean isFallback(WorldSource world, int x, int y, int z) {
		return (world.getBlockMetadata(x, y, z) >> 4 & 1) == 1;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		CommandBlockEntity blockEntity = (CommandBlockEntity) world.getBlockTileEntity(x, y, z);
		if (blockEntity == null) return;
		if (blockEntity.auto || world.isBlockGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y, z)) {
			if (!blockEntity.powered) {
				blockEntity.powered = true;
				blockEntity.tryExecuteCommand();
			}
		} else {
			blockEntity.powered = false;
		}
		world.scheduleBlockUpdate(x, y, z, this.id, this.tickRate());
	}
}
