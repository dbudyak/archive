from website_monitor.services.errors import AppError


class DatabaseError(AppError):

    def __init__(self, message: str, original: Exception | None = None) -> None:
        super().__init__(message, retryable=True)
        self.original = original
