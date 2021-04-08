# Jmix AWS Queue

This repository contains AWS Queue project of the [Jmix](https://jmix.io) framework.

For more information see:

* Jmix Core project source [repository](https://github.com/Haulmont/jmix-core).
* Jmix [documentation](https://docs.jmix.io).


## Usage

Add to your project's `build.gradle` dependencies:

```groovy
implementation 'io.jmix.aws-queue:jmix-aws-queue-starter'
```

Specify AWS credentials and region in `application.properties`:
```
jmix.awsqueue.region = eu-central-1
jmix.awsqueue.access-key = AWS_ACCESS_KEY
jmix.awsqueue.secret-key = AWS_SECRET_KEY
```