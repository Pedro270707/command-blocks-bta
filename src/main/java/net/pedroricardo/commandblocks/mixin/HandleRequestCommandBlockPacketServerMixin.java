package net.pedroricardo.commandblocks.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.handler.NetServerHandler;
import net.pedroricardo.commandblocks.content.CommandBlock;
import net.pedroricardo.commandblocks.content.CommandBlockEntity;
import net.pedroricardo.commandblocks.content.CommandBlockPacket;
import net.pedroricardo.commandblocks.content.RequestCommandBlockEntityPacket;
import net.pedroricardo.commandblocks.duck.RequestCommandBlockPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.SERVER)
@Mixin(value = NetServerHandler.class, remap = false)
public abstract class HandleRequestCommandBlockPacketServerMixin extends NetHandler implements RequestCommandBlockPacketHandler {
	@Shadow
	private EntityPlayerMP playerEntity;

	@Override
	public void handleRequestCommandBlockPacket(RequestCommandBlockEntityPacket packet) {
		TileEntity tileEntity = this.playerEntity.world.getBlockTileEntity(packet.x, packet.y, packet.z);
		if (this.playerEntity.isOperator() && tileEntity instanceof CommandBlockEntity) {
			CommandBlockEntity cmdBlockEntity = (CommandBlockEntity) tileEntity;
			int commandBlockType = 0;
			int blockID = this.playerEntity.world.getBlockId(packet.x, packet.y, packet.z);
			for (int i = 0; i < CommandBlock.COMMAND_BLOCKS.size(); i++) {
				if (CommandBlock.COMMAND_BLOCKS.get(i).getLeft() != blockID) continue;
				commandBlockType = i;
			}
			boolean conditional = CommandBlock.isConditional(this.playerEntity.world, packet.x, packet.y, packet.z);
			boolean fallback = CommandBlock.isFallback(this.playerEntity.world, packet.x, packet.y, packet.z);
			this.playerEntity.playerNetServerHandler.sendPacket(new CommandBlockPacket(cmdBlockEntity.text, cmdBlockEntity.lastOutput, cmdBlockEntity.x, cmdBlockEntity.y, cmdBlockEntity.z, commandBlockType, cmdBlockEntity.trackOutput, cmdBlockEntity.auto, conditional, fallback, false));
		}
	}
}
