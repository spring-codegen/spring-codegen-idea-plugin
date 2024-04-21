<#include "common.ftl">
<@pkgDeclare pkg=ctrlClass.pkg/>

<@imports items=ctrlClass.imports/>
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import net.takela.common.spring.exception.ParamException;
import net.takela.common.spring.http.HttpResponse;
import net.takela.common.web.model.ListResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

<@clsComment proj=project comment=ctrlClass.comment/>
@RestController
@RequestMapping("${ctrlClass.request.path}")
public class ${ctrlClass.className}<#if ctrlClass.extend??> extends ${ctrlClass.extend.className}</#if> {
<#if ctrlClass.dependency??>

    private final ${ctrlClass.dependency.className} ${ctrlClass.dependency.refName} ;
    public ${ctrlClass.className}(${ctrlClass.dependency.className} ${ctrlClass.dependency.refName}) {
        this.${ctrlClass.dependency.refName} = ${ctrlClass.dependency.refName};
    }

</#if>
}
