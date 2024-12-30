package ru.prusakova.mdm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.dto.EventRequest;
import ru.prusakova.mdm.dto.MdmMessageServiceOneRequest;
import ru.prusakova.mdm.dto.MdmMessageServiceTwoRequest;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.property.MdmProperty;
import ru.prusakova.mdm.service.feign.ChangePhoneOneFeignClientService;
import ru.prusakova.mdm.service.feign.ChangePhoneTwoFeignClientService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceTwoStrategy implements TargetStrategy {

    private final ChangePhoneTwoFeignClientService changePhoneTwoFeignClientService;
    private final MdmProperty mdmProperty;

    @Override
    public void request(String guid, String phone, MdmMessageOutbox mdmMessageOutbox) {
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
}
