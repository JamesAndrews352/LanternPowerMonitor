package com.lanternsoftware.zwave;

import com.lanternsoftware.zwave.context.ZWaveApp;

public class TestStartup {
	public static void main(String[] args) {
		ZWaveApp app = new ZWaveApp();
		app.start();
		try {
			Thread.sleep(20000);
		} catch (InterruptedException _e) {
			_e.printStackTrace();
		}
		app.stop();
	}
}
