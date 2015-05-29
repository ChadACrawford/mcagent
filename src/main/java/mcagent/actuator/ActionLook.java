package mcagent.actuator;

import net.minecraft.client.entity.EntityPlayerSP;

/**
 * Created by Chad on 5/25/2015.
 */
public class ActionLook extends PlayerControllerAction {
    public static final double ACCEL = 5.0;
    public static final double MAX_SPEED = 5.0;

    private double lookX, lookY, lookZ;

    public ActionLook(double lookX, double lookY, double lookZ) {
        this.lookX = lookX;
        this.lookY = lookY;
        this.lookZ = lookZ;
    }

    @Override
    public void performAction() {
        PlayerController pc = PlayerController.getInstance();
        EntityPlayerSP p = pc.getPlayer();
        double dist = Math.sqrt(Math.pow(lookZ-p.posZ,2)+Math.pow(lookX-p.posX,2));
        double fYaw = Math.atan2(lookX-p.posX, p.posZ-lookZ)/(Math.PI)*180+180;
        double fPitch = 90-Math.atan2(lookY - p.posY, dist)/(Math.PI)*180;
        p.setSneaking(true);


        double r = angleDistance(pc.getYaw(), fYaw);
        double dY = MAX_SPEED * (ACCEL*r)/(4*(r+90));
        if(dY > MAX_SPEED) dY = MAX_SPEED;

        r = angleDistance(pc.getPitch(),fPitch);
        double dP = MAX_SPEED*(ACCEL*r)/(4*(r+90));
        if(dP > MAX_SPEED) dP = MAX_SPEED;

        //System.out.format("fYaw: %8.6f cYaw: %8.6f dY: %8.6f fPitch: %8.6f cPitch: %8.6f dP: %8.6f\n",fYaw,relativeYaw(),dY,fPitch,relativePitch(),dY);

        double d = pc.getYaw()-fYaw;
        if(-180 < d && d < 0 || 180 < d && d < 360) p.rotationYaw += dY;
        else p.rotationYaw -= dY;

        d = pc.getPitch()-fPitch;
        if(d < 0) p.rotationPitch += dP;
        else p.rotationPitch -= dP;
    }

    private static double angleDistance(double a1, double a2) {
        double d1 = Math.abs(a1 - a2);
        double d2 = 360f-d1;
        return Math.min(d1,d2);
    }
}
