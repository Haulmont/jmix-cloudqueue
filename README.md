# Jmix AWS Queue

This repository contains AWS Queue project of the [Jmix](https://jmix.io) framework.

For more information see:

* Jmix Core project source [repository](https://github.com/Haulmont/jmix-core).
* Jmix [documentation](https://docs.jmix.io).


## Usage

Add to your project's `build.gradle` dependencies:

```groovy
implementation 'io.jmix.simplequeueui:jmix-simplequeue-ui-starter'
```

For AWS Queue:
```groovy
implementation 'io.jmix.simplequeueui:jmix-simplequeue-aws-starter'
```

For Yandex Queue:
```groovy
implementation 'io.jmix.simplequeueui:jmix-simplequeue-yandex-starter'
```

Specify AWS credentials and region in `application.properties`:
```
jmix.simplequeue.aws.region = eu-central-1
jmix.simplequeue.aws.access-key = AWS_ACCESS_KEY
jmix.simplequeue.aws.secret-key = AWS_SECRET_KEY
```

Specify Yandex credentials, region and endpoint in `application.properties`:
```
jmix.simplequeue.yandex.region = ru-central1
jmix.simplequeue.yandex.access-key = YANDEX_ACCESS_KEY
jmix.simplequeue.yandex.secret-key = YANDEX_SECRET_KEY
jmix.simplequeue.yandex.endpoint-configuration = https://message-queue.api.cloud.yandex.net
```

By default, tag is not defined and application loads every queue and
create queues without any tag

Add Provider family queues tag to use only tagged to restrict queues in your application
```
jmix.simplequeue.queue.prefix = jmixqueues
```

After this ADD-on will create and load only 'jmixqueues' tagged queues.


Specify next parameters for Simple Queue Listener in `application.properties`:
1. long-polling-timeout - the duration (in milliseconds) after that will be sent new request to aws to check new messages.
2. waiting-time-receive-request - the duration (in seconds) for which the call waits for a message to arrive in the queue before returning. If a message is available, the call returns sooner than WaitTimeSeconds. If no messages are available and the wait time expires, the call returns successfully with an empty list of messages.
3. max-number-of-messages - the maximum number of messages to return. Queue never returns more messages than this value (however, fewer messages might be returned). Valid values: 1 to 10.
4. thread-pool-core-size - the number of threads to keep in the pool, even if they are idle. Use in thread pool that serves for handling messages.

```
jmix.simplequeue.listener.long-polling-timeout = 10000
jmix.simplequeue.listener.waiting-time-receive-request = 5
jmix.simplequeue.listener.max-number-of-messages = 10
jmix.simplequeue.listener.thread-pool-core-size = 5
```