package com.itdoors.haccp.fragments;

import com.itdoors.haccp.R;
import com.itdoors.haccp.activities.MainActivity;
import com.itdoors.haccp.activities.MainActivity.MenuActionMode;
import com.itdoors.haccp.utils.ImageHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MenuFragment extends Fragment implements OnClickListener{
	
	public interface OnProfilePressedListener{
		public void onProfilePressed();
	}

	private OnProfilePressedListener mOnProfilePressedListener;
	
	public static final int profile_id = 1;
	public static final int scanner_id = 2;
	public static final int points_id = 3;
	public static final int about_id = 4;
	
	String[] menus;
	TypedArray imgs;
	
	final int padding = 10;
	int height;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mOnProfilePressedListener = (OnProfilePressedListener)activity;
	}
	
	@SuppressLint("Recycle")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout menuView = (LinearLayout)inflater.inflate(R.layout.menu_layout, null);
		
		menus = getResources().getStringArray(R.array.menu_items);
		imgs = getResources().obtainTypedArray(R.array.menu_items_img);
		height = getResources().getDimensionPixelSize(R.dimen.slidingmenu_icons_size);
		
		
		for(int position = 0; position < menus.length; position++){
			View menuItemView = inflater.inflate(R.layout.menu_list_item, null);
			TextView menu = (TextView)menuItemView.findViewById(R.id.menu_name);
			menu.setText(menus[position]);
			setScaledCompoundDrawable(imgs.getResourceId(position, -1), menu, height, height, padding); 
			
			menuItemView.setId(position + 1);
			menuItemView.setTag(Integer.valueOf(position));
			menuItemView.setOnClickListener(this);
			menuView.addView(menuItemView);
		}
		
		return menuView;
	}
	
	private void setScaledCompoundDrawable(int id, TextView text, int width, int height, int padding){
		Drawable drawable = getResources().getDrawable(id);
		text.setCompoundDrawablePadding(padding);
		text.setCompoundDrawables(ImageHelper.scaleDrawable(drawable, width, height), null, null, null);
	}
	
	@Override
	public void onClick(View v) {
		
		Fragment newContent = null;
		MenuActionMode actionMode = null;
		
		String title = null;
		int position = ((Integer)v.getTag()).intValue();
		switch (position) {
			case 0:
				if(mOnProfilePressedListener != null)
					mOnProfilePressedListener.onProfilePressed();
			break;
			
			case 1:
				title = getResources().getString(R.string.scanner);
				newContent = new QRFragment();
				actionMode = MenuActionMode.SCANNER;
			break;
			
			case 2:
				title = getResources().getString(R.string.points);
				newContent = new ServicesAndContoursFragment();
				actionMode = MenuActionMode.POINTS;
			break;
			
			case 3:
				title = getResources().getString(R.string.about_program);
				newContent = new AboutFragment();
				actionMode = MenuActionMode.ABOUT;
		
			break;
			
		}
		
		if (newContent != null)
		switchFragment(newContent,title,v, actionMode);
		
	}
	
	private void switchFragment(Fragment fragment, String title, View v, MenuActionMode actionMode) {
		if (getActivity() == null) return;
		
		if (getActivity() instanceof MainActivity) {
					MainActivity ra = (MainActivity) getActivity();
					ra.switchContent(fragment,title,v, actionMode);
			}
	}
	
	
}
