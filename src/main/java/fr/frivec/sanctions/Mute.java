package fr.frivec.sanctions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.frivec.sanctions.abstracts.AbstractSanction;

public class Mute extends AbstractSanction {
	
	public Mute(final UUID moderator, final UUID victim, final String reason, final boolean permanent, final Date end) {
		
		super(SanctionType.MUTE, moderator, victim, reason, permanent, end);
		
	}

	public Mute(final UUID moderator, final UUID victim, final String reason, final boolean permanent, final long end) {
		
		super(SanctionType.MUTE, moderator, victim, reason, permanent, end);
		
	}
	
	@Override
	public void applySanction() {
		
		final Player player = Bukkit.getPlayer(this.victim);
		
		if(player != null && player.isOnline()) {
			
			player.sendMessage("§cVous êtes à présent sous silence.");
			player.sendMessage("§cVous ne pouvez plus utiliser le chat général.");
			player.sendMessage("");
			
			if(this.permanent)
				
				player.sendMessage("§bCette sanction est permanente.");
				
			else
			
				player.sendMessage("§bVotre sanction prendra fin le: " + new SimpleDateFormat("dd/MM/YY à HH:mm:ss").format(this.end));
			
		}
		
	}

}
