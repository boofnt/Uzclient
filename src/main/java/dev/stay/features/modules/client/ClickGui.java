package dev.stay.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import dev.stay.Stay;
import dev.stay.event.events.ClientEvent;
import dev.stay.features.command.Command;
import dev.stay.features.gui.StayGui;
import dev.stay.features.modules.Module;
import dev.stay.features.setting.Setting;
import dev.stay.util.render.ColorUtil;
import dev.stay.util.render.RenderUtil;

import dev.stay.util.Util;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import dev.stay.event.events.Render2DEvent;

public class ClickGui extends Module
{
    private final Setting<Settings> setting;
    private static ClickGui INSTANCE = new ClickGui();
    public Setting<String> prefix;
    public Setting<Boolean> customFov;
    public Setting<Float> fov;
    public Setting<Integer> red;
    public Setting<Integer> green;
    public Setting<Integer> blue;
    public Setting<Integer> hoverAlpha;
    public Setting<Integer> alphaBox;
    public Setting<Integer> alpha;
    public Setting<Boolean> rainbow;
    public Setting<rainbowMode> rainbowModeHud;
    public Setting<rainbowModeArray> rainbowModeA;
    public Setting<Integer> rainbowHue;
    public Setting<Float> rainbowBrightness;
    public Setting<Float> rainbowSaturation;
    public Setting<Boolean> rainbowg;
    public Setting<Boolean> guiComponent;
    public Setting<Mode> mode;
    public Setting<Integer> backgroundAlpha;
    public Setting<Integer> gb_red;
    public Setting<Integer> gb_green;
    public Setting<Integer> gb_blue;
    private int color;

    public ClickGui() {
        super("ClickGui", "Opens the ClickGui", Category.CLIENT, true, false, false);
        this.setting = (Setting<Settings>)this.register(new Setting("Settings", Settings.Gui));
        this.prefix = (Setting<String>)this.register(new Setting("Prefix", ";", v -> this.setting.getValue() == Settings.Gui));
        this.customFov = (Setting<Boolean>)this.register(new Setting("CustomFov", false, v -> this.setting.getValue() == Settings.Gui));
        this.fov = (Setting<Float>)this.register(new Setting("Fov", 147.0f, (-180.0f), 180.0f, v -> this.setting.getValue() == Settings.Gui && this.customFov.getValue()));
        this.red = (Setting<Integer>)this.register(new Setting("Red", 158, 0, 255, v -> this.setting.getValue() == Settings.Gui));
        this.green = (Setting<Integer>)this.register(new Setting("Green", 158, 0, 255, v -> this.setting.getValue() == Settings.Gui));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", 225, 0, 255, v -> this.setting.getValue() == Settings.Gui));
        this.hoverAlpha = (Setting<Integer>)this.register(new Setting("Alpha", 90, 0, 255, v -> this.setting.getValue() == Settings.Gui));
        this.alphaBox = (Setting<Integer>)this.register(new Setting("AlphaBox", 200, 0, 255, v -> this.setting.getValue() == Settings.Gui));
        this.alpha = (Setting<Integer>)this.register(new Setting("HoverAlpha", 120, 0, 255, v -> this.setting.getValue() == Settings.Gui));
        this.rainbow = (Setting<Boolean>)this.register(new Setting("Rainbow", false, v -> this.setting.getValue() == Settings.Gui));
        this.rainbowModeHud = (Setting<rainbowMode>)this.register(new Setting("HUD", rainbowMode.Static, v -> this.rainbow.getValue() && this.setting.getValue() == Settings.Gui));
        this.rainbowModeA = (Setting<rainbowModeArray>)this.register(new Setting("ArrayList", rainbowModeArray.Static, v -> this.rainbow.getValue() && this.setting.getValue() == Settings.Gui));
        this.rainbowHue = (Setting<Integer>)this.register(new Setting("Delay", 200, 0, 600, v -> this.rainbow.getValue() && this.setting.getValue() == Settings.Gui));
        this.rainbowBrightness = (Setting<Float>)this.register(new Setting("Brightness ", 255.0f, 1.0f, 255.0f, v -> this.rainbow.getValue() && this.setting.getValue() == Settings.Gui));
        this.rainbowSaturation = (Setting<Float>)this.register(new Setting("Saturation", 100.0f, 1.0f, 255.0f, v -> this.rainbow.getValue() && this.setting.getValue() == Settings.Gui));
        this.mode = (Setting<Mode>)this.register(new Setting("Mode", Mode.COLOR, v -> this.setting.getValue() == Settings.Background));
        this.backgroundAlpha = (Setting<Integer>)this.register(new Setting("Background Alpha", 80, 0, 255, v -> this.mode.getValue() == Mode.COLOR && this.setting.getValue() == Settings.Background));
        this.gb_red = (Setting<Integer>)this.register(new Setting("RedBG", 145, 0, 255, v -> this.mode.getValue() == Mode.COLOR && this.setting.getValue() == Settings.Background));
        this.gb_green = (Setting<Integer>)this.register(new Setting("GreenBG", 120, 0, 255, v -> this.mode.getValue() == Mode.COLOR && this.setting.getValue() == Settings.Background));
        this.gb_blue = (Setting<Integer>)this.register(new Setting("BlueBG", 225, 0, 255, v -> this.mode.getValue() == Mode.COLOR && this.setting.getValue() == Settings.Background));
        this.setInstance();

    }

    public static ClickGui getInstance() {
        if (ClickGui.INSTANCE == null) {
            ClickGui.INSTANCE = new ClickGui();
        }
        return ClickGui.INSTANCE;
    }

    public static ClickGui INSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new ClickGui();
        return INSTANCE;
    }

    private void setInstance() {
        ClickGui.INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.customFov.getValue()) {
            ClickGui.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, (float)this.fov.getValue());
        }
    }

    @SubscribeEvent
    public void onSettingChange(final ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                Stay.commandManager.setPrefix(this.prefix.getPlannedValue());
                Command.sendMessage("Prefix set to " + ChatFormatting.DARK_GRAY + Stay.commandManager.getPrefix());
            }
            Stay.colorManager.setColor(this.red.getPlannedValue(), this.green.getPlannedValue(), this.blue.getPlannedValue(), this.hoverAlpha.getPlannedValue());
        }
    }

    @Override
    public void onEnable() {
        ClickGui.mc.displayGuiScreen((GuiScreen)StayGui.getClickGui());
    }

    @Override
    public void onLoad() {
        Stay.colorManager.setColor(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.hoverAlpha.getValue());
        Stay.commandManager.setPrefix(this.prefix.getValue());
    }

    @Override
    public void onRender2D(final Render2DEvent event) {
        this.drawBackground();
    }

    public void drawBackground() {
        if (this.mode.getValue() == Mode.COLOR) {
            if (getInstance().isEnabled()) {
                RenderUtil.drawRectangleCorrectly(0, 0, 1920, 1080, ColorUtil.toRGBA(this.gb_red.getValue(), this.gb_green.getValue(), this.gb_blue.getValue(), this.backgroundAlpha.getValue()));
            }
            else {
                RenderUtil.drawRectangleCorrectly(0, 0, 1920, 1080, ColorUtil.toRGBA(0, 0, 0, 0));
            }
        }
        if (this.mode.getValue() == Mode.NONE) {
            if (getInstance().isEnabled()) {
                RenderUtil.drawRectangleCorrectly(0, 0, 1920, 1080, ColorUtil.toRGBA(this.gb_red.getValue(), this.gb_green.getValue(), this.gb_blue.getValue(), this.backgroundAlpha.getValue()));
            }
            else {
                RenderUtil.drawRectangleCorrectly(0, 0, 1920, 1080, ColorUtil.toRGBA(0, 0, 0, 0));
            }
        }
    }

    @Override
    public void onTick() {
        if (!(mc.currentScreen instanceof StayGui))
            disable();
    }


    @Override
    public void onDisable() {
        if (ClickGui.mc.currentScreen instanceof StayGui) {
            Util.mc.displayGuiScreen((GuiScreen)null);
        }
    }


    public final int getColor() {
        return getColor2().hashCode();
    }

    public final Color getColor2() {
        return new Color(red.getValue(), green.getValue(), blue.getValue());
    }

    static {
        ClickGui.INSTANCE = new ClickGui();
    }

    public enum rainbowModeArray
    {
        Static,
        Up;
    }

    public enum rainbowMode
    {
        Static,
        Sideway;
    }

    public enum Settings
    {
        Gui,
        Background;
    }

    public enum Mode
    {
        COLOR,
        BLUR,
        NONE;
    }
}