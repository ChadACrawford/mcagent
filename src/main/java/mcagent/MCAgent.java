package mcagent;
import mcagent.actuator.PlayerController;
import mcagent.actuator.movement.Move;
import mcagent.actuator.movement.WorldGrid;
import mcagent.util.WorldTools;
import mcagent.util.render.Render3D;
import mcagent.util.render.RenderBase;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.concurrent.Semaphore;


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
        World w = Minecraft.getMinecraft().theWorld;
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;

        if(start == 0) {
            start = Minecraft.getMinecraft().getSystemTime();
        }
        else if(!init && Minecraft.getMinecraft().getSystemTime()>start+4000) {
            init = true;
            drawing = true;
            System.out.println("Searching...");
            WorldGrid.getInstance().explore(p.getPosition().getX(), p.getPosition().getY(), p.getPosition().getZ());
            WorldGrid.getInstance().debugTargets();
            System.out.println("Finished!");
            drawing = false;
        }
        else if(init && !drawing && !moving) {
            PlayerController pc = PlayerController.getInstance();
            pc.moveTo(5000, 100, 5000, true);
            moving = true;
        }
        else if(init && moving) {
            PlayerController pc = PlayerController.getInstance();
            pc.act();
        }

//        if(!init) {
//            init = true;
//            start = Minecraft.getSystemTime();
//        }
//        if(Minecraft.getSystemTime()-start>10000) {
//            System.out.println("Finding valid paths...");
//            start = Minecraft.getSystemTime();
//            Debugger.getInstance().reset(this);
//            Vec3 from = p.getPositionVector();
//            for (int x = -10; x <= 10; x++) {
//                for (int y = -2; y <= 0; y++) {
//                    for (int z = -10; z <= 10; z++) {
//                        Vec3 to = from.addVector(Math.floor(x)+0.5, Math.floor(y), Math.floor(z)+0.5);
//                        if (WorldTools.isValidPath(w, from.addVector(0,-1,0), to)) {
//                            Debugger.getInstance().debugBlock(this, new BlockPos(to), Block.getStateById(89));
//                        }
//                    }
//                }
//            }
//        }
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent event) {
        if(init && !drawing) {
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

    @Override
    public String debugName() {
        return "MCAgent";
    }

//    boolean bs;
//    Vec3 b1, b2;
//    @SubscribeEvent
//    public void playerInteractEvent(PlayerInteractEvent event) {
//        if(event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
//            return;
//        if(Minecraft.getSystemTime() - start < 1000) return;
//        start = Minecraft.getSystemTime();
//        BlockPos b = event.pos;
//        if(bs) {
//            System.out.println("Checking block " + b.toString());
//            b2 = new Vec3(b.getX() + 0.5, b.getY(), b.getZ() + 0.5);
//            List<BlockPos> blocks = WorldTools.intersectingBlocks(event.world, b1, b2);
//            Debugger.getInstance().reset(this);
//            for(BlockPos bi: blocks) {
//                Debugger.getInstance().debugBlock(this, bi, Block.getStateById(20));
//            }
//            bs = !bs;
//        }
//        else if(!bs) {
//            System.out.println("Checking block " + b.toString());
//            b1 = new Vec3(b.getX() + 0.5, b.getY(), b.getZ() + 0.5);
//            bs = !bs;
//        }
//    }

}
