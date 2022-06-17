package org.mimir.deserializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mimir.KafkaMessageJson;

import java.nio.charset.StandardCharsets;

public class KafkaMessageDeserializer extends GenericCustomDeserializer<KafkaMessageJson> {

    @Override
    public KafkaMessageJson deserializeClass(byte[] data, ObjectMapper objectMapper) throws JsonProcessingException {
        return objectMapper.readValue(new String(data, StandardCharsets.UTF_8), KafkaMessageJson.class);
    }
}
