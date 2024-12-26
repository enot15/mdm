package ru.prusakova.mdm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.prusakova.mdm.dto.*;
import ru.prusakova.mdm.dto.enums.DeliveryStatus;
import ru.prusakova.mdm.dto.enums.EventType;
import ru.prusakova.mdm.dto.enums.Target;
import ru.prusakova.mdm.exception.MdmException;
import ru.prusakova.mdm.model.MdmMessage;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.property.MdmProperty;
import ru.prusakova.mdm.repository.MdmMessageOutboxRepository;
import ru.prusakova.mdm.repository.MdmMessageRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MdmService {

    private final MdmMessageRepository mdmMessageRepository;
    private final MdmMessageOutboxRepository mdmMessageOutboxRepository;
    private final ChangePhoneOneFeignClientService changePhoneOneFeignClientService;
    private final ChangePhoneTwoFeignClientService changePhoneTwoFeignClientService;
    private final MdmProperty mdmProperty;

    public void saveInDbAndRequestClients(MdmChangePhoneEvent event) {
        List<MdmMessageOutbox> mdmMessageOutboxes = saveMessageInDb(event);

        requestServiceOne(event.getGuid(), event.getPhone(), mdmMessageOutboxes.get(0));
        requestServiceTwo(event.getGuid(), event.getPhone(), mdmMessageOutboxes.get(1));
    }

    @Async("deleteOldLinkInfosExecutor")
    @Scheduled(cron = "${mdm.retry-sending-cron}")
    public void retrySending() {
        log.info("Старт процесса повторной отправки сообщений");
        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDateTime ago15Minutes = dateTimeNow.minusMinutes(15);
        LocalDateTime ago24Hours = dateTimeNow.minusHours(24);

        int pageNumber = 0;
        int pageSize = 100;
        List<MdmMessageOutbox> messages;

        do {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            messages = mdmMessageOutboxRepository.findByUpdateTimeAndStatus(ago15Minutes, ago24Hours, pageable);

            if (!messages.isEmpty()) {
                for (MdmMessageOutbox message : messages) {
                    if (message.getTarget() == Target.USER_DATA_SERVICE_ONE) {
                        MdmMessage mdmMessage = findMdmMessage(message.getMdmMessageId());
                        requestServiceOne(mdmMessage.getGuid(), mdmMessage.getPayload().getPhone(), message);
                    }
                    if (message.getTarget() == Target.USER_DATA_SERVICE_TWO) {
                        MdmMessage mdmMessage = findMdmMessage(message.getMdmMessageId());
                        requestServiceTwo(mdmMessage.getGuid(), mdmMessage.getPayload().getPhone(), message);
                    }
                }
                pageNumber++;
            }
        } while (!messages.isEmpty());

        log.info("Завершение процесса повторной отправки сообщений");
    }

    private void requestServiceOne(String guid, String phone, MdmMessageOutbox mdmMessageOutbox) {
        changePhoneOneFeignClientService.updatePhone(MdmMessageServiceOneRequest.builder()
                .id(UUID.randomUUID())
                .guid(guid)
                .phone(phone)
                .build(), mdmMessageOutbox);
    }

    private void requestServiceTwo(String guid, String phone, MdmMessageOutbox mdmMessageOutbox) {
        changePhoneTwoFeignClientService.updatePhone(MdmMessageServiceTwoRequest.builder()
                .id(UUID.randomUUID())
                .systemId(mdmProperty.getSystemId())
                .events(List.of(EventRequest.builder()
                        .eventType("change_phone")
                        .guid(guid)
                        .phone(phone)
                        .build()))
                .build(), mdmMessageOutbox);
    }

    @Transactional
    private List<MdmMessageOutbox> saveMessageInDb(MdmChangePhoneEvent event) {
        MdmMessage mdmMessage = MdmMessage.builder()
                .externalId(UUID.fromString(event.getId()))
                .guid(event.getGuid())
                .type(EventType.valueOf(event.getType()))
                .payload(new MdmMessagePayload(event.getPhone()))
                .build();
        MdmMessage saveMdmMessage = mdmMessageRepository.save(mdmMessage);

        List<MdmMessageOutbox> mdmMessageOutboxes = new ArrayList<>();
        Arrays.stream(Target.values()).forEach(target -> {
            MdmMessageOutbox mdmMessageOutbox = MdmMessageOutbox.builder()
                    .mdmMessageId(saveMdmMessage.getId())
                    .status(DeliveryStatus.NEW)
                    .target(target)
                    .build();
            MdmMessageOutbox saveMdmMessageOutbox = mdmMessageOutboxRepository.save(mdmMessageOutbox);
            mdmMessageOutboxes.add(saveMdmMessageOutbox);
        });

        return mdmMessageOutboxes;
    }

    private MdmMessage findMdmMessage(UUID id) {
        return mdmMessageRepository.findById(id)
                .orElseThrow(() -> new MdmException("Не удалось найти сущность по id"));
    }
}
