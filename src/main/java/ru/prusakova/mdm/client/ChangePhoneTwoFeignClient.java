package ru.prusakova.mdm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import ru.prusakova.mdm.dto.MdmMessageResponse;
import ru.prusakova.mdm.dto.MdmMessageServiceTwoPayload;
import ru.prusakova.mdm.dto.common.CommonResponse;

@Component
@FeignClient(url = "${mdm.integration.change-phone.host-two}/user-data-service-two/user/update/phone", name = "mdm-client", contextId = "mdm-client-two")
public interface ChangePhoneTwoFeignClient {

    @PostMapping()
    CommonResponse<MdmMessageResponse> postChangePhoneServiceTwo(MdmMessageServiceTwoPayload request);
}
