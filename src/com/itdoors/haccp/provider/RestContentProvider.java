package com.itdoors.haccp.provider;

import com.itdoors.haccp.utils.Logger;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class RestContentProvider extends ContentProvider {

	/** MIME types */
	// ------------------------------------------------------------------------------------
	/** The MIME type of a directory */
	private static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE;

	/** The MIME type of a single item */
	private static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE;
	// ------------------------------------------------------------------------------------

	private static final int COMPANIES = 100;
	private static final int COMPANIES_ID = 101;
	private static final int COMPANIES_ID_COMPANY_OBJECTS = 103;
	
	private static final int COMPANY_OBJECTS_ID = 200;
	private static final int COMPANY_OBJECTS_ID_PLANS = 201;
	
	//private static final int PLANS_ID = 300;
	private static final int PLANS_ID_CONTOUR_ID_POINTS = 301;
	
	private static final int CONTOURS = 400;
	//private static final int CONTOURS_ID = 401;
	
	private static final int POINTS_ID = 500;
	
	// --------------------------------------------------------------------------------------
	public static final String CONTENT_AUTHORITY = "com.itdoors.haccp.restcontentprovider";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
			+ CONTENT_AUTHORITY);

	private static final UriMatcher uriMatcher = buildUriMatcher();

	private PointsDatabase dbHelper;

	private static UriMatcher buildUriMatcher() {

		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = CONTENT_AUTHORITY;

		matcher.addURI(authority, "/companies", COMPANIES);
		matcher.addURI(authority, "/companies/#", COMPANIES_ID);
		matcher.addURI(authority, "/companies/#/company_objects", COMPANIES_ID_COMPANY_OBJECTS);
		
		matcher.addURI(authority, "/company_objects/#", COMPANY_OBJECTS_ID);
		matcher.addURI(authority, "/company_objects/#/plans", COMPANY_OBJECTS_ID_PLANS);

		matcher.addURI(authority, "/plans/#/#/points", PLANS_ID_CONTOUR_ID_POINTS);
		matcher.addURI(authority, "/contours", CONTOURS);
		matcher.addURI(authority, "/points/#", POINTS_ID);
		
		return matcher;
	}

	@Override
	public boolean onCreate() {
		this.dbHelper = new PointsDatabase(getContext());
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		
		switch (uriMatcher.match(uri)) {
			
			case COMPANIES:
				return PointContract.Companies.CONTENT_TYPE;
			case CONTOURS:
				return PointContract.Contours.CONTENT_TYPE;
			case COMPANIES_ID_COMPANY_OBJECTS:
				return PointContract.CompanyObjects.CONTENT_TYPE;
			case COMPANY_OBJECTS_ID_PLANS:
				return PointContract.Plans.CONTENT_TYPE;
			case PLANS_ID_CONTOUR_ID_POINTS:
				return PointContract.Points.CONTENT_TYPE;
			
			case COMPANIES_ID:
				return PointContract.Companies.CONTENT_ITEM_TYPE;
			case COMPANY_OBJECTS_ID:
				return PointContract.CompanyObjects.CONTENT_ITEM_TYPE;
			case POINTS_ID:
				return PointContract.Points.CONTENT_ITEM_TYPE;
				
			default:
				Logger.Loge( getClass(), "Unknown uri[" + uri + "]" );
				throw new IllegalArgumentException("Unknows uri[" + uri +"]");
		}
		
	}
	

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		SQLiteQueryBuilder sb = new SQLiteQueryBuilder();
		
		switch (uriMatcher.match(uri)) {
			
			case COMPANIES:
				
				// String sql = "SELECT * FROM " + PointsDatabase.Tables.COMPANIES;
				sb.setTables(PointsDatabase.Tables.COMPANIES);
				
				break;
			
			case CONTOURS:
				sb.setTables(PointsDatabase.Tables.CONTOURS);
				
				break;
			
			case COMPANIES_ID_COMPANY_OBJECTS:
				
				
				break;
			case COMPANY_OBJECTS_ID_PLANS:
				
				break;
			case PLANS_ID_CONTOUR_ID_POINTS:
				
				break;
			//---------------------------------------------------------		
			
			case COMPANIES_ID:
				
				break;
			case COMPANY_OBJECTS_ID:
				
				break;
			case POINTS_ID:
				
				break;
			default:
				 Logger.Loge( getClass(), "Unknown uri[" + uri + "]" );
	             throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return null;
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
