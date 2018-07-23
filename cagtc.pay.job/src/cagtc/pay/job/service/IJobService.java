package cagtc.pay.job.service;

import cagtc.pay.core.common.InPacket;
import cagtc.pay.core.common.OutPacket;

public interface IJobService {

	/**
	 * 查询当前定时器
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public OutPacket queryCurrentRunJobs(InPacket inParam)throws Exception;
	
	/**
	 * 添加定时器
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public OutPacket addJob(InPacket inParam)throws Exception;
	
	/**
	 * 修改定时器
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public OutPacket modifyJob(InPacket inParam)throws Exception;
	
	/**
	 * 修改定时器
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public OutPacket removeJob(InPacket inParam)throws Exception;
	
}
