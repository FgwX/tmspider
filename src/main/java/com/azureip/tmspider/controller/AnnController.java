package com.azureip.tmspider.controller;

import com.azureip.tmspider.model.Announcement;
import com.azureip.tmspider.pojo.AnnQueryPojo;
import com.azureip.tmspider.pojo.AnnoucementListPojo;
import com.azureip.tmspider.pojo.AnnoucementPojo;
import com.azureip.tmspider.pojo.GlobalResponse;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.util.List;

@Controller
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

    /**
     * 查询公告总量
     * @param pojo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "importConfirm", method = RequestMethod.POST)
    public GlobalResponse importConfirm(AnnQueryPojo pojo) {
        GlobalResponse<AnnQueryPojo> response = new GlobalResponse<AnnQueryPojo>();
        String s = gson.toJson(pojo);
        System.out.println(s);
        response.setResult(pojo);
        return response;
    }

    /**
     * 查询公告数据，并导入数据库。
     * @return
     * @throws IOException
     */
    @PostMapping("importFirstTrialAnns")
    public String importFirstTrialAnns(AnnQueryPojo p) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(2000).setConnectTimeout(3000).setSocketTimeout(30000).build();
        StringBuffer countUrl = new StringBuffer("http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearchDG.html");
        countUrl.append("?page=" + p.getPage()).append("&rows=" + p.getRows()).append("&annNum=" + p.getAnnNum()).append("&annType=" + p.getAnnType());
        countUrl.append("&totalYOrN=true&appDateBegin=" + p.getAppDateBegin()).append("&appDateEnd=" + p.getAppDateEnd());
        System.out.println("查询总数URL：" + countUrl.toString());
        HttpPost countPost = new HttpPost(countUrl.toString());
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
        int successCount = 0;
        for (int i = 0; i < times; i++) {
            StringBuffer url = new StringBuffer("http://sbgg.saic.gov.cn:9080/tmann/annInfoView/annSearchDG.html");
            url.append("?page=" + (i + 1)).append("&rows=" + PAGE_SIZE).append("&annNum=" + p.getAnnNum()).append("&annType=" + p.getAnnType());
            url.append("&totalYOrN=true&appDateBegin=" + p.getAppDateBegin()).append("&appDateEnd=" + p.getAppDateEnd());
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

    /**
     * 处理EXCEL表格，标记出已有初审公告的行。
     * @return
     * @throws IOException
     */
    @GetMapping("optExcel")
    public String optExcel() throws IOException {
        // 获取待处理的EXCEL文件集合
        File srcDir = new File("D:\\TMSpider\\src");
        File[] files = srcDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            System.err.println("==========> 正在处理第【" + i + 1 + "】个文档 <==========");
            FileInputStream in = new FileInputStream(files[i]);
            XSSFWorkbook src = new XSSFWorkbook(in);
            in.close();
            /*
            处理第一个SHEET
            表格格式：首行为标题行; 第二列为注册号
             */
            XSSFSheet sheet = src.getSheetAt(0);
            int count = 0;
            System.out.println("待处理的数据共有【" + sheet.getLastRowNum() + "】条，开始处理……");
            for (int j = 1; j < sheet.getLastRowNum(); j++) {
                XSSFRow row = sheet.getRow(j);
                try {
                    String regNum = row.getCell(2).getStringCellValue();
                    List<Announcement> annList = annService.getByRegNum(regNum);
                    if (annList.size() > 0) {
                        row.createCell(7).setCellValue("初审公告");
                        count += 1;
                        System.out.println("正在处理第【" + j + "】行，注册号[" + regNum + "]，查询到" + annList.size() + "条初审公告");
                    } else {
                        System.out.println("正在处理第【" + j + "】行，注册号[" + regNum + "]，未查询到初审公告");
                    }
                } catch (NullPointerException e) {
                    System.err.println("正在处理第【" + j + "】行，此行为空！");
                }
            }
            System.out.println("共查询到【" + count + "】条初审公告！");

            // 输出目标文件
            FileOutputStream out = new FileOutputStream("D:\\TMSpider\\tar\\" + fileName);
            src.write(out);
            out.close();

            System.err.println("==========> 第【" + i + 1 + "】个文档处理完成 <==========");
        }
        return "Success!";
    }
}
