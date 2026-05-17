from __future__ import annotations

import json
import logging
import os
from datetime import datetime, timezone
from typing import Any


_LOGGER = logging.getLogger("girisimkolay.backend")


def configure_logging() -> None:
    level_name = os.getenv("LOG_LEVEL", "INFO").upper()
    level = getattr(logging, level_name, logging.INFO)
    if _LOGGER.handlers:
        _LOGGER.setLevel(level)
        return

    handler = logging.StreamHandler()
    handler.setFormatter(logging.Formatter("%(message)s"))
    _LOGGER.addHandler(handler)
    _LOGGER.setLevel(level)
    _LOGGER.propagate = False


def log_event(event: str, **fields: Any) -> None:
    configure_logging()
    payload = {
        "timestamp": datetime.now(timezone.utc).isoformat(),
        "event": event,
        **fields,
    }
    _LOGGER.info(json.dumps(payload, ensure_ascii=True, default=str))
