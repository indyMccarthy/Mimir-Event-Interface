package org.mimir.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericCustomSerializer<T> implements Serializer<T> {

    private final Logger log = Logger.getLogger(GenericCustomSerializer.class.getName());

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JodaModule());

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String topic, T data) {

        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            log.log(Level.SEVERE,"Error when serializing MessageDto to byte[]");
            return null;
        }
    }

    @Override
    public byte[] serialize(String topic, Headers headers, T data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }
}
