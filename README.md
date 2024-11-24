# Wire Reminders Bot

This is a bot that can create reminders for you and your group, and send you a message when the reminder is due.

> [!IMPORTANT]  
> As of now, the bot only supports a maximum of 3 active reminders per group, and recurrent reminders are supported only up to 5 repetitions.

## Getting started

### The basics of `/remind` command:

- `/remind to <"what"> <"when">`: Sets a reminder for the group conversation, the reminder will be sent to the group when it's due.

### `"When"` syntax or setting the time for the reminder:

- By default, all reminders should use 24 hrs format, including minutes.
- If the reminder is recurrent or to happen relative to today, you must add the time in the format `<hh:mm> "21:15"`.

> [!NOTE]  
> You can set reminders for the entire group, but not for someone else (how rude would that be?).
>
> You can also set reminders to repeat by the day (ex. every Monday), repetitions by time are not supported (ex. every hour).

### Examples:

|                    | Command      | What                                           | When                      |
|--------------------|--------------|------------------------------------------------|---------------------------|
| one time reminder  | `/remind to` | `"Fill in your invoices by end of day"`        | `"tomorrow at 17:30"`     |
| one time reminder  | `/remind to` | `"Reply to HR email"`                          | `"in 10 minutes"`         |
| one time reminder  | `/remind to` | `"Travel back in time to not develop the bot"` | `"11/11/2150"`            |
| recurrent reminder | `/remind to` | `"Join the daily stand-up"`                    | `"every day at 10:00"`    |
| recurrent reminder | `/remind to` | `"Empty the unread emails"`                    | `"every friday at 17:00"` |

> [!TIP]  
> You can set reminders for yourself. To do so, you can use the commands in a private conversation, a 1:1 with the bot.

### Other helpful commands:

- `/remind help` (displays help about command usage)
- `/remind list` (list the active reminders set in the conversation)
- `/remind delete <reminder-identifier>` (deletes the target reminder, the identifier can be obtained from the list
  command)

## Technical details

### Bot Architecture

We are using a DDD-like architecture, but without the burden of defining a full DDD model (involving domains "experts"
and so on). The idea is to have a clear separation of concerns between the different layers of the application.
So each layer does the following:

- **Application**: Exposes the REST API and handles the HTTP requests, it's what the clients see.
- **Domain**: Contains the business logic, domain core entities, this layer is "clean" in other words, doesn't have any
  dependency on other layers or frameworks. To access the logic, we provide UseCases, so we can group them (kind of
  services + aggregates in DDD)
- **Infrastructure**: Contains the implementation of the domain repositories, and other external dependencies, like
  databases, queues technologies, framework configurations, etc.

<img src="https://imgpile.com/images/C7Q2Gj.png" width="800"/>

**Note**: To enforce layer dependency, can be split into different gradle modules, but for now, we are keeping it
simple.

### Bot Framework

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./gradlew build
```
It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./gradlew build -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./gradlew build -Dquarkus.package.type=native
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/reminders-bots-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling.

## Related Guides

- Flyway ([guide](https://quarkus.io/guides/flyway)): Handle your database schema migrations
- Quartz ([guide](https://quarkus.io/guides/quartz)): Schedule clustered tasks with Quartz
- Kotlin ([guide](https://quarkus.io/guides/kotlin)): Write your services in Kotlin
- RESTEasy Classic ([guide](https://quarkus.io/guides/resteasy)): REST endpoint framework implementing Jakarta REST and more
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC

## Provided Code

### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)
