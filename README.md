# AWS Photo Lab — Java

Build a photo-sharing application while learning AWS from the ground up. This first milestone runs entirely on your computer, makes no AWS calls, and creates no cloud resources. Java 17 or later is required; no external libraries or build tool are needed for this first step.

## Run locally

From this directory, check `java -version` and `javac -version`. If either command is missing, install a JDK (not just a JRE). Then run:

```sh
mkdir -p build
javac --add-modules jdk.httpserver -d build src/main/java/lab/Main.java
java --add-modules jdk.httpserver -cp build lab.Main
```

Open http://127.0.0.1:8080. Stop the server with Ctrl+C. To use a different port, start it with `PORT=8081 java --add-modules jdk.httpserver -cp build lab.Main`.

Try `curl http://127.0.0.1:8080/health` and `curl http://127.0.0.1:8080/api/photos`. The API currently returns an empty array; uploading and storage are later milestones.

## Your first exercises

1. Run the app and explain what the browser, HTTP server, and endpoint each do.
2. Change the page title and refresh. Why doesn't this change require recompiling Java?
3. Change the JSON returned by `/health`, recompile, and restart. Why is a restart needed?
4. Request a missing path and observe its HTTP status: `curl -i http://127.0.0.1:8080/missing`.

See [the learning roadmap](docs/ROADMAP.md) for the progression. The application uses the JDK's small HTTP server as a learning scaffold; it is not configured as a public production service.
