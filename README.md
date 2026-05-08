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

