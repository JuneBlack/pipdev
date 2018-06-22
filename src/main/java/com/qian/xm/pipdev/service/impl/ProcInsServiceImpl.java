package com.qian.xm.pipdev.service.impl;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import com.qian.xm.pipdev.service.BaseService;

@Service
public class ProcInsServiceImpl extends BaseService implements ProcInsService {

	@Override
	public List<Map<String, Object>> getProcInsList() {
		ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
		List<Execution> list2 = runtimeService.createExecutionQuery().onlyChildExecutions().list();
		List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
		for (Execution e : list2) {
			System.err.println("Execution:" + e.getId());
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("id", e.getProcessInstanceId());
			map.put("parentId", e.getParentId());
			map.put("activityId",	 e.getActivityId());
			map.put("procDefId", ((ExecutionEntityImpl)e).getProcessDefinitionId());
			
			Task task = taskService.createTaskQuery().executionId(e.getId()).singleResult();
			map.put("taskName", task.getName());
			map.put("assignee", task.getAssignee());
			map.put("state", e.isSuspended()? "挂起":"激活");
			arrayList.add(map);
		}
		
		for (ProcessInstance p : list) {
			System.out.println("ProcessInstance:" + p.getId() + p.getName());
		}
		return arrayList;
	}

	@Override
	public InputStream getTraceRresource(String procInsId) {
		ProcessInstance procIns = runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
		//BpmnModel
		BpmnModel bpmnModel = null;
		//正在运行节点
		List<String> activeActivityIds = Collections.emptyList();
		//高亮路线集合
		List<String> highLightedFlows = Collections.emptyList();
		if (procIns!= null) {
			bpmnModel = repositoryService.getBpmnModel(procIns.getProcessDefinitionId());
			
			activeActivityIds = runtimeService.getActiveActivityIds(procIns.getId());
		}
		
		HistoricProcessInstance hisProcIns = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).singleResult();
		//流程定义
		ProcessDefinition procDef = repositoryService.getProcessDefinition(hisProcIns.getProcessDefinitionId());
		//获取流程历史已执行节点
		List<HistoricActivityInstance> highLightActivitiList = historyService.createHistoricActivityInstanceQuery().processInstanceId(procInsId).list();
		
		
		//设置高亮路线ID集合
		highLightedFlows = gettHighLightedFlows(bpmnModel,procDef,highLightActivitiList);
		
		String fontName = "宋体";
		InputStream imageStream = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator()
                .generateDiagram(bpmnModel, "png", activeActivityIds, highLightedFlows, fontName, fontName, fontName, null,
                        1.0);
		
		
		return imageStream; 
		
		
	}

	private List<String> gettHighLightedFlows(BpmnModel bpmnModel, ProcessDefinition procDef,
			List<HistoricActivityInstance> historicActivityInstances) {

		/**
         * 24小时制
         */
        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        /**
         * 保存高亮的线
         */
        List<String> highFlows = new ArrayList<String>();

        /**
         * 对历史流程节点进行遍历
         */
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
            /**
             * 对历史流程节点进行遍历 得到节点定义的详细信息
             */
            FlowNode activityImpl = (FlowNode) bpmnModel.getMainProcess()
                    .getFlowElement(historicActivityInstances.get(i).getActivityId());

            /**
             * 用以保存后需开始时间相同的节点
             */
            List<FlowNode> sameStartTimeNodes = new ArrayList<FlowNode>();
            FlowNode sameActivityImpl1 = null;
            HistoricActivityInstance activityImp1_ = historicActivityInstances.get(i);
            HistoricActivityInstance activityImp2_;
            for (int k = i + 1; k <= historicActivityInstances.size() - 1; k++) {
                /***
                 * 后续第一个节点
                 */
                activityImp2_ = historicActivityInstances.get(k);
                /**
                 * 判断是否为后续节点
                 */
                if (activityImp1_.getActivityType().equals("userTask")
                        && activityImp2_.getActivityType().equals("userTask")
                        && df.format(activityImp1_.getStartTime()).equals(df.format(activityImp2_.getStartTime()))) {

                } else {
                    /**
                     * 找到紧跟在后面的节点
                     */
                    sameActivityImpl1 = (FlowNode) bpmnModel.getMainProcess()
                            .getFlowElement(historicActivityInstances.get(k).getActivityId());
                    break;
                }

            }
            /**
             * 将后面第一个节点放在时间相同节点的集合里
             */
            sameStartTimeNodes.add(sameActivityImpl1);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                /**
                 * 后续第一个节点
                 */
                HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);
                /**
                 * 后续第二个节点
                 */
                HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);
                /**
                 * 如果第一个节点和第二个节点开始时间相同保存
                 */
                if (df.format(activityImpl1.getStartTime()).equals(df.format(activityImpl2.getStartTime()))) {
                    FlowNode sameActivityImpl2 = (FlowNode) bpmnModel.getMainProcess()
                            .getFlowElement(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {
                    /**
                     * 不相同跳出循环
                     */
                    break;
                }

            }
            /**
             * 取出节点的所有出去的线
             */
            List<SequenceFlow> pvmTransitions = activityImpl.getOutgoingFlows();
            /**
             * 对所有的线进行遍历
             */
            for (SequenceFlow pvmTransition : pvmTransitions) {
                FlowNode pvmActivityImpl = (FlowNode) bpmnModel.getMainProcess()
                        .getFlowElement(pvmTransition.getTargetRef());
                /**
                 * 如果取出的线的目标节点存在相同的节点里，保存该线的id，进行高亮显示
                 */
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
	}

}
