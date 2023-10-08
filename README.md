# OpenApi Usage Example

This repository contains the codebase that was used in the article
[How to Write a Robust REST API with OpenAPI](https://yonatankarp.com/blog/how-to-write-a-robust-rest-api-with-openapi) in the blog
[yonatankarp.com](https://yonatankarp.com).

## How to run

To build the project using Gradle run the following command in the project
root directory:

```shell
$ ./gradlew build
```

Once the project was successfully build, run the service with the following
command:

```shell
$ ./gradlew bootRun
```

When the service runs, you can call it with the following example:

```shell
$ curl "http://localhost:8080/v1/greet?name=Yonatan"
```

Response example:

```json
{"greet":"Hello, yonatan!"}
```

## Built With

- [OpenJdk 17](https://openjdk.java.net/projects/jdk/17/)
- [Kotlin](https://kotlinlang.org/)
- [OpenAPI](https://www.openapis.org/) - API Spec
- [SpringBoot 3.x.x](https://spring.io/projects/spring-boot) - The web framework used
- [Gradle Kotlin DSL](https://gradle.org/) - Dependency Management

## Authors

- **Yonatan Karp-Rudin** - *Initial work* - [yonatankarp](https://github.com/yonatankarp)
