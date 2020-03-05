#!/usr/bin/env bash

set -e
REGION=westeurope
GROUP=fint-audit
NAMESPACE=fint-audit-basic
SKU=Basic
NAME=fint-audit-alpha

echo "Creating namespace ${NAMESPACE} ..."
#az eventhubs namespace create --name ${NAMESPACE} --resource-group ${GROUP} --sku ${SKU} --location ${REGION} \
#  --output table

echo "Creating hub ${NAME} ..."
az eventhubs eventhub create --name ${NAME} --message-retention 1 \
  --resource-group ${GROUP} --namespace-name ${NAMESPACE} --output table

echo "Creating Send rule ..."
az eventhubs eventhub authorization-rule create --name ${NAME}-send --rights Send \
  --eventhub-name ${NAME} --namespace-name ${NAMESPACE} --resource-group ${GROUP} --output table

echo "Creating Listen rule ..."
az eventhubs eventhub authorization-rule create --name ${NAME}-listen --rights Listen \
  --eventhub-name ${NAME} --namespace-name ${NAMESPACE} --resource-group ${GROUP} --output table

echo "Connection string for SEND:"
az eventhubs eventhub authorization-rule keys list  --name ${NAME}-send --eventhub-name ${NAME} \
  --namespace-name ${NAMESPACE} --resource-group ${GROUP} --query primaryConnectionString

echo "Connection string for LISTEN:"
az eventhubs eventhub authorization-rule keys list  --name ${NAME}-listen --eventhub-name ${NAME} \
  --namespace-name ${NAMESPACE} --resource-group ${GROUP} --query primaryConnectionString

