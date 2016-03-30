package me.voidinvoid.chatmod.server;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * © VoidInVoid - 2015
 * This project is open source but please leave credit if you use any of the code in your projects :)
 */
public class ServerTracker {

    public static boolean isHypixel;

    @SubscribeEvent
    public void checkHypixel(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        String[] s = e.manager.getRemoteAddress().toString().split("/");
        if (s.length < 1) {
            isHypixel = false;
            return;
        }
        if (s[0].endsWith(".hypixel.net")) isHypixel = true; else return;
        final Timer r = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                awaitCommand = true;
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/chat a");
                r.cancel();
            }
        };
        r.schedule(t, 600); //might change later
    }

    @SubscribeEvent
    public void disconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        isHypixel = false;
    }

    private boolean awaitCommand;

    @SubscribeEvent
    public void chat(ClientChatReceivedEvent e) {
        if (awaitCommand) {
            String s = e.message.getUnformattedText();
            if (s.equals("You're already in this channel!") || s.equals("You are now in the ALL channel")) {
                e.setCanceled(true);
                awaitCommand = false;
            }
        }
    }
}
