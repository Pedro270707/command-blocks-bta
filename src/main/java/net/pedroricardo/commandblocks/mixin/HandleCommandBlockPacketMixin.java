package net.pedroricardo.commandblocks.mixin;

import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.core.net.packet.Packet;
import net.pedroricardo.commandblocks.content.CommandBlockPacket;
import net.pedroricardo.commandblocks.duck.CommandBlockPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = NetHandler.class, remap = false)
public abstract class HandleCommandBlockPacketMixin implements CommandBlockPacketHandler {
	@Shadow
	public abstract void handleInvalidPacket(Packet packet);

	@Override
	public void handleCommandBlockPacket(CommandBlockPacket packet) {
		handleInvalidPacket(packet);
	}
}
