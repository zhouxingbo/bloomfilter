package com.github.xingbo.bitset;

import com.chinamobiad.adx.idl.AdxDspEvent;
import com.google.protobuf.ByteString;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class KafkaEsSend {

    public static void main(String[] args) throws IOException {

        Random r = new Random(1);
        Random r1 = new Random(12);

        long start = System.currentTimeMillis();
        InputStream is = new FileInputStream("/home/bobo/down/adx-edge.2020-04-19-00.log");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String str ;

        Properties props = new Properties();
        props.put("metadata.broker.list","adx61:9092,adx62:9092,adx63:9092");
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.BytesSerializer");
        ProducerConfig producerConfig = new ProducerConfig(props);
        Producer<String, byte[]> kafkaProducer = new Producer<>(producerConfig);
        List<AdxDspEvent.AdxDspData.Builder> list1 = new ArrayList<>();
        List<KeyedMessage<String,byte[]>> list = new ArrayList<>();
        int cc=0;
        int ee = 0;
        while ((str = br.readLine()) != null) {
            if (str.startsWith("2020-04-19T")) {
                if (str.indexOf("going to do dsp request.") >= 0 || str.indexOf("got DSP response.") >= 0 || str.indexOf("s2s final response.") >= 0 ||  str.indexOf("zm imp2 api response.") >= 0 ) {
                    cc++;
                    if (cc % 1000000 == 0) {
                        System.out.println("cc:" + cc);
                    }
                    String reqId = "";
                    String dspCode = "";
                    String url = "";
                    AdxDspEvent.DspDataType dataType = AdxDspEvent.DspDataType.UNKNOWN_DATA_TYPE;
                    String method = "";
                    String data = "";

                    String requestReg = "requestId: (.*?)\\,";
                    Pattern pattern = Pattern.compile(requestReg);
                    Matcher matcher = pattern.matcher(str);
                    if (matcher.find()) {
                        reqId = matcher.group(1);
                    }

                    String dspCodeReg = "dspCode: (.*?)\\,";
                    Pattern dspCodePattern = Pattern.compile(dspCodeReg);
                    Matcher dspCodeMatcher = dspCodePattern.matcher(str);

                    if (dspCodeMatcher.find()) {
                        dspCode = dspCodeMatcher.group(1);
                    } else {
                        System.out.println("__" + str);
                        continue;
                    }


                    if (str.indexOf("going to do dsp request.") >= 0) {
                        if(!"54".equals(dspCode)){
                            dataType = AdxDspEvent.DspDataType.forNumber(1);
                            String urlReg = "url: (.*?)\\, ";
                            Pattern urlPattern = Pattern.compile(urlReg);
                            Matcher urlMatcher = urlPattern.matcher(str);
                            if (urlMatcher.find()) {
                                url = urlMatcher.group(1);
                                if (url.indexOf("&") >= 0) {
                                    method = "GET";
                                } else {
                                    method = "POST";
                                    data = str.substring(str.indexOf("postData: ") + 10);
                                }
                            }
                        }else{
                            ee++;
                        }

                    } else if (str.indexOf("got DSP response.") >= 0) {
                        dataType = AdxDspEvent.DspDataType.forNumber(2);
                        data = str.substring(str.indexOf("data: ") + 6);
                    } else if (str.indexOf("s2s final response.") >= 0 || str.indexOf("zm imp2 api response.") >= 0) {
                        dataType = AdxDspEvent.DspDataType.forNumber(3);
                        data = str.substring(str.indexOf("response: ") + 10);
                    }

                    AdxDspEvent.AdxDspData.Builder builder = AdxDspEvent.AdxDspData.newBuilder();
                    builder.setRequestId(reqId);
                    builder.setDspCode(Integer.parseInt(dspCode));
                    builder.setDspAppId(r1.nextInt(4) + 100);
                    builder.setAdType(AdxDspEvent.AdType.forNumber(r.nextInt(4) + 1));
                    builder.setDataType(dataType);
                    builder.setData(ByteString.copyFrom(data.getBytes()));
                    builder.setUrl(url);
                    builder.setMethod(method);
                    builder.setNow(System.currentTimeMillis());
                    list1.add(builder);
                }
            } else {
                if (!StringUtils.isEmpty(str.trim())) {
                    int index = list1.size() - 1;
                    AdxDspEvent.AdxDspData.Builder builder = list1.get(index);
                    String adxDspStr = new String(builder.getData().toByteArray());
                    String finalStr ;
                    if(builder.getDspCode() == 54){
                        continue;
                    }else{
                        finalStr = adxDspStr + str;
                    }

                    builder.setData(ByteString.copyFrom(finalStr.getBytes()));
                    list1.remove(index);
                    list1.add(index, builder);
                }
            }
        }
        for (int i = 0; i < list1.size(); i++) {
            AdxDspEvent.AdxDspData.Builder builder = list1.get(i);
            AdxDspEvent.AdxDspData adxDspData = builder.build();
            String str1 = new String(adxDspData.getData().toByteArray());
            System.out.println(str1);
            byte[] bytes = adxDspData.toByteArray();
            KeyedMessage<String,byte[]> keyedMessage = new KeyedMessage("d1",bytes);
            list.add(keyedMessage);
        }
        System.out.println("cc:" + cc);
        System.out.println("ee:" + ee);
        try{
            int size = list.size();
            System.out.println("size=" + size);
            int part = size / 100; //分5W条每次插入
            if (size > part) {

                //分批插入
                for (int i = 0; i < part; i++) {
                    List<KeyedMessage<String,byte[]>> subList = list.subList(0, 100);
                    kafkaProducer.send(subList);
                    subList.clear();
                }
                if (list.size() > 0) {
                    kafkaProducer.send(list);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("总耗时" + (System.currentTimeMillis() - start));

    }

}
