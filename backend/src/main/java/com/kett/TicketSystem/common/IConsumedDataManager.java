package com.kett.TicketSystem.common;

import java.util.Optional;
import java.util.UUID;

public interface IConsumedDataManager<T> {
    Boolean add(T date);
    Boolean overwrite(T date);
    Boolean remove(UUID id);

    Optional<T> get(UUID id);
    Boolean exists(UUID id);
}
