package com.azureip.tmspider.controller;

import com.azureip.tmspider.pojo.AnnoucementListPojo;
import com.azureip.tmspider.pojo.AnnoucementPojo;
import com.azureip.tmspider.service.AnnService;
import com.google.gson.Gson;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("ann")
public class AnnController {

    @Autowired
    private AnnService annService;
    // RequestHeader: User-Agent
    private static final String AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";
    // Page size
    private static final int PAGE_SIZE = 10000;
    // Gson
    private static Gson gson = new Gson();

    @GetMapping("test")
    public String test() {
        return "OK!";
    }

    @GetMapping("firstTrial")
    public String firstTrial() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(2000).setConnectTimeout(3000).setSocketTimeout(30000).build();
        HttpPost countPost = new HttpPost("http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearchDG.html?page=1&rows=2&annNum=1595&annType=TMZCSQ&totalYOrN=true&appDateBegin=2017-06-01&appDateEnd=2017-07-31");
        // countPost.setHeader("Accept","application/json, text/javascript, */*; q=0.01");
        // countPost.setHeader("Accept-Encoding","gzip, deflate");
        // countPost.setHeader("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8");
        // countPost.setHeader("Connection","keep-alive");
        // countPost.setHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
        // countPost.setHeader("Host","sbgg.saic.gov.cn:9080");
        // countPost.setHeader("Origin","http://sbgg.saic.gov.cn:9080");
        // countPost.setHeader("Referer","http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearch.html?annNum=");
        // countPost.setHeader("X-Requested-With","XMLHttpRequest");
        countPost.setHeader("User-Agent", AGENT);
        countPost.setConfig(config);
        CloseableHttpResponse countResp = client.execute(countPost);
        AnnoucementListPojo countPojo = gson.fromJson(EntityUtils.toString(countResp.getEntity()), AnnoucementListPojo.class);
        int times = (int) Math.ceil(countPojo.getTotal() / Double.valueOf(PAGE_SIZE));
        System.out.println("==========>>>>>>>>>> Total Pages : " + times);
        System.out.println("==========>>>>>>>>>> Total Pages : " + times);
        System.out.println("==========>>>>>>>>>> Total Pages : " + times);
        System.out.println("==========>>>>>>>>>> Total Pages : " + times);
        System.out.println("==========>>>>>>>>>> Total Pages : " + times);
        int successCount = 0;
        for (int i = 0; i < times; i++) {
            StringBuffer url = new StringBuffer("http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearchDG.html");
            url.append("?page=" + (i + 1));
            url.append("&rows=" + PAGE_SIZE);
            url.append("&annNum=" + 1595);
            url.append("&annType=TMZCSQ&totalYOrN=true&appDateBegin=2017-06-01&appDateEnd=2017-07-31");
            HttpPost post = new HttpPost(url.toString());
            post.setHeader("User-Agent", AGENT);
            post.setHeader("Connection", "keep-alive");
            post.setConfig(config);
            CloseableHttpResponse resp = client.execute(post);
            AnnoucementListPojo listPojo = gson.fromJson(EntityUtils.toString(resp.getEntity()), AnnoucementListPojo.class);
            for (AnnoucementPojo pojo : listPojo.getRows()) {
                int result = annService.save(pojo);
                if (result > 0) {
                    successCount += result;
                    System.out.println("Success: " + pojo);
                } else {
                    System.out.println(" Failed: " + pojo);
                }
            }
        }
        client.close();
        System.out.println("Total Success: " + successCount);
        return "Insert Data Size: " + successCount;
    }

    @GetMapping("excelTest")
    public String excelTest() throws IOException {
        /*
        HSSFWorkbook    excel文档对象
        HSSFSheet       excel的sheet
        HSSFRow         excel的行
        HSSFCell        excel的单元格
        HSSFFont        excel字体
        HSSFName        名称
        HSSFDataFormat  日期格式
        HSSFHeader      sheet头
        HSSFFooter      sheet尾
        HSSFCellStyle   cell样式
        HSSFDateUtil    日期
        HSSFPrintSetup  打印
        HSSFErrorConstants 错误信息表
        */
        FileInputStream in = new FileInputStream("D:/test.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(in);
        in.close();
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = sheet.getRow(0);
        XSSFCell cell1 = row.getCell(0);
        System.out.println(cell1.getStringCellValue());
        sheet.shiftRows(1,4,1);
        XSSFRow newRow = sheet.createRow(1);
        CellCopyPolicy policy = new CellCopyPolicy();
        newRow.copyRowFrom(row, policy);
        XSSFCell cell = newRow.createCell(1);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("测试单元格");
        FileOutputStream out = new FileOutputStream("D:/test.xlsx");
        workbook.write(out);
        out.close();
        return "Excel Test OK!";
    }
}
