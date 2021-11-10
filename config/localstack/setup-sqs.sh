#!/usr/bin/env bash
set -euo

export AWS_ACCESS_KEY_ID=foobar
export AWS_SECRET_ACCESS_KEY=foobar
export AWS_DEFAULT_REGION=eu-west-2

until curl http://localstack:4566/health --silent | grep -q "running"; do
   sleep 5
   echo "Waiting for LocalStack to be ready..."
done

aws --endpoint-url=http://localstack:4576 sqs create-queue --queue-name search-dlq --attributes '{"VisibilityTimeout":"1"}'
aws --endpoint-url=http://localstack:4576 sqs create-queue --queue-name search-sqs --attributes '{"RedrivePolicy": "{\"deadLetterTargetArn\":\"arn:aws:sqs:eu-west-2:000000000000:search-dlq\",\"maxReceiveCount\":1}"}'

aws --endpoint-url=http://localstack:4576 sqs create-queue --queue-name audit-dlq --attributes '{"VisibilityTimeout":"1"}'
aws --endpoint-url=http://localstack:4576 sqs create-queue --queue-name audit-sqs --attributes '{"RedrivePolicy": "{\"deadLetterTargetArn\":\"arn:aws:sqs:eu-west-2:000000000000:audit-dlq\",\"maxReceiveCount\":1}"}'

aws --endpoint-url=http://localstack:4576 sqs create-queue --queue-name document-dlq --attributes '{"VisibilityTimeout":"1"}'
aws --endpoint-url=http://localstack:4576 sqs create-queue --queue-name document-sqs --attributes '{"RedrivePolicy": "{\"deadLetterTargetArn\":\"arn:aws:sqs:eu-west-2:000000000000:document-dlq\",\"maxReceiveCount\":1}"}'

aws --endpoint-url=http://localstack:4576 sqs create-queue --queue-name notify-dlq --attributes '{"VisibilityTimeout":"1"}'
aws --endpoint-url=http://localstack:4576 sqs create-queue --queue-name notify-sqs --attributes '{"RedrivePolicy": "{\"deadLetterTargetArn\":\"arn:aws:sqs:eu-west-2:000000000000:notify-dlq\",\"maxReceiveCount\":1}"}'

echo Queues are Ready
