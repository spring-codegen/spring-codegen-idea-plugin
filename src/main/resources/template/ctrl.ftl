<#include "./common.ftl">
<@pkgDeclare cls=ctrlClass/>


import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.cmit.paas.common.spring.http.HttpResponse;

<@clsComment proj=project comment=ctrlClass.comment/>

@RestController
@RequestMapping("${ctrlClass.request.path}")
public class ${ctrlClass.className}<#if ctrlClass.extends??> extends ${ctrlClass.superClass.className}</#if>{
<#if ctrlClass.dependency??>
    private final ${ctrlClass.dependency.className} ${ctrlClass.dependency.name} ;
    public ${ctrlClass.className}(${ctrlClass.dependency.className} ${ctrlClass.dependency.name}) {
        this.${ctrlClass.dependency.name} = ${ctrlClass.dependency.name};
    }
</#if>
<#list ctrlClass.methods as method>
    /**
    * ${method.comment!}
    * @param ${method.inputClass.name}
    */
    @RequestMapping(path="${ctrlClass.request.path}", method=RequestMethod.${method.request.httpMethod})
    <#if !method.resultListFlag>
    public HttpResponse<#if method.outputClass.className!="-"><${method.outputClass.className}></#if> ${method.name}(${method.inputClass.className} ${method.inputClass.name}){
        HttpResponse<#if method.outputClass.className!="-"><${method.outputClass.className}></#if> res = new HttpResponse();
        <#if method.dependency??>
<#--            <#assign svcArgs = method.inputClass.name>-->
<#--            <#if method.dependency.inputClass.className != method.inputClass.className>-->
<#--        ${method.dependency.inputClass.className} ${method.dependency.inputClass.name} = ${method.inputClass.name}.copyTo(${method.dependency.inputClass.className}.class);-->
<#--                <#assign svcArgs=method.dependency.inputClass.name>-->
<#--            </#if>-->
<#--            cls1=${method.inputClass.baseType} cls2=${method.dependency.inputClass.baseType}-->

<#--            <@argsConvert cls1=method.inputClass cls2=method.dependency.inputClass/>-->
<#--            <#if method.dependency.outputClass.className != method.outputClass.className>-->
<#--        ${method.dependency.outputClass.className} ${method.dependency.outputClass.name} = ${svcClass.name}.${method.dependency.name}(${method.dependency.inputClass.name});-->
<#--                <#if method.outputClass.className!="-">-->
<#--        ${method.outputClass.className} data = ${method.dependency.outputClass.name}.copyTo(${method.outputClass.className}.class);-->
<#--                </#if>-->
<#--            <#elseif method.outputClass.className!="-">-->
<#--        ${method.outputClass.className} data = ${svcClass.name}.${method.dependency.name}(${method.dependency.inputClass.name});-->
<#--            </#if>-->
            <@methodCall cls1=ctrlClass method1=method cls2=svcClass method2=method.dependency/>
            <#if method.outputClass.className!="-">
        res.setData(result);
            </#if>
        </#if>
        return res;
    }
    <#elseif method.paged>
    public HttpResponse<ListResult<${method.outputClass.className}>> ${method.name}(${method.inputClass.className} ${method.inputClass.name}){
        HttpResponse<ListResult<${method.outputClass.className}>> res = new HttpResponse();
        <#if method.dependency??>
            <@argsConvert cls1=method.inputClass cls2=method.dependency.inputClass/>
        Integer totalCount = ${svcClass.name}.get${method.dependency.name?cap_first}Count(${method.dependency.inputClass.name});
        List<${method.outputClass.className}> items = null;
        if( totalCount > 0 ){
            <#if method.dependency.outputClass.className != method.outputClass.className>
            List<${method.dependency.outputClass.className}> ${method.dependency.outputClass.name}s = ${svcClass.name}.${method.dependency.name}(${method.dependency.inputClass.name});
            items = ${method.dependency.outputClass.name}s.stream().map(e -> e.copyTo(${method.outputClass.className}.class).toList();
            <#elseif method.outputClass.className!="-">
            items = ${svcClass.name}.${method.dependency.name}(${method.dependency.inputClass.name});
            </#if>
        }
        ListResult<${method.outputClass.className}> result = new ListResult<>(totalCount, ${method.inputClass.name}.getPageSize(), ${method.inputClass.name}.getOffset(), items);
            <#if method.outputClass.className!="-">
        res.setData(result);
            </#if>
        </#if>
        return res;
    }
    <#else>
    public HttpResponse<List<${method.outputClass.className}>> ${method.name}(${method.inputClass.className} ${method.inputClass.name}){
        HttpResponse<List<${method.outputClass.className}>> res = new HttpResponse();
        <#if method.dependency??>

            <@argsConvert cls1=method.inputClass cls2=method.dependency.inputClass/>
            <#if method.dependency.outputClass.className != method.outputClass.className>
        List<${method.dependency.outputClass.className}> ${method.dependency.outputClass.name}s = ${svcClass.name}.${method.dependency.name}(${method.dependency.inputClass.name});
        List<${method.outputClass.className}> items = ${method.dependency.outputClass.name}s.stream().map(e -> e.copyTo(${method.outputClass.className}.class).toList();

            <#elseif method.outputClass.className!="-">
        List<${method.outputClass.className}> items = ${svcClass.name}.${method.dependency.name}(${method.dependency.inputClass.name});
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
