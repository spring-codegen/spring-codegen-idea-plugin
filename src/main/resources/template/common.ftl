<#macro methodDeclare method>
    public <#if !method.resultListFlag>${method.outputClass.className}<#else>List<${method.outputClass.className}> </#if> ${method.name}(${method.inputClass.className} <#if method.inputClass.pkg?starts_with("java")>${method.inputClass.fields[0].name}<#else>${method.inputClass.className?uncap_first}</#if>);
</#macro>
<#macro imports items>
<#list items as item>
    <#if !item?starts_with("java.lang")>
import ${item};
    </#if>
</#list>
</#macro>
<#macro pkgDeclare cls>
package ${cls.pkg};
<@imports items=cls.imports></@imports>
</#macro>
<#macro clsComment proj comment="">
/**
* ${comment!}
* @author ${proj.author}
* @date ${.now?string["yyyy-MM-dd"]}
*/
</#macro>
