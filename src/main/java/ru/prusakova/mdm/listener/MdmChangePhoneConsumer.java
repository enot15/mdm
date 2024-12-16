package ru.prusakova.mdm.listener;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.dto.MdmChangePhoneEvent;
import ru.prusakova.mdm.util.JsonUtil;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "mdm.kafka.mdm-change-phone", name = "enabled", havingValue = "true")
public class MdmChangePhoneConsumer {

    private final JsonUtil jsonUtil;
    private final Validator validator;

    @KafkaListener(topics = "${mdm.kafka.mdm-change-phone.topic-in}")
    public void consume(ConsumerRecord<String, String> consumerRecord) {
        log.info("Событие из кафки получен: {}", consumerRecord.toString());
        try {
            MdmChangePhoneEvent response = jsonUtil.fromJson(consumerRecord.value(), MdmChangePhoneEvent.class);
            validator.validate(response)
                    .forEach(it -> log.warn(it.getMessage()));
        } catch (Exception e) {
            log.error("Ошибка преобразования JSON: {}", consumerRecord.value(), e);
        }
    }
}