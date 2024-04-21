<#assign classType="INTERFACE"/>
<#include "common.ftl">
<@pkgDeclare pkg=daoClass.pkg/>

<@imports items=daoClass.imports/>

import net.takela.common.web.dao.BaseDao;
import org.springframework.stereotype.Repository;

<@clsComment proj=project comment=""/>
@Repository
public interface ${daoClass.className} extends BaseDao<${entityClass.className}, ${searchClass.className}> {
<#list daoClass.methods as method>
    <#assign methodTypes=["add","update","get", "remove", "search"] >
    <#if methodTypes?seqContains(method.name) >
    <#else>
    /**
    * ${method.comment!}
    * @param ${method.inputClass.refName}
    */
    <@methodDeclare method=method></@methodDeclare>
    </#if>
</#list>
}