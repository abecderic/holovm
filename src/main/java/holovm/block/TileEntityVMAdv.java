package holovm.block;

import holovm.HoloVM;

public class TileEntityVMAdv extends TileEntityVMBase
{
	private static final int SLOTS = 6;
	private static final String[] SLOTNAMES = {"stackB", "stackT", "stackN", "stackE", "stackS", "stackW"};
	
	@Override
	public int getSizeInventory()
	{
		return SLOTS;
	}
	
	@Override
	public String getInventoryName()
	{
		return HoloVM.VMADV_KEY;
	}
	
	@Override
	protected String getSlotname(int slot)
	{
		return SLOTNAMES[slot];
	}
}
