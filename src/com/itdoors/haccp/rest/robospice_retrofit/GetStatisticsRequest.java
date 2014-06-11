package com.itdoors.haccp.rest.robospice_retrofit;

import com.itdoors.haccp.model.rest.retrofit.MoreStatistics;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

public class GetStatisticsRequest extends RetrofitSpiceRequest<MoreStatistics, HaccpApi>{

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
			case DEFAULT: 		 return service.getStaticstics(params.id);
			case NEXT_DEFAULT:	 return service.getMoreStatistics(params.id, params.lastId);
			case FROM_TIME:		 return service.getStaticstics(params.id, params.startDate, params.endDate);
			case NEXT_FROM_TIME: return service.getMoreStatistics(params.id, params.startDate, params.endDate, params.lastId);
		}
		throw new IllegalArgumentException("Wrong Statistics request inicialization!");
	}
	
	private static enum Type{
		DEFAULT, NEXT_DEFAULT, FROM_TIME, NEXT_FROM_TIME;
	}
	
	private static class Params{
		String id;
		String lastId;
		String startDate;
		String endDate;
	}
	
	public static class Builder{
		
		Params params = new Params();
		public Builder setId(int id){
			params.id = Integer.toString(id);
			return this;
		}
		public Builder setLastId(int lastId){
			params.lastId = Integer.toString(lastId);
			return this;
		}
		public Builder setStartDate(String timeStamp){
			params.startDate = timeStamp;
			return this;
		}
		public Builder setEndDate(String timeStamp){
			params.endDate = timeStamp;
			return this;
		}
		
		public GetStatisticsRequest build(){
			Type type = ensureType();
			return new GetStatisticsRequest(type, params);
		}
		
		private Type ensureType(){
			if(params.id != null && params.startDate != null && params.endDate != null && params.lastId != null)
				return Type.NEXT_FROM_TIME;
			else if(params.id != null && params.startDate != null && params.endDate != null)
				return Type.FROM_TIME;
			else if(params.id != null && params.lastId != null)
				return Type.NEXT_DEFAULT;
			else if(params.id != null)
				return Type.DEFAULT;
			else throw new IllegalArgumentException("Wrong Statistics request inicialization!");
		}
		
		
		
	}

}
