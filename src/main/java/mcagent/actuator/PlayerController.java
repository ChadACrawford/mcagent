package mcagent.actuator;

import mcagent.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import scala.util.control.TailCalls;

import java.util.LinkedList;

/**
 * Created by Chad on 5/24/2015.
 */
public class PlayerController implements Controller {
    private static PlayerController instance = null;
    private final EntityPlayerSP player;
    private final World world;
    private ControllerStatus status = ControllerStatus.WAITING;
    private LinkedList<PlayerControllerAction> currentActions = new LinkedList<PlayerControllerAction>();

    static OverrideKeyBinding keyUp,keyDown,keyLeft,keyRight,keyJump,keyAttack;

    private PlayerController() {
        player = Minecraft.getMinecraft().thePlayer;
        world = Minecraft.getMinecraft().theWorld;
        //setKeyBindings();
    }

    public static PlayerController getInstance() {
        if(instance == null) {
            instance = new PlayerController();
        }
        return instance;
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

    public void act() {
        unpressAll();

        for(PlayerControllerAction action: currentActions) {
            if(action.getStatus() == ControllerStatus.WAITING) action.act();
            if(action.getStatus() == ControllerStatus.FINISHED || action.getStatus() == ControllerStatus.FAILURE) {
                System.out.println("Finished action " + action);
                currentActions.remove(action);
            }
        }
    }

    public void moveTo(double x, double y, double z, boolean look) {
        PlayerControllerAction action;
        if(look) {
            action = new ActionMoveLook(x,y,z);
        } else {
            action = new ActionMove(x, y, z);
        }
        currentActions.add(action);
    }
    public void moveTo(double x, double y, double z) {
        moveTo(x,y,z,false);
    }

    public void lookAt(double x, double y, double z) {
        PlayerControllerAction action = new ActionLook(x, y, z);
        currentActions.add(action);
    }

    public void attack(Entity e) {
        PlayerControllerAction action = new ActionAttackEntity(e);
    }

    public void attack(double x, double y, double z) {

    }

    public void mine(int x, int y, int z) {

    }

    public void mine(BlockPos b) {

    }

    public void harvest(BlockPos b) {

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
    public void attack() {
        keyAttack.press();
    }
    public void unpressAll() {
        keyUp.unpress();
        keyDown.unpress();
        keyLeft.unpress();
        keyRight.unpress();
        keyJump.unpress();
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

    public ControllerStatus getStatus() {
        return status;
    }
    public EntityPlayerSP getPlayer() {
        return player;
    }
    public World getWorld() {
        return world;
    }
}
