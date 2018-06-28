package com.qian.xm.pipdev.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qian.xm.pipdev.service.BaseService;
import com.qian.xm.pipdev.service.ProcDefService;

@Service
public class ProcDefServiceImpl extends BaseService implements ProcDefService {
	
	private static final Logger log = LoggerFactory.getLogger(ProcDefServiceImpl.class);
	
	
	
	@Override
	public void deploy(String modelId) throws JsonProcessingException, IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
		Model model = repositoryService.getModel(modelId);
		BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
		
		if (model != null) {
		//	资源文件（二进制）--> JsonNode --> BpmnMode --> XML（二进制） --> 部署
			byte[] modelEditorSource = repositoryService.getModelEditorSource(model.getId());
			JsonNode editorNode = objectMapper.readTree(modelEditorSource);
			BpmnModel bpmnModel = bpmnJsonConverter.convertToBpmnModel(editorNode);
			byte[] modelXml = bpmnXMLConverter.convertToXML(bpmnModel);
			
			DeploymentBuilder deployment = repositoryService.createDeployment();
			if (!model.getName().endsWith(".bpmn20.xml") && !model.getName().endsWith(".pbmn"))  {
				deployment.addBpmnModel(model.getName() + ".bpmn20.xml", bpmnModel).name(model.getName());
			}else {
				deployment.addBpmnModel(model.getName(), bpmnModel).name(model.getName());
			}
//			deployment.addInputStream(model.getName(), new ByteArrayInputStream(modelXml));
			Deployment deploy = deployment.deploy();
			log.debug("部署成功,部署ID：  " + deploy.getId());
		}
		
	}

	@Override
	public List<ProcessDefinition> getProcDefList() {
		
		ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
		List<ProcessDefinition> list = query.list();
		
		return list;
	}

	@Override
	public String startProcess(String procDefId) {
		ProcessInstance procIns = runtimeService.startProcessInstanceById(procDefId);
		return procIns.getId();
	}

	@Override
	public InputStream getProcDefResource(String procDefId, String resourceType) {
		InputStream resource = null;
		ProcessDefinition procDef = repositoryService.getProcessDefinition(procDefId);
		String resourceName = null ;
		if (procDef != null) {
			if ("image".equals(resourceType)) {
				resourceName = procDef.getDiagramResourceName();
			}else if ("xml".equals(resourceType)) {
				resourceName = procDef.getResourceName();
			} 
		}
		
		if (resourceName != null) {
			resource = repositoryService.getResourceAsStream(procDef.getDeploymentId(), resourceName);
		}
		return resource;
	}

}
