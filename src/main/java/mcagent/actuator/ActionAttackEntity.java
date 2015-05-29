package mcagent.actuator;

import net.minecraft.entity.Entity;

/**
 * Created by Chad on 5/27/2015.
 */
public class ActionAttackEntity extends PlayerControllerAction {
    protected Entity e;
    public ActionAttackEntity(Entity e) {
        this.e = e;
    }

    @Override
    public void performAction() {
        //cant do much now
    }
}
