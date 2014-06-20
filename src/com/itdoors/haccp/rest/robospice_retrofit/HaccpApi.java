package com.itdoors.haccp.rest.robospice_retrofit;

import retrofit.http.GET;
import retrofit.http.Path;

import com.itdoors.haccp.model.rest.retrofit.MoreStatistics;

public interface HaccpApi {

	@GET("/api/v1/point/{id}/statistics")
	public MoreStatistics getStaticstics(@Path("id") String id);
	@GET("/api/v1/point/{id}/statistics/{startDate}/{endDate}")
	public MoreStatistics getStaticstics(@Path("id") String id, @Path("startDate") String startDate, @Path("endDate") String endDate);
	
	@GET("/api/v1/point/{id}/statistics/{lastStatisticId}")
	public MoreStatistics getMoreStatistics(@Path("id") String id, @Path("lastStatisticId") String lastId);
	@GET("/api/v1/point/{id}/statistics/{startDate}/{endDate}/{lastStatisticId}")
	public MoreStatistics getMoreStatistics(@Path("id") String id, @Path("startDate") String startDate, @Path("endDate") String endDate, @Path("lastStatisticId") String lastId);

}
