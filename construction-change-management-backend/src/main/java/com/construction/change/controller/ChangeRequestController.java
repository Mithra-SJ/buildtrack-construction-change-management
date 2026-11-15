package com.construction.change.controller;

import com.construction.change.dto.request.CreateChangeRequest;
import com.construction.change.dto.request.DecisionRequest;
import com.construction.change.dto.response.ChangeRequestResponse;
import com.construction.change.service.ChangeRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/change-requests")
public class ChangeRequestController {
    private final ChangeRequestService changeRequestService;
    public ChangeRequestController(ChangeRequestService changeRequestService) { this.changeRequestService = changeRequestService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChangeRequestResponse create(@Valid @RequestBody CreateChangeRequest request) {
        return changeRequestService.create(request);
    }

    @GetMapping
    public List<ChangeRequestResponse> findAll(@RequestParam(required = false) Long projectId) {
        return changeRequestService.findAll(projectId);
    }

    @PostMapping("/{id}/approve")
    public ChangeRequestResponse approve(@PathVariable Long id, @RequestBody(required = false) DecisionRequest request) {
        return changeRequestService.decide(id, true, request == null ? null : request.comment());
    }

    @PostMapping("/{id}/reject")
    public ChangeRequestResponse reject(@PathVariable Long id, @RequestBody(required = false) DecisionRequest request) {
        return changeRequestService.decide(id, false, request == null ? null : request.comment());
    }
}
