package net.techguild.base.ui;

import android.content.Context;
import android.view.View;

import net.techguild.base.data.model.Dummy;

public class DummyAdapter extends CAdapter<Dummy> {
    public DummyAdapter(Context context) {
        super(context);
    }

    @Override protected void bindView(Dummy item, int position, View view) {
        ((DummyItemView) view).bindTo(item);
    }

    @Override protected int getViewId(int position) {
        return DummyItemView.getLayoutId();
    }
}
