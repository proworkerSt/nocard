package cagtc.pay.job.exception;

import cagtc.pay.job.constant.RetCode;

public class JobException extends Exception{


	private RetCode code;
	private Exception e;
	public RetCode getRetCode() {
		return code;
	}
	
	public Exception getException() {
		return e;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -4953101753824966168L;

	public JobException(String message) {
		super(message);
	}
	
	public JobException(RetCode code,String message) {
		super(message);
		this.code = code;
	}
	
	public JobException() {
		super(RetCode.Common.ERROR_PARAM.getMessage());
		this.code = RetCode.Common.ERROR_PARAM;
	}

	public JobException(RetCode code) {
		super(code.getMessage());
		this.code = code;
	}
	
	public JobException(Exception e,String message) {
		super(message);
		this.e = e;
	}
	
	@Override
	public Throwable fillInStackTrace() {
		return this;
	}
	
}
