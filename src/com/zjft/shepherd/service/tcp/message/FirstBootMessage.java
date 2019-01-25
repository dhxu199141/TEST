package com.zjft.shepherd.service.tcp.message;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class FirstBootMessage extends CommonMessage
{

	public FirstBootMessage(Document doc) 
	{
		super(doc);
	}
	public List<ProjectInfo> getProjinfo()
	{
		List<ProjectInfo> list = new ArrayList<ProjectInfo>();
		NodeList nodes = this.getDoc().getElementsByTagName("projinfo");
		
		for(int i=0;i<nodes.getLength();i++)
		{
			ProjectInfo project = new ProjectInfo();
			
			Node node = nodes.item(i);
			
			node.getChildNodes();			
			
			NamedNodeMap attrs = node.getAttributes();			
			
			project.setProjectName(attrs.getNamedItem("projname").getNodeValue());
			project.setVersionNo(attrs.getNamedItem("currversion").getNodeValue());
			list.add(project);
		}
		
		return list;
	}
	public class ProjectInfo
	{
		private String projectName = null;
		private String versionNo = null;
		
		public String getProjectName() 
		{
			return projectName;
		}
		public void setProjectName(String projectName) 
		{
			this.projectName = projectName;
		}
		public String getVersionNo() 
		{
			return versionNo;
		}
		public void setVersionNo(String versionNo) 
		{
			this.versionNo = versionNo;
		}
	}

}
