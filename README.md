# Database exam project(Choose your fate)

## Running the databases

Start the database containers in detached mode:

```bash
docker compose up -d
```

If you need a clean MySQL reseed, recreate the Docker volumes:

```bash
docker compose down -v
docker compose up -d
```

## Running the backend

With the database containers running, start the Spring Boot application locally from the backend project.

Swagger UI is available at:

[http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)

Deployed Swagger UI is available at:

https://rpg-choose-your-fate.onrender.com/swagger-ui/index.html#/

## Migration endpoints

Neo4j migration:

```http
POST http://localhost:8080/api/migrations/neo4j?clearExisting=true
```

Neo4j integrity check:

```http
GET http://localhost:8080/api/migrations/neo4j/integrity
```

MongoDB migration:

```http
GET http://localhost:8080/migration/run
```

## SQL scripts

All SQL scripts are located in the [rpg_mysql](C:\Users\pf\Desktop\Skole\Databases\projects\mandetory\RPG_Choose_Your_Fate\rpg_mysql) folder.

The local Docker MySQL setup runs the scripts in this order:

1. `01_create_schema.sql`
2. `02_procedures.sql`
3. `03_functions.sql`
4. `04_triggers.sql`
5. `05_seed_data.sql`
6. `06_seed_mock_data.sql`
7. `07_seed_scene_choice_expansion.sql`
8. `08_security_roles.sql`
9. `09_events.sql`

