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

import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class PlaylisterRunner implements Runnable{

	private static Logger log = LoggerFactory.getLogger(PlaylisterRunner.class);
	private static Marker MARKER_PlaylisterRunner = MarkerFactory.getMarker("PlaylisterRunner");
	
	private String streamPath;
	
	private String encOrNot;
	private int abs;
	
	private double DURATION;
	private int URI_IN_PLAYLIST;
	
	private long timerPlaylisterDelay;
	private long timerPlaylisterPeriod;

	PlaylisterRunner(
			
		String _streamPath,
		
		String _encOrNot,
		int _abs,
		
		double _DURATION,
		int _URI_IN_PLAYLIST,
		
		long _timerPlaylisterDelay,
		long _timerPlaylisterPeriod
		){
		
		this.streamPath = _streamPath;
		
		this.encOrNot = _encOrNot;
		this.abs = _abs;
		
		this.DURATION = _DURATION;
		this.URI_IN_PLAYLIST = _URI_IN_PLAYLIST;
		
		this.timerPlaylisterDelay = _timerPlaylisterDelay;
		this.timerPlaylisterPeriod = _timerPlaylisterPeriod;
		
		log.debug(MARKER_PlaylisterRunner, "{} Construcor of PlaylisterRunner : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
		
	} // Constructor
	
	public void run() {

		Timer timerPlaylister = new Timer();
		timerPlaylister.scheduleAtFixedRate(new Playlister(
				streamPath,
				
				encOrNot,
				abs,
				
				DURATION,
				URI_IN_PLAYLIST
		), timerPlaylisterDelay, timerPlaylisterPeriod);
		
		while(true){
			if(SingletonForSSFMP.getInstance().getFlagTimerPlaylister()){
				timerPlaylister.cancel();
				timerPlaylister = null;
				break;
			}
		}
		log.debug(MARKER_PlaylisterRunner, "{} END : RunPlaylister : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), abs);
		
	} // run()

} // class