package edu.utulsa.masters.mcagent.actuator;

import edu.utulsa.masters.mcagent.util.WorldTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import javax.vecmath.Vector2d;

/**
 * Created by Chad on 5/24/2015.
 */
public class PlayerController {
    private static PlayerController instance = null;
    private final EntityPlayerSP player;
    private final World world;

    static OverrideKeyBinding keyUp, keyDown, keyLeft, keyRight, keyJump, keyAttack, keyUse;

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
        KeyBinding kbu = mc.gameSettings.keyBindUseItem;
        KeyBinding kbi = mc.gameSettings.keyBindInventory;
        KeyBinding kbsneak = mc.gameSettings.keyBindSneak;
        KeyBinding kbsprint = mc.gameSettings.keyBindSprint;


        if(OVERRIDE_KEYS) {
            keyUp = new OverrideKeyBinding(kbf.getKeyDescription(), kbf.getKeyCode(), kbf.getKeyCategory());
            keyDown = new OverrideKeyBinding(kbb.getKeyDescription(), kbb.getKeyCode(), kbb.getKeyCategory());
            keyLeft = new OverrideKeyBinding(kbl.getKeyDescription(), kbl.getKeyCode(), kbl.getKeyCategory());
            keyRight = new OverrideKeyBinding(kbr.getKeyDescription(), kbr.getKeyCode(), kbr.getKeyCategory());
            keyJump = new OverrideKeyBinding(kbj.getKeyDescription(), kbj.getKeyCode(), kbj.getKeyCategory());
            keyAttack = new OverrideKeyBinding(kba.getKeyDescription(), kba.getKeyCode(), kba.getKeyCategory());
            keyUse = new OverrideKeyBinding(kbu.getKeyDescription(), kbu.getKeyCode(), kbu.getKeyCategory());

            mc.gameSettings.keyBindForward = keyUp;
            mc.gameSettings.keyBindBack = keyDown;
            mc.gameSettings.keyBindLeft = keyLeft;
            mc.gameSettings.keyBindRight = keyRight;
            mc.gameSettings.keyBindJump = keyJump;
            mc.gameSettings.keyBindAttack = keyAttack;
            mc.gameSettings.keyBindUseItem = keyUse;
        }
        else {
            keyUp = new OverrideKeyBinding(null, -1, null);
            keyDown = new OverrideKeyBinding(null, -1, null);
            keyLeft = new OverrideKeyBinding(null, -1, null);
            keyRight = new OverrideKeyBinding(null, -1, null);
            keyJump = new OverrideKeyBinding(null, -1, null);
            keyAttack = new OverrideKeyBinding(null, -1, null);
            keyUse = new OverrideKeyBinding(null, -1, null);
        }
    }

    public void left() {
        //System.out.println("Left");
        keyLeft.press();
    }

    public void unleft() {
        keyLeft.unpress();
    }

    public void right() {
        //System.out.println("Right");
        keyRight.press();
    }
    public void unright() {
        keyRight.unpress();
    }

    public void forward() {
        //System.out.println("Forward");
        keyUp.press();
    }
    public void unforward(){
        keyUp.unpress();
    }

    public void back() {
        //System.out.println("Back");
        keyDown.press();
    }
    public void unback() {
        keyDown.unpress();
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
        World w = getWorld();
        Vec3 o = player.getLookVec();

        Vector2d toVec = new Vector2d(x - player.posX, z - player.posZ);
        Vector2d forwardVec = new Vector2d(o.xCoord, o.zCoord);
        Vector2d leftVec = new Vector2d(o.zCoord, o.xCoord);
        Vector2d bothVec = new Vector2d(forwardVec.getX() + leftVec.getX(), forwardVec.getY() + leftVec.getY());
        toVec.normalize();
        forwardVec.normalize();
        leftVec.normalize();
        bothVec.normalize();

        double projF = forwardVec.dot(toVec),
                projL = leftVec.dot(toVec),
                projB = bothVec.dot(toVec);

        unpressAll();
        if(Math.abs(projB) > Math.abs(projF) && Math.abs(projB) > Math.abs(projL)) {
            if(projF > 0) forward();
            else back();

            if(projL > 0) left();
            else right();
        }
        else if(Math.abs(projF) > Math.abs(projL)) {
            if(projF > 0) forward();
            else back();
        }
        else {
            if(projL > 0) left();
            else right();
        }

        if(player.isInWater() && player.getPosition().getY() <= y) {
            jump();
        }
        else if(WorldTools.isBlocked(w, player.getPosition(), new BlockPos(x, y, z), 1)) {
            jump();
        }
        else {
            unjump();
        }

        return player.getPositionVector().distanceTo(new Vec3(x, y, z));
    }

    public static final double ACCEL = 5.0;
    public static final double MAX_SPEED = 5.0;
    private double lookX, lookY, lookZ;
    private boolean isLooking = false;
    public double dYaw, dPitch;
    public void look(double x, double y, double z) {
        isLooking = true;
        lookX = x;
        lookY = y;
        lookZ = z;
    }

    public void unlook() {
        isLooking = false;
    }

    public void doLook() {
        if(!isLooking) return;
        EntityPlayerSP p = getPlayer();
        Vec3 ppos = player.getPositionVector().addVector(0, 1, 0);
        double dist = Math.sqrt(Math.pow(lookZ - ppos.xCoord, 2) + Math.pow(lookX - ppos.zCoord, 2));
        double fYaw = Math.atan2(lookX - ppos.xCoord, ppos.zCoord - lookZ) / (Math.PI) * 180 + 180;
        double fPitch = 90 - Math.atan2(lookY - ppos.yCoord, dist) / (Math.PI) * 180;
//        p.setSneaking(true);

        double r = angleDistance(getYaw(), fYaw);
        double dY = MAX_SPEED * (ACCEL * r) / (4 * (r + 90));
        if(dY > MAX_SPEED) dY = MAX_SPEED;

        r = angleDistance(getPitch(), fPitch);
        double dP = MAX_SPEED * (ACCEL * r) / (4 * (r + 90));
        if(dP > MAX_SPEED) dP = MAX_SPEED;

        //System.out.format("fYaw: %8.6f cYaw: %8.6f dY: %8.6f fPitch: %8.6f cPitch: %8.6f dP: %8.6f\n",fYaw,relativeYaw(),dY,fPitch,relativePitch(),dY);

        double dyaw = getYaw() - fYaw;
        this.dYaw = dyaw;
        if(-180 < dyaw && dyaw < 0 || 180 < dyaw && dyaw < 360) p.rotationYaw += dY;
        else p.rotationYaw -= dY;

        double dpitch = getPitch() - fPitch;
        this.dPitch = dpitch;
        if(dpitch < 0) p.rotationPitch += dP;
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
