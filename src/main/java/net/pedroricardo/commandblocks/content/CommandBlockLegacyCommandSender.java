package net.pedroricardo.commandblocks.content;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.world.World;

public class CommandBlockLegacyCommandSender implements CommandSender {
	public final World world;
	public final CommandBlockEntity blockEntity;

	public CommandBlockLegacyCommandSender(World world, CommandBlockEntity blockEntity) {
		this.world = world;
		this.blockEntity = blockEntity;
	}

	@Override
	public boolean isAdmin() {
		return true;
	}

	@Override
	public EntityPlayer getPlayer() {
		return null;
	}

	@Override
	public void sendMessage(String string) {
		this.blockEntity.lastOutput = string;
	}

	@Override
	public String getName() {
		return "@";
	}
}
