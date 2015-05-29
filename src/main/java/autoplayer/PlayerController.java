package autoplayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import tools.WorldTools;

/**
 * Created by Chad on 3/28/2015.
 */
public class PlayerController {
    private final double JUMP_DELAY = 1000;

    public final EntityPlayerSP p;
    private final Minecraft mc;
    public PlayerController(Minecraft mc, EntityPlayerSP p) {
        this.mc = mc;
        this.p = p;
        overrideKeyBindings();
    }

    OverrideKeyBinding kForward;
    OverrideKeyBinding kBack;
    OverrideKeyBinding kLeft;
    OverrideKeyBinding kRight;
    OverrideKeyBinding kJump;

    private void overrideKeyBindings() {
        KeyBinding kbf = mc.gameSettings.keyBindForward;
        KeyBinding kbb = mc.gameSettings.keyBindBack;
        KeyBinding kbl = mc.gameSettings.keyBindLeft;
        KeyBinding kbr = mc.gameSettings.keyBindRight;
        KeyBinding kbj = mc.gameSettings.keyBindJump;

        kForward = new OverrideKeyBinding(kbf.getKeyDescription(), kbf.getKeyCode(), kbf.getKeyCategory());
        kBack = new OverrideKeyBinding(kbf.getKeyDescription(), kbf.getKeyCode(), kbf.getKeyCategory());
        kLeft = new OverrideKeyBinding(kbf.getKeyDescription(), kbf.getKeyCode(), kbf.getKeyCategory());
        kRight = new OverrideKeyBinding(kbf.getKeyDescription(), kbf.getKeyCode(), kbf.getKeyCategory());
        kJump = new OverrideKeyBinding(kbj.getKeyDescription(), kbj.getKeyCode(), kbj.getKeyCategory());

//        mc.gameSettings.keyBindForward = kForward;
//        mc.gameSettings.keyBindBack = kBack;
//        mc.gameSettings.keyBindLeft = kLeft;
//        mc.gameSettings.keyBindRight = kRight;
//        mc.gameSettings.keyBindJump = kJump;
    }

    double stopDelay = 0;
    public void setDelay(double t) {
        stopDelay = Minecraft.getSystemTime()+t;
    }
    public boolean delay() {
        return Minecraft.getSystemTime()<stopDelay;
    }

    public void test() {
        p.rotationYaw += 1f;
        System.out.println(p.rotationYaw);
    }

    private double relativeYaw() {
        if(p.rotationYaw >= 0)
            return p.rotationYaw % 360;
        else
            return 360-(Math.abs(p.rotationYaw) % 360);
    }
    private double relativePitch() {
        return p.rotationPitch + 90;
    }
    private double angleDistance(double a1, double a2) {
        double d1 = Math.abs(a1 - a2);
        double d2 = 360f-d1;
        return Math.min(d1,d2);
    }

    private final double MAX_D_YAW = 5;
    private final double MAX_D_PITCH = 5;
    public void lookAt(BlockPos at) {
        lookAt(at.getX(),at.getY(),at.getZ());
    }
    public void lookAt(double x, double y, double z) {
        double dist = Math.sqrt(Math.pow(z-p.posZ,2)+Math.pow(x-p.posX,2));
        double fYaw = Math.atan2(x-p.posX, p.posZ-z)/(Math.PI)*180+180;
        double fPitch = 90-Math.atan2(y - p.posY, dist)/(Math.PI)*180;
        p.setSneaking(true);


        double r = angleDistance(relativeYaw(), fYaw);
        double dY = MAX_D_YAW * (5*r)/(4*(r+90));
        if(dY > MAX_D_YAW) dY = MAX_D_YAW;

        r = angleDistance(relativePitch(),fPitch);
        double dP = MAX_D_PITCH*(5*r)/(4*(r+90));
        if(dP > MAX_D_PITCH) dP = MAX_D_PITCH;

        //System.out.format("fYaw: %8.6f cYaw: %8.6f dY: %8.6f fPitch: %8.6f cPitch: %8.6f dP: %8.6f\n",fYaw,relativeYaw(),dY,fPitch,relativePitch(),dY);

        double d = relativeYaw()-fYaw;
        if(-180 < d && d < 0 || 180 < d && d < 360) p.rotationYaw += dY;
        else p.rotationYaw -= dY;

        d = relativePitch()-fPitch;
        if(d < 0) p.rotationPitch += dP;
        else p.rotationPitch -= dP;
    }

    public void moveTo(BlockPos to) {
        moveTo(to.getX(),to.getY(),to.getZ());
    }
    public void directMove(BlockPos to) {
        directMove(to.getX(),to.getY(),to.getZ());
    }
    public void directMove(double x, double y, double z) {
        moveTo(x,y,z);
        if(p.isInWater() || WorldTools.isBlocked(mc.theWorld, p.getPosition(), new BlockPos(x,y,z), 1)) jump();
    }

    public void moveTo(double x, double y, double z) {
        Vec3 o = p.getLookVec();
        double dx = x-p.posX, dz = z-p.posZ;

        double angle = Math.atan2(dx, -dz)/(2*Math.PI)*360+180 - relativeYaw();
        if(Math.abs(angle) > 180) {
            angle /= -2;
        }
        angle += 180;

        //System.out.format("Angle: %9.6f\n", angle);
        if(angle > 100 && angle < 260) forward();
        else if(angle > 5 && angle < 85 || angle > 275 && angle < 355) back();
        else stopWalk();
//
        if(angle > 10 && angle < 160) left();
        else if(angle > 180 && angle < 350) right();
        else stopStrafe();
    }

    double lastJumpTime = 0;
    public PlayerController jump() {
        if((p.onGround || p.isInWater()) && mc.getSystemTime()-lastJumpTime>250 ) {
            p.jump();
            lastJumpTime = mc.getSystemTime();
        }
        return this;
    }
    public PlayerController forward() {
        kForward.pressed = true;
        kBack.pressed = false;
        return this;
    }
    public PlayerController back() {
        kForward.pressed = false;
        kBack.pressed = true;
        return this;
    }
    public PlayerController stopWalk() {
        kForward.pressed = false;
        kBack.pressed = false;
        return this;
    }
    public PlayerController left() {
        kLeft.pressed = true;
        kRight.pressed = false;
        return this;
    }
    public PlayerController right() {
        kLeft.pressed = false;
        kRight.pressed = true;
        return this;
    }
    public PlayerController stopStrafe() {
        kLeft.pressed = false;
        kRight.pressed = false;
        return this;
    }

    public double getX() {
        return p.posX;
    }
    public double getY() {
        return p.posY;
    }
    public double getZ() {
        return p.posZ;
    }
}
