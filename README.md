Brainwave
==============

--
## I. Pr�sentation du projet BrainWave

BrainWave est une application Andoid qui se connecte � des capteurs d'ondes c�r�brales de type MindPlay.
L'application r�cup�rere les donn�es et les stocks localement. 
L'affichage d'un graphique avec les diff�rentes donn�es r�cup�r�es peut �tre affich�.
De plus, nous pouvons cr�er un fichier CSV depuis les donn�es recueillies. 
Par ailleurs, la d�marche inverse est possible, 
nous pouvons convertir un fichier contenant les donn�es sur un CSV et les afficher sur un graphique.
	
## II. Besoins li�s � l'application

L'application se connecte en Bluetooth sur le capteur. Les donn�es recueillies doivent �tres facilement compr�henssibles et 
l'application doit �tre claire pour un utilisateur lambda. Par ailleurs, un export en CSV est �tre possible. 
Pour repr�senter les diff�rents niveaux de concentration, et de m�ditation nous avons d�cider de les repr�senter 
par des courbes dans des graphiques plut�t que par compteurs comme sur l�application MindWave. 
Nous avons donc recherch� une librairie permettant de r�aliser des graphiques et des courbes, nous avons trouv� la librairie GraphView. 
Enfin, nous pouvons comparer les courbes de deux personnes.

## III. D�veloppement

D�s le d�part, �tant donn� que nous n'avions pas encore de capteurs d'ondes c�r�brales � la port�e de la main, 
nous nous sommes focalis�s sur la connexion en bluetooth sous Android. Nous avons donc fusionn�es nos travaux dans notre application. 
Nous avons obtenu une application qui � son lancement tente de se connecter au casque MindWave, 
si le module Bluetooth de celui ci �met alors la connexion se fait et l�application r�cup�re les donn�es de concentration 
et de m�ditation que le casque envoie et affiche ces donn�es sur le graphique et les courbes se dessinent au fur et a mesures des donn�es re�ues.
Nous nous sommes mis dans le bain dans le projet et nous avons commenc� un plan de d�veloppement que vous trouver ci-dessous.
L'application se d�compose en trois intents : le menu g�n�ral ainsi que le contr�le du bluetooth et le graphique.

![Alt text](\Screenshots/Screenshot_Menu_Buttons.png?raw=true "Menu buttons")

### 1. Connexion en bluetooth entre le casque et le device

Le Bluetooth est un moyen d'envoyer ou de recevoir des donn�es entre deux appareils diff�rents. 
En effet, Android inclut un support pour le Bluetooth qui permet � un p�riph�rique sans fil  d'�changer
 des donn�es avec d'autres appareils Bluetooth. Grace � l'API Bluetooth, nous pouvons, dans l'application BrainWave : 
 activer et d�sactiver le Bluetooth, scanner les autres appareil aux alentours, montrer la liste des devices d�j� appair�s,
  se connecter et de d�connecter d'un appareil donn�.
  
![Alt text](\Screenshots//Screenshot_Example_Buttons.png?raw=true "Exemple stylisation boutons")

### 2. R�cup�ration des donn�es du casque

Le casque poss�de plusieurs �l�ctodes qui, plac�es sur le front, envoient des ondes EGG (Ondes de tr�s faibles amplitudes, de l'ordre du microVolt chez l'�tre humain) � un boitier bluetooth qui les convertis en donn�es brutes, dites row_data. Ces derni�res sont trait�es dirrectement dans l'applications.

'''
/**
* Renvoie une valeur entre 0 et 100 sur la capacit� d'attention/m�ditation
* 0 : incapacit� � calculer une valeur d'attention/m�ditation
* 1 - 20 : tr�s faible attention/m�ditation
* 20 - 40 : faible attention/m�ditation
* 40 - 60 : valeur d'attention/m�ditation normale
* 60 - 80 : attention/m�ditation relativement �lev�e
* 80 - 100 : attention/m�ditation �lev�e
*/
'''

### 3. G�n�ration d'un graphique d'apr�s les donn�es

Grace � la classe GraphViewData qui impl�mente GraphViewDataInterface, nous pouvons r�cup�rer les coordonn�es (x,y) 
correspondant � l'attention et � la m�ditation et les afficher grace � la classe MainActivity.

#### a) Lissage de la courbe

La courbe n'�tant pas liss�e ...

![Alt text](\Screenshots//graphique.png?raw=true "Exemple de graphique")


### 4. G�n�ration CSV d'apr�s les donn�es

Pour pouvoir garder une trace des donn�es, nous avons d�cid� de les exporter dans un fichier CSV.

### 5. G�n�ration d'un graphique d'apr�s un CSV

Nous avons la possibilit� de g�n�rer un graphique d'apr�s un fichier CSV.

### 6. Comparaison de deux graphiques.

Pour pouvoir comparer les donn�es de deux personnes, nous calquons le graphe de la personne 1 sur le graphe de la personne 2.

### 7. Ajout de la librairie stylistique

Pour rendre notre application plus ergonomique et plus jolie, nous avons d�cid�e de faire appel � une librairie stylistique GraphView pour le graphique.

## IV. Conclusion

L'application est claire, fonctionnelle et facile d'utilisation, le design est relativement �pur� et les boutons sont imposants.
La premiere page menu rassemble l'application de gestion du bluetooth et celle de gestion des donn�es.
Un export/import des donn�es est possible.

![Alt text](\Screenshots//parametre.png?raw=true "Fen�tre de param�tres")

Memo :
![Alt text](\Screenshots//memo.png?raw=true "Memo")

