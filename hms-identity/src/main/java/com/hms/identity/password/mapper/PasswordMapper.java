package com.hms.identity.password.mapper;

import com.hms.identity.password.dto.PasswordHistoryResponse;
import com.hms.identity.password.entity.PasswordHistory;

public final class PasswordMapper {

    private PasswordMapper() {
    }

    public static PasswordHistoryResponse toResponse(
            PasswordHistory history) {

        return new PasswordHistoryResponse(

                history.getId(),

                history.getChangedAt()

        );

    }

}