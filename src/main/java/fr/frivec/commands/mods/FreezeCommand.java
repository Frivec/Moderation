package fr.frivec.commands.mods;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import fr.frivec.Moderation;

public class FreezeCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		
		if(sender instanceof Player) {
			
			final Player player = (Player) sender;
			
			if(player.hasPermission("moderation.freeze")) {
				
				if(args.length > 0) {
					
					final Set<Player> targetList = new HashSet<>();
					
					for(int i = 0; i < args.length; i++)
						
						targetList.add(Bukkit.getPlayer(args[i]));
					
					for(Player target : targetList) {
						
						if(!target.hasMetadata("Freeze")) {
							
							target.setMetadata("Freeze", new FixedMetadataValue(Moderation.getInstance(), true));
							
							target.sendMessage("§bVous êtes à présent gelé sur place.");
							target.sendMessage("§cMerci de ne pas vous déconnecter sous peine de sanction.");
							
							player.sendMessage("§aLe joueur " + target.getName() + " a bien été immobilisé.");
							
							final Location location = target.getLocation();
							
							double i = location.getY() - 1;
							
							Block block = location.getWorld().getBlockAt(new Location(location.getWorld(), location.getX(), i, location.getZ()));
							
							while(block.getType().equals(Material.AIR)) {
								
								i--;
								
								block = location.getWorld().getBlockAt(new Location(location.getWorld(), location.getX(), i, location.getZ()));
								
							}
							
							target.teleport(new Location(block.getWorld(), block.getX(), block.getY() + 1, block.getZ(), location.getYaw(), location.getPitch()));
							
							return true;
							
						}else {
							
							target.removeMetadata("Freeze", Moderation.getInstance());
							
							target.sendMessage("§bVous êtes à présent libre de vos mouvements.");
							target.sendMessage("§aVous pouvez maintenant vous déconnecter sans craindre de sanctions vis-à-vis de l'immobilisation.");
							
							player.sendMessage("§aLe joueur " + target.getName() + " a bien été immobilisé.");
							
						}
						
					}
					
				}
				
			}else {
				
				player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
				return true;
				
			}
			
		}
		
		return false;
	}

}
