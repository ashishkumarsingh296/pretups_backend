package com.btsl.pretups.channel.profile.businesslogic;

/**
 * @(#)CommissionProfileDeatilsVO.java
 *                                     Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                     All Rights Reserved
 * 
 *                                     <description>
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     Author Date History
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     mohit.goel Aug 5, 2005 Initital Creation
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 * 
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommissionProfileDeatilsVO implements Serializable {
	private String _baseCommProfileDetailID;
	private String _commProfileDetailID;
    private String _commProfileProductsID;
    private long _startRange;
    private String _startRangeAsString = null;
    private long _endRange;
    private String _endRangeAsString = null;
    private String _commType;
    private double _commRate;
    private String _commRateAsString = "0";
    private String _tax1Type;
    private double _tax1Rate;
    private String _tax1RateAsString = "0";
    private String _tax2Type;
    private double _tax2Rate;
    private String _tax2RateAsString = "0";
    private String _tax3Type;
    private double _tax3Rate;
    private String _tax3RateAsString = "0";
	private String productCode;
    private List<OTFDetailsVO> otfDetails;
    private int otfDetailsSize;
    private boolean otfmodify;
    private int rowIndex;
    private String otfApplicableFromStr;
    private String otfApplicableToStr;
    private Date otfApplicableFrom;
    private Date otfApplicableTo;
    private String otfType;
    private String otfTimeSlab;
    private String otfValueAsString;
	private String otfRateAsString;
	private String otfTypePctOrAMt;
	private long otfValue;
	private double otfRate;
	private String baseCommProfileOTFDetailID;
	private String transactionType;
	private String paymentMode;
	
    public CommissionProfileDeatilsVO() {
    };

    public CommissionProfileDeatilsVO(
                    CommissionProfileDeatilsVO commissionProfileDeatilsVO) {
    	this._baseCommProfileDetailID = commissionProfileDeatilsVO._baseCommProfileDetailID;
        this._commProfileDetailID = commissionProfileDeatilsVO._commProfileDetailID;
        this._commProfileProductsID = commissionProfileDeatilsVO._commProfileProductsID;
        this._startRange = commissionProfileDeatilsVO._startRange;
        this._startRangeAsString = commissionProfileDeatilsVO._startRangeAsString;
        this._endRange = commissionProfileDeatilsVO._endRange;
        this._endRangeAsString = commissionProfileDeatilsVO._endRangeAsString;
        this._commType = commissionProfileDeatilsVO._commType;
        this._commRate = commissionProfileDeatilsVO._commRate;
        this._commRateAsString = commissionProfileDeatilsVO._commRateAsString;
        this._tax1Type = commissionProfileDeatilsVO._tax1Type;
        this._tax1Rate = commissionProfileDeatilsVO._tax1Rate;
        this._tax1RateAsString = commissionProfileDeatilsVO._tax1RateAsString;
        this._tax2Type = commissionProfileDeatilsVO._tax2Type;
        this._tax2Rate = commissionProfileDeatilsVO._tax2Rate;
        this._tax2RateAsString = commissionProfileDeatilsVO._tax2RateAsString;
        this._tax3Type = commissionProfileDeatilsVO._tax3Type;
        this._tax3Rate = commissionProfileDeatilsVO._tax3Rate;
        this._tax3RateAsString = commissionProfileDeatilsVO._tax3RateAsString;
		this.productCode = commissionProfileDeatilsVO.productCode;
		this.transactionType = commissionProfileDeatilsVO.transactionType;
		this.paymentMode = commissionProfileDeatilsVO.paymentMode;
        this.rowIndex = commissionProfileDeatilsVO.rowIndex;
        List<OTFDetailsVO> otf = new ArrayList<>(); 
        int number=Integer.parseInt(commissionProfileDeatilsVO.getOtfDetailsSize());
        for(int i=0;i<number;i++){
        	otf.add(new OTFDetailsVO(commissionProfileDeatilsVO.getOtfDetails().get(i)));
        }
        this.otfDetails=otf;
        this.otfTimeSlab=commissionProfileDeatilsVO.otfTimeSlab;
        this.otfApplicableFromStr=commissionProfileDeatilsVO.otfApplicableFromStr;
        this.otfApplicableToStr=commissionProfileDeatilsVO.otfApplicableToStr;
        this.baseCommProfileOTFDetailID=commissionProfileDeatilsVO.baseCommProfileOTFDetailID;
        this.otfType=commissionProfileDeatilsVO.otfType;
        this.origOtfApplicableToStr=commissionProfileDeatilsVO.origOtfApplicableToStr;
        }
    
    

    public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
     * @return Returns the rowIndex.
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * @param rowIndex
     *            The rowIndex to set.
     */
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String toString() {
        final StringBuffer sb = new StringBuffer("CommissionProfileDeatilsVO Data ");
        sb.append("_baseCommProfileDetailID=" + _baseCommProfileDetailID + ",");
        sb.append("_commProfileDetailID=" + _commProfileDetailID + ",");
        sb.append("_commProfileProductsID=" + _commProfileProductsID + ",");
        sb.append("productCode=" + productCode + ",");
        sb.append("transactionType=" + transactionType + ",");
        sb.append("paymentMode=" + paymentMode + ",");
        sb.append("_startRange=" + _startRange + ",");
        sb.append("_endRange=" + _endRange + ",");
        sb.append("_commType=" + _commType + ",");
        sb.append("_commRate=" + _commRate + ",");
        sb.append("_tax1Type=" + _tax1Type + ",");
        sb.append("_tax1Rate=" + _tax1Rate + ",");
        sb.append("_tax2Type=" + _tax2Type + ",");
        sb.append("_tax2Rate=" + _tax2Rate + ",");
        sb.append("_tax3Type=" + _tax3Type + ",");
        sb.append("_tax3Rate=" + _tax3Rate + ",");
        sb.append("otfApplicableFrom=" + otfApplicableFrom + ",");
        sb.append("otfApplicableTo=" + otfApplicableTo + ",");
        sb.append("otfTimeSlab=" + otfTimeSlab + ",");
        sb.append("otfType=" + otfType + ",");
        sb.append("otfValue=" + otfValue + ",");
        sb.append("otfRate=" + otfRate + ",");

        return sb.toString();
    }

    public double getCommRate() {
        return _commRate;
    }

    public void setCommRate(double commRate) {
        _commRate = commRate;
    }

    public String getCommRateAsString() {
        return _commRateAsString;
    }

    public void setCommRateAsString(String commRateAsString) {
        if (commRateAsString != null) {
            _commRateAsString = commRateAsString.trim();
        }
    }

    public String getCommType() {
        return _commType;
    }

    public void setCommType(String commType) {
        _commType = commType;
    }

    public long getEndRange() {
        return _endRange;
    }

    public void setEndRange(long endRange) {
        _endRange = endRange;
    }

    public String getEndRangeAsString() {
        return _endRangeAsString;
    }

    public void setEndRangeAsString(String endRangeAsString) {
        if (endRangeAsString != null) {
            _endRangeAsString = endRangeAsString.trim();
        }
    }

    public long getStartRange() {
        return _startRange;
    }

    public void setStartRange(long startRange) {
        _startRange = startRange;
    }

    public String getStartRangeAsString() {
        return _startRangeAsString;
    }

    public void setStartRangeAsString(String startRangeAsString) {
        if (startRangeAsString != null) {
            _startRangeAsString = startRangeAsString.trim();
        }
    }

    public double getTax1Rate() {
        return _tax1Rate;
    }

    public void setTax1Rate(double tax1Rate) {
        _tax1Rate = tax1Rate;
    }

    public String getTax1RateAsString() {
        return _tax1RateAsString;
    }

    public void setTax1RateAsString(String tax1RateAsString) {
        if (tax1RateAsString != null) {
            _tax1RateAsString = tax1RateAsString.trim();
        }
    }

    public String getTax1Type() {
        return _tax1Type;
    }

    public void setTax1Type(String tax1Type) {
        _tax1Type = tax1Type;
    }

    public double getTax2Rate() {
        return _tax2Rate;
    }

    public void setTax2Rate(double tax2Rate) {
        _tax2Rate = tax2Rate;
    }

    public String getTax2RateAsString() {
        return _tax2RateAsString;
    }

    public void setTax2RateAsString(String tax2RateAsString) {
        if (tax2RateAsString != null) {
            _tax2RateAsString = tax2RateAsString.trim();
        }
    }

    public String getTax2Type() {
        return _tax2Type;
    }

    public void setTax2Type(String tax2Type) {
        _tax2Type = tax2Type;
    }

    public double getTax3Rate() {
        return _tax3Rate;
    }

    public void setTax3Rate(double tax3Rate) {
        _tax3Rate = tax3Rate;
    }

    public String getTax3RateAsString() {
        return _tax3RateAsString;
    }

    public void setTax3RateAsString(String tax3RateAsString) {
        if (tax3RateAsString != null) {
            _tax3RateAsString = tax3RateAsString.trim();
        }
    }

    public String getTax3Type() {
        return _tax3Type;
    }

    public void setTax3Type(String tax3Type) {
        _tax3Type = tax3Type;
    }

    public String getCommProfileDetailID() {
        return _commProfileDetailID;
    }

    /**
     * @param commProfileDetailID
     *            The commProfileDetailID to set.
     */
    public void setCommProfileDetailID(String commProfileDetailID) {
        _commProfileDetailID = commProfileDetailID;
    }

    /**
     * @return Returns the commProfileProductsID.
     */
    public String getCommProfileProductsID() {
        return _commProfileProductsID;
    }

    /**
     * @param commProfileProductsID
     *            The commProfileProductsID to set.
     */
    public void setCommProfileProductsID(String commProfileProductsID) {
        _commProfileProductsID = commProfileProductsID;
    }
    
    public List<OTFDetailsVO> getOtfDetails() {
		return otfDetails;
	}
    
    public void setOtfDetails(List<OTFDetailsVO> otfDetails) {
		this.otfDetails = otfDetails;
	}
    
    public void setOtfDetailsSize(int otfDetailsSize) {
  		this.otfDetailsSize = otfDetailsSize;
  	}
    
    public String getOtfDetailsSize() {
		return Integer.toString(otfDetailsSize);
	}
    
	public boolean isOtfmodify() {
		return otfmodify;
	}
	public void setOtfmodify(boolean otfmodify) {
		this.otfmodify = otfmodify;
	}
    
	public String getOtfApplicableFromStr() {
		return otfApplicableFromStr;
	}

	public void setOtfApplicableFromStr(String otfApplicableFromStr) {
		this.otfApplicableFromStr = otfApplicableFromStr;
	}

	public String getOtfApplicableToStr() {
		return otfApplicableToStr;
	}

	public void setOtfApplicableToStr(String otfApplicableToStr) {
		this.otfApplicableToStr = otfApplicableToStr;
	}

	public Date getOtfApplicableFrom() {
		return otfApplicableFrom;
	}

	public void setOtfApplicableFrom(Date otfApplicableFrom) {
		this.otfApplicableFrom = otfApplicableFrom;
	}

	public Date getOtfApplicableTo() {
		return otfApplicableTo;
	}

	public void setOtfApplicableTo(Date otfApplicableTo) {
		this.otfApplicableTo = otfApplicableTo;
	}

	public String getOtfType() {
		return otfType;
	}

	public void setOtfType(String otfType) {
		this.otfType = otfType;
	}
	public String getOtfTimeSlab() {
		return otfTimeSlab;
	}

	public void setOtfTimeSlab(String otfTimeSlab) {
		this.otfTimeSlab = otfTimeSlab;
	}
    public String getOtfTypePctOrAMt() {
		return otfTypePctOrAMt;
	}
	public void setOtfTypePctOrAMt(String otfTypePctOrAMt) {
		this.otfTypePctOrAMt = otfTypePctOrAMt;
	}
public String getOtfValueAsString() {
	return otfValueAsString;
}

public void setOtfValueAsString(String otfValue) {
	this.otfValueAsString = otfValue;
}

public String getOtfRateAsString() {
	return otfRateAsString;
}

public void setOtfRateAsString(String otfRate) {
	this.otfRateAsString = otfRate;
}
public long getOtfValue() {
	return otfValue;
}

public void setOtfValue(long otfValue) {
	this.otfValue = otfValue;
}

public double getOtfRate() {
	return otfRate;
}

public void setOtfRate(Double otfRate) {
	this.otfRate = otfRate;
}

   public String getBaseCommProfileOTFDetailID() {
		return baseCommProfileOTFDetailID;
	}

	public void setBaseCommProfileOTFDetailID(String baseCommProfileOTFDetailID) {
		this.baseCommProfileOTFDetailID = baseCommProfileOTFDetailID;
	}
	
	/**
	    * @return Returns the baseCommProfileDetailID.
	    */
	   public String getBaseCommProfileDetailID() {
	       return _baseCommProfileDetailID;
	   }

	   /**
	    * @param baseCommProfileDetailID
	    *            The baseCommProfileDetailID to set.
	    */
	   public void setBaseCommProfileDetailID(String _baseCommProfileDetailID) {
		   this._baseCommProfileDetailID = _baseCommProfileDetailID;
	   }
	  
	   private String origOtfApplicableToStr;
	   public String getOrigOtfApplicableToStr() {
			return origOtfApplicableToStr;
		}

		public void setOrigOtfApplicableToStr(String origOtfApplicableToStr) {
			this.origOtfApplicableToStr = origOtfApplicableToStr;
		}
		
		public String getTransactionType() {
					return transactionType;
				}
			
				public void setTransactionType(String transactionType) {
					this.transactionType = transactionType;
				}
			
				public String getPaymentMode() {
					return paymentMode;
			}
			
				public void setPaymentMode(String paymentMode) {
					this.paymentMode = paymentMode;
				}
	
    
}
