package org.mimir;

public class AbstractCommonTest {

    public static String KAFKA_BROKERS = System.getenv("MIMIR_INTERFACE_KAFKA_BROKER_TEST");
    public static String OFFSET_RESET_EARLIER="earliest";
    public static String USERNAME_PRODUCER=System.getenv("MIMIR_INTERFACE_KAFKA_PRODUCER_USER_TEST");
    public static String PWD_PRODUCER=System.getenv("MIMIR_INTERFACE_KAFKA_PRODUCER_PWD_TEST");
    public static String USERNAME_CONSUMER=System.getenv("MIMIR_INTERFACE_KAFKA_CONSUMER_USER_TEST");
    public static String PWD_CONSUMER=System.getenv("MIMIR_INTERFACE_KAFKA_CONSUMER_PWD_TEST");
    public static Integer CONSUMER_POLL_DURATION_SECONDS = 15;

}
