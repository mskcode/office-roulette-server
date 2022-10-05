# Office Roulette

When you need to decide who gets the next promotion.

## Development

### Running server locally with Docker Compose

You can run the server and real PostgreSQL database locally with Docker
Compose by executing following commands.

```shell
cd docker
docker-compose up [--build]
```

The optional `--build` parameter forcefully builds the server container if
needed since Docker cannot detect stale build.

Once the containers are up and running, you can test if the server responds to
an HTTP request by executing following command.

```shell
$ curl http://localhost:8080/
{"message":"OK"}
```

### Automatic source code formatting

The project uses [Spotless](https://github.com/diffplug/spotless)
[Maven Plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven)
for automatic code formatting and code style enforcement.

When running `mvn verify`, the build will error out if the code style does not
align with requirements.

To fix code style issues, you can run `mvn spotless:apply`. This will reformat
all Java code.

There's also an
[IntelliJ plugin](https://plugins.jetbrains.com/plugin/13180-palantir-java-format)
you can install. When you enable the plugin for the project from
`File -> Settings... -> palantir-java-format Settings -> Enable palantir-java-format`
you can use the `Reformat Code` action (default shortcut `Ctrl+Alt+L`) to
format your code.