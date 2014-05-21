package com.itdoors.haccp.rest;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.itdoors.haccp.parser.rest.UpdatePointStatusParser;
import com.itdoors.haccp.utils.Logger;

public class UpdatePointStatusCommand extends BaseRESTCommand{

	public UpdatePointStatusCommand(Context context, long requestId, String uri, Bundle params) {
		
		super(	context,
				HttpMethod.POST,
				Uri.parse(uri),
				params,
				new UpdatePointStatusParser(),
				requestId
				);
	}

	@Override
	public boolean handleError(int httpResult, boolean allowRetry) {
		Logger.Loge(getClass(), "handleError");
		return Processor.getInstance(mContext).requestFailure(mRequestId, httpResult, allowRetry);
	}

	public static int getPointId(Uri uri){
		return Integer.parseInt(uri.getPathSegments().get(3));
	}
	
	public static int getStatusId(Bundle params){
		return Integer.parseInt(params.getString("pointStatusApiForm[statusId]"));
	}
	
}
