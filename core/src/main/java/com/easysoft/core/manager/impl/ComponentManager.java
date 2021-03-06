package com.easysoft.core.manager.impl;

import com.easysoft.core.common.dao.spring.BaseSupport;
import com.easysoft.core.component.context.WidgetContext;
import com.easysoft.core.dao.IComponentDao;
import com.easysoft.framework.component.ComponentView;
import com.easysoft.framework.component.PluginView;
import com.easysoft.framework.component.WidgetView;
import com.easysoft.framework.component.context.ComponentContext;
import com.easysoft.framework.component.plugin.IPlugin;
import com.easysoft.framework.component.plugin.IPluginBundle;
import com.easysoft.core.manager.IComponentManager;
import com.easysoft.framework.utils.SpringContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("componentManager")
public class ComponentManager extends BaseSupport
        implements IComponentManager{
    @Autowired
    private IComponentDao componentDao;

    public void startComponents() {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("start components start...");
        }

        List<ComponentView> dbList = getDbList();
        for (ComponentView componentView : dbList) {
            if ((componentView.getInstall_state() != 2) &&
                    (componentView.getEnable_state() == 1)) {
                start(componentView.getComponentid());
            }

        }
        if (this.logger.isDebugEnabled())
            this.logger.debug("start components end!");
    }


    public void saasStartComponents() {

    }

    public List<ComponentView> list()
    {
        List<ComponentView> viewList = new ArrayList<ComponentView>();
        List<ComponentView> componentViews = ComponentContext.getComponents();

        List dbList = getDbList();

        if (componentViews != null) {
            for (ComponentView view : componentViews)
            {
                ComponentView componentView = (ComponentView)view.clone();

                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("load component[" + componentView.getName() + "] start ");
                }

                try
                {
                    componentView.setInstall_state(0);
                    componentView.setEnable_state(0);

                    loadComponentState(componentView, dbList);

                    if (this.logger.isDebugEnabled())
                        this.logger.debug("load component[" + componentView.getName() + "] end ");
                }
                catch (Exception e) {
                    this.logger.error("加载组件[" + componentView.getName() + "]出错", e);
                    componentView.setEnable_state(2);
                    componentView.setError_message(e.getMessage());
                }
                viewList.add(componentView);
            }
        }
        System.out.println("component size is "+viewList.size());
        return viewList;
    }

    private void loadComponentState(ComponentView componentView, List<ComponentView> dbList)
    {
        for (ComponentView dbView : dbList)
            if (dbView.getComponentid().equals(componentView.getComponentid()))
            {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("load component[" + componentView.getName() + "]state->install state:" + dbView.getInstall_state() + ",enable_state:" + dbView.getEnable_state());
                }

                componentView.setInstall_state(dbView.getInstall_state());
                componentView.setEnable_state(dbView.getEnable_state());
                componentView.setId(dbView.getId());

                if (componentView.getInstall_state() != 2)
                {
                    if (dbView.getEnable_state() != 0)
                    {
                        if (dbView.getEnable_state() != 1)
                        {
                            componentView.setError_message(dbView.getError_message());
                        }
                    }
                }
            }
    }

    public void install(String componentid){
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("install component[" + componentid + "]...");
        }
        try{
            ComponentView componentView = getComponentView(componentid);

            if (componentView != null) {
                componentView.getComponent().install();
                if (!isInDb(componentView.getComponentid())) {
                    ComponentView temp = (ComponentView)componentView.clone();
                    temp.setInstall_state(1);
                    componentDao.save(temp);
                }
                else {
                    Map<String,Object> params = new HashMap<String,Object>();
                    params.put("install_state",1);
                    params.put("componentId",componentView.getComponentid());
                    componentDao.updateByCondition(params);

                }
            }
        } catch (RuntimeException e) {
            this.logger.error("安装组件[" + componentid + "]出错", e);
        }
    }

    private boolean isInDb(String componentid){
        return componentDao.queryComponentByCompId(componentid)!=null;
    }

    public void unInstall(String componentid){
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("install component[" + componentid + "]...");
        }

        ComponentView componentView = getComponentView(componentid);
        if (componentView != null) {
            componentView.getComponent().unInstall();

            if (!isInDb(componentView.getComponentid())) {
                ComponentView temp = (ComponentView)componentView.clone();
                temp.setInstall_state(0);
                componentDao.save( temp);
            } else {
                Map<String,Object> params = new HashMap<String,Object>();
                params.put("install_state",0);
                params.put("componentId",componentView.getComponentid());
                componentDao.updateByCondition(params);

            }
        }
    }

   
    public void start(String componentid) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("start component[" + componentid + "]...");
        }

        ComponentView componentView = getComponentView(componentid);
        List<PluginView> pluginList = componentView.getPluginList();
        IPlugin plugin;
        for (PluginView pluginView : pluginList) {
            String pluginid = pluginView.getId();
            plugin = (IPlugin) SpringContextHolder.getBean(pluginid);

            List<String> bundleList = pluginView.getBundleList();
            if (bundleList != null)
                for (String bundleId : bundleList) {
                    IPluginBundle pluginBundle = (IPluginBundle) SpringContextHolder.getBean(bundleId);
                    pluginBundle.registerPlugin(plugin);
                }
        }

        List<WidgetView> widgetList = componentView.getWidgetList();
        for (WidgetView widgetView : widgetList) {
            String widgetid = widgetView.getId();
            WidgetContext.putWidgetState(widgetid, Boolean.valueOf(true));
        }


        Map<String,Object> params = new HashMap<String, Object>();
        params.put("enable_state",1);
        params.put("install_state",null);
        params.put("componentId",componentView.getComponentid());
        componentDao.updateByCondition(params);


        if (this.logger.isDebugEnabled())
            this.logger.debug("start component[" + componentid + "] complete");
    }

    
    public void stop(String componentid) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("stop component[" + componentid + "]...");
        }

        ComponentView componentView = getComponentView(componentid);

        List<PluginView> pluginList = componentView.getPluginList();
        IPlugin plugin;
        for (PluginView pluginView : pluginList) {
            String pluginid = pluginView.getId();
            plugin = (IPlugin) SpringContextHolder.getBean(pluginid);
            List<String> bundleList = pluginView.getBundleList();
            for (String bundleId : bundleList) {
                IPluginBundle pluginBundle = (IPluginBundle) SpringContextHolder.getBean(bundleId);
                pluginBundle.unRegisterPlugin(plugin);
            }
        }

        List<WidgetView> widgetList = componentView.getWidgetList();
        for (WidgetView widgetView : widgetList) {
            String widgetid = widgetView.getId();
            WidgetContext.putWidgetState(widgetid, Boolean.valueOf(false));
        }
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("enable_state",0);
        params.put("install_state",null);
        params.put("componentId",componentView.getComponentid());
        componentDao.updateByCondition(params);


        if (this.logger.isDebugEnabled())
            this.logger.debug("stop component[" + componentid + "] complete");

    }

    private ComponentView getComponentView(String componentid)
    {
        List<ComponentView> componentList = ComponentContext.getComponents();

        for (ComponentView componentView : componentList) {
            if (componentView.getComponentid().equals(componentid)) {
                return componentView;
            }
        }

        return null;
    }



    private List<ComponentView> getDbList()
    {
        return componentDao.queryForList();
    }
}