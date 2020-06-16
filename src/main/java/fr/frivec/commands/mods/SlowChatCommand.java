package fr.frivec.commands.mods;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.frivec.Moderation;

public class SlowChatCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		
		if(sender instanceof Player) {
			
			final Player player = (Player) sender;
			
			if(player.hasPermission("moderation.slowchat")) {
			
				if(args.length >= 1) {
					
					try {
						
						final int newCooldown = Integer.parseInt(args[0]);
						
						if(newCooldown >= 0 && newCooldown <= 15) {
							
							if(newCooldown == 0) {
								
								player.sendMessage("§aVous avez désactivé le ralentissement du chat !");
								player.getServer().broadcastMessage("§aLe chat repasse à une vitesse normale ! N'est-ce pas fantastique ?");
								
							}
							
							if(newCooldown > Moderation.getInstance().getCooldown()) {
								
								player.sendMessage("§bLe chat est maintenant ralenti. Les joueurs ne pourront poster un message que toutes les §6" + newCooldown + " §bsecondes.");
								player.getServer().broadcastMessage("§4Le chat est maintenant sur voie lente ! Pas plus d'un message toutes les §6" + newCooldown + " §4secondes ! §bPrenez votre temps.");
								
							}else if(newCooldown < Moderation.getInstance().getCooldown()) {
								
								player.sendMessage("§bLe chat est plus rapide. Les joueurs ne pourront poster un message que toutes les §6" + newCooldown + " §bsecondes.");
								player.getServer().broadcastMessage("§4Le chat devient plus rapide, mais le ralentissement n'est pas désactivé. Pas plus d'un message toutes les §6" + newCooldown + " §4secondes ! §bPrenez votre temps.");
								
							}
							
							Moderation.getInstance().setCooldown(newCooldown);
							
						}else {
							
							player.sendMessage("§cErreur: la valeur entrée n'est pas comprise entre 0 et 15.");
							player.sendMessage("§aC'est pas pratique une veleur négative, ou devoir envoyer un message toutes les minutes :p");
							
							return true;
							
						}
						
					}catch (NumberFormatException e) {
						
						player.sendMessage("§cErreur: la valeur entrée n'est pas un nombre.");
						player.sendMessage("§aExemple: /slowchat 60");
						
						return true;
						
					}
					
				}else {
					
					player.sendMessage("§cErreur: il manque des arguments. La commande ne peut pas s'exécuter.");
					player.sendMessage("§cExemple: /slowchat <secondes>");
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
