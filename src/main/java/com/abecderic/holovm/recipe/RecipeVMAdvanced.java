package com.abecderic.holovm.recipe;

import com.abecderic.holovm.HoloVM;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeVMAdvanced implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting crafting, World worldIn)
    {
        ItemStack vmbase = null;
        int goldAmount = 0;

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
                        return false;
                    }
                }
                else if (current.getItem() == Items.GOLD_INGOT)
                {
                    goldAmount++;
                }
            }
        }
        return vmbase != null && goldAmount == 4;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting crafting)
    {
        ItemStack vmbase = null;
        int goldAmount = 0;

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
                            return null;
                        }
                        vmbase = current;
                    }
                    else
                    {
                        return null;
                    }
                }
                else if (current.getItem() == Items.GOLD_INGOT)
                {
                    goldAmount++;
                }
            }
        }

        if (vmbase != null && goldAmount == 4)
        {
            ItemStack result = vmbase.copy();
            if (result.getTagCompound() == null)
            {
                result.setTagCompound(new NBTTagCompound());
            }
            result.getTagCompound().setTag("advanced", new NBTTagCompound());
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
