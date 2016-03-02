package net.techguild.base.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

// Custom Abstract Base Adapter class
public abstract class CAdapter<T> extends BaseAdapter {
    protected static final int UNKNOWN_TYPE = -1;

    protected final Context context;
    protected final LayoutInflater inflater;
    protected List<T> list = new ArrayList<T>();

    public CAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void loadData(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void addData(List<T> items) {
        list.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        this.list.clear();
        notifyDataSetChanged();
    }

    @Override public int getCount() {
        return list.size();
    }

    @Override public T getItem(int i) {
        return list.get(i);
    }

    @Override public long getItemId(int i) {
        return i;
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

        return view;
    }

    protected abstract void bindView(T item, int position, View view);

    protected View newView(LayoutInflater inflater, int position, ViewGroup container) {
        int viewId = getViewId(position);

        if (viewId != UNKNOWN_TYPE) {
            return inflater.inflate(getViewId(position), container, false);
        } else {
            return null;
        }
    }

    protected abstract int getViewId(int position);
}
