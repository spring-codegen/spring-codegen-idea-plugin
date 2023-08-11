package ${daoClass.pkg};

<#assign baseTypes=["Integer","Long","Boolean", "String", "Date", "BigDecimal", "Decimal"] >
<#macro var s >${s[0]?capitalize}${s[1]}</#macro>
import org.springframework.stereotype.Repository;
import java.util.List;

/**
* @author ${author}
* @date ${.now?string["yyyy-MM-dd"]}
*/
@Repository
public interface ${daoClass.className} {
<#list daoClass.methods as method>
    public ${method.outputClass.className} ${method.name}(${method.inputClass.className} ${method.inputClass.className?uncap_first});
</#list>
}