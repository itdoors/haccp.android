
package com.itdoors.haccp.provider;

import android.net.Uri;

public final class HaccpContract {

    public static final String CONTENT_AUTHORITY = "com.itdoors.haccp.restcontentprovider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_SEARCH = "search";

    interface BaseColumns extends android.provider.BaseColumns {
        String UID = "uid";
    }

    public static interface UserColumns {
        public static final String NAME = "username";
        public static final String EMAIL = "email";

    }

    public static interface CompaniesColumns {
        public static final String NAME = "name";
    }

    public static interface CompanyObjectsColums {
        public static final String COMPANY_ID = "company_id";
        public static final String NAME = "name";
    }

    public static interface ServicesColumns {
        public static final String NAME = "name";

        public static final String NAME_FULL = HaccpDatabase.Tables.SERVICES + "." + NAME;
        public static final String UID_FULL = HaccpDatabase.Tables.SERVICES + "." + BaseColumns.UID;
        public static final String _ID_FULL = HaccpDatabase.Tables.SERVICES + "." + BaseColumns._ID;

    }

    public static interface ContoursColumns {

        public static final String NAME = "name";
        public static final String COLOR = "color";
        public static final String SERVICE_ID = "service_id";
        public static final String SLUG = "slug";
        public static final String LEVEL = "level";

        public static final String NAME_FULL = HaccpDatabase.Tables.CONTOURS + "." + NAME;
        public static final String UID_FULL = HaccpDatabase.Tables.CONTOURS + "." + BaseColumns.UID;
        public static final String _ID_FULL = HaccpDatabase.Tables.CONTOURS + "." + BaseColumns._ID;
        public static final String SLUG_FULL = HaccpDatabase.Tables.CONTOURS + "." + SLUG;

    }

    public static interface PlansColumns {
        public static final String NAME = "name";
        public static final String CONPANY_OBJECT_ID = "company_object_id";
        public static final String IMG_SRC = "img_src";
        public static final String PARENT_ID = "parent_id";
        public static final String IMG_WIDHT = "image_width";
        public static final String IMG_HEIGHT = "image_height";
        public static final String LATITUDE = "latitude";
        public static final String LONGTITUDE = "longitude";
        public static final String TYPE = "type";

        public static final String NAME_FULL = HaccpDatabase.Tables.PLANS + "." + NAME;
        public static final String UID_FULL = HaccpDatabase.Tables.PLANS + "." + BaseColumns.UID;
        public static final String _ID_FULL = HaccpDatabase.Tables.PLANS + "." + BaseColumns._ID;

    }

    public static interface PoisonColumns {

        public static final String NAME = "name";
        public static final String ACTIVE_SUBSTANCE = "active_substance";
        public static final String QUANTITY = "quantity";
        public static final String STANDART_AMOUNT = "standard_amount";

        public static final String NAME_FULL = HaccpDatabase.Tables.POISONS + "." + NAME;
        public static final String UID_FULL = HaccpDatabase.Tables.POISONS + "." + BaseColumns.UID;
        public static final String _ID_FULL = HaccpDatabase.Tables.POISONS + "." + BaseColumns._ID;

    }

    public static interface PointPoisonColumns {

        public static final String POISON_ID = "poison_id";
        public static final String POINT_ID = "point_id";

    }

    public static interface StatusesColumns {

        public static final String NAME = "name";
        public static final String SLUG = "slug";

        public static final String NAME_FULL = HaccpDatabase.Tables.POINT_STATUSES + "." + NAME;
        public static final String UID_FULL = HaccpDatabase.Tables.POINT_STATUSES + "."
                + BaseColumns.UID;
        public static final String _ID_FULL = HaccpDatabase.Tables.POINT_STATUSES + "."
                + BaseColumns._ID;

        public static final String SLUG_FULL = HaccpDatabase.Tables.POINT_STATUSES + "." + SLUG;

    }

    public static interface GroupsColumns {

        public static final String NAME = "name";

        public static final String NAME_FULL = HaccpDatabase.Tables.POINT_GROUPS + "." + NAME;
        public static final String UID_FULL = HaccpDatabase.Tables.POINT_GROUPS + "."
                + BaseColumns.UID;
        public static final String _ID_FULL = HaccpDatabase.Tables.POINT_GROUPS + "."
                + BaseColumns._ID;

    }

    public static interface GroupCharacteristicsColumns {

        public static final String NAME = "name";

        public static final String POINT_GROUP_ID = "point_group_id";
        public static final String DESCRIPTION = "description";
        public static final String UNIT = "unit";
        public static final String DATA_TYPE = "data_type";
        public static final String ALLOW_VALUE_MAX = "allow_value_max";
        public static final String ALLOW_VALUE_MIN = "allow_value_min";
        public static final String CRITICAL_VALUE_TOP = "critical_value_top";
        public static final String CRITICAL_VALUE_BOTTOM = "critical_value_bottom";
        public static final String CRITICAL_VALUE_MIDDLE = "critical_color_middle";
        public static final String INPUT_TYPE = "input_type";

        public static final String NAME_FULL = HaccpDatabase.Tables.POINT_GROUP_CHARACTERISTICS
                + "." + NAME;
        public static final String UID_FULL = HaccpDatabase.Tables.POINT_GROUP_CHARACTERISTICS
                + "." + BaseColumns.UID;
        public static final String _ID_FULL = HaccpDatabase.Tables.POINT_GROUP_CHARACTERISTICS
                + "." + BaseColumns._ID;

    }

    public static interface PointsColumns {
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

    public static interface StatisticsColumns {

        public static final String CHARACTERISTICS_ID = "characteristic_id";
        public static final String POINT_ID = "point_id";
        public static final String CREATED_AT = "created_at";
        public static final String ENTRY_DATE = "entry_date";
        public static final String VALUE = "value";

    }

    public static interface TransactionsColumns {

        public static final String ACTION_TYPE = "action_type";
        public static final String URI = "uri";
        public static final String PARAMS = "params";
        public static final String METHOD = "method";
        public static final String TRANSACTING = "transacting";
        public static final String RESULT = "result";
        public static final String TRANSACTING_DATE = "transacting_date";
        public static final String TRY_COUNT = "try_count";

    }

    public static class User implements UserColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("user").build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.user";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.user";

    }

    public static class Services implements ServicesColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("services").build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.services";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.sercices";

        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";

    }

    public static class Contours implements ContoursColumns, BaseColumns {

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

    public static class Companies implements CompaniesColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("companies").build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.companies";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.companies";

        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";
        public static final String SORT_BY_NAME = CompaniesColumns.NAME + " ASC";

    }

    public static class CompanyObjects implements CompanyObjectsColums, BaseColumns {

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.company_objects";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.company_objects";

        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";
        public static final String SORT_BY_NAME = CompanyObjectsColums.NAME + " ASC";

        public static Uri buildUriForCompanyId(int companyId) {
            return BASE_CONTENT_URI
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

    public static class Statuses implements StatusesColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath("statuses").build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.statuses";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.statuses";

        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";

    }

    public static class Groups implements GroupsColumns, BaseColumns {

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.groups";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.groups";

        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";

    }

    public static class GroupCharacterisitcs implements GroupCharacteristicsColumns, BaseColumns {

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.groupcharacterisitics";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.groupcharacterisitics";
        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";

        public static final String GROUP_ID_PROJECTION = "group_id";
        public static final String GROUP_NAME_PROJECTION = "group_name";
        public static final String GROUP_UID_PROJECTION = "group_uid";

        public static Uri buildUriForGroup(int groupId) {
            return BASE_CONTENT_URI
                    .buildUpon()
                    .appendPath("groups")
                    .appendPath(String.valueOf(groupId))
                    .appendPath("characteristics")
                    .build();
        }

        public static String getGroupId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static class Plans implements PlansColumns, BaseColumns {

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.plans";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.plans";

        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";
    }

    public static class Poisons implements PoisonColumns, BaseColumns {

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.poisons";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.poisons";

        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";

    }

    public static class PointPoison implements PointPoisonColumns {

    }

    public static class Points implements PointsColumns, BaseColumns {

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.points";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.points";

        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";
        public static final String SORT_BY_NAME = PointsColumns.NAME + " ASC";

        public static final String PLANS_ID_PROJECTION = "plan_id";
        public static final String PLANS_NAME_PROJECTION = "plan_name";
        public static final String PLANS_UID_PROJECTION = "plan_uid";

        public static final String CONTOUR_ID_PROJECTION = "contour_id";
        public static final String CONTOUR_NAME_PROJECTION = "contour_name";
        public static final String CONTOUR_UID_PROJECTION = "contour_uid";
        public static final String CONTOUR_SLUG_PROJECTION = "contour_slug";

        public static final String STATUS_ID_PROJECTION = "status_id";
        public static final String STATUS_NAME_PROJECTION = "status_name";
        public static final String STATUS_UID_PROJECTION = "status_uid";
        public static final String STATUS_SLUG_PROJECTION = "status_slug";

        public static final String GROUP_ID_PROJECTION = "group_id";
        public static final String GROUP_NAME_PROJECTION = "group_name";
        public static final String GROUP_UID_PROJECTION = "group_uid";

        public static final String POISON_UID_PROJECTION = "poison_uid";
        public static final String POISON_NAME_PROJECTION = "poison_name";
        public static final String POISON_ACTIVE_SUBSTANCE_PROJECTION = "poison_active_substance";

        public static Uri builduriForCompanyObjectInContour(int companyObjectId, int contourId) {
            return BASE_CONTENT_URI
                    .buildUpon()
                    .appendPath("company_objects")
                    .appendPath(String.valueOf(companyObjectId))
                    .appendPath("contours")
                    .appendPath(String.valueOf(contourId))
                    .appendPath("points")
                    .build();
        }

        public static String getCompanyObjectId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getContourId(Uri uri) {
            return uri.getPathSegments().get(3);
        }

        public static String getSearchStatement(Uri uri) {
            return uri.getPathSegments().get(6);
        }

        public static Uri buildSearchUri(int companyObjectId, int contourId, String query) {
            return builduriForCompanyObjectInContour(companyObjectId, contourId)

                    .buildUpon()
                    .appendPath(PATH_SEARCH)
                    .appendPath(query).build();

        }

        public static Uri buildPointUri(String point_id) {
            return BASE_CONTENT_URI
                    .buildUpon()
                    .appendPath("points")
                    .appendPath(point_id)
                    .build();
        }

        public static String getPointId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static class Statistics implements StatisticsColumns, BaseColumns {

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.statistics";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.statistics";
        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns.UID + " ASC";

        public static final String GROUP_CHARACTERISTICS_ID_PROJECTION = "group_char_id";
        public static final String GROUP_CHARACTERISTICS_UID_PROJECTION = "group_char_uid";

        public static Uri buildUriForPoint(String pointID) {
            return BASE_CONTENT_URI
                    .buildUpon()
                    .appendPath("points")
                    .appendPath(String.valueOf(pointID))
                    .appendPath("statististics")
                    .build();
        }

        public static String getPointId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static class Transactions implements TransactionsColumns, android.provider.BaseColumns {

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.itdoors.haccp.transactions";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.itdoors.haccp.transactions";
        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = _ID + " ASC";

        public static final Uri PENDING_TRANSACTIONS_URI = buildPendingUri();
        public static final Uri IN_PROGRESS_TRANSACTIONS_URI = buildInProgressUri();

        private static Uri buildPendingUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath("transactions").appendPath("pending")
                    .build();
        }

        private static Uri buildInProgressUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath("transactions")
                    .appendPath("in-progress").build();
        }

        public static Uri buildPendingUriForId(long id) {
            return Uri.withAppendedPath(PENDING_TRANSACTIONS_URI, Long.toString(id));
        }

        public static Uri buildInProgressUriForId(long id) {
            return Uri.withAppendedPath(IN_PROGRESS_TRANSACTIONS_URI, Long.toString(id));
        }

        public static Uri buildUriForId(long transactionId) {
            return BASE_CONTENT_URI.buildUpon().appendPath("transactions")
                    .appendPath(Long.toString(transactionId)).build();
        }

        public static String getTransactionId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getInProgressTransactionsId(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }
}
