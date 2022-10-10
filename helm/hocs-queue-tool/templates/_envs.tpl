{{- define "deployment.envs" }}
- name: JAVA_OPTS
  value: '{{ tpl .Values.app.env.javaOpts . }}'
- name: SERVER_PORT
  value: '{{ .Values.app.env.port }}'
- name: SPRING_PROFILES_ACTIVE
  value: '{{ tpl .Values.app.env.springProfiles . }}'
- name: MIGRATION_QUEUE_ENABLED
  value: '{{ .Values.app.env.migrationQueueEnabled }}'
{{- range .Values.app.env.queues }}
- name: {{. | title | replace "-" "_" | upper }}_QUEUE
  valueFrom:
    secretKeyRef:
      name: {{ $.Release.Namespace }}-{{. | title | lower }}-sqs
      key: sqs_queue_url
- name: {{. | title | replace "-" "_" | upper }}_QUEUE_ACCESS_KEY_ID
  valueFrom:
    secretKeyRef:
      name: {{ $.Release.Namespace }}-{{. | title | lower }}-sqs
      key: access_key_id
- name: {{. | title | replace "-" "_" | upper }}_QUEUE_SECRET_ACCESS_KEY
  valueFrom:
    secretKeyRef:
      name: {{ $.Release.Namespace }}-{{. | title | lower }}-sqs
      key: secret_access_key
- name: {{. | title | replace "-" "_" | upper }}_DLQ_QUEUE
  valueFrom:
    secretKeyRef:
      name: {{ $.Release.Namespace }}-{{. | title | lower }}-sqs
      key: sqs_dlq_url
      optional: true
- name: {{. | title | replace "-" "_" | upper }}_DLQ_ACCESS_KEY_ID
  valueFrom:
    secretKeyRef:
      name: {{ $.Release.Namespace }}-{{. | title | lower }}-sqs
      key: access_key_id
      optional: true
- name: {{. | title | replace "-" "_" | upper }}_DLQ_SECRET_ACCESS_KEY
  valueFrom:
    secretKeyRef:
      name: {{ $.Release.Namespace }}-{{. | title | lower }}-sqs
      key: secret_access_key
      optional: true
{{- end -}}
{{- end -}}
