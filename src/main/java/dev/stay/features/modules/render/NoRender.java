package dev.stay.features.modules.render;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import dev.stay.event.events.PacketEvent;
import dev.stay.features.modules.Module;
import dev.stay.features.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BossInfo;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class NoRender extends Module {

    private static NoRender INSTANCE = new NoRender();

    public Setting<Boolean> fire = this.register(new Setting<>("Fire", false, "Removes the portal overlay."));
    public Setting<Boolean> portal = this.register(new Setting<>("Portal", false, "Removes the portal overlay."));
    public Setting<Boolean> pumpkin = this.register(new Setting<>("Pumpkin", false, "Removes the pumpkin overlay."));
    public Setting<Boolean> totemPops = this.register(new Setting<>("TotemPop", false, "Removes the Totem overlay."));
    public Setting<Boolean> items = this.register(new Setting<>("Items", false, "Removes items on the ground."));
    public Setting<Boolean> nausea = this.register(new Setting<>("Nausea", false, "Removes Portal Nausea."));
    public Setting<Boolean> hurtcam = this.register(new Setting<>("HurtCam", false, "Removes shaking after taking damage."));
    public Setting<Fog> fog = this.register(new Setting<>("Fog", Fog.NONE, "Removes Fog."));
    public Setting<Boolean> noWeather = this.register(new Setting<>("Weather", false, "AntiWeather"));
    public Setting<Boss> boss = this.register(new Setting<>("BossBars", Boss.NONE, "Modifies the bossbars."));
    public Setting<Float> scale = this.register(new Setting<>("Scale", 0.5f, 0.0f, 1.0f, v -> this.boss.getValue() == Boss.MINIMIZE || this.boss.getValue() != Boss.STACK, "Scale of the bars."));
    public Setting<Boolean> bats = this.register(new Setting<>("Bats", false, "Removes bats."));
    public Setting<NoArmor> noArmor = this.register(new Setting<>("NoArmor", NoArmor.NONE, "Doesn't render armor on players."));
    public Setting<Boolean> glint = this.register(new Setting<>("Glint", false, v -> this.noArmor.getValue() != NoArmor.NONE));
    public Setting<Skylight> skylight = this.register(new Setting<>("Skylight", Skylight.NONE));
    public Setting<Boolean> barriers = this.register(new Setting<>("Barriers", false, "Barriers"));
    public Setting<Boolean> blocks = this.register(new Setting<>("Blocks", false, "Blocks"));
    public Setting<Boolean> advancements = this.register(new Setting<>("Advancements", false));
    public Setting<Boolean> pigmen = this.register(new Setting<>("Pigmen", false));
    public Setting<Boolean> timeChange = this.register(new Setting<>("TimeChange", false));
    public Setting<Integer> time = this.register(new Setting<>("Time", 0, 0, 23000, v -> this.timeChange.getValue()));

    public NoRender() {
        super("NoRender", "Allows you to stop rendering stuff", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }

    public static NoRender getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoRender();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.items.getValue()) {
            NoRender.mc.world.loadedEntityList.stream()
                    .filter(EntityItem.class::isInstance)
                    .map(EntityItem.class::cast)
                    .forEach(Entity::setDead);
        }
        if (this.noWeather.getValue() && NoRender.mc.world.isRaining()) {
            NoRender.mc.world.setRainStrength(0.0f);
        }
        if (this.timeChange.getValue()) {
            NoRender.mc.world.setWorldTime(this.time.getValue());
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketTimeUpdate && this.timeChange.getValue()) {
            event.setCanceled(true);
        }
    }

    public void doVoidFogParticles(int posX, int posY, int posZ) {
        Random random = new Random();
        ItemStack itemstack = NoRender.mc.player.getHeldItemMainhand();
        boolean flag = !this.barriers.getValue() || (NoRender.mc.playerController.getCurrentGameType() == GameType.CREATIVE && !itemstack.isEmpty() && itemstack.getItem() == Item.getItemFromBlock(Blocks.BARRIER));
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int j = 0; j < 667; ++j) {
            this.showBarrierParticles(posX, posY, posZ, 16, random, flag, blockpos$mutableblockpos);
            this.showBarrierParticles(posX, posY, posZ, 32, random, flag, blockpos$mutableblockpos);
        }
    }

    public void showBarrierParticles(int x, int y, int z, int offset, Random random, boolean holdingBarrier, BlockPos.MutableBlockPos pos) {
        int i = x + NoRender.mc.world.rand.nextInt(offset) - NoRender.mc.world.rand.nextInt(offset);
        int j = y + NoRender.mc.world.rand.nextInt(offset) - NoRender.mc.world.rand.nextInt(offset);
        int k = z + NoRender.mc.world.rand.nextInt(offset) - NoRender.mc.world.rand.nextInt(offset);
        pos.setPos(i, j, k);
        IBlockState iblockstate = NoRender.mc.world.getBlockState(pos);
        iblockstate.getBlock().randomDisplayTick(iblockstate, NoRender.mc.world, pos, random);
        if (!holdingBarrier && iblockstate.getBlock() == Blocks.BARRIER) {
            NoRender.mc.world.spawnParticle(EnumParticleTypes.BARRIER, (double) i + 0.5f, (double) j + 0.5f, (double) k + 0.5f, 0.0, 0.0, 0.0);
        }
    }

    @SubscribeEvent
    public void onRenderPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.BOSSINFO && this.boss.getValue() != Boss.NONE) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderPost(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.BOSSINFO || this.boss.getValue() == Boss.NONE) {
            return;
        }

        Map<UUID, BossInfoClient> map = NoRender.mc.ingameGUI.getBossOverlay().mapBossInfos;

        if (map == null) {
            return;
        }

        ScaledResolution scaledresolution = new ScaledResolution(mc);
        int i = scaledresolution.getScaledWidth();
        int j = 12;

        if (this.boss.getValue() == Boss.MINIMIZE) {
            for (Map.Entry<UUID, BossInfoClient> entry : map.entrySet()) {
                BossInfoClient info = entry.getValue();
                String text = info.getName().getFormattedText();
                int k = (int) (i / this.scale.getValue() / 2.0f - 91.0f);
                GL11.glScaled(this.scale.getValue(), this.scale.getValue(), 1.0);
                if (!event.isCanceled()) {
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    mc.getTextureManager().bindTexture(GuiBossOverlay.GUI_BARS_TEXTURES);
                    NoRender.mc.ingameGUI.getBossOverlay().render(k, j, info);
                    NoRender.mc.fontRenderer.drawStringWithShadow(text, i / this.scale.getValue() / 2.0f - NoRender.mc.fontRenderer.getStringWidth(text) / 2, j - 9, 0xFFFFFF);
                }
                GL11.glScaled(1.0 / this.scale.getValue(), 1.0 / this.scale.getValue(), 1.0);
                j += 10 + NoRender.mc.fontRenderer.FONT_HEIGHT;
            }
        } else if (this.boss.getValue() == Boss.STACK) {
            Map<String, Pair<BossInfoClient, Integer>> to = new HashMap<>();
            for (Map.Entry<UUID, BossInfoClient> entry : map.entrySet()) {
                String s = entry.getValue().getName().getFormattedText();
                to.merge(s, new Pair<>(entry.getValue(), 1), (oldVal, newVal) -> new Pair<>(oldVal.getKey(), oldVal.getValue() + 1));
            }

            for (Map.Entry<String, Pair<BossInfoClient, Integer>> entry : to.entrySet()) {
                String text = entry.getKey() + " x" + entry.getValue().getValue();
                BossInfoClient info = entry.getValue().getKey();
                int k = (int) (i / this.scale.getValue() / 2.0f - 91.0f);
                GL11.glScaled(this.scale.getValue(), this.scale.getValue(), 1.0);
                if (!event.isCanceled()) {
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    mc.getTextureManager().bindTexture(GuiBossOverlay.GUI_BARS_TEXTURES);
                    NoRender.mc.ingameGUI.getBossOverlay().render(k, j, info);
                    NoRender.mc.fontRenderer.drawStringWithShadow(text, i / this.scale.getValue() / 2.0f - NoRender.mc.fontRenderer.getStringWidth(text) / 2, j - 9, 0xFFFFFF);
                }
                GL11.glScaled(1.0 / this.scale.getValue(), 1.0 / this.scale.getValue(), 1.0);
                j += 10 + NoRender.mc.fontRenderer.FONT_HEIGHT;
            }
        }
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Pre<?> event) {
        if (this.bats.getValue() && event.getEntity() instanceof EntityBat) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlaySound(PlaySoundAtEntityEvent event) {
        if (this.bats.getValue() && (
                event.getSound().equals(SoundEvents.ENTITY_BAT_AMBIENT) ||
                        event.getSound().equals(SoundEvents.ENTITY_BAT_DEATH) ||
                        event.getSound().equals(SoundEvents.ENTITY_BAT_HURT) ||
                        event.getSound().equals(SoundEvents.ENTITY_BAT_LOOP) ||
                        event.getSound().equals(SoundEvents.ENTITY_BAT_TAKEOFF))) {
            event.setVolume(0.0f);
            event.setPitch(0.0f);
            event.setCanceled(true);
        }
    }

    static {
        INSTANCE = new NoRender();
    }

    public static class Pair<T, S> {
        private T key;
        private S value;

        public Pair(T key, S value) {
            this.key = key;
            this.value = value;
        }

        public T getKey() {
            return this.key;
        }

        public void setKey(T key) {
            this.key = key;
        }

        public S getValue() {
            return this.value;
        }

        public void setValue(S value) {
            this.value = value;
        }
    }

    public enum NoArmor {
        NONE,
        ALL,
        HELMET;
    }

    public enum Boss {
        NONE,
        REMOVE,
        STACK,
        MINIMIZE;
    }

    public enum Fog {
        NONE,
        AIR,
        NOFOG;
    }

    public enum Skylight {
        NONE,
        WORLD,
        ENTITY,
        ALL;
    }
}
