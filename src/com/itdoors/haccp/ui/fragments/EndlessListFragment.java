package com.itdoors.haccp.ui.fragments;

import com.itdoors.haccp.R;
import com.itdoors.haccp.utils.Logger;

import android.os.Bundle;

import android.view.View;
import android.widget.AbsListView;

import android.widget.ListView;

public abstract class  EndlessListFragment extends SwipeRefreshListFragment implements AbsListView.OnScrollListener{

	protected static final String STREAMING_STATE 			= "com.itdoors.haccp.fragments.EndlessListFragment.LOADING_STATE";
	protected static final String HAS_MORE_ITEMS_TO_LOAD 	= "com.itdoors.haccp.fragments.EndlessListFragment.HAS_MORE_ITEMS_TO_LOAD";
	protected static final String FIRST_LOAD 			 	= "com.itdoors.haccp.fragments.EndlessListFragment.FIRST_LOAD";
	
	protected final long DELAY_TIME = 300;
	
	private View mListViewFootter;
	
	protected static enum StreamingState {	
		INIT, LOADING, REPEAT, DONE, ERROR, COMPLETE; 
	}
	
	private StreamingState mStreamingState;
	
	protected abstract void loadMoreResults();
	
	protected StreamingState getStreamingState(){
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
	    if(savedInstanceState != null)
	    	state = (StreamingState)savedInstanceState.getSerializable(STREAMING_STATE);
	    mStreamingState = state;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	   outState.putSerializable(STREAMING_STATE, mStreamingState);
	   super.onSaveInstanceState(outState);
	}
	
	 
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        mListViewFootter = (View) getActivity().getLayoutInflater().inflate(
			R.layout.list_item_load_more_or_retry_or_complete, null, false);
  		mListViewFootter.findViewById(R.id.retry_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				load();
			}
		});
        
  		changeFooterState();
        
        final ListView listView = getListView();
        listView.setOnScrollListener(this);
        listView.addFooterView(mListViewFootter, null, false);
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
				
			case REPEAT:
				
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
	}
 	
	public void load(){
		setState(StreamingState.LOADING);
		loadMoreResults();
	}
	
    private boolean streamHasMoreResults() {
    	return  mStreamingState == StreamingState.DONE;
    }
    
	@Override
	public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	  if (visibleItemCount != 0 && firstVisibleItem + visibleItemCount >= totalItemCount && streamHasMoreResults()) {
		  Logger.Logi(getClass(), "try load More");
	      load();
	  }
	}
	@Override
	public void onScrollStateChanged(AbsListView listView, int scrollState) {}

}

