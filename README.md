# Kafka Streams end to end demo

Through a hypothetical IoT scenario this project demonstrates how to combine the Kafka Streams API and a Tensorflow image recognition algorithm to predict smuggling activities.

We are using the Tensorflow image classifier algorithm from the [Machine Learning + Kafka Streams example](https://github.com/kaiwaehner/kafka-streams-machine-learning-examples) github repo.

# Architectural overview


# Requirements, Installation and Usage

Java 8 and Maven 3 are required. Maven will download all required dependencies.

Just download the project and run

```
mvn clean package
```

In order to run the demo Zookeeper, Kafka, Kafka Connect and Confluent Schema Registry are required. As of today the Telegram sink connector is not
compatible with the latest versions of the Confluent platform so the project has been tested using the version 5.0.0 of the platform. Unfortunately this version
is not available within the official Docker confluent github repo so we recommend to download the binaries of the Confluent platform from here: [Confluent platform 5.0.0 binaries](https://packages.confluent.io/archive/5.0/confluent-5.0.0-2.11.tar.gz) and start up all the components
using the following command (included within the bin folder):

```
./confluent start
```
 

## Connectors set-up

### Spooldir source connector

### Telegram sink connector

### Elasticsearch sink connector

The images of vessels not recognized as potential smugglers