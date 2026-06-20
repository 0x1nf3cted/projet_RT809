Sujet de TP RT0809
Le TP se fait en groupe de 2 étudiants.
Le TP doit donner lieu à :
• L'écriture d'un cahier des charges détaillé ;
• La modélisation de l'application à développer (échanges, UML, données, erreurs …) ;
• Un tour d'horizon des techniques existantes, et des choix technologiques justifiés ;
• Le développement d'une solution fonctionnelle ;
• La rédaction d'un rapport.
Covoiturage
Le but de cette application est de mettre en relation des conducteurs et des passagers sur des lignes
de covoiturage établies.
Les acteurs de cette application sont :
• Les conducteurs : ils s’inscrivent sur la plateforme de covoiturage, indiquent le nombre de
place dont ils disposent ;
• Les passagers : ils s’inscrivent sur la plateforme ;
Les passagers se rendent à un point de rendez-vous de covoiturage, et envoient par l’intermédiaire de
l’application WEB leur localisation et leur destination.
Les conducteurs qui se trouvent dans la zone d’un rendez-vous reçoivent en temps réel des alertes (la
mise à jour d’une page web par exemple) sur leur application, et sont ainsi avertis de la présence d’un
passager.
Lorsqu’un conducteur prend un passager en charge, ils échangent leurs identifiants et le trajet est alors
enregistré dans l’application, côté conducteur et côté passager. Une fois le trajet terminé, chaque
acteur attribue une note à l’autre.
Votre application WEB pourra assurer, par exemple :
• La gestion des utilisateurs ;
• L'envoi des demandes de covoiturage ;
• La réception des demandes ;
• L’enregistrement des trajets ;
• La notation des acteurs (conducteur / passager)
• L’accès à l’historique des trajets de chaque utilisateur
• …
La partie serveur sera développée sur une plate-forme Jakarta EE. Vous utiliserez le serveur
d’application de votre choix. Vous utiliserez Maven et Git pour assurer la structuration et la gestion
des sources.