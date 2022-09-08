package com.lionel.activiti;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@SpringBootTest
class SpringBootActiviti7ApplicationTests {

    @Autowired
    private RuntimeService runtimeService;

    private Logger logger = (Logger) LogFactory.getLog(SpringBootActiviti7ApplicationTests.class);

    @Test
    void contextLoads() {
    }

    @Test
    public void start() {
        String instanceKey = "test01";
        logger.info("开启请假流程...");
        Map<String, Object> map = new HashMap<String, Object>();
        //在holiday.bpmn中,填写请假单的任务办理人为动态传入的userId,此处模拟一个id
        map.put("userId", "10001");
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(instanceKey, map);
        logger.info("启动流程实例成功:{}", instance);
        logger.info("流程实例ID:{}", instance.getId());
        logger.info("流程定义ID:{}", instance.getProcessDefinitionId());

    }


}
