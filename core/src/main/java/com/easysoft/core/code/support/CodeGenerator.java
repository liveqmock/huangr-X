package com.easysoft.core.code.support;


import com.easysoft.core.code.ICallBack;
import com.easysoft.core.code.pojo.CreateFileProperty;
import com.easysoft.core.code.pojo.GenerateEntity;
import com.easysoft.core.code.support.factory.CodeFactory;
import freemarker.template.TemplateException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Map;

/**
 * User: andy
 * Date: 14-4-2
 * Time: 下午4:25
 *
 * @since:
 */
public class CodeGenerator implements ICallBack {
    private static final Log logger = LogFactory.getLog(CodeGenerator.class);
    private String entityPackage = "test";
    private String entityName = "Person";
    private String tableName = "person";
    private String tableTitle = "公告";
    public static int FIELD_ROW_NUM = 1;
    private String primaryKeyPolicy = "uuid";
    private String sequenceCode = "";
    private GenerateEntity cgformConfig;
    private static CreateFileProperty createFileProperty = new CreateFileProperty();
    public CodeGenerator(){
    }

    public CodeGenerator(CreateFileProperty createFileProperty2, GenerateEntity generateEntity){
        this.entityName = generateEntity.getEntityName();
        this.entityPackage = generateEntity.getEntityPackage();
        this.tableName = generateEntity.getTableName();
        this.tableTitle = generateEntity.getFtlDescription();
        FIELD_ROW_NUM = 1;
        createFileProperty = createFileProperty2;
        createFileProperty.setJspMode("01");
        this.primaryKeyPolicy = generateEntity.getPrimaryKeyPolicy();
        this.sequenceCode = "";
        this.cgformConfig = generateEntity;
    }
    public void generateToFile() throws TemplateException, IOException {
        logger.info("----jeap---Code----Generation----[单表模型:" + this.tableName + "]------- 生成中。。。");
        CodeFactory codeFactory = new CodeFactory();
        codeFactory.setProjectPath(this.cgformConfig.getProjectPath());
        if (this.cgformConfig.getFormEntity().getFormType() == 1)
            codeFactory.setCallBack(new CodeGenerator(createFileProperty, this.cgformConfig));
        else {
            //codeFactory.setCallBack(new CodeGenerator(this.sub, this.subG, this.subFileProperty, "uuid", this.foreignKeys));
        }

        if (createFileProperty.isJspFlag()) {
            if ("03".equals(createFileProperty.getJspMode())) {
                codeFactory.invoke("onetomany/cgform_jspSubTemplate.ftl", "jspList");
            } else {
                if ("01".equals(createFileProperty.getJspMode())) {
                    codeFactory.invoke("cgform_jspTableTemplate_add.ftl", "jsp_add");
                    codeFactory.invoke("cgform_jspTableTemplate_update.ftl", "jsp_update");
                }
                if ("02".equals(createFileProperty.getJspMode())) {
                    codeFactory.invoke("cgform_jspDivTemplate_add.ftl", "jsp_add");
                    codeFactory.invoke("cgform_jspDivTemplate_update.ftl", "jsp_update");
                }
                codeFactory.invoke("cgform_jspListTemplate.ftl", "jspList");
                codeFactory.invoke("cgform_jsListEnhanceTemplate.ftl", "jsList");
                codeFactory.invoke("cgform_jsEnhanceTemplate.ftl", "js");
            }
        }
        if (createFileProperty.isServiceImplFlag()) {
            codeFactory.invoke("cgform_serviceImplTemplate.ftl", "serviceImpl");
        }
        if (createFileProperty.isServiceIFlag()) {
            codeFactory.invoke("cgform_serviceITemplate.ftl", "service");
        }
        if (createFileProperty.isActionFlag()) {
            codeFactory.invoke("cgform_controllerTemplate.ftl", "controller");
        }
        if (createFileProperty.isEntityFlag())
        {
            codeFactory.invoke("cgform_entityTemplate.ftl", "entity");
        }
        logger.info("----jeecg----Code----Generation-----[单表模型：" + this.tableName + "]------ 生成完成。。。");
    }
    @Override
    public Map<String, Object> execute(){
        return null;
    }

}