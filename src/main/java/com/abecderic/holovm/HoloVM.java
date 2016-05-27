package com.abecderic.holovm;

import com.abecderic.holovm.block.BlockVMBase;
import com.abecderic.holovm.block.TileVMBase;
import com.abecderic.holovm.item.ItemBlockVMBase;
import com.abecderic.holovm.network.VMBasePacket;
import com.abecderic.holovm.network.VMBaseRequest;
import com.abecderic.holovm.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = HoloVM.MODID, name = HoloVM.MODNAME, version = "1.9.4-1.0")
public class HoloVM
{
    @Mod.Instance(HoloVM.MODID)
    public static HoloVM instance;
    public static SimpleNetworkWrapper snw;

    public static final String MODID = "holovm";
    public static final String MODNAME = "Holographic Victory Monument";
    public static final String VMBASE_KEY = "vmbase";
    public static Block vmbase;

    @SidedProxy(clientSide = "com.abecderic.holovm.proxy.ClientProxy", serverSide = "com.abecderic.holovm.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        vmbase = new BlockVMBase();
        GameRegistry.register(vmbase);
        GameRegistry.register(new ItemBlockVMBase(vmbase).setRegistryName(vmbase.getRegistryName()));
        GameRegistry.registerTileEntity(TileVMBase.class, VMBASE_KEY);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        snw = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        snw.registerMessage(VMBasePacket.Handler.class, VMBasePacket.class, 0, Side.CLIENT);
        snw.registerMessage(VMBaseRequest.Handler.class, VMBaseRequest.class, 1, Side.SERVER);
        GameRegistry.addShapedRecipe(new ItemStack(vmbase, 1, 0), " g ", "rir", "sss", 'g', Items.GLOWSTONE_DUST, 'r', Items.REDSTONE, 'i', Items.SIGN, 's', Blocks.STONE);
        proxy.init();
    }
}