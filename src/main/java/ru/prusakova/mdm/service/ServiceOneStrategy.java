package ru.prusakova.mdm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.dto.MdmMessageServiceOneRequest;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.service.feign.ChangePhoneOneFeignClientService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceOneStrategy implements TargetStrategy {

    private final ChangePhoneOneFeignClientService changePhoneOneFeignClientService;

    @Override
    public void request(String guid, String phone, MdmMessageOutbox mdmMessageOutbox) {
        changePhoneOneFeignClientService.updatePhone(MdmMessageServiceOneRequest.builder()
                .id(UUID.randomUUID())
                .guid(guid)
                .phone(phone)
                .build(), mdmMessageOutbox);
    }
}
