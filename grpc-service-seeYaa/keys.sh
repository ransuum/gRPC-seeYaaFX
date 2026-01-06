#!/bin/bash
set -e

CERT_DIR="src/main/resources/certs"
KEYPAIR="$CERT_DIR/keypair.pem"
PUBLIC_KEY="$CERT_DIR/publicKey.pem"
PRIVATE_KEY="$CERT_DIR/privateKey.pem"

missing=0

# Check existence
[[ -f "$KEYPAIR" ]] || missing=1
[[ -f "$PUBLIC_KEY" ]] || missing=1
[[ -f "$PRIVATE_KEY" ]] || missing=1

if [[ $missing -eq 0 ]]; then
  echo "All key files already exist in $CERT_DIR."
  exit 0
fi

echo "Generating RSA key files in $CERT_DIR ..."

mkdir -p "$CERT_DIR"

if [[ ! -f "$KEYPAIR" ]]; then
  openssl genrsa -out "$KEYPAIR" 2048
  echo "Generated $KEYPAIR"
fi

if [[ ! -f "$PUBLIC_KEY" ]]; then
  openssl rsa -in "$KEYPAIR" -pubout -out "$PUBLIC_KEY"
  echo "Generated $PUBLIC_KEY"
fi

if [[ ! -f "$PRIVATE_KEY" ]]; then
  openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in "$KEYPAIR" -out "$PRIVATE_KEY"
  echo "Generated $PRIVATE_KEY"
fi

echo "Key generation complete."