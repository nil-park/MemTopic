#!/bin/sh
curl "https://texttospeech.googleapis.com/v1beta1/text:synthesize" \
     -H "Content-Type: application/json" \
     -H "X-goog-api-key: $GCP_TTS_API_TOKEN" \
     -d @$1

