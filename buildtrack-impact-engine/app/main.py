
from fastapi import FastAPI, HTTPException
from fastapi.responses import JSONResponse

from app.schemas import ImpactRequest, ImpactResponse
from app.services.impact_calculator import calculate_impact
import logging
from fastapi.exceptions import RequestValidationError

app = FastAPI(
    title="BuildTrack Impact Engine",
    description="Construction project cost and schedule impact calculator",
    version="1.0.0"
)

logger = logging.getLogger("uvicorn.error")

@app.exception_handler(RequestValidationError)
async def validation_error_handler(request, exc):
    logger.error(
        "IMPACT ENGINE VALIDATION ERROR: %s",
        exc.errors()
    )

    return JSONResponse(
        status_code=422,
        content={"detail": exc.errors()}
    )


@app.get("/health")
def health():
    return {"status": "UP", "service": "BuildTrack Impact Engine"}


@app.post("/api/v1/impact/calculate", response_model=ImpactResponse)
def calculate_project_impact(request: ImpactRequest):
    try:
        return calculate_impact(request)
    except ValueError as error:
        raise HTTPException(status_code=400, detail=str(error))
