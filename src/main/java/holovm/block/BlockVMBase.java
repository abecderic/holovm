package holovm.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import holovm.HoloVM;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockVMBase extends BlockContainer
{
	@SideOnly(Side.CLIENT)
	protected IIcon front;
	@SideOnly(Side.CLIENT)
	protected IIcon side;
	
	protected String blockName;

	public BlockVMBase()
	{
		super(Material.rock);
		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setBlockName(HoloVM.VMBASE_KEY);
		this.setHardness(1.0f);
		this.setStepSound(soundTypeStone);
		this.setLightLevel(1.0f);
	}

	@Override
	public Block setBlockName(String name)
	{
		this.blockName = name;
		return super.setBlockName(name);
	}
	
	@Override
	public String getUnlocalizedName()
	{
		return ("tile." + HoloVM.MODID + ":" + blockName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		IIcon camouflage = getCamouflageIcon(world, x, y, z, side);
		if (camouflage != null) return camouflage;
		TileEntityVMBase te = (TileEntityVMBase) world.getTileEntity(x, y, z);
		int meta = 0;
		if (te != null)
		{
			meta = te.getDirection() + 1;
		}
		return getIcon(side, meta);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if (side == 0 && meta >= 1 && meta <= 4) return this.front;
		else if (side == 1 && meta >= 5 && meta <= 8) return this.front;
		else if (side >= 2 && side <= 6 && side == meta - 7) return this.front;
		else return this.side;
	}

	@SideOnly(Side.CLIENT)
	protected IIcon getCamouflageIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		TileEntityVMBase te = (TileEntityVMBase) world.getTileEntity(x, y, z);
		if (te != null)
		{
			ItemStack stack = te.getCamouflage();
			if (stack != null && stack.getItem() instanceof ItemBlock)
			{
				Block block = ((ItemBlock)stack.getItem()).field_150939_a;
				return block.getIcon(side, stack.getItemDamage());
			}
		}
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		front = iconRegister.registerIcon(HoloVM.MODID + ":" + HoloVM.VMBASE_KEY);
		side = iconRegister.registerIcon(HoloVM.MODID + ":" + HoloVM.VMBASE_KEY + "2");
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityVMBase();
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player)
	{
		super.onBlockHarvested(world, x, y, z, meta, player);
		TileEntity tileentity = world.getTileEntity(x, y, z);
		if (tileentity != null && tileentity instanceof TileEntityVMBase)
		{
			TileEntityVMBase te = (TileEntityVMBase) tileentity;
			for (int i = 0; i < te.getSizeInventory(); i++)
			{
				ItemStack stack = te.getStackInSlot(i);
				if (stack != null)
				{
					EntityItem item = new EntityItem(world, x, y, z, stack);
					world.spawnEntityInWorld(item);
				}
			}
			ItemStack camouflage = te.getCamouflage();
			if (camouflage != null)
			{
				EntityItem item = new EntityItem(world, x, y, z, camouflage);
				world.spawnEntityInWorld(item);
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hX, float hY, float hZ)
	{
		if (!world.isRemote)
		{
			TileEntityVMBase te = (TileEntityVMBase) world.getTileEntity(x, y, z);
			if (player.isSneaking())
			{
				String s = te.getItemString(0);
				if (s != null) player.addChatMessage(new ChatComponentText(s));
			}
			else
			{
				ItemStack stack = te.getStackInSlot(0);
				if (stack != null)
				{
					EntityItem item = new EntityItem(world, player.posX, player.posY + 1, player.posZ, stack);
					world.spawnEntityInWorld(item);
				}
				te.setInventorySlotContents(0, player.getHeldItem());
				if (te.getStackInSlot(0) != null)
				{
					int f3no = playerYawToF3No(player.getRotationYawHead());
					/* bottom, dir 0-3 */
					if (side == 0 && f3no != -1)
					{
						te.setDirection((byte)f3no);
					}
					/* top, dir 4-7 */
					else if (side == 1 && f3no != -1)
					{
						te.setDirection((byte)(4+f3no));
					}
					/* sides, dir 8-11 */
					else
					{
						te.setDirection((byte)(6+side));
					}
					world.setBlockMetadataWithNotify(x, y, z, 15, 2);
				}
				else
				{
					if (te.getCamouflage() == null)
						world.setBlockMetadataWithNotify(x, y, z, 0, 2);
				}
				te.sendUpdates();
				if (!player.capabilities.isCreativeMode) player.setCurrentItemOrArmor(0, null);
			}
		}
		return true;
	}

	protected int playerYawToF3No(float yaw)
	{
		yaw %= 360;
		if (yaw < 0) yaw += 360;
		if (yaw >= 315 || yaw < 45) return 0;
		else if (yaw >= 45 && yaw < 135) return 1;
		else if (yaw >= 135 && yaw < 225) return 2;
		else if (yaw >= 225 && yaw < 315) return 3;
		else return -1;
	}
}
