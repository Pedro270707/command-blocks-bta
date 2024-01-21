package net.pedroricardo.commandblocks.content;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.net.packet.Packet3Chat;
import net.minecraft.core.util.helper.AES;
import net.minecraft.core.util.phys.Vec3d;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.pedroricardo.commander.content.CommanderCommandSource;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClientCommandBlockCommandSource implements CommanderCommandSource {
	public final World world;
	public final CommandBlockEntity blockEntity;

    public ClientCommandBlockCommandSource(World world, CommandBlockEntity blockEntity) {
        this.world = world;
		this.blockEntity = blockEntity;
    }

    @Override
	public Collection<String> getPlayerNames() {
		List<String> list = new ArrayList<>();
		for (EntityPlayer player : this.world.players) {
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
		if (this.world.getGameRule(CBGameRules.COMMAND_BLOCK_OUTPUT)) Minecraft.getMinecraft(Minecraft.class).ingameGUI.addChatMessage(TextFormatting.LIGHT_GRAY + (TextFormatting.ITALIC + "[" + this.getName() + ": " + message.replaceAll("§r", TextFormatting.LIGHT_GRAY.toString() + TextFormatting.ITALIC) + "]"));
	}

	@Override
	public void sendMessage(EntityPlayer player, String message) {
		if (Minecraft.getMinecraft(Minecraft.class).thePlayer == player) {
			Minecraft.getMinecraft(Minecraft.class).ingameGUI.addChatMessage(message);
		}
	}

	@Override
	public void sendMessageToAllPlayers(String message) {
		this.sendMessage(message);
		Minecraft.getMinecraft(Minecraft.class).ingameGUI.addChatMessage(message);
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public World getWorld(int dimension) {
		return Minecraft.getMinecraft(Minecraft.class).theWorld.dimension.id == dimension ? Minecraft.getMinecraft(Minecraft.class).theWorld : new World(Minecraft.getMinecraft(Minecraft.class).theWorld, Dimension.getDimensionList().get(dimension));
	}

	@Override
	public void movePlayerToDimension(EntityPlayer player, int dimension) {
		Dimension lastDim = Dimension.getDimensionList().get(player.dimension);
		Dimension newDim = Dimension.getDimensionList().get(dimension);
		System.out.println("Switching to dimension \"" + newDim.getTranslatedName() + "\"!!");
		player.dimension = dimension;
		Minecraft.getMinecraft(Minecraft.class).theWorld.setEntityDead(player);
		Minecraft.getMinecraft(Minecraft.class).thePlayer.removed = false;
		double x = player.x;
		double y = player.y + 64;
		double z = player.z;
		player.moveTo(x *= Dimension.getCoordScale(lastDim, newDim), y, z *= Dimension.getCoordScale(lastDim, newDim), player.yRot, player.xRot);
		if (player.isAlive()) {
			Minecraft.getMinecraft(Minecraft.class).theWorld.updateEntityWithOptionalForce(player, false);
		}
		World world = new World(Minecraft.getMinecraft(Minecraft.class).theWorld, newDim);
		if (newDim == lastDim.homeDim) {
			Minecraft.getMinecraft(Minecraft.class).changeWorld(world, "Leaving " + lastDim.getTranslatedName(), player);
		} else {
			Minecraft.getMinecraft(Minecraft.class).changeWorld(world, "Entering " + newDim.getTranslatedName(), player);
		}
		player.world = Minecraft.getMinecraft(Minecraft.class).theWorld;
		if (player.isAlive()) {
			player.moveTo(x, y, z, player.yRot, player.xRot);
			Minecraft.getMinecraft(Minecraft.class).theWorld.updateEntityWithOptionalForce(player, false);
		}
	}

	@Override
	public String getName() {
		return this.blockEntity.getName();
	}

	@Override
	public CommandHandler getCommandHandler() {
		return Minecraft.getMinecraft(Minecraft.class).commandHandler;
	}

	@Override
	public CommandSender getCommandSender() {
		return new CommandBlockLegacyCommandSender(this.world, this.blockEntity);
	}
}
