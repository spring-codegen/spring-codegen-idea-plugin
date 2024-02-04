<#include "./common.ftl">
<@pkgDeclare pkg=svcClass.pkg+".impl"/>
<@imports items=svcClass.imports/>

import org.springframework.stereotype.Service;

<@clsComment proj=project comment=svcClass.comment/>
@Service
public class ${svcClass.className}Impl<#if svcClass.implement??> implements ${svcClass.implement.className}</#if> {
<#if svcClass.dependency??>

    private final ${svcClass.dependency.className} ${svcClass.dependency.refName} ;
    public ${svcClass.className}Impl(${svcClass.dependency.className} ${svcClass.dependency.refName}) {
        this.${svcClass.dependency.refName} = ${svcClass.dependency.refName};
    }

</#if>
<#list svcClass.methods as method>
    /**
    * ${method.comment!}
    * @param ${method.inputClass.refName}
    */
    @Override
    <#if !method.resultListFlag>
    public ${method.outputClass.className} ${method.name}(${method.inputClass.className} ${method.inputClass.refName}) {
        <#if method.dependency??>
            <@methodCall cls1=svcClass method1=method cls2=daoClass method2=method.dependency/>
        </#if>
        return result;
    }
    <#elseif method.paged>
    <#else>
    public List<${method.outputClass.className}> ${method.name}(${method.inputClass.className} ${method.inputClass.refName}) {
        <#if method.dependency??>
            <#assign daoArgs = method.inputClass.refName>
            <#if method.dependency.inputClass.className != method.inputClass.className>
        ${method.dependency.inputClass.className} ${method.dependency.inputClass.refName} = ${method.inputClass.refName}.copyTo(${method.dependency.inputClass.className}.class);
                <#assign daoArgs=method.dependency.inputClass.refName>
            </#if>
            <#if method.dependency.outputClass.className != method.outputClass.className>
        List<${method.dependency.outputClass.className}> ${method.dependency.outputClass.refName}s = ${daoClass.refName}.${method.dependency.name}(${daoArgs});
        List<${method.outputClass.className}> items = ${method.dependency.outputClass.refName}s.stream().map(e -> e.copyTo(${method.outputClass.className}.class).toList();
            <#elseif method.outputClass.className!="-">
        List<${method.outputClass.className}> items = ${daoClass.refName}.${method.dependency.name}(${daoArgs});
            </#if>
        </#if>
        return items;
    }
    </#if>

</#list>
}
