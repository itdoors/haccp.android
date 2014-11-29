
package com.itdoors.haccp.oauth;

import com.google.gson.annotations.SerializedName;

public final class AccessToken {

    @SerializedName("access_token")
    private final String token;

    @SerializedName("expires_in")
    private final int expiresIn;

    @SerializedName("token_type")
    private final String tokenType;

    @SerializedName("scope")
    private final String scope;

    @SerializedName("refresh_token")
    private final String refreshToken;

    public AccessToken(String token, int expiresIn, String tokenType, String scope,
            String refreshToken) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.scope = scope;
        this.refreshToken = refreshToken;

    }

    public String getToken() {
        return token;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getScope() {
        return scope;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

}
