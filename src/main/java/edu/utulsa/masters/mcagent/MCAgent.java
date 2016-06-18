package edu.utulsa.masters.mcagent;

import edu.utulsa.masters.mcagent.actuator.PlayerController;
import edu.utulsa.masters.mcagent.actuator.PlayerControllerAction;
import edu.utulsa.masters.mcagent.overrides.OverrideMouseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * This agent controllers a single MC player.
 */
public abstract class MCAgent extends Thread {
    private static int idCount = 0;
    protected boolean overrideWindow = true;
    int id;
    World world;
    EntityPlayerSP player;
    PlayerController pc;
    Random rand = new Random();
    Debugger debug = new Debugger(this);

    protected MCAgent(EntityPlayerSP player) {
        this.player = player;
        this.world = player.getEntityWorld();
        this.pc = new PlayerController(world, this.player);
        this.id = idCount++;
    }

    public boolean isCurrentPlayer() {
        return this.player.getUniqueID() == Minecraft.getMinecraft().thePlayer.getUniqueID();
    }

    protected boolean stayAlive = true;
    public void kill() {
        stayAlive = false;
    }

//    public void log(String format, Object... args) {
//        String message = String.format(format, args);
//        System.out.format("[MCAgent id: %d] %s\n", id, message);
//    }

    public void prepare() {
        Minecraft.getMinecraft().thePlayer = this.player;
        Minecraft.getMinecraft().setRenderViewEntity(this.player);
    }

    public void run() {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);

        debug.info(String.format("Started { world: %s player: %s }", world.toString(), player.toString()));

        // Wait a bit for the world to load
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        debug.info("Beginning execution...");

        exec();

        FMLCommonHandler.instance().bus().unregister(this);
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public abstract void exec();

//    public void randomWalk() {
//        BlockPos p1 = player.getPosition().add(0,-1,0);
//        BlockPos p2 = p1.add(rand.nextInt(10),0,rand.nextInt(10));
//
//        debug.format("Trying path %s.", p2.toString());
//
//        if(WorldTools.isSolid(world, p2)) {
//            p2 = WorldTools.findAboveSurface(world, p2);
//        }
//        else {
//            p2 = WorldTools.findBelowSurface(world, p2);
//        }
//
//        if(p2 == null) {
//            debug.info("Invalid path.");
//            return;
//        }
//
//        ActionMove a = new ActionMove(pc, p2.getX(), p2.getY(), p2.getZ());
//
//        debug.info("Finished computing path. Now to follow it.");
//
//        while(true) {
//            ControllerStatus status = a.getStatus();
//            //log.log(Level.INFO, status.toString());
//            if(status == ControllerStatus.FINISHED) {
//                debug.info("Finished!");
//                break;
//            }
//            if(status == ControllerStatus.FAILURE) {
//                debug.info("Failed!");
//                break;
//            }
//            a.performAction();
//        }
//    }

    protected CountDownLatch actionLatch = new CountDownLatch(1);
    protected PlayerControllerAction currentAction = null;
    protected boolean actionRunning = false;
    protected void waitOnActionComplete() {
        try {
            actionLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    protected void setAction(PlayerControllerAction action) {
        currentAction = action;
        actionRunning = true;
    }
    protected void stopAction() {
        actionRunning = false;
        actionLatch.countDown();
        actionLatch = new CountDownLatch(1);
    }
    private void runCurrentAction() {
        if(!actionRunning) return;
        if(currentAction == null || currentAction.isFinished()) {
            actionRunning = false;
            actionLatch.countDown();
            actionLatch = new CountDownLatch(1);
        }
        else {
            currentAction.act();
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(overrideWindow) {
            OverrideMouseHelper.override = true;
            Minecraft.getMinecraft().gameSettings.pauseOnLostFocus = false;
            Minecraft.getMinecraft().inGameHasFocus = false;
        } else {
            OverrideMouseHelper.override = false;
            Minecraft.getMinecraft().gameSettings.pauseOnLostFocus = true;
            //Minecraft.getMinecraft().inGameHasFocus = true;
        }
        if(event.phase == TickEvent.Phase.START) {
            pc.prePlayerTick();
            runCurrentAction();
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if(overrideWindow) {
            pc.renderTick();
        }
    }

    @Override
    public String toString() {
        return String.format("MCAgent id: %d", id);
    }
}
