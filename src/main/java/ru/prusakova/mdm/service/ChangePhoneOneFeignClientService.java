package ru.prusakova.mdm.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.client.ChangePhoneOneFeignClient;
import ru.prusakova.mdm.dto.MdmMessagePayload;
import ru.prusakova.mdm.dto.MdmMessageResponse;
import ru.prusakova.mdm.dto.MetaRequest;
import ru.prusakova.mdm.dto.common.CommonRequest;
import ru.prusakova.mdm.dto.common.CommonResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePhoneOneFeignClientService {

    public static final MdmMessageResponse UPDATE_PHONE_FALLBACK_RESPONSE = new MdmMessageResponse();

    private final ChangePhoneOneFeignClient changePhoneOneFeignClient;

    public MdmMessageResponse updatePhone(MdmMessagePayload request) {
        try {
            CommonResponse<MdmMessageResponse> commonResponse = changePhoneOneFeignClient.postChangePhoneServiceOne(CommonRequest.<MdmMessagePayload>builder()
                    .meta(MetaRequest.builder()
                            .systemId("mdm-prusakova")
                            .sender("prusakova")
                            .build())
                    .body(request)
                    .build());

            if (commonResponse.getErrorMessage() != null) {
                log.warn("Получено сообщение об ошибке от сервиса user-data-service-one при попытке обновить телефон: {}", commonResponse.getErrorMessage());

                return UPDATE_PHONE_FALLBACK_RESPONSE;
            }

            return commonResponse.getBody();

        } catch (FeignException feignException) {
            log.error("Ошибка при обращении к сервису user-data-service-one. Статус ответа: {}. Тело ответа: {}. Сообщение об ошибке: {}",
                    feignException.status(), feignException.responseBody(), feignException.getMessage(), feignException);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при обращении к сервису user-data-service-one: {}", e.getMessage(), e);
        }

        return UPDATE_PHONE_FALLBACK_RESPONSE;
    }
}
