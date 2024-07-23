package com.btsl.kafka.constants;
public interface IKafkaConstants {
    public static Integer MESSAGE_COUNT=1000;
    public static String CLIENT_ID="client1";
    public static String TOPIC_C2S_INSERT="c2s-insert";
    public static String TOPIC_C2S_ADJ="c2s-adj";
    public static String TOPIC_C2S_FILTER="c2s-filter";
    public static String GROUP_ID_CONFIG="consumerGroup1";
    public static Integer MAX_NO_MESSAGE_FOUND_COUNT=100;
    public static String OFFSET_RESET_LATEST="latest";
    public static String OFFSET_RESET_EARLIER="earliest";
    public static Integer MAX_POLL_RECORDS=5;
}