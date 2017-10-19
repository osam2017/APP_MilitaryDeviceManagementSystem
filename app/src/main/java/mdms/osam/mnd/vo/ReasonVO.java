package mdms.osam.mnd.vo;

import java.util.Date;

/**
 * Created by Administrator on 2017-10-20.
 */

public class ReasonVO {

    private String sn;
    private String reason;
    private String used_func;
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ReasonVO() {
    }

    public ReasonVO(String sn, String reason, String used_func, Date date) {
        this.sn = sn;
        this.reason = reason;
        this.used_func = used_func;
        this.date = date;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUsed_func() {
        return used_func;
    }

    public void setUsed_func(String used_func) {
        this.used_func = used_func;
    }

    @Override
    public String toString() {
        return "ReasonVO{" +
                "sn='" + sn + '\'' +
                ", reason='" + reason + '\'' +
                ", used_func='" + used_func + '\'' +
                ", date=" + date +
                '}';
    }
}
