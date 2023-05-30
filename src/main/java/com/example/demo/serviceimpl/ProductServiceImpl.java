package com.example.demo.serviceimpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.common.FileUploadService;
import com.example.demo.constants.CafeConstants;
import com.example.demo.dao.IProductDao;
import com.example.demo.dao.IShelfDao;
import com.example.demo.dao.IStockDao;
import com.example.demo.dao.IUSerDao;
import com.example.demo.dao.IWareTransactionDao;
import com.example.demo.dao.IwareTransactionDetailDao;
import com.example.demo.dao.impl.ProductRepositoryImpl;
import com.example.demo.dto.request.ProductDTO;
import com.example.demo.dto.request.ProductSearchCriteriaDTO;
import com.example.demo.entity.Product;
import com.example.demo.entity.Shelf;
import com.example.demo.entity.Stock;
import com.example.demo.entity.User;
import com.example.demo.entity.WareTransaction;
import com.example.demo.entity.WareTransactionDetail;
import com.example.demo.enums.ProductStatus;
import com.example.demo.enums.WareTransactionType;
import com.example.demo.exception.FileMissingException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.jwt.JWTFilter;
import com.example.demo.service.IProductService;
import com.example.demo.utils.CafeUtils;
import com.example.demo.utils.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class ProductServiceImpl implements IProductService {

	@Autowired
	private IProductDao productDao;

	@Autowired
	private IStockDao stockDao;

	@Autowired
	private ProductRepositoryImpl productDaoImpl;

	@Autowired
	private FileUploadService fileUploadService;

	@Autowired
	private IShelfDao shelfDao;

	@Autowired
	private IWareTransactionDao wareTransactionDao;

	@Autowired
	private IUSerDao userDao;

	@Autowired
	private JWTFilter jwtFilter;

	@Autowired
	DateUtils dateUtils;

	@Override
	public ProductDTO findById(Long id) {
		Product findProduct = productDao.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
		return new ProductDTO(findProduct);
	}

	/**
	 * Find all products in the database.
	 *
	 * @return Returns a list of ProductDTO objects, containing information for all
	 *         products in the database.
	 */
	@Override
	public List<ProductDTO> findAll() {
		return productDao.findAll().stream().map(product -> new ProductDTO(product)).collect(Collectors.toList());
	}

	/**
	 * Creates a new product based on the provided product DTO.
	 *
	 * @param productDto The product DTO containing the information of the product.
	 * @return The response entity with the result of the operation.
	 */
	@Override
	@Transactional
	public ResponseEntity<?> newProduct(ProductDTO productDto) {
		log.info("inside newProduct ", productDto);
		try {
			validateProductDTO(productDto);

			Product newProduct = createNewProduct(productDto);

			int quantity = productDto.getQuantity();

			List<Shelf> availableShelves = shelfDao.findAvailableShelves();

			validateAvailableShelves(availableShelves);

			List<Stock> listStock = allocateStockToShelves(newProduct, availableShelves, quantity);

			newProduct.setListStock(listStock);

			createWareTransaction(newProduct, listStock);

			com.example.demo.dto.response.ProductDTO savedProductDTO = convertToProductDTO(newProduct);

			return CafeUtils.getResponseData("create Product successfully", HttpStatus.OK, savedProductDTO);
		} catch (Exception e) {
			log.error("Error! An error occurred. Please try again later: " + e.getMessage(), e);

		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CafeConstants.SOMETHING_WENT_WRONG);

	}

	/**
	 * Creates a new product based on the provided product DTO.
	 *
	 * @param productDto The product DTO containing the information of the product.
	 * @return The newly created product.
	 * @throws IOException If an error occurs during file upload.
	 */
	private Product createNewProduct(ProductDTO productDto) throws IOException {
		Product newProduct = new Product();
		BeanUtils.copyProperties(productDto, newProduct);
		newProduct.setProductStatus(ProductStatus.IN_STOCK);
		newProduct.setRerceiptDate(dateUtils.convertDateToLocalDateTime(new Date()));
		if (productDto.getImages() != null && productDto.getImages().size() > 0) {
			List<String> imageUrls = fileUploadService.uploadFiles(productDto.getImages());
			newProduct.setImages(imageUrls);
		}

		String newAvatar = fileUploadService.uploadFile(productDto.getAvatar());

		newProduct.setAvatar(StringUtils.isBlank(newAvatar)
				? new FileMissingException("Product image cannot be blank", HttpStatus.BAD_REQUEST).toString()
				: newAvatar);

		return productDao.save(newProduct);

	}

	/**
	 * Validates the availability of shelves for storing products.
	 *
	 * @param shelves The list of shelves to validate.
	 * @throws Exception If no empty shelves or enough room is found.
	 */
	private void validateAvailableShelves(List<Shelf> shelves) throws Exception {
		if (shelves.isEmpty()) {
			throw new Exception("Can't find empty shelves or enough room to store products.");
		}
	}

	/**
	 * Allocates stocks to available shelves for the given product.
	 *
	 * @param newProduct The newly created product.
	 * @param shelves    The available shelves.
	 * @param quantity   The quantity of stocks to allocate.
	 * @return The list of allocated stocks.
	 * @throws Exception If there is not enough space to store all products.
	 */
	private List<Stock> allocateStockToShelves(Product newProduct, List<Shelf> shelves, int quantity) throws Exception {

		List<Stock> listStock = new ArrayList<>();
		boolean isShelfFound = false;
		for (Shelf shelf : shelves) {
			int availableSpace = getAvalibleCapacity(shelf);

			if (availableSpace > 0) {
				int spaceToUse = Math.min(availableSpace, quantity);
				for (int i = 1; i <= spaceToUse; i++) {
					Stock newStock = createNewStock(newProduct, shelf);

					stockDao.save(newStock);
					listStock.add(newStock);

					quantity--;
				}

				isShelfFound = true;
				if (quantity == 0) {
					break;
				}
			}
		}

		log.info("Size List", listStock.size());

		if (!isShelfFound || quantity > 0) {
			throw new Exception("Can't find enough space to store all products.");
		}

		return listStock;
	}

	/**
	 * Creates a new stock for the given product and shelf.
	 *
	 * @param newProduct The newly created product.
	 * @param shelf      The shelf to allocate the stock.
	 * @return The newly created stock.
	 */
	public Stock createNewStock(Product newProduct, Shelf shelf) {
		Stock newStock = new Stock();
		newStock.setLastUpdateDate(dateUtils.convertDateToLocalDateTime(new Date()));
		newStock.setProduct(newProduct);
		newStock.setShelf(shelf);

		return newStock;
	}

	/**
	 * Creates a ware transaction for the given product and list of stocks.
	 *
	 * @param product   The product involved in the transaction.
	 * @param listStock The list of stocks to include in the transaction.
	 */
	private void createWareTransaction(Product product, List<Stock> listStock) {
		String userName = jwtFilter.getCurrentUser();
		User createTransactionUser = getCuurentUser(userName);

		WareTransaction wareTransaction = new WareTransaction();
		wareTransaction.setUser(createTransactionUser);
		wareTransaction.setTransactionDate(dateUtils.convertDateToLocalDateTime(new Date()));
		wareTransaction.setTransactionType(WareTransactionType.IMPORT);
		wareTransaction.setTransactionDetails(new ArrayList<>());

		for (Stock stock : listStock) {
			WareTransactionDetail detail = new WareTransactionDetail();
			detail.setWareTransaction(wareTransaction);
			detail.setStock(stock);
//			detail.setQuantity(quantityStock);
			// WareTransactionDetail

			wareTransaction.getTransactionDetails().add(detail);
		}

		wareTransaction = wareTransactionDao.save(wareTransaction);
	}

	/**
	 * Converts a product entity to a product DTO.
	 *
	 * @param product The product entity to convert.
	 * @return The product DTO.
	 */
	private com.example.demo.dto.response.ProductDTO convertToProductDTO(Product product) {
		com.example.demo.dto.response.ProductDTO productDTO = new com.example.demo.dto.response.ProductDTO(product);
		BeanUtils.copyProperties(product, productDTO);
		return productDTO;
	}

	/** Returns the empty space in List<Stock> contained in the Shelf **/
	public int getAvalibleCapacity(Shelf shelf) {
		int usedSpace = 0;

		List<Stock> listStockUsed = shelf.getListStock().stream().filter(Objects::nonNull).collect(Collectors.toList());
		usedSpace = listStockUsed.size();

		return 10 - usedSpace;
	}

	@Override
	public void deleteById(Long id) {
		productDao.deleteById(id);

	}

	public User getCuurentUser(String userName) {
		return userDao.findByUserName(userName).orElse(null);
	}

	@Override
	public List<ProductDTO> searchProduct(ProductSearchCriteriaDTO criteria) {
		log.info("inside searchProduct ", criteria);
		try {
			List<Product> products = productDaoImpl.search(criteria);
			List<ProductDTO> productsDTO = products.stream().map(product -> new ProductDTO(product))
					.collect(Collectors.toList());

			return productsDTO;
		} catch (Exception e) {
			log.error("Error! An error occurred. Please try again later: " + e.getMessage(), e);
			return Collections.emptyList();
		}

	}

	private void validateProductDTO(ProductDTO productDTO) {
		log.info("inside validate product", productDTO);

		if (productDTO.getName() == null || productDTO.getName().isBlank()) {
			throw new IllegalArgumentException("Product name cannot be null or empty");
		} else if (productDTO.getPrice() == null || productDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Product price must be greater than 0");
		} else if (productDTO.getQuantity() == 0
				|| new BigDecimal(Integer.toString(productDTO.getQuantity())).compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("quanttity must be greater than 0");
		} else if (productDTO.getMeasurementUnit().equals("KG") || productDTO.getMeasurementUnit().equals("TONS")) {
			throw new IllegalArgumentException("Measure unit must be KG or Tons");
		} else if (productDTO.getWeight() == null || productDTO.getWeight().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Product price must be greater than 0");
		}
	}

}
