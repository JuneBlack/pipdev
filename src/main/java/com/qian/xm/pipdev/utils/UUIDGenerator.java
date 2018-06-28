package com.qian.xm.pipdev.utils;

import org.activiti.engine.impl.cfg.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UUIDGenerator implements IdGenerator {

	@Autowired
	private SnowflakeIdWorker snowFlakeIdWorker;
	
	@Override
	public String getNextId() {
		
		return String.valueOf(snowFlakeIdWorker.nextId());
	}

}
