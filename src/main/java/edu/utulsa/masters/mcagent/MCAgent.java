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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by chad on 4/26/16.
 */
public class MCAgent extends Thread {
    private static int idCount = 0;
    int id;
    World world;
    EntityPlayerSP player;
    PlayerController pc;
    Logger log;

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
        log = Logger.getLogger(String.format("MCAgent {id: %d}", id ));
    }

//    public void log(String format, Object... args) {
//        String message = String.format(format, args);
//        System.out.format("[MCAgent id: %d] %s\n", id, message);
//    }

    public void run() {
        log.log(Level.INFO, String.format("Started { world: %s player: %s }", world.toString(), player.toString()));

        // Wait a bit for the world to load
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        player = Minecraft.getMinecraft().thePlayer;

        log.log(Level.INFO, "Beginning execution...");

        while(true) {
            randomWalk();
        }
    }

    public void randomWalk() {
        BlockPos p1 = player.getPosition().add(0,-1,0);
        BlockPos p2 = p1.add(5,0,5);
        if(WorldTools.isSolid(world, p2)) {
            Vec3 v2 = WorldTools.findAboveSurface(world, WorldTools.toVec3(p2));
            if(v2 != null) p2 = new BlockPos(v2);
        }
        else {
            Vec3 v2 = WorldTools.findBelowSurface(world, WorldTools.toVec3(p2));
            if(v2 != null) p2 = new BlockPos(v2);
        }

        ActionMove a = new ActionMove(pc, p2.getX(), p2.getY(), p2.getZ());

        log.log(Level.INFO, "Finished computing path. Now to follow it.");

        while(true) {
            ControllerStatus status = a.getStatus();
            //log.log(Level.INFO, status.toString());
            if(status == ControllerStatus.FINISHED) {
                log.log(Level.INFO, "Finished!");
                break;
            }
            if(status == ControllerStatus.FAILURE) {
                log.log(Level.INFO, "Failed!");
                break;
            }
            a.performAction();
        }
    }

    public void renderEvent() {
        pc.doLook();
    }
}
