package com.kett.TicketSystem.common;

import java.util.UUID;

public interface IConsumedDataManager<T> {
    Boolean add(Class<T> date);
    Boolean overwrite(Class<T> date);
    Boolean remove(UUID id);

    Class<T> get(UUID id);
    Boolean exists(UUID id);
}
