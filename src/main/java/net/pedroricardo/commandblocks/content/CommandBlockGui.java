package net.pedroricardo.commandblocks.content;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.hud.ComponentAnchor;
import net.minecraft.client.gui.text.ITextField;
import net.minecraft.client.gui.text.TextFieldEditor;
import net.minecraft.client.render.Lighting;
import net.minecraft.core.block.Block;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.util.helper.LogPrintStream;
import net.minecraft.core.util.helper.MathHelper;
import net.pedroricardo.commander.gui.GuiChatSuggestions;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CommandBlockGui extends GuiScreen implements ITextField {
	private int updateCounter;
	private int clipboardCommandCooldown = -99999;
	private int clipboardLastOutputCooldown = -99999;

	private final CommandBlockEntity blockEntity;
	private final TextFieldEditor editor;
	public GuiChatSuggestions SUGGESTIONS_GUI;
	protected String text;
	protected int textPosition;
	boolean save = true;
	boolean auto;
	boolean trackOutput;
	boolean conditional;
	boolean fallback;
	boolean cool;

	int selectedTextBox = 0;
	int commandBlockType;

	public CommandBlockGui(CommandBlockEntity blockEntity) {
		this.blockEntity = blockEntity;
		this.text = blockEntity.text != null ? blockEntity.text : "";
		this.editor = new TextFieldEditor(this);
		this.auto = blockEntity.auto;
		this.trackOutput = blockEntity.trackOutput;
		this.commandBlockType = 0;
		for (int i = 0; i < CommandBlock.COMMAND_BLOCKS.size(); i++) {
			if (CommandBlock.COMMAND_BLOCKS.get(i).getLeft() != this.blockEntity.worldObj.getBlockId(this.blockEntity.x, this.blockEntity.y, this.blockEntity.z)) continue;
			this.commandBlockType = i;
		}
		this.conditional = CommandBlock.isConditional(this.blockEntity.worldObj, this.blockEntity.x, this.blockEntity.y, this.blockEntity.z);
		this.fallback = CommandBlock.isFallback(this.blockEntity.worldObj, this.blockEntity.x, this.blockEntity.y, this.blockEntity.z);
	}

	@Override
	public void init() {
		this.controlList.clear();
		Keyboard.enableRepeatEvents(true);
		this.SUGGESTIONS_GUI = new GuiChatSuggestions(this.mc, this.editor, this, (parent, child, minecraft, followParameters) ->
			MathHelper.clamp((this.width - 296) / 2 + (followParameters ? this.SUGGESTIONS_GUI.getDefaultParameterPosition() - 1 : 0) - this.fontRenderer.getStringWidth(coloredSubstring(this.SUGGESTIONS_GUI.colorCodeText(this.text, false), 0, this.textPosition)), (this.width - 296) / 2, (this.width + 296) / 2)
		, (parent, child, minecraft, followParameters) -> 71 + (int) (this.mc.gameSettings.screenPadding.get() * this.height / 8.0f), ComponentAnchor.TOP_LEFT);
		this.SUGGESTIONS_GUI.updateSuggestions();

		this.controlList.add(new GuiButton(0, this.width / 2 - 154, this.height / 4 + 132 + (int)(this.mc.gameSettings.screenPadding.get() * this.height / 8.0f), 150, 20, I18n.getInstance().translateKey("gui.commandblocks.command_block.button.done")));
		this.controlList.add(new GuiButton(1, this.width / 2 + 4, this.height / 4 + 132 + (int)(this.mc.gameSettings.screenPadding.get() * this.height / 8.0f), 150, 20, I18n.getInstance().translateKey("gui.commandblocks.command_block.button.cancel")));
		this.controlList.add(new GuiButton(2, this.width / 2 + 130, 135 + (int)(this.mc.gameSettings.screenPadding.get() * this.height / 8.0f), 20, 20, this.trackOutput ? "X" : "O"));
		this.controlList.add(new GuiButton(3, this.width / 2 - 154, 165 + (int)(this.mc.gameSettings.screenPadding.get() * this.height / 8.0f), 100, 20, I18n.getInstance().translateKey("gui.commandblocks.command_block.button.type." + CommandBlock.COMMAND_BLOCKS.get(this.commandBlockType).getRight())));
		this.controlList.add(new GuiButton(4, this.width / 2 - 50, 165 + (int)(this.mc.gameSettings.screenPadding.get() * this.height / 8.0f), 100, 20, I18n.getInstance().translateKey("gui.commandblocks.command_block.button.conditional." + (this.fallback ? "fallback" : this.conditional ? "conditional" : "unconditional"))));
		this.controlList.add(new GuiButton(5, this.width / 2 + 54, 165 + (int)(this.mc.gameSettings.screenPadding.get() * this.height / 8.0f), 100, 20, I18n.getInstance().translateKey("gui.commandblocks.command_block.button.auto." + (this.auto ? "on" : "off"))));
	}

	@Override
	public void onClosed() {
		Keyboard.enableRepeatEvents(false);
		if (this.canUseGUI()) {
			if (this.save) {
				CommandBlockEntity blockEntity = this.blockEntity;
				if (this.cool) {
					if (this.mc.isMultiplayerWorld()) {
						this.mc.getSendQueue().addToSendQueue(new CommandBlockPacket("", "", blockEntity.x, blockEntity.y, blockEntity.z, 0, false, false, false, false, true));
					}
					this.mc.theWorld.setBlockWithNotify(blockEntity.x, blockEntity.y, blockEntity.z, Block.ice.id);
					return;
				}
				int metadata = this.mc.theWorld.getBlockMetadata(blockEntity.x, blockEntity.y, blockEntity.z);
				metadata = CommandBlock.getMetadataBasedOnCondition(metadata, this.conditional, this.fallback);
				this.mc.theWorld.setBlockMetadataWithNotify(blockEntity.x, blockEntity.y, blockEntity.z, metadata);
				this.blockEntity.text = this.text;
				this.blockEntity.auto = this.auto;
				this.blockEntity.trackOutput = this.trackOutput;
				this.blockEntity.successCount = 0;

				this.commandBlockType = MathHelper.clamp(this.commandBlockType, 0, CommandBlock.COMMAND_BLOCKS.size() - 1);
				if (Block.blocksList[CommandBlock.COMMAND_BLOCKS.get(this.commandBlockType).getLeft()] instanceof CommandBlock) {
					this.mc.theWorld.setBlockWithNotify(this.blockEntity.x, this.blockEntity.y, this.blockEntity.z, CommandBlock.COMMAND_BLOCKS.get(this.commandBlockType).getLeft());
					this.mc.theWorld.setBlockMetadataWithNotify(blockEntity.x, blockEntity.y, blockEntity.z, metadata);
					blockEntity.validate();
					this.mc.theWorld.setBlockTileEntity(this.blockEntity.x, this.blockEntity.y, this.blockEntity.z, blockEntity);
				}

				if (this.mc.isMultiplayerWorld()) {
					this.mc.getSendQueue().addToSendQueue(new CommandBlockPacket(this.text, blockEntity.lastOutput, blockEntity.x, blockEntity.y, blockEntity.z, this.commandBlockType, blockEntity.trackOutput, blockEntity.auto, this.conditional, this.fallback, false));
				} else if (!this.text.isEmpty()) {
					this.mc.ingameGUI.addChatMessage(I18n.getInstance().translateKeyAndFormat("gui.commandblocks.command_block.set", this.text));
				}
			}
		} else {
			this.mc.ingameGUI.addChatMessage(TextFormatting.RED + I18n.getInstance().translateKeyAndFormat("gui.commandblocks.command_block.failure", this.text));
		}
	}

	@Override
	public void tick() {
		++this.updateCounter;
		--this.clipboardCommandCooldown;
		--this.clipboardLastOutputCooldown;

		super.tick();
		if (!this.canUseGUI()) {
			this.mc.displayGuiScreen(null);
		}
		this.SUGGESTIONS_GUI.hidden = this.selectedTextBox != 0;
		this.textPosition = Math.min(this.textPosition, this.getText().length());
		while (this.fontRenderer.getStringWidth(this.getText().substring(0, this.editor.getCursor())) - this.fontRenderer.getStringWidth(this.getText().substring(0, this.textPosition)) > 292) {
			++this.textPosition;
		}
		if (this.editor.getCursor() < this.textPosition) this.textPosition = this.editor.getCursor();
		if (this.SUGGESTIONS_GUI != null) this.SUGGESTIONS_GUI.updateScreen(Mouse.getDWheel());
	}

	public boolean canUseGUI() {
		return this.mc.thePlayer.getGamemode().isPlayerInvulnerable();
	}

	@Override
	public void keyTyped(char c, int key, int mouseX, int mouseY) {
		if (key == 1 || key == 28) {
			this.mc.displayGuiScreen(null);
		} else if (key == 46 && Keyboard.isKeyDown(29) && (this.selectedTextBox == 0 || this.trackOutput)) {
			if (this.selectedTextBox == 0) this.clipboardCommandCooldown = 50;
			else this.clipboardLastOutputCooldown = 50;
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(this.selectedTextBox == 0 ? this.text : this.blockEntity.lastOutput), null);
		} else if (this.selectedTextBox == 0) {
			this.editor.handleInput(key, c);
		}
		if (this.SUGGESTIONS_GUI != null) this.SUGGESTIONS_GUI.keyTyped(c, key);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		int i = this.mc.renderEngine.getTexture("/assets/commandblocks/gui/command_block.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(i);
		int x = (this.width - 302) / 2;
		int yTop = 49 + (int) (this.mc.gameSettings.screenPadding.get() * this.height / 8.0f);
		int yBottom = 134 + (int)(this.mc.gameSettings.screenPadding.get() * this.height / 8.0f);

		this.drawDefaultBackground();
		this.drawTexturedModalRect(x, yTop, 0, this.selectedTextBox == 0 ? 0 : 44, 256, 22);
		this.drawTexturedModalRect(x + 256, yTop, 0, this.selectedTextBox == 0 ? 22 : 66, 46, 22);
		if (this.trackOutput) {
			this.drawTexturedModalRect(x, yBottom, 0, this.selectedTextBox == 1 ? 0 : 44, 256, 22);
			this.drawTexturedModalRect(x + 234, yBottom, 0, this.selectedTextBox == 1 ? 22 : 66, 46, 22);
		}

		GL11.glEnable(32826);
		GL11.glEnable(2903);
		GL11.glEnable(2929);
		Lighting.disable();
		GL11.glDisable(32826);

		this.fontRenderer.drawCenteredString(I18n.getInstance().translateKey("gui.commandblocks.command_block.title"), this.width / 2, yTop - 29, 16777215);
		GL11.glEnable(3042);
		GL11.glDisable(3008);
		GL11.glBlendFunc(770, 771);

		int commandClipboardAlpha = (int)MathHelper.clamp((this.clipboardCommandCooldown / 5.0f * 255.0f), 0, 255);
		if (commandClipboardAlpha > 0) {
			this.fontRenderer.drawStringWithShadow(I18n.getInstance().translateKey("gui.commandblocks.command_block.copied_to_clipboard"), x + 1, yTop - 9, 0x9e9e9e | commandClipboardAlpha << 24);
		} else if (this.clipboardCommandCooldown != 0) {
			int commandAlpha = (int)MathHelper.clamp((-this.clipboardCommandCooldown / 5.0f * 255.0f), 0, 255);
			this.fontRenderer.drawStringWithShadow(I18n.getInstance().translateKey("gui.commandblocks.command_block.console_command"), x + 1, yTop - 9, 0x9e9e9e | commandAlpha << 24);
		}
		if (this.trackOutput) {
			int clipboardAlpha = (int)MathHelper.clamp((this.clipboardLastOutputCooldown / 5.0f * 255.0f), 0, 255);
			if (clipboardAlpha > 0) {
				this.fontRenderer.drawStringWithShadow(I18n.getInstance().translateKey("gui.commandblocks.command_block.copied_to_clipboard"), x + 1, yBottom - 9, 0x9e9e9e | clipboardAlpha << 24);
			} else if (this.clipboardLastOutputCooldown != 0) {
				int previousOutputAlpha = (int)MathHelper.clamp((-this.clipboardLastOutputCooldown / 5.0f * 255.0f), 0, 255);
				this.fontRenderer.drawStringWithShadow(I18n.getInstance().translateKey("gui.commandblocks.command_block.previous_output"), x + 1, yBottom - 9, 0x9e9e9e | previousOutputAlpha << 24);
			}
		}
		GL11.glDisable(3042);
		GL11.glEnable(3008);

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor((x + 1) * this.mc.resolution.scale, this.mc.resolution.height - (yTop + 22) * this.mc.resolution.scale, 300 * this.mc.resolution.scale, 22 * this.mc.resolution.scale);

		String textToDraw = this.SUGGESTIONS_GUI.colorCodeText(this.text, false);
		this.fontRenderer.drawStringWithShadow(LogPrintStream.removeColorCodes(this.SUGGESTIONS_GUI.getSuggestionPreview()), x + 3 - this.fontRenderer.getStringWidth(coloredSubstring(textToDraw, 0, this.textPosition)) + this.SUGGESTIONS_GUI.getDefaultParameterPosition(), yTop + 7, 0x7e7e7e);
		this.fontRenderer.drawStringWithShadow(textToDraw, x + 5 - this.fontRenderer.getStringWidth(coloredSubstring(textToDraw, 0, this.textPosition)), yTop + 7, 16777215);
		if (this.selectedTextBox == 0 && this.updateCounter / 6 % 2 == 0) {
			int width = this.fontRenderer.getStringWidth(this.getText());
			if (this.editor.getCursor() < this.text.length()) {
				width = this.fontRenderer.getStringWidth(this.getText().substring(0, this.editor.getCursor()));
			}

			this.drawString(this.fontRenderer, "_", x + 5 + width - this.fontRenderer.getStringWidth(coloredSubstring(textToDraw, 0, this.textPosition)), yTop + 7, 16777215);
		}

		GL11.glScissor((x + 1) * this.mc.resolution.scale, this.mc.resolution.height - (yBottom + 22) * this.mc.resolution.scale, 278 * this.mc.resolution.scale, 22 * this.mc.resolution.scale);
		String lastOutput = this.blockEntity.lastOutput.isEmpty() ? "-" : this.blockEntity.lastOutput;
		if (this.trackOutput) this.fontRenderer.drawStringWithShadow(lastOutput, x + 5 - (int)(((Math.sin(this.updateCounter / 40.0f) + 1) / 2) * Math.max(0, this.fontRenderer.getStringWidth(lastOutput) - 270)), yBottom + 7, 0x9e9e9e);

		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		if (this.SUGGESTIONS_GUI != null) this.SUGGESTIONS_GUI.drawScreen();
		super.drawScreen(mouseX, mouseY, partialTick);
	}

	private static String coloredSubstring(String text, int start) {
		return coloredSubstring(text, start, LogPrintStream.removeColorCodes(text).length());
	}

	private static String coloredSubstring(String text, int start, int end) {
		int i = 0;
		while (i < text.length() && i < end) {
			if (text.charAt(i) == '§' && text.length() > i + 1) end += 2;
			++i;
		}
		i = 0;
		while (i < text.length() && i < start) {
			if (text.charAt(i) == '§' && text.length() > i + 1) start += 2;
			++i;
		}
		end = MathHelper.clamp(end, 0, text.length());
		start = MathHelper.clamp(start, 0, end);
		text = text.substring(start, end);
		return text;
	}

	@Override
	public void setText(String string) {
		this.text = string;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public int maxLength() {
		return 2048;
	}

	@Override
	protected void buttonPressed(GuiButton button) {
		if (button.enabled) {
			switch (button.id) {
				case 0:
					this.save = true;
					this.mc.displayGuiScreen(null);
					break;
				case 1:
					this.save = false;
					this.mc.displayGuiScreen(null);
					break;
				case 2:
					this.trackOutput = !this.trackOutput;
					if (this.selectedTextBox == 1) this.selectedTextBox = 0;
					button.displayString = this.trackOutput ? "X" : "O";
					break;
				case 3:
					++this.commandBlockType;
					if (this.commandBlockType >= CommandBlock.COMMAND_BLOCKS.size()) this.commandBlockType = 0;
					button.displayString = I18n.getInstance().translateKey("gui.commandblocks.command_block.button.type." + CommandBlock.COMMAND_BLOCKS.get(this.commandBlockType).getRight());
					break;
				case 4:
					if (this.cool) {
						this.cool = false;
						this.conditional = false;
						this.fallback = true;
					} else if (!this.conditional && !this.fallback) {
						this.conditional = true;
					} else if (!this.fallback) {
						List<String> names = Arrays.asList(
							"jonkadelic",
							"maggandgeez",
							"sunsetsatellite",
							"useless7695",
							"dinnerbone"
						);
						if (names.contains(this.text.toLowerCase(Locale.ROOT))) {
							this.cool = true;
						} else {
							this.fallback = true;
						}
						this.conditional = false;
					} else {
						this.fallback = false;
						this.conditional = false;
					}
					button.displayString = I18n.getInstance().translateKey("gui.commandblocks.command_block.button.conditional." + (this.cool ? "cool" : this.fallback ? "fallback" : this.conditional ? "conditional" : "unconditional"));
					break;
				case 5:
					this.auto = !this.auto;
					button.displayString = I18n.getInstance().translateKey("gui.commandblocks.command_block.button.auto." + (this.auto ? "on" : "off"));
					break;
			}
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		int x = (this.width - 302) / 2;
		int yTop = 49 + (int) (this.mc.gameSettings.screenPadding.get() * this.height / 8.0f);
		int yBottom = 134 + (int)(this.mc.gameSettings.screenPadding.get() * this.height / 8.0f);
		if (new Rectangle(x, yTop, 302, 22).contains(mouseX, mouseY)) {
			this.selectedTextBox = 0;
		} else if (new Rectangle(x, yBottom, 302, 22).contains(mouseX, mouseY)) {
			this.selectedTextBox = 1;
		}
		if (this.SUGGESTIONS_GUI != null) this.SUGGESTIONS_GUI.mouseClicked(mouseX, mouseY, mouseButton);
	}
}
