package com.abecderic.holovm.proxy;

import com.abecderic.holovm.recipe.RecipeVMAdvanced;
import com.abecderic.holovm.recipe.RecipeVMCamouflage;
import com.abecderic.holovm.recipe.RecipeVMLocked;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;

public class CommonProxy
{
	public void init()
	{
        GameRegistry.addRecipe(new RecipeVMAdvanced());
        GameRegistry.addRecipe(new RecipeVMCamouflage());
        GameRegistry.addRecipe(new RecipeVMLocked());
        RecipeSorter.register("holovm:vmadv", RecipeVMAdvanced.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        RecipeSorter.register("holovm:vmcamo", RecipeVMCamouflage.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        RecipeSorter.register("holovm:vmlock", RecipeVMLocked.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
	}
}
