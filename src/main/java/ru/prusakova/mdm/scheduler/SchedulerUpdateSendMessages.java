package ru.prusakova.mdm.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.dto.EventRequest;
import ru.prusakova.mdm.dto.MdmMessageServiceOneRequest;
import ru.prusakova.mdm.dto.MdmMessageServiceTwoRequest;
import ru.prusakova.mdm.dto.enums.Target;
import ru.prusakova.mdm.model.MdmMessage;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.property.MdmProperty;
import ru.prusakova.mdm.service.*;
import ru.prusakova.mdm.service.feign.ChangePhoneOneFeignClientService;
import ru.prusakova.mdm.service.feign.ChangePhoneTwoFeignClientService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerUpdateSendMessages {

    private final MdmService mdmService;
    private final MdmOutboxService mdmOutboxService;
    private final ServicesClient servicesClient;
    private final MdmProperty mdmProperty;

    @Async("sendMessagesExecutor")
    @Scheduled(cron = "${mdm.retry-sending-cron}")
    public void retrySending() {
        log.info("Старт процесса повторной отправки сообщений");
        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDateTime ago15Minutes = dateTimeNow.minusMinutes(15);
        LocalDateTime ago24Hours = dateTimeNow.minusHours(24);

        Pageable pageable = PageRequest.of(mdmProperty.getPageNumber(), mdmProperty.getPageSize());
        Slice<MdmMessageOutbox> messages = mdmOutboxService.findByUpdateTimeAndStatus(ago15Minutes, ago24Hours, pageable);

        while (messages.hasNext()) {
            if (!messages.isEmpty()) {
                for (MdmMessageOutbox message : messages) {
                    MdmMessage mdmMessage = mdmService.findMdmMessage(message.getMdmMessageId());
                    servicesClient.request(mdmMessage.getGuid(), mdmMessage.getPayload().getPhone(), message);
                }
            }
        }
        log.info("Завершение процесса повторной отправки сообщений");
    }
}
