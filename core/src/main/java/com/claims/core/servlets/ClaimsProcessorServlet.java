package com.claims.core.servlets;

import com.claims.core.services.ClaimsApiService;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/claims/process",
                "sling.servlet.methods=POST"
        }
)
public class ClaimsProcessorServlet
        extends SlingAllMethodsServlet {

    @Reference
    private ClaimsApiService claimsApiService;

    @Override
    protected void doPost(
            SlingHttpServletRequest request,
            SlingHttpServletResponse response)
            throws IOException {

        response.setContentType(
                "application/json");

        response.setCharacterEncoding(
                StandardCharsets.UTF_8.name());

        try {

            String claimJson =
                    request.getReader()
                            .lines()
                            .reduce(
                                    "",
                                    (current, line) ->
                                            current + line);

            if (claimJson.isBlank()) {

                response.setStatus(
                        SlingHttpServletResponse
                                .SC_BAD_REQUEST);

                response.getWriter().write(
                        "{\"errorMessage\":"
                                + "\"Claim request is required\"}");

                return;
            }

            String apiResponse =
                    claimsApiService.processClaim(
                            claimJson);

            response.setStatus(
                    SlingHttpServletResponse.SC_OK);

            response.getWriter().write(
                    apiResponse);

        } catch (IllegalStateException exception) {

            response.setStatus(
                    SlingHttpServletResponse
                            .SC_INTERNAL_SERVER_ERROR);

            response.getWriter().write(
                    "{\"errorMessage\":"
                            + "\"Unable to process claim\"}");
        }
    }
}
