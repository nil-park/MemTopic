import requests
from retry import retry
import json
from pydub import AudioSegment
from pydub.playback import play
from io import BytesIO
import argparse
import sys
import os
from os.path import basename, splitext, join as ospj
import base64


KEY = os.environ.get("GCP_TTS_API_TOKEN")
BASE_URL = "https://texttospeech.googleapis.com/v1beta1"
HEADERS = {
    "Content-Type": "application/json",
    "X-goog-api-key": KEY
}
VOICES = [
    {"languageCode": "ko-KR", "name": "ko-KR-Neural2-C"},
    {"languageCode": "en-US", "name": "en-US-Neural2-J"}
]

@retry(tries=3)
def synthesize(s: str):
    url = BASE_URL + "/text:synthesize"
    data = {
        "audioConfig": {"audioEncoding": "LINEAR16", "effectsProfileId": ["headphone-class-device"], "pitch": 0, "speakingRate": 1},
        "input": {"text": s},
        "voice": VOICES[0]
    }
    response = requests.post(url, data=json.dumps(data), headers=HEADERS)
    if response.status_code != 200:
        raise Exception(f"Status code for tts is {response.status_code}, input: \"{s}\"")
    response = json.loads(response.text)
    audoContent = base64.b64decode(response["audioContent"])
    with BytesIO(audoContent) as fp:
        return AudioSegment.from_wav(fp)

parser = argparse.ArgumentParser()
_, alist = parser.parse_known_args()

if len(alist) < 1:
    print("\033[91mNeed to input text file path as argument.\033[0m", file=sys.stderr)

os.makedirs("outs", exist_ok=True)

for f in alist:
    dst = ospj("outs", splitext(basename(f))[0]) + ".wav"
    with open(f, "r") as fp:
        s = fp.read()
        sound = synthesize(s)
        # play(sound)
        sound.export(dst, format="wav")
