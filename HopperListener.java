package com.scarlet.hopper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class HopperListener implements Listener {

    private final ScarletHopper plugin;

    public HopperListener(ScarletHopper plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(InventoryMoveItemEvent e) {
        Inventory hopper = e.getInitiator();
        if (hopper.getType() != InventoryType.HOPPER) return;

        int max = plugin.getAmount();
        if (max <= 1) return; // sama dengan vanilla

        Inventory from = e.getSource();
        Inventory to = e.getDestination();
        if (!plugin.isSupported(from.getType()) || !plugin.isSupported(to.getType())) return;

        Location loc = hopper.getLocation();
        if (loc != null && loc.getWorld() != null && plugin.isWorldDisabled(loc.getWorld().getName())) return;

        ItemStack proto = e.getItem();
        if (isShulker(proto.getType()) && to.getType() == InventoryType.SHULKER_BOX) return; // aturan vanilla

        // Batalkan transfer 1 item bawaan, ganti dengan transfer manual sebanyak "max"
        e.setCancelled(true);
        move(from, to, plugin.isMixed() ? null : proto, max);
    }

    private int move(Inventory from, Inventory to, ItemStack proto, int max) {
        int moved = 0;
        int size = from.getSize();

        for (int i = 0; i < size && moved < max; i++) {
            ItemStack s = from.getItem(i);
            if (s == null || s.getType().isAir()) continue;
            if (proto != null && !s.isSimilar(proto)) continue;
            if (isShulker(s.getType()) && to.getType() == InventoryType.SHULKER_BOX) continue;

            int want = Math.min(s.getAmount(), max - moved);
            ItemStack part = s.clone();
            part.setAmount(want);

            Map<Integer, ItemStack> left = to.addItem(part);
            int leftover = left.isEmpty() ? 0 : left.get(0).getAmount();
            int done = want - leftover;

            if (done > 0) {
                int remain = s.getAmount() - done;
                if (remain <= 0) {
                    from.setItem(i, null);
                } else {
                    ItemStack r = s.clone();
                    r.setAmount(remain);
                    from.setItem(i, r);
                }
                moved += done;
            }

            // tujuan penuh untuk jenis item ini -> berhenti (mode sejenis)
            if (leftover > 0 && proto != null) break;
        }
        return moved;
    }

    private static boolean isShulker(Material m) {
        return m.name().endsWith("SHULKER_BOX");
    }
}
