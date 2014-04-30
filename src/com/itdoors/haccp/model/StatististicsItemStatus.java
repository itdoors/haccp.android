package com.itdoors.haccp.model;

import com.itdoors.haccp.R;

public enum StatististicsItemStatus {
	WARNING, APPROVED, DANGER;
	
	public int getStringResourceID(){
		
		int id = R.string.cp_statistics_type_approved;
		switch (this) {
			case WARNING:
				id = R.string.cp_statistics_type_warning;
			break;
			case APPROVED:
				id = R.string.cp_statistics_type_approved;
			break;
			case DANGER:
				id = R.string.cp_statistics_type_danger;
			break;
		}
		return id;
	}
}
