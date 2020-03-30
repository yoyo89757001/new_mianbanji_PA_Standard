package megvii.testfacepass.pa.beans;

public class ConﬁgsBean {
//    {
//        "faceConfidenceThreshold":0.85, // float ，是否是人脸的检测阈值
//            "yawThreshold": 40,  //int  人脸检测角度默认40，
//            "rollThreshold":40,  //int  人脸检测角度默认40，
//            "pitchThreshold":40   //int  人脸检测角度默认40，
//        "compareThreshold": 0.52, //int 人脸识别阈值，超过0.52表示是在库里面的人员
//            2"companyName": “XX公司”, // String，公司名称
//        3"isStranger": 1, // Int，1,显示陌生人 2，不显示陌生人
//            4"saveIdentifyTime": 15, // Int，陌生人判断，识别多少次判断为陌生人
//            5"identifyScores": 0.52, //float 人脸识别阈值，超过0.52表示是在库里面的人员
//            "isOpenDoor": 1, // Int 1:开启本地密码开门模式,2:不开启本地模式
//            6"recRank": 1, // int，1:不开启活体识别; 2: 开启双目活体识别; 默认1
//            23	}

    private float faceConfidenceThreshold;
    private int yawThreshold;
    private int rollThreshold;
    private int pitchThreshold;
    private float compareThreshold;
    private String companyName;
    private int isStranger;
    private int saveIdentifyTime;
    private float identifyScores;
    private int recRank;
    private int isOpenDoor;
    private int relayInterval;
    private int configModel;

    public int getConfigModel() {
        return configModel;
    }

    public void setConfigModel(int configModel) {
        this.configModel = configModel;
    }

    public int getRelayInterval() {
        return relayInterval;
    }

    public void setRelayInterval(int relayInterval) {
        this.relayInterval = relayInterval;
    }

    public int getIsOpenDoor() {
        return isOpenDoor;
    }

    public void setIsOpenDoor(int isOpenDoor) {
        this.isOpenDoor = isOpenDoor;
    }

    public float getFaceConfidenceThreshold() {
        return faceConfidenceThreshold;
    }

    public void setFaceConfidenceThreshold(float faceConfidenceThreshold) {
        this.faceConfidenceThreshold = faceConfidenceThreshold;
    }

    public int getYawThreshold() {
        return yawThreshold;
    }

    public void setYawThreshold(int yawThreshold) {
        this.yawThreshold = yawThreshold;
    }

    public int getRollThreshold() {
        return rollThreshold;
    }

    public void setRollThreshold(int rollThreshold) {
        this.rollThreshold = rollThreshold;
    }

    public int getPitchThreshold() {
        return pitchThreshold;
    }

    public void setPitchThreshold(int pitchThreshold) {
        this.pitchThreshold = pitchThreshold;
    }

    public float getCompareThreshold() {
        return compareThreshold;
    }

    public void setCompareThreshold(float compareThreshold) {
        this.compareThreshold = compareThreshold;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getIsStranger() {
        return isStranger;
    }

    public void setIsStranger(int isStranger) {
        this.isStranger = isStranger;
    }

    public int getSaveIdentifyTime() {
        return saveIdentifyTime;
    }

    public void setSaveIdentifyTime(int saveIdentifyTime) {
        this.saveIdentifyTime = saveIdentifyTime;
    }

    public float getIdentifyScores() {
        return identifyScores;
    }

    public void setIdentifyScores(float identifyScores) {
        this.identifyScores = identifyScores;
    }

    public int getRecRank() {
        return recRank;
    }

    public void setRecRank(int recRank) {
        this.recRank = recRank;
    }
}
