package ru.prusakova.mdm.service;

import org.springframework.stereotype.Service;
import ru.prusakova.mdm.dto.MdmChangePhoneEvent;
import ru.prusakova.mdm.dto.MdmMessageServiceOneRequest;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.service.feign.ChangePhoneOneClientService;

import java.util.UUID;

import static ru.prusakova.mdm.dto.enums.Target.USER_DATA_SERVICE_ONE;

@Service
public class MdmServiceRequestClientOne {

    private final MdmService mdmService;
    private final ChangePhoneOneClientService changePhoneOneClientService;

    public MdmServiceRequestClientOne(MdmService mdmService, ChangePhoneOneClientService changePhoneOneClientService) {
        this.mdmService = mdmService;
        this.changePhoneOneClientService = changePhoneOneClientService;
    }

    public void saveAndRequestServiceOne(MdmChangePhoneEvent event) {
        MdmMessageOutbox mdmMessageOutbox = mdmService.saveMessage(event, USER_DATA_SERVICE_ONE);
        requestServiceOne(event.getGuid(), event.getPhone(), mdmMessageOutbox);
    }

    public void requestServiceOne(String guid, String phone, MdmMessageOutbox mdmMessageOutbox) {
        changePhoneOneClientService.updatePhone(MdmMessageServiceOneRequest.builder()
                .id(UUID.randomUUID())
                .guid(guid)
                .phone(phone)
                .build(), mdmMessageOutbox);
    }
}
