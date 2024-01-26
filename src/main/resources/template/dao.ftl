<#include "./common.ftl">
<@pkgDeclare pkg=daoClass.pkg/>
<@imports items=daoClass.imports/>

import org.springframework.stereotype.Repository;
import java.util.List;


<@clsComment proj=project comment=""/>
@Repository
public interface ${daoClass.className} {
<#list daoClass.methods as method>
    <@methodDeclare method=method></@methodDeclare>
</#list>
}