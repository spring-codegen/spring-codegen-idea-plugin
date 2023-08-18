<#include "./common.ftl">
<@pkgDeclare cls=svcClass/>

<@clsComment proj=project comment=svcClass.comment/>
public interface ${svcClass.className}{
<#list svcClass.methods as method>
    <@methodDeclare method=method></@methodDeclare>
</#list>
}
