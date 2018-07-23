package cagtc.pay.job.constant;


public interface RetCode {
	
	String getMessage();
	Integer getCode();
	
	/**
	 * 调用接口返回代码定义
	* @author linxy
	* @date 2015-9-16 上午10:00:51 
	 */
	enum Common implements RetCode  {
		
		/**
		 * 业务逻辑公共返回 Code 
		 */
		RET_10000(10000,"success"),
		
		/**
		 * 异常
		 */
		RET_ERROR(-14444,"error"),
		
		/**
		 * -20000 参数错误
		 */
		ERROR_PARAM(-20000,"param_error"),
		
		/**
		 * 解析日期类型数据出错"
		 */
		ParseDate(-14001, "date_format_error"),
		
		/**
		 * 作业已达到上限
		 */
		MAX_LIMIT(-14002, "job is max limit,plase way a moment..."),
	 
		/**
		 * 失效的触发时间
		 */
		FAILD_TIGGER_TIME(-14003, "tigger_time_error"),
 
		/**
		 * JOB已被覆盖
		 */
		COVER_OBJECT(-14004, "job_cover"),
		
		;
		/** 代码 */
		private Integer code;
		/** 消息 */
		private String message;
		
		public String getMessage() {
			return message;
		}
		Common(Integer code,String message) {
			this.code = code;
			this.message = message;
		}
		public Integer getCode() {
			return code;
		}
	}
	
	
}
