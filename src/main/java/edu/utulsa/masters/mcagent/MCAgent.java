package edu.utulsa.masters.mcagent;

import edu.utulsa.masters.mcagent.actuator.ActionMove;
import edu.utulsa.masters.mcagent.actuator.PlayerController;
import edu.utulsa.masters.mcagent.util.WorldTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This agent controllers a single MC player.
 */
public class MCAgent extends Thread {
    private static int idCount = 0;
    int id;
    World world;
    EntityPlayerSP player;
    PlayerController pc;
    Random rand = new Random();
    Debugger debug = new Debugger(this);

    private static HashMap<EntityPlayerSP, MCAgent> agents = new HashMap<EntityPlayerSP, MCAgent>();
    public static synchronized MCAgent getAgent(EntityPlayerSP player) {
        if(!agents.containsKey(player)) {
            agents.put(player, new MCAgent(player));
        }
        return agents.get(player);
    }

    public MCAgent(EntityPlayerSP player) {
        this.player = player;
        this.world = player.getEntityWorld();
        this.pc = new PlayerController(world, player);
        this.id = idCount++;
    }

//    public void log(String format, Object... args) {
//        String message = String.format(format, args);
//        System.out.format("[MCAgent id: %d] %s\n", id, message);
//    }

    public void run() {
        debug.info(String.format("Started { world: %s player: %s }", world.toString(), player.toString()));

        // Wait a bit for the world to load
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        player = Minecraft.getMinecraft().thePlayer;

        debug.info("Beginning execution...");

        while(true) {
            randomWalk();
        }
    }

    public void randomWalk() {
        BlockPos p1 = player.getPosition().add(0,-1,0);
        BlockPos p2 = p1.add(rand.nextInt(10),0,rand.nextInt(10));

        debug.format("Trying path %s.", p2.toString());

        if(WorldTools.isSolid(world, p2)) {
            p2 = WorldTools.findAboveSurface(world, p2);
        }
        else {
            p2 = WorldTools.findBelowSurface(world, p2);
        }

        if(p2 == null) {
            debug.info("Invalid path.");
            return;
        }

        ActionMove a = new ActionMove(pc, p2.getX(), p2.getY(), p2.getZ());

        debug.info("Finished computing path. Now to follow it.");

        while(true) {
            ControllerStatus status = a.getStatus();
            //log.log(Level.INFO, status.toString());
            if(status == ControllerStatus.FINISHED) {
                debug.info("Finished!");
                break;
            }
            if(status == ControllerStatus.FAILURE) {
                debug.info("Failed!");
                break;
            }
            a.performAction();
        }
    }

    public void renderEvent() {
        pc.doLook();
    }

    @Override
    public String toString() {
        return String.format("MCAgent id: %d", id);
    }
}
