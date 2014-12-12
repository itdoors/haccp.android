
package com.itdoors.haccp.oauth;

import com.google.gson.annotations.SerializedName;

public final class OAuthError {

    @SerializedName("error")
    private final String error_message;

    public OAuthError(String msg) {
        this.error_message = msg;
    }

    public String getErrorMessage() {
        return error_message;
    }

    public OAuthErrorType getType() {
        if ("invalid_grant".equals(error_message))
            return OAuthErrorType.INVALID_GRANT;
        else {
            return OAuthErrorType.UNKNOWN;
        }
    }

    @Override
    public String toString() {
        return "OAuthError:{" +
                "error='" + error_message + '\'' +
                '}';
    }

}
