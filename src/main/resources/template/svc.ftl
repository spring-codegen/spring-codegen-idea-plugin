<#assign classType="INTERFACE"/>
<#include "./common.ftl">
<@pkgDeclare pkg=svcClass.pkg/>

<@imports items=svcClass.imports/>

<@clsComment proj=project comment=svcClass.comment/>
public interface ${svcClass.className}<#if svcClass.extend??> extends ${svcClass.extend.className}</#if> {

<#list svcClass.methods as method>
    /**
     * ${method.comment!}
     * @param ${method.inputClass.refName}
     */
    <@methodDeclare method=method></@methodDeclare>
</#list>
}
