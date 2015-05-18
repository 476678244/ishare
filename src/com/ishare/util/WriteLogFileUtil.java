package com.ishare.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteLogFileUtil {

	public static void android_write(String content) {
		final String machinePath = PropertyUtil.ANDROID_ERROR_LOG_PATH;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH_mm_ss");
		String time = sdf.format(new Date()).replace(":", "-");
		String path = machinePath + time + ".txt";
		File file = new File(path);
		FileWriter fr;
		try {
			fr = new FileWriter(file);
			BufferedWriter br = new BufferedWriter(fr);
			br.write(content);
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}

	public static void main(String[] args) {
		// final String machinePath = "/mnt/vdc/android_error";
		final String machinePath = "C:/LQL/";
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH_mm_ss");
		String time = sdf.format(new Date()).replace(":", "-");
		String path = machinePath + time + ".txt";
		String content = "testlog";
		File file = new File(path);
		FileWriter fr;
		try {
			fr = new FileWriter(file);
			BufferedWriter br = new BufferedWriter(fr);
			br.write(content);
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
	}
}
