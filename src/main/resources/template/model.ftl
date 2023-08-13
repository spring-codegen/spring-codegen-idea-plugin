package ${model.pkg};


import javax.validation.constraints.NotNull;
<#list model.imports as impt>
import ${impt};
</#list>

/**
* ${model.comment!}
* @author ${author}
* @date ${.now?string["yyyy-MM-dd"]}
*/
public class ${model.className}{
<#list model.fields as field>
    /*
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