package org.mimir;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

public class Utils {

    public static KafkaProducer createKafkaProducer(String servers, String username, String password, Class<?> keySerializer, Class<?> valueSerializer, Class<?> customPartitioner){
        Properties props = new Properties();
        props.put("bootstrap.servers", servers);
        props.put("sasl.mechanism", "SCRAM-SHA-256");
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.jaas.config", getJaasConf(username, password));
        props.put("key.serializer", keySerializer);
        props.put("value.serializer", valueSerializer);
        if (customPartitioner != null) {
            props.put("partitioner.class", customPartitioner);
        }
        return new KafkaProducer<>(props);
    }

    public static KafkaConsumer createKafkaConsumer(String servers, String username, String password, Class<?> keySerializer, Class<?> valueSerializer, String autoOffsetReset, String groupId){
        Properties props = new Properties();
        props.put("bootstrap.servers", servers);
        props.put("sasl.mechanism", "SCRAM-SHA-256");
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.jaas.config", getJaasConf(username, password));
        props.put("key.deserializer", keySerializer);
        props.put("value.deserializer", valueSerializer);
        props.put("auto.offset.reset", autoOffsetReset);
        props.put("group.id", groupId);
        return new KafkaConsumer<>(props);
    }

    private static String getJaasConf(String username, String password){
        return new StringBuilder("org.apache.kafka.common.security.scram.ScramLoginModule required username=\"")
        .append(username)
        .append("\" password=\"")
        .append(password)
        .append("\";").toString();
    }
}
