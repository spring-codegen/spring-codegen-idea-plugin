## 安装CodeGen
1. 打开idea，打开菜单 File -> Settings -> Plugins
1. 点击右侧“Setting”按钮，弹出菜单选择【Install Plugin From Disk...】，从文件夹选择 idea-plugin-codegen-0.0.1.zip文件。点击右下角[Apply]按钮安装。
1. 安装完成后在idea侧边栏显示【MVC代码生成器】

## 使用说明
### 项目配置
1. 点击面板“设置”选项卡，配置项目作者、包名。
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
1. 设置完成后，点击【保存】按钮，配置生效
### 代码生成
1. 选择表，配置表明前缀，默认“t_”, 根据表明生成实体类，忽略前缀
1. 配置模块名，base uri为Controller的RequestMapping值
1. 根据实际要求修改Controller、Service、Dao类名
1. 默认会生成add/update/get/search/remove方法，并完成代码从Controller -> Service -> Dao的调用。根据需求修改各个方法的输入参数和输出参数类名，以及对应参数字段，参数字段名默认为数据库字段的驼峰型名称。
1. 点击Generate生成代码。刷新项目目录查看代码及注释。
1. smart-doc插件可根据注释生成文档，[查看smart-doc文档](https://smart-doc-group.github.io/#/zh-cn/README)
