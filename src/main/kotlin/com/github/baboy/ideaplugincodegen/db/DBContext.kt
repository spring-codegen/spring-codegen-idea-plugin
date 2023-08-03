package com.github.baboy.ideaplugincodegen.db

import com.alibaba.druid.pool.DruidDataSource
import com.alibaba.druid.util.JdbcUtils
import com.github.baboy.ideaplugincodegen.db.model.DBTableField
import org.apache.ibatis.builder.xml.XMLConfigBuilder
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.TransactionFactory
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory


/**
 *
 * @author zhangyinghui
 * @date 2023/8/3
 */
object DBContext {
    var dataSource:DruidDataSource? = null
    var driverClass: String? = "org.postgresql.Driver"
    var url: String? = "jdbc:postgresql://123.56.40.132:17701/auto_delivery?currentSchema=capability_gateway&useUnicode=true&characterEncoding=utf-8"
    var username: String? = "auto_delivery"
    var pwd: String? = "Fuck&U2022!"
    var sqlSessionFactory:SqlSessionFactory? = null
    fun resetDataSource(){
        if (dataSource == null){
            dataSource = DruidDataSource();
            dataSource!!.setValidationQuery("SELECT 1")
            dataSource!!.setTestOnBorrow(true)
        }
        var driver = JdbcUtils.createDriver(driverClass)
        dataSource!!.username = username;
        dataSource!!.password = pwd;
        dataSource!!.url = url;
        dataSource!!.driver = driver;
        dataSource!!.username = username;
    }
    fun resetSessionFactory(){
        if (sqlSessionFactory == null) {
            val configFile = "/mybatis/config.xml" //相对路径
            val transactionFactory: TransactionFactory = JdbcTransactionFactory()
            val configStream = this.javaClass.getResourceAsStream(configFile)
            var xmlMapperBuilder = XMLConfigBuilder(configStream, null as String?, null);
            xmlMapperBuilder.parse()
            xmlMapperBuilder.configuration.environment = Environment("development", transactionFactory, dataSource)
            sqlSessionFactory = SqlSessionFactoryBuilder().build(xmlMapperBuilder.configuration)
        }
    }
    fun refresh(){
        resetDataSource()
        resetSessionFactory()
    }
    fun queryFields(params: Any):List<DBTableField>{
        var sqlSession = sqlSessionFactory!!.openSession();
        var ret = sqlSession.selectList<DBTableField>("com.github.baboy.ideaplugincodegen.db.dao.TableDao.queryFields", params)
        return ret
    }
}