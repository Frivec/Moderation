 package fr.frivec.sanctions;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.frivec.sanctions.abstracts.AbstractSanction;

public class Kick extends AbstractSanction {
	
	public Kick(final UUID moderator, final UUID victim, final String reason) {
		
		super(SanctionType.KICK, moderator, victim, reason, false, Date.from(Instant.now()));
		
	}
	
	@Override
	public void applySanction() {
		
		final Player player = Bukkit.getPlayer(this.victim);
		
		if(player != null && player.isOnline())
			
			player.kickPlayer(String.join("\n", Arrays.asList("§cVous avez été éjecté(e) du serveur.", "§bRaison: §a" + this.reason)));
		
	}

}
