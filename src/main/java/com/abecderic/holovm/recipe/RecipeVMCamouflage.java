package com.abecderic.holovm.recipe;

import com.abecderic.holovm.HoloVM;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeVMCamouflage implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting crafting, World world)
    {
        ItemStack vmbase = null;
        ItemStack camouflage = null;

        for (int i = 0; i < crafting.getSizeInventory(); i++)
        {
            ItemStack current = crafting.getStackInSlot(i);
            if (current != null)
            {
                if (current.getItem() instanceof ItemBlock)
                {
                    Block block = ((ItemBlock)current.getItem()).getBlock();
                    if (block == HoloVM.vmbase)
                    {
                        if (vmbase != null)
                        {
							/* two times vmbase in recipe?! */
                            return false;
                        }
                        vmbase = current;
                    }
                    else
                    {
                        if (camouflage != null)
                        {
							/* two other blocks in recipe?! */
                            return false;
                        }
                        if (block.isOpaqueCube())
                        {
                            camouflage = current;
                        }
                        else
                        {
                            return false;
                        }
                    }
                }
            }
        }
        return vmbase != null && camouflage != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting crafting)
    {
        ItemStack vmbase = null;
        ItemStack camouflage = null;
        for (int i = 0; i < crafting.getSizeInventory(); i++)
        {
            ItemStack current = crafting.getStackInSlot(i);
            if (current != null)
            {
                if (current.getItem() instanceof ItemBlock)
                {
                    Block block = ((ItemBlock)current.getItem()).getBlock();
                    if (block == HoloVM.vmbase)
                    {
                        vmbase = current;
                    }
                    else
                    {
                        camouflage = current;
                    }
                }
            }
        }
        if (vmbase != null && camouflage != null)
        {
            ItemStack result = vmbase.copy();
            NBTTagCompound compoundCamouflage = new NBTTagCompound();
            camouflage.writeToNBT(compoundCamouflage);
            if (result.getTagCompound() == null)
            {
                result.setTagCompound(new NBTTagCompound());
            }
            result.getTagCompound().setTag("camouflage", compoundCamouflage);
            result.stackSize = 1;
            return result;
        }
        else
        {
            return null;
        }
    }

    @Override
    public int getRecipeSize()
    {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return null;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv)
    {
        return new ItemStack[getRecipeSize()];
    }
}
