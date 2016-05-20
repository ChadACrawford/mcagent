package edu.utulsa.masters.mcagent.overrides;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by chad on 5/18/16.
 */
public class OverrideEntityPlayerSP extends EntityPlayerSP {

    public OverrideEntityPlayerSP(EntityPlayerSP player) {
        super(Minecraft.getMinecraft(), player.worldObj, player.sendQueue, player.getStatFileWriter());

        // use reflection to fuck things up
        // nothing references entityplayerSP right now, so this should work
        // no actually a lot of things reference it, this wont work
        try {
            for(Field field: player.getClass().getDeclaredFields()) {
                if(!Modifier.isFinal(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    field.set(this, field.get(player));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean override = false;
    public float moveForward = 0;
    public float moveStrafe = 0;

    @Override
    public void updateEntityActionState() {
        super.updateEntityActionState();

        if(override) {
            this.movementInput.moveForward = moveForward;
            this.movementInput.moveStrafe = moveStrafe;
        }
    }
}
