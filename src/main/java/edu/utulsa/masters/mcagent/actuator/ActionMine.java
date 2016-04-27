package edu.utulsa.masters.mcagent.actuator;

import edu.utulsa.masters.mcagent.ControllerStatus;
import edu.utulsa.masters.mcagent.util.WorldTools;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

/**
 * Created by chad on 4/26/16.
 */
public class ActionMine extends PlayerControllerAction {
    BlockPos block;
    Vec3 lookVec;

    protected ActionMine(PlayerController pc, BlockPos block) {
        super(pc);
        this.block = block;
        this.lookVec = new Vec3(block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5);

        if(pc.getPlayer().getPositionVector().distanceTo(WorldTools.toVec3(block)) > 4.5) {
            status = ControllerStatus.FAILURE;
        }
        else {
            status = ControllerStatus.WAITING;
        }
    }

    int blockID = 0;

    int stage = 0;
    @Override
    protected void performAction() {
        // look at the block
        if(stage == 0) {
            pc.look(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);
            pc.doLook();
            if(Math.abs(pc.dYaw) < 0.1 && Math.abs(pc.dPitch) < 0.1) {
                blockID = WorldTools.getBlockID(pc.getWorld(), block);
                stage++;
            }
        }
        else if(stage == 1) {
            pc.attack();
            if(blockID != WorldTools.getBlockID(pc.getWorld(), block)) stage++;
        }
        else if(stage == 2) {
            pc.unattack();
            status = ControllerStatus.FINISHED;
        }
    }
}
