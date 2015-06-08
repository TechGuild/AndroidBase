package net.techguild.base.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.techguild.base.CApp;
import net.techguild.base.R;
import net.techguild.base.data.adapter.base.BindableAdapter;
import net.techguild.base.data.adapter.base.BindableList;
import net.techguild.base.data.api.DummyService;
import net.techguild.base.data.model.Dummy;
import net.techguild.base.data.request.DummyRequest;
import net.techguild.base.data.response.DummyResponse;
import net.techguild.base.ui.DummyItemView;
import net.techguild.base.util.CLog;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DummyAdapter extends BindableAdapter<Dummy> {
    @Inject DummyService dummyService;
    private String filter = "";
    private int more = 0;

    public DummyAdapter(Context context, BindableList bindableList) {
        super(context);
        CApp.get(context).inject(this);
        this.bindableList = bindableList;
        showMoreEnabled = true;
    }

    @Override protected String getAdapterName() {
        return "Dummy Adapter";
    }

    public void loadData(String filter) {
        this.filter = filter;
        loadData();
    }

    @Override public void loadData() {
        showMore = true;
        bindableList.onRefreshBegin();
        loadDummy(filter, 0);
    }

    @Override public void loadMoreData() {
        if (more > 0) {
            if (bindableList != null) {
                bindableList.onShowMoreBegin();
            }
            showMore = false;
            loadDummy(filter, more);
        }
    }

    private void loadDummy(final String filter, final int more) {
        dataSubscription = dummyService.getDummyList(new DummyRequest(filter))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DummyResponse>() {
                    @Override
                    public void call(DummyResponse response) {
                        CLog.t("DummyResponse count:", response.results.size());

                        if (more > 1) {
                            showMore = appendToList(response.results);
                            DummyAdapter.this.more++;
                            if (bindableList != null) {
                                bindableList.onShowMoreEnd();
                            }
                        } else {
                            replaceWith(response.results);
                            if (bindableList != null) {
                                bindableList.onRefreshEnd();
                            }
                            DummyAdapter.this.more = 1;
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        CLog.e("T", "GetDummy Error", throwable);
                        if (bindableList != null) {
                            bindableList.onError();
                        }
                    }
                });
    }

    @Override public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.item_view_dummy, container, false);
    }

    @Override public void bindView(Dummy item, int position, View view) {
        ((DummyItemView) view).bindTo(item);
    }
}
