package com.tuitui.tool.http.invoker.constants;

public enum HttpRequestType {

	POST(1),
	FORM(10),
	GET(2),
	PUT(3),
	DELETE(4);

	private final int type;

	private HttpRequestType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
}
