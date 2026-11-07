#!/bin/sh
set -eu
cd "$(dirname "$0")"
: "${S3_BUCKET:?Set S3_BUCKET first}"
: "${AWS_REGION:?Set AWS_REGION to the bucket region}"
exec mvn -q compile exec:java
