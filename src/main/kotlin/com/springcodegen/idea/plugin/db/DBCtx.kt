package com.springcodegen.idea.plugin.db

import com.alibaba.druid.pool.DruidDataSource
import com.alibaba.druid.util.JdbcUtils
import com.springcodegen.idea.plugin.ctx.DBSettingCtx
import com.springcodegen.idea.plugin.db.model.DBTable
import com.springcodegen.idea.plugin.db.model.DBTableField
import com.springcodegen.idea.plugin.notify.NotificationCenter
import com.springcodegen.idea.plugin.notify.NotificationType
import org.apache.commons.lang3.StringUtils
import org.apache.ibatis.builder.xml.XMLConfigBuilder
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.TransactionFactory
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import java.sql.SQLException


/**
 *
 * @author zhangyinghui
 * @date 2023/8/3
 */
object DBCtx {
    var dataSource:DruidDataSource? = null
    var sqlSessionFactory:SqlSessionFactory? = null
    @JvmStatic fun resetDataSource(){
        var dbCfg = DBSettingCtx
        if (
                StringUtils.isEmpty(dbCfg.dbName)
                || StringUtils.isEmpty(dbCfg.host)
                || StringUtils.isEmpty(dbCfg.user)
                || StringUtils.isEmpty(dbCfg.pwd)
                || StringUtils.isEmpty(dbCfg.driverType)
                || StringUtils.isEmpty(dbCfg.schema)){
            dataSource = null
            return
        }
        if (dataSource != null && dataSource!!.isInited){
            dataSource!!.close()
            dataSource!!.restart()
        }
        if (dataSource == null){
            dataSource = DruidDataSource();
            dataSource!!.setValidationQuery("SELECT 1")
            dataSource!!.setTestOnBorrow(true)
        }
        var driverClass: String? = "org.postgresql.Driver"
        var url:String? = null
        if (dbCfg.driverType == "postgresql"){
            driverClass = "org.postgresql.Driver"
            url = String.format("jdbc:postgresql://%s:%d/%s?currentSchema=%s&useUnicode=true&characterEncoding=utf-8", dbCfg.host, dbCfg.port, dbCfg.dbName, dbCfg.schema)
        }
        var driver = JdbcUtils.createDriver(driverClass)
        dataSource!!.username = dbCfg.user;
        dataSource!!.password = dbCfg.pwd;
        dataSource!!.url = url;
        dataSource!!.driver = driver
        dataSource!!.connectTimeout = 10000;
        dataSource!!.socketTimeout = 10000;
    }
    @JvmStatic fun resetSessionFactory(){
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
    @JvmStatic fun refresh(){
        resetDataSource()
        test()
        resetSessionFactory()
    }
    @JvmStatic fun queryFields(params: Map<String, Any>):List<DBTableField>{
        var dbCfg = DBSettingCtx
        var sqlSession = sqlSessionFactory!!.openSession();
        var p = HashMap<String, Any>()
        p["schema"] = dbCfg.schema!!
        p.putAll(params)

        var ret = sqlSession.selectList<DBTableField>("postgresql.TableDao.queryFields", p)
        sqlSession.close()
        return ret
    }
    @JvmStatic fun queryTables():List<DBTable>{
        var dbCfg = DBSettingCtx
        if ( StringUtils.isEmpty(dbCfg.schema) || sqlSessionFactory == null || dataSource == null){
            return ArrayList()
        }
        var params = HashMap<String, String>()
        params["schema"] = dbCfg.schema!!
        var sqlSession = sqlSessionFactory!!.openSession();
        var ret = sqlSession.selectList<DBTable>("postgresql.TableDao.queryTables", params)
        sqlSession.close()
        return ret
    }
    @JvmStatic private fun test(){
        if (dataSource != null){
            try {
                dataSource?.getConnection(3000)
                NotificationCenter.sendMessage(NotificationType.DB_CONN_EXCEPTION, "数据库连接正常")
            }catch ( e: SQLException){
                e.printStackTrace()
                NotificationCenter.sendMessage(NotificationType.DB_CONN_EXCEPTION, "数据库连接失败")
            }
        }
    }
}