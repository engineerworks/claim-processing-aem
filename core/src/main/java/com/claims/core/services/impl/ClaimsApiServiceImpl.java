package com.claims.core.services.impl;

import com.claims.core.services.ClaimsApiService;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.osgi.service.component.annotations.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component(
        service = ClaimsApiService.class,
        immediate = true
)
public class ClaimsApiServiceImpl
        implements ClaimsApiService {

    /*
     * Temporary local URL.
     *
     * We will move this into OSGi configuration
     * in the next step.
     */
    private static final String CLAIMS_API_URL =
            "http://localhost:8080/api/claims/process";


    @Override
    public String processClaim(String claimJson) {

        HttpPost httpPost =
                new HttpPost(CLAIMS_API_URL);

        StringEntity requestEntity =
                new StringEntity(
                        claimJson,
                        ContentType.APPLICATION_JSON);

        httpPost.setEntity(requestEntity);

        try (CloseableHttpClient httpClient =
                     HttpClients.createDefault();

             CloseableHttpResponse response =
                     httpClient.execute(httpPost)) {

            int statusCode =
                    response
                            .getStatusLine()
                            .getStatusCode();

            HttpEntity responseEntity =
                    response.getEntity();

            String responseBody =
                    responseEntity == null
                            ? ""
                            : EntityUtils.toString(
                            responseEntity,
                            StandardCharsets.UTF_8);

            if (statusCode >= 200
                    && statusCode < 300) {

                return responseBody;
            }

            throw new IllegalStateException(
                    "Claims API returned HTTP "
                            + statusCode
                            + ": "
                            + responseBody);

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Unable to call Claims API",
                    exception);
        }
    }
}
