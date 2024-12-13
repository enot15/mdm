package ru.prusakova.mdm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import ru.prusakova.mdm.dto.common.CommonResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class AbstractWireMockTest extends AbstractTest {

    @BeforeEach
    public void setUp() {
        WireMock.reset();
    }

    @SneakyThrows
    protected void stubResponse(String url, HttpMethod method, CommonResponse<?> commonResponse) throws JsonProcessingException {
        stubResponseWithDelay(url, method, commonResponse, 0);
    }

    @SneakyThrows
    protected void stubResponseWithDelay(String url, HttpMethod method, CommonResponse<?> commonResponse, int delay) throws JsonProcessingException {
        stubFor(request(method.name(), urlMatching(url))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                          .withBody(objectMapper.writeValueAsBytes(commonResponse))
                        .withFixedDelay(delay)));
    }

    @SneakyThrows
    protected void stub400Response(String url, HttpMethod method) {
        stubFor(request(method.name(), urlMatching(url))
                .willReturn(badRequest()));
    }

    @SneakyThrows
    protected void stub500Response(String url, HttpMethod method) {
        stubFor(request(method.name(), urlMatching(url))
                .willReturn(serverError()));
    }
}
