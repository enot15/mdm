package ru.prusakova.mdm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.dto.EventRequest;
import ru.prusakova.mdm.dto.MdmChangePhoneEvent;
import ru.prusakova.mdm.dto.MdmMessageServiceTwoRequest;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.property.MdmProperty;
import ru.prusakova.mdm.service.feign.ChangePhoneTwoFeignClientService;

import java.util.List;
import java.util.UUID;

import static ru.prusakova.mdm.dto.enums.Target.USER_DATA_SERVICE_TWO;

@Service
public class MdmServiceRequestClientTwo extends ServicesClient {

    private final MdmService mdmService;
    private final ChangePhoneTwoFeignClientService changePhoneTwoFeignClientService;
    private final MdmProperty mdmProperty;

    public MdmServiceRequestClientTwo(MdmService mdmService,
                                      ChangePhoneTwoFeignClientService changePhoneTwoFeignClientService,
                                      MdmProperty mdmProperty) {
        this.mdmService = mdmService;
        this.changePhoneTwoFeignClientService = changePhoneTwoFeignClientService;
        this.mdmProperty = mdmProperty;
        this.targetStrategy = new ServiceTwoStrategy(changePhoneTwoFeignClientService, mdmProperty);
    }

    public void saveInDbAndRequestServiceTwo(MdmChangePhoneEvent event) {
        MdmMessageOutbox mdmMessageOutbox = mdmService.saveMessageInDb(event, USER_DATA_SERVICE_TWO);
        requestServiceTwo(event.getGuid(), event.getPhone(), mdmMessageOutbox);
    }

    public void requestServiceTwo(String guid, String phone, MdmMessageOutbox mdmMessageOutbox) {
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
