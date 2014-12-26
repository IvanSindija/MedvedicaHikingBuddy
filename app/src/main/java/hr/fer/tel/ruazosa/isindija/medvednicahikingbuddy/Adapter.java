package hr.fer.tel.ruazosa.isindija.medvednicahikingbuddy;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by ivan on 25.12.14..
 */
public class Adapter extends BaseAdapter {

    public static final int NUM_ELEMENTS = 100;

    private final Context context;
    private final LayoutInflater inflater;
    private String[] trackName = {"1","2","3"};//privremeno

    private static class ViewHolder {
        public TextView title;
        public TextView subtitle;
    }

    public Adapter(final Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return NUM_ELEMENTS;
    }

    @Override
    public String getItem(int position) {
        return String.format("#%09d",position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_2, null, false);

            final TextView title = (TextView) convertView.findViewById(android.R.id.text1);
            final TextView subtitle = (TextView) convertView.findViewById(android.R.id.text2);

            final ViewHolder vh = new ViewHolder();
            vh.title = title;
            vh.subtitle = subtitle;

            convertView.setTag(vh);
        }

        final ViewHolder vh = (ViewHolder) convertView.getTag();

        final TextView title = vh.title;
        final TextView subtitle = vh.subtitle;

        final String item = getItem(position);

        if(position % 2 == 0) {
            title.setTextColor(Color.parseColor("#22aa66"));
        }else {
            title.setTextColor(Color.parseColor("#FF0000"));
        }

        title.setText(item);

        subtitle.setText("Staza " + position);

        return convertView;
    }
}
