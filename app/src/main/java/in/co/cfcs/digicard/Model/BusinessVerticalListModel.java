package in.co.cfcs.digicard.Model;

public class BusinessVerticalListModel {

    public String businessVerticalID;
    public String businessVertical;


    public BusinessVerticalListModel(String BusinessVerticalID, String BusinessVertical) {
        this.businessVerticalID = BusinessVerticalID;
        this.businessVertical = BusinessVertical;
    }

    public String getBusinessVerticalID() {
        return businessVerticalID;
    }

    public String getBusinessVertical() {
        return businessVertical;
    }

    @Override
    public String toString() {
        return businessVertical;
    }
}
