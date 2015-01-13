
package com.itdoors.haccp.oauth;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

import com.google.gson.annotations.SerializedName;

public interface HaccpOAuthServiceApi {

    @POST("/oauth/v2/token")
    @FormUrlEncoded
    public void getAccessToken(

            @Field("client_id")
            String clientId,

            @Field("client_secret")
            String clientSecret,

            @Field("grant_type")
            String grantType,

            @Field("username")
            String username,

            @Field("password")
            String password,

            Callback<AccessToken> cb);

    @GET("/api/user")
    public void getUser(
            @Query("access_token")
            String token,
            Callback<User> cb);

    public class User {

        @SerializedName("id")
        private final int id;

        @SerializedName("username")
        private final String name;

        @SerializedName("email")
        private final String email;

        @SerializedName("big_avatar")
        private final String bigAvatar;

        @SerializedName("small_avatar")
        private final String smallAvatar;

        public User(int id, String name, String email, String bigAvatar, String smallAvatar) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.bigAvatar = bigAvatar;
            this.smallAvatar = smallAvatar;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getBigAvatar() {
            return bigAvatar;
        }

        public String getSmallAvatar() {
            return smallAvatar;
        }

        @Override
        public String toString() {
            return new StringBuilder()
                    .append("{")
                    .append("id").append(":").append(id).append(",\t")
                    .append("name").append(":").append(name).append(",\t")
                    .append("email").append(":").append(email).append(",\t")
                    .append("bigAvatar").append(":").append(bigAvatar).append(",\t")
                    .append("smallAvatar").append(":").append(smallAvatar).append(",\t")
                    .append("};")

                    .toString();
        }

    }
}
