package net.techguild.base.data.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.squareup.picasso.Picasso;

import net.techguild.base.CApp;
import net.techguild.base.data.api.DummyService;
import net.techguild.base.data.api.UserService;
import net.techguild.base.data.event.UnauthorizedErrorEvent;
import net.techguild.base.data.manager.UserManager;
import net.techguild.base.data.manager.UserStore;
import net.techguild.base.data.serializer.DateTypeAdapter;
import net.techguild.base.ui.MainActivity;
import net.techguild.base.util.CLog;

import java.net.HttpURLConnection;
import java.util.Date;

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
                // Fragments

                // Views
        }
)
public class BasicModule {
    private static final String API_URL = "http://api.appurl.com/"; //TODO: App url
    private final CApp app;
    private static final String SHARED_PREF_KEY = "appname"; //TODO: App pref key

    public BasicModule(CApp app) {
        this.app = app;
    }

    @Provides @Singleton Picasso providePicasso() {
        return Picasso.with(app);
    }

    @Provides @Singleton Endpoint provideEndpoint() {
        return Endpoints.newFixedEndpoint(API_URL);
    }

    @Provides @Singleton Application provideApplication() {
        return app;
    }

    @Provides @Singleton SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
    }

    // Managers and Stores

    @Provides @Singleton UserStore provideUserStore(SharedPreferences sharedPreferences) {
        return new UserStore(sharedPreferences);
    }

    @Provides @Singleton UserManager provideUserManager(UserStore userStore, UserService userService) {
        return new UserManager(userStore, userService);
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

    @Provides @Singleton Gson provideGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
        return builder.create();
    }

    // Generic Request interceptor for adding manual headers
    @Provides @Singleton RequestInterceptor provideRequestInterceptor(final UserStore userStore) {
        return new RequestInterceptor() {
            @Override public void intercept(RequestFacade request) {
                // Authorization header
                request.addHeader("Accept", "application/json");
                if (userStore.getAccessToken() != null) {
                    request.addHeader("Authorization", "Bearer " + userStore.getAccessToken());
                }
            }
        };
    }

    // Generic Rest Adapter class. comment out log line to output all rest api action logs
    @Provides @Singleton RestAdapter provideRestAdapter(Gson gson, Endpoint endpoint, Client client, Bus bus, RequestInterceptor requestInterceptor) {
        return new RestAdapter.Builder() //
                .setRequestInterceptor(requestInterceptor)
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

    @Provides @Singleton UserService provideUserService(RestAdapter restAdapter) {
        return restAdapter.create(UserService.class);
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
