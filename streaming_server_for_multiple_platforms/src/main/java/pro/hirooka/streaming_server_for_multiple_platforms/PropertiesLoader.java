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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class PropertiesLoader {
	
	private static Logger log = LoggerFactory.getLogger(PropertiesLoader.class);
	private static Marker MARKER_PropertiesLoader = MarkerFactory.getMarker("PropertiesLoader");

	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	//private static final String LINE_SEPARATOR = System.lineSeparator();

	private Properties prop;

	PropertiesLoader(){
		log.debug(MARKER_PropertiesLoader, "{} Construcor of PropertiesLoader", Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	boolean setProperties(String str){
		prop = new Properties();
		try{
			//prop.load(this.getClass().getResourceAsStream(str));
			//prop.load(this.getClass().getResourceAsStream(FILE_SEPARATOR + str));
			//InputStream is = new BufferedInputStream(new FileInputStream(System.getenv("HOME") + FILE_SEPARATOR + "ssfmd.properties"));
			prop.load(new BufferedInputStream(new FileInputStream(System.getenv("HOME") + FILE_SEPARATOR + "ssfmd.properties")));
			return true;
		}catch(NullPointerException | IOException ex){
			return false;
		}
	}

	String getValue(String key){
		return prop.getProperty(key);
	}

}
