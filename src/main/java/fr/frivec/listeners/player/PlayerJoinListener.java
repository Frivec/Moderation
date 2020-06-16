package fr.frivec.listeners.player;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import fr.frivec.Moderation;
import fr.frivec.sanctions.abstracts.AbstractSanction;
import fr.frivec.sanctions.abstracts.AbstractSanction.SanctionType;

public class PlayerJoinListener implements Listener {
	
	@EventHandler
	public void onJoin(final AsyncPlayerPreLoginEvent event) {
		
		final UUID uuid = event.getUniqueId(); 
		final long currentTime = Date.from(Instant.now()).getTime();
		
		for(SanctionType types : SanctionType.values()) {
			
			if(AbstractSanction.hasSanction(uuid, types)) {
				
				final AbstractSanction sanction = AbstractSanction.getSanction(uuid, types);
				
				if(sanction.isPermanent()) {
					
					if(types.equals(SanctionType.BAN))
					
						event.disallow(Result.KICK_BANNED, String.join("\n", Arrays.asList("§cVous avez été banni(e) du serveur.", "§bRaison: §a" + sanction.getReason(), 
								sanction.isPermanent() ? "§cCette sanction est permanente" : "§bCette sanction est active jusqu'au: " + new SimpleDateFormat("dd/MM/YY à HH:mm:ss").format(sanction.getEnd()))));
					
					return;
					
				}
				
				if(sanction.getEnd() <= currentTime) {
					
					sanction.deleteFromDatabase();
					sanction.toLog().updateLog();
				
				}else
					
					if(types.equals(SanctionType.BAN))
						
						event.disallow(Result.KICK_BANNED, String.join("\n", Arrays.asList("§cVous avez été banni(e) du serveur.", "§bRaison: §a" + sanction.getReason(), 
								sanction.isPermanent() ? "§cCette sanction est permanente" : "§bCette sanction est active jusqu'au: " + new SimpleDateFormat("dd/MM/YY à HH:mm:ss").format(sanction.getEnd()))));
				
			}
			
		}
		
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		
		final Player player = event.getPlayer();
		
		if(player.hasPermission("moderation.reports.view"))
		
			Moderation.getInstance().getModerators().add(player);
			
	}
	
	@EventHandler
	public void onQuit(final PlayerQuitEvent event) {
		
		final Player player = event.getPlayer();
		
		if(Moderation.getInstance().getModerators().contains(player))
			
			Moderation.getInstance().getModerators().remove(player);
		
	}

}
