/*
 * Copyright (c) 2015,robinjim(robinjim@126.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.robin.comm.util.xls;

import com.robin.core.base.util.Const;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelBaseOper {
	public static String TYPE_EXCEL2003="xls";
	public static String TYPE_EXCEL2007="xlsx";
	private static String defaultFontName="Microsoft YaHei";
	private static Logger logger=LoggerFactory.getLogger(ExcelBaseOper.class);
	
	public static Sheet createSheet(Workbook wb,String sheetName,ExcelSheetProp prop){
		Sheet sheet=null;
		String fileext=prop.getFileext();
		if(TYPE_EXCEL2003.equalsIgnoreCase(fileext))
			sheet=wb.createSheet(sheetName);
		else if(TYPE_EXCEL2007.equalsIgnoreCase(fileext))
			sheet=wb.createSheet(sheetName);
		return sheet;
	}
	public static Workbook creatWorkBook(ExcelSheetProp prop){
		Workbook wb=null;
		String fileext=prop.getFileext();
		if(TYPE_EXCEL2003.equalsIgnoreCase(fileext))
			wb=new HSSFWorkbook();
		else if(TYPE_EXCEL2007.equalsIgnoreCase(fileext))
			wb=new SXSSFWorkbook(100);
		return wb;
	}

	public static Workbook getWorkBook(ExcelSheetProp prop,InputStream stream) throws IOException{
		Workbook wb=null;
		String fileext=prop.getFileext();
		if(TYPE_EXCEL2003.equalsIgnoreCase(fileext))
			wb=new HSSFWorkbook(stream);
		else if(TYPE_EXCEL2007.equalsIgnoreCase(fileext))
			wb=new XSSFWorkbook(stream);
		return wb;
	}
	public static CellStyle getStyle(Workbook wb, int rowspan,int align,TableMergeRegion region,TableHeaderProp header) {  
        CellStyle cs = wb.createCellStyle();  
       
        if (rowspan > 1)  
            cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);  
        cs.setAlignment(getAlignment(align));  

        
        cs.setBorderLeft(CellStyle.BORDER_THIN); 

        cs.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cs.setBorderRight(CellStyle.BORDER_THIN);

        cs.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cs.setBorderTop(CellStyle.BORDER_THIN);  

        cs.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cs.setBorderBottom(CellStyle.BORDER_THIN);
        cs.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        if(header!=null){
        	Font font=wb.createFont();
        	if(header.getFontName()!=null && header.getFontName().equals(""))
        		font.setFontName(header.getFontName());
        	else
        		font.setFontName(defaultFontName);
        	if(header.isBold())
        	{
        		font.setBoldweight((short)2);
        	}
        	if(header.isItalic()){
        		font.setItalic(true);
        	}
        	cs.setFont(font);
        }
        
        String color=region.getForegroundcolor();
        if(color!=null &&!"".equals(color.trim())){
        	int[] rgb=hex2rgb(color);
        	cs.setFillForegroundColor(new XSSFColor(new Color(rgb[0],rgb[1],rgb[2])).getIndexed());
        }else
        	cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());

        cs.setFillPattern(CellStyle.SOLID_FOREGROUND);  
        return cs;  
    }  
	public static CellStyle getStyle(Workbook wb, int rowspan,int align,TableHeaderProp header) {  
        CellStyle cs = wb.createCellStyle();  
       
        if (rowspan > 1)  
            cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);  
        cs.setAlignment(getAlignment(align));  

        cs.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cs.setBorderLeft(CellStyle.BORDER_THIN); 

        cs.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cs.setBorderRight(CellStyle.BORDER_THIN);

        cs.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cs.setBorderTop(CellStyle.BORDER_THIN);  

        cs.setTopBorderColor(IndexedColors.BLACK.getIndex());
//        String color=region.getForegroundcolor();
//        if(color!=null &&!"".equals(color.trim())){
//        	int[] rgb=hex2rgb(color);
//        	cs.setFillForegroundColor(new XSSFColor(new Color(rgb[0],rgb[1],rgb[2])).getIndexed());
//        }else
        	cs.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        	 if(header!=null){
             	Font font=wb.createFont();
             	if(header.getFontName()!=null && header.getFontName().equals(""))
             		font.setFontName(header.getFontName());
             	else
             		font.setFontName(defaultFontName);
             	if(header.isBold())
             	{
             		font.setBoldweight((short)2);
             	}
             	if(header.isItalic()){
             		font.setItalic(true);
             	}
             	cs.setFont(font);
             }
        cs.setFillPattern(CellStyle.SOLID_FOREGROUND);  
        cs.setWrapText(true);
        return cs;  
    }  
  
	public static Cell merged(Sheet sheet,Row row1,String colType, int startRow, int startCell, int endRow, int endCell, CellStyle cs,String value,CreationHelper helper) {  
            Cell cell=null;
			CellRangeAddress cellRangeAddress = new CellRangeAddress(startRow,endRow, (short) startCell,  
                    (short) endCell);  
            sheet.addMergedRegion(cellRangeAddress);  
            setRegionStyle(sheet, cellRangeAddress, cs);  
            
            cell=createCell(row1, startCell, value, colType, cs,helper);
//            else
//            	cell=createCell(row2, startCell, value, Const.META_TYPE_STRING, cs,helper);
            return cell;
        }  
  
    private static void setRegionStyle(Sheet sheet, CellRangeAddress cellRangeAddress,  CellStyle cs) {  
        for (int i = cellRangeAddress.getFirstRow(); i <= cellRangeAddress.getLastRow(); i++) {  
            Row row = getRow(sheet,i);  
            for (int j = cellRangeAddress.getFirstColumn(); j <= cellRangeAddress.getLastColumn(); j++) {  
                Cell cell = CellUtil.getCell(row, (short) j);  
                cell.setCellStyle(cs);  
            }  
        }  
    }  
    public static void caculateHeaderParam(ExcelSheetProp prop,ExcelHeaderProp header){
    	if(header.getHeaderColumnList().size()!=0){
    		int rows=header.getHeaderColumnList().size();
    		
    		int startrow=prop.getStartRow()-1;
    		int startcol=prop.getStartCol()-1;
    		for(int i=0;i<rows;i++){
    			List<ExcelHeaderColumn> clist=header.getHeaderColumnList().get(i);
    			for(ExcelHeaderColumn col:clist){
    				col.setStartrow(startrow+i);
    			}
    		}
    		
    	}
    }
   
    public static int[][] caculateHeaderRowStartcol(ExcelHeaderProp header){
    	int rows=header.getHeaderColumnList().size();
    	
    	int[][] startCol=new int[rows][header.getTotalCol()];
    	for (int i = 0; i < startCol.length; i++) {
			startCol[i]=new int[header.getTotalCol()];
		}
    	
    	for(int i=0;i<rows;i++){
    		int colcount=header.getHeaderColumnList().get(i).size();
    		for(int j=0;j<colcount;j++){
    			caculateStartColofRow(i, j, header,startCol);
    		}
    	}
    	return startCol;
    }
   
    private static void caculateStartColofRow(int row,int pos,ExcelHeaderProp header,int[][] startColArr){
    	if (row == 0) {
			if (pos == 0)
				startColArr[0][0] = 0;
			else {
				startColArr[0][pos] = startColArr[0][pos - 1]
						+ header.getHeaderColumnList().get(0).get(pos - 1)
								.getColspan();
			}
		} else {
			int count = header.getHeaderColumnList().size();
			//�׸�λ���ж�
			if (pos == 0) {
				int posnum = 0;
				int poscount = 0;
				int posfix = pos + 1;
				for (int i = 0; i < row; i++) {
					List<ExcelHeaderColumn> list = header.getHeaderColumnList().get(i);
					for (int j = 0; j < list.size(); j++) {
						ExcelHeaderColumn column = list.get(j);
						int rowspan = column.getRowspan();
						int colspan = column.getColspan();
						if (rowspan + i != count) {
							poscount++;
						}
						if (poscount == posfix) {
							startColArr[row][pos] = startColArr[i][j];
							return;
						}
					}
				}
			}else{
				List<ExcelHeaderColumn> listabove = header.getHeaderColumnList().get(row - 1);
				List<ExcelHeaderColumn> list = header.getHeaderColumnList().get(row);
				int nums = 0;
				int totallength = 0;
				int[] collength=new int[pos];
				for (int i = 0; i < pos; i++) {
					totallength += list.get(i).getColspan();
					collength[i]=totallength;
				}
				int step = 0;
				
				for (int i = 0; i < listabove.size(); i++) {
					int substep=0;
					ExcelHeaderColumn column = listabove.get(i);
					int rowspan = column.getRowspan();
					int colspan = column.getColspan();
					if (rowspan + row-1 != count) {
						nums += colspan;
						step++;
					}
					for (int j = 0; j < collength.length; j++) {
						if(collength[j]>=nums )
							substep=0;
						else
							substep++;
					}
					if (nums > totallength) {
						if (isColumnTheFristChild(collength, listabove, i, row, count)) {
							// Ҷ�ӽڵ��ǵ�һ���ڵ�
								startColArr[row][pos] = startColArr[row - 1][i];
							
						} else {
							// Ҷ�ӽڵ㲻�ǵ�һ���ڵ�
							startColArr[row][pos] = startColArr[row][pos - 1] + header.getHeaderColumnList().get(row).get(pos - 1).getColspan();
						}
						break;

					}else if(nums==totallength && i<listabove.size()-1){
						//ѭ���ж���һ����Ԫ���Ƿ��Ǹ��׽ڵ�
						int temp1=i+1;
						while(header.getHeaderColumnList().get(row-1).get(temp1).getRowspan()+row-1==count){
							temp1++;
						}
						startColArr[row][pos] = startColArr[row - 1][temp1];
						break;
					}
				}
				if(startColArr[row][pos]==0)
					startColArr[row][pos]=startColArr[row][pos - 1] + header.getHeaderColumnList().get(row).get(pos - 1).getColspan();
			}
		}
    	
    }
    private static boolean isColumnTheFristChild(int[] collengtharr,List<ExcelHeaderColumn> listabove,int abovecol,int aboverow,int rowcount){
    	int nums=0;
    	boolean isfrist=false;
    	int beforemaxnums=collengtharr[collengtharr.length-1];
    	for (int i = 0; i < abovecol; i++) {
    		ExcelHeaderColumn column = listabove.get(i);
			int rowspan = column.getRowspan();
			int colspan = column.getColspan();
			if (rowspan + aboverow-1 != rowcount) {
				nums += colspan;
			}
		}
    	if(nums==beforemaxnums)
    		isfrist=true;
    	return isfrist;
    	
    }
    private static void caculateStartColofPos(int row,int pos,ExcelHeaderProp header,int[][] startColArr){
    	//startColArr[row][pos]=startColArr[row][pos-1]+header.getHeaderColumnList().get(index);
    }
  

    public static Row creatRow(Sheet sheet,int i){
    	return sheet.createRow(i);
    }
    public static Row getRow(Sheet sheet, int i){
    	return sheet.getRow(i);
    }
    private static Cell creatCell(Row row,int i){
    	return row.createCell(i);
    }
    private static short getAlignment(int align){
    	return (short) align;
    }
    public static Cell createCell(Row row1,int j,String value,String colType,CellStyle cellStyle,CreationHelper helper){
    	Cell cell=null;
    	if(colType.equals(Const.META_TYPE_STRING))
			cell=createCell(row1,  j, cellStyle,helper,value);
		else if(colType.equals(Const.META_TYPE_NUMERIC)){ 
		   if(!"".equals(value))
				cell=createCell(row1,  j,cellStyle,helper,Double.parseDouble(value));
		}
		else if(colType.equals(Const.META_TYPE_INTEGER)){
			if(!"".equals(value))
				cell=createCell(row1,  j,cellStyle,helper,Integer.parseInt(value));
		}
		else if(colType.equals(Const.META_TYPE_DATE)){
			if(!"".equals(value))
				cell=createCellDate(row1,  j, cellStyle,helper, value);
		}else
			cell=createCell(row1,  j, cellStyle,helper,value);
    	return cell;
    }
    private static Cell createCell(Row row, int column, CellStyle cellStyle,CreationHelper helper,String objvalue)
    {
        Cell cell = row.createCell(column);
       
        String value="";
        if(objvalue!=null)
        	value=objvalue.toString();
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
        return cell;
    }
	private static Cell createCell(Row row, int column, CellStyle cellStyle,CreationHelper helper,double value)
    {
        Cell cell = row.createCell(column);
        
        cellStyle.setDataFormat(helper.createDataFormat().getFormat("#,##0.0"));
      
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
        return cell;
    }
	private static Cell createCell(Row row, int column, CellStyle cellStyle,CreationHelper helper,int value)
    {
        Cell cell = row.createCell(column);
        
        cellStyle.setDataFormat(helper.createDataFormat().getFormat("#,##0"));
      
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
        return cell;
    }
	private static Cell createCellDate(Row row, int column, CellStyle cellStyle,CreationHelper helper,String value)
    {
        Cell cell = row.createCell(column);
        cellStyle.setDataFormat(helper.createDataFormat().getFormat("yyyy-MM-dd hh:mm:ss"));
      
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
        return cell;
    }
	public static ExcelHeaderProp getHeaderPropFromJson(String jsonStr){
		ExcelHeaderProp prop=new ExcelHeaderProp();
		JSONArray array=JSONArray.fromObject(jsonStr);
		JsonConfig config=new JsonConfig();
		config.setRootClass(ExcelMergeRegion.class);
		Map map=new HashMap();
		map.put("subRegions", ExcelMergeRegion.class);
		config.setClassMap(map);
		Collection<ExcelMergeRegion> col=JSONArray.toCollection(array, config);
		prop.setHeaderList((List)col);
		return prop;
	}
	private static int[] hex2rgb(String colorstr)
	{
		int[] colorset=new int[3];
		String sr, sg, sb, sHex;
		if (colorstr.length() != 6)
			colorstr="D6D3CE";
		sHex = colorstr.toUpperCase();
		sr = sHex.substring(0, 2);
		sg = sHex.substring(2, 4);
		sb = sHex.substring(4, 6);
		try
		{
			// Convert Hex to Dec
			colorset[0] = Integer.parseInt(sr, 16);
			colorset[1] = Integer.parseInt(sg, 16);
			colorset[2] = Integer.parseInt(sb, 16);
			for (int i = 0; i < colorset.length; i++) {
				if(colorset[i]>255)
					colorset[i]=255;
			}
			System.out.println(" using color"+colorset[0]+","+colorset[1]+","+colorset[2]);
		}
		catch (Exception e)
		{
			logger.error("Encounter Error ",e);
		}
		return  colorset;
	}

}