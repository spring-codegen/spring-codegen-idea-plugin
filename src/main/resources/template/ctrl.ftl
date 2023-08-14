package ${ctrlClass.pkg};


import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

<#list ctrlClass.imports as impt>
import ${impt};
</#list>

/**
* ${ctrlClass.comment!}
* @author ${author}
* @date ${.now?string["yyyy-MM-dd"]}
*/
@RestController
@RequestMapping("${ctrlClass.request.path}")
public class ${ctrlClass.className} {
private final ${svcClass.className} ${svcClass.name} ;
    public ApiController(${svcClass.className} ${svcClass.name}) {
        this.${svcClass.name} = ${svcClass.name};
    }
<#list ctrlClass.methods as method>
    /**
    * ${method.comment!}
    * @param ${method.inputClass.name}
    */
    @RequestMapping(path="${ctrlClass.request.path}", method=RequestMapping.${method.request.httpMethod})
    <#if !method.resultListFlag>
    public HttpResponse<${method.outputClass.className}> ${method.name}(${method.inputClass.className} ${method.inputClass.name}){
        HttpResponse<${method.outputClass.className}> res = new HttpResponse();
        <#if method.dependency??>
            <#assign svcArgs = method.inputClass.name>
            <#if method.dependency.inputClass.className != method.inputClass.className>
        ${method.dependency.inputClass.className} ${method.dependency.inputClass.name} = ${method.inputClass.name}.copyTo(${method.dependency.inputClass.className}.class);
                <#assign svcArgs=method.dependency.inputClass.name>
            </#if>
            <#if method.dependency.outputClass.className != method.outputClass.className>
        ${method.dependency.outputClass.className} ${method.dependency.outputClass.name} = ${svcClass.name}.${method.dependency.name}(${svcArgs});
                <#if method.outputClass.className!="-">
        ${method.outputClass.className} data = ${method.dependency.outputClass.name}.copyTo(${method.outputClass.className}.class);
                </#if>
            <#elseif method.outputClass.className!="-">
        ${method.outputClass.className} data = ${svcClass.name}.${method.dependency.name}(${svcArgs});
            </#if>
            <#if method.outputClass.className!="-">
        res.setData(data);
            </#if>
        </#if>
        return res;
    }
    <#elseif method.paged>
        public HttpResponse<ListResult<${method.outputClass.className}>> ${method.name}(${method.inputClass.className} ${method.inputClass.name}){
        HttpResponse<ListResult<${method.outputClass.className}>> res = new HttpResponse();
        <#if method.dependency??>
            <#assign svcArgs = method.inputClass.name>
            <#if method.dependency.inputClass.className != method.inputClass.className>
        ${method.dependency.inputClass.className} ${method.dependency.inputClass.name} = ${method.inputClass.name}.copyTo(${method.dependency.inputClass.className}.class);
                <#assign svcArgs=method.dependency.inputClass.name>
            </#if>
        Integer totalCount = ${svcClass.name}.get${method.dependency.name?cap_first}Count(${svcArgs});
        List<${method.outputClass.className}> items = null;
        if( totalCount > 0 ){
            <#if method.dependency.outputClass.className != method.outputClass.className>
            List<${method.dependency.outputClass.className}> ${method.dependency.outputClass.name}s = ${svcClass.name}.${method.dependency.name}(${svcArgs});
            items = ${method.dependency.outputClass.name}s.stream().map(e -> e.copyTo(${method.outputClass.className}.class).toList();
            <#elseif method.outputClass.className!="-">
            items = ${svcClass.name}.${method.dependency.name}(${svcArgs});
            </#if>
        }
        ListResult<${method.outputClass.className}> result = new ListResult<>(totalCount, ${method.inputClass.name}.getPageSize(), ${method.inputClass.name}.getOffset(), items);
    <#if method.outputClass.className!="-">
        res.setData(items);
            </#if>
        </#if>
        return res;
    }
    <#else>
    public HttpResponse<List<${method.outputClass.className}>> ${method.name}(${method.inputClass.className} ${method.inputClass.name}){
        HttpResponse<List<${method.outputClass.className}>> res = new HttpResponse();
        <#if method.dependency??>
            <#assign svcArgs = method.inputClass.name>
            <#if method.dependency.inputClass.className != method.inputClass.className>
        ${method.dependency.inputClass.className} ${method.dependency.inputClass.name} = ${method.inputClass.name}.copyTo(${method.dependency.inputClass.className}.class);
                <#assign svcArgs=method.dependency.inputClass.name>
            </#if>
            <#if method.dependency.outputClass.className != method.outputClass.className>
        List<${method.dependency.outputClass.className}> ${method.dependency.outputClass.name}s = ${svcClass.name}.${method.dependency.name}(${svcArgs});
        List<${method.outputClass.className}> items = ${method.dependency.outputClass.name}s.stream().map(e -> e.copyTo(${method.outputClass.className}.class).toList();

            <#elseif method.outputClass.className!="-">
        List<${method.outputClass.className}> items = ${svcClass.name}.${method.dependency.name}(${svcArgs});
            </#if>
            <#if method.outputClass.className!="-">
        res.setData(items);
            </#if>
        </#if>
        return res;
    }
    </#if>

</#list>
}
