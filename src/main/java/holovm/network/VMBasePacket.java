package holovm.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import holovm.block.TileEntityVMBase;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class VMBasePacket implements IMessage
{
	private int x;
	private int y;
	private int z;
	private int stackLength;
	private ItemStack[] stack;
	private ItemStack camouflage;
	private byte direction;
	
	public VMBasePacket() {}
	
	public VMBasePacket(int x, int y, int z, ItemStack[] stack, ItemStack camouflage, byte direction)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.stack = stack;
		this.stackLength = stack.length;
		this.camouflage = camouflage;
		this.direction = direction;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		stackLength = buf.readInt();
		stack = new ItemStack[stackLength];
		for (int i = 0; i < stackLength; i++)
		{
			stack[i] = ByteBufUtils.readItemStack(buf);
		}
		camouflage = ByteBufUtils.readItemStack(buf);
		direction = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(stackLength);
		for (int i = 0; i < stackLength; i++)
		{
			ByteBufUtils.writeItemStack(buf, stack[i]);
		}
		ByteBufUtils.writeItemStack(buf, camouflage);
		buf.writeByte(direction);
	}
	
	public static class Handler implements IMessageHandler<VMBasePacket, IMessage>
	{
		@Override
		public IMessage onMessage(VMBasePacket msg, MessageContext ctx)
		{
			TileEntityVMBase te = (TileEntityVMBase)Minecraft.getMinecraft().theWorld.getTileEntity(msg.x, msg.y, msg.z);
			for (int i = 0; i < msg.stackLength; i++)
			{
				te.setInventorySlotContents(i, msg.stack[i]);
			}
			te.setInventorySlotContents(-1, msg.camouflage);
			te.setDirection(msg.direction);
			Minecraft.getMinecraft().theWorld.markBlockForUpdate(msg.x, msg.y, msg.z);
			return null;
		}
	}
}
