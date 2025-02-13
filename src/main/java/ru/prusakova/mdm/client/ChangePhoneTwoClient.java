package ru.prusakova.mdm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import ru.prusakova.mdm.dto.MdmMessageResponse;
import ru.prusakova.mdm.dto.MdmMessageServiceTwoRequest;
import ru.prusakova.mdm.dto.common.CommonResponse;

@Component
@FeignClient(url = "${mdm.integration.change-phone.host-two}/user-data-service-two/user/update/phone", name = "mdm-client-two")
public interface ChangePhoneTwoClient {

    @PostMapping()
    @Retryable(maxAttemptsExpression  = "${mdm.retry.max-attempts}", backoff = @Backoff(delayExpression = "${mdm.retry.backoff}"))
    ResponseEntity<CommonResponse<MdmMessageResponse>> postChangePhoneServiceTwo(MdmMessageServiceTwoRequest request);
}
