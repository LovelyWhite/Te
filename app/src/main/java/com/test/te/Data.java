package com.test.te;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.test.te.model.Alert;
import com.test.te.model.CValue;
import com.test.te.model.Device;
import com.test.te.model.DeviceCtrl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class Data {
   static Vector<Alert> alerts = new Vector<>();
   static Vector<Device> devices =new Vector<>();
   static String showed;
   static DeviceCtrl ctrl = new DeviceCtrl();
   //远程读设参的数组
   static Map<String,Vector<CValue>> dataLists = new HashMap<>();
   static List<CValue> allpCode = new ArrayList<>();
   static List<String> tableList = new ArrayList<>();
   static int nowTable;
   static MainActivity mainActivity;
   static int cDevicePosition;

   static void getJson(String fileName, Context context) {
      //将json数据变成字符串
      StringBuilder stringBuilder = new StringBuilder();
      try {
         //获取assets资源管理器
         AssetManager assetManager = context.getAssets();
         //通过管理器打开文件并读取
         BufferedReader bf = new BufferedReader(new InputStreamReader(
                 assetManager.open(fileName)));
         String line;
         while ((line = bf.readLine()) != null) {
            stringBuilder.append(line);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      JSONArray  temp = (JSONArray) JSON.parse(stringBuilder.toString());
      Vector<CValue> cValues =new Vector<>();
      for(int i = 0;i<temp.size();i++)
      {
         JSONObject o =  (((JSONObject)temp.get(i)));
         CValue cValue = new CValue();
         cValue.setpCode(o.getString("pCode"));
         cValue.setAddress(o.getString("Address"));
         cValue.setMinUnit(o.getString("MinUnit"));
         cValues.add(cValue);
      }
   }
   public static String getMatcher(String regex, String source) {
   String result = "";
   Pattern pattern = Pattern.compile(regex);
   Matcher matcher = pattern.matcher(source);
   while (matcher.find()) {
      result = matcher.group(0);
   }
   return result;
}
   static void getExcel(String filePath,Context context)
   {
      AssetManager assetManager = context.getAssets();
      try {
         String[] fileList = assetManager.list(filePath);
         for(String fileName:fileList)
         {
            Workbook workbook = Workbook.getWorkbook(assetManager.open(filePath+"/"+fileName));
            Sheet sheet= workbook.getSheet(0);
            int rows =  sheet.getRows();
            Vector<CValue> cs =new Vector<>();
            for(int i =1;i<rows;i++)
            {
               CValue cValue = new CValue();
               cValue.setpCode(sheet.getCell(1,i).getContents());
               String unit = sheet.getCell(4,i).getContents();
               if(unit!=null&&!unit.equals(""))
               {
                //  System.out.println(getMatcher("[^\\d.]*", unit));
                  cValue.setMinUnit(unit.replaceAll("[^\\d.]*",""));
               }
               String ress = sheet.getCell(6,i).getContents();
               if(ress!=null&&!ress.equals(""))
               {
                  cValue.setAddress(ress.replace("H",""));
               }
               if(showed.contains(sheet.getCell(1,i).getContents()))
                  cs.add(cValue);
               else
               {
                  allpCode.add(cValue);
               }
            }
            String f = fileName.substring(13,15).replace("P","F");
            tableList.add(f);
            dataLists.put(f,cs);
         }
      } catch (IOException e) {
         e.printStackTrace();
      } catch (BiffException e) {
         e.printStackTrace();
      }
   }
   static void CopyAssets(Context context) {
      try {
         File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Te_Devices");
         if (!file.exists())
         {
            file.mkdirs();
         }
         String fileNames[] = context.getAssets().list("data");
         for (String fileName : fileNames) {
            InputStream is = context.getAssets().open("data/"+fileName);
            System.out.println(file.getPath()+fileName);
            File f = new File(file.getPath()+"/"+fileName);
            if(f.exists())
            {
               continue;
            }
            else {
               FileOutputStream fos = new FileOutputStream(f);
               byte[] buffer = new byte[10240];
               int byteCount = 0;
               while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                  // buffer字节
                  fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
               }
               fos.flush();// 刷新缓冲区
               fos.close();
            }
            is.close();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   static boolean getAccess(String fileName)
   {
      try
      {
         Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

         Connection conn = DriverManager.getConnection("jdbc:ucanaccess://"+Environment.getExternalStorageDirectory().getAbsolutePath()+"/Te_Devices/"+fileName,"","hngddqxy67758837");
         DatabaseMetaData dbmd = conn.getMetaData();
         Statement s = conn.createStatement();
         ResultSet  ddrs=dbmd.getTables(null,null,"%",null);
         while(ddrs.next()) {
            System.out.println(ddrs.getString(3));
            if (ddrs.getString(3).contains("P")) {
               Vector<CValue> cs =new Vector<>();
          //     System.out.println(ddrs.getString(3));
               ResultSet rs = s.executeQuery("select * from " + ddrs.getString(3));
               while (rs.next()) {
            //      System.out.println(rs.getString(2) + "," + rs.getString(5) + "," + rs.getString(7));
                  CValue cValue = new CValue();
                  cValue.setpCode(rs.getString(2));
                  String unit = rs.getString(5);
                  if (unit != null && !unit.equals("")) {
                     //  System.out.println(getMatcher("[^\\d.]*", unit));
                     cValue.setMinUnit(unit.replaceAll("[^\\d.]*", ""));
                  }
                  String ress =rs.getString(7);
                  if(ress!=null&&!ress.equals(""))
                  {
                     cValue.setAddress(ress.replace("H",""));
                  }
                  if(showed.contains(rs.getString(2)))
                     cs.add(cValue);
                  else
                  {
                     allpCode.add(cValue);
                  }
               }
               String f = ddrs.getString(3).substring(13,15).replace("P","F");
               tableList.add(f);
               dataLists.put(f,cs);
            }
            else if(ddrs.getString(3).contains("Controltable")){
               ResultSet rs = s.executeQuery("select * from " + ddrs.getString(3));
               while (rs.next()) {
                 int id =  rs.getInt(1);
                 if(id == 2)
                 {
                    ctrl.setStart(rs.getString(3));
                 }
                 if(id==3)
                 {
                    ctrl.setStop(rs.getString(3));
                 }
               }
            }
         }
         ddrs.close();
         s.close();
         conn.close();
         return true;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return false;
      }
   }
}
