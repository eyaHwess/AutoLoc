# Atelier 4 — IoC, Injection de Dépendances et Couche Service

**Projet :** AutoLoc API  
**Technologies :** Java 17, Spring Boot, Spring Data JPA, Lombok, MySQL, SonarQube for IDE

## A. Modes d'injection comparés

Dans cet atelier, nous avons comparé deux méthodes d'injection de dépendances : l'injection par constructeur et l'injection par attribut.

| Critère | Injection par constructeur | Injection par attribut |
|---|---|---|
| Annotation | `@RequiredArgsConstructor` (Lombok) | `@Autowired` |
| Champ `final` | Oui | Non |
| Dépendance explicite | Oui, dans le constructeur | Non, cachée dans l'attribut |
| Utilisation hors conteneur Spring | Possible en fournissant la dépendance | Risque de dépendance `null` |
| Détection SonarQube | Pas d'anomalie attendue | Règle S6813 |
| Recommandation | Recommandée | Déconseillée |


L'injection par constructeur est préférable car elle garantit que les dépendances nécessaires sont fournies lors de la création de l'objet.

Dans notre projet, nous utilisons `@RequiredArgsConstructor` avec des champs `final`.

L'injection par attribut avec `@Autowired` a été testée temporairement dans `ContratServiceImpl`.

SonarQube a détecté l'anomalie **java:S6813**, recommandant de remplacer l'injection par attribut par l'injection par constructeur.

Nous avons ensuite restauré la méthode recommandée.

## B. Services et dépendances injectées

La couche Service contient neuf services métier. Chaque implémentation utilise l'injection par constructeur et dépend d'une interface Repository.

| Service | Dépendance injectée | Mode | Justification |
|---|---|---|---|
| AgenceServiceImpl | IAgenceRepository | Constructeur | Gestion CRUD des agences et validation du nom |
| ClientServiceImpl | IClientRepository | Constructeur | Gestion CRUD des clients et validation du nom et de l'e-mail |
| ContratServiceImpl | IContratRepository | Constructeur | Gestion CRUD des contrats, montant total non négatif |
| EmployeServiceImpl | IEmployeRepository | Constructeur | Gestion CRUD des employés et validation du nom et du rôle |
| EquipementServiceImpl | IEquipementRepository | Constructeur | Gestion CRUD des équipements et validation du libellé |
| MaintenanceServiceImpl | IMaintenanceRepository | Constructeur | Gestion CRUD des maintenances et contrôle des dates |
| PaiementServiceImpl | IPaiementRepository | Constructeur | Consultation des paiements uniquement |
| ReservationServiceImpl | IReservationRepository | Constructeur | Gestion CRUD des réservations et contrôle des dates |
| VehiculeServiceImpl | IVehiculeRepository | Constructeur | Gestion CRUD des véhicules et validation du tarif journalier |

## C. Messages d'erreur du conteneur Spring

### Message 1 — Bean introuvable

**Erreur :** `NoSuchBeanDefinitionException`

Un contrôleur a besoin d'un bean de type `IContratService`, mais Spring ne le trouve pas.

**Deux causes possibles :**

1. L'annotation `@Service` est absente de la classe `ContratServiceImpl`.
2. La classe `ContratServiceImpl` se trouve en dehors du package analysé par Spring.

**Correction :**

Ajouter `@Service` et placer la classe dans `tn.esprit.autoloc.service`, sous le package racine de l'application.

### Message 2 — Plusieurs beans du même type

**Erreur :** `NoUniqueBeanDefinitionException`

Spring trouve deux implémentations possibles :

- `emailNotificateur`
- `smsNotificateur`

Le conteneur ne sait pas quelle implémentation injecter.

**Solution 1 : `@Primary`**

Ajouter `@Primary` sur la classe d'implémentation qui doit être sélectionnée par défaut.

**Solution 2 : `@Qualifier`**

Ajouter `@Qualifier("emailNotificateur")` sur le paramètre du constructeur pour choisir explicitement le bean.

### Message 3 — Dépendance circulaire

**Erreur :** `BeanCurrentlyInCreationException`

`ClientServiceImpl` dépend de `ReservationServiceImpl`, qui dépend à son tour de `ClientServiceImpl`.

Lors de l'injection par constructeur, chaque service a besoin de l'autre pour être créé. Le conteneur ne peut donc pas résoudre ce cycle.

**Correction :**

Revoir la conception en extrayant les fonctionnalités communes dans un troisième service indépendant.

Ainsi, les deux services pourront dépendre de ce nouveau composant sans se référencer mutuellement.

## D. Anomalies SonarQube for IDE

Nous avons analysé le code des packages `service` et `exception` avec SonarQube for IDE.

Trois anomalies ont été identifiées lors des expériences dans `ContratServiceImpl`.

| Règle | Anomalie détectée | Explication | Correction |
|---|---|---|---|
| S6813 | Field dependency injection should be avoided | L'injection par attribut cache les dépendances et empêche l'utilisation d'un champ `final`. | Remplacement de `@Autowired` sur l'attribut par `@RequiredArgsConstructor` et un champ `final`. |
| S1481 | Unused local variable | La variable `nombreContrats` était déclarée sans être utilisée. | Suppression de la variable inutile. |
| S1854 | Useless assignment | Une valeur était affectée à `nombreContrats` sans jamais être lue. | Suppression de l'affectation inutile. |

### Expérience complémentaire — Exception générique

Nous avons temporairement remplacé `ResourceNotFoundException` par `RuntimeException` dans `findById()`.

La règle S112 était attendue selon le support, mais elle n'a pas été signalée par notre configuration SonarQube.

Nous avons restauré `ResourceNotFoundException`, plus précise pour représenter une ressource introuvable.

## E. Questions de compréhension

### 1. Où est le `new` de `ContratServiceImpl` et qui l'exécute ?

Nous n'écrivons pas explicitement `new ContratServiceImpl(...)` dans notre application.

Le conteneur Spring IoC crée automatiquement le bean au démarrage grâce à l'annotation `@Service`.

Spring fournit ensuite l'instance de `IContratRepository` au constructeur de `ContratServiceImpl`.

### 2. Pourquoi un contrôleur dépendra-t-il de `IContratService` plutôt que de `ContratServiceImpl` ?

Un contrôleur doit dépendre d'une interface pour réduire le couplage.

Cela permet de remplacer l'implémentation du service sans modifier le contrôleur.

Cette organisation facilite également les tests et la maintenance.

### 3. Pourquoi un service singleton doit-il rester sans état ? Donner un exemple de bug.

Un service singleton est partagé entre plusieurs utilisateurs et plusieurs requêtes.

S'il conserve des informations propres à une requête dans un attribut modifiable, ces informations peuvent être écrasées par une autre requête.

**Exemple :**

Si `ContratServiceImpl` possède un attribut `contratCourant`, deux utilisateurs peuvent modifier cette valeur simultanément.

L'un pourrait alors traiter le contrat de l'autre.

Pour éviter cela, les données temporaires doivent rester dans les variables locales des méthodes.

### 4. Pourquoi `update()` charge-t-il l'entité existante avant d'appeler `save()` ?

La méthode `update()` récupère d'abord l'entité existante avec `findById()`.

Elle modifie ensuite uniquement les attributs autorisés avant d'enregistrer l'entité.

Cela permet de préserver l'identifiant et les associations existantes.

Dans notre projet, par exemple, nous évitons de remplacer directement la collection des paiements d'un contrat afin de ne pas provoquer de suppressions involontaires avec `orphanRemoval = true`.

### 5. Quelle différence entre `@Component` et `@Bean` ? Entre `@Primary` et `@Qualifier` ? Pourquoi `IPaiementService` est-il en lecture seule ?

**`@Component` et `@Bean` :**

`@Component` est placé sur une classe pour que Spring la détecte automatiquement lors du scan.

`@Bean` est placé sur une méthode d'une classe de configuration pour déclarer explicitement un objet géré par Spring.

**`@Primary` et `@Qualifier` :**

`@Primary` désigne le bean préféré par défaut lorsque plusieurs implémentations du même type existent.

`@Qualifier` permet de sélectionner explicitement un bean précis lors de l'injection.

**Pourquoi `IPaiementService` est-il en lecture seule ?**

Le service Paiement expose seulement `findById()` et `findAll()`.

Les opérations de création, de modification et de suppression des paiements doivent passer par la logique métier du Contrat.

Cette conception centralise la gestion des paiements et préserve la cohérence entre les contrats et leurs paiements.
