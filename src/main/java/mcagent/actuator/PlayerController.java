package mcagent.actuator;

import mcagent.*;
import mcagent.util.WorldTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import scala.util.control.TailCalls;

import java.util.LinkedList;

/**
 * Created by Chad on 5/24/2015.
 */
public class PlayerController {
    private static PlayerController instance = null;
    private final EntityPlayerSP player;
    private final World world;

    static OverrideKeyBinding keyUp, keyDown, keyLeft, keyRight, keyJump, keyAttack;

    public PlayerController(World world, EntityPlayerSP player) {
        this.world = world;
        this.player = player;
    }

    private static final boolean OVERRIDE_KEYS = true;
    public static void setKeyBindings() {
        Minecraft mc = Minecraft.getMinecraft();
        KeyBinding kbf = mc.gameSettings.keyBindForward;
        KeyBinding kbb = mc.gameSettings.keyBindBack;
        KeyBinding kbl = mc.gameSettings.keyBindLeft;
        KeyBinding kbr = mc.gameSettings.keyBindRight;
        KeyBinding kbj = mc.gameSettings.keyBindJump;
        KeyBinding kba = mc.gameSettings.keyBindAttack;

        if(OVERRIDE_KEYS) {
            keyUp = new OverrideKeyBinding(kbf.getKeyDescription(), kbf.getKeyCode(), kbf.getKeyCategory());
            keyDown = new OverrideKeyBinding(kbb.getKeyDescription(), kbb.getKeyCode(), kbb.getKeyCategory());
            keyLeft = new OverrideKeyBinding(kbl.getKeyDescription(), kbl.getKeyCode(), kbl.getKeyCategory());
            keyRight = new OverrideKeyBinding(kbr.getKeyDescription(), kbr.getKeyCode(), kbr.getKeyCategory());
            keyJump = new OverrideKeyBinding(kbj.getKeyDescription(), kbj.getKeyCode(), kbj.getKeyCategory());
            keyAttack = new OverrideKeyBinding(kba.getKeyDescription(), kba.getKeyCode(), kba.getKeyCategory());

            mc.gameSettings.keyBindForward = keyUp;
            mc.gameSettings.keyBindBack = keyDown;
            mc.gameSettings.keyBindLeft = keyLeft;
            mc.gameSettings.keyBindRight = keyRight;
            mc.gameSettings.keyBindJump = keyJump;
            mc.gameSettings.keyBindAttack = keyAttack;
        }
        else {
            keyUp = new OverrideKeyBinding(null, -1, null);
            keyDown = new OverrideKeyBinding(null, -1, null);
            keyLeft = new OverrideKeyBinding(null, -1, null);
            keyRight = new OverrideKeyBinding(null, -1, null);
            keyJump = new OverrideKeyBinding(null, -1, null);
            keyAttack = new OverrideKeyBinding(null, -1, null);
        }
    }

    public void left() {
        //System.out.println("Left");
        keyLeft.press();
    }

    public void right() {
        //System.out.println("Right");
        keyRight.press();
    }

    public void forward() {
        //System.out.println("Forward");
        keyUp.press();
    }

    public void back() {
        //System.out.println("Back");
        keyDown.press();
    }

    public void stopMoving() {
        keyUp.unpress();
        keyDown.unpress();
        keyLeft.unpress();
        keyRight.unpress();
    }

    public void jump() {
        keyJump.press();
    }

    public void unjump() {
        keyJump.unpress();
    }

    public void attack() {
        keyAttack.press();
    }

    public void unattack() {
        keyAttack.unpress();
    }

    public void unpressAll() {
        keyUp.unpress();
        keyDown.unpress();
        keyLeft.unpress();
        keyRight.unpress();
        keyJump.unpress();
        keyAttack.unpress();
    }

    public double moveTo(double x, double y, double z) {
        EntityPlayerSP p = getPlayer();
        World w = getWorld();
        Vec3 o = p.getLookVec();
        double dx = x-p.posX, dz = z-p.posZ;

        double angle = Math.atan2(dx, -dz)/(2*Math.PI)*360+180 - getYaw();
        if(Math.abs(angle) > 180) {
            angle /= -2;
        }
        angle += 180;

        //System.out.format("Angle: %9.6f\n", angle);
        if(angle > 100 && angle < 260) forward();
        else if(angle > 5 && angle < 85 || angle > 275 && angle < 355) back();

        if(angle > 10 && angle < 160) left();
        else if(angle > 180 && angle < 350) right();
        else {
//            //do obstacle checks
//            if (Minecraft.getSystemTime() % 1000 < 500) {
//                left();
//            } else {
//                right();
//            }
        }

        if(p.isInWater() && p.getPosition().getY() <= y) {
            jump();
        }
        if(WorldTools.isBlocked(w, p.getPosition(), new BlockPos(x, y, z), 1)) {
            jump();
        }

        return p.getPosition().distanceSq(x, y, z);
    }

    public static final double ACCEL = 5.0;
    public static final double MAX_SPEED = 5.0;
    private double lookX, lookY, lookZ;
    public void look(double x, double y, double z) {
        lookX = x;
        lookY = y;
        lookZ = z;
    }

    protected void doLook() {
        EntityPlayerSP p = player;
        double dist = Math.sqrt(Math.pow(lookZ - p.posZ, 2) + Math.pow(lookX - p.posX, 2));
        double fYaw = Math.atan2(lookX - p.posX, p.posZ - lookZ) / (Math.PI) * 180 + 180;
        double fPitch = 90 - Math.atan2(lookY - p.posY, dist) / (Math.PI) * 180;
//        p.setSneaking(true);

        double r = angleDistance(getYaw(), fYaw);
        double dY = MAX_SPEED * (ACCEL * r) / (4 * (r + 90));
        if(dY > MAX_SPEED) dY = MAX_SPEED;

        r = angleDistance(getPitch(), fPitch);
        double dP = MAX_SPEED * (ACCEL * r) / (4 * (r + 90));
        if(dP > MAX_SPEED) dP = MAX_SPEED;

        //System.out.format("fYaw: %8.6f cYaw: %8.6f dY: %8.6f fPitch: %8.6f cPitch: %8.6f dP: %8.6f\n",fYaw,relativeYaw(),dY,fPitch,relativePitch(),dY);

        double d = getYaw() - fYaw;
        if(-180 < d && d < 0 || 180 < d && d < 360) p.rotationYaw += dY;
        else p.rotationYaw -= dY;

        d = getPitch() - fPitch;
        if(d < 0) p.rotationPitch += dP;
        else p.rotationPitch -= dP;
    }

    private static double angleDistance(double a1, double a2) {
        double d1 = Math.abs(a1 - a2);
        double d2 = 360f-d1;
        return Math.min(d1,d2);
    }

    public double getYaw() {
        if(player.rotationYaw >= 0)
            return player.rotationYaw % 360;
        else
            return 360-(Math.abs(player.rotationYaw) % 360);
    }

    public double getPitch() {
        return player.rotationPitch + 90;
    }

    public EntityPlayerSP getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }
}
