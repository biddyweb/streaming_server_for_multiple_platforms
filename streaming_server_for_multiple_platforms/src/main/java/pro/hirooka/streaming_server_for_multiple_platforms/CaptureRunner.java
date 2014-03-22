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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class CaptureRunner implements Runnable{
	
	static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private static Logger log = LoggerFactory.getLogger(CaptureRunner.class);
	private static Marker MARKER_CaptureRunner = MarkerFactory.getMarker("CaptureRunner");
	
	private String FFMPEG_FOR_CAP;
	private String CAPTURE_PROGRAM;
	
	private String TEMP_PATH;
	
	private String videoBitrate;
	private String videoResolution;
	private String thread;
	private String id;
	
	CaptureRunner(
			String _FFMPEG_FOR_CAP,
			String _CAPTURE_PROGRAM,
			
			String _TEMP_PATH,
			
			String _videoBitrate,
			String _videoResolution,
			String _thread,
			String _id
			){
		
		this.FFMPEG_FOR_CAP = _FFMPEG_FOR_CAP;
		this.CAPTURE_PROGRAM = _CAPTURE_PROGRAM;
		
		this.TEMP_PATH = _TEMP_PATH;
		
		this.videoBitrate = _videoBitrate;
		this.videoResolution = _videoResolution;
		this.thread = _thread;
		this.id = _id;
		
		log.debug(MARKER_CaptureRunner, "{} {}", Thread.currentThread().getStackTrace()[1].getMethodName(), "Construcor of CaptureRunner");
	}
	
	public void run(){
		
		SingletonForSSFMP info = SingletonForSSFMP.getInstance();
		
		String[] cmdArray = null;
		
		String[] cmdArrayBase = {
			CAPTURE_PROGRAM, "--b25", "--strip", id, "-", "-", "|",
			FFMPEG_FOR_CAP, "-i", "-",
			"-acodec", "libfaac", "-ab", "128k", "-ar", "44100", "-ac", "2",
			"-s", videoResolution, 
			"-vcodec", "libx264", "-profile:v", "baseline", "-level", "3.0", "-b:v", videoBitrate+"k", "-preset:v", "ultrafast",
			"-threads", thread,
			"-f", "mpegts",
			"-y", TEMP_PATH + FILE_SEPARATOR + "mystream"+videoBitrate+".ts"					
		};
		cmdArray = cmdArrayBase;
		
		
		String cmd = "";
		for(int i = 0; i < cmdArray.length; i++){
			cmd += cmdArray[i] + " ";
		}
		log.debug(MARKER_CaptureRunner, "{} {}", Thread.currentThread().getStackTrace()[1].getMethodName(), cmd);
		
		String capSh = TEMP_PATH + FILE_SEPARATOR + "cap.sh";
		File f = new File(capSh);
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(f));
			bw.write("#!/bin/bash");
			bw.newLine();
			bw.write(cmd);
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// chmod 755 cap.sh
		if(true){
			String[] cmdArrayChmod = {"chmod", "755", capSh};
			ProcessBuilder pb = new ProcessBuilder(cmdArrayChmod);
			Process pr;
			try {
				pr = pb.start();
				//InputStream is = pr.getInputStream();
				InputStream is = pr.getErrorStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String s = "";
				while((s = br.readLine()) != null){
					log.debug(MARKER_CaptureRunner, "{} {}", Thread.currentThread().getStackTrace()[1].getMethodName(), s);
				}
				br.close();
				isr.close();
				is.close();
				pr.destroy();
				pr = null;
				pb = null;
			} catch (IOException e) {
				e.printStackTrace();
			}

		} // cmdArrayChmod
		
		// run cap.sh
		if(true){
			String[] cmdArrayCapSh = {capSh};
			ProcessBuilder pb = new ProcessBuilder(cmdArrayCapSh);
			Process pr;
			try {
				pr = pb.start();
				//InputStream is = pr.getInputStream();
				InputStream is = pr.getErrorStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String s = "";
				while((s = br.readLine()) != null){
					log.debug(MARKER_CaptureRunner, "{} {}", Thread.currentThread().getStackTrace()[1].getMethodName(), s);
				}
				br.close();
				isr.close();
				is.close();
				pr.destroy();
				pr = null;
				pb = null;
				
				if(info.getFlagRemoveFile()){
					Remover r = new Remover(0);
					r.doRemove();
					info.setFlagRemoveFile(0);
				}
			
			} catch (IOException e) {
				e.printStackTrace();
			}

		} // cmdArrayCapSh
		
	} // run()

} // class