<#include "./common.ftl">
<@pkgDeclare pkg=model.pkg/>

import jakarta.validation.constraints.*;

<@imports items=model.imports/>

<@clsComment proj=project comment=model.comment/>
public class ${model.className}<#if model.extend??> extends ${model.extend.className}</#if> {

<#list model.fields as field>
    /**
     * ${field.comment!}
     */
    <#if validator>
    <#if field.notNull?? && field.notNull>
    @NotNull()
    </#if>
    <#if field.maxLen gt 0>
    @Size(max=${field.maxLen})
    </#if>
    </#if>
    private ${field.javaType} ${field.name};
</#list>

<#list model.fields as field>
    public ${field.javaType} ${field.getter}() {
        return this.${field.name};
    }

    public void ${field.setter}(${field.javaType} ${field.name}) {
        this.${field.name} = ${field.name};
    }

</#list>
}