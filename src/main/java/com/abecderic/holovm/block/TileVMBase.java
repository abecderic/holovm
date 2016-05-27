package com.abecderic.holovm.block;

import com.abecderic.holovm.HoloVM;
import com.abecderic.holovm.network.VMBasePacket;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class TileVMBase extends TileEntity implements IInventory
{
    private static final int PACKET_UPDATE_RADIUS = 500;
    private static final int SLOTS = 6;
    private static final String[] SLOTNAMES = {"stackB", "stackT", "stackN", "stackE", "stackS", "stackW"};
    private ItemStack[] stack = new ItemStack[getSizeInventory()];
    private ItemStack camouflage = null;
    private byte direction = -1;

    private static final String OWNER = "owner";
    private static final String OWNER_UUID_MOST_SIG = "ownerUUIDMostSig";
    private static final String OWNER_UUID_LEAST_SIG = "ownerUUIDLeastSig";
    private String owner = null;
    private long ownerUUIDMostSig;
    private long ownerUUIDLeastSig;

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
    public ItemStack removeStackFromSlot(int index)
    {
        ItemStack result = stack[index];
        stack[index] = null;
        return result;
    }

    public ItemStack[] getStacks()
    {
        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (stack != null && stack.stackSize <= 0) return;
        if (slot == -1)
            this.camouflage = stack;
        else if (slot >= 0 && slot < getSizeInventory())
            this.stack[slot] = stack;
        this.markDirty();
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
    public void openInventory(EntityPlayer player)
    { /* NO-OP */ }

    @Override
    public void closeInventory(EntityPlayer player)
    { /* NO-OP */ }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value)
    { /* NO-OP */ }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear()
    {
        setInventorySlotContents(-1, null);
        for (int i = 0; i < getSizeInventory(); i++)
            setInventorySlotContents(i, null);
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
        String owner = compound.getString(OWNER);
        if (owner != null && !owner.isEmpty())
        {
            this.owner = owner;
            this.ownerUUIDMostSig = compound.getLong(OWNER_UUID_MOST_SIG);
            this.ownerUUIDLeastSig = compound.getLong(OWNER_UUID_LEAST_SIG);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
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
        if (this.owner != null)
        {
            compound.setString(OWNER, owner);
            compound.setLong(OWNER_UUID_MOST_SIG, ownerUUIDMostSig);
            compound.setLong(OWNER_UUID_LEAST_SIG, ownerUUIDLeastSig);
        }

        return compound;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return false;
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

    public void setOwner(EntityPlayer player)
    {
        this.owner = player.getDisplayNameString();
        this.ownerUUIDMostSig = player.getUniqueID().getMostSignificantBits();
        this.ownerUUIDLeastSig = player.getUniqueID().getLeastSignificantBits();
        this.markDirty();
    }

    public boolean isOwned()
    {
        return owner != null;
    }

    public boolean isOwner(EntityPlayer player)
    {
        if (!isOwned()) return false;
        boolean isOwner = ownerUUIDMostSig == player.getUniqueID().getMostSignificantBits() && ownerUUIDLeastSig == player.getUniqueID().getLeastSignificantBits();

        /* update saved name if neccessary */
        if (isOwner && !owner.equals(player.getDisplayNameString()))
        {
            owner = player.getDisplayNameString();
        }

        return isOwner;
    }

    public String getOwnerName()
    {
        return owner;
    }

    public void sendUpdates()
    {
        if (!worldObj.isRemote)
            HoloVM.snw.sendToAllAround(new VMBasePacket(pos, stack, camouflage, direction), new NetworkRegistry.TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), PACKET_UPDATE_RADIUS));
    }

    private String getSlotname(int slot)
    {
        return SLOTNAMES[slot];
    }

    @Override
    public String getName()
    {
        return HoloVM.VMBASE_KEY;
    }

    @Override
    public boolean hasCustomName()
    {
        return true;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentTranslation(blockType.getUnlocalizedName());
    }
}
