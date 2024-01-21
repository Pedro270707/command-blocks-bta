package net.pedroricardo.commandblocks.content;

import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.net.packet.Packet3Chat;
import net.minecraft.core.util.helper.AES;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.pedroricardo.commander.content.CommanderCommandSource;
import net.pedroricardo.commander.content.IServerCommandSource;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServerCommandBlockCommandSource implements CommanderCommandSource, IServerCommandSource {
	public final World world;
	public final CommandBlockEntity blockEntity;

    public ServerCommandBlockCommandSource(World world, CommandBlockEntity blockEntity) {
        this.world = world;
		this.blockEntity = blockEntity;
    }

    @Override
	public Collection<String> getPlayerNames() {
		List<String> list = new ArrayList<>();
		for (EntityPlayer player : this.getServer().playerList.playerEntities) {
			list.add(player.username);
		}
		return list;
	}

	@Override
	public @Nullable EntityPlayer getSender() {
		return null;
	}

	@Override
	public boolean hasAdmin() {
		return true;
	}

	@Override
	public @Nullable Vec3d getCoordinates(boolean offsetHeight) {
		return this.getBlockCoordinates();
	}

	@Override
	public @Nullable Vec3d getBlockCoordinates() {
		return Vec3d.createVector(this.blockEntity.x, this.blockEntity.y, this.blockEntity.z);
	}

	@Override
	public boolean messageMayBeMultiline() {
		return false;
	}

	@Override
	public void sendMessage(String message) {
		if (this.blockEntity.trackOutput) this.blockEntity.lastOutput = message;
		if (this.world.getGameRule(CBGameRules.COMMAND_BLOCK_OUTPUT)) {
			for (EntityPlayerMP player : this.getServer().playerList.playerEntities) {
				if (player.isOperator()) this.sendMessage(player, TextFormatting.LIGHT_GRAY + (TextFormatting.ITALIC + "[" + this.getName() + ": " + message.replaceAll("§r", TextFormatting.LIGHT_GRAY.toString() + TextFormatting.ITALIC) + "]"));
			}
		}
	}

	@Override
	public void sendMessage(EntityPlayer player, String message) {
		this.getServer().playerList.sendPacketToPlayer(player.username, new Packet3Chat(message, AES.keyChain.get(player.username)));
	}

	@Override
	public void sendMessageToAllPlayers(String message) {
		this.getServer().playerList.sendPacketToAllPlayers(new Packet3Chat(message));
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public World getWorld(int dimension) {
		return this.getServer().getDimensionWorld(dimension);
	}

	@Override
	public void movePlayerToDimension(EntityPlayer player, int dimension) {
		if (player instanceof EntityPlayerMP) this.getServer().playerList.sendPlayerToOtherDimension((EntityPlayerMP) player, dimension);
		else throw new IllegalStateException("Player is not an instance of EntityPlayerMP");
	}

	@Override
	public String getName() {
		return this.blockEntity.getName();
	}

	@Override
	public CommandHandler getCommandHandler() {
		return this.getServer().serverCommandHandler;
	}

	@Override
	public CommandSender getCommandSender() {
		return new CommandBlockLegacyCommandSender(this.world, this.blockEntity);
	}

	@Override
	public MinecraftServer getServer() {
		return MinecraftServer.getInstance();
	}
}
