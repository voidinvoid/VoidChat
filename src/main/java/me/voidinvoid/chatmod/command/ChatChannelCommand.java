package me.voidinvoid.chatmod.command;

import com.google.common.base.Joiner;
import me.voidinvoid.chatmod.channel.ChatChannel;
import me.voidinvoid.chatmod.server.ServerTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * © VoidInVoid - 2015
 * This project is open source but please leave credit if you use any of the code in your projects :)
 */
public class ChatChannelCommand extends CommandBase {


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
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        List<String> va = new ArrayList<>();
        if (args.length == 1) {
            for (ChatChannel c : ChatChannel.values()) {
                if (c.name().toLowerCase().startsWith(args[0].toLowerCase())) va.add(c.name().toLowerCase());
            }
        }
        return va;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerSP)) return;
        if (ServerTracker.isHypixel) {
            if (args.length == 0) {
                sender.addChatMessage(new ChatComponentText("Invalid usage! Correct usage: /chat channel\nValid channels: " + Joiner.on(", ").join(ChatChannel.values()).toLowerCase()).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED))); //hypixel consistency ftw
            } else {
                char c = args[0].toLowerCase().charAt(0);
                if (c == ChatChannel.activeChannel.name().charAt(0)) {
                    sender.addChatMessage(new ChatComponentText("You're already in this channel!").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED))); //hypixel consistency ftw
                } else {
                    for (ChatChannel cc : ChatChannel.values()) {
                        if (cc.name().toLowerCase().charAt(0) == c) {
                            sender.addChatMessage(new ChatComponentText("You are now in the ").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))
                                    .appendSibling(new ChatComponentText(cc.name()).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)))
                                    .appendSibling(new ChatComponentText(" channel").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))));
                            ChatChannel.activeChannel = cc;
                            return;
                        }
                    }
                    sender.addChatMessage(new ChatComponentText("Invalid channel! Valid channels: " + Joiner.on(", ").join(ChatChannel.values()).toLowerCase()).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
                }
            }
        } else {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/chat " + Joiner.on(" ").join(args)); //in case another server has the same cmd
        }
    }
}
