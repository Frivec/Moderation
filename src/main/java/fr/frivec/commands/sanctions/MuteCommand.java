package fr.frivec.commands.sanctions;

import java.sql.Date;
import java.time.Instant;
import java.util.Calendar;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.frivec.Moderation;
import fr.frivec.sanctions.Mute;
import fr.frivec.sanctions.abstracts.AbstractSanction;
import fr.frivec.sanctions.abstracts.AbstractSanction.SanctionType;
import fr.frivec.sanctions.abstracts.AbstractSanction.TimeUnit;
import fr.frivec.utils.Utils;

public class MuteCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		
		if(sender instanceof Player) {
			
			final Player player = (Player) sender;
			
			if(player.hasPermission("moderation.mute")) {
				
				if(args.length > 0) {
					
					/*
					 * 
					 * Récupération des informations de la sanction
					 * 
					 */
					
					//Informations de la sanction
					
					boolean permanent = false;
					long end = Date.from(Instant.now()).getTime();
					
					final String targetName = args[0];
					final Player target = Bukkit.getPlayer(targetName);
					UUID targetUUID;
					
					final StringBuilder reason = new StringBuilder();
					
					
					for(String str : args) //Vérif de l'argument "permanent"
						
						if(str.equalsIgnoreCase("permanent"))
							
							permanent = true;
					
					//Récupération de l'UUID
					
					if(target == null) {
						
						//Recupération de l'uuid vial les serveurs de mojang
						final String baseUUID = Moderation.getInstance().getJson().getUUIDFromMojang(targetName);
					
						if(baseUUID == null || baseUUID.equalsIgnoreCase("error") || baseUUID.equalsIgnoreCase("invalid name")) {
							
							player.sendMessage("§cErreur: l'UUID du joueur demandé n'existe pas ou n'a pas été trouvée. Merci de vérifier le pseudonyme entré.");
							return true;
							
						}
						
						targetUUID = UUID.fromString(Utils.addUUIDDashes(baseUUID));
						
					}else
						targetUUID = target.getUniqueId(); //Récupération de l'UUID du joueur depuis le Player
					
					
					//Vérification de l'existence du joueur
					
					if(targetUUID == null) { //Le joueur n'existe pas
						
						player.sendMessage("§cErreur: le joueur demandé n'existe pas. Merci de vérifier le pseudonyme entré.");
						return true;
						
					}
					
					//Check si le joueur est déjà banni
					
					if(AbstractSanction.hasSanction(targetUUID, SanctionType.MUTE)) {
						
						player.sendMessage("§c" + targetName + " est déjà sous silence.");
						return true;
						
					}
					
					/*
					 * 
					 * Application de la sanction
					 * 
					 */
					
					if(permanent) {					//Sanction permanente
						
						if(args.length >= 3) {
							
							for(int i = 2; i < args.length; i++)
								
								reason.append(" " + args[i]);
							
						}else {
							
							player.sendMessage("§cErreur: il manque des arguments. La commande ne peut pas s'exécuter.");
							player.sendMessage("§aExemple:");
							player.sendMessage("§b/mute Frivec permanent Insultes");
							
							return true;
							
						}
						
					}else { 			//Sanction non permanente
						
						if(args.length >= 4) {
							
							final TimeUnit unit = TimeUnit.getTimeUnitBySympbole(args[2]); //Unité de temps donnée par le modo
							
							try {
								
								final int time = Integer.parseInt(args[1]); //Valeur numérique donnée par le modo
								
								//Vérif de l'existence de l'unité donnée
								if(unit == null) {
									
									player.sendMessage("§cErreur: l'unité de temps n'a pas été reconnue.");
									return false;
									
								}
								
								//Verif si le temps donné est positif
								if(time <= 0) {
									
									player.sendMessage("§CErreur: le temps est inférieur ou égal à 0.");
									return false;
									
								}
								
								//Récupération de la raison
								for(int i = 3; i < args.length; i++)
									
									reason.append(" " + args[i]);
								
								//Définition du temps de fin de la sanction
								final Calendar calendar = Calendar.getInstance();
								calendar.set(unit.getCalendar(), calendar.get(unit.getCalendar()) + time);
								
								end = calendar.getTimeInMillis();
								
							}catch (NumberFormatException e) {
								
								//Le temps donné n'est pas entier
								player.sendMessage("§cErreur: le temps entré n'est pas un nombre entier.");
								return false;
								
							}
							
						}else {
							
							player.sendMessage("§cErreur: il manque des arguments. La commande ne peut pas s'exécuter.");
							player.sendMessage("§aExemple:");
							player.sendMessage("§b/mute Frivec 5 d Insultes");
							
							return true;
							
						}
						
					}
					
					/*
					 * 
					 * Envoi de la sanction
					 * 
					 * 
					 */
										
					final Mute mute = new Mute(player.getUniqueId(), targetUUID, reason.toString(), permanent, end);
					
					mute.toLog().sendInDatabase();
					
					mute.sendInDatabase();
					mute.applySanction();
					
					player.sendMessage("§aLa sanction a été envoyée. " + targetName + " est maintenant sous silence !");
					
					return true;
					
				}else {
					
					player.sendMessage("§cErreur: il manque des arguments. La commande ne peut pas s'exécuter.");
					player.sendMessage("§aExemples:");
					player.sendMessage("§b/mute Frivec permanent Cheat(FF)");
					player.sendMessage("§b/mute Frivec 15 d Cheat(FF)");
					
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
