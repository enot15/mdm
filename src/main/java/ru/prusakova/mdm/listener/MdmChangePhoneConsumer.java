package ru.prusakova.mdm.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.dto.KafkaMdmChangePhoneInResponse;
import ru.prusakova.mdm.util.JsonUtil;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "mdm.kafka.mdm-change-phone", name = "enabled", havingValue = "true")
public class MdmChangePhoneConsumer {

    private final JsonUtil jsonUtil;

    @KafkaListener(topics = "${mdm.kafka.mdm-change-phone.topic-in}")
    public void consume(ConsumerRecord<String, String> consumerRecord) {
        log.info("Ответ из кафка получен: {}", consumerRecord.toString());
        try {
            KafkaMdmChangePhoneInResponse response = jsonUtil.fromJson(consumerRecord.value(), KafkaMdmChangePhoneInResponse.class);
        } catch (Exception e) {
            log.error("Ошибка преобразования JSON: {}", consumerRecord.value(), e);
        }
    }
}