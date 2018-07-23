package cagtc.pay.job.support.job;

import java.text.ParseException;

import org.osgi.framework.BundleContext;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.triggers.CronTriggerImpl;

import cagtc.pay.job.constant.Constant;
import cagtc.pay.job.utils.UUIDUtil;
import cagtc.pay.om.Stpjobservice;

/**
 * 作业明细
 * @author Administrator
 *
 */
public class MyJobDetail {
	
	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	private String jobId;//ID
	
	private  String jobName;  //作业名称
	
	private  String groupName;//组名

	private String tiggerTime;//触发时间
	
	private JobDataMap dataMap;//数据
	
	private Job job;
	
	private JobKey jobKey;
	
	private CronTriggerImpl trigger;
	
	private Scheduler sched;

	public CronTriggerImpl getTrigger() {
		return trigger;
	}

	public void setTrigger(CronTriggerImpl trigger) {
		this.trigger = trigger;
	}

	public Scheduler getSched() {
		return sched;
	}

	public void setSched(Scheduler sched) {
		this.sched = sched;
	}

	public MyJobDetail(Stpjobservice s,BundleContext context){
		this.jobId = s.getJobid();
		this.jobName = s.getJobname();
		this.groupName = s.getGroupname();
		this.tiggerTime = s.getTiggertime();
		this.dataMap = new JobDataMap();
		this.dataMap.put("data", "{'ID':'"+this.jobId+"','serviceId':'"+s.getServiceid()+"'}");
		this.job = new TriggerJob(context);
		this.trigger = new CronTriggerImpl(s.getJobname(),s.getGroupname());
		try {
			this.trigger.setCronExpression(this.tiggerTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public MyJobDetail(String jobId,String jobName,String groupName,
			String tiggerTime,JobDataMap dataMap){
		this.jobId = jobId;
		this.jobName = jobName;
		this.groupName = groupName;
		this.tiggerTime = tiggerTime;
		this.dataMap = dataMap;
		this.job = new TriggerJob();
		this.trigger = new CronTriggerImpl(jobName,groupName);
		try {
			this.trigger.setCronExpression(tiggerTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public MyJobDetail(String jobName,
			String tiggerTime,JobDataMap dataMap){
		
		this.jobId = UUIDUtil.getUUID();
		this.jobName = jobName;
		this.groupName = Constant.JOB_GROUP_NAME;
		this.tiggerTime = tiggerTime;
		this.dataMap = dataMap;
		this.job = new TriggerJob();
		this.trigger = new CronTriggerImpl(jobName,groupName);
		try {
			this.trigger.setCronExpression(tiggerTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public MyJobDetail(String jobName,
			String tiggerTime,JobDataMap dataMap,Job job){
		
		this.jobId = UUIDUtil.getUUID();
		this.jobName = jobName;
		this.groupName = Constant.JOB_GROUP_NAME;
		this.tiggerTime = tiggerTime;
		this.dataMap = dataMap;
		this.job = job;
		this.trigger = new CronTriggerImpl(jobName,groupName);
		try {
			this.trigger.setCronExpression(tiggerTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public String getJobId() {
		return jobId;
	}
	
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getTiggerTime() {
		return tiggerTime;
	}

	public void setTiggerTime(String tiggerTime) {
		this.tiggerTime = tiggerTime;
	}

	public JobDataMap getDataMap() {
		return dataMap;
	}

	public void setDataMap(JobDataMap dataMap) {
		this.dataMap = dataMap;
	}

	public JobKey getJobKey() {
		return jobKey;
	}

	public void setJobKey(JobKey jobKey) {
		this.jobKey = jobKey;
	}
	
	
	
}

