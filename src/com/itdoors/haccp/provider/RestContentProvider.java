
package com.itdoors.haccp.provider;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.itdoors.haccp.rest.HttpMethod;
import com.itdoors.haccp.rest.TransactionState;
import com.itdoors.haccp.sync.accounts.GenericAccountService;
import com.itdoors.haccp.utils.CalendarUtils;
import com.itdoors.haccp.utils.Logger;

public class RestContentProvider extends ContentProvider {

    public static String TAG = "RestContentProvider";

    private static final int USER = 1000;

    private static final int COMPANIES = 100;
    private static final int COMPANIES_ID = 101;
    private static final int COMPANIES_ID_COMPANY_OBJECTS = 103;

    private static final int COMPANY_OBJECTS_ID = 200;

    private static final int SERVICES = 400;
    private static final int CONTOURS = 500;

    private static final int POINTS_ID = 600;
    private static final int POINTS_IN_COMPANY_OBJECT_BY_CONTOUR = 601;
    private static final int POINTS_IN_COMPANY_OBJECT_BY_CONTOUR_SEARCH = 602;
    private static final int POINT_ID_STATISTICS = 603;

    private static final int STATUSES = 700;

    private static final int GROUP_ID_CHARACTERISTICS = 800;

    private static final int TRANSACTIONS_ID = 900;
    private static final int TRANSACTIONS_PENDING = 901;
    private static final int TRANSACTIONS_PENDING_ID = 902;

    private static final int TRANSACTIONS_IN_PROGRESS = 903;
    private static final int TRANSACTIONS_IN_PROGRESS_ID = 904;

    public static final String CONTENT_AUTHORITY = "com.itdoors.haccp.restcontentprovider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"
            + CONTENT_AUTHORITY);

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private HaccpDatabase dbHelper;

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, "user", USER);
        matcher.addURI(authority, "companies", COMPANIES);
        matcher.addURI(authority, "companies/#", COMPANIES_ID);
        matcher.addURI(authority, "companies/#/company_objects", COMPANIES_ID_COMPANY_OBJECTS);

        matcher.addURI(authority, "company_objects/#", COMPANY_OBJECTS_ID);

        matcher.addURI(authority, "services", SERVICES);
        matcher.addURI(authority, "contours", CONTOURS);
        matcher.addURI(authority, "company_objects/#/contours/#/points",
                POINTS_IN_COMPANY_OBJECT_BY_CONTOUR);
        matcher.addURI(authority, "company_objects/#/contours/#/points/search/*",
                POINTS_IN_COMPANY_OBJECT_BY_CONTOUR_SEARCH);

        matcher.addURI(authority, "points/*", POINTS_ID);
        matcher.addURI(authority, "points/*/statististics", POINT_ID_STATISTICS);

        matcher.addURI(authority, "statuses", STATUSES);
        matcher.addURI(authority, "groups/#/characteristics", GROUP_ID_CHARACTERISTICS);

        matcher.addURI(authority, "transactions/#", TRANSACTIONS_ID);
        matcher.addURI(authority, "transactions/pending", TRANSACTIONS_PENDING);
        matcher.addURI(authority, "transactions/in-progress", TRANSACTIONS_IN_PROGRESS);

        matcher.addURI(authority, "transactions/pending/#", TRANSACTIONS_PENDING_ID);
        matcher.addURI(authority, "transactions/in-progress/#", TRANSACTIONS_IN_PROGRESS_ID);

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

            case USER:
                return HaccpContract.User.CONTENT_ITEM_TYPE;

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
            case POINTS_IN_COMPANY_OBJECT_BY_CONTOUR_SEARCH:
                return HaccpContract.Points.CONTENT_TYPE;
            case POINTS_ID:
                return HaccpContract.Points.CONTENT_ITEM_TYPE;

            case STATUSES:
                return HaccpContract.Statuses.CONTENT_TYPE;

            case GROUP_ID_CHARACTERISTICS:
                return HaccpContract.GroupCharacterisitcs.CONTENT_TYPE;

            case POINT_ID_STATISTICS:
                return HaccpContract.Statistics.CONTENT_TYPE;

            case TRANSACTIONS_ID:
            case TRANSACTIONS_PENDING_ID:
            case TRANSACTIONS_IN_PROGRESS_ID:
                return HaccpContract.Transactions.CONTENT_ITEM_TYPE;

            case TRANSACTIONS_PENDING:
            case TRANSACTIONS_IN_PROGRESS:
                return HaccpContract.Transactions.CONTENT_TYPE;

            default:
                throw new IllegalArgumentException("Unknows uri[" + uri + "]");
        }
    }

    private static HashMap<String, String> sContoursProjectionMap;

    private static HashMap<String, String> sPointsMap;
    private static HashMap<String, String> sPointsInCObjByContProjMap;
    private static HashMap<String, String> sPointInfoMap;
    private static HashMap<String, String> sPointStatisticsMap;

    static {

        {
            sContoursProjectionMap = new HashMap<String, String>();

            sContoursProjectionMap.put(HaccpContract.Contours._ID, HaccpDatabase.Tables.CONTOURS
                    + "." + HaccpContract.Contours._ID);
            sContoursProjectionMap.put(HaccpContract.Contours.UID, HaccpDatabase.Tables.CONTOURS
                    + "." + HaccpContract.Contours.UID);
            sContoursProjectionMap.put(HaccpContract.Contours.NAME, HaccpDatabase.Tables.CONTOURS
                    + "." + HaccpContract.Contours.NAME);
            sContoursProjectionMap.put(HaccpContract.Contours.SERVICE_ID,
                    HaccpDatabase.Tables.CONTOURS + "." + HaccpContract.Contours.SERVICE_ID);
            sContoursProjectionMap.put(HaccpContract.Contours.COLOR, HaccpDatabase.Tables.CONTOURS
                    + "." + HaccpContract.Contours.COLOR);

            sContoursProjectionMap.put(HaccpContract.Contours.SERVICE_ID_PROJECTION,
                    HaccpContract.Services._ID_FULL + " AS "
                            + HaccpContract.Contours.SERVICE_ID_PROJECTION);
            sContoursProjectionMap.put(HaccpContract.Contours.SERVICE_UID_PROJECTION,
                    HaccpContract.Services.UID_FULL + " AS "
                            + HaccpContract.Contours.SERVICE_UID_PROJECTION);
            sContoursProjectionMap.put(HaccpContract.Contours.SERVICE_NAME_PROJECTION,
                    HaccpContract.Services.NAME_FULL + " AS "
                            + HaccpContract.Contours.SERVICE_NAME_PROJECTION);
        }

        {
            sPointsMap = new HashMap<String, String>();

            sPointsMap.put(HaccpContract.Points._ID, HaccpDatabase.Tables.POINTS + "."
                    + HaccpContract.Points._ID);
            sPointsMap.put(HaccpContract.Points.UID, HaccpDatabase.Tables.POINTS + "."
                    + HaccpContract.Points.UID);
            sPointsMap.put(HaccpContract.Points.NAME, HaccpDatabase.Tables.POINTS + "."
                    + HaccpContract.Points.NAME);
            sPointsMap.put(HaccpContract.Points.PLAN_ID, HaccpDatabase.Tables.POINTS + "."
                    + HaccpContract.Points.PLAN_ID);
            sPointsMap.put(HaccpContract.Points.CONTOUR_ID, HaccpDatabase.Tables.POINTS + "."
                    + HaccpContract.Points.CONTOUR_ID);
            sPointsMap.put(HaccpContract.Points.STATUS_ID, HaccpDatabase.Tables.POINTS + "."
                    + HaccpContract.Points.STATUS_ID);
            sPointsMap.put(HaccpContract.Points.POINT_GROUP_ID, HaccpDatabase.Tables.POINTS + "."
                    + HaccpContract.Points.POINT_GROUP_ID);
            sPointsMap.put(HaccpContract.Points.INSTALATION_DATE, HaccpDatabase.Tables.POINTS + "."
                    + HaccpContract.Points.INSTALATION_DATE);
            sPointsMap.put(HaccpContract.Points.IMG_LATITUDE, HaccpDatabase.Tables.POINTS + "."
                    + HaccpContract.Points.IMG_LATITUDE);
            sPointsMap.put(HaccpContract.Points.IMG_LONGTITUDE, HaccpDatabase.Tables.POINTS + "."
                    + HaccpContract.Points.IMG_LONGTITUDE);
            sPointsMap.put(HaccpContract.Points.MAP_LATITUDE, HaccpDatabase.Tables.POINTS + "."
                    + HaccpContract.Points.MAP_LATITUDE);
            sPointsMap.put(HaccpContract.Points.MAP_LONGTITUDE, HaccpDatabase.Tables.POINTS + "."
                    + HaccpContract.Points.MAP_LONGTITUDE);
        }

        {
            sPointsInCObjByContProjMap = new HashMap<String, String>(sPointsMap);

            sPointsInCObjByContProjMap.put(HaccpContract.Points.PLANS_ID_PROJECTION,
                    HaccpContract.Plans._ID_FULL + " AS "
                            + HaccpContract.Points.PLANS_ID_PROJECTION);
            sPointsInCObjByContProjMap.put(HaccpContract.Points.PLANS_UID_PROJECTION,
                    HaccpContract.Plans.UID_FULL + " AS "
                            + HaccpContract.Points.PLANS_UID_PROJECTION);
            sPointsInCObjByContProjMap.put(HaccpContract.Points.PLANS_NAME_PROJECTION,
                    HaccpContract.Plans.NAME_FULL + " AS "
                            + HaccpContract.Points.PLANS_NAME_PROJECTION);
        }

        {
            sPointInfoMap = new HashMap<String, String>(sPointsMap);

            sPointInfoMap.put(HaccpContract.Points.PLANS_ID_PROJECTION,
                    HaccpContract.Plans._ID_FULL + " AS "
                            + HaccpContract.Points.PLANS_ID_PROJECTION);
            sPointInfoMap.put(HaccpContract.Points.PLANS_UID_PROJECTION,
                    HaccpContract.Plans.UID_FULL + " AS "
                            + HaccpContract.Points.PLANS_UID_PROJECTION);
            sPointInfoMap.put(HaccpContract.Points.PLANS_NAME_PROJECTION,
                    HaccpContract.Plans.NAME_FULL + " AS "
                            + HaccpContract.Points.PLANS_NAME_PROJECTION);

            sPointInfoMap.put(HaccpContract.Points.CONTOUR_ID_PROJECTION,
                    HaccpContract.Contours._ID_FULL + " AS "
                            + HaccpContract.Points.CONTOUR_ID_PROJECTION);
            sPointInfoMap.put(HaccpContract.Points.CONTOUR_UID_PROJECTION,
                    HaccpContract.Contours.UID_FULL + " AS "
                            + HaccpContract.Points.CONTOUR_UID_PROJECTION);
            sPointInfoMap.put(HaccpContract.Points.CONTOUR_NAME_PROJECTION,
                    HaccpContract.Contours.NAME_FULL + " AS "
                            + HaccpContract.Points.CONTOUR_NAME_PROJECTION);
            sPointInfoMap.put(HaccpContract.Points.CONTOUR_SLUG_PROJECTION,
                    HaccpContract.Contours.SLUG_FULL + " AS "
                            + HaccpContract.Points.CONTOUR_SLUG_PROJECTION);

            sPointInfoMap.put(HaccpContract.Points.STATUS_ID_PROJECTION,
                    HaccpContract.Statuses._ID_FULL + " AS "
                            + HaccpContract.Points.STATUS_ID_PROJECTION);
            sPointInfoMap.put(HaccpContract.Points.STATUS_UID_PROJECTION,
                    HaccpContract.Statuses.UID_FULL + " AS "
                            + HaccpContract.Points.STATUS_UID_PROJECTION);
            sPointInfoMap.put(HaccpContract.Points.STATUS_NAME_PROJECTION,
                    HaccpContract.Statuses.NAME_FULL + " AS "
                            + HaccpContract.Points.STATUS_NAME_PROJECTION);
            sPointInfoMap.put(HaccpContract.Points.STATUS_SLUG_PROJECTION,
                    HaccpContract.Statuses.SLUG_FULL + " AS "
                            + HaccpContract.Points.STATUS_SLUG_PROJECTION);

            sPointInfoMap.put(HaccpContract.Points.GROUP_ID_PROJECTION,
                    HaccpContract.Groups._ID_FULL + " AS "
                            + HaccpContract.Points.GROUP_ID_PROJECTION);
            sPointInfoMap.put(HaccpContract.Points.GROUP_UID_PROJECTION,
                    HaccpContract.Groups.UID_FULL + " AS "
                            + HaccpContract.Points.GROUP_UID_PROJECTION);
            sPointInfoMap.put(HaccpContract.Points.GROUP_NAME_PROJECTION,
                    HaccpContract.Groups.NAME_FULL + " AS "
                            + HaccpContract.Points.GROUP_NAME_PROJECTION);

        }

        {

            sPointStatisticsMap = new HashMap<String, String>();

            sPointStatisticsMap.put(HaccpContract.Statistics._ID,
                    HaccpDatabase.Tables.POINT_STATISTICS + "." + HaccpContract.Statistics._ID);
            sPointStatisticsMap.put(HaccpContract.Statistics.CHARACTERISTICS_ID,
                    HaccpDatabase.Tables.POINT_STATISTICS + "."
                            + HaccpContract.Statistics.CHARACTERISTICS_ID);
            sPointStatisticsMap
                    .put(HaccpContract.Statistics.POINT_ID, HaccpDatabase.Tables.POINT_STATISTICS
                            + "." + HaccpContract.Statistics.POINT_ID);
            sPointStatisticsMap.put(HaccpContract.Statistics.CREATED_AT,
                    HaccpDatabase.Tables.POINT_STATISTICS + "."
                            + HaccpContract.Statistics.CREATED_AT);
            sPointStatisticsMap.put(HaccpContract.Statistics.ENTRY_DATE,
                    HaccpDatabase.Tables.POINT_STATISTICS + "."
                            + HaccpContract.Statistics.ENTRY_DATE);
            sPointStatisticsMap.put(HaccpContract.Statistics.VALUE,
                    HaccpDatabase.Tables.POINT_STATISTICS + "." + HaccpContract.Statistics.VALUE);
            sPointStatisticsMap.put(HaccpContract.Statistics.UID,
                    HaccpDatabase.Tables.POINT_STATISTICS + "." + HaccpContract.Statistics.UID);

            sPointStatisticsMap.put(HaccpContract.GroupCharacterisitcs.NAME,
                    HaccpDatabase.Tables.POINT_GROUP_CHARACTERISTICS + "."
                            + HaccpContract.GroupCharacterisitcs.NAME);
            sPointStatisticsMap.put(HaccpContract.GroupCharacterisitcs.UNIT,
                    HaccpDatabase.Tables.POINT_GROUP_CHARACTERISTICS + "."
                            + HaccpContract.GroupCharacterisitcs.UNIT);
            sPointStatisticsMap.put(HaccpContract.GroupCharacterisitcs.CRITICAL_VALUE_BOTTOM,
                    HaccpDatabase.Tables.POINT_GROUP_CHARACTERISTICS + "."
                            + HaccpContract.GroupCharacterisitcs.CRITICAL_VALUE_BOTTOM);
            sPointStatisticsMap.put(HaccpContract.GroupCharacterisitcs.CRITICAL_VALUE_TOP,
                    HaccpDatabase.Tables.POINT_GROUP_CHARACTERISTICS + "."
                            + HaccpContract.GroupCharacterisitcs.CRITICAL_VALUE_TOP);

            sPointStatisticsMap.put(HaccpContract.Statistics.GROUP_CHARACTERISTICS_ID_PROJECTION,
                    HaccpContract.GroupCharacterisitcs._ID_FULL + " AS "
                            + HaccpContract.Statistics.GROUP_CHARACTERISTICS_ID_PROJECTION);
            sPointStatisticsMap.put(HaccpContract.Statistics.GROUP_CHARACTERISTICS_UID_PROJECTION,
                    HaccpContract.GroupCharacterisitcs.UID_FULL + " AS "
                            + HaccpContract.Statistics.GROUP_CHARACTERISTICS_UID_PROJECTION);

        }

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {

            case USER: {

                qBuilder.setTables(HaccpDatabase.Tables.USER);

                break;
            }

            case SERVICES: {
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = HaccpContract.Services.DEFAULT_SORT;
                qBuilder.setTables(HaccpDatabase.Tables.SERVICES);

                break;
            }
            case COMPANIES: {
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = HaccpContract.Companies.DEFAULT_SORT;

                qBuilder.setTables(HaccpDatabase.Tables.COMPANIES);

                break;
            }
            case CONTOURS: {
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = HaccpContract.Contours.DEFAULT_SORT;

                qBuilder.setTables(
                        HaccpDatabase.Tables.CONTOURS + " INNER JOIN "
                                + HaccpDatabase.Tables.SERVICES +
                                " ON " + "(" + HaccpDatabase.Tables.CONTOURS + "."
                                + HaccpContract.Contours.SERVICE_ID + " = " +
                                HaccpDatabase.Tables.SERVICES + "." + HaccpContract.Services.UID +
                                ")"
                        );
                qBuilder.setProjectionMap(sContoursProjectionMap);

                break;
            }

            case COMPANIES_ID_COMPANY_OBJECTS: {
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = HaccpContract.CompanyObjects.DEFAULT_SORT;

                String companyId = HaccpContract.CompanyObjects.getCompanyId(uri);
                qBuilder.setTables(HaccpDatabase.Tables.COMPANY_OBJECTS);
                qBuilder.appendWhere(HaccpContract.CompanyObjects.COMPANY_ID);
                qBuilder.appendWhere("=");
                qBuilder.appendWhereEscapeString(companyId);

                break;
            }

            case POINTS_IN_COMPANY_OBJECT_BY_CONTOUR: {

                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = HaccpDatabase.Tables.PLANS + "." + HaccpContract.Plans.UID + ","
                            + HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.NAME;

                qBuilder.setTables(
                        HaccpDatabase.Tables.PLANS + " INNER JOIN " + HaccpDatabase.Tables.POINTS +
                                " ON " + "(" + HaccpDatabase.Tables.PLANS + "."
                                + HaccpContract.Plans.UID + " = " +
                                HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.PLAN_ID +
                                ")"
                        );

                qBuilder.appendWhere(HaccpDatabase.Tables.POINTS + "."
                        + HaccpContract.Points.CONTOUR_ID + "="
                        + HaccpContract.Points.getContourId(uri));
                qBuilder.appendWhere(" AND ");
                qBuilder.appendWhere(HaccpDatabase.Tables.PLANS + "."
                        + HaccpContract.Plans.CONPANY_OBJECT_ID + "="
                        + HaccpContract.Points.getCompanyObjectId(uri));

                qBuilder.setProjectionMap(sPointsInCObjByContProjMap);

                break;
            }

            case POINTS_IN_COMPANY_OBJECT_BY_CONTOUR_SEARCH: {

                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = HaccpDatabase.Tables.PLANS + "." + HaccpContract.Plans.UID + ","
                            + HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.NAME;

                qBuilder.setTables(
                        HaccpDatabase.Tables.PLANS + " INNER JOIN " + HaccpDatabase.Tables.POINTS +
                                " ON " + "(" + HaccpDatabase.Tables.PLANS + "."
                                + HaccpContract.Plans.UID + " = " +
                                HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.PLAN_ID +
                                ")"
                        );

                qBuilder.appendWhere(HaccpDatabase.Tables.POINTS + "."
                        + HaccpContract.Points.CONTOUR_ID + "="
                        + HaccpContract.Points.getContourId(uri));
                qBuilder.appendWhere(" AND ");
                qBuilder.appendWhere(HaccpDatabase.Tables.PLANS + "."
                        + HaccpContract.Plans.CONPANY_OBJECT_ID + "="
                        + HaccpContract.Points.getCompanyObjectId(uri));
                qBuilder.appendWhere(" AND ");
                qBuilder.appendWhere(HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.NAME
                        + " LIKE '%" + HaccpContract.Points.getSearchStatement(uri) + "%'");

                qBuilder.setProjectionMap(sPointsInCObjByContProjMap);

                break;
            }

            case POINTS_ID: {
                qBuilder.setTables(
                        HaccpDatabase.Tables.POINTS +

                                " INNER JOIN " + HaccpDatabase.Tables.POINT_STATUSES +
                                " ON " + "(" + HaccpDatabase.Tables.POINTS + "."
                                + HaccpContract.Points.STATUS_ID + " = " +
                                HaccpDatabase.Tables.POINT_STATUSES + "."
                                + HaccpContract.Points.UID +
                                ")" +
                                " INNER JOIN " + HaccpDatabase.Tables.POINT_GROUPS +
                                " ON " + "(" + HaccpDatabase.Tables.POINTS + "."
                                + HaccpContract.Points.POINT_GROUP_ID + " = " +
                                HaccpDatabase.Tables.POINT_GROUPS + "." + HaccpContract.Groups.UID +
                                ")" +
                                " INNER JOIN " + HaccpDatabase.Tables.CONTOURS +
                                " ON " + "(" + HaccpDatabase.Tables.POINTS + "."
                                + HaccpContract.Points.CONTOUR_ID + " = " +
                                HaccpDatabase.Tables.CONTOURS + "." + HaccpContract.Contours.UID +
                                ")" +
                                " INNER JOIN " + HaccpDatabase.Tables.PLANS +
                                " ON " + "(" + HaccpDatabase.Tables.POINTS + "."
                                + HaccpContract.Points.PLAN_ID + " = " +
                                HaccpDatabase.Tables.PLANS + "." + HaccpContract.Plans.UID +
                                ")"
                        );

                qBuilder.appendWhere(HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.UID
                        + "=" + "'" + HaccpContract.Points.getPointId(uri) + "'");

                Logger.Logd(getClass(), "qBuilder:" + qBuilder.toString());

                qBuilder.setProjectionMap(sPointInfoMap);

                break;

            }
            case POINT_ID_STATISTICS: {

                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = HaccpDatabase.Tables.POINT_STATISTICS + "."
                            + HaccpContract.Statistics.ENTRY_DATE + " DESC";

                qBuilder.setTables(
                        HaccpDatabase.Tables.POINT_STATISTICS +

                                " INNER JOIN " + HaccpDatabase.Tables.POINT_GROUP_CHARACTERISTICS +
                                " ON " + "(" + HaccpDatabase.Tables.POINT_STATISTICS + "."
                                + HaccpContract.Statistics.CHARACTERISTICS_ID + " = " +
                                HaccpDatabase.Tables.POINT_GROUP_CHARACTERISTICS + "."
                                + HaccpContract.GroupCharacterisitcs.UID +
                                ")"
                        );

                qBuilder.appendWhere(HaccpDatabase.Tables.POINT_STATISTICS + "."
                        + HaccpContract.Statistics.POINT_ID + "="
                        + "'" + HaccpContract.Statistics.getPointId(uri) + "'");
                qBuilder.setProjectionMap(sPointStatisticsMap);
                break;

            }

            case STATUSES: {
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = HaccpContract.Statuses.DEFAULT_SORT;
                qBuilder.setTables(HaccpDatabase.Tables.POINT_STATUSES);

                break;
            }
            case GROUP_ID_CHARACTERISTICS: {
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = HaccpContract.GroupCharacterisitcs.DEFAULT_SORT;
                qBuilder.setTables(HaccpDatabase.Tables.POINT_GROUP_CHARACTERISTICS);
                qBuilder.appendWhere(HaccpDatabase.Tables.POINT_GROUP_CHARACTERISTICS + "."
                        + HaccpContract.GroupCharacterisitcs.UID + "="
                        + HaccpContract.GroupCharacterisitcs.getGroupId(uri));

                break;
            }
            case TRANSACTIONS_PENDING: {
                sortOrder = HaccpContract.Transactions.TRANSACTING_DATE + " ASC";

                qBuilder.setTables(HaccpDatabase.Tables.TRANSACTIONS);
                qBuilder.appendWhere(HaccpDatabase.Tables.TRANSACTIONS + "."
                        + HaccpContract.Transactions.TRANSACTING);
                qBuilder.appendWhere(" IN ( ");
                qBuilder.appendWhere(String.valueOf(TransactionState.TRANSACTION_PENDING));
                qBuilder.appendWhere(",");
                qBuilder.appendWhere(String.valueOf(TransactionState.TRANSACTION_RETRY));
                qBuilder.appendWhere(" )");

                break;
            }
            case TRANSACTIONS_ID: {
                qBuilder.setTables(HaccpDatabase.Tables.TRANSACTIONS);
                qBuilder.appendWhere(HaccpDatabase.Tables.TRANSACTIONS + "."
                        + HaccpContract.Transactions._ID);
                qBuilder.appendWhere("=");
                qBuilder.appendWhere(HaccpContract.Transactions.getTransactionId(uri));

                break;

            }
            case COMPANIES_ID:
                break;
            case COMPANY_OBJECTS_ID:
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);

        }

        Cursor cursor = qBuilder.query(db, projection, selection, selectionArgs, null, null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {

            case USER: {
                try {
                    db.insertOrThrow(HaccpDatabase.Tables.USER, null, values);
                } catch (SQLException e) {
                    Logger.Loge(TAG, "Cannot insert statistics with uri:" + uri.toString());
                    return null;
                }
                return uri;
            }

            case POINT_ID_STATISTICS: {
                int uid = values.getAsInteger(HaccpContract.Statistics.UID);

                Uri newUri;
                try {
                    db.insertOrThrow(HaccpDatabase.Tables.POINT_STATISTICS, null, values);
                } catch (SQLException e) {
                    Logger.Loge(TAG, "Cannot insert statistics with uri:" + uri.toString());
                    return null;
                }
                Logger.Logi(getClass(), "insert statistics with uri: " + uri.toString() + ", "
                        + "values: " + values.toString());
                newUri = ContentUris.withAppendedId(uri, uid);
                getContext().getContentResolver().notifyChange(newUri, null);

                return newUri;
            }
            case TRANSACTIONS_PENDING: {

                long requestId;
                Uri newUri;

                values.put(HaccpContract.Transactions.METHOD, HttpMethod.POST_STR);
                values.put(HaccpContract.Transactions.TRANSACTING,
                        TransactionState.TRANSACTION_PENDING);
                values.put(HaccpContract.Transactions.RESULT, 0);
                values.put(HaccpContract.Transactions.TRANSACTING_DATE,
                        CalendarUtils.toTimeStamp(CalendarUtils.currentDate()));
                values.put(HaccpContract.Transactions.TRY_COUNT, 0);

                // Insert into database
                try {
                    Logger.Logi(getClass(), "start insert pending transaction  with values:"
                            + values.toString());
                    requestId = db.insertOrThrow(HaccpDatabase.Tables.TRANSACTIONS, null, values);
                } catch (SQLException e) {
                    Logger.Loge(
                            TAG,
                            "Cannot insert transaction in PENDING status with uri:"
                                    + uri.toString());
                    return null;
                }

                Logger.Logi(getClass(), "request Sync for uri: " + HaccpContract.CONTENT_AUTHORITY);
                ContentResolver.requestSync(
                        GenericAccountService.GetAccount(), HaccpContract.CONTENT_AUTHORITY,
                        new Bundle());

                // Notify any watchers of the change
                newUri = HaccpContract.Transactions.buildPendingUriForId(requestId);

                Logger.Logi(getClass(), "notify change for uri: " + newUri.toString());
                getContext().getContentResolver().notifyChange(newUri, null);

                return newUri;
            }
            default:
                throw new UnsupportedOperationException("insert with uri:" + uri);
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        if (uri.equals(BASE_CONTENT_URI)) {
            deleteDatabase();
            return 1;
        }

        StringBuilder whereBuilder = new StringBuilder();
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case TRANSACTIONS_ID: {
                whereBuilder.append(HaccpDatabase.Tables.TRANSACTIONS + "."
                        + HaccpContract.Transactions._ID);
                whereBuilder.append("=");
                whereBuilder.append(HaccpContract.Transactions.getTransactionId(uri));

                Logger.Logi(getClass(), "removing transaction with uri :" + uri.toString() + ", "
                        + "where : " + whereBuilder.toString());
                int count = db.delete(HaccpDatabase.Tables.TRANSACTIONS, whereBuilder.toString(),
                        null);
                return count;
            }

            default:
                throw new UnsupportedOperationException("delete with uri:" + uri);
        }

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {

        StringBuilder whereSelections = new StringBuilder();
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case TRANSACTIONS_ID: {
                String transactionId = HaccpContract.Transactions.getTransactionId(uri);
                whereSelections
                        .append(HaccpDatabase.Tables.TRANSACTIONS + "."
                                + HaccpContract.Transactions._ID)
                        .append("=")
                        .append(transactionId);
                if (!TextUtils.isEmpty(selection)) {
                    whereSelections.append(" AND ( ").append(selection).append(")");
                }

                Logger.Logi(getClass(), "update transaction with uri: " + uri.toString() + ", "
                        + "values: " + values.toString());

                int count = db.update(HaccpDatabase.Tables.TRANSACTIONS, values,
                        whereSelections.toString(), null);

                Logger.Logi(getClass(), "notifyChange uri: " + uri.toString());
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }
            case TRANSACTIONS_IN_PROGRESS_ID: {
                values.put(HaccpContract.Transactions.TRANSACTING,
                        TransactionState.TRANSACTION_IN_PROGRESS);

                String transactionsId = HaccpContract.Transactions.getInProgressTransactionsId(uri);
                whereSelections
                        .append(HaccpDatabase.Tables.TRANSACTIONS + "."
                                + HaccpContract.Transactions._ID)
                        .append("=")
                        .append(transactionsId);
                if (!TextUtils.isEmpty(selection)) {
                    whereSelections.append(" AND ( ").append(selection).append(")");
                }

                Logger.Logi(getClass(),
                        "update. uri: " + uri.toString() + ", values: " + values.toString());
                int count = db.update(HaccpDatabase.Tables.TRANSACTIONS, values,
                        whereSelections.toString(), selectionArgs);

                Logger.Logi(getClass(), "notifyChange: " + uri.toString());
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }

            case POINTS_ID: {
                String pointId = "'" + HaccpContract.Points.getPointId(uri) + "'";
                whereSelections
                        .append(HaccpDatabase.Tables.POINTS + "." + HaccpContract.Points.UID)
                        .append("=")
                        .append(pointId);
                if (!TextUtils.isEmpty(selection)) {
                    whereSelections.append(" AND ( ").append(selection).append(")");
                }

                Logger.Logi(getClass(), "update point. uri: " + uri.toString() + ", values: "
                        + values.toString());
                int count = db.update(HaccpDatabase.Tables.POINTS, values,
                        whereSelections.toString(), selectionArgs);

                Logger.Logi(getClass(), "notifyChange: " + uri.toString());
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            }
            default:
                throw new UnsupportedOperationException("update with uri:" + uri);
        }
    }

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    private void deleteDatabase() {
        dbHelper.close();
        Context context = getContext();
        HaccpDatabase.deleteDatabase(context);
        dbHelper = new HaccpDatabase(getContext());
    }

}
