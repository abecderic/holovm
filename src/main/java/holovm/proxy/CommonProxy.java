package holovm.proxy;

import holovm.recipes.RecipesVMCamouflage;
import net.minecraft.item.crafting.CraftingManager;

public class CommonProxy
{
	public void init()
	{
		CraftingManager.getInstance().getRecipeList().add(new RecipesVMCamouflage());
	}
}
