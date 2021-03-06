
package com.itdoors.haccp.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.zip.GZIPInputStream;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public final class StreamParser {

    private StreamParser() {
    }

    private static void readCompany(JsonParser parser, SQLiteDatabase db)
            throws JsonParseException, SQLException, IOException {

        String id = null;
        String name = null;

        parser.nextToken();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            if ("id".equals(fieldName)) {
                id = parser.getText();
            }
            else if ("name".equals(fieldName)) {
                name = parser.getText();
            }
        }

        ContentValues values = new ContentValues();
        values.put("uid", id);
        values.put("name", name);

        db.insertOrThrow("companies", null, values);

    }

    private static void readCompanyObject(JsonParser parser, SQLiteDatabase db)
            throws JsonParseException, SQLException, IOException {

        String id = null;
        String companyId = null;
        String name = null;

        parser.nextToken();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            if ("id".equals(fieldName)) {
                id = parser.getText();
            }
            else if ("company_id".equals(fieldName)) {
                companyId = parser.getText();
            }
            else if ("name".equals(fieldName)) {
                name = parser.getText();
            }
        }

        ContentValues values = new ContentValues();
        values.put("uid", id);
        values.put("name", name);
        values.put("company_id", companyId);

        db.insertOrThrow("company_objects", null, values);

    }

    private static void readService(JsonParser parser, SQLiteDatabase db)
            throws JsonParseException, SQLException, IOException {

        String id = null;
        String name = null;

        parser.nextToken();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            if ("id".equals(fieldName)) {
                id = parser.getText();
            } else if ("name".equals(fieldName)) {
                name = parser.getText();
            }
        }

        ContentValues values = new ContentValues();
        values.put("uid", id);
        values.put("name", name);

        db.insertOrThrow("services", null, values);

    }

    private static void readContour(JsonParser parser, SQLiteDatabase db)
            throws JsonParseException, SQLException, IOException {

        String id = null;
        String name = null;
        String color = null;
        String serviceId = null;
        String slug = null;
        String level = null;

        parser.nextToken();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();

            if ("id".equals(fieldName)) {
                id = parser.getText();
            } else if ("name".equals(fieldName)) {
                name = parser.getText();
            } else if ("color".equals(fieldName)) {
                color = parser.getText();
            } else if ("service_id".equals(fieldName)) {
                serviceId = parser.getText();
            } else if ("slug".equals(fieldName)) {
                slug = parser.getText();
            } else if ("level".equals(fieldName)) {
                level = parser.getText();
            }
        }

        ContentValues values = new ContentValues();
        values.put("uid", id);
        values.put("name", name);
        values.put("color", color);
        values.put("service_id", serviceId);
        values.put("slug", slug);
        values.put("level", level);
        db.insertOrThrow("contours", null, values);

    }

    private static void readPlan(JsonParser parser, SQLiteDatabase db) throws JsonParseException,
            SQLException, IOException {

        String id = null;
        String name = null;
        String compObjId = null;
        String imgSrc = null;
        String parentId = null;
        String imgW = null;
        String imgH = null;
        String latitude = null;
        String longtitude = null;
        String type = null;

        parser.nextToken();

        while (parser.nextToken() != JsonToken.END_OBJECT) {

            String fieldName = parser.getCurrentName();

            if ("id".equals(fieldName)) {
                id = parser.getText();
            } else if ("name".equals(fieldName)) {
                name = parser.getText();
            } else if ("company_object_id".equals(fieldName)) {
                compObjId = parser.getText();
            } else if ("image_src".equals(fieldName)) {
                imgSrc = parser.getText();
            } else if ("parent_id".equals(fieldName)) {
                parentId = parser.getText();
            } else if ("image_width".equals(fieldName)) {
                imgW = parser.getText();
            } else if ("image_height".equals(fieldName)) {
                imgH = parser.getText();
            } else if ("latitude".equals(fieldName)) {
                latitude = parser.getText();
            } else if ("longitude".equals(fieldName)) {
                longtitude = parser.getText();
            } else if ("type".equals(fieldName)) {
                type = parser.getText();
            }
        }

        ContentValues values = new ContentValues();
        values.put("uid", id);
        values.put("name", name);
        values.put("company_object_id", compObjId);
        values.put("img_src", imgSrc);
        values.put("parent_id", parentId);
        values.put("image_width", imgW);
        values.put("image_height", imgH);
        values.put("latitude", latitude);
        values.put("longitude", longtitude);
        values.put("type", type);

        db.insertOrThrow("plans", null, values);

    }

    private static void readGroup(JsonParser parser, SQLiteDatabase db) throws JsonParseException,
            SQLException, IOException {

        String id = null;
        String name = null;

        parser.nextToken();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            if ("id".equals(fieldName)) {
                id = parser.getText();
            }
            else if ("name".equals(fieldName)) {
                name = parser.getText();
            }
        }

        ContentValues values = new ContentValues();
        values.put("uid", id);
        values.put("name", name);

        db.insertOrThrow("point_groups", null, values);

    }

    private static void readGroupCharacteristic(JsonParser parser, SQLiteDatabase db)
            throws JsonParseException, SQLException, IOException {

        String id = null;
        String name = null;
        String groupId = null;
        String descr = null;
        String unit = null;
        String dataType = null;
        String avmax = null;
        String avmin = null;
        String cvt = null;
        String cvb = null;
        String cvm = null;
        String inputType = null;

        parser.nextToken();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            if ("id".equals(fieldName)) {
                id = parser.getText();
            } else if ("name".equals(fieldName)) {
                name = parser.getText();
            } else if ("point_group_id".equals(fieldName)) {
                groupId = parser.getText();
            } else if ("description".equals(fieldName)) {
                descr = parser.getText();
            } else if ("unit".equals(fieldName)) {
                unit = parser.getText();
            } else if ("data_type".equals(fieldName)) {
                dataType = parser.getText();
            } else if ("allow_value_max".equals(fieldName)) {
                avmax = parser.getText();
            } else if ("allow_value_min".equals(fieldName)) {
                avmin = parser.getText();
            } else if ("critical_value_top".equals(fieldName)) {
                cvt = parser.getText();
            } else if ("critical_value_bottom".equals(fieldName)) {
                cvb = parser.getText();
            } else if ("critical_color_middle".equals(fieldName)) {
                cvm = parser.getText();
            } else if ("input_type".equals(fieldName)) {
                inputType = parser.getText();
            }

        }

        ContentValues values = new ContentValues();

        values.put("uid", id);
        values.put("name", name);

        values.put("point_group_id", groupId);
        values.put("description", descr);
        values.put("unit", unit);
        values.put("data_type", dataType);
        values.put("allow_value_max", avmax);
        values.put("allow_value_min", avmin);
        values.put("critical_value_top", cvt);
        values.put("critical_value_bottom", cvb);
        values.put("critical_color_middle", cvm);
        values.put("input_type", inputType);
        db.insertOrThrow("point_group_characteristics", null, values);

    }

    private static void readStatus(JsonParser parser, SQLiteDatabase db) throws JsonParseException,
            SQLException, IOException {

        String id = null;
        String name = null;
        String slug = null;

        parser.nextToken();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            if ("id".equals(fieldName)) {
                id = parser.getText();
            } else if ("name".equals(fieldName)) {
                name = parser.getText();
            } else if ("slug".equals(fieldName)) {
                slug = parser.getText();
            }
        }

        ContentValues values = new ContentValues();
        values.put("uid", id);
        values.put("name", name);
        values.put("slug", slug);

        db.insertOrThrow("point_statuses", null, values);

    }

    private static void readPoint(JsonParser parser, SQLiteDatabase db) throws JsonParseException,
            SQLException, IOException {

        String id = null;
        String name = null;
        String planId = null;
        String groupId = null;
        String imgLatitude = null;
        String imgLongtitude = null;
        String mapLatitude = null;
        String mapLongtitude = null;
        String contourId = null;
        String instDate = null;
        String statusId = null;

        parser.nextToken();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.getCurrentName();
            if ("id".equals(fieldName)) {
                id = parser.getText();
            } else if ("name".equals(fieldName)) {
                name = parser.getText();
            } else if ("plan_id".equals(fieldName)) {
                planId = parser.getText();
            } else if ("point_group_id".equals(fieldName)) {
                groupId = parser.getText();
            } else if ("imagelatitude".equals(fieldName)) {
                imgLatitude = parser.getText();
            } else if ("imagelongitude".equals(fieldName)) {
                imgLongtitude = parser.getText();
            } else if ("maplatitude".equals(fieldName)) {
                mapLatitude = parser.getText();
            } else if ("maplongitude".equals(fieldName)) {
                mapLongtitude = parser.getText();
            } else if ("contour_id".equals(fieldName)) {
                contourId = parser.getText();
            } else if ("installationdate".equals(fieldName)) {
                instDate = parser.getText();
            } else if ("status_id".equals(fieldName)) {
                statusId = parser.getText();
            }
        }

        ContentValues values = new ContentValues();

        values.put("uid", id);
        values.put("name", name);

        values.put("plan_id", planId);
        values.put("point_group_id", groupId);
        values.put("imagelatitude", imgLatitude);
        values.put("imagelongitude", imgLongtitude);
        values.put("maplatitude", mapLatitude);
        values.put("maplongitude", mapLongtitude);
        values.put("contour_id", contourId);
        values.put("installationdate", instDate);
        values.put("status_id", statusId);
        db.insertOrThrow("points", null, values);

    }

    private static void readStatistics(JsonParser parser, SQLiteDatabase db)
            throws JsonParseException, SQLException, IOException {

        String id = null;
        String charId = null;
        String pointId = null;
        String createdAt = null;
        String entrydate = null;
        String value = null;

        parser.nextToken();

        while (parser.nextToken() != JsonToken.END_OBJECT) {

            String fieldName = parser.getCurrentName();
            if ("id".equals(fieldName)) {
                id = parser.getText();
            } else if ("characteristic_id".equals(fieldName)) {
                charId = parser.getText();
            } else if ("point_id".equals(fieldName)) {
                pointId = parser.getText();
            } else if ("created_at".equals(fieldName)) {
                createdAt = parser.getText();
            } else if ("entry_date".equals(fieldName)) {
                entrydate = parser.getText();
            } else if ("value".equals(fieldName)) {
                value = parser.getText();
            }
        }

        ContentValues values = new ContentValues();

        values.put("uid", id);
        values.put("characteristic_id", charId);
        values.put("point_id", pointId);
        values.put("created_at", createdAt);
        values.put("entry_date", entrydate);
        values.put("value", value);

        db.insertOrThrow("point_statistics", null, values);

    }

    private static void readPoison(JsonParser parser, SQLiteDatabase db)
            throws JsonParseException, SQLException, IOException {

        String id = null;
        String name = null;
        String active_substance = null;
        String quantity = null;
        String standard_amount = null;

        parser.nextToken();

        while (parser.nextToken() != JsonToken.END_OBJECT) {

            String fieldName = parser.getCurrentName();
            if ("id".equals(fieldName)) {
                id = parser.getText();
            } else if ("name".equals(fieldName)) {
                name = parser.getText();
            } else if ("active_substance".equals(fieldName)) {
                active_substance = parser.getText();
            } else if ("quantity".equals(fieldName)) {
                quantity = parser.getText();
            } else if ("standard_amount".equals(fieldName)) {
                standard_amount = parser.getText();
            }
        }

        ContentValues values = new ContentValues();

        values.put("uid", id);
        values.put("name", name);
        values.put("active_substance", active_substance);
        values.put("quantity", quantity);
        values.put("standard_amount", standard_amount);

        db.insertOrThrow("poisons", null, values);

    }

    private static void readPointPoison(JsonParser parser, SQLiteDatabase db)
            throws JsonParseException, SQLException, IOException {

        String poison_id = null;
        String point_id = null;

        parser.nextToken();

        while (parser.nextToken() != JsonToken.END_OBJECT) {

            String fieldName = parser.getCurrentName();
            if ("poison_id".equals(fieldName)) {
                poison_id = parser.getText();
            } else if ("point_id".equals(fieldName)) {
                point_id = parser.getText();
            }
        }

        ContentValues values = new ContentValues();
        values.put("poison_id", poison_id);
        values.put("point_id", point_id);
        db.insertOrThrow("point_poison", null, values);

    }

    @SuppressWarnings("unused")
    private static String readStream(GZIPInputStream gzip) throws IOException {

        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(gzip, "utf8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            gzip.close();
        }
        return writer.toString();

    }

    public static void parseZippedDatabase(File file, SQLiteDatabase db) throws JsonParseException,
            SQLException, IOException {

        JsonFactory jFactory = new JsonFactory();

        GZIPInputStream gzip = null;
        JsonParser jParser = null;
        try {

            gzip = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file)));
            jParser = jFactory.createParser(gzip);

            int points = 0;
            int statistics = 0;

            while (jParser.nextToken() != JsonToken.END_OBJECT) {

                String fieldName = jParser.getCurrentName();
                if ("company".equals(fieldName)) {
                    jParser.nextToken();
                    while (jParser.nextToken() != JsonToken.END_ARRAY) {
                        StreamParser.readCompany(jParser, db);
                    }
                } else if ("company_object".equals(fieldName)) {
                    jParser.nextToken();
                    while (jParser.nextToken() != JsonToken.END_ARRAY) {
                        StreamParser.readCompanyObject(jParser, db);
                    }
                } else if ("service".equals(fieldName)) {
                    jParser.nextToken();
                    while (jParser.nextToken() != JsonToken.END_ARRAY) {
                        StreamParser.readService(jParser, db);
                    }
                } else if ("contour".equals(fieldName)) {
                    jParser.nextToken();
                    while (jParser.nextToken() != JsonToken.END_ARRAY) {
                        StreamParser.readContour(jParser, db);
                    }
                } else if ("plan".equals(fieldName)) {
                    jParser.nextToken();
                    while (jParser.nextToken() != JsonToken.END_ARRAY) {
                        StreamParser.readPlan(jParser, db);
                    }
                } else if ("point_group".equals(fieldName)) {
                    jParser.nextToken();
                    while (jParser.nextToken() != JsonToken.END_ARRAY) {
                        StreamParser.readGroup(jParser, db);
                    }
                } else if ("point_group_characteristic".equals(fieldName)) {
                    jParser.nextToken();
                    while (jParser.nextToken() != JsonToken.END_ARRAY) {
                        StreamParser.readGroupCharacteristic(jParser, db);
                    }
                } else if ("point_status".equals(fieldName)) {
                    jParser.nextToken();
                    while (jParser.nextToken() != JsonToken.END_ARRAY) {
                        StreamParser.readStatus(jParser, db);
                    }
                } else if ("point".equals(fieldName)) {
                    jParser.nextToken();
                    while (jParser.nextToken() != JsonToken.END_ARRAY) {
                        StreamParser.readPoint(jParser, db);
                        points++;
                    }
                } else if ("point_statistics".equals(fieldName)) {
                    jParser.nextToken();
                    while (jParser.nextToken() != JsonToken.END_ARRAY) {
                        StreamParser.readStatistics(jParser, db);
                        statistics++;
                    }
                } else if ("poison".equals(fieldName)) {
                    jParser.nextToken();
                    while (jParser.nextToken() != JsonToken.END_ARRAY) {
                        StreamParser.readPoison(jParser, db);
                    }
                } else if ("point_poison".equals(fieldName)) {
                    jParser.nextToken();
                    while (jParser.nextToken() != JsonToken.END_ARRAY) {
                        StreamParser.readPointPoison(jParser, db);
                    }
                }
            }
            Logger.Logi(StreamParser.class, " Total parsing: points - " + points
                    + ", statistics - " + statistics);
        } finally {
            if (gzip != null)
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (jParser != null)
                jParser.close();
        }
    }
}
