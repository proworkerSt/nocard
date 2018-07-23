package cagtc.pay.job.dao;

import java.util.ArrayList;
import java.util.List;

import com.coreframework.util.DateOper;

import cagtc.pay.core.common.dao.commonDao;
import cagtc.pay.core.db.support.ReflectObject;
import cagtc.pay.om.Stpjobservice;

public class JobDao extends commonDao{

	
	/**
	 * 获取作业列表
	 * @return
	 */
	public List<Stpjobservice> getJobList() {
		String sql = "select * from STPJOBSERVICE t where t.status != 0 ";
		return (List<Stpjobservice>)this.template.querySQL(sql,new Object[]{},
				this.template.getRowMapper(Stpjobservice.class));
	} 
	
	/**
	 * 获取作业
	 * @return
	 */
	public Stpjobservice getJobById(String JobId) {
		String sql = "select * from STPJOBSERVICE t where t.jobId = '"+JobId+"'";
		List<Stpjobservice> list = (List<Stpjobservice>)this.template.querySQL(sql,null,
				this.template.getRowMapper(Stpjobservice.class));
		if(list.size()==0) return null;
		return list.get(0);
	} 
	
	/**
	 * 是否存在
	 * @return
	 */
	public boolean isJobExists(String jobName,String jobId) {
		StringBuffer sql = new StringBuffer();
		sql.append("select count(*) from STPJOBSERVICE t ");
		sql.append(" where t.status != 0 ");
		sql.append(" and jobName = '").append(jobName).append("'");
		if(null != jobId){
			sql.append(" and jobId != '").append(jobId).append("'");
		}
		Integer count = this.template.queryForInt(sql.toString());
		if(null == count || count == 0){
			return false;
		}
		return true;
	} 
	
	/**
	 * 新增一个作业
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public int insertJobService(Stpjobservice obj)throws Exception{
		return this.template.insertObject(obj);
	}
	
	/**
	 * 修改作业
	 * @param casordno
	 * @param status
	 */
	public int updateStpjobservice(Stpjobservice obj)throws Exception{
		StringBuffer sql = new StringBuffer();
		String date = DateOper.getNow("yyyyMMddHHmmss");
		sql.append("update STPJOBSERVICE set optdate = ").append(date) ;
		
		StringBuffer param_sql = new StringBuffer();
		List<String> params = new ArrayList<String>();
		if(null != obj.getServiceid()){
			param_sql.append(", Serviceid = ?");
			params.add(obj.getServiceid());
		}
		if(null != obj.getStatus()){
			param_sql.append(", Status = ?");
			params.add(obj.getStatus());
		} 
		if(null != obj.getTiggertime()){
			param_sql.append(", Tiggertime = ?");
			params.add(obj.getTiggertime());
		} 
		if(null != obj.getRemark()){
			param_sql.append(", Remark = ?");
			params.add(obj.getRemark());
		} 
		if(null != obj.getReqbody()){
			param_sql.append(", reqbody = ?");
			params.add(obj.getReqbody());
		} 
		if(params.size() < 1){
			throw new Exception(" 没有传入有效的修改的参数  ");
		}
		StringBuffer where_sql = new StringBuffer();
		param_sql.append(" where 1=1 ");
		if(null != obj.getJobid() && !"".equals(obj.getJobid())){
			where_sql.append(" and Jobid = ?");
			params.add(obj.getJobid());
		}
		if(where_sql.length() == 0){
			throw new Exception(" 没有传入有效的查询参数  ");
		}else{
			sql = sql.append(param_sql).append(where_sql);
			return this.template.updateSQL(sql.toString(), setParams(params));
		}	
	}

	private Object[] setParams(List params){
		Object[] obj = new Object[params.size()];
		for (int i = 0; i < params.size(); i++) {
			obj[i] = params.get(i);
		}
		return obj;
	}
	
	
}
