package edu.utulsa.masters.mcagent;

import edu.utulsa.masters.mcagent.actuator.ActionMove;
import edu.utulsa.masters.mcagent.actuator.PlayerController;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Testing movement module.
 */
@SideOnly(Side.CLIENT)
public class TestMovementAgent extends MCAgent {
    BlockPos start, end;

    boolean continueAction = true;

    protected TestMovementAgent(EntityPlayerSP player) {
        super(player);
    }

    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void exec() {
        while(stayAlive) {
            debug.message("info", "Waiting for player to choose block coords...");

            overrideWindow = false;
            PlayerController.doOverrideKeys = false;
            try {
                latch.await();

                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            debug.reset();

            overrideWindow = true;
            PlayerController.doOverrideKeys = true;

            debug.info(end == null ? "what" : "it works!");
            ActionMove a = new ActionMove(pc, end.getX(), end.getY(), end.getZ());
//            Path path = Path.compute(world, start, end);
//            ActionMine a = new ActionMine(pc, start);
//            Path path = Path.compute(world, start, end, true);

            if(a.getStatus() == ControllerStatus.FAILURE) {
                debug.info("Crap.");
            }
            else {
                for (BlockPos p : a.getPath()) {
                    debug.debugBlock(p, Block.getStateById(41));
                }

                debug.info("Finished computing path. Now to follow it.");

                while (continueAction) {
                    ControllerStatus status = a.getStatus();
                    if (status == ControllerStatus.FINISHED) {
                        debug.info("Finished!");
                        break;
                    } else if (status == ControllerStatus.FAILURE) {
                        debug.info("Failed!");
                        break;
                    } else {
                        a.act();
                    }
                }
            }
            continueAction = true;

            latch = new CountDownLatch(1);
            debug.info("Action complete.");
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && latch.getCount() > 0) {
            BlockPos at = new BlockPos(event.pos);
            if(!at.equals(end)) {
                debug.format("Setting block at %s", at.toString());
                end = at;
            }
            //debug.debugBlock(at, Block.getStateById(57));
        }
    }

    class Message {
        String user;
        String text;
        public Message(String user, String text) {
            this.user = user;
            this.text = text;
        }
    }

//    @Override
//    @SubscribeEvent
//    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        super.onPlayerTick(event);
//        debug.format("Forward: %8.4f Strafe: %8.4f", pc.getPlayer().movementInput.moveForward, pc.getPlayer().movementInput.moveStrafe);
//    }

    Pattern chatPattern = Pattern.compile("<(.*)> (.*)");
    private Message processMessage(IChatComponent message) {
        Matcher m = chatPattern.matcher(message.getUnformattedText());
        if(m.matches()) {
            return new Message(m.group(1), m.group(2));
        }
        else {
            return null;
        }
    }

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        //debug.format("Received message: '%s'", event.message.getUnformattedText());
        Message message = processMessage(event.message);
        if(message == null) {
            debug.info("Error in regexp!");
            return;
        }
        if(message.text.equals("stop")) {
            continueAction = false;
        }
        if(message.text.equals("start") && end != null) {
            latch.countDown();
        }
    }
}
