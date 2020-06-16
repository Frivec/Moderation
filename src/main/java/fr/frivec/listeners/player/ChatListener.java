package fr.frivec.listeners.player;

import java.time.Instant;
import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.frivec.sanctions.Mute;
import fr.frivec.sanctions.abstracts.AbstractSanction;
import fr.frivec.sanctions.abstracts.AbstractSanction.SanctionType;

public class ChatListener implements Listener {
	
	@EventHandler
	public void onChat(final AsyncPlayerChatEvent event) {
		
		final Player player = event.getPlayer();
		
		if(AbstractSanction.hasSanction(player.getUniqueId(), SanctionType.MUTE)) {
			
			final Mute mute = (Mute) AbstractSanction.getSanction(player.getUniqueId(), SanctionType.MUTE);
			
			if(mute.isPermanent() || mute.getEnd() > Date.from(Instant.now()).getTime()) {
				
				mute.applySanction();
				event.setCancelled(true);
				
			}else {
				
				mute.deleteFromDatabase();
				mute.toLog().updateLog();
				
			}
				
		}
		
	}

}
