package com.example.demo.serviceimpl;

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
	private IwareTransactionDetailDao detailDao;

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
	 * Create new products.
	 *
	 * @param productDto The ProductDTO object contains new product information.
	 * @return Returns a ResponseEntity object containing the data and the
	 *         corresponding status code. If the new product creation is successful,
	 *         the ResponseEntity object will contain the data that is the newly
	 *         created ProductDTO object, message "create Product successfully" and
	 *         status code HttpStatus.OK. If an error occurs, the ResponseEntity
	 *         object will contain the message "Something went wrong" and the status
	 *         code HttpStatus.BAD_REQUEST.
	 */
	@Override
	@Transactional
	public ResponseEntity<?> newProduct(ProductDTO productDto) {
		log.info("inside newProduct ", productDto);
		try {
			validateProductDTO(productDto);

			Product newProduct = new Product();
			BeanUtils.copyProperties(productDto, newProduct);

			if (productDto.getImages() != null && productDto.getImages().size() > 0) {
				List<String> imageUrls = fileUploadService.uploadFiles(productDto.getImages());
				newProduct.setImages(imageUrls);
			}

			String newAvatar = fileUploadService.uploadFile(productDto.getAvatar());

			newProduct.setAvatar(StringUtils.isBlank(newAvatar)
					? new FileMissingException("Product image cannot be blank", HttpStatus.BAD_REQUEST).toString()
					: newAvatar);
			int quantity = productDto.getQuantity();

			// Lấy những Shelf có số lượng Stock chưa đẩy 10
			List<Shelf> availableShelves = shelfDao.findAvailableShelves();

			if (availableShelves.isEmpty()) {
				throw new Exception("Can't find empty shelves or enough room to store products.");
			}

			List<Stock> listStock = new ArrayList<>();

			Product savedProduct = productDao.save(newProduct);

			boolean isShelfFound = false;
			for (Shelf shelf : availableShelves) {
				int availableSpace = getAvalibleCapacity(shelf);

				if (availableSpace > 0) {
					int spaceToUse = Math.min(availableSpace, quantity);
					for (int i = 1; i <= spaceToUse; i++) {
						Stock newStock = new Stock();
						newStock.setProduct(newProduct);
						newStock.setShelf(shelf);

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

			newProduct.setListStock(listStock);

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
//				detail.setQuantity(quantityStock);
				// WareTransactionDetail

				wareTransaction.getTransactionDetails().add(detail);
			}

			wareTransaction = wareTransactionDao.save(wareTransaction);

			ProductDTO savedProductDTO = new ProductDTO(newProduct);
			BeanUtils.copyProperties(savedProduct, savedProductDTO);

			return CafeUtils.getResponseData("create Product successfully", HttpStatus.OK, savedProductDTO);
		} catch (Exception e) {
			log.error("Error! An error occurred. Please try again later: " + e.getMessage(), e);
		}
		return CafeUtils.getResponseData(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST, null);

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
		}
		return Collections.emptyList();
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
