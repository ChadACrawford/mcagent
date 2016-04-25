package mcagent;
import mcagent.actuator.PlayerController;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
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
public class MCAgent implements DebugObject {
    public static final String MODID = "Minecraft Agent";
    public static final String VERSION = "0.1";
    private Debugger debug = new Debugger(this);

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        PlayerController.setKeyBindings();
        MCAgent mc = new MCAgent();
        FMLCommonHandler.instance().bus().register(mc);
        MinecraftForge.EVENT_BUS.register(mc);
    }

    boolean init = false, drawing=false, moving = false;
    long start = 0;
    @SubscribeEvent
    public void onPlayerUpdate(TickEvent.PlayerTickEvent e) {
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
    }

    @Override
    public String debugName() {
        return "MCAgent";
    }
}
