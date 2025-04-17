
## Contexte
Ce casse-tête vietnamien a fait le tour d’internet en 2015. Donné initialement à des enfants de CE2,
des internautes se sont remués les méninges pour tenter de le résoudre. Y parviendrez-vous ?
Énoncé du casse-tête: compléter le tableau ci-dessous en utilisant des chiffres de 1 à 9 avec la
contrainte de ne pas pouvoir utiliser deux fois le même chiffre.# vietnamian-game

### Travail demandé
Développer une petite application mobile qui permet de visualiser les résultats et de les manipuler.
Sur cette application, l’utilisateur pourra:
● générer l’ensemble des solutions trouvées par l’algorithme
○ les solutions seront stockées en base de données
○ le temps de calcul de la génération sera affiché à l’utilisateur en retour de son action

● proposer une solution au problème
○ la solution proposée sera enregistrée en base de données
○ un flag permettra de mémoriser si la solution est correcte ou non

● visualiser l’ensemble des solutions enregistrées en base de données

● visualiser une des solutions enregistrées en base de données

● modifier une des solutions enregistrée en base de données
○ prendre en compte que la solution peut changer de statut (correcte/incorrecte)

● supprimer une solution de la liste des solutions

● supprimer toutes les solutions enregistrées en base de données

Pour répondre à ce problème, un algorithme de génération de solutions devra être implémenté.
Il est demandé que le back soit développé à l’aide de Spring Boot, à l’aide de l’ORM JPA. Utilisez H2
pour la base de données.
Le back-end devra exposer des API REST qui seront consommées par le front-end.
Le front-end mobile devra être développé avec React Native.
Si vous êtes à l’aise avec, l’écriture de quelques tests automatisés sera appréciée.
