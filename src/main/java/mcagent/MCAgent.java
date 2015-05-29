package mcagent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


/**
 * Created by Chad on 5/24/2015.
 *
 * Top-level class handles all agent behavior.
 */


@SideOnly(Side.CLIENT)
@Mod(modid = MCAgent.MODID, version = MCAgent.VERSION)
public class MCAgent {
    public static final String MODID = "Minecraft Agent";
    public static final String VERSION = "0.1";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(new MCAgent());
    }

    @SubscribeEvent
    public void onPlayerUpdate(TickEvent.PlayerTickEvent e) {
    }
}
