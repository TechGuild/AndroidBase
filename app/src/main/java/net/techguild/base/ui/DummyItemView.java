package net.techguild.base.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.techguild.base.R;
import net.techguild.base.data.model.Dummy;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DummyItemView extends LinearLayout {
    @InjectView(R.id.dummyText) TextView dummyText;

    private Dummy item;

    public DummyItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow() {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void bindTo(final Dummy item) {
        this.item = item;
        dummyText.setText(item.description);
    }
}
