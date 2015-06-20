package mcagent;
import mcagent.actuator.PlayerController;
import mcagent.actuator.movement.WorldGrid;
import mcagent.util.render.Render3D;
import mcagent.util.render.RenderBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;


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
        PlayerController.setKeyBindings();
        MCAgent mc = new MCAgent();
        FMLCommonHandler.instance().bus().register(mc);
        MinecraftForge.EVENT_BUS.register(mc);
    }

    boolean init = false, busy=false;
    long start = 0;
    @SubscribeEvent
    public void onPlayerUpdate(TickEvent.PlayerTickEvent e) {
        World w = Minecraft.getMinecraft().theWorld;
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        if(start == 0) {
            start = Minecraft.getMinecraft().getSystemTime();
        }
        else if(!init && !busy && Minecraft.getMinecraft().getSystemTime()>start+4000) {
            busy = true;
            System.out.println("Searching...");
            WorldGrid.getInstance().explore(p.getPosition().getX(), p.getPosition().getY(), p.getPosition().getZ());
            WorldGrid.getInstance().debugTargets();
            PlayerController pc = PlayerController.getInstance();
            pc.moveTo(5000,100,5000,true);
            System.out.println("Finished!");
            init = true;
        }
        else if(init) {
            PlayerController pc = PlayerController.getInstance();
            pc.act();
        }
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        if(init) {
            RenderBase.EnableDrawingMode();
            WorldGrid wg = WorldGrid.getInstance();
            Render3D render = new Render3D();
            render.setTessellator(Tessellator.getInstance());
            render.setOffset(event.partialTicks);
            wg.drawEdges(render);
            RenderBase.DisableDrawingMode();
            //System.out.println("FUCK IT ALL");
            //t.draw();
        }
    }
}
