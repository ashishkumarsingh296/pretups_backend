DROP FUNCTION USERCLOSINGBALANCE;

CREATE OR REPLACE FUNCTION UserClosingBalance
--This function is used for generate report of closing balance
--(p_userId  VARCHAR2) RETURN VARCHAR2
-- p_startDate date
-- p_endDate date
(p_userId  VARCHAR2,p_startDate DATE,p_endDate DATE,p_startAmt NUMBER,p_endAmt NUMBER) RETURN VARCHAR2
IS
p_userCloBalDateWise VARCHAR2(32000) DEFAULT '';
balDate DATE;
balance NUMBER(10) DEFAULT 0;
productCode VARCHAR(10);
CURSOR c_userCloBal(p_userId VARCHAR2,p_startDate DATE,p_endDate DATE,p_startAmt NUMBER,p_endAmt NUMBER) IS
        SELECT  UDB.user_id user_id,UDB.balance_date balance_date,UDB.balance balance,UDB.PRODUCT_CODE
                        FROM    USER_DAILY_BALANCES UDB
                        WHERE UDB.user_id=p_userId
                        AND UDB.balance_date >=p_startDate
                        AND UDB.balance_date <=p_endDate
                        AND UDB.balance >=p_startAmt
                        AND UDB.balance <=p_endAmt ORDER BY balance_date ASC;
            BEGIN
        FOR bal IN c_userCloBal(p_userId,p_startDate,p_endDate,p_startAmt,p_endAmt)
        LOOP
                            balDate:=bal.balance_date;
                            balance:=bal.balance;
                            productCode:=bal.PRODUCT_CODE;
                            p_userCloBalDateWise:=p_userCloBalDateWise||productCode||':'||balDate||':'||balance||',';
        END LOOP;
                        IF LENGTH(p_userCloBalDateWise) > 0 THEN
         p_userCloBalDateWise:=SUBSTR(p_userCloBalDateWise,0,LENGTH(p_userCloBalDateWise)-1);
        END IF;
            RETURN p_userCloBalDateWise;
END;
/


DROP FUNCTION GETUSERROLESTYPE;

CREATE OR REPLACE FUNCTION Getuserrolestype
(p_userId  VARCHAR2,p_nameorcode VARCHAR2) RETURN VARCHAR2
IS
p_userRolesType VARCHAR2(20);
roleTypeNameOrCode VARCHAR2(5);
counter NUMBER:=0;
CURSOR c_userRolesType(p_userId VARCHAR2) IS
       SELECT (CASE r.group_role WHEN 'Y' THEN 'group role' WHEN 'N' THEN 'system role' END) roleType
       FROM ROLES r,USER_ROLES ur,USERS u
       WHERE r.ROLE_CODE=ur.ROLE_CODE
       AND ur.USER_ID=p_userId;
       --AND u.USER_ID=p_userId;
CURSOR c_userFixRolesType(p_userId VARCHAR2) IS
       SELECT ur.user_id,r.role_name,r.GROUP_ROLE
            FROM USERS ur,CATEGORIES C,CATEGORY_ROLES CR ,ROLES r,PAGES p
            WHERE ur.user_id=p_userId
            AND ur.CATEGORY_CODE=C.CATEGORY_CODE
            AND C.FIXED_ROLES='Y'
            AND C.CATEGORY_CODE=CR.CATEGORY_CODE
            AND CR.ROLE_CODE=R.ROLE_CODE
            AND r.status='Y';
BEGIN
     FOR tr IN c_userRolesType(p_userId)
       LOOP
              counter:=counter+1;
           IF counter>0 THEN
                 IF p_nameorcode LIKE 'Y' THEN
                          p_userRolesType:=tr.roleType;
              ELSE --IF p_nameorcode LIKE 'N' THEN
                    IF tr.roleType LIKE 'group role' THEN
                       p_userRolesType:='Y';
                  ELSE --IF tr.roleType LIKE 'system role' THEN
                       p_userRolesType:='N';
                  END IF;
              END IF;
              EXIT;
              END IF;
       END LOOP;

     FOR tr1 IN c_userFixRolesType(p_userId)
       LOOP
              counter:=counter+1;
           IF counter>0 THEN
                 IF p_nameorcode LIKE 'Y' THEN
                     p_userRolesType:='fix roles';
              ELSE --IF p_nameorcode LIKE 'N' THEN
                    p_userRolesType:='F';
              END IF;
              EXIT;
              END IF;
       END LOOP;

     RETURN p_userRolesType;
END;
/


DROP FUNCTION GETUSERROLES;

CREATE OR REPLACE FUNCTION "GETUSERROLES" 
--This function is used for generate report for assigned roles
--(p_userId  VARCHAR2) RETURN CLOB
--p_roleName(if Y then role_name else role_code)
(p_userId  VARCHAR2,p_roleName VARCHAR2) RETURN VARCHAR
IS
--p_userRoles CLOB;
p_userRoles VARCHAR(32767) DEFAULT '';
role_name_code VARCHAR2(100) DEFAULT '';
role_type VARCHAR2(1);
group_role VARCHAR2(1) DEFAULT '';
oldGroupName VARCHAR2(100)DEFAULT '';
newGroupName VARCHAR2(100)DEFAULT '';
var NUMBER(5) DEFAULT 0;
CURSOR c_userRoles(p_userId VARCHAR2) IS
            SELECT ur.user_id,trim(r.role_name)role_name,r.GROUP_ROLE,m.MODULE_NAME,r.ROLE_CODE
            FROM USER_ROLES ur, ROLES r,PAGE_ROLES pr,PAGES p,MODULES m,CATEGORIES c, USERS u, DOMAINS d
            WHERE r.role_code=ur.role_code
            AND ur.user_id=p_userId
            AND r.status='Y'
            AND r.ROLE_CODE=pr.ROLE_CODE
            AND pr.PAGE_CODE=p.PAGE_CODE
            AND p.MENU_LEVEL=1
            AND p.MODULE_CODE=m.MODULE_CODE
            AND d.DOMAIN_TYPE_CODE=r.DOMAIN_TYPE
            AND ur.USER_ID=u.USER_ID
            AND u.CATEGORY_CODE=c.CATEGORY_CODE
            AND c.DOMAIN_CODE=d.DOMAIN_CODE
            ORDER BY m.MODULE_NAME,r.role_name;
CURSOR c_userGroupRoles(p_userId VARCHAR2) IS
            SELECT ur.user_id,trim(r.role_name)role_name ,r.GROUP_ROLE,m.MODULE_NAME,r.ROLE_CODE
            FROM USER_ROLES ur, ROLES r,GROUP_ROLES gr,PAGE_ROLES pr,PAGES p,MODULES m,CATEGORIES c, USERS u, DOMAINS d
            WHERE ur.ROLE_CODE=gr.GROUP_ROLE_CODE
            AND gr.ROLE_CODE=r.ROLE_CODE
            AND ur.user_id=p_userId
            AND r.status='Y'
            --AND cr.CATEGORY_CODE=u.CATEGORY_CODE
            --AND cr.ROLE_CODE=r.ROLE_CODE
            AND r.ROLE_CODE=pr.ROLE_CODE
            AND pr.PAGE_CODE=p.PAGE_CODE
            AND p.MENU_LEVEL=1
            AND p.MODULE_CODE=m.MODULE_CODE
            AND d.DOMAIN_TYPE_CODE=r.DOMAIN_TYPE
            AND ur.USER_ID=u.USER_ID
            AND u.CATEGORY_CODE=c.CATEGORY_CODE
            AND c.DOMAIN_CODE=d.DOMAIN_CODE
            ORDER BY m.MODULE_NAME,r.role_name;

CURSOR c_userFixRoles(p_userId VARCHAR2) IS
            SELECT ur.user_id,TRIM(r.role_name)role_name,r.GROUP_ROLE,m.MODULE_NAME,r.ROLE_CODE
            FROM USERS ur,CATEGORIES C,CATEGORY_ROLES CR ,ROLES r,PAGE_ROLES pr,PAGES p,MODULES m,DOMAINS D
            WHERE ur.user_id=p_userId
            AND ur.CATEGORY_CODE=C.CATEGORY_CODE
            AND C.FIXED_ROLES='Y'
            AND C.CATEGORY_CODE=CR.CATEGORY_CODE
            AND CR.ROLE_CODE=R.ROLE_CODE
            AND r.status='Y'
            AND r.ROLE_CODE=pr.ROLE_CODE
            AND pr.PAGE_CODE=p.PAGE_CODE
            AND p.MODULE_CODE=m.MODULE_CODE
            AND p.MENU_LEVEL=1
            AND d.DOMAIN_TYPE_CODE=r.DOMAIN_TYPE
            AND c.DOMAIN_CODE=d.DOMAIN_CODE
            ORDER BY m.MODULE_NAME,r.role_name;
BEGIN
       oldGroupName:='###';
       FOR tr IN c_userRoles(p_userId)
       LOOP
               role_type:=tr.GROUP_ROLE;
             IF p_roleName LIKE 'Y' THEN
               role_name_code:=tr.role_name;
            ELSE
               role_name_code:=tr.ROLE_CODE;
            END IF;
            IF role_type LIKE 'N' THEN
                  newGroupName:=UPPER(trim(tr.MODULE_NAME));
            --DBMS_OUTPUT.PUT_LINE('newGroupName='||newGroupName||' oldGroupName='||oldGroupName);
               IF newGroupName <> oldGroupName THEN
                     oldGroupName:=newGroupName;
                  IF p_roleName LIKE 'Y' THEN
                          p_userRoles:=p_userRoles||'<b>'||'('||tr.MODULE_NAME||')'||'</b>'||role_name_code||', ';
                  ELSE
                       p_userRoles:=p_userRoles||role_name_code||', ';
                  END IF;
               ELSE
                p_userRoles:=p_userRoles||role_name_code||', ';
               END IF;
            END IF;

        END LOOP;
        FOR tr1 IN c_userGroupRoles(p_userId)
        LOOP
            role_type:=tr1.GROUP_ROLE;
            IF p_roleName LIKE 'Y' THEN
               role_name_code:=tr1.role_name;
            ELSE
               role_name_code:=tr1.ROLE_CODE;
            END IF;
            IF role_type LIKE 'N' THEN
               --p_userRoles:=p_userRoles||to_clob(tr.role_name)||',';
                newGroupName:=UPPER(trim(tr1.MODULE_NAME));
            --DBMS_OUTPUT.PUT_LINE('newGroupName='||newGroupName||' oldGroupName='||oldGroupName);
                IF newGroupName <> oldGroupName THEN
                   oldGroupName:=newGroupName;
                   IF p_roleName LIKE 'Y' THEN
                         p_userRoles:=p_userRoles||'<b>'||'('||tr1.MODULE_NAME||')'||'</b>'||role_name_code||', ';
                   ELSE
                         p_userRoles:=p_userRoles||role_name_code||', ';
                   END IF;
                ELSE
                    p_userRoles:=p_userRoles||role_name_code||', ';
                END IF;
              END IF;
        END LOOP;
        FOR tr2 IN c_userFixRoles(p_userId)
        LOOP
            role_type:='F';
            IF p_roleName LIKE 'Y' THEN
               role_name_code:=tr2.role_name;
            ELSE
               role_name_code:=tr2.ROLE_CODE;
            END IF;
            newGroupName:=UPPER(trim(tr2.MODULE_NAME));
            --DBMS_OUTPUT.PUT_LINE('newGroupName='||newGroupName||' oldGroupName='||oldGroupName);
            IF newGroupName <> oldGroupName THEN
               oldGroupName:=newGroupName;
               IF p_roleName LIKE 'Y' THEN
                     p_userRoles:=p_userRoles||'<b>'||'('||tr2.MODULE_NAME||')'||'</b>'||role_name_code||', ';
               ELSE
                          p_userRoles:=p_userRoles||role_name_code||', ';
                  END IF;
            ELSE
                p_userRoles:=p_userRoles||role_name_code||', ';
            END IF;

          END LOOP;

      IF LENGTH(p_userRoles) > 0 THEN
         p_userRoles:=SUBSTR(p_userRoles,0,LENGTH(p_userRoles)-2);
      END IF;
      --p_userRoles:='<div align="left" style="white-space: 0; letter-spacing: 0; " >'||p_userRoles||'</div>';

      RETURN p_userRoles;
END;


--DEF 1073 fix
/


DROP FUNCTION GETPRODUCT;

CREATE OR REPLACE FUNCTION getProduct
--This function is used for corporate low balance alert 
(p_productType  varchar2,p_amount number) RETURN varchar2
IS
product varchar2(15);
CURSOR c_product(p_productType varchar2) IS
        SELECT short_name,unit_value FROM products
            WHERE product_type=p_productType
            ORDER BY unit_value DESC;            
BEGIN
    FOR tr IN c_product(p_productType) LOOP
        IF p_amount=tr.unit_value THEN
           RETURN tr.short_name;
        END IF;
        product:=tr.short_name;
    END LOOP;
    RETURN product;
END;
/


DROP FUNCTION EVALUATE_NEW_MSISDN;

CREATE OR REPLACE FUNCTION evaluate_new_msisdn ( p_msisdn IN varchar2 )
  return varchar2 is
  prefix3 varchar2(3);
  prefix4 varchar2(4);
  newmsisdn varchar2(20);
  begin
   prefix3:=substr(p_msisdn,1,3);
   prefix4:=substr(p_msisdn,1,4);

             if prefix3='012' then
                             newmsisdn:='0122'|| substr(p_msisdn,4);
             elsif prefix3='017' then
                             newmsisdn:='0127'|| substr(p_msisdn,4);
             elsif prefix3='018' then
                             newmsisdn:='0128'|| substr(p_msisdn,4);
             elsif prefix4='0150' then
                             newmsisdn:='0120'|| substr(p_msisdn,5);
             elsif prefix3='010' then
                             newmsisdn:='0100'|| substr(p_msisdn,4);
             elsif prefix3='016' then
                             newmsisdn:='0106'|| substr(p_msisdn,4);
             elsif prefix3='019' then
                             newmsisdn:='0109'|| substr(p_msisdn,4);
             elsif prefix4='0151' then
                             newmsisdn:='0101'|| substr(p_msisdn,5);
             elsif prefix3='011' then
                             newmsisdn:='0111'|| substr(p_msisdn,4);
             elsif prefix3='014' then
                             newmsisdn:='0114'|| substr(p_msisdn,4);
             elsif prefix4='0152' then
                             newmsisdn:='0112'|| substr(p_msisdn,5);
             else
                             newmsisdn:=p_msisdn;
             end if;

   return newmsisdn;
   end;
/


