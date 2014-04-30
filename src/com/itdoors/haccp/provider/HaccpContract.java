package com.itdoors.haccp.provider;

import android.net.Uri;


public final class HaccpContract {

    public static final String CONTENT_AUTHORITY = "com.itdoors.haccp.restcontentprovider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    
    
    private static final String PATH_SEARCH = "search";
    
    
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
    
    public static interface ServicesColumns{
    	public static final String NAME = "name";
    	
    	public static final String NAME_FULL = HaccpDatabase.Tables.SERVICES +"." + NAME;
    	public static final String UID_FULL = HaccpDatabase.Tables.SERVICES +"." + BaseColumns.UID;
    	public static final String _ID_FULL = HaccpDatabase.Tables.SERVICES +"." + BaseColumns._ID;
    	
    	
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
    	
    	public static final String NAME_FULL = HaccpDatabase.Tables.PLANS +"." + NAME;
    	public static final String UID_FULL = HaccpDatabase.Tables.PLANS +"." + BaseColumns.UID;
    	public static final String _ID_FULL = HaccpDatabase.Tables.PLANS +"." + BaseColumns._ID;
    	
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
   
    public static class Services implements ServicesColumns, BaseColumns{
    
    	public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("services").build();
    	
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.services";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.sercices";
        
        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";
        
        
    }
    
    public static class Contours implements ContoursColumns, BaseColumns{
    	
    	public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("contours").build();
    	public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.contours";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.contours";
        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";
        public static final String SERVICE_SORT = ContoursColumns.SERVICE_ID + " ASC";
        
        public static final String SERVICE_ID_PROJECTION = "service_id";
        public static final String SERVICE_NAME_PROJECTION = "service_name";
        public static final String SERVICE_UID_PROJECTION = "service_uid";
        
    }	
    
    
    public static class Companies implements CompaniesColumns, BaseColumns{
    	public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("companies").build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.companies";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.companies";
        
        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";
    }
   
    public static class CompanyObjects implements CompanyObjectsColums, BaseColumns{
    	        
    	public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.company_objects";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.company_objects";
     
        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";
        
        public static Uri buildUriForCompanyId(int companyId){
            return  BASE_CONTENT_URI
            		.buildUpon()
            		.appendPath("companies")
            		.appendPath(String.valueOf(companyId))
            		.appendPath("company_objects")
            		.build();
        }
        
        public static String getCompanyId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
    public static class Plans implements PlansColumns, BaseColumns{
    
    	public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.plans";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.plans";
        
        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";
    }
    
    public static class Points implements PointsColumns, BaseColumns{
    	
    	public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.points";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.points";
        
        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";
        
        public static final String PLANS_ID_PROJECTION = "plan_id";
        public static final String PLANS_NAME_PROJECTION = "plan_name";
        public static final String PLANS_UID_PROJECTION = "plan_uid";
        
        public static Uri builduriForCompanyObjectInContour(int companyObjectId, int contourId){
        	return  BASE_CONTENT_URI
            		.buildUpon()
            		.appendPath("company_objects")
            		.appendPath(String.valueOf(companyObjectId))
            		.appendPath("contours")
            		.appendPath(String.valueOf(contourId))
            		.appendPath("points")
            		.build();
        }
        
        
        
        public static String getCompanyObjectId(Uri uri){
        	return uri.getPathSegments().get(1);
        }
        
        public static String getContourId(Uri uri){
        	return uri.getPathSegments().get(3);
        }
        public static String getSearchStatement(Uri uri){
        	return uri.getPathSegments().get(6);
        }
        
        public static Uri buildSearchUri(int companyObjectId, int contourId, String query) {
            return builduriForCompanyObjectInContour(companyObjectId, contourId)
            		
            		.buildUpon()
            		.appendPath(PATH_SEARCH)
            		.appendPath(query).build();
        
        }
    }
}
