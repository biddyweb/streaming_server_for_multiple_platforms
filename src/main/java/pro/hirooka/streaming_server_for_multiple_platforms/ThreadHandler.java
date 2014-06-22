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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class ThreadHandler implements Runnable{
	
	private static Logger log = LoggerFactory.getLogger(ThreadHandler.class);
	private static Marker MARKER_ThreadHandler = MarkerFactory.getMarker("ThreadHandler");

	private String FFMPEG_FOR_CAM;
	private String FFMPEG_FOR_FILE;
	private String FFMPEG_FOR_CAP;
	private String CAPTURE_PROGRAM;
	
	private String streamPath;
	private String TEMP_PATH;
	private String TEMP_PATH_FOR_ENC;
	private String FILE_PATH;
	private String CAP_PATH;
	
	private String modeLive;
	private String file;
	private String capFile; 
	private String capResolution; 
	private String videoResolution; 
	private String videoProfile;
	private String videoBitrate;
	private String audioCodec;
	private String audioBitrate; 
	private String audioSamplingFreq;
	private String durationLive;
	private String encOrNot;
	private String id;
	private String thread;
	private int abs;
	
	private int TS_PACKET_LENGTH;
	private double DURATION;
	private int URI_IN_PLAYLIST;
	
	private long timerSegmenterDelay;
	private long timerSegmenterPeriod;
	private long timerPlaylisterDelay;
	private long timerPlaylisterPeriod;
	
	ThreadHandler(
			String _FFMPEG_FOR_CAM,
			String _FFMPEG_FOR_FILE,
			String _FFMPEG_FOR_CAP,
			String _CAPTURE_PROGRAM,
			
			String _streamPath,
			String _TEMP_PATH,
			String _TEMP_PATH_FOR_ENC,
			String _FILE_PATH,
			String _CAP_PATH,
			
			String _modeLive,
			String _file,
			String _capFile, 
			String _capResolution, 
			String _videoResolution, 
			String _videoProfile, 
			String _videoBitrate,
			String _audioCodec, 
			String _audioBitrate, 
			String _audioSamplingFreq,
			String _durationLive, 
			String _encOrNot, 
			String _id,
			String _thread,
			int _abs,
			
			int _TS_PACKET_LENGTH,
			double _DURATION,
			int _URI_IN_PLAYLIST,
			
			long _timerSegmenterDelay,
			long _timerSegmenterPeriod,
			long _timerPlaylisterDelay,
			long _timerPlaylisterPeriod
			){
		
		this.FFMPEG_FOR_CAM = _FFMPEG_FOR_CAM;
		this.FFMPEG_FOR_FILE = _FFMPEG_FOR_FILE;
		this.FFMPEG_FOR_CAP = _FFMPEG_FOR_CAP;
		this.CAPTURE_PROGRAM = _CAPTURE_PROGRAM;
		
		this.streamPath =_streamPath;
		this.TEMP_PATH = _TEMP_PATH;
		this.TEMP_PATH_FOR_ENC = _TEMP_PATH_FOR_ENC;
		this.FILE_PATH = _FILE_PATH;
		this.CAP_PATH = _CAP_PATH;
		
		this.modeLive = _modeLive;
		this.file = _file;
		this.capFile = _capFile; 
		this.capResolution = _capResolution; 
		this.videoResolution = _videoResolution; 
		this.videoProfile = _videoProfile;
		this.videoBitrate = _videoBitrate;
		this.audioCodec = _audioCodec;
		this.audioBitrate = _audioBitrate; 
		this.audioSamplingFreq = _audioSamplingFreq;
		this.durationLive = _durationLive;
		this.encOrNot = _encOrNot;
		this.id = _id;
		this.thread = _thread;
		this.abs = _abs;
		
		this.TS_PACKET_LENGTH = _TS_PACKET_LENGTH;
		this.DURATION = _DURATION;
		this.URI_IN_PLAYLIST = _URI_IN_PLAYLIST;
		this.timerSegmenterDelay = _timerSegmenterDelay;
		this.timerSegmenterPeriod = _timerSegmenterPeriod;
		this.timerPlaylisterDelay = _timerPlaylisterDelay;
		this.timerPlaylisterPeriod = _timerPlaylisterPeriod;
		
		log.debug(MARKER_ThreadHandler, "{} Construcor of ThreadHandler", Thread.currentThread().getStackTrace()[1].getMethodName());
	}

	public void run() {
		
		if(modeLive.equals("cap")){
		
			CaptureRunner rc = new CaptureRunner(
				FFMPEG_FOR_CAP,
				CAPTURE_PROGRAM,
				
				TEMP_PATH,
				
				videoBitrate,
				videoResolution, 
				thread,
				id
			);
			Thread rcThread = new Thread(rc, "__CaptureRunner__");
			rcThread.start();
		
			SegmenterRunner rs = new SegmenterRunner(
				FFMPEG_FOR_CAM,
				FFMPEG_FOR_FILE,
				
				streamPath,
				TEMP_PATH,
				TEMP_PATH_FOR_ENC,
				FILE_PATH,
				CAP_PATH,
					
				modeLive,
				file,
				capFile, 
				capResolution, 
				videoResolution, 
				videoProfile, 
				videoBitrate,
				audioCodec, 
				audioBitrate, 
				audioSamplingFreq,
				durationLive, 
				encOrNot, 
				thread,
				abs,
					
				TS_PACKET_LENGTH,
				DURATION,
				
				timerSegmenterDelay,
				timerSegmenterPeriod
			);
			Thread rsThread = new Thread(rs, "__SegmenterRunner__");
			rsThread.start();
		
			PlaylisterRunner rp = new PlaylisterRunner(
				streamPath,
				
				encOrNot,
				abs,
				
				DURATION,
				URI_IN_PLAYLIST,
				
				timerPlaylisterDelay,
				timerPlaylisterPeriod
			);
			Thread rpThread = new Thread(rp, "__PlaylisterRunner__");
			rpThread.start();
		
		} // cap
		
		if(modeLive.equals("camV") || modeLive.equals("camAV") || modeLive.equals("file") || modeLive.equals("captured")){
			
			FFmpegRunner rf = new FFmpegRunner(
				FFMPEG_FOR_CAM,
				FFMPEG_FOR_FILE,
				
				streamPath,
				TEMP_PATH,
				TEMP_PATH_FOR_ENC,
				FILE_PATH,
				CAP_PATH,

				modeLive,
				file,
				capFile, 
				capResolution,
				videoResolution,
				videoBitrate,
				videoProfile, 
				audioCodec,
				audioBitrate,
				audioSamplingFreq,
				durationLive,
				encOrNot,
				thread,
				abs,
				
				TS_PACKET_LENGTH	
			);
			Thread rfThread = new Thread(rf, "__FFmpegRunner__");
			rfThread.start();
		
			SegmenterRunner rs = new SegmenterRunner(
				FFMPEG_FOR_CAM,
				FFMPEG_FOR_FILE,
				
				streamPath,
				TEMP_PATH,
				TEMP_PATH_FOR_ENC,
				FILE_PATH,
				CAP_PATH,
						
				modeLive,
				file,
				capFile, 
				capResolution, 
				videoResolution, 
				videoProfile, 
				videoBitrate,
				audioCodec, 
				audioBitrate, 
				audioSamplingFreq,
				durationLive, 
				encOrNot, 
				thread,
				abs,
						
				TS_PACKET_LENGTH,
				DURATION,
					
				timerSegmenterDelay,
				timerSegmenterPeriod
			);
			Thread rsThread = new Thread(rs, "__SegmenterRunner__");
			rsThread.start();
		
			PlaylisterRunner rp = new PlaylisterRunner(
				streamPath,
				
				encOrNot,
				abs,
				
				DURATION,
				URI_IN_PLAYLIST,
				
				timerPlaylisterDelay,
				timerPlaylisterPeriod
			);
			Thread rpThread = new Thread(rp, "__RunPlaylister__");
			rpThread.start();
		
		} // cam, file, captured
		
		if(modeLive.equals("capturedTimeShifted")){
		
			SegmenterRunner rs = new SegmenterRunner(
				FFMPEG_FOR_CAM,
				FFMPEG_FOR_FILE,
				
				streamPath,
				TEMP_PATH,
				TEMP_PATH_FOR_ENC,
				FILE_PATH,
				CAP_PATH,
						
				modeLive,
				file,
				capFile, 
				capResolution, 
				videoResolution, 
				videoProfile, 
				videoBitrate,
				audioCodec, 
				audioBitrate, 
				audioSamplingFreq,
				durationLive, 
				encOrNot, 
				thread,
				abs,
						
				TS_PACKET_LENGTH,
				DURATION,
					
				timerSegmenterDelay,
				timerSegmenterPeriod
			);
			Thread rsThread = new Thread(rs, "__SegmenterRunner__");
			rsThread.start();
		
			PlaylisterRunner rp = new PlaylisterRunner(
				streamPath,
				
				encOrNot,
				abs,
				
				DURATION,
				URI_IN_PLAYLIST,
				
				timerPlaylisterDelay,
				timerPlaylisterPeriod
			);
			Thread rpThread = new Thread(rp, "__PlaylisterRunner__");
			rpThread.start();
		
		} // capturedTimeShifted
		
	} // run
	
} // class