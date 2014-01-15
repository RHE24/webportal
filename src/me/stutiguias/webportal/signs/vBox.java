/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.webportal.signs;

import java.util.List;
import me.stutiguias.webportal.init.WebPortal;
import me.stutiguias.webportal.settings.Shop;
import me.stutiguias.webportal.settings.InventoryHandler;
import me.stutiguias.webportal.settings.TradeSystem;
import me.stutiguias.webportal.settings.WebItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Daniel
 */
public class vBox {
    
    WebPortal plugin;
    InventoryHandler inventoryHandler;
    
    public vBox(WebPortal instance) {
        plugin = instance;
    }
    
    public void Open(PlayerInteractEvent event){
        inventoryHandler = new InventoryHandler(plugin,event.getPlayer());
        event.getPlayer().openInventory(inventoryHandler.getInventory());
        event.setCancelled(true);
    }
    
    public void Close(Inventory inventory,Player player) {
        inventoryHandler = new InventoryHandler(plugin,player);
        inventoryHandler.InventoryClose(inventory);
    }
    
    public void AddItem(ItemStack item,Player pl,InventoryClickEvent event) {
              if(event.isRightClick()) {
                  ItemStack newamount = new ItemStack(item);
                  newamount.setAmount(1);
                  new TradeSystem(plugin).ItemtoStore((WebItemStack)newamount, pl);
              }
              if(event.isLeftClick()) {
                  new TradeSystem(plugin).ItemtoStore((WebItemStack)item, pl);
              }
    }
        
        
    public void Delete(InventoryClickEvent event,Player pl) {
        // Delete Item
        List<Shop> auctions = plugin.db.getAuctionsLimitbyPlayer(pl.getName(), 0, 44, plugin.Myitems);
        for(Shop auction:auctions) {
            if(event.getCurrentItem().getTypeId() == auction.getItemStack().getTypeId() && auction.getItemStack().getDurability() == event.getCurrentItem().getDurability()) {
                if(event.isLeftClick()) {
                    if(auction.getItemStack().getAmount() == event.getCurrentItem().getAmount()) {
                        plugin.db.DeleteAuction(auction.getId());
                    }
                    if(auction.getItemStack().getAmount() > event.getCurrentItem().getAmount()) {
                        int total = auction.getItemStack().getAmount() -  event.getCurrentItem().getAmount();
                        plugin.db.updateItemQuantity(total, auction.getId());
                    }
                }else if(event.isRightClick()) {
                    int total;
                    if(event.getCurrentItem().getAmount() <= 1) {
                        total = auction.getItemStack().getAmount() - 1;
                    }else{
                        total = auction.getItemStack().getAmount() - ( event.getCurrentItem().getAmount() / 2 );
                    }
                    if(auction.getItemStack().getAmount() == event.getCurrentItem().getAmount()) {
                        if(total != 0) {
                            plugin.db.updateItemQuantity(total, auction.getId());
                        }
                        if(total == 0) {
                            plugin.db.DeleteAuction(auction.getId());
                        }
                    }else if(auction.getItemStack().getAmount() > event.getCurrentItem().getAmount()) {
                        plugin.db.updateItemQuantity(total, auction.getId());
                    }                                
                }else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
