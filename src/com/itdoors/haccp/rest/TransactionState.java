package com.itdoors.haccp.rest;

public enum TransactionState {
	
	PENDING, RETRY, IN_PROGRESS, COMPLETE;
	
	public static final int TRANSACTION_PENDING = 0;
	public static final int TRANSACTION_RETRY = 1;
	public static final int TRANSACTION_IN_PROGRESS = 2;
	public static final int TRANSACTION_COMPLETED = 3;
	
	public static TransactionState valueOf(int code){
		
		switch (code) {
			case TRANSACTION_PENDING:
				return PENDING;
			case TRANSACTION_RETRY:
				return RETRY;
			case TRANSACTION_IN_PROGRESS:
				return IN_PROGRESS;
			case TRANSACTION_COMPLETED:
				return COMPLETE;
			default:
				throw new IllegalArgumentException("Wrong transactional state code: "  + code );
		}
	}
}
