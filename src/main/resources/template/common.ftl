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
<#macro methodCall cls1 method1 cls2 method2>
        <#assign args=method2.inputClass.name>
    <#if method1.inputClass.className == method2.inputClass.className>
    <#elseif method1.inputClass.baseType>
        <#assign a= method2.inputClass.fields?filter(x -> x.name == method1.inputClass.name)>
        <#if a?size gt 0>
            ${method2.inputClass.className} ${method2.inputClass.name} = new ${method2.inputClass.className}();
            ${method2.inputClass.name}.${a[0].setter}(method1.inputClass.name);
            <#assign args=method2.inputClass.name>
        </#if>
    <#elseif method2.inputClass.baseType>
        <#assign a=method1.inputClass.fields?filter(x -> x.name == method2.inputClass.name)>
        <#if a?size gt 0>
            ${method2.inputClass.className} ${method2.inputClass.name} = ${method1.inputClass.name}.${a[0].getter}();
            <#assign args=method2.inputClass.name>
        </#if>
    <#else>
        ${method2.inputClass.className} ${method2.inputClass.name} = ${method1.inputClass.name}.copyTo(${method2.inputClass.className}.class);
        <#assign args=method2.inputClass.name>
    </#if>

    <#if method1.outputClass.className == method2.outputClass.className>
        ${method2.outputClass.className} result = ${cls2.name}.${method2.name}(${args});
    <#elseif method2.outputClass.baseType>
        <#assign a=method1.outputClass.fields?filter(x -> x.name == method2.outputClass.name)>
        <#if a?size gt 0>
            ${method2.outputClass.className} ${method2.outputClass.name} = ${cls2.name}.${method2.name}(${args});
            ${method1.outputClass.className} result = new ${method1.outputClass.className}();
            result.${a[0].setter}(${method2.outputClass.name});
        </#if>
    <#elseif method1.outputClass.baseType>
        <#assign a=method2.outputClass.fields?filter(x -> x.name == method1.outputClass.name)>
        <#if a?size gt 0>
            ${method2.outputClass.className} ${method2.outputClass.name} = ${cls2.name}.${method2.name}(${args});
            ${method1.outputClass.className} result = ${method2.outputClass.name}.${a[0].getter}();
        </#if>
    <#else>
        ${method1.outputClass.className} result = ${method2.outputClass.name}.copyTo(${method1.outputClass.className}.class);
    </#if>
</#macro>


