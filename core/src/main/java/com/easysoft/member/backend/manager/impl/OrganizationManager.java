package com.easysoft.member.backend.manager.impl;


import com.easysoft.member.backend.dao.IOrganizatiOnDao;
import com.easysoft.member.backend.manager.ICompanyManager;
import com.easysoft.member.backend.manager.IDepartManager;
import com.easysoft.member.backend.manager.IOrganizationManager;

import com.easysoft.member.backend.model.Company;
import com.easysoft.member.backend.model.Depart;
import com.easysoft.member.backend.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("organizatiOnService")
@Transactional
public class OrganizationManager implements IOrganizationManager {
    @Autowired
    private IOrganizatiOnDao organizatiOnDao;
    @Autowired
    private ICompanyManager companyManager;
    @Autowired
    private IDepartManager departManager;

 	
 	public void save(Organization organizatiOn) {
        organizatiOnDao.save(organizatiOn);
 	}
    public void update(Organization organizatiOn){
        organizatiOnDao.update(organizatiOn);
    }
    public List<Organization> queryForList(){
        return organizatiOnDao.queryForList();
    }

    
    public List<Organization> queryForTree(int orgid) {
        List<Company> orgList  = companyManager.queryForList();
        List<Organization> topOrgList  = new ArrayList<Organization>();
        for(Organization org :orgList){
            if(org.getPid().compareTo(orgid)==0){
                List<Organization> children = this.getChildrenForCompany(orgList, org.getId());
                org.setChildren(children);
                topOrgList.add(org);
            }
        }
        return topOrgList;
    }

    /**
     * 在一个集合中查找子
     * @param compList 所有菜单集合
     * @param pid 父id
     * @return 找到的子集合
     */
    private List<Organization> getChildrenForCompany(List<Company> compList ,int pid){
        List<Organization> children =new ArrayList<Organization>();
        for(Organization org :compList){
            if(org.getPid()==pid){
                org.setChildren(this.getChildrenForCompany(compList, org.getId()));

                children.add(org);
            }
        }
        return children;
    }

    /**
     * 在一个集合中查找子
     * @param compList 所有菜单集合
     * @param pid 父id
     * @return 找到的子集合
     */
    private List<Organization> getChildren(List<Company> compList ,int pid){
        List<Organization> children =new ArrayList<Organization>();
        for(Organization org :compList){
            if(org.getPid()==pid){
                org.setChildren(this.getChildren(compList, org.getId()));
                org.getChildren().addAll(this.getChildren4Depart(org.getId(),0));
                children.add(org);
            }
        }
        return children;
    }

    public List<Organization> getChildren4Depart(int compId,int pid){
        List<Depart> departs = departManager.queryByCompIdAndPid(compId,pid);
        List<Organization> result = new ArrayList<Organization>();
        for(Organization depart : departs){
            depart.setChildren(getChildren4Depart(compId,depart.getId()));
            result.add(depart);
        }
        return result;
    }

    public Organization queryById(Integer id){
        return organizatiOnDao.queryById(id);
    }
    public void deleteById(Integer id){
        organizatiOnDao.deleteById(id);
    }

    
    public List<Organization> queryOrganizationByTree(int pid) {
        //找出所有公司
        List<Company> orgList  = companyManager.queryForList();
        List<Organization> topOrgList  = new ArrayList<Organization>();
        for(Organization org : orgList){
            if(org.getPid()==pid){
                List<Organization> children = this.getChildren(orgList, org.getId());
                org.setChildren(children);
                org.getChildren().addAll(this.getChildren4Depart(org.getId(),0));
                topOrgList.add(org);
            }
        }
        return topOrgList;
    }

    
    public Organization queryByTypeAndId(String type, int id) {
        Organization organization = null;
        if(type.equals(Organization.OrgType.COMPANY.name())){
            organization = companyManager.queryById(id);
        }else if(type.equals(Organization.OrgType.DEPT.name())){
            organization = departManager.queryById(id);
        }
        return organization;
    }
}