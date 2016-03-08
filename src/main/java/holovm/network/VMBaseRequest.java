package holovm.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import holovm.HoloVM;
import holovm.block.TileEntityVMBase;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class VMBaseRequest implements IMessage
{
	private int dim;
	private int x;
	private int y;
	private int z;
	
	public VMBaseRequest(){}
	
	public VMBaseRequest(int dim, int x, int y, int z)
	{
		this.dim = dim;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		dim = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(dim);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
	
	public static class Handler implements IMessageHandler<VMBaseRequest, VMBasePacket>
	{
		@Override
		public VMBasePacket onMessage(VMBaseRequest msg, MessageContext ctx)
		{
			TileEntity te = DimensionManager.getWorld(msg.dim).getTileEntity(msg.x, msg.y, msg.z);
			if (te != null && te instanceof TileEntityVMBase)
			{
				ItemStack[] stack = ((TileEntityVMBase)te).getStacks();
				ItemStack camouflage = ((TileEntityVMBase)te).getCamouflage();
				byte direction = ((TileEntityVMBase)te).getDirection();
				return new VMBasePacket(msg.x, msg.y, msg.z, stack, camouflage, direction);
			}
			return null;
		}
	}
}
