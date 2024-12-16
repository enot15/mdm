package ru.prusakova.mdm.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import ru.prusakova.mdm.dto.MdmMessagePayload;
import ru.prusakova.mdm.dto.MdmMessageResponse;
import ru.prusakova.mdm.dto.common.CommonRequest;
import ru.prusakova.mdm.dto.common.CommonResponse;

@Component
@FeignClient(url = "${mdm.integration.change-phone.host-one}/user-data-service-one/update-phone", name = "mdm-client", contextId = "mdm-client-one")
public interface ChangePhoneOneFeignClient {

    @Value("${mdm.retry.max-attempts}")
    int maxAttempts = 2;

    @Value("${mdm.retry.backoff}")
    int backoff = 1000;

    @PostMapping()
    @Retryable(maxAttempts = maxAttempts, backoff = @Backoff(backoff))
    CommonResponse<MdmMessageResponse> postChangePhoneServiceOne(CommonRequest<MdmMessagePayload> request);
}
