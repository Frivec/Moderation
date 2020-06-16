package fr.frivec.commands.sanctions;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.frivec.Moderation;
import fr.frivec.sanctions.abstracts.AbstractSanction;
import fr.frivec.sanctions.abstracts.AbstractSanction.SanctionType;
import fr.frivec.utils.Utils;

public class UnBanCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		
		if(sender instanceof Player) {
			
			final Player player = (Player) sender;
			
			if(!player.hasPermission("moderation.unban")) {
				
				player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
				
				return true;
			}
			
		}
			
		if(args.length == 1) {
					
			final UUID targetUUID = UUID.fromString(Utils.addUUIDDashes(Moderation.getInstance().getJson().getUUIDFromMojang(args[0])));
					
			if(targetUUID == null) {
						
				sender.sendMessage("§cUne erreur est survenue.");
				return true;
				
			}
			
			if(!AbstractSanction.hasSanction(targetUUID, SanctionType.BAN)) {
				
				sender.sendMessage("§cCe joueur n'est pas banni.");
				return true;
				
			}
			
			final AbstractSanction abstractSanction = AbstractSanction.getSanction(targetUUID, SanctionType.BAN);
			
			abstractSanction.deleteFromDatabase();
			abstractSanction.toLog().updateLog();
			
			Moderation.getInstance().getSanctions().remove(abstractSanction);
			
			sender.sendMessage("§aLe joueur " + args[0] + " peut maintenant se reconnecter sur le serveur.");
			
			return true;
					
		}else {
					
			sender.sendMessage("§cErreur: il manque des arguments ou il y en a trop. La commande ne peut pas s'exécuter.");
			sender.sendMessage("§cExemple: /unban Frivec");
			return true;
					
		}
		
	}

}
