package me.voidinvoid.chatmod.server;

import me.voidinvoid.chatmod.command.ChatChannelCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

/**
 * © VoidInVoid - 2015
 * This project is open source but please leave credit if you use any of the code in your projects :)
 */
public class ChatTracker {

    public ChatTracker() {
        allChat = new KeyBinding("Chat to the ALL channel", Keyboard.KEY_Z, "key.categories.multiplayer");
        guildChat = new KeyBinding("Chat to the GUILD channel", Keyboard.KEY_X, "key.categories.multiplayer");
        partyChat = new KeyBinding("Chat to the PARTY channel", Keyboard.KEY_C, "key.categories.multiplayer");
        replyChat = new KeyBinding("Chat to the REPLY channel", Keyboard.KEY_V, "key.categories.multiplayer");

        ClientRegistry.registerKeyBinding(allChat);
        ClientRegistry.registerKeyBinding(guildChat);
        ClientRegistry.registerKeyBinding(partyChat);
        ClientRegistry.registerKeyBinding(replyChat);
    }

    private KeyBinding allChat, guildChat, partyChat, replyChat;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void key(InputEvent.KeyInputEvent e) {
        if (!ServerTracker.isHypixel) return;
        String chat = null;
        if (allChat.isPressed()) {
            chat = "/achat ";
        } else if (guildChat.isPressed()) {
            chat = "/gchat ";
        } else if (partyChat.isPressed()) {
            chat = "/pchat ";
        } else if (replyChat.isPressed()) {
            chat = "/r ";
        } else if (Minecraft.getMinecraft().gameSettings.keyBindChat.isPressed()) {
            char c = ChatChannelCommand.activeChannel;
            if (c == 'a') chat = ""; else
            chat = "/" + (c == 'r' ? "r " : c + "chat "); //really hacky fix but forge doesnt have event for client chat send D:
        }
        if (chat != null && Minecraft.getMinecraft().currentScreen == null) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiChat(chat));
            //Minecraft.getMinecraft().displayGuiScreen(new ChatOverrideGUI(chat));
        }
    }
}
