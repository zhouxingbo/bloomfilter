package com.github.wxisme.bloomfilter.pdd;

import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkRpPromUrlGenerateRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkRpPromUrlGenerateResponse;

import java.util.ArrayList;
import java.util.List;

import static com.github.wxisme.bloomfilter.pdd.DesUtil.encryptBasedDes;

public class PddUserAuth {


    public static void main(String[] args) throws Exception {
        String clientId = "7f3c86afc4a34e2e8f2343efcb82ca22";
        String clientSecret = "1d44d0a5be97c52b098ab74d8ad49b2f70385ab6";
        PopClient client = new PopHttpClient(clientId, clientSecret);

        PddDdkRpPromUrlGenerateRequest request = new PddDdkRpPromUrlGenerateRequest();
        request.setChannelType(10);
        String str1="{\"uid\":\"33a962c4f72846e5add54d573c8707b4\",\"appId\":\"a1o5ubom\"}";
        // DES数据加密
        String s1=encryptBasedDes(str1);
        request.setCustomParameters(s1);
        List<String> pids = new ArrayList<>();
        pids.add("11258166_148405200");
        request.setPIdList(pids);

        PddDdkRpPromUrlGenerateResponse response = client.syncInvoke(request);
        System.out.println(JsonUtil.transferToJson(response));
    }


}
