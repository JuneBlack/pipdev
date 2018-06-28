/**
 *  @Package: com.qian.xm.pipdev.controller
 *  @author: Changmey
 *  @date: 2018年6月28日 上午10:25:05 
 */
package com.qian.xm.pipdev.controller;

import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.Model;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qian.xm.pipdev.service.TaskService;

/** 
 * @ClassName: TaskController 
 * @Description: TODO
 * @author: Changmey
 * @date: 2018年6月28日 上午10:25:05  
 */
@RestController
@RequestMapping("task")
public class TaskController {
	private static final Logger log = LoggerFactory.getLogger(TaskController.class);

	@Autowired
	private TaskService taskService;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@RequestMapping("agencyTasks")
	public String agencyTasks() {
		ResponseData<Map<String,Object>> rdata = new ResponseData<Map<String,Object>>();
		List<Map<String,Object>> taskList = taskService.getAgencyTasks();
		rdata.setData(taskList);
		rdata.setTotal(taskList.size());
		
		try {
			return objectMapper.writeValueAsString(rdata);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	@RequestMapping("complateTask")
	public String complateTask(String taskId) {
		log.info(">>>>>>>>>>>>>>>>完成任务，任务ID = " + taskId);
		String result = "";
		if (taskId != null && !"".equals(taskId)) {
			result = taskService.complateTask(taskId);
		}
		return result;
	}

}
