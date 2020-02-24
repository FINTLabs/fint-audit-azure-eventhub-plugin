# FINT Azure Eventhub Audit Plugin

[![Build Status](https://travis-ci.org/FINTlabs/fint-audit-azure-eventhub-plugin.svg?branch=master)](https://travis-ci.org/FINTlabs/fint-audit-azure-eventhub-plugin)
[![Coverage Status](https://coveralls.io/repos/github/FINTLabs/fint-audit-azure-eventhub-plugin/badge.svg?branch=master)](https://coveralls.io/github/FINTLabs/fint-audit-azure-eventhub-plugin?branch=master)

Implementation of fint-audit-api using Azure Eventhub.

## Installation

build.gradle

```
repositories {
    maven {
        url  "http://dl.bintray.com/fint/maven"
    }
}

compile('no.fint:fint-audit-azure-eventhub-plugin:+')
```

## Usage

- Set `@EnableFintAudit` on your application class
- `@Autowire` in the FintAuditService interface and call `audit(Event event)`. This will automatically clear the event data
- Use `audit(Event event, Status... statuses)` will set the status on the event and audit it. Multiple statuses will cause multiple audit log statements
- If you need control of when to clear the event data, use `audit(Event event, boolean clearData)`

## Configuration

| Key | Default value | Comment |
|-----|---------------|---------|
| fint.audit.azure.eventhub.connection-string | | Get using `az eventhubs eventhub authorization-rule keys list` |
| fint.audit.azure.eventhub.name | | Event Hub name |
| fint.audit.azure.eventhub.buffer-size | 200000 | Number of audit events that can be pending delivery. |
| fint.audit.azure.eventhub.rate | 2500 | Scheduling rate for worker thread writing events to Eventhub. |
| fint.audit.test-mode | false | If `true`, events will not be written. |
