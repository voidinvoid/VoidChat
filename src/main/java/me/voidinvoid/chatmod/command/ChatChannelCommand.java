package me.voidinvoid.chatmod.command;

import com.google.common.base.Joiner;
import me.voidinvoid.chatmod.server.ServerTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

/**
 * © VoidInVoid - 2015
 * This project is open source but please leave credit if you use any of the code in your projects :)
 */
public class ChatChannelCommand extends CommandBase {

    public static char activeChannel = 'a';

    @Override
    public String getCommandName() {
        return "chat";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/chat <channel>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerSP)) return;
        if (ServerTracker.isHypixel) {
            if (args.length == 0) {
                sender.addChatMessage(new ChatComponentText("Invalid usage! Correct usage: /chat channel\nValid channels: all, party, guild, reply").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED))); //hypixel consistency ftw
            } else {
                char c = args[0].toLowerCase().charAt(0);
                if (c == activeChannel) {
                    sender.addChatMessage(new ChatComponentText("You're already in this channel!").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED))); //hypixel consistency ftw
                    return;
                } else switch (c) {
                    case 'a':
                        sender.addChatMessage(new ChatComponentText("You are now in the ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))
                                .appendSibling(new ChatComponentText("ALL").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)))
                                        .appendSibling(new ChatComponentText(" channel").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))));
                        break;
                    case 'g':
                        sender.addChatMessage(new ChatComponentText("You are now in the ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))
                                .appendSibling(new ChatComponentText("GUILD").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)))
                                .appendSibling(new ChatComponentText(" channel").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))));
                        break;
                    case 'p':
                        sender.addChatMessage(new ChatComponentText("You are now in the ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))
                                .appendSibling(new ChatComponentText("PARTY").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)))
                                .appendSibling(new ChatComponentText(" channel").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))));
                        break;
                    case 'r':
                        sender.addChatMessage(new ChatComponentText("You are now in the ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))
                                .appendSibling(new ChatComponentText("REPLY").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)))
                                .appendSibling(new ChatComponentText(" channel").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))));
                        break;
                    default:
                        sender.addChatMessage(new ChatComponentText("Invalid channel! Valid channels: all, party, guild, reply").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
                        return;
                }
                activeChannel = c;
            }
        } else {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/chat " + Joiner.on(" ").join(args)); //in case another server has the same cmd
        }
    }
}
