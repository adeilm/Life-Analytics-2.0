# Life Analytics 2.0 – Rapport de Conception

*(Draft structure – à compléter et adapter selon les exigences de ton enseignant)*

---

## 1. Introduction

### 1.1 Contexte et motivation

La vie d’un étudiant en cycle d’ingénieur est marquée par une accumulation de tâches, de projets, d’examens, d’activités personnelles (sport, santé, loisirs) et de contraintes financières. En pratique, la planification manuelle dans Google Calendar, des to‑do lists dispersées et des notes non structurées conduisent souvent à l’oubli de tâches, un manque de visibilité sur la charge de travail et une difficulté à suivre ses objectifs à moyen et long terme.

**Life Analytics 2.0** est une plateforme de type *“Personal Life OS”* centrée sur :

- La centralisation de données personnelles (études, habitudes, santé, finances, objectifs) dans une base de données structurée.
- L’intégration avec des modèles de langage (LLM) capables de transformer des descriptions naturelles de la journée en données JSON structurées.
- La génération automatique de plannings (fichiers `.ics` ou intégration Google Calendar) et de statistiques sur les habitudes et la performance.

L’objectif principal est de réduire la friction cognitive : l’utilisateur parle à une IA, et le système se charge de traduire cette description en tâches, événements, suivis d’habitudes et analytics.

### 1.2 Objectifs pédagogiques

Dans le cadre du module **Services Web**, ce projet vise à :

- Concevoir et implémenter un backend REST en **Spring Boot + Maven**.
- Exposer des **services RESTful** pour la gestion des entités métiers (habitudes, santé, études, finances, calendrier).
- Implémenter des **endpoints d’analytics** basés sur des requêtes SQL agrégées.
- Préparer une architecture évolutive vers des **microservices** et une version **NoSQL** (MongoDB) dans un second projet.

---

## 2. Description générale du système

### 2.1 Vision globale

Life Analytics 2.0 se positionne comme un backend centralisé qui :

1. Reçoit des données structurées (JSON) décrivant la journée, les tâches, les examens, les dépenses, etc.
2. Stocke ces données dans une base SQL.
3. Expose des API pour consulter ces données et générer des indicateurs.
4. Génère des fichiers de calendrier (`.ics`) ou interagit avec l’API Google Calendar pour créer/mettre à jour des événements.

Le système ne dépend d’aucun fournisseur d’IA particulier : tant qu’un LLM (Gemini, modèle local, etc.) est capable de produire le JSON attendu, il peut servir de “front-end intelligent” au projet.

### 2.2 Acteurs

- **Utilisateur** : étudiant qui souhaite organiser ses études, objectifs et habitudes, et visualiser des statistiques sur sa vie.
- **Client IA / LLM** : modèle de langage (local ou via API) chargé de :
  - lire la description naturelle de la journée/semaines
  - produire du JSON structuré selon un schéma défini
  - éventuellement interpréter les réponses du backend (conflits de calendriers, suggestions, etc.).
- **Google Calendar** (optionnel) : service externe permettant d’importer/exporter des événements.

### 2.3 Cas d’utilisation principaux

- UC1 : Gérer les habitudes et les logs (création, mise à jour, suppression, suivi).
- UC2 : Gérer les métriques de santé (sommeil, humeur, stress, énergie).
- UC3 : Gérer les cours, examens, tâches et sessions d’étude.
- UC4 : Gérer les dépenses et budgets.
- UC5 : Ingestion d’un “daily log” généré par l’IA (JSON) et création automatique des entités correspondantes.
- UC6 : Générer des analytics (habitudes, temps d’étude, santé, finances).
- UC7 : Exporter un planning (étude, examens, objectifs) sous forme de fichier `.ics` pour Google Calendar.
- UC8 : Synchroniser les événements existants de Google Calendar vers le backend (optionnel).

Un diagramme de cas d’utilisation en Mermaid peut être ajouté pour illustrer ces interactions.

---

## 3. Architecture logicielle

### 3.1 Architecture globale

L’architecture retenue est un **monolithe modulaire** en Spring Boot, structuré en trois grands sous-domaines :

- **Tracking** : gestion des événements de vie (habits, santé, activités, tâches, dépenses).
- **Calendar** : gestion des événements de calendrier, export `.ics`, synchronisation avec Google Calendar.
- **Analytics** : calcul des indicateurs et agrégations (habitudes, temps, finances, objectifs).

À cela s’ajoute un module **Intake** chargé de recevoir les JSON générés par l’IA (daily logs) et de les transformer en opérations sur les services de Tracking et Calendar.

Cette organisation rend le projet **microservices-ready** : chaque sous-domaine pourrait être extrait dans une application Spring Boot indépendante à l’avenir.

### 3.2 Schéma de couches

- **Couche Controller (API REST)** : expose des endpoints `/api/...` pour les différentes entités et cas d’utilisation.
- **Couche Service** : contient la logique métier (validation, calculs, vérification de conflits, agrégations simples).
- **Couche Repository** : accès aux données via Spring Data JPA (MySQL).
- **Couche Intégration externe** : éventuellement, clients vers l’API Google Calendar.

### 3.3 Diagramme de classes (vue simplifiée)

*(Référence au diagramme Mermaid déjà conçu : Habits, HabitLog, HealthMetric, CalendarEvent, ActivityLog, Course, Exam, Task, Expense, Goal, GoalProgress + services IntakeService, TrackingService, CalendarService, AnalyticsService.)*

---

## 4. Modèle de données (SQL)

### 4.1 Principales entités

- **Habit**(id, name, category, target_per_week)
- **HabitLog**(id, habit_id, date, value, note)
- **HealthMetric**(id, timestamp, sleep_hours, mood_score, stress_level, energy_level, notes)
- **Course**(id, name, code)
- **Exam**(id, course_id, date_time, location, weight)
- **Task**(id, title, description, status, priority, due_date)
- **ActivityLog**(id, activity_type, start_time, end_time, duration_minutes, tags, optional event_id)
- **CalendarEvent**(id, external_id, title, start_time, end_time, source, type)
- **Expense**(id, amount, category, date, note)
- **Goal**(id, title, domain, target_value, target_date)
- **GoalProgress**(id, goal_id, date, current_value)

### 4.2 Relations principales

- Habit 1–N HabitLog.
- Course 1–N Exam.
- CalendarEvent 0–N ActivityLog (un ActivityLog peut être lié à un événement de calendrier ou être autonome).
- Goal 1–N GoalProgress.

Un schéma ER (MySQL Workbench ou équivalent) pourra être inséré dans le rapport.

---

## 5. API REST

### 5.1 Module Tracking

- `/api/habits` : CRUD sur les habitudes.
- `/api/habits/{id}/logs` : consultation et insertion des HabitLog.
- `/api/health-metrics` : enregistrement et consultation des métriques de santé (avec filtres de dates).
- `/api/tasks` : CRUD sur les tâches.
- `/api/expenses` : CRUD sur les dépenses.
- `/api/activity-logs` : enregistrement des activités (étude, sport, travail, etc.).

### 5.2 Module Intake (Ingestion IA)

- `/api/intake/daily-log` :
  - Méthode : POST
  - Corps : JSON structuré issu de l’IA décrivant la journée (sessions d’étude, dépenses, humeur, etc.).
  - Rôle : transformer ce JSON en appels aux services de Tracking et, éventuellement, créer des tâches ou sessions planifiées.

### 5.3 Module Calendar

- `/api/calendar-events` : CRUD basique sur les événements de calendrier.
- `/api/calendar/export` : génération d’un fichier `.ics` entre deux dates.
- `/api/calendar/sync` (optionnel) : synchronisation avec Google Calendar sur un intervalle de temps.

### 5.4 Module Analytics

- `/api/analytics/habits/weekly` : statistiques hebdomadaires de complétion des habitudes.
- `/api/analytics/health/trends` : tendances des métriques de santé sur une période.
- `/api/analytics/time-by-activity` : temps passé par type d’activité.
- `/api/analytics/goals/{id}` : progression d’un objectif donné.

Chaque endpoint sera détaillé dans le rapport final (format JSON d’entrée/sortie, paramètres, exemples Postman).

---

## 6. Gestion des conflits de calendrier

### 6.1 Problématique

Lorsqu’un nouvel événement (par exemple une session d’étude) est proposé pour être ajouté au calendrier, il peut se trouver en conflit avec un événement existant (réunion, cours, rendez-vous médical, etc.).

### 6.2 Stratégie adoptée

- Le backend **ne crée jamais** un événement sans vérifier les conflits.
- Lorsqu’une requête de création d’événement arrive (via `/api/calendar-events` ou via un daily log interprété), le service Calendar :
  1. Interroge la base de données (et, si nécessaire, Google Calendar) pour l’intervalle demandé.
  2. Détecte les chevauchements.
  3. En cas de conflit, renvoie une réponse de type **CONFLICT** contenant :
     - le créneau demandé,
     - la liste des événements en conflit,
     - une liste de créneaux alternatifs proposés.

Ce mécanisme permet à l’IA ou à l’utilisateur de choisir une alternative avant la création finale de l’événement.

---

## 7. Intégration avec l’IA et Google Calendar

### 7.1 Rôle de l’IA

- L’IA ne constitue pas la logique principale du système.
- Elle sert de **traducteur** entre le langage naturel de l’utilisateur et les structures JSON consommées par le backend.
- La logique métier (vérification de conflits, calcul d’analytics, génération de fichiers `.ics`) reste entièrement dans le backend Spring Boot.

### 7.2 Intégration IA

Deux modes sont envisagés :

1. **Mode manuel** : l’utilisateur copie/colle le JSON généré par l’IA dans Postman ou un petit client et l’envoie à `/api/intake/daily-log`.
2. **Mode automatisé** : un script externe (Python, Node.js, etc.) appelle l’API de l’IA (Gemini, modèle local…) puis envoie directement le JSON vers le backend.

### 7.3 Intégration Google Calendar

- **Export** : le backend génère un fichier `.ics` à partir des événements planifiés (Exam, Planned Study Sessions, etc.). L’utilisateur importe ce fichier dans Google Calendar.
- **Synchronisation (optionnel)** : le backend peut consommer l’API Google Calendar pour :
  - récupérer les événements existants et les stocker dans `CalendarEvent` ;
  - éviter des conflits lors de la génération de nouveaux événements.

---

## 8. Sécurité, performances et limites

### 8.1 Sécurité

- Authentification et autorisation peuvent être simplifiées pour le projet académique (un seul utilisateur). Cependant, l’architecture pourra évoluer vers une gestion de comptes.
- Les clés d’API (IA, Google) ne doivent pas être versionnées dans le code source, mais stockées dans des variables d’environnement.

### 8.2 Performances

- Le volume de données attendu est faible (usage personnel), donc MySQL est largement suffisant.
- Des index sont prévus sur les colonnes de dates et de types d’activités pour accélérer les requêtes d’analytics.

### 8.3 Limites

- Le système dépend de la qualité du JSON généré par l’IA : en cas de format invalide, le backend rejettera la requête.
- L’intégration complète avec Google Calendar (OAuth, refresh tokens, etc.) peut être partiellement mockée dans la version académique.

---

## 9. Conclusion et perspectives

Ce projet propose une approche intégrée pour la gestion des études, des habitudes, de la santé, des finances et des objectifs dans un contexte étudiant. En séparant clairement les rôles :

- **IA = traducteur** du langage naturel vers JSON,
- **Backend Spring Boot = cerveau métier**,
- **Base de données = mémoire persistante**,
- **Google Calendar = interface de visualisation du temps**,

Life Analytics 2.0 offre une base solide pour construire un véritable “assistant de vie” piloté par les données.

Les perspectives d’évolution incluent :

- Extraction des modules Tracking, Calendar et Analytics en **microservices** distincts.
- Déploiement d’une version **NoSQL** (MongoDB) pour les événements génériques et l’analytics avancée.
- Ajout d’une couche IA plus riche pour la recommandation (planning automatique d’étude, alertes intelligentes, détection de périodes de surcharge ou de baisse de performance).
- Développement d’une interface web ou mobile pour remplacer Postman et offrir une expérience utilisateur complète.

