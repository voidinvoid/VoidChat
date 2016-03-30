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

/**
 * © VoidInVoid - 2015
 * This project is open source but please leave credit if you use any of the code in your projects :)
 */
@Mod(modid = VoidChatMod.MODID, version = VoidChatMod.VERSION, clientSideOnly = true)
public class VoidChatMod {

    public static final String MODID = "voidchat";
    public static final String VERSION = "1.1";

    @EventHandler
    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new ChatTracker());
        MinecraftForge.EVENT_BUS.register(new ServerTracker());
        ClientCommandHandler.instance.registerCommand(new AllChatCommand());
        ClientCommandHandler.instance.registerCommand(new ChatChannelCommand());
    }
}
