package fr.frivec.listeners.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
	
	@EventHandler
	public void onJump(PlayerMoveEvent e) {
		
		final Player player = e.getPlayer();
		
		if(player.hasMetadata("Freeze")) {
		
			if(e.getFrom().getY() != e.getTo().getY() || e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
				
				e.setCancelled(true);
				player.sendMessage("§cInutile de bouger.");
				
			}
			
		}
		
	}

}
