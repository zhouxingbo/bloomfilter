package com.github.wxisme.bloomfilter.pdd;

import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkGoodsSearchRequest;
import com.pdd.pop.sdk.http.api.pop.request.PddDdkGoodsSearchRequest.RangeListItem;
import com.pdd.pop.sdk.http.api.pop.response.PddDdkGoodsSearchResponse;

import java.util.ArrayList;
import java.util.List;

public class PddGoods {


    public static void main(String[] args) throws Exception {
        String clientId = "4c90dc3826db47fda7824e9724c8490f";
        String clientSecret = "492f4be256640f5129097a47873535187ada610c";
        PopClient client = new PopHttpClient(clientId, clientSecret);

        PddDdkGoodsSearchRequest request = new PddDdkGoodsSearchRequest();
        List<Integer> activityTags = new ArrayList<Integer>();
        activityTags.add(0);
        request.setActivityTags(activityTags);
        request.setCatId(0L);
        request.setCustomParameters("str");
        List<Long> goodsIdList = new ArrayList<Long>();
        goodsIdList.add(0L);
        request.setGoodsIdList(goodsIdList);
        request.setIsBrandGoods(false);
        request.setKeyword("str");
        request.setListId("str");
        request.setMerchantType(0);
        List<Integer> merchantTypeList = new ArrayList<Integer>();
        merchantTypeList.add(0);
        request.setMerchantTypeList(merchantTypeList);
        request.setOptId(0L);
        request.setPage(0);
        request.setPageSize(0);
        request.setPid("str");
        List<RangeListItem> rangeList = new ArrayList<RangeListItem>();

        RangeListItem item = new RangeListItem();
        item.setRangeFrom(0L);
        item.setRangeId(0);
        item.setRangeTo(0L);
        rangeList.add(item);
        request.setRangeList(rangeList);
        request.setSortType(0);
        request.setWithCoupon(false);
        PddDdkGoodsSearchResponse response = client.syncInvoke(request);
        System.out.println(JsonUtil.transferToJson(response));
    }


}
