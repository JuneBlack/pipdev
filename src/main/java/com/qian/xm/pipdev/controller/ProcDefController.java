package com.qian.xm.pipdev.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qian.xm.pipdev.service.ProcDefService;

@RestController
@RequestMapping("procDef")
public class ProcDefController {
	
	
	private static final Logger log = LoggerFactory.getLogger(ProcDefController.class);

	
	@Autowired
	private ProcDefService procDefService;
	
	
	@RequestMapping("deploy")
	public String deploy(String modelId) {
		
		if (modelId != null && !modelId.equals("")) {
			try {
				procDefService.deploy(modelId);
			} catch (Exception e) {
				log.error("部署异常:" + e.getMessage());
				e.printStackTrace();
				return e.getMessage();
			} 
		}
		return "success";
	}

	@RequestMapping("procDefList")
	public String procDefList() {
		
		ObjectMapper objectMapper = new ObjectMapper();
		HashMap<String, Object> rspMap = new HashMap<String, Object>();
		ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
		//不能使用ProcessDefinition类型直接返回前台【through reference chain: java.util.HashMap["data"]->java.util.ArrayList[0]-】，原因未知
		List<ProcessDefinition> procDefList = procDefService.getProcDefList();
		if (procDefList.size() != 0) {
			for (ProcessDefinition processDefinition : procDefList) {
				Map<String, Object> map = new HashMap<String, Object>();
				
				map.put("id", processDefinition.getId());
				map.put("name", processDefinition.getName());
				map.put("key", processDefinition.getKey());
				map.put("version", processDefinition.getVersion());
				map.put("deploymentId", processDefinition.getDeploymentId());
				map.put("suspensionState", ((ProcessDefinitionEntityImpl)processDefinition).getSuspensionState());
				map.put("resourceName", ((ProcessDefinitionEntityImpl)processDefinition).getResourceName());
				arrayList.add(map);
			}
			rspMap.put("data", arrayList);
			rspMap.put("total", arrayList.size());
			log.debug("procDefList：List.size = " + procDefList.size());
		}
		try {
			return objectMapper.writeValueAsString(rspMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping("startProcess")
	public String startPorcess(String procDefId) {
		
		@SuppressWarnings("unused")
		String procInsId = procDefService.startProcess(procDefId);
		log.debug("启动流程实例成功");
		return "success";
	}
	
	/**
	 * 获取流程定义二进制文件 
	 * @param procDefId
	 * @param resourceType
	 * @param response
	 */
	@RequestMapping("getProcDefResource")
	public void getProcDefResource(String procDefId,String resourceType,HttpServletResponse response) {
		InputStream resource = null;
		if (procDefId != null && !"".equals(procDefId)) {
			resource = procDefService.getProcDefResource(procDefId,resourceType);
		}
		
		if (resource != null) {
			byte[] b = new byte[1024];
			int len = 0;
			try {
				while ((len = resource.read(b, 0, 1024))!= -1) {
					response.getOutputStream().write(b, 0, len);
					System.out.println(b);
				}
			} catch (IOException e) {
				log.error("响应流数据异常：" + e.getMessage(), e);
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
}
