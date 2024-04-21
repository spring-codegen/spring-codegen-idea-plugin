<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${daoClass.pkg}.${daoClass.className}">

<#assign baseTypes=["Integer","Long","Boolean", "String", "Date", "BigDecimal", "Decimal"] >
<#list resultMaps as k,resultMap>
    <#if baseTypes?seqContains(resultMap.className) >
    <#else>
    <resultMap id="${resultMap.className}" type="${resultMap.pkg}.${resultMap.className}">
        <#list resultMap.fields as field>
            <#if field.name=="id">
        <id column="${field.column}" property="${field.name}"/>
            <#else>
        <result column="${field.column}" property="${field.name}" <#if field.javaType == "Map">typeHandler="net.takela.common.spring.mybatis.typehandler.JsonObjectTypeHandler" </#if>/>
            </#if>
        </#list>
    </resultMap>
    </#if>
</#list>

<#list daoClass.methods as method>
    <#assign columns = method.sqlDataFields?map(field -> field.column)>
    <#assign values = method.sqlDataFields?map(field -> "#{"+field.name+"}")>
    <#assign conds = method.sqlCondFields?map(field -> field.column+"=#{"+field.name+"}")>
    <#if method.type == "add">
    <insert id="${method.name}" useGeneratedKeys="true" keyProperty="id" keyColumn="id">
        INSERT INTO ${daoClass.tableName}(${columns?join(", ")})
<#--        values (${values?join(", ")})-->
            values (<#list method.sqlDataFields as field><#if field_index gt 0>, </#if><#if field.name == "createTime" || field.name == "updateTime">NOW()<#else>${r'#{'}${field.name}<#if field.javaType=="Map">, typeHandler=net.takela.common.spring.mybatis.typehandler.JsonObjectTypeHandler</#if>${r'}'}</#if></#list>)
    </insert>
    </#if>
    <#if method.type == "get">
    <select id="${method.name}" <#if baseTypes?seqContains(method.outputClass.className)>resultType="${method.outputClass.className}"<#else>resultMap="${method.outputClass.className}"</#if> >
        SELECT ${columns?join(", ")}
        FROM ${daoClass.tableName}
        WHERE <#if baseTypes?seqContains(method.inputClass.className)>${conds?join(" AND ")}</#if>
    </select>
    </#if>
    <#if method.type == "update">
    <update id="${method.name}" >
        UPDATE ${daoClass.tableName}
        SET
            <trim suffixOverrides=",">
            <#list method.sqlDataFields as field>
                <#if field.column == "update_time">
                ${field.column}=NOW(),
                <#else>
                <if test="${field.name} != null">
                    ${field.column}=${r'#{'}${field.name}<#if field.javaType=="Map">, typeHandler=net.takela.common.spring.mybatis.typehandler.JsonObjectTypeHandler</#if>${r'}'},
                </if>
                </#if>
            </#list>
            </trim>
        WHERE
            ${conds?join(" AND ")}
    </update>
    </#if>
    <#if method.type == "remove">
        <#if method.sqlDataFields?? && ( method.sqlDataFields?size gt 0) >
    <update id="${method.name}" >
        UPDATE ${daoClass.tableName}
        SET
        <#list method.sqlDataFields as field>
            <#if field_index == 0>
            ${field.column}=1
            </#if>
        </#list>
        WHERE
            ${conds?join(" AND ")}
    </update>
        <#else>
    <delete id="${method.name}" >
        DELETE FROM ${daoClass.tableName}
        WHERE
            ${conds?join(" AND ")}
    </delete>
        </#if>
    </#if>
    <#if method.type == "search">
    <sql id="${method.name}Cond">
        <where>
            <trim prefixOverrides="AND">
            <#list method.sqlCondFields as field>
                <#if field.javaType == "String">
                <if test="${field.name}!=null and !${field.name}.isEmpty()">
                    AND ${field.column}=${r'#{'}${field.name}${r'}'}
                </if>
                <#else>
                <if test="${field.name}!=null">
                    AND ${field.column}=${r'#{'}${field.name}${r'}'}
                </if>
                </#if>
            </#list>
        </trim>
        </where>
    </sql>
    <select id="${method.name}" <#if baseTypes?seqContains(method.outputClass.className)>resultType="${method.outputClass.className}"<#else>resultMap="${method.outputClass.className}"</#if> >
        SELECT ${columns?join(", ")}
        FROM ${daoClass.tableName}
        <include refid="${method.name}Cond"/>
    </select>
    </#if>
</#list>
</mapper>