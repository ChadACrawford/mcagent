package mcagent.actuator.movement;

import mcagent.Controller;
import mcagent.ControllerStatus;
import mcagent.actuator.ActionMove;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

/**
 * Created by Chad on 5/25/2015.
 */
public abstract class Move implements Controller {
    protected double toX, toY, toZ;
    protected ActionMove action;
    protected ControllerStatus status = ControllerStatus.WAITING;

    public Move(double toX, double toY, double toZ) {
        this.toX = toX;
        this.toY = toY;
        this.toZ = toZ;
    }

    int stage = 0;
    @Override
    public void act() {
        this.status = ControllerStatus.BUSY;
        try {
            move();
            if (this.status != ControllerStatus.BUSY) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = ControllerStatus.FAILURE;
            return;
        }
        status = ControllerStatus.WAITING;
    }
    @Override
    public ControllerStatus getStatus() {
        return status;
    }

    public abstract void move();
    public abstract boolean calculate();

    public abstract Vec3 getCurrentGoal();

    public static BlockPos getPosition() {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        return new BlockPos(Math.floor(p.posX), Math.floor(p.posY), Math.floor(p.posZ));
    }
}
