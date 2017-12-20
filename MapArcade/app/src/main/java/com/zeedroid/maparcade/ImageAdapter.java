package com.zeedroid.maparcade;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Steve Dixon on 12/06/2017.
 */

public class ImageAdapter extends ArrayAdapter<String> {
    private Context context;
    private int[] images;
    private String[] titles;

    public ImageAdapter(Context c, String[] titles, int[] images) {
        super(c,R.layout.route_main,R.id.route_text,titles);
        this.context = c;
        this.images = images;
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.route_main, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        if (holder.imageView != null) {
            holder.imageView.setImageResource(images[position]);
        }
        if (holder.textView != null) {
            holder.textView.setText(titles[position]);
        }
        return row;
    }

    class ViewHolder {
        ImageView imageView;
        TextView textView;
        Button buttonOne, buttonTwo;
        ViewHolder(View v) {
              imageView = v.findViewById(R.id.route_image);
              textView = v.findViewById(R.id.route_text);
              buttonOne = v.findViewById(R.id.route_create);
              buttonTwo = v.findViewById(R.id.route_search);
              buttonOne.setOnClickListener(new View.OnClickListener(){
                  @Override
                  public void onClick(View view){
//                      Intent createMapIntent = new Intent(context, CreateMapActivity.class);
                      Intent createMapIntent = new Intent(context, MapCreateActivity.class);
                      createMapIntent.putExtra("mapType",(String)textView.getText());
                      createMapIntent.putExtra("zoomLevel",19);
                      context.startActivity(createMapIntent);
                  }
              });
              buttonTwo.setOnClickListener(new View.OnClickListener(){
                  @Override
                  public void onClick(View view){
                      Toast.makeText(view.getContext(),"Search",Toast.LENGTH_SHORT);
                  }
              });
        }
    }
}