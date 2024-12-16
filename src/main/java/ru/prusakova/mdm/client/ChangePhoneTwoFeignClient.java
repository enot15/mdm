package ru.prusakova.mdm.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import ru.prusakova.mdm.dto.MdmMessageResponse;
import ru.prusakova.mdm.dto.MdmMessageServiceTwoPayload;
import ru.prusakova.mdm.dto.common.CommonResponse;

@Component
@FeignClient(url = "${mdm.integration.change-phone.host-two}/user-data-service-two/user/update/phone", name = "mdm-client", contextId = "mdm-client-two")
public interface ChangePhoneTwoFeignClient {

    @Value("${mdm.retry.max-attempts}")
    int maxAttempts = 2;

    @Value("${mdm.retry.backoff}")
    int backoff = 1000;

    @PostMapping()
    @Retryable(maxAttempts = maxAttempts, backoff = @Backoff(backoff))
    CommonResponse<MdmMessageResponse> postChangePhoneServiceTwo(MdmMessageServiceTwoPayload request);
}
