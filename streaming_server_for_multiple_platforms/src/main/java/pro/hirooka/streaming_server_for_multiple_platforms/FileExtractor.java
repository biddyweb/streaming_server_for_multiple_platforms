/*

The MIT License (MIT)

Copyright (c) 2014 hirooka <https://hirooka.pro/>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

 */

package pro.hirooka.streaming_server_for_multiple_platforms;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@WebServlet(name="FileExtractor", urlPatterns={"/fe"})
public class FileExtractor extends HttpServlet{
	
	// TODO XML ではなく JSON にする．
	// TODO このクラスはいろいろと変更する必要あり．
	
	private static final long serialVersionUID = 4020448520630671964L;
	static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private static Logger log = LoggerFactory.getLogger(FileExtractor.class);
	private static Marker MARKER_FileExtractor = MarkerFactory.getMarker("FileExtractor");

	public void doGet(HttpServletRequest req, HttpServletResponse res){
		
		// Select Directory for File Live or Captured File Live 
		// --- Local ---
		// sw = 0 : File Streaming
		// sw = 1 : File Streaming (Adaptive Bitrate Streaming)
		// sw = 2 : Captured File Streaming
		// sw = 3 : Captured File Streaming (Adaptive Bitrate Streaming)
		// --- NAS ---
		// sw = 4 : File Streaming
		// se = 5 : File Streaming (Adaptive Bitrate Streaming)
		// sw = 6 : Captured File Streaming
		// sw = 7 : Captured File Streaming (Adaptive Bitrate Streaming)
		
		String path = "---";
		Integer flag = Integer.parseInt(req.getParameter("flag"));
		log.debug(MARKER_FileExtractor, "{} [flag] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), flag);
		
		PropertiesLoader prop = new PropertiesLoader();
	    prop.setProperties("ssfmd.properties");
		
	    switch(flag){
		case 0:
		case 1:
			path = prop.getValue("PATH_OF_FILE_LOCAL");
			break;
		case 2:
		case 3:
			path = prop.getValue("PATH_OF_CAPTURED_LOCAL");
			break;
		case 4:
		case 5:
			path = prop.getValue("PATH_OF_FILE_NAS");
			break;
		case 6:
		case 7:
			path = prop.getValue("PATH_OF_CAPTURED_NAS");
			break;
		default:
			break;
		}
		log.debug(MARKER_FileExtractor, "{} [path] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), path);
		
		// ディレクトリ内のファイル名の抽出
		File fileDir = new File(path);
		File[] fileArray = fileDir.listFiles();
		ArrayList<String> fileNameArrayList = new ArrayList<String>();
		
		for(int i = 0; i < fileArray.length; i++){
			
			File file = fileArray[i];
			
			// TODO 拡張子でパターンマッチさせる．
			if(!fileArray[i].toString().endsWith("html") && !fileArray[i].toString().endsWith("xml")){
				
				fileNameArrayList.add(file.getName());
				log.debug(MARKER_FileExtractor, "{} [file.getAbsolutePath()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), file.getAbsolutePath());
				log.debug(MARKER_FileExtractor, "{} [file.getPath()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), file.getPath());
				log.debug(MARKER_FileExtractor, "{} [file.getName()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), file.getName());

			}
			
		}
		//log.debug(MARKER_FileExtractor, "{} [fileName.size()] {}", Thread.currentThread().getStackTrace()[1].getMethodName(), fileNameArrayList.size());
		
		if(req.getParameter("xml") == null){
		
			// file.jsp or file_abs.jsp にディスパッチ
			req.setAttribute("fileNameArrayList", fileNameArrayList);
			RequestDispatcher rd = null;
			
			switch(flag){
			case 0:
			case 4:
				rd = req.getRequestDispatcher("file.jsp");
				break;
			case 1:
			case 5:
				rd = req.getRequestDispatcher("file_abs.jsp");
				break;
			case 2:
			case 6:
				rd = req.getRequestDispatcher("captured.jsp");
				break;
			case 3:
			case 7:
				rd = req.getRequestDispatcher("captured_abs.jsp");
				break;
			default:
				break;
			}
			
			try {
				rd.forward(req,res);
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}else{
			
			res.setContentType("text/xml");
			res.setCharacterEncoding("UTF-8");
			PrintWriter pw;
			try {
				
				pw = res.getWriter();
		   		pw.println("<root>");
	    		
	    		for(int i = 0; i < fileNameArrayList.size(); i++){
	    			
	    			// TODO 拡張子でパターンマッチさせる．
	    			if(!fileNameArrayList.get(i).endsWith("html") && !fileNameArrayList.get(i).endsWith("xml")){
	    				pw.println("<name>" + fileNameArrayList.get(i) + "</name>");
	    			}
	    			
	    		}
	    		
	    		pw.println("</root>");
				pw.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
 
		}
		
	} // doGet
	
} // class