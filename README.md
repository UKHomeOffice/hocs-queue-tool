# hocs-queue-tool

[![CodeQL](https://github.com/UKHomeOffice/hocs-queue-tool/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/UKHomeOffice/hocs-queue-tool/actions/workflows/codeql-analysis.yml)


A utility to investigate and manage dead letter queues on the hocs platform.

## Getting Started

### Prerequisites

* ```Kotlin 1.8```
* ```Docker```
* ```LocalStack```

### Submodules

This project contains a 'ci' submodule with a docker-compose and infrastructure scripts in it.
Most modern IDEs will handle pulling this automatically for you, but if not

```console
$ git submodule update --init --recursive
```

## Docker Compose

This repository contains a [Docker Compose](https://docs.docker.com/compose/)
file.

### Start localstack (sqs, sns, s3)
From the project root run:
```console
$ docker-compose -f ./ci/docker-compose.yml up -d localstack
```

> With Docker using 4 GB of memory, this takes approximately 2 minutes to startup.

### Stop the services
From the project root run:
```console
$ docker-compose -f ./ci/docker-compose.yml stop
```
> This will retain data in the local database and other volumes.

## Running in an IDE

> Ensure you have the Kotlin Plugin installed within your IDE ([link](https://plugins.jetbrains.com/plugin/6954-kotlin)).

If you are using an IDE, such as IntelliJ, this service can be started by running the ```QueueToolApplication``` main class.
The service can then be accessed at ```http://localhost:8094```.

You need to specify appropriate Spring profiles.
Paste `development,localstack` into the "Active profiles" box of your run configuration.

> For curl commands:
> - use `http` instead of `https` 
> - use `8094` instead of `10443`
> - remove the `-k` flag 
> 
> For example, `curl "http://localhost:8094/transfer?queue=AUDIT"`

## Running hocs-queue-tool in kubernetes

### Use the deployed instances

The tool is deployed to all `cs-`, `wcs-` and `hocs-` namespaces and can be port forwarded to in the normal kubernetes way. It might need to be scaled up first!

```sh
kubectl scale deployment --replicas=1 hocs-queue-tool -n <<NAMESPACE>>

kubectl port-forward deployment/hocs-queue-tool 10443:10443 -n <<NAMESPACE>>
```

## Queue Management Endpoints

All endpoints take `?queue` as a required parameter.

The valid values are:
- `AUDIT`
- `CASECREATOR`
- `CASEMIGRATOR`
- `DOCUMENT`
- `NOTIFY`
- `OPENSEARCH`
- `SEARCH`

### Transfer
`GET /transfer?queue=<<QUEUE>>`

Moves all messages from the dead letter queue onto the main queue.

```sh
curl -k "https://localhost:10443/transfer?queue=AUDIT"
```

### Purge

`GET /purge?queue=<<QUEUE>>&dlq=<<BOOL>>`

Deletes all messages from the specified queue.

```sh
curl -k "https://localhost:10443/purge?queue=AUDIT&dlq=true" 
```

### Print

`GET /pringdlq?queue=<<QUEUE>>&count=<<NUM>>`

Prints all, or `count` messages on the dead letter queue. To be used to inspect a small number of messages.

> This marks the messages as not visible on the dead letter queue for the duration of the visibility timeout. 

```sh
curl -k "https://localhost:10443/printdlq?queue=AUDIT&count=1"
```

### Send

`POST /send?queue=<<QUEUE>>` 

Sends request body to the desired queue.

```sh
curl -k "https://localhost:10443/send?queue=AUDIT" \
  -H "Content-Type: application/json" \
  -d '{ <<MESSAGE>> }'  
```

### Attributes

`GET /attributes?queue=<<QUEUE>>&dlq=<<BOOL>>`

Returns the current attributes for the desired queue.

```sh
curl -k "https://localhost:10443/attributes?queue=AUDIT&dlq=true"
```

## Versioning

For versioning this project uses [SemVer](https://semver.org/).

## Authors

This project is authored by the Home Office.

## License

This project is licensed under the MIT license. For details please see [License](LICENSE) 
