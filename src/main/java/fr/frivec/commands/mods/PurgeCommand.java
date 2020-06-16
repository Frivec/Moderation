package fr.frivec.commands.mods;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PurgeCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		
		if(sender instanceof Player) {
			
			final Player player = (Player) sender;
			
			if(player.hasPermission("moderation.purge")) {
				
				for(int i = 0; i < 30; i++)
					
					for(Player players : Bukkit.getOnlinePlayers())
						
						players.sendMessage("");
				
				player.getServer().broadcastMessage("§aLe salon de discussion a été nettoyé par un §6responsable de modération§a.");
				
				return true;
				
			}else {
				
				player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
				return true;
					
			}
			
		}else {
			
			
			
		}
		
		return false;
	}

}
