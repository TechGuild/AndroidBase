package net.techguild.base.data.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.techguild.base.util.CLog;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

// Custom Abstract Adapter class used in almost all adapters
public abstract class BindableAdapter<T> extends BaseAdapter {
    private final Context context;
    protected final LayoutInflater inflater;
    public BindableList bindableList;
    protected List<T> list = new ArrayList<T>();
    public boolean showMoreEnabled = false;
    public boolean showMore = true;
    protected Subscription dataSubscription;
    private final String adapterName = "Bindable";

    // constructor
    public BindableAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public Context getContext() {
        return context;
    }

    // View creater + data binder with view reusability features.
    @Override public View getView(int position, View view, ViewGroup container) {
        // If input view is null, a new view is created using the newView function in main adapter function. if input view is defined, reuse it
        if (view == null) {
            view = newView(inflater, position, container);
            if (view == null) {
                throw new IllegalStateException("newView result must not be null.");
            }
        }
        bindView(getItem(position), position, view); // bind data to view function

        if (showMoreEnabled && position == getCount() - 1) {
            onShowMore(); // if current view is the last object in list, trigger show more api call
        }

        return view;
    }

    protected void onShowMore() {
        int size = list.size();
        // synchronized to avoid multiple parallel show more calls.
        synchronized (this) {
            if (size > 0 && showMore) {
                CLog.d("T", getAdapterName() + " Show More");
                loadMoreData();
            }
        }
    }

    protected abstract String getAdapterName();

    @Override protected void finalize() throws Throwable {
        // on adapter finalize, unsubscribe to any api calls
        if (dataSubscription != null) {
            dataSubscription.unsubscribe();
        }
        super.finalize();
    }

    @Override public T getItem(int position) {
        return list.get(position);
    }

    @Override public int getCount() {
        return list.size();
    }

    @Override public long getItemId(int position) {
        return position;
    }

    // Change all data with the list input and notify the adapter
    public void replaceWith(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    // Add new items to current list. returns true if anything is added. This boolean is used to re-enable show more functionality
    public boolean appendToList(List<T> newList) {
        if (this.list == null) {
            this.list = newList;
        } else {
            this.list.addAll(newList);
        }
        notifyDataSetChanged();
        return newList.size() > 0;
    }

    public abstract void loadData();

    public abstract void loadMoreData();

    public abstract View newView(LayoutInflater inflater, int position, ViewGroup container);

    public abstract void bindView(T item, int position, View view);

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }
}
