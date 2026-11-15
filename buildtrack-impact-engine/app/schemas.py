
from datetime import date
from decimal import Decimal
from typing import List

from pydantic import BaseModel, Field


class BaselineInput(BaseModel):
    approvedBudget: Decimal = Field(ge=0)
    plannedEndDate: date


class ChangeRequestInput(BaseModel):
    id: int
    estimatedCost: Decimal = Field(ge=0)
    estimatedDelayDays: int = Field(ge=0)
    status: str


class ImpactRequest(BaseModel):
    projectId: int
    baseline: BaselineInput
    changeRequests: List[ChangeRequestInput]


class ApprovedForecast(BaseModel):
    budget: Decimal
    completionDate: date


class PendingExposure(BaseModel):
    cost: Decimal
    delayDays: int


class ImpactResponse(BaseModel):
    projectId: int
    approvedForecast: ApprovedForecast
    pendingExposure: PendingExposure
    scenarioForecast: ApprovedForecast
