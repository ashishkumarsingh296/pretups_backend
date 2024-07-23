package com.btsl.pretups.product.businesslogic;

import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;

/**
 * 
 * @author akhilesh.mittal1
 *
 */
public class VomsProductsCache {

	private static Log _log = LogFactory.getLog(VomsProductsCache.class.getName());
	private static final String CLASS_NAME = "VomsProductsCache";

	private static HashMap<String, ProductVO> _productsMap = new HashMap<String, ProductVO>();

	/**
	 * ensures no instantiation
	 */
	private VomsProductsCache() {

	}

	public static void loadProductsAtStartup() throws BTSLBaseException {
		final String methodName = "loadProductsAtStartup";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		try {
			_productsMap = loadProducts();

		} catch (BTSLBaseException be) {
			_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
			_log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			_log.errorTrace(methodName, e);
			throw new BTSLBaseException(CLASS_NAME, methodName, "", e);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, PretupsI.EXITED);
			}
		}
	}

	/**
	 * To load the networks details
	 * 
	 * @return HashMap ProductCache
	 * @throws Exception
	 */
	private static HashMap<String, ProductVO> loadProducts() throws BTSLBaseException {
		final String methodName = "loadProducts";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		NetworkProductDAO productDao = new NetworkProductDAO();
		HashMap<String, ProductVO> map = null;
		try {
			map = productDao.loadProductsCache();
		} catch (BTSLBaseException be) {
			_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
			_log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			_log.errorTrace(methodName, e);
			throw new BTSLBaseException(CLASS_NAME, methodName, "", e);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, PretupsI.EXITED);
			}
		}
		return map;
	}

	/**
	 * to update the cache
	 * 
	 * void ProductCache
	 */
	public static void updateProduct() throws BTSLBaseException {
		final String methodName = "updateNetwork";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		try {

			HashMap<String, ProductVO> currentMap = loadProducts();
			_productsMap = currentMap;
		} catch (BTSLBaseException be) {
			_log.error(methodName, PretupsI.BTSLEXCEPTION + be.getMessage());
			_log.errorTrace(methodName, be);
			throw be;
		} catch (Exception e) {
			_log.error(methodName, PretupsI.EXCEPTION + e.getMessage());
			_log.errorTrace(methodName, e);
			throw new BTSLBaseException(CLASS_NAME, methodName, "", e);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, PretupsI.EXITED);
				_log.debug(methodName, PretupsI.EXITED + _productsMap.size());
			}
		}
	}

	/**
	 * to get the requested objcet from the cache
	 * 
	 * @param p_networkCode
	 * @return Object ProductCache
	 */
	public static ProductVO getObject(String productId) {

		ProductVO productVO = null;

		if (_log.isDebugEnabled()) {
			_log.debug("getObject()", "Entered  productId: " + productId);
		}

		productVO = (ProductVO) _productsMap.get(productId);
        
		if (_log.isDebugEnabled()) {
			_log.debug("getObject()", "Exited productVO: " + productVO);
		}

		return productVO;
	}

}
