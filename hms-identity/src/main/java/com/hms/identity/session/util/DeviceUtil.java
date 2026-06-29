package com.hms.identity.session.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeviceUtil {

    public static String deviceName(

            HttpServletRequest request){

        String ua =
                request.getHeader("User-Agent");

        if(ua==null){

            return "Unknown";

        }

        if(ua.contains("Windows"))
            return "Windows";

        if(ua.contains("Android"))
            return "Android";

        if(ua.contains("iPhone"))
            return "iPhone";

        if(ua.contains("Mac"))
            return "Mac";

        return "Unknown";

    }

}
