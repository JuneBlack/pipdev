package com.qian.xm.pipdev.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qian.xm.pipdev.service.impl.ProcInsService;

@RestController
@RequestMapping("procIns")
public class ProcInsController {


	private static final Logger log = LoggerFactory.getLogger(ProcInsController.class);
	
	@Autowired
	private ProcInsService procInsService;
	

	@RequestMapping("procInsList")
	public String getProcInsList() {
		HashMap<String, Object> responseData = new HashMap<String, Object>();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> procInsList = procInsService.getProcInsList();
		
		responseData.put("data", procInsList);
		responseData.put("total", procInsList.size());
		log.debug("****** 实例查询 ******");
		try {
			return objectMapper.writeValueAsString(responseData); 
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	@RequestMapping("processTrace")
	public void processTrace(String procInsId, HttpServletResponse response) {
		
		if (procInsId != null && !"".equals(procInsId)) {
			InputStream traceResource = procInsService.getTraceRresource(procInsId);
			
			byte[] b = new byte[1024];
			
			int len = 0;
			try {
				while ((len = traceResource.read(b, 0, 1024))!= -1) {
						response.getOutputStream().write(b, 0, len);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
}
