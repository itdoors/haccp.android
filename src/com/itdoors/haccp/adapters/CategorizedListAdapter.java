package com.itdoors.haccp.adapters;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.widget.BaseAdapter;

@SuppressLint("UseSparseArrays")
public abstract class CategorizedListAdapter<T, V> extends BaseAdapter{
	
	public static final int TYPE_ITEM = 0;
	public static final int TYPE_SECTION = 1;
	
	private HashMap<Integer, Integer> itemTypes;
	
	private HashMap<Integer, T> items;
	private HashMap<Integer, V> sections;
	
	
	
	{
		itemTypes = new HashMap<Integer, Integer>();
		items = new HashMap<Integer, T>();
		sections = new HashMap<Integer, V>();
	}
	
	
	public void addItem(T item){
		
		int index = getCount();
		
		itemTypes.put(index, TYPE_ITEM);
		items.put(index, item);
		
		notifyDataSetChanged();
	}
	
	
	public void addSection(V item){
		
		int index = getCount();
		
		itemTypes.put(index, TYPE_SECTION);
		sections.put(index, item);
		
		notifyDataSetChanged();
	
	}
	
	@Override
	public int getCount() {
		return sections.size() + items.size();
	}

	@Override
	public Object getItem(int position) {
		int type = getItemViewType(position);
		if(type == TYPE_ITEM)	
			return items.get(position);
		else
			return sections.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getItemViewType(int position) {
		return itemTypes.get(position);
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
}