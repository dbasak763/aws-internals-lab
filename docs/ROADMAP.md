# Learning roadmap

We will revisit the application as each new cloud concept becomes useful. Each milestone includes building, inspecting, breaking and repairing something, and cleaning up resources. AWS is broad; this project gives you practical depth across its core services, with room to explore specialties later.

1. **Local foundations:** Run Java and explore HTTP, ports, processes, JSON, and the request lifecycle. Deliverable: a working local service.
2. **Account and identity:** Learn accounts, regions, IAM identities and roles, temporary credentials, MFA, and the shared responsibility model. Set a budget and understand that alerts are not a spending cap. Avoid putting credentials in source code.
3. **Infrastructure as code:** Introduce AWS CloudFormation or CDK with Java. Learn stacks, parameters, outputs, change review, and deletion. Keep infrastructure reproducible.
4. **Storage:** Add S3 for photos. Learn buckets, object keys, access controls, encryption, lifecycle, and presigned uploads. Keep buckets private.
5. **Application and networking:** Learn VPCs, subnets, routes, security groups, and DNS. Deploy the Java API to compute; compare EC2, containers, and serverless and examine their tradeoffs.
6. **Data and identity:** Add a database for photo metadata and user authentication. Compare managed SQL and NoSQL, and learn authorization boundaries.
7. **Operations:** Add logging, metrics, alarms, tracing, backups, deployment automation, and failure exercises. Practice investigating an outage.
8. **Architecture:** Explore scaling, availability zones, queues and asynchronous processing, caching, disaster recovery, and cost optimization. Document decisions and tradeoffs.

## Cost and cleanup

Review current AWS pricing and free-tier eligibility before creating resources; availability and charges change. Configure a budget early, check actual usage regularly, and delete resources when finished. Some resources or retained data can continue to incur charges even after a stack is deleted. We will identify costs and cleanup steps for each cloud lesson before deploying it.

## Next session

Once the local app works, map a browser request to the Java handler. Then set up an AWS account and a safe development identity, tailored to your experience and whether you already have an account.
