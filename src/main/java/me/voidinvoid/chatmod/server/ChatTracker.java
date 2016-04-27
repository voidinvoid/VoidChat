package me.voidinvoid.chatmod.server;

import me.voidinvoid.chatmod.channel.ChatChannel;
import me.voidinvoid.chatmod.command.ChatChannelCommand;
import me.voidinvoid.chatmod.gui.ChatOverrideGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
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

    private KeyBinding modeSwitchKeybind;

    private boolean chatMode = true; //true = new mode

    public ChatTracker() {
        for (ChatChannel c : ChatChannel.values()) {
            ClientRegistry.registerKeyBinding(c.keyBind);
        }

        modeSwitchKeybind = new KeyBinding("Switch Void Chat mode", Keyboard.KEY_M, "key.categories.multiplayer");
        ClientRegistry.registerKeyBinding(modeSwitchKeybind);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void key(InputEvent.KeyInputEvent e) {
        if (!ServerTracker.isHypixel) return;
        Minecraft m = Minecraft.getMinecraft();
        /*if (m.gameSettings.keyBindCommand.isPressed()) {
            m.displayGuiScreen(new ChatOverrideGUI("/")); maybe will add command chat channel in future or default all text with a /.
        }*/
        if (modeSwitchKeybind.isPressed()) {
            chatMode = !chatMode;
            m.thePlayer.addChatMessage(new ChatComponentText("Chat mode set to ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.YELLOW)).appendSibling(new ChatComponentText(chatMode ? "FANCY" : "CLASSIC").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD))));
            return;
        }
        ChatChannel cc = null;
        for (ChatChannel c : ChatChannel.values()) {
            if (c.keyBind.isPressed()) {
                cc = c;
                break;
            }
        }
        if (cc == null && m.gameSettings.keyBindChat.isPressed()) {
            cc = ChatChannel.activeChannel;
        }
        if (cc != null && m.currentScreen == null) {
            if (m.gameSettings.keyBindSneak.isPressed()) {
                new ChatChannelCommand().processCommand(m.thePlayer, new String[] {cc.name()});
            }
            m.displayGuiScreen(chatMode ? new ChatOverrideGUI(cc) : new GuiChat(cc.command));
        }
    }
}
