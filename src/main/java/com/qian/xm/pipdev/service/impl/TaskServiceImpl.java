/**
 *  @Package: com.qian.xm.pipdev.service.impl
 *  @author: Changmey
 *  @date: 2018年6月28日 上午10:47:29 
 */
package com.qian.xm.pipdev.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qian.xm.pipdev.service.TaskService;

/** 
 * @ClassName: TaskServiceImpl 
 * @Description: TODO
 * @author: Changmey
 * @date: 2018年6月28日 上午10:47:29  
 */
@Service
public class TaskServiceImpl implements TaskService {

	@Autowired
	private org.activiti.engine.TaskService taskService;

	@Override
	public List<Map<String, Object>> getAgencyTasks() {
		List<Task> list = taskService.createTaskQuery().active().list();
		
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Task task : list) {
			HashMap<String, Object> temp = new HashMap<String, Object>();
			temp.put("id", task.getId());
			temp.put("taskName",task.getName());
			temp.put("assignee", task.getAssignee());
			Date createTime = task.getCreateTime();
			temp.put("createTime",sdf.format(createTime));
			temp.put("owner",task.getOwner());
			temp.put("procDefId",task.getProcessDefinitionId());
			dataList.add(temp);
		}
		return dataList;
	}

	
	@Override
	public String complateTask(String taskId) {
		taskService.complete(taskId);
		return "success";
	}

}
