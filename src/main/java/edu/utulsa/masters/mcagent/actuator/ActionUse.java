package edu.utulsa.masters.mcagent.actuator;

import edu.utulsa.masters.mcagent.ControllerStatus;
import net.minecraft.util.Vec3;

/**
 * Use whatever item is in your hand currently.
 */
public class ActionUse extends PlayerControllerAction {
    Vec3 at;

    protected ActionUse(PlayerController pc, Vec3 at) {
        super(pc);
        this.at = at;
        if(pc.getPlayer().getPositionVector().distanceTo(at) > 4) {
            status = ControllerStatus.FAILURE;
        }
        else {
            status = ControllerStatus.WAITING;
        }
    }

    int stage = 0;
    @Override
    protected void performAction() {
        if(stage == 0) {
            pc.look(at.xCoord, at.yCoord, at.zCoord);
            pc.doLook();
            if(pc.dPitch < 0.1 && pc.dYaw < 0.1) {
                pc.use();
                stage++;
            }
        } else if (stage == 1) {
            if(!pc.keyUse.isPressed()) {
                stage++;
                status = ControllerStatus.FINISHED;
            }
        }
    }
}
