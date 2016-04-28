package edu.utulsa.masters.mcagent;
import edu.utulsa.masters.mcagent.actuator.PlayerController;
import edu.utulsa.masters.mcagent.util.OverrideMouseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by Chad on 5/24/2015.
 *
 * Top-level class handles all agent behavior.
 */


@SideOnly(Side.CLIENT)
@Mod(modid = MCAgentMod.MODID, version = MCAgentMod.VERSION)
public class MCAgentMod {
    public static final String MODID = "Minecraft Agent";
    public static final String VERSION = "0.1";
    private Debugger debug = new Debugger(this);
    private MCAgent activeAgent = null;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        PlayerController.setKeyBindings();
        Minecraft.getMinecraft().mouseHelper = new OverrideMouseHelper();

        MCAgentMod mc = new MCAgentMod();
        FMLCommonHandler.instance().bus().register(mc);
        MinecraftForge.EVENT_BUS.register(mc);
    }

    @SubscribeEvent
    public void onPlayerUpdate(TickEvent.PlayerTickEvent e) {
        Minecraft.getMinecraft().gameSettings.pauseOnLostFocus = false;
        Minecraft.getMinecraft().inGameHasFocus = false;
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        //System.out.println(player);
        if(player == null) {
            return;
        }
        MCAgent agent = MCAgent.getAgent(player);
        if(!agent.isAlive()) {
            System.out.println(agent.id);
            agent.start();
        }
        activeAgent = agent;
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if(activeAgent == null) return;

        activeAgent.renderEvent();
    }

//    @SubscribeEvent
//    public void onWorldLoad(WorldEvent.Load e) {
//
//    }

    @Override
    public String toString() {
        return "MCAgentMod";
    }
}
