package com.qian.xm.pipdev.service.impl;

import java.util.List;
import java.util.Map;

import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qian.xm.pipdev.service.BaseService;
import com.qian.xm.pipdev.service.ModelService;


@Service
public class ModelServiceImpl extends BaseService implements ModelService {

	
	public List<Model> getModelList() {
		List<Model> list = repositoryService.createModelQuery().list();
		
		return list;
	}

	@Override
	public String addMdole(Map<String, Object> rModel) {
		
		String name = (String)rModel.get("name");
		String category = (String)rModel.get("category");
		String description = (String)rModel.get("description");
		
		
		String modelId = createModel(name,description,category);
		
		/*
		ObjectMapper objectMapper = new ObjectMapper();
		
		Model newModel = repositoryService.newModel();
		
		newModel.setName(name);
		newModel.setCategory(category);
		
		//metoinf 对象
		ObjectNode metaInfo = objectMapper.createObjectNode();
		
		metaInfo.put(ModelDataJsonConstants.MODEL_NAME, name);
		metaInfo.put(ModelDataJsonConstants.MODEL_REVISION, 0);
		description = StringUtils.defaultString(description);
		metaInfo.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
		newModel.setMetaInfo(metaInfo.toString());
		
		repositoryService.saveModel(newModel);
		
		ObjectNode editorNode =  objectMapper.createObjectNode();
		editorNode.put("id","canvas");
		editorNode.put("resourceId", "canvas");
		ObjectNode stencilSetNode = objectMapper.createObjectNode();
		 stencilSetNode.put("name", "http://bpmn.org/stencailset/bpmn2.0#");
		 editorNode.put("stencilset", stencilSetNode);
		 
		 
		Charset charset = Charset.forName("utf-8");
		repositoryService.addModelEditorSource(newModel.getId(), editorNode.toString().getBytes(charset ));
		*/
		return modelId;
	}
	
	
	private String createModel(String name,String description,String category ) {
		
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
//	           modelData.setKey(StringUtils.defaultString(key));  
	  
	           repositoryService.saveModel(modelData);  
	           repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));  
	             
	          //request.setAttribute("modelId", o);  
	          return modelData.getId();   
	       } catch (Exception e) {  
//	    	   log.error("创建模型失败：", e);  
	       }  
		return null;
	}

}
