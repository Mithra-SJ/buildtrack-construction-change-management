
package com.construction.change.service.impl;

import com.construction.change.dto.request.ImpactCalculationRequest;
import com.construction.change.dto.response.ImpactCalculationResponse;
import com.construction.change.entity.ChangeRequest;
import com.construction.change.entity.ProjectBaseline;
import com.construction.change.exception.ResourceNotFoundException;
import com.construction.change.repository.BaselineRepository;
import com.construction.change.repository.ChangeRequestRepository;
import com.construction.change.repository.ProjectRepository;
import com.construction.change.service.ImpactService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ImpactServiceImpl implements ImpactService {

    private final BaselineRepository baselineRepository;
    private final ChangeRequestRepository changeRequestRepository;
    private final ProjectRepository projectRepository;
    private final HttpClient httpClient;
    private final String impactEngineUrl;

    private final ObjectMapper objectMapper;

   public ImpactServiceImpl(
        BaselineRepository baselineRepository,
        ChangeRequestRepository changeRequestRepository,
        ProjectRepository projectRepository,
        ObjectMapper objectMapper,
        @Value("${buildtrack.impact-engine.url:http://localhost:8001}")
        String impactEngineUrl) {

    this.baselineRepository = baselineRepository;
    this.changeRequestRepository = changeRequestRepository;
    this.projectRepository = projectRepository;
    this.objectMapper = objectMapper;

    this.impactEngineUrl = impactEngineUrl.replaceAll("/+$", "");
    this.httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();
}

    @Override
    @Transactional(readOnly = true)
    public ImpactCalculationResponse calculateImpact(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException(
                    "Project not found: " + projectId);
        }

        // Get the latest baseline.
        List<ProjectBaseline> baselines =
                baselineRepository.findByProjectIdOrderByVersionDesc(projectId);

        if (baselines.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No baseline found for project: " + projectId);
        }

        ProjectBaseline baseline = baselines.get(0);

        // Get all change requests for this project.
        List<ChangeRequest> changes =
                changeRequestRepository.findByProjectIdOrderByCreatedAtDesc(projectId);

        // Convert database records into the Python request format.
        List<ImpactCalculationRequest.ChangeRequestInput> changeInputs =
                changes.stream()
                        .map(change -> new ImpactCalculationRequest.ChangeRequestInput(
                                change.getId(),
                                change.getEstimatedCost(),
                                change.getEstimatedDelayDays(),
                                change.getStatus()
                        ))
                        .toList();

        ImpactCalculationRequest request =
                new ImpactCalculationRequest(
                        projectId,
                        new ImpactCalculationRequest.BaselineInput(
                                baseline.getApprovedBudget(),
                                baseline.getPlannedEndDate()
                        ),
                        changeInputs
                );

                System.out.println("========== BUILDTRACK IMPACT REQUEST ==========");
System.out.println("Project ID: " + request.projectId());
System.out.println("Baseline: " + request.baseline());
System.out.println("Change Requests: " + request.changeRequests());
System.out.println("================================================");

        // Call the Python Impact Engine.
        try {
            String jsonBody = objectMapper.writeValueAsString(request);

            System.out.println("========== JSON SENT TO PYTHON ==========");
            System.out.println(jsonBody);
            System.out.println("=========================================");

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(
                            impactEngineUrl + "/api/v1/impact/calculate"
                    ))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            jsonBody,
                            StandardCharsets.UTF_8
                    ))
                    .build();

            HttpResponse<String> engineResponse = httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("Python HTTP Status: "
                    + engineResponse.statusCode());

            System.out.println("Python Response: "
                    + engineResponse.body());

            if (engineResponse.statusCode() < 200
                    || engineResponse.statusCode() >= 300) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Impact Engine returned HTTP "
                                + engineResponse.statusCode()
                                + ": "
                                + engineResponse.body()
                );
            }

            return objectMapper.readValue(
                    engineResponse.body(),
                    ImpactCalculationResponse.class
            );

        } catch (JsonProcessingException exception) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "JSON processing failed",
                    exception
            );

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Impact Engine request was interrupted",
                    exception
            );

        } catch (IOException exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to communicate with Impact Engine: "
                            + exception.getMessage(),
                    exception
            );
        }
    }
}
