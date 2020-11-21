package system00.handofgod;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class ModItems {

	public final static Item handOfGod = new Item().setCreativeTab(CreativeTabs.MISC);

	@SubscribeEvent
	public static void registerItem(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(handOfGod.setRegistryName(HandOfGod.MODID, "hand_of_god"));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerModel(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(handOfGod, 0,
				new ModelResourceLocation(handOfGod.getRegistryName(), "inventory"));
	}
}
