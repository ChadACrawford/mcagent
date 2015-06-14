package mcagent;
import mcagent.actuator.movement.WorldGrid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
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

    boolean init = false;
    long start = 0;
    @SubscribeEvent
    public void onPlayerUpdate(TickEvent.PlayerTickEvent e) {
        World w = Minecraft.getMinecraft().theWorld;
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        if(start == 0) {
            start = Minecraft.getMinecraft().getSystemTime();
        }
        else if(!init && Minecraft.getMinecraft().getSystemTime()>start+10000) {
            init = true;
            WorldGrid.getInstance().explore(p.getPosition().getX(), p.getPosition().getY(), p.getPosition().getZ());
            WorldGrid.getInstance().debugTargets();
        }
    }
}
