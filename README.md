# Mandatory-one-database

To run the container detached

docker compose up -d

With the spring application running: http://localhost:8080/swagger-ui/index.html#/

Neo4j migration can be triggered with:

POST http://localhost:8080/api/migrations/neo4j?clearExisting=true

MongoDB migration can be triggered with:
GET http://localhost:8080/migration/run

All our SQL scripts are in rpg_mysql folder

MySQL seed data is available in `rpg_mysql/05_seed_data.sql`.
If the MySQL Docker volume already exists, reload the schema and seed data with:

To run the application with databases use "docker compose up -d" and make sure the needed ports (They can be found in the docker compose file) are open.
Then simply run the Spring Boot application

docker compose down -v
docker compose up -d

