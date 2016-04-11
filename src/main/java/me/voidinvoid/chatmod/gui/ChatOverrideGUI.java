package me.voidinvoid.chatmod.gui;

import com.google.common.collect.Lists;
import me.voidinvoid.chatmod.channel.ChatChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

/**
 * © VoidInVoid - 2015
 * This project is open source but please leave credit if you use any of the code in your projects :)
 */

public class ChatOverrideGUI extends GuiScreen {

    private String historyBuffer = "";
    /**
     * keeps position of which chat message you will select when you press up, (does not increase for duplicated
     * messages sent immediately after each other)
     */
    private int sentHistoryCursor = -1;
    private boolean playerNamesFound;
    private boolean waitingOnAutocomplete;
    private int autocompleteIndex;
    private String defaultText;
    private List<String> foundPlayerNames = Lists.newArrayList();

    private ChatChannel channel;
    /**
     * Chat entry field
     */
    protected GuiTextField inputField;
    /** is the text that appears when you press the chat key and the input box appears pre-filled */
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */

    public ChatOverrideGUI(ChatChannel channel) {
        super();
        this.channel = channel;
    }

    public ChatOverrideGUI(String defaultText) {
        super();
        this.defaultText = defaultText; //will probs add command channel later. will leave for now
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.sentHistoryCursor = Minecraft.getMinecraft().ingameGUI.getChatGUI().getSentMessages().size();
        this.inputField = new GuiTextField(0, this.fontRendererObj, 42, this.height - 12, this.width - 4, 12);
        this.inputField.setMaxStringLength(100 - (channel.command.length()));
        this.inputField.setEnableBackgroundDrawing(false);
        this.inputField.setFocused(true);
        this.inputField.setCanLoseFocus(false);
        if (defaultText != null) inputField.setText(defaultText);
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        Minecraft.getMinecraft().ingameGUI.getChatGUI().resetScroll();
    }

    /**
     * Called from the main game loop to update the screen.
     */

    @Override
    public void updateScreen() {
        this.inputField.updateCursorCounter();
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.waitingOnAutocomplete = false;

        if (keyCode == 15) {
            this.autocompletePlayerNames();
        } else {
            this.playerNamesFound = false;
        }

        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        } else if (keyCode != 28 && keyCode != 156) {
            if (keyCode == 200) {
                this.getSentHistory(-1);
            } else if (keyCode == 208) {
                this.getSentHistory(1);
            } else if (keyCode == 201) {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().scroll(Minecraft.getMinecraft().ingameGUI.getChatGUI().getLineCount() - 1);
            } else if (keyCode == 209) {
                Minecraft.getMinecraft().ingameGUI.getChatGUI().scroll(-Minecraft.getMinecraft().ingameGUI.getChatGUI().getLineCount() + 1);
            } else {
                this.inputField.textboxKeyTyped(typedChar, keyCode);
            }
        } else {
            //ENTER
            String s = this.inputField.getText().trim();

            if (s.length() > 0) {
                this.sendChatMessage(channel.command + s); //prepend the command prefix
            }

            this.mc.displayGuiScreen(null);
        }
    }

    /**
     * Handles mouse input.
     */

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0) {
            if (i > 1) {
                i = 1;
            }

            if (i < -1) {
                i = -1;
            }

            if (!isShiftKeyDown()) {
                i *= 7;
            }

            Minecraft.getMinecraft().ingameGUI.getChatGUI().scroll(i);
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            IChatComponent ichatcomponent = Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

            if (this.handleComponentClick(ichatcomponent)) {
                return;
            }
        }

        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Sets the text of the chat
     */
    protected void setText(String newChatText, boolean shouldOverwrite) {
        if (shouldOverwrite) {
            this.inputField.setText(newChatText);
        } else {
            this.inputField.writeText(newChatText);
        }
    }

    public void autocompletePlayerNames() {
        if (this.playerNamesFound) {
            this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());

            if (this.autocompleteIndex >= this.foundPlayerNames.size()) {
                this.autocompleteIndex = 0;
            }
        } else {
            int i = this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false);
            this.foundPlayerNames.clear();
            this.autocompleteIndex = 0;
            String s = this.inputField.getText().substring(i).toLowerCase();
            String s1 = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
            this.sendAutocompleteRequest(s1, s);

            if (this.foundPlayerNames.isEmpty()) {
                return;
            }

            this.playerNamesFound = true;
            this.inputField.deleteFromCursor(i - this.inputField.getCursorPosition());
        }

        if (this.foundPlayerNames.size() > 1) {
            StringBuilder stringbuilder = new StringBuilder();

            for (String s2 : this.foundPlayerNames) {
                if (stringbuilder.length() > 0) {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(s2);
            }

            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(stringbuilder.toString()), 1);
        }

        this.inputField.writeText(EnumChatFormatting.getTextWithoutFormattingCodes(this.foundPlayerNames.get(this.autocompleteIndex++)));
    }

    private void sendAutocompleteRequest(String p_146405_1_, String p_146405_2_) {
        if (p_146405_1_.length() >= 1) {
            net.minecraftforge.client.ClientCommandHandler.instance.autoComplete(p_146405_1_, p_146405_2_);
            BlockPos blockpos = null;

            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                blockpos = this.mc.objectMouseOver.getBlockPos();
            }

            this.mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(p_146405_1_, blockpos));
            this.waitingOnAutocomplete = true;
        }
    }

    /**
     * input is relative and is applied directly to the sentHistoryCursor so -1 is the previous message, 1 is the next
     * message from the current cursor position
     */

    public void getSentHistory(int msgPos) {
        int i = this.sentHistoryCursor + msgPos;
        int j = Minecraft.getMinecraft().ingameGUI.getChatGUI().getSentMessages().size();
        i = MathHelper.clamp_int(i, 0, j);

        if (i != this.sentHistoryCursor) {
            if (i == j) {
                this.sentHistoryCursor = j;
                this.inputField.setText(this.historyBuffer);
            } else {
                if (this.sentHistoryCursor == j) {
                    this.historyBuffer = this.inputField.getText();
                }

                this.inputField.setText(Minecraft.getMinecraft().ingameGUI.getChatGUI().getSentMessages().get(i));
                this.sentHistoryCursor = i;
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //CHAT TYPE
        drawRect(2, this.height - 14, 38, this.height - 2, channel.colour);
        String sn = channel.name();
        fontRendererObj.drawString(sn, 19 - ((int) (fontRendererObj.getStringWidth(sn) * 0.5)), this.height - 12, 0x80ffffff);
        //
        drawRect(38, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
        this.inputField.drawTextBox();
        IChatComponent ichatcomponent = Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

        if (ichatcomponent != null && ichatcomponent.getChatStyle().getChatHoverEvent() != null) {
            this.handleComponentHover(ichatcomponent, mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void onAutocompleteResponse(String[] p_146406_1_) {
        if (this.waitingOnAutocomplete) {
            this.playerNamesFound = false;
            this.foundPlayerNames.clear();

            String[] complete = net.minecraftforge.client.ClientCommandHandler.instance.latestAutoComplete;
            if (complete != null) {
                p_146406_1_ = com.google.common.collect.ObjectArrays.concat(complete, p_146406_1_, String.class);
            }

            for (String s : p_146406_1_) {
                if (s.length() > 0) {
                    this.foundPlayerNames.add(s);
                }
            }

            String s1 = this.inputField.getText().substring(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false));
            String s2 = org.apache.commons.lang3.StringUtils.getCommonPrefix(p_146406_1_);
            s2 = net.minecraft.util.EnumChatFormatting.getTextWithoutFormattingCodes(s2);

            if (s2.length() > 0 && !s1.equalsIgnoreCase(s2)) {
                this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
                this.inputField.writeText(s2);
            } else if (this.foundPlayerNames.size() > 0) {
                this.playerNamesFound = true;
                this.autocompletePlayerNames();
            }
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
