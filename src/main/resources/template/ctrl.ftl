<#include "./common.ftl">
<@pkgDeclare pkg=ctrlClass.pkg/>
<@imports items=ctrlClass.imports/>
import com.cmit.paas.common.spring.exception.ParamException;


import org.springframework.validation.annotation.Validated;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.cmit.paas.common.spring.http.HttpResponse;
import com.cmit.paas.common.web.model.ListResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

<@clsComment proj=project comment=ctrlClass.comment/>

@RestController
@RequestMapping("${ctrlClass.request.path}")
public class ${ctrlClass.className}<#if ctrlClass.extend??> extends ${ctrlClass.extend.className}</#if>{
<#if ctrlClass.dependency??>
    private final ${ctrlClass.dependency.className} ${ctrlClass.dependency.refName} ;
    public ${ctrlClass.className}(${ctrlClass.dependency.className} ${ctrlClass.dependency.refName}) {
        this.${ctrlClass.dependency.refName} = ${ctrlClass.dependency.refName};
    }
</#if>
<#list ctrlClass.methods as method>
    /**
    * ${method.comment!}
    * @param ${method.inputClass.refName}
    */
    @RequestMapping(path="${method.request.path}", method=RequestMethod.${method.request.httpMethod})
    <#--返回非列表-->
    <#if !method.resultListFlag>
    public HttpResponse<#if method.outputClass.className!="-"><${method.outputClass.className}></#if> ${method.name}(@Validated <#if method.request.httpMethod!="GET">@RequestBody</#if> ${method.inputClass.className} ${method.inputClass.refName}, BindingResult br){
        <@checkParam />
        HttpResponse<#if method.outputClass.className!="-"><${method.outputClass.className}></#if> res = new HttpResponse();
        <#if method.dependency??>
            <@methodCall cls1=ctrlClass method1=method cls2=svcClass method2=method.dependency/>
            <#if method.outputClass.className!="-">
        res.setData(result);
            </#if>
        </#if>
        return res;
    }
    <#elseif method.paged>
    public HttpResponse<ListResult<${method.outputClass.className}>> ${method.name}(@Validated <#if method.request.httpMethod!="GET">@RequestBody</#if> ${method.inputClass.className} ${method.inputClass.refName}, BindingResult br){
        <@checkParam />
        HttpResponse<ListResult<${method.outputClass.className}>> res = new HttpResponse();
        PageHelper.startPage(${method.inputClass.refName}.getPageNum(), ${method.inputClass.refName}.getPageSize());
        <#if method.dependency??>
            <@argsConvert cls1=method.inputClass cls2=method.dependency.inputClass/>
        <#if method.dependency.outputClass.className != method.outputClass.className>
        List<${method.dependency.outputClass.className}> ${method.dependency.outputClass.refName}s = ${svcClass.refName}.${method.dependency.name}(${method.dependency.inputClass.refName});
        List<${method.outputClass.className}> items = ${method.dependency.outputClass.refName}s.stream().map(e -> e.copyTo(${method.outputClass.className}.class).toList();
        <#elseif method.outputClass.className!="-">
        List<${method.outputClass.className}> items = ${svcClass.refName}.${method.dependency.name}(${method.dependency.inputClass.refName});
        </#if>
        ListResult<${method.outputClass.className}> result = new ListResult<>(items == null ? 0 : (int)( (Page)items).getTotal(), ((Page)items).getPageSize(), ((Page)items).getPageNum(), items);
            <#if method.outputClass.className!="-">
        res.setData(result);
            </#if>
        </#if>
        return res;
    }
    <#else>
    public HttpResponse<List<${method.outputClass.className}>> ${method.name}(@Validated <#if method.request.httpMethod!="GET">@RequestBody</#if> ${method.inputClass.className} ${method.inputClass.refName}, BindingResult br){
        <@checkParam />
        HttpResponse<List<${method.outputClass.className}>> res = new HttpResponse();
        <#if method.dependency??>
            <@argsConvert cls1=method.inputClass cls2=method.dependency.inputClass/>
            <#if method.dependency.outputClass.className != method.outputClass.className>
        List<${method.dependency.outputClass.className}> ${method.dependency.outputClass.refName}s = ${svcClass.refName}.${method.dependency.name}(${method.dependency.inputClass.refName});
        List<${method.outputClass.className}> items = ${method.dependency.outputClass.refName}s.stream().map(e -> e.copyTo(${method.outputClass.className}.class).toList();
            <#elseif method.outputClass.className!="-">
        List<${method.outputClass.className}> items = ${svcClass.refName}.${method.dependency.name}(${method.dependency.inputClass.refName});
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
