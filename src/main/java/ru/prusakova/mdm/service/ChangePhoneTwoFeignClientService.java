package ru.prusakova.mdm.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.client.ChangePhoneTwoFeignClient;
import ru.prusakova.mdm.dto.MdmMessageResponse;
import ru.prusakova.mdm.dto.MdmMessageServiceTwoRequest;
import ru.prusakova.mdm.dto.common.CommonResponse;
import ru.prusakova.mdm.dto.enums.DeliveryStatus;
import ru.prusakova.mdm.dto.enums.IntegrationStatus;
import ru.prusakova.mdm.model.MdmMessageOutbox;
import ru.prusakova.mdm.repository.MdmMessageOutboxRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePhoneTwoFeignClientService {

    public static final MdmMessageResponse UPDATE_PHONE_FALLBACK_RESPONSE = new MdmMessageResponse();

    private final ChangePhoneTwoFeignClient changePhoneTwoFeignClient;
    private final MdmMessageOutboxRepository mdmMessageOutboxRepository;

    public MdmMessageResponse updatePhone(MdmMessageServiceTwoRequest request, MdmMessageOutbox mdmMessageOutbox) {
        try {
            ResponseEntity<CommonResponse<MdmMessageResponse>> responseEntity = changePhoneTwoFeignClient.postChangePhoneServiceTwo(request);

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
                log.warn("Получено сообщение об ошибке от сервиса user-data-service-two при попытке обновить телефон: {}", responseEntity.getBody().getErrorMessage());

                return UPDATE_PHONE_FALLBACK_RESPONSE;
            }

            return responseEntity.getBody().getBody();

        } catch (FeignException feignException) {
            log.error("Ошибка при обращении к сервису user-data-service-two. Статус ответа: {}. Тело ответа: {}. Сообщение об ошибке: {}",
                    feignException.status(), feignException.responseBody(), feignException.getMessage(), feignException);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при обращении к сервису user-data-service-two: {}", e.getMessage(), e);
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
