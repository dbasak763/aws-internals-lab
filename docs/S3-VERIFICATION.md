# Live S3 verification — 2026-11-07

Built the public GitHub checkout in AWS CloudShell with Amazon Corretto and Maven. The Java service used the shell's temporary AWS credentials.

- Maven compilation passed.
- GET /health returned 200.
- GET /api/photos initially returned an empty list.
- POST of a 28-byte test payload returned 201 and an S3 object key.
- GET /api/photos returned that same key and size.
- Empty POST returned 400; DELETE returned 405; a 5 MiB + 1 byte POST returned 413.
- Restarting Java preserved the uploaded object in S3.
- S3 HeadObject independently reported 28 bytes and AES256 encryption.

A tiny test object remains in the private bucket for inspection. The app is running as a learning session in CloudShell, not as a persistent hosted API. CloudShell sessions are temporary.

## Reproduce infrastructure

infra/storage.yaml creates a NEW bucket with the same core storage settings. It does not manage or import the bucket already created in the console. Supply a new unique name when creating a stack in CloudFormation. Retain policies preserve data when a stack is deleted; clean up retained buckets explicitly after reviewing their contents.
