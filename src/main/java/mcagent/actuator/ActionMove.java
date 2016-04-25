package mcagent.actuator;

/**
 * Created by Chad on 5/24/2015.
 */
public class ActionMove extends PlayerControllerAction {

    private double moveX,moveY,moveZ;

    public ActionMove(PlayerController pc, double moveX, double moveY, double moveZ) {
        super(pc);
        this.moveX = moveX;
        this.moveY = moveY;
        this.moveZ = moveZ;
    }

    @Override
    public void performAction() {
    }
}
