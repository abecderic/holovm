package com.abecderic.holovm.network;

import com.abecderic.holovm.block.TileVMBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class VMBasePacket implements IMessage
{
	private BlockPos pos;
	private int stackLength;
	private ItemStack[] stack;
	private ItemStack camouflage;
	private byte direction;

	public VMBasePacket()
	{
	}

	public VMBasePacket(BlockPos pos, ItemStack[] stack, ItemStack camouflage, byte direction)
	{
		this.pos = pos;
		this.stack = stack;
		this.stackLength = stack.length;
		this.camouflage = camouflage;
		this.direction = direction;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
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
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
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
			TileVMBase te = (TileVMBase) Minecraft.getMinecraft().theWorld.getTileEntity(msg.pos);
			if (te != null)
			{
				for (int i = 0; i < msg.stackLength; i++)
				{
					te.setInventorySlotContents(i, msg.stack[i]);
				}
				te.setInventorySlotContents(-1, msg.camouflage);
				te.setDirection(msg.direction);
				Minecraft.getMinecraft().theWorld.markBlockForUpdate(msg.pos);
			}
			return null;
		}
	}
}
