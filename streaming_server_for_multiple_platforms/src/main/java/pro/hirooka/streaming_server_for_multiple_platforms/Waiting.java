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

/* HTML5 の video タグでも再生できることを確認するためのテスト用の簡易的な UI */
/* iOS ではネイティブな再生アプリを製作する． */

package pro.hirooka.streaming_server_for_multiple_platforms;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@WebServlet(name="Waiting", urlPatterns={"/wait"})
public class Waiting extends HttpServlet{

	private static final long serialVersionUID = -5417770358182783280L;
	
	private static Logger log = LoggerFactory.getLogger(Waiting.class);
	private static Marker MARKER_Waiting = MarkerFactory.getMarker("Waiting");
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res){
		
		ServletContext sc = getServletContext();
		String[] strForWaiting = (String[])sc.getAttribute("strForWaiting");
		
		res.setContentType("text/html");
		res.setCharacterEncoding("UTF-8");
		
		try {
			PrintWriter pw = res.getWriter();
			
	        int random = new Random().nextInt(10);
			
			pw.write(
					"<!DOCTYPE html>" +
					"<html>" +
					"<head>" +
					"<meta charset=\"UTF-8\">" +
					"<meta name=\"viewport\" content=\"width=device-width, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no\">" +
					//"<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">" +
					"<meta name=\"apple-mobile-web-app-capable\" content=\"yes\">" + 
					//"<script src=\"jquery-1.9.1.min.js\"></script>" +
					//"<script src=\"link.js\"></script>" +
				
					"<script>" +
					"count = " + strForWaiting[2] + ";" +
//		 			"count = count + 2;" +
					"count = count - 1;" +
					"function counter(){" +
					"	document.getElementById(\"counter\").innerText = count;" +
					"	count--;" +
					"}" +
					"window.onload = function(){" +
					"	setInterval(counter, 1000);" +
					"};" +
					"</script>" +
				
					"<title>please wait for a while...</title>" +

					// single playlist : filename/bitrate/plname.m3u8 : [0] [4] [3]
					"<meta http-equiv = \"refresh\" content=\"" + strForWaiting[1] + "; URL=stream/" + strForWaiting[0] + "/" + strForWaiting[4] + "/" + strForWaiting[3] + "\">" + 
					"</head><body><p id=\"pl\">Streaming starts after " + "<span id=\"counter\"></span>"  +  " s automatically." + 
					"<p id=\"pl\">Direct link to M3U8 playlist " +
					"<span id=\"pl_url\"><a href = \"stream/" + strForWaiting[0] + "/" + strForWaiting[4] + "/" + strForWaiting[3] + "\">" + strForWaiting[3] + "</a></span>" +
					
					"<hr>" +
					"<video src = \"stream/" + strForWaiting[0] + "/" + strForWaiting[4] + "/" + strForWaiting[3] + "\" controls autoplay >" + "</video>" +

					
					"<hr>" +
//					"<form action=\"./cancel\">" +
//					"<button id=\"stopButton\" type=\"submit\">Cancel</button>" +
//					"</form>" +
//					"<a href=\"./cancel\">cancel</a>" +

					"<a href=\"cancel?foo=" + random + "\">cancel</a>" +
					
//					"<form action=\"./cancel\">" +
//					"<div id=\"button\">" +
//					"<button id=\"stopButton\" type=\"submit\">Cancel</button>" +
//					"</div>" +
//					"</form>" +
					

				
//					"<hr>" +
//					"<form action=\"./remove\">" +
//					"<button id=\"stopButton\" type=\"submit\">Complete</button>" +
//					"</form>" +

					"</body>" +
					"</html>"
			);
			pw.close();
			log.debug(MARKER_Waiting, "{} HTTP Response", Thread.currentThread().getStackTrace()[1].getMethodName());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	} // doGet()

} // class