package edu.utulsa.masters.mcagent.actuator;

import edu.utulsa.masters.mcagent.ControllerStatus;
import edu.utulsa.masters.mcagent.Debugger;
import edu.utulsa.masters.mcagent.actuator.Path;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;

import java.util.LinkedList;

/**
 * Created by Chad on 5/24/2015.
 */
public class ActionMove extends PlayerControllerAction {

    private double moveX,moveY,moveZ;
    private Path path;

    public ActionMove(PlayerController pc, double moveX, double moveY, double moveZ) {
        super(pc);
        EntityPlayerSP player = pc.getPlayer();
        this.moveX = moveX;
        this.moveY = moveY;
        this.moveZ = moveZ;
        if(player.getPosition().distanceSq(moveX, moveY, moveZ) > 10000)
            status = ControllerStatus.FAILURE;
        else {
            status = ControllerStatus.WAITING;
            path = Path.compute(pc.getWorld(), player.getPosition().add(0,-1,0), new BlockPos(moveX, moveY, moveZ));
            if(path == null) {
                status = ControllerStatus.FAILURE;
                return;
            }
            path.debug();
        }
    }

    public LinkedList<BlockPos> getPath() {
        return path.getPath();
    }

    @Override
    public void performAction() {
        if(status == ControllerStatus.WAITING) status = ControllerStatus.BUSY;

        if(status != ControllerStatus.BUSY) return;

        boolean complete = path.control(pc);

        if(complete) status = ControllerStatus.FINISHED;
    }
}
