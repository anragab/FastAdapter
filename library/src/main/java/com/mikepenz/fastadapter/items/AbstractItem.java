package com.mikepenz.fastadapter.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

/**
 * Created by mikepenz on 14.07.15.
 */
public abstract class AbstractItem<T> implements IItem<T> {
    protected int mIdentifier = -1;

    public T withIdentifier(int identifier) {
        this.mIdentifier = identifier;
        return (T) this;
    }

    @Override
    public int getIdentifier() {
        return mIdentifier;
    }

    protected Object mTag;

    public T withTag(Object object) {
        this.mTag = object;
        return (T) this;
    }

    @Override
    public Object getTag() {
        return mTag;
    }

    protected boolean mEnabled = true;

    public T withEnabled(boolean enabled) {
        this.mEnabled = enabled;
        return (T) this;
    }

    @Override
    public boolean isEnabled() {
        return mEnabled;
    }

    protected boolean mSelected = false;

    @Override
    public T withSetSelected(boolean selected) {
        this.mSelected = selected;
        return (T) this;
    }

    @Override
    public boolean isSelected() {
        return mSelected;
    }

    protected boolean mSelectable = true;

    @Override
    public T withSelectable(boolean selectable) {
        this.mSelectable = selectable;
        return (T) this;
    }

    @Override
    public boolean isSelectable() {
        return mSelectable;
    }

    public abstract ViewHolderFactory getFactory();

    @Override
    public View generateView(Context ctx) {
        RecyclerView.ViewHolder viewHolder = getFactory().factory(LayoutInflater.from(ctx).inflate(getLayoutRes(), null, false));
        bindView(viewHolder);
        return viewHolder.itemView;
    }

    @Override
    public View generateView(Context ctx, ViewGroup parent) {
        RecyclerView.ViewHolder viewHolder = getFactory().factory(LayoutInflater.from(ctx).inflate(getLayoutRes(), parent, false));
        bindView(viewHolder);
        return viewHolder.itemView;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup parent) {
        return getFactory().factory(LayoutInflater.from(parent.getContext()).inflate(getLayoutRes(), parent, false));
    }

    public boolean equals(Integer id) {
        return id != null && id == mIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractItem<?> that = (AbstractItem<?>) o;
        return mIdentifier == that.mIdentifier;
    }

    @Override
    public int hashCode() {
        return mIdentifier;
    }
}