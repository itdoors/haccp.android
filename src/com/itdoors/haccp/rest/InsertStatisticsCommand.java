package com.itdoors.haccp.rest;


import com.itdoors.haccp.parser.rest.AddStatisticsParser;
import com.itdoors.haccp.utils.Logger;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

public class InsertStatisticsCommand extends BaseRESTCommand {

	public InsertStatisticsCommand(Context context, long requestId, String uri, Bundle params) {
		
		super(	context,
				HttpMethod.POST,
				Uri.parse(uri),
				params,
				new AddStatisticsParser(),
				requestId
				);
	}

	@Override
	public boolean handleError(int httpResult, boolean allowRetry) {
		Logger.Loge(getClass(), "handleError");
		return Processor.getInstance(mContext).requestFailure(mRequestId, httpResult, allowRetry);
	}
	

	public static int getPointId(Uri uri) {
		return Integer.parseInt(uri.getPathSegments().get(3));
	}
}
