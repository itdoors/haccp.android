package com.itdoors.haccp.model.enums;

import com.itdoors.haccp.R;

public enum CP_TYPE {
	
	GROUNBAIT_BOX, 			       // 1. pricormochnuy yashchik, 
	TRAP, 		   			       // 2. lovushka, 
	INSECTICIDAL_LAMP_ACTIVE, 	   // 3. insecticidnaya lampa aktivanaya, 
	INSECTICIDAL_LAMP_PASSIVE,     // 4. insecticidnaya lampa passivnaya, 
	THERMOMETER,                   // 5. gradusnik, 
	MONITORING_PLATFOMR_FLYING,    // 6. monitoringovaya ploshchadka letayuschaya,
	MONITORING_PLATFOMR_CRAWLING;  // 7. monitoringovaya ploshchadka polsayuschaya
	
	
	public int getStringResorceID(){
		
		int id = R.string.cp_type_grounbait_box;
		
		switch (this) {
			case GROUNBAIT_BOX:
				id = R.string.cp_type_grounbait_box;
				break;
			case TRAP:
				break;
			case INSECTICIDAL_LAMP_ACTIVE:
				id = R.string.cp_type_insecticidal_lamp_active;
				break;
			case INSECTICIDAL_LAMP_PASSIVE:
				id = R.string.cp_type_insecticidal_lamp_passive;
				break;
			case THERMOMETER:
				id = R.string.cp_type_thermometer;
				break;
			case MONITORING_PLATFOMR_FLYING:
				id = R.string.cp_type_monitoring_platform_flying;
				break;
			case MONITORING_PLATFOMR_CRAWLING:
				id = R.string.cp_type_monitoring_platform_crawling;
				break;
		}
		
		return id;
	}
	
	
}
