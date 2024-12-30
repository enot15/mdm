package ru.prusakova.mdm.service.feign;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.client.ChangePhoneTwoFeignClient;
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
public class ChangePhoneTwoFeignClientService {

    public static final MdmMessageResponse UPDATE_PHONE_FALLBACK_RESPONSE = new MdmMessageResponse();

    private final ChangePhoneTwoFeignClient changePhoneTwoFeignClient;
    private final MdmOutboxService mdmOutboxService;

    public MdmMessageResponse updatePhone(MdmMessageServiceTwoRequest request, MdmMessageOutbox mdmMessageOutbox) {
        try {
            ResponseEntity<CommonResponse<MdmMessageResponse>> responseEntity = changePhoneTwoFeignClient.postChangePhoneServiceTwo(request);

            if (responseEntity.getBody() == null) {
                return UPDATE_PHONE_FALLBACK_RESPONSE;
            }

            if (responseEntity.getBody().getErrorMessage() != null) {
                log.warn("Получено сообщение об ошибке от сервиса user-data-service-two при попытке обновить телефон: {}", responseEntity.getBody().getErrorMessage());
                mdmOutboxService.updateMessageOutboxInDb(mdmMessageOutbox, DeliveryStatus.ERROR,
                        ResponseData.<MdmMessageResponse>builder().response(responseEntity.getBody().getBody()).build());

                return UPDATE_PHONE_FALLBACK_RESPONSE;
            }

            if (responseEntity.getStatusCode() == HttpStatus.OK
                    && IntegrationStatus.SUCCESS == responseEntity.getBody().getBody().getStatus()) {
                mdmOutboxService.updateMessageOutboxInDb(mdmMessageOutbox, DeliveryStatus.DELIVERED,
                        ResponseData.<MdmMessageResponse>builder().response(responseEntity.getBody().getBody()).build());

                return responseEntity.getBody().getBody();
            }

            boolean notHttpStatus200AndSuccess = responseEntity.getStatusCode() != HttpStatus.OK
                    && IntegrationStatus.SUCCESS.equals(responseEntity.getBody().getBody().getStatus());
            boolean httpStatus200AndNotSuccess =  responseEntity.getStatusCode() == HttpStatus.OK
                    && !IntegrationStatus.SUCCESS.equals(responseEntity.getBody().getBody().getStatus());
            if (notHttpStatus200AndSuccess || httpStatus200AndNotSuccess) {
                mdmOutboxService.updateMessageOutboxInDb(mdmMessageOutbox, DeliveryStatus.ERROR,
                        ResponseData.<MdmMessageResponse>builder().response(responseEntity.getBody().getBody()).build());

                return responseEntity.getBody().getBody();
            }
        } catch (FeignException feignException) {
            log.error("Ошибка при обращении к сервису user-data-service-two. Статус ответа: {}. Тело ответа: {}. Сообщение об ошибке: {}",
                    feignException.status(), feignException.responseBody(), feignException.getMessage(), feignException);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при обращении к сервису user-data-service-two: {}", e.getMessage(), e);
            MdmMessageResponse response = new MdmMessageResponse();
            response.setErrorMessage(e.getMessage());
            mdmOutboxService.updateMessageOutboxInDb(mdmMessageOutbox, DeliveryStatus.FATAL_ERROR,
                    ResponseData.<MdmMessageResponse>builder()
                        .errorMessages(List.of(e.getMessage()))
                        .build());
        }

        return UPDATE_PHONE_FALLBACK_RESPONSE;
    }
}
