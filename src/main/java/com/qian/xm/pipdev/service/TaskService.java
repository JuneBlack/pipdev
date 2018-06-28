/**
 *  @Package: com.qian.xm.pipdev.service
 *  @author: Changmey
 *  @date: 2018年6月28日 上午10:40:58 
 */
package com.qian.xm.pipdev.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;

/** 
 * @ClassName: TaskService 
 * @Description: TODO
 * @author: Changmey
 * @date: 2018年6月28日 上午10:40:58  
 */
public interface TaskService {

	/** 
	 * @Title: getAgencyTasks 
	 * @Description: 查询代办任务
	 * @return List<Task>
	 * @author Changmey
	 * @date 2018年6月28日上午10:44:52
	 */ 
	public List<Map<String, Object>> getAgencyTasks();

	/**
	 * 
	 * @Title: complateTask 
	 * @Description: 完成任务
	 * @return String
	 * @author Changmey
	 * @date 2018年6月28日下午3:18:40
	 */
	public String complateTask(String taskId);

	
}
