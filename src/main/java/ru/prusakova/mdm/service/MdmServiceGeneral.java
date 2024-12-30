package ru.prusakova.mdm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.prusakova.mdm.dto.*;
import ru.prusakova.mdm.dto.enums.DeliveryStatus;
import ru.prusakova.mdm.dto.enums.EventType;
import ru.prusakova.mdm.dto.enums.Target;
import ru.prusakova.mdm.exception.MdmException;
import ru.prusakova.mdm.model.MdmMessage;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.property.MdmProperty;
import ru.prusakova.mdm.repository.MdmMessageOutboxRepository;
import ru.prusakova.mdm.repository.MdmMessageRepository;
import ru.prusakova.mdm.service.feign.ChangePhoneOneFeignClientService;
import ru.prusakova.mdm.service.feign.ChangePhoneTwoFeignClientService;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MdmServiceGeneral {

    private final MdmServiceRequestClientOne mdmServiceRequestClientOne;
    private final MdmServiceRequestClientTwo mdmServiceRequestClientTwo;

    public void saveInDbAndRequestClients(MdmChangePhoneEvent event) {

        mdmServiceRequestClientOne.saveInDbAndRequestServiceOne(event);
        mdmServiceRequestClientTwo.saveInDbAndRequestServiceTwo(event);
    }
}
