package com.itdoors.haccp.fragments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.itdoors.haccp.R;
import com.itdoors.haccp.utils.ContextUtils;
import com.itdoors.haccp.utils.Logger;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.ListView;

public abstract class  EndlessListFragment extends ListFragment implements AbsListView.OnScrollListener{

	protected static final String STATE_POSITION = "com.itdoors.haccp.fragments.EndlessListFragment.STATE_POSITION";
	protected static final String STATE_TOP = "com.itdoors.haccp.fragments.EndlessListFragment.STATE_TOP";
	protected static final String STREAMING_STATE = "com.itdoors.haccp.fragments.EndlessListFragment.LOADING_STATE";
	
	protected static final String HAS_MORE_ITEMS_TO_LOAD = "com.itdoors.haccp.fragments.EndlessListFragment.HAS_MORE_ITEMS_TO_LOAD";
	protected static final String FIRST_LOAD = "com.itdoors.haccp.fragments.EndlessListFragment.FIRST_LOAD";
	
	
	protected final long DELAY_TIME = 300;
	
	private int mListViewStatePosition;
	private int mListViewStateTop;
	

	Handler mHandler = new Handler();
	
	private View mListViewFootter;
	private View mErrorView;
	private View mLoadingView;
	
	protected BaseAdapter mStreamAdapter;
	protected List<Object> mStream = new ArrayList<Object>();
	
	protected static enum StreamingState {	INIT, LOADING, DONE, ERROR, COMPLETE; }
	
	private StreamingState mStreamingState;
	
	
	
	protected abstract void loadMoreResults();
	
	protected StreamingState getState(){
		return mStreamingState;
	}
	
	protected void setState(StreamingState state){
		
		mStreamingState = state;
		changeFooterState();
		Logger.Logi(getClass(), "Setting streaming state" + state.toString());
	
	}
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setHasOptionsMenu(false);
	    
	    StreamingState state = StreamingState.INIT;
	    if(savedInstanceState != null)	 state = (StreamingState)savedInstanceState.getSerializable(STREAMING_STATE);
	    mStreamingState = state;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        
    	Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mListViewStatePosition = savedInstanceState.getInt(STATE_POSITION, -1);
            mListViewStateTop = savedInstanceState.getInt(STATE_TOP, 0);
        } else {
            mListViewStatePosition = -1;
            mListViewStateTop = 0;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	         View v = getListView().getChildAt(0);
	         int top = (v == null) ? 0 : v.getTop();
	         outState.putInt(STATE_POSITION, getListView().getFirstVisiblePosition());
	         outState.putInt(STATE_TOP, top);
	         outState.putSerializable(STREAMING_STATE, mStreamingState);
	         super.onSaveInstanceState(outState);
	}
	 
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);

        final ListView listView = getListView();
        listView.setSelector(R.drawable.abs__tab_indicator_ab_holo);
        listView.setCacheColorHint(Color.WHITE);
        listView.setOnScrollListener(this);
        //listView.setDrawSelectorOnTop(true);
		listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
    	
		mListViewFootter = (View) getActivity().getLayoutInflater().inflate(
                			R.layout.load_more_or_retry_list_or_complete_item, null, false);
    	mLoadingView = ContextUtils.getLoadingView(getActivity());
  		mErrorView = ContextUtils.getErrorWhileConnectionView(getActivity(), new View.OnClickListener() {
 			@Override
 			public void onClick(View v) {
 				retry();
 			}
  		});
  		
    	Button retryBtn = (Button)mListViewFootter.findViewById(R.id.retry_btn);
        retryBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				retry();
			}
		});
        
        changeFooterState();
        listView.addFooterView(mListViewFootter);
		        
	
		//if(mStreamingState == StreamingState.INIT )
		//	((ViewGroup)view).addView(mLoadingView);
						
		//if(mStreamingState == StreamingState.ERROR && mStream.isEmpty())
		//	((ViewGroup)view).addView(mErrorView);
		
		
    }
    
    private void changeFooterState(){
    	setFooter(mListViewFootter, mStreamingState);
    }
    
    private static void setFooter(View footer, StreamingState state){
    	
    	switch (state) {
			
    		case INIT:
    			footer.setVisibility(View.INVISIBLE);
    			footer.findViewById(R.id.load_more_lo).setVisibility(View.GONE);
				footer.findViewById(R.id.retry_lo).setVisibility(View.GONE);
				footer.findViewById(R.id.complete_lo).setVisibility(View.GONE);
				
				break;
			
			case LOADING:
				
				footer.setVisibility(View.VISIBLE);
				footer.findViewById(R.id.load_more_lo).setVisibility(View.VISIBLE);
				footer.findViewById(R.id.retry_lo).setVisibility(View.GONE);
				footer.findViewById(R.id.complete_lo).setVisibility(View.GONE);
				
				
				break;
			case DONE:
				
				footer.setVisibility(View.VISIBLE);
				footer.findViewById(R.id.load_more_lo).setVisibility(View.VISIBLE);
				footer.findViewById(R.id.retry_lo).setVisibility(View.GONE);
				footer.findViewById(R.id.complete_lo).setVisibility(View.GONE);
			
				break;
			case ERROR:
				
				footer.setVisibility(View.VISIBLE);
				footer.findViewById(R.id.load_more_lo).setVisibility(View.GONE);
				footer.findViewById(R.id.retry_lo).setVisibility(View.VISIBLE);
				footer.findViewById(R.id.complete_lo).setVisibility(View.GONE);
			
				break;
			case COMPLETE:
				
				footer.setVisibility(View.VISIBLE);
				footer.findViewById(R.id.load_more_lo).setVisibility(View.GONE);
				footer.findViewById(R.id.retry_lo).setVisibility(View.GONE);
				footer.findViewById(R.id.complete_lo).setVisibility(View.VISIBLE);
			
				break;
		}
    
    }
    protected void onError(){
    	
    	setState(StreamingState.ERROR);
    	
    	if(getView() != null && mStream.isEmpty()){
    		((ViewGroup)getView()).removeView(mLoadingView);
    		((ViewGroup)getView()).addView(mErrorView);
    	}
    	
			
	}
    
	
	 
	
    protected void onListPackageReady(final List<? extends Object> data){
    	
    	if(data != null){
    		Iterator<? extends Object> iterator = data.iterator();
			while(iterator.hasNext()){
				mStream.add((Object)iterator.next());
			}
    	}
    	
	   if(mStreamingState == StreamingState.COMPLETE){
			
		   //if views exist
			((ViewGroup)getView()).removeView(mErrorView);
			((ViewGroup)getView()).removeView(mLoadingView);
				
				
			//toastMsg(R.string.no_more_items_to_load);
		 	if (mListViewStatePosition != -1 ) {
	         	getListView().setSelectionFromTop(mListViewStatePosition, mListViewStateTop);
	         	mListViewStatePosition = -1;
	     	}
		 	mStreamAdapter.notifyDataSetChanged();

	   }
	   else{
		  
		   mStreamAdapter.notifyDataSetChanged();
		   if(isAdded())
			   ((ViewGroup)getView()).removeView(mLoadingView);
	   }
	}
	
	public void load(){
		
		setState(StreamingState.LOADING);
		loadMoreResults();
	}
	
    public void refresh() {
    	
    	setState(StreamingState.LOADING);
        mStream.clear();
        mStreamAdapter.notifyDataSetInvalidated();
        loadMoreResults();
    }

      

      private boolean streamHasMoreResults() {
    	  return  mStreamingState != StreamingState.COMPLETE && mStreamingState != StreamingState.INIT;
      }
      
	  
	  private void retry(){
		  
		  if(isAdded()){
			  if(mStream.isEmpty()){
						((ViewGroup)getView()).removeView(mErrorView);
						((ViewGroup)getView()).addView(mLoadingView);
			  }
		  }
		  
		  load();
			
	  }
	  

	  @Override
	  public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		   
		  if (visibleItemCount != 0 && firstVisibleItem + visibleItemCount >= totalItemCount && streamHasMoreResults()) {
	            loadMoreResults();
	       }
	  }

	  @Override
	  public void onScrollStateChanged(AbsListView listView, int scrollState) {
	  }
	  
	  

}

