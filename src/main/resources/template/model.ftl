<#include "./common.ftl">
<@pkgDeclare cls=model/>


import javax.validation.constraints.NotNull;

<@clsComment proj=project comment=model.comment/>
public class ${model.className}<#if model.superClass??> implements ${model.superClass}</#if>{
<#list model.fields as field>
    /**
    * ${field.comment!}
    */
    <#if field.notNull?? && field.notNull>
    @NotNull
    </#if>
    private ${field.javaType} ${field.name};
</#list>
<#list model.fields as field>
    public ${field.javaType} ${field.getter}(){
        return this.${field.name};
    }
    public void ${field.setter}(${field.javaType} ${field.name}){
        this.${field.name} = ${field.name};
    }
</#list>
}