package cagtc.pay.job.support.job;

import java.text.ParseException;

import org.osgi.framework.BundleContext;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cagtc.pay.core.NodeConfig;
import cagtc.pay.core.common.Constants;
import cagtc.pay.core.common.InPacket;
import cagtc.pay.core.common.OutPacket;
import cagtc.pay.core.common.SystemContext;
import cagtc.pay.core.kafka.MessageBuffer;
import cagtc.pay.core.log.IDailyLog;
import cagtc.pay.core.log.Log;
import cagtc.pay.core.log.LogFactory;
import cagtc.pay.core.log.PayLogger;
import cagtc.pay.core.log.TdLogger;
import cagtc.pay.core.log.TdLoggerLevel;
import cagtc.pay.core.util.DateUtil;
import cagtc.pay.core.util.JsonUtil;
import cagtc.pay.core.util.ServiceUtil;
import cagtc.pay.core.util.SqnUtil;
import cagtc.pay.job.Activator;
import cagtc.pay.om.ServiceRecord;

import com.alibaba.fastjson.JSONObject;
import com.coreframework.util.DateOper;

/**
 * 触发执行类
 * @author Administrator
 *
 */ 
public class TriggerJob implements Job {  

	private static final PayLogger log = PayLogger.getInstance(Activator.bundleName,TriggerJob.class);
	
	public static IDailyLog dailyLogger;
	
	public static BundleContext context;

	public TriggerJob(BundleContext context) {
		super();
		this.context =  context;
		if(0==NodeConfig.logModel){ //判断是否本地记录日志文件
			try{
			LogFactory fogFactory = (LogFactory)ServiceUtil.getService(context, LogFactory.class, "("+Constants.SERVICE_ID+"=pay.log.factory)");
			dailyLogger = 	fogFactory.createDailyLog("Remote", "INFO");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public TriggerJob() {
		super();
		
	}





	@Override
	public void execute(JobExecutionContext context) 
			throws JobExecutionException {
		long nanoStartTime = System.nanoTime();
		InPacket in = null;
		OutPacket out = null;
		String serviceId =null;
		try {
			JobDataMap d = context.getMergedJobDataMap();
			JSONObject data = JSONObject.parseObject(d.get("data").toString());
			in = new InPacket(data);
			serviceId = data.getString("serviceId");
			if(serviceId.equals("810001")){
				String AcDate = DateOper.addDate(DateOper.getNow("yyyyMMdd"), -1);
				data.put("AcDate", AcDate);
			}
			TdLogger logger;
			if(0==NodeConfig.logModel){ //判断是否本地记录日志文件
				String trcName = String.format("Scheduler%013d_%s", SqnUtil.getSqn("remote"),serviceId);
				Log.seed(dailyLogger.createMsgLogger(trcName, "INFO"));
				logger = (TdLogger) SystemContext.getLog();
				logger.setLogLevel("INFO");
			}else{
				logger = new TdLogger(TdLoggerLevel.INFO);
				Log.seed(logger);
			}
			Log.info("---------定时任务执行开始");
			Log.info("in:"+data.toString());
			MessageBuffer.add(MessageBuffer.BUF_JOBSR, data);
			out = new OutPacket();
			out.setCode(1);
			out.set("desc", "定时任务调用成功……");
			Log.info("---------定时任务往消息总线添加完毕");
		} 
		catch (Exception e) {
			log.exceptionLog(e);
			out = new OutPacket(-1,"[catgc.pay]"+e.getMessage()).set("exception", e);
			Log.error(e);
			Log.info("---------定时任务往消息总线添加完毕");
			e.printStackTrace();
		}finally{
			if(NodeConfig.saveServiceRecord==1){
				long nanoEndTime = System.nanoTime();
				ServiceRecord record = new ServiceRecord();
				record.setInvorkDate(DateUtil.getNowDate("yyyyMMdd"));
				record.setInvorkTime(DateUtil.getNow("HH:mm:ss:SSS"));
				record.setResponseTime(((float)(nanoEndTime-nanoStartTime))/1000000000);
				record.setIslocal(Constants.LOCAL_SERVICE);
				record.setLoginid(in.getString("loginid",""));
				record.setInvorkType("3");
				if(in!=null)
					record.setInParam(in.toString());
				if(out!=null){
					record.setOutParam(out.toString());
					record.setServiceName("定时任务");
				}	
				record.setServiceId(serviceId);
				record.setIpAddress(NodeConfig.nodeIP);
				record.setNodeId("node"+NodeConfig.nodeId);
				TdLogger logger = (TdLogger) SystemContext.getLog();
				StringBuffer logMsgs =  logger.getSb();
				if(logMsgs!=null)
					record.setLogs(logger.getSb().toString());
				JSONObject srObj = null;
				try {
					srObj = JsonUtil.bean2jsonWithAnnotation(record);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//srLog.debug(srObj);
				MessageBuffer.add(MessageBuffer.BUF_SRE, srObj);
				srObj = null;
				record = null;
				in = null;
			}
			
		
			SystemContext.removeLog();
		}
	}  
	
	
}
