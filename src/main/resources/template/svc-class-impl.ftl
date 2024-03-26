<#include "./common.ftl">
<@pkgDeclare pkg=svcClass.pkg+".impl"/>
<@imports items=svcClass.imports/>

import org.springframework.stereotype.Service;

<@clsComment proj=project comment=svcClass.comment/>
@Service
public class ${svcClass.className}<#if svcClass.implement??> implements ${svcClass.implement.className}</#if> {
<#if svcClass.dependency??>

    private final ${svcClass.dependency.className} ${svcClass.dependency.refName};
    public ${svcClass.className}Impl(${svcClass.dependency.className} ${svcClass.dependency.refName}) {
        this.${svcClass.dependency.refName} = ${svcClass.dependency.refName};
    }

</#if>
}
