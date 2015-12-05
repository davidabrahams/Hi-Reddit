package com.mobileproto.hireddit.hireddit.visuals;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobileproto.hireddit.hireddit.R;

import java.util.ArrayList;

/**
 * Created by nmohamed on 12/5/2015.
 */
public class ListViewAdapter extends ArrayAdapter {

    Activity context;
    ArrayList<String> requests;
    ArrayList<String> responses;

    public ListViewAdapter(Activity mcontext, ArrayList<String> question, ArrayList<String> answer) {
        super();
        this.context = mcontext;
        this.requests = question;
        this.responses = answer;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.speech = (TextView) convertView.findViewById(R.id.speechText);
            holder.comment = (TextView) convertView.findViewById(R.id.commentText);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.speech.setText(requests.get(position));
        holder.comment.setText(responses.get(position));

        return convertView;
    }

    private class ViewHolder {
        TextView speech;
        TextView comment;
    }
}
