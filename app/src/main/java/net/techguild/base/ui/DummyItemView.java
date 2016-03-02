package net.techguild.base.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.techguild.base.R;
import net.techguild.base.data.model.Dummy;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DummyItemView extends LinearLayout {
    @Bind(R.id.dummyText) TextView dummyText;

    private Dummy item;

    public DummyItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static int getLayoutId() {
        return R.layout.item_view_dummy;
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow() {
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    public void bindTo(final Dummy item) {
        this.item = item;
        dummyText.setText(item.description);
    }
}
