package org.mimir.deserializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import jdk.jshell.spi.ExecutionControl;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericCustomDeserializer<T> implements Deserializer<T> {

    private final Logger log = Logger.getLogger(GenericCustomDeserializer.class.getName());

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JodaModule());

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                log.warning("Null received at deserializing");
                return null;
            }
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
            return deserializeClass(data, objectMapper);
        } catch (Exception e) {
            System.out.println(e);
            log.warning("Error when strictly deserializing byte[] to MessageDto." +
                    "Deserialization does not work if one or more message field is not in POJO." +
                    "Set FAIL_ON_UNKNOWN_PROPERTIES to FALSE and put unknown fields in additionalProperties attribute.\n"
                    + e);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                return deserializeClass(data, objectMapper);
            } catch (JsonProcessingException ex) {
                log.log(Level.SEVERE,"Error while deserializing message to POJO :" + ex);
            } catch (ExecutionControl.NotImplementedException ex) {
                log.log(Level.WARNING, ex.toString());
            }
            return null;
        }
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    public T deserializeClass( byte[] data, ObjectMapper objectMapper) throws ExecutionControl.NotImplementedException, JsonProcessingException {
        throw new ExecutionControl.NotImplementedException("This method is not implement for the GenericCustomDeserializer.");
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
