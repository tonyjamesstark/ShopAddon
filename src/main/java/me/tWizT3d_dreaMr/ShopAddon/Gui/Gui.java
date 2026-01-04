package me.tWizT3d_dreaMr.ShopAddon.Gui;

import com.snowgears.shop.Shop;
import com.snowgears.shop.shop.AbstractShop;
import com.snowgears.shop.util.CurrencyType;
import me.tWizT3d_dreaMr.ShopAddon.main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Gui {
    private Player p;
    private AbstractShop shop;
    private String name;
    private Inventory inv;

    public Gui(Player player, AbstractShop aShop) {
        this.p = player;
        this.shop = aShop;
        this.inv = openGui();
        p.openInventory(inv);
    }

    private Inventory openGui() {
        Shop.getPlugin().getCurrencyType();
        Location l = shop.getSignLocation();
        this.name = "Shop at " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ();
        Inventory inventory = Bukkit.createInventory(null, InventoryType.CHEST, name);
        for (int i = 0; i < 27; i++) {
            if (i == 10 || i == 11 || i == 12) {
                inventory.setItem(i, getItems(i));
            } else if (i == 14 || i == 15 || i == 16) {
                inventory.setItem(i, getPrice(i));
            } else if (i == 26 || i == 0 || i == 18 || i == 9 || i == 0 || i == 17 || i == 8) {
                ItemStack item = new ItemStack(main.Confirm);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(
                        ChatColor.GREEN + "Confirm " + shop.getType().name().toLowerCase());
                item.setItemMeta(meta);
                inventory.setItem(i, item);
            } else if (i == 13) {
                inventory = set13(inventory);
            } else {
                inventory.setItem(i, new ItemStack(main.Default));
            }
        }

        return inventory;
    }

    public boolean is(InventoryView test) {
        return test.getTopInventory().getHolder() == null && test.getTitle().equals(name);
    }

    public void update() {
        inv = set13(inv);
    }

    private Inventory set13(Inventory inventory) {
        int amount = shop.getStock();
        ItemStack i;
        if (amount > 64) i = new ItemStack(main.TransAmount, 64);
        else if (amount == 0) i = new ItemStack(main.TransOut, 1);
        else i = new ItemStack(main.TransAmount, amount);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(
                ChatColor.AQUA
                        + "This shop has "
                        + ChatColor.WHITE
                        + amount
                        + ChatColor.AQUA
                        + " transactions left.");
        i.setItemMeta(meta);
        inventory.setItem(13, i);
        return inventory;
    }

    private ItemStack getItems(int i) {
        ItemStack item = new ItemStack(main.NotStock);

        if (shop.getAmount() < 64) {
            if (i == 10 || i == 11) return item;
            return shop.getItemStack();
        }
        if (shop.getAmount() < 129) {
            if (i == 10) return item;
            if (i == 11) {
                item = shop.getItemStack();
                item.setAmount(64);
                return item;
            }
            if (i == 12) {
                item = shop.getItemStack();
                item.setAmount(shop.getAmount() - 64);
                return item;
            }
        }
        if (shop.getAmount() >= 129) {
            if (i == 10 || i == 11) {
                item = shop.getItemStack();
                item.setAmount(64);
                return item;
            }
            if (i == 12) {
                item = shop.getItemStack();
                int left = shop.getAmount() - 128;
                if (left > 64) left = 64;
                item.setAmount(left);
                return item;
            }
        }
        return item;
    }

    private ItemStack getPrice(int i) {
        ItemStack defItem = new ItemStack(main.NotMoney);
        ItemStack item = defItem;
        CurrencyType type = Guis.getCurrencyType();
        if (type == CurrencyType.ITEM) {
            item = new ItemStack(Shop.getPlugin().getItemCurrency().getType());
            if (shop.getPrice() < 65) {
                if (i == 15 || i == 16) return defItem;
                item.setAmount((int) shop.getPrice());
                return item;
            }
            if (shop.getPrice() < 129) {
                if (i == 16) return defItem;
                if (i == 14) {
                    item.setAmount(64);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.GREEN + shop.getPriceString());
                    item.setItemMeta(meta);
                    return item;
                }
                if (i == 15) {
                    item.setAmount((int) shop.getPrice() - 64);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.GREEN + shop.getPriceString());
                    item.setItemMeta(meta);
                    return item;
                }
            }
            if (shop.getPrice() >= 129) {
                if (i == 14 || i == 15) {
                    item.setAmount(64);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.GREEN + shop.getPriceString());
                    item.setItemMeta(meta);
                    return item;
                }
                if (i == 16) {
                    int left = (int) shop.getPrice() - 128;
                    if (left > 64) left = 64;
                    item.setAmount(left);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.GREEN + shop.getPriceString());
                    item.setItemMeta(meta);
                    return item;
                }
            }
        }
        if (type == CurrencyType.EXPERIENCE) {
            if (i == 15 || i == 16) return defItem;
            item = new ItemStack(Material.EXPERIENCE_BOTTLE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + shop.getPriceString());
            item.setItemMeta(meta);
            return item;
        }
        if (type == CurrencyType.VAULT) {
            if (i == 15 || i == 16) return defItem;
            item = new ItemStack(Material.GOLD_INGOT);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + shop.getPriceString());
            item.setItemMeta(meta);
            return item;
        }
        return item;
    }

    public AbstractShop getShop() {
        return shop;
    }
}
