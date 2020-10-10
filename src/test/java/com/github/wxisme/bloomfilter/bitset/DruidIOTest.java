package com.github.wxisme.bloomfilter.bitset;

import java.sql.*;
import java.util.Properties;

public class DruidIOTest {

    public static void main(String[] args) throws SQLException {
        String url = "jdbc:avatica:remote:url=http://192.168.0.57:8082/druid/v2/sql/avatica/";
        Properties connectionProperties = new Properties();
        //connectionProperties.put("sqlTimeZone","+08:00"); //标明存储数据的时区
        connectionProperties.put("useApproximateCountDistinct","false"); //是否使用近似基数算法COUNT(DISTINCT foo)
        connectionProperties.put("useApproximateTopN","false"); //当SQL查询可以表达时，是否使用近似TopN查询。如果为false，则将使用确切的GroupBy查询。


        String query = "select  SUM(\"CKIMG\"), SUM(\"CREATEF\"), SUM(\"OPEN\"), SUM(\"PWSHOW\"), SUM(\"LINK\"), SUM(\"SUSHOW\"), SUM(\"CLICK2\"), SUM(\"OPENSS\"),\n" +
                "\t\t SUM(\"OPENSC\"), SUM(\"CREATED\"), SUM(\"CANCEL\"), SUM(\"PWOPN\"), SUM(\"QUERY\"), SUM(\"DTCLOSE\"), SUM(\"PULL\"), SUM(\"SHOW2\"), SUM(\"DEL\"), SUM(\"CLICKD\"), SUM(\"DCLOSE\"), SUM(\"ACPD\"), SUM(\"CKINS\"), SUM(\"DOWNS\"), SUM(\"INSLS\"),\n" +
                "\t\t  SUM(\"SSDK\"), SUM(\"DSHOW\"), SUM(\"SUSOPN\"), SUM(\"SDSP\"),\n" +
                "\t\t SUM(\"CKMD5\"), SUM(\"DOWN\"), SUM(\"CKDRAG\"), SUM(\"JUMP\"), SUM(\"FAIL\"), SUM(\"INSTDC\"), SUM(\"ASDK\"),\n" +
                "\t\t SUM(\"OPENDS\"), SUM(\"CANCEL2\"), SUM(\"CLICK\"), SUM(\"ADSP\"), SUM(\"SCPD\"), SUM(\"INSLF\"), SUM(\"CKINS2\"), SUM(\"DOWNF\"), SUM(\"OPENDC\"), SUM(\"SHOW\"), SUM(\"ATOPEN\"), SUM(\"INSTDS\"), \n" +
                "\t\t SUM(\"CKCHK\"), SUM(\"OPENFS\"), SUM(\"OPENFC\"), SUM(\"CKDROP\"), SUM(\"AUTOC2\"),SUM(\"AUTOC\"),SUM(\"INSTL\"),adid,TIME_FORMAT(__time,'yyyy-MM-dd') as \"date\" " +
                "\t\t from stats_day_1_prt_p20190616_XX where __time >= '2019-06-16T00:00:00.000Z' and __time  < '2019-06-17T00:00:00.000Z'group by adid,TIME_FORMAT(__time,'yyyy-MM-dd') order by sum(\"SHOW\") DESC ";
        try {
            Connection connection = DriverManager.getConnection(url, connectionProperties);
            DatabaseMetaData metaData = connection.getMetaData();
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                System.out.println(resultSet.getObject("date"));

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
