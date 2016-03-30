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
public class AllChatCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "achat";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/achat <message>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerSP)) return;
        if (ServerTracker.isHypixel) {
            if (args.length == 0) {
                sender.addChatMessage(new ChatComponentText("The correct syntax is /achat <message>.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED))); //hypixel consistency ftw
            } else {
                Minecraft.getMinecraft().thePlayer.sendChatMessage(Joiner.on(" ").join(args));
            }
        } else {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/achat " + Joiner.on(" ").join(args)); //in case another server has the same cmd
        }
    }
}
