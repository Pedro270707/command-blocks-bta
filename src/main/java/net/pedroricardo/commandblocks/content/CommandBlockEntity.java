package net.pedroricardo.commandblocks.content;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.nbt.CompoundTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.util.helper.Direction;
import net.pedroricardo.commander.duck.ClassWithManager;

public class CommandBlockEntity extends TileEntity {
	public String text = "";
	public String lastOutput = "";
	public String customName = null;
	public int successCount = 0;
	public boolean trackOutput = true;
	public boolean auto = false;
	public boolean powered = false;
	public int lastExecution = 0;
	public boolean updateLastExecution = true;
	private boolean initTracker = false;
	private boolean conditionMet = false;

	public CommandBlockEntity() {
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		super.readFromNBT(tag);
		if (tag.containsKey("CustomName")) this.customName = tag.getString("CustomName");
		this.text = tag.getString("Command");
		this.lastOutput = tag.getString("LastOutput");
		this.successCount = tag.getInteger("SuccessCount");
		this.trackOutput = tag.getBoolean("TrackOutput");
		this.auto = tag.getBoolean("auto");
		this.powered = tag.getBoolean("powered");
		this.lastExecution = tag.getInteger("LastExecution");
		this.updateLastExecution = tag.getBoolean("UpdateLastExecution");
		this.conditionMet = tag.getBoolean("conditionMet");
	}

	@Override
	public void writeToNBT(CompoundTag tag) {
		super.writeToNBT(tag);
		if (this.customName != null) tag.putString("CustomName", this.customName);
		tag.putString("Command", this.text);
		tag.putString("LastOutput", this.trackOutput ? this.lastOutput : "");
		tag.putInt("SuccessCount", this.successCount);
		tag.putBoolean("TrackOutput", this.trackOutput);
		tag.putBoolean("auto", this.auto);
		tag.putBoolean("powered", this.powered);
		tag.putInt("LastExecution", this.lastExecution);
		tag.putBoolean("UpdateLastExecution", this.updateLastExecution);
		tag.putBoolean("conditionMet", this.conditionMet);
	}

	public void tryExecuteCommand() {
		Direction direction = Direction.getDirectionById(this.worldObj.getBlockMetadata(this.x, this.y, this.z) & 7);
		try {
			if (this.canExecuteCommand()) {
				this.conditionMet = true;
				if (this.text.equalsIgnoreCase("Searge")) {
					this.lastOutput = "#itzlipofutzli";
					this.successCount = 1;
				} else {
					this.successCount = ((ClassWithManager) this.worldObj).getManager().execute(this.text == null ? "" : this.text.startsWith("/") ? this.text.substring(1) : this.text, FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER ? new ServerCommandBlockCommandSource(this.worldObj, this) : new ClientCommandBlockCommandSource(this.worldObj, this));
				}
			} else {
				this.conditionMet = false;
				this.successCount = 0;
			}
		} catch (CommandSyntaxException e) {
			if (this.trackOutput) this.lastOutput = e.getMessage();
			this.successCount = 0;
		}
		int blockInFrontID = this.worldObj.getBlockId(this.x + direction.getOffsetX(), this.y + direction.getOffsetY(), this.z + direction.getOffsetZ());
		TileEntity blockEntityInFront = this.worldObj.getBlockTileEntity(this.x + direction.getOffsetX(), this.y + direction.getOffsetY(), this.z + direction.getOffsetZ());
		if (blockEntityInFront instanceof CommandBlockEntity && blockInFrontID == CBBlocks.CHAIN_COMMAND_BLOCK.id) {
			boolean blockInFrontIsPowered = ((CommandBlockEntity) blockEntityInFront).auto || this.worldObj.isBlockGettingPowered(this.x + direction.getOffsetX(), this.y + direction.getOffsetY(), this.z + direction.getOffsetZ()) || this.worldObj.isBlockIndirectlyGettingPowered(this.x + direction.getOffsetX(), this.y + direction.getOffsetY(), this.z + direction.getOffsetZ());
			if (blockInFrontIsPowered) {
				((CommandBlockEntity) blockEntityInFront).tryExecuteCommand();
			}
		}
	}

	public boolean canExecuteCommand() {
		Direction direction = Direction.getDirectionById(this.worldObj.getBlockMetadata(this.x, this.y, this.z) & 7);
		boolean conditional = CommandBlock.isConditional(this.worldObj, this.x, this.y, this.z);
		boolean fallback = CommandBlock.isFallback(this.worldObj, this.x, this.y, this.z);
		TileEntity tileEntityBack = this.worldObj.getBlockTileEntity(this.x + direction.getOpposite().getOffsetX(), this.y + direction.getOpposite().getOffsetY(), this.z + direction.getOpposite().getOffsetZ());
		return !(conditional || fallback)
			|| (conditional && (tileEntityBack instanceof CommandBlockEntity && ((CommandBlockEntity) tileEntityBack).successCount > 0))
			|| (fallback && !(tileEntityBack instanceof CommandBlockEntity && ((CommandBlockEntity) tileEntityBack).successCount > 0));
	}

	@Override
	public void tick() {
		if (!this.initTracker && this.worldObj.getBlock(this.x, this.y, this.z) instanceof CommandBlock) {
			this.initTracker = true;
			this.worldObj.getBlock(this.x, this.y, this.z).updateTick(this.worldObj, this.x, this.y, this.z, this.worldObj.rand);
		}
		if (this.updateLastExecution) ++this.lastExecution;
		else this.lastExecution = 0;
		if (this.lastExecution != 0) return;
		if (this.worldObj.getBlock(this.x, this.y, this.z) instanceof CommandBlock) this.worldObj.getBlock(this.x, this.y, this.z).updateTick(this.worldObj, this.x, this.y, this.z, this.worldObj.rand);
    }

	public String getName() {
		return this.customName != null ? this.customName : "@";
	}
}
