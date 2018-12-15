package com.minicreate.online_taxi.transmission.protocol.beidou;

/**
 * 终端注册，消息ID是0x0100
 */
public class SignInBeidou extends BaseParamBeidou {
    private byte[] provinceID;//省域ID
    private byte[] cityID;//市域ID
    private byte[] manufacturerID;//制造商ID
    private byte[] terminalType;//终端型号，20个字节
    private byte[] terminalID;//终端ID，7个字节
    private byte numberPlateColor;//车牌颜色，1个字节
    private String vehicleLogo;//车辆标识

    public void setProvinceID(String provinceID) {

    }

    public void setCityID(String cityID) {

    }

    public void setManufacturerID(String manufacturerID) {

    }

    public void setTerminalType(String terminalType) {
    }

    public void setTerminalID(String terminalID) {

    }

    public void setNumberPlateColor(String numberPlateColor) {

    }

    public void setVehicleLogo(String vehicleLogo) {

    }

    /**
     * 终端注册的消息ID是0x0100
     */
    public SignInBeidou() {
        super(0x0100);
    }

    @Override
    protected void packContent() {
        //填充数据，消息体总长：38字节

        //首先判断有没有车辆标识
        if (vehicleLogo == null) {
            content = new byte[37];
        } else {
            content = new byte[38];
        }
        //省域ID，2字节
        int index = 0;
        content[index] = content[index + 1] = 0;
        index += 2;
        //市县域ID，2字节
        content[index] = content[index + 1] = 0;
        index += 2;
        //制造商ID，5个字节
        content[index++] = 0;
        content[index++] = 0;
        content[index++] = 0;
        content[index++] = 0;
        content[index++] = 0;
        //终端型号，20个字节
        for (int i = 0; i < 20; i++) {
            content[index + i] = 0;
        }
        index += 20;
        //终端ID，7个字节
        byte[] temp = new byte[]{7, 9, 3, 2, 1, 0, 0};
        System.arraycopy(temp, 0, content, index, temp.length);
        index += 7;
        //车牌颜色，1字节
        content[index] = 1;
        index++;
        //车辆标识，如果没有就置空
    }

    @Override
    public void parseFromProtocol(byte[] src) {
    }
}
