package mcagent.actuator;

import mcagent.ControllerStatus;
import mcagent.actuator.movement.Move;
import mcagent.actuator.movement.MoveLong;
import mcagent.actuator.movement.MoveShort;
import mcagent.actuator.movement.MoveVeryLong;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Vec3;
import tools.WorldTools;

/**
 * Created by Chad on 5/24/2015.
 */
public class ActionMove extends PlayerControllerAction {

    private double moveX,moveY,moveZ;
    private Move move;

    public ActionMove(double moveX, double moveY, double moveZ) {
        this.moveX = moveX;
        this.moveY = moveY;
        this.moveZ = moveZ;

        EntityPlayerSP player = PlayerController.getInstance().getPlayer();
        double dist = WorldTools.distance(player.getPositionVector(), new Vec3(moveX, moveY, moveZ));
        if(dist <= 10)
            this.move = new MoveShort(moveX, moveY, moveZ);
        else if(dist <= 100)
            this.move = new MoveLong(moveX, moveY, moveZ);
        else
            this.move = new MoveVeryLong(moveX, moveY, moveZ);

        if(move.calculate())
            this.status = ControllerStatus.BUSY;
        else
            this.status = ControllerStatus.FAILURE;
    }

    @Override
    public void performAction() {
        move.act();
    }

    public Vec3 getCurrentGoal() {
        return move.getCurrentGoal();
    }
}
