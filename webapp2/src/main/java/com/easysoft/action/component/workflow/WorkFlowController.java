package com.easysoft.action.component.workflow;


import com.easysoft.core.common.vo.json.AjaxJson;
import com.easysoft.core.common.vo.json.DataGridReturn;
import com.easysoft.core.utils.WorkflowUtils;
import com.easysoft.framework.utils.JsonUtils;
import com.easysoft.workflow.manager.impl.WorkflowTraceService;
import com.easysoft.workflow.vo.DefAndDeployVo;
import com.easysoft.workflow.vo.FlowDefTlp;
import com.easysoft.workflow.vo.SequenceFlowVo;
import com.easysoft.workflow.vo.UserNode;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;

import org.activiti.engine.impl.context.Context;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * 流程管理控制器
 *
 * @author andy
 */
@Controller
@RequestMapping(value = "/core/admin/workflow")
public class WorkFlowController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected TaskService taskService;



    protected static Map<String, ProcessDefinition> PROCESS_DEFINITION_CACHE = new HashMap<String, ProcessDefinition>();

    @Autowired
    ProcessEngineFactoryBean processEngine;
    @Autowired
    protected WorkflowTraceService traceService;

    /**
     * 流程定义列表
     *
     * @return
     */
    @RequestMapping(params = {"process-list"})
    public ModelAndView processList(HttpServletRequest request) {
        return new ModelAndView("admin/core/workflow/process-list");
    }
    @RequestMapping(params = {"dataGrid"})
    public ModelAndView dataGrid(){

        List dataList= new ArrayList();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().orderByDeploymentId().desc();
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage(0,100);
        DefAndDeployVo defAndDeployVo = null;
        for (ProcessDefinition processDefinition : processDefinitionList) {
            String deploymentId = processDefinition.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            defAndDeployVo = new DefAndDeployVo();
            defAndDeployVo.setDefId(processDefinition.getId());
            defAndDeployVo.setDeploymentId(processDefinition.getDeploymentId());
            defAndDeployVo.setDefName(processDefinition.getName());
            defAndDeployVo.setDefKey(processDefinition.getKey());
            defAndDeployVo.setDefVersion(processDefinition.getVersion());
            defAndDeployVo.setResourceName(processDefinition.getResourceName());
            defAndDeployVo.setDiagramResourceName(processDefinition.getDiagramResourceName());
            defAndDeployVo.setDeploymentTime(deployment.getDeploymentTime());
            dataList.add(defAndDeployVo);
        }
        DataGridReturn dataGridReturn = new DataGridReturn(dataList.size(),dataList);
        String json = JsonUtils.beanToJson(dataGridReturn);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("json",json);
        return new ModelAndView("admin/json_message",map);
    }
    /**
     * 流程定义列表
     *
     * @return
     */
    @RequestMapping(params = {"toDeploy"})
    public ModelAndView toDeploy() {
        return new ModelAndView("admin/core/workflow/toDeploy");
    }
    @RequestMapping(params = {"deploy"})
    public String deploy(@Value("#{APP_PROPERTIES['export.diagram.path']}") String exportDir, @RequestParam(value = "file", required = false) MultipartFile file) {

        String fileName = file.getOriginalFilename();

        try {
            InputStream fileInputStream = file.getInputStream();
            Deployment deployment = null;

            String extension = FilenameUtils.getExtension(fileName);
            if (extension.equals("zip") || extension.equals("bar")) {
                ZipInputStream zip = new ZipInputStream(fileInputStream);
                deployment = repositoryService.createDeployment().addZipInputStream(zip).deploy();
            } else {
                deployment = repositoryService.createDeployment().addInputStream(fileName, fileInputStream).deploy();
            }

            List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();

            for (ProcessDefinition processDefinition : list) {
                WorkflowUtils.exportDiagramToFile(repositoryService, processDefinition, exportDir);
            }

        } catch (Exception e) {
            logger.error("error on deploy process, because of file input stream", e);
        }

        return "redirect:/workflow/process-list";
    }
    //新建流程部署
    @RequestMapping(params = {"designerDeploy"})
    @ResponseBody
    public AjaxJson designerDeploy(String jsonData){
        AjaxJson result = new AjaxJson();
        Map<String,Class> map = new HashMap<String,Class>();
        map.put("nodes", UserNode.class);
        map.put("paths", SequenceFlowVo.class);
        FlowDefTlp flowDefTlp = (FlowDefTlp)JsonUtils.jsonToBean(jsonData,FlowDefTlp.class,map);
        Configuration cfg = new Configuration();
        Writer out = null;
        try {
            cfg.setDirectoryForTemplateLoading(new File("E:\\jeap\\core\\src\\main\\resources\\jeap\\bpm"));
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            Template template = cfg.getTemplate("jpdl-template.ftl","UTF-8");
            Map data = new HashMap();
            data.put("processKey",flowDefTlp.getKey());
            data.put("processName",flowDefTlp.getName());
            data.put("displayName",flowDefTlp.getDesc());
            data.put("applyUserId","applyUserId");
            data.put("afterModifyApplyContentProcessor","afterModifyApplyContentProcessor");
            data.put("reportBackEndProcessor","reportBackEndProcessor");
            data.put("flowDefTlp",flowDefTlp);
            File dirFile = new File("E:\\jeap\\core\\src\\main\\resources\\jeap\\bpm\\temp.bpmn20.xml");
            if(!dirFile.exists()){
                dirFile.createNewFile();
            }
            out = new BufferedWriter(new FileWriter(dirFile));
            template.process(data,out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TemplateException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if(out!=null) try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return result;
    }

    @RequestMapping(params = {"delete"})
    @ResponseBody
    public AjaxJson delete(String id) throws Exception {
        AjaxJson json = new AjaxJson();
        try {
            repositoryService.deleteDeployment(id, true);
            json.setMsg("删除成功");
        } catch (RuntimeException e) {
            json.setMsg("删除失败:" + e.getMessage());
            json.setSuccess(false);
        }

        return json;
    }

    /**
     * 待办任务--Portlet
     */
    @RequestMapping(params = {"taskTodoList"})

    public ModelAndView todoList() throws Exception {
      /*  AdminUser user = UserServiceFactory.getUserService().getCurrentUser();
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        // 已经签收的任务
        List<Task> todoList = taskService.createTaskQuery().taskAssignee(user.getUserid().toString()).active().list();
        for (Task task : todoList) {
            String processDefinitionId = task.getProcessDefinitionId();
            ProcessDefinition processDefinition = getProcessDefinition(processDefinitionId);

            Map<String, Object> singleTask = packageTaskInfo(sdf, task, processDefinition);
            singleTask.put("status", "todo");
            result.add(singleTask);
        }

        // 等待签收的任务
        List<Task> toClaimList = taskService.createTaskQuery().taskCandidateUser(user.getUsername().toString()).active().list();
        for (Task task : toClaimList) {
            String processDefinitionId = task.getProcessDefinitionId();
            ProcessDefinition processDefinition = getProcessDefinition(processDefinitionId);

            Map<String, Object> singleTask = packageTaskInfo(sdf, task, processDefinition);
            singleTask.put("status", "claim");
            result.add(singleTask);
        }*/
        Map<String,Object> params = new HashMap<String,Object>();
        //params.put("result",result);
        return new ModelAndView("workflow/task/tasklist",params);
    }

    private ProcessDefinition getProcessDefinition(String processDefinitionId) {
        ProcessDefinition processDefinition = PROCESS_DEFINITION_CACHE.get(processDefinitionId);
        if (processDefinition == null) {
            processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
            PROCESS_DEFINITION_CACHE.put(processDefinitionId, processDefinition);
        }
        return processDefinition;
    }
    private Map<String, Object> packageTaskInfo(SimpleDateFormat sdf, Task task, ProcessDefinition processDefinition) {
        Map<String, Object> singleTask = new HashMap<String, Object>();
        singleTask.put("id", task.getId());
        singleTask.put("name", task.getName());
        singleTask.put("createTime", sdf.format(task.getCreateTime()));
        singleTask.put("pdname", processDefinition.getName());
        singleTask.put("pdversion", processDefinition.getVersion());
        singleTask.put("pid", task.getProcessInstanceId());
        return singleTask;
    }

    /**
     * 读取带跟踪的图片
     */
    @RequestMapping(value = "/trace/auto/{executionId}")
    public void readResource(@PathVariable("executionId") String executionId, HttpServletResponse response)
            throws Exception {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(executionId).singleResult();
        //BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        List<String> activeActivityIds = runtimeService.getActiveActivityIds(executionId);
        // 不使用spring请使用下面的两行代码
//    ProcessEngineImpl defaultProcessEngine = (ProcessEngineImpl) ProcessEngines.getDefaultProcessEngine();
//    Context.setProcessEngineConfiguration(defaultProcessEngine.getProcessEngineConfiguration());

        // 使用spring注入引擎请使用下面的这行代码
        Context.setProcessEngineConfiguration(processEngine.getProcessEngineConfiguration());

       // InputStream imageStream = ProcessDiagramGenerator.generateDiagram(bpmnModel, "png", activeActivityIds);

        // 输出资源内容到相应对象
        byte[] b = new byte[1024];
        int len;
        /*while ((len = imageStream.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }*/
    }
    /**
     * 输出跟踪流程信息
     *
     * @param processInstanceId
     * @return
     * @throws Exception
     */
    @RequestMapping(params = {"trace"})
    @ResponseBody
    public List<Map<String, Object>> traceProcess(@RequestParam("pid") String processInstanceId) throws Exception {
        List<Map<String, Object>> activityInfos = traceService.traceProcess(processInstanceId);
        return activityInfos;
    }

    /**
     * 读取资源，通过流程ID
     *
     * @param resourceType      资源类型(xml|image)
     * @param processInstanceId 流程实例ID
     * @param response
     * @throws Exception
     */
    @RequestMapping(params = {"pInstanceRes"})
    public void loadByProcessInstance(@RequestParam("type") String resourceType, @RequestParam("pid") String processInstanceId, HttpServletResponse response)
            throws Exception {
        InputStream resourceAsStream = null;
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId())
                .singleResult();

        String resourceName = "";
        if (resourceType.equals("image")) {
            resourceName = processDefinition.getDiagramResourceName();
        } else if (resourceType.equals("xml")) {
            resourceName = processDefinition.getResourceName();
        }
        resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }
    /**
     * 读取资源，通过部署ID
     *
     * @param processDefinitionId 流程定义
     * @param resourceType        资源类型(xml|image)
     * @throws Exception
     */
    @RequestMapping(params = {"readResouce"})
    @ResponseBody
    public void loadByDeployment(String processDefinitionId, String resourceType,
                                 HttpServletResponse response) throws Exception {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        String resourceName = "";
        if (resourceType.equals("image")) {
            resourceName = processDefinition.getDiagramResourceName();
        } else if (resourceType.equals("xml")) {
            resourceName = processDefinition.getResourceName();
        }
        InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }

    /**
     * 到设计界面
     * @return
     */
    @RequestMapping(params = {"toFlowDesigner"})
    public ModelAndView toFlowDesigner(){
        return new ModelAndView("admin/core/workflow/designer");
    }
}