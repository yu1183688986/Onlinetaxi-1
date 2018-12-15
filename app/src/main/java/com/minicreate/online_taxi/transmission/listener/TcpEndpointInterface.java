package com.minicreate.online_taxi.transmission.listener;

public interface TcpEndpointInterface {
	public void onNetConnSuccess();
	public void onNetBroken();
	public void onEndpointStop();
	public void onConnServerFailed();
}
