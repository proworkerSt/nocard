package cagtc.pay.job.support.job;

import java.text.ParseException;
import java.util.List;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;

import cagtc.pay.core.log.PayLogger;
import cagtc.pay.job.Activator;
 
/**
 * 作业管理器
 * @author Administrator
 *
 */
public class QuartzManage {
	
	private static final PayLogger log = PayLogger.getInstance(Activator.bundleName,QuartzManage.class);


	/**
	 * 调度工厂
	 */
//	private static SchedulerFactory sf = new StdSchedulerFactory();  
//	private static MySchedulerFactory sf = new MySchedulerFactory();  
    
	/** *//** 
    *  添加一个定时任务，使用默认的任务组名，触发器名，触发器组名 
    * @param jobName 任务名 
    * @param job     任务 
    * @param time    时间设置，参考quartz说明文档 
    * @throws SchedulerException 
    * @throws ParseException 
    */  
	public static void  addJob(MyJobDetail detail)throws SchedulerException{  
       Scheduler sched = JobManage.mySchedulerFactory.getScheduler();  
       detail.setSched(sched);
       //任务名，任务组，任务执行类  
       JobDetailImpl jobDetail = new JobDetailImpl(
    		   detail.getJobName(), detail.getGroupName(), detail.getJob().getClass());
       detail.setJobKey(jobDetail.getKey());
       //执行作业的业务数据
       jobDetail.setJobDataMap(detail.getDataMap());
       try {
    	   sched.scheduleJob(jobDetail, detail.getTrigger());
       }catch (ObjectAlreadyExistsException e) {
			log.info("该任务已经在数据库中存在,无需重新添加……");
			//log.exceptionLog(e);
		}
       //启动  
       if(!sched.isShutdown()){
    	   sched.start();  
       }
	}  
     
	/** *//** 
	 * 修改一个任务的触发时间 
	 * @param triggerName 
	 * @param triggerGroupName 
	 * @param time 
	 * @throws SchedulerException 
	 * @throws ParseException 
  
	public static void modifyJobTime(MyJobDetail detail)   
				   throws SchedulerException, ParseException{  
       Scheduler sched = sf.getScheduler();  
       Trigger trigger =  sched.getTrigger(
    		   detail.getJobName(),detail.getGroupName());  
       if(trigger != null){  
           CronTrigger  ct = (CronTrigger)trigger;  
           //修改时间  
           ct.setCronExpression(detail.getTiggerTime());  
           //重启触发器  
           sched.resumeTrigger(detail.getJobName(),detail.getGroupName());
       }
   }    */  
     
   /** *//** 
    * 移除一个任务(使用默认的任务组名，触发器名，触发器组名) 
    * @param jobName 
    * @throws SchedulerException 
    */  
   public static void removeJob(MyJobDetail detail)throws SchedulerException{  
       Scheduler sched = JobManage.mySchedulerFactory.getScheduler();  
       JobKey jobKey =  detail.getJobKey();
      /* sched.pauseTrigger(detail.getJobName(),detail.getGroupName());//停止触发器  
       sched.unscheduleJob(detail.getJobName(),detail.getGroupName());//移除触发器  
       sched.deleteJob(detail.getJobName(),detail.getGroupName());//删除任务  
*/  
       List<? extends Trigger> triggers = sched.getTriggersOfJob(jobKey);
       for(Trigger trigger : triggers) {
    	   sched.pauseTrigger(trigger.getKey());  //停止触发器
    	   sched.unscheduleJob(trigger.getKey()); //移除触发器
       } 
       sched.deleteJob(jobKey);          
   }  
   
	
}
