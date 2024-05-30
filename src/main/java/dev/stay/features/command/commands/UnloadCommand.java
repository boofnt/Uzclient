package dev.stay.features.command.commands;

import dev.stay.Stay;
import dev.stay.features.command.Command;

public class UnloadCommand
        extends Command {
    public UnloadCommand() {
        super("unload", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        Stay.unload(true);
    }
}

