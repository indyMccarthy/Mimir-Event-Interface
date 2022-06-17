package org.mimir;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.mimir.deserializers.KafkaMessageDeserializer;
import org.mimir.serializers.KafkaMessageSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static org.mimir.Utils.createKafkaConsumer;
import static org.mimir.Utils.createKafkaProducer;

public class ProducerConsumerTest extends AbstractCommonTest {

    public static String TOPIC_TEST ="mimir-message-test";
    public static String GROUP_ID_TEST ="groupidtest";
    public static String GROUP_ID_TEST_2 ="anothergroupidtest";

    @Test
    public void consumerProducerIntegrationTest() throws ExecutionException, InterruptedException {

        final Producer<String, KafkaMessageJson> producer = createKafkaProducer(
                KAFKA_BROKERS,
                USERNAME_PRODUCER,
                PWD_PRODUCER,
                StringSerializer.class,
                KafkaMessageSerializer.class,
                null);

        final Consumer<String, KafkaMessageJson> consumer = createKafkaConsumer(
                KAFKA_BROKERS,
                USERNAME_CONSUMER,
                PWD_CONSUMER,
                StringDeserializer.class,
                KafkaMessageDeserializer.class,
                OFFSET_RESET_EARLIER,
                GROUP_ID_TEST);

        KafkaMessageJson msg = new KafkaMessageJson();
        msg.setIdentifier("ID1");
        msg.setMessageContent("TEST");
        msg.setMessageDate(DateTime.now());
        msg.setRandomNum(2.2);

        // Produce message
        try {
            final ProducerRecord<String, KafkaMessageJson> record =
                    new ProducerRecord<>(TOPIC_TEST, "key1", msg);

            RecordMetadata metadata = producer.send(record).get();

            System.out.printf("sent record(key=%s value=%s) " +
                            "meta(partition=%s, offset=%s)\n",
                    record.key(), record.value().toString(), metadata.partition(),
                    metadata.offset());

        } finally {
            producer.flush();
            producer.close();
        }

        // Consume message
        consumer.subscribe(Collections.singletonList(TOPIC_TEST));

        final ConsumerRecords<String, KafkaMessageJson> consumerRecords =
                consumer.poll(Duration.ofSeconds(CONSUMER_POLL_DURATION_SECONDS));

        consumerRecords.forEach(record -> {
            System.out.printf("Consumer Record:(%s, %s, %s, %s)\n",
                    record.key(), record.value(),
                    record.partition(), record.offset());
        });

        consumer.commitAsync();
        consumer.close();

        // Consume with another GID
        final Consumer<String, KafkaMessageJson> consumerWithOtherGid = createKafkaConsumer(
                KAFKA_BROKERS,
                USERNAME_CONSUMER,
                PWD_CONSUMER,
                StringDeserializer.class,
                KafkaMessageDeserializer.class,
                OFFSET_RESET_EARLIER,
                GROUP_ID_TEST_2);

        // Consume message
        consumerWithOtherGid.subscribe(Collections.singletonList(TOPIC_TEST));

        final ConsumerRecords<String, KafkaMessageJson> consumerSameRecords =
                consumerWithOtherGid.poll(Duration.ofSeconds(CONSUMER_POLL_DURATION_SECONDS));

        consumerSameRecords.forEach(record -> {
            System.out.printf("Consumer Record:(%s, %s, %s, %s)\n",
                    record.key(), record.value(),
                    record.partition(), record.offset());
        });

        consumerWithOtherGid.commitAsync();
        consumerWithOtherGid.close();
    }
}
