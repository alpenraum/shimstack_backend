package com.alpenraum.domain.exceptions

class SessionAlreadyExistsException(
    message: String = "",
) : ShimstackException(message)

class SessionAlreadyFinishedException(
    message: String = "",
) : ShimstackException(message)

