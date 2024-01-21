package net.pedroricardo.commandblocks.content;

import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.core.net.packet.Packet;
import net.pedroricardo.commandblocks.duck.CommandBlockPacketHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CommandBlockPacket extends Packet {
	public String command;
	public String lastOutput;
	public int x;
	public int y;
	public int z;
	public int commandBlockType;
	public boolean trackOutput;
	public boolean auto;
	public boolean conditional;
	public boolean fallback;
	public boolean cool;

	public CommandBlockPacket(String command, String lastOutput, int x, int y, int z, int commandBlockType, boolean trackOutput, boolean auto, boolean conditional, boolean fallback, boolean cool) {
		this.command = command;
		this.lastOutput = lastOutput;
		this.x = x;
		this.y = y;
		this.z = z;
		this.commandBlockType = commandBlockType;
		this.trackOutput = trackOutput;
		this.auto = auto;
		this.conditional = conditional;
		this.fallback = fallback;
		this.cool = cool;
	}

	public CommandBlockPacket() {
	}

	@Override
	public void readPacketData(DataInputStream dataInputStream) throws IOException {
		this.command = dataInputStream.readUTF();
		this.lastOutput = dataInputStream.readUTF();
		this.x = dataInputStream.readInt();
		this.y = dataInputStream.readInt();
		this.z = dataInputStream.readInt();
		this.commandBlockType = dataInputStream.readInt();
		this.trackOutput = dataInputStream.readBoolean();
		this.auto = dataInputStream.readBoolean();
		this.conditional = dataInputStream.readBoolean();
		this.fallback = dataInputStream.readBoolean();
		this.cool = dataInputStream.readBoolean();
	}

	@Override
	public void writePacketData(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.writeUTF(this.command == null ? "" : this.command);
		dataOutputStream.writeUTF(this.lastOutput == null ? "" : this.lastOutput);
		dataOutputStream.writeInt(this.x);
		dataOutputStream.writeInt(this.y);
		dataOutputStream.writeInt(this.z);
		dataOutputStream.writeInt(this.commandBlockType);
		dataOutputStream.writeBoolean(this.trackOutput);
		dataOutputStream.writeBoolean(this.auto);
		dataOutputStream.writeBoolean(this.conditional);
		dataOutputStream.writeBoolean(this.fallback);
		dataOutputStream.writeBoolean(this.cool);
	}

	@Override
	public void processPacket(NetHandler netHandler) {
		((CommandBlockPacketHandler)netHandler).handleCommandBlockPacket(this);
	}

	@Override
	public int getPacketSize() {
		return 4;
	}
}
