package ru.prusakova.mdm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.dto.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MdmServiceGeneral {

    private final MdmServiceRequestClientOne mdmServiceRequestClientOne;
    private final MdmServiceRequestClientTwo mdmServiceRequestClientTwo;

    public void saveAndRequestClients(MdmChangePhoneEvent event) {

        try {
            mdmServiceRequestClientOne.saveAndRequestServiceOne(event);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка", e);
        }

        try {
            mdmServiceRequestClientTwo.saveAndRequestServiceTwo(event);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка", e);
        }
    }
}
