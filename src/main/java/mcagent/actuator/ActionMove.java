package mcagent.actuator;

import mcagent.ControllerStatus;
import mcagent.actuator.movement.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Vec3;
import mcagent.util.WorldTools;

/**
 * Created by Chad on 5/24/2015.
 */
public class ActionMove extends PlayerControllerAction {

    private double moveX,moveY,moveZ;
    private Move move;

    public ActionMove(double moveX, double moveY, double moveZ) {
        super();
        this.moveX = moveX;
        this.moveY = moveY;
        this.moveZ = moveZ;

        EntityPlayerSP player = PlayerController.getInstance().getPlayer();
        double dist = WorldTools.distance(player.getPositionVector(), new Vec3(moveX, moveY, moveZ));
        if(dist <= WorldGrid.MAX_DISTANCE)
            this.move = new MoveShort(moveX, moveY, moveZ);
        else if(WorldGrid.getInstance().inRange((int)moveX, (int)moveZ))
            this.move = new MoveLong(moveX, moveY, moveZ);
        else
            this.move = new MoveVeryLong(moveX, moveY, moveZ);

        if(!move.calculate()) {
            this.status = ControllerStatus.FAILURE;
        }
    }

    @Override
    public void performAction() {
        move.act();
        status = move.getStatus();
    }

    public Vec3 getCurrentGoal() {
        return move.getCurrentGoal();
    }
}
