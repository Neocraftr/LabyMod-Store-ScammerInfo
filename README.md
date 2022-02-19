# LabyMod\-ScammerInfo

Scammerliste für GrieferGames und ähnliche Server.

Spielversion: **LabyMod 1.8.9 Vanilla/Forge**

### Funktionen
- Speicherung der UUID von Spielern (Scammer bleiben auch nach Namensänderung auf der Liste)
- Unterstützung für Pocket Edition Spieler (**Achtung: Diese Spieler werden nach Namensänderungen nicht mehr angezeigt**)
- Automatische Aktualisierung der Spielernamen
- Markierung von Scammern/trusted Spielern im Chat, in Clans, in Direktnachrichten und bei Startkicks
- Befehl zum Hinzufügen / Entfernen ganzer Clans
- Befehl um alle Scammer/trusted Spielern auf dem CityBuild anzuzeigen
- Speicherung von zusätzlichen informationen zu Spielern auf der Liste (Datum/Uhrzeit, Beschreibung, Ursprünglicher Name)
- Möglichkeit eigene\* oder öffentliche Listen hinzuzufügen z.B. die des [\[SCAMMER\] Radar](https://scammer-radar.de/) Projekts

\* Auch Dateipfade können in der URL Zeile angegeben werden. Bsp: _file://C:\Pfad\zu\meiner\Liste.json_

### Installation
Download: https://github.com/Neocraftr/LabyMod-ScammerInfo/releases/latest

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
Befehle für trusted Spieler sind **.trusted** bzw. **.tr**.

### Format für öffentliche Listen
Die API muss über HTTP/HTTPS im JSON Format erreichbar sein.
```json
[
  {
    "uuid": "fe2b3027-1d4c-474a-be24-2fc3f1e6f143",
    "name": "Neocraftr",
    "description": "Ist ein cooler Typ",
    "originalName": "VorherigerName",
    "date": 1645287190590
  },
  ...
]
```
`uuid`: UUID des Spielers\
`name`: Name des Spielers (optional, wird automatisch geladen)\
`description`: Grund für die Aufnahme auf die Liste (optional)\
`originalName`: Ursprünglicher Name unter dem der Spieler aufgenommen wurde (optional)\
`date`: Zeitpunkt der Aufnahme auf die Liste in Unixzeit Millisekunden (optional)\


### Andere Server
Folgende Funktionen sind ausschließlich auf GrieferGames (oder Servern mit gleicher Chatformatierung) nutzbar:
- Markierung von Scammern im Chat, in Clans und per MSG
- Hinzufügen und Entfernen von Clans
