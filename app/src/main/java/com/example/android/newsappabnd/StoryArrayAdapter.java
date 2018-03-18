package com.example.android.newsappabnd;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by nibos on 3/17/2018.
 */

public class StoryArrayAdapter extends ArrayAdapter<Story> {
    public StoryArrayAdapter(@NonNull Context context, int resource, @NonNull List <Story> lista) {
        super(context, 0, lista);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.story_listview_item,parent,false);
        }
        Story currentStory=getItem(position);
        TextView storyTitleTextView = convertView.findViewById(R.id.tv_story_title_listview_item);
        TextView storySectionTextView = convertView.findViewById(R.id.tv_story_section_listview_item);
        TextView storyDateTextView = convertView.findViewById(R.id.tv_story_date_listview_item);
        storyTitleTextView.setText("desters="+String.valueOf(position)+". "+currentStory.getTitle());
        storySectionTextView.setText(currentStory.getSectionName());
        storyDateTextView.setText(Utility.getFormatedDate(currentStory.getDate()));
        return convertView;
    }
}
