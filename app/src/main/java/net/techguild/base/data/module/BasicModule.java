package net.techguild.base.data.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import net.techguild.base.CApp;
import net.techguild.base.data.adapter.DummyAdapter;
import net.techguild.base.data.api.DummyService;
import net.techguild.base.data.event.UnauthorizedErrorEvent;
import net.techguild.base.ui.MainActivity;
import net.techguild.base.util.CLog;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

// Add basic classes that use injection to this list
@Module(
        complete = true,
        library = true,
        injects = {
                CApp.class,
                // Activities
                MainActivity.class,
                // Adapters
                DummyAdapter.class
        }
)
public class BasicModule {
    private static final String API_URL = "http://api.techguild.net/";
    private final CApp app;
    private final SharedPreferences sharedPreferences;
    private String userToken;

    public BasicModule(CApp app, SharedPreferences sharedPreferences) {
        this.app = app;
        this.sharedPreferences = sharedPreferences;
    }

    // Change commented lines to switch production-test-dev api endpoints
    @Provides @Singleton Endpoint provideEndpoint() {
        return Endpoints.newFixedEndpoint(API_URL);
    }

    @Provides @Singleton Application provideApplication() {
        return app;
    }

    @Provides @Singleton SharedPreferences provideSharedPreferences(Application app) {
        return sharedPreferences;
    }

    @Provides @Singleton OkHttpClient provideOkHttpClient(Application app) {
        return new OkHttpClient();
    }

    @Provides @Singleton Client provideClient(OkHttpClient client) {
        return new OkClient(client);
    }

    // OTTO event bus. Event management is done by using this class
    @Provides @Singleton Bus provideBus() {
        return new Bus(ThreadEnforcer.ANY);
    }

    // Generic Rest Adapter class. comment out log line to output all rest api action logs
    @Provides @Singleton RestAdapter provideRestAdapter(Endpoint endpoint, Client client, Bus bus) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();
        return new RestAdapter.Builder() //
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override public void intercept(RequestFacade request) {
                        request.addHeader("Accept", "application/json");
                        if (userToken != null) {
                            request.addHeader("Authorization", userToken);
                        }
                    }
                })
                .setClient(client)
                .setErrorHandler(new NetworkErrorHandler(bus))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(endpoint)
                .setConverter(new GsonConverter(gson))
                .build();
    }

    // API Services

    @Provides @Singleton DummyService provideBaseService(RestAdapter restAdapter) {
        return restAdapter.create(DummyService.class);
    }

    public void setUserToken(String token) {
        CLog.t("Token Set to:", token);
        this.userToken = token;
    }

    // Custom Date formatter for api
    private static class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
        private final DateFormat dateFormat;
        private final DateFormat dateFormatWithMillis;

        private DateTypeAdapter() {
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

    private class NetworkErrorHandler implements ErrorHandler {
        // Custom error handler to detect app-wide 401 and connection errors
        private final Handler MAIN_LOOPER_HANDLER = new Handler(Looper.getMainLooper());
        private final Bus eventBus;

        public NetworkErrorHandler(Bus eventBus) {
            this.eventBus = eventBus;
        }

        @Override public Throwable handleError(RetrofitError error) {
            app.onApiError(error);

            Response r = error.getResponse();
            if (r != null && r.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                // we need to make sure we post in the UI thread
                MAIN_LOOPER_HANDLER.post(new Runnable() {
                    @Override public void run() {
                        CLog.e("T", "UnauthorizedErrorEvent");
                        eventBus.post(new UnauthorizedErrorEvent());
                    }
                });
            }

            return error;
        }
    }
}
