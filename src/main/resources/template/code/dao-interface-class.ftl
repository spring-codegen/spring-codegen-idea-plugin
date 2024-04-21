<#assign classType="INTERFACE"/>
<#include "common.ftl">
<@pkgDeclare pkg=daoClass.pkg/>

<@imports items=daoClass.imports/>

import net.takela.common.web.dao.BaseDao;
import org.springframework.stereotype.Repository;

<@clsComment proj=project comment=""/>
@Repository
public interface ${daoClass.className}<#if daoClass.extend??> extends ${daoClass.extend.className}</#if><${entityClass.className}, ${searchClass.className}> {
<#list daoClass.methods as method>
</#list>
}