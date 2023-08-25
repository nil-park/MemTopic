#!/bin/sh
curl "https://us-central1-texttospeech.googleapis.com/v1beta1/text:synthesize?key=$GCP_TTS_API_TOKEN" \
     -H "Content-Type: application/json" \
     -d @$1

