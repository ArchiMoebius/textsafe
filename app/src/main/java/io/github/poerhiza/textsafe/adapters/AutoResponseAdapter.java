package io.github.poerhiza.textsafe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import io.github.poerhiza.textsafe.R;
import io.github.poerhiza.textsafe.valueobjects.AutoResponse;

public class AutoResponseAdapter extends ArrayAdapter<AutoResponse> {
    private final int autoResponseItemLayoutResource;

    public AutoResponseAdapter(final Context context, final int autoResponseItemLayoutResource) {
        super(context, 0);
        this.autoResponseItemLayoutResource = autoResponseItemLayoutResource;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public boolean isEnabled(int arg0)
    {
        return true;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
// We need to get the best view (re-used if possible) and then
// retrieve its corresponding ViewHolder, which optimizes lookup efficiency

        final View view = getWorkingView(convertView);
        final ViewHolder viewHolder = getViewHolder(view);
        final AutoResponse entry = getItem(position);

        viewHolder.titleView.setText(entry.getTitle());
        viewHolder.subTitleView.setText(entry.getResponse());

        return view;
    }

    private View getWorkingView(final View convertView) {
// The workingView is basically just the convertView re-used if possible
// or inflated new if not possible
        View workingView = null;
        if (null == convertView) {
            final Context context = getContext();
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            workingView = inflater.inflate(autoResponseItemLayoutResource, null);
        } else {
            workingView = convertView;
        }
        return workingView;
    }

    private ViewHolder getViewHolder(final View workingView) {
// The viewHolder allows us to avoid re-looking up view references
// Since views are recycled, these references will never change
        final Object tag = workingView.getTag();
        ViewHolder viewHolder = null;
        if (null == tag || !(tag instanceof ViewHolder)) {
            viewHolder = new ViewHolder();
            viewHolder.titleView = (TextView) workingView.findViewById(R.id.auto_response_item_title);
            viewHolder.subTitleView = (TextView) workingView.findViewById(R.id.auto_response_item_response);
            workingView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) tag;
        }
        return viewHolder;
    }

    /**
     * ViewHolder allows us to avoid re-looking up view references
     * Since views are recycled, these references will never change
     */
    private static class ViewHolder {
        public TextView titleView;
        public TextView subTitleView;
    }
}
