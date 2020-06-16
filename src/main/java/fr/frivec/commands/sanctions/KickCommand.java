package fr.frivec.commands.sanctions;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.frivec.Moderation;
import fr.frivec.sanctions.Kick;
import fr.frivec.utils.Utils;

public class KickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		
		if(sender instanceof Player) {
			
			final Player player = (Player) sender;
			
			if(player.hasPermission("moderation.kick")) {
				
				if(args.length >= 2) {
					
					final String targetName = args[0];
					final Player target = Bukkit.getPlayer(targetName);
					
					final StringBuilder reason = new StringBuilder();
					
					for(int i = 1; i < args.length; i++)
						
						reason.append(args[i]);
					
					UUID targetUUID;
					
					if(target != null)
						
						targetUUID = target.getUniqueId();
					
					else {
						
						final String baseUUID = Moderation.getInstance().getJson().getUUIDFromMojang(targetName);
						
						if(baseUUID == null) {
							
							player.sendMessage("§cAucun joueur n'a été trouvé avec ce pseudonyme. Vérifiez le pseudonyme.");
							player.sendMessage("§bFais gaffe aux underscore, c'est souvent trompeur :p");
							return true;
							
						}else
							
							targetUUID = UUID.fromString(Utils.addUUIDDashes(baseUUID));
						
					}
					
					final Kick kick = new Kick(player.getUniqueId(), targetUUID, reason.toString());
					kick.toLog().sendInDatabase();
					kick.applySanction();
					
					player.sendMessage("§aLa sanction a été envoyée. Le joueur va être kick !");
					
					return true;
					
				}else {
					
					player.sendMessage("§cErreur: il manque des arguments. La commande ne peut pas s'exécuter.");
					player.sendMessage("§cExemple: /kick Frivec Joue au diabolo");
					
					return true;
					
				}
				
			}else {
				
				player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
				
				return true;
				
			}
			
		}
		
		return false;
	}

}
