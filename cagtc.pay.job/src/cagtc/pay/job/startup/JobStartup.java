package cagtc.pay.job.startup;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;

import cagtc.pay.core.common.Constants;
import cagtc.pay.core.log.PayLogger;
import cagtc.pay.core.startup.IStartup;
import cagtc.pay.job.Activator;
import cagtc.pay.job.constant.Constant;
import cagtc.pay.job.support.job.JobManage;
import cagtc.pay.job.support.job.MySchedulerFactory;
import cagtc.pay.job.support.job.TriggerJob;



public class JobStartup implements IStartup {
	
	private static final PayLogger log = PayLogger.getInstance(Activator.bundleName,TriggerJob.class);

	private JobManage jobManage = null;
	
	@Override
	public void start(BundleContext context) throws Exception {
		 
		jobManage = JobManage.getInstant(Constant.MAX_ACTIVE_LIMIT);
		Dictionary<String,String> dictionary = new Hashtable<String,String>(); 
		dictionary.put(Constants.SERVICE_ID, Activator.bundleName);
		context.registerService(JobManage.class.getName(), jobManage, dictionary);
		jobManage.mySchedulerFactory = MySchedulerFactory.getMySchedulerFactory(context);
		
		//初始化定时作业
		JobManage.initJob(context);
		log.info("register job_pool");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		JobManage.removeALLJob();
		jobManage = null;
		log.info("stop job_pool");
	}

}
