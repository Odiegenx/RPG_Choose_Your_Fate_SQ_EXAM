# RPG Choose Your Fate SQ

To run the SQL containers detached:

```bash
docker compose up -d
```

With the spring application running: http://localhost:8080/swagger-ui/index.html#/

All our SQL scripts are in rpg_mysql folder

MySQL seed data is available in `rpg_mysql/06_seed_data.sql`.
The Software Quality version uses one primary MySQL container on port `3307` and one secondary MySQL container on port `3308`.

The availability design is intentionally scoped:

- primary SQL is the normal active database
- secondary SQL is the failover database
- successful primary writes create application-level replication jobs
- replication is asynchronous, so eventual consistency is accepted
- the primary health monitor checks primary on a fixed interval
- emergency failover tries to drain queued replication jobs before switching to secondary
- manual failover requires the replication queue to be empty
- failback to primary is manual and should happen during a maintenance window

Availability status endpoints are exposed under:

```text
GET  /availability/status
POST /availability/failover
POST /availability/failback/begin
POST /availability/failback/complete
```

If the MySQL Docker volumes already exist, reload the schema and seed data with:

```bash
docker compose down -v
docker compose up -d
```
