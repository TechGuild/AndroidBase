package net.techguild.base.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import net.techguild.base.R;
import net.techguild.base.data.adapter.DummyAdapter;
import net.techguild.base.data.adapter.base.BindableList;
import net.techguild.base.util.CActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends CActivity implements BindableList, SwipeRefreshLayout.OnRefreshListener {
    @InjectView(R.id.list) ListView list;
    @InjectView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    private DummyAdapter adapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        adapter = new DummyAdapter(this, this);
        list.setAdapter(adapter);
        swipeContainer.setOnRefreshListener(this);
        adapter.loadData("");
    }

    @Override protected void onDestroy() {
        ButterKnife.reset(this);
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

    @Override public void onRefreshBegin() {
        // Show loading
    }

    @Override public void onRefreshEnd() {
        // Hide Loading

        if (swipeContainer != null) {
            swipeContainer.setRefreshing(false);
        }
    }

    @Override public void onShowMoreBegin() {
        // Show scroll loading
    }

    @Override public void onShowMoreEnd() {
        // Hide scroll loading
    }

    @Override public void onError() {
        if (swipeContainer != null) {
            swipeContainer.setRefreshing(false);
        }

        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    @Override public void onRefresh() {
        adapter.loadData();
    }
}
