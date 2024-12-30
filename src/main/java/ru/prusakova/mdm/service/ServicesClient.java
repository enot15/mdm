package ru.prusakova.mdm.service;

import org.springframework.stereotype.Service;
import ru.prusakova.mdm.model.MdmMessageOutbox;

@Service
public class ServicesClient {

    TargetStrategy targetStrategy;

    public void request(String guid, String phone, MdmMessageOutbox mdmMessageOutbox) {
        targetStrategy.request(guid, phone, mdmMessageOutbox);
    }
}
