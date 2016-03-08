package holovm.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import holovm.HoloVM;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockVMAdvanced extends BlockVMBase
{
	public BlockVMAdvanced()
	{
		super();
		this.setBlockName(HoloVM.VMADV_KEY);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		IIcon camouflage = getCamouflageIcon(world, x, y, z, side);
		if (camouflage != null) return camouflage;
		TileEntityVMAdv te = (TileEntityVMAdv) world.getTileEntity(x, y, z);
		if (te != null)
		{
			return te.getStackInSlot(side) != null ? this.front : this.side;
		}
		return getIcon(side, 0);
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityVMAdv();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hX, float hY, float hZ)
	{
		if (!world.isRemote)
		{
			TileEntityVMBase te = (TileEntityVMBase) world.getTileEntity(x, y, z);
			if (player.isSneaking())
			{
				String s = te.getItemString(side);
				if (s != null) player.addChatMessage(new ChatComponentText(s));
			}
			else
			{
				ItemStack stack = te.getStackInSlot(side);
				if (stack != null)
				{
					EntityItem item = new EntityItem(world, player.posX, player.posY + 1, player.posZ, stack);
					world.spawnEntityInWorld(item);
				}
				te.setInventorySlotContents(side, player.getHeldItem());
				if (te.getStackInSlot(side) != null)
				{
					int f3no = playerYawToF3No(player.getRotationYawHead());
					/* bottom, dir 0-3 */
					if (side == 0 && f3no != -1)
					{
						int b = te.getDirection() & 12; /* 12 = 0b00001100 */
						te.setDirection((byte)(b+f3no));
					}
					/* top, dir 4-7 */
					else if (side == 1 && f3no != -1)
					{
						int b = te.getDirection() & 3; /*  3 = 0b00000011 */
						te.setDirection((byte)(b+(f3no<<2)));
					}
					world.setBlockMetadataWithNotify(x, y, z, 15, 2);
				}
				else
				{
					if (side == 0) te.setDirection((byte)(te.getDirection() & 12));
					else if (side == 1) te.setDirection((byte)(te.getDirection() & 3));
					boolean stacksNull = true;
					for (int i = 0; i < te.getSizeInventory(); i++)
					{
						if (te.getStackInSlot(i) != null) stacksNull = false;
					}
					if (stacksNull && te.getCamouflage() == null)
						world.setBlockMetadataWithNotify(x, y, z, 0, 2);
				}
				te.sendUpdates();
				if (!player.capabilities.isCreativeMode) player.setCurrentItemOrArmor(0, null);
			}
		}
		return true;
	}
}
