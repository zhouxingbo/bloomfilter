package com.github.xingbo.bitset;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;


public class DruidFileSend {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        File file = new File("/home/bobo/down/pulllive_one_2.json");
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream fos=new FileOutputStream(file);
        OutputStreamWriter osw=new OutputStreamWriter(fos, "UTF-8");
        BufferedWriter bw=new BufferedWriter(osw);
        Random random = new Random();//指定种子数字
        String[] operators = new String[]{"CLICK","SHOW","REQT","PREQT","WAKE","WAKEF","FILT", "QUERY"};

        for(int i =0 ;i<= 1000000;i++){
            Map<String,Object> map = new LinkedHashMap<String,Object>();

            map.put("appId","LK1006");
            map.put("channelId","XL5555");
            map.put("subChannelId","5555");
            map.put("mid","ssiddca98c74423142258b994801ed9f779f"+i);
            map.put("adId",random.nextInt(10));
            map.put("createTime",String.valueOf(System.currentTimeMillis()));
            map.put(operators[random.nextInt(operators.length)],random.nextInt(1000));
            map.put("productId",random.nextInt(10));

            String json = mapper.writeValueAsString(map);

            bw.write(json + "\t\n");
        }

        bw.close();
        osw.close();
        fos.close();

    }

}
