DECLARE 
  P_TOTALROWSDELETED NUMBER;
  P_MESSAGE VARCHAR2(200);
  P_MESSAGEFORLOG VARCHAR2(200);
  P_SQLERRMSGFORLOG VARCHAR2(200);

BEGIN 
  P_TOTALROWSDELETED := NULL;
  P_MESSAGE := NULL;
  P_MESSAGEFORLOG := NULL;
  P_SQLERRMSGFORLOG := NULL;

 PURGE_HISTORY_TABLES_PKG.DELETE_HISTORY_TABLES ( P_TOTALROWSDELETED, P_MESSAGE, P_MESSAGEFORLOG, P_SQLERRMSGFORLOG );
  COMMIT; 
END; 
/
exit
