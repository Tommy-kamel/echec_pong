# Echec Pong - Microservice EJB pour MySQL

Ce module fournit un microservice EJB déployé sur WildFly 28 qui gère les paramètres de partie stockés dans MySQL 8.0.

## Architecture

```
┌─────────────────┐    REST API    ┌─────────────────┐    JPA/EJB    ┌─────────────────┐
│   Jeu JavaFX    │ ──────────────► │   WildFly 28    │ ─────────────► │   MySQL 8.0     │
│  (Client REST)  │                 │  (EJB + JAX-RS) │               │  (Docker)       │
└─────────────────┘                 └─────────────────┘               └─────────────────┘
```

## Prérequis

- Java 17+
- Maven 3.8+
- Docker Desktop
- WildFly 28.0.0.Final installé à `C:\wildfly-28.0.0.Final`

## Installation

### 1. Démarrer MySQL avec Docker

```bash
cd echec-pong-ejb
docker-compose up -d
```

Cela démarre MySQL 8.0 avec:
- **Database**: echec_pong_db
- **User**: echec_user
- **Password**: echec_password
- **Port**: 3306

### 2. Configurer WildFly

#### a) Copier le connecteur MySQL

Téléchargez `mysql-connector-j-8.0.33.jar` depuis Maven Central et créez le module:

```bash
mkdir -p C:\wildfly-28.0.0.Final\modules\system\layers\base\com\mysql\main
copy mysql-connector-j-8.0.33.jar C:\wildfly-28.0.0.Final\modules\system\layers\base\com\mysql\main\
```

Créez le fichier `module.xml` dans ce dossier:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.5" name="com.mysql">
    <resources>
        <resource-root path="mysql-connector-j-8.0.33.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
    </dependencies>
</module>
```

#### b) Démarrer WildFly

```bash
C:\wildfly-28.0.0.Final\bin\standalone.bat
```

#### c) Configurer la datasource via CLI

Dans un autre terminal:

```bash
C:\wildfly-28.0.0.Final\bin\jboss-cli.bat --connect
```

Puis exécutez ces commandes:

```
# Ajouter le driver MySQL
/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql,driver-class-name=com.mysql.cj.jdbc.Driver)

# Créer la datasource
/subsystem=datasources/data-source=EchecPongDS:add(jndi-name=java:jboss/datasources/EchecPongDS,driver-name=mysql,connection-url=jdbc:mysql://localhost:3306/echec_pong_db,user-name=echec_user,password=echec_password,min-pool-size=5,max-pool-size=20)

# Recharger la configuration
:reload
```

### 3. Compiler et déployer le microservice

```bash
cd echec-pong-ejb
mvn clean package
copy target\echec-pong-ejb.war C:\wildfly-28.0.0.Final\standalone\deployments\
```

### 4. Vérifier le déploiement

L'API REST est disponible à: `http://localhost:8080/echec-pong-ejb/api/settings`

## API REST

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | /api/settings | Liste tous les paramètres |
| GET | /api/settings/latest | Derniers paramètres créés |
| GET | /api/settings/{id} | Paramètres par ID |
| GET | /api/settings/name/{name} | Paramètres par nom |
| GET | /api/settings/default | Paramètres par défaut |
| POST | /api/settings | Créer de nouveaux paramètres |
| PUT | /api/settings/{id} | Mettre à jour des paramètres |
| DELETE | /api/settings/{id} | Supprimer des paramètres |

### Exemple de requête

```bash
curl http://localhost:8080/echec-pong-ejb/api/settings
```

### Exemple de réponse

```json
[
  {
    "id": 1,
    "name": "default",
    "boardWidth": 8,
    "pionHealth": 3,
    "cavalierHealth": 5,
    "fouHealth": 5,
    "tourHealth": 5,
    "dameHealth": 8,
    "roiHealth": 10,
    "firstServe": "black"
  }
]
```

## Paramètres pré-configurés

La base de données est initialisée avec ces configurations:

| Nom | Largeur | Pion | Cavalier | Fou | Tour | Dame | Roi | 1er Service |
|-----|---------|------|----------|-----|------|------|-----|-------------|
| default | 8 | 3 | 5 | 5 | 5 | 8 | 10 | Noir |
| easy | 8 | 5 | 7 | 7 | 7 | 10 | 15 | Noir |
| hard | 8 | 2 | 3 | 3 | 3 | 5 | 7 | Noir |
| small_board | 4 | 3 | 5 | 5 | 5 | 8 | 10 | Blanc |
| large_board | 8 | 3 | 5 | 5 | 5 | 8 | 10 | Noir |

## Utilisation dans le jeu

1. Lancez le jeu JavaFX normalement
2. En tant qu'hôte, cochez "Charger depuis base de données (EJB)"
3. Sélectionnez une configuration dans la liste déroulante
4. Les paramètres sont automatiquement appliqués
5. Démarrez la partie normalement

## Dépannage

### Le service EJB n'est pas disponible

1. Vérifiez que WildFly est démarré
2. Vérifiez que l'application est déployée (fichier `.deployed` dans deployments)
3. Vérifiez les logs: `C:\wildfly-28.0.0.Final\standalone\log\server.log`

### Erreur de connexion à MySQL

1. Vérifiez que le conteneur Docker est démarré: `docker ps`
2. Vérifiez que MySQL est accessible: `docker exec -it echec_pong_mysql mysql -u echec_user -pechec_password echec_pong_db`
3. Vérifiez la datasource dans WildFly: Console d'admin `http://localhost:9990`

## Structure du projet

```
echec-pong-ejb/
├── pom.xml                      # Configuration Maven
├── docker-compose.yml           # Configuration Docker MySQL
├── init-db.sql                  # Script d'initialisation DB
├── configure-wildfly.bat        # Script de configuration WildFly
└── src/main/
    ├── java/com/example/echec_pong/
    │   ├── entity/
    │   │   └── GameSettings.java    # Entité JPA
    │   ├── ejb/
    │   │   └── GameSettingsDAO.java # EJB DAO
    │   └── rest/
    │       ├── RestApplication.java # Config JAX-RS
    │       └── GameSettingsResource.java # Endpoint REST
    └── resources/META-INF/
        └── persistence.xml          # Configuration JPA
```
