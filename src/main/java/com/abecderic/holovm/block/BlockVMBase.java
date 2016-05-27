package com.abecderic.holovm.block;

import com.abecderic.holovm.HoloVM;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.List;

public class BlockVMBase extends BlockContainer
{
    public static final PropertyBool HASITEM = PropertyBool.create("hasitem");
    public static final PropertyBool ADV = PropertyBool.create("adv");

    public BlockVMBase()
    {
        super(Material.IRON);
        setDefaultState(blockState.getBaseState().withProperty(HASITEM, false).withProperty(ADV, false));
        setRegistryName(HoloVM.VMBASE_KEY);
        setUnlocalizedName(HoloVM.VMBASE_KEY);
        setHardness(2.2F);
        setResistance(5.0F);
        setLightOpacity(0);
        setLightLevel(1.0F);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "tile." + HoloVM.MODID + ":" + HoloVM.VMBASE_KEY;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int i)
    {
        return new TileVMBase();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 2));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, HASITEM, ADV);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(HASITEM, (meta & 1) == 1).withProperty(ADV, (meta & 2) == 2);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(HASITEM) ? 1 : 0) + (state.getValue(ADV) ? 2 : 0);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        super.onBlockHarvested(worldIn, pos, state, player);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (!worldIn.isRemote && tileentity != null && tileentity instanceof TileVMBase)
        {
            TileVMBase te = (TileVMBase) tileentity;
            for (int i = 0; i < te.getSizeInventory(); i++)
            {
                ItemStack stack = te.getStackInSlot(i);
                if (stack != null)
                {
                    EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                    worldIn.spawnEntityInWorld(item);
                }
            }
            ItemStack camouflage = te.getCamouflage();
            if (camouflage != null)
            {
                EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, camouflage);
                worldIn.spawnEntityInWorld(item);
            }
            if (state.getValue(ADV))
            {
                EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(Items.GOLD_INGOT, 4));
                worldIn.spawnEntityInWorld(item);
            }
            if (te.isOwned())
            {
                EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(Items.DIAMOND));
                worldIn.spawnEntityInWorld(item);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote && hand == EnumHand.MAIN_HAND)
        {
            TileVMBase te = (TileVMBase) worldIn.getTileEntity(pos);
            final boolean adv = state.getValue(ADV);
            final int face = adv ? side.getIndex() : 0;

            if (playerIn.isSneaking())
            {
                String s = te.getItemString(face);
                if (s != null) playerIn.addChatMessage(new TextComponentString(s));
            }
            else if (!te.isOwned() || te.isOwner(playerIn))
            {
                ItemStack stack = te.getStackInSlot(face);
                if (stack != null)
                {
                    EntityItem item = new EntityItem(worldIn, playerIn.posX, playerIn.posY + 1, playerIn.posZ, stack);
                    worldIn.spawnEntityInWorld(item);
                }
                te.setInventorySlotContents(face, playerIn.getHeldItem(EnumHand.MAIN_HAND));
                if (te.getStackInSlot(face) != null)
                {
                    worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(HASITEM, true));

                    int f3no = playerYawToF3No(playerIn.getRotationYawHead());

                    if (!adv)
                    {
                        /* bottom, dir 0-3 */
                        if (side == EnumFacing.DOWN && f3no != -1)
                        {
                            te.setDirection((byte)f3no);
                        }
					    /* top, dir 4-7 */
                        else if (side == EnumFacing.UP && f3no != -1)
                        {
                            te.setDirection((byte)(4+f3no));
                        }
					    /* sides, dir 8-11 */
                        else
                        {
                            te.setDirection((byte)(6+side.ordinal()));
                        }
                    }
                    else
                    {
                        /* bottom, dir 0-3 */
                        if (side == EnumFacing.DOWN && f3no != -1)
                        {
                            int b = te.getDirection() & 12; /* 12 = 0b00001100 */
                            te.setDirection((byte)(b+f3no));
                        }
					    /* top, dir 4-7 */
                        else if (side == EnumFacing.UP && f3no != -1)
                        {
                            int b = te.getDirection() & 3; /*  3 = 0b00000011 */
                            te.setDirection((byte)(b+(f3no<<2)));
                        }
                    }
                }
                else
                {
                    boolean stacksNull = true;
                    for (int i = 0; i < te.getSizeInventory(); i++)
                    {
                        if (te.getStackInSlot(i) != null) stacksNull = false;
                    }
                    if (stacksNull && te.getCamouflage() == null)
                        worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(HASITEM, false));
                    else
                        worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(HASITEM, true));
                }

                te.sendUpdates();
                if (!playerIn.capabilities.isCreativeMode) playerIn.setHeldItem(EnumHand.MAIN_HAND, null);
            }
            else
            {
                playerIn.addChatComponentMessage(new TextComponentTranslation("holovm.notowner", te.getOwnerName()));
            }
        }
        return true;
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        TileVMBase te = (TileVMBase) world.getTileEntity(pos);
        if (!te.isOwned() || te.isOwner(player) || player.isCreative())
        {
            return super.removedByPlayer(state, world, pos, player, willHarvest);
        }
        return false;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion)
    {
        TileVMBase te = (TileVMBase) world.getTileEntity(pos);
        if (!te.isOwned())
        {
            return super.getExplosionResistance(world, pos, exploder, explosion);
        }
        return 3600000.0F; // like bedrock
    }

    private int playerYawToF3No(float yaw)
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
