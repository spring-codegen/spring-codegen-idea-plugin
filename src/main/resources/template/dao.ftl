<#include "./common.ftl">
<@pkgDeclare cls=daoClass/>

import org.springframework.stereotype.Repository;
import java.util.List;


<@clsComment proj=project comment=""/>
@Repository
public interface ${daoClass.className} {
<#list daoClass.methods as method>
    public <#if !method.resultListFlag>${method.outputClass.className}<#else>List<${method.outputClass.className}> </#if> ${method.name}(${method.inputClass.className} <#if method.inputClass.pkg?starts_with("java")>${method.inputClass.fields[0].name}<#else>${method.inputClass.className?uncap_first}</#if>);
</#list>
}