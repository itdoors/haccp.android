
package com.itdoors.haccp.ui.fragments;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.itdoors.haccp.R;

public class AboutFragment extends SherlockFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about_program, container, false);
        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.about_content_frame);

        String[] instructions = getResources().getStringArray(R.array.instructions);
        for (String instruction : instructions) {
            View textLayoutView = inflater.inflate(R.layout.text_instructions,
                    linearLayout, false);
            TextView textView = (TextView) textLayoutView.findViewById(R.id.about_instruction_text);
            textView.setText(instruction);
            linearLayout.addView(textLayoutView);
        }
        TextView linkView = (TextView) rootView.findViewById(R.id.about_link);
        linkView.setMovementMethod(LinkMovementMethod.getInstance());
        return rootView;
    }

}
