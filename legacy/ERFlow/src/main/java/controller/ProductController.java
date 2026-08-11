package controller;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import model.ProductBean;
import service.implementation.ProductServiceImpl;

public class ProductController {
	private ProductServiceImpl productSvc = null;
	
	public ProductController() {
		productSvc = new ProductServiceImpl();
	}
	
	public boolean createProduct(HttpServletRequest request) {
		return productSvc.createProduct(request);
	}
	
	public boolean createProduct(ProductBean product) {
		return productSvc.createProduct(product);
	}
	
	public boolean deleteProduct(String productId) {
		return productSvc.deleteProduct(productId);
	}
	
	public boolean updateProduct(HttpServletRequest request) {
		return productSvc.updateProduct(request);
	}
	
	public boolean updateProduct(ProductBean product) {
		return productSvc.updateProduct(product);
	}
	
	public int getProductCount(String keyfield, String keyword, String type) {
		return productSvc.getProductCount(keyfield, keyword , type);
	}
		
	public ProductBean getProduct(String productId) {
		return productSvc.getProduct(productId);
	}
	
	public Vector<ProductBean> getProducts(String keyfield, String keyword , int start, int cnt, String type) {
		return productSvc.getProducts(keyfield, keyword, start, cnt, type);
	}
	public Vector<ProductBean> getAllProducts() {
		return productSvc.getAllProducts();
	}
}
