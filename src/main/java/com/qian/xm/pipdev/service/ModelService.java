package com.qian.xm.pipdev.service;


import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.Model;

public interface ModelService {
	
	public List<Model> getModelList();

	public String addMdole(Map<String, Object> rModel);

}
