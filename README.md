# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

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
[![Untitled]([https://github.com/user-attachments/assets/d82b6b87-1135-4146-ab42-f9dc29ee3fab](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtG5Dgbl0e7L4vN4favrTbbV8HYsgoU+YlsOtwvp8Tzvksr59AC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm6ZIUgatLPqMJpEuGFoctyvIGoKwowKK4qutKSaXkh5SMdojrOgSREsqUJRIAAZpYVT6K8SzkQK2jTFB7x4XKBGCdRxGet6QYBkGIbseakYctGMCxsGfH4Z2wFpphPLZrmmDWZ2AFXE+I6jAuk59NOs7NuOrawe2IKnNkPYwP2g69G5YEef5X7eUGvnznFbbLqu3h+IEXgoOge4Hr4zDHukmSYKFF5FNQ17SAAoru1X1NVzQtAY6gJGg3Q+Y2uWmABbLWeUnVzjAphOYhTryjAqH2AVul1l1aDYvxibqUyxEwKJElmbNM7zZiw0GRGhSWjA9ExnpfEHc5qnlGZ8aqRqQmkjAkLDBANBbYN6B7aGGkcUdHLPRsb2GJ9aBLQRybwSWmEFfZCDMMNjlQ8FXElFcewwMNvWcV2ORgH2A5DhjiNpZ4GUbpCtq7tCMAAOKjqyRWnqV57MGN160-VTX2KOHWJfNPVXoBZxAiWoOI6NqPgpN0LbUli2WYRv1PRtlgffzc7fZdRnlKdpnnfIP2rRxY3S7dFn3UrxtPeSYD06Mqiwlrj2Hey5SRBYqA0HTDPg31yPlNTsRwwjI3I1dlVpj0Uw8w74yVP0scoAAktI8cAIy9gAzAALE8J6ZAaFYTF8OgIKADZF+BfRfEnAByo4lxjjTE0FnZlfj4WE70McM-HFSJ6OqcZ9nedTAX+oUfcNdPGXFdVx5JdPPXjc183GNmJw6XroE2A+FA2DcPAuqZD7owpMVZ542yLmVLUDTc7zwQa+gQ4r6M-5C-7otpqDb+jg3D+Ycf432ujALSmR7YoDlvNWY78UAK0titM07p1qUHEmrHSoNnbK1dsdPW5tDba1NhNQhwA-ZWxQeUCBKAoGwngTg62eCAbABtGfFADpiFSwmlAwUFCgIB2Pl6TIIdMbANTBHNGpZe6jGHuUTOucYAb2xiFVmBNIrRz6EnORMAFE5yUcNUma5MoBEsCgZUEBkgwAAFIQB5OwwIc8QANhZtfdmaZqiUjvC0JOfM5pziHIfYAZioBwAgKhKAcCh7SE-pHb+qYBov3ar0IJISwkRKibImJEtw4kOQjAAAVnYtAMC5yzFSZQdJ0BMkp2kIgpCD1cHlFVurfxX19ou1orrHkZ04wXU6TjfCN0DbkP4TIF2JE7ajnodEqiTCuknR6ewpiYRtHSCNigyR0teF8TGQdcofgtCQOmTs+QsxWIoHKeXNJ4ToCMM2TrFi2AjmGFOeQrhFVxr5LeVjJBIsEnsNETkkBgyhbozbjjDu6iiamCMeTLKUBgldi9LAYA2BD6EHiIkC+zMO6gMjuUCoNU6oNSasYUwEKBE-3KIgFF2EMTiIQtw-JIBuB4CdoLBplDwwHPtMACAHLtb-XdkDb2BpxmGz2RMmASdZACrmQ84V6ZXreygRK0ZitkE8pgNOOVgqBlKsiCqwwZl1UUs1f84ENK2VZjULmYao13HgpUYUKFXcNFLi3kAA))
](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtG5Dgbl0e7L4vN4favrTbbV8HYsgoU+YlsOtwvp8Tzvksr59AC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm6ZIUgatLPqMJpEuGFoctyvIGoKwowKK4qutKSaXkh5SMdojrOgSREsqUJRIAAZpYVT6K8SzkQK2jTFB7x4XKBGCdRxGet6QYBkGIbseakYctGMCxsGfH4Z2wFpphPLZrmmDWZ2AFXE+I6jAuk59NOs7NuOrawe2IKnNkPYwP2g69G5YEef5X7eUGvnznFbbLqu3h+IEXgoOge4Hr4zDHukmSYKFF5FNQ17SAAoru1X1NVzQtAY6gJGg3Q+Y2uWmABbLWeUnVzjAphOYhTryjAqH2AVul1l1aDYvxibqUyxEwKJElmbNM7zZiw0GRGhSWjA9ExnpfEHc5qnlGZ8aqRqQmkjAkLDBANBbYN6B7aGGkcUdHLPRsb2GJ9aBLQRybwSWmEFfZCDMMNjlQ8FXElFcewwMNvWcV2ORgH2A5DhjiNpZ4GUbpCtq7tCMAAOKjqyRWnqV57MGN160-VTX2KOHWJfNPVXoBZxAiWoOI6NqPgpN0LbUli2WYRv1PRtlgffzc7fZdRnlKdpnnfIP2rRxY3S7dFn3UrxtPeSYD06Mqiwlrj2Hey5SRBYqA0HTDPg31yPlNTsRwwjI3I1dlVpj0Uw8w74yVP0scoAAktI8cAIy9gAzAALE8J6ZAaFYTF8OgIKADZF+BfRfEnAByo4lxjjTE0FnZlfj4WE70McM-HFSJ6OqcZ9nedTAX+oUfcNdPGXFdVx5JdPPXjc183GNmJw6XroE2A+FA2DcPAuqZD7owpMVZ542yLmVLUDTc7zwQa+gQ4r6M-5C-7otpqDb+jg3D+Ycf432ujALSmR7YoDlvNWY78UAK0titM07p1qUHEmrHSoNnbK1dsdPW5tDba1NhNQhwA-ZWxQeUCBKAoGwngTg62eCAbABtGfFADpiFSwmlAwUFCgIB2Pl6TIIdMbANTBHNGpZe6jGHuUTOucYAb2xiFVmBNIrRz6EnORMAFE5yUcNUma5MoBEsCgZUEBkgwAAFIQB5OwwIc8QANhZtfdmaZqiUjvC0JOfM5pziHIfYAZioBwAgKhKAcCh7SE-pHb+qYBov3ar0IJISwkRKibImJEtw4kOQjAAAVnYtAMC5yzFSZQdJ0BMkp2kIgpCD1cHlFVurfxX19ou1orrHkZ04wXU6TjfCN0DbkP4TIF2JE7ajnodEqiTCuknR6ewpiYRtHSCNigyR0teF8TGQdcofgtCQOmTs+QsxWIoHKeXNJ4ToCMM2TrFi2AjmGFOeQrhFVxr5LeVjJBIsEnsNETkkBgyhbozbjjDu6iiamCMeTLKUBgldi9LAYA2BD6EHiIkC+zMO6gMjuUCoNU6oNSasYUwEKBE-3KIgFF2EMTiIQtw-JIBuB4CdoLBplDwwHPtMACAHLtb-XdkDb2BpxmGz2RMmASdZACrmQ84V6ZXreygRK0ZitkE8pgNOOVgqBlKsiCqwwZl1UUs1f84ENK2VZjULmYao13HgpUYUKFXcNFLi3kAA)

