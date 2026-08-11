package service;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.ProductBean;

public interface ProductService extends Service {
	public boolean createProduct(HttpServletRequest request);
	public boolean createProduct(ProductBean product);
	public boolean deleteProduct(String productId);
	public boolean updateProduct(HttpServletRequest request);
	public boolean updateProduct(ProductBean product);
	
	public ProductBean getProduct(String productId);
	
	public Vector<ProductBean> getProducts(String keyfield, String keyword , int start, int cnt, String type);
	
	public int getProductCount(String keyfield, String keyword, String type);
	
	public Vector<ProductBean> getAllProducts();
}
