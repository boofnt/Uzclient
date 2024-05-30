package dev.stay.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.stay.Stay;
import dev.stay.features.command.Command;

public class HelpCommand
        extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String[] commands) {
        HelpCommand.sendMessage("Commands: ");
        for (Command command : Stay.commandManager.getCommands()) {
            HelpCommand.sendMessage(ChatFormatting.GRAY + Stay.commandManager.getPrefix() + command.getName());
        }
    }
}

