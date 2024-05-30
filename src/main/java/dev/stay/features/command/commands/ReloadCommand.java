package dev.stay.features.command.commands;

import dev.stay.Stay;
import dev.stay.features.command.Command;

public class ReloadCommand
        extends Command {
    public ReloadCommand() {
        super("reload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        Stay.reload();
    }
}

