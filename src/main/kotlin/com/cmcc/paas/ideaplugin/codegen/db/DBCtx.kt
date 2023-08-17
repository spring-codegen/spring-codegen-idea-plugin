package com.cmcc.paas.ideaplugin.codegen.db

import com.alibaba.druid.pool.DruidDataSource
import com.alibaba.druid.util.JdbcUtils
import com.cmcc.paas.ideaplugin.codegen.config.DBCfg
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTable
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField
import org.apache.commons.lang3.StringUtils
import org.apache.ibatis.builder.xml.XMLConfigBuilder
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.TransactionFactory
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory


/**
 *
 * @author zhangyinghui
 * @date 2023/8/3
 */
object DBCtx {
    var dataSource:DruidDataSource? = null

    var dbCfg: DBCfg? = null
    var sqlSessionFactory:SqlSessionFactory? = null
    fun resetDataSource(){
        if (dbCfg == null
                || StringUtils.isEmpty(dbCfg!!.dbName)
                || StringUtils.isEmpty(dbCfg!!.host)
                || StringUtils.isEmpty(dbCfg!!.user)
                || StringUtils.isEmpty(dbCfg!!.pwd)
                || StringUtils.isEmpty(dbCfg!!.driverType)
                || StringUtils.isEmpty(dbCfg!!.schema)){
            dataSource = null
            return
        }

        if (dataSource == null){
            dataSource = DruidDataSource();
            dataSource!!.setValidationQuery("SELECT 1")
            dataSource!!.setTestOnBorrow(true)
        }
        var driverClass: String? = "org.postgresql.Driver"
        var url:String? = null
        if (dbCfg!!.driverType == "postgresql"){
            driverClass = "org.postgresql.Driver"
            url = String.format("jdbc:postgresql://%s:%d/%s?currentSchema=%s&useUnicode=true&characterEncoding=utf-8", dbCfg!!.host, dbCfg!!.port, dbCfg!!.dbName, dbCfg!!.schema)
        }
        var driver = JdbcUtils.createDriver(driverClass)
        dataSource!!.username = dbCfg!!.user;
        dataSource!!.password = dbCfg!!.pwd;
        dataSource!!.url = url;
        dataSource!!.driver = driver
    }
    fun resetSessionFactory(){
        if(dataSource == null){
            sqlSessionFactory = null
            return
        }
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
        var ret = sqlSession.selectList<DBTableField>("postgresql.TableDao.queryFields", params)
        sqlSession.close()
        return ret
    }
    fun queryTables():List<DBTable>{
        if (dbCfg == null || StringUtils.isEmpty(dbCfg!!.schema) || sqlSessionFactory == null || dataSource == null){
            return ArrayList()
        }
        var params = HashMap<String, String>()
        params["schema"] = dbCfg!!.schema!!
        var sqlSession = sqlSessionFactory!!.openSession();
        var ret = sqlSession.selectList<DBTable>("postgresql.TableDao.queryTables", params)
        sqlSession.close()
        return ret
    }
}