package net.pedroricardo.commandblocks.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.EntityPlayerSP;
import net.pedroricardo.commandblocks.content.CommandBlockEntity;
import net.pedroricardo.commandblocks.content.CommandBlockGui;
import net.pedroricardo.commandblocks.duck.PlayerWhichCanOpenCommandBlockGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Environment(EnvType.CLIENT)
@Mixin(value = EntityPlayerSP.class, remap = false)
public class PlayerSPOpenCommandBlockGuiMixin implements PlayerWhichCanOpenCommandBlockGui {
	@Shadow
	protected Minecraft mc;

	@Override
	public void displayGUICommandBlock(CommandBlockEntity blockEntity) {
		this.mc.displayGuiScreen(new CommandBlockGui(blockEntity));
	}
}
