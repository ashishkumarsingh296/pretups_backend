/**
 * @(#)BulkPushSender.java
 *                         Copyright(c) 2003, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 *                         This class is used to send SMS in Bulk Push
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 * 
 *                         Gaurav Garg 31/12/2003 Initial Creation
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 */
package com.btsl.ota.bulkpush.businesslogic;

import java.sql.Connection;
import java.util.ArrayList;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.ServicesDAO;
import com.btsl.ota.services.businesslogic.ServicesVO;
import com.btsl.ota.services.businesslogic.SimProfileVO;

public class BulkPushSender {
    private static final Log logger = LogFactory.getLog(BulkPushSender.class.getName());

    public BulkPushSender() {
        super();
    }

    public static void main(String[] args) {

    }

    /**
     * This method is used to send SMS while bulk pushing
     * 
     * @param con
     *            Connection
     * @param listOfServiceVO
     *            ArrayList
     */
    public void sendSMSBulkPush(Connection con, ArrayList listOfServiceVO) {
        try {
            if (listOfServiceVO == null || listOfServiceVO.isEmpty()) {
                logger.debug("sendSMSBulkPush", " :: List is Empty");
                return;
            }
            logger.debug("sendSMSBulkPush", " Entering......................" + listOfServiceVO.size());
            int size = listOfServiceVO.size();
            // Testing begins here
            /*
             * listOfServiceVO = new ArrayList();
             * ServicesVO servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("9820267784");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("9818824241");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981881");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981882");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981883");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981884");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981885");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981886");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981887");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981888");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981889");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981811");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981821");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981831");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981841");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981851");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981861");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981871");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             * servicesVO=new ServicesVO();
             * servicesVO.setMsisdn("981891");
             * servicesVO.setTransactionId("030D3a18");
             * servicesVO.setKey(("973F59703A3B93371B763442EC394307"));
             * servicesVO.setByteCode((
             * "5001b25101af01000700000A23FF0E5472616E73666572204C696D6974FF11810E12B2BFAEBF9F209FCDB0BE82B8ABB00014017e55017e0182017A0202544C05820172060261612D292301020D1FFF0753452773204944FF14090F0938002009080020090609080020092109401102030A014104080122012D322301020D28FF0A50726F64756374204944FF1A092A094D0930094B09210941091F0020090609080020092109401102000A022D1a2300020D10FF06416D6F756E74FF0609300915092E11020207034104080123012D172304020D0DFF0350494EFF06092A093F09281102030604410408012103430405080121450703080121080101450702080121080103246c06080A34FF10205472616E73666572204C696D69743AFF200932093F092E093F091F0020091F094D0930093E09020938092B0930003A00200A10FF042053452DFF08090F09380907002D0801010A1AFF0A202C20416D6F756E742DFF0C002C002009300915092E002D0801032D072181020DFF0406241a07040A042054534508010408016008010108010308010208010526030801072D161300830602FF010B0D510102FF017FF6FF0103080150"
             * ));
             * listOfServiceVO.add(servicesVO);
             */
            // Testing ends here
            BulkPushThread t1 = null;
            BulkPushThread t2 = new BulkPushThread();
            BulkPushThread t3 = new BulkPushThread();
            BulkPushThread t4 = new BulkPushThread();
            BulkPushThread t5 = new BulkPushThread();
            String name1 = "t1";
            String name2 = "t2";
            String name3 = "t3";
            String name4 = "t4";
            String name5 = "t5";

            ServicesDAO servicesDAO = new ServicesDAO();
            ServicesVO sVO1 = (ServicesVO) listOfServiceVO.get(0);
            SimProfileVO simProfileVO = servicesDAO.loadSimProfileInfo(con, sVO1.getMsisdn());
            logger.info("sendSMSBulkPush", "::simProfileVO::------->" + simProfileVO.getMaxContSMSSize());
            // Here simProfile has to be fetched from database for any mobile
            // and set it same for all mobiles
            t1 = new BulkPushThread(sVO1, name1, simProfileVO);
            t1.start();
            t1.join();

            for (int i = 0; i < size;)
            // for(int i = 0;i< 19;) //for testing
            {
                if (!t1.isAlive()) {
                    try {
                        sVO1 = (ServicesVO) listOfServiceVO.get(++i);
                        t1 = new BulkPushThread(sVO1, name1, simProfileVO);
                        t1.start();
                    } catch (IndexOutOfBoundsException iobe) {
                        logger.error("sendSMSBulkPush", " :: Thread Name " + name1 + " Value i = " + i + " IndexOutOfBoundsException ::" + iobe);
                        break;
                    } catch (Exception e) {
                        logger.error("sendSMSBulkPush", " :: Thread Name " + name1 + " Value i = " + i + " Exception ::" + e);
                    }
                }
                if (!t2.isAlive()) {
                    try {
                    	ServicesVO sVO2 = (ServicesVO) listOfServiceVO.get(++i);
                        t2 = new BulkPushThread(sVO2, name2, simProfileVO);
                        t2.start();
                    } catch (IndexOutOfBoundsException iobe) {
                        logger.error("sendSMSBulkPush", " :: Thread Name " + name2 + " Value i = " + i + " IndexOutOfBoundsException ::" + iobe);
                        break;
                    } catch (Exception e) {
                        logger.error("sendSMSBulkPush", " :: Thread Name " + name2 + " Value i = " + i + " Exception ::" + e);
                    }
                }
                if (!t3.isAlive()) {
                    try {
                    	ServicesVO sVO3 = (ServicesVO) listOfServiceVO.get(++i);
                        t3 = new BulkPushThread(sVO3, name3, simProfileVO);
                        t3.start();
                    } catch (IndexOutOfBoundsException iobe) {
                        logger.error("sendSMSBulkPush", " :: Thread Name " + name3 + " Value i = " + i + " IndexOutOfBoundsException ::" + iobe);
                        break;
                    } catch (Exception e) {
                        logger.error("sendSMSBulkPush", " :: Thread Name " + name3 + " Value i = " + i + " Exception ::" + e);
                    }
                }
                if (!t4.isAlive()) {
                    try {
                        ServicesVO sVO4 = (ServicesVO) listOfServiceVO.get(++i);
                        t4 = new BulkPushThread(sVO4, name4, simProfileVO);
                        t4.start();
                    } catch (IndexOutOfBoundsException iobe) {
                        logger.error("sendSMSBulkPush", " :: Thread Name " + name4 + " Value i = " + i + " IndexOutOfBoundsException ::" + iobe);
                        break;
                    } catch (Exception e) {
                        logger.error("sendSMSBulkPush", " :: Thread Name " + name4 + " Value i = " + i + " Exception ::" + e);
                    }
                }
                if (!t5.isAlive()) {
                    try {
                        ServicesVO sVO5 = (ServicesVO) listOfServiceVO.get(++i);
                        t5 = new BulkPushThread(sVO5, name5, simProfileVO);
                        t5.start();
                    } catch (IndexOutOfBoundsException iobe) {
                        logger.error("sendSMSBulkPush", " :: Thread Name " + name5 + " Value i = " + i + " IndexOutOfBoundsException ::" + iobe);
                        break;
                    } catch (Exception e) {
                        logger.error("sendSMSBulkPush", " :: Thread Name " + name5 + " Value i = " + i + " Exception ::" + e);
                    }
                }
            }
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            t5.join();
        }

        catch (Exception e) {
            logger.error("sendSMSBulkPush", " :: Error :: " + e);
        }

        finally {
            logger.debug("sendSMSBulkPush", " Exiting.....................");
        }

    }
}