package com.construction.change.service;

import com.construction.change.dto.request.CreateChangeRequest;
import com.construction.change.dto.response.ChangeRequestResponse;
import java.util.List;

public interface ChangeRequestService {
    ChangeRequestResponse create(CreateChangeRequest request);
    List<ChangeRequestResponse> findAll(Long projectId);
    ChangeRequestResponse decide(Long id, boolean approve, String comment);
}
