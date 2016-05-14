package com.abecderic.holovm.proxy;

import com.abecderic.holovm.HoloVM;
import com.abecderic.holovm.block.TileVMBase;
import com.abecderic.holovm.render.VMBaseRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy
{
	@Override
	public void init()
	{
		super.init();
		ClientRegistry.bindTileEntitySpecialRenderer(TileVMBase.class, new VMBaseRenderer());

		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(HoloVM.vmbase), 0, new ModelResourceLocation(HoloVM.MODID + ":" + HoloVM.VMBASE_KEY, "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(HoloVM.vmbase), 0, new ModelResourceLocation(HoloVM.MODID + ":" + HoloVM.VMBASE_KEY, "inventory"));
	}
}
