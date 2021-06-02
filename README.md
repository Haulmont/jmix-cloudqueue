# Jmix AWS Queue

This repository contains AWS Queue project of the [Jmix](https://jmix.io) framework.

For more information see:

* Jmix Core project source [repository](https://github.com/Haulmont/jmix-core).
* Jmix [documentation](https://docs.jmix.io).


## Usage

Add to your project's `build.gradle` dependencies:

```groovy
implementation 'io.jmix.awsqueue:jmix-sqs-ui-starter'
```

For AWS Queue:
```groovy
implementation 'io.jmix.awsqueue:jmix-awsqueue-starter'
```

For Yandex Queue:
```groovy
implementation 'io.jmix.awsqueue:jmix-yandexqueue-starter'
```

Specify AWS credentials and region in `application.properties`:
```
jmix.sqs.awsqueue.region = eu-central-1
jmix.sqs.awsqueue.access-key = AWS_ACCESS_KEY
jmix.sqs.awsqueue.secret-key = AWS_SECRET_KEY
```

Specify Yandex credentials, region and endpoint in `application.properties`:
```
jmix.sqs.yandexqueue.region = ru-central1
jmix.sqs.yandexqueue.access-key = YANDEX_ACCESS_KEY
jmix.sqs.yandexqueue.secret-key = YANDEX_SECRET_KEY
jmix.sqs.yandexqueue.endpoint-configuration = https://message-queue.api.cloud.yandex.net
```

By default, tag is not defined and application loads every queue and
create queues without any tag

Add AWS family queues tag to use only tagged to restrict queues in your application
```
jmix.sqs.awsqueue.queue-prefix = jmixqueues
```

Add Yandex family queues tag to use only tagged to restrict queues in your application
```
jmix.sqs.yandexqueue.queue-prefix = jmixqueues
```

After this ADD-on will create and load only 'jmixqueues' tagged queues.


Specify next parameters for SQS Listener in `application.properties`:
1. long-polling-timeout - the duration (in milliseconds) after that will be sent new request to aws to check new messages.
2. waiting-time-receive-request - the duration (in seconds) for which the call waits for a message to arrive in the queue before returning. If a message is available, the call returns sooner than WaitTimeSeconds. If no messages are available and the wait time expires, the call returns successfully with an empty list of messages.
3. max-number-of-messages - the maximum number of messages to return. Amazon SQS never returns more messages than this value (however, fewer messages might be returned). Valid values: 1 to 10.
4. thread-pool-core-size - the number of threads to keep in the pool, even if they are idle. Use in thread pool that serves for handling messages.

```
jmix.sqs.listener.long-polling-timeout = 10000
jmix.sqs.listener.waiting-time-receive-request = 5
jmix.sqs.listener.max-number-of-messages = 10
jmix.sqs.listener.thread-pool-core-size = 5
```