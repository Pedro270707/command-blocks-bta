package net.pedroricardo.commandblocks.mixin;

import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.core.net.packet.Packet;
import net.pedroricardo.commandblocks.content.CommandBlockPacket;
import net.pedroricardo.commandblocks.content.RequestCommandBlockEntityPacket;
import net.pedroricardo.commandblocks.duck.CommandBlockPacketHandler;
import net.pedroricardo.commandblocks.duck.RequestCommandBlockPacketHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = NetHandler.class, remap = false)
public abstract class HandleRequestCommandBlockPacketMixin implements RequestCommandBlockPacketHandler {
	@Shadow
	public abstract void handleInvalidPacket(Packet packet);

	@Override
	public void handleRequestCommandBlockPacket(RequestCommandBlockEntityPacket packet) {
		handleInvalidPacket(packet);
	}
}
