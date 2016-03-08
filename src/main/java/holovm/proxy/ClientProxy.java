package holovm.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import holovm.block.TileEntityVMAdv;
import holovm.block.TileEntityVMBase;
import holovm.render.VMAdvRenderer;
import holovm.render.VMBaseRenderer;

public class ClientProxy extends CommonProxy
{
	@Override
	public void init()
	{
		super.init();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVMBase.class, new VMBaseRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVMAdv.class, new VMAdvRenderer());
	}
}
