package net.techguild.base.data.api;

import net.techguild.base.data.request.DummyRequest;
import net.techguild.base.data.response.DummyResponse;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

public interface DummyService {
    @POST("/dummy/list") Observable<DummyResponse> getDummyList(@Body DummyRequest request);
}
