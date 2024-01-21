package net.pedroricardo.commandblocks.duck;

import net.pedroricardo.commandblocks.content.CommandBlockPacket;

public interface CommandBlockPacketHandler {
	void handleCommandBlockPacket(CommandBlockPacket packet);
}
