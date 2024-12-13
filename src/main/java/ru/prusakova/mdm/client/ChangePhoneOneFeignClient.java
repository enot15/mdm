package ru.prusakova.mdm.client;

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

    @PostMapping()
    @Retryable(maxAttempts = 2, backoff = @Backoff(1000))
    CommonResponse<MdmMessageResponse> postChangePhoneServiceOne(CommonRequest<MdmMessagePayload> request);
}
