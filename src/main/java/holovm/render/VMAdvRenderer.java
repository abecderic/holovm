package holovm.render;

import holovm.block.TileEntityVMBase;

public class VMAdvRenderer extends VMBaseRenderer
{
	@Override
	protected void renderVM(TileEntityVMBase tileentity, double x, double y, double z)
	{
		for (int i = 0; i < tileentity.getSizeInventory(); i++)
		{
			if (tileentity.getStackInSlot(i) == null) continue;
			int direction = 0;
			if (i >= 2) direction = 6+i;
			else if (i == 0) direction = tileentity.getDirection() & 3;
			else if (i == 1) direction = 4 + ((tileentity.getDirection() & 12) >> 2);
			renderItemStack(tileentity.getWorldObj(), x, y, z, tileentity.getStackInSlot(i), direction);
		}
	}
}
