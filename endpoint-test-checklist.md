# Endpoint Test Checklist

This checklist is the current baseline for manual Swagger verification and later endpoint/integration tests.

## Setup

1. Start the containers with a fresh seed when needed:
   ```powershell
   docker compose down -v
   docker compose up -d
   ```
2. Start the backend.
3. Use Swagger UI.
4. Set `X-Data-Source: sql` when you want to be explicit. SQL is also the default.

## Admin Login

`POST /auth/login`

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Use the returned token in Swagger:

```text
Bearer <token>
```

## Character Tests As Admin

### Read all characters

`GET /api/characters`

Expected:
- `200`

### Read one character

`GET /api/characters/1`

Expected:
- `200`

### Create character

`POST /api/characters`

```json
{
  "accountId": 1,
  "chapterId": 1,
  "sceneId": 1,
  "raceDetailsId": 1,
  "name": "SwaggerCharacter"
}
```

Expected:
- `200`
- Save the returned id as `newCharacterId`

### Read created character

`GET /api/characters/{newCharacterId}`

Expected:
- `200`

## Character Details Tests As Admin

### Read all character details

`GET /api/character-details`

Expected:
- `200`

### Read seeded character details

`GET /api/character-details/1`

Expected:
- `200`

### Read auto-created details for a new character

`GET /api/character-details/{newCharacterId}`

Expected:
- `200`

Note:
- `character_details` are automatically created by SQL trigger logic when a character is created.

### Update character details

`PUT /api/character-details/{newCharacterId}`

```json
{
  "intelligence": 12,
  "charisma": 8,
  "fashion": 6
}
```

Expected:
- `200`

### Verify updated details

`GET /api/character-details/{newCharacterId}`

Expected:
- `200`
- Updated values are returned

## Character Path Tests As Admin

### Read all character paths

`GET /api/character-paths`

Expected:
- `200`

### Read seeded character path

`GET /api/character-paths/1`

Expected:
- `200`

### Read auto-created path for a new character

`GET /api/character-paths/{newCharacterId}`

Expected:
- `200`

Note:
- `character_path` is automatically created by SQL trigger logic when a character is created.

### Update character path summary

`PUT /api/character-paths/{newCharacterId}`

```json
{
  "summary": "Updated path summary from Swagger test."
}
```

Expected:
- `200`

### Verify updated path summary

`GET /api/character-paths/{newCharacterId}`

Expected:
- `200`
- Updated summary is returned

## Equipment Tests As Admin

### Read all equipment

`GET /api/equipment`

Expected:
- `200`

### Read seeded equipment

`GET /api/equipment/1`

Expected:
- `200`

### Read auto-created equipment for a new character

`GET /api/equipment/{newCharacterId}`

Expected:
- `200`

Note:
- `equipment` is automatically created by SQL trigger logic when a character is created.

### Update equipment

`PUT /api/equipment/{newCharacterId}`

```json
{
  "headItemId": 2,
  "chestItemId": 3,
  "legsItemId": 4
}
```

Expected:
- `200`

### Verify updated equipment

`GET /api/equipment/{newCharacterId}`

Expected:
- `200`
- Updated item ids are returned

## Race Details Tests As Admin

### Read all race details

`GET /api/race-details`

Expected:
- `200`

### Read one race details row

`GET /api/race-details/1`

Expected:
- `200`

### Create race details

`POST /api/race-details`

Expected:
- `200`
- Save the returned id as `newRaceDetailsId`

### Read created race details

`GET /api/race-details/{newRaceDetailsId}`

Expected:
- `200`

### Delete unreferenced race details

`DELETE /api/race-details/{newRaceDetailsId}`

Expected:
- `200` or `204`

### Attempt to delete referenced race details

`DELETE /api/race-details/1`

Expected:
- `400`

## Cleanup As Admin

### Delete character

`DELETE /api/characters/{newCharacterId}`

Expected:
- `200` or `204`

## Normal User Login

Use Astra:

`POST /auth/login`

```json
{
  "username": "astra",
  "password": "astra123"
}
```

Use the returned token in Swagger:

```text
Bearer <token>
```

Known ownership from seed:
- Astra owns account `1`
- Astra owns seeded character `1`

## Character Tests As Normal User

### Read all characters

`GET /api/characters`

Expected:
- `403`

### Read own character

`GET /api/characters/1`

Expected:
- `200`

### Read another user's character

`GET /api/characters/2`

Expected:
- `403`

### Create own character

`POST /api/characters`

```json
{
  "accountId": 1,
  "chapterId": 1,
  "sceneId": 1,
  "raceDetailsId": 1,
  "name": "AstraCharacter"
}
```

Expected:
- `200`
- Save returned id as `astraCharacterId`

### Attempt to create character for another account

`POST /api/characters`

```json
{
  "accountId": 2,
  "chapterId": 1,
  "sceneId": 1,
  "raceDetailsId": 1,
  "name": "ShouldFail"
}
```

Expected:
- `403`

## Character Details Tests As Normal User

### Read own details

`GET /api/character-details/1`

Expected:
- `200`

### Read another user's details

`GET /api/character-details/2`

Expected:
- `403`

### Read details for own newly created character

`GET /api/character-details/{astraCharacterId}`

Expected:
- `200`

### Update details for own character

`PUT /api/character-details/{astraCharacterId}`

```json
{
  "intelligence": 13,
  "charisma": 9,
  "fashion": 5
}
```

Expected:
- `200`

### Attempt to update another user's details

`PUT /api/character-details/2`

```json
{
  "intelligence": 99,
  "charisma": 99,
  "fashion": 99
}
```

Expected:
- `403`

## Character Path Tests As Normal User

### Read own character path

`GET /api/character-paths/1`

Expected:
- `200`

### Read another user's character path

`GET /api/character-paths/2`

Expected:
- `403`

### Update own character path

`PUT /api/character-paths/{astraCharacterId}`

```json
{
  "summary": "Astra updated her own path."
}
```

Expected:
- `200`

### Attempt to update another user's character path

`PUT /api/character-paths/2`

```json
{
  "summary": "Should fail"
}
```

Expected:
- `403`

## Equipment Tests As Normal User

### Read own equipment

`GET /api/equipment/1`

Expected:
- `200`

### Read another user's equipment

`GET /api/equipment/2`

Expected:
- `403`

### Update own equipment

`PUT /api/equipment/{astraCharacterId}`

```json
{
  "headItemId": 2,
  "chestItemId": null,
  "legsItemId": 4
}
```

Expected:
- `200`

### Attempt to update another user's equipment

`PUT /api/equipment/2`

```json
{
  "headItemId": 2,
  "chestItemId": 3,
  "legsItemId": 4
}
```

Expected:
- `403`

## Race Details Tests As Normal User

### Read all race details

`GET /api/race-details`

Expected:
- `200`

### Read one race details row

`GET /api/race-details/1`

Expected:
- `200`

### Attempt to create race details

`POST /api/race-details`

Expected:
- `403`

### Attempt to delete race details

`DELETE /api/race-details/1`

Expected:
- `403`

## Cleanup As Normal User

### Delete own created character

`DELETE /api/characters/{astraCharacterId}`

Expected:
- `200` or `204`

## Key Assertions

- Admin can access all relevant endpoints.
- Normal users cannot call `GET all` character endpoints.
- Normal users can only read and modify their own characters and character details.
- `character_details` are auto-created as part of SQL character creation.
- `character_path` is auto-created as part of SQL character creation.
- `equipment` is auto-created as part of SQL character creation.
- `PUT /api/character-details/{id}` works for the owner.
- `PUT /api/character-paths/{id}` works for the owner.
- `PUT /api/equipment/{id}` works for the owner.
- `RaceDetails` is read-only for normal users and admin-managed for create/delete.
- Referenced `RaceDetails` cannot be deleted.
- Unauthorized ownership access returns `403`.
