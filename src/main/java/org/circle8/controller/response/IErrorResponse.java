package org.circle8.controller.response;

public interface IErrorResponse {
	ErrorCode code();
	String message();
	String dev();
}
