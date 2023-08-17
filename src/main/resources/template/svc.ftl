package ${svcClass.pkg};
<#list svcClass.imports as item>
    <#if !item?starts_with("java.lang")>
import ${item};
    </#if>
</#list>
import org.springframework.stereotype.Service;

/**
* @author ${project.author}
* @date ${.now?string["yyyy-MM-dd"]}
*/
public interface ${svcClass.className}{
<#list svcClass.methods as method>
    public <#if !method.resultListFlag>${method.outputClass.className}<#else>List<${method.outputClass.className}> </#if> ${method.name}(${method.inputClass.className} <#if method.inputClass.pkg?starts_with("java")>${method.inputClass.fields[0].name}<#else>${method.inputClass.className?uncap_first}</#if>);
</#list>
}
