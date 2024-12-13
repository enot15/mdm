package ru.prusakova.mdm.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.prusakova.mdm.client.ChangePhoneTwoFeignClient;
import ru.prusakova.mdm.dto.MdmMessageResponse;
import ru.prusakova.mdm.dto.MdmMessageServiceTwoPayload;
import ru.prusakova.mdm.dto.common.CommonResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePhoneTwoFeignClientService {

    public static final MdmMessageResponse UPDATE_PHONE_FALLBACK_RESPONSE = new MdmMessageResponse();

    private final ChangePhoneTwoFeignClient changePhoneTwoFeignClient;

    public MdmMessageResponse updatePhone(MdmMessageServiceTwoPayload request) {
        try {
            CommonResponse<MdmMessageResponse> commonResponse = changePhoneTwoFeignClient.postChangePhoneServiceTwo(request);

            if (commonResponse.getErrorMessage() != null) {
                log.warn("Получено сообщение об ошибке от сервиса user-data-service-two при попытке обновить телефон: {}", commonResponse.getErrorMessage());

                return UPDATE_PHONE_FALLBACK_RESPONSE;
            }

            return commonResponse.getBody();

        } catch (FeignException feignException) {
            log.error("Ошибка при обращении к сервису user-data-service-two. Статус ответа: {}. Тело ответа: {}. Сообщение об ошибке: {}",
                    feignException.status(), feignException.responseBody(), feignException.getMessage(), feignException);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при обращении к сервису user-data-service-two: {}", e.getMessage(), e);
        }

        return UPDATE_PHONE_FALLBACK_RESPONSE;
    }
}
