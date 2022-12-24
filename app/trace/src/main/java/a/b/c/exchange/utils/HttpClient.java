package a.b.c.exchange.utils;

import a.b.c.MarketConfig;
import a.b.c.base.util.ClassUtil;
import a.b.c.base.util.json.JsonUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import lombok.extern.slf4j.Slf4j;
import a.b.c.exchange.Api;
import a.b.c.exchange.Constants;
import a.b.c.exchange.response.ApiResponse;
import a.b.c.exchange.response.ListResponse;

import java.util.function.Function;

@Slf4j
public class HttpClient {
    private String base;
    private ApiSignature apiSignature;

    public HttpClient() {
        this.apiSignature = new ApiSignature(MarketConfig.SECRET_KEY);
        this.base = Constants.API_BASE_URL;
    }

    public <T extends ApiResponse> T get(Api api, UrlParamsBuilder builder) {
        return invoke(api, builder, HttpUtil::createGet);
    }

    public <T extends ApiResponse> T post(Api api, UrlParamsBuilder builder) {
        return invoke(api, builder, HttpUtil::createPost);
    }

    public <T extends ApiResponse> T delete(Api api, UrlParamsBuilder builder) {
        return invoke(api, builder, url -> {
            return HttpUtil.createRequest(Method.DELETE, url);
        });

    }


    public <T extends ApiResponse> T invoke(Api api, UrlParamsBuilder builder,
                                            HttpRequestSupply supply, Function<String, T> responseConsumer) {
        if(api.signature){
            apiSignature.createSignature(builder);
        }
        String requestUrl = api.url;
        if (!requestUrl.startsWith("http")) {
            requestUrl = base + requestUrl;
        }
        requestUrl += builder.buildUrl();
        log.info("开始请求：" + requestUrl);
        String resp = supply.create(requestUrl).header("X-MBX-APIKEY", MarketConfig.API_KEY).execute().body();
        log.info("请求返回：" + resp);

        try {
            T t = responseConsumer.apply(resp);
            t.setRequest(requestUrl);
            t.setResponse(resp);
            return t;
        } catch (Exception ex) {
            String param=builder.buildUrlToJsonString();
            log.error("将结果转为java对象失败\nclass={}\nparam={},\nresp={}", api.response.getName(),param, resp, ex);
            throw new RuntimeException(ex);
        }


    }

    public <T extends ApiResponse> T invoke(Api api, UrlParamsBuilder builder, HttpRequestSupply supply) {
        return this.invoke(api, builder, supply, response -> {
            if (ListResponse.class.isAssignableFrom(api.response)) {
                ListResponse list = ClassUtil.newInstance(api.response);
                list.addResult(response);
                return (T) list;
            } else {
                return (T) JsonUtil.toBean(response, api.response);
            }
        });
    }

    /**
     * 没有任何参数的请求，获取websocket的key时用到
     */
    public <T extends ApiResponse> T invoke(Api api, HttpRequestSupply supply) {
        String requestUrl = api.url;
        if (!requestUrl.startsWith("http")) {
            requestUrl = base + requestUrl;
        }
        log.info("开始请求：" + requestUrl);
        String resp = supply.create(api.url).header("X-MBX-APIKEY", MarketConfig.API_KEY).execute().body();
        log.info("请求返回：" + resp);
        T t = (T) JsonUtil.toBean(resp, api.response);
        t.setRequest(requestUrl);
        t.setResponse(resp);
        return t;
    }


    public interface HttpRequestSupply {
        HttpRequest create(String url);
    }
}
