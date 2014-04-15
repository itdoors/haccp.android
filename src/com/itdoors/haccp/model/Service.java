package com.itdoors.haccp.model;

import com.itdoors.haccp.R;

import android.content.Context;
import android.content.res.Resources;

public enum Service {
	
	DERATISATION, DESINSECTUM;
	
	@Override
	public String toString() {
		switch (this) {
			case DERATISATION:
				return "deratisation";
			case DESINSECTUM:
				return "desinsectum";
		}
		
		return super.toString();
	}
	
	public String toString(Context context){
		
		Resources res = context.getResources();
		switch (this) {
			case DERATISATION:
				return res.getString(R.string.deratisation);
		
			case DESINSECTUM:
				return res.getString(R.string.desinsectum);
		}
		return null;
	}
}
