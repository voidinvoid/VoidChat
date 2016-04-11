package me.voidinvoid.chatmod;

import me.voidinvoid.chatmod.command.AllChatCommand;
import me.voidinvoid.chatmod.command.ChatChannelCommand;
import me.voidinvoid.chatmod.server.ChatTracker;
import me.voidinvoid.chatmod.server.ServerTracker;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import java.net.URL;
import java.util.Scanner;

/**
 * © VoidInVoid - 2015
 * This project is open source but please leave credit if you use any of the code in your projects :)
 */
@Mod(modid = VoidChatMod.MODID, version = VoidChatMod.VERSION, clientSideOnly = true)
public class VoidChatMod {

    public static final String MODID = "voidchat";
    public static final String VERSION = "1.2";

    public static String updateInfo = null;

    @EventHandler
    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new ChatTracker());
        MinecraftForge.EVENT_BUS.register(new ServerTracker());
        ClientCommandHandler.instance.registerCommand(new AllChatCommand());
        ClientCommandHandler.instance.registerCommand(new ChatChannelCommand());
    }

    @EventHandler
    public void postinit(FMLPostInitializationEvent e) {
        //update check
        try {
            URL u = new URL("https://raw.githubusercontent.com/voidinvoid/VoidChat/master/version");
            Scanner s = new Scanner(u.openStream());
            String v = s.nextLine();
            if (v.startsWith("release ")) {
                v = v.split(" ")[1];
                if (!v.equals(VERSION)) {
                    updateInfo = v;
                    System.out.println("An update is available to Void Chat (" + v + ")");
                }
            }
        } catch (Exception ex) {
            System.out.println("Error checking for Void Chat update :(");
            ex.printStackTrace();
        }
    }
}
