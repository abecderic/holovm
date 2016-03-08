package holovm.item;

import java.util.List;

import holovm.block.TileEntityVMBase;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBlockVMBase extends ItemBlock
{
	public ItemBlockVMBase(Block block)
	{
		super(block);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
	{
		if (stack.getTagCompound() != null && stack.getTagCompound().getCompoundTag("camouflage") != null)
		{
			ItemStack camouflage = ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("camouflage"));
			list.add(StatCollector.translateToLocal("holovm.camouflage") + camouflage.getDisplayName());
		}
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		boolean success = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		if (!world.isRemote && success && stack.getTagCompound() != null && stack.getTagCompound().getCompoundTag("camouflage") != null)
		{
			TileEntityVMBase te = (TileEntityVMBase)world.getTileEntity(x, y, z);
			if (te != null)
			{
				te.setInventorySlotContents(-1, ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("camouflage")));
				world.setBlockMetadataWithNotify(x, y, z, 15, 2);
				te.sendUpdates();
			}
		}
		return success;
	}
}
