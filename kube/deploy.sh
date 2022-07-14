#!/bin/bash
set -euo pipefail

export KUBE_NAMESPACE=${ENVIRONMENT}
export VERSION=${VERSION}

QUEUES="audit,case-creator,document,notify,search"
if [[ ${KUBE_NAMESPACE} == cs-dev ]]; then
  QUEUES+=",case-migrator"
fi
export QUEUES=${QUEUES}

echo
echo "Deploying hocs-queue-tool to ${ENVIRONMENT}"
echo "Service version: ${VERSION}"
echo "Queues: ${QUEUES}"
echo

export KUBE_CERTIFICATE_AUTHORITY="https://raw.githubusercontent.com/UKHomeOffice/acp-ca/master/${CLUSTER_NAME}.crt"

cd kd

kd --timeout 10m \
    -f deployment.yaml \
