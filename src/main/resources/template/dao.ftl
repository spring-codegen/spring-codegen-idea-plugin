<#include "./common.ftl">
<@pkgDeclare pkg=daoClass.pkg/>

<@imports items=daoClass.imports/>

import com.cmit.paas.common.web.dao.BaseDao;
import org.springframework.stereotype.Repository;

<@clsComment proj=project comment=""/>
@Repository
public interface ${daoClass.className} extends BaseDao<${entityClass.className}, ${searchClass.className}> {
<#--<#list daoClass.methods as method>-->
<#--    <@methodDeclare method=method></@methodDeclare>-->
<#--</#list>-->
}