package fr.frivec.sanctions;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.frivec.sanctions.abstracts.AbstractSanction;

public class Ban extends AbstractSanction {
	
	public Ban(final UUID moderator, final UUID victim, final String reason, final boolean permanent, final Date end) {
		
		super(SanctionType.BAN, moderator, victim, reason, permanent, end);
		
	}
	
	public Ban(final UUID moderator, final UUID victim, final String reason, final boolean permanent, final long end) {
		
		super(SanctionType.BAN, moderator, victim, reason, permanent, end);
		
	}
	
	@Override
	public void applySanction() {
		
		final Player player = Bukkit.getPlayer(this.victim);
		
		if(player != null && player.isOnline()) {
			
			player.kickPlayer(String.join("\n", Arrays.asList("§cVous avez été banni(e) du serveur.", "§bRaison: §a" + this.reason, 
					this.permanent ? "§cCette sanction est permanente" : "§bCette sanction est active jusqu'au: " + new SimpleDateFormat("dd/MM/YY à HH:mm:ss").format(this.end))));
			
		}
		
	}

}
