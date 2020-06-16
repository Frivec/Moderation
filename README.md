**Moderation Plugin**

**__Features:__**

This plugins contains the basics of a moderation plugin:
  - Ban command
  - Mute command
  - Kick command
  - unmute and unban commands
  - freeze command
  - an history of the sanctions of a player
  - a report by using a command (no GUI)
  - a command to slow/clear the chat

**__Permissions:__**
  - moderation.history
  - moderation.ban
  - moderation.kick
  - moderation.mute
  - moderation.unban
  - moderation.freeze
  - moderation.mod
  - moderation.reports.view
  - moderation.purge
  - moderation.slowchat
  - moderation.unmute
  - moderation.resethistory
  
**__Librairies:__**

This project use Gradle to manage all the librairies. 

So, the librairies are:
  - Spigot-API (no need for NMS)
  - HikariCP (not usefull for little projects. I recommend you to use the JDBC lib of Java if your project doesn't need too much connections to the database)
  - Apache Commons Codec
  - Apache Commons IO
