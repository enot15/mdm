package ru.prusakova.mdm.service.feign;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.client.ChangePhoneOneClient;
import ru.prusakova.mdm.dto.MdmMessageResponse;
import ru.prusakova.mdm.dto.MdmMessageServiceOneRequest;
import ru.prusakova.mdm.dto.MetaRequest;
import ru.prusakova.mdm.dto.ResponseData;
import ru.prusakova.mdm.dto.common.CommonRequest;
import ru.prusakova.mdm.dto.common.CommonResponse;
import ru.prusakova.mdm.dto.enums.DeliveryStatus;
import ru.prusakova.mdm.dto.enums.IntegrationStatus;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.property.MdmProperty;
import ru.prusakova.mdm.service.MdmOutboxService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePhoneOneClientService {

    public static final MdmMessageResponse UPDATE_PHONE_FALLBACK_RESPONSE = new MdmMessageResponse();

    private final ChangePhoneOneClient changePhoneOneClient;
    private final MdmOutboxService mdmOutboxService;
    private final MdmProperty mdmProperty;

    public void updatePhone(MdmMessageServiceOneRequest request, MdmMessageOutbox mdmMessageOutbox) {
        try {
            ResponseEntity<CommonResponse<MdmMessageResponse>> responseEntity = changePhoneOneClient.postChangePhoneServiceOne(CommonRequest.<MdmMessageServiceOneRequest>builder()
                    .meta(MetaRequest.builder()
                            .systemId(mdmProperty.getSystemId())
                            .sender(mdmProperty.getSender())
                            .build())
                    .body(request)
                    .build());

            if (responseEntity.getBody() != null && responseEntity.getBody().getErrorMessage() != null) {
                log.warn("Получено сообщение об ошибке от сервиса user-data-service-one при попытке обновить телефон: {}", responseEntity.getBody().getErrorMessage());
                mdmOutboxService.updateMessageOutbox(mdmMessageOutbox, DeliveryStatus.ERROR,
                        ResponseData.<MdmMessageResponse>builder().response(responseEntity.getBody().getBody()).build());
                return;
            }

            if (!responseEntity.getStatusCode().equals(HttpStatus.OK) || !IntegrationStatus.SUCCESS.equals(responseEntity.getBody().getBody().getStatus())) {
                mdmOutboxService.updateMessageOutbox(mdmMessageOutbox, DeliveryStatus.ERROR,
                        ResponseData.<MdmMessageResponse>builder().response(responseEntity.getBody().getBody()).build());
                return;
            }

            mdmOutboxService.updateMessageOutbox(mdmMessageOutbox, DeliveryStatus.DELIVERED,
                    ResponseData.<MdmMessageResponse>builder().response(responseEntity.getBody().getBody()).build());

        } catch (FeignException feignException) {
            log.error("Ошибка при обращении к сервису user-data-service-one. Статус ответа: {}. Тело ответа: {}. Сообщение об ошибке: {}",
                    feignException.status(), feignException.responseBody(), feignException.getMessage(), feignException);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при обращении к сервису user-data-service-one: {}", e.getMessage(), e);
            mdmOutboxService.updateMessageOutbox(mdmMessageOutbox, DeliveryStatus.FATAL_ERROR,
                    ResponseData.<MdmMessageResponse>builder()
                            .errorMessages(List.of(e.getMessage()))
                            .build());
        }
    }
}
