package com.qian.xm.pipdev.utils;

import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class MyActivitiEngineConfiguration implements ProcessEngineConfigurationConfigurer {

	@Autowired
	private UUIDGenerator idGnerator;
	
	@Override
	public void configure(SpringProcessEngineConfiguration spec) {
		//中文乱码
		spec.setActivityFontName("WenQuanYi Zen Hei");

		//配置自定义ID生成器
		spec.setIdGenerator(idGnerator);
	}

}
