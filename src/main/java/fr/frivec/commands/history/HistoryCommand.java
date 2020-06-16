package fr.frivec.commands.history;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.frivec.sanctions.logs.Logs;

public class HistoryCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		
		if(sender instanceof Player) {
			
			final Player player = (Player) sender;
			
			if(player.hasPermission("moderation.history")) {
			
				if(args.length >= 1) {
					
					Logs.openIndexMenu(args[0], player);
					
				}else {
					
					player.sendMessage("§cErreur. Vous devez ajouter le pseudonyme d'un joueur pour afficher son historique.");
					return true;
					
				}
				
			}else {
				
				player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
				return true;
				
			}
			
		}else {
			
			Bukkit.getServer().getConsoleSender().sendMessage("§cErreur. Seuls les joueurs peuvent utiliser cette commande.");
			return true;
			
		}
		
		return false;
	}

}
