package edu.utulsa.masters.mcagent.actuator;

import edu.utulsa.masters.mcagent.ControllerStatus;
import edu.utulsa.masters.mcagent.GameInfo;
import edu.utulsa.masters.mcagent.actuator.movement.MoveControl;
import edu.utulsa.masters.mcagent.actuator.movement.Path;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

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
            Vec3[] refinedPath = path.getRefinedPath();
            moveControls = new MoveControl[refinedPath.length];
            for(int i = 0; i < moveControls.length; i++) {
                moveControls[i] = new MoveControl(pc, refinedPath[i]);
            }
            path.debug();
        }
    }

    public LinkedList<BlockPos> getPath() {
        return path.getPath();
    }
    int currentPosition = 0;
    MoveControl[] moveControls;

    @Override
    public void performAction() {
        if(status == ControllerStatus.WAITING) status = ControllerStatus.BUSY;

        if(status != ControllerStatus.BUSY) return;

        if(moveControls[currentPosition].done()) {
            currentPosition++;
            while(currentPosition+1 < moveControls.length && moveControls[currentPosition+1].isValid())
                currentPosition++;

            if(currentPosition >= moveControls.length) {
                status = ControllerStatus.FINISHED;
                return;
            }

            System.out.format("%d ", currentPosition);
            moveControls[currentPosition].print();
        }

        moveControls[currentPosition].run();
    }
}
