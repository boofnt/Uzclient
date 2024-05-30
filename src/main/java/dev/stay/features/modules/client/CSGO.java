package dev.stay.features.modules.client;

import dev.stay.Stay;
import dev.stay.event.events.Render2DEvent;
import dev.stay.features.modules.Module;
import dev.stay.features.modules.client.ClickGui;
import dev.stay.features.setting.Setting;
import dev.stay.util.render.ColorUtil;
import dev.stay.util.render.RenderUtil;
import dev.stay.util.word.Timer;

public class CSGO extends Module {
    Timer delayTimer = new Timer();
    public Setting<Integer> X = this.register(new Setting<>("watermarkx", 5, 0, 300));
    public Setting<Integer> Y = this.register(new Setting<>("watermarky", 5, 0, 300));
    public Setting<Integer> delay = this.register(new Setting<>("delay", 240, 0, 600));
    public Setting<Integer> saturation = this.register(new Setting<>("saturation", 127, 1, 255));
    public Setting<Integer> brightness = this.register(new Setting<>("brightness", 100, 0, 255));
    private String message = "";

    public CSGO() {
        super("CSGO Watermark", "Enhances your screen with additional info", Category.CLIENT, true, false, false);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        drawCSGOWatermark();
    }

    private void drawCSGOWatermark() {
        String playerName = CSGO.mc.player != null ? CSGO.mc.player.getName() : "Unknown";
        int ping = Stay.serverManager != null ? Stay.serverManager.getPing() : 0;
        this.message = "Stay  | " + playerName + " | " + ping + "ms";

        int textWidth = CSGO.mc.fontRenderer.getStringWidth(this.message);
        int textHeight = CSGO.mc.fontRenderer.FONT_HEIGHT;

        int backgroundColor = ColorUtil.toRGBA(0, 0, 0, 150);
        int outlineColor = ColorUtil.toRGBA(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue());

        RenderUtil.drawRectangleCorrectly(X.getValue(), Y.getValue(), textWidth + 8, textHeight + 4, backgroundColor);
        RenderUtil.drawRectangleCorrectly(X.getValue(), Y.getValue(), textWidth + 8, 2, outlineColor);
        CSGO.mc.fontRenderer.drawStringWithShadow(this.message, X.getValue() + 3, Y.getValue() + 3, ColorUtil.toRGBA(255, 255, 255, 255));
    }
}
