package net.pedroricardo.commandblocks.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.entity.player.EntityPlayer;
import net.pedroricardo.commandblocks.content.CommandBlockEntity;
import net.pedroricardo.commandblocks.duck.PlayerWhichCanOpenCommandBlockGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.SERVER)
@Mixin(EntityPlayer.class)
public abstract class PlayerMPOpenCommandBlockGuiMixin implements PlayerWhichCanOpenCommandBlockGui {
	@Override
	public void displayGUICommandBlock(CommandBlockEntity blockEntity) {
	}
}
