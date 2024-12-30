package ru.prusakova.mdm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.dto.MdmMessageResponse;
import ru.prusakova.mdm.dto.ResponseData;
import ru.prusakova.mdm.dto.enums.DeliveryStatus;
import ru.prusakova.mdm.dto.enums.Target;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.repository.MdmMessageOutboxRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MdmOutboxService {

    private final MdmMessageOutboxRepository mdmMessageOutboxRepository;

    public void updateMessageOutboxInDb(MdmMessageOutbox mdmMessageOutbox, DeliveryStatus deliveryStatus, ResponseData<MdmMessageResponse> responseData) {
        mdmMessageOutbox.setResponseData(responseData);
        mdmMessageOutbox.setStatus(deliveryStatus);
        mdmMessageOutboxRepository.save(mdmMessageOutbox);
    }

    public Slice<MdmMessageOutbox> findByUpdateTimeAndStatus(LocalDateTime ago15Minutes, LocalDateTime ago24Hours, Pageable pageable) {
        return mdmMessageOutboxRepository.findByUpdateTimeAndStatus(ago15Minutes, ago24Hours, pageable);
    }

    public MdmMessageOutbox saveNewEntity(UUID id, Target target) {
        MdmMessageOutbox mdmMessageOutbox = MdmMessageOutbox.builder()
                .mdmMessageId(id)
                .status(DeliveryStatus.NEW)
                .target(target)
                .build();
        return mdmMessageOutboxRepository.save(mdmMessageOutbox);
    }
}
