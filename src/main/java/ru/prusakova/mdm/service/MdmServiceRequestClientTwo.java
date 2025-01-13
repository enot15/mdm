package ru.prusakova.mdm.service;

import org.springframework.stereotype.Service;
import ru.prusakova.mdm.dto.EventRequest;
import ru.prusakova.mdm.dto.MdmChangePhoneEvent;
import ru.prusakova.mdm.dto.MdmMessageServiceTwoRequest;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.property.MdmProperty;
import ru.prusakova.mdm.service.feign.ChangePhoneTwoClientService;

import java.util.List;
import java.util.UUID;

import static ru.prusakova.mdm.dto.enums.Target.USER_DATA_SERVICE_TWO;

@Service
public class MdmServiceRequestClientTwo {

    private final MdmService mdmService;
    private final ChangePhoneTwoClientService changePhoneTwoClientService;
    private final MdmProperty mdmProperty;

    public MdmServiceRequestClientTwo(MdmService mdmService,
                                      ChangePhoneTwoClientService changePhoneTwoClientService,
                                      MdmProperty mdmProperty) {
        this.mdmService = mdmService;
        this.changePhoneTwoClientService = changePhoneTwoClientService;
        this.mdmProperty = mdmProperty;
    }

    public void saveAndRequestServiceTwo(MdmChangePhoneEvent event) {
        MdmMessageOutbox mdmMessageOutbox = mdmService.saveMessage(event, USER_DATA_SERVICE_TWO);
        requestServiceTwo(event.getGuid(), event.getPhone(), mdmMessageOutbox);
    }

    public void requestServiceTwo(String guid, String phone, MdmMessageOutbox mdmMessageOutbox) {
        changePhoneTwoClientService.updatePhone(MdmMessageServiceTwoRequest.builder()
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
