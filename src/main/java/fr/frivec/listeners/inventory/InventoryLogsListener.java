package fr.frivec.listeners.inventory;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import fr.frivec.Moderation;
import fr.frivec.items.ItemCreator;
import fr.frivec.sanctions.logs.Logs;
import fr.frivec.utils.Utils;

public class InventoryLogsListener implements Listener {
	
	@EventHandler
	public void onInventoryInteract(InventoryClickEvent e) {
		
		final Inventory inventory = e.getClickedInventory();
		final String title = e.getView().getTitle();
		final Player player = (Player) e.getWhoClicked();
		final ItemStack item = e.getCurrentItem();
		final Moderation main = Moderation.getInstance();
		
		if(inventory != null) {
		
			if(title != null) {
			
				if(title.contains("§aIndex")) {
					
					e.setCancelled(true);
					
					if(item != null) {
						
						final String targetName = title.replaceAll("§aIndex historique: ", "");
						
						for(Logs.Filter filter : Logs.Filter.values())
							
							if(filter.getSanctionType() != null && item.getType().equals(filter.getSanctionType().getMaterial()) && item.getItemMeta().getDisplayName().contains(filter.getSanctionType().getName())) {
									
								Logs.displayLogs(targetName, player, 1, filter);
								return;
							
							}
						
					}else {
						
						player.sendMessage("§cErreur (onInventoryInteract - Item null). Merci de prévenir un développeur ou un administrateur.");
						return;
						
					}
					
				}else if(title.contains("§bHistorique")) {
					
					final String targetName = title.replaceAll("§bHistorique - ", "");
					
					e.setCancelled(true);
					
					if(item != null) {
						
						if(item.getType().equals(Material.ARROW)) {
							
							player.sendMessage("§aRetour à l'index.");
							
							player.closeInventory();
							
							Logs.openIndexMenu(targetName, player);
							
							return;
							
						}else if(item.getType().equals(Material.SKULL_ITEM)) {
							
							player.sendMessage("§cCette fonction n'est pas encore prête. Veuillez patienter.");
							return;
							
						}else if(item.getType().equals(Material.BARRIER) && player.hasPermission("moderation.resethistory")) { //PERMISSION main.resethistory
							
							if(item.getItemMeta().getDisplayName().contains("§cEffacer")) {
								
								inventory.setItem(53, new ItemCreator(Material.BARRIER, 1).setDisplayName("§cConfirmer l'effacement").build());
								
								player.updateInventory();
								
								player.setMetadata("ResetHistory", new FixedMetadataValue(main, true));
								
								Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
									
									@Override
									public void run() {
										
										if(player.hasMetadata("ResetHistory")) {
											
											inventory.setItem(53, new ItemCreator(Material.BARRIER, 1).setDisplayName("§cEffacer l'historique").build());
											
											player.updateInventory();
											player.sendMessage("§cL'effacement de l'historique a été annulé. Raison: trop de temps d'attente.");
											player.removeMetadata("ResetHistory", main);
											
										}
										
										return;
										
									}
								}, 10000l);
								
							}else if(item.getItemMeta().getDisplayName().contains("§cConfirmer")) {
								
								if(player.hasMetadata("ResetHistory")) {
									
									player.sendMessage("§aEffacement de l'historique de " + targetName + " §alancé.");
									
									Logs.resetAllLogs(UUID.fromString(Utils.addUUIDDashes(main.getJson().getUUIDFromMojang(targetName))));
									
									player.sendMessage("§aEffecement terminé.");
									
									player.removeMetadata("ResetHistory", main);
									
									player.closeInventory();
									
								}else {
									
									player.closeInventory();
									player.sendMessage("§cErreur, vous n'êtes pas sensé voir cet item. ");
									return;
									
								}
								
							}
							
						}
						
					}
					
				}
				
			}else
				return;
			
		}else
			return;
		
	}

}
