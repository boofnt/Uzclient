package dev.stay.features.modules.movement;

import dev.stay.features.modules.Module;

public class Speed
        extends Module {
    public Speed() {
        super("Speed", "Speeds Your Movement", Module.Category.MOVEMENT, false, false, false);
    }

    @Override
    public String getDisplayInfo() {
        return "Strafe";
    }
}