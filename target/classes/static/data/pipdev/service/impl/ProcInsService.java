package com.qian.xm.pipdev.service.impl;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ProcInsService {

	public List<Map<String, Object>> getProcInsList();

	/**
	 * 获取流程跟踪资源
	 * @param procInsId
	 * @return
	 */
	public InputStream getTraceRresource(String procInsId);
}
