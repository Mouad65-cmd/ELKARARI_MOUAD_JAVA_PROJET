# 🎨 Paint ENSA Safi - Application de Dessin Internationalisée

**Université Cadi Ayyad Marrakech — École Nationale des Sciences Appliquées de Safi (ENSA Safi)**  
*Projet 1 : Conception & Programmation Orientée Objet Avancée en Java*

---

## 👤 Réalisé par

**ELKARARI MOUAD**  
*Génie Informatique — ENSA Safi, Université Cadi Ayyad Marrakech*

---

## 📦 Formats de Rendu Livrés

Conformément aux consignes du sujet :
1. **Format Exécutable** :
   - `PaintJava.jar` / `Paint-ENSA-Safi.jar` (Archive Java autonome exécutable en double-cliquant).
   - `run.bat` (Lanceur Windows direct en un clic).
2. **Format Code Source** :
   - Arborescence complète dans `src/main/java/` et `src/main/resources/`.
   - Scripts de compilation automatisés `build.bat` et `build.ps1`.

---

## 🌟 Fonctionnalités Principales

### 1. 🖌️ Outils de Dessin
- **Crayon (Pencil)** : Dessin à main levée continu et fluide avec lissage anti-aliasing.
- **Pinceau (Brush)** : Tracé épais et arrondi avec intensité ajustable.
- **Gomme (Eraser)** : Effacement vers la couleur d'arrière-plan ou blanc.
- **Pot de peinture (Flood Fill)** : Algorithme itératif BFS (Breadth-First Search) haute performance pour remplir instantanément les zones fermées.
- **Pipette (Eyedropper / Color Picker)** : Capture précise de la couleur d'un pixel du canvas (Clic gauche pour Couleur 1, Clic droit pour Couleur 2).
- **Outil Texte (Text Tool)** : Insertion de textes personnalisés avec prise en charge du style (Gras, Italique) et de la taille de police.
- **Sélection Rectangulaire (Selection Tool)** : Délimitation d'une zone avec cadre pointillé, déplacement à la souris, copier/coller, couper et suppression.

### 2. 📐 Formes Géométriques
- **Ligne droite** (Line)
- **Rectangle** et Carré
- **Rectangle à coins arrondis** (Rounded Rectangle)
- **Cercle / Ellipse**
- **Triangle** isocèle
- **Étoile à 5 branches** (Star)
- **Flèche directionnelle** (Arrow)
- **Options de forme** :
  - *Contour seul* (Stroke only)
  - *Rempli seul* (Fill only)
  - *Contour & Rempli* simultanés (Both)

### 3. 🎨 Nuancier et Propriétés du Tracé
- **Couleur 1 (Premier plan / Contour)** et **Couleur 2 (Arrière-plan / Remplissage)**.
- **Nuancier rapide de 24 couleurs classiques**.
- **Sélecteur complet `JColorChooser`** permettant de choisir n'importe quelle nuance hexadécimale/RGB.
- **Épaisseur de trait** réglable au pixel près (1 à 50 px).
- **Styles de contour** : Continu (Solid), Tirets (Dashed), Pointillés (Dotted).

### 4. 🌐 Internationalisation (i18n) Dynamique
L'application supporte le changement de langue en direct sans redémarrage :
- 🇫🇷 **Français** (Langue par défaut)
- 🇬🇧 **English**
- 🇲🇦 **العربية** (Arabe avec orientation de l'interface **RTL - Right To Left**)
- 🇪🇸 **Español**
- Tous les menus, infobulles, dialogues, barre d'état et composants s'adaptent instantanément via `ResourceBundle` et un bus d'événements `LocaleChangeListener`.

### 5. 🔄 Historique et Transformations d'Image
- **Annuler / Rétablir (Undo / Redo)** : Piles de snapshots mémorisés (Memento pattern), accessibles via raccourcis `Ctrl + Z` et `Ctrl + Y`.
- **Rotations** : 90° à droite, 90° à gauche, 180°.
- **Retournements** : Miroir horizontal, Miroir vertical.
- **Filtres** : Inversion des couleurs (Négatif), Conversion en niveaux de gris (Grayscale).
- **Effacer tout (Clear)** : Remise à blanc instantanée du canvas.

### 6. 🔍 Affichage, Règles et Redimensionnement
- **Règles graduées en pixels** (horizontale et verticale) avec suivi du curseur de la souris en temps réel.
- **Grille de repère matricielle** activable à tout moment.
- **Zoom dynamique** : Zoom avant, Zoom arrière, Taille réelle (100%), ou `Ctrl + Molette de la souris`.
- **Redimensionnement interactif** : Poignée d'étirement en bas à droite du canvas, ou boîte de dialogue précise avec choix de conserver les proportions et mise à l'échelle.

### 7. 💾 Gestion de Fichiers & Impression
- **Nouveau canvas**, **Ouvrir**, **Enregistrer**, **Enregistrer sous...** (formats `.png`, `.jpg`, `.bmp`, `.gif`).
- **Détection des modifications non enregistrées** à la fermeture ou à l'ouverture d'un nouveau projet.
- **Impression directe** via le sous-système `PrinterJob` de Java.

---

## 🏛️ Architecture Logicielle & Design Patterns

L'application respecte les principes de conception orientée objet (SOLID) et utilise plusieurs Design Patterns majeurs :

1. **MVC (Modèle - Vue - Contrôleur)** :
   - **Modèle** (`DrawingModel`) : Contient l'image matricielle `BufferedImage`, l'état des outils, les sélections et les couleurs.
   - **Vue** (`MainWindow`, `CanvasPanel`, `ToolBarPanel`, `MenuBarView`, `StatusBarPanel`, `RulerPanel`) : Affiche l'état du modèle et capture les entrées utilisateur.
   - **Contrôleur** : Écouteurs d'événements et délégation vers le modèle.
2. **Strategy Pattern** (`Tool` interface et ses implémentations `PencilTool`, `ShapeTool`, `BucketFillTool`...) : Permet d'interchanger l'algorithme de dessin dynamiquement selon l'outil sélectionné.
3. **Template Method Pattern** (`ShapeTool`) : Factorise le calcul géométrique, la prévisualisation en temps réel et le rendu final pour toutes les formes géométriques.
4. **Memento Pattern** (`HistoryManager`) : Capture des instantanés de l'image pour gérer le retour en arrière (Undo/Redo) de manière non destructive.
5. **Observer Pattern** :
   - `DrawingModel.ModelListener` notifie la vue lors de tout changement du dessin, des couleurs ou des outils.
   - `LocaleChangeListener` notifie tous les composants lors du basculement de langue.
6. **Factory Pattern** (`IconFactory`) : Génère dynamiquement des icônes vectorielles nettes et indépendantes de fichiers d'images externes.

---

## 📁 Arborescence du Projet

```
c:\Users\Ultrapc\Desktop\JAVA\
├── bin\                               # Fichiers .class compilés
├── src\
│   └── main\
│       ├── java\
│       │   └── ensa\
│       │       └── paint\
│       │           ├── Main.java              # Point d'entrée de l'application
│       │           ├── i18n\
│       │           │   ├── I18n.java                 # Gestionnaire de ressources i18n
│       │           │   └── LocaleChangeListener.java # Interface d'écoute de changement de langue
│       │           ├── model\
│       │           │   ├── DrawingModel.java         # Modèle de données de dessin
│       │           │   ├── FillMode.java             # Énumération des modes de remplissage
│       │           │   ├── HistoryManager.java       # Gestionnaire Undo / Redo
│       │           │   ├── StrokeStyle.java          # Styles de traits (continu, tirets...)
│       │           │   └── ToolType.java             # Énumération des outils
│       │           ├── tools\
│       │           │   ├── Tool.java                 # Interface Strategy
│       │           │   ├── AbstractTool.java         # Classe de base
│       │           │   ├── ShapeTool.java            # Template method pour formes
│       │           │   ├── PencilTool.java           # Outil Crayon
│       │           │   ├── BrushTool.java            # Outil Pinceau
│       │           │   ├── EraserTool.java           # Outil Gomme
│       │           │   ├── LineTool.java             # Outil Ligne
│       │           │   ├── RectangleTool.java        # Outil Rectangle
│       │           │   ├── RoundedRectangleTool.java # Outil Rectangle arrondi
│       │           │   ├── EllipseTool.java          # Outil Ellipse
│       │           │   ├── TriangleTool.java         # Outil Triangle
│       │           │   ├── StarTool.java             # Outil Étoile
│       │           │   ├── ArrowTool.java            # Outil Flèche
│       │           │   ├── BucketFillTool.java       # Outil Pot de peinture (Flood Fill)
│       │           │   ├── PickerTool.java           # Outil Pipette
│       │           │   ├── TextTool.java             # Outil Texte
│       │           │   ├── SelectionTool.java        # Outil Sélection rectangulaire
│       │           │   └── ToolRegistry.java         # Registre d'instanciation des outils
│       │           ├── utils\
│       │           │   ├── FileService.java          # Service Ouvrir/Enregistrer/Imprimer
│       │           │   └── IconFactory.java          # Générateur d'icônes vectorielles
│       │           └── view\
│       │               ├── MainWindow.java           # Fenêtre principale JFrame
│       │               ├── CanvasPanel.java          # Surface de dessin JPanel
│       │               ├── MenuBarView.java          # Barre de menus
│       │               ├── ToolBarPanel.java         # Barre d'outils
│       │               ├── ColorPalettePanel.java    # Palette de couleurs
│       │               ├── StatusBarPanel.java       # Barre d'état
│       │               ├── RulerPanel.java           # Règles graduées
│       │               ├── ResizeDialog.java         # Dialogue de redimensionnement
│       │               └── AboutDialog.java          # Dialogue À propos
│       └── resources\
│           └── i18n\
│               ├── messages_fr.properties    # Traductions Françaises
│               ├── messages_en.properties    # Traductions Anglaises
│               ├── messages_ar.properties    # Traductions Arabes
│               └── messages_es.properties    # Traductions Espagnoles
├── Manifest.txt                       # Déclaration du point d'entrée JAR
├── PaintJava.jar                      # Archive exécutable autonome (Livrable 1)
├── Paint-ENSA-Safi.jar                # Copie nommée du livrable exécutable
├── run.bat                            # Script de lancement rapide Windows
├── build.bat                          # Script de compilation batch
├── build.ps1                          # Script de compilation PowerShell
└── README.md                          # Documentation et rapport du projet
```

---

## 🚀 Instructions d'Exécution & de Compilation

### 1. Lancement direct de l'exécutable
- Double-cliquez sur `PaintJava.jar` ou `run.bat`.
- Ou en ligne de commande :
  ```cmd
  java -jar PaintJava.jar
  ```

### 2. Recompilation à partir du code source
- **Sous Windows (Invite de commande)** :
  Double-cliquez sur `build.bat` ou lancez :
  ```cmd
  build.bat
  ```
- **Sous PowerShell** :
  ```powershell
  .\build.ps1
  ```
- **Manuellement via JDK** :
  ```cmd
  javac -encoding UTF-8 -d bin src\main\java\ensa\paint\*.java src\main\java\ensa\paint\model\*.java src\main\java\ensa\paint\tools\*.java src\main\java\ensa\paint\view\*.java src\main\java\ensa\paint\i18n\*.java src\main\java\ensa\paint\utils\*.java
  xcopy /s /e /y src\main\resources\* bin\
  jar cfm PaintJava.jar Manifest.txt -C bin .
  ```

---

## ⌨️ Raccourcis Clavier Principaux

| Raccourci | Action |
|---|---|
| `Ctrl + N` | Nouvelle image |
| `Ctrl + O` | Ouvrir une image existante |
| `Ctrl + S` | Enregistrer l'image |
| `Ctrl + Shift + S` | Enregistrer sous... |
| `Ctrl + P` | Imprimer |
| `Ctrl + Z` | Annuler (Undo) |
| `Ctrl + Y` | Rétablir (Redo) |
| `Ctrl + X` | Couper la sélection |
| `Ctrl + C` | Copier la sélection |
| `Ctrl + V` | Coller la sélection |
| `Ctrl + A` | Tout sélectionner |
| `Ctrl + R` | Boîte de dialogue Redimensionner |
| `Ctrl + I` | Inverser les couleurs (Négatif) |
| `Ctrl + +` / `Ctrl + -` | Zoom avant / arrière |
| `Ctrl + 0` | Réinitialiser le zoom à 100% |
| `Ctrl + Molette` | Zoom continu centré |

---
*Projet réalisé dans le cadre du cursus d'Ingénieur d'État — ENSA Safi, Université Cadi Ayyad.*
