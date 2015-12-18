package com.mobileproto.hireddit.hireddit.visuals;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
    private ArrayList<Integer> mHeights = new ArrayList<>();
    private ListViewAdapterCallback listCallback;

    public ListViewAdapter(Activity mcontext, ArrayList<String> question,
                           ArrayList<String> answer, ListViewAdapterCallback mlistCallback) {
        super();
        this.context = mcontext;
        this.requests = question;
        this.responses = answer;
        listCallback = mlistCallback;
    }

    @Override public int getCount() {
        return requests.size();
    }

    @Override public Object getItem(int position) {
        ArrayList<String> items = new ArrayList<>(2);
        items.add(requests.get(position));
        items.add(responses.get(position));
        return items;
    }

    @Override public long getItemId(int position) {
        return position;
    }

    public int getLastItemHeight(){
      return mHeights.get(getCount() - 1);
    }

    @Override public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.speech = (TextView) convertView.findViewById(R.id.speechText);
            holder.comment = (TextView) convertView.findViewById(R.id.commentText);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        // add items
        holder.speech.setText(requests.get(position));
        holder.comment.setText(responses.get(position));
        mHeights.add(0); //TODO: make this instead just making sure there's a space at element position that isn't overwritten
                        //so you can do mHeights.set(position, bottom - top) later without null pointer error

        // animating comment in
        Animation commentAnimation = AnimationUtils.loadAnimation(context, R.anim.comment_slide);
        holder.comment.startAnimation(commentAnimation);

        // get height of the item
        if(android.os.Build.VERSION.SDK_INT >= 11) { // see http://stackoverflow.com/questions/13131948/calculating-the-height-of-each-row-of-a-listview
            convertView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    mHeights.set(position, bottom - top);
                    Log.d(DEBUG_TAG, "For comment: '" + requests.get(position) + "', height is: " + mHeights.get(position));
                    listCallback.itemHeightCallback(mHeights.get(getCount() - 1));
                }
            });
        }

        return convertView;
    }

    private class ViewHolder {
        TextView speech;
        TextView comment;
    }
}
