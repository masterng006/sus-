# SusPlugin

Plugin Paper/Spigot pour suivre les joueurs suspects, inspiré des GUIs "SUS" style DonutSMP.

## Compiler le plugin

Tu as besoin de **Maven** et du **JDK 25** installés sur ta machine (requis depuis Minecraft 26.1+).

```bash
cd sus-plugin
mvn clean package
```

Le fichier `sus-plugin.jar` sera généré dans le dossier `target/`.

**Note sur le versioning** : depuis 2026, Mojang et Paper sont passés à un nouveau format
de version `année.drop.patch` (ex: `26.2` au lieu de `1.21.x`). Le `pom.xml` utilise donc
`[26.2.build,)` pour toujours prendre le dernier build disponible de la version 26.2.
Si ton serveur tourne sur une version différente, adapte ce numéro dans `pom.xml` et
`api-version` dans `plugin.yml`.

## Installer le plugin

1. Copie `target/sus-plugin.jar` dans le dossier `plugins/` de ton serveur.
2. Redémarre le serveur (ou `/reload` si tu acceptes les risques que ça comporte).
3. Édite `plugins/SusPlugin/config.yml` si tu veux changer les messages, le titre du GUI, etc.

## Commandes

| Commande | Description | Permission |
|---|---|---|
| `/sus` | Ouvre le GUI des joueurs suspects | `sus.use` |
| `/sus flag <player> <check>` | Signale un joueur (utilisable par la console) | `sus.flag` |
| `/sus clear <player>` | Efface les signalements d'un joueur | `sus.clear` |
| `/sus reload` | Recharge le config.yml | `sus.reload` |

## Connecter GrimAC

Dans `plugins/GrimAC/punishments.yml`, ajoute une commande dans les catégories de checks
que tu veux surveiller :

```yaml
Punishments:
  Movement:
    remove-violations-after: 300
    checks:
      - "Simulation"
      - "Speed"
      - "NoFall"
    commands:
      - "1:1 [alert]"
      - "10:10 sus flag %player% Movement"

  Combat:
    remove-violations-after: 300
    checks:
      - "Killaura"
      - "Reach"
    commands:
      - "1:1 [alert]"
      - "10:10 sus flag %player% Combat"
```

Puis `/grim reload`. Chaque fois qu'un joueur atteint 10 violations (et tous les 10 en plus),
Grim exécutera automatiquement `sus flag <joueur> <catégorie>` en console, qui alimentera
le GUI `/sus`.

## Fonctionnement du GUI

- **Clic gauche** sur une tête : passe le staff en mode spectateur et le téléporte sur le joueur.
- **Clic droit** sur une tête : efface les signalements de ce joueur.
- **Flèche gauche/droite** : navigation entre les pages (45 têtes par page).
- **Horloge** : rafraîchit la page actuelle.

## Notes techniques

- Les données sont stockées en mémoire et sauvegardées dans `plugins/SusPlugin/flags.yml`
  à l'arrêt du serveur (si `persist-data: true` dans le config).
- Le plugin n'a **aucune dépendance obligatoire** sur GrimAC ou Vulcan — il fonctionne
  uniquement via la commande `/sus flag`, ce qui le rend compatible avec n'importe quel
  anticheat capable d'exécuter des commandes console sur seuil de violation (pas seulement Grim).
