package com.module.core.models.topic;

public class Like{

	public enum Status{
		LIKED,
		NOT_LIKED
	}
	
	private int likeQty;
	private Status status;

	public Like(int likeQty, Status status) {
		super();
		this.likeQty = likeQty;
		this.status = status;
	}

	public Like() {
		super();
	}

	public int getLikeQty() {
		return likeQty;
	}

	public void setLikeQty(int likeQty) {
		this.likeQty = likeQty;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
}
