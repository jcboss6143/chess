# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## Sequence Diagrams

- https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmIxnjeH4-jQOwjIxCKcARtIcAKDAAAyEBZIUWHME61D+s0bRdL0BjqPkaBZoqcxrL8-wcFcoGCkB-ojKpKDqfofw7NCjzAZJVBIjACAieKGJ2aJBJEmApJvoYu40vuDJMlOhlzt5d5LsKYoSm6MpymW7xKpgKrBhqbpGBAaikFaNo3kFDpJr6zpdjAPZ9tunlWf6ACSaBUCaSAcG6nJRjGcaFDpOXwMgqYwOmACMBE5qoebzNBRYlvUPjTJe0BIAAXiguwwHRTbDtlVk2RFW4eYKHn1DUSAAGaWE0JmaY59loC5ahgCsa3yKS8XqjAFVVcg64aTsxUgdU5WVdVtVESgDUoLGSnofCybtdhnVOD1oxjH1A0FmMw3QKN416pNM1zQtDEbV5vIjmOE4oM+8Tnpe15LYulQPmuAZk+teXtnp36oriUAZKoAGYEzH1SWBhHlsRpHfBRVH1iRqENi1YMlGAOF4QRhk0WRYwi4hYtC1jTGmF4vgBF4KDoDEcSJPrhvCfYvhYOJm2faWDTSBGgkRu0EbdD0CmqEpwyq0hIO6RZ+k++g5mwhhK0urZImWydFunudJLvbjC6jjAO37RQikFKT8Fq2ggV49lVOheKT50-IsrykHhR3RqxMyhwKWqGl1o5Lat7Lblnbdr2-Y46VpaPT9xOi2gANA-GUuVOJaZQ71-Lw0NxbIwqqPxOjs1LJrFMCq1W20y+9OdoK2-1PRGee1nFHk+3lNCrU0goNw6L71e2gV8al4j4nH4B6WhX9tzXetsXjaVtphcGssYC4XwqMTW2sWIBBROufw2BxQakEmiGAABxJUGhrb93qA0LBzs3b2CVN7T+udGwMWYrrfwHAADsbgnAoCcDECMwQ4A8QAGzwEJtgisRQIE215oQ1oHRSHkNXiPLMZC5gADklRwJ1qxSwj87KbCNkgBIYA1F9ggJogAUhAcUAi5gxGSKANUQiZYiJqGI5kckehyJQBQnOSEszYAQMANRUA4AQDslANYLiyrSGUQg-wXgfFaJ0VE+UiBgywGANgLxhA8gFBgDY8wdjpIOydi7N2xhqGYCAA
- https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmIxnjeH4-jQOwjIxCKcARtIcAKDAAAyEBZIUWHME61D+s0bRdL0BjqPkaBZoqcxrL8-wcFcoGCkB-ojKpKDqfofw7NCjzAZJVBIjACAieKGJ2aJBJEmApJvoYu40vuDJMlOhlzt5d5LsKYoSm6MpymW7xKpgKrBhqbpGBAaikFaNo3kFDpJr6zpdjAPZ9tunlWf6ACSaBUCaSAcG6nJRjGcaFDpOXwMgqYwOmACMBE5qoebzNBRYlvUPjTJe0BIAAXiguwwHRTbDtlVk2RFW4eYKHn1DUSAAGaWE0JmaY59loC5ahgCsa3yKS8XqjAFVVcg64aTsxUgdU5WVdVtVESgDUoLGSnofCybtdhnVOD1oxjH1A0FmMw3QKN416pNM1zQtDEbV5vIjmOE4oM+8Tnpe15LYulQPmuAZk+teXtnp36oriUAZKoAGYEzH1SWBhHlsRpHfBRVH1iRqENi1YMlGAOF4QRhk0WRYwi4hYtC1jTGmF4vgBF4KDoDEcSJPrhvCfYvhYOJm2faWDTSBGgkRu0EbdD0CmqEpwyq0hIO6RZ+k++g5mwhhK0urZImWydFunudJLvbjC6jjAjJgBQikFKT8Fq2ggV49lVOheKT50-IsrykHhR3RqxMyhwKWqGl1o5Lat7Lblnbdr2-Y46VpaPT9xOi2gANA-GUuVOJaZQ71-Lw0NxbIwqqPxOjs1LJrFMCq1W20y+9OdoK2-1PRGee1nFHk+3lNCrU0goNw6L71e2gV8al4j4nH4B6WhX9tzXetsXjaVtphcGssYC4XwqMTW2sWIBBROufw2BxQakEmiGAABxJUGhrb93qA0LBzs3b2CVN7T+uc-Y-1DmBKuIcvw2y7jAXaJccFzBjpbeOblE5UhvinNO58lLZ0ornfOyd7zFwlHXN+UUq5xVVLXMuwBkqpWYOlVumUC6LnDvlf+39O41HqIPZ6w9c5jyatQ6WHVupz1zPmReI0V4UXXpjRsDFt482shHGR8heEyH4fUVhnB2FqAxOI-ckjagUG4OASsCAWHeBmNgpUHpPFAOYaEj09F4G638BwAA7G4JwKAnAxAjMEOAPEABs8BCYpLmDAIoECmFGMaK0DopDyGrxHlmMhcwABySo4E61YpYR+dlNhGyQAkMA4y+wQCmQAKQgOKBphh-DJFAGqZpMtWnSSaMyOSPR+koAoTnJCWZsAIGAOMqAcAIB2SgGsU5ZVpAjIQf4LwtzpmzO+fKRAwZYDAGwNcwgeQChNPwYY6SDsnYuzdsYdxmAgA

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
