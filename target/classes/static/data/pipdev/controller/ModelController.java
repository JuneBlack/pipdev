package com.qian.xm.pipdev.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qian.xm.pipdev.service.ModelService;

@RestController()
@RequestMapping("/model")
public class ModelController {

	
	private static final Logger log = LoggerFactory.getLogger(ModelController.class);

	
	@Autowired
	private ModelService modelService;
	@Autowired
	private RepositoryService repositoryService;
	
	@RequestMapping("test")
	public String test() {
		return "must";
	}
	
	@RequestMapping("modelList")
	public String modelList() {
		ResponseData<Model> rdata = new ResponseData<Model>();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Model> modelList = modelService.getModelList();
		
		rdata.setData(modelList);
		rdata.setTotal(modelList.size());
		
		try {
			return objectMapper.writeValueAsString(rdata);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping("add")
	public Map<String, Object> AddModel(@RequestBody Map<String, Object> rModel, HttpServletRequest request ) {
		
		HashMap<String, Object> rspMap = new HashMap<String, Object>();
		if (rModel != null) {
			String modelId = modelService.addMdole(rModel);
			
			rspMap.put("url", request.getContextPath() + "/modeler.html?modelId=" + modelId);
		}
		return rspMap;
	}
	
	
	
	/**
	 * 创建空的流程模型，进入流程模型编辑器  06-12
	 * @param name
	 * @param key
	 * @param description
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "create", method = RequestMethod.POST)  
	   public void create(@RequestParam("name") String name, @RequestParam("key") String key, @RequestParam("description") String description,  
	                      HttpServletRequest request, HttpServletResponse response) {  
	      Map<String, String[]> parameterMap = request.getParameterMap();  
	      
	    try {  
	           ObjectMapper objectMapper = new ObjectMapper();  
	           ObjectNode editorNode = objectMapper.createObjectNode();  
	           editorNode.put("id", "canvas");  
	           editorNode.put("resourceId", "canvas");  
	           ObjectNode stencilSetNode = objectMapper.createObjectNode();  
	           stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");  
	           editorNode.put("stencilset", stencilSetNode);  
	           Model modelData = repositoryService.newModel();  
	  
	           ObjectNode modelObjectNode = objectMapper.createObjectNode();  
	           modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);  
	           modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);  
	           description = StringUtils.defaultString(description);  
	           modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);  
	           modelData.setMetaInfo(modelObjectNode.toString());  
	           modelData.setName(name);  
	           modelData.setKey(StringUtils.defaultString(key));  
	  
	           repositoryService.saveModel(modelData);  
	           repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));  
	             
	          //request.setAttribute("modelId", o);  
	             
	           response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + modelData.getId());  
	       } catch (Exception e) {  
	    	   log.error("创建模型失败：", e);  
	       }  
	   }  
	
}
