package com.abecderic.holovm.network;

import com.abecderic.holovm.block.TileVMBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class VMBaseRequest implements IMessage
{
	private int dim;
	private BlockPos pos;
	
	public VMBaseRequest(){}
	
	public VMBaseRequest(int dim, BlockPos pos)
	{
		this.dim = dim;
		this.pos = pos;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		dim = buf.readInt();
		pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(dim);
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
	}
	
	public static class Handler implements IMessageHandler<VMBaseRequest, VMBasePacket>
	{
		@Override
		public VMBasePacket onMessage(VMBaseRequest msg, MessageContext ctx)
		{
			TileEntity te = DimensionManager.getWorld(msg.dim).getTileEntity(msg.pos);
			if (te != null && te instanceof TileVMBase)
			{
				ItemStack[] stack = ((TileVMBase)te).getStacks();
				ItemStack camouflage = ((TileVMBase)te).getCamouflage();
				byte direction = ((TileVMBase)te).getDirection();

                /* handle map data */
                for (ItemStack s : stack)
                {
                    if (s != null && s.getItem().isMap())
                    {
                        ItemMap map  = (ItemMap)s.getItem();
                        Packet<?> packet = map.getMapData(s, DimensionManager.getWorld(msg.dim)).getMapInfo(ctx.getServerHandler().playerEntity).getPacket(s);
                        System.out.println("stack: " + s + ", packet: " + packet);
                        if (packet != null)
                        {
                            ctx.getServerHandler().sendPacket(packet);
                        }
                    }
                }

				return new VMBasePacket(msg.pos, stack, camouflage, direction);
			}
			return null;
		}
	}
}
