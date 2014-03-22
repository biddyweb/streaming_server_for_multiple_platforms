<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="java.util.ArrayList"%>

<!-- 
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
 -->
 
<!-- HTML5 の video タグでも再生できることを確認するためのテスト用の簡易的な UI -->
<!-- iOS ではネイティブな再生アプリを製作する． -->

<!DOCTYPE html>
<html>

<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
	<link rel="stylesheet" type="text/css" href="style.css">
	<title>Video Streaming</title>
</head>

<body>

<p id="title">File Streaming</p>

<form action="./ss_abs">

	<input type = "hidden" name = "modeLive" value = "file">

	<div id="button">
		<button id=startButton type="submit">Start!</button>
	</div>

	<p><hr>

	<table id="setting">

		<tr>
			<td>Select File</td>
			<td>
				<select name = "file">
					<%
						ArrayList<String> fileNameArrayList = new ArrayList<String>();
						fileNameArrayList = (ArrayList<String>)request.getAttribute("fileNameArrayList");
						for(int i = 0; i < fileNameArrayList.size(); i++){
							out.println(fileNameArrayList.get(i));
							out.println("<option value=\"" + fileNameArrayList.get(i) + "\">" + fileNameArrayList.get(i) + "</option>");
						}
					%>
				</select>
			</td>
		</tr>

		<tr>
			<td>Video Resolution</td>
			<td>
				<select name = "videoResolution">
					<option value="1920x1080">1920x1080</option>
					<option value="1280x720" selected>1280x720</option>
					<option value="960x540">960x540</option>
					<option value="640x360">640x360</option>
				</select>
			</td>
		</tr>

		<tr>
			<td>Video Codec</td>
			<td>H.264</td>
		</tr>

		<tr>
			<td>H.264 Profile</td>
			<td>
				<select name = "videoProfile">
					<option value="base" selected>Baseline</option>
					<option value="main">Main</option>
					<option value="high">High</option>
				</select>
			</td>
		</tr>

		<tr>
			<td>Video Bitrate [kbps]</td>
			<td><input type = "number" name = "videoBitrate" value = "300"></td>
		</tr>
		<tr>
			<td>Video Bitrate2 [kbps]</td>
			<td><input type = "number" name = "videoBitrate2" value = "1000"></td>
		</tr>
		<tr>
			<td>Video Bitrate3 [kbps]</td>
			<td><input type = "number" name = "videoBitrate3" value = "3000"></td>
		</tr>

		<tr>
			<td>Audio Codec</td>
			<td>
				<select name = "audioCodec">
					<option value="libfaac">AAC-LC</option>
					<option value="libmp3lame">MP3</option>
				</select>
			</td>
		</tr>

		<tr>
			<td>Audio Bitrate [kbps]</td>
			<td><input type = "number" name = "audioBitrate" value = "128"></td>
		</tr>

		<tr>
			<td>Audio Sampling Frequency [kHz]</td>
			<td>
				<select name = "audioSamplingFreq">
					<option value="44100">44.1</option>
					<option value="48000">48.0</option>
				</select>
			</td>
		</tr>

		<tr>	
			<td>Encryption</td>
			<td> 
				<select name = "encOrNot">
					<option value="3">Encrypted Stream</option>
					<option value="4">Not Encrypted Stream</option>
				</select>
			</td>
		</tr>	

	</table>

</form>

<p><hr>

<div id="back"><a href="index.html">back</a></div>

</body>

</html>