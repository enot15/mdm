package ru.prusakova.mdm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.dto.MdmChangePhoneEvent;
import ru.prusakova.mdm.dto.MdmMessageServiceOneRequest;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.service.feign.ChangePhoneOneFeignClientService;

import java.util.UUID;

import static ru.prusakova.mdm.dto.enums.Target.USER_DATA_SERVICE_ONE;

@Service
public class MdmServiceRequestClientOne extends ServicesClient {

    private final MdmService mdmService;
    private final ChangePhoneOneFeignClientService changePhoneOneFeignClientService;

    public MdmServiceRequestClientOne(MdmService mdmService, ChangePhoneOneFeignClientService changePhoneOneFeignClientService) {
        this.mdmService = mdmService;
        this.changePhoneOneFeignClientService = changePhoneOneFeignClientService;
        this.targetStrategy = new ServiceOneStrategy(changePhoneOneFeignClientService);
    }

    public void saveInDbAndRequestServiceOne(MdmChangePhoneEvent event) {
        MdmMessageOutbox mdmMessageOutbox = mdmService.saveMessageInDb(event, USER_DATA_SERVICE_ONE);
        requestServiceOne(event.getGuid(), event.getPhone(), mdmMessageOutbox);
    }

    public void requestServiceOne(String guid, String phone, MdmMessageOutbox mdmMessageOutbox) {
        changePhoneOneFeignClientService.updatePhone(MdmMessageServiceOneRequest.builder()
                .id(UUID.randomUUID())
                .guid(guid)
                .phone(phone)
                .build(), mdmMessageOutbox);
    }
}
