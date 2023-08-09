package ${ctrl.pkg};


import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
* API管理
* @author ${author}
* @date ${.now?string["yyyy-MM-dd"]}
*/
@RestController
@RequestMapping("${ctrl.baseURI}")
public class ApiController {
private final ApiService apiService;

public ApiController(ApiService apiService) {
this.apiService = apiService;
}

/**
* 注册接口
* @param apiAddArgs 新增接口
* @return
*/
@PostMapping("/add")
@ResponseBody
public HttpResponse<IdResult> add(@RequestBody ApiAddArgs apiAddArgs, BindingResult br) {
    Api api = apiAddArgs.copyTo(Api.class);
    api.setCreateUid(getAuthUser().getUid());
    Long apiId = apiService.add(api);
    if (apiId == null){
    return (HttpResponse<IdResult>) HttpResponse.getFailureResponse();
        }
        IdResult idResult = new IdResult();
        idResult.setId(apiId);
        // 设置上下文
        HttpResponse res = new HttpResponse();
        res.setData(idResult);
        return res;
        }

        /**
        * 获取API详情
        * @param id API id
        * @return
        */
        @GetMapping("/get")
        @ResponseBody
        public HttpResponse<ApiResult> get(Integer id) {
            ApiResult apiResult = new ApiResult();
            apiResult.setId(1L);
            // 设置上下文
            HttpResponse res = new HttpResponse();
            res.setData(apiResult);
            return res;
            }

            /**
            * 检索所api
            * @param apiSearchArgs
            * @return
            */
            @GetMapping("/search")
            @ResponseBody
            public HttpResponse<ApiResult> search(ApiSearchArgs apiSearchArgs) {
                ApiResult apiResult = new ApiResult();
                apiResult.setId(1L);
                List items = new ArrayList();
                items.add(apiResult);
                // 设置上下文
                HttpResponse res = new HttpResponse();
                res.setData(items);
                return res;
                }
                }
