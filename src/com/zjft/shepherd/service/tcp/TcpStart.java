package com.zjft.shepherd.service.tcp;

import com.zjft.shepherd.service.tcp.process.AgentPwdProcess;
import com.zjft.shepherd.service.tcp.process.DevBaseInfoProcess;
import com.zjft.shepherd.service.tcp.process.DevInfoChkProcess;
import com.zjft.shepherd.service.tcp.process.FirstBootProcess;

public class TcpStart 
{
	public static void main(String[] args)
	{
		//初始化报文分发对象
		AgentPwdProcess agentPwdProcess = new AgentPwdProcess();
		FirstBootProcess firstBootProcess = new FirstBootProcess();
		DevBaseInfoProcess devBaseInfoProcess = new DevBaseInfoProcess();
		DevInfoChkProcess devInfoChkProcess = new DevInfoChkProcess();
		
		TcpDispatcher tcpDispatcher = new TcpDispatcher();
		tcpDispatcher.setAgentPwdProcess(agentPwdProcess);
		tcpDispatcher.setFirstBootProcess(firstBootProcess);
		tcpDispatcher.setDevBaseInfoProcess(devBaseInfoProcess);
		tcpDispatcher.setDevInfoChkProcess(devInfoChkProcess);
		
		//开启tcp服务进程
		Thread t = new Thread(new TcpService(tcpDispatcher));
		t.start();
	}
}
