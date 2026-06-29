package com.hms.identity.session.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IpUtil {

    public static String ip(

            HttpServletRequest request){

        String forwarded =
                request.getHeader(
                        "X-Forwarded-For");

        if(forwarded!=null){

            return forwarded.split(",")[0];

        }

        return request.getRemoteAddr();

    }

}
