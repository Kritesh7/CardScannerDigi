package in.co.cfcs.digicard.Model;

public class ManagmentTypeListModel {
    public String managemnetTypeId;
    public String mangmentType;

    public ManagmentTypeListModel(String managemnetTypeId, String mangmentType) {
        this.managemnetTypeId = managemnetTypeId;
        this.mangmentType = mangmentType;
    }

    public String getManagemnetTypeId() {
        return managemnetTypeId;
    }

    public String getMangmentType() {
        return mangmentType;
    }

    @Override
    public String toString() {
        return mangmentType;
    }
}
