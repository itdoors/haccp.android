
package com.itdoors.haccp.rest.robospice_retrofit;

import android.text.TextUtils;

import com.itdoors.haccp.model.rest.retrofit.MoreStatistics;
import com.itdoors.haccp.utils.ObjectUtils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

public class GetStatisticsRequest extends RetrofitSpiceRequest<MoreStatistics, HaccpApi> {

    private final static char dp = ':';
    private final static String head = "more";

    private Type type;
    private Params params;

    private GetStatisticsRequest(Type type, Params params) {
        super(MoreStatistics.class, HaccpApi.class);
        this.params = params;
        this.type = type;
    }

    @Override
    public MoreStatistics loadDataFromNetwork() {

        HaccpApi service = getService();
        switch (type) {
            case DEFAULT:
                return service.getStaticstics(params.token, params.id);
            case FROM_TIME:
                return service.getStaticstics(params.token, params.id, params.startDate,
                        params.endDate);
            case NEXT_DEFAULT:
                return service.getMoreStatistics(params.token, params.id, params.lastId);
            case NEXT_FROM_TIME:
                return service.getMoreStatistics(params.token, params.id, params.startDate,
                        params.endDate,
                        params.lastId);
        }
        throw new IllegalArgumentException("Wrong Statistics request inicialization!");
    }

    private static enum Type {
        DEFAULT, NEXT_DEFAULT, FROM_TIME, NEXT_FROM_TIME;
    }

    private static class Params {

        String token;
        String id;
        String lastId;
        String startDate;
        String endDate;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (!TextUtils.isEmpty(token))
                sb.append(dp).append(token);
            if (!TextUtils.isEmpty(id))
                sb.append(dp).append(id);
            if (!TextUtils.isEmpty(lastId))
                sb.append(dp).append(lastId);
            if (!TextUtils.isEmpty(startDate))
                sb.append(dp).append(startDate);
            if (!TextUtils.isEmpty(endDate))
                sb.append(dp).append(endDate);
            return sb.toString();
        }

    }

    public String getCacheKey() {

        StringBuilder sb = new StringBuilder();
        sb.append(head);
        if (!ObjectUtils.isNull(params))
            sb.append(params.toString());
        return sb.toString();
    }

    private static String getCacheKey(GetStatisticsRequest request) {
        return request.getCacheKey();
    }

    private static GetStatisticsRequest build(Params params) {
        return new Builder().setParams(params).build();
    }

    public static String getCacheKey(Params params) {
        return getCacheKey(build(params));
    }

    public static String getCacheKey(String token, String id) {

        Params params = new Params();
        params.id = String.valueOf(id);
        params.token = token;
        return getCacheKey(params);

    }

    public static String getCacheKey(String token, String id, String from, String to) {

        Params params = new Params();
        params.token = token;
        params.id = String.valueOf(id);
        params.startDate = from;
        params.endDate = to;

        return getCacheKey(params);

    }

    public static class Builder {

        Params params = new Params();

        public Builder setToken(String token) {
            params.token = token;
            return this;
        }

        public Builder setId(String id) {
            params.id = id;
            return this;
        }

        public Builder setLastId(String lastId) {
            params.lastId = lastId;
            return this;
        }

        public Builder setStartDate(String timeStamp) {
            params.startDate = timeStamp;
            return this;
        }

        public Builder setEndDate(String timeStamp) {
            params.endDate = timeStamp;
            return this;
        }

        public Builder setParams(Params params) {
            this.params = params;
            return this;
        }

        public GetStatisticsRequest build() {
            Type type = ensureType();
            return new GetStatisticsRequest(type, params);
        }

        private Type ensureType() {
            if (!TextUtils.isEmpty(params.token) && !TextUtils.isEmpty(params.id)
                    && !TextUtils.isEmpty(params.startDate)
                    && !TextUtils.isEmpty(params.endDate) && !TextUtils.isEmpty(params.lastId))
                return Type.NEXT_FROM_TIME;
            else if (!TextUtils.isEmpty(params.token) && !TextUtils.isEmpty(params.id)
                    && !TextUtils.isEmpty(params.startDate)
                    && !TextUtils.isEmpty(params.endDate))
                return Type.FROM_TIME;
            else if (!TextUtils.isEmpty(params.token) && !TextUtils.isEmpty(params.id)
                    && !TextUtils.isEmpty(params.lastId))
                return Type.NEXT_DEFAULT;
            else if (!TextUtils.isEmpty(params.token) && !TextUtils.isEmpty(params.id))
                return Type.DEFAULT;
            else
                throw new IllegalArgumentException("Wrong Statistics request inicialization!");
        }

    }

}
