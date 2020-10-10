package com.github.wxisme.bloomfilter.pdd;

import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.pop.request.PddGoodsCatsGetRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddGoodsCatsGetResponse;
import com.sun.corba.se.impl.orbutil.concurrent.Sync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class PddCatsDemo2 {

    static Connection conn;
    static String URL = "jdbc:mysql://192.168.0.223:3306/shop_support?autoReconnect=true&useUnicode=true&characterEncoding=utf-8";
    static String USER = "zzmanager";
    static String PASSWORD = "iadMOB-2013@0622)";
    private final static Logger logger = LoggerFactory.getLogger(Sync.class);

    public static void main(String[] args) throws Exception {

//        String clientId = "4c90dc3826db47fda7824e9724c8490f";
//        String clientSecret = "492f4be256640f5129097a47873535187ada610c";

        String clientId = "7f3c86afc4a34e2e8f2343efcb82ca22";
        String clientSecret = "1d44d0a5be97c52b098ab74d8ad49b2f70385ab6";
        int level = 3;
        PopClient client = new PopHttpClient(clientId, clientSecret);


        try {
            //1.加载驱动程序
            Class.forName("com.mysql.jdbc.Driver");
            //2.获得数据库链接
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select cid from pdd_category where level= " + level);//
            List<Map<String, Object>> datas = new ArrayList<>();
            while (rs.next()) {
                long cid = rs.getLong("cid");
                PddGoodsCatsGetRequest request = new PddGoodsCatsGetRequest();
                request.setParentCatId(cid);
                PddGoodsCatsGetResponse response = client.syncInvoke(request);
                System.out.println(JsonUtil.transferToJson(response));
                PddGoodsCatsGetResponse.GoodsCatsGetResponse goodsCatsGetResponse = response.getGoodsCatsGetResponse();
                System.out.println(JsonUtil.transferToJson(goodsCatsGetResponse));
                if(goodsCatsGetResponse != null){
                    List<PddGoodsCatsGetResponse.GoodsCatsGetResponseGoodsCatsListItem> goodsCatsList = goodsCatsGetResponse.getGoodsCatsList();
                    if(!CollectionUtils.isEmpty(goodsCatsList)){
                        for (PddGoodsCatsGetResponse.GoodsCatsGetResponseGoodsCatsListItem goodsItem : goodsCatsList) {
                            Long catId = goodsItem.getCatId();
                            String catName = goodsItem.getCatName();
                            Integer level2 = goodsItem.getLevel();
                            Long parentCatId = goodsItem.getParentCatId();

                            Map<String, Object> user = new HashMap<>();
                            user.put("cid", catId);
                            user.put("name", catName);
                            user.put("level", level2);
                            user.put("parent_cid", parentCatId);
                            user.put("create_time", new Date());
                            user.put("update_time", new Date());
                            user.put("status", 1);
                            user.put("del", 0);
                            datas.add(user);
                        }
                    }
                }
                Thread.sleep(100);
            }
            if(!CollectionUtils.isEmpty(datas)){
                PreparedStatement prepared = null;

                conn.setAutoCommit(false); // 设置手动提交
                StringBuffer fieldString = new StringBuffer();
                StringBuffer paraNo = new StringBuffer(); //
                List<Object> values = new ArrayList<Object>();
                boolean flag = true;
                for (int i = 0; i < datas.size(); i++) {
                    for (Object element : datas.get(i).keySet()) {
                        if (flag) {
                            fieldString.append("," + element);
                            paraNo.append(",?");
                        }
                        values.add(datas.get(i).get(element));
                    }
                    flag = false;

                    if (prepared == null) {
                        // 所有参数组成的数组
                        String queryString = " REPLACE INTO pdd_category (" + fieldString.toString().substring(1)
                                + ") VALUES (" + paraNo.substring(1) + ")";
                        prepared = conn.prepareStatement(queryString);
                    }
                    // 设置对应参数值
                    for (int j = 0; j < datas.get(i).size(); j++) {
                        prepared.setObject(j + 1, values.get(j));
                    }
                    prepared.addBatch();
                    if (i % 10000 == 0) {
                        prepared.executeBatch();
                        conn.commit();
                    }
                    values.clear();
                }
                prepared.executeBatch();
                conn.commit();
            }else{
                System.out.println("data is empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }
}