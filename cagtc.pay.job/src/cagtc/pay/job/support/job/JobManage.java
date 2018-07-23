package cagtc.pay.job.support.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coreframework.util.DateOper;

import cagtc.pay.core.log.PayLogger;
import cagtc.pay.job.Activator;
import cagtc.pay.job.constant.RetCode;
import cagtc.pay.job.dao.JobDao;
import cagtc.pay.job.exception.JobException;
import cagtc.pay.om.Stpjobservice;
 
/**
 * 作业管理
 * @author Administrator
 *
 */
public class JobManage{

	private static final PayLogger log = PayLogger.getInstance(Activator.bundleName,JobManage.class);
	
	public static List<Map<String,MyJobDetail>> job_pool = null;
	
	private static int MAX_ACTIVE = 10;
	
	private static JobManage job = null;
	
	private static JobDao jobDao = null;
	
	public static MySchedulerFactory mySchedulerFactory = null;
	
	private JobManage(int maxActive){
		job_pool = new ArrayList<Map<String,MyJobDetail>>();
		this.MAX_ACTIVE = maxActive;
	}
	
	public static int getMaxActive(){
		return MAX_ACTIVE;
	}
	
	public static int getCurrAcCount(){
		return job_pool.size();
	}
	
	public static JSONArray  getJobs()throws Exception{
		JSONArray arr = new JSONArray();
		if(null == job_pool || job_pool.size() == 0){
			return arr;
		}
		for (Map<String,MyJobDetail> map :job_pool) {
			Set<String> keys = map.keySet();
			String key = keys.iterator().next();
			MyJobDetail obj = map.get(key);
			Trigger trigger = obj.getSched().getTriggersOfJob(obj.getJobKey()).get(0);
			TriggerState state = obj.getSched().getTriggerState(trigger.getKey());
			
			JSONObject json = new JSONObject();
			json.put("jobName", obj.getJobName());
			json.put("name", state.name());
			json.put("nextTime", DateOper.formatDate(
					trigger.getNextFireTime(), "yyyy-MM-dd HH:mm:ss"));
			arr.add(json);
		}
		return arr;
	}
	
	public static JobManage getInstant(int maxActive){
		if(null == job){
			job = new JobManage(maxActive);  
		}
		return job;
	}
	
	public static void addJob(Stpjobservice s,BundleContext context)throws JobException{
		addJob(new MyJobDetail(s,context));
	}
	
	
	/**
	 * 添加
	 * @param job
	 * @throws BusinessException
	 */
	public static void addJob(MyJobDetail job)throws JobException{
	
		String id = job.getJobId();
		for (Map<String,MyJobDetail> map : job_pool ) {
			if(map.containsKey(id)){
				MyJobDetail obj = (MyJobDetail)map.get(id);
				try {
					QuartzManage.removeJob(job);
					removeJob(obj);//
					break;
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			}
		}
		if(job_pool.size() == MAX_ACTIVE){//超过数量
			throw new JobException(RetCode.Common.MAX_LIMIT);
		}
//		else if(!isFaildTiggerTime(job.getTiggerTime())){//
//			throw new JobException(RetCode.Common.FAILD_TIGGER_TIME);
//		}
		else{
			try {
				QuartzManage.addJob(job);
				Map<String,MyJobDetail> obj = new HashMap<String, MyJobDetail>();
				obj.put(id, job);
				job_pool.add(obj);
			}catch (ObjectAlreadyExistsException e) {
				log.info("该任务已经在数据库中存在,无需重新添加……");
			    e.printStackTrace();
			}
			catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ��ȡjob
	* @Title: getCustomJobById 
	* @Description: TODO() 
	* @param id
	* @return CustomJob   
	* @throws
	 */
	public static MyJobDetail getCustomJobById(String id){
		for (Map<String,MyJobDetail> map : job_pool) {
			if(null != map.get(id)){
				return (MyJobDetail)map.get(id);
			}
		}
		return null;
	}
	
	/**
	 * �Ƴ���ҵ
	 * @param job
	 */
	public static void removeJobById(String jobId){
		MyJobDetail job = getCustomJobById(jobId);
		if(null != job){
			removeJob(job);
		}
	}
	
	/**
	 * 移除
	 * @param job
	 */
	public static void removeJob(MyJobDetail job){
		String id = job.getJobId();
		//job
		try {
			QuartzManage.removeJob(job);
			//
			for (Map<String,MyJobDetail> map : job_pool ) {
				if(map.containsKey(id)){
					job_pool.remove(map);
					break;
				}
			}
			//LOG
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 移除全部
	 * @param job
	 */
	public static void removeALLJob(){
		
		for (Map<String,MyJobDetail> map : job_pool) {
			Set<String> keys = map.keySet();
			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
				String key = iterator.next();
				MyJobDetail job = map.get(key);
				try {
					String id = job.getJobId();
					QuartzManage.removeJob(job);
					log.info("作业：["+id+"] 被移除 ");
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 失效时间
	 * @param tiggerTime
	 * @return
	 */
	public static boolean isFaildTiggerTime(String tiggerTime){
		
		if(null== tiggerTime || "".equals(tiggerTime)){
			return false;
		}
		try {
			String format = "yyyy-MM-dd HH:mm:ss";
			String currentTime = DateOper.getNow(format);
			tiggerTime = DateOper.formatDate(tiggerTime, format, format);
			if(tiggerTime.compareTo(currentTime) > 0){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public static void initNewJob(MyJobDetail detail,BundleContext context
			,String id,String serviceId,String timer)throws Exception{
		
		//初始化一个定时作业
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("context", context);
		dataMap.put("data", "{'ID':'"+id+"','serviceId':'"+serviceId+"'}");
		detail = new MyJobDetail(id,timer, dataMap);
		JobManage.addJob(detail);	
	}
	
	//初始化 job
	public static void initJob(BundleContext context)throws Exception{
		
		//清风报表
		//instantDetail("JOB_QFBB", "00 50 00 * * ?","810001",context);
		//提现转账
		//instantDetail("JOB_TXZZ", "0 0/15 * * * ?","500015",context);
		//重复支付异常信息作业
		//instantDetail("JOB_CFZF", "00 50 00 * * ?","500015",context);
		//微信退款
		//instantDetail("JOB_WXTK", "0/10 * * * * ?","WXRefundJob",context);
		jobDao = new JobDao();
		jobDao.init(context);
		List<Stpjobservice> list = jobDao.getJobList();
		if(null != list && list.size()> 0){
			for (Stpjobservice s : list) {
				if(s.getStatus().equals("1")){
					instantDetail(s,context);
				}
			}
		}
	}
	
	public static MyJobDetail instantDetail(Stpjobservice s,BundleContext context)throws Exception{
		MyJobDetail job = new MyJobDetail(s,context);
		JobManage.addJob(job);
		return job;
	}
	
	public static MyJobDetail instantDetail(String jobId,String timer,
			String serviceId,BundleContext context)throws Exception{
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("data", "{'ID':'"+jobId+"','serviceId':'"+serviceId+"'}");
		MyJobDetail job = new MyJobDetail(jobId,timer,dataMap);
		JobManage.addJob(job);
		return job;
	}
	
	
	
	
	public static void main(String[] args) throws Exception{
		
//		String jobName = "jobName1111";
//		String tiggerTime = "0/2 * * * * ?";
//		JobDataMap dataMap = new JobDataMap();
//		dataMap.put("data", "{'code':'0000','message':'aaaaa'}");
//		TriggerJob job = new TriggerJob();
//		
//		MyJobDetail detail = new MyJobDetail(jobName, tiggerTime, dataMap, job);
//		QuartzManage.addJob(detail);
		
		
	}
	
}
