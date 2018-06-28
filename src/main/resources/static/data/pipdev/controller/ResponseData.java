package com.qian.xm.pipdev.controller;

import java.util.ArrayList;
import java.util.List;

public class ResponseData<T> {

	private int total;
	private List<T> data = new ArrayList<T>();
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data.addAll(data);
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
}
