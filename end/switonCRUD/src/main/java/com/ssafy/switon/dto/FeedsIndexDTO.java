package com.ssafy.switon.dto;

public class FeedsIndexDTO {
	
	int user_id;
	int start_idx;
	int amount;
	
	
	public int getUserId() {
		return user_id;
	}
	public void setUserId(int userId) {
		this.user_id = userId;
	}
	public int getStartIdx() {
		return start_idx;
	}
	public void setStartIdx(int startIdx) {
		this.start_idx = startIdx;
	}
	public int getEndIdx() {
		return amount;
	}
	public void setEndIdx(int endIdx) {
		this.amount = endIdx;
	}
	public FeedsIndexDTO(int userId, int startIdx, int endIdx) {
		this.user_id = userId;
		this.start_idx = startIdx;
		this.amount = endIdx;
	}
	@Override
	public String toString() {
		return "FeedsIndexDTO [userId=" + user_id + ", startIdx=" + start_idx + ", endIdx=" + amount + "]";
	}
}
