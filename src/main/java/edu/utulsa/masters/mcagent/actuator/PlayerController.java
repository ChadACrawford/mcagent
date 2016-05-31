package edu.utulsa.masters.mcagent.actuator;

import edu.utulsa.masters.mcagent.actuator.inventory.PlayerInventory;
import edu.utulsa.masters.mcagent.overrides.OverrideKeyBinding;
import edu.utulsa.masters.mcagent.util.WorldTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import javax.vecmath.Vector2d;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chad on 5/24/2015.
 */
public class PlayerController {
    private static PlayerController instance = null;
    private final EntityPlayerSP player;
    private final World world;
    public final PlayerInventory inventory;
    public static boolean doOverrideKeys = true;

    public PlayerController(World world, EntityPlayerSP player) {
        this.world = world;
        this.player = player;
        this.inventory = new PlayerInventory(this);
    }

    private static OverrideKeyBinding overrideKeyBinding(KeyBinding kb) {
        if(OVERRIDE_KEYS) {
            return new OverrideKeyBinding(kb.getKeyDescription(), kb.getKeyCode(), kb.getKeyCategory());
        }
        else {
            return new OverrideKeyBinding(null, -1, null);
        }
    }

    private static final boolean OVERRIDE_KEYS = true;
    public static OverrideKeyBinding keyUp, keyDown, keyLeft, keyRight, keyJump, keyAttack, keyUse, keyInventory,
            keySneak, keySprint, keyDrop, keyPickBlock, keyScreenshot, keyPlayerList, keySpectatorOutlines;
    public static List<OverrideKeyBinding> keys = new LinkedList<OverrideKeyBinding>();
    public static void setKeyBindings() {
        Minecraft mc = Minecraft.getMinecraft();
        keyUp = overrideKeyBinding(mc.gameSettings.keyBindForward);
        keyDown = overrideKeyBinding(mc.gameSettings.keyBindBack);
        keyLeft = overrideKeyBinding(mc.gameSettings.keyBindLeft);
        keyRight = overrideKeyBinding(mc.gameSettings.keyBindRight);
        keyJump = overrideKeyBinding(mc.gameSettings.keyBindJump);
        keyAttack = overrideKeyBinding(mc.gameSettings.keyBindAttack);
        keyUse = overrideKeyBinding(mc.gameSettings.keyBindUseItem);
        keyInventory = overrideKeyBinding(mc.gameSettings.keyBindInventory);
        keySneak = overrideKeyBinding(mc.gameSettings.keyBindSneak);
        keySprint = overrideKeyBinding(mc.gameSettings.keyBindSprint);
        keyDrop = overrideKeyBinding(mc.gameSettings.keyBindDrop);
        keyPickBlock = overrideKeyBinding(mc.gameSettings.keyBindPickBlock);
        keyScreenshot = overrideKeyBinding(mc.gameSettings.keyBindScreenshot);
        keyPlayerList = overrideKeyBinding(mc.gameSettings.keyBindPlayerList);
        keySpectatorOutlines = overrideKeyBinding(mc.gameSettings.keyBindSpectatorOutlines);
        keys.add(keyUp); keys.add(keyDown); keys.add(keyLeft); keys.add(keyRight);
        keys.add(keyJump); keys.add(keyAttack); keys.add(keyUse); keys.add(keyInventory);
        keys.add(keySneak); keys.add(keySprint); keys.add(keyDrop); keys.add(keyPickBlock);
        keys.add(keyScreenshot); keys.add(keyPlayerList); keys.add(keySpectatorOutlines);

        if(OVERRIDE_KEYS) {
            mc.gameSettings.keyBindForward = keyUp;
            mc.gameSettings.keyBindBack = keyDown;
            mc.gameSettings.keyBindLeft = keyLeft;
            mc.gameSettings.keyBindRight = keyRight;
            mc.gameSettings.keyBindJump = keyJump;
            mc.gameSettings.keyBindAttack = keyAttack;
            mc.gameSettings.keyBindUseItem = keyUse;
            mc.gameSettings.keyBindInventory = keyInventory;
            mc.gameSettings.keyBindSneak = keySneak;
            mc.gameSettings.keyBindDrop = keyDrop;
            mc.gameSettings.keyBindPickBlock = keyPickBlock;
            mc.gameSettings.keyBindScreenshot = keyScreenshot;
            mc.gameSettings.keyBindPlayerList = keyPlayerList;
            mc.gameSettings.keyBindSpectatorOutlines = keySpectatorOutlines;
        }
    }

    public long lastUpdatedPlayerTick;
    public void prePlayerTick() {
        lastUpdatedPlayerTick = Minecraft.getSystemTime();
        for(OverrideKeyBinding key: keys) {
            key.tick();
        }
        updateVelocity();
    }

    Vec3 lastPosition;
    double currentVelocity = 0;
    public void updateVelocity() {
        if(lastPosition == null) {
            lastPosition = player.getPositionVector();
            return;
        }

        currentVelocity = lastPosition.distanceTo(player.getPositionVector());
        lastPosition = player.getPositionVector();
    }

    public double getCurrentVelocity() {
        return currentVelocity;
    }

    public void renderTick() {
        doLook();
    }

    public void use() {
        keyUse.pressFor(300);
    }

    public void openInventory() {
        keyInventory.pressFor(5);
    }

    public void sneak() {
        keySneak.press();
        keySprint.unpress();
    }
    public void sprint() {
        keySprint.press();
        keySneak.unpress();
    }
    public void walk() {
        keySprint.unpress();
        keySneak.unpress();
    }

    public void left() {
        //System.out.println("Left");
        keyLeft.press();
        //moveStrafe = amount;
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
        keyJump.pressFor(50);
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
        //player.moveForward = 0;
        //player.moveStrafe = 0;
        keyUp.unpress();
        keyDown.unpress();
        keyLeft.unpress();
        keyRight.unpress();
        keyJump.unpress();
        keyAttack.unpress();
    }

    public double moveTo(final double x, final double y, final double z) {
        World w = getWorld();
        Vec3 o = player.getLookVec();

//        player.movementInput.

        class MoveVector {
            double x, y, z;
        }

        Vector2d toVec = new Vector2d(x - player.posX, z - player.posZ);
        Vector2d forwardVec = new Vector2d(o.xCoord, o.zCoord);
        Vector2d leftVec = new Vector2d(o.zCoord, -o.xCoord);
        Vector2d bothVec = new Vector2d(forwardVec.getX() + leftVec.getX(), forwardVec.getY() + leftVec.getY());
        toVec.normalize();
        forwardVec.normalize();
        leftVec.normalize();
        bothVec.normalize();

        double dist = player.getPositionVector().distanceTo(new Vec3(x, y, z));
        float offset = 1;
        if(getCurrentVelocity() > dist) {
            offset = (float)dist;
        }
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

        if(player.isInWater() && player.getPosition().getY() < y) {
            jump();
        }
        else if(player.getPositionVector().yCoord < y &&
                !WorldTools.isValidPath(w, player.getPositionVector(), new Vec3(x, y, z))) {
            jump();
        }
        else {
            unjump();
        }

        return dist;
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

    public double dist(double x, double y, double z) {
        return player.getPositionVector().distanceTo(new Vec3(x, y, z));
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
