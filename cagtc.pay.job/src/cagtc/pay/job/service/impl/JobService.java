package cagtc.pay.job.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.osgi.framework.BundleContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coreframework.util.DateOper;

import cagtc.pay.core.common.Constants;
import cagtc.pay.core.common.InPacket;
import cagtc.pay.core.common.OutPacket;
import cagtc.pay.core.common.support.CommonService;
import cagtc.pay.core.log.PayLogger;
import cagtc.pay.core.util.JsonUtil;
import cagtc.pay.core.util.ServiceUtil;
import cagtc.pay.core.util.annotation.Service;
import cagtc.pay.core.util.annotation.ServiceNamed;
import cagtc.pay.job.Activator;
import cagtc.pay.job.constant.Constant;
import cagtc.pay.job.constant.JobConstants_ErrorCode;
import cagtc.pay.job.dao.JobDao;
import cagtc.pay.job.service.IJobService;
import cagtc.pay.job.support.job.JobManage;
import cagtc.pay.job.utils.UUIDUtil;
import cagtc.pay.om.Stpjobservice;

/**
 * job服务
 * @author Administrator
 *
 */
@Service
public class JobService extends CommonService implements IJobService{
	
	private static final PayLogger log = PayLogger.getInstance(Activator.bundleName, JobService.class);
	
	private JobManage jobManage = null;
	
	private JobDao dao;
	
	@Override
	public void init(BundleContext context) {
		try {
			super.init(context);
			jobManage = (JobManage)ServiceUtil.getService(context, JobManage.class, "("+Constants.SERVICE_ID+"="+Activator.bundleName+")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询当前定时器
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	@ServiceNamed(id="600001",name="查询当前定时器")
	public OutPacket queryCurrentRunJobs(InPacket inParam)throws Exception{
		OutPacket result  = new OutPacket();
		List<Stpjobservice> list = dao.getJobList();
		JSONArray records = new JSONArray();
		JSONArray record1 = JobManage.getJobs();
    	if(record1.size() > 0){
    		for(Stpjobservice ord : list){
    			JSONObject j =  JsonUtil.bean2jsonWithAnnotation(ord);
    			for (int i = 0; i < record1.size(); i++) {
    				String jobName = record1.getJSONObject(i).getString("jobName");
    				String name = record1.getJSONObject(i).getString("name");
    				String nextTime = record1.getJSONObject(i).getString("nextTime");
    				if(ord.getJobname().equals(jobName)){
    					j.put("name", name);
    					j.put("nextTime", nextTime);
    				}
    			}
    			records.add(j);
        	}
    	}else{
    		for(Stpjobservice ord : list){
        		records.add(JSONObject.toJSON(ord));
        	}
    	}
		
    	result.setSuccess("查询成功");
    	result.setRecords(records);
		return result;
	}
	
	/**
	 * 添加定时器
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	@ServiceNamed(id="600002",name="添加一个定时器")
	public OutPacket addJob(InPacket inParam)throws Exception{
		OutPacket result  = new OutPacket();
		
		String jobName = inParam.getString("jobName",null);
		String tiggerTime = inParam.getString("tiggerTime",null);
		String serviceId = inParam.getString("callServiceId",null);
		String remark = inParam.getString("remark","");
		String reqbody = inParam.getString("reqbody","");
		
		if(StringUtils.isEmpty(jobName)){
	    	result.setError(JobConstants_ErrorCode.Error_1505001, JobConstants_ErrorCode.Error_1505001_Desc+"jobName");
			return result;
		}
		if(StringUtils.isEmpty(tiggerTime)){
	    	result.setError(JobConstants_ErrorCode.Error_1505001, JobConstants_ErrorCode.Error_1505001_Desc+"tiggerTime");
			return result;
		}
		if(StringUtils.isEmpty(serviceId)){
	    	result.setError(JobConstants_ErrorCode.Error_1505001, JobConstants_ErrorCode.Error_1505001_Desc+"serviceId");
			return result;
		}
		if(dao.isJobExists(jobName,null)){
			log.info("["+jobName+"：]定时作业名称重复");
	    	result.setError(JobConstants_ErrorCode.Error_1505002, "["+jobName+"：]"+JobConstants_ErrorCode.Error_1505002_Desc);
			return result;
		}
		String date = DateOper.getNow("yyyyMMddHHmmss");
		Stpjobservice s = new Stpjobservice();
		s.setJobid(UUIDUtil.getUUID());
		s.setGroupname(Constant.JOB_GROUP_NAME);
		s.setJobname(jobName);
		s.setOptdate(date);
		s.setStatus(Constant.JOB_STATUS_2);
		s.setServiceid(serviceId);
		s.setTiggertime(tiggerTime);
		s.setRemark(remark);
		s.setReqbody(reqbody);
		int c = dao.insertJobService(s);
		if(c < 1){
			log.info("新增失败，影响数目为："+c);
	    	result.setError(JobConstants_ErrorCode.Error_1505003, JobConstants_ErrorCode.Error_1505003_Desc+c);
			return result;
		}
		result.setSuccess("成功");
		return result;
	}
	
	/**
	 * 修改定时器
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	@ServiceNamed(id="600003",name="修改一个定时器")
	public OutPacket modifyJob(InPacket inParam)throws Exception{
		OutPacket result  = new OutPacket();
		Stpjobservice s = new Stpjobservice();
		String jobId = inParam.getString("jobId",null);
		String jobName = inParam.getString("jobName",null);
		String tiggerTime = inParam.getString("tiggerTime",null);
		String serviceId = inParam.getString("callServiceId",null);
		String remark = inParam.getString("remark",null);
		String reqbody = inParam.getString("reqbody",null);
		if(StringUtils.isEmpty(jobId)){
	    	result.setError(JobConstants_ErrorCode.Error_1505001, JobConstants_ErrorCode.Error_1505001_Desc+"jobId");
			return result;
		}
		if(StringUtils.isEmpty(jobName)){
	    	result.setError(JobConstants_ErrorCode.Error_1505001, JobConstants_ErrorCode.Error_1505001_Desc+"jobName");
			return result;
		}else{
			s.setJobname(jobName);
		}
		if(StringUtils.isEmpty(tiggerTime)){
	    	result.setError(JobConstants_ErrorCode.Error_1505001, JobConstants_ErrorCode.Error_1505001_Desc+"tiggerTime");
			return result;
		}else{
			s.setTiggertime(tiggerTime);
		}
		if(StringUtils.isEmpty(serviceId)){
	    	result.setError(JobConstants_ErrorCode.Error_1505001, JobConstants_ErrorCode.Error_1505001_Desc+"serviceId");
			return result;
		}else{
			s.setServiceid(serviceId);
		}
		s.setJobid(jobId);
		s.setRemark(remark);
		s.setReqbody(reqbody);
		if(dao.isJobExists(jobName,jobId)){
			log.info("["+jobName+"：]定时作业名称重复");
	    	result.setError(JobConstants_ErrorCode.Error_1505002, "["+jobName+"：]"+JobConstants_ErrorCode.Error_1505002_Desc);
			return result;
		}
		jobManage.removeJobById(jobId);
		int count = dao.updateStpjobservice(s);
		if(count < 1){
			log.info("修改失败，影响数目为："+count);
	    	result.setError(JobConstants_ErrorCode.Error_1505003, JobConstants_ErrorCode.Error_1505003_Desc+count);
			return result;
		}
		Stpjobservice obj = dao.getJobById(jobId);
		if(obj.getStatus().equals("1")){//运行状态，重启
			jobManage.addJob(obj,this.context);
		}
		result.setSuccess("成功");
		return result;
	}
	
	/**
	 * 启用_禁用_移除
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	@ServiceNamed(id="600004",name="启用_禁用_移除一个定时器")
	public OutPacket removeJob(InPacket inParam)throws Exception{
		OutPacket result  = new OutPacket();
		Stpjobservice s = new Stpjobservice();
		String jobId = inParam.getString("jobId",null);
		String status = inParam.getString("status",null);//1 启用，2禁用，0 移除
		
		if(StringUtils.isEmpty(jobId)){
	    	result.setError(JobConstants_ErrorCode.Error_1505001, JobConstants_ErrorCode.Error_1505001_Desc+"jobId");
			return result;
		}
		if(StringUtils.isEmpty(status)){
	    	result.setError(JobConstants_ErrorCode.Error_1505001, JobConstants_ErrorCode.Error_1505001_Desc+"status");
			return result;
		}else{
			s.setStatus(status);
		}
		s.setJobid(jobId);
		int count = dao.updateStpjobservice(s);
		if(count < 1){
			log.info("修改失败，影响数目为："+count);
	    	result.setError(JobConstants_ErrorCode.Error_1505004, JobConstants_ErrorCode.Error_1505004_Desc+count);
			return result;
		}
		if(status.equals("1")){
			Stpjobservice obj = dao.getJobById(jobId);
			jobManage.addJob(obj,this.context);
		}else{
			jobManage.removeJobById(jobId);
		}
		result.setSuccess("成功");
		return result;
	}
	
	public JobDao getDao() {
		return dao;
	}

	public void setDao(JobDao dao) {
		this.dao = dao;
	}

	
	
	
}
