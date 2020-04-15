package com.jackiifilwhh.services;

import com.alibaba.fastjson.annotation.JSONField;

public class Line {
	@JSONField(name = "text")
	private String text;
	// 0 不变 1增加 2减少
	@JSONField(name = "status")
	private int status;
	@JSONField(name = "srcRow")
	private Integer srcRow;
	@JSONField(name = "dstRow")
	private Integer dstRow;

	public Line() {
	}

	public Line(String text, int status, Integer srcRow, Integer dstRow) {
		this.text = text;
		this.status = status;
		if (srcRow != null)
			this.srcRow = srcRow;
		else
			this.srcRow = 0;
		if (dstRow != null)
			this.dstRow = dstRow;
		else
			this.dstRow = 0;
	}

	public int getSrcRow() {
		return srcRow;
	}

	public void setSrcRow(Integer srcRow) {
		this.srcRow = srcRow;
	}

	public int getDstRow() {
		return dstRow;
	}

	public void setDstRow(Integer dstRow) {
		this.dstRow = dstRow;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
