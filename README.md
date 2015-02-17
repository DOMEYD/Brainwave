Brainwave
==============

--
## I. Présentation du projet BrainWave

BrainWave est une application Andoid qui se connecte à des capteurs d'ondes cérébrales de type MindPlay.
L'application récupérere les données et les stocks localement. 
L'affichage d'un graphique avec les différentes données récupérées peut être affiché.
De plus, nous pouvons créer un fichier CSV depuis les données recueillies. 
Par ailleurs, la démarche inverse est possible, 
nous pouvons convertir un fichier contenant les données sur un CSV et les afficher sur un graphique.
	
## II. Besoins liés à l'application

L'application se connecte en Bluetooth sur le capteur. Les données recueillies doivent êtres facilement compréhenssibles et 
l'application doit être claire pour un utilisateur lambda. Par ailleurs, un export en CSV est être possible. 
Pour représenter les différents niveaux de concentration, et de méditation nous avons décider de les représenter 
par des courbes dans des graphiques plutôt que par compteurs comme sur l’application MindWave. 
Nous avons donc recherché une librairie permettant de réaliser des graphiques et des courbes, nous avons trouvé la librairie GraphView. 
Enfin, nous pouvons comparer les courbes de deux personnes.

## III. Développement

Dès le départ, étant donné que nous n'avions pas encore de capteurs d'ondes cérébrales à la portée de la main, 
nous nous sommes focalisés sur la connexion en bluetooth sous Android. Nous avons donc fusionnées nos travaux dans notre application. 
Nous avons obtenu une application qui à son lancement tente de se connecter au casque MindWave, 
si le module Bluetooth de celui ci émet alors la connexion se fait et l’application récupère les données de concentration 
et de méditation que le casque envoie et affiche ces données sur le graphique et les courbes se dessinent au fur et a mesures des données reçues.
Nous nous sommes mis dans le bain dans le projet et nous avons commencé un plan de développement que vous trouver ci-dessous.
L'application se décompose en trois intents : le menu général ainsi que le contrôle du bluetooth et le graphique.

![Alt text](\Screenshots/Screenshot_Menu_Buttons.png?raw=true "Menu buttons")

### 1. Connexion en bluetooth entre le casque et le device

Le Bluetooth est un moyen d'envoyer ou de recevoir des données entre deux appareils différents. 
En effet, Android inclut un support pour le Bluetooth qui permet à un périphérique sans fil  d'échanger
 des données avec d'autres appareils Bluetooth. Grace à l'API Bluetooth, nous pouvons, dans l'application BrainWave : 
 activer et désactiver le Bluetooth, scanner les autres appareil aux alentours, montrer la liste des devices déjà appairés,
  se connecter et de déconnecter d'un appareil donné.
  
![Alt text](\Screenshots//Screenshot_Example_Buttons.png?raw=true "Exemple stylisation boutons")

### 2. Récupération des données du casque

Le casque possède plusieurs éléctodes qui, placées sur le front, envoient des ondes EGG (Ondes de très faibles amplitudes, de l'ordre du microVolt chez l'être humain) à un boitier bluetooth qui les convertis en données brutes, dites row_data. Ces dernières sont traitées dirrectement dans l'applications.

'''
/**
* Renvoie une valeur entre 0 et 100 sur la capacité d'attention/méditation
* 0 : incapacité à calculer une valeur d'attention/méditation
* 1 - 20 : très faible attention/méditation
* 20 - 40 : faible attention/méditation
* 40 - 60 : valeur d'attention/méditation normale
* 60 - 80 : attention/méditation relativement élevée
* 80 - 100 : attention/méditation élevée
*/
'''

### 3. Génération d'un graphique d'après les données

Grace à la classe GraphViewData qui implémente GraphViewDataInterface, nous pouvons récupérer les coordonnées (x,y) 
correspondant à l'attention et à la méditation et les afficher grace à la classe MainActivity.

#### a) Lissage de la courbe

La courbe n'étant pas lissée ...

![Alt text](\Screenshots//graphique.png?raw=true "Exemple de graphique")


### 4. Génération CSV d'après les données

Pour pouvoir garder une trace des données, nous avons décidé de les exporter dans un fichier CSV.

### 5. Génération d'un graphique d'après un CSV

Nous avons la possibilité de générer un graphique d'après un fichier CSV.

### 6. Comparaison de deux graphiques.

Pour pouvoir comparer les données de deux personnes, nous calquons le graphe de la personne 1 sur le graphe de la personne 2.

### 7. Ajout de la librairie stylistique

Pour rendre notre application plus ergonomique et plus jolie, nous avons décidée de faire appel à une librairie stylistique GraphView pour le graphique.

## IV. Conclusion

L'application est claire, fonctionnelle et facile d'utilisation, le design est relativement épuré et les boutons sont imposants.
La premiere page menu rassemble l'application de gestion du bluetooth et celle de gestion des données.
Un export/import des données est possible.

![Alt text](\Screenshots//parametre.png?raw=true "Fenêtre de paramètres")

Memo :
![Alt text](\Screenshots//memo.png?raw=true "Memo")

