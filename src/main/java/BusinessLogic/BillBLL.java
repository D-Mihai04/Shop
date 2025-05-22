package BusinessLogic;

import DataAccess.BillDAO;
import Model.Bill;

import java.util.List;

/**

 * BillBLL implements the logic for bill management.
 * It manages the creation of the bill and the showing of the bills
 */
public class BillBLL {
    private final BillDAO billDAO;


    public BillBLL() {
        billDAO = new BillDAO();
    }


    public List<Bill> getAllBills() {
        return billDAO.findAll();
    }

}
