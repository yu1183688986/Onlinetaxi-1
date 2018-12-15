package com.minicreate.online_taxi.transmission.protocol.dianchuang;

public class UnvarnishedTransmissionDianchuang extends BaseParamDianchuang {
    private byte[] data;

    public void setData(byte[] data) {
        this.data = data;
    }

    public UnvarnishedTransmissionDianchuang(int command) {
        super(command);
    }

    @Override
    public byte[] parseToProtocol() {
        return data;
    }

    @Override
    public int getToken() {
        return data[7];
    }
}
