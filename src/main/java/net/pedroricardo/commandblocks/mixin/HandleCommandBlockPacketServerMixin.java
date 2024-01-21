package net.pedroricardo.commandblocks.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.block.Block;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.core.net.packet.Packet3Chat;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.handler.NetServerHandler;
import net.pedroricardo.commandblocks.content.CommandBlock;
import net.pedroricardo.commandblocks.content.CommandBlockEntity;
import net.pedroricardo.commandblocks.content.CommandBlockPacket;
import net.pedroricardo.commandblocks.duck.CommandBlockPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.SERVER)
@Mixin(value = NetServerHandler.class, remap = false)
public abstract class HandleCommandBlockPacketServerMixin extends NetHandler implements CommandBlockPacketHandler {
	@Shadow
	private EntityPlayerMP playerEntity;

	@Override
	public void handleCommandBlockPacket(CommandBlockPacket packet) {
		CommandBlockEntity blockEntity = (CommandBlockEntity) this.playerEntity.world.getBlockTileEntity(packet.x, packet.y, packet.z);
		if (this.playerEntity.isOperator() && blockEntity != null) {
			if (packet.cool) {
				blockEntity.worldObj.setBlock(packet.x, packet.y, packet.z, Block.ice.id);
				return;
			}
			int metadata = blockEntity.worldObj.getBlockMetadata(packet.x, packet.y, packet.z);
			metadata = CommandBlock.getMetadataBasedOnCondition(metadata, packet.conditional, packet.fallback);
			blockEntity.worldObj.setBlockMetadataWithNotify(packet.x, packet.y, packet.z, metadata);
			blockEntity.text = packet.command;
			blockEntity.auto = packet.auto;
			blockEntity.trackOutput = packet.trackOutput;
			blockEntity.successCount = 0;

			packet.commandBlockType = MathHelper.clamp(packet.commandBlockType, 0, CommandBlock.COMMAND_BLOCKS.size() - 1);
			if (Block.blocksList[CommandBlock.COMMAND_BLOCKS.get(packet.commandBlockType).getLeft()] instanceof CommandBlock) {
				blockEntity.worldObj.setBlockWithNotify(packet.x, packet.y, packet.z, CommandBlock.COMMAND_BLOCKS.get(packet.commandBlockType).getLeft());
				blockEntity.worldObj.setBlockMetadataWithNotify(blockEntity.x, blockEntity.y, blockEntity.z, metadata);
				blockEntity.validate();
				blockEntity.worldObj.setBlockTileEntity(packet.x, packet.y, packet.z, blockEntity);
			}

			if (!packet.command.isEmpty()) this.playerEntity.playerNetServerHandler.sendPacket(new Packet3Chat(I18n.getInstance().translateKeyAndFormat("gui.commandblocks.command_block.set", packet.command)));
		} else {
			this.playerEntity.playerNetServerHandler.sendPacket(new Packet3Chat(TextFormatting.RED + I18n.getInstance().translateKey("gui.commandblocks.command_block.failure")));
		}
	}
}
