package com.github.xingbo.pdd;

import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkMemberAuthorityQueryRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkMemberAuthorityQueryResponse;

import static com.github.xingbo.pdd.DesUtil.encryptBasedDes;

public class PddQueryUserAuth {


    public static void main(String[] args) throws Exception {
        String clientId = "7f3c86afc4a34e2e8f2343efcb82ca22";
        String clientSecret = "1d44d0a5be97c52b098ab74d8ad49b2f70385ab6";
        PopClient client = new PopHttpClient(clientId, clientSecret);

        PddDdkMemberAuthorityQueryRequest request = new PddDdkMemberAuthorityQueryRequest();
        request.setPid("11258166_148405200");
        String str1="{\"uid\":\"33a962c4f72846e5add54d573c8707b4\",\"appId\":\"a1o5ubom\"}";
        // DES数据加密
        String s1=encryptBasedDes(str1);
        request.setCustomParameters(s1);
        PddDdkMemberAuthorityQueryResponse response = client.syncInvoke(request);
        System.out.println(JsonUtil.transferToJson(response));
    }


}
