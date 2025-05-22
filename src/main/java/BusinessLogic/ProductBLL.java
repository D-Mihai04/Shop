package BusinessLogic;

import DataAccess.ProductDAO;
import Model.Product;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProductBLL implements the logic for product management
 * It manages the creating, checking the stock and bill generation
 */
public class ProductBLL {
    private final ProductDAO productDAO;

    public ProductBLL() {
        productDAO = new ProductDAO();
    }

    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    public Product findProductById(int id) {
        return productDAO.findById(id);
    }

    public Product insertProduct(Product product) {
        return productDAO.insert(product);
    }

    public Product updateProduct(Product product) {
        return productDAO.update(product);
    }

    public void deleteProduct(int id) {
        productDAO.delete(id);
    }
}
