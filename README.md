<!-- Plugin description -->
## 构建CodeGen
1. 打开源码项目，在idea侧边栏Gradle选项卡中，点击Tasks -> intellij -> buildPlugin 
2. 在build/distributions目录下生成*.zip文件
## 安装CodeGen
1. 打开idea，打开菜单 File -> Settings -> Plugins
1. 点击右侧“Setting”按钮，弹出菜单选择【Install Plugin From Disk...】，从文件夹选择 idea-plugin-codegen-0.0.1.zip文件。点击右下角[Apply]按钮安装。
1. 安装完成后在idea侧边栏显示【MVC代码生成器】

## 使用说明
### 项目配置
1. 点击面板“设置”选项卡，配置项目作者、包名。
![项目设置](/doc/screenshots/code-settings.png)
1. URL前缀即为Controller类的RequestMapping路径参数
1. 设置Model基类：统一集成net.takela.common.web.model.Model类
1. 设置Ctrl基类，设置成功后代码生成Contrller将统一集成该类
1. 模型（Ctrl/Svc/Dao）所在项目目录,设置成功后，将在该目录下的src/main/java/{package}下生成相应文件，规则为{project_dir}/src/main/java/{package}/{domain/controller/service/dao}/{module}/*.java
1. 设置列表查询基类，eg:PagedSearchArg,通常声明常用的列表查询参数: pageSize.pageNum，keyword等
2. Mybatis目录：设置生成mybatis mapper文件所放置的路径。
1. 设置统一返回类，需要支持一个泛型参数为具体的数据对象类，并要支持常见的参数错误静态方法，eg：HttpResponse.paramError(errorMsg)
1. 设置DaoMapper基类，基类需要生命add({Entity})/update({Entity})/remove(Long)/get(Long)/search({SearchArg})四个方法
### 数据库配置
1. 目前仅支持postgresql，依次配置数据库名、schema、host、port、user、password参数
   ![数据库设置](/doc/screenshots/db-settings.png)
1. 设置完成后，点击【保存】按钮，配置生效
### 代码生成
1. 选择表，配置表明前缀，默认“t_”, 根据表明生成实体类，忽略前缀
1. 配置模块名，base uri为Controller的RequestMapping值
1. 根据实际要求修改Controller、Service、Dao类名
2. 配置各Domain类字段以及约束和注释，或者新增相关类
   ![输入参数模型](/doc/screenshots/domain-settings.png)
1. 默认会生成add/update/get/search/remove方法，并完成代码从Controller -> Service -> Dao的调用。根据需求修改各个方法的输入参数和输出参数类名，以及对应参数字段，参数字段名默认为数据库字段的驼峰型名称。
   ![代码生成设置](/doc/screenshots/code-gen.png)
2. 点击Generate生成代码。刷新项目目录查看代码及注释。
   ![代码生成结果](/doc/screenshots/gen-result.png)
   ![controller类](/doc/screenshots/ctrl-cls.png)
   ![controller类](/doc/screenshots/domain-cls.png)
1. smart-doc插件可根据注释生成文档，[查看smart-doc文档](https://smart-doc-group.github.io/#/zh-cn/README)
   ![api doc](/doc/screenshots/api-doc.png)


## 常见问题
因为生成代码依赖某些功能，例如从web页面传入参数到实体类的转换，数据库文本字段到业务对象的映射需要Mybatis的TypeHandler,这些可以自定义，也可以使用默认的公共包；
```
<dependency>
   <groupId>net.takela</groupId>
   <artifactId>common-web</artifactId>
   <version>1.0.1</version>
</dependency>
```
改包提供了一些公共类：
1. 模型基类net.takela.web.model.Model，该类提供了copyTo方法，实现模型转换
1. net.takela.web.model.PagedSearchArg,提供了分页检索参数模型
1. net.takela.web.model.ListResult, 提供了分页检索结果模型
1. net.takela.spring.mybatis.typehandler.JsonObjectTypeHandler, 提供了数据库字符串类型到Map<?,?>或者List<?>类型的转换
1. net.takela.spring.mybatis.typehandler.LongArrayTypeHandler, 提供了数据库字符串类型到长整型数组的转换
1. net.takela.spring.mybatis.typehandler.StringArrayTypeHandler, 提供了数据库字符串类型到字符串数组的转换

需要文档支持的话，需要加入smart-doc插件依赖，该依赖恢根据代码注释生成doc、html、md等格式的Api文档，同时也能生成复合openapi规范的描述文件
```xml
<build>
  <plugins>
      ...
      <plugin>
          <groupId>com.ly.smart-doc</groupId>
          <artifactId>smart-doc-maven-plugin</artifactId>
          <version>3.0.2</version>
          <configuration>
              <configFile>./src/main/resources/smart-doc.json</configFile>
              <includes>
                 <!-- 扫描的包名，可不写 -->
                  <include>{package}:*</include>
              </includes>
          </configuration>
      </plugin>
  </plugins>
</build>
```
[查看smart-doc文档](https://smart-doc-group.github.io/#/zh-cn/README)
<!-- Plugin description end -->