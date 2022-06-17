package org.mimir;

import net.mguenther.kafka.junit.EmbeddedKafkaCluster;
import net.mguenther.kafka.junit.KeyValue;
import net.mguenther.kafka.junit.ReadKeyValues;
import net.mguenther.kafka.junit.SendKeyValues;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.joda.time.DateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mimir.deserializers.KafkaMessageDeserializer;
import org.mimir.serializers.KafkaMessageLessFieldsSerializer;
import org.mimir.serializers.KafkaMessageMoreFieldsSerializer;
import org.mimir.serializers.KafkaMessageSerializer;

import java.util.ArrayList;
import java.util.List;

import static net.mguenther.kafka.junit.EmbeddedKafkaCluster.provisionWith;
import static net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig.defaultClusterConfig;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeserializationCompatibilityTest {

    private EmbeddedKafkaCluster kafka;
    private KafkaMessageJson msg;
    private KafkaMessageJsonLessFields msgL;
    private KafkaMessageJsonMoreFields msgM;

    @BeforeEach
    void setupKafka() {
        kafka = provisionWith(defaultClusterConfig());
        kafka.start();
    }

    @BeforeEach
    void setupKafkaMessageTest() {
        msg = new KafkaMessageJson();
        msg.setIdentifier("ID1");
        msg.setMessageContent("TEST");
        msg.setMessageDate(DateTime.now());
        msg.setRandomNum(2.2);

        msgM = new KafkaMessageJsonMoreFields();
        msgM.setIdentifier("ID3");
        msgM.setMessageContent("TESTM");
        msgM.setMessageDate(DateTime.now());
        msgM.setRandomNum(2.2);
        msgM.setAnotherRandomNum(3.3);

        msgL = new KafkaMessageJsonLessFields();
        msgL.setIdentifier("ID2");
        msgL.setMessageContent("TESTL");
        msgL.setMessageDate(DateTime.now());
    }

    @AfterEach
    void tearDownKafka() {
        kafka.stop();
    }

    private final String topic = "TEST";

    @Test
    public void serializationDeserializationOfEqualObjectTest() throws InterruptedException {

        List<KeyValue<String, KafkaMessageJson>> records = new ArrayList<>();
        records.add(new KeyValue<>("A", msg));

        List<KeyValue<String, KafkaMessageJsonMoreFields>> recordsWithMoreFieldsThanConsumerObject = new ArrayList<>();
        recordsWithMoreFieldsThanConsumerObject.add(new KeyValue<>("A", msgM));

        List<KeyValue<String, KafkaMessageJsonLessFields>> recordsWithLessFieldsThanConsumerObject = new ArrayList<>();
        recordsWithLessFieldsThanConsumerObject.add(new KeyValue<>("A", msgL));

        sendRecordToKafkaTopic(records, KafkaMessageSerializer.class);
        sendRecordToKafkaTopic(recordsWithMoreFieldsThanConsumerObject, KafkaMessageMoreFieldsSerializer.class);
        sendRecordToKafkaTopic(recordsWithLessFieldsThanConsumerObject, KafkaMessageLessFieldsSerializer.class);

        List<KeyValue<String, KafkaMessageJson>> consumedRecords = kafka.read(ReadKeyValues
                .from(topic, KafkaMessageJson.class)
                .with(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaMessageDeserializer.class));

        System.out.println(consumedRecords.size());
        System.out.println("======================== Message received =========================");
        consumedRecords.forEach(record -> {
            System.out.println("Message received :" + record.getValue());
        });
        System.out.println("Message received :" + consumedRecords.get(1).getValue().getAdditionalProperties());
        System.out.println("Message received :" + consumedRecords.get(2).getValue().getRandomNum());
        System.out.println("======================== Message received =========================");
    }

    @Test
    public void serializationDeserializationOfBiggerJsonToObjectTest() throws InterruptedException {

        List<KeyValue<String, KafkaMessageJsonLessFields>> recordsWithLessFieldsThanConsumerObject = new ArrayList<>();
        recordsWithLessFieldsThanConsumerObject.add(new KeyValue<>("A", msgL));

        sendRecordToKafkaTopic(recordsWithLessFieldsThanConsumerObject, KafkaMessageLessFieldsSerializer.class);

        List<KeyValue<String, KafkaMessageJson>> consumedRecords = kafka.read(ReadKeyValues
                .from(topic, KafkaMessageJson.class)
                .with(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaMessageDeserializer.class));

        System.out.println(consumedRecords.size());
        System.out.println("======================== Message received =========================");
        consumedRecords.forEach(record -> {
            System.out.println("Message received :" + record.getValue());
        });
        System.out.println("Message received :" + consumedRecords.get(0).getValue().getAdditionalProperties());
        System.out.println("Message received :" + consumedRecords.get(0).getValue().getRandomNum());
        System.out.println("======================== Message received =========================");
    }

    private <T> void sendRecordToKafkaTopic(List<KeyValue<String,T>> records, Class deserializerClass) throws InterruptedException {
        kafka.send(SendKeyValues.to(topic, records)
                .with(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true")
                .with(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1")
                .with(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, deserializerClass));
    }
}
