package ru.prusakova.mdm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.prusakova.mdm.dto.MdmChangePhoneEvent;
import ru.prusakova.mdm.dto.MdmMessagePayload;
import ru.prusakova.mdm.dto.enums.EventType;
import ru.prusakova.mdm.dto.enums.Target;
import ru.prusakova.mdm.exception.MdmException;
import ru.prusakova.mdm.model.MdmMessage;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.repository.MdmMessageRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MdmService {

    private final MdmMessageRepository mdmMessageRepository;
    private final MdmOutboxService mdmOutboxService;

    @Transactional
    public MdmMessageOutbox saveMessageInDb(MdmChangePhoneEvent event, Target target) {
        MdmMessage mdmMessage = MdmMessage.builder()
                .externalId(UUID.fromString(event.getId()))
                .guid(event.getGuid())
                .type(EventType.valueOf(event.getType()))
                .payload(new MdmMessagePayload(event.getPhone()))
                .build();
        MdmMessage saveMdmMessage = mdmMessageRepository.save(mdmMessage);

        return mdmOutboxService.saveNewEntity(saveMdmMessage.getId(), target);
    }

    public MdmMessage findMdmMessage(UUID id) {
        return mdmMessageRepository.findById(id)
                .orElseThrow(() -> new MdmException("Не удалось найти сущность по id"));
    }
}
