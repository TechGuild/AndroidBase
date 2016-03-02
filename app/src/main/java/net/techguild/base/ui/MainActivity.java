package net.techguild.base.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import net.techguild.base.R;
import net.techguild.base.data.api.DummyService;
import net.techguild.base.data.manager.UserManager;
import net.techguild.base.data.request.DummyRequest;
import net.techguild.base.data.response.DummyResponse;
import net.techguild.base.util.ApiResponseObserver;
import net.techguild.base.util.CActivity;
import net.techguild.base.util.DaggerHelper;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class MainActivity extends CActivity implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.list) ListView list;
    @Bind(R.id.swipeContainer) SwipeRefreshLayout swipeRefreshLayout;

    @Inject DummyService dummyService;
    @Inject UserManager userManager;

    private DummyAdapter adapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerHelper.inject(this);
        ButterKnife.bind(this);

        initListView();
        fetchDummyList();
    }

    private void initListView() {
        adapter = new DummyAdapter(this);
        list.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void fetchDummyList() {
        // Sample api request
        ApiResponseObserver.observe(
                dummyService.getDummyList(new DummyRequest("filter")),
                new Action1<DummyResponse>() {
                    @Override public void call(DummyResponse dummyResponse) {
                        // Feed adapter with data
                        adapter.loadData(dummyResponse.results);
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                },
                new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
        );
    }

    @Override protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public void onRefresh() {
        // On pull to refresh
        fetchDummyList();
    }
}
