package holovm.block;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import holovm.HoloVM;
import holovm.network.VMBasePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityVMBase extends TileEntity implements IInventory
{
	private static final int PACKET_UPDATE_RADIUS = 500;
	private static final int SLOTS = 1;
	private static final String[] SLOTNAMES = {"stack"};
	private ItemStack[] stack = new ItemStack[getSizeInventory()];
	private ItemStack camouflage = null;
	private byte direction = -1;
	
	@Override
	public int getSizeInventory()
	{
		return SLOTS;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		if (slot < 0 || slot >= getSizeInventory()) return null;
		else return stack[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		if (slot < 0 || slot >= getSizeInventory()) return null;
		if (stack[slot] != null)
		{
			if (stack[slot].stackSize <= amount)
			{
				setInventorySlotContents(slot, null);
			}
			else
			{
				stack[slot] = stack[slot].splitStack(amount);
			}
		}

		return stack[slot];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		return getStackInSlot(slot);
	}

	public ItemStack[] getStacks()
	{
		return stack;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		if (slot == -1)
			this.camouflage = stack;
		else if (slot >= 0 && slot < getSizeInventory())
			this.stack[slot] = stack;
		this.markDirty();
	}

	@Override
	public String getInventoryName()
	{
		return HoloVM.VMBASE_KEY;
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return true;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack)
	{
		return true;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		for (int i = 0; i < getSizeInventory(); i++)
		{
			NBTTagCompound compoundStack = compound.getCompoundTag(getSlotname(i));
			this.stack[i] = ItemStack.loadItemStackFromNBT(compoundStack);
		}
		NBTTagCompound compoundCamouflage = compound.getCompoundTag("camouflage");
		this.camouflage = ItemStack.loadItemStackFromNBT(compoundCamouflage);
		this.direction = compound.getByte("direction");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		for (int i = 0; i < getSizeInventory(); i++)
		{
			if (this.stack[i] != null)
			{
				NBTTagCompound compoundStack = new NBTTagCompound();
				compoundStack = this.stack[i].writeToNBT(compoundStack);
				compound.setTag(getSlotname(i), compoundStack);
			}
		}
		if (this.camouflage != null)
		{
			NBTTagCompound compoundCamouflage = new NBTTagCompound();
			compoundCamouflage = this.camouflage.writeToNBT(compoundCamouflage);
			compound.setTag("camouflage", compoundCamouflage);
		}
		compound.setByte("direction", direction);
	}
	
	public String getItemString(int slot)
	{
		if (slot < 0 || slot >= getSizeInventory()) return null;
		else return stack[slot] == null ? null : stack[slot].getDisplayName();
	}

	public ItemStack getCamouflage()
	{
		return camouflage;
	}

	public byte getDirection()
	{
		return direction;
	}

	public void setDirection(byte direction)
	{
		this.direction = direction;
		this.markDirty();
	}
	
	public void sendUpdates()
	{
		if (!worldObj.isRemote) HoloVM.snw.sendToAllAround(new VMBasePacket(xCoord, yCoord, zCoord, stack, camouflage, direction), new TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, PACKET_UPDATE_RADIUS));
	}
	
	protected String getSlotname(int slot)
	{
		return SLOTNAMES[slot];
	}
}
