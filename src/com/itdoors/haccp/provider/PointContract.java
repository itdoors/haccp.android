package com.itdoors.haccp.provider;

import android.net.Uri;

public final class PointContract {

    public static final String CONTENT_AUTHORITY = "com.itdoors.haccp.restcontentprovider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    
    interface BaseColumns extends android.provider.BaseColumns{
		String UID = "uid";
	}
    
    public static interface CompaniesColumns{
    	public static final String NAME = "name";
    }
   
    public static interface CompanyObjectsColums{
    	public static final String COMPANY_ID = "company_id";
    	public static final String NAME = "name";
    }

    public static interface ContoursColumns{
    	public static final String NAME = "name";
    	public static final String COLOR = "color";
    	public static final String SERVICE_ID = "service_id";
    	public static final String SLUG = "slug";
    	public static final String LEVEL = "level";
    }
    
    public static interface PlansColumns{
    	public static final String NAME = "name";
    	public static final String CONPANY_OBJECT_ID = "company_object_id";
    	public static final String IMG_SRC = "img_src";
    	public static final String PARENT_ID = "parent_id";
    	public static final String IMG_WIDHT = "image_width";
    	public static final String IMG_HEIGHT = "image_height";
    	public static final String LATITUDE = "latitude";
    	public static final String LONGTITUDE = "longitude";
    	public static final String TYPE = "type";
    }
    
    public static interface PointsColumns{
    	public static final String NAME = "name";
    	public static final String PLAN_ID = "plan_id";
    	public static final String POINT_GROUP_ID = "point_group_id";
    	public static final String IMG_LATITUDE = "imagelatitude";
    	public static final String IMG_LONGTITUDE = "imagelongitude";
    	public static final String MAP_LATITUDE = "maplatitude";
    	public static final String MAP_LONGTITUDE = "maplongitude";
    	public static final String CONTOUR_ID = "contour_id";
    	public static final String INSTALATION_DATE = "installationdate";
    	public static final String STATUS_ID = "status_id";
    }
   
    
    public static class Companies implements CompaniesColumns, BaseColumns{
    	public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("/companies").build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.companies";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.companies";
    }
   
    public static class CompanyObjects implements CompanyObjectsColums, BaseColumns{
    	public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("/company_objects").build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.company_objects";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.company_objects";
    }	
    
    public static class Contours implements ContoursColumns, BaseColumns{
    	public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("/contours").build();
       
    	public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.contours";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.contours";
    }	
    
    public static class Plans implements PlansColumns, BaseColumns{
    	public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("/plans").build();
       
    	public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.plans";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.plans";
    }
    
    public static class Points implements PointsColumns, BaseColumns{
    	public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("/points").build();
       
    	public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.points";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.points";
    }
}
