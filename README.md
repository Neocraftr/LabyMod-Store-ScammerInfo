# LabyMod\-Scammerliste

Scammerliste für GrieferGames und ähnliche Server.

### Funktionen
- Speicherung der UUID von Spielern (Scammer beliben auch nach Nemansänderung auf der Liste)
- Automatische Aktualisierung der Spielernamen
- Markierung von Scammern im Chat, in Clans und per MSg
- Befehl zum hinzufügen / entfernen ganzer Clans
- Befehl um Namensänderungen seit der letzten Aktualisierung aufzulisten.
- Integration der öffentlichen Scammerliste des [\[SCAMMER\] Radar](https://scammer-radar.de/) Projekts

### Verfügbare Befehle:
- **.scammer add \<Name\>** - Fügt einen Spieler zur Scammerliste hinzu.
- **.scammer remove \<Name\>** - Entfernt einen Spieler von der Scammerliste.
- **.scammer addclan \<Name|ClanTag\>** - Fügt die Spieler eines Clans zur Scammerliste hinzu.
- **.scammer removeclan \<Name|ClanTag\>** - Entfernt die Spieler eines Clans von der Scammerliste.
- **.scammer check \<Name\>** - Überprüft ob sich ein Spieler auf der Scammerliste befindet.
- **.scammer clear** - Entfernt alle Spieler von der Scammerliste.
- **.scammer list** - Zeige alle Spieler auf der Scammerliste.
- **.scammer update** - Aktualisiert die Namen der Spieler.
- **.scammer namechanges** - Zeigt die Namensänderungen der letzten Aktualisierung an.

Anstatt **.scammer** kann auch der Befehl **.sc** verwendet werden.

### Andere Server als GrieferGames
Folgende Funktionen sind ausschließlich auf GrieferGames (oder Servern mit gleicher Chatformatierung) nutzbar:
- Markierung von Scammern im Chat, in Clans und per MSG
- Hinzufügen und Entfernen von Clans

### Installation
Download: https://github.com/Neocraftr/LabyMod-Scammerliste/releases

Verschiebe die Datei in den Addons Ordner von LabyMod:
|Betriebssystem |Dateipfad                                                 |
|---------------|----------------------------------------------------------|
|Windows        |%APPDATA%\\.minecraft\\LabyMod\\addons-1.8                |
|Linux          |~/.minecraft/LabyMod/addons-1.8                           |
|macOS          |~/Library/Application Support/minecraft/LabyMod/addons-1.8|
