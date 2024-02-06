<#assign DEBUG=false/>
<#macro methodDeclare method>
    <#if classType != "INTERFACE">public </#if><#if !method.resultListFlag>${method.outputClass.className}<#else>List<${method.outputClass.className}></#if> ${method.name}(${method.inputClass.className} ${method.inputClass.refName});
</#macro>
<#macro imports items>
<#list items as item>
    <#if !item?starts_with("java.lang")>
import ${item};
    </#if>
</#list>
</#macro>
<#--package 和 import定义-->
<#macro pkgDeclare pkg>
package ${pkg};
</#macro>
<#macro clsComment proj comment="">
/**
 * ${comment!}
 *
 * @author ${proj.author}
 * @date ${.now?string["yyyy-MM-dd"]}
 */
</#macro>
<#macro checkParam>
        if (br.hasErrors()){
            throw new ParamException( br.getFieldError().getField() + br.getFieldError().getDefaultMessage() );
        }
</#macro>
<#macro argsConvert cls1 cls2>
    <#if cls1.className == cls2.className>
    <#elseif cls1.baseType>
        <#assign a=cls2.fields?filter(x -> x.refName == cls1.refName)>
        <#if a?size gt 0>
        ${cls2.className} ${cls2.refName} = new ${cls2.className};
        ${cls2.refName}.${a[0].setter}(cls1.refName);
        </#if>
    <#elseif cls2.baseType>
        <#assign a=cls1.fields?filter(x -> x.refName == cls2.refName)>
        <#if a?size gt 0>
        ${cls2.className} ${cls2.refName} = ${cls1.refName}.${a[0].getter}();
        </#if>
    <#else>
        ${cls2.className} ${cls2.refName} = ${cls1.refName}.copyTo(${cls2.className}.class);
    </#if>
</#macro>
<#macro methodCall cls1 method1 cls2 method2>
    <#assign args=method2.inputClass.refName>
    <#if method1.inputClass.className == method2.inputClass.className>
        <#if DEBUG>
            1
        </#if>
    <#elseif method1.inputClass.isBaseType()>
        <#if DEBUG>
            2
        </#if>
        <#assign a= method2.inputClass.fields?filter(x -> x.name == method1.inputClass.refName)>
        <#if a?size gt 0>
        ${method2.inputClass.className} ${method2.inputClass.refName} = new ${method2.inputClass.className}();
        ${method2.inputClass.refName}.${a[0].setter}(method1.inputClass.refName);
            <#assign args=method2.inputClass.refName>
        </#if>
    <#elseif method2.inputClass.isBaseType()>
        <#if DEBUG>
            3
        </#if>
        <#assign a=method1.inputClass.fields?filter(x -> x.name == method2.inputClass.refName)>
        <#if a?size gt 0>
        ${method2.inputClass.className} ${method2.inputClass.refName} = ${method1.inputClass.refName}.${a[0].getter}();
            <#assign args=method2.inputClass.refName>
        </#if>
    <#else>
        <#if DEBUG>
            4
        </#if>
        ${method2.inputClass.className} ${method2.inputClass.refName} = ${method1.inputClass.refName}.copyTo(${method2.inputClass.className}.class);
        <#assign args=method2.inputClass.refName>
    </#if>

    <#if method1.outputClass.className == method2.outputClass.className>
        <#if DEBUG>
            5
        </#if>
        ${method2.outputClass.className} result = ${cls2.refName}.${method2.name}(${args});
    <#elseif method2.outputClass.isBaseType()>
        <#if DEBUG>
            6
        </#if>
        ${method2.outputClass.className} ${method2.outputClass.refName} = ${cls2.refName}.${method2.name}(${args});
        <#assign a=method1.outputClass.fields?filter(x -> x.name == method2.outputClass.refName)>
        <#if a?size gt 0>
        ${method1.outputClass.className} result = new ${method1.outputClass.className}();
        result.${a[0].setter}(${method2.outputClass.refName});
        <#elseif method2.outputClass.className=="Boolean">
            <#if DEBUG>
                6.1
            </#if>
        </#if>
    <#elseif method1.outputClass.isBaseType()>
        <#if DEBUG>
            7
        </#if>
        ${method2.outputClass.className} ${method2.outputClass.refName} = ${cls2.refName}.${method2.name}(${args});
        <#assign a=method2.outputClass.fields?filter(x -> x.name == method1.outputClass.refName)>
        <#if a?size gt 0>
        ${method1.outputClass.className} result = ${method2.outputClass.refName}.${a[0].getter}();
        </#if>
    <#else>
        <#if DEBUG>
            8
        </#if>
        ${method1.outputClass.className} result = ${method2.outputClass.refName}.copyTo(${method1.outputClass.className}.class);
    </#if>
</#macro>


