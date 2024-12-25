package ru.prusakova.mdm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import ru.prusakova.mdm.AbstractWireMockTest;
import ru.prusakova.mdm.dto.common.CommonResponse;

class ChangePhoneTwoFeignClientServiceTest extends AbstractWireMockTest {

    @Test
    void when_updatePhone_success() throws JsonProcessingException {
        stubResponse("/user-data-service-two/user/update/phone", HttpMethod.POST, CommonResponse.builder().build());
        changePhoneTwoFeignClientService.updatePhone(MDM_MESSAGE_TWO_PAYLOAD);
    }

    @Test
    void when_updatePhone_timeout() throws JsonProcessingException {
        stubResponseWithDelay("/user-data-service-two/user/update/phone", HttpMethod.POST, CommonResponse.builder().build(), 6000);
        changePhoneTwoFeignClientService.updatePhone(MDM_MESSAGE_TWO_PAYLOAD);
    }

    @Test
    void when_updatePhone_400badRequest() {
        stub400Response("/user-data-service-two/user/update/phone", HttpMethod.POST);
        changePhoneTwoFeignClientService.updatePhone(MDM_MESSAGE_TWO_PAYLOAD);
    }

    @Test
    void when_updatePhone_500serverError() {
        stub500Response("/user-data-service-two/user/update/phone", HttpMethod.POST);
        changePhoneTwoFeignClientService.updatePhone(MDM_MESSAGE_TWO_PAYLOAD);
    }
}