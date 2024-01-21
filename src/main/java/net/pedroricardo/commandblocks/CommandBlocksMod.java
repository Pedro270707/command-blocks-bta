package net.pedroricardo.commandblocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.pedroricardo.commandblocks.content.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.EntityHelper;
import turniplabs.halplibe.helper.NetworkHelper;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;


public class CommandBlocksMod implements ModInitializer, GameStartEntrypoint, RecipeEntrypoint {
    public static final String MOD_ID = "commandblocks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
		CBGameRules.init();
		EntityHelper.Core.createTileEntity(CommandBlockEntity.class, "CommandBlock");
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) CBBlocksClient.init();
		else CBBlocks.init();
		NetworkHelper.register(CommandBlockPacket.class, true, true);
		NetworkHelper.register(RequestCommandBlockEntityPacket.class, true, false);
		LOGGER.info("Command Blocks initialized.");
    }

	@Override
	public void beforeGameStart() {

	}

	@Override
	public void afterGameStart() {

	}

	@Override
	public void onRecipesReady() {

	}
}
