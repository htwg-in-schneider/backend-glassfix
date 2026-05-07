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