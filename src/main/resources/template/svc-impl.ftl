package ${svcClassImpl.pkg};


import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

<#list svcClassImpl.imports as impt>
    import ${impt};
</#list>

/**
* ${svcClassImpl.comment!}
* @author ${author}
* @date ${.now?string["yyyy-MM-dd"]}
*/
public class ${svcClassImpl.className} {
    private final ${daoClass.className} ${daoClass.name} ;
    public ApiController(${daoClass.className} ${daoClass.name}) {
        this.${daoClass.name} = ${daoClass.name};
    }
<#list svcClassImpl.methods as method>
    /**
    * ${method.comment!}
    * @param ${method.inputClass.name}
    */
    <#if !method.resultListFlag>
    public ${method.outputClass.className} ${method.name}(${method.inputClass.className} ${method.inputClass.name}){
        <#if method.dependency??>
            <#assign daoArgs = method.inputClass.name>
            <#if method.dependency.inputClass.className != method.inputClass.className>
        ${method.dependency.inputClass.className} ${method.dependency.inputClass.name} = ${method.inputClass.name}.copyTo(${method.dependency.inputClass.className}.class);
                <#assign daoArgs=method.dependency.inputClass.name>
            </#if>
            <#if method.dependency.outputClass.className != method.outputClass.className>
        ${method.dependency.outputClass.className} ${method.dependency.outputClass.name} = ${daoClass.name}.${method.dependency.name}(${daoArgs});
                <#if method.outputClass.className!="-">
        ${method.outputClass.className} ${method.outputClass.name} = ${method.dependency.outputClass.name}.copyTo(${method.outputClass.className}.class);
                </#if>
            <#elseif method.outputClass.className!="-">
        ${method.outputClass.className} ${method.outputClass.name} = ${daoClass.name}.${method.dependency.name}(${daoArgs});
            </#if>
        </#if>
        return ${method.outputClass.name};
    }
    <#elseif method.paged>
    <#else>
    public List<${method.outputClass.className}> ${method.name}(${method.inputClass.className} ${method.inputClass.name}){
        <#if method.dependency??>
            <#assign daoArgs = method.inputClass.name>
            <#if method.dependency.inputClass.className != method.inputClass.className>
        ${method.dependency.inputClass.className} ${method.dependency.inputClass.name} = ${method.inputClass.name}.copyTo(${method.dependency.inputClass.className}.class);
                <#assign daoArgs=method.dependency.inputClass.name>
            </#if>
            <#if method.dependency.outputClass.className != method.outputClass.className>
        List<${method.dependency.outputClass.className}> ${method.dependency.outputClass.name}s = ${daoClass.name}.${method.dependency.name}(${daoArgs});
        List<${method.outputClass.className}> items = ${method.dependency.outputClass.name}s.stream().map(e -> e.copyTo(${method.outputClass.className}.class).toList();

            <#elseif method.outputClass.className!="-">
        List<${method.outputClass.className}> items = ${daoClass.name}.${method.dependency.name}(${daoArgs});
            </#if>
        </#if>
        return items;
    }
    </#if>

</#list>
}
