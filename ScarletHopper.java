package com.scarlet.hopper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ScarletHopper extends JavaPlugin implements CommandExecutor, TabCompleter {

    private static final int MAX_AMOUNT = 4096;
    private static final String PREFIX = "\u00a7c[ScarletHopper] \u00a7f";

    private int amount = 32;
    private boolean mixed = false;
    private final Set<InventoryType> types = EnumSet.noneOf(InventoryType.class);
    private final Set<String> disabledWorlds = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSettings();
        getServer().getPluginManager().registerEvents(new HopperListener(this), this);

        PluginCommand cmd = getCommand("scarlethopper");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
        getLogger().info("ScarletHopper aktif. Hopper memindahkan " + amount + " item per transfer.");
    }

    public void loadSettings() {
        reloadConfig();
        FileConfiguration c = getConfig();

        amount = Math.max(1, Math.min(MAX_AMOUNT, c.getInt("transfer-amount", 32)));
        mixed = c.getBoolean("mixed-items", false);

        types.clear();
        for (String s : c.getStringList("inventory-types")) {
            try {
                types.add(InventoryType.valueOf(s.trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                getLogger().warning("inventory-types tidak dikenal: " + s);
            }
        }
        types.add(InventoryType.HOPPER); // hopper wajib aktif

        disabledWorlds.clear();
        for (String w : c.getStringList("disabled-worlds")) {
            disabledWorlds.add(w.toLowerCase(Locale.ROOT));
        }
    }

    public int getAmount() { return amount; }
    public boolean isMixed() { return mixed; }
    public boolean isSupported(InventoryType t) { return types.contains(t); }
    public boolean isWorldDisabled(String world) { return disabledWorlds.contains(world.toLowerCase(Locale.ROOT)); }

    private void send(CommandSender s, String msg) {
        s.sendMessage(PREFIX + msg);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("scarlethopper.admin")) {
            send(sender, "\u00a7cKamu tidak punya izin.");
            return true;
        }

        String sub = args.length == 0 ? "info" : args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "reload" -> {
                loadSettings();
                send(sender, "Config di-reload. Jumlah per transfer: " + amount);
            }
            case "set" -> {
                if (args.length < 2) {
                    send(sender, "Pakai: /" + label + " set <jumlah 1-" + MAX_AMOUNT + ">");
                    return true;
                }
                try {
                    int n = Integer.parseInt(args[1]);
                    if (n < 1 || n > MAX_AMOUNT) {
                        send(sender, "Jumlah harus antara 1 dan " + MAX_AMOUNT + ".");
                        return true;
                    }
                    getConfig().set("transfer-amount", n);
                    saveConfig();
                    loadSettings();
                    send(sender, "Jumlah per transfer diubah menjadi " + amount);
                } catch (NumberFormatException ex) {
                    send(sender, "Jumlah harus berupa angka.");
                }
            }
            case "info" -> {
                send(sender, "Jumlah per transfer: " + amount + " | mixed-items: " + mixed);
                send(sender, "Inventory aktif: " + types);
            }
            default -> send(sender, "Pakai: /" + label + " <reload|set|info>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            for (String s : List.of("reload", "set", "info")) {
                if (s.startsWith(args[0].toLowerCase(Locale.ROOT))) out.add(s);
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            out.addAll(List.of("1", "8", "16", "32", "64"));
        }
        return out;
    }
}
