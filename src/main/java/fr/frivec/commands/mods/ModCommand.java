package fr.frivec.commands.mods;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		
		if(sender instanceof Player) {
			
			final Player player = (Player) sender;
			
			if(player.hasPermission("moderation.modbroadcast")) {
				
				if(args.length > 0) {
					
					final StringBuilder builder = new StringBuilder();
					
					for(int i = 0; i < args.length; i++)
						
						builder.append(" " + args[i]);
					
					Bukkit.broadcastMessage("§6§l[§9§lModération§6§l] §c" + player.getName() + " §7>> §a" + builder.toString());
					
					return true;
					
				}else {
					
					player.sendMessage("§cErreur. Vous devez indiquer un message à envoyer aux joueurs.");
					return true;
					
				}
								
			}else {
				
				player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");				
				return true;
				
			}
			
		}else {
			
			sender.sendMessage("§cErreur. Cette commande ne peut être exécutée que par un joueur.");
			return true;
			
		}
		
	}

}
