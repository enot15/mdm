package ru.prusakova.mdm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import ru.prusakova.mdm.client.ChangePhoneOneFeignClient;
import ru.prusakova.mdm.client.ChangePhoneTwoFeignClient;
import ru.prusakova.mdm.dto.EventRequest;
import ru.prusakova.mdm.dto.MdmMessagePayload;
import ru.prusakova.mdm.dto.MdmMessageServiceTwoPayload;
import ru.prusakova.mdm.service.ChangePhoneOneFeignClientService;
import ru.prusakova.mdm.service.ChangePhoneTwoFeignClientService;

import java.util.List;
import java.util.UUID;

@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
            "mdm.integration.change-phone.host-one=http://localhost:${wiremock.server.port}",
            "mdm.integration.change-phone.host-two=http://localhost:${wiremock.server.port}"
    })
@ActiveProfiles("test")
public class AbstractTest {

    protected static MdmMessagePayload MDM_MESSAGE_PAYLOAD = MdmMessagePayload.builder()
            .id(UUID.randomUUID())
            .guid("12345678901234567890123456789012")
            .phone("+79268880011")
            .build();

    protected static MdmMessageServiceTwoPayload MDM_MESSAGE_TWO_PAYLOAD = MdmMessageServiceTwoPayload.builder()
            .id(UUID.randomUUID())
            .systemId("mdm-prusakova")
            .events(List.of(EventRequest.builder()
                    .eventType("change_phone")
                    .guid("12345678901234567890123456789012")
                    .phone("+79268880011")
                    .build()))
            .build();

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ChangePhoneOneFeignClientService changePhoneOneFeignClientService;

    @Autowired
    protected ChangePhoneTwoFeignClientService changePhoneTwoFeignClientService;

    @Autowired
    protected ChangePhoneOneFeignClient changePhoneOneFeignClient;

    @Autowired
    protected ChangePhoneTwoFeignClient changePhoneTwoFeignClient;
}
