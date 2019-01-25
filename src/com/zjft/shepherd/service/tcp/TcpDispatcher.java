package com.zjft.shepherd.service.tcp;

import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.service.tcp.message.AgentPwdMessage;
import com.zjft.shepherd.service.tcp.message.CommonMessage;
import com.zjft.shepherd.service.tcp.message.DevBaseInfoMessage;
import com.zjft.shepherd.service.tcp.message.DevInfoChkMessage;
import com.zjft.shepherd.service.tcp.message.FirstBootMessage;
import com.zjft.shepherd.service.tcp.process.AgentPwdProcess;
import com.zjft.shepherd.service.tcp.process.DevBaseInfoProcess;
import com.zjft.shepherd.service.tcp.process.DevInfoChkProcess;
import com.zjft.shepherd.service.tcp.process.FirstBootProcess;



public class TcpDispatcher 
{
	private static Log log = LogFactory.getLog(TcpDispatcher.class);
	private AgentPwdProcess agentPwdProcess = null;
	private FirstBootProcess firstBootProcess = null;
	private DevBaseInfoProcess devBaseInfoProcess = null;
	private DevInfoChkProcess devInfoChkProcess = null;
	/**
	 * 分发消息
	 * @param message
	 */
	public void dispatch(CommonMessage message, Socket socket) 
	{
		if(message.getCmdid() == null || message.getCmdid().equals(""))
		{
			log.debug("接收到的报文为空");
			return;
		}
		else if(message.getCmdid().equals("110001"))
		{
			agentPwdProcess.process(new AgentPwdMessage(message.getDoc()), socket);
		}
		else if(message.getCmdid().equals("110002"))
		{
			devInfoChkProcess.process(new DevInfoChkMessage(message.getDoc()), socket);
		}
		else if(message.getCmdid().equals("900007"))
		{
			firstBootProcess.process(new FirstBootMessage(message.getDoc()), socket);
		}
		else if(message.getCmdid().equals("120001"))
		{
			devBaseInfoProcess.process(new DevBaseInfoMessage(message.getDoc()), socket);
		}
	}

	public AgentPwdProcess getAgentPwdProcess() {
		return agentPwdProcess;
	}

	public void setAgentPwdProcess(AgentPwdProcess agentPwdProcess) {
		this.agentPwdProcess = agentPwdProcess;
	}

	public FirstBootProcess getFirstBootProcess() {
		return firstBootProcess;
	}

	public void setFirstBootProcess(FirstBootProcess firstBootProcess) {
		this.firstBootProcess = firstBootProcess;
	}

	public DevBaseInfoProcess getDevBaseInfoProcess() {
		return devBaseInfoProcess;
	}

	public void setDevBaseInfoProcess(DevBaseInfoProcess devBaseInfoProcess) {
		this.devBaseInfoProcess = devBaseInfoProcess;
	}

	public DevInfoChkProcess getDevInfoChkProcess() {
		return devInfoChkProcess;
	}

	public void setDevInfoChkProcess(DevInfoChkProcess devInfoChkProcess) {
		this.devInfoChkProcess = devInfoChkProcess;
	}
}
