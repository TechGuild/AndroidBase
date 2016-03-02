package net.techguild.base.data.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

// Custom Date formatter for api
public class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
    private final DateFormat dateFormat;
    private final DateFormat dateFormatWithMillis;

    public DateTypeAdapter() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        dateFormatWithMillis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateFormatWithMillis.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    // Java object to JSON conversion for requests
    @Override public synchronized JsonElement serialize(Date date, Type type,
                                                        JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dateFormat.format(date));
    }

    // JSON to java object conversion for responses
    @Override public synchronized Date deserialize(JsonElement jsonElement, Type type,
                                                   JsonDeserializationContext jsonDeserializationContext) {
        try {
            String dateStr = jsonElement.getAsString();
            if (dateStr.length() > 20) {
                return dateFormatWithMillis.parse(jsonElement.getAsString());
            } else {
                return dateFormat.parse(jsonElement.getAsString());
            }
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }
}