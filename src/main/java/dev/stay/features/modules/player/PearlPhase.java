package dev.stay.features.modules.player;

import dev.stay.features.modules.Module;
import dev.stay.features.setting.Setting;
import dev.stay.util.entity.InventoryUtil;
import dev.stay.util.misc.Locks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class PearlPhase extends Module {
    private final Setting<Boolean> bypass = register(new Setting<>("Bypass", true));

    public PearlPhase() {
        super("PearlPhase", "Enable this to throw a pearl to phase.", Category.MISC, true, false, false);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }

        int pearlSlot = InventoryUtil.findHotbarItem(Items.ENDER_PEARL);
        int obsidianSlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);

        if (pearlSlot == -1) {
            sendDisableMessage("Disabled, no Ender Pearl.");
            return;
        }

        if (obsidianSlot == -1 && bypass.getValue()) {
            sendDisableMessage("Disabled, no Obsidian.");
            return;
        }

        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
            int lastSlot = mc.player.inventory.currentItem;

            if (bypass.getValue() && obsidianSlot != -1) {
                InventoryUtil.switchTo(obsidianSlot);
                placeObsidianUnderPlayer();
                InventoryUtil.switchTo(lastSlot);
            }

            throwPearl(pearlSlot);
            InventoryUtil.switchTo(lastSlot);
            disable();
        });
    }

    private void placeObsidianUnderPlayer() {
        int x = (int) Math.floor(mc.player.posX);
        int z = (int) Math.floor(mc.player.posZ);
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(new BlockPos(x, 0, z), EnumFacing.DOWN, EnumHand.MAIN_HAND, x, -1, z));
    }

    private void throwPearl(int slot) {
        InventoryUtil.switchTo(slot);
        float yaw = mc.player.rotationYaw;
        float pitch = 80.0f;
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, mc.player.onGround));
        mc.playerController.processRightClick(mc.player, mc.world, InventoryUtil.getHand(slot));
    }

    private void sendDisableMessage(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(new TextComponentString(message));
        }
        disable();
    }
}