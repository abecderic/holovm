package com.abecderic.holovm.render;

import com.abecderic.holovm.HoloVM;
import com.abecderic.holovm.block.BlockVMBase;
import com.abecderic.holovm.block.TileVMBase;
import com.abecderic.holovm.network.VMBaseRequest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;

public class VMBaseRenderer extends TileEntitySpecialRenderer<TileVMBase>
{
    private static final ResourceLocation MAP_BG = new ResourceLocation("textures/map/map_background.png");
    private static ItemStack vmbaseStack = null;

    @Override
    public void renderTileEntityAt(TileVMBase te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        ItemStack camouflage = te.getCamouflage();

        IBlockState state = te.getBlockType().getStateFromMeta(te.getBlockMetadata());
        boolean stacksNull = true;
        for (int i = 0; i < te.getSizeInventory(); i++)
        {
            if (te.getStackInSlot(i) != null)
            {
                stacksNull = false;
                break;
            }
        }
        if (state.getValue(BlockVMBase.HASITEM) && stacksNull && camouflage == null)
        {
            HoloVM.snw.sendToServer(new VMBaseRequest(te.getWorld().provider.getDimensionId(), te.getPos()));
        }

        renderVM(te, state, x, y, z);
    }

    private void renderVM(TileVMBase tileentity, IBlockState state, double x, double y, double z)
    {
        setupRendering(x, y, z);

        int brightness = tileentity.getWorld().getCombinedLight(tileentity.getPos(), 0);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(brightness % 0x10000) / 1f, (float)(brightness / 0x10000) / 1f);

        GlStateManager.blendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);

        ItemStack stack = tileentity.getCamouflage();
        if (!state.getValue(BlockVMBase.HASITEM) || stack == null)
        {
            if (vmbaseStack == null)
            {
                vmbaseStack = new ItemStack(HoloVM.vmbase);
            }
            stack = vmbaseStack;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (!state.getValue(BlockVMBase.ADV))
        {
            if (tileentity.getStackInSlot(0) != null)
            {
                renderItemStack(tileentity, x, y, z, tileentity.getStackInSlot(0), tileentity.getDirection());
            }
        }
        else
        {
            for (int i = 0; i < tileentity.getSizeInventory(); i++)
            {
                if (tileentity.getStackInSlot(i) == null) continue;
                int direction = 0;
                if (i >= 2) direction = 6+i;
                else if (i == 0) direction = tileentity.getDirection() & 3;
                else if (i == 1) direction = 4 + ((tileentity.getDirection() & 12) >> 2);
                renderItemStack(tileentity, x, y, z, tileentity.getStackInSlot(i), direction);
            }
        }

        finishRendering();
    }

    private void renderItemStack(TileVMBase te, double x, double y, double z, ItemStack stack, int direction)
    {
        GlStateManager.pushMatrix();
        translateOnDirection(direction);

        if (stack.getItem() instanceof net.minecraft.item.ItemMap)
        {
            Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(MAP_BG);
            GlStateManager.rotate(180.0F, 180.0F, 0.0F, 1.0F);
            float f = 0.0078125F;
            GlStateManager.scale(f, f, f);
            GlStateManager.translate(-64.0F, -64.0F, 0.0F);
            MapData mapdata = Items.filled_map.getMapData(stack, te.getWorld());
            GlStateManager.translate(0.0F, 0.0F, -1.0F);

            if (mapdata != null)
            {
                // boolean = hide player markers on map
                // TODO make this a config option
                Minecraft.getMinecraft().entityRenderer.getMapItemRenderer().renderMap(mapdata, true);
            }
        }
        else
        {
            Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
        GlStateManager.popMatrix();
    }

    private void setupRendering(double x, double y, double z)
    {
        GlStateManager.pushMatrix();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (Minecraft.isAmbientOcclusionEnabled())
        {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        }
        else
        {
            GL11.glShadeModel(GL11.GL_FLAT);
        }

        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
    }

    private void translateOnDirection(int direction)
    {
        switch (direction)
        {
            case 0:
                GL11.glTranslated(0.0D, -1.0D, 0.0D);
                GL11.glRotated(180.0D, 0.0D, 1.0D, 0.0D);
                break;
            case 1:
                GL11.glTranslated(0.0D, -1.0D, 0.0D);
                GL11.glRotated(90.0D, 0.0D, 1.0D, 0.0D);
                break;
            case 2:
                GL11.glTranslated(0.0D, -1.0D, 0.0D);
                break;
            case 3:
                GL11.glTranslated(0.0D, -1.0D, 0.0D);
                GL11.glRotated(-90.0D, 0.0D, 1.0D, 0.0D);
                break;
            case 4:
                GL11.glTranslated(0.0D, 1.0D, 0.0D);
                GL11.glRotated(180.0D, 0.0D, 1.0D, 0.0D);
                break;
            case 5:
                GL11.glTranslated(0.0D, 1.0D, 0.0D);
                GL11.glRotated(90.0D, 0.0D, 1.0D, 0.0D);
                break;
            case 6:
                GL11.glTranslated(0.0D, 1.0D, 0.0D);
                break;
            case 7:
                GL11.glTranslated(0.0D, 1.0D, 0.0D);
                GL11.glRotated(-90.0D, 0.0D, 1.0D, 0.0D);
                break;
            case 8:
                GL11.glTranslated(0.0D, 0.0D, -1.0D);
                GL11.glRotated(180.0D, 0.0D, 1.0D, 0.0D);
                break;
            case 9:
                GL11.glTranslated(0.0D, 0.0D, 1.0D);
                break;
            case 10:
                GL11.glTranslated(-1.0D, 0.0D, 0.0D);
                GL11.glRotated(-90.0D, 0.0D, 1.0D, 0.0D);
                break;
            case 11:
                GL11.glTranslated(1.0D, 0.0D, 0.0D);
                GL11.glRotated(90.0D, 0.0D, 1.0D, 0.0D);
        }
    }

    private void finishRendering()
    {
        GlStateManager.disableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}