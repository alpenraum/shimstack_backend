package com.alpenraum.domain.exceptions

class SessionAlreadyExistsException(
    message: String = "",
) : ShimstackException(message)
