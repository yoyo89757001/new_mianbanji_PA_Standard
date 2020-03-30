package megvii.testfacepass.pa.beans;

public class ResBean {
    private int result;
    private boolean success;
    private String msg;
    private Object data;

    public ResBean(int result, boolean success,Object data, String msg) {
        this.result = result;
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    public ResBean(int result, boolean success, Object data) {
        this.result = result;
        this.success = success;
        this.data = data;
    }



    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
