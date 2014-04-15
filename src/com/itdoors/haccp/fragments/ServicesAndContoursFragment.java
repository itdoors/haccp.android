package com.itdoors.haccp.fragments;

import com.itdoors.haccp.R;
import com.itdoors.haccp.activities.PointDetailsActivity;
import com.itdoors.haccp.activities.PointsListActivity;
import com.itdoors.haccp.adapters.CategorizedListAdapter;
import com.itdoors.haccp.model.Contour;
import com.itdoors.haccp.model.Service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


public class ServicesAndContoursFragment extends ListFragment {

	private ServicesAndContoursAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new ServicesAndContoursAdapter(getActivity());
		
		Contour[] contours1 = new Contour[3];
		Contour[] contours2 = new Contour[1];
		
		Service service1 = Service.DERATISATION;
		Service service2 = Service.DESINSECTUM;
		
		mAdapter.addSection(service1);
		for(int i = 0; i < contours1.length; i++){
			contours1[i] = new Contour(i, ( i + 1 ) +"-й " + "контур " );
			mAdapter.addItem(contours1[i]);
		}
		mAdapter.addSection(service2);
		for(int i = 0; i < contours2.length; i ++){
			contours2[i] = new Contour(i, ( i + 1 ) + "-й " + "контур ");
			mAdapter.addItem(contours2[i]);
		}
		
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final ListView mListView = getListView();
		mListView.setBackgroundResource(R.drawable.abs__ab_solid_light_holo);
		mListView.setSelector(R.drawable.abs__tab_indicator_ab_holo);
		mListView.setCacheColorHint(Color.TRANSPARENT);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(mAdapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		int count, delta;
		if(position == 1){
			count = 65;
			delta = 19;

			Intent intent = PointsListActivity.newInstance(getActivity(), count, delta);
			startActivity(intent);
		}
		if(position == 2){
			count = 57;
			delta = 86;

			Intent intent = PointsListActivity.newInstance(getActivity(), count, delta);
			startActivity(intent);
		}
		
		if(position == 3){
			count = 91;
			delta = 153;

			Intent intent = PointsListActivity.newInstance(getActivity(), count, delta);
			startActivity(intent);
		}
		

	}
	
	public static class ServicesAndContoursAdapter extends CategorizedListAdapter<Contour, Service>{

		private Context context;
		private LayoutInflater mLayoutInflater;
		
		public ServicesAndContoursAdapter(Context context) {
			mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.context = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			 
			ViewHolder holder = null;
			
			int rowType = getItemViewType(position);
			 
		        if (convertView == null) {
		            switch (rowType) {
		            	case TYPE_ITEM:
		            		
		            		convertView = mLayoutInflater.inflate(R.layout.countour_item_layout, parent, false);
			            	
		            		holder = new ContourViewHolder();
		            		((ContourViewHolder)holder).name = (TextView)convertView.findViewById(R.id.cont_item_name);
		            		
		            	break;
		            	case TYPE_SECTION:
		            	
		            		convertView = mLayoutInflater.inflate(R.layout.service_item_layout, parent, false);
		            	
		            		holder = new ServiceViewHolder();
		            		((ServiceViewHolder)holder).name = (TextView)convertView.findViewById(R.id.serv_item_name);
	            	
		            	break;
		            }
		            convertView.setTag(holder);
		        } else {
		        	 switch (rowType) {
		        	 	 case TYPE_ITEM:
		        	 		 holder = (ContourViewHolder) convertView.getTag();
		        		 break;
		        	 	case TYPE_SECTION:
		        	 		 holder = (ServiceViewHolder) convertView.getTag();
		        	 	break;
		        	 		
		        	 }
		           
		        }
		    
		        
		        if(rowType == TYPE_ITEM){
		        	
		        	Contour contour = (Contour)getItem(position);
		        	String name = contour.getName();
		        	((ContourViewHolder)holder).name.setText(name == null ? "" : name);
		        
		        }
		        else if(rowType == TYPE_SECTION){
		        
		        	Service service = (Service)getItem(position);
		        
	        		String name = service.toString(context);
	        		((ServiceViewHolder)holder).name.setText(name == null ? "" : name);
		        }
		        
		        return convertView;
		}
		
		
		interface ViewHolder{}
		
		class ServiceViewHolder implements ViewHolder{
			TextView name;
		}
		
		class ContourViewHolder implements ViewHolder{
			TextView name;
		}
		
	}
	
	
}
