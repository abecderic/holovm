package com.abecderic.holovm.item;

import com.abecderic.holovm.block.BlockVMBase;
import com.abecderic.holovm.block.TileVMBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;

public class ItemBlockVMBase extends ItemBlock
{
    public ItemBlockVMBase(Block block)
    {
        super(block);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
    {
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("advanced"))
        {
            list.add(new TextComponentTranslation("holovm.advanced").getUnformattedText());
        }
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("camouflage"))
        {
            ItemStack camouflage = ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("camouflage"));
            list.add(new TextComponentTranslation("holovm.camouflage", camouflage.getDisplayName()).getUnformattedText());
        }
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("locked"))
        {
            list.add(new TextComponentTranslation("holovm.locked").getUnformattedText());
        }
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState)
    {
        boolean success = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
        if (!world.isRemote && success && stack.getTagCompound() != null)
        {
            if (stack.getTagCompound().hasKey("advanced"))
            {
                world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockVMBase.ADV, true));
            }
            if (stack.getTagCompound().hasKey("camouflage"))
            {
                TileVMBase te = (TileVMBase)world.getTileEntity(pos);
                if (te != null)
                {
                    te.setInventorySlotContents(-1, ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("camouflage")));
                    world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockVMBase.HASITEM, true));
                    te.sendUpdates();
                }
            }
            if (stack.getTagCompound().hasKey("locked"))
            {
                TileVMBase te = (TileVMBase)world.getTileEntity(pos);
                if (te != null)
                {
                    te.setOwner(player);
                    player.addChatComponentMessage(new TextComponentTranslation("holovm.ownedbyyou"));
                }
            }
        }
        return success;
    }
}
