package com.hms.identity.session.mapper;

import com.hms.identity.session.dto.SessionResponse;
import com.hms.identity.session.entity.RefreshToken;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionMapper {

    public static SessionResponse toResponse(

            RefreshToken session){

        return new SessionResponse(

                session.getId(),

                session.getDeviceName(),

                session.getIpAddress(),

                session.getUserAgent(),

                session.getCreatedAt(),

                session.getLastUsedAt(),

                session.isRevoked()

        );

    }

}
