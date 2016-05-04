package com.kineticcafe.kcpmall.views;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Kay on 2016-05-03.
 */
public class FancyBehavior <V extends View>
        extends CoordinatorLayout.Behavior<V> {
    /**
     * Default constructor for instantiating a FancyBehavior in code.
     */
    public FancyBehavior() {
    }
    /**
     * Default constructor for inflating a FancyBehavior from layout.
     *
     * @param context The {@link Context}.
     * @param attrs The {@link AttributeSet}.
     */
    public FancyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Extract any custom attributes out
        // preferably prefixed with behavior_ to denote they
        // belong to a behavior
    }
}
