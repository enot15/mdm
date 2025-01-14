package ru.prusakova.mdm.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.dto.enums.Target;
import ru.prusakova.mdm.model.MdmMessage;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.property.MdmProperty;
import ru.prusakova.mdm.service.*;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerUpdateSendMessages {

    private final MdmService mdmService;
    private final MdmOutboxService mdmOutboxService;
    private final MdmServiceRequestClientOne mdmServiceRequestClientOne;
    private final MdmServiceRequestClientTwo mdmServiceRequestClientTwo;
    private final MdmProperty mdmProperty;

    @Async("sendMessagesExecutor")
    @Scheduled(cron = "${mdm.retry-sending-cron}")
    public void retrySending() {
        log.info("Старт процесса повторной отправки сообщений");
        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDateTime ago15Minutes = dateTimeNow.minusMinutes(5);
        LocalDateTime ago24Hours = dateTimeNow.minusHours(24);

        Pageable pageable = PageRequest.of(mdmProperty.getPageNumber(), mdmProperty.getPageSize());
        Slice<MdmMessageOutbox> messages = mdmOutboxService.findByUpdateTimeAndStatus(ago15Minutes, ago24Hours, pageable);
        int count = 0;
        while (messages.hasNext()) {
            if (!messages.isEmpty()) {
                for (MdmMessageOutbox message : messages) {
                    if (message.getTarget() == Target.USER_DATA_SERVICE_ONE) {
                        MdmMessage mdmMessage = mdmService.findMdmMessage(message.getMdmMessageId());
                        mdmServiceRequestClientOne.requestServiceOne(mdmMessage.getGuid(), mdmMessage.getPayload().getPhone(), message);
                        count++;
                    }
                    if (message.getTarget() == Target.USER_DATA_SERVICE_TWO) {
                        MdmMessage mdmMessage = mdmService.findMdmMessage(message.getMdmMessageId());
                        mdmServiceRequestClientTwo.requestServiceTwo(mdmMessage.getGuid(), mdmMessage.getPayload().getPhone(), message);
                        count++;
                    }
                }
            }
            messages = mdmOutboxService.findByUpdateTimeAndStatus(ago15Minutes, ago24Hours, pageable);
            pageable = messages.nextPageable();
        }
        log.info("Завершение процесса повторной отправки сообщений. Отправлено {} сообщений", count);
    }
}
