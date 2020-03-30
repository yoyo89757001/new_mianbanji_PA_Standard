package megvii.testfacepass.pa.beans;

public class IdsBean {
    private String effective;
    private String invalid;

    public IdsBean(String effective, String invalid) {
        this.effective = effective;
        this.invalid = invalid;
    }

    public String getEffective() {
        return effective;
    }

    public void setEffective(String effective) {
        this.effective = effective;
    }

    public String getInvalid() {
        return invalid;
    }

    public void setInvalid(String invalid) {
        this.invalid = invalid;
    }
}
