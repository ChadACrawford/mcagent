package edu.utulsa.masters.mcagent;

import edu.utulsa.masters.mcagent.actuator.PlayerController;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chad on 5/17/16.
 */
public class PlayerCalibratorAgent extends MCAgent {
    public PlayerCalibratorAgent(EntityPlayerSP player) {
        super(player);
    }

    private boolean active = false;
    private Lock mutexTick = new ReentrantLock(true);

    private CountDownLatch latch = new CountDownLatch(1);
    @Override
    public void exec() {
        overrideWindow = false;
        PlayerController.doOverrideKeys = false;

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        overrideWindow = true;
        PlayerController.doOverrideKeys = true;

        nextTick();

        debug.info("Walking test.");
        speedTest(RunningType.WALKING);
        jumpingTest(RunningType.WALKING);

        debug.info("Sneaking test.");
        speedTest(RunningType.SNEAKING);
        jumpingTest(RunningType.SNEAKING);

        debug.info("Sprinting test.");
        speedTest(RunningType.SPRINTING);
        jumpingTest(RunningType.SPRINTING);

        GameInfo.saveData();

        finish();

        latch = new CountDownLatch(1);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    Thread tickThread;
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        super.onPlayerTick(event);

        if(event.phase == TickEvent.Phase.END) {
            tickThread = Thread.currentThread();
            if (active) {
                continueThread();
            }
        }
    }

    private void nextTick() {
//        if(tickThread == null) {
        try {
            synchronized (this) {
                this.wait();
            }
        } catch(InterruptedException e) {}
//        }
//        else {
//            swap(this, tickThread);
//        }
    }

    private void continueThread() {
        synchronized (this) {
            this.notify();
        }
        while(this.getState() == State.RUNNABLE);
        //swap(tickThread, this);
    }

    private void finish() {
//        synchronized (tickThread) {
//            tickThread.notify();
//        }
    }

    private static void swap(Thread running, Thread toRun) {
        try {
            synchronized (toRun) {
                toRun.notify();
            }
            synchronized (running) {
                running.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    enum RunningType { WALKING, SPRINTING, SNEAKING }

    protected void move(RunningType runType) {
        switch(runType) {
            case WALKING:
                pc.walk();
                break;
            case SPRINTING:
                pc.sprint();
                break;
            case SNEAKING:
                pc.sneak();
                break;
        }
    }

    protected void speedTest(RunningType runType) {
        move(runType);

        pc.forward();

        double velocity = 0;

        for(int i = 0; i < 50; i++)
            nextTick();

        final int NUM_TICKS = 200;
        for(int i = 0; i < NUM_TICKS; i++) {
            nextTick();
            velocity += pc.getCurrentVelocity();
        }

        switch(runType) {
            case WALKING:
                GameInfo.WALK_SPEED = velocity / NUM_TICKS;
                break;
            case SNEAKING:
                GameInfo.SNEAK_SPEED = velocity / NUM_TICKS;
                break;
            case SPRINTING:
                GameInfo.SPRINT_SPEED = velocity / NUM_TICKS;
                break;
        }

        pc.stopMoving();

        for(int i = 0; i < 50; i++) nextTick();
    }

    protected void jumpingTest(RunningType runType) {
        move(runType);

        pc.forward();
        // start running!
        for(int i = 0; i < 50; i++) {
            nextTick();
        }

        Vec3 origPos = pc.getPlayer().getPositionVector();
        double velocity = 0;
        double maxHeightDistance;
        int ticks = 0;
        pc.jump();

        nextTick();

        Vec3 lastPos = pc.getPlayer().getPositionVector();
        while(true) {
            Vec3 cPos = pc.getPlayer().getPositionVector();
            //System.out.println(cPos.yCoord);

            velocity += pc.getCurrentVelocity();
            if(cPos.yCoord < lastPos.yCoord) {
                maxHeightDistance = pc.xzdist(cPos, lastPos);
                break;
            }

            ticks++;
            lastPos = pc.getPlayer().getPositionVector();
            nextTick();
        }

        while(true) {
            Vec3 cPos = pc.getPlayer().getPositionVector();
            //System.out.println(cPos.yCoord);
            velocity += pc.getCurrentVelocity();
            if(cPos.yCoord == origPos.yCoord)
                break;

            ticks++;
            lastPos = new Vec3(cPos.xCoord, cPos.yCoord, cPos.zCoord);
            nextTick();
        }

        double totalDistance = pc.xzdist(pc.getPlayer().getPositionVector(), origPos);

        switch(runType) {
            case WALKING:
                GameInfo.WALK_JUMP_SPEED = velocity / ticks;
                GameInfo.WALK_JUMP_HALFDIST = maxHeightDistance;
                GameInfo.WALK_JUMP_DIST = totalDistance;
                break;
            case SPRINTING:
                GameInfo.SPRINT_JUMP_SPEED = velocity / ticks;
                GameInfo.SPRINT_JUMP_HALFDIST = maxHeightDistance;
                GameInfo.SPRINT_JUMP_DIST = totalDistance;
                break;
            case SNEAKING:
                GameInfo.SNEAK_JUMP_SPEED = velocity / ticks;
                GameInfo.SNEAK_JUMP_HALFDIST = maxHeightDistance;
                GameInfo.SNEAK_JUMP_DIST = totalDistance;
                break;
        }

        pc.stopMoving();

        for(int i = 0; i < 50; i++)
            nextTick();
    }

    class Message {
        String user;
        String text;
        public Message(String user, String text) {
            this.user = user;
            this.text = text;
        }
    }

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
        Message message = processMessage(event.message);
        if(message == null) {
            debug.info("Error in regexp!");
            return;
        }
        if(message.text.equals("start calibration")) {
            latch.countDown();
            active = true;
        }
    }
}
