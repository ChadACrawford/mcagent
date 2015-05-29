package mcagent.actuator;

import mcagent.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

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

    OverrideKeyBinding keyUp;
    OverrideKeyBinding keyDown;
    OverrideKeyBinding keyLeft;
    OverrideKeyBinding keyRight;
    OverrideKeyBinding keyJump;
    OverrideKeyBinding keyAttack;

    private PlayerController() {
        player = Minecraft.getMinecraft().thePlayer;
        world = Minecraft.getMinecraft().theWorld;
    }

    public static PlayerController getInstance() {
        if(instance == null) {
            instance = new PlayerController();
        }
        return instance;
    }

    private void setKeyBindings() {
        Minecraft mc = Minecraft.getMinecraft();
        KeyBinding kbf = mc.gameSettings.keyBindForward;
        KeyBinding kbb = mc.gameSettings.keyBindBack;
        KeyBinding kbl = mc.gameSettings.keyBindLeft;
        KeyBinding kbr = mc.gameSettings.keyBindRight;
        KeyBinding kbj = mc.gameSettings.keyBindJump;
        KeyBinding kba = mc.gameSettings.keyBindAttack;

        keyUp = new OverrideKeyBinding(kbf.getKeyDescription(), kbf.getKeyCode(), kbf.getKeyCategory());
        keyDown = new OverrideKeyBinding(kbf.getKeyDescription(), kbf.getKeyCode(), kbf.getKeyCategory());
        keyLeft = new OverrideKeyBinding(kbf.getKeyDescription(), kbf.getKeyCode(), kbf.getKeyCategory());
        keyRight = new OverrideKeyBinding(kbf.getKeyDescription(), kbf.getKeyCode(), kbf.getKeyCategory());
        keyJump = new OverrideKeyBinding(kbj.getKeyDescription(), kbj.getKeyCode(), kbj.getKeyCategory());
        keyAttack = new OverrideKeyBinding(kba.getKeyDescription(), kba.getKeyCode(), kba.getKeyCategory());

        mc.gameSettings.keyBindForward = keyUp;
        mc.gameSettings.keyBindBack = keyDown;
        mc.gameSettings.keyBindLeft = keyLeft;
        mc.gameSettings.keyBindRight = keyRight;
        mc.gameSettings.keyBindJump = keyJump;
        mc.gameSettings.keyBindAttack = keyAttack;
    }

    public void act() {
        unpressAll();

        for(PlayerControllerAction action: currentActions) {
            action.act();
            if(action.getStatus() == ControllerStatus.FINISHED || action.getStatus() == ControllerStatus.FAILURE) currentActions.remove(action);
        }
    }

    public void moveTo(double x, double y, double z) {
        PlayerControllerAction action = new ActionMove(x, y, z);
        currentActions.add(action);
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
        keyLeft.press();
    }
    public void right() {
        keyRight.press();
    }
    public void forward() {
        keyUp.press();
    }
    public void back() {
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
