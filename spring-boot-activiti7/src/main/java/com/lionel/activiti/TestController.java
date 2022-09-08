package com.lionel.activiti;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ManagementService managementService;

    @GetMapping("/run")
    public void run() {

        // 1.部署流程
        prepare();

        // 2.启动一个流程实例
        ProcessInstance processInstance = startProcess();

        // 3.任务查询
        Task task = searchTask(processInstance);

        // 4.处理任务
        processTask(task);
    }

    public void prepare() {
        Deployment deployment = repositoryService.createDeployment()
                .name("请假流程")
                .addClasspathResource("processes/process.bpmn")
                .addClasspathResource("processes/process.png")
                .deploy();

        System.out.println("部署ID：" + deployment.getId());
        System.out.println("部署名称：" + deployment.getName());
    }

    public ProcessInstance startProcess() {
        String processDefinitionKey = "test01";
        Map<String, Object> map = new HashMap<>();

        //使用UEL 表达式设置

        // 学生填写申请单    Assignee：${student}
        map.put ("student", "lucy");

        // 班主任审批    Assignee：${teacher}
        map.put ("teacher", "jack");

        ProcessInstance instance = runtimeService.startProcessInstanceByKey (processDefinitionKey, map);
        System.out.println ("流程实例ID:" + instance.getId ());
        System.out.println ("流程定义ID:" + instance.getProcessDefinitionId ());
        return instance;
    }

    public Task searchTask(ProcessInstance processInstance) {
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println ("任务ID:" + task.getId ());
        System.out.println ("任务名称:" + task.getName ());
        System.out.println ("任务的创建时间:" + task.getCreateTime ());
        System.out.println ("任务的办理人:" + task.getAssignee ());
        System.out.println ("流程实例ID：" + task.getProcessInstanceId ());
        System.out.println ("执行对象ID:" + task.getExecutionId ());
        System.out.println ("流程定义ID:" + task.getProcessDefinitionId ());

        String days = (String) taskService.getVariable(task.getId(), "days");
        Date date = (Date) taskService.getVariable(task.getId(), "date");
        String reason = (String) taskService.getVariable(task.getId(), "reason");
        System.out.println("请假天数:  " + days);
        System.out.println("请假理由:  " + reason);
        String datestr = date != null ? date.toString():"";
        System.out.println("请假日期:  " + datestr);

        return task;
    }

    public void processTask(Task task) {
        String leaveDays = "10"; // 请假天数
        String leaveReason = "回老家结婚"; // 请假原因
        Map<String, Object> map = new HashMap<>();
        map.put("days", leaveDays);
        map.put("date", new Date());
        map.put("reason", leaveReason);
        taskService.complete(task.getId());
        System.out.println("处理任务Id：" + task.getId());
    }

}
