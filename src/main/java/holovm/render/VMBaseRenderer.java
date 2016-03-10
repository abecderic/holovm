package holovm.render;

import org.lwjgl.opengl.GL11;

import holovm.HoloVM;
import holovm.block.TileEntityVMBase;
import holovm.network.VMBaseRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class VMBaseRenderer extends TileEntitySpecialRenderer
{
	private RenderItem renderer;

	public VMBaseRenderer()
	{
		this.renderer = new RenderItem()
		{
			public boolean shouldBob()
			{
				return false;
			}
		};
		this.renderer.setRenderManager(RenderManager.instance);
	}

	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f)
	{
		if (te.blockMetadata == 0) return; /* no item */
		TileEntityVMBase tileentity = (TileEntityVMBase)te;
		ItemStack camouflage = tileentity.getCamouflage();
		boolean stacksNull = true;
		for (int i = 0; i < tileentity.getSizeInventory(); i++)
		{
			if (tileentity.getStackInSlot(i) != null)
			{
				stacksNull = false;
				break;
			}
		}
		if (stacksNull && camouflage == null)
		{
			HoloVM.snw.sendToServer(new VMBaseRequest(te.getWorldObj().provider.dimensionId, te.xCoord, te.yCoord, te.zCoord));
			return;
		}
		if (stacksNull)
		{
			return;
		}
		renderVM(tileentity, x, y, z);
	}
	
	protected void renderVM(TileEntityVMBase tileentity, double x, double y, double z)
	{
		if (tileentity.getStackInSlot(0) != null)
			renderItemStack(tileentity.getWorldObj(), x, y, z, tileentity.getStackInSlot(0), tileentity.getDirection());
	}
	
	protected void renderItemStack(World world, double x, double y, double z, ItemStack stack, int direction)
	{
		GL11.glPushMatrix();

		EntityItem item = new EntityItem(world);
		item.hoverStart = 0.0F;
		item.setEntityItemStack(stack);
		GL11.glTranslated(x + 0.5D, y + 0.3D, z + 0.5D);
		boolean doRotate = Minecraft.getMinecraft().gameSettings.fancyGraphics;
		switch (direction)
		{
			case 0:
				GL11.glTranslated(0.0D, -1.0D, 0.0D);
				break;
			case 1:
				GL11.glTranslated(0.0D, -1.0D, 0.0D);
				if (doRotate) GL11.glRotated(-90.0D, 0.0D, 1.0D, 0.0D);
				break;
			case 2:
				GL11.glTranslated(0.0D, -1.0D, 0.0D);
				if (doRotate) GL11.glRotated(180.0D, 0.0D, 1.0D, 0.0D);
				break;
			case 3:
				GL11.glTranslated(0.0D, -1.0D, 0.0D);
				if (doRotate) GL11.glRotated(90.0D, 0.0D, 1.0D, 0.0D);
				break;
			case 4:
				GL11.glTranslated(0.0D, 1.0D, 0.0D);
				break;
			case 5:
				GL11.glTranslated(0.0D, 1.0D, 0.0D);
				if (doRotate) GL11.glRotated(-90.0D, 0.0D, 1.0D, 0.0D);
				break;
			case 6:
				GL11.glTranslated(0.0D, 1.0D, 0.0D);
				if (doRotate) GL11.glRotated(180.0D, 0.0D, 1.0D, 0.0D);
				break;
			case 7:
				GL11.glTranslated(0.0D, 1.0D, 0.0D);
				if (doRotate) GL11.glRotated(90.0D, 0.0D, 1.0D, 0.0D);
				break;
			case 8:
				GL11.glTranslated(0.0D, 0.0D, -1.0D);
				break;
			case 9:
				GL11.glTranslated(0.0D, 0.0D, 1.0D);
				if (doRotate) GL11.glRotated(180.0D, 0.0D, 1.0D, 0.0D);
				break;
			case 10:
				GL11.glTranslated(-1.0D, 0.0D, 0.0D);
				if (doRotate) GL11.glRotated(90.0D, 0.0D, 1.0D, 0.0D);
				break;
			case 11:
				GL11.glTranslated(1.0D, 0.0D, 0.0D);
				if (doRotate) GL11.glRotated(-90.0D, 0.0D, 1.0D, 0.0D);
		}
		GL11.glScaled(2.0D, 2.0D, 2.0D);
		this.renderer.doRender(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

		GL11.glPopMatrix();
	}
}