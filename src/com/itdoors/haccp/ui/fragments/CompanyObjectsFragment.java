package com.itdoors.haccp.ui.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.itdoors.haccp.R;
import com.itdoors.haccp.model.Company;
import com.itdoors.haccp.model.CompanyObject;
import com.itdoors.haccp.provider.HaccpContract;

public class CompanyObjectsFragment extends SherlockListFragment  implements LoaderManager.LoaderCallbacks<Cursor>{

		private static String COMPANY_ARGS = "com.itdoors.haccp.com.itdoors.haccp.fragments.pointtree.CompanyObjectsFragment.COMPANY_ARGS";
		
		public interface OnCompanyObjectItemPressedListener{
			public void onCompanyObjectPressedListener(CompanyObject companyObject);
		}
		
		public static CompanyObjectsFragment newInstance(Company company){
			CompanyObjectsFragment mFragment = new CompanyObjectsFragment();
			Bundle args = new Bundle();
			args.putSerializable(COMPANY_ARGS, company);
			mFragment.setArguments(args);
			return mFragment;
		}
		
		
		private SimpleCursorAdapter mAdapter;
		private OnCompanyObjectItemPressedListener mOnCompanyObjectItemPressedListener;
		
		@SuppressWarnings("unused")
	    private interface CompanyObjectsQuery{
		    
	    	String[] PROJECTION = new String[]{
	    			HaccpContract.Companies._ID,
		            HaccpContract.CompanyObjects.NAME,
		            HaccpContract.CompanyObjects.UID
		    };
	    	
	    	
			int _ID = 0;
	        int NAME = 1;
	 	    int UID = 2;
	 	   
	    }
	   
	    private static final String[] FROM_COLUMNS = new String[]{
	    	 	HaccpContract.CompanyObjects.NAME,
	    };

	    private static final int[] TO_FIELDS = new int[]{
	        	R.id.comp_obj_name
	    };
	    
	    @Override
	    public void onAttach(Activity activity) {
	    	super.onAttach(activity);
	    	mOnCompanyObjectItemPressedListener = (OnCompanyObjectItemPressedListener)activity;
	    }
	    
	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {
	        super.onViewCreated(view, savedInstanceState);

	        final ListView mListView = getListView();
			mListView.setSelector(R.drawable.abs__tab_indicator_ab_holo);
			mListView.setCacheColorHint(Color.TRANSPARENT);
			
	        mAdapter = new SimpleCursorAdapter( getActivity(), R.layout.list_item_company_obj,
	        		null, FROM_COLUMNS, TO_FIELDS, 0
	        );
	        
	    }

	    @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	    	super.onActivityCreated(savedInstanceState);
	        setListAdapter(mAdapter);
	        getLoaderManager().initLoader(0, null, this);
	    	
	    }
	    @Override
	    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
	    	
	    	if(getArguments() == null) 
	    		return null;
	    	
	    	Company company = (Company)getArguments().getSerializable(COMPANY_ARGS);
	    	return new CursorLoader(getActivity(),  // Context
	        		HaccpContract.CompanyObjects.buildUriForCompanyId(company.getId()), // URI
	                CompanyObjectsQuery.PROJECTION,                // Projection
	                null,                           // Selection
	                null,                           // Selection args
	                HaccpContract.CompanyObjects.DEFAULT_SORT); // Sort
	    
	    }

	    @Override
	    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
	    	if(mAdapter!=null && cursor!=null) {
	    		mAdapter.swapCursor(cursor); //swap the new cursor in.
	        }
	    }

	    @Override
	    public void onLoaderReset(Loader<Cursor> cursorLoader) {
	    	 if(mAdapter!=null) {
	    		 mAdapter.swapCursor(null);
	         }
	    }

	    @Override
	    public void onListItemClick(ListView listView, View view, int position, long id) {
	        super.onListItemClick(listView, view, position, id);
	       
	        if(getArguments() != null && mOnCompanyObjectItemPressedListener != null){
	        
		        Cursor c = (Cursor) mAdapter.getItem(position);
		        int uid = c.getInt(CompanyObjectsQuery.UID);
		        String name = c.getString(CompanyObjectsQuery.NAME);
		        Company company = (Company)getArguments().get(COMPANY_ARGS);
		        
		        CompanyObject companyObject = new CompanyObject(uid, name, company);
		        if(mOnCompanyObjectItemPressedListener != null)
		        	mOnCompanyObjectItemPressedListener.onCompanyObjectPressedListener(companyObject);
		        
	        }
	    }


	
}
