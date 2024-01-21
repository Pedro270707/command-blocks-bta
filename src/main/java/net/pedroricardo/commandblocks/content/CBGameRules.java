package net.pedroricardo.commandblocks.content;

import net.minecraft.core.data.gamerule.GameRuleBoolean;
import net.minecraft.core.data.gamerule.GameRules;

public class CBGameRules {
	public static GameRuleBoolean COMMAND_BLOCK_OUTPUT;

	public static void init() {
		COMMAND_BLOCK_OUTPUT = GameRules.register(new GameRuleBoolean("commandBlockOutput", false));
	}
}
