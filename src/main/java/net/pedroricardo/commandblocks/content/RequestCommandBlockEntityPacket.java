package net.pedroricardo.commandblocks.content;

import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.core.net.packet.Packet;
import net.pedroricardo.commandblocks.duck.RequestCommandBlockPacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestCommandBlockEntityPacket extends Packet {
	public int x;
	public int y;
	public int z;

	public RequestCommandBlockEntityPacket(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public RequestCommandBlockEntityPacket() {
	}

	@Override
	public void readPacketData(DataInputStream dataInputStream) throws IOException {
		this.x = dataInputStream.readInt();
		this.y = dataInputStream.readInt();
		this.z = dataInputStream.readInt();
	}

	@Override
	public void writePacketData(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.writeInt(this.x);
		dataOutputStream.writeInt(this.y);
		dataOutputStream.writeInt(this.z);
	}

	@Override
	public void processPacket(NetHandler netHandler) {
		((RequestCommandBlockPacketHandler)netHandler).handleRequestCommandBlockPacket(this);
	}

	@Override
	public int getPacketSize() {
		return 3;
	}
}
