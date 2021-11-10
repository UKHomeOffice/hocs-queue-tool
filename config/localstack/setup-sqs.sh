#!/usr/bin/env bash
set -e
export TERM=ansi
export AWS_ACCESS_KEY_ID=foobar
export AWS_SECRET_ACCESS_KEY=foobar
export AWS_DEFAULT_REGION=eu-west-2
export PAGER=

aws --endpoint-url=http://localhost:4576 sqs create-queue --queue-name search-sqs
aws --endpoint-url=http://localhost:4576 sqs create-queue --queue-name search-dlq

aws --endpoint-url=http://localhost:4576 sqs create-queue --queue-name audit-sqs
aws --endpoint-url=http://localhost:4576 sqs create-queue --queue-name audit-dlq

aws --endpoint-url=http://localhost:4576 sqs create-queue --queue-name document-sqs
aws --endpoint-url=http://localhost:4576 sqs create-queue --queue-name document-dlq

aws --endpoint-url=http://localhost:4576 sqs create-queue --queue-name notify-sqs
aws --endpoint-url=http://localhost:4576 sqs create-queue --queue-name notify-dlq


aws --endpoint-url=http://localhost:4576 sqs set-queue-attributes --queue-url "http://localhost:4576/queue/search-sqs" --attributes '{"RedrivePolicy":"{\"maxReceiveCount\":\"3\", \"deadLetterTargetArn\":\"arn:aws:sqs:eu-west-2:000000000000:search-dlq\"}"}'
aws --endpoint-url=http://localhost:4576 sqs set-queue-attributes --queue-url "http://localhost:4576/queue/audit-sqs" --attributes '{"RedrivePolicy":"{\"maxReceiveCount\":\"3\", \"deadLetterTargetArn\":\"arn:aws:sqs:eu-west-2:000000000000:audit-dlq\"}"}'
aws --endpoint-url=http://localhost:4576 sqs set-queue-attributes --queue-url "http://localhost:4576/queue/document-sqs" --attributes '{"RedrivePolicy":"{\"maxReceiveCount\":\"3\", \"deadLetterTargetArn\":\"arn:aws:sqs:eu-west-2:000000000000:document-dlq\"}"}'
aws --endpoint-url=http://localhost:4576 sqs set-queue-attributes --queue-url "http://localhost:4576/queue/notify-sqs" --attributes '{"RedrivePolicy":"{\"maxReceiveCount\":\"3\", \"deadLetterTargetArn\":\"arn:aws:sqs:eu-west-2:000000000000:notify-dlq\"}"}'

aws --endpoint-url=http://localhost:4576 sqs set-queue-attributes --queue-url "http://localhost:4576/queue/search-dlq" --attributes '{"VisibilityTimeout":"1"}'
aws --endpoint-url=http://localhost:4576 sqs set-queue-attributes --queue-url "http://localhost:4576/queue/audit-dlq" --attributes '{"VisibilityTimeout":"1"}'
aws --endpoint-url=http://localhost:4576 sqs set-queue-attributes --queue-url "http://localhost:4576/queue/document-dlq" --attributes '{"VisibilityTimeout":"1"}'
aws --endpoint-url=http://localhost:4576 sqs set-queue-attributes --queue-url "http://localhost:4576/queue/notify-dlq" --attributes '{"VisibilityTimeout":"1"}'

echo Queues are Ready
