<#include "./common.ftl">
<@pkgDeclare pkg=svcClass.pkg/>
<@imports items=svcClass.imports/>
<@clsComment proj=project comment=svcClass.comment/>
public interface ${svcClass.className}{
<#list svcClass.methods as method>
    <@methodDeclare method=method></@methodDeclare>
</#list>
}
