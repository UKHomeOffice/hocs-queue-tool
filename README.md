# hocs-queue-tool

[![CodeQL](https://github.com/UKHomeOffice/hocs-queue-tool/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/UKHomeOffice/hocs-queue-tool/actions/workflows/codeql-analysis.yml)


A utility to investigate and manage dead letter queues on the hocs platform.

## Getting Started

### Prerequisites

* ```Kotlin 1.7```
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

If you are using an IDE, such as IntelliJ, this service can be started by running the ```QueueToolApplication``` main class.
The service can then be accessed at ```http://localhost:8080```.

You need to specify appropriate Spring profiles.
Paste `development,localstack` into the "Active profiles" box of your run configuration.

## Running hocs-queue-tool in kubernetes

### Use the deployed instances

The tool is deployed to all `cs-`, `wcs-` and `hocs-` namespaces and can be port forwarded to in the normal kubernetes way. It might need to be scaled up first!

```sh
kubectl scale deployment --replicas=1 hocs-queue-tool -n <<NAMESPACE>>

kubectl port-forward deployment/hocs-queue-tool 8080:8080 -n <<NAMESPACE>>
```

## Queue Management Endpoints

All endpoints take `?queue` as a required parameter.
The valid values are `SEARCH, AUDIT, NOTIFY, DOCUMENT, CASECREATOR`.

### Transfer
`GET /transfer?queue=<<QUEUE>>`

Moves all messages from the dead letter queue onto the main queue.

```sh
curl http://localhost:8080/transfer?queue=AUDIT
```

### Purge

`GET /purgedlq?queue=<<QUEUE>>`

Deletes all messages on the dead letter queue. To be used when the message can never be processed successfully.

```sh
curl http://localhost:8080/purgedlq?queue=AUDIT
```

### Print

`GET /pringdlq?queue=<<QUEUE>>&count=<<NUM>>`

Prints all, or `count` messages on the dead letter queue while still leaving them on the dead letter queue. To be used to inspect a small number of messages.

```sh
curl http://localhost:8080/printdlq?queue=AUDIT&count=1
```

## Versioning

For versioning this project uses [SemVer](https://semver.org/).

## Authors

This project is authored by the Home Office.

## License

This project is licensed under the MIT license. For details please see [License](LICENSE) 
