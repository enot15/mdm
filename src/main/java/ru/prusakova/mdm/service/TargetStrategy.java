package ru.prusakova.mdm.service;

import ru.prusakova.mdm.model.MdmMessageOutbox;

public interface TargetStrategy {

    void request(String guid, String phone, MdmMessageOutbox mdmMessageOutbox);
}
