# AWS Internals Lab

A small Java HTTP service backed by a private Amazon S3 bucket.

## Run

Use JDK 17+, Maven, and temporary AWS credentials available to the SDK default credential chain. AWS CloudShell is a convenient first environment: it supplies temporary credentials for your console identity. For local development, configure an AWS profile using your organization's supported sign-in flow. Never put access keys in this repository.

```sh
export AWS_REGION=us-east-2
export S3_BUCKET=aws-internals-lab-photos-704660347107
sh run.sh
```

The app listens only on 127.0.0.1:8080. In another terminal in the same environment:

```sh
curl -i http://127.0.0.1:8080/health
curl -i -X POST --data-binary @photo.jpg http://127.0.0.1:8080/api/photos
curl -i http://127.0.0.1:8080/api/photos
```

POST stores raw bytes under a random photos/ key and returns 201. Uploads must be 1 byte to 5 MiB. The server does not validate image content or expose public download links. GET lists the first 100 objects and reports whether results were truncated. Health checks process liveness; call the photo endpoint to check S3 access.

## AWS setup

The lab bucket was created in the console in US East (Ohio), us-east-2. It uses S3-managed encryption, disabled ACLs, blocked public access, and no versioning. Your client may be in Los Angeles; AWS_REGION must still match the bucket. Storage and requests can consume account credits or incur charges.

The calling identity needs s3:ListBucket on the bucket and s3:PutObject on its photos/* prefix. The console session does not automatically authenticate a program on your laptop. CloudShell supplies its own temporary credentials. No credentials are committed here.

## Explore the internals

1. Upload a tiny file and find its returned key in S3. Compare its byte size with the API response.
2. Stop and restart Java: the objects remain because S3 owns persistence.
3. Start with a deliberately nonexistent bucket name. /health stays healthy; /api/photos returns 503. Restore the name afterward. This separates process health from dependency health.
4. Inspect Main.java: one SDK client is reused, uploads are bounded, and Jackson escapes JSON values. The SDK signs requests using temporary credentials.

This is a learning service with no user authentication; keep it bound to localhost. It is not deployed to public compute yet.

## Cleanup

Stop Java with Ctrl+C. When finished with your own test data, use S3's console to empty and delete the lab bucket. Inspect the listed objects before confirming deletion. These actions permanently remove data.
