package com.qian.xm.pipdev.service;

import java.io.IOException;
import java.util.List;

import org.activiti.engine.repository.ProcessDefinition;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface ProcDefService {

	
	public void deploy(String modelId) throws JsonProcessingException, IOException;

	public List<ProcessDefinition> getProcDefList();

	public String startProcess(String procDefId);
}
