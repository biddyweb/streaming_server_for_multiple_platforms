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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

class FFmpegRunner implements Runnable{
	
	// TODO コマンドをハードコードではなくて外部から取り込めるように．
	// TODO USB カメラのデバイス名をハードコードではなくて外部から取り込めるように．
	
	static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private static Logger log = LoggerFactory.getLogger(FFmpegRunner.class);
	private static Marker MARKER_FFmpegRunner = MarkerFactory.getMarker("FFmpegRunner");
	
	private String FFMPEG_FOR_CAM;
	private String FFMPEG_FOR_FILE;
	
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
	private String videoBitrate;
	private String videoProfile;
	private String audioCodec;
	private String audioBitrate;
	private String audioSamplingFreq;
	private String durationLive;
	private String encOrNot;
	private String thread;
	private int abs;
	
	private int MPEG2_TS_PACKET_LENGTH;
	
	FFmpegRunner(
			String _FFMPEG_FOR_CAM,
			String _FFMPEG_FOR_FILE,
			
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
			String _videoBitrate,
			String _videoProfile, 
			String _audioCodec,
			String _audioBitrate,
			String _audioSamplingFreq,
			String _durationLive, 
			String _encOrNot,
			String _thread,
			int _abs,
			
			int _MPEG2_TS_PACKET_LENGTH
			){
		
		this.FFMPEG_FOR_CAM = _FFMPEG_FOR_CAM;
		this.FFMPEG_FOR_FILE = _FFMPEG_FOR_FILE;
		
		this.streamPath = _streamPath;
		this.TEMP_PATH = _TEMP_PATH;
		this.TEMP_PATH_FOR_ENC = _TEMP_PATH_FOR_ENC;
		this.FILE_PATH = _FILE_PATH;
		this.CAP_PATH = _CAP_PATH;

		this.modeLive = _modeLive;
		this.file = _file;
		this.capFile = _capFile;
		this.capResolution = _capResolution;
		this.videoResolution = _videoResolution;
		this.videoBitrate = _videoBitrate;
		this.videoProfile = _videoProfile;
		this.audioCodec = _audioCodec;
		this.audioBitrate = _audioBitrate;
		this.audioSamplingFreq = _audioSamplingFreq;
		this.durationLive = _durationLive;
		this.encOrNot = _encOrNot;
		this.thread = _thread;
		this.abs = _abs;

		this.MPEG2_TS_PACKET_LENGTH = _MPEG2_TS_PACKET_LENGTH;
		
		log.debug(MARKER_FFmpegRunner, "{} Construcor of FFmpegRunner : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
		
	} // Constructor
	
	public void run(){
		
		SingletonForSSFMP info = null;
		SingletonForSSFMP2 info2 = null;
		SingletonForSSFMP3 info3 = null;
		
		switch(abs){
		case 0:
			info = SingletonForSSFMP.getInstance();
			break;
		case 1:
			info2 = SingletonForSSFMP2.getInstance();
			break;
		case 2:
			info3 = SingletonForSSFMP3.getInstance();
			break;
		default:
			//info = SingletonForMyStreamer.getInstance();
			break;
		}
		
		int seqCapturedTimeShifted = 0; //info.getSeqTsOkkakeEncode();
		if((abs == 0) && (info != null)){
			info.getSeqTsCapturedTimeShifted();
		}else if((abs == 1) && (info2 != null)){
			info2.getSeqTsCapturedTimeShifted();
		}else if((abs == 2) && (info3 != null)){
			info3.getSeqTsCapturedTimeShifted();
		}
		
		String[] cmdArray = null;
		
		if(modeLive.equals("camV")){ 
		
			if(videoProfile.equals("base")){
				
				String[] cmdArrayBase = {
						FFMPEG_FOR_CAM, "-f", "video4linux2", "-s", capResolution, "-r", "30", "-i", "/dev/video0",
						"-s", videoResolution, 
						//"-vcodec", "libx264", "-b:v", videoBitrate+"k", "-pix_fmt", "yuv420p",
						"-vcodec", "libx264", "-preset", "ultrafast", "-b:v", videoBitrate+"k", "-pix_fmt", "yuv420p",
						//"-coder", "0", "-bf", "0", "-pix_fmt", "yuv420p",
						"-threads", thread,
						"-t", durationLive,
						"-f", "mpegts",
						"-y", TEMP_PATH + FILE_SEPARATOR + "mystream"+videoBitrate+".ts"					
				};
				cmdArray = cmdArrayBase;
				
			}else if(videoProfile.equals("main")){
				
				String[] cmdArrayMain = {
						//"-coder", "1", "-bf", "3", "-pix_fmt", "yuv420p",
				};
				cmdArray = cmdArrayMain;
				
			}else if(videoProfile.equals("high")){
				
				String[] cmdArrayHigh = {
						//"-coder", "1", "-bf", "3", "-flags2", "dct8x8", "-pix_fmt", "yuv420p",	
				};
				cmdArray = cmdArrayHigh;
				
			} else {
				
				//
				
			}
			
		}else if(modeLive.equals("camAV")){
			
			if(videoProfile.equals("base")){
				
				String[] cmdArrayBase = {
						FFMPEG_FOR_CAM, "-f", "video4linux2", "-s", capResolution, "-r", "30", "-i", "/dev/video0",
						"-f", "alsa", "-i", "hw:0,0", "-acodec", "libfaac", "-ab", "128k", "-ac", "2", //"-ar", "44100"
						"-s", videoResolution, 
						"-vcodec", "libx264", "-preset", "ultrafast", "-b:v", videoBitrate+"k", "-pix_fmt", "yuv420p",
						//"-vcodec", "libx264", "-preset", "ultrafast", "-b:v", videoBitrate+"k",
						//"-vcodec", "libx264", "-profile:v", "baseline", "-level", "3.1", "-b:v", videoBitrate+"k", "-pix_fmt", "yuv420p",
						"-threads", thread,
						"-t", durationLive,
						"-f", "mpegts",
						"-y", TEMP_PATH + FILE_SEPARATOR + "mystream"+videoBitrate+".ts"					
				};
				cmdArray = cmdArrayBase;
				
			}else if(videoProfile.equals("main")){
				
				String[] cmdArrayMain = {	
				};
				cmdArray = cmdArrayMain;
			
			}else if(videoProfile.equals("high")){
			
				String[] cmdArrayHigh = {
				};
				cmdArray = cmdArrayHigh;
			
			} else {
			
				//
				
			}
			
		} else if(modeLive.equals("file")){
			
			if(videoProfile.equals("base")){
			
				String[] cmdArrayFileLive = {
						FFMPEG_FOR_FILE, "-i", FILE_PATH + FILE_SEPARATOR + file,
						"-acodec", audioCodec, "-ab", audioBitrate+"k", "-ar", audioSamplingFreq, "-ac", "2",
						"-s", videoResolution, 
					//	"-vcodec", "libx264", "-profile:v", "baseline", "-level", "3.1", "-b:v", videoBitrate+"k",
						"-vcodec", "libx264", "-profile:v", "baseline", "-level", "3.1", "-b:v", videoBitrate+"k", "-preset:v", "ultrafast",
						"-threads", thread,
						"-f", "mpegts",
						"-y", TEMP_PATH + FILE_SEPARATOR + "mystream"+videoBitrate+".ts"
				};
				cmdArray = cmdArrayFileLive;
				
			}else if(videoProfile.equals("main")){
				
				String[] cmdArrayFileLive = {
				};
				cmdArray = cmdArrayFileLive;
				
			}else if(videoProfile.equals("high")){
				
				String[] cmdArrayFileLive = {
				};
				cmdArray = cmdArrayFileLive;
				
			} else {
				
				//
				
			}
			
		} else if(modeLive.equals("captured")){
			
			if(videoProfile.equals("base")){
				
				String[] cmdArrayPt2Live = {
						FFMPEG_FOR_FILE, "-i", CAP_PATH + FILE_SEPARATOR + capFile,
						"-acodec", audioCodec, "-ab", audioBitrate+"k", "-ar", audioSamplingFreq, "-ac", "2",
						"-s", videoResolution, 
						"-vcodec", "libx264", "-profile:v", "baseline", "-level", "3.1", "-b:v", videoBitrate+"k",
						"-threads", thread,
						"-f", "mpegts",
						"-y", TEMP_PATH + FILE_SEPARATOR + "mystream"+videoBitrate+".ts"
				};
				cmdArray = cmdArrayPt2Live;
				
			}else if(videoProfile.equals("main")){
				
				String[] cmdArrayPt2Live = {
				};
				cmdArray = cmdArrayPt2Live;
				
			}else if(videoProfile.equals("high")){
				
				String[] cmdArrayPt2Live = {
				};
				cmdArray = cmdArrayPt2Live;
				
			} else {
				
				//
				
			}
			
		} else if(modeLive.equals("capturedTimeShifted")){
			
			if(encOrNot.equals("1") || encOrNot.equals("3")){
				
				if(videoProfile.equals("base")){
				
					String[] cmdArrayPt2Live = {
						"ffmpeg", "-i", TEMP_PATH_FOR_ENC + FILE_SEPARATOR + "fileSequence" + seqCapturedTimeShifted + ".ts",
						"-acodec", audioCodec, "-ab", audioBitrate+"k", "-ar", audioSamplingFreq, "-ac", "2",
						"-s", videoResolution, 
						"-vcodec", "libx264", "-profile:v", "baseline", "-level", "3.1", "-b:v", videoBitrate+"k",
						"-threads", thread,
						"-f", "mpegts",
						"-y", TEMP_PATH_FOR_ENC + FILE_SEPARATOR + "fileSequenceEncoded" + seqCapturedTimeShifted + ".ts"
					};
					cmdArray = cmdArrayPt2Live;
					
				}else if(videoProfile.equals("main")){
					
					String[] cmdArrayPt2Live = {
					};
					cmdArray = cmdArrayPt2Live;
				
				}else if(videoProfile.equals("high")){
					
					String[] cmdArrayPt2Live = {
					};
					cmdArray = cmdArrayPt2Live;
					
				} else {
				
				}
				
			}else if(encOrNot.equals("2") || encOrNot.equals("4")){
				
				if(videoProfile.equals("base")){
					
					String[] cmdArrayPt2Live = {
						FFMPEG_FOR_FILE, "-i", TEMP_PATH_FOR_ENC + FILE_SEPARATOR + "fileSequence" + seqCapturedTimeShifted + ".ts",
						"-acodec", audioCodec, "-ab", audioBitrate+"k", "-ar", audioSamplingFreq, "-ac", "2",
						"-s", videoResolution, 
						"-vcodec", "libx264", "-profile:v", "baseline", "-level", "3.1", "-b:v", videoBitrate+"k",
						"-threads", thread,
						"-f", "mpegts",
						"-y", streamPath + FILE_SEPARATOR + "fileSequence" + seqCapturedTimeShifted + ".ts"
					};
					cmdArray = cmdArrayPt2Live;
					
				}else if(videoProfile.equals("main")){
					
					String[] cmdArrayPt2Live = {
					};
					cmdArray = cmdArrayPt2Live;
				
				}else if(videoProfile.equals("high")){
					
					String[] cmdArrayPt2Live = {
					};
					cmdArray = cmdArrayPt2Live;
					
				} else {
				
					//
					
				}
				
			}else{
				
				//
			}
			
		} else {
			
			//
			
		}
		
		String cmd = "";
		for(int i = 0; i < cmdArray.length; i++){
			cmd += cmdArray[i] + " ";
		}
		log.debug(MARKER_FFmpegRunner, "{} {}", Thread.currentThread().getStackTrace()[1].getMethodName(), cmd);
		
		ProcessBuilder pb = new ProcessBuilder(cmdArray);
		try {
			
			log.debug(MARKER_FFmpegRunner, "{} Begin FFmpeg!", Thread.currentThread().getStackTrace()[1].getMethodName());
			Process pr = pb.start();
			InputStream is = pr.getErrorStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			String str = "";
			while((str = br.readLine()) != null){
				log.debug(MARKER_FFmpegRunner, "{} : {} {}", abs, Thread.currentThread().getStackTrace()[1].getMethodName(), str);
			}
			br.close();
			isr.close();
			is.close();
			pr.destroy();
			pr = null;
			pb = null;
			log.debug(MARKER_FFmpegRunner, "{} End FFmpeg!", Thread.currentThread().getStackTrace()[1].getMethodName());
			
			// TODO remove 処理が冗長すぎる．
			if(!modeLive.equals("capturedTimeShifted")){

				if((abs == 0) && (info != null ) && SingletonForSSFMP.getInstance().getFlagRemoveFile()){
					if((info2 == null) && (info3 == null)){
						Remover r = new Remover(0);
						r.doRemove();
					}else if((info2 != null) && (info3 == null)){
						if(SingletonForSSFMP2.getInstance().getFlagRemoveFile()){
							Remover r = new Remover(1);
							r.doRemove();
						}
					}else if((info2 == null) && (info3 != null)){
						if(SingletonForSSFMP3.getInstance().getFlagRemoveFile()){
							Remover r = new Remover(1);
							r.doRemove();
						}
					}else if((info2 != null) && (info3 != null)){
						if(SingletonForSSFMP2.getInstance().getFlagRemoveFile() && SingletonForSSFMP3.getInstance().getFlagRemoveFile()){
							Remover r = new Remover(1);
							r.doRemove();
						}
					}
				}else if((abs == 1) && (info2 != null )){
					if(SingletonForSSFMP.getInstance().getFlagRemoveFile() && SingletonForSSFMP2.getInstance().getFlagRemoveFile() && SingletonForSSFMP3.getInstance().getFlagRemoveFile()){
						Remover r = new Remover(1);
						r.doRemove();
					}
				}else if((abs == 2) && (info3 != null )){
					if(SingletonForSSFMP.getInstance().getFlagRemoveFile() && SingletonForSSFMP2.getInstance().getFlagRemoveFile() && SingletonForSSFMP3.getInstance().getFlagRemoveFile()){
						Remover r = new Remover(1);
						r.doRemove();
					}
				}
				
			}else if(modeLive.equals("capturedTimeShifted")){
			
				log.debug(MARKER_FFmpegRunner, "{} finish, capturedTimeShifted", Thread.currentThread().getStackTrace()[1].getMethodName());
				
				if((abs == 0) && (info != null)){
					if((encOrNot.equals("1") || encOrNot.equals("3")) && !SingletonForSSFMP.getInstance().getFlagRemoveFile()){
						log.debug(MARKER_FFmpegRunner, "{} -> Encrypter", Thread.currentThread().getStackTrace()[1].getMethodName());
						SingletonForSSFMP.getInstance().setSeqTsCapturedTimeShifted(seqCapturedTimeShifted);
					
						Encrypter ec = new Encrypter(streamPath, TEMP_PATH_FOR_ENC, modeLive, abs, MPEG2_TS_PACKET_LENGTH);
						Thread ecThread = new Thread(ec);
						ecThread.start();
					}
				}
				
				if((abs == 1) && (info2 != null)){
					if((encOrNot.equals("1") || encOrNot.equals("3")) && !SingletonForSSFMP2.getInstance().getFlagRemoveFile()){
						log.debug(MARKER_FFmpegRunner, "{} -> Encrypter", Thread.currentThread().getStackTrace()[1].getMethodName());
						SingletonForSSFMP2.getInstance().setSeqTsCapturedTimeShifted(seqCapturedTimeShifted);
					
						Encrypter ec = new Encrypter(streamPath, TEMP_PATH_FOR_ENC, modeLive, abs, MPEG2_TS_PACKET_LENGTH);
						Thread ecThread = new Thread(ec);
						ecThread.start();
					}
				}
				
				if((abs == 2) && (info3 != null)){
					if((encOrNot.equals("1") || encOrNot.equals("3")) && !SingletonForSSFMP3.getInstance().getFlagRemoveFile()){
						log.debug(MARKER_FFmpegRunner, "{} -> Encrypter", Thread.currentThread().getStackTrace()[1].getMethodName());
						SingletonForSSFMP3.getInstance().setSeqTsCapturedTimeShifted(seqCapturedTimeShifted);
					
						Encrypter ec = new Encrypter(streamPath, TEMP_PATH_FOR_ENC, modeLive, abs, MPEG2_TS_PACKET_LENGTH);
						Thread ecThread = new Thread(ec);
						ecThread.start();
					}
				}
				
				if(info2 == null && info3 == null){
					if(SingletonForSSFMP.getInstance().getFlagRemoveFile()){
						Remover r = new Remover(0);
						r.doRemove();
						SingletonForSSFMP.getInstance().setFlagRemoveFile(0);
						log.debug(MARKER_FFmpegRunner, "{} capturedTimeShifted - canceller - FFmepgRunner calls remover type1", Thread.currentThread().getStackTrace()[1].getMethodName());
					}
				}
				if(info2 != null && info3 == null){
					if(SingletonForSSFMP.getInstance().getFlagRemoveFile() && SingletonForSSFMP2.getInstance().getFlagRemoveFile()){
						Remover r = new Remover(1);
						r.doRemove();
						SingletonForSSFMP.getInstance().setFlagRemoveFile(0);
						SingletonForSSFMP2.getInstance().setFlagRemoveFile(0);
						log.debug(MARKER_FFmpegRunner, "{} capturedTimeShifted - canceller - FFmepgRunner calls remover type2", Thread.currentThread().getStackTrace()[1].getMethodName());
					}
				}
				if(info2 == null && info3 != null){
					if(SingletonForSSFMP.getInstance().getFlagRemoveFile() && SingletonForSSFMP3.getInstance().getFlagRemoveFile()){
						Remover r = new Remover(1);
						r.doRemove();
						SingletonForSSFMP.getInstance().setFlagRemoveFile(0);
						SingletonForSSFMP3.getInstance().setFlagRemoveFile(0);
						log.debug(MARKER_FFmpegRunner, "{} capturedTimeShifted - canceller - FFmepgRunner calls remover type2", Thread.currentThread().getStackTrace()[1].getMethodName());
					}
				}
				if(info2 != null && info3 != null){
					if(SingletonForSSFMP.getInstance().getFlagRemoveFile() && SingletonForSSFMP2.getInstance().getFlagRemoveFile() && SingletonForSSFMP3.getInstance().getFlagRemoveFile()){
						Remover r = new Remover(1);
						r.doRemove();
						info.setFlagRemoveFile(0);
						info2.setFlagRemoveFile(0);
						info3.setFlagRemoveFile(0);
						log.debug(MARKER_FFmpegRunner, "{} capturedTimeShifted - canceller - FFmepgRunner calls remover type2", Thread.currentThread().getStackTrace()[1].getMethodName());
					}
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} //try
		
	} // run
	
} // class