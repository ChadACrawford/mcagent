package edu.utulsa.masters.mcagent.actuator;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.InventoryPlayer;

/**
 * Created by chad on 4/26/16.
 */
public class PlayerInventory {
    public PlayerInventory(PlayerController pc, EntityPlayerSP player) {
        InventoryPlayer inventory = player.inventory;
    }
}
