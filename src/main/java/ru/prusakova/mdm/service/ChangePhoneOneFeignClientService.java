package ru.prusakova.mdm.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.client.ChangePhoneOneFeignClient;
import ru.prusakova.mdm.dto.MdmMessageResponse;
import ru.prusakova.mdm.dto.MdmMessageServiceOneRequest;
import ru.prusakova.mdm.dto.MetaRequest;
import ru.prusakova.mdm.dto.common.CommonRequest;
import ru.prusakova.mdm.dto.common.CommonResponse;
import ru.prusakova.mdm.dto.enums.DeliveryStatus;
import ru.prusakova.mdm.dto.enums.IntegrationStatus;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.property.MdmProperty;
import ru.prusakova.mdm.repository.MdmMessageOutboxRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePhoneOneFeignClientService {

    public static final MdmMessageResponse UPDATE_PHONE_FALLBACK_RESPONSE = new MdmMessageResponse();

    private final ChangePhoneOneFeignClient changePhoneOneFeignClient;
    private final MdmMessageOutboxRepository mdmMessageOutboxRepository;
    private final MdmProperty mdmProperty;

    public MdmMessageResponse updatePhone(MdmMessageServiceOneRequest request, MdmMessageOutbox mdmMessageOutbox) {
        try {
            ResponseEntity<CommonResponse<MdmMessageResponse>> responseEntity = changePhoneOneFeignClient.postChangePhoneServiceOne(CommonRequest.<MdmMessageServiceOneRequest>builder()
                    .meta(MetaRequest.builder()
                            .systemId(mdmProperty.getSystemId())
                            .sender(mdmProperty.getSender())
                            .build())
                    .body(request)
                    .build());

            if (responseEntity.getBody() != null
                    && responseEntity.getStatusCode() == HttpStatusCode.valueOf(200)
                    && IntegrationStatus.SUCCESS == responseEntity.getBody().getBody().getStatus()) {
                updateMessageOutboxInDb(mdmMessageOutbox, DeliveryStatus.DELIVERED, responseEntity.getBody().getBody());
            }

            if (responseEntity.getBody() != null
                    && (responseEntity.getStatusCode() != HttpStatusCode.valueOf(200)
                          && IntegrationStatus.SUCCESS == responseEntity.getBody().getBody().getStatus()
                        || responseEntity.getStatusCode() == HttpStatusCode.valueOf(200)
                          && IntegrationStatus.SUCCESS != responseEntity.getBody().getBody().getStatus()
                       )) {
                updateMessageOutboxInDb(mdmMessageOutbox, DeliveryStatus.ERROR, responseEntity.getBody().getBody());
            }

            if (responseEntity.getBody() != null && responseEntity.getBody().getErrorMessage() != null) {
                log.warn("Получено сообщение об ошибке от сервиса user-data-service-one при попытке обновить телефон: {}", responseEntity.getBody().getErrorMessage());

                return UPDATE_PHONE_FALLBACK_RESPONSE;
            }

            return responseEntity.getBody().getBody();

        } catch (FeignException feignException) {
            log.error("Ошибка при обращении к сервису user-data-service-one. Статус ответа: {}. Тело ответа: {}. Сообщение об ошибке: {}",
                    feignException.status(), feignException.responseBody(), feignException.getMessage(), feignException);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при обращении к сервису user-data-service-one: {}", e.getMessage(), e);
            MdmMessageResponse response = new MdmMessageResponse();
            response.setErrorMessage(e.getMessage());
            updateMessageOutboxInDb(mdmMessageOutbox, DeliveryStatus.FATAL_ERROR, response);
        }

        return UPDATE_PHONE_FALLBACK_RESPONSE;
    }

    private void updateMessageOutboxInDb(MdmMessageOutbox mdmMessageOutbox, DeliveryStatus deliveryStatus, MdmMessageResponse response) {
        mdmMessageOutbox.setResponseData(response);
        mdmMessageOutbox.setStatus(deliveryStatus);
        mdmMessageOutboxRepository.save(mdmMessageOutbox);
    }
}
