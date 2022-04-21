#!/bin/bash
set -euo pipefail

export KUBE_NAMESPACE=${ENVIRONMENT}
export VERSION=${VERSION}

echo
echo "Deploying hocs-queue-tool to ${ENVIRONMENT}"
echo "Service version: ${VERSION}"
echo

export KUBE_CERTIFICATE_AUTHORITY="https://raw.githubusercontent.com/UKHomeOffice/acp-ca/master/${CLUSTER_NAME}.crt"

cd kd

kd --timeout 10m \
    -f deployment.yaml \
