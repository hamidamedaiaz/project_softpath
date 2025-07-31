# 📋 Gestionnaire de Tâches JavaFX

> Application moderne de gestion de tâches développée en JavaFX avec une interface intuitive et des fonctionnalités avancées de productivité.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-24.0.1-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)

## 🚀 Fonctionnalités Actuelles

### ✅ Gestion Complète des Tâches
- **CRUD complet** : Créer, lire, modifier et supprimer des tâches
- **Système de priorités** : Faible, Moyenne, Élevée avec codes couleur
- **États multiples** : À faire, En cours, Terminée
- **Dates d'échéance** avec alertes visuelles pour les tâches en retard
- **Validation en temps réel** des formulaires avec feedback visuel

### 🔍 Fonctionnalités de Recherche et Filtrage
- **Recherche textuelle** dans les titres et descriptions
- **Filtres rapides** par statut (Toutes, À faire, En cours, Terminées)
- **Filtres temporels** (En retard, Aujourd'hui)
- **Tri personnalisable** par titre, priorité, échéance, statut ou date de création

### 💾 Persistance et Import/Export
- **Sauvegarde automatique** au format JSON
- **Import/Export** de tâches pour la portabilité des données
- **Rechargement automatique** au démarrage de l'application

### 🎨 Interface Utilisateur Moderne
- **Design Material Design** avec thème cohérent
- **Interface responsive** avec panneau de détails adaptatif
- **Feedback visuel** pour les tâches en retard et dues aujourd'hui


## 🛠️ Technologies Utilisées

- **Java 17+** - Langage de programmation principal
- **JavaFX 24.0.1** - Framework d'interface utilisateur moderne
- **FXML** - Définition déclarative des interfaces
- **CSS** - Stylisation avancée avec thème personnalisé
- **Jackson JSON** - Sérialisation/désérialisation des données
- **Maven** - Gestion des dépendances et build

## ⚡ Installation et Démarrage

### 1. Clonage du Repository
```bash
git clone https://github.com/hamidamedaiaz/project_softpath.git
```

### 2. Compilation du Projet
```bash
mvn clean compile
```

### 3. Lancement de l'Application
```bash
mvn javafx:run
```

### 4. Génération d'un JAR Exécutable (Optionnel)
```bash
mvn clean package
java -jar target/task-manager-javafx-1.0.0.jar
```

## 📁 Structure du Projet

```
src/main/java/com/taskmanager/
├── TaskManagerApplication.java          # Point d'entrée principal
├── controller/                          # Contrôleurs MVC
│   ├── MainController.java             # Contrôleur principal
│   └── TaskDialogController.java       # Contrôleur de dialogue
├── model/                               # Modèles de données
│   └── Task.java                       # Entité Tâche
├── service/                             # Services métier
│   └── TaskService.java                # Service de gestion des tâches
└── enums/                              # Énumérations
    ├── Priority.java                   # Priorités des tâches
    └── Status.java                     # États des tâches

src/main/resources/
├── fxml/                               # Fichiers FXML
│   ├── MainView.fxml                   # Interface principale
│   └── TaskDialog.fxml                 # Dialogue de tâche
└── css/
    └── styles.css                      # Feuille de style principale
```

## 🎯 Guide d'Utilisation

### Création d'une Nouvelle Tâche
1. Cliquez sur **"Ajouter"** ou utilisez `Fichier > Nouvelle Tâche`
2. Remplissez les informations requises (titre obligatoire)
3. Sélectionnez la priorité et le statut
4. Définissez une date d'échéance (optionnelle)
5. Cliquez sur **"OK"** pour sauvegarder

### Gestion des Tâches
- **Modifier** : Sélectionnez une tâche et cliquez sur "Modifier"
- **Supprimer** : Sélectionnez une tâche et cliquez sur "Supprimer"
- **Changer le statut** : Utilisez les boutons "Actions Rapides" dans le panneau de détails

### Filtres et Recherche
- Utilisez la **barre de recherche** pour trouver des tâches spécifiques
- Cliquez sur les **boutons de filtre** pour afficher certaines catégories
- Utilisez le **menu déroulant de tri** pour organiser la liste

## 🔧 Configuration

L'application sauvegarde automatiquement les données dans un fichier `tasks.json` dans le répertoire de travail. Ce fichier est créé automatiquement lors de la première utilisation.



## 📝 Roadmap de Développement

### Phase 1 - Fondations ✅
- [x] Architecture MVC robuste
- [x] CRUD complet des tâches
- [x] Système de priorités et statuts
- [x] Interface utilisateur moderne
- [x] Persistance JSON

### Phase 2 - Fonctionnalités Avancées(Fonctionnalités à Venir)
- [ ] Dashboard avec statistiques
- [ ] Système Pomodoro intégré
- [ ] Vue en cartes Kanban
- [ ] Notifications système

### Phase 3 - Intelligence Artificielle (Fonctionnalités à Venir)
- [ ] Agent IA pour suggestions


## 👨‍💻 Auteur

**AMEDIAZ HAMID**
- LinkedIn: [AMEDIAZ Hamid]([https://linkedin.com/in/votre-profil](https://www.linkedin.com/in/hamid-amediaz-4b8a14286/])
- Email: amediazhamid736@gmail.com
