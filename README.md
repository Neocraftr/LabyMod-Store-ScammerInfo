# LabyMod\-Scammerliste

Scammerliste für GrieferGames und ähnliche Server.

Spielversion: **LabyMod 1.8.9 Vanilla/Forge**

### Funktionen
- Speicherung der UUID von Spielern (Scammer bleiben auch nach Namensänderung auf der Liste)
- Unterstützung für Pocket Edition Spieler (**Achtung: Spieler werden nach Namensänderungen nicht mehr angezeigt**)
- Automatische Aktualisierung der Spielernamen
- Markierung von Scammern im Chat, in Clans, in Direktnachrichten und bei Startkicks
- Befehl zum hinzufügen / entfernen ganzer Clans
- Befehl um alle Scammer auf dem CityBuild anzuzeigen
- Anzeige des Datums wann ein Spieler hinzugefügt wurde
- Möglichkeit einen eigenen Grund zu jedem hinzugefügtem Spieler anzugeben
- Möglichkeit eigene\* oder öffentliche Listen hinzuzufügen z.B. die des [\[SCAMMER\] Radar](https://scammer-radar.de/) Projekt

\* Auch Dateipfade können in der URL Zeile angegeben werden. Bsp: _file://C:\Pfad\zu\meiner\Liste.json_

### Installation
Download: https://github.com/Neocraftr/LabyMod-Scammerliste/releases/latest

LabyMod Addons Ordner:
|Betriebssystem |Dateipfad                                                 |
|---------------|----------------------------------------------------------|
|Windows        |%APPDATA%\\.minecraft\\LabyMod\\addons-1.8                |
|Linux          |~/.minecraft/LabyMod/addons-1.8                           |
|macOS          |~/Library/Application Support/minecraft/LabyMod/addons-1.8|

### Verfügbare Befehle
- **.scammer add \<Name\>** - Fügt einen Spieler zur Scammerliste hinzu.
- **.scammer remove \<Name\>** - Entfernt einen Spieler von der Scammerliste.
- **.scammer addclan \<Name|ClanTag\>** - Fügt die Spieler eines Clans zur Scammerliste hinzu.
- **.scammer removeclan \<Name|ClanTag\>** - Entfernt die Spieler eines Clans von der Scammerliste.
- **.scammer check \<Name\>** - Überprüft ob sich ein Spieler auf der Scammerliste befindet.
- **.scammer checkall** - Zeigt alle Scammer auf dem CityBuild an.
- **.scammer clear** - Entfernt alle Spieler von der Scammerliste.
- **.scammer list** - Zeige alle Spieler auf der Scammerliste.
- **.scammer update** - Aktualisiert die Namen der Spieler.
- **.scammer version** - Zeigt die Version des Addons an.

Anstatt **.scammer** kann auch der Befehl **.sc** verwendet werden.

### Andere Server als GrieferGames
Folgende Funktionen sind ausschließlich auf GrieferGames (oder Servern mit gleicher Chatformatierung) nutzbar:
- Markierung von Scammern im Chat, in Clans und per MSG
- Hinzufügen und Entfernen von Clans
