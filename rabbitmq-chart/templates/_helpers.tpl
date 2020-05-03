{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}

{{- define "rabbitmq-server.fullname" -}}
{{- default "rabbitmq" .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- define "producer.fullname" -}}
{{- default "producer" .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- define "consumer.fullname" -}}
{{- default "consumer" .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}


{{/*
define roles of the 3 apps
*/}}
{{- define "rabbitmq-role.fullname" -}}
{{- default "messagingBroker" .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- define "producer-role.fullname" -}}
{{- default "transmitter" .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- define "consumer-role.fullname" -}}
{{- default "receiver" .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "rabbitmq-chart.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "rabbitmq-chart.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{/*
rabbitmq chart label
*/}}
{{- define "rabbitmq-chart.labels" -}}
app: {{ include "rabbitmq-chart.fullname" . }}
{{- end -}}
{{/*
rabbitmq Selector labels producer
*/}}
{{- define "rabbitmq-chart.selectorLabels" -}}
app: {{ include "rabbitmq-server.fullname" . }}
role: {{ include "rabbitmq-role.fullname" . }}
{{- end -}}


{{/*
producer Common labels
*/}}
{{- define "producer.labels" -}}
script: {{ include "producer.fullname" . }}
{{- end -}}
{{/*
producer Selector labels producer
*/}}
{{- define "producer.selectorLabels" -}}
script: {{ include "producer.fullname" . }}
role: {{ include "producer-role.fullname" . }}
{{- end -}}



{{/*
consumer Common labels
*/}}
{{- define "consumer.labels" -}}
script: {{ include "consumer.fullname" . }}
{{- end -}}
{{/*
consumer Selector labels consumer
*/}}
{{- define "consumer.selectorLabels" -}}
app: {{ include "consumer.fullname" . }}
role: {{ include "consumer-role.fullname" . }}
{{- end -}}


{{/*
Create the name of the service account to use
*/}}
{{- define "rabbitmq-chart.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
    {{ default (include "rabbitmq-chart.fullname" .) .Values.serviceAccount.name }}
{{- else -}}
    {{ default "default" .Values.serviceAccount.name }}
{{- end -}}
{{- end -}}
