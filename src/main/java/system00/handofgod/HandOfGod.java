package system00.handofgod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;


@Mod(modid = HandOfGod.MODID, name = HandOfGod.NAME, version = HandOfGod.VERSION)
public class HandOfGod {

	public static final String MODID = "handofgod";
	public static final String NAME = "The Hand Of God";
	public static final String VERSION = "@VERSION@";

	@Instance(HandOfGod.MODID)
	public static HandOfGod instance;

	public static Logger log;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		log = event.getModLog();
	}
}
