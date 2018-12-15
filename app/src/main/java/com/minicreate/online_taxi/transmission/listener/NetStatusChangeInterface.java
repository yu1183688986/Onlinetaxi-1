package com.minicreate.online_taxi.transmission.listener;

public interface NetStatusChangeInterface {

	public void onNetOn2Off();
	public void onNetOff2On();
	public void onNetWifi2Moblie();
	public void onNetMoblie2Wifi();
	public void onNetChange();
}
