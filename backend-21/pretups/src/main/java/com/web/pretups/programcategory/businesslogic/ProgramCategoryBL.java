package com.web.pretups.programcategory.businesslogic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.web.pretups.programcategory.web.ProgramCategoryForm;

@Service
@Lazy
@Scope("session")
public class ProgramCategoryBL {
    @Autowired
    private ProgramCategoryDAO programCategoryDAO;
    private static final Log LOG = LogFactory.getLog(ProgramCategoryBL.class.getName());

    public void loadProgramCategory(ProgramCategoryForm progCategoryForm) throws BTSLBaseException {
        final String methodName = "loadProgramCategory";
        List<ListValueVO> programList;
        try {
            programList = new ArrayList<ListValueVO>();
            programList = LookupsCache.loadLookupDropDown(PretupsI.PROG_MGMT_TYPE, true);
            progCategoryForm.setProgramList(programList);
        } catch (Exception e) {
            LOG.error(methodName, "Exception: " + e.getMessage());
            LOG.errorTrace(methodName, e);
        }
    }

    public void loadProgramEarningType(ProgramCategoryForm progCategoryForm) throws BTSLBaseException {
        final String methodName = "loadProgramEarningType";
        List<ListValueVO> programEarningTypeList;
        try {
            programEarningTypeList = new ArrayList<ListValueVO>();
            programEarningTypeList = LookupsCache.loadLookupDropDown(PretupsI.LMS_PROMOTION_TYPE, true);
            progCategoryForm.setProgramEarningTypeList(programEarningTypeList);
        } catch (Exception e) {
            LOG.error(methodName, "Exception: " + e.getMessage());
            LOG.errorTrace(methodName, e);
        }
    }

    public void loadProgramRewardType(ProgramCategoryForm progCategoryForm) throws BTSLBaseException {
        final String methodName = "loadProgramRewardType";
        List<ListValueVO> rewardTypeList;
        try {
            rewardTypeList = new ArrayList<ListValueVO>();
            rewardTypeList = LookupsCache.loadLookupDropDown(PretupsI.REWARD_TYPE, true);
            progCategoryForm.setRewardTypeList(rewardTypeList);
        } catch (Exception e) {
            LOG.error(methodName, "Exception: " + e.getMessage());
            LOG.errorTrace(methodName, e);
        }

    }

    public void loadRedempFrequencyType(ProgramCategoryForm progCategoryForm) throws BTSLBaseException {
        final String methodName = "loadRedempFrequencyType";

        List<ListValueVO> redempFreqList;
        try {
            redempFreqList = new ArrayList<ListValueVO>();
            redempFreqList = LookupsCache.loadLookupDropDown(PretupsI.REDEMP_FREQ_TYPE, true);
            progCategoryForm.setRedempFreqList(redempFreqList);
        } catch (Exception e) {
            LOG.error(methodName, "Exception: " + e.getMessage());
            LOG.errorTrace(methodName, e);
        }

    }

    public void loadRedempWalletType(ProgramCategoryForm progCategoryForm) throws BTSLBaseException {
        final String methodName = "loadRedempWalletType";
        ArrayList<String> redempWalletList = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            redempWalletList = programCategoryDAO.loadWalletList(con);
            progCategoryForm.setRedempWalletList(redempWalletList);
        } catch (Exception e) {
            LOG.error(methodName, "Exception: " + e.getMessage());
            LOG.errorTrace(methodName, e);
        } finally {
            if (mcomCon != null) {
                mcomCon.close("ProgramCategoryBL#" + methodName);
                mcomCon = null;
            }

        }
    }

    public void saveProgramRules(ProgramCategoryVO programCategoryVO) throws BTSLBaseException {
        final String methodName = "saveProgramRules";
        Connection con = null;
        MComConnectionI mcomCon = null;
        int insertCount = 0;
        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            insertCount = programCategoryDAO.saveProgramRules(con, programCategoryVO);
            if (insertCount > 0) {
                throw new BTSLBaseException("ProgramCategoryBL", methodName, "program.category.success.message");
            }
        } catch (SQLException sqle) {
            LOG.error(methodName, "Exception: " + sqle.getMessage());
            LOG.errorTrace(methodName, sqle);
        } finally {
            if (mcomCon != null) {
                mcomCon.close("ProgramCategoryBL#saveProgramRules");
                mcomCon = null;
            }

        }
    }

    public void updateProgramRules(ProgramCategoryVO programCategoryVO) throws BTSLBaseException {
        final String methodName = "updateProgramRules";
        int updateCount = 0;
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            updateCount = programCategoryDAO.updateProgramRules(con, programCategoryVO);
            if (updateCount > 0) {
                throw new BTSLBaseException("ProgramCategoryBL", methodName, "program.category.success.message");
            }
        }catch (Exception e) {
            LOG.error(methodName, "Exception: " + e.getMessage());
            LOG.errorTrace(methodName, e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
        }
    }

    public ArrayList<ProgramCategoryVO> checkIfRuleSetExists(ProgramCategoryVO programCategoryVO)
            throws BTSLBaseException {
        final String methodName = "checkIfRuleSetExists";
        ArrayList<ProgramCategoryVO> List = new ArrayList<ProgramCategoryVO>();
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            List = programCategoryDAO.checkIfRuleSetExists(con, programCategoryVO);
            if (List.size() > 0) {
                programCategoryVO.setRuleListExist("Y");
            }
        } catch (Exception e) {
            LOG.error(methodName, "Exception: " + e.getMessage());
            LOG.errorTrace(methodName, e);
        } finally {
            if (mcomCon != null) {
                mcomCon.close("ProgramCategoryBL#checkIfRuleSetExists");
                mcomCon = null;
            }
        }
        return List;
    }

    public void businessValidation(ProgramCategoryForm progCategoryForm) throws BTSLBaseException {
        final String methodName = "businessValidation";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        if (!(Integer.valueOf(progCategoryForm.getMaxPeriod()) > Integer.valueOf(progCategoryForm.getMinPeriod()))) {
            throw new BTSLBaseException("ProgramCategoryBL", "businessValidation", "program.category.maxPeriod.greater");
        }
    }
}
