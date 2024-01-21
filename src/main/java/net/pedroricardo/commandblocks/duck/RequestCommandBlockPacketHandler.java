package net.pedroricardo.commandblocks.duck;

import net.pedroricardo.commandblocks.content.RequestCommandBlockEntityPacket;

public interface RequestCommandBlockPacketHandler {
	void handleRequestCommandBlockPacket(RequestCommandBlockEntityPacket packet);
}
