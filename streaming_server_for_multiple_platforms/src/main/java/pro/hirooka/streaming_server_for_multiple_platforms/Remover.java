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

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Remover{

	static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private static Logger log = LoggerFactory.getLogger(Remover.class);
	private static Marker MARKER_Remover = MarkerFactory.getMarker("Remover");
	
	private int flagAbs;
	
	Remover(int _flagAbs){
		this.flagAbs = _flagAbs;
		log.debug(MARKER_Remover, "{} Construcor of Remover : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), flagAbs);
	} // Constructor

	public void doRemove(){
		
		log.debug(MARKER_Remover, "{}", Thread.currentThread().getStackTrace()[1].getMethodName());
		
		// STREAM_PATH_ROOT, TEMP_PATH のすべてのファイルを削除
		//removeFiles();
		
		SingletonForSSFMP info = SingletonForSSFMP.getInstance();
		
		SingletonForSSFMP2 info2 = null;
		SingletonForSSFMP3 info3 = null;
		if(flagAbs == 1){
			info2 = SingletonForSSFMP2.getInstance();
			info3 = SingletonForSSFMP3.getInstance();
		}
		
		boolean flagStreamDir = false;
		boolean flagTempDir = false;
		
		try {
			
			// STREAM_ROOT_PATH の削除
			FileUtils.cleanDirectory(new File(info.getStreamPathRoot()));
			log.debug(MARKER_Remover, "{} {} is cleaned.", Thread.currentThread().getStackTrace()[1].getMethodName(), info.getStreamPathRoot());
			
			if(new File(info.getStreamPathRoot()).listFiles().length == 0){
				File streamDir = new File(info.getStreamPathRoot());
				streamDir.delete();
				log.debug(MARKER_Remover, "{} {} is deleted.", Thread.currentThread().getStackTrace()[1].getMethodName(), info.getStreamPathRoot());
				info.setStreamPathRoot("");
				flagStreamDir = true;
			}
			
			PropertiesLoader prop = new PropertiesLoader();
			prop.setProperties("ssfmp.properties");
			
			// TEMP_PATH の削除
			FileUtils.cleanDirectory(new File(prop.getValue("PATH_OF_TEMP")));
			log.debug(MARKER_Remover, "{} {} is cleaned.", Thread.currentThread().getStackTrace()[1].getMethodName(), prop.getValue("PATH_OF_TEMP"));
			
			if(new File(prop.getValue("PATH_OF_TEMP")).listFiles().length == 0){
				File tempDir = new File(prop.getValue("PATH_OF_TEMP"));
				tempDir.delete();
				log.debug(MARKER_Remover, "{} {} is deleted.", Thread.currentThread().getStackTrace()[1].getMethodName(), prop.getValue("PATH_OF_TEMP"));
				flagTempDir = true;
			}
			
			if(flagStreamDir && flagTempDir){
				log.debug(MARKER_Remover, "{} REMOVE ALL!", Thread.currentThread().getStackTrace()[1].getMethodName());
				info.init();
				flagStreamDir = false;
				flagTempDir = false;
				
				if(info2 != null) info2.init();
				if(info3 != null) info3.init();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	} // doRemove()

} // class Remover