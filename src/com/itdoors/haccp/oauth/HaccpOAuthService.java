
package com.itdoors.haccp.oauth;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;

public class HaccpOAuthService {

    private static HaccpOAuthServiceApi service;

    private HaccpOAuthService() {
    }

    public synchronized static HaccpOAuthServiceApi getService() {

        if (service == null) {

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(com.itdoors.haccp.Config.BASE_URL)
                    .setLogLevel(LogLevel.FULL)
                    .build();

            service = restAdapter.create(HaccpOAuthServiceApi.class);
        }

        return service;
    }

}
