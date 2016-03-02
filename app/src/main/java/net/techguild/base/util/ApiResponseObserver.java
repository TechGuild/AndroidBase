package net.techguild.base.util;

import net.techguild.base.data.response.CResponse;

import retrofit.client.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ApiResponseObserver {
    public static <T extends CResponse> void observe(Observable<T> observable, Action1<T> successAction, Action1<Throwable> failAction) {
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(successAction, failAction);
    }

    public static <T extends CResponse> void observe(Observable<T> observable, Action1<T> successAction, Action1<Throwable> failAction, Action0 completeAction) {
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(successAction, failAction, completeAction);
    }

    public static void observeRaw(Observable<Response> observable, Action1<Response> successAction, Action1<Throwable> failAction) {
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(successAction, failAction);
    }
}