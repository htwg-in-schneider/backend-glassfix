# GlassFix Backend

GlassFix is a web application that connects users seeking repairs for damaged glass objects with experts who can provide professional repair offers.

## How to Run

To run the application, ensure you have Maven installed. Then execute the following command in the project root directory:

```sh
mvn spring-boot:run
```

The application will start on `http://localhost:8081`.

## Iterations

### Iteration 1a: First REST Controller

- basic project configuration in `application.properties`
- a first simple REST-Controller `AnfrageController` for GET request to `/api/anfrage` that returns a list of strings.
- test e.g. with using `curl`: `curl http://localhost:8081/api/anfrage` or bruno (see bruno project in `src/test/bruno`)

### Iteration 1b: JSON (de)serialization

- REST-Controller `AnfrageController` supporting GET and POST requests to `/api/anfrage`
- using Java Objects instead of strings
- test GET with using `curl`: `curl http://localhost:8081/api/anfrage`
- test POST with `curl -X POST http://localhost:8081/api/anfrage -H 'Content-Type: application/json' -d '{"id" : 3,
  "kategorie" : "C",
  "kunde" : "Felix Muster",
  "beschreibung" : "Mein Glas ist kaputt.",
  "fragen" : "Wie kann ich das Problem beheben? Was kostet die Reparatur? Wie lange dauert die Reparatur?",
  "bildUrl" : "https://example.com/images/example3.jpg"
}'` or in bruno

### Iteration 1c: REST-Controller with model class

- introduced model classes `Anfrage` and `AnfrageStatus`
- `AnfrageController` returns some example data that can be consumed by the frontend

### Iteration 2: CORS Configuration

In this iteration, a `WebConfig` for a global CORS (Cross-Origin Resource Sharing) configuration was added to the backend. This allows the frontend application, which may be served from a different origin, to access the backend APIs without running into cross-origin issues.

### Iteration 3: Database Integration

In this iteration, the application was updated to integrate with a database. The following changes were made:

1. **Database Configuration**:
   - Added support for H2 and MariaDB (`pom.xml`)
   - Updated the `application.properties` to include database configurations
2. **Product Entity**:
   - The `Anfrage` class was annotated with JPA annotations to map it to a database table.
   - Added missing `equals` and `hashCode` methods.
3. **Anfrage Repository**:
   - A new `AnfrageRepository` interface was created to handle database operations for the `Anfrage` entity.
4. **Data Loader**:
   - `config.DataLoader` is a CommandLineRunner that is run during application startup and used to fill initial data into the database. It is only run when no `Anfrage` are defined yet.
5. **Anfrage Controller**:
   - Updated the `/api/anfrage` endpoint to fetch `Anfrage` from the database instead of returning hardcoded values.

### Iteration 4: CRUD for Anfrage

In this iteration the backend was extended to support full CRUD operations for `Anfrage`.

- CRUD Endpoints (REST)
  - GET /api/anfrage — list all `Anfrage`
  - GET /api/anfrage/{id} — fetch a single product (404 if not found)
  - POST /api/anfrage — create a `Anfrage` (returns 201)
  - PUT /api/anfrage/{id} — update an existing `Anfrage` (404 if not found)
  - DELETE /api/anfrage/{id} — delete a `Anfrage` (204 on success, 404 if not found)
- No Validation: Entities are not validated before written to the database!
- Example request to be used in bruno were added to `src/test/bruno`

## Iteration 5: Added 1:n relation Anfrage - Benutzer

- Added `Benutzer` entity with bidirectional relation to `Anfrage`, see `Anfrage#kunde`, `Anfrage#experte` and `Benutzer#anfragenKunde`, `Benutzer#anfragenExperte` with the corresponding Annotations.
- **Important**: In order to avoid endless recursion when serializing these types to JSON in the rest controller, `Benutzer#anfragenKunde` and `Benutzer#anfragenExperte` have `@JsonIgnore`. Hence, all REST endpoints returning `Benutzer` do not include the `Anfragen` of a `Benutzer`.
  - To get the `Anfrage` of a `Benutzer`, you have to call `/api/anfrage/kunde/<KundeID>` or `/api/anfrage/experte/<experteID>`.
- Also note the `@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })` on every Entity which avoids problems with serialization JPA entities to JSON.
- Added Create, Read, Update and Delete for `Benutzer` in `BenutzerController`.
  - To create a `Anfrage` for the `Benutzer` with id 1, POST this content to `/api/anfrage`:

      ```json
      {
      "kategorie" : "C",
      "kunde" : {
         "id" : 1
      },
      "beschreibung" : "Mein Glas ist kaputt.",
      "fragen" : "Wie kann ich das Problem beheben? Was kostet die Reparatur? Wie lange dauert die Reparatur?",
      "bildUrl" : "https://example.com/images/smartphone.jpg"
      }
      ```

  - this and other example requests can be found in `src/test/bruno` which can be opened with the [Bruno API Client](https://www.usebruno.com/).
- In `DataLoader`, some example of `Benutzer` are added to the database.