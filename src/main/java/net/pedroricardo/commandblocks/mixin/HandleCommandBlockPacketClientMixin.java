package net.pedroricardo.commandblocks.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.net.handler.NetClientHandler;
import net.minecraft.core.block.Block;
import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.handler.NetServerHandler;
import net.pedroricardo.commandblocks.content.CommandBlock;
import net.pedroricardo.commandblocks.content.CommandBlockEntity;
import net.pedroricardo.commandblocks.content.CommandBlockGui;
import net.pedroricardo.commandblocks.content.CommandBlockPacket;
import net.pedroricardo.commandblocks.duck.CommandBlockPacketHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(value = NetClientHandler.class, remap = false)
public abstract class HandleCommandBlockPacketClientMixin extends NetHandler implements CommandBlockPacketHandler {
	@Shadow
	@Final
	private Minecraft mc;

	@Override
	public void handleCommandBlockPacket(CommandBlockPacket packet) {
		CommandBlockEntity blockEntity = new CommandBlockEntity();
		blockEntity.worldObj = this.mc.theWorld;
		blockEntity.x = packet.x;
		blockEntity.y = packet.y;
		blockEntity.z = packet.z;
		blockEntity.text = packet.command;
		blockEntity.auto = packet.auto;
		blockEntity.trackOutput = packet.trackOutput;
		blockEntity.lastOutput = packet.lastOutput;

		packet.commandBlockType = MathHelper.clamp(packet.commandBlockType, 0, CommandBlock.COMMAND_BLOCKS.size() - 1);
		this.mc.displayGuiScreen(new CommandBlockGui(blockEntity));
	}
}
