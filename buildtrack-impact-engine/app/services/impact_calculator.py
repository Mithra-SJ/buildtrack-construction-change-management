
import pandas as pd
from datetime import timedelta
from decimal import Decimal

from app.schemas import ImpactRequest


VALID_STATUSES = {"DRAFT", "SUBMITTED", "APPROVED", "REJECTED"}


def calculate_impact(request: ImpactRequest):
    baseline_budget = request.baseline.approvedBudget
    baseline_date = request.baseline.plannedEndDate

    records = []

    for change in request.changeRequests:
        status = change.status.upper()

        if status not in VALID_STATUSES:
            raise ValueError(f"Unsupported status: {change.status}")

        records.append({
            "status": status,
            "cost": change.estimatedCost,
            "delay": change.estimatedDelayDays
        })

    df = pd.DataFrame(records, columns=["status", "cost", "delay"])

    approved = df[df["status"] == "APPROVED"]
    pending = df[df["status"].isin(["DRAFT", "SUBMITTED"])]

    approved_cost = Decimal(str(approved["cost"].sum()))
    approved_delay = int(approved["delay"].sum())

    pending_cost = Decimal(str(pending["cost"].sum()))
    pending_delay = int(pending["delay"].sum())

    approved_forecast_budget = baseline_budget + approved_cost
    approved_forecast_date = baseline_date + timedelta(days=approved_delay)

    scenario_budget = approved_forecast_budget + pending_cost
    scenario_date = approved_forecast_date + timedelta(days=pending_delay)

    return {
        "projectId": request.projectId,
        "approvedForecast": {
            "budget": approved_forecast_budget,
            "completionDate": approved_forecast_date
        },
        "pendingExposure": {
            "cost": pending_cost,
            "delayDays": pending_delay
        },
        "scenarioForecast": {
            "budget": scenario_budget,
            "completionDate": scenario_date
        }
    }
