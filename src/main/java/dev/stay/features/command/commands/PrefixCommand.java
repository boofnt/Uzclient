package dev.stay.features.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.stay.Stay;
import dev.stay.features.command.Command;

public class PrefixCommand
        extends Command {
    public PrefixCommand() {
        super("prefix", new String[]{"<char>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            Command.sendMessage(ChatFormatting.GREEN + "Current prefix is " + Stay.commandManager.getPrefix());
            return;
        }
        Stay.commandManager.setPrefix(commands[0]);
        Command.sendMessage("Prefix changed to " + ChatFormatting.GRAY + commands[0]);
    }
}

