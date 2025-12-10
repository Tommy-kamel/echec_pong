# 🎮 Guide : Jouer en réseau local (2 PC)

## 📋 Configuration requise

**Sur les DEUX PC** :
- Java 21 installé OU utiliser `EchecPong.exe` (Java inclus)
- Même réseau local (WiFi ou Ethernet)

---

## 🚀 Étapes pour jouer

### 1️⃣ Sur le PC HOST (celui qui héberge)

1. **Lancer le jeu** :
   ```powershell
   java -jar echec_pong-1.0-SNAPSHOT.jar
   # OU double-clic sur EchecPong.exe
   ```

2. **Choisir "HÔTE"**

3. **Configurer la partie** :
   - Taille du plateau
   - Points de vie des pièces
   - Qui commence

4. **Cliquer sur "Démarrer le serveur"**

5. **Noter l'adresse IP** :
   - Ouvrir PowerShell et taper : `ipconfig`
   - Chercher "Adresse IPv4" (ex: `192.168.1.100`)
   - **Communiquer cette IP au CLIENT**

---

### 2️⃣ Sur le PC CLIENT (celui qui rejoint)

1. **Lancer le jeu** :
   ```powershell
   java -jar echec_pong-1.0-SNAPSHOT.jar
   # OU double-clic sur EchecPong.exe
   ```

2. **Choisir "CLIENT"**

3. **Saisir l'adresse IP du HOST** :
   - Entrer l'IP communiquée (ex: `192.168.1.100`)
   - OU laisser `localhost` si les deux joueurs sont sur le même PC

4. **Cliquer sur "Se connecter"**

5. **Attendre la connexion** → Le jeu démarre automatiquement !

---

## 🎯 Contrôles

**HOST (joueur BLANC - en bas)** :
- ⬅️ `Flèche Gauche` : déplacer la raquette à gauche
- ➡️ `Flèche Droite` : déplacer la raquette à droite

**CLIENT (joueur NOIR - en haut)** :
- ⬅️ `Flèche Gauche` : déplacer la raquette à gauche
- ➡️ `Flèche Droite` : déplacer la raquette à droite

---

## ⚙️ Configuration réseau

### Si la connexion échoue :

1. **Vérifier le firewall Windows** :
   ```powershell
   # Autoriser le port 12345
   New-NetFirewallRule -DisplayName "Echec Pong" -Direction Inbound -LocalPort 12345 -Protocol TCP -Action Allow
   ```

2. **Tester la connexion** :
   ```powershell
   # Sur le PC CLIENT, tester si le HOST est accessible
   Test-NetConnection -ComputerName 192.168.1.100 -Port 12345
   ```

3. **Utiliser l'IP locale** :
   - Ne PAS utiliser l'IP publique
   - Utiliser l'IP du réseau local (192.168.x.x ou 10.x.x.x)

---

## 🏆 Fin de partie

- Le jeu se termine quand un ROI atteint 0 HP
- Cliquer sur **"Rejouer"** pour lancer une nouvelle partie
- Le réseau reste connecté, pas besoin de tout reconfigurer !

---

## 🐛 Dépannage

| Problème | Solution |
|----------|----------|
| "Connexion échouée" | Vérifier l'IP, le firewall, et que les deux sont sur le même réseau |
| "Port déjà utilisé" | Fermer toutes les instances du jeu et relancer |
| Décalage des pions | Les pions se synchronisent automatiquement, vérifier la connexion réseau |

---

## 📦 Distribution

**Pour donner le jeu à un ami** :

1. Partager `EchecPong/` (dossier complet avec .exe)
2. OU partager `echec_pong-1.0-SNAPSHOT.jar` (nécessite Java 21)

**Aucune installation requise** avec `EchecPong.exe` !
