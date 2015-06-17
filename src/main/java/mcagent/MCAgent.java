package mcagent;
import mcagent.actuator.PlayerController;
import mcagent.actuator.movement.WorldGrid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
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
        FMLCommonHandler.instance().bus().register(mc);
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
            //pc.moveTo(5000,100,5000,true);
            System.out.println("Finished!");
            init = true;
        }
        else if(init) {
            //PlayerController pc = PlayerController.getInstance();
            //pc.act();
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        /*if(init) {
            Tessellator t = Tessellator.getInstance();
            EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
            Vec3 pos = p.getPositionVector();
            GL11.glPushMatrix();
            GL11.glTranslated(pos.xCoord, pos.yCoord, pos.zCoord);
            WorldGrid wg = WorldGrid.getInstance();
            wg.drawEdges();
            GL11.glPopMatrix();
            //t.draw();
        }*/
        if(init) {
            EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
            double doubleX = p.posX - 0.5;
            double doubleY = p.posY + 0.1;
            double doubleZ = p.posZ - 0.5;

            GL11.glPushMatrix();
            GL11.glTranslated(-doubleX, -doubleY, -doubleZ);
            GL11.glColor3ub((byte) 255, (byte) 0, (byte) 0);
            float mx = 9;
            float my = 9;
            float mz = 9;
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex3f(mx + 0.4f, my, mz + 0.4f);
            GL11.glVertex3f(mx - 0.4f, my, mz - 0.4f);
            GL11.glVertex3f(mx + 0.4f, my, mz - 0.4f);
            GL11.glVertex3f(mx - 0.4f, my, mz + 0.4f);
            GL11.glEnd();
            GL11.glPopMatrix();
        }
    }
}
