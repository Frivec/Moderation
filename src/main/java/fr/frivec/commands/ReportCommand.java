package fr.frivec.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.frivec.Moderation;

public class ReportCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		
		if(sender instanceof Player) {
			
			final Player player = (Player) sender;
			
			if(args.length >= 2) {
				
				final Player target = Bukkit.getPlayer(args[0]);
				
				if(target != null) {
					
					if(!target.isOnline()) {
						
						player.sendMessage("§cErreur. Ce joueur n'est pas connecté sur le même serveur que vous.");
						player.sendMessage("§cAssurez-vous d'être sur le même serveur que la personne que vous voulez signaler.");
						return true;
						
					}
					
					final StringBuilder reason = new StringBuilder();
					
					for(int i = 1; i < args.length; i++)
						
						reason.append(" " + args[i]);
					
					for(Player mods : Moderation.getInstance().getModerators())
						
						mods.sendMessage("§b[Signalement] §a" + player.getName() + " §3signale §c" + target.getName() + " §3pour: §6" + reason.toString());
					
					player.sendMessage("§aVotre signalement a été envoyé avec succès ! Si un modérateur est disponible, ce dernier ira vérifier le joueur signalé.");
					
					return true;
					
				}else {
					
					player.sendMessage("§cErreur. Ce joueur n'existe pas ou n'est pas connecté.");
					return true;
					
				}
				
			}else {
				
				player.sendMessage("§cErreur. Vous devez renseigner le pseudonyme d'un joueur ainsi qu'une raison.");
				return true;
				
			}
			
		}else {
			
			sender.sendMessage("§cErreur. Vous ne pouvez pas utiliser cette commande en n'étant pas un joueur.");
			return true;
			
		}
		
	}

}
