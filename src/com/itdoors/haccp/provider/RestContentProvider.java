package com.itdoors.haccp.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class RestContentProvider extends ContentProvider {

	private static final int COMPANIES = 100;
	private static final int COMPANIES_ID = 101;
	private static final int COMPANIES_ID_COMPANY_OBJECTS = 103;
	
	private static final int COMPANY_OBJECTS_ID = 200;
	
	private static final int SERVICES = 400;
	private static final int CONTOURS = 500;
	
	private static final int POINTS_ID = 600;
	private static final int POINTS_IN_COMPANY_OBJECT_BY_CONTOUR = 601;
	private static final int POINTS_IN_COMPANY_OBJECT_BY_CONTOUR_SEARCH = 602;
	
	
	public static final String CONTENT_AUTHORITY = "com.itdoors.haccp.restcontentprovider";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);

	private static final UriMatcher uriMatcher = buildUriMatcher();

	private HaccpDatabase dbHelper;

	private static UriMatcher buildUriMatcher() {

		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = CONTENT_AUTHORITY;

		matcher.addURI(authority, "companies", COMPANIES);
		matcher.addURI(authority, "companies/#", COMPANIES_ID);
		matcher.addURI(authority, "companies/#/company_objects", COMPANIES_ID_COMPANY_OBJECTS);
		
		matcher.addURI(authority, "company_objects/#", COMPANY_OBJECTS_ID);
		
		matcher.addURI(authority, "services", SERVICES);
		matcher.addURI(authority, "contours", CONTOURS);
		matcher.addURI(authority, "company_objects/#/contours/#/points", POINTS_IN_COMPANY_OBJECT_BY_CONTOUR);
		matcher.addURI(authority, "company_objects/#/contours/#/points/search/*", POINTS_IN_COMPANY_OBJECT_BY_CONTOUR_SEARCH);
		
		matcher.addURI(authority, "points/#", POINTS_ID);
		
		return matcher;
	}

	@Override
	public boolean onCreate() {
		this.dbHelper = new HaccpDatabase(getContext());
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
			case COMPANIES:
				return HaccpContract.Companies.CONTENT_TYPE;
			case COMPANIES_ID:
				return HaccpContract.Companies.CONTENT_ITEM_TYPE;
			case COMPANIES_ID_COMPANY_OBJECTS:
				return HaccpContract.CompanyObjects.CONTENT_TYPE;
			case COMPANY_OBJECTS_ID:
				return HaccpContract.CompanyObjects.CONTENT_ITEM_TYPE;
			case SERVICES:
				return HaccpContract.Services.CONTENT_TYPE;
			case CONTOURS:
				return HaccpContract.Contours.CONTENT_TYPE;
			case POINTS_IN_COMPANY_OBJECT_BY_CONTOUR:
				return HaccpContract.Points.CONTENT_TYPE;
			case POINTS_IN_COMPANY_OBJECT_BY_CONTOUR_SEARCH:
				return HaccpContract.Points.CONTENT_TYPE;
			case POINTS_ID:
				return HaccpContract.Points.CONTENT_ITEM_TYPE;
			
			default:
				throw new IllegalArgumentException("Unknows uri[" + uri +"]");
		}
	}
	

	private static HashMap<String, String> sContoursProjectionMap;
	private static HashMap<String, String> sPointsInCObjByContProjMap;
	
	
	static{
		
		sContoursProjectionMap = new HashMap<String, String>();
		
		sContoursProjectionMap.put(HaccpContract.Contours._ID, 
				HaccpDatabase.Tables.CONTOURS + "." + HaccpContract.Contours._ID);
		sContoursProjectionMap.put(HaccpContract.Contours.UID, 
				HaccpDatabase.Tables.CONTOURS + "." + HaccpContract.Contours.UID);
		sContoursProjectionMap.put(HaccpContract.Contours.NAME, 
				HaccpDatabase.Tables.CONTOURS + "." + HaccpContract.Contours.NAME);
		sContoursProjectionMap.put(HaccpContract.Contours.SERVICE_ID, 
				HaccpDatabase.Tables.CONTOURS + "." + HaccpContract.Contours.SERVICE_ID);
		sContoursProjectionMap.put(HaccpContract.Contours.COLOR, 
				HaccpDatabase.Tables.CONTOURS + "." + HaccpContract.Contours.COLOR);
		
		sContoursProjectionMap.put(HaccpContract.Contours.SERVICE_ID_PROJECTION, 
				HaccpContract.Services._ID_FULL +" AS " + HaccpContract.Contours.SERVICE_ID_PROJECTION);
		sContoursProjectionMap.put(HaccpContract.Contours.SERVICE_UID_PROJECTION, 
				HaccpContract.Services.UID_FULL +" AS " + HaccpContract.Contours.SERVICE_UID_PROJECTION);
		sContoursProjectionMap.put(HaccpContract.Contours.SERVICE_NAME_PROJECTION, 
				HaccpContract.Services.NAME_FULL +" AS " + HaccpContract.Contours.SERVICE_NAME_PROJECTION);
		
		
		sPointsInCObjByContProjMap = new HashMap<String, String>();
		
		sPointsInCObjByContProjMap.put(HaccpContract.Points._ID, 
				HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points._ID);
		sPointsInCObjByContProjMap.put(HaccpContract.Points.UID, 
				HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.UID);
		sPointsInCObjByContProjMap.put(HaccpContract.Points.NAME, 
				HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.NAME);
		sPointsInCObjByContProjMap.put(HaccpContract.Points.PLAN_ID, 
				HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.PLAN_ID);
		sPointsInCObjByContProjMap.put(HaccpContract.Points.CONTOUR_ID, 
				HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.CONTOUR_ID);
		sPointsInCObjByContProjMap.put(HaccpContract.Points.STATUS_ID, 
				HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.STATUS_ID);
		sPointsInCObjByContProjMap.put(HaccpContract.Points.POINT_GROUP_ID, 
				HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.POINT_GROUP_ID);
		sPointsInCObjByContProjMap.put(HaccpContract.Points.INSTALATION_DATE, 
				HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.INSTALATION_DATE);
		sPointsInCObjByContProjMap.put(HaccpContract.Points.IMG_LATITUDE, 
				HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.IMG_LATITUDE);
		sPointsInCObjByContProjMap.put(HaccpContract.Points.IMG_LONGTITUDE, 
				HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.IMG_LONGTITUDE);
		sPointsInCObjByContProjMap.put(HaccpContract.Points.MAP_LATITUDE, 
				HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.MAP_LATITUDE);
		sPointsInCObjByContProjMap.put(HaccpContract.Points.MAP_LONGTITUDE, 
				HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.MAP_LONGTITUDE);
		
		sPointsInCObjByContProjMap.put(HaccpContract.Points.PLANS_ID_PROJECTION, 
				HaccpContract.Plans._ID_FULL +" AS " + HaccpContract.Points.PLANS_ID_PROJECTION);
		sPointsInCObjByContProjMap.put(HaccpContract.Points.PLANS_UID_PROJECTION, 
				HaccpContract.Plans.UID_FULL +" AS " + HaccpContract.Points.PLANS_UID_PROJECTION);
		sPointsInCObjByContProjMap.put(HaccpContract.Points.PLANS_NAME_PROJECTION, 
				HaccpContract.Plans.NAME_FULL +" AS " + HaccpContract.Points.PLANS_NAME_PROJECTION);
	
		
	}
	
	
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
		final SQLiteDatabase db = dbHelper.getReadableDatabase();
	 
		switch (uriMatcher.match(uri)) {
			
			case SERVICES:
				{
					if(TextUtils.isEmpty(sortOrder))
						sortOrder = HaccpContract.Services.DEFAULT_SORT;
					qBuilder.setTables(HaccpDatabase.Tables.SERVICES);
					Cursor cursor = qBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
					cursor.setNotificationUri(getContext().getContentResolver(), uri);
					return cursor;
				}
			case COMPANIES:
				{
					if(TextUtils.isEmpty(sortOrder))
						sortOrder = HaccpContract.Companies.DEFAULT_SORT;
					
					qBuilder.setTables(HaccpDatabase.Tables.COMPANIES);
					Cursor cursor = qBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
					cursor.setNotificationUri(getContext().getContentResolver(), uri);
					
					return cursor;
				}
			case CONTOURS:
				{
					if(TextUtils.isEmpty(sortOrder))
						sortOrder = HaccpContract.Contours.DEFAULT_SORT;
					
					qBuilder.setTables(
							HaccpDatabase.Tables.CONTOURS + " INNER JOIN " +  HaccpDatabase.Tables.SERVICES + 
							" ON " +  "(" + HaccpDatabase.Tables.CONTOURS + "." + HaccpContract.Contours.SERVICE_ID  + " = " +
										    HaccpDatabase.Tables.SERVICES + "." + HaccpContract.Services.UID + 
									   ")"
					);
					qBuilder.setProjectionMap(sContoursProjectionMap);
					Cursor cursor = qBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
					cursor.setNotificationUri(getContext().getContentResolver(), uri);
					return cursor;
				}
				
			
			case COMPANIES_ID_COMPANY_OBJECTS:
				{
					if(TextUtils.isEmpty(sortOrder))
						sortOrder = HaccpContract.CompanyObjects.DEFAULT_SORT;
				
					qBuilder.setTables(HaccpDatabase.Tables.COMPANY_OBJECTS);
					qBuilder.appendWhere(HaccpContract.CompanyObjects.UID);
					qBuilder.appendWhere("=");
					qBuilder.appendWhereEscapeString(HaccpContract.CompanyObjects.getCompanyId(uri));
					
					Cursor cursor = qBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
					cursor.setNotificationUri(getContext().getContentResolver(), uri);
					return cursor;
				}
				
			case POINTS_IN_COMPANY_OBJECT_BY_CONTOUR:
			{
				
				if(TextUtils.isEmpty(sortOrder))
					sortOrder = HaccpDatabase.Tables.PLANS + "." + HaccpContract.Plans.UID + "," +HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.UID;
				
				qBuilder.setTables(
						HaccpDatabase.Tables.PLANS + " INNER JOIN " +  HaccpDatabase.Tables.POINTS + 
						" ON " +  "(" + HaccpDatabase.Tables.PLANS + "." + HaccpContract.Plans.UID  + " = " +
									    HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.PLAN_ID + 
								   ")"
				);
				
				qBuilder.appendWhere(HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.CONTOUR_ID + "=" + HaccpContract.Points.getContourId(uri));
				qBuilder.appendWhere(" AND ");
				qBuilder.appendWhere(HaccpDatabase.Tables.PLANS + "." + HaccpContract.Plans.CONPANY_OBJECT_ID + "=" + HaccpContract.Points.getCompanyObjectId(uri));
				
				qBuilder.setProjectionMap(sPointsInCObjByContProjMap);
				
				
				Cursor cursor = qBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
				cursor.setNotificationUri(getContext().getContentResolver(), uri);
				return cursor;
			}
			
			case POINTS_IN_COMPANY_OBJECT_BY_CONTOUR_SEARCH:
			{
				
				if(TextUtils.isEmpty(sortOrder))
					sortOrder = HaccpDatabase.Tables.PLANS + "." + HaccpContract.Plans.UID + "," +HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.UID;
				
				qBuilder.setTables(
						HaccpDatabase.Tables.PLANS + " INNER JOIN " +  HaccpDatabase.Tables.POINTS + 
						" ON " +  "(" + HaccpDatabase.Tables.PLANS + "." + HaccpContract.Plans.UID  + " = " +
									    HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.PLAN_ID + 
								   ")"
				);
				
				qBuilder.appendWhere(HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.CONTOUR_ID + "=" + HaccpContract.Points.getContourId(uri));
				qBuilder.appendWhere(" AND ");
				qBuilder.appendWhere(HaccpDatabase.Tables.PLANS + "." + HaccpContract.Plans.CONPANY_OBJECT_ID + "=" + HaccpContract.Points.getCompanyObjectId(uri));
				qBuilder.appendWhere(" AND ");
				qBuilder.appendWhere(HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.NAME + " LIKE '%" + HaccpContract.Points.getSearchStatement(uri) +"%'");
				
				qBuilder.setProjectionMap(sPointsInCObjByContProjMap);
				
				Cursor cursor = qBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
				cursor.setNotificationUri(getContext().getContentResolver(), uri);
				return cursor;
			}
			
			case COMPANIES_ID:
			case COMPANY_OBJECTS_ID:
			case POINTS_ID:
				return null;
			default:
			     throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
	}



	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
