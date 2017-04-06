package org.hqpots.bolt.selector;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.hqpots.bolt.listeners.ItemStackBuilder;


public class ServerSelector implements Listener
{
    static List<String> serverSelectorLore = new ArrayList<>();
    static List<String> hardcoreFactionsLore = new ArrayList<>();
    static List<String> developmentLore = new ArrayList<>();
    static List<String> kitsLore = new ArrayList<>();
    static List<String> practiceLore = new ArrayList<>();
    static List<String> kohisgLore = new ArrayList<>();
    private static ItemStack somethhing = new ItemStack(351, 1, (short)8);
    
    static {
        serverSelectorLore.add(ChatColor.translateAlternateColorCodes('&', "&d&lRIGHT CLICK &eto play a server"));

        hardcoreFactionsLore.add("");
        hardcoreFactionsLore.add(ChatColor.translateAlternateColorCodes('&', "&7DTR, Deathban, and Hardcore"));
        hardcoreFactionsLore.add(ChatColor.translateAlternateColorCodes('&', "&7Map started on: 7th January"));
        hardcoreFactionsLore.add("");
        hardcoreFactionsLore.add(ChatColor.translateAlternateColorCodes('&', "&a[Click to connect]"));


        kohisgLore.add("");
        kohisgLore.add(ChatColor.translateAlternateColorCodes('&', "&cDevelopment"));
        kohisgLore.add("");
        kohisgLore.add(ChatColor.translateAlternateColorCodes('&', "&a[Click to connect]"));
        
        developmentLore.add(ChatColor.translateAlternateColorCodes('&', "&7&m-------------------------------------------"));
        developmentLore.add(ChatColor.translateAlternateColorCodes('&', "&eOnline Players &d»"));
        developmentLore.add(ChatColor.translateAlternateColorCodes('&', ""));
        developmentLore.add(ChatColor.translateAlternateColorCodes('&', "&d&lRIGHT CLICK&e to join queue"));
        developmentLore.add(ChatColor.translateAlternateColorCodes('&', "&7&m-------------------------------------------"));

        kitsLore.add("");
        kitsLore.add(ChatColor.translateAlternateColorCodes('&', "&7DTR, Non-Deathban, and Hardcore"));
        kitsLore.add(ChatColor.translateAlternateColorCodes('&', "&7Map started on: 22th December"));
        kitsLore.add("");
        kitsLore.add(ChatColor.translateAlternateColorCodes('&', "&a[Click to connect]"));
    }

    public static ItemStack selector = ItemStackBuilder.get(Material.COMPASS, 1, (short)0, "&dServer Selector", serverSelectorLore);
    public static ItemStack ddd = ItemStackBuilder.get(Material.COMPASS, 1, (short)0, "&dLobbies", developmentLore);
    public static ItemStack factions = ItemStackBuilder.get(Material.INK_SACK, 1, (short)9, "&dHardcore Factions", hardcoreFactionsLore);
    public static ItemStack kitmap = ItemStackBuilder.get(Material.INK_SACK, 1, (short)5, "&dKitMap", kitsLore);
    public static ItemStack kohiSG = ItemStackBuilder.get(Material.INK_SACK, 1, (short)12, "&dKohiSG", kohisgLore);
    public static Inventory inv = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', "&dServer Selector"));

    public ServerSelector()
    {
        inv.setItem(4, factions);
        inv.setItem(2, kitmap);
        inv.setItem(6, kohiSG);
    }

    /*
        Bukkit.dispatchCommand(p, "play hcf");
        Bukkit.dispatchCommand(p, "play kitmap");
        Bukkit.dispatchCommand(p, "play kohisg");
     */

    @EventHandler
    public void InventoryClick(InventoryClickEvent e)
    {
        e.setCancelled(true);
        if ((!(e.getWhoClicked() instanceof Player)) || (e.getCurrentItem() == null)) {
            return;
        }
        if (e.getInventory().getType().equals(InventoryType.PLAYER)) {
            e.setCancelled(false);
        }
        Player p = (Player)e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if ((item.getType().equals(ddd)) && (e.getSlot() + 1 == item.getAmount()))
        {
            PlayerEvents.sendPlayerToServer(p, "dev");
            p.closeInventory();
        }
        else if (item.isSimilar(factions))
        {
        	p.sendMessage("");
            PlayerEvents.sendPlayerToServer(p, "hcf");
            p.sendMessage("");
            p.closeInventory();
        }
        else if (item.isSimilar(kitmap)) {
        	PlayerEvents.sendPlayerToServer(p, "kitmap");
        	p.closeInventory();
        } else if(item.isSimilar(kohiSG)) {
            PlayerEvents.sendPlayerToServer(p, "kohisg");
            p.closeInventory();
        }
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
        if ((!e.getAction().equals(Action.PHYSICAL)) && (e.getItem() != null) && (e.getItem().isSimilar(selector)))
        {
            Player p = e.getPlayer();
            p.openInventory(inv);
            e.setCancelled(true);
        }
    }
}
