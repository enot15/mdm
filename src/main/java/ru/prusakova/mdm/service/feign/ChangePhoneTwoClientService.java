package ru.prusakova.mdm.service.feign;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.client.ChangePhoneTwoClient;
import ru.prusakova.mdm.dto.MdmMessageResponse;
import ru.prusakova.mdm.dto.MdmMessageServiceTwoRequest;
import ru.prusakova.mdm.dto.ResponseData;
import ru.prusakova.mdm.dto.common.CommonResponse;
import ru.prusakova.mdm.dto.enums.DeliveryStatus;
import ru.prusakova.mdm.dto.enums.IntegrationStatus;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.service.MdmOutboxService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePhoneTwoClientService {

    public static final MdmMessageResponse UPDATE_PHONE_FALLBACK_RESPONSE = new MdmMessageResponse();

    private final ChangePhoneTwoClient changePhoneTwoClient;
    private final MdmOutboxService mdmOutboxService;

    public void updatePhone(MdmMessageServiceTwoRequest request, MdmMessageOutbox mdmMessageOutbox) {
        try {
            ResponseEntity<CommonResponse<MdmMessageResponse>> responseEntity = changePhoneTwoClient.postChangePhoneServiceTwo(request);

            if (responseEntity.getBody() != null && responseEntity.getBody().getErrorMessage() != null) {
                log.warn("Получено сообщение об ошибке от сервиса user-data-service-two при попытке обновить телефон: {}", responseEntity.getBody().getErrorMessage());
                mdmOutboxService.updateMessageOutbox(mdmMessageOutbox, DeliveryStatus.ERROR,
                        ResponseData.<MdmMessageResponse>builder().response(responseEntity.getBody().getBody()).build());
            }

            if (!responseEntity.getStatusCode().equals(HttpStatus.OK) || !IntegrationStatus.SUCCESS.equals(responseEntity.getBody().getBody().getStatus())) {
                mdmOutboxService.updateMessageOutbox(mdmMessageOutbox, DeliveryStatus.ERROR,
                        ResponseData.<MdmMessageResponse>builder().response(responseEntity.getBody().getBody()).build());
            }

            mdmOutboxService.updateMessageOutbox(mdmMessageOutbox, DeliveryStatus.DELIVERED,
                    ResponseData.<MdmMessageResponse>builder().response(responseEntity.getBody().getBody()).build());

        } catch (FeignException feignException) {
            log.error("Ошибка при обращении к сервису user-data-service-two. Статус ответа: {}. Тело ответа: {}. Сообщение об ошибке: {}",
                    feignException.status(), feignException.responseBody(), feignException.getMessage(), feignException);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при обращении к сервису user-data-service-two: {}", e.getMessage(), e);
            MdmMessageResponse response = new MdmMessageResponse();
            response.setErrorMessage(e.getMessage());
            mdmOutboxService.updateMessageOutbox(mdmMessageOutbox, DeliveryStatus.FATAL_ERROR,
                    ResponseData.<MdmMessageResponse>builder()
                        .errorMessages(List.of(e.getMessage()))
                        .build());
        }
    }
}
