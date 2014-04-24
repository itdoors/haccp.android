package com.itdoors.haccp.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PointsDatabase extends SQLiteOpenHelper {

	@SuppressWarnings("unused")
	private static String TAG = PointsDatabase.class.getSimpleName();

	private final static String DATABASE_NAME = "haccp.db";
	private final static int DATABASE_VERSION = 1;

	@SuppressWarnings("unused")
	private final Context context;

	

	interface CreateTableSqls {

		String COMPANIES = "CREATE TABLE companies ("+
				" _id   integer PRIMARY KEY AUTOINCREMENT NOT NULL,"+
				" name  varchar(50) NOT NULL,"+
				" uid   integer NOT NULL UNIQUE"+
				" );";
		
		String COMPANY_OBJECTS = "CREATE TABLE company_objects ("+
				" _id         integer PRIMARY KEY AUTOINCREMENT NOT NULL,"+
				" company_id  integer NOT NULL,"+
				" name        varchar(50) NOT NULL,"+
				" uid         integer NOT NULL UNIQUE,"+
				  /* Foreign keys */
				  " FOREIGN KEY (company_id) "+
				  " REFERENCES companies(uid) "+
				  " ON DELETE NO ACTION "+
				  " ON UPDATE NO ACTION "+
				  " );";
		
		String CONTOURS = "CREATE TABLE contours ("+
				" _id         integer PRIMARY KEY AUTOINCREMENT NOT NULL,"+
				" name        varchar(50) NOT NULL,"+
				" color       varchar(50) NOT NULL,"+
				" service_id  integer,"+
				" slug        varchar(50) NOT NULL DEFAULT NULL,"+
				" level       integer,"+
				" uid         integer NOT NULL UNIQUE,"+
				  /* Foreign keys */
				  " FOREIGN KEY (service_id) "+
				  " REFERENCES services(uid) "+
				  " ON DELETE NO ACTION "+
				  " ON UPDATE NO ACTION "+
				  " );";
		
		String PLANS = "CREATE TABLE plans ("+
				" _id                integer PRIMARY KEY AUTOINCREMENT NOT NULL,"+
				" company_object_id  integer NOT NULL,"+
				" name               varchar(50) NOT NULL,"+
				" img_src            varchar(50),"+
				" parent_id          integer DEFAULT NULL,"+
				" image_width        integer,"+
				" image_height       integer,"+
				" latitude           varchar(50) DEFAULT NULL,"+
				" longitude          varchar(50) DEFAULT NULL,"+
				" type               varchar(20) DEFAULT NULL,"+
				" uid                integer NOT NULL UNIQUE,"+
				  /* Foreign keys */
				  " FOREIGN KEY (parent_id) "+
				  " REFERENCES plans(uid) "+
				  " ON DELETE NO ACTION "+
				  " ON UPDATE NO ACTION, "+
				  " FOREIGN KEY (company_object_id) "+
				  " REFERENCES company_objects(uid) "+
				  " ON DELETE NO ACTION "+
				  " ON UPDATE NO ACTION "+
				  " );";
		
		String POINT_GROUP_CHARACTERISTICS = "CREATE TABLE point_group_characteristics ("+
				" _id                    integer PRIMARY KEY AUTOINCREMENT NOT NULL,"+
				" point_group_id         integer NOT NULL,"+
				" name                   varchar(50) NOT NULL,"+
				" description            text,"+
				" unit                   varchar(20) NOT NULL,"+
				" data_type              varchar(20) NOT NULL,"+
				" allow_value_max        varchar(20) DEFAULT NULL,"+
				" allow_value_min        varchar(20) DEFAULT NULL,"+
				" critical_value_top     varchar(20) DEFAULT NULL,"+
				" critical_value_bottom  varchar(20) DEFAULT NULL,"+
				" critical_color_middle  varchar(20) DEFAULT NULL,"+
				" input_type             varchar(20) DEFAULT NULL,"+
				" uid                    integer UNIQUE,"+
				  /* Foreign keys */
				  " FOREIGN KEY (point_group_id) "+
				  " REFERENCES point_groups(uid) "+
				  " ON DELETE NO ACTION "+
				  " ON UPDATE NO ACTION "+
				  " );";
		
		String POINT_GROUPS = "CREATE TABLE point_groups ("+
				" _id   integer PRIMARY KEY AUTOINCREMENT NOT NULL,"+
				" name  varchar(50) NOT NULL,"+
				" uid   integer NOT NULL UNIQUE"+
				" );";
		
		String POINT_STATISTICS = "CREATE TABLE point_statistics ("+
				" _id                integer PRIMARY KEY AUTOINCREMENT NOT NULL,"+
				" characteristic_id  integer NOT NULL,"+
				" point_id           integer NOT NULL,"+
				" created_at         timestamp,"+
				" entry_date         timestamp,"+
				" value              varchar(50) NOT NULL,"+
				" uid                integer NOT NULL UNIQUE,"+
				  /* Foreign keys */
				  " FOREIGN KEY (characteristic_id) "+
				  " REFERENCES point_group_characteristics(uid) "+
				  " ON DELETE NO ACTION "+
				  " ON UPDATE NO ACTION, "+
				  " FOREIGN KEY (point_id) "+
				  " REFERENCES points(uid) "+
				  " ON DELETE NO ACTION "+
				  " ON UPDATE NO ACTION "+
				  " );";
		
		String POINT_STATUSES = "CREATE TABLE point_statuses ("+
				" _id   integer PRIMARY KEY AUTOINCREMENT NOT NULL,"+
				" slug  varchar(50) NOT NULL,"+
				" name  varchar(50) NOT NULL,"+
				" uid   integer NOT NULL UNIQUE"+
				" );";
		String POINTS = "CREATE TABLE points ("+
				" _id               integer PRIMARY KEY AUTOINCREMENT NOT NULL,"+
				" plan_id           integer NOT NULL,"+
				" point_group_id    integer NOT NULL,"+
				" name              varchar(50) NOT NULL,"+
				" imagelatitude     varchar(50) DEFAULT NULL,"+
				" imagelongitude    varchar(50) DEFAULT NULL,"+
				" maplatitude       varchar(50) DEFAULT NULL,"+
				" maplongitude      varchar(50) DEFAULT NULL,"+
				" contour_id        integer,"+
				" installationdate  timestamp,"+
				" status_id         integer,"+
				" uid               integer NOT NULL UNIQUE,"+
				  /* Foreign keys */
				  " FOREIGN KEY (status_id) "+
				  " REFERENCES point_statuses(uid) "+
				  " ON DELETE NO ACTION "+
				  " ON UPDATE NO ACTION, "+
				  " FOREIGN KEY (contour_id) "+
				  " REFERENCES contours(uid) "+
				  " ON DELETE NO ACTION "+
				  " ON UPDATE NO ACTION,  "+
				  " FOREIGN KEY (point_group_id) "+
				  " REFERENCES point_groups(uid) "+
				  " ON DELETE NO ACTION "+
				  " ON UPDATE NO ACTION, "+ 
				  " FOREIGN KEY (plan_id) "+
				  " REFERENCES plans(uid) "+
				  " ON DELETE NO ACTION "+
				  " ON UPDATE NO ACTION "+
				  " );";
			
		String SERVICES = "CREATE TABLE services ("+
				" _id   integer PRIMARY KEY AUTOINCREMENT NOT NULL,"+
				" name  varchar(50) NOT NULL,"+
				" uid   integer NOT NULL UNIQUE"+
				" );";
		
	}

	interface Tables {
		
		String COMPANIES = "companies";
		String COMPANY_OBJECTS = "company_objects";
		String SERVICES = "services";
		String CONTOURS = "contours";
		String PLANS = "plans";
		String POINT_GROUPS = "point_groups";
		String POINT_GROUP_CHARACTERISTICS = "point_group_characteristics";
		String POINT_STATUSES ="point_statuses";
		String POINTS = "points";
		String POINT_STATISTICS = "point_statistics";
		
	}
	interface DeleteTableSqls{
		
		String COMPANIES 	 	  			=   "DROP TABLE IF EXISTS " + Tables.COMPANIES;
		String COMPANY_OBJECTS 	  			=   "DROP TABLE IF EXISTS " + Tables.COMPANY_OBJECTS;
		String CONTOURS 	 	  			=   "DROP TABLE IF EXISTS " + Tables.CONTOURS;
		String PLANS 	 	  				=   "DROP TABLE IF EXISTS " + Tables.PLANS;
		String POINT_GROUP_CHARACTERISTICS  =   "DROP TABLE IF EXISTS " + Tables.POINT_GROUP_CHARACTERISTICS;
		String POINT_GROUPS 	 	  		=   "DROP TABLE IF EXISTS " + Tables.POINT_GROUPS;
		String POINT_STATISTICS 	 	  	=   "DROP TABLE IF EXISTS " + Tables.POINT_STATISTICS;
		String POINT_STATUSES 	 	  		=   "DROP TABLE IF EXISTS " + Tables.POINT_STATUSES;
		String POINTS 	 	  				=   "DROP TABLE IF EXISTS " + Tables.POINTS;
		String SERVICES 	 	  			=   "DROP TABLE IF EXISTS " + Tables.SERVICES;
	}
	

	public PointsDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(CreateTableSqls.COMPANIES);
		db.execSQL(CreateTableSqls.COMPANY_OBJECTS);
		db.execSQL(CreateTableSqls.SERVICES);
		db.execSQL(CreateTableSqls.CONTOURS);
		db.execSQL(CreateTableSqls.PLANS);
		db.execSQL(CreateTableSqls.POINT_GROUPS);
		db.execSQL(CreateTableSqls.POINT_GROUP_CHARACTERISTICS);
		db.execSQL(CreateTableSqls.POINT_STATUSES);
		db.execSQL(CreateTableSqls.POINTS);
		db.execSQL(CreateTableSqls.POINT_STATISTICS);
		
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL(DeleteTableSqls.POINT_STATISTICS);
		db.execSQL(DeleteTableSqls.POINTS);
		db.execSQL(DeleteTableSqls.POINT_STATUSES);
		db.execSQL(DeleteTableSqls.POINT_GROUP_CHARACTERISTICS);
		db.execSQL(DeleteTableSqls.POINT_GROUPS);
		db.execSQL(DeleteTableSqls.PLANS);
		db.execSQL(DeleteTableSqls.CONTOURS);
		db.execSQL(DeleteTableSqls.SERVICES);
		db.execSQL(DeleteTableSqls.COMPANY_OBJECTS);
		db.execSQL(DeleteTableSqls.COMPANIES);
		
		onCreate(db);
		
	}
	
	
}
