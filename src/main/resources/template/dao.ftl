package ${daoClass.pkg};

<#list daoClass.imports as item>
    <#if !item?starts_with("java.lang")>
import ${item};
    </#if>
</#list>
import org.springframework.stereotype.Repository;
import java.util.List;

/**
* @author ${project.author}
* @date ${.now?string["yyyy-MM-dd"]}
*/
@Repository
public interface ${daoClass.className} {
<#list daoClass.methods as method>
    public ${method.outputClass.className} ${method.name}(${method.inputClass.className} ${method.inputClass.className?uncap_first});
</#list>
}