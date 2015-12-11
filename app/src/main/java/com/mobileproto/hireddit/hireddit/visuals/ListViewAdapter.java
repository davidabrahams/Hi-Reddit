package com.mobileproto.hireddit.hireddit.visuals;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mobileproto.hireddit.hireddit.R;
import java.util.ArrayList;

/**
 * ListViewAdapter: A custom adapter for list views that displays two text views.
 **/
public class ListViewAdapter extends BaseAdapter {
    private String DEBUG_TAG = "SpeechFragmentDebug";
    private Activity context;
    private ArrayList<String> requests;
    private ArrayList<String> responses;
    private int lastItemHeight = 0;

    public ListViewAdapter(Activity mcontext, ArrayList<String> question, ArrayList<String> answer) {
        super();
        this.context = mcontext;
        this.requests = question;
        this.responses = answer;
    }

    @Override
    public int getCount() {
        return requests.size();
    }

    @Override
    public Object getItem(int position) {
        ArrayList<String> items = new ArrayList<>(2);
        items.add(requests.get(position));
        items.add(responses.get(position));
        return items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getLastItemHeight(){
      return lastItemHeight;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.list_item, null);
        //View mView = listViewAdapter.getView(listViewAdapter.getCount()-1, null, listView);
        convertView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { //initialize listener after there is something in the listView
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //ref.removeOnLayoutChangeListener(this);
                if (position == getCount() - 1) {
                    lastItemHeight = bottom - top;
                    Log.d(DEBUG_TAG, "Layout change! Height is: " + lastItemHeight);
                }
            }
        });

        holder = new ViewHolder();
        holder.speech = (TextView) convertView.findViewById(R.id.speechText);
        holder.comment = (TextView) convertView.findViewById(R.id.commentText);
        //holder.emptyspace = (TextView) convertView.findViewById(R.id.emptySpace);
        convertView.setTag(holder);

        holder.speech.setText(requests.get(position));
        holder.comment.setText(responses.get(position));
        //holder.emptyspace.setText("p\np\np\np\np\np\np");


        return convertView;
    }

    private class ViewHolder {
        TextView speech;
        TextView comment;
        //TextView emptyspace;
    }
}
