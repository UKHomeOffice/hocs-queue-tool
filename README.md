# Hocs-Queue-Tool

A utility to investigate and manage dead letter queues on the hocs platform.

---

# Running Hocs-Queue-Tool

## Use the deployed instances

The tool is deployed to all `cs-`, `wcs-` and `hocs-` namespaces and can be port forwarded to in the normal kubernetes way. It might need to be scaled up first!

```sh
kubectl scale deployment --replicas=1 hocs-queue-tool -n <<NAMESPACE>>

kubectl port-forward deployment/hocs-queue-tool 8080:8080 -n <<NAMESPACE>>
```

## Run from your terminal against Localstack

Start up localstack then start the project using gradlew

```sh
docker-compose up -d
SPRING_PROFILES_ACTIVE=development,localstack ./gradlew bootRun
```

---

# Testing

When running locally, the tests require localstack to be running. The queues are automatically created using `/config/localstack/setup-sqs.sh`

```sh
docker-compose up -d
./gradlew check
```

---

# Queue Management Endpoints

All endpoints take `?queue` as a required parameter.
The valid values are `SEARCH, AUDIT, NOTIFY, DOCUMENT`

## Transfer
`GET /transfer?queue=<<QUEUE>>`

Moves all messages from the dead letter queue onto the main queue.

```sh
curl http://localhost:8080/transfer?queue=AUDIT
```

## Purge

`GET /purgedlq?queue=<<QUEUE>>`

Deletes all messages on the dead letter queue. To be used when the message can never be processed successfully.

```sh
curl http://localhost:8080/purgedlq?queue=AUDIT
```

## Print

`GET /pringdlq?queue=<<QUEUE>>&count=<<NUM>>`

Prints all, or `count` messages on the dead letter queue while still leaving them on the dead letter queue. To be used to inspect a small number of messages.

```sh
curl http://localhost:8080/printdlq?queue=AUDIT&count=1
```


-------
