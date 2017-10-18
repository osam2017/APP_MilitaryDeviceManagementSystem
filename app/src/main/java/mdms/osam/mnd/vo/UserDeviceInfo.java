package mdms.osam.mnd.vo;

import java.util.Date;

/**
 * Created by Administrator on 2017-10-17.
 */

public class UserDeviceInfo {
    //UserInfo
    private String sn;
    private String name;
    private String mil_class;
    private String unit_name;
    private String rank;
    private String pushkey;
    //DeviceInfo
    private String manft;
    private String model;
    private String device_id;
    private String os;
    private String os_version;
    private Date reg_date;


    public String getSn() {
        return sn;
    }
    public UserDeviceInfo(){
        super();
    }

    public UserDeviceInfo(String sn, String name, String mil_class, String unit_name, String rank, String manft, String model, String device_id, String os, String os_version) {
        this.sn = sn;
        this.name = name;
        this.mil_class = mil_class;
        this.unit_name = unit_name;
        this.rank = rank;
        this.manft = manft;
        this.model = model;
        this.device_id = device_id;
        this.os = os;
        this.os_version = os_version;

    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMil_class() {
        return mil_class;
    }

    public void setMil_class(String mil_class) {
        this.mil_class = mil_class;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPushkey() {
        return pushkey;
    }

    public void setPushkey(String pushkey) {
        this.pushkey = pushkey;
    }

    public String getManft() {
        return manft;
    }

    public void setManft(String manft) {
        this.manft = manft;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOs_version() {
        return os_version;
    }

    public void setOs_version(String os_version) {
        this.os_version = os_version;
    }

    public Date getReg_date() {
        return reg_date;
    }

    public void setReg_date(Date reg_date) {
        this.reg_date = reg_date;
    }



    @Override
    public String toString() {
        return "UserDeviceInfo{" +
                "sn='" + sn + '\'' +
                ", name='" + name + '\'' +
                ", mil_class='" + mil_class + '\'' +
                ", unit_name='" + unit_name + '\'' +
                ", rank='" + rank + '\'' +
                ", pushkey='" + pushkey + '\'' +
                ", manft='" + manft + '\'' +
                ", model='" + model + '\'' +
                ", device_id='" + device_id + '\'' +
                ", os='" + os + '\'' +
                ", os_version='" + os_version + '\'' +
                ", reg_date=" + reg_date +
                '}';
    }

}
