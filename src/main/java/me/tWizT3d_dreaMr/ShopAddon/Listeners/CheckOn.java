package me.tWizT3d_dreaMr.ShopAddon.Listeners;

import com.snowgears.shop.event.PlayerInitializeShopEvent;
import com.snowgears.shop.shop.AbstractShop;
import com.snowgears.shop.shop.ComboShop;
import com.snowgears.shop.shop.ShopType;
import me.tWizT3d_dreaMr.ShopAddon.limits.CreationCheck;
import me.tWizT3d_dreaMr.ShopAddon.limits.MatchType;
import me.tWizT3d_dreaMr.ShopAddon.main;
import me.tWizT3d_dreaMr.ShopAddon.util.Filter;
import me.tWizT3d_dreaMr.ShopAddon.util.Format;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CheckOn implements Listener {

    @EventHandler
    public void onShopCreate(PlayerInitializeShopEvent event) {

        CreationCheck ItemList = main.getCreationCheck();
        AbstractShop shop = event.getShop();

        MatchType type = null;
        if (shop.getType() == ShopType.COMBO) {
            ComboShop cs = (ComboShop) shop;
            type =
                    ItemList.test(
                            event.getPlayer().getInventory().getItemInMainHand(),
                            cs.getPrice(),
                            shop.getAmount(),
                            ShopType.BUY);
            if (type == null)
                type =
                        ItemList.test(
                                event.getPlayer().getInventory().getItemInMainHand(),
                                cs.getPriceSell(),
                                shop.getAmount(),
                                ShopType.SELL);
        } else if (shop.getType() == ShopType.BUY || shop.getType() == ShopType.SELL)
            type =
                    ItemList.test(
                            event.getPlayer().getInventory().getItemInMainHand(),
                            shop.getPrice(),
                            shop.getAmount(),
                            shop.getType());
        if (!(type == null || type.getType().equalsIgnoreCase("WhiteList"))) {

            event.setCancelled(true);
            shop.delete();
            Player player = event.getPlayer();
            if (type.getType().equalsIgnoreCase("BlackList"))
                player.sendMessage(Format.format(main.getCon().getString("Interaction.blacklist")));
            String message =
                    Format.format(
                            main.getCon().getString("Interaction." + type.getType().toLowerCase()));
            Filter f = type.getFilter();
            if (type.getType().equalsIgnoreCase("pricemin")) {
                message = message.replace("%amount%", f.getFriendlyMinAmount());
                message = message.replace("%price%", f.getFriendlyMinPrice());
            }
            if (type.getType().equalsIgnoreCase("pricemax")) {
                message = message.replace("%amount%", f.getFriendlyMaxAmount());
                message = message.replace("%price%", f.getFriendlyMaxPrice());
            }
            message = message.replace("%title%", f.Title());
            player.sendMessage(message);
        }
    }
}
