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

<#macro argsConvert cls1 cls2>
    <#if cls1.className == cls2.className>
    <#elseif cls1.baseType>
        <#assign a=cls2.fields?filter(x -> x.name == cls1.name)>
        <#if a?size gt 0>
        ${cls2.className} ${cls2.name} = new ${cls2.className};
        ${cls2.name}.${a[0].setter}(cls1.name);
        </#if>
    <#elseif cls2.baseType>
        <#assign a=cls1.fields?filter(x -> x.name == cls2.name)>
        <#if a?size gt 0>
        ${cls2.className} ${cls2.name} = ${cls1.name}.${a[0].getter}();
        </#if>
    <#else>
        ${cls2.className} ${cls2.name} = ${cls1.name}.copyTo(${cls2.className}.class);
    </#if>
</#macro>