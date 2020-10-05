**Moderation Plugin**


[![CC BY-NC-SA 4.0][cc-by-nc-sa-shield]][cc-by-nc-sa]

**This work is licensed under a** [Attribution-NonCommercial-ShareAlike 4.0 International License][cc-by-nc-sa].

[![CC BY-NC-SA 4.0][cc-by-nc-sa-image]][cc-by-nc-sa]

[cc-by-nc-sa]: http://creativecommons.org/licenses/by-nc-sa/4.0/
[cc-by-nc-sa-image]: https://licensebuttons.net/l/by-nc-sa/4.0/88x31.png
[cc-by-nc-sa-shield]: https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-lightgrey.svg

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
