package holovm;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import holovm.block.BlockVMAdvanced;
import holovm.block.BlockVMBase;
import holovm.block.TileEntityVMAdv;
import holovm.block.TileEntityVMBase;
import holovm.item.ItemBlockVMBase;
import holovm.network.VMBasePacket;
import holovm.network.VMBaseRequest;
import holovm.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@Mod(modid = HoloVM.MODID, name = HoloVM.MODNAME, version = "1.2.2")
public class HoloVM
{
	@Instance(HoloVM.MODID)
	public static HoloVM instance;
	public static SimpleNetworkWrapper snw;
	
	public static final String MODID = "holovm";
	public static final String MODNAME = "Holographic Victory Monument";
	public static final String VMBASE_KEY = "vmbase";
	public static final String VMADV_KEY = "vmadv";
	public static Block vmbase;
	public static Block vmadv;
	
	@SidedProxy(clientSide = "holovm.proxy.ClientProxy", serverSide = "holovm.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		vmbase = new BlockVMBase();
		GameRegistry.registerBlock(vmbase, ItemBlockVMBase.class, VMBASE_KEY);
		GameRegistry.registerTileEntity(TileEntityVMBase.class, VMBASE_KEY);
		vmadv = new BlockVMAdvanced();
		GameRegistry.registerBlock(vmadv, ItemBlockVMBase.class, VMADV_KEY);
		GameRegistry.registerTileEntity(TileEntityVMAdv.class, VMADV_KEY);
		proxy.init();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		snw = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		snw.registerMessage(VMBasePacket.Handler.class, VMBasePacket.class, 0, Side.CLIENT);
		snw.registerMessage(VMBaseRequest.Handler.class, VMBaseRequest.class, 1, Side.SERVER);
		GameRegistry.addShapedRecipe(new ItemStack(vmbase), " g ", "rir", "sss", 'g', Items.glowstone_dust, 'r', Items.redstone, 'i', Items.sign, 's', Blocks.stone);
		GameRegistry.addShapedRecipe(new ItemStack(vmadv), " g ", "gvg", " g ", 'g', Items.gold_ingot, 'v', vmbase);
	}
}
