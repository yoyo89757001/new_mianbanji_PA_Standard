package megvii.testfacepass.pa.beans;

public class WiﬁMsgBean {

    private String ssId; //Wi-Fi名称，必传
    private String pwd; //Wi-Fi密码，只允许数字、英文和英文字符；若Wi-Fi无密码，则传入空或者任意字符都可
    private Boolean isDHCPMod; //是否设置为动态IP，必传。若传入false，则ip，
    //    gateway，dns必传且不可为空；若传入true，则ip，gateway，dns无需传入，传入也不会生效
    private String ip; //IP地址
    private String gateway; //网关
    private String dns; //DNS服务器 8

    public String getSsId() {
        return ssId;
    }

    public void setSsId(String ssId) {
        this.ssId = ssId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Boolean getDHCPMod() {
        return isDHCPMod;
    }

    public void setDHCPMod(Boolean DHCPMod) {
        isDHCPMod = DHCPMod;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }
}
