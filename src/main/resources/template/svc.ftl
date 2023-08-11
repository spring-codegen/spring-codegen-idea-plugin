package ${svcClass.pkg};



<#list svcClass.imports as item>
    import ${item};
</#list>

/**
* @author ${author}
* @date ${.now?string["yyyy-MM-dd"]}
*/
public interface ${svcClass.className}{
<#list svcClass.methods as method>
    public ${method.outputClass.className} ${method.name}(${method.inputClass.className} <#if method.inputClass.pkg?starts_with("java")>${method.inputClass.fields[0].name}<#else>${method.inputClass.className?uncap_first}</#if>);
</#list>
}