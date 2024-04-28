{
    "outPath": "${outputDir}",
    "sortByTitle": false,
    "allInOne": true,
    "allInOneDocFileName": "index.html",
    "coverOld": true,
    "inlineEnum": true,
    "randomMock": true,
    "customResponseFields": [
        {
            "name": "code",
            "desc": "响应代码",
            "ownerClassName": "net.takela.common.spring.http.HttpResponse",
            "ignore":false,
            "value": "0"
        },
        {
            "name": "message",
            "desc": "响应信息或出错信息",
            "ownerClassName": "net.takela.common.spring.http.HttpResponse",
            "ignore":false
        },
        {
            "name": "data",
            "desc": "返回结果",
            "ownerClassName": "net.takela.common.spring.http.HttpResponse",
            "ignore":false
        },
        {
            "name": "id",
            "desc": "新增数据Id",
            "ownerClassName": "net.takela.common.web.model.IdResult",
            "ignore":false
        }
    ]
}