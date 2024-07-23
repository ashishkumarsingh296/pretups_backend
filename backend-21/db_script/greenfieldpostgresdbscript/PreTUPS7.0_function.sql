
CREATE FUNCTION acs__add_user(user_id character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $_$
DECLARE
    user_id ALIAS FOR $1;
    gv_user_name character varying;
    v_user_id user_id%TYPE;
   
BEGIN
    v_user_id := user__new(user_id);

    RETURN v_user_id;
END;
$_$;


ALTER FUNCTION pretupsdatabase.acs__add_user(user_id character varying) OWNER TO pgdb;

--
-- Name: add_new_msisdn_columns(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION add_new_msisdn_columns() RETURNS void
    LANGUAGE plpgsql
    AS $$
  DECLARE
exec_stmt varchar(400);
 table_column_names cursor is select distinct table_name,column_names from migrate_msisdn order by 1;
 tbl_names cursor is select distinct table_name from migrate_msisdn;

begin

--open table_column_names;
--open tbl_names;
for clmn in table_column_names
loop
exec_stmt:='alter table '|| clmn.table_name || ' add old_' || clmn.column_names || ' varchar2(15)';
RAISE NOTICE '%','executing stmt : '|| exec_stmt;
execute immediate exec_stmt;
end loop;
for tbl in tbl_names
loop
exec_stmt:='alter table '|| tbl.table_name|| ' add msisdn_modified varchar2(1)';
RAISE NOTICE '%','executing stmt : '|| exec_stmt;
execute immediate exec_stmt;
end loop;
--close tbl_names;
--close table_column_names;
end;
$$;


ALTER FUNCTION pretupsdatabase.add_new_msisdn_columns() OWNER TO pgdb;

--
-- Name: c2s_transfers_details(character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION c2s_transfers_details(aiv_date character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare 
   the_date               timestamp(0);
   v_message              varchar (500);
   v_messageforlog        varchar (500);
   v_sqlerrmsgforlog      varchar (500);
   sql_stmt               varchar (2000);
   sql_stmt1               varchar (2000);
   user_type            c2s_transfer_items.user_type%type;
   entry_type           c2s_transfer_items.entry_type%type; 
   sno_t                c2s_transfer_items.sno%type;
   validation_status    c2s_transfer_items.validation_status%type;
   sender_status        c2s_transfer_items.transfer_status%type;
   reciever_status      c2s_transfer_items.transfer_status%type;
   
   empty_e                varchar (2);
 declare c2s_transfer_cursor cursor ( in_date timestamp(0)) 
   for
      select  * from c2s_transfers where transfer_date=in_date;
   begin

      the_date:=to_date(aiv_date,'dd/mm/yy');
      RAISE NOTICE '%', 'i am here 1:' || the_date || aiv_date;
      sql_stmt := '';
      sql_stmt1 := '';
      empty_e := '';
      for c2s in c2s_transfer_cursor(the_date)
      loop
           validation_status := c2s.validation_status;
           sender_status := c2s.debit_status;
           reciever_status := c2s.credit_status;
         begin
            insert into c2s_transfers_old values
            (c2s.transfer_id,c2s.transfer_date,c2s.transfer_date_time,c2s.network_code,c2s.sender_id
            ,c2s.sender_category,c2s.product_code,c2s.sender_msisdn,c2s.receiver_msisdn,c2s.receiver_network_code
            ,c2s.transfer_value,c2s.error_code,c2s.request_gateway_type,c2s.request_gateway_code,c2s.reference_id
            ,c2s.service_type,c2s.differential_applicable,c2s.pin_sent_to_msisdn,c2s.language,c2s.country
            ,c2s.skey,c2s.skey_generation_time,c2s.skey_sent_to_msisdn,c2s.request_through_queue,c2s.credit_back_status
            ,c2s.quantity,c2s.reconciliation_flag,c2s.reconciliation_date,c2s.reconciliation_by,c2s.created_on
            ,c2s.created_by,c2s.modified_on,c2s.modified_by,c2s.transfer_status,c2s.card_group_set_id
            ,c2s.version,c2s.card_group_id,c2s.sender_transfer_value,c2s.receiver_access_fee,c2s.receiver_tax1_type
            ,c2s.receiver_tax1_rate,c2s.receiver_tax1_value,c2s.receiver_tax2_type,c2s.receiver_tax2_rate,c2s.receiver_tax2_value
            ,c2s.receiver_validity,c2s.receiver_transfer_value,c2s.receiver_bonus_value,c2s.receiver_grace_period,c2s.receiver_bonus_validity
            ,c2s.card_group_code,c2s.receiver_valperiod_type,c2s.temp_transfer_id,c2s.transfer_profile_id,c2s.commission_profile_id
            ,c2s.differential_given,c2s.grph_domain_code,c2s.source_type,c2s.sub_service,c2s.start_time
            ,c2s.end_time,c2s.serial_number,c2s.ext_credit_intfce_type,c2s.bonus_details,c2s.active_user_id
            ,c2s.subs_sid);
            
             user_type := 'sender';
             entry_type := 'dr';
             sno_t := 1;
             if(validation_status <> '200')
             then
                validation_status := '212';
             end if;   
                
             if(sender_status <> '200')
             then
                sender_status := '212';
             end if;
             
             if(reciever_status is null)
             then
                reciever_status := '206';
             end if;
             
             insert into c2s_transfer_items
             values (c2s.transfer_id,c2s.sender_msisdn,c2s.transfer_date,c2s.transfer_value,c2s.sender_previous_balance,
             c2s.sender_post_balance,user_type,c2s.transfer_type,entry_type,validation_status,c2s.debit_status,c2s.transfer_value,
             empty_e,empty_e,empty_e,empty_e,empty_e,empty_e,null,null,sender_status,c2s.transfer_date,c2s.transfer_date_time,
             c2s.transfer_date_time,empty_e,sno_t,c2s.sender_prefix_id,empty_e,empty_e,empty_e,c2s.adjust_dr_txn_type,c2s.adjust_dr_txn_id,
             c2s.adjust_dr_update_status,empty_e,empty_e,empty_e,c2s.adjust_value,empty_e,c2s.country,c2s.language);
          
         
            
            user_type := 'reciever';
            entry_type := 'cr';
            sno_t := 2;
            
            insert into c2s_transfer_items
            values (c2s.transfer_id,c2s.receiver_msisdn,c2s.transfer_date,c2s.transfer_value,c2s.receiver_previous_balance,
             c2s.receiver_post_balance,user_type,c2s.transfer_type,entry_type,c2s.validation_status,
             c2s.credit_status,c2s.receiver_transfer_value,c2s.interface_type,c2s.interface_id,c2s.interface_response_code,
             c2s.interface_reference_id,c2s.subscriber_type,c2s.service_class_code,c2s.msisdn_previous_expiry,c2s.msisdn_new_expiry,
             reciever_status,c2s.transfer_date,c2s.transfer_date_time,c2s.transfer_date_time,c2s.first_call,
             sno_t,c2s.prefix_id,c2s.service_class_id,c2s.protocol_status,c2s.account_status,
             empty_e,empty_e,empty_e,c2s.adjust_cr_txn_type,c2s.adjust_cr_txn_id,
             c2s.adjust_cr_update_status,c2s.adjust_value,c2s.rcvr_intrfc_reference_id,c2s.country,c2s.language);
                         
            exception                       
                        when others
                        then
                            raise notice '%', 'others exception in c2s_transfers_details 1'|| sqlerrm;
                            v_messageforlog := 'others exception in c2s_transfers_details 1';
                            v_sqlerrmsgforlog := sqlerrm;
                            RAISE EXCEPTION 'others exception in c2s_transfers_details 1';
                    end;
         end loop;  
         
         exception
        when others
        then
        raise notice '%', 'others exception in c2s_transfers_details 2:' || sqlerrm;
        v_message:='failed';
        /* commit; */
end;
$$;


ALTER FUNCTION pretupsdatabase.c2s_transfers_details(aiv_date character varying) OWNER TO pgdb;

--
-- Name: c2stxnminutswise(character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION c2stxnminutswise(p_date character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare 
-- p_date should be dd-mon-yy format
   v_cnt varchar(10);
   v_temp varchar(25);
   v_h varchar (2);
   v_m varchar (2);

begin


    truncate table temp_c2s_hrourly_count;

	raise notice '%','Before Insert .....';
        insert into temp_c2s_hrourly_count (date_minute, txt_count) select to_char(transfer_date_time,'dd-mm-yy hh24:mi') date_minute, count(1) txt_count from c2s_transfers where transfer_date=to_date(p_date,'dd/mm/yy') group by to_char(transfer_date_time,'dd-mm-yy hh24:mi');

        /* commit; */

        for i in 0..23 loop

              if i<10 then
               v_h:='0'||i;
              else
                   v_h:=i;
              end if;

              for j in 0..59 loop

               if j<10 then
                    v_m:='0'||j;
               else
                   v_m:=j;
               end if;

                    v_temp:='';

                    v_temp:=p_date||' '||v_h||':'||v_m;

                                        --dbms_output.put_line(v_temp);

                                        begin
                                        raise notice '%','Before select .....';

                                        v_cnt=(select txt_count  from  temp_c2s_hrourly_count where to_date(date_minute,'dd-mm-yy hh24:mi')=to_date(v_temp,'dd-mm-yy hh24:mi'));

                                        exception
                                         when no_data_found
                                         then
                                         v_cnt := 0;
                    end;
                   -- execute immediate 'select txt_count from  temp_c2s_hrourly_count where to_char(date_minute,''dd-mon-yy hh24:mi'')=:1' into v_cnt using to_char(v_temp,'dd-mon-yy hh24:mi');

                    raise notice '%',v_temp ||':'||v_h||','||v_m||'='||v_cnt;


              end loop;-- minute loop

        end loop; --hours loop

end;
$$;


ALTER FUNCTION pretupsdatabase.c2stxnminutswise(p_date character varying) OWNER TO pgdb;

--
-- Name: copy_msisdns_in_mstr_tbls(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION copy_msisdns_in_mstr_tbls() RETURNS void
    LANGUAGE plpgsql
    AS $$
 declare 
count bigint;
master_table_list cursor  is select distinct table_name from migrate_msisdn where table_name not in( select distinct table_name from USER_TAB_PARTITIONS) order by 1;

begin

--open master_table_list;
for tbl in master_table_list
	loop
		RAISE NOTICE '%','executing updt_crtd_clmns_in_tbl('||tbl.table_name||')';
		perform updt_crtd_clmns_in_tbl(tbl.table_name);
	end loop;
--close master_table_list;
end;
$$;


ALTER FUNCTION pretupsdatabase.copy_msisdns_in_mstr_tbls() OWNER TO pgdb;

--
-- Name: copy_msisdns_in_prtn_tbls(character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION copy_msisdns_in_prtn_tbls(p_month character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$
 declare 

count bigint;
prtn_table_list cursor  is select table_name,PARTITION_NAME from USER_TAB_PARTITIONS where table_name in( select distinct table_name from migrate_msisdn) and PARTITION_NAME like '%'||p_month||'%' order by 1;

begin

--open prtn_table_list;
for tbl in prtn_table_list
	loop
				--open column_names(tbl.table_name);
				RAISE NOTICE '%','executing updt_crtd_clmns_in_tbl('||tbl.table_name||','||tbl.PARTITION_NAME||')';
				perform updt_crtd_clmns_in_tbl(tbl.table_name,tbl.PARTITION_NAME);
	end loop;
--close prtn_table_list;
end;
$$;


ALTER FUNCTION pretupsdatabase.copy_msisdns_in_prtn_tbls(p_month character varying) OWNER TO pgdb;

--
-- Name: create_indexes_for_partitioned_table(text, boolean); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION create_indexes_for_partitioned_table(p_table_names text, p_is_table_names_supplied boolean, OUT return_message character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE
V_TABLE_NAMES TEXT[] ;
V_TABLE_NAME TEXT;
V_PARTITION_TABLES_NAME_CUR cursor(TABLE_NAME text) For 
select relname from pg_inherits i join pg_class c on c.oid = inhrelid where inhparent = TABLE_NAME ::regclass;
CREATE_INDEX_QRY TEXT;
DROP_EXIST_INDEX_QRY TEXT; 
IS_PK_INDEX_EXISTS BOOLEAN:=FALSE;
--INDEX_NAMES_CURR cursor(PARTITION_TABLE_NAME text) for SELECT indexname FROM pg_catalog.pg_indexes WHERE tablename = PARTITION_TABLE_NAME ;
BEGIN

IF(P_IS_TABLE_NAMES_SUPPLIED = true) then
	V_TABLE_NAMES := string_to_array(P_TABLE_NAMES,',');
ELSE
	V_TABLE_NAMES := '{ADJUSTMENTS,C2S_DAILY_FAILURE_DETAILS,C2S_DAILY_TRANSACTIONS,C2S_SUMMARY_DAILY,C2S_TRANSFER_ITEMS,C2S_TRANSFERS,CHANNEL_TRANSFERS,HOURLY_TRANSACTION_SUMMARY,MONTHLY_CHNL_TRANS_DETAILS,MONTHLY_TRANSACTION_SUMMARY,P2P_SUBSCRIBER_SUMMARY,TRANSFER_ITEMS,VOMS_VOUCHERS}';
END IF;

RAISE NOTICE  'Indexes creating for partitioned tables: %',V_TABLE_NAMES;
--Looping for all tables where partiton is applied
FOREACH V_TABLE_NAME IN ARRAY V_TABLE_NAMES
LOOP
RAISE NOTICE  'Indexes creating for table: %',V_TABLE_NAME;
IF(UPPER(TRIM(V_TABLE_NAME)) = 'ADJUSTMENTS') then
--Looping for all partitions of table ADJUSTMENTS 
	FOR V_PARTITION_TABLE_NAME in V_PARTITION_TABLES_NAME_CUR(V_TABLE_NAME)
	LOOP
		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (USER_ID ASC, PRODUCT_CODE ASC , NETWORK_CODE_FOR ASC, ADJUSTMENT_DATE ASC )
		TABLESPACE PRTP_DATA_1 ' ;
		EXECUTE CREATE_INDEX_QRY;

		CREATE_INDEX_QRY :='CREATE INDEX  '  ||' ON '||V_PARTITION_TABLE_NAME.relname ||
		 ' USING btree (reference_id COLLATE pg_catalog."default") TABLESPACE prtp_data_1';
		EXECUTE CREATE_INDEX_QRY;

		--IF pk index already available then no need to create index again
		BEGIN
		CREATE_INDEX_QRY := ' CREATE UNIQUE INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ||' ON '|| V_PARTITION_TABLE_NAME.relname ||' USING btree (ADJUSTMENT_ID ASC )';
		EXECUTE CREATE_INDEX_QRY;
		EXCEPTION
		WHEN OTHERS THEN
		IS_PK_INDEX_EXISTS :=TRUE;
		RAISE NOTICE  'PK Indexing already exists for: %',V_PARTITION_TABLE_NAME.relname;
		END;

		IF(IS_PK_INDEX_EXISTS = FALSE) then
		CREATE_INDEX_QRY :='ALTER TABLE '|| V_PARTITION_TABLE_NAME.relname ||' ADD PRIMARY KEY USING INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ;
		EXECUTE CREATE_INDEX_QRY;
		END IF;

		RAISE NOTICE  'Indexes created for partitioned table: %',V_PARTITION_TABLE_NAME.relname;
	END LOOP;

ELSIF (UPPER(TRIM(V_TABLE_NAME)) = 'C2S_DAILY_FAILURE_DETAILS') THEN
--Looping for all partitions of table C2S_DAILY_FAILURE_DETAILS
	FOR V_PARTITION_TABLE_NAME in V_PARTITION_TABLES_NAME_CUR(V_TABLE_NAME)
	LOOP
		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (TRANS_DATE ASC, ERROR_CODE ASC  )
		TABLESPACE ALL_INDX ' ;
		EXECUTE CREATE_INDEX_QRY;

		RAISE NOTICE  'Indexes created for partitioned table: %',V_PARTITION_TABLE_NAME.relname;
	END LOOP;


ELSIF (UPPER(TRIM(V_TABLE_NAME)) = 'C2S_DAILY_TRANSACTIONS') THEN
--Looping for all partitions of table C2S_DAILY_TRANSACTIONS
	FOR V_PARTITION_TABLE_NAME in V_PARTITION_TABLES_NAME_CUR(V_TABLE_NAME)
	LOOP
		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (TRANS_DATE ASC, NETWORK_CODE ASC , RECEIVER_SERVICE_CLASS_ID ASC )
		TABLESPACE ALL_INDX ' ;
		EXECUTE CREATE_INDEX_QRY;

		BEGIN
		CREATE_INDEX_QRY := ' CREATE UNIQUE INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ||' ON '|| V_PARTITION_TABLE_NAME.relname ||' USING btree (TRANS_DATE ASC,NETWORK_CODE ASC,RECEIVER_NETWORK_CODE ASC,SENDER_CATEGORY ASC,SERVICE_TYPE ASC,SUB_SERVICE_TYPE ASC, RECEIVER_SERVICE_CLASS_ID ASC, RECEIVER_SERVICE_CLASS_CODE ASC )';
		EXECUTE CREATE_INDEX_QRY;
		EXCEPTION
		WHEN OTHERS THEN
		IS_PK_INDEX_EXISTS :=TRUE;
		RAISE NOTICE  'PK Indexing already exists for: %',V_PARTITION_TABLE_NAME.relname;
		END;

		IF(IS_PK_INDEX_EXISTS = FALSE) then
		CREATE_INDEX_QRY :='ALTER TABLE '|| V_PARTITION_TABLE_NAME.relname ||' ADD PRIMARY KEY USING INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ;
		EXECUTE CREATE_INDEX_QRY;
		END IF;

		RAISE NOTICE  'Indexes created for partitioned table: %',V_PARTITION_TABLE_NAME.relname;
	END LOOP;



ELSIF (UPPER(TRIM(V_TABLE_NAME)) = 'C2S_SUMMARY_DAILY') THEN
--Looping for all partitions of table C2S_SUMMARY_DAILY
	FOR V_PARTITION_TABLE_NAME in V_PARTITION_TABLES_NAME_CUR(V_TABLE_NAME)
	LOOP
		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (TRANS_DATE ASC, SENDER_NETWORK_CODE ASC )
		TABLESPACE ALL_INDX ' ;
		EXECUTE CREATE_INDEX_QRY;

		BEGIN
		CREATE_INDEX_QRY := ' CREATE UNIQUE INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ||' ON '|| V_PARTITION_TABLE_NAME.relname ||' USING btree (TRANS_DATE ASC,SENDER_NETWORK_CODE ASC,RECEIVER_NETWORK_CODE ASC,SERVICE_TYPE ASC,SUB_SERVICE_TYPE ASC)';
		EXECUTE CREATE_INDEX_QRY;
		EXCEPTION
		WHEN OTHERS THEN
		IS_PK_INDEX_EXISTS :=TRUE;
		RAISE NOTICE  'PK Indexing already exists for: %',V_PARTITION_TABLE_NAME.relname;
		END;

		IF(IS_PK_INDEX_EXISTS = FALSE) then
		CREATE_INDEX_QRY :='ALTER TABLE '|| V_PARTITION_TABLE_NAME.relname ||' ADD PRIMARY KEY USING INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ;
		EXECUTE CREATE_INDEX_QRY;
		END IF;

		RAISE NOTICE  'Indexes created for partitioned table: %',V_PARTITION_TABLE_NAME.relname;
	END LOOP;

ELSIF (UPPER(TRIM(V_TABLE_NAME)) = 'C2S_TRANSFER_ITEMS') THEN
--Looping for all partitions of table C2S_TRANSFER_ITEMS
	FOR V_PARTITION_TABLE_NAME in V_PARTITION_TABLES_NAME_CUR(V_TABLE_NAME)
	LOOP
		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (TRANSFER_DATE ASC)
		TABLESPACE P_C2SDATA ' ;
		EXECUTE CREATE_INDEX_QRY;

		BEGIN
		CREATE_INDEX_QRY := ' CREATE UNIQUE INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ||' ON '|| V_PARTITION_TABLE_NAME.relname ||' USING btree (TRANSFER_ID ASC,SNO ASC) TABLESPACE P_C2SDATA';
		EXECUTE CREATE_INDEX_QRY;
		EXCEPTION
		WHEN OTHERS THEN
		IS_PK_INDEX_EXISTS :=TRUE;
		RAISE NOTICE  'PK Indexing already exists for: %',V_PARTITION_TABLE_NAME.relname;
		END;

		IF(IS_PK_INDEX_EXISTS = FALSE) then
		CREATE_INDEX_QRY :='ALTER TABLE '|| V_PARTITION_TABLE_NAME.relname ||' ADD PRIMARY KEY USING INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ;
		EXECUTE CREATE_INDEX_QRY;
		END IF;

		RAISE NOTICE  'Indexes created for partitioned table: %',V_PARTITION_TABLE_NAME.relname;
	END LOOP;

ELSIF (UPPER(TRIM(V_TABLE_NAME)) = 'C2S_TRANSFERS') THEN
--Looping for all partitions of table C2S_TRANSFERS
	FOR V_PARTITION_TABLE_NAME in V_PARTITION_TABLES_NAME_CUR(V_TABLE_NAME)
	LOOP
		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (TRANSFER_STATUS ASC, SENDER_ID, PRODUCT_CODE ASC ) TABLESPACE P_C2SINDEX ' ;
		EXECUTE CREATE_INDEX_QRY;

		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (SENDER_CATEGORY ASC)
		TABLESPACE P_C2SINDEX ' ;
		EXECUTE CREATE_INDEX_QRY;

		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (SENDER_ID ASC, PRODUCT_CODE ASC, NETWORK_CODE ASC, RECEIVER_NETWORK_CODE ASC, TRANSFER_DATE ASC )
		TABLESPACE P_C2SINDEX ' ;
		EXECUTE CREATE_INDEX_QRY;

		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (SENDER_MSISDN ASC)
		TABLESPACE P_C2SINDEX ' ;
		EXECUTE CREATE_INDEX_QRY;

		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (RECONCILIATION_DATE ASC)
		TABLESPACE P_C2SINDEX ' ;
		EXECUTE CREATE_INDEX_QRY;

		BEGIN
		CREATE_INDEX_QRY := ' CREATE UNIQUE INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ||' ON '|| V_PARTITION_TABLE_NAME.relname ||' USING btree (TRANSFER_ID ASC) TABLESPACE P_C2SINDEX ';
		EXECUTE CREATE_INDEX_QRY;
		EXCEPTION
		WHEN OTHERS THEN
		IS_PK_INDEX_EXISTS :=TRUE;
		RAISE NOTICE  'PK Indexing already exists for: %',V_PARTITION_TABLE_NAME.relname;
		END;

		IF(IS_PK_INDEX_EXISTS = FALSE) then
		CREATE_INDEX_QRY :='ALTER TABLE '|| V_PARTITION_TABLE_NAME.relname ||' ADD PRIMARY KEY USING INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ;
		EXECUTE CREATE_INDEX_QRY;
		END IF;

		RAISE NOTICE  'Indexes created for partitioned table: %',V_PARTITION_TABLE_NAME.relname;
	END LOOP;


ELSIF (UPPER(TRIM(V_TABLE_NAME)) = 'CHANNEL_TRANSFERS') THEN
--Looping for all partitions of table CHANNEL_TRANSFERS
	FOR V_PARTITION_TABLE_NAME in V_PARTITION_TABLES_NAME_CUR(V_TABLE_NAME)
	LOOP
		
		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (TRANSFER_DATE ASC, TRANSFER_CATEGORY)
		TABLESPACE P_C2SDATA ' ;
		EXECUTE CREATE_INDEX_QRY;

		RAISE NOTICE  'Indexes created for partitioned table: %',V_PARTITION_TABLE_NAME.relname;
	END LOOP;

ELSIF (UPPER(TRIM(V_TABLE_NAME)) = 'HOURLY_TRANSACTION_SUMMARY') THEN
--Looping for all partitions of table HOURLY_TRANSACTION_SUMMARY
	FOR V_PARTITION_TABLE_NAME in V_PARTITION_TABLES_NAME_CUR(V_TABLE_NAME)
	LOOP
		BEGIN
		CREATE_INDEX_QRY := ' CREATE UNIQUE INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ||' ON '|| V_PARTITION_TABLE_NAME.relname ||' USING btree (TRANS_DATE ASC,TRANS_HOUR ASC,SERVICE ASC, SUB_SERVICE ASC, TRANSFER_CATEGORY ASC, SENDER_SERVICE_CLASS ASC, RECEIVER_SERVICE_CLASS ASC) TABLESPACE P_C2SINDEX';
		EXECUTE CREATE_INDEX_QRY;
		EXCEPTION
		WHEN OTHERS THEN
		IS_PK_INDEX_EXISTS :=TRUE;
		RAISE NOTICE  'PK Indexing already exists for: %',V_PARTITION_TABLE_NAME.relname;
		END;

		IF(IS_PK_INDEX_EXISTS = FALSE) then
		CREATE_INDEX_QRY :='ALTER TABLE '|| V_PARTITION_TABLE_NAME.relname ||' ADD PRIMARY KEY USING INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ;
		EXECUTE CREATE_INDEX_QRY;
		END IF;

		
		RAISE NOTICE  'Indexes created for partitioned table: %',V_PARTITION_TABLE_NAME.relname;
	END LOOP;


ELSIF (UPPER(TRIM(V_TABLE_NAME)) = 'MONTHLY_CHNL_TRANS_DETAILS') THEN
--Looping for all partitions of table MONTHLY_CHNL_TRANS_DETAILS
	FOR V_PARTITION_TABLE_NAME in V_PARTITION_TABLES_NAME_CUR(V_TABLE_NAME)
	LOOP
		BEGIN
		CREATE_INDEX_QRY := ' CREATE UNIQUE INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ||' ON '|| V_PARTITION_TABLE_NAME.relname ||' USING btree (TRANS_DATE ASC,USER_ID ASC,RECEIVER_CATEGORY_CODE ASC, PRODUCT_CODE ASC, TYPE ASC, TRANSFER_CATEGORY ASC, TRANSFER_TYPE ASC, TRANSFER_SUB_TYPE ASC) 
		TABLESPACE ALL_INDX';
		EXECUTE CREATE_INDEX_QRY;
		EXCEPTION
		WHEN OTHERS THEN
		IS_PK_INDEX_EXISTS :=TRUE;
		RAISE NOTICE  'PK Indexing already exists for: %',V_PARTITION_TABLE_NAME.relname;
		END;

		IF(IS_PK_INDEX_EXISTS = FALSE) then
		CREATE_INDEX_QRY :='ALTER TABLE '|| V_PARTITION_TABLE_NAME.relname ||' ADD PRIMARY KEY USING INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ;
		EXECUTE CREATE_INDEX_QRY;
		END IF;

		
		RAISE NOTICE  'Indexes created for partitioned table: %',V_PARTITION_TABLE_NAME.relname;
	END LOOP;

ELSIF (UPPER(TRIM(V_TABLE_NAME)) = 'MONTHLY_TRANSACTION_SUMMARY') THEN
--Looping for all partitions of table MONTHLY_TRANSACTION_SUMMARY
	FOR V_PARTITION_TABLE_NAME in V_PARTITION_TABLES_NAME_CUR(V_TABLE_NAME)
	LOOP
		BEGIN
		CREATE_INDEX_QRY := ' CREATE UNIQUE INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ||' ON '|| V_PARTITION_TABLE_NAME.relname ||' USING btree (TRANS_MONTH_YEAR ASC,TRANS_MONTH ASC,SENDER_NETWORK_CODE ASC, RECEIVER_NETWORK_CODE ASC, SERVICE ASC, SUB_SERVICE ASC, TRANSFER_CATEGORY ASC, SENDER_SERVICE_CLASS ASC, RECEIVER_SERVICE_CLASS ASC) 
		TABLESPACE ALL_INDX';
		EXECUTE CREATE_INDEX_QRY;
		EXCEPTION
		WHEN OTHERS THEN
		IS_PK_INDEX_EXISTS :=TRUE;
		RAISE NOTICE  'PK Indexing already exists for: %',V_PARTITION_TABLE_NAME.relname;
		END;

		IF(IS_PK_INDEX_EXISTS = FALSE) then
		CREATE_INDEX_QRY :='ALTER TABLE '|| V_PARTITION_TABLE_NAME.relname ||' ADD PRIMARY KEY USING INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ;
		EXECUTE CREATE_INDEX_QRY;
		END IF;

		
		RAISE NOTICE  'Indexes created for partitioned table: %',V_PARTITION_TABLE_NAME.relname;
	END LOOP;

ELSIF (UPPER(TRIM(V_TABLE_NAME)) = 'P2P_SUBSCRIBER_SUMMARY') THEN
--Looping for all partitions of table P2P_SUBSCRIBER_SUMMARY
	FOR V_PARTITION_TABLE_NAME in V_PARTITION_TABLES_NAME_CUR(V_TABLE_NAME)
	LOOP
	
		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (USER_ID ASC, MONTH ASC, MONTH_YEAR ASC, SENDER_MSISDN ASC)
		TABLESPACE P_C2SDATA ' ;
		EXECUTE CREATE_INDEX_QRY;

		
		BEGIN
		CREATE_INDEX_QRY := ' CREATE UNIQUE INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ||' ON '|| V_PARTITION_TABLE_NAME.relname ||' USING btree (MONTH_YEAR ASC,MONTH ASC,USER_ID ASC, SUBSCRIBER_TYPE ASC, SENDER_SERVICE_CLASS ASC, SERVICE ASC, SUB_SERVICE ASC, TRANSFER_CATEGORY ASC) 
		TABLESPACE P_C2SDATA';
		EXECUTE CREATE_INDEX_QRY;
		EXCEPTION
		WHEN OTHERS THEN
		IS_PK_INDEX_EXISTS :=TRUE;
		RAISE NOTICE  'PK Indexing already exists for: %',V_PARTITION_TABLE_NAME.relname;
		END;

		IF(IS_PK_INDEX_EXISTS = FALSE) then
		CREATE_INDEX_QRY :='ALTER TABLE '|| V_PARTITION_TABLE_NAME.relname ||' ADD PRIMARY KEY USING INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ;
		EXECUTE CREATE_INDEX_QRY;
		END IF;

		
		RAISE NOTICE  'Indexes created for partitioned table: %',V_PARTITION_TABLE_NAME.relname;
	END LOOP;

ELSIF (UPPER(TRIM(V_TABLE_NAME)) = 'TRANSFER_ITEMS') THEN
--Looping for all partitions of table TRANSFER_ITEMS
	FOR V_PARTITION_TABLE_NAME in V_PARTITION_TABLES_NAME_CUR(V_TABLE_NAME)
	LOOP
	
		BEGIN
		CREATE_INDEX_QRY := ' CREATE UNIQUE INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ||' ON '|| V_PARTITION_TABLE_NAME.relname ||' USING btree (TRANSFER_ID ASC,SNO ASC) 
		TABLESPACE P_C2SDATA';
		EXECUTE CREATE_INDEX_QRY;
		EXCEPTION
		WHEN OTHERS THEN
		IS_PK_INDEX_EXISTS :=TRUE;
		RAISE NOTICE  'PK Indexing already exists for: %',V_PARTITION_TABLE_NAME.relname;
		END;

		IF(IS_PK_INDEX_EXISTS = FALSE) then
		CREATE_INDEX_QRY :='ALTER TABLE '|| V_PARTITION_TABLE_NAME.relname ||' ADD PRIMARY KEY USING INDEX PK_ADJUSTMENTS_'||V_PARTITION_TABLE_NAME.relname ;
		EXECUTE CREATE_INDEX_QRY;
		END IF;

		
		RAISE NOTICE  'Indexes created for partitioned table: %',V_PARTITION_TABLE_NAME.relname;
	END LOOP;

ELSIF (UPPER(TRIM(V_TABLE_NAME)) = 'VOMS_VOUCHERS') THEN
--Looping for all partitions of table VOMS_VOUCHERS
	FOR V_PARTITION_TABLE_NAME in V_PARTITION_TABLES_NAME_CUR(V_TABLE_NAME)
	LOOP
	
		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (CURRENT_STATUS ASC)
		TABLESPACE P_C2SDATA ' ;
		EXECUTE CREATE_INDEX_QRY;

		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (PIN_NO ASC)
		TABLESPACE P_C2SDATA ' ;
		EXECUTE CREATE_INDEX_QRY;

		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (PRODUCT_ID ASC)
		TABLESPACE P_C2SDATA ' ;
		EXECUTE CREATE_INDEX_QRY;

		CREATE_INDEX_QRY :='CREATE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (SEQUENCE_ID ASC)
		TABLESPACE P_C2SDATA ' ;
		EXECUTE CREATE_INDEX_QRY;
		
		CREATE_INDEX_QRY :='CREATE UNIQUE INDEX ' ||
		' ON ' || V_PARTITION_TABLE_NAME.relname || ' USING btree (SERIAL_NO ASC)
		TABLESPACE P_C2SDATA ' ;
		EXECUTE CREATE_INDEX_QRY;

		
		RAISE NOTICE  'Indexes created for partitioned table: %',V_PARTITION_TABLE_NAME.relname;
	END LOOP;
END IF;

END LOOP;

RAISE NOTICE  'Indexes created for all partitioned tables successfully';
RETURN_MESSAGE :='Indexes created for all partitioned tables successfully';
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE 'EXCEPTION while creating Indexes for partitioned tables: %' , SQLERRM; 
RETURN_MESSAGE :=   'EXCEPTION : ' ||SQLERRM;
END;
$$;


ALTER FUNCTION pretupsdatabase.create_indexes_for_partitioned_table(p_table_names text, p_is_table_names_supplied boolean, OUT return_message character varying) OWNER TO pgdb;

--
-- Name: create_pretups_indexes(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION create_pretups_indexes(OUT return_message character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
BEGIN

--Note  : For Unique, PK constraints of a table,by deafault postgres maintain index.
--table : users
--IDX_EXTERNAL_CODE
BEGIN
DROP INDEX IF EXISTS  idx_external_code;
CREATE INDEX idx_external_code
ON users USING btree (external_code COLLATE pg_catalog."default" ASC)
TABLESPACE ALL_INDX ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IDX_MSISDN
--ALTER TABLE subscriber_msisdn_alias RENAME  CONSTRAINT idx_msisdn to MSISDN;
BEGIN
DROP INDEX IF EXISTS  IDX_MSISDN;
CREATE INDEX IDX_MSISDN
ON users USING btree (MSISDN COLLATE pg_catalog."default" ASC)
TABLESPACE ALL_INDX ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--INDX_USERS_UNAME
BEGIN
DROP INDEX IF EXISTS  INDX_USERS_UNAME;
CREATE INDEX INDX_USERS_UNAME
ON users USING btree (USER_NAME COLLATE pg_catalog."default" ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_CATEGORY_CODE
BEGIN
DROP INDEX IF EXISTS  IND_CATEGORY_CODE;
CREATE INDEX IND_CATEGORY_CODE
ON users USING btree (CATEGORY_CODE COLLATE pg_catalog."default" ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_NWCODE1
BEGIN
DROP INDEX IF EXISTS  IND_NWCODE1;
CREATE INDEX IND_NWCODE1
ON users USING btree (NETWORK_CODE COLLATE pg_catalog."default" ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_STATUS
BEGIN
DROP INDEX IF EXISTS  IND_STATUS;
CREATE INDEX IND_STATUS
ON users USING btree (STATUS COLLATE pg_catalog."default" ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--IND_USERS1
BEGIN
DROP INDEX IF EXISTS  IND_USERS1;
CREATE INDEX IND_USERS1
ON users USING btree (UPPER("login_id") COLLATE pg_catalog."default" ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--IND_USERS2
BEGIN
DROP INDEX IF EXISTS  IND_USERS2;
CREATE INDEX IND_USERS2
ON users USING btree (PARENT_ID COLLATE pg_catalog."default" ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_USER_CODE
BEGIN
DROP INDEX IF EXISTS  IND_USER_CODE;
CREATE INDEX IND_USER_CODE
ON users USING btree (USER_CODE COLLATE pg_catalog."default" ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--INDX_ADDNL_COMMPDETAILS
BEGIN
DROP INDEX IF EXISTS  INDX_ADDNL_COMMPDETAILS;
CREATE INDEX INDX_ADDNL_COMMPDETAILS
ON ADDNL_COMM_PROFILE_DETAILS USING btree (COMM_PROFILE_SERVICE_TYPE_ID COLLATE pg_catalog."default" ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--table  :ADJUSTMENTS
--IND_ADJ_PROD_DATE
--DROP INDEX IF EXISTS  IND_ADJ_PROD_DATE;
--CREATE INDEX IND_ADJ_PROD_DATE
--ON ADJUSTMENTS USING btree (USER_ID ASC, PRODUCT_CODE ASC , NETWORK_CODE_FOR ASC, ADJUSTMENT_DATE ASC )
--TABLESPACE PRTP_DATA_1 ;

--IND_ADJ_REFID
--DROP INDEX IF EXISTS  IND_ADJ_REFID;
--CREATE INDEX IF NOT EXISTS  IND_ADJ_REFID
--ON ADJUSTMENTS USING btree (REFERENCE_ID COLLATE pg_catalog."default" ASC)
--TABLESPACE PRTP_DATA_1 ;


--table  :ADJUSTMENTS_MISTMP
--IND_ADJUSTMENTS_MISTMP
BEGIN
DROP INDEX IF EXISTS  IND_ADJUSTMENTS_MISTMP;
CREATE INDEX IND_ADJUSTMENTS_MISTMP
ON ADJUSTMENTS_MISTMP USING btree (REFERENCE_ID COLLATE pg_catalog."default" ASC)
TABLESPACE P_C2SDATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--table : BARRED_MSISDN_HISTORY
--IND_BARRED_MSISDN_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_BARRED_MSISDN_HISTORY;
CREATE INDEX IND_BARRED_MSISDN_HISTORY
ON BARRED_MSISDN_HISTORY USING btree (CREATED_ON  ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--table : BARRED_MSISDNS
--IND_BARRED_MSISDNS
BEGIN
DROP INDEX IF EXISTS  IND_BARRED_MSISDNS;
CREATE INDEX IND_BARRED_MSISDNS
ON BARRED_MSISDNS USING btree (CREATED_DATE  ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--table : BATCH_MASTER
--IND_BATCH_MASTER_JOB_ID
BEGIN
DROP INDEX IF EXISTS  IND_BATCH_MASTER_JOB_ID;
CREATE INDEX IND_BATCH_MASTER_JOB_ID
ON BATCH_MASTER USING btree (JOB_ID COLLATE pg_catalog."default"  ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--table : BONUS_HISTORY
--IND_BONUS_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_BONUS_HISTORY;
CREATE INDEX IND_BONUS_HISTORY
ON BONUS_HISTORY USING btree (CREATED_ON ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--table : C2S_BONUSES
--IND_C2S_BONUS
BEGIN
DROP INDEX IF EXISTS  IND_C2S_BONUS;
CREATE INDEX IND_C2S_BONUS
ON C2S_BONUSES USING btree (TRANSFER_ID ASC)
TABLESPACE P_C2SDATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--table : C2S_DAILY_FAILURE_DETAILS
--IND_C2S_DAILY_FAILURE_DETAILS
--DROP INDEX IF EXISTS  IND_C2S_DAILY_FAILURE_DETAILS;
--CREATE INDEX IND_C2S_DAILY_FAILURE_DETAILS
--ON C2S_DAILY_FAILURE_DETAILS USING btree (TRANS_DATE  ASC, ERROR_CODE  ASC )
--TABLESPACE ALL_INDX ;


--table : C2S_DAILY_TRANSACTIONS
--IND_C2S_DAILY_TRANSACTIONS
--DROP INDEX IF EXISTS  IND_C2S_DAILY_TRANSACTIONS;
--CREATE INDEX IND_C2S_DAILY_TRANSACTIONS
--ON C2S_DAILY_TRANSACTIONS USING btree (TRANS_DATE  ASC, NETWORK_CODE  ASC, RECEIVER_SERVICE_CLASS_ID ASC )
--TABLESPACE ALL_INDX ;

--C2S_IAT_TRANSFER_ITEMS
--IND_C2S_IAT_TRF_ITEM
BEGIN
DROP INDEX IF EXISTS  IND_C2S_IAT_TRF_ITEM;
CREATE INDEX IND_C2S_IAT_TRF_ITEM
ON C2S_IAT_TRANSFER_ITEMS USING btree (TRANSFER_DATE ASC, SENDER_ID ASC, REC_COUNTRY_CODE ASC, REC_NW_CODE ASC, SERVICE_TYPE ASC )
TABLESPACE P_C2SDATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--C2S_RECEIVER_REQUESTS
--IND_C2S_REC_REQUEST
BEGIN
DROP INDEX IF EXISTS  IND_C2S_REC_REQUEST;
CREATE INDEX IND_C2S_REC_REQUEST
ON C2S_RECEIVER_REQUESTS USING btree (TRANSACTION_ID ASC )
TABLESPACE P_C2SDATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_C2S_REC_REQ_MSISDN
BEGIN
DROP INDEX IF EXISTS  IND_C2S_REC_REQ_MSISDN;
CREATE INDEX IND_C2S_REC_REQ_MSISDN
ON C2S_RECEIVER_REQUESTS USING btree (REQUEST_MSISDN ASC )
TABLESPACE P_C2SDATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--C2S_SUB_DENOM_DETAILS
--IND_C2S_SUB_DENOM_DETAILS
BEGIN
DROP INDEX IF EXISTS  IND_C2S_SUB_DENOM_DETAILS;
CREATE INDEX IND_C2S_SUB_DENOM_DETAILS
ON C2S_SUB_DENOM_DETAILS USING btree (SERVICE_CLASS_ID ASC, INTERFACE_ID ASC )
TABLESPACE P_C2SDATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_C2S_SUB_DENOM_DETAILS1
BEGIN
DROP INDEX IF EXISTS  IND_C2S_SUB_DENOM_DETAILS1;
CREATE INDEX IND_C2S_SUB_DENOM_DETAILS1
ON C2S_SUB_DENOM_DETAILS USING btree (TRANSFER_DATE ASC )
TABLESPACE P_C2SDATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--C2S_SUMMARY_DAILY
--IND_C2S_SUMMARY_DAILY
--DROP INDEX IF EXISTS  IND_C2S_SUMMARY_DAILY;
--CREATE INDEX IND_C2S_SUMMARY_DAILY
--ON C2S_SUMMARY_DAILY USING btree (TRANS_DATE ASC )
--TABLESPACE P_C2SDATA ;

--IND_C2S_SUMMARY_DAILY
--DROP INDEX IF EXISTS  IND_C2S_SUMMARY_DAILY;
--CREATE INDEX IND_C2S_SUMMARY_DAILY
--ON C2S_SUMMARY_DAILY USING btree (TRANS_DATE ASC , SENDER_NETWORK_CODE ASC)
--TABLESPACE P_C2SDATA ;

--C2S_TRANSFER_ITEMS
--IND_C2S_TRANSFERS_ITE1
--DROP INDEX IF EXISTS  IND_C2S_TRANSFERS_ITE1;
--CREATE INDEX IND_C2S_TRANSFERS_ITE1
--ON C2S_TRANSFER_ITEMS USING btree (TRANSFER_DATE ASC )
--TABLESPACE P_C2SDATA ;

--C2S_TRANSFERS
--INDX_C2S_TRANSFER_LMS_PRF

--DROP INDEX IF EXISTS  INDX_C2S_TRANSFER_LMS_PRF;
--CREATE INDEX INDX_C2S_TRANSFER_LMS_PRF
--ON C2S_TRANSFERS USING btree (LMS_PROFILE ASC, LMS_VERSION ASC )
--TABLESPACE P_C2SINDEX ;

--INDX_C2S_TRANSFER_TRAFER_DATE
--DROP INDEX IF EXISTS  INDX_C2S_TRANSFER_TRAFER_DATE;
--CREATE INDEX INDX_C2S_TRANSFER_TRAFER_DATE
--ON C2S_TRANSFERS USING btree (TRANSFER_DATE ASC, SENDER_CATEGORY ASC )
--TABLESPACE P_C2SINDEX ;

--INDX_REC_MSISDN_C2STRANSFER
--DROP INDEX IF EXISTS  INDX_REC_MSISDN_C2STRANSFER;
--CREATE INDEX INDX_REC_MSISDN_C2STRANSFER
--ON C2S_TRANSFERS USING btree (RECEIVER_MSISDN ASC)
--TABLESPACE P_C2SINDEX ;

--INDX_SNDR_PROD_STAT
--DROP INDEX IF EXISTS  INDX_SNDR_PROD_STAT;
--CREATE INDEX INDX_SNDR_PROD_STAT
--ON C2S_TRANSFERS USING btree (TRANSFER_STATUS ASC,SENDER_ID ASC, PRODUCT_CODE ASC)
--TABLESPACE P_C2SINDEX ;

--IND_C2S_TRANSFER_CT
--DROP INDEX IF EXISTS  IND_C2S_TRANSFER_CT;
--CREATE INDEX IND_C2S_TRANSFER_CT
--ON C2S_TRANSFERS USING btree (SENDER_CATEGORY ASC)
--TABLESPACE P_C2SINDEX ;

--IND_SENDER_PROD_DATE
--DROP INDEX IF EXISTS  IND_C2S_TRANSFER_CT;
--CREATE INDEX IND_C2S_TRANSFER_CT
--ON C2S_TRANSFERS USING btree (SENDER_ID ASC,PRODUCT_CODE ASC,NETWORK_CODE ASC,RECEIVER_NETWORK_CODE ASC,TRANSFER_DATE ASC)
--TABLESPACE P_C2SINDEX ;

--INX_SND_MSISDN_C2STRANSFER
--DROP INDEX IF EXISTS  INX_SND_MSISDN_C2STRANSFER;
--CREATE INDEX INX_SND_MSISDN_C2STRANSFER
--ON C2S_TRANSFERS USING btree (SENDER_MSISDN ASC)
--TABLESPACE P_C2SINDEX ;

--RECON_DATE
--DROP INDEX IF EXISTS  RECON_DATE;
--CREATE INDEX RECON_DATE
--ON C2S_TRANSFERS USING btree (RECONCILIATION_DATE ASC)
--TABLESPACE P_C2SINDEX ;

--C2S_TRANSFERS_MISTMP
--IND_C2S_TRF_MISTMP
BEGIN
DROP INDEX IF EXISTS  IND_C2S_TRF_MISTMP;
CREATE INDEX IND_C2S_TRF_MISTMP
ON C2S_TRANSFERS_MISTMP USING btree (TRANSFER_STATUS ASC, TRANSFER_DATE ASC, TRANSFER_ID ASC)
TABLESPACE P_C2SDATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_SENDER_PROD_DATE_MISTMP
BEGIN
DROP INDEX IF EXISTS  IND_SENDER_PROD_DATE_MISTMP;
CREATE INDEX IND_SENDER_PROD_DATE_MISTMP
ON C2S_TRANSFERS_MISTMP USING btree (SENDER_ID ASC, PRODUCT_CODE ASC,NETWORK_CODE ASC, RECEIVER_NETWORK_CODE ASC, TRANSFER_DATE ASC)
TABLESPACE P_C2SDATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_SNDR_PROD_STAT_MISTMP
BEGIN
DROP INDEX IF EXISTS  IND_SNDR_PROD_STAT_MISTMP;
CREATE INDEX IND_SNDR_PROD_STAT_MISTMP
ON C2S_TRANSFERS_MISTMP USING btree (TRANSFER_STATUS ASC, SENDER_ID ASC,PRODUCT_CODE ASC)
TABLESPACE P_C2SDATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--CATEGORIES
--IND_DOMAIN_CODE
BEGIN
DROP INDEX IF EXISTS  IND_DOMAIN_CODE;
CREATE INDEX IND_DOMAIN_CODE
ON CATEGORIES USING btree (DOMAIN_CODE ASC, STATUS ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--CHANNEL_GRADES
--IND_CATCODE
BEGIN
DROP INDEX IF EXISTS  IND_CATCODE;
CREATE INDEX IND_CATCODE
ON CATEGORIES USING btree (CATEGORY_CODE ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--CHANNEL_TRANSFERS
--IND_CHANNEL_TRANSFERS
BEGIN
DROP INDEX IF EXISTS  IND_CHANNEL_TRANSFERS;
CREATE INDEX IND_CHANNEL_TRANSFERS
ON CHANNEL_TRANSFERS USING btree (TRANSFER_DATE ASC, TRANSFER_CATEGORY ASC);
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--CHANNEL_USERS
--IND_CHANNEL_USERS
BEGIN
DROP INDEX IF EXISTS  IND_CHANNEL_USERS;
CREATE INDEX IND_CHANNEL_USERS
ON CHANNEL_USERS USING btree (date_trunc('day',activated_on::timestamp)  ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_COMM_PRO_SET_ID
BEGIN
DROP INDEX IF EXISTS  IND_COMM_PRO_SET_ID;
CREATE INDEX IND_COMM_PRO_SET_ID
ON CHANNEL_USERS USING btree (COMM_PROFILE_SET_ID ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_TRANSFER_PROFILE_ID
BEGIN
DROP INDEX IF EXISTS  IND_TRANSFER_PROFILE_ID;
CREATE INDEX IND_TRANSFER_PROFILE_ID
ON CHANNEL_USERS USING btree (TRANSFER_PROFILE_ID ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_USERGRADE
BEGIN
DROP INDEX IF EXISTS  IND_USERGRADE;
CREATE INDEX IND_USERGRADE
ON CHANNEL_USERS USING btree (USER_GRADE ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--CHNL_TRANSFER_RULES
--IND_CAT
BEGIN
DROP INDEX IF EXISTS  IND_CAT;
CREATE INDEX IND_CAT
ON CHNL_TRANSFER_RULES USING btree (FROM_CATEGORY ASC, TO_CATEGORY ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_DOMAINCODE
BEGIN
DROP INDEX IF EXISTS  IND_DOMAINCODE;
CREATE INDEX IND_DOMAINCODE
ON CHNL_TRANSFER_RULES USING btree (DOMAIN_CODE ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_NTWCODE
BEGIN
DROP INDEX IF EXISTS  IND_NTWCODE;
CREATE INDEX IND_NTWCODE
ON CHNL_TRANSFER_RULES USING btree (NETWORK_CODE ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_TYPE
BEGIN
DROP INDEX IF EXISTS  IND_TYPE;
CREATE INDEX IND_TYPE
ON CHNL_TRANSFER_RULES USING btree (STATUS ASC, TYPE ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--CHNL_TRANSFER_RULES_HISTORY
--IND_CHNL_TRF_RULES_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_CHNL_TRF_RULES_HISTORY;
CREATE INDEX IND_CHNL_TRF_RULES_HISTORY
ON CHNL_TRANSFER_RULES_HISTORY USING btree (CREATED_ON ASC )
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--COMM_PROFILE_SERVICE_TYPES
--INDX_COMM_PROF_SERVICE_TYPE_ID
BEGIN
DROP INDEX IF EXISTS  INDX_COMM_PROF_SERVICE_TYPE_ID;
CREATE INDEX INDX_COMM_PROF_SERVICE_TYPE_ID
ON COMM_PROFILE_SERVICE_TYPES USING btree (COMM_PROFILE_SERVICE_TYPE_ID ASC )
TABLESPACE P_C2SINDEX ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--INDX_COMM_PROF_SET_ID
BEGIN
DROP INDEX IF EXISTS  INDX_COMM_PROF_SET_ID;
CREATE INDEX INDX_COMM_PROF_SET_ID
ON COMM_PROFILE_SERVICE_TYPES USING btree (COMM_PROFILE_SET_ID ASC )
TABLESPACE P_C2SINDEX ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--INDX_COMM_PROF_SET_VERSION
BEGIN
DROP INDEX IF EXISTS  INDX_COMM_PROF_SET_VERSION;
CREATE INDEX INDX_COMM_PROF_SET_VERSION
ON COMM_PROFILE_SERVICE_TYPES USING btree (COMM_PROFILE_SET_VERSION ASC )
TABLESPACE P_C2SINDEX ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--COMMISSION_PROFILE_DETAILS
--INDX_COMM_PROF_DETAILS
BEGIN
DROP INDEX IF EXISTS  INDX_COMM_PROF_DETAILS;
CREATE INDEX INDX_COMM_PROF_DETAILS
ON COMMISSION_PROFILE_DETAILS USING btree (COMM_PROFILE_PRODUCTS_ID ASC )
TABLESPACE P_C2SINDEX ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--COMMISSION_PROFILE_PRODUCTS
--COMM_PROFILE_SET_ID
BEGIN
DROP INDEX IF EXISTS  COMM_PROFILE_SET_ID;
CREATE INDEX COMM_PROFILE_SET_ID
ON COMMISSION_PROFILE_PRODUCTS USING btree (COMM_PROFILE_SET_ID ASC )
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--COMM_PROFILE_SET_VERSION
BEGIN
DROP INDEX IF EXISTS  COMM_PROFILE_SET_VERSION;
CREATE INDEX COMM_PROFILE_SET_VERSION
ON COMMISSION_PROFILE_PRODUCTS USING btree (COMM_PROFILE_SET_VERSION ASC )
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--COMMISSION_PROFILE_SET_VERSION
--IND_APPFROM
BEGIN
DROP INDEX IF EXISTS  IND_APPFROM;
CREATE INDEX IND_APPFROM
ON COMMISSION_PROFILE_SET_VERSION USING btree (APPLICABLE_FROM ASC )
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--CONTROL_PRF_HISTORY
--IND_CONTROL_PRF_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_CONTROL_PRF_HISTORY;
CREATE INDEX IND_CONTROL_PRF_HISTORY
ON CONTROL_PRF_HISTORY USING btree (CREATED_ON ASC )
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--DAILY_C2S_LMS_SUMMARY
--LMS_PRO_USER_ID
BEGIN
DROP INDEX IF EXISTS  LMS_PRO_USER_ID;
CREATE INDEX LMS_PRO_USER_ID
ON DAILY_C2S_LMS_SUMMARY USING btree (LMS_PROFILE ASC, USER_ID ASC )
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--GEOGRAPHICAL_DOMAINS
--IND_NWCODE
BEGIN
DROP INDEX IF EXISTS  IND_NWCODE;
CREATE INDEX IND_NWCODE
ON GEOGRAPHICAL_DOMAINS USING btree (NETWORK_CODE ASC )
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_STA
BEGIN
DROP INDEX IF EXISTS  IND_STA;
CREATE INDEX IND_STA
ON GEOGRAPHICAL_DOMAINS USING btree (STATUS ASC )
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--UK_GEODOMAIN_PARENTCODE
BEGIN
DROP INDEX IF EXISTS  UK_GEODOMAIN_PARENTCODE;
CREATE INDEX UK_GEODOMAIN_PARENTCODE
ON GEOGRAPHICAL_DOMAINS USING btree (PARENT_GRPH_DOMAIN_CODE ASC )
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--MSISDN_USAGE_SUMMARY
--IND_MSISDN_USAGE_SUMMARY
BEGIN
DROP INDEX IF EXISTS  IND_MSISDN_USAGE_SUMMARY;
CREATE INDEX IND_MSISDN_USAGE_SUMMARY
ON MSISDN_USAGE_SUMMARY USING btree (MSISDN ASC, MONTH_YEAR ASC )
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--NETWORK_PRF_HISTORY
--IND_NETWORK_PRF_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_NETWORK_PRF_HISTORY;
CREATE INDEX IND_NETWORK_PRF_HISTORY
ON NETWORK_PRF_HISTORY USING btree (CREATED_ON ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--NETWORK_PRODUCT_MAPPING
--IND_STATUS1
BEGIN
DROP INDEX IF EXISTS  IND_STATUS1;
CREATE INDEX IND_STATUS1
ON NETWORK_PRODUCT_MAPPING USING btree (STATUS ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--NETWORK_STOCK_TRANS_ITEMS
--INDX_NW_STK_TRANS_ITMS
BEGIN
DROP INDEX IF EXISTS  INDX_NW_STK_TRANS_ITMS;
CREATE INDEX INDX_NW_STK_TRANS_ITMS
ON NETWORK_STOCK_TRANS_ITEMS USING btree (TXN_NO ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--NETWORK_STOCKS
--IND_NW
BEGIN
DROP INDEX IF EXISTS  IND_NW;
CREATE INDEX IND_NW
ON NETWORK_STOCKS USING btree (NETWORK_CODE ASC, NETWORK_CODE_FOR ASC, WALLET_TYPE ASC, DAILY_STOCK_UPDATED_ON ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--NETWORKS_HISTORY
--IND_NETWORKS_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_NETWORKS_HISTORY;
CREATE INDEX IND_NETWORKS_HISTORY
ON NETWORKS_HISTORY USING btree (CREATED_ON ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--OTA_ADM_TRANSACTION
--IND_OTA_ADM_TRANSACTION
BEGIN
DROP INDEX IF EXISTS  IND_OTA_ADM_TRANSACTION;
CREATE INDEX IND_OTA_ADM_TRANSACTION
ON OTA_ADM_TRANSACTION USING btree (CREATED_ON ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--OTA_ADM_TRANSACTION_HISTORY
--IND_OTA_ADM_TRANS_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_OTA_ADM_TRANS_HISTORY;
CREATE INDEX IND_OTA_ADM_TRANS_HISTORY
ON OTA_ADM_TRANSACTION_HISTORY USING btree (CREATED_ON ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--P2P_BUDDIES_HISTORY
--IND_P2P_BUDDIES_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_P2P_BUDDIES_HISTORY;
CREATE INDEX IND_P2P_BUDDIES_HISTORY
ON P2P_BUDDIES_HISTORY USING btree (CREATED_ON ASC)
TABLESPACE PRTP_DATA ;
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--P2P_DAILY_FAILURE_DETAILS
--IND_P2P_DAILY_FAILURE_DETAILS
BEGIN
DROP INDEX IF EXISTS  IND_P2P_DAILY_FAILURE_DETAILS;
CREATE INDEX IND_P2P_DAILY_FAILURE_DETAILS
ON P2P_DAILY_FAILURE_DETAILS USING btree (TRANS_DATE ASC, SENDER_NETWORK_CODE ASC, RECEIVER_NETWORK_CODE ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--P2P_SUBSCRIBER_SUMMARY
--IND_P2P_SUBSCRIBER_SUMMARY
--DROP INDEX IF EXISTS  IND_P2P_SUBSCRIBER_SUMMARY;
--CREATE INDEX IND_P2P_SUBSCRIBER_SUMMARY
--ON P2P_SUBSCRIBER_SUMMARY USING btree (USER_ID ASC, MONTH ASC, MONTH_YEAR ASC, SENDER_MSISDN ASC)
--TABLESPACE PRTP_DATA ; 

--P2P_SUBSCRIBERS_HISTORY
--IND_P2P_SUBSCRIBERS_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_P2P_SUBSCRIBERS_HISTORY;
CREATE INDEX IND_P2P_SUBSCRIBERS_HISTORY
ON P2P_SUBSCRIBERS_HISTORY USING btree (CREATED_ON ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--IND_P2P_SUBSRIBERERS_MSISDN
BEGIN
DROP INDEX IF EXISTS  IND_P2P_SUBSRIBERERS_MSISDN;
CREATE INDEX IND_P2P_SUBSRIBERERS_MSISDN
ON P2P_SUBSCRIBERS_HISTORY USING btree (MSISDN ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--POS_KEY_HISTORY
--IND_POS_KEY_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_POS_KEY_HISTORY;
CREATE INDEX IND_POS_KEY_HISTORY
ON POS_KEY_HISTORY USING btree (CREATED_ON ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--POSTPAID_CUST_PAY_MASTER
--IND_POST_CUST_MASTER
BEGIN
DROP INDEX IF EXISTS  IND_POST_CUST_MASTER;
CREATE INDEX IND_POST_CUST_MASTER
ON POSTPAID_CUST_PAY_MASTER USING btree (TRANSFER_ID ASC,MSISDN ASC, STATUS ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--PROFILE_SET_VERSION
--INDX_PRF_SET_APPLICABLE
BEGIN
DROP INDEX IF EXISTS  INDX_PRF_SET_APPLICABLE;
CREATE INDEX INDX_PRF_SET_APPLICABLE
ON PROFILE_SET_VERSION USING btree (DATE_TRUNC('day',APPLICABLE_FROM::timestamp) ASC,DATE_TRUNC('day',APPLICABLE_TO::timestamp) ASC )
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--REG_INFO
--INDX_REG_INFO
BEGIN
DROP INDEX IF EXISTS  INDX_REG_INFO;
CREATE INDEX INDX_REG_INFO
ON REG_INFO USING btree (MSISDN  ASC )
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--REG_INFO_HISTORY
--IND_REG_INFO_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_REG_INFO_HISTORY;
CREATE INDEX IND_REG_INFO_HISTORY
ON REG_INFO_HISTORY USING btree (CREATED_ON  ASC )
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--SERVICE_CLASS_PRF_HISTORY
--IND_SERVICE_CLASS_PRF_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_SERVICE_CLASS_PRF_HISTORY;
CREATE INDEX IND_SERVICE_CLASS_PRF_HISTORY
ON SERVICE_CLASS_PRF_HISTORY USING btree (CREATED_ON  ASC )
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--SERVICE_CLASSES
--INDX_SERVICE_CLASS_CODE
BEGIN
DROP INDEX IF EXISTS  INDX_SERVICE_CLASS_CODE;
CREATE INDEX INDX_SERVICE_CLASS_CODE
ON SERVICE_CLASSES USING btree (SERVICE_CLASS_CODE  ASC )
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--SKEY_TRANSFER_HISTORY
--IND_SKEY_TRANSFER_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_SKEY_TRANSFER_HISTORY;
CREATE INDEX IND_SKEY_TRANSFER_HISTORY
ON SKEY_TRANSFER_HISTORY USING btree (CREATED_ON  ASC )
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--SUBSCRIBER_CONTROL
--SUB_CTL_MOD_MSISDN_TY_DT_IDX
BEGIN
DROP INDEX IF EXISTS  SUB_CTL_MOD_MSISDN_TY_DT_IDX;
CREATE INDEX SUB_CTL_MOD_MSISDN_TY_DT_IDX
ON SUBSCRIBER_CONTROL USING btree (MODULE  ASC , MSISDN ASC , SUBSCRIBER_TYPE ASC ,  CREATED_DATE ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--SUBSCRIBER_ROUTING_HISTORY
--IND_SUBSCRIBER_ROUTING_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_SUBSCRIBER_ROUTING_HISTORY;
CREATE INDEX IND_SUBSCRIBER_ROUTING_HISTORY
ON SUBSCRIBER_ROUTING_HISTORY USING btree ( CREATED_ON ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--SYSTEM_PRF_HISTORY
--IND_SYSTEM_PRF_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_SYSTEM_PRF_HISTORY;
CREATE INDEX IND_SYSTEM_PRF_HISTORY
ON SYSTEM_PRF_HISTORY USING btree ( CREATED_ON ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--TEMP_DAILY_CHNL_TRANS_MAIN
--IND_TEMP_DAILY_CHNL_TRANS_MAIN
BEGIN
DROP INDEX IF EXISTS  IND_TEMP_DAILY_CHNL_TRANS_MAIN;
CREATE INDEX IND_TEMP_DAILY_CHNL_TRANS_MAIN
ON TEMP_DAILY_CHNL_TRANS_MAIN USING btree ( USER_ID ASC, NETWORK_CODE ASC,NETWORK_CODE_FOR ASC, PRODUCT_CODE ASC, GRPH_DOMAIN_CODE ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--TRANSFER_PROFILE
--INDEX_CATEGORY_CODE 
BEGIN
DROP INDEX IF EXISTS  INDEX_CATEGORY_CODE;
CREATE INDEX INDEX_CATEGORY_CODE
ON TRANSFER_PROFILE USING btree ( CATEGORY_CODE ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--INDEX_NETWORK_CODE
BEGIN
DROP INDEX IF EXISTS  INDEX_NETWORK_CODE;
CREATE INDEX INDEX_NETWORK_CODE
ON TRANSFER_PROFILE USING btree ( NETWORK_CODE ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--INDEX_PRNT_PROFILE_ID
BEGIN
DROP INDEX IF EXISTS  INDEX_PRNT_PROFILE_ID;
CREATE INDEX INDEX_PRNT_PROFILE_ID
ON TRANSFER_PROFILE USING btree ( PARENT_PROFILE_ID ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--INDEX_STATUS
BEGIN
DROP INDEX IF EXISTS  INDEX_STATUS;
CREATE INDEX INDEX_STATUS
ON TRANSFER_PROFILE USING btree ( STATUS ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--TRANSFER_RULES_HISTORY
--IND_TRANSFER_RULES_HISTORY
BEGIN
DROP INDEX IF EXISTS  IND_TRANSFER_RULES_HISTORY;
CREATE INDEX IND_TRANSFER_RULES_HISTORY
ON TRANSFER_RULES_HISTORY USING btree ( CREATED_ON ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--USER_BALANCES
--IND_DAILYUPDATED
BEGIN
DROP INDEX IF EXISTS  IND_DAILYUPDATED;
CREATE INDEX IND_DAILYUPDATED
ON USER_BALANCES USING btree ( DAILY_BALANCE_UPDATED_ON ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;



--USER_OTP
--INDEX_MSISDN
BEGIN
DROP INDEX IF EXISTS  INDEX_MSISDN;
CREATE INDEX INDEX_MSISDN
ON USER_OTP USING btree ( MSISDN ASC)
TABLESPACE PRTP_DATA_1 ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--USER_THRESHOLD_COUNTER
--IND_THRESHOLD_COUNT_CAT_CODE
BEGIN
DROP INDEX IF EXISTS  IND_THRESHOLD_COUNT_CAT_CODE;
CREATE INDEX IND_THRESHOLD_COUNT_CAT_CODE
ON USER_THRESHOLD_COUNTER USING btree ( CATEGORY_CODE ASC)
TABLESPACE PRTP_DATA_1 ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--IND_THRESHOLD_COUNT_ENTRY_DATE
BEGIN
DROP INDEX IF EXISTS  IND_THRESHOLD_COUNT_ENTRY_DATE;
CREATE INDEX IND_THRESHOLD_COUNT_ENTRY_DATE
ON USER_THRESHOLD_COUNTER USING btree ( ENTRY_DATE ASC)
TABLESPACE PRTP_DATA_1 ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--IND_THRESHOLD_COUNT_TRNS_ID
BEGIN
DROP INDEX IF EXISTS  IND_THRESHOLD_COUNT_TRNS_ID;
CREATE INDEX IND_THRESHOLD_COUNT_TRNS_ID
ON USER_THRESHOLD_COUNTER USING btree ( TRANSFER_ID ASC)
TABLESPACE PRTP_DATA_1 ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--THRESHOLD_COUNT_USER
BEGIN
DROP INDEX IF EXISTS  THRESHOLD_COUNT_USER;
CREATE INDEX THRESHOLD_COUNT_USER
ON USER_THRESHOLD_COUNTER USING btree ( USER_ID ASC)
TABLESPACE PRTP_DATA_1 ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--USER_TRANSFER_COUNTS
--IND_USER_TRANSFER_COUNTS
BEGIN
DROP INDEX IF EXISTS  IND_USER_TRANSFER_COUNTS;
CREATE INDEX IND_USER_TRANSFER_COUNTS
ON USER_TRANSFER_COUNTS USING btree ( DATE_TRUNC('day',LAST_OUT_TIME::timestamp) ASC)
TABLESPACE PRTP_DATA_1 ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--USERS
--IND_CATEGORY_CODE
BEGIN
DROP INDEX IF EXISTS  IND_CATEGORY_CODE;
CREATE INDEX IND_CATEGORY_CODE
ON USERS USING btree ( CATEGORY_CODE ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--IDX_EXTERNAL_CODE
BEGIN
DROP INDEX IF EXISTS  IDX_EXTERNAL_CODE;
CREATE INDEX IDX_EXTERNAL_CODE
ON USERS USING btree ( EXTERNAL_CODE ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--IDX_MSISDN
BEGIN
DROP INDEX IF EXISTS  IDX_MSISDN;
CREATE INDEX IDX_MSISDN
ON USERS USING btree ( MSISDN ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;



--IND_NWCODE1
BEGIN
DROP INDEX IF EXISTS  IND_NWCODE1;
CREATE INDEX IND_NWCODE1
ON USERS USING btree ( NETWORK_CODE ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--IND_USERS2
BEGIN
DROP INDEX IF EXISTS  IND_USERS2;
CREATE INDEX IND_USERS2
ON USERS USING btree ( PARENT_ID ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--IND_STATUS
BEGIN
DROP INDEX IF EXISTS  IND_STATUS;
CREATE INDEX IND_STATUS
ON USERS USING btree ( STATUS ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--IND_USERS1
BEGIN
DROP INDEX IF EXISTS  IND_USERS1;
CREATE INDEX IND_USERS1
ON USERS USING btree ( UPPER(LOGIN_ID) ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--IND_USER_CODE
BEGIN
DROP INDEX IF EXISTS  IND_USER_CODE;
CREATE INDEX IND_USER_CODE
ON USERS USING btree ( USER_CODE ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--INDX_USERS_UNAME
BEGIN
DROP INDEX IF EXISTS  INDX_USERS_UNAME;
CREATE INDEX INDX_USERS_UNAME
ON USERS USING btree ( USER_NAME ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--VAS_BATCH_HISTORY
--NK_VAS_BATCH_ID
BEGIN
DROP INDEX IF EXISTS  NK_VAS_BATCH_ID;
CREATE INDEX NK_VAS_BATCH_ID
ON VAS_BATCH_HISTORY USING btree ( VAS_BATCH_ID ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--VAS_CATEGORIES
--UK_PARENT_VAS_CATEGORY_ID
BEGIN
DROP INDEX IF EXISTS  UK_PARENT_VAS_CATEGORY_ID;
CREATE INDEX UK_PARENT_VAS_CATEGORY_ID
ON VAS_CATEGORIES USING btree ( PARENT_VAS_CATEGORY_ID ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--VAS_CIRCLE_ITEM_PRICES
--NK_VAS_CIRCLE_CODE
BEGIN
DROP INDEX IF EXISTS  NK_VAS_CIRCLE_CODE;
CREATE INDEX NK_VAS_CIRCLE_CODE
ON VAS_CIRCLE_ITEM_PRICES USING btree ( VAS_CIRCLE_CODE ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;


--VAS_ITEMS
--UK_VAS_CATEGORY_ID
BEGIN
DROP INDEX IF EXISTS  UK_VAS_CATEGORY_ID;
CREATE INDEX UK_VAS_CATEGORY_ID
ON VAS_ITEMS USING btree ( VAS_CATEGORY_ID ASC)
TABLESPACE PRTP_DATA ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--VOMS_PRINT_BATCHES
--INX_VOMS_PRINT_BATCHES
BEGIN
DROP INDEX IF EXISTS  INX_VOMS_PRINT_BATCHES;
CREATE INDEX INX_VOMS_PRINT_BATCHES
ON VOMS_PRINT_BATCHES USING btree ( USER_ID ASC)
TABLESPACE VOMS_INDEXES ; 
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE  'Exception while creating index:  %', SQLERRM;
END;

--VOMS_VOUCHERS
--IND_VOMS_CURR_STATUS
--DROP INDEX IF EXISTS  IND_VOMS_CURR_STATUS;
--CREATE INDEX IND_VOMS_CURR_STATUS
--ON VOMS_VOUCHERS USING btree ( CURRENT_STATUS ASC)
--TABLESPACE VOMS_INDEXES ; 

--IND_VOMS_PIN
--DROP INDEX IF EXISTS  IND_VOMS_PIN;
--CREATE INDEX IND_VOMS_PIN
--ON VOMS_VOUCHERS USING btree ( PIN_NO ASC)
--TABLESPACE VOMS_INDEXES ; 

--IND_VOMS_PRODUCT
--DROP INDEX IF EXISTS  IND_VOMS_PRODUCT;-
--CREATE INDEX IND_VOMS_PRODUCT
--ON VOMS_VOUCHERS USING btree ( PRODUCT_ID ASC)
--TABLESPACE VOMS_INDEXES ; 

--IND_VOMS_SEQ
--DROP INDEX IF EXISTS  IND_VOMS_SEQ;
--CREATE INDEX IND_VOMS_SEQ
--ON VOMS_VOUCHERS USING btree ( SEQUENCE_ID ASC)
--TABLESPACE VOMS_INDEXES ; 

RAISE NOTICE  'Indexes created successfully for pretups database';
RETURN_MESSAGE := 'Indexes created successfully for pretups database';
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE 'EXCEPTION while creating indexes %' , SQLERRM;
RETURN_MESSAGE := 'EXCEPTION: %'||SQLERRM;
END;
$$;


ALTER FUNCTION pretupsdatabase.create_pretups_indexes(OUT return_message character varying) OWNER TO pgdb;

--
-- Name: drop_old_msisdn_columns(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION drop_old_msisdn_columns() RETURNS void
    LANGUAGE plpgsql
    AS $$
 declare 
exec_drop varchar(400);
exec_stmt varchar(400);
cmt_frqncy int;
prv_tbl_name varchar(30);
 table_column_names cursor is select table_name,column_names from migrate_msisdn order by 1;
 table_list cursor is select distinct table_name from migrate_msisdn order by 1;

begin

--open table_column_names;
--open table_list;
cmt_frqncy:=10000;
prv_tbl_name:='';
for clmn in table_column_names
loop

--loop
--exec_stmt:='update '|| clmn.table_name ||' set old_'|| clmn.column_names
--||'=null, msisdn_modified=null where msisdn_modified=:1 and rownum<='||cmt_frqncy;
--DBMS_OUTPUT.put_line('executing stmt : '|| exec_stmt);
--execute immediate exec_stmt using 'Y';
--exit when sql%rowcount=0;
--commit;
--end loop;

exec_drop:='alter table '|| clmn.table_name || ' drop column old_' || clmn.column_names;
RAISE NOTICE '%','executing stmt : '|| exec_drop;
execute immediate exec_drop;
end loop;

for tbl in table_list
loop
	RAISE NOTICE '%','executing stmt : '|| 'alter table '|| tbl.table_name || ' drop column msisdn_modified';
	execute immediate 'alter table '|| tbl.table_name || ' drop column msisdn_modified';
end loop;

--close table_list;
--close table_column_names;
end;
$$;


ALTER FUNCTION pretupsdatabase.drop_old_msisdn_columns() OWNER TO pgdb;

--
-- Name: dump_trans_summary(character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION dump_trans_summary(start_date_time character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare 
   err_val                varchar (2000);
   check_flag             smallint ;
   check_flag2              smallint ;
   stdate	timestamp without time zone;

begin
-- thrown if the proc has already been executed already, and the process termnates
   select count (*)
     into check_flag
     from process_status p
    where executed_upto = to_date(start_date_time,'dd/mm/yy')
      and  p.process_id = 'RUNTRNSUM'
      and scheduler_status = 'C';
    
     select count (*)
     into check_flag2
     from c2s_transfers c
     where transfer_date = to_date(start_date_time,'dd/mm/yy')
     and transfer_status in ('250','205');


       raise notice '%',
                              'the value of check flag' ||check_flag ;

                                 raise notice '%',
                              'the value of check flag2' ||check_flag2 ;
   if check_flag <> 0 
   then
                  insert into proc_error_log
                              (desc1
                              )
                       values (   'RUNTRNSUM :run_duplicate  at time :-  '|| to_char (current_timestamp, 'ddmmrrrrhhmmss') || start_date_time|| current_timestamp || 'f'
                              );

                  raise notice '%',
                              'the procedure is executed several times for the same date';
                  RAISE EXCEPTION 'alreadydoneexception:the procedure is executed several times for the same date'; 
                  
                  /* commit; */
                 
               
           elsif check_flag2 <>0
               then
               
                  insert into proc_error_log
                              (desc1
                              )
                       values (   'RUNTRNSUM : the settelment process not run yet'
                               || to_char (current_timestamp, 'ddmmrrrrhhmmss')
                               || start_date_time
                               || current_timestamp
                               || 'f'
                              );

                  raise notice '%',
                              'the settelment process not run yet';
                   RAISE EXCEPTION 'setelmentnotdoneexception:the settelment process not run yet';           
           
                  /* commit; */
                  
                  
               
    else 
	begin
	 raise notice '%','entered in block' ;
	stdate=to_date(start_date_time,'dd/mm/yy');
      insert into transaction_summary
         select nextval('transsumm_id'), x.* from (select   transfer_date, cast (to_char (transfer_date_time, 'hh24') as int),
                          c.network_code, c.interface_id, c.sender_category,
                          c.service_type, c.sub_service,
                          request_gateway_code g_c,
                          sum (case  transfer_status  when '200' then  1  else 0 end
                              ) success_count,
                          sum (case  transfer_status  when '200' then  0  when '250' then  0  else 1 end
                              ) error_count,
                          sum (case  transfer_status
                                        when '200' then  c.transfer_value
                                        else 0
                                       end
                              ) success_amt,
                          sum (case  transfer_status
                                        when '200' then  0
                                        when '250' then  0
                                        else c.transfer_value
                                       end
                              ) error_amt,
                          sum (receiver_tax1_value + receiver_tax2_value
                              ) tax_amt,
                          sum (c.receiver_access_fee) access_fee,
                          sum (c.receiver_transfer_value) as rec
                     from c2s_transfers c
                    where transfer_date = stdate
                   
                 group by transfer_date,
                          c.network_code,
                          c.interface_id,
                          c.service_type,
                          c.sub_service,
                          to_char (transfer_date_time, 'hh24'),
                          request_gateway_code,
                          c.sender_category
                          ) x;

		 raise notice '%','insert 1' ;
                             
              /*  IF NOT FOUND 
		THEN
           
              RAISE NOTICE '%; SQLSTATE1:SQL Exception in INSERT ',  SQLSTATE;
		RAISE EXCEPTION 'SQL Exception in INSERT ' ;
               END IF;
            EXCEPTION
               
               WHEN OTHERS
               THEN
              RAISE NOTICE '%; SQLSTATE2:SQL Exception in INSERT ', SQLSTATE;
              RAISE EXCEPTION 'SQL Exception in INSERT';*/
                             
             end;                 
      /* commit; */
   end if;
exception
   when no_data_found
   then

      insert into proc_error_log
           values ('insert_into_trans-exception no_data_found');

      /* commit; */
   when others
   then

      insert into proc_error_log
           values ('insert_into_trans-exception in  others');

      /* commit; */
end;
$$;


ALTER FUNCTION pretupsdatabase.dump_trans_summary(start_date_time character varying) OWNER TO pgdb;

--
-- Name: enable_voucher_count(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION enable_voucher_count() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
      
DECLARE

V_RECORD_EXISTS SMALLINT;
BEGIN  
     IF (OLD.CURRENT_STATUS = 'EN' AND NEW.CURRENT_STATUS <> 'CU' AND NEW.CURRENT_STATUS <> 'UP' AND OLD.CURRENT_STATUS <> NEW.CURRENT_STATUS  ) THEN

              UPDATE VOMS_ENABLE_SUMMARY SET VOUCHER_COUNT = VOUCHER_COUNT - 1 WHERE PRODUCT_ID = OLD.PRODUCT_ID
                               AND CREATED_DATE  =  OLD.CREATED_DATE AND EXPIRY_DATE =  OLD.EXPIRY_DATE;
      END IF;

     IF (NEW.CURRENT_STATUS = 'EN' AND OLD.CURRENT_STATUS <> NEW.CURRENT_STATUS  AND OLD.CURRENT_STATUS<> 'CU' AND OLD.CURRENT_STATUS<>'UP' ) THEN
         SELECT COUNT(1) INTO V_RECORD_EXISTS FROM VOMS_ENABLE_SUMMARY WHERE PRODUCT_ID = OLD.PRODUCT_ID
                               AND CREATED_DATE  =  OLD.CREATED_DATE AND EXPIRY_DATE =  OLD.EXPIRY_DATE;
         IF V_RECORD_EXISTS = 1 THEN
                  UPDATE VOMS_ENABLE_SUMMARY SET VOUCHER_COUNT = VOUCHER_COUNT + 1 WHERE PRODUCT_ID = OLD.PRODUCT_ID
                              AND CREATED_DATE  =  OLD.CREATED_DATE  AND EXPIRY_DATE =  OLD.EXPIRY_DATE;
         ELSE
               INSERT INTO VOMS_ENABLE_SUMMARY (
              PRODUCT_ID, CREATED_DATE, VOUCHER_COUNT,
              EXPIRY_DATE) VALUES ( OLD.PRODUCT_ID, OLD.CREATED_DATE , 1 , OLD.EXPIRY_DATE);
         END IF;

     END IF;
	RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.enable_voucher_count() OWNER TO pgdb;

--
-- Name: evaluate_new_msisdn(character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION evaluate_new_msisdn(p_msisdn character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
 declare
  prefix3 varchar(3);
  prefix4 varchar(4);
  newmsisdn varchar(20);
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
$$;


ALTER FUNCTION pretupsdatabase.evaluate_new_msisdn(p_msisdn character varying) OWNER TO pgdb;

--
-- Name: function_adjustments_duplicate(character varying, character varying, character varying, character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION function_adjustments_duplicate(table_name character varying, partition_key character varying, table_space character varying, date_from character varying, date_to character varying, table_ext character varying, primary_key character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$

declare
seq integer;

BEGIN

--EXECUTE 'CREATE TRIGGER '||table_name||'_partition_trigger BEFORE INSERT ON '||table_name||' FOR EACH ROW EXECUTE PROCEDURE Partition_table_insert_trigger('||table_name||','||partition_key||');';

                                EXECUTE 'CREATE TABLE ' || table_name||'_' || table_ext ||  '(LIKE ' || table_name||'   INCLUDING ALL) INHERITS('|| table_name||') tablespace '||table_space||';';

                                EXECUTE 'ALTER TABLE '|| table_name||'_' || table_ext || ' ADD CONSTRAINT ' || table_name||'_' || table_ext || ' CHECK ( '||partition_key||' >=  DATE '||quote_literal(date_from)||' AND '||partition_key||' < DATE '||quote_literal(date_to)||');';

                                EXECUTE 'CREATE TRIGGER '||table_name||'_updatepartition_trigger BEFORE UPDATE ON ' ||  table_name||'_' || table_ext || ' FOR EACH ROW EXECUTE PROCEDURE partition_table_update_trigger('||table_name||'_' || table_ext ||','||table_name||','||partition_key||','||primary_key||');';

END

$$;


ALTER FUNCTION pretupsdatabase.function_adjustments_duplicate(table_name character varying, partition_key character varying, table_space character varying, date_from character varying, date_to character varying, table_ext character varying, primary_key character varying) OWNER TO pgdb;

--
-- Name: function_create_halfmonth_partition_adjustment(character varying, character varying, character varying, character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION function_create_halfmonth_partition_adjustment(table_name character varying, partition_key character varying, table_space character varying, date_from character varying, date_to character varying, table_ext character varying, primary_key character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$

declare
seq integer;

BEGIN

--EXECUTE 'CREATE TRIGGER '||table_name||'_partition_trigger BEFORE INSERT ON '||table_name||' FOR EACH ROW EXECUTE PROCEDURE Partition_table_insert_trigger('||table_name||','||partition_key||');';

                                EXECUTE 'CREATE TABLE ' || table_name||'_' || table_ext ||  '(LIKE ' || table_name||'   INCLUDING ALL) INHERITS('|| table_name||') tablespace '||table_space||';';

                                EXECUTE 'ALTER TABLE '|| table_name||'_' || table_ext || ' ADD CONSTRAINT ' || table_name||'_' || table_ext || ' CHECK ( '||partition_key||' >=  DATE '||quote_literal(date_from)||' AND '||partition_key||' < DATE '||quote_literal(date_to)||');';

                                EXECUTE 'CREATE TRIGGER '||table_name||'_updatepartition_trigger BEFORE UPDATE ON ' ||  table_name||'_' || table_ext || ' FOR EACH ROW EXECUTE PROCEDURE partition_table_update_trigger('||table_name||'_' || table_ext ||','||table_name||','||partition_key||','||primary_key||');';

END

$$;


ALTER FUNCTION pretupsdatabase.function_create_halfmonth_partition_adjustment(table_name character varying, partition_key character varying, table_space character varying, date_from character varying, date_to character varying, table_ext character varying, primary_key character varying) OWNER TO pgdb;

--
-- Name: function_create_halfmonth_partition_c2s_transfer(character varying, character varying, character varying, character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION function_create_halfmonth_partition_c2s_transfer(table_name character varying, partition_key character varying, table_space character varying, date_from character varying, date_to character varying, table_ext character varying, primary_key character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$

declare
seq integer;

BEGIN

--EXECUTE 'CREATE TRIGGER '||table_name||'_partition_trigger BEFORE INSERT ON '||table_name||' FOR EACH ROW EXECUTE PROCEDURE Partition_table_insert_trigger('||table_name||','||partition_key||');';

                                EXECUTE 'CREATE TABLE ' || table_name||'_' || table_ext ||  '(LIKE ' || table_name||'   INCLUDING ALL) INHERITS('|| table_name||') tablespace '||table_space||';';

                                EXECUTE 'ALTER TABLE '|| table_name||'_' || table_ext || ' ADD CONSTRAINT ' || table_name||'_' || table_ext || ' CHECK ( '||partition_key||' >=  DATE '||quote_literal(date_from)||' AND '||partition_key||' < DATE '||quote_literal(date_to)||');';

                                EXECUTE 'CREATE TRIGGER '||table_name||'_updatepartition_trigger BEFORE UPDATE ON ' ||  table_name||'_' || table_ext || ' FOR EACH ROW EXECUTE PROCEDURE partition_table_update_trigger('||table_name||'_' || table_ext ||','||table_name||','||partition_key||','||primary_key||');';
END

$$;


ALTER FUNCTION pretupsdatabase.function_create_halfmonth_partition_c2s_transfer(table_name character varying, partition_key character varying, table_space character varying, date_from character varying, date_to character varying, table_ext character varying, primary_key character varying) OWNER TO pgdb;

--
-- Name: function_create_halfmonth_partition_channel_transfer(character varying, character varying, character varying, character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION function_create_halfmonth_partition_channel_transfer(table_name character varying, partition_key character varying, table_space character varying, date_from character varying, date_to character varying, table_ext character varying, primary_key character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$

declare

seq integer;

BEGIN
    
	-- EXECUTE 'CREATE TRIGGER '||table_name||'_partition_trigger BEFORE INSERT ON '||table_name||' FOR EACH ROW EXECUTE PROCEDURE Partition_table_insert_trigger('||table_name||','||partition_key||');';

								EXECUTE 'CREATE TABLE ' || table_name||'_' || table_ext ||  '(LIKE ' || table_name||'   INCLUDING ALL) INHERITS('|| table_name||') tablespace '||table_space||';';

                                EXECUTE 'ALTER TABLE '|| table_name||'_' || table_ext || ' ADD CONSTRAINT ' || table_name||'_' || table_ext || ' CHECK ( '||partition_key||' >=  DATE '||quote_literal(date_from)||' AND '||partition_key||' < DATE '||quote_literal(date_to)||');';

                                EXECUTE 'CREATE TRIGGER '||table_name||'_updatepartition_trigger BEFORE UPDATE ON ' ||  table_name||'_' || table_ext || ' FOR EACH ROW EXECUTE PROCEDURE partition_table_update_trigger('||table_name||'_' || table_ext ||','||table_name||','||partition_key||','||primary_key||');';

END

$$;


ALTER FUNCTION pretupsdatabase.function_create_halfmonth_partition_channel_transfer(table_name character varying, partition_key character varying, table_space character varying, date_from character varying, date_to character varying, table_ext character varying, primary_key character varying) OWNER TO pgdb;

--
-- Name: function_create_halfmonth_partition_channel_transfer_items(character varying, character varying, character varying, character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION function_create_halfmonth_partition_channel_transfer_items(table_name character varying, partition_key character varying, table_space character varying, date_from character varying, date_to character varying, table_ext character varying, primary_key character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$

declare
seq integer;

BEGIN
            --	EXECUTE 'CREATE TRIGGER '||table_name||'_partition_trigger BEFORE INSERT ON '||table_name||' FOR EACH ROW EXECUTE PROCEDURE Partition_table_insert_trigger('||table_name||','||partition_key||');';
                    
								EXECUTE 'CREATE TABLE ' || table_name||'_' || table_ext ||  '(LIKE ' || table_name||'   INCLUDING ALL) INHERITS('|| table_name||') tablespace '||table_space||';';

                                EXECUTE 'ALTER TABLE '|| table_name||'_' || table_ext || ' ADD CONSTRAINT ' || table_name||'_' || table_ext || ' CHECK ( '||partition_key||' >=  DATE '||quote_literal(date_from)||' AND '||partition_key||' < DATE '||quote_literal(date_to)||');';

                                EXECUTE 'CREATE TRIGGER '||table_name||'_updatepartition_trigger BEFORE UPDATE ON ' ||  table_name||'_' || table_ext || ' FOR EACH ROW EXECUTE PROCEDURE partition_table_update_trigger('||table_name||'_' || table_ext ||','||table_name||','||partition_key||','||primary_key||');';

END
$$;


ALTER FUNCTION pretupsdatabase.function_create_halfmonth_partition_channel_transfer_items(table_name character varying, partition_key character varying, table_space character varying, date_from character varying, date_to character varying, table_ext character varying, primary_key character varying) OWNER TO pgdb;

--
-- Name: function_create_halfmonth_partition_user_balance_history(character varying, character varying, character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION function_create_halfmonth_partition_user_balance_history(table_name character varying, partition_key character varying, table_space character varying, date_from character varying, date_to character varying, table_ext character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$

declare
seq integer;
BEGIN
       --      EXECUTE 'CREATE TRIGGER '||table_name||'_partition_trigger BEFORE INSERT ON '||table_name||' FOR EACH ROW EXECUTE PROCEDURE Partition_table_insert_trigger('||table_name||','||partition_key||');';
 
								EXECUTE 'CREATE TABLE ' || table_name||'_' || table_ext ||  '(LIKE ' || table_name||'   INCLUDING ALL) INHERITS('|| table_name||') tablespace '||table_space||';';

                                EXECUTE 'ALTER TABLE '|| table_name||'_' || table_ext || ' ADD CONSTRAINT ' || table_name||'_' || table_ext || ' CHECK ( '||partition_key||' >=  DATE '||quote_literal(date_from)||' AND '||partition_key||' < DATE '||quote_literal(date_to)||');';

                                EXECUTE 'CREATE TRIGGER '||table_name||'_updatepartition_trigger BEFORE UPDATE ON ' ||  table_name||'_' || table_ext || ' FOR EACH ROW EXECUTE PROCEDURE user_balances_history_update_trigger('||table_name||'_' || table_ext ||','||table_name||','||partition_key||');';

END

$$;


ALTER FUNCTION pretupsdatabase.function_create_halfmonth_partition_user_balance_history(table_name character varying, partition_key character varying, table_space character varying, date_from character varying, date_to character varying, table_ext character varying) OWNER TO pgdb;

--
-- Name: function_create_halfmonth_partition_user_daily_balance(character varying, character varying, character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION function_create_halfmonth_partition_user_daily_balance(table_name character varying, partition_key character varying, table_space character varying, date_from character varying, date_to character varying, table_ext character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$

declare

seq integer;

BEGIN

    --         EXECUTE 'CREATE TRIGGER '||table_name||'_partition_trigger BEFORE INSERT ON '||table_name||' FOR EACH ROW EXECUTE PROCEDURE Partition_table_insert_trigger('||table_name||','||partition_key||');';


                                EXECUTE 'CREATE TABLE ' || table_name||'_' || table_ext ||  '(LIKE ' || table_name||'   INCLUDING ALL) INHERITS('|| table_name||') tablespace '||table_space||';';

                                EXECUTE 'ALTER TABLE '|| table_name||'_' || table_ext || ' ADD CONSTRAINT ' || table_name||'_' || table_ext || ' CHECK ( '||partition_key||' >=  DATE '||quote_literal(date_from)||' AND '||partition_key||' < DATE '||quote_literal(date_to)||');';

                                EXECUTE 'CREATE TRIGGER '||table_name||'_updatepartition_trigger BEFORE UPDATE ON ' ||  table_name||'_' || table_ext || ' FOR EACH ROW EXECUTE PROCEDURE user_daily_balances_update_trigger('||table_name||'_' || table_ext ||','||table_name||','||partition_key||');';



END

$$;


ALTER FUNCTION pretupsdatabase.function_create_halfmonth_partition_user_daily_balance(table_name character varying, partition_key character varying, table_space character varying, date_from character varying, date_to character varying, table_ext character varying) OWNER TO pgdb;

--
-- Name: getaccountinformation(character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION getaccountinformation(p_msisdn character varying, p_transactionnumber character varying, OUT p_status character varying, OUT p_transactionnumberout character varying, OUT p_serviceclass character varying, OUT p_accountid character varying, OUT p_accountstatus character varying, OUT p_creditlimit character varying, OUT p_languageid character varying, OUT p_imsi character varying, OUT p_balance character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
	BEGIN
--	select ((to_char(sysdate,'mi')*60)+ to_char(sysdate,'ss')) into OldTime from dual;
	p_status :='0';
	p_transactionNumberOut:=p_transactionNumber;
	p_serviceClass :='123';
	p_accountId :='AA12345';
	p_accountStatus:= 'ACTIVE';
	p_creditLimit:='10000';
	p_languageId :='1';
	p_Imsi :='123456789123456';
	p_balance :='500000';
	--dbms_lock.sleep(60);
 --dbms_output.put_line('aa');
--loop
--  select ((to_char(sysdate,'mi')*60)+ to_char(sysdate,'ss')) into NewTime from dual;
--   if (newtime-oldtime>60) then
--   	  dbms_output.put_line('aaaa');
--   end if;
--end loop;
	END;
$$;


ALTER FUNCTION pretupsdatabase.getaccountinformation(p_msisdn character varying, p_transactionnumber character varying, OUT p_status character varying, OUT p_transactionnumberout character varying, OUT p_serviceclass character varying, OUT p_accountid character varying, OUT p_accountstatus character varying, OUT p_creditlimit character varying, OUT p_languageid character varying, OUT p_imsi character varying, OUT p_balance character varying) OWNER TO pgdb;

--
-- Name: getproduct(character varying, integer); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION getproduct(p_producttype character varying, p_amount integer) RETURNS character varying
    LANGUAGE plpgsql
    AS $$ 
DECLARE
product varchar(15);
 c_product CURSOR(p_productType varchar)  IS
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
$$;


ALTER FUNCTION pretupsdatabase.getproduct(p_producttype character varying, p_amount integer) OWNER TO pgdb;

--
-- Name: getuserroles(character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION getuserroles(p_userid character varying, p_rolename character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
declare 
p_userRoles VARCHAR(32767) DEFAULT '';
role_name_code varchar(100) DEFAULT '';
role_type varchar(1);
group_role varchar(1) DEFAULT '';
oldGroupName varchar(100)DEFAULT '';
newGroupName varchar(100)DEFAULT '';
var integer DEFAULT 0;
 c_userRoles CURSOR(p_userId varchar) IS
            SELECT ur.user_id,trim(r.role_name)role_name,r.GROUP_ROLE,m.MODULE_NAME,r.ROLE_CODE
            FROM USER_ROLES ur, ROLES r,PAGE_ROLES pr,PAGES p,MODULES m,CATEGORIES c, USERS u, DOMAINS d
            WHERE r.role_code=ur.role_code
            AND ur.user_id=p_userId
            AND r.status='Y'
            AND r.ROLE_CODE=pr.ROLE_CODE
            AND pr.PAGE_CODE=p.PAGE_CODE
            AND p.MENU_LEVEL::integer=1
            AND p.MODULE_CODE=m.MODULE_CODE
            AND d.DOMAIN_TYPE_CODE=r.DOMAIN_TYPE
            AND ur.USER_ID=u.USER_ID
            AND u.CATEGORY_CODE=c.CATEGORY_CODE
            AND c.DOMAIN_CODE=d.DOMAIN_CODE
            ORDER BY m.MODULE_NAME,r.role_name;
 c_userGroupRoles CURSOR(p_userId varchar) IS
            SELECT ur.user_id,trim(r.role_name)role_name ,r.GROUP_ROLE,m.MODULE_NAME,r.ROLE_CODE
            FROM USER_ROLES ur, ROLES r,GROUP_ROLES gr,PAGE_ROLES pr,PAGES p,MODULES m,CATEGORIES c, USERS u, DOMAINS d
            WHERE ur.ROLE_CODE=gr.GROUP_ROLE_CODE
            AND gr.ROLE_CODE=r.ROLE_CODE
            AND ur.user_id=p_userId
            AND r.status='Y'
            AND r.ROLE_CODE=pr.ROLE_CODE
            AND pr.PAGE_CODE=p.PAGE_CODE
            AND p.MENU_LEVEL::integer=1
            AND p.MODULE_CODE=m.MODULE_CODE
            AND d.DOMAIN_TYPE_CODE=r.DOMAIN_TYPE
            AND ur.USER_ID=u.USER_ID
            AND u.CATEGORY_CODE=c.CATEGORY_CODE
            AND c.DOMAIN_CODE=d.DOMAIN_CODE
            ORDER BY m.MODULE_NAME,r.role_name;

 c_userFixRoles CURSOR (p_userId varchar) IS
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
            AND p.MENU_LEVEL::integer=1
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
         p_userRoles:=substring(p_userRoles,1,LENGTH(p_userRoles)-2);
      END IF;
      --p_userRoles:='<div align="left" style="white-space: 0; letter-spacing: 0; " >'||p_userRoles||'</div>';

      RETURN p_userRoles;
END;
$$;


ALTER FUNCTION pretupsdatabase.getuserroles(p_userid character varying, p_rolename character varying) OWNER TO pgdb;

--
-- Name: getuserrolestype(character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION getuserrolestype(p_userid character varying, p_nameorcode character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
declare 
p_userRolesType VARCHAR(20);
roleTypeNameOrCode VARCHAR(5);
counter integer:=0;
 c_userRolesType CURSOR (p_userId VARCHAR) IS
	   SELECT (CASE r.group_role WHEN 'Y' THEN 'group role' WHEN 'N' THEN 'system role' END) roleType
	   FROM ROLES r,USER_ROLES ur,USERS u
	   WHERE r.ROLE_CODE=ur.ROLE_CODE
	   AND ur.USER_ID=p_userId;
	   --AND u.USER_ID=p_userId;
 c_userFixRolesType CURSOR(p_userId VARCHAR) IS
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
$$;


ALTER FUNCTION pretupsdatabase.getuserrolestype(p_userid character varying, p_nameorcode character varying) OWNER TO pgdb;

--
-- Name: iatdwhtempprc(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION iatdwhtempprc(p_date timestamp without time zone, OUT p_iattranscnt integer, OUT p_message character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$

begin
        raise notice '%','start iat dwh proc';

        truncate table temp_iat_dwh_iattrans;

	raise notice '%','iat dwh insertion';
	insert into temp_iat_dwh_iattrans ( srno, data,transfer_status)
        select nextval('iat_dwh_id'),data1,transfer_status from(select (ct.transfer_id||','
        ||iti.iat_txn_id||','||ct.request_gateway_type||','
        ||to_char(ct.transfer_date,'dd/mm/yyyy')||','
        ||to_char(ct.transfer_date_time,'dd/mm/yyyy hh12:mi:ss pm')||','
        ||ct.network_code||','||iti.rec_nw_code||','||iti.rec_country_code||','
        ||ct.service_type||','||'c2s'||','||ct.sender_id||','||ct.sender_msisdn
        ||','||ct.receiver_msisdn||','||iti.notify_msisdn||','
        ||ct.sender_category||','||ct.sender_transfer_value||','
        ||ct.receiver_transfer_value||','||ct.transfer_value||','||ct.quantity
        ||','||ct.differential_applicable||','||ct.differential_given||','||','
        ||ct.product_code||','||ct.credit_back_status||','||ct.transfer_status
        ||','||ct.card_group_code||','||iti.prov_ratio||','||iti.exchange_rate
        ||','||replace(kv.value,',',' ') ) data1, ct.transfer_status transfer_status
        from   key_values kv right outer join c2s_transfers ct on  (kv.key=ct.error_code and kv.type='C2S_ERR_CD'),
        c2s_iat_transfer_items iti where --ct.transfer_date=p_date 
        ct.transfer_date=CURRENT_TIMESTAMP
        and iti.transfer_id=ct.transfer_id
        and ct.ext_credit_intfce_type='IAT'
        order by ct.transfer_date_time) as d1;
        /* commit; */

       p_iattranscnt=(select max(srno)  from temp_iat_dwh_iattrans);


        raise notice '%','iat dwh proc completed';
        p_message:='success';

        exception
            when others then
                p_message:='not able to migrate data, exception occoured';
                RAISE EXCEPTION 'not able to migrate data, exception occoured';

end;
$$;


ALTER FUNCTION pretupsdatabase.iatdwhtempprc(p_date timestamp without time zone, OUT p_iattranscnt integer, OUT p_message character varying) OWNER TO pgdb;

--
-- Name: insert_cat_roles(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION insert_cat_roles(OUT p_returnmessage character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
declare 

v_category_code                          category_roles.category_code%type;
--v_returnmessage                           varchar(255);


 DECLARE category_cur cursor for
                              select  CATEGORY_CODE from CATEGORY_ROLES where CATEGORY_CODE not in ('SUADM','BTNADM','CHADM','SSADM');

begin

                open  category_cur;
           
                                loop
 raise notice '%','inside loop';
                            
                                                                fetch category_cur into v_category_code ;
                                                             IF NOT FOUND THEN EXIT;END IF;

                                                                     raise notice '%','insertion1';                       
                                                               insert into category_roles (category_code,role_code)
                                                              values (v_category_code,'BC2CINITIATE');

                                   raise notice '%','insertion2'; 
                                                              insert into category_roles (category_code,role_code)
                                                            values ('BCU','BC2CAPPROVE');
                                                              p_returnmessage:='Success';
					end loop;
        close category_cur;
                      
                      EXCEPTION
					WHEN OTHERS
						THEN
						
                                                                raise notice '%','exception in inserting new record for '||v_category_code || SQLERRM;
                                                                p_returnmessage:='exception in inserting new record for ',v_category_code;
                                                                
                                                              RAISE EXCEPTION 'exception in inserting new record for';
end ;
$$;


ALTER FUNCTION pretupsdatabase.insert_cat_roles(OUT p_returnmessage character varying) OWNER TO pgdb;

--
-- Name: insert_dly_no_c2s_lms_smry(character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION insert_dly_no_c2s_lms_smry(aiv_date character varying, OUT rtn_message character varying, OUT rtn_messageforlog character varying, OUT rtn_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
declare 
p_trans_date            daily_c2s_lms_summary.trans_date%type;
p_user_id                daily_c2s_lms_summary.user_id%type;
p_product_code            daily_c2s_lms_summary.product_code%type;
p_lms_profile            daily_c2s_lms_summary.lms_profile%type;
p_txn_amount            daily_c2s_lms_summary.transaction_amount%type;
p_txn_count                daily_c2s_lms_summary.transaction_count%type;
p_accumulatepoint        daily_c2s_lms_summary.accumulated_points%type;
p_version				DAILY_C2S_LMS_SUMMARY.version%type;
p_count int;

 declare insert_cur cursor FOR
        select distinct cu.user_id,cu.LMS_PROFILE, ps.PRODUCT_CODE,ps.version  from channel_users cu,  users U, PROFILE_DETAILS ps
	where cu.LMS_PROFILE is not null and u.USER_ID=cu.USER_ID and u.status not in ('N','C') and cu.LMS_PROFILE=ps.SET_ID
        EXCEPT  
       select ds.user_id,ds.LMS_PROFILE, ds.PRODUCT_CODE,ds.version from DAILY_C2S_LMS_SUMMARY ds  where ds.trans_date=to_date('08/12/16','dd/mm/yy');
    begin

      p_count:=0;
      p_trans_date:=to_date(aiv_date,'dd/mm/yy');    
      p_txn_amount:=0;
      p_txn_count:=0;
      p_accumulatepoint:=0;
        p_version:=1;	
      raise notice '%','i am here' ||p_trans_date ;      
      for user_records in insert_cur
             loop
              raise notice '%',' inside loop';
                     p_user_id:=user_records.user_id;
                    p_product_code:=user_records.product_code;
                    p_lms_profile:=user_records.lms_profile;
                    p_version:=user_records.version;	
                begin
                    p_count:=p_count+1;
                    raise notice '%',' insert before';
                    insert into daily_c2s_lms_summary(trans_date,user_id,product_code,lms_profile,transaction_amount,transaction_count,accumulated_points,version) values  (p_trans_date,p_user_id,p_product_code,p_lms_profile,p_txn_amount,p_txn_count,p_accumulatepoint,p_version);

raise notice '%',' insert after';
                    exception
                        when others       then
                                  raise notice '%', 'exception in insert_dly_no_c2s_lms_smry update sql, user:' || p_user_id ||' date:'||p_trans_date||' profile:'||p_lms_profile|| sqlerrm;
                                  rtn_messageforlog := 'exception in insert_dly_no_c2s_lms_smry update sql, user:' || p_user_id||' date:'||p_trans_date||' profile:'||p_lms_profile;
                                  rtn_sqlerrmsgforlog := sqlerrm;
                                  raise exception 'exception in insert_dly_no_c2s_lms_smry update sql';
                 end;
             end loop;
                 rtn_message:='success';
                rtn_messageforlog :='pretups insert_dly_no_c2s_lms_smry successfully executed, excuted date time:'||current_timestamp||' for date:'||p_trans_date||' number updates:'||p_count;
                rtn_sqlerrmsgforlog :=' ';
                
        exception --exception handling of main procedure
         
         when others then
              
              
               raise notice '%','others error in insert_dly_no_c2s_lms_smry procedure:='||sqlerrm;
              rtn_messageforlog :='others error in insert_dly_no_c2s_lms_smry procedure'||sqlerrm;
              rtn_message :='failed';
              raise exception 'others error in insert_dly_no_c2s_lms_smry procedure';
    end;
$$;


ALTER FUNCTION pretupsdatabase.insert_dly_no_c2s_lms_smry(aiv_date character varying, OUT rtn_message character varying, OUT rtn_messageforlog character varying, OUT rtn_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: lmb_mis_sp_sos_error_status_summary(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION lmb_mis_sp_sos_error_status_summary(p_exec_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $_$
  DECLARE 

DECLARE SUBS_LMB_SUMM_BY_ERR_STA CURSOR(p_exec_date TIMESTAMP) FOR
		SELECT ERROR_STATUS, COUNT(1) tot_cnt, NETWORK_CODE
		FROM SOS_TRANSACTION_DETAILS
		WHERE
		RECHARGE_DATE = p_exec_date
		AND ERROR_STATUS IS NOT NULL
		GROUP BY ERROR_STATUS,
		NETWORK_CODE;

DECLARE SUBS_SETT_SUMM_BY_ERR_STA CURSOR (p_exec_date TIMESTAMP) FOR
		SELECT SETTLEMENT_ERROR_CODE, COUNT(1) tot_cnt, NETWORK_CODE
		FROM SOS_TRANSACTION_DETAILS
		WHERE
		DATE_TRUNC('day',SETTLEMENT_DATE::TIMESTAMP) = p_exec_date
		AND SETTLEMENT_ERROR_CODE IS NOT NULL
		GROUP BY SETTLEMENT_ERROR_CODE,
		NETWORK_CODE;

BEGIN

	FOR lctr IN SUBS_LMB_SUMM_BY_ERR_STA (p_exec_date)
	LOOP
		BEGIN
			--$$ Insertion in SOS_SUMMARY_BY_ERROR_STATUS for LMB failure details $$--
			INSERT INTO SOS_SUMMARY_BY_ERROR_STATUS (
				process_type,transaction_date,
				error_status,total_count,
				created_on,created_by,
				network_code )
			VALUES(
				'LMB',p_exec_date,
				lctr.error_status,lctr.tot_cnt,
				CURRENT_TIMESTAMP,'SYSTEM',
				lctr.network_code);

			EXCEPTION
			WHEN OTHERS THEN
				RAISE NOTICE '%','EXCEPTION while Inserting in SOS_SUMMARY_BY_ERROR_STATUS for LMB failure summary 111'|| SQLERRM;
				v_messageforlog :='Error while Inserting record in SOS_SUMMARY_BY_ERROR_STATUS for LMB failure summary 111, Date:'|| p_exec_date;
				v_sqlerrmsgforlog := SQLERRM;
			RAISE EXCEPTION  using errcode = 'ERR05';
		END;
	END LOOP;
	BEGIN
		FOR sctr IN SUBS_SETT_SUMM_BY_ERR_STA (p_exec_date)
		LOOP
			BEGIN
				--$$ Insertion in SOS_SUMMARY_BY_ERROR_STATUS for Settlemnet failure details $$--
				INSERT INTO SOS_SUMMARY_BY_ERROR_STATUS (
					process_type,transaction_date,
					error_status,total_count,
					created_on,created_by,
					network_code )
				VALUES(
					'SETTLEMENT',p_exec_date,
					sctr.settlement_error_code,sctr.tot_cnt,
					CURRENT_TIMESTAMP,'SYSTEM',
					sctr.network_code);

				EXCEPTION
				WHEN OTHERS THEN
					RAISE NOTICE '%','EXCEPTION while Inserting in SOS_SUMMARY_BY_ERROR_STATUS for settlement failure summary 222 '|| SQLERRM;
					v_messageforlog :='Error while Inserting record in SOS_SUMMARY_BY_ERROR_STATUS for settlement failure summary 222, Date:'|| p_exec_date;
					v_sqlerrmsgforlog := SQLERRM;
				RAISE EXCEPTION  using errcode = 'ERR05';
			END;
		END LOOP;
	END;

	EXCEPTION
	when sqlstate 'ERR05' then
		RAISE NOTICE '%','EXCEPTION IN SP_SOS_ERROR_STATUS_SUMMARY 222';
		RAISE EXCEPTION  using errcode = 'ERR01';
	WHEN OTHERS THEN
		RAISE NOTICE '%', 'OTHERS EXCEPTION =' || SQLERRM;
		v_messageforlog :='Error in SP_SOS_ERROR_STATUS_SUMMARY for settlement failure summary 333, Date:'|| p_exec_date;
		v_sqlerrmsgforlog := SQLERRM;
	RAISE EXCEPTION  using errcode = 'ERR01';
END;
$_$;


ALTER FUNCTION pretupsdatabase.lmb_mis_sp_sos_error_status_summary(p_exec_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: lmb_mis_sp_sos_transaction_summary(timestamp without time zone, integer, integer, integer, integer, integer, integer, integer, integer, integer, integer, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION lmb_mis_sp_sos_transaction_summary(p_exec_date timestamp without time zone, p_tot_lmb_cnt integer, p_suc_lmb_cnt integer, p_fail_lmb_cnt integer, p_suc_lmb_amt integer, p_tot_access_fee integer, p_tot_sett_cnt integer, p_suc_sett_cnt integer, p_fail_sett_cnt integer, p_suc_sett_amt integer, p_mon_unsett_cnt integer, p_network_code character varying, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
 DECLARE 

		v_month		SOS_MONTHLY_SUMMARY.MONTH%TYPE;
		v_year		SOS_MONTHLY_SUMMARY.YEAR%TYPE;
		v_tot_unsett_cnt	SOS_MONTHLY_SUMMARY.TOTLE_UNSETTLEED_COUNT%TYPE = 0;

BEGIN


	v_month := (TO_CHAR(p_exec_date, 'MM'))::integer;
	v_year := (TO_CHAR(p_exec_date, 'YYYY'))::integer ;
	BEGIN
		RAISE NOTICE '%','@@@@@@@@@@  NOW in SOS_DAILY_SUMMARY block';

		v_tot_unsett_cnt=(SELECT COUNT(1)
		FROM SOS_TRANSACTION_DETAILS
		WHERE
		SETTLEMENT_FLAG='N'
		AND RECHARGE_DATE=p_exec_date
		AND NETWORK_CODE=p_network_code
		AND SOS_RECHARGE_STATUS = '200'-----added @nu
		GROUP BY RECHARGE_DATE, NETWORK_CODE);

		IF v_tot_unsett_cnt is not null  THEN
			INSERT INTO SOS_DAILY_SUMMARY (
				transaction_date,
				total_lmb_count,success_lmb_count,
				failed_lmb_count,success_lmb_amt,
				total_access_fee,total_settled_count,
				total_success_settled_count,
				total_failed_settled_count,
				total_success_settled_amt,
				totle_unsettleed_count,created_on,
				created_by,network_code )
			VALUES(
				p_exec_date,
				p_tot_lmb_cnt,p_suc_lmb_cnt,
				p_fail_lmb_cnt,p_suc_lmb_amt,
				p_tot_access_fee,p_tot_sett_cnt,
				p_suc_sett_cnt,
				p_fail_sett_cnt,
				p_suc_sett_amt,
				v_tot_unsett_cnt,CURRENT_TIMESTAMP,
				'SYSTEM',p_network_code);

		else
			INSERT INTO SOS_DAILY_SUMMARY (
				transaction_date,
				total_lmb_count,success_lmb_count,
				failed_lmb_count,success_lmb_amt,
				total_access_fee,total_settled_count,
				total_success_settled_count,
				total_failed_settled_count,
				total_success_settled_amt,
				totle_unsettleed_count,created_on,
				created_by,network_code )
			VALUES(
				p_exec_date,
				p_tot_lmb_cnt,p_suc_lmb_cnt,
				p_fail_lmb_cnt,p_suc_lmb_amt,
				p_tot_access_fee,p_tot_sett_cnt,
				p_suc_sett_cnt,
				p_fail_sett_cnt,
				p_suc_sett_amt,
				v_tot_unsett_cnt,CURRENT_TIMESTAMP,
				'SYSTEM',p_network_code);
		END IF;
		EXCEPTION
		WHEN OTHERS THEN
				RAISE NOTICE '%','EXCEPTION while Inserting in SOS_DIALY_SUMMARY 111 '|| SQLERRM;
				v_messageforlog :='Error while while Inserting in SOS_DIALY_SUMMARY 111, Date:'|| p_exec_date;
				v_sqlerrmsgforlog := SQLERRM;
		RAISE EXCEPTION  using errcode = 'ERR05';
	END;

	BEGIN

		RAISE NOTICE '%','###########  NOW in SOS_MONTHLY_SUMMARY block';

		---Updation for SOS_MONTHLY_SUMMARY
		UPDATE SOS_MONTHLY_SUMMARY SET
			TOTAL_LMB_COUNT = TOTAL_LMB_COUNT + p_tot_lmb_cnt,
			SUCCESS_LMB_COUNT =  SUCCESS_LMB_COUNT + p_suc_lmb_cnt,
			FAILED_LMB_COUNT = FAILED_LMB_COUNT + p_fail_lmb_cnt,
			SUCCESS_LMB_AMT = SUCCESS_LMB_AMT + p_suc_lmb_amt,
			TOTAL_ACCESS_FEE = TOTAL_ACCESS_FEE + p_tot_access_fee,
			TOTAL_SETTLED_COUNT = TOTAL_SETTLED_COUNT + p_tot_sett_cnt,
			TOTAL_SUCCESS_SETTLED_COUNT = TOTAL_SUCCESS_SETTLED_COUNT + p_suc_sett_cnt,
			TOTAL_FAILED_SETTLED_COUNT = TOTAL_FAILED_SETTLED_COUNT + p_fail_sett_cnt,
			TOTAL_SUCCESS_SETTLED_AMT = TOTAL_SUCCESS_SETTLED_AMT + p_suc_sett_amt,
			TOTLE_UNSETTLEED_COUNT = p_mon_unsett_cnt,
			MODIFIED_ON = CURRENT_TIMESTAMP,
			MODIFIED_BY = 'SYSTEM'
			WHERE
			MONTH = v_month
			AND YEAR = v_year
			AND NETWORK_CODE = p_network_code;

		IF NOT FOUND THEN
			INSERT INTO SOS_MONTHLY_SUMMARY (
				MONTH,YEAR,
				total_lmb_count,success_lmb_count,
				failed_lmb_count,success_lmb_amt,
				total_access_fee,total_settled_count,
				total_success_settled_count,
				total_failed_settled_count,
				total_success_settled_amt,
				totle_unsettleed_count,
				created_on,created_by,
				network_code)
			VALUES (
				v_month,v_year,
				p_tot_lmb_cnt,p_suc_lmb_cnt,
				p_fail_lmb_cnt,p_suc_lmb_amt,
				p_tot_access_fee,p_tot_sett_cnt,
				p_suc_sett_cnt,
				p_fail_sett_cnt,
				p_suc_sett_amt,
				p_mon_unsett_cnt,
				CURRENT_TIMESTAMP,'SYSTEM',
				p_network_code);
		END IF;

		EXCEPTION
		WHEN OTHERS THEN
			RAISE NOTICE '%','EXCEPTION while Updating/Inserting in SOS_MONTHLY_SUMMARY for LMB and settlement 111'|| SQLERRM;
			v_messageforlog :='Error while Updating/Inserting record in SOS_MONTHLY_SUMMARY for LMB and settlement 111, Date:'|| p_exec_date;
			v_sqlerrmsgforlog := SQLERRM;
			RAISE EXCEPTION  using errcode = 'ERR05';
	END;


	EXCEPTION

	when sqlstate 'ERR05' then
		RAISE NOTICE '%','EXCEPTION IN SP_SOS_TRANSACTION_SUMMARY 222';
		RAISE EXCEPTION  using errcode = 'ERR01';

	WHEN OTHERS THEN
		RAISE NOTICE '%','EXCEPTION in SP_SOS_TRANSACTION_SUMMARY for LMB and settlement transactions 333'|| SQLERRM;
		v_messageforlog :='Error while in SP_SOS_TRANSACTION_SUMMARY for LMB and settlement transactions 333, Date:'|| p_exec_date;
		v_sqlerrmsgforlog := SQLERRM;
		RAISE EXCEPTION  using errcode = 'ERR05';
END;
$$;


ALTER FUNCTION pretupsdatabase.lmb_mis_sp_sos_transaction_summary(p_exec_date timestamp without time zone, p_tot_lmb_cnt integer, p_suc_lmb_cnt integer, p_fail_lmb_cnt integer, p_suc_lmb_amt integer, p_tot_access_fee integer, p_tot_sett_cnt integer, p_suc_sett_cnt integer, p_fail_sett_cnt integer, p_suc_sett_amt integer, p_mon_unsett_cnt integer, p_network_code character varying, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: lmb_mis_sp_subscriber_monthly_summary(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION lmb_mis_sp_subscriber_monthly_summary(p_exec_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
 DECLARE 
	v_subs_msisdn				SOS_SUBSCRIBER_MONTHLY_SUMMARY.SUBSCRIBER_MSISDN%TYPE;
	v_month						SOS_SUBSCRIBER_MONTHLY_SUMMARY.MONTH%TYPE;
	v_year						SOS_SUBSCRIBER_MONTHLY_SUMMARY.YEAR%TYPE;
	v_tot_lmb_count				SOS_SUBSCRIBER_MONTHLY_SUMMARY.TOTAL_LMB_COUNT%TYPE=0;
	v_succ_lmb_count			SOS_SUBSCRIBER_MONTHLY_SUMMARY.SUCCESS_LMB_COUNT%TYPE=0;
	v_failed_lmb_count			SOS_SUBSCRIBER_MONTHLY_SUMMARY.FAILED_LMB_COUNT%TYPE=0;
	v_succ_lmb_amt				SOS_SUBSCRIBER_MONTHLY_SUMMARY.SUCCESS_LMB_AMT%TYPE=0;
	v_tot_access_fee			SOS_SUBSCRIBER_MONTHLY_SUMMARY.TOTAL_ACCESS_FEE%TYPE=0;
	v_tot_settled_count			SOS_SUBSCRIBER_MONTHLY_SUMMARY.TOTAL_SETTLED_COUNT%TYPE=0;
	v_succ_settled_count		SOS_SUBSCRIBER_MONTHLY_SUMMARY.TOTAL_SUCCESS_SETTLED_COUNT%TYPE=0;
	v_failed_settled_count		SOS_SUBSCRIBER_MONTHLY_SUMMARY.TOTAL_FAILED_SETTLED_COUNT%TYPE=0;
	v_succ_settled_amt			SOS_SUBSCRIBER_MONTHLY_SUMMARY.TOTAL_SUCCESS_SETTLED_AMT%TYPE=0;
	v_tot_unsettled_count		SOS_SUBSCRIBER_MONTHLY_SUMMARY.TOTLE_UNSETTLEED_COUNT%TYPE=0;
	v_created_on				SOS_SUBSCRIBER_MONTHLY_SUMMARY.CREATED_ON%TYPE;
	v_created_by				SOS_SUBSCRIBER_MONTHLY_SUMMARY.CREATED_BY%TYPE;
	v_modified_on				SOS_SUBSCRIBER_MONTHLY_SUMMARY.MODIFIED_ON%TYPE;
	v_modified_by				SOS_SUBSCRIBER_MONTHLY_SUMMARY.MODIFIED_BY%TYPE;
	v_network_code				SOS_SUBSCRIBER_MONTHLY_SUMMARY.NETWORK_CODE%TYPE;

	v_settled_flag				SOS_TRANSACTION_DETAILS.SETTLEMENT_FLAG%TYPE;
	v_settled_status			SOS_TRANSACTION_DETAILS.SETTLEMENT_STATUS%TYPE;
	v_settled_date				SOS_TRANSACTION_DETAILS.SETTLEMENT_DATE%TYPE;

	pv_tot_lmb_cnt				SOS_MONTHLY_SUMMARY.TOTAL_LMB_COUNT%TYPE=0;
	pv_suc_lmb_cnt				SOS_MONTHLY_SUMMARY.SUCCESS_LMB_COUNT%TYPE=0;
  	pv_fail_lmb_cnt				SOS_MONTHLY_SUMMARY.FAILED_LMB_COUNT%TYPE=0;
  	pv_suc_lmb_amt				SOS_MONTHLY_SUMMARY.SUCCESS_LMB_AMT%TYPE=0;
  	pv_tot_access_fee			SOS_MONTHLY_SUMMARY.TOTAL_ACCESS_FEE%TYPE=0;
  	pv_tot_sett_cnt				SOS_MONTHLY_SUMMARY.TOTAL_SETTLED_COUNT%TYPE=0;
  	pv_suc_sett_cnt				SOS_MONTHLY_SUMMARY.TOTAL_SUCCESS_SETTLED_COUNT%TYPE=0;
  	pv_fail_sett_cnt			SOS_MONTHLY_SUMMARY.TOTAL_FAILED_SETTLED_COUNT%TYPE=0;
  	pv_suc_sett_amt				SOS_MONTHLY_SUMMARY.TOTAL_SUCCESS_SETTLED_AMT%TYPE=0;
  	pv_mon_unsett_cnt			SOS_MONTHLY_SUMMARY.TOTLE_UNSETTLEED_COUNT%TYPE=0;

	v_lmb_record_counter DECIMAL(20)=0;
	v_lmb_subs_counter DECIMAL(20)=0;
	v_lmb_update_flag SMALLINT;
	v_sett_record_counter DECIMAL(20)=0;
	v_sett_subs_counter DECIMAL(20)=0;
	v_sett_update_flag SMALLINT=0;
	v_prev_subscriber BIGINT=0;
	v_is_data_found VARCHAR(10) = 'FALSE';
--Cursor Declaration
declare SUBS_LMB_CURSOR CURSOR(p_exec_date  TIMESTAMP) for
		SELECT TRANSACTION_ID, SUBSCRIBER_MSISDN,
			TO_CHAR(RECHARGE_DATE, 'MM')::integer as MONTH,
			TO_CHAR(RECHARGE_DATE, 'YYYY')::integer as YEAR,
			SOS_RECHARGE_STATUS,
			COALESCE(SOS_RECHARGE_AMOUNT,0) AMOUNT,
			COALESCE(SOS_CREDIT_AMOUNT,0) CREDIT_AMOUNT,
			COALESCE(PROCESS_FEE_VALUE,0) PROCESSING_FEE,
			NETWORK_CODE
			FROM SOS_TRANSACTION_DETAILS
			WHERE
			RECHARGE_DATE = p_exec_date
			AND SOS_RECHARGE_STATUS IN('200','206')
			ORDER BY SUBSCRIBER_MSISDN	;

declare SUBS_SETTLEMENT_CURSOR CURSOR(p_exec_date TIMESTAMP) for
		SELECT TRANSACTION_ID, SUBSCRIBER_MSISDN,
			COALESCE(SOS_DEBIT_AMOUNT,0) DEBIT_AMOUNT,
			SETTLEMENT_FLAG,
			SETTLEMENT_STATUS,
			SETTLEMENT_DATE,
			NETWORK_CODE
			FROM SOS_TRANSACTION_DETAILS
			WHERE
			date_trunc('day',SETTLEMENT_DATE::TIMESTAMP) =p_exec_date
			AND
			SETTLEMENT_STATUS IN('200','206')
			ORDER BY SUBSCRIBER_MSISDN;

declare SUBS_UNSETTLEMENT_CURSOR CURSOR (p_exec_date TIMESTAMP) for
		SELECT SUBSCRIBER_MSISDN,COUNT(1) UNSETTLED_COUNT,
			NETWORK_CODE
			FROM SOS_TRANSACTION_DETAILS
			WHERE SETTLEMENT_FLAG='N'
			AND TO_CHAR(RECHARGE_DATE, 'MM')::integer= v_month
			AND TO_CHAR(RECHARGE_DATE, 'YYYY')::integer= v_year
			AND RECHARGE_DATE = p_exec_date
			AND SOS_RECHARGE_STATUS = '200'-----added @nu
			GROUP BY
			SUBSCRIBER_MSISDN,NETWORK_CODE;

BEGIN

	BEGIN
		---DBMS_OUTPUT.PUT_LINE('Entered SP_SUBSCRIBER_MONTHLY_SUMMARY FOR ::::::::'||n_date_for_mis);
		v_prev_subscriber:=0;
		v_month := TO_CHAR(p_exec_date, 'MM')::integer;
		v_year := TO_CHAR(p_exec_date, 'YYYY')::integer ;

		FOR tr IN SUBS_LMB_CURSOR (p_exec_date)
		LOOP
			v_is_data_found := 'TRUE';
			RAISE NOTICE '%','first';
			RAISE NOTICE '%','###### v_prev_subscriber and record is :::::::: '||v_prev_subscriber;
   			IF v_prev_subscriber::character varying <> tr.subscriber_msisdn AND v_lmb_record_counter>0 THEN
	  			v_lmb_update_flag:= 1 ;
			END IF;

			IF  v_lmb_update_flag = 1 THEN
	  			v_lmb_subs_counter:=v_lmb_subs_counter+1;
				RAISE NOTICE '%','inside !!!! if ';
				BEGIN
					UPDATE SOS_SUBSCRIBER_MONTHLY_SUMMARY SET
					TOTAL_LMB_COUNT = TOTAL_LMB_COUNT + v_tot_lmb_count,
					SUCCESS_LMB_COUNT = SUCCESS_LMB_COUNT + v_succ_lmb_count,
					FAILED_LMB_COUNT = FAILED_LMB_COUNT + v_failed_lmb_count,
					SUCCESS_LMB_AMT = SUCCESS_LMB_AMT + v_succ_lmb_amt,
					TOTAL_ACCESS_FEE = TOTAL_ACCESS_FEE + v_tot_access_fee,
					MODIFIED_ON = CURRENT_TIMESTAMP,
					MODIFIED_BY = 'SYSTEM'
					WHERE SUBSCRIBER_MSISDN  = v_prev_subscriber ::character varying
					AND MONTH = v_month
					AND YEAR =  v_year
					AND NETWORK_CODE = tr.network_code;
					RAISE NOTICE '%','inside !!!! begin ';
					IF NOT FOUND THEN
						INSERT INTO SOS_SUBSCRIBER_MONTHLY_SUMMARY
							(subscriber_msisdn,MONTH,
							YEAR,total_lmb_count,
							success_lmb_count,failed_lmb_count,
							success_lmb_amt,total_access_fee,
							created_on,created_by,network_code)
						SELECT (
							v_prev_subscriber,v_month,
							v_year,v_tot_lmb_count,
							v_succ_lmb_count,v_failed_lmb_count,
							v_succ_lmb_amt,v_tot_access_fee,
							CURRENT_TIMESTAMP,'SYSTEM',v_network_code);
					END IF;

					-- Resetting the value of variables for next subscriber
					v_tot_lmb_count:=0;
					v_succ_lmb_count:=0;
					v_succ_lmb_amt:=0;
					v_tot_access_fee:=0;
					v_lmb_update_flag:=0;
					v_failed_lmb_count:=0; -----added @nu

					EXCEPTION
					WHEN OTHERS THEN
						RAISE NOTICE '%','EXCEPTION while Updating/Inserting in SOS_SUBSCRIBER_MONTHLY_SUMMARY 111='|| SQLERRM;
						v_messageforlog :='Error while Updating/Inserting record in SOS_SUBSCRIBER_MONTHLY_SUMMARY 111, Date:'|| p_exec_date;
						v_sqlerrmsgforlog := SQLERRM;
					RAISE EXCEPTION  using errcode = 'ERR05';
				END;
				
				RAISE NOTICE '%','inside !!!! end if ';
			END IF;

			-- All variable prefix by pv_ will be passed in SP_TRANSACTION_SUMMARY Procedure to make the daily and monthly SOS summary
			v_tot_lmb_count:= v_tot_lmb_count+1 ;
			pv_tot_lmb_cnt := pv_tot_lmb_cnt+1;

			IF tr.sos_recharge_status = '200' THEN
				v_succ_lmb_count := v_succ_lmb_count+1;
				v_succ_lmb_amt:= v_succ_lmb_amt+tr.AMOUNT;
				v_tot_access_fee:= v_tot_access_fee+tr.PROCESSING_FEE;
				pv_suc_lmb_cnt := pv_suc_lmb_cnt+1;
				pv_suc_lmb_amt := pv_suc_lmb_amt+tr.AMOUNT;
				pv_tot_access_fee := pv_tot_access_fee+tr.PROCESSING_FEE;
			ELSIF tr.sos_recharge_status = '206' THEN
				v_failed_lmb_count:=v_failed_lmb_count+1;
				pv_fail_lmb_cnt := pv_fail_lmb_cnt+1;
			END IF;

			v_prev_subscriber :=  tr.subscriber_msisdn;
			v_lmb_record_counter:= v_lmb_record_counter+1;
			v_network_code:= tr.network_code;

		END LOOP;

		-- Inserting OR Updating last subscriber details for LMB transactions
		IF  v_tot_lmb_count >0 THEN
			v_lmb_subs_counter:=v_lmb_subs_counter+1;
			BEGIN
				UPDATE SOS_SUBSCRIBER_MONTHLY_SUMMARY SET
				TOTAL_LMB_COUNT = TOTAL_LMB_COUNT + v_tot_lmb_count,
				SUCCESS_LMB_COUNT = SUCCESS_LMB_COUNT + v_succ_lmb_count,
				FAILED_LMB_COUNT = FAILED_LMB_COUNT + v_failed_lmb_count,
				SUCCESS_LMB_AMT = SUCCESS_LMB_AMT + v_succ_lmb_amt,
				TOTAL_ACCESS_FEE = TOTAL_ACCESS_FEE + v_tot_access_fee,
				MODIFIED_ON = CURRENT_TIMESTAMP,
				MODIFIED_BY = 'SYSTEM'
				WHERE SUBSCRIBER_MSISDN= v_prev_subscriber::character varying
				AND MONTH = v_month
				AND YEAR =  v_year
				AND NETWORK_CODE = v_network_code;

				IF NOT FOUND THEN
					INSERT INTO SOS_SUBSCRIBER_MONTHLY_SUMMARY
						(subscriber_msisdn,
						MONTH, YEAR,
						total_lmb_count,success_lmb_count,
						failed_lmb_count,success_lmb_amt,
						total_access_fee,created_on,
						created_by,network_code)
					VALUES (v_prev_subscriber,
						v_month,v_year,
						v_tot_lmb_count,v_succ_lmb_count,
						v_failed_lmb_count,v_succ_lmb_amt,
						v_tot_access_fee,CURRENT_TIMESTAMP,
						'SYSTEM',v_network_code);
				END IF;

				EXCEPTION
				WHEN OTHERS THEN
					RAISE NOTICE '%','EXCEPTION when Updating/Inserting record in SOS_SUBSCRIBER_MONTHLY_SUMMARY 222 ='|| SQLERRM;
					v_messageforlog :='Error while Updating/Inserting record in SOS_SUBSCRIBER_MONTHLY_SUMMARY 222, Date:'|| p_exec_date;
					v_sqlerrmsgforlog := SQLERRM;
				RAISE EXCEPTION  using errcode = 'ERR05';
			END;
		END IF;

		BEGIN
			FOR ctr IN SUBS_SETTLEMENT_CURSOR (p_exec_date)
			LOOP
				v_is_data_found := 'TRUE';
				IF v_prev_subscriber::character varying <> ctr.subscriber_msisdn AND v_sett_record_counter > 0 THEN
					v_sett_update_flag:= 1 ;
				END IF;

				IF  v_sett_update_flag = 1 THEN
					v_sett_subs_counter:=v_sett_subs_counter+1;
					RAISE NOTICE '%','************ v_succ_settled_amt :   '||v_succ_settled_amt;
					BEGIN
						UPDATE SOS_SUBSCRIBER_MONTHLY_SUMMARY SET
						TOTAL_SETTLED_COUNT = TOTAL_SETTLED_COUNT + v_tot_settled_count,
						TOTAL_SUCCESS_SETTLED_COUNT = TOTAL_SUCCESS_SETTLED_COUNT + v_succ_settled_count,
						TOTAL_FAILED_SETTLED_COUNT = TOTAL_FAILED_SETTLED_COUNT + v_failed_settled_count,
						TOTAL_SUCCESS_SETTLED_AMT = CASE  WHEN TOTAL_SUCCESS_SETTLED_AMT IS NULL THEN 0 ELSE TOTAL_SUCCESS_SETTLED_AMT END + v_succ_settled_amt,
						MODIFIED_ON = CURRENT_TIMESTAMP,
						MODIFIED_BY = 'SYSTEM'
						WHERE SUBSCRIBER_MSISDN= v_prev_subscriber::character varying
						AND MONTH = v_month
						AND YEAR =  v_year
						AND NETWORK_CODE = v_network_code;

						IF NOT FOUND THEN
							RAISE NOTICE '%','Updation failed for update the settlement record for subscriber mobile::::::'||ctr.subscriber_msisdn;
							INSERT INTO SOS_SUBSCRIBER_MONTHLY_SUMMARY
								(subscriber_msisdn,
								MONTH, YEAR,
								total_settled_count,
								total_success_settled_count,
								total_failed_settled_count,
								total_success_settled_amt,
								created_on, created_by,
								network_code)
							VALUES (v_prev_subscriber,
								v_month,v_year,
								v_tot_settled_count,
								v_succ_settled_count,
								v_failed_settled_count,
								v_succ_settled_amt,
								CURRENT_TIMESTAMP,'SYSTEM',
								v_network_code);
						END IF;

						-- Resetting the value of variables for next subscriber
						v_sett_update_flag:=0;
						v_tot_settled_count:=0;
						v_succ_settled_count:=0;
						v_failed_settled_count:=0;
						v_succ_settled_amt:=0;

						EXCEPTION
						WHEN OTHERS THEN
							RAISE NOTICE '%','EXCEPTION while Updating/Inserting in SOS_SUBSCRIBER_MONTHLY_SUMMARY for settlement 111 ='|| SQLERRM;
							v_messageforlog :='Error while Updating/Inserting record in SOS_SUBSCRIBER_MONTHLY_SUMMARY for settlement 111, Date:'|| ctr.subscriber_msisdn;
							v_sqlerrmsgforlog := SQLERRM;
						RAISE EXCEPTION  using errcode = 'ERR05';
					END;
				END IF;

				v_tot_settled_count:=v_tot_settled_count+1;
				pv_tot_sett_cnt:=pv_tot_sett_cnt+1;
				IF ctr.settlement_flag = 'Y' AND ctr.settlement_status = '200' THEN
	  				v_succ_settled_count := v_succ_settled_count+1;
					RAISE NOTICE '%','debit amount is :  '||ctr.debit_amount;
					v_succ_settled_amt:=v_succ_settled_amt+ctr.debit_amount;
					pv_suc_sett_cnt := pv_suc_sett_cnt+1;
					pv_suc_sett_amt := pv_suc_sett_amt+ctr.debit_amount;
				ELSIF ctr.settlement_status = '206' THEN
					v_failed_settled_count:=v_failed_settled_count+1;
					pv_fail_sett_cnt := pv_fail_sett_cnt+1;
				END IF;

				v_prev_subscriber :=  ctr.subscriber_msisdn;
				v_sett_record_counter:= v_sett_record_counter+1;
				v_network_code:= ctr.network_code;

			END LOOP;

			-- Inserting OR Updating last subscriber details for SETTLEMENT transactions
			IF  v_tot_settled_count > 0 THEN
				v_sett_subs_counter:=v_sett_subs_counter+1;
				RAISE NOTICE '%','************ v_succ_settled_amt :   '||v_succ_settled_amt;
				BEGIN
					UPDATE SOS_SUBSCRIBER_MONTHLY_SUMMARY SET
					TOTAL_SETTLED_COUNT = TOTAL_SETTLED_COUNT + v_tot_settled_count,
					TOTAL_SUCCESS_SETTLED_COUNT = TOTAL_SUCCESS_SETTLED_COUNT + v_succ_settled_count,
					TOTAL_FAILED_SETTLED_COUNT = TOTAL_FAILED_SETTLED_COUNT + v_failed_settled_count,
					TOTAL_SUCCESS_SETTLED_AMT = CASE  WHEN TOTAL_SUCCESS_SETTLED_AMT IS NULL THEN 0 ELSE TOTAL_SUCCESS_SETTLED_AMT END + v_succ_settled_amt,
					MODIFIED_ON = CURRENT_TIMESTAMP,
					MODIFIED_BY = 'SYSTEM'
					WHERE SUBSCRIBER_MSISDN= v_prev_subscriber::character varying
					AND MONTH = v_month
					AND YEAR =  v_year
					AND NETWORK_CODE = v_network_code;
				IF NOT FOUND THEN
						RAISE NOTICE '%','Updation failed for update the settlement record for subscriber mobile'||v_prev_subscriber;
						INSERT INTO SOS_SUBSCRIBER_MONTHLY_SUMMARY
							(subscriber_msisdn,
							MONTH,YEAR,
							total_settled_count,
							total_success_settled_count,
							total_failed_settled_count,
							total_success_settled_amt,
							created_on,created_by,network_code)
						VALUES (v_prev_subscriber,
							v_month,v_year,
							v_tot_settled_count,
							v_succ_settled_count,
							v_failed_settled_count,
							v_succ_settled_amt,
							CURRENT_TIMESTAMP,'SYSTEM',v_network_code);
					END IF;
					EXCEPTION
					WHEN OTHERS THEN
						RAISE NOTICE '%','EXCEPTION while Updating/Inserting in SOS_SUBSCRIBER_MONTHLY_SUMMARY for settlement 222 ='|| SQLERRM;
						v_messageforlog :='Error while Updating/Inserting record in SOS_SUBSCRIBER_MONTHLY_SUMMARY for settlement 222, Date:'|| v_prev_subscriber;
						v_sqlerrmsgforlog := SQLERRM;
					RAISE EXCEPTION  using errcode = 'ERR05';
				END;
			END IF;

			-- Updating the unsettled records for LMB taken in current month.
			BEGIN
			RAISE NOTICE '%','inside begin block';
				FOR utr IN SUBS_UNSETTLEMENT_CURSOR (p_exec_date)
				LOOP
					RAISE NOTICE '%','@@@@@@@@@@  NOW in SUBS_UNSETTLEMENT_CURSOR block';
					v_is_data_found := 'TRUE';
					pv_mon_unsett_cnt:= pv_mon_unsett_cnt+utr.UNSETTLED_COUNT;
					BEGIN
						UPDATE SOS_SUBSCRIBER_MONTHLY_SUMMARY SET
						TOTLE_UNSETTLEED_COUNT = utr.UNSETTLED_COUNT,
						MODIFIED_ON = CURRENT_TIMESTAMP,
						MODIFIED_BY = 'SYSTEM'
						WHERE SUBSCRIBER_MSISDN	= utr.subscriber_msisdn
						AND MONTH = v_month
						AND YEAR =  v_year
						AND NETWORK_CODE = utr.network_code;

						IF NOT FOUND THEN
							RAISE NOTICE '%','Updation failed for subscriber mobile = '||utr.subscriber_msisdn;
						END IF;

						EXCEPTION
						WHEN OTHERS THEN
							RAISE NOTICE '%','EXCEPTION while Updating in SOS_SUBSCRIBER_MONTHLY_SUMMARY for unsettled record 111'|| SQLERRM;
							v_messageforlog :='Error while Updating record in SOS_SUBSCRIBER_MONTHLY_SUMMARY for unsettled record 111, Date:'|| utr.subscriber_msisdn;
							v_sqlerrmsgforlog := SQLERRM;
						RAISE EXCEPTION  using errcode = 'ERR05';
					END;
				END LOOP;
RAISE NOTICE '%','outside begin block';
				-------Calling Procedure to insert the Daily and monthly SOS details.------------
				IF v_is_data_found = 'TRUE' THEN
					BEGIN
					RAISE NOTICE '%','inside if v_si_date_found'||v_is_data_found;
						perform lmb_mis_sp_sos_transaction_summary(
						p_exec_date,
						pv_tot_lmb_cnt::integer,
						pv_suc_lmb_cnt::integer,
						pv_fail_lmb_cnt::integer,
						pv_suc_lmb_amt::integer,
						pv_tot_access_fee::integer,
						pv_tot_sett_cnt::integer,
						pv_suc_sett_cnt::integer,
						pv_fail_sett_cnt::integer,
						pv_suc_sett_amt::integer,
						pv_mon_unsett_cnt::integer,
						v_network_code);


						
						EXCEPTION
						WHEN OTHERS THEN
							RAISE NOTICE '%','Exception in calling Procedure SP_SOS_TRANSACTION_SUMMARY 111:'||SQLERRM;
							v_messageforlog:='Error in calling Procedure SP_SOS_TRANSACTION_SUMMARY 111, Date:' || p_exec_date;
							v_sqlerrmsgforlog:=SQLERRM;
						RAISE EXCEPTION  using errcode = 'ERR05';
					END;
				END IF;
				EXCEPTION
				when sqlstate 'ERR05' then
					RAISE NOTICE '%','EXCEPTION IN SP_SUBSCRIBER_MONTHLY_SUMMARY 222';
				RAISE EXCEPTION  using errcode = 'ERR01';
				WHEN OTHERS THEN
				RAISE NOTICE '%','FiRST!!!!';
					RAISE NOTICE '%', 'OTHERS EXCEPTION =' || SQLERRM;
					v_messageforlog :='Exception while inserting/updating record for LMB and settlement summary 111';
					v_sqlerrmsgforlog := SQLERRM;
				RAISE EXCEPTION  using errcode = 'ERR01';

RAISE NOTICE '%','outside main begin block';
			END;-- Ending for SUBS_UNSETTLEMENT_CURSOR

		END;-- Ending for SUBS_SETTLEMENT_CURSOR

	END;
	EXCEPTION
		when sqlstate 'ERR05' then
			RAISE NOTICE '%','EXCEPTION IN SP_SUBSCRIBER_MONTHLY_SUMMARY 333';
			RAISE EXCEPTION  using errcode = 'ERR01';
		WHEN OTHERS THEN
		RAISE NOTICE '%','Second!!!!';
			RAISE NOTICE '%', 'OTHERS EXCEPTION =' || SQLERRM;
			v_messageforlog :='Exception while inserting/updating record for LMB and settlement summary 222';
			v_sqlerrmsgforlog := SQLERRM;
	RAISE EXCEPTION  using errcode = 'ERR01';

END ;
$$;


ALTER FUNCTION pretupsdatabase.lmb_mis_sp_subscriber_monthly_summary(p_exec_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: lmb_mis_summary_report_sp_get_lmbmis_data_dtrange(character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION lmb_mis_summary_report_sp_get_lmbmis_data_dtrange(aiv_fromdate character varying, aiv_todate character varying, OUT aov_message character varying, OUT aov_messageforlog character varying, OUT aov_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE 
n_date_for_mis         PROCESS_STATUS.executed_upto%TYPE;
  ld_from_date TIMESTAMP(0);
  ld_to_date TIMESTAMP(0);
  ld_created_on TIMESTAMP(0);
  flag SMALLINT;
  status SMALLINT;
  ld_mis_date TIMESTAMP(0);
  mis_already_executed SMALLINT;
  v_messageforlog 	VARCHAR(4000);
v_sqlerrmsgforlog 	VARCHAR(4000);

BEGIN

  ld_from_date   :=TO_DATE(aiv_fromDate,'dd/mm/yy');
  ld_to_date     :=TO_DATE(aiv_toDate,'dd/mm/yy');
  n_date_for_mis :=ld_from_date;
  flag           :=0;
  ld_created_on  :=CURRENT_TIMESTAMP;  -- Initailaize Created On date
  mis_already_executed :=0;

WHILE n_date_for_mis <= ld_to_date ---run the MIS process for each date less than the To Date
	LOOP
		RAISE NOTICE '%','EXCEUTING FOR ::::::::'||n_date_for_mis;
		BEGIN
			---Check if MIS process has already run for the date
			mis_already_executed=(SELECT 1 
			FROM PROCESS_STATUS
			WHERE PROCESS_ID='SOSMIS' AND EXECUTED_UPTO>=n_date_for_mis);
			if mis_already_executed <> null then
			RAISE NOTICE '%','PreTUPS SOS MIS already Executed, Date:' || n_date_for_mis;
				aov_message :='FAILED';
				aov_messageForLog:='PreTUPS SOS MIS already Executed, Date:' || n_date_for_mis;
				aov_sqlerrMsgForLog:=' ';
			--RAISE alreadyDoneException;
			RAISE EXCEPTION  using errcode ='ERR02';
			RAISE NOTICE '%','MIS CHECK FOR ::::::::';
			else
			BEGIN
				---Check if Underprocess or Ambigous transactions are found in the Transaction table for the date
				status=(SELECT 1  FROM SOS_TRANSACTION_DETAILS ST
				WHERE ST.RECHARGE_DATE = n_date_for_mis
				AND ST.SOS_RECHARGE_STATUS IN ('205','250'));
				if status <> null then
				RAISE NOTICE '%','Underprocess or Ambigous transaction found for LMB. PreTUPS SOS MIS cannot continue, Date:' || n_date_for_mis;
					aov_messageForLog:='Underprocess or Ambigous transaction found for LMB. PreTUPS SOS MIS cannot continue, Date:' || n_date_for_mis;
					aov_sqlerrMsgForLog:=' ';
					flag:=1;
				else ----If MIS not executed for the date and no Underprocess or Ambigous transactions found then update all the MIS tables
				BEGIN
					---Check if Underprocess or Ambigous transactions are found in the Transaction table for the date
					RAISE NOTICE '%','205,250 CHECK  ::::::::';
					status =(SELECT 1  FROM SOS_TRANSACTION_DETAILS
					WHERE DATE_TRUNC('day',SETTLEMENT_DATE::TIMESTAMP) = n_date_for_mis
					AND SETTLEMENT_STATUS IN ('205','250'));
					if status <> null then
					RAISE NOTICE '%','Underprocess or Ambigous transaction found for Settlement. PreTUPS SOS MIS cannot continue, Date:' || n_date_for_mis;
					aov_messageForLog:='Underprocess or Ambigous transaction found for Settlement. PreTUPS SOS MIS cannot continue, Date:' || n_date_for_mis;
					aov_sqlerrMsgForLog:=' ';
					flag:=1;

					elsif status >1 THEN
						RAISE NOTICE '%','Too Many rows found. PreTUPS LMB MIS cannot continue, Date:' || n_date_for_mis;
						aov_messageForLog:='Too Many rows found. PreTUPS LMB MIS cannot continue, Date:' || n_date_for_mis;
						aov_sqlerrMsgForLog:=' ';
						flag:=1;
					else
					--- Call of procedure to make SOS LMB and settlement transaction summary.
						BEGIN
						RAISE NOTICE '%','cLEAN ::::::::';
						select  lmb_mis_sp_subscriber_monthly_summary (n_date_for_mis) into v_messageforlog,v_sqlerrmsgforlog;
						EXCEPTION
							when sqlstate 'ERR05' then
								aov_messageForLog:=v_messageforlog;
								aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
							RAISE EXCEPTION  using errcode = 'ERR01';
							when sqlstate 'ERR01' then
								aov_messageForLog:=v_messageforlog;
								aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
							RAISE EXCEPTION  using errcode = 'ERR01';
							WHEN OTHERS THEN
								RAISE NOTICE '%','Error in Procedure SP_SUBSCRIBER_MONTHLY_SUMMARY 111:'||SQLERRM;
								aov_messageForLog:='Error in Procedure SP_SUBSCRIBER_MONTHLY_SUMMARY 111, Date:' || n_date_for_mis;
								aov_sqlerrMsgForLog:=SQLERRM;
							RAISE EXCEPTION  using errcode = 'ERR01';
						END;

					--- Call of Update Daily Failure transaction summary for LMB and settlement
						BEGIN
						RAISE NOTICE '%','cLEAN1 ::::::::';
						select  lmb_mis_sp_sos_error_status_summary(n_date_for_mis) into v_messageforlog,v_sqlerrmsgforlog;
						EXCEPTION
							when sqlstate 'ERR05' then
								aov_messageForLog:=v_messageforlog;
								aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
							RAISE EXCEPTION  using errcode = 'ERR01';
							when sqlstate 'ERR01' then
								aov_messageForLog:=v_messageforlog;
								aov_sqlerrMsgForLog:=v_sqlerrmsgforlog;
							RAISE EXCEPTION  using errcode = 'ERR01';
							WHEN OTHERS THEN
							RAISE NOTICE '%','Error in Procedure SP_SOS_ERROR_STATUS_SUMMARY 111:'||SQLERRM;
								aov_messageForLog:='Error in Procedure SP_SOS_ERROR_STATUS_SUMMARY 111, Date:' || n_date_for_mis;
								aov_sqlerrMsgForLog:=SQLERRM;
							RAISE EXCEPTION  using errcode = 'ERR01';
						END;

					UPDATE PROCESS_STATUS SET executed_upto=n_date_for_mis, executed_on=CURRENT_TIMESTAMP WHERE PROCESS_ID='SOSMIS';
					/* COMMIT; */
					aov_message :='SUCCESS';
					aov_messageForLog :='PreTUPS SOS MIS successfully executed, Date Time:'||CURRENT_TIMESTAMP;
					aov_sqlerrMsgForLog :=' ';
					
					end if;
					
					EXCEPTION
					WHEN OTHERS THEN
						RAISE NOTICE '%','Error when checking for Underprocess or Ambigous transactions'||SQLERRM;
						aov_messageForLog:='Error when checking for Underprocess or Ambigous transactions, Date:'|| n_date_for_mis;
						aov_sqlerrMsgForLog:=SQLERRM;
					RAISE EXCEPTION  using errcode = 'ERR01';
				
					END;
				end if;
				EXCEPTION
				WHEN OTHERS THEN
				RAISE NOTICE '%','Error when checking if Underprocess or Ambigous transactions are found in the Transaction table for the date'||SQLERRM;
				aov_messageForLog:='Error when checking if Underprocess or Ambigous transactions are found in the Transaction table for the date, Date:'|| n_date_for_mis;
				aov_sqlerrMsgForLog:=SQLERRM;
				RAISE EXCEPTION  using errcode = 'ERR01'; 
				
				END;
			end if;
			
		EXCEPTION
		when sqlstate 'ERR02' then--exception handled in case MIS already executed
		aov_sqlerrMsgForLog:=SQLERRM;
		RAISE EXCEPTION  using errcode = 'ERR01';

		WHEN OTHERS THEN
			RAISE NOTICE '%','Error when checking if MIS process has already been executed'||SQLERRM;
			aov_messageForLog:='Error when checking if MIS process has already been executed, Date:'|| n_date_for_mis;
			aov_sqlerrMsgForLog:=SQLERRM;
		RAISE EXCEPTION  using errcode = 'ERR01';

		END;

		IF flag = 1 THEN
			n_date_for_mis := ld_to_date; ---If Underprocess or Ambigous transaction found then stop the MIS process for further execution of other dates
			RAISE EXCEPTION  using errcode = 'ERR01';
		ELSE
			n_date_for_mis:=n_date_for_mis+interval '1 day';
		END IF;
END LOOP;

EXCEPTION --Exception Handling of main procedure
	when sqlstate 'ERR01' then
		/* ROLLBACK; */
		RAISE NOTICE '%','EXCEPTION Caught='||SQLERRM;
		aov_message :='FAILED';

	WHEN OTHERS THEN
		/* ROLLBACK; */
		RAISE NOTICE '%','ERROR in Main procedure:='||SQLERRM;
		aov_message :='FAILED';
END;
$$;


ALTER FUNCTION pretupsdatabase.lmb_mis_summary_report_sp_get_lmbmis_data_dtrange(aiv_fromdate character varying, aiv_todate character varying, OUT aov_message character varying, OUT aov_messageforlog character varying, OUT aov_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: migrate_msisdns_in_mstr_tables(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION migrate_msisdns_in_mstr_tables() RETURNS void
    LANGUAGE plpgsql
    AS $$
 declare 
count bigint;
 master_table_list cursor is select distinct table_name from migrate_msisdn where table_name not in( select distinct table_name from USER_TAB_PARTITIONS) order by 1;

begin

--open master_table_list;
for tbl in master_table_list
	loop
		RAISE NOTICE '%','executing replace_msisdn_from_table('||tbl.table_name||')';
		perform replace_msisdn_from_table(tbl.table_name);
	end loop;
--close master_table_list;
end;
$$;


ALTER FUNCTION pretupsdatabase.migrate_msisdns_in_mstr_tables() OWNER TO pgdb;

--
-- Name: migrate_msisdns_in_tables(character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION migrate_msisdns_in_tables(p_month character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$
 declare count bigint;
prtn_table_list cursor  is select table_name,PARTITION_NAME from USER_TAB_PARTITIONS where table_name in( select distinct table_name from migrate_msisdn) and PARTITION_NAME like '%'||p_month||'%' order by 1;

begin

--open prtn_table_list;
for tbl in prtn_table_list
	loop
				RAISE NOTICE '%','executing replace_msisdn_from_table('||tbl.table_name||','||tbl.PARTITION_NAME||')';
				perform replace_msisdn_from_table(tbl.table_name,tbl.PARTITION_NAME);
	end loop;
--close prtn_table_list;
END;
$$;


ALTER FUNCTION pretupsdatabase.migrate_msisdns_in_tables(p_month character varying) OWNER TO pgdb;

--
-- Name: mis_move_to_final_data(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_move_to_final_data(p_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
   DECLARE 
   gd_createdon           DATE;
	gv_userid              TEMP_DAILY_CHNL_TRANS_MAIN.user_id%TYPE;
	gv_networkcode         TEMP_DAILY_CHNL_TRANS_MAIN.network_code%TYPE;
	gv_networkcodefor      TEMP_DAILY_CHNL_TRANS_MAIN.network_code_for%TYPE;
	gv_grphdomaincode      TEMP_DAILY_CHNL_TRANS_MAIN.grph_domain_code%TYPE;
	gv_productcode         TEMP_DAILY_CHNL_TRANS_MAIN.product_code%TYPE;
	gv_categorycode        TEMP_DAILY_CHNL_TRANS_MAIN.category_code%TYPE;
	gv_domaincode          TEMP_DAILY_CHNL_TRANS_MAIN.sender_domain_code%TYPE;
	gv_productmrp          TEMP_DAILY_CHNL_TRANS_MAIN.product_mrp%TYPE;
	user_rcd_count         numeric (5):= 0;
	
        gd_transaction_date    DATE;
      /* variables to store order related amounts */
      ln_roam_c2s_out           TEMP_DAILY_CHNL_TRANS_MAIN.roam_c2s_transfer_out_amount%TYPE;
      ln_c2s_trans_out_ct       TEMP_DAILY_CHNL_TRANS_MAIN.c2s_transfer_out_count%TYPE;
      ln_c2s_trans_out_amt      TEMP_DAILY_CHNL_TRANS_MAIN.c2s_transfer_out_amount%TYPE;
      ln_o2c_trans_in_ct        TEMP_DAILY_CHNL_TRANS_MAIN.o2c_transfer_in_count%TYPE;
      ln_o2c_trans_in_amt       TEMP_DAILY_CHNL_TRANS_MAIN.o2c_transfer_in_amount%TYPE;
      ln_o2c_return_out_ct      TEMP_DAILY_CHNL_TRANS_MAIN.o2c_return_out_count%TYPE;
      ln_o2c_return_out_amt     TEMP_DAILY_CHNL_TRANS_MAIN.o2c_return_out_amount%TYPE;
      ln_o2c_withdraw_out_ct    TEMP_DAILY_CHNL_TRANS_MAIN.o2c_withdraw_out_count%TYPE;
      ln_o2c_withdraw_out_amt   TEMP_DAILY_CHNL_TRANS_MAIN.o2c_withdraw_out_amount%TYPE;
      ln_c2c_transfer_in_ct     TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_in_count%TYPE;
      ln_c2c_transfer_in_amt    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_in_amount%TYPE;
      ln_c2c_transfer_out_ct    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_out_count%TYPE;
      ln_c2c_transfer_out_amt   TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_out_amount%TYPE;
      ln_c2c_return_in_ct       TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_in_count%TYPE;
      ln_c2c_return_in_amt      TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_in_amount%TYPE;
      ln_c2c_return_out_ct      TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_out_count%TYPE;
      ln_c2c_return_out_amt     TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_out_amount%TYPE;
      ln_c2c_withdraw_in_ct     TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_in_count%TYPE;
      ln_c2c_withdraw_in_amt    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_in_amount%TYPE;
      ln_c2c_withdraw_out_ct    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_out_count%TYPE;
      ln_c2c_withdraw_out_amt   TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_out_amount%TYPE;
      ln_differential           TEMP_DAILY_CHNL_TRANS_MAIN.differential%TYPE;
      ln_adjustment_in          TEMP_DAILY_CHNL_TRANS_MAIN.adjustment_in%TYPE;
      ln_adjustment_out         TEMP_DAILY_CHNL_TRANS_MAIN.adjustment_out%TYPE;
      ln_opening_stock          TEMP_DAILY_CHNL_TRANS_MAIN.opening_balance%TYPE;
      ln_closing_stock          DAILY_CHNL_TRANS_MAIN.closing_balance%TYPE;
      ln_stock_updated          DAILY_CHNL_TRANS_MAIN.closing_balance%TYPE;
      ln_productmrp             TEMP_DAILY_CHNL_TRANS_MAIN.product_mrp%TYPE;

      ln_c2c_reverse_in_ct     TEMP_DAILY_CHNL_TRANS_MAIN.c2c_reverse_in_count%TYPE;
      ln_c2c_reverse_in_amt    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_reverse_in_amount%TYPE;
      ln_c2c_reverse_out_ct    TEMP_DAILY_CHNL_TRANS_MAIN.c2c_reverse_out_count%TYPE;
      ln_c2c_reverse_out_amt   TEMP_DAILY_CHNL_TRANS_MAIN.c2c_reverse_out_amount%TYPE;
      ln_o2c_reverse_in_ct     TEMP_DAILY_CHNL_TRANS_MAIN.o2c_reverse_in_count%TYPE;
      ln_o2c_reverse_in_amt    TEMP_DAILY_CHNL_TRANS_MAIN.o2c_reverse_in_amount%TYPE;
      ln_o2c_reverse_out_ct    TEMP_DAILY_CHNL_TRANS_MAIN.o2c_reverse_out_count%TYPE;
      ln_o2c_reverse_out_amt   TEMP_DAILY_CHNL_TRANS_MAIN.o2c_reverse_out_amount%TYPE;
      ln_C2S_TRANSFER_IN_AMOUNT    TEMP_DAILY_CHNL_TRANS_MAIN.C2S_TRANSFER_IN_AMOUNT%TYPE;
      ln_C2S_TRANSFER_IN_COUNT    TEMP_DAILY_CHNL_TRANS_MAIN.C2S_TRANSFER_IN_COUNT%TYPE;
      ln_REV_DIFFERENTIAL   TEMP_DAILY_CHNL_TRANS_MAIN.REV_DIFFERENTIAL%TYPE;

      /* Cursor Declaration */
      declare  user_data CURSOR (p_date TIMESTAMP(0))
      IS
         SELECT user_id, trans_date, product_code, category_code,
                network_code, network_code_for, sender_domain_code,
                roam_c2s_transfer_out_amount, c2s_transfer_out_count,
                c2s_transfer_out_amount, o2c_transfer_in_count,
                o2c_transfer_in_amount, o2c_return_out_count,
                o2c_return_out_amount, o2c_withdraw_out_count,
                o2c_withdraw_out_amount, c2c_transfer_in_count,
                c2c_transfer_in_amount, c2c_transfer_out_count,
                c2c_transfer_out_amount, c2c_return_in_count,
                c2c_return_in_amount, c2c_return_out_count,
                c2c_return_out_amount, c2c_withdraw_in_count,
                c2c_withdraw_in_amount, c2c_withdraw_out_count,
                c2c_withdraw_out_amount, differential, adjustment_in,
                adjustment_out, created_on, opening_balance,product_mrp,
                grph_domain_code,c2c_reverse_in_count,c2c_reverse_in_amount,
        c2c_reverse_out_count,c2c_reverse_out_amount,o2c_reverse_in_count,
        o2c_reverse_in_amount,o2c_reverse_out_count,o2c_reverse_out_amount ,
        C2S_TRANSFER_IN_AMOUNT ,C2S_TRANSFER_IN_COUNT,REV_DIFFERENTIAL
       FROM TEMP_DAILY_CHNL_TRANS_MAIN;
          BEGIN
          gd_createdon := current_timestamp; 
--WHERE trans_date = p_date;
     raise notice 'MIS flow:start of mis_move_to_final_data';
     
      ln_closing_stock := 0;
      ln_stock_updated := 0;
     

      /* Iterate RETAILER_DATA cursor */
      FOR user_data_cur IN user_data (p_date)
      LOOP
         raise notice 'IN RETAILER CURSOR ';
         gv_userid := user_data_cur.user_id;
         --gd_transaction_date := p_date;
         gd_transaction_date := user_data_cur.trans_date;
         gv_productcode := user_data_cur.product_code;
         gv_categorycode := user_data_cur.category_code;
         gv_networkcode := user_data_cur.network_code;
         gv_networkcodefor := user_data_cur.network_code_for;
         gv_domaincode := user_data_cur.sender_domain_code;
         ln_roam_c2s_out := user_data_cur.roam_c2s_transfer_out_amount;
         ln_c2s_trans_out_ct := user_data_cur.c2s_transfer_out_count;
         ln_c2s_trans_out_amt := user_data_cur.c2s_transfer_out_amount;
         ln_o2c_trans_in_ct := user_data_cur.o2c_transfer_in_count;
         ln_o2c_trans_in_amt := user_data_cur.o2c_transfer_in_amount;
         ln_o2c_return_out_ct := user_data_cur.o2c_return_out_count;
         ln_o2c_return_out_amt := user_data_cur.o2c_return_out_amount;
         ln_o2c_withdraw_out_ct := user_data_cur.o2c_withdraw_out_count;
         ln_o2c_withdraw_out_amt := user_data_cur.o2c_withdraw_out_amount;
         ln_c2c_transfer_in_ct := user_data_cur.c2c_transfer_in_count;
         ln_c2c_transfer_in_amt := user_data_cur.c2c_transfer_in_amount;
         ln_c2c_transfer_out_ct := user_data_cur.c2c_transfer_out_count;
         ln_c2c_transfer_out_amt := user_data_cur.c2c_transfer_out_amount;
         ln_c2c_return_in_ct := user_data_cur.c2c_return_in_count;
         ln_c2c_return_in_amt := user_data_cur.c2c_return_in_amount;
         ln_c2c_return_out_ct := user_data_cur.c2c_return_out_count;
         ln_c2c_return_out_amt := user_data_cur.c2c_return_out_amount;
         ln_c2c_withdraw_in_ct := user_data_cur.c2c_withdraw_in_count;
         ln_c2c_withdraw_in_amt := user_data_cur.c2c_withdraw_in_amount;
         ln_c2c_withdraw_out_ct := user_data_cur.c2c_withdraw_out_count;
         ln_c2c_withdraw_out_amt := user_data_cur.c2c_withdraw_out_amount;
         ln_differential := user_data_cur.differential;
         ln_adjustment_in := user_data_cur.adjustment_in;
         ln_adjustment_out := user_data_cur.adjustment_out;
         ln_opening_stock := user_data_cur.opening_balance;
         gv_grphdomaincode := user_data_cur.grph_domain_code;
         ln_productmrp := user_data_cur.product_mrp;
         ln_c2c_reverse_in_ct := user_data_cur.c2c_reverse_in_count;
         ln_c2c_reverse_in_amt := user_data_cur.c2c_reverse_in_amount;
         ln_c2c_reverse_out_ct := user_data_cur.c2c_reverse_out_count;
         ln_c2c_reverse_out_amt := user_data_cur.c2c_reverse_out_amount;
     ln_o2c_reverse_in_ct := user_data_cur.o2c_reverse_in_count;
         ln_o2c_reverse_in_amt := user_data_cur.o2c_reverse_in_amount;
         ln_o2c_reverse_out_ct := user_data_cur.o2c_reverse_out_count;
         ln_o2c_reverse_out_amt := user_data_cur.o2c_reverse_out_amount;

         ---added on 12/09
         ln_C2S_TRANSFER_IN_AMOUNT := user_data_cur.C2S_TRANSFER_IN_AMOUNT;
         ln_C2S_TRANSFER_IN_COUNT  := user_data_cur.C2S_TRANSFER_IN_COUNT;
         ln_REV_DIFFERENTIAL  :=  user_data_cur.REV_DIFFERENTIAL;
         -- TO DO CHANGE THE FORMULAE
         ln_stock_updated :=
              ln_o2c_trans_in_amt
            + ln_c2c_transfer_in_amt
            + ln_c2c_return_in_amt
            + ln_c2c_withdraw_in_amt
            + ln_differential
            + ln_adjustment_in
            - ln_c2s_trans_out_amt
            - ln_o2c_return_out_amt
            - ln_o2c_withdraw_out_amt
            - ln_c2c_transfer_out_amt
            - ln_c2c_return_out_amt
            - ln_c2c_withdraw_out_amt
            - ln_adjustment_out
        + ln_c2c_reverse_in_amt --added for C2C reversal
        - ln_c2c_reverse_out_amt --added for C2C reversal
        + ln_o2c_reverse_in_amt --added for O2C reversal
        - ln_o2c_reverse_out_amt; --added for O2C reversal

     --ln_stock_updated := ln_stock_updated / ln_productmrp; TBD

         BEGIN
            IF (ln_stock_updated > 0)
            THEN
               ln_closing_stock := ln_opening_stock + ln_stock_updated;
            ELSE
               ln_closing_stock := ln_opening_stock + ln_stock_updated;
            END IF;
            
            BEGIN
                raise notice 'MIS flow:UPDATE DAILY_CHNL_TRANS_MAIN';
                UPDATE DAILY_CHNL_TRANS_MAIN
                SET c2s_transfer_out_count=c2s_transfer_out_count+ln_c2s_trans_out_ct,
                    c2s_transfer_out_amount=c2s_transfer_out_amount+ln_c2s_trans_out_amt,
                    differential=differential+ln_differential,
                    adjustment_in=adjustment_in+ln_adjustment_in, 
                    adjustment_out=adjustment_out+ln_adjustment_out,
                    C2S_TRANSFER_IN_AMOUNT=C2S_TRANSFER_IN_AMOUNT+ln_C2S_TRANSFER_IN_AMOUNT ,
                    C2S_TRANSFER_IN_COUNT=C2S_TRANSFER_IN_COUNT+ln_C2S_TRANSFER_IN_COUNT,
                    REV_DIFFERENTIAL=REV_DIFFERENTIAL+ln_REV_DIFFERENTIAL
                  WHERE user_id = gv_userid
                  AND network_code = gv_networkcode
                  AND network_code_for = gv_networkcodefor
                  AND product_code = gv_productcode
                  AND trans_date = gd_transaction_date;
                
                IF NOT FOUND 
                THEN
                raise notice 'inserting since data not found ';
                    INSERT INTO DAILY_CHNL_TRANS_MAIN
                        (user_id, trans_date, product_code,
                         category_code, network_code, network_code_for,
                         sender_domain_code, roam_c2s_transfer_out_amount,opening_balance,
                         closing_balance,c2s_transfer_out_count, c2s_transfer_out_amount,
                         o2c_transfer_in_count, o2c_transfer_in_amount,o2c_return_out_count,
                         o2c_return_out_amount,o2c_withdraw_out_count, o2c_withdraw_out_amount,
                         c2c_transfer_in_count, c2c_transfer_in_amount,c2c_transfer_out_count,
                         c2c_transfer_out_amount,c2c_return_in_count, c2c_return_in_amount,
                         c2c_return_out_count, c2c_return_out_amount,c2c_withdraw_in_count,
                         c2c_withdraw_in_amount,c2c_withdraw_out_count, c2c_withdraw_out_amount,
                         differential, adjustment_in, adjustment_out,
                         created_on, grph_domain_code, c2c_reverse_in_count,
                         c2c_reverse_in_amount,c2c_reverse_out_count, c2c_reverse_out_amount,
                         o2c_reverse_in_count, o2c_reverse_in_amount,o2c_reverse_out_count,
                         o2c_reverse_out_amount,C2S_TRANSFER_IN_AMOUNT ,C2S_TRANSFER_IN_COUNT,REV_DIFFERENTIAL
                        )
                    VALUES (gv_userid, gd_transaction_date, gv_productcode,
                         gv_categorycode, gv_networkcode, gv_networkcodefor,
                         gv_domaincode, ln_roam_c2s_out,
                         ln_opening_stock, ln_closing_stock,
                         ln_c2s_trans_out_ct, ln_c2s_trans_out_amt,
                         ln_o2c_trans_in_ct, ln_o2c_trans_in_amt,
                         ln_o2c_return_out_ct, ln_o2c_return_out_amt,
                         ln_o2c_withdraw_out_ct, ln_o2c_withdraw_out_amt,
                         ln_c2c_transfer_in_ct, ln_c2c_transfer_in_amt,
                         ln_c2c_transfer_out_ct, ln_c2c_transfer_out_amt,
                         ln_c2c_return_in_ct, ln_c2c_return_in_amt,
                         ln_c2c_return_out_ct, ln_c2c_return_out_amt,
                         ln_c2c_withdraw_in_ct, ln_c2c_withdraw_in_amt,
                         ln_c2c_withdraw_out_ct, ln_c2c_withdraw_out_amt,
                         ln_differential, ln_adjustment_in,
                         ln_adjustment_out, gd_createdon, gv_grphdomaincode,
                         ln_c2c_reverse_in_ct, ln_c2c_reverse_in_amt,
                         ln_c2c_reverse_out_ct, ln_c2c_reverse_out_amt,
                         ln_o2c_reverse_in_ct, ln_o2c_reverse_in_amt,
                         ln_o2c_reverse_out_ct, ln_o2c_reverse_out_amt,
                         ln_C2S_TRANSFER_IN_AMOUNT ,ln_C2S_TRANSFER_IN_COUNT, ln_REV_DIFFERENTIAL
                        );
                END IF;
            EXCEPTION
                WHEN OTHERS
                THEN
                    RAISE NOTICE '%',   'EXCEPTION in MOVE_TO_FINAL_DATA 1, User:' || gv_userid || SQLERRM;
                    v_messageforlog := 'Exception in MOVE_TO_FINAL_DATA 1, User:'|| gv_userid || gd_transaction_date;
                    v_sqlerrmsgforlog := SQLERRM;
                    RAISE EXCEPTION  using errcode = 'ERR05';
            END;
         EXCEPTION
            WHEN OTHERS
            THEN
               RAISE NOTICE '%',   'EXCEPTION CAUGHT in MOVE_TO_FINAL_DATA 2, User:' || gv_userid || SQLERRM;
               v_messageforlog := 'Exception in MOVE_TO_FINAL_DATA 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION  using errcode = 'ERR05';
         END;                                    --end of user insertion block
      END LOOP; 
      raise notice 'MIS flow :End of mis_move_to_final_data'; 
	v_messageforlog := 'mis_move_to_final_data SUCCESS';
	v_sqlerrmsgforlog := 'mis_move_to_final_data NO error ';                                                     --end of
   EXCEPTION
      when sqlstate 'ERR05' then
         RAISE NOTICE 'SQLexception in mis_sp_insert_opening_bal 3';
         RAISE EXCEPTION  using errcode = 'ERR01';
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'EXCEPTION in MOVE_TO_FINAL_DATA 3'|| SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
   END;  
     $$;


ALTER FUNCTION pretupsdatabase.mis_move_to_final_data(p_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_move_users_to_new_date(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_move_users_to_new_date(p_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
   DECLARE 
	gv_userid              TEMP_DAILY_CHNL_TRANS_MAIN.user_id%TYPE;
	gv_networkcode         TEMP_DAILY_CHNL_TRANS_MAIN.network_code%TYPE;
	gv_networkcodefor      TEMP_DAILY_CHNL_TRANS_MAIN.network_code_for%TYPE;
	gv_grphdomaincode      TEMP_DAILY_CHNL_TRANS_MAIN.grph_domain_code%TYPE;
	gv_productcode         TEMP_DAILY_CHNL_TRANS_MAIN.product_code%TYPE;
	gv_categorycode        TEMP_DAILY_CHNL_TRANS_MAIN.category_code%TYPE;
	gv_domaincode          TEMP_DAILY_CHNL_TRANS_MAIN.sender_domain_code%TYPE;
	gv_productmrp          TEMP_DAILY_CHNL_TRANS_MAIN.product_mrp%TYPE;
	user_rcd_count         numeric (5):= 0;
	
        gd_transaction_date    DATE;
	gd_createdon DATE;
        
      ld_closing_stock   DAILY_CHNL_TRANS_MAIN.closing_balance%TYPE;


      declare user_data CURSOR (pv_date TIMESTAMP(0))
      FOR
         SELECT dtr.user_id , dtr.product_code, dtr.network_code,
                dtr.category_code, dtr.sender_domain_code domain_code, dtr.network_code_for,
                dtr.grph_domain_code, dtr.closing_balance
           FROM DAILY_CHNL_TRANS_MAIN dtr, USERS us
          WHERE dtr.trans_date =pv_date-interval'1 day'
                      AND us.user_type = 'CHANNEL'
            AND dtr.user_id = us.user_id
           AND date_trunc('day',us.modified_on::TIMESTAMP) >=
	CASE WHEN (us.status='N') THEN pv_date ELSE date_trunc('day',us.modified_on::TIMESTAMP) END;
	
	


      BEGIN
        raise notice 'MIS flow:start of mis_move_users_to_new_date';
         gd_transaction_date := p_date;
         ld_closing_stock := 0;
         
        gd_createdon  :=CURRENT_TIMESTAMP;
	
         /* Iterate DIST_DATA cursor */
         FOR user_data_cur IN user_data (p_date)
         LOOP
         raise notice 'MIS flow:start of mis_move_users_to_new_date2';
            user_rcd_count := 0;                          --reinitialize to 0
            ld_closing_stock := user_data_cur.closing_balance;
            gv_userid := user_data_cur.user_id;
            gv_productcode := user_data_cur.product_code;
            gv_networkcode := user_data_cur.network_code;
            gv_networkcodefor := user_data_cur.network_code_for;
            gv_grphdomaincode := user_data_cur.grph_domain_code;
            ld_closing_stock := user_data_cur.closing_balance;
            gv_categorycode := user_data_cur.category_code;
            gv_domaincode := user_data_cur.domain_code;

            BEGIN
               BEGIN
               
                  user_rcd_count=(SELECT 1 
                  FROM DAILY_CHNL_TRANS_MAIN
                  WHERE user_id = gv_userid
                  AND network_code = gv_networkcode
                  AND network_code_for = gv_networkcodefor
                  AND product_code = gv_productcode
                  AND trans_date = gd_transaction_date);
                 raise notice 'user_rcd_count=%',user_rcd_count; 
		IF user_rcd_count is null then 
		raise notice 'setting user_rcd_count=0'; 
                       user_rcd_count := 0;
                  END IF;
                 
               EXCEPTION
                  
                  WHEN OTHERS
                  THEN
                  RAISE NOTICE '%','EXCEPTION while selecting in MOVE_USERS_TO_NEW_DATE 2, User:' || gv_userid || SQLERRM;
                     v_messageforlog :='EXCEPTION while selecting data in MOVE_USERS_TO_NEW_DATE 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                     v_sqlerrmsgforlog := SQLERRM;
                     RAISE EXCEPTION  using errcode = 'ERR07';
               END;

               IF user_rcd_count = 0
               THEN
               raise notice 'if user_rcd_count=0 then inserting in DAILY_CHNL_TRANS_MAIN';
                  INSERT INTO DAILY_CHNL_TRANS_MAIN
                              (user_id, trans_date,
                               product_code, category_code,
                               network_code, network_code_for,
                               sender_domain_code, created_on,
                               grph_domain_code, opening_balance,
                               closing_balance
                              )
                       VALUES (gv_userid, gd_transaction_date,
                               gv_productcode, gv_categorycode,
                               gv_networkcode, gv_networkcodefor,
                               gv_domaincode, gd_createdon,
                               gv_grphdomaincode, ld_closing_stock,
                               ld_closing_stock
                              );
                              raise notice 'Inserted into DAILY_CHNL_TRANS_MAIN for mis_move_users_to_new_date';
               END IF;
             
            EXCEPTION
            when sqlstate 'ERR07' then
                  raise notice  'sqlexception while inserting in MOVE_USERS_TO_NEW_DATE 3, User:%,error:%',gv_userid,SQLERRM;
                  v_messageforlog := 'sqlexception while inserting in MOVE_USERS_TO_NEW_DATE 3, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                 RAISE EXCEPTION  using errcode = 'ERR05';
              
               WHEN OTHERS
               THEN
                  RAISE NOTICE '%','OTHERS CAUGHT in MOVE_USERS_TO_NEW_DATE 3, User' || gv_userid || SQLERRM;
                  v_messageforlog := 'Others Exception in MOVE_USERS_TO_NEW_DATE 3, User' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                 RAISE EXCEPTION  using errcode = 'ERR05';
            END;--end of user insertion block
         END LOOP;--end of USER_DATA Loop
                 raise notice 'MOVE_USERS_TO_NEW_DATE SUCCESS';
	   v_messageforlog := 'MOVE_USERS_TO_NEW_DATE SUCCESS';
	v_sqlerrmsgforlog := 'MOVE_USERS_TO_NEW_DATE NO error ';  
    

   EXCEPTION
    when sqlstate 'ERR05' then
         raise notice '%','procexception in MOVE_USERS_TO_NEW_DATE 4'||SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
     
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'EXCEPTION in MOVE_USERS_TO_NEW_DATE 4'||SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
   END;

 $$;


ALTER FUNCTION pretupsdatabase.mis_move_users_to_new_date(p_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_refills_lms_c2s_summary_proc(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_refills_lms_c2s_summary_proc(p_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
   DECLARE 
   gv_userid              TEMP_DAILY_CHNL_TRANS_MAIN.user_id%TYPE;
   gd_transaction_date    DATE;
      /* Variables for refill amount values */
      lv_productCode            DAILY_C2S_LMS_SUMMARY.PRODUCT_CODE%TYPE;
      lv_lmsProfiletype         DAILY_C2S_LMS_SUMMARY.LMS_PROFILE%TYPE;
      ln_c2s_trans_ct           DAILY_C2S_LMS_SUMMARY.transaction_count%TYPE= 0;
      ln_c2s_trans_amt          DAILY_C2S_LMS_SUMMARY.transaction_amount%TYPE= 0;
          /* Cursor Declaration */
      declare refill_lms_data CURSOR (p_date TIMESTAMP(0))
      FOR
        SELECT  CT.sender_id,CT.PRODUCT_CODE,CT.LMS_PROFILE, COUNT (CT.transfer_id) c2s_count, SUM(CT.transfer_value) c2s_amount
        FROM   C2S_TRANSFERS_MISTMP CT
        WHERE CT.transfer_date = p_date                  
        AND  CT.transfer_status = '200'
        AND  CT.transfer_type = 'TXN'
        and    CT.LMS_PROFILE is not NULL    
        GROUP BY CT.sender_id,CT.PRODUCT_CODE,CT.LMS_PROFILE;
   BEGIN

     
      FOR ret_data_cur IN refill_lms_data (p_date)
      LOOP
         gv_userid := ret_data_cur.sender_id;
         lv_productCode := ret_data_cur.PRODUCT_CODE;
         lv_lmsProfiletype := ret_data_cur.LMS_PROFILE;
         gd_transaction_date := p_date;
         ln_c2s_trans_ct := ret_data_cur.c2s_count;
         ln_c2s_trans_amt := ret_data_cur.c2s_amount;
        
         BEGIN
            INSERT INTO DAILY_C2S_LMS_SUMMARY (user_id, trans_date, PRODUCT_CODE, LMS_PROFILE, transaction_amount, transaction_count)
                VALUES (gv_userid, gd_transaction_date, lv_productCode, lv_lmsProfiletype, ln_c2s_trans_amt, ln_c2s_trans_ct);
         EXCEPTION
  
            WHEN OTHERS
            THEN
               RAISE NOTICE '%', 'OTHERS EXCEPTION in refills_lms_c2s_summary_proc 2, User:' || gv_userid || SQLERRM;
               v_messageforlog := 'OTHERS EXCEPTION in refills_lms_c2s_summary_proc 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION  using errcode = 'ERR05';
         END;                           
     END LOOP;
      raise notice 'refills_lms_c2s_summary_proc SUCCESS';
	   v_messageforlog := 'refills_lms_c2s_summary_proc SUCCESS';
	v_sqlerrmsgforlog := 'refills_lms_c2s_summary_proc NO error ';  
   EXCEPTION
	when sqlstate 'ERR05' then
         RAISE NOTICE 'procexception in refills_lms_c2s_summary_proc 3:%',SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION in refills_lms_c2s_summary_proc 4:' || SQLERRM;
          RAISE EXCEPTION  using errcode = 'ERR01';
   END;
$$;


ALTER FUNCTION pretupsdatabase.mis_refills_lms_c2s_summary_proc(p_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_ret_refills_data_proc(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_ret_refills_data_proc(p_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
   DECLARE
	gv_userid              TEMP_DAILY_CHNL_TRANS_MAIN.user_id%TYPE;
	gv_networkcode         TEMP_DAILY_CHNL_TRANS_MAIN.network_code%TYPE;
	gv_networkcodefor      TEMP_DAILY_CHNL_TRANS_MAIN.network_code_for%TYPE;
	gv_grphdomaincode      TEMP_DAILY_CHNL_TRANS_MAIN.grph_domain_code%TYPE;
	gv_productcode         TEMP_DAILY_CHNL_TRANS_MAIN.product_code%TYPE;
	gv_categorycode        TEMP_DAILY_CHNL_TRANS_MAIN.category_code%TYPE;
	gv_domaincode          TEMP_DAILY_CHNL_TRANS_MAIN.sender_domain_code%TYPE;
	gv_productmrp          TEMP_DAILY_CHNL_TRANS_MAIN.product_mrp%TYPE;
	user_rcd_count         numeric (5):= 0;
	gd_createdon           DATE;
	
        gd_transaction_date    DATE;
        
      /* Variables for refill amount values */
      lv_servicetype            DAILY_C2S_TRANS_DETAILS.SERVICE_TYPE%TYPE;
      lv_sub_service            DAILY_C2S_TRANS_DETAILS.SUB_SERVICE%TYPE;
      ln_c2s_trans_ct           DAILY_C2S_TRANS_DETAILS.transaction_count%TYPE= 0;
      ln_c2s_trans_amt          DAILY_C2S_TRANS_DETAILS.transaction_amount%TYPE= 0;
      ln_c2s_trans_tax1         DAILY_C2S_TRANS_DETAILS.total_tax1%TYPE  = 0;
      ln_c2s_trans_tax2         DAILY_C2S_TRANS_DETAILS.total_tax2%TYPE  = 0;
      ln_c2s_trans_tax3         DAILY_C2S_TRANS_DETAILS.total_tax3%TYPE  = 0;
      ln_c2s_sender_trans_amt   DAILY_C2S_TRANS_DETAILS.sender_transfer_amount%TYPE= 0;
      ln_c2s_rec_credit_amt     DAILY_C2S_TRANS_DETAILS.receiver_credit_amount%TYPE= 0;
      ln_c2s_rec_access_fee     DAILY_C2S_TRANS_DETAILS.receiver_access_fee%TYPE= 0;
      ln_c2s_diff_tax1          DAILY_C2S_TRANS_DETAILS.differential_adjustment_tax1%TYPE= 0;
      ln_c2s_diff_tax2          DAILY_C2S_TRANS_DETAILS.differential_adjustment_tax2%TYPE= 0;
      ln_c2s_diff_tax3          DAILY_C2S_TRANS_DETAILS.differential_adjustment_tax3%TYPE= 0;
      ln_c2s_receiver_bonus     DAILY_C2S_TRANS_DETAILS.receiver_bonus%TYPE= 0;
      ln_c2s_diff_amt           DAILY_C2S_TRANS_DETAILS.differential_amount%TYPE= 0;
      ln_c2s_diff_count           DAILY_C2S_TRANS_DETAILS.differential_count%TYPE= 0;
      ln_roam_c2s_amount        DAILY_CHNL_TRANS_MAIN.roam_c2s_transfer_out_amount%TYPE= 0;
      lv_sendercategory DAILY_C2S_TRANS_DETAILS.sender_category_code%TYPE =0;
      lv_receiverserviceclassid DAILY_C2S_TRANS_DETAILS.receiver_service_class_id%TYPE =0;
      ln_receiver_validity        DAILY_C2S_TRANS_DETAILS.receiver_validity%TYPE=0;
      ln_receiver_bonus_validity    DAILY_C2S_TRANS_DETAILS.receiver_bonus_validity%TYPE=0;
      ln_c2s_sender_penalty DAILY_C2S_TRANS_DETAILS.PENALTY%TYPE=0; 
      ln_c2s_sender_ownwer_penalty DAILY_C2S_TRANS_DETAILS.OWNER_PENALTY%TYPE=0; 
      ln_c2s_sender_roam_amount DAILY_C2S_TRANS_DETAILS.Roam_amount%TYPE=0; 
      ln_c2s_penalty_count DAILY_C2S_TRANS_DETAILS.PENALTY_COUNT%TYPE=0;


      /* Cursor Declaration */
      declare  refill_data  CURSOR (p_date TIMESTAMP(0))
      FOR
    SELECT   Mast.transfer_date,Mast.sender_id, Mast.network_code,Mast.service_class_id,
                  Mast.receiver_network_code, Mast.product_code,
                  Mast.unit_value,Mast.grph_domain_code, Mast.category_code,
                  Mast.domain_code, Mast.SERVICE_TYPE,Mast.sub_service,
                  COUNT (Mast.transfer_id) c2s_count,
                  SUM(Mast.transfer_value) c2s_amount,
                  COALESCE(SUM(Mast.receiver_transfer_value),0) c2s_rectrans,
                  COALESCE(SUM(Mast.receiver_access_fee),0) c2s_recfee,
                  COALESCE(SUM(Mast.receiver_tax1_value),0) c2s_rectax1,
                  COALESCE(SUM(Mast.receiver_tax2_value),0) c2s_rectax2,
                  COALESCE(SUM(Mast.sender_transfer_value),0) c2s_sender_value,
                  COALESCE(SUM(Mast.receiver_bonus_value),0) c2s_rec_bonus,
                  COALESCE(SUM(adj.tax1_value),0) diff_tax1,
                  COALESCE(SUM(adj.tax2_value),0) diff_tax2,
                  COALESCE(SUM(adj.tax3_value),0) diff_tax3,
                  COALESCE(SUM(adj.transfer_value),0) diff_amount,
                  SUM(CASE  WHEN COALESCE(adj.transfer_value,0) <>0 THEN 1 ELSE 0 END) diff_count,
                  COALESCE(SUM(Mast.receiver_validity),0) validity,
                  COALESCE(SUM(Mast.receiver_bonus_validity),0) bonus_validity,
                  COALESCE(SUM(Mast.PENALTY),0) PENALTY,
                  COALESCE(SUM(Mast.OWNER_PENALTY),0) OWNER_PENALTY,
                  COALESCE(SUM(case when COALESCE(PENALTY,0)>0 then 1 else 0 end),0) PENALTY_COUNT
          FROM (SELECT
                  c2strans.transfer_date,c2strans.sender_id, c2strans.network_code,c2strans.service_class_id,
                  c2strans.receiver_network_code, p.product_code,
                  p.unit_value,ug.grph_domain_code, cat.category_code,
                  cat.domain_code, c2strans.SERVICE_TYPE,c2strans.sub_service,
                  c2strans.transfer_id,c2strans.transfer_value,c2strans.receiver_transfer_value,
                  c2strans.receiver_access_fee,c2strans.receiver_tax1_value,c2strans.receiver_tax2_value,
                  c2strans.sender_transfer_value,c2strans.receiver_bonus_value,c2strans.receiver_validity,
                  c2strans.receiver_bonus_validity,
                  c2strans.PENALTY,
                  c2strans.owner_penalty
        FROM   C2S_TRANSFERS_MISTMP c2strans,PRODUCTS P,
        CATEGORIES cat,USER_GEOGRAPHIES ug
        WHERE --(c2strans.transfer_date = p_date or c2strans.reconciliation_date = p_date) AND
          c2strans.transfer_status = '200'
        AND  c2strans.transfer_type = 'TXN'
        AND   p.product_code=c2strans.product_code
        AND   cat.category_code=c2strans.sender_category
        AND ug.user_id=c2strans.sender_id
        ) Mast left join ADJUSTMENTS_MISTMP adj on (Mast.transfer_id =adj.reference_id and Mast.sender_id = adj.user_id)
        
        GROUP BY Mast.transfer_date,Mast.sender_id, Mast.network_code, Mast.receiver_network_code,Mast.product_code,Mast.grph_domain_code,
             Mast.category_code,Mast.domain_code,Mast.SERVICE_TYPE,Mast.sub_service,Mast.service_class_id,Mast.unit_value;


   BEGIN
    gd_createdon := current_timestamp;      
	RAISE notice 'inside  mis_ret_refills_data_proc';
      /* Iterate thru. the Refill cursor */
      FOR ret_data_cur IN refill_data (p_date)
      LOOP
         user_rcd_count := 0;                             --reinitialize to 0
         --cnt := 0;
         lv_sendercategory := ret_data_cur.category_code;
         lv_receiverserviceclassid := ret_data_cur.service_class_id;
         gv_userid := ret_data_cur.sender_id;
         gv_productcode := ret_data_cur.product_code;
         gv_networkcode := ret_data_cur.network_code;
         gv_networkcodefor := ret_data_cur.receiver_network_code;
         gv_grphdomaincode := ret_data_cur.grph_domain_code;
         gv_categorycode := ret_data_cur.category_code;
         gv_domaincode := ret_data_cur.domain_code;
         lv_servicetype := ret_data_cur.SERVICE_TYPE;
     lv_sub_service:= ret_data_cur.sub_service;
         --gd_transaction_date := p_date;
         gd_transaction_date := ret_data_cur.transfer_date;
         ln_c2s_trans_ct := ret_data_cur.c2s_count;
         ln_c2s_trans_amt := ret_data_cur.c2s_amount;
         ln_c2s_trans_tax1 := ret_data_cur.c2s_rectax1;
         ln_c2s_trans_tax2 := ret_data_cur.c2s_rectax2;
         ln_c2s_trans_tax3 := 0;
         ln_c2s_sender_trans_amt := ret_data_cur.c2s_sender_value;
         ln_c2s_rec_credit_amt := ret_data_cur.c2s_rectrans;
         ln_c2s_rec_access_fee := ret_data_cur.c2s_recfee;
         ln_c2s_diff_tax1 := ret_data_cur.diff_tax1;
         ln_c2s_diff_tax2 := ret_data_cur.diff_tax2;
         ln_c2s_diff_tax3 := ret_data_cur.diff_tax3;
         ln_c2s_receiver_bonus := ret_data_cur.c2s_rec_bonus;
         ln_c2s_diff_amt := ret_data_cur.diff_amount;
         ln_receiver_validity := ret_data_cur.validity;
         ln_receiver_bonus_validity := ret_data_cur.bonus_validity;
         gv_productmrp := ret_data_cur.unit_value;
         ln_c2s_diff_count:= ret_data_cur.diff_count;
         ln_c2s_sender_penalty:=ret_data_cur.penalty;
         ln_c2s_sender_ownwer_penalty:=ret_data_cur.OWNER_PENALTY ; 
         ln_c2s_sender_roam_amount:=0;
         ln_c2s_penalty_count:=ret_data_cur.PENALTY_COUNT ; 
         
         IF (gv_networkcode <> gv_networkcodefor)
            THEN
            ln_c2s_sender_roam_amount:=ln_c2s_trans_amt;
        END IF;

         /* insert into temp_retailer_mis table */
         BEGIN
         RAISE notice 'inside  mis_ret_refills_data_proc getting user count';
               user_rcd_count=(SELECT 1
               FROM DAILY_C2S_TRANS_DETAILS
               WHERE user_id = gv_userid    ----  ,sender_category_code, receiver_service_class_id,SERVICE_TYPE, sub_service,
               AND receiver_network_code = gv_networkcodefor
               AND trans_date = gd_transaction_date
               AND sender_category_code=gv_categorycode
               AND receiver_service_class_id=lv_receiverserviceclassid
               AND SERVICE_TYPE=lv_servicetype
               AND sub_service=lv_sub_service);
		raise notice 'user_rcd_count=%',user_rcd_count;
		if user_rcd_count is null then                        --when no row returned for the user
                 --cnt := 0;
                 raise notice 'No Record found for User.';
                 user_rcd_count := 0;
                end if;

               EXCEPTION 
               WHEN OTHERS
               THEN
                  RAISE NOTICE '%', 'Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid;
                  v_messageforlog := 'Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE EXCEPTION  using errcode = 'ERR07';
            END;

            --IF cnt = 0
            IF user_rcd_count = 0
            THEN
                BEGIN
                RAISE notice 'mis_ret_refills_data_proc: inserting into DAILY_C2S_TRANS_DETAILS';
                    INSERT INTO DAILY_C2S_TRANS_DETAILS
                        (user_id, trans_date, receiver_network_code,
                         sender_category_code, receiver_service_class_id,
                         SERVICE_TYPE, sub_service, total_tax1,
                         total_tax2, total_tax3,
                         sender_transfer_amount, receiver_credit_amount,
                         receiver_access_fee, differential_adjustment_tax1,
                         differential_adjustment_tax2,
                         differential_adjustment_tax3, receiver_bonus,
                         created_on, transaction_amount, transaction_count,
                         differential_amount,receiver_validity,receiver_bonus_validity,differential_count,
                          penalty,owner_penalty,roam_amount,penalty_count
                        )
                    VALUES (gv_userid, gd_transaction_date, gv_networkcodefor,
                          lv_sendercategory,lv_receiverserviceclassid,
                         lv_servicetype, lv_sub_service,ln_c2s_trans_tax1,
                         ln_c2s_trans_tax2, ln_c2s_trans_tax3,
                         ln_c2s_sender_trans_amt, ln_c2s_rec_credit_amt,
                         ln_c2s_rec_access_fee, ln_c2s_diff_tax1,
                         ln_c2s_diff_tax2,
                         ln_c2s_diff_tax3, ln_c2s_receiver_bonus,
                         gd_createdon, ln_c2s_trans_amt, ln_c2s_trans_ct,
                         ln_c2s_diff_amt,ln_receiver_validity,
                         ln_receiver_bonus_validity,ln_c2s_diff_count,
                         ln_c2s_sender_penalty,ln_c2s_sender_ownwer_penalty,
                         ln_c2s_sender_roam_amount,ln_c2s_penalty_count
                        );
                        RAISE notice 'mis_ret_refills_data_proc: inserted into DAILY_C2S_TRANS_DETAILS';
                EXCEPTION
                when sqlstate 'ERR07' then
                    
                        RAISE NOTICE 'sqlexception in RET_REFILLS_DATA_PROC 1, User:%,Error:%',gv_userid,SQLERRM;
                        v_messageforlog :='sqlexception in RET_REFILLS_DATA_procedure 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
                        v_sqlerrmsgforlog := SQLERRM;
                       RAISE EXCEPTION  using errcode = 'ERR05';
                    WHEN OTHERS
                    THEN
                        RAISE NOTICE '%', ' EXCEPTION in RET_REFILLS_DATA_PROC 1, User:' || gv_userid || SQLERRM;
                        v_messageforlog := 'EXCEPTION in RET_REFILLS_DATA_procedure 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
                        v_sqlerrmsgforlog := SQLERRM;
                        RAISE EXCEPTION  using errcode = 'ERR05';
                END;                                --end of retailer insertion block
            ELSE
		RAISE notice 'mis_ret_refills_data_proc: updating into DAILY_C2S_TRANS_DETAILS';
                UPDATE DAILY_C2S_TRANS_DETAILS
                SET total_tax1=total_tax1+ln_c2s_trans_tax1,
                    total_tax2=total_tax2+ln_c2s_trans_tax2,
                    total_tax3=total_tax3+ln_c2s_trans_tax3,
                    sender_transfer_amount=sender_transfer_amount+ln_c2s_sender_trans_amt,
                    receiver_credit_amount=receiver_credit_amount+ln_c2s_rec_credit_amt,
                    receiver_access_fee=receiver_access_fee+ln_c2s_rec_access_fee,
                    differential_adjustment_tax1=differential_adjustment_tax1+ln_c2s_diff_tax1,
                    differential_adjustment_tax2=differential_adjustment_tax2+ln_c2s_diff_tax2,
                    differential_adjustment_tax3=differential_adjustment_tax3+ln_c2s_diff_tax3,
                    receiver_bonus=receiver_bonus+ln_c2s_receiver_bonus,
                    transaction_amount=transaction_amount+ln_c2s_trans_amt,
                    transaction_count=transaction_count+ln_c2s_trans_ct,
                    differential_amount=differential_amount+ln_c2s_diff_amt,
                    differential_count=differential_count+ln_c2s_diff_count,
                    penalty=penalty+ln_c2s_sender_penalty,
                    owner_penalty=owner_penalty+ln_c2s_sender_ownwer_penalty,
                    roam_amount=roam_amount+ln_c2s_sender_roam_amount,
                    penalty_count=penalty_count+ln_c2s_penalty_count
                WHERE user_id = gv_userid
                AND receiver_network_code = gv_networkcodefor
                AND sender_category_code = lv_sendercategory
                AND receiver_service_class_id = lv_receiverserviceclassid
                AND SERVICE_TYPE = lv_servicetype
                AND sub_service = lv_sub_service
                AND trans_date = gd_transaction_date;
                RAISE notice 'mis_ret_refills_data_proc: updated into DAILY_C2S_TRANS_DETAILS';
            END IF;
            
         BEGIN
            BEGIN
             RAISE notice 'mis_ret_refills_data_proc: select from TEMP_DAILY_CHNL_TRANS_MAIN';
               user_rcd_count=(SELECT 1 
               FROM TEMP_DAILY_CHNL_TRANS_MAIN
               WHERE user_id = gv_userid
               AND network_code = gv_networkcode
               AND network_code_for = gv_networkcodefor
               AND product_code = gv_productcode
               AND grph_domain_code = gv_grphdomaincode
               AND trans_date = gd_transaction_date);

              raise notice 'user_rcd_count=%',user_rcd_count;
		if user_rcd_count is null then                        --when no row returned for the user
                 --cnt := 0;
                 raise notice 'No Record found for User.';                      --when no row returned for the user
                 user_rcd_count := 0;
                 end if;

               EXCEPTION
               WHEN OTHERS
               THEN
                  RAISE NOTICE '%', 'Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid;
                  v_messageforlog := 'Exception in RET_REFILLS_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                   RAISE EXCEPTION  using errcode = 'ERR07';
            END;

            IF user_rcd_count = 0
            THEN
               INSERT INTO TEMP_DAILY_CHNL_TRANS_MAIN
                           (user_id, trans_date, product_code,
                            category_code, network_code,
                            network_code_for, sender_domain_code,ROAM_C2S_TRANSFER_OUT_AMOUNT,
                            created_on, product_mrp,grph_domain_code,
                            c2s_transfer_out_count, c2s_transfer_out_amount,
                            differential
                           )
                    VALUES (gv_userid, gd_transaction_date, gv_productcode,
                            gv_categorycode, gv_networkcode,
                            gv_networkcodefor, gv_domaincode,ln_c2s_sender_roam_amount,
                            gd_createdon, gv_productmrp,gv_grphdomaincode,
                            ln_c2s_trans_ct, ln_c2s_trans_amt,
                            ln_c2s_diff_amt
                           );
            ELSE
               UPDATE TEMP_DAILY_CHNL_TRANS_MAIN
                  SET c2s_transfer_out_count = c2s_transfer_out_count + ln_c2s_trans_ct,
                      c2s_transfer_out_amount = c2s_transfer_out_amount + ln_c2s_trans_amt,
                      differential = differential + ln_c2s_diff_amt,
                      ROAM_C2S_TRANSFER_OUT_AMOUNT = ROAM_C2S_TRANSFER_OUT_AMOUNT + ln_c2s_sender_roam_amount
                WHERE user_id = gv_userid
                  AND trans_date = gd_transaction_date
                  AND product_code = gv_productcode
                  AND network_code = gv_networkcode
                  AND network_code_for = gv_networkcodefor;
            END IF;

            --Update Roam Counts
            /*IF (gv_networkcode <> gv_networkcodefor)
            THEN
               sp_update_roam_c2s_trans (gv_userid,
                                         gd_transaction_date,
                                         gv_productcode,
                                         gv_categorycode,
                                         gv_networkcode,
                                         gv_networkcodefor,
                                         gv_domaincode,
                                         gv_productmrp,
                                         gv_grphdomaincode,
                                         ln_c2s_trans_amt
                                        );
            END IF;*/
         EXCEPTION
		when sqlstate 'ERR07' then
               raise notice 'sqlexception SQL/OTHERS EXCEPTION in RET_REFILLS_DATA_PROC 3, User:%,error:%',gv_userid,SQLERRM;
               v_messageforlog := 'sqlexception Exception in RET_REFILLS_DATA_PROC 3, User:' || gv_userid || ' date=' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION  using errcode = 'ERR05';
            WHEN OTHERS
            THEN
               RAISE NOTICE ' EXCEPTION in RET_REFILLS_DATA_PROC 3, User:%,error:%',gv_userid,SQLERRM;
               v_messageforlog :='Exception in RET_REFILLS_DATA_PROC 3, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION  using errcode = 'ERR05';
         END;                           --end of distributor insertion block
      END LOOP;
   EXCEPTION
   when sqlstate 'ERR05' then
         raise notice 'procexception in RET_REFILLS_DATA_PROC 4:%',SQLERRM;
        RAISE EXCEPTION  using errcode = 'ERR01';
      WHEN OTHERS
      THEN
         RAISE NOTICE 'EXCEPTION in RET_REFILLS_DATA_PROC 4:%',SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
   END;     
        $$;


ALTER FUNCTION pretupsdatabase.mis_ret_refills_data_proc(p_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_ret_refills_failure_data_proc(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_ret_refills_failure_data_proc(p_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
   DECLARE 
    gd_createdon           DATE;
   gv_userid              TEMP_DAILY_CHNL_TRANS_MAIN.user_id%TYPE;
	gv_networkcode         TEMP_DAILY_CHNL_TRANS_MAIN.network_code%TYPE;
	gv_networkcodefor      TEMP_DAILY_CHNL_TRANS_MAIN.network_code_for%TYPE;
	gv_grphdomaincode      TEMP_DAILY_CHNL_TRANS_MAIN.grph_domain_code%TYPE;
	gv_productcode         TEMP_DAILY_CHNL_TRANS_MAIN.product_code%TYPE;
	gv_categorycode        TEMP_DAILY_CHNL_TRANS_MAIN.category_code%TYPE;
	gv_domaincode          TEMP_DAILY_CHNL_TRANS_MAIN.sender_domain_code%TYPE;
	gv_productmrp          TEMP_DAILY_CHNL_TRANS_MAIN.product_mrp%TYPE;
	user_rcd_count         numeric (5):= 0;
	
        gd_transaction_date    DATE;
      /* Variables for refill amount values */
      lv_servicetype              DAILY_C2S_TRANS_DETAILS.SERVICE_TYPE%TYPE;
      lv_subservice              DAILY_C2S_TRANS_DETAILS.SUB_SERVICE%TYPE;
      ln_c2s_failure_ct           DAILY_C2S_TRANS_DETAILS.failure_count%TYPE= 0;
      lv_sendercategory           DAILY_C2S_TRANS_DETAILS.sender_category_code%TYPE =0;
      lv_receiverserviceclassid   DAILY_C2S_TRANS_DETAILS.receiver_service_class_id%TYPE =0;


      /* Cursor Declaration */
      declare refill_failure_data CURSOR (p_date TIMESTAMP(0))
      FOR
        SELECT
                c2strans.transfer_date,c2strans.sender_id, c2strans.network_code,COALESCE(c2strans.service_class_id,' ') service_class_id,
                c2strans.receiver_network_code, p.product_code,
                p.unit_value,ug.grph_domain_code, cat.category_code,cat.domain_code, c2strans.SERVICE_TYPE,c2strans.SUB_SERVICE,
                COUNT(c2strans.transfer_id) c2s_failure_count
                FROM   C2S_TRANSFERS_MISTMP c2strans,PRODUCTS P,CATEGORIES cat,USER_GEOGRAPHIES ug
                WHERE --c2strans.transfer_date = p_date AND
                 c2strans.transfer_status = '206'
                AND p.product_code=c2strans.product_code
                AND cat.category_code=c2strans.sender_category
                AND ug.user_id=c2strans.sender_id
                GROUP BY c2strans.transfer_date,c2strans.sender_id, c2strans.network_code, c2strans.receiver_network_code, p.product_code, ug.grph_domain_code,
                cat.category_code, cat.domain_code, c2strans.SERVICE_TYPE,c2strans.SUB_SERVICE, service_class_id, p.unit_value;

   BEGIN
    gd_createdon := current_timestamp;  
RAISE notice 'inside  mis_ret_refills_failure_data_proc';
      /* Iterate thru. the Refill_failure cursor */
      FOR ret_data_cur IN refill_failure_data (p_date)
      LOOP
         user_rcd_count := 0;                             --reinitialize to 0
         lv_sendercategory := ret_data_cur.category_code;
     lv_receiverserviceclassid := ret_data_cur.service_class_id;
     gv_userid := ret_data_cur.sender_id;
         gv_productcode := ret_data_cur.product_code;
         gv_networkcode := ret_data_cur.network_code;
         gv_networkcodefor := ret_data_cur.receiver_network_code;
         gv_grphdomaincode := ret_data_cur.grph_domain_code;
         gv_categorycode := ret_data_cur.category_code;
         gv_domaincode := ret_data_cur.domain_code;
         lv_servicetype := ret_data_cur.SERVICE_TYPE;
     lv_subservice := ret_data_cur.SUB_SERVICE;
         --gd_transaction_date := p_date;
          gd_transaction_date := ret_data_cur.transfer_date;
         ln_c2s_failure_ct := ret_data_cur.c2s_failure_count;
         gv_productmrp := ret_data_cur.unit_value;

    BEGIN
    RAISE notice ' UPDATE DAILY_C2S_TRANS_DETAILS';
            UPDATE DAILY_C2S_TRANS_DETAILS
               SET
                   failure_count = ln_c2s_failure_ct
        WHERE user_id = gv_userid
        AND trans_date = gd_transaction_date
        AND receiver_network_code = gv_networkcodefor
            AND receiver_service_class_id = lv_receiverserviceclassid
        AND SERVICE_TYPE = lv_servicetype
            AND SUB_SERVICE = lv_subservice
                AND sender_category_code = lv_sendercategory;

         IF NOT FOUND 
         THEN
         RAISE notice ' INSERT DAILY_C2S_TRANS_DETAILS';
               INSERT INTO DAILY_C2S_TRANS_DETAILS
                        (user_id, trans_date, receiver_network_code,
             sender_category_code, receiver_service_class_id,
                         SERVICE_TYPE, SUB_SERVICE,total_tax1,
                         total_tax2, total_tax3,
                         sender_transfer_amount, receiver_credit_amount,
                         receiver_access_fee, differential_adjustment_tax1,
                         differential_adjustment_tax2,
                         differential_adjustment_tax3, receiver_bonus,
                         created_on, transaction_amount, transaction_count,
                         differential_amount,receiver_validity,receiver_bonus_validity,differential_count,failure_count
                        )
                 VALUES (gv_userid, gd_transaction_date, gv_networkcodefor,
             lv_sendercategory,lv_receiverserviceclassid,
                         lv_servicetype,lv_subservice, 0,0, 0,0, 0,0, 0,0,0, 0,
                         gd_createdon, 0, 0,0,0,0,0,ln_c2s_failure_ct
                        );
         END IF;
         EXCEPTION
       
           
            WHEN OTHERS
            THEN
               RAISE NOTICE '%', 'OTHERS EXCEPTION in RET_REFILLS_FAILURE_DATA_PROC 1, User:' || gv_userid || SQLERRM;
               v_messageforlog := 'OTHERS EXCEPTION in RET_REFILLS_FAILURE_DATA_PROC 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION  using errcode = 'ERR05';
         END;                                --end of retailer insertion block

      END LOOP;
      raise notice 'RET_REFILLS_FAILURE_DATA_PROC SUCCESS';
	   v_messageforlog := 'RET_REFILLS_FAILURE_DATA_PROC SUCCESS';
	v_sqlerrmsgforlog := 'RET_REFILLS_FAILURE_DATA_PROC NO error ';  
      EXCEPTION
       when sqlstate 'ERR05' then
         RAISE NOTICE 'procexception in SP_UPDATE_DAILY_CHNL_TRANS_DET 3';
         RAISE EXCEPTION  using errcode = 'ERR01';
      
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION in RET_REFILLS_FAILURE_DATA_PROC 4:' || SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
   END; 
   $$;


ALTER FUNCTION pretupsdatabase.mis_ret_refills_failure_data_proc(p_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_ret_reverse_data_proc(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_ret_reverse_data_proc(p_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
   DECLARE
	gv_userid              TEMP_DAILY_CHNL_TRANS_MAIN.user_id%TYPE;
	gv_networkcode         TEMP_DAILY_CHNL_TRANS_MAIN.network_code%TYPE;
	gv_networkcodefor      TEMP_DAILY_CHNL_TRANS_MAIN.network_code_for%TYPE;
	gv_grphdomaincode      TEMP_DAILY_CHNL_TRANS_MAIN.grph_domain_code%TYPE;
	gv_productcode         TEMP_DAILY_CHNL_TRANS_MAIN.product_code%TYPE;
	gv_categorycode        TEMP_DAILY_CHNL_TRANS_MAIN.category_code%TYPE;
	gv_domaincode          TEMP_DAILY_CHNL_TRANS_MAIN.sender_domain_code%TYPE;
	gv_productmrp          TEMP_DAILY_CHNL_TRANS_MAIN.product_mrp%TYPE;
	user_rcd_count         numeric (5):= 0;
        gd_transaction_date    DATE;
        gd_createdon           DATE;
        
      /* Variables for refill amount values */
      lv_servicetype                DAILY_C2S_TRANS_DETAILS.SERVICE_TYPE%TYPE;
      lv_sub_service                DAILY_C2S_TRANS_DETAILS.SUB_SERVICE%TYPE;

      ln_c2s_rev_ct                   DAILY_C2S_TRANS_DETAILS.REVERSE_COUNT%TYPE= 0;
      ln_c2s_rev_amt                  DAILY_C2S_TRANS_DETAILS.REVERSE_AMOUNT%TYPE= 0;

      ln_c2s_sender_rev_amt           DAILY_C2S_TRANS_DETAILS.SENDER_REVERSE_AMOUNT%TYPE= 0;
      ln_c2s_rec_debit_amt             DAILY_C2S_TRANS_DETAILS.RECEIVER_DEBIT_AMOUNT%TYPE= 0;

      ln_c2s_rev_diff_amt           DAILY_C2S_TRANS_DETAILS.REVERSE_DIFF_AMOUNT%TYPE= 0;
      ln_c2s_rev_diff_count         DAILY_C2S_TRANS_DETAILS.REVERSE_DIFF_COUNT%TYPE= 0;

      lv_sendercategory             DAILY_C2S_TRANS_DETAILS.sender_category_code%TYPE =0;
      lv_receiverserviceclassid     DAILY_C2S_TRANS_DETAILS.receiver_service_class_id%TYPE =0;
      ln_c2s_sender_penalty         DAILY_C2S_TRANS_DETAILS.PENALTY%TYPE=0; 
      ln_c2s_sender_owner_penalty     DAILY_C2S_TRANS_DETAILS.OWNER_PENALTY%TYPE=0; 
      ln_c2s_sender_roam_amount     DAILY_C2S_TRANS_DETAILS.Roam_amount%TYPE=0; 
      /* Cursor Declaration */
        declare  refill_data CURSOR (p_date TIMESTAMP(0))
        FOR
        SELECT  Mast.transfer_date,Mast.sender_id, Mast.network_code,Mast.service_class_id,
                Mast.receiver_network_code, Mast.product_code,
                Mast.unit_value,Mast.grph_domain_code, Mast.category_code,
                Mast.domain_code, Mast.SERVICE_TYPE,Mast.sub_service,
                COUNT (Mast.transfer_id) c2s_count,
                SUM(Mast.transfer_value) c2s_amount,
                COALESCE(SUM(Mast.receiver_transfer_value),0) c2s_rectrans,
                COALESCE(SUM(Mast.sender_transfer_value),0) c2s_sender_value,
                COALESCE(SUM(adj.transfer_value),0) diff_amount,
                SUM(CASE  WHEN COALESCE(adj.transfer_value,0) <> 0 THEN 1 ELSE 0 END) diff_count,
                COALESCE(SUM(Mast.penalty),0) penalty,
                COALESCE(SUM(Mast.owner_penalty),0) owner_penalty
                FROM (SELECT
                        c2strans.transfer_date,c2strans.sender_id, c2strans.network_code,c2strans.service_class_id,
                        c2strans.receiver_network_code, p.product_code,
                        p.unit_value,ug.grph_domain_code, cat.category_code,
                        cat.domain_code, c2strans.SERVICE_TYPE,c2strans.sub_service,
                        c2strans.transfer_id,c2strans.transfer_value,c2strans.receiver_transfer_value,
                        c2strans.sender_transfer_value,c2strans.PENALTY,
                        c2strans.owner_penalty
                        FROM   C2S_TRANSFERS_MISTMP c2strans,PRODUCTS P, CATEGORIES cat,USER_GEOGRAPHIES ug
                        WHERE --(c2strans.transfer_date = p_date or c2strans.reconciliation_date = p_date) AND 
                           c2strans.transfer_status = '200'
                        AND   SERVICE_TYPE = 'REV'
                        AND   p.product_code=c2strans.product_code
                        AND   cat.category_code=c2strans.sender_category
                        AND   ug.user_id=c2strans.sender_id
                      ) Mast left join  ADJUSTMENTS_MISTMP adj on 
                ( Mast.transfer_id = adj.reference_id 
                AND    Mast.sender_id   = adj.user_id)
                GROUP BY Mast.transfer_date,Mast.sender_id, Mast.network_code, Mast.receiver_network_code,Mast.product_code,Mast.grph_domain_code,
                Mast.category_code,Mast.domain_code,Mast.SERVICE_TYPE,Mast.sub_service,Mast.service_class_id,Mast.unit_value;


   BEGIN
   RAISE notice 'inside  mis_ret_reverse_data_proc';
	 gd_createdon := current_timestamp;
      /* Iterate thru. the Refill cursor */
      FOR ret_data_cur IN refill_data (p_date)
      LOOP
         user_rcd_count := 0;                             --reinitialize to 0
         lv_sendercategory := ret_data_cur.category_code;
         lv_receiverserviceclassid := ret_data_cur.service_class_id;
         gv_userid := ret_data_cur.sender_id;
         gv_productcode := ret_data_cur.product_code;
         gv_networkcode := ret_data_cur.network_code;
         gv_networkcodefor := ret_data_cur.receiver_network_code;
         gv_grphdomaincode := ret_data_cur.grph_domain_code;
         gv_categorycode := ret_data_cur.category_code;
         gv_domaincode := ret_data_cur.domain_code;
         lv_servicetype := ret_data_cur.SERVICE_TYPE;
         lv_sub_service:= ret_data_cur.sub_service;
         --gd_transaction_date := p_date;
         gd_transaction_date := ret_data_cur.transfer_date;
         gv_productmrp := ret_data_cur.unit_value;
         ln_c2s_rev_ct := ret_data_cur.c2s_count;
         ln_c2s_rev_amt := ret_data_cur.c2s_amount;
         ln_c2s_sender_rev_amt := ret_data_cur.c2s_sender_value;
         ln_c2s_rec_debit_amt := ret_data_cur.c2s_rectrans;
         ln_c2s_rev_diff_amt := ret_data_cur.diff_amount;
         ln_c2s_rev_diff_count:= ret_data_cur.diff_count;
         ln_c2s_sender_penalty:=ret_data_cur.penalty;
         ln_c2s_sender_owner_penalty:=ret_data_cur.owner_penalty;
        ln_c2s_sender_roam_amount:=0;
        
         /* insert into DAILY_C2S_TRANS_DETAILS table */
        IF (gv_networkcode <> gv_networkcodefor)
            THEN
            ln_c2s_sender_roam_amount:=ln_c2s_rev_amt;
        END IF;

         BEGIN
            RAISE notice 'mis_ret_reverse_data_proc: select DAILY_C2S_TRANS_DETAILS';
              user_rcd_count=(SELECT 1 
               FROM DAILY_C2S_TRANS_DETAILS
               WHERE user_id = gv_userid
               AND receiver_network_code = gv_networkcodefor
               AND sender_category_code = lv_sendercategory
               AND receiver_service_class_id = lv_receiverserviceclassid
               AND SERVICE_TYPE = lv_servicetype
               AND sub_service = lv_sub_service
               AND trans_date = gd_transaction_date);
		raise notice 'user_rcd_count=%',user_rcd_count;
		if user_rcd_count is null then                        --when no row returned for the user
                
                 raise notice 'No Record found for User.';
                 user_rcd_count := 0;
                end if;
                EXCEPTION
               WHEN OTHERS
               THEN
                  RAISE NOTICE '%', 'Exception in mis_ret_reverse_data_proc 2, User:' || gv_userid;
                  v_messageforlog := 'Exception in mis_ret_reverse_data_proc 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE EXCEPTION  using errcode = 'ERR07';

         END;

         IF user_rcd_count = 0
            THEN
            RAISE notice 'mis_ret_reverse_data_proc: inserting into DAILY_C2S_TRANS_DETAILS';
                INSERT INTO DAILY_C2S_TRANS_DETAILS
                        (user_id, trans_date, receiver_network_code,
                         sender_category_code, receiver_service_class_id,
                         SERVICE_TYPE, sub_service,SENDER_REVERSE_AMOUNT ,
                        RECEIVER_DEBIT_AMOUNT ,REVERSE_AMOUNT ,REVERSE_COUNT,
                        REVERSE_DIFF_AMOUNT ,REVERSE_DIFF_COUNT,PENALTY,
                        OWNER_PENALTY,ROAM_AMOUNT
                        )
                 VALUES (gv_userid, gd_transaction_date, gv_networkcodefor,
                          lv_sendercategory,lv_receiverserviceclassid,
                         lv_servicetype, lv_sub_service,ln_c2s_sender_rev_amt,
                         ln_c2s_rec_debit_amt,ln_c2s_rev_amt,ln_c2s_rev_ct,
                         ln_c2s_rev_diff_amt,ln_c2s_rev_diff_count,ln_c2s_sender_penalty,
                        ln_c2s_sender_owner_penalty,ln_c2s_sender_roam_amount
                        );
         ELSE
		 RAISE notice 'mis_ret_reverse_data_proc: updating into DAILY_C2S_TRANS_DETAILS';
                UPDATE DAILY_C2S_TRANS_DETAILS
                SET SENDER_REVERSE_AMOUNT =  SENDER_REVERSE_AMOUNT + ln_c2s_sender_rev_amt,
                    RECEIVER_DEBIT_AMOUNT =  RECEIVER_DEBIT_AMOUNT + ln_c2s_rec_debit_amt,
                    REVERSE_AMOUNT        = REVERSE_AMOUNT + ln_c2s_rev_amt,
                    REVERSE_COUNT         = REVERSE_COUNT + ln_c2s_rev_ct,
                    REVERSE_DIFF_AMOUNT   = REVERSE_DIFF_AMOUNT + ln_c2s_rev_diff_amt,
                    REVERSE_DIFF_COUNT    = REVERSE_DIFF_COUNT + ln_c2s_rev_diff_count,
                    PENALTY = PENALTY+ln_c2s_sender_penalty,
                    OWNER_PENALTY = OWNER_PENALTY+ln_c2s_sender_owner_penalty,
                    roam_amount=roam_amount+ln_c2s_sender_roam_amount
                WHERE user_id = gv_userid
                AND receiver_network_code = gv_networkcodefor
                AND sender_category_code = lv_sendercategory
                AND receiver_service_class_id = lv_receiverserviceclassid
                AND SERVICE_TYPE = lv_servicetype
                AND sub_service = lv_sub_service
                AND trans_date = gd_transaction_date;
         END IF;

          /* insert into TEMP_DAILY_CHNL_TRANS_MAIN table */

         BEGIN
            BEGIN
                user_rcd_count=(SELECT 1
                FROM TEMP_DAILY_CHNL_TRANS_MAIN
                WHERE user_id = gv_userid
                AND network_code = gv_networkcode
                AND network_code_for = gv_networkcodefor
                AND product_code = gv_productcode
                AND grph_domain_code = gv_grphdomaincode
                AND trans_date = gd_transaction_date);
           
              raise notice 'user_rcd_count=%',user_rcd_count;
		if user_rcd_count is null then                       
                 raise notice 'No Record found for User.';                      --when no row returned for the user
                 user_rcd_count := 0;
                 end if;
                EXCEPTION
                WHEN OTHERS
                THEN
                  RAISE NOTICE '%', 'Exception in mis_ret_reverse_data_proc 2, User:' || gv_userid;
                  v_messageforlog := 'Exception in mis_ret_reverse_data_proc 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE EXCEPTION  using errcode = 'ERR07';
            END;

            IF user_rcd_count = 0 THEN
                INSERT INTO TEMP_DAILY_CHNL_TRANS_MAIN
                           (user_id, trans_date, product_code,
                            category_code, network_code,
                            network_code_for, sender_domain_code,
                            created_on, product_mrp,grph_domain_code,ROAM_C2S_TRANSFER_OUT_AMOUNT,
                            C2S_TRANSFER_IN_COUNT , C2S_TRANSFER_IN_AMOUNT,
                            REV_DIFFERENTIAL
                           )
                    VALUES (gv_userid, gd_transaction_date, gv_productcode,
                            gv_categorycode, gv_networkcode,
                            gv_networkcodefor, gv_domaincode,
                            gd_createdon, gv_productmrp,gv_grphdomaincode, (0-ln_c2s_sender_roam_amount),
                            ln_c2s_rev_ct, ln_c2s_rev_amt,ln_c2s_rev_diff_amt
                           );
            ELSE
                UPDATE TEMP_DAILY_CHNL_TRANS_MAIN
                  SET C2S_TRANSFER_IN_COUNT = C2S_TRANSFER_IN_COUNT + ln_c2s_rev_ct,
                      C2S_TRANSFER_IN_AMOUNT = C2S_TRANSFER_IN_AMOUNT + ln_c2s_rev_amt,
                      REV_DIFFERENTIAL = REV_DIFFERENTIAL + ln_c2s_rev_diff_amt,
                      ROAM_C2S_TRANSFER_OUT_AMOUNT = ROAM_C2S_TRANSFER_OUT_AMOUNT - ln_c2s_sender_roam_amount
                  WHERE user_id = gv_userid
                  AND trans_date = gd_transaction_date
                  AND product_code = gv_productcode
                  AND network_code = gv_networkcode
                  AND network_code_for = gv_networkcodefor;
            END IF;

            EXCEPTION
            when sqlstate 'ERR07' then
               raise notice 'sqlexception SQL/OTHERS EXCEPTION in mis_ret_reverse_data_proc 3, User:%,error:%',gv_userid,SQLERRM;
               v_messageforlog := 'sqlexception Exception in mis_ret_reverse_data_proc 3, User:' || gv_userid || ' date=' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION  using errcode = 'ERR05';
            
            WHEN OTHERS
            THEN
               RAISE NOTICE '%',   'EXCEPTION in mis_ret_reverse_data_proc 3, User:' || gv_userid  || SQLERRM;
               v_messageforlog :='Exception in mis_ret_reverse_data_proc 3, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION  using errcode = 'ERR05';
         END;                             --end of distributor insertion block
      END LOOP;
   EXCEPTION
      when sqlstate 'ERR05' then
         raise notice 'procexception in mis_ret_reverse_data_proc 4:%',SQLERRM;
        RAISE EXCEPTION  using errcode = 'ERR01';
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'EXCEPTION in mis_ret_reverse_data_proc 4:' || SQLERRM;
          RAISE EXCEPTION  using errcode = 'ERR01';
   END;
   $$;


ALTER FUNCTION pretupsdatabase.mis_ret_reverse_data_proc(p_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_sp_chnl_transfer_in_data_proc(timestamp without time zone, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_chnl_transfer_in_data_proc(OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying, p_date timestamp without time zone, p_transfersubtype character varying) RETURNS record
    LANGUAGE plpgsql
    AS $_$
   DECLARE 
	n_date_for_mis         PROCESS_STATUS.executed_upto%TYPE;
	gv_userid              TEMP_DAILY_CHNL_TRANS_MAIN.user_id%TYPE;
	gv_networkcode         TEMP_DAILY_CHNL_TRANS_MAIN.network_code%TYPE;
	gv_networkcodefor      TEMP_DAILY_CHNL_TRANS_MAIN.network_code_for%TYPE;
	gv_productcode         TEMP_DAILY_CHNL_TRANS_MAIN.product_code%TYPE;
	gv_categorycode        TEMP_DAILY_CHNL_TRANS_MAIN.category_code%TYPE;
	gv_domaincode          TEMP_DAILY_CHNL_TRANS_MAIN.sender_domain_code%TYPE;
	gv_productmrp          TEMP_DAILY_CHNL_TRANS_MAIN.product_mrp%TYPE;
	gv_grphdomaincode      TEMP_DAILY_CHNL_TRANS_MAIN.grph_domain_code%TYPE;
	gd_transaction_date    DATE;
	user_rcd_count         numeric (5):= 0;

        gd_createdon           DATE;
	
      /* variables to store order related amounts */
      ln_o2c_trans          TEMP_DAILY_CHNL_TRANS_MAIN.o2c_transfer_in_count%TYPE;
      ln_o2c_trans_amt      TEMP_DAILY_CHNL_TRANS_MAIN.o2c_transfer_in_amount%TYPE;
      ln_c2c_trans          TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_in_count%TYPE;
      ln_c2c_trans_amt      TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_in_amount%TYPE;
      ln_c2creturns         TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_in_count%TYPE;
      ln_c2creturn_amt      TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_in_amount%TYPE;
      ln_c2cwithdraws       TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_in_count%TYPE;
      ln_c2cwithdraws_amt   TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_in_amount%TYPE;
      ln_adjustment_amt     TEMP_DAILY_CHNL_TRANS_MAIN.adjustment_in%TYPE;
      sql_stmt              VARCHAR (2000);
	


      /* Cursor Declaration */
      declare  chnl_data CURSOR (p_date TIMESTAMP(0), p_transfersubtype VARCHAR)
      for
         SELECT   ch.to_user_id, ch.network_code, ch.network_code_for,
                  chi.product_code, ug.grph_domain_code grph_domain_code,
                  ch.receiver_category_code category_code, ch.to_domain_code domain_code, chi.user_unit_price unit_value,
                  SUM (CASE WHEN (ch.TYPE='O2C') THEN 1 ELSE 0 END) o2c_num_trans,
                  SUM (CASE WHEN (ch.TYPE='O2C') THEN chi.approved_quantity ELSE 0 END) o2c_amount,
                  SUM (CASE WHEN (ch.TYPE='C2C') THEN 1 ELSE 0 END) c2c_num_trans,
                  SUM (CASE WHEN (ch.TYPE='C2C') THEN chi.approved_quantity ELSE 0 END) c2c_amount
             FROM CHANNEL_TRANSFERS ch,
                  CHANNEL_TRANSFERS_ITEMS chi,
          USER_GEOGRAPHIES ug
              WHERE ch.transfer_date<=p_date+interval '1 day'
              AND DATE_TRUNC('day',ch.close_date::TIMESTAMP) = p_date
              AND ch.transfer_sub_type = p_transfersubtype
              AND ch.transfer_id = chi.transfer_id
          AND ch.to_user_id=ug.user_id
              AND ch.status = 'CLOSE'
              AND ch.to_user_id <> 'OPT'
         GROUP BY ch.to_user_id,
                  ch.network_code,
                  ch.network_code_for,
                  chi.product_code,
                  ch.receiver_category_code,
                  ug.grph_domain_code,
                  ch.to_domain_code,
                  chi.user_unit_price;
   BEGIN
	n_date_for_mis := p_date;
	 gd_createdon := current_timestamp;
	 RAISE notice 'inside  mis_sp_chnl_transfer_in_data_proc';
      /* Iterate CHNL_DATA cursor */
      FOR chnl_data_cur IN chnl_data (n_date_for_mis, p_transfersubtype)
      LOOP
         gv_userid := chnl_data_cur.to_user_id;
         gv_networkcode := chnl_data_cur.network_code;
         gv_networkcodefor := chnl_data_cur.network_code_for;
         gv_grphdomaincode := chnl_data_cur.grph_domain_code;
         gv_productcode := chnl_data_cur.product_code;
         gv_categorycode := chnl_data_cur.category_code;
         gv_domaincode := chnl_data_cur.domain_code;
         gd_transaction_date := p_date;
         gv_productmrp := chnl_data_cur.unit_value;
         user_rcd_count := 0;
         ln_o2c_trans := chnl_data_cur.o2c_num_trans;
         ln_o2c_trans_amt := chnl_data_cur.o2c_amount;
         ln_c2c_trans := chnl_data_cur.c2c_num_trans;
         ln_c2c_trans_amt := chnl_data_cur.c2c_amount;
         sql_stmt := '';

         BEGIN
            BEGIN
            RAISE notice 'inside  mis_sp_chnl_transfer_in_data_proc getting user count';
               user_rcd_count=(SELECT 1 
               FROM TEMP_DAILY_CHNL_TRANS_MAIN
               WHERE user_id = gv_userid
               AND network_code = gv_networkcode
               AND network_code_for = gv_networkcodefor
               AND product_code = gv_productcode
               AND grph_domain_code = gv_grphdomaincode
               AND trans_date = gd_transaction_date);
               raise notice 'user_rcd_count=%',user_rcd_count;
               if user_rcd_count is null then 
                                   --when no row returned for the user
                  raise notice 'No Record found for User. in Orders';
                  user_rcd_count := 0;
                 end if;
            EXCEPTION
               
               WHEN OTHERS
               THEN
                  v_messageforlog := 'OTHERS SQL Exception in SP_CHNL_TRANSFER_IN_DATA_PROC 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE NOTICE 'SQL Exception in SP_CHNL_TRANSFER_IN_DATA_PROC 1';
                  RAISE EXCEPTION  using errcode = 'ERR07';
            END;

            IF user_rcd_count = 0
            THEN
               sql_stmt :=
                  'INSERT INTO temp_daily_chnl_trans_main (user_id, trans_date, product_code, category_code, network_code, network_code_for, sender_domain_code, ';
               sql_stmt :=
                   sql_stmt || ' created_on, product_mrp,grph_domain_code, ';

               IF (p_transfersubtype = 'R')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' c2c_return_in_count, c2c_return_in_amount ';
               ELSIF (p_transfersubtype = 'W')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' c2c_withdraw_in_count, c2c_withdraw_in_amount ';
               ELSIF (p_transfersubtype = 'T')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_transfer_in_count, o2c_transfer_in_amount,c2c_transfer_in_count, c2c_transfer_in_amount ';
           ELSIF (p_transfersubtype = 'X')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_reverse_in_count, o2c_reverse_in_amount, c2c_reverse_in_count, c2c_reverse_in_amount ';
               END IF;

               sql_stmt := sql_stmt || ')';

               IF (p_transfersubtype = 'T' OR p_transfersubtype = 'X')
               THEN
                  sql_stmt := sql_stmt || ' VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13,$14)';
               ELSE
                  sql_stmt := sql_stmt || ' VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12)';
               END IF;

             IF (p_transfersubtype = 'T' OR p_transfersubtype = 'X')
             THEN
             raise notice ' sql_stmt %',sql_stmt;
               EXECUTE  sql_stmt USING gv_userid,gd_transaction_date,gv_productcode,gv_categorycode,
                          gv_networkcode,gv_networkcodefor,gv_domaincode,gd_createdon,gv_productmrp,gv_grphdomaincode,
                       ln_o2c_trans,ln_o2c_trans_amt,ln_c2c_trans,ln_c2c_trans_amt;
             ELSE
                EXECUTE  sql_stmt USING gv_userid,gd_transaction_date,gv_productcode,gv_categorycode,
                          gv_networkcode,gv_networkcodefor,gv_domaincode,gd_createdon,gv_productmrp,gv_grphdomaincode,
                       ln_c2c_trans,ln_c2c_trans_amt;

             END IF;

            ELSE
               sql_stmt := 'UPDATE temp_daily_chnl_trans_main  SET  ';

               IF (p_transfersubtype = 'R')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' c2c_return_in_count='
                     || ln_c2c_trans
                     || ', c2c_return_in_amount='
                     || ln_c2c_trans_amt;
               ELSIF (p_transfersubtype = 'W')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' c2c_withdraw_in_count='
                     || ln_c2c_trans
                     || ',c2c_withdraw_in_amount='
                     || ln_c2c_trans_amt;
               ELSIF (p_transfersubtype = 'T')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_transfer_in_count='
                     || ln_o2c_trans
                     || ', o2c_transfer_in_amount='
                     || ln_o2c_trans_amt
                     || ', c2c_transfer_in_count='
                     || ln_c2c_trans
                     || ', c2c_transfer_in_amount ='
                     || ln_c2c_trans_amt;
           ELSIF (p_transfersubtype = 'X')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || 'c2c_reverse_in_count='
                     || ln_c2c_trans
                     || ', c2c_reverse_in_amount ='
                     || ln_c2c_trans_amt
             || ', o2c_reverse_in_count='
                     || ln_o2c_trans
                     || ', o2c_reverse_in_amount ='
                     || ln_o2c_trans_amt;
               END IF;

               sql_stmt := sql_stmt|| 'WHERE user_id=$1 AND trans_date=$2 AND product_code=$3 AND network_code=$4 AND network_code_for=$5';

              EXECUTE  sql_stmt USING gv_userid,gd_transaction_date,gv_productcode,gv_networkcode,gv_networkcodefor;

            END IF;
         EXCEPTION
         when sqlstate 'ERR07' then
           
                RAISE NOTICE'sqlexception in SP_CHNL_TRANSFER_IN_DATA_PROC 2:%,error:%', gv_userid,SQLERRM;
               v_messageforlog := 'sqlexception in SP_CHNL_TRANSFER_IN_DATA_PROC 2:' || gv_userid || ' date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
              RAISE EXCEPTION  using errcode = 'ERR05';
            WHEN OTHERS
            THEN
               RAISE NOTICE 'EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC 2, User:%, Error:%',gv_userid,SQLERRM;
               v_messageforlog := 'Exception in SP_CHNL_TRANSFER_IN_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               
                RAISE EXCEPTION  using errcode = 'ERR05';
         END;                             --end of distributor insertion block
      END LOOP;                                    --end of ORDER_CURSOR Loop
      raise notice 'mis_sp_chnl_transfer_in_data_proc SUCCESS';
 v_messageforlog := 'mis_sp_chnl_transfer_in_data_proc SUCCESS';
v_sqlerrmsgforlog := 'mis_sp_chnl_transfer_in_data_proc NO error';  
   EXCEPTION
   when sqlstate 'ERR05' then
         RAISE NOTICE 'procexception in SP_CHNL_TRANSFER_IN_DATA_PROC 3:%', SQLERRM;
          RAISE EXCEPTION  using errcode = 'ERR01';
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC 3'|| SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
   END;   
     $_$;


ALTER FUNCTION pretupsdatabase.mis_sp_chnl_transfer_in_data_proc(OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying, p_date timestamp without time zone, p_transfersubtype character varying) OWNER TO pgdb;

--
-- Name: mis_sp_chnl_transfer_out_data_proc(timestamp without time zone, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_chnl_transfer_out_data_proc(OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying, p_date timestamp without time zone, p_transfersubtype character varying) RETURNS record
    LANGUAGE plpgsql
    AS $_$
   DECLARE

	n_date_for_mis         PROCESS_STATUS.executed_upto%TYPE; 
	gv_userid              TEMP_DAILY_CHNL_TRANS_MAIN.user_id%TYPE;
	gv_networkcode         TEMP_DAILY_CHNL_TRANS_MAIN.network_code%TYPE;
	gv_networkcodefor      TEMP_DAILY_CHNL_TRANS_MAIN.network_code_for%TYPE;
	gv_productcode         TEMP_DAILY_CHNL_TRANS_MAIN.product_code%TYPE;
	gv_categorycode        TEMP_DAILY_CHNL_TRANS_MAIN.category_code%TYPE;
	gv_domaincode          TEMP_DAILY_CHNL_TRANS_MAIN.sender_domain_code%TYPE;
	gv_productmrp          TEMP_DAILY_CHNL_TRANS_MAIN.product_mrp%TYPE;
	gv_grphdomaincode      TEMP_DAILY_CHNL_TRANS_MAIN.grph_domain_code%TYPE;
	gd_transaction_date    DATE;
	user_rcd_count         numeric (5):= 0;
	      
        gd_createdon           DATE;
        
   
      ln_o2c_trans          TEMP_DAILY_CHNL_TRANS_MAIN.o2c_transfer_in_count%TYPE;
      ln_o2c_trans_amt      TEMP_DAILY_CHNL_TRANS_MAIN.o2c_transfer_in_amount%TYPE;
      ln_c2c_trans          TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_in_count%TYPE;
      ln_c2c_trans_amt      TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_in_amount%TYPE;
      ln_o2creturns         TEMP_DAILY_CHNL_TRANS_MAIN.o2c_return_out_count%TYPE;
      ln_o2creturn_amt      TEMP_DAILY_CHNL_TRANS_MAIN.o2c_return_out_amount%TYPE;
      ln_o2cwithdraws       TEMP_DAILY_CHNL_TRANS_MAIN.o2c_withdraw_out_count%TYPE;
      ln_o2cwithdraw_amt    TEMP_DAILY_CHNL_TRANS_MAIN.o2c_withdraw_out_amount%TYPE;
      ln_c2ctransfers       TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_out_count%TYPE;
      ln_c2ctransfers_amt   TEMP_DAILY_CHNL_TRANS_MAIN.c2c_transfer_out_amount%TYPE;
      ln_c2creturns         TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_out_count%TYPE;
      ln_c2creturn_amt      TEMP_DAILY_CHNL_TRANS_MAIN.c2c_return_out_amount%TYPE;
      ln_c2cwithdraws       TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_out_count%TYPE;
      ln_c2cwithdraws_amt   TEMP_DAILY_CHNL_TRANS_MAIN.c2c_withdraw_out_amount%TYPE;
      ln_adjustment_amt     TEMP_DAILY_CHNL_TRANS_MAIN.adjustment_out%TYPE;
      sql_stmt              VARCHAR (2000);

      /* Cursor Declaration */
      declare  chnl_data CURSOR (p_date TIMESTAMP(0), p_transfersubtype VARCHAR)
     for

         SELECT   ch.from_user_id, ch.network_code, ch.network_code_for,
                  chi.product_code, ug.grph_domain_code,chi.user_unit_price unit_value,
                  ch.sender_category_code category_code , ch.domain_code,
                  SUM (CASE WHEN (ch.TYPE='O2C') THEN 1 ELSE 0 END) o2c_num_trans,
                  SUM (CASE WHEN (ch.TYPE='O2C') THEN chi.approved_quantity ELSE 0 END) o2c_amount,
                  SUM (CASE WHEN (ch.TYPE='C2C') THEN 1 ELSE 0 END) c2c_num_trans,
                  SUM (CASE WHEN (ch.TYPE='C2C') THEN chi.approved_quantity ELSE 0 END) c2c_amount
             FROM CHANNEL_TRANSFERS ch,
                  CHANNEL_TRANSFERS_ITEMS chi,
          USER_GEOGRAPHIES ug
            WHERE ch.transfer_date<=p_date+interval '1 day'
              AND date_trunc('day',ch.close_date::TIMESTAMP) = p_date
              AND ch.transfer_sub_type = p_transfersubtype
              AND ch.transfer_id = chi.transfer_id
          AND ch.from_user_id=ug.user_id
              AND ch.status = 'CLOSE'
              AND ch.from_user_id <> 'OPT'
         GROUP BY ch.from_user_id,
                  ch.network_code,
                  ch.network_code_for,
                  chi.product_code,
                  ch.sender_category_code,
                  ug.grph_domain_code,
                  ch.domain_code,
                  chi.user_unit_price;
   BEGIN
      n_date_for_mis:=p_date;
      gd_createdon := current_timestamp;
	 RAISE notice 'inside  mis_sp_chnl_transfer_out_data_proc 1';
      /* Iterate CHNL_DATA cursor */
      FOR chnl_data_cur IN chnl_data (n_date_for_mis, p_transfersubtype)
      LOOP
         gv_userid := chnl_data_cur.from_user_id;
         gv_networkcode := chnl_data_cur.network_code;
         gv_networkcodefor := chnl_data_cur.network_code_for;
         gv_grphdomaincode := chnl_data_cur.grph_domain_code;
         gv_productcode := chnl_data_cur.product_code;
         gv_categorycode := chnl_data_cur.category_code;
         gv_domaincode := chnl_data_cur.domain_code;
         gd_transaction_date := p_date;
         user_rcd_count := 0;
         ln_o2c_trans := chnl_data_cur.o2c_num_trans;
         ln_o2c_trans_amt := chnl_data_cur.o2c_amount;
         ln_c2c_trans := chnl_data_cur.c2c_num_trans;
         ln_c2c_trans_amt := chnl_data_cur.c2c_amount;
         gv_productmrp := chnl_data_cur.unit_value;
         sql_stmt := '';

         BEGIN
            BEGIN
            RAISE notice 'inside  mis_sp_chnl_transfer_out_data_proc 2';
                user_rcd_count=(SELECT 1 
                FROM TEMP_DAILY_CHNL_TRANS_MAIN
                WHERE user_id = gv_userid
                AND network_code = gv_networkcode
                AND network_code_for = gv_networkcodefor
                AND product_code = gv_productcode
                AND grph_domain_code = gv_grphdomaincode
                AND trans_date = gd_transaction_date);
               raise notice 'user_rcd_count=%',user_rcd_count;
               if user_rcd_count is null then 
                                   --when no row returned for the user
                  raise notice 'No Record found for Dist. in Orders';
                  user_rcd_count := 0;
                 end if;
                  EXCEPTION
               WHEN OTHERS
               THEN
                  v_messageforlog := 'OTHERS Exception in SP_CHNL_TRANSFER_OUT_DATA_PROC 1, User ' || gv_userid || ' Date:' || gd_transaction_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE NOTICE 'Exception in SP_CHNL_TRANSFER_OUT_DATA_PROC';
                  RAISE EXCEPTION  using errcode = 'ERR07';
            END;
	
            IF user_rcd_count = 0
            THEN
               sql_stmt :=
                  'INSERT INTO temp_daily_chnl_trans_main (user_id, trans_date, product_code, category_code, network_code, network_code_for, sender_domain_code,created_on, product_mrp,grph_domain_code,';

               IF (p_transfersubtype = 'R')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_return_out_count, o2c_return_out_amount, c2c_return_out_count, c2c_return_out_amount ';
               ELSIF (p_transfersubtype = 'W')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_withdraw_out_count, o2c_withdraw_out_amount, c2c_withdraw_out_count, c2c_withdraw_out_amount ';
               ELSIF (p_transfersubtype = 'T')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' c2c_transfer_out_count, c2c_transfer_out_amount ';

           ELSIF (p_transfersubtype = 'X')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_reverse_out_count, o2c_reverse_out_amount, c2c_reverse_out_count, c2c_reverse_out_amount ';


               END IF;

               sql_stmt := sql_stmt || ')';

               IF (p_transfersubtype = 'T')
               THEN
                  sql_stmt := sql_stmt || ' VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12)';

               ELSE
                  sql_stmt := sql_stmt || ' VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13,$14)';
               END IF;

             IF (p_transfersubtype = 'T')
             THEN
             raise notice 'i am here 3';
             raise notice ' sql_stmt %',sql_stmt;
               EXECUTE  sql_stmt USING gv_userid,gd_transaction_date,gv_productcode,gv_categorycode,
                          gv_networkcode,gv_networkcodefor,gv_domaincode,gd_createdon,gv_productmrp,gv_grphdomaincode,
                       ln_c2c_trans,ln_c2c_trans_amt;
                   raise notice ' sql_stmt %',sql_stmt;
             ELSE
             raise notice 'i am here 1';
                EXECUTE  sql_stmt USING gv_userid,gd_transaction_date,gv_productcode,gv_categorycode,
                          gv_networkcode,gv_networkcodefor,gv_domaincode,gd_createdon,gv_productmrp,gv_grphdomaincode,
                       ln_o2c_trans,ln_o2c_trans_amt,ln_c2c_trans,ln_c2c_trans_amt;

             END IF;

            ELSE
            
               sql_stmt := 'UPDATE temp_daily_chnl_trans_main SET  ';

               IF (p_transfersubtype = 'R')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_return_out_count='
                     || ln_o2c_trans
                     || ', o2c_return_out_amount='
                     || ln_o2c_trans_amt
                     || ', c2c_return_out_count='
                     || ln_c2c_trans
                     || ', c2c_return_out_amount='
                     || ln_c2c_trans_amt;
               ELSIF (p_transfersubtype = 'W')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' o2c_withdraw_out_count='
                     || ln_o2c_trans
                     || ',o2c_withdraw_out_amount='
                     || ln_o2c_trans_amt
                     || ',c2c_withdraw_out_count='
                     || ln_c2c_trans
                     || ',c2c_withdraw_out_amount='
                     || ln_c2c_trans_amt;
               ELSIF (p_transfersubtype = 'T')
               THEN
               
                  sql_stmt :=
                        sql_stmt
                     || ' c2c_transfer_out_count='
                     || ln_c2c_trans
                     || ', c2c_transfer_out_amount ='
                     || ln_c2c_trans_amt;
           ELSIF (p_transfersubtype = 'X')
               THEN
                  sql_stmt :=
                        sql_stmt
                     || ' c2c_reverse_out_count='
                     || ln_c2c_trans
                     || ', c2c_reverse_out_amount ='
                     || ln_c2c_trans_amt
             || ', o2c_reverse_out_count='
                     || ln_o2c_trans
                     || ', o2c_reverse_out_amount ='
                     || ln_o2c_trans_amt;
               END IF;
               sql_stmt := sql_stmt|| ' WHERE user_id=$1 AND trans_date=$2 AND product_code=$3 AND network_code=$4 AND network_code_for=$5';

		EXECUTE  sql_stmt USING gv_userid,gd_transaction_date,gv_productcode,gv_networkcode,gv_networkcodefor;
            END IF;
         EXCEPTION
         when sqlstate 'ERR07' then
	
               RAISE NOTICE  'sqlexception in SP_CHNL_TRANSFER_OUT_DATA_PROC 2, User:% and error:%', gv_userid ,SQLERRM ;
               v_messageforlog := 'sqlexception in SP_CHNL_TRANSFER_OUT_DATA_PROC 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION  using errcode = 'ERR05';
            WHEN OTHERS
            THEN
               RAISE NOTICE 'EXCEPTION CAUGHT while Inserting/Updating record, User:% and error:%', gv_userid,SQLERRM;
               v_messageforlog := 'Exception in SP_CHNL_TRANSFER_OUT_DATA_PROC, User:' || gv_userid || ' Date:' || gd_transaction_date || sql_stmt;
               v_sqlerrmsgforlog := SQLERRM;
               
               RAISE EXCEPTION  using errcode = 'ERR05';
         END; 
                                 --end of distributor insertion block
      END LOOP;--end of ORDER_CURSOR Loop
	raise notice 'mis_sp_chnl_transfer_out_data_proc SUCCESS';
	   v_messageforlog := 'mis_sp_chnl_transfer_out_data_proc SUCCESS';
	v_sqlerrmsgforlog := 'mis_sp_chnl_transfer_out_data_proc NO error ';  
	
		
   --CLOSE ORDER_DATE; --closing the cursor
   EXCEPTION 
	when sqlstate 'ERR05' then
	RAISE NOTICE 'procexception CAUGHT in SP_CHNL_TRANSFER_OUT_DATA_PROC 3';
         RAISE EXCEPTION  using errcode = 'ERR01';
      
    WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'Exception CAUGHT in SP_CHNL_TRANSFER_OUT_DATA_PROC 3'||SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
   END;   
   --end of orders values
   $_$;


ALTER FUNCTION pretupsdatabase.mis_sp_chnl_transfer_out_data_proc(OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying, p_date timestamp without time zone, p_transfersubtype character varying) OWNER TO pgdb;

--
-- Name: mis_sp_get_mis_data_dtrange(character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_get_mis_data_dtrange(aiv_fromdate character varying, aiv_todate character varying, OUT aov_message character varying, OUT aov_messageforlog character varying, OUT aov_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE 
	n_date_for_mis         PROCESS_STATUS.executed_upto%TYPE;
	gd_createdon           DATE;
  ld_from_date TIMESTAMP(0);
  ld_to_date TIMESTAMP(0);
  ld_created_on TIMESTAMP(0);
  flag SMALLINT;
  mis_sno SMALLINT;
  status SMALLINT;
  mis_already_executed INT;
  msisdn_usage_summ_flag SMALLINT;
  sql_stmt1        VARCHAR (2000);
  DIFFERENTIAL_COUNT INT;
  LN_C2S_DIFF_COUNT INT;
  v_message              VARCHAR(500);
   v_messageforlog        VARCHAR(500);
   v_sqlerrmsgforlog      VARCHAR(500);

BEGIN

  ld_from_date   :=TO_DATE(aiv_fromDate,'dd/mm/yy');
  ld_to_date     :=TO_DATE(aiv_toDate,'dd/mm/yy');
  n_date_for_mis :=ld_from_date;
  flag           :=0;
  ld_created_on  :=CURRENT_TIMESTAMP;  -- Initailaize Created On date
  gd_createdon := ld_created_on;
  mis_already_executed :=0;
  msisdn_usage_summ_flag :=0;
  sql_stmt1 := '';
  

WHILE n_date_for_mis <= ld_to_date ---run the MIS process for each date less than the To Date
   LOOP
	RAISE notice 'fromdate:%  todate:%',n_date_for_mis,ld_to_date;
    RAISE NOTICE '%','EXCEUTING FOR ::::::::'||n_date_for_mis;
    BEGIN
    
    --SAVEPOINT s;
   -- RAISE notice 'I AM HERE102';
         ---Check if MIS process has already run for the date
         mis_sno := 0;
         mis_already_executed=(SELECT 1 
         FROM PROCESS_STATUS
         WHERE PROCESS_ID='C2SMIS' AND EXECUTED_UPTO>=n_date_for_mis);
         if mis_already_executed is not null then
        -- RAISE notice 'I am here:MIS executed %',mis_already_executed;
         RAISE NOTICE '%','PreTUPS C2S MIS already Executed, Date:' || n_date_for_mis;
         aov_message :='FAILED';
         aov_messageForLog:='PreTUPS C2S MIS already Executed, Date:' || n_date_for_mis;
         aov_sqlerrMsgForLog:=' ';
         --RAISE alreadyDoneException;
         RAISE EXCEPTION  using errcode ='ERR02';
        else
      --   EXCEPTION
        --    WHEN NO_DATA_FOUND THEN
           -- RAISE notice 'I AM HERE 11';

            BEGIN
			--	RAISE notice 'I AM HERE 12';
				RAISE NOTICE '%','before truncation : '||TO_CHAR(CURRENT_TIMESTAMP, 'DD-MON-YY HH24:MI:SS');
				mis_sno := mis_sno+1;
				RAISE notice ' mis_no=%',mis_sno;
				INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before table truncate',n_date_for_mis,CURRENT_TIMESTAMP);
				sql_stmt1 := 'TRUNCATE TABLE adjustments_MISTMP';
				EXECUTE  sql_stmt1;
			--	RAISE notice 'I AM HERE 13';
				sql_stmt1 := 'TRUNCATE TABLE C2S_TRANSFERS_MISTMP';
				EXECUTE  sql_stmt1;
				RAISE NOTICE '%','middle truncation : '||TO_CHAR(CURRENT_TIMESTAMP, 'DD-MON-YY HH24:MI:SS');
				mis_sno := mis_sno+1;
			--	RAISE notice 'I AM HERE 14';
				INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'after table truncate and before tmp tbale populate',n_date_for_mis,CURRENT_TIMESTAMP);
				INSERT INTO C2S_TRANSFERS_MISTMP
				    SELECT TRANSFER_ID, TRANSFER_DATE, NETWORK_CODE, SENDER_ID, SENDER_CATEGORY, PRODUCT_CODE, RECEIVER_NETWORK_CODE,
				    TRANSFER_VALUE, ERROR_CODE ,SERVICE_TYPE, TRANSFER_STATUS, SENDER_TRANSFER_VALUE, RECEIVER_ACCESS_FEE,
				    RECEIVER_TAX1_VALUE, RECEIVER_TAX2_VALUE, RECEIVER_VALIDITY, RECEIVER_TRANSFER_VALUE, RECEIVER_BONUS_VALUE,
				    RECEIVER_BONUS_VALIDITY, GRPH_DOMAIN_CODE, SUB_SERVICE, RECEIVER_MSISDN,
				    INTERFACE_ID,PREFIX_ID,SERVICE_CLASS_ID, SERVICE_CLASS_CODE,BONUS_DETAILS,TRANSFER_TYPE,TXN_TYPE,REVERSAL_ID,PENALTY,OWNER_PENALTY,RECONCILIATION_DATE,LMS_PROFILE
					FROM C2S_TRANSFERS WHERE transfer_date = n_date_for_mis AND TRANSFER_STATUS in ('200','206') and RECONCILIATION_DATE is NULL;
				
				INSERT INTO C2S_TRANSFERS_MISTMP
				    SELECT TRANSFER_ID, TRANSFER_DATE, NETWORK_CODE, SENDER_ID, SENDER_CATEGORY, PRODUCT_CODE, RECEIVER_NETWORK_CODE,
				    TRANSFER_VALUE, ERROR_CODE ,SERVICE_TYPE, TRANSFER_STATUS, SENDER_TRANSFER_VALUE, RECEIVER_ACCESS_FEE,
				    RECEIVER_TAX1_VALUE, RECEIVER_TAX2_VALUE, RECEIVER_VALIDITY, RECEIVER_TRANSFER_VALUE, RECEIVER_BONUS_VALUE,
				    RECEIVER_BONUS_VALIDITY, GRPH_DOMAIN_CODE, SUB_SERVICE, RECEIVER_MSISDN,
				    INTERFACE_ID,PREFIX_ID,SERVICE_CLASS_ID, SERVICE_CLASS_CODE,BONUS_DETAILS,TRANSFER_TYPE,TXN_TYPE,REVERSAL_ID,PENALTY,OWNER_PENALTY,RECONCILIATION_DATE,LMS_PROFILE 
					FROM C2S_TRANSFERS WHERE RECONCILIATION_DATE = n_date_for_mis AND TRANSFER_STATUS in ('200','206');
				
				--INSERT INTO C2S_TRANSFER_ITEMS_MISTMP
				    --SELECT transfer_id, msisdn, transfer_value, interface_id, sno , prefix_id, service_class_id, service_class_code
					--FROM C2S_TRANSFER_ITEMS WHERE transfer_date = n_date_for_mis;
				INSERT INTO ADJUSTMENTS_MISTMP
				    SELECT ADJUSTMENT_ID, ADJUSTMENT_DATE, USER_ID, TRANSFER_VALUE, TAX1_VALUE, TAX2_VALUE, TAX3_VALUE, REFERENCE_ID
					FROM ADJUSTMENTS WHERE adjustment_date = n_date_for_mis;
					
				INSERT INTO ADJUSTMENTS_MISTMP
				    SELECT ADJUSTMENT_ID, ADJUSTMENT_DATE, USER_ID, TRANSFER_VALUE, TAX1_VALUE, TAX2_VALUE, TAX3_VALUE, REFERENCE_ID
					FROM ADJUSTMENTS WHERE reference_id in (select transfer_id from C2S_TRANSFERS_MISTMP 
					where RECONCILIATION_DATE = n_date_for_mis and transfer_date != n_date_for_mis);
				-- specify the column which is required in mis.
				RAISE NOTICE '%','after truncation : '||TO_CHAR(CURRENT_TIMESTAMP, 'DD-MON-YY HH24:MI:SS');
				mis_sno := mis_sno+1;
				INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'after tmp table populate',n_date_for_mis,CURRENT_TIMESTAMP);
			--	RAISE notice 'I AM HERE 14-1';
				--COMMIT;
				
            EXCEPTION
               
			       WHEN OTHERS THEN
				  RAISE NOTICE '%','OTHERS in SP_GET_MIS_DATA_DTRANGE :'|| SQLERRM;
				   aov_messageForLog:='OTHERS in SP_GET_MIS_DATA_DTRANGE';
				   aov_sqlerrMsgForLog:=SQLERRM;
				   RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
			--    RAISE notice 'I AM HERE 15';
			       mis_sno := mis_sno+1;
			       RAISE notice 'mis_sp_chnl_transfer_out_data_proc';
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_out_data_proc T',n_date_for_mis,CURRENT_TIMESTAMP);
			       RAISE notice 'mis_sp_chnl_transfer_out_data_proc after inserting in c2smis_logs';
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_sp_chnl_transfer_out_data_proc (n_date_for_mis, 'T');
			    EXCEPTION
               
               WHEN OTHERS THEN
			  RAISE NOTICE '%','OTHERS Exception in SP_CHNL_TRANSFER_OUT_DATA_PROC for transfer :'|| SQLERRM;
			   aov_messageForLog:='OTHERS Exception in SP_CHNL_TRANSFER_OUT_DATA_PROC for transfer';
			   aov_sqlerrMsgForLog:=SQLERRM;
			  RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
			--    RAISE notice 'I AM HERE 16';
			       mis_sno := mis_sno+1;
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_out_data_proc R',n_date_for_mis,CURRENT_TIMESTAMP);
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_sp_chnl_transfer_out_data_proc (n_date_for_mis, 'R');
		EXCEPTION
               
			       WHEN OTHERS THEN
				  RAISE NOTICE '%','OTHERS EXCEPTION in SP_CHNL_TRANSFER_OUT_DATA_PROC for return:'|| SQLERRM;
				   aov_messageForLog:='OTHERS EXCEPTION in SP_CHNL_TRANSFER_OUT_DATA_PROC for return';
				    aov_sqlerrMsgForLog:=SQLERRM;
				  RAISE EXCEPTION  using errcode = 'ERR01';
               END;

            BEGIN
			--    RAISE notice 'I AM HERE 17';
			       mis_sno := mis_sno+1;
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_out_data_proc W',n_date_for_mis,CURRENT_TIMESTAMP);
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_sp_chnl_transfer_out_data_proc (n_date_for_mis, 'W');
            EXCEPTION
               
			       WHEN OTHERS THEN
				  RAISE NOTICE '%',   'OTHERS EXCEPTION in SP_CHNL_TRANSFER_OUT_DATA_PROC for withdraw:' || SQLERRM;
			      aov_messageForLog:='OTHERS EXCEPTION in SP_CHNL_TRANSFER_OUT_DATA_PROC for withdraw';
			      aov_sqlerrMsgForLog:=SQLERRM;
				 RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
		       mis_sno := mis_sno+1;
		       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_out_data_proc X',n_date_for_mis,CURRENT_TIMESTAMP);
		       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_sp_chnl_transfer_out_data_proc (n_date_for_mis, 'X');
            EXCEPTION
               
			       WHEN OTHERS THEN
				  RAISE NOTICE '%','OTHERS EXCEPTION in SP_CHNL_TRANSFER_OUT_DATA_PROC for reversal:'|| SQLERRM;
				   aov_messageForLog:='OTHERS EXCEPTION in SP_CHNL_TRANSFER_OUT_DATA_PROC for reversal';
				    aov_sqlerrMsgForLog:=SQLERRM;
				  RAISE EXCEPTION  using errcode = 'ERR01';
               END;

            BEGIN
		 --   RAISE notice 'I AM HERE 18';
		       mis_sno := mis_sno+1;
		       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_in_data_proc T',n_date_for_mis,CURRENT_TIMESTAMP);
		        RAISE notice 'I AM HERE 18-1';
		       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_sp_chnl_transfer_in_data_proc (n_date_for_mis, 'T');
		       
	EXCEPTION
               
               WHEN OTHERS THEN
			  RAISE NOTICE '%',   'OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for transfer:' || SQLERRM;
		      aov_messageForLog:='OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for transfer';
		      aov_sqlerrMsgForLog:=SQLERRM;
			  RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
			--    RAISE notice 'I AM HERE 190';
			       mis_sno := mis_sno+1;
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_in_data_proc R',n_date_for_mis,CURRENT_TIMESTAMP);
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_sp_chnl_transfer_in_data_proc (n_date_for_mis, 'R');
            EXCEPTION
               
			       WHEN OTHERS THEN
				  RAISE NOTICE '%',   'OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for return:' || SQLERRM;
			      aov_messageForLog:='OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for return';
			      aov_sqlerrMsgForLog:=SQLERRM;
				  RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
			       mis_sno := mis_sno+1;
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_in_data_proc W',n_date_for_mis,CURRENT_TIMESTAMP);
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_sp_chnl_transfer_in_data_proc (n_date_for_mis, 'W');
            EXCEPTION
               
			       WHEN OTHERS
			       THEN
				  RAISE NOTICE '%',   'OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for withdraw:' || SQLERRM;
			      aov_messageForLog:='OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for withdraw';
			      aov_sqlerrMsgForLog:=SQLERRM;
				  RAISE EXCEPTION  using errcode = 'ERR01';
            END;
            BEGIN
			--    RAISE notice 'I AM HERE 19';
			       mis_sno := mis_sno+1;
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_chnl_transfer_in_data_proc X',n_date_for_mis,CURRENT_TIMESTAMP);
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_sp_chnl_transfer_in_data_proc (n_date_for_mis, 'X');
            EXCEPTION
               
			       WHEN OTHERS
			       THEN
				  RAISE NOTICE '%',
					 'OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for reversal:'
					|| SQLERRM
		;
			      aov_messageForLog:='OTHERS EXCEPTION in SP_CHNL_TRANSFER_IN_DATA_PROC for reversal';
			      aov_sqlerrMsgForLog:=SQLERRM;
				  RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
			--    RAISE notice 'I AM HERE 20';
			       mis_sno := mis_sno+1;
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before ret_refills_data_proc',n_date_for_mis,CURRENT_TIMESTAMP);
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_ret_refills_data_proc (n_date_for_mis);

            EXCEPTION
              
			       WHEN OTHERS
			       THEN
				  RAISE NOTICE '%',   'OTHERS EXCEPTION in RET_REFILLS_DATA_PROC:' || SQLERRM;
			      aov_messageForLog:='OTHERS EXCEPTION in RET_REFILLS_DATA_PROC';
			      aov_sqlerrMsgForLog:=SQLERRM;
				  RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
			--    RAISE notice 'I AM HERE 21';
			       mis_sno := mis_sno+1;
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before ret_reverse_data_proc',n_date_for_mis,CURRENT_TIMESTAMP);
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_ret_reverse_data_proc(n_date_for_mis);
            EXCEPTION
               
			       WHEN OTHERS
			       THEN
				  RAISE NOTICE '%',   'OTHERS EXCEPTION in ret_reverse_data_proc:' || SQLERRM;
			      aov_messageForLog:='OTHERS EXCEPTION in ret_reverse_data_proc';
			      aov_sqlerrMsgForLog:=SQLERRM;
				  RAISE EXCEPTION  using errcode = 'ERR01';
            END;
            
            BEGIN
			--    RAISE notice 'I AM HERE 22';
			       mis_sno := mis_sno+1;
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before Sp_Update_Daily_Chnl_Trans_Det',n_date_for_mis,CURRENT_TIMESTAMP);
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_Sp_Update_Daily_Chnl_Trans_Det (n_date_for_mis);
            EXCEPTION
               
			       WHEN OTHERS
			       THEN
				  RAISE NOTICE '%',   'OTHERS EXCEPTION in SP_UPDATE_DAILY_CHNL_TRANS_DET:' || SQLERRM;
			      aov_messageForLog:='OTHERS EXCEPTION in SP_UPDATE_DAILY_CHNL_TRANS_DET';
			      aov_sqlerrMsgForLog:=SQLERRM;
				 RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
			--    RAISE notice 'I AM HERE 23';
			       mis_sno := mis_sno+1;
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_insert_opening_bal',n_date_for_mis,CURRENT_TIMESTAMP);
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_sp_insert_opening_bal (n_date_for_mis);
            EXCEPTION
               
			       WHEN OTHERS
			       THEN
				  RAISE NOTICE '%',   'OTHERS EXCEPTION in sp_insert_opening_bal:' || SQLERRM;
			      aov_messageForLog:='OTHERS EXCEPTION in sp_insert_opening_bal';
			      aov_sqlerrMsgForLog:=SQLERRM;
				 RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
			--    RAISE notice 'I AM HERE 24';
				mis_sno := mis_sno+1;
				INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before move_to_final_data',n_date_for_mis,CURRENT_TIMESTAMP);
				select * into v_messageforlog,v_sqlerrmsgforlog from  mis_move_to_final_data (n_date_for_mis);
            EXCEPTION
               
			       WHEN OTHERS
			       THEN
				  RAISE NOTICE '%',   'OTHERS EXCEPTION in MOVE_TO_FINAL_DATA:' || SQLERRM;
			      aov_messageForLog:='EXCEPTION in MOVE_TO_FINAL_DATA';
			      aov_sqlerrMsgForLog:=SQLERRM;
				  RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
			--    RAISE notice 'I AM HERE 25';
			       mis_sno := mis_sno+1;
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before move_users_to_new_date',n_date_for_mis,CURRENT_TIMESTAMP);
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_move_users_to_new_date (n_date_for_mis);
            EXCEPTION
               
			       WHEN OTHERS
			       THEN
				  RAISE NOTICE '%',   'OTHERS EXCEPTION in MOVE_USERS_TO_NEW_DATE:'|| SQLERRM;
			      aov_messageForLog:='EXCEPTION in MOVE_USERS_TO_NEW_DATE';
			      aov_sqlerrMsgForLog:=SQLERRM;
				 RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
			--    RAISE notice 'I AM HERE 26';
			       mis_sno := mis_sno+1;
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_update_c2s_sub_denom',n_date_for_mis,CURRENT_TIMESTAMP);
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_sp_update_c2s_sub_denom (n_date_for_mis);
            EXCEPTION
               
			       WHEN OTHERS
			       THEN
				  RAISE NOTICE '%','OTHERS EXCEPTION in SP_UPDATE_C2S_SUB_DENOM:'|| SQLERRM;
			      aov_messageForLog:='OTHERS EXCEPTION in SP_UPDATE_C2S_SUB_DENOM';
			      aov_sqlerrMsgForLog:=SQLERRM;
				  RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
			 --   RAISE notice 'I AM HERE 27';
			       mis_sno := mis_sno+1;
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_update_daily_trn_summary',n_date_for_mis,CURRENT_TIMESTAMP);
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_sp_update_daily_trn_summary (n_date_for_mis,'N');
            EXCEPTION
               
			       WHEN OTHERS
			       THEN
				  RAISE NOTICE '%',' OTHERS EXCEPTION in SP_UPDATE_DAILY_TRN_SUMMARY'|| SQLERRM;
			      aov_messageForLog:='OTHERS EXCEPTION in SP_UPDATE_DAILY_TRN_SUMMARY';
			      aov_sqlerrMsgForLog:=SQLERRM;
				  RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
			--      RAISE notice 'I AM HERE 28';
			       mis_sno := mis_sno+1;
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_update_c2s_success_failure',n_date_for_mis,CURRENT_TIMESTAMP);
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_sp_update_c2s_success_failure (n_date_for_mis);
            EXCEPTION
               
			       WHEN OTHERS
			       THEN
				  RAISE NOTICE '%','OTHERS EXCEPTION in SP_UPDATE_C2S_SUCCESS_FAILURE'|| SQLERRM;
			      aov_messageForLog:='OTHERS EXCEPTION in SP_UPDATE_C2S_SUCCESS_FAILURE';
			      aov_sqlerrMsgForLog:=SQLERRM;
				 RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
			--      RAISE notice 'I AM HERE 29';
			       mis_sno := mis_sno+1;
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before ret_refills_failure_data_proc',n_date_for_mis,CURRENT_TIMESTAMP);
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_ret_refills_failure_data_proc (n_date_for_mis);
            EXCEPTION
               
			       WHEN OTHERS
			       THEN
				  RAISE NOTICE '%',   'OTHERS EXCEPTION in RET_REFILLS_FAILURE_DATA_PROC:' || SQLERRM;
			      aov_messageForLog:='OTHERS EXCEPTION in RET_REFILLS_FAILURE_DATA_PROC';
			      aov_sqlerrMsgForLog:=SQLERRM;
				 RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
			     ---check msisdn usage summary need to populate or not
			    msisdn_usage_summ_flag= (SELECT 1
			     FROM SYSTEM_PREFERENCES
			     WHERE PREFERENCE_CODE='MSISDN_USAGE_SUMM_FLAG' AND DEFAULT_VALUE<>'TRUE');
             
				if msisdn_usage_summ_flag is null then 
				    BEGIN
						       mis_sno := mis_sno+1;
						       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before sp_update_c2s_msisdn_usage',n_date_for_mis,CURRENT_TIMESTAMP);
						        select * into v_messageforlog,v_sqlerrmsgforlog from  mis_sp_update_c2s_msisdn_usage (n_date_for_mis);
				    EXCEPTION
				       
						       WHEN OTHERS
						       THEN
							  RAISE NOTICE '%','OTHERS EXCEPTION in SP_UPDATE_C2S_MSISDN_USAGE'|| SQLERRM;
						     aov_messageForLog:='OTHERS EXCEPTION in SP_UPDATE_C2S_MSISDN_USAGE';
						     aov_sqlerrMsgForLog:=SQLERRM;
							 RAISE EXCEPTION  using errcode = 'ERR01';
				    END;
				    end if;
				    EXCEPTION
				     WHEN OTHERS
						       THEN
							  RAISE NOTICE '%','OTHERS EXCEPTION in SP_UPDATE_C2S_MSISDN_USAGE1'|| SQLERRM;
						     aov_messageForLog:='OTHERS EXCEPTION in SP_UPDATE_C2S_MSISDN_USAGE';
						     aov_sqlerrMsgForLog:=SQLERRM;
							 RAISE EXCEPTION  using errcode = 'ERR01';
				    
            END;
            --new procedure added for c2s_bonus removal
            BEGIN
			--      RAISE notice 'I AM HERE 31';
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_sp_split_bonus_details (n_date_for_mis);
            EXCEPTION
               
			       WHEN OTHERS
			       THEN
				  RAISE NOTICE '%',' OTHERS EXCEPTION in sp_split_bonus_details 3'|| SQLERRM;
			      aov_messageForLog:='OTHERS EXCEPTION in sp_split_bonus_details';
			      aov_sqlerrMsgForLog:=SQLERRM;
				  RAISE EXCEPTION  using errcode = 'ERR01';
            END;

            BEGIN
			--    RAISE notice 'I AM HERE 32';
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_sp_update_c2s_bonuses (n_date_for_mis);
            EXCEPTION
			       
			       WHEN OTHERS
			       THEN
				  RAISE NOTICE '%','EXCEPTION in sp_update_c2s_bonuses'|| SQLERRM;
			      aov_messageForLog:='EXCEPTION in sp_update_c2s_bonuses';
			      aov_sqlerrMsgForLog:=SQLERRM;
				  RAISE EXCEPTION  using errcode = 'ERR01';
            END;

         BEGIN
		--	 RAISE notice 'I AM HERE 330';
			    mis_sno := mis_sno+1;
			       INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before refills_lms_c2s_summary_proc',n_date_for_mis,CURRENT_TIMESTAMP);
			       select * into v_messageforlog,v_sqlerrmsgforlog from  mis_refills_lms_c2s_summary_proc (n_date_for_mis);
            EXCEPTION
               
			       WHEN OTHERS
			       THEN
				  RAISE NOTICE '%','EXCEPTION in refills_lms_c2s_summary_proc'|| SQLERRM;
			      aov_messageForLog:='EXCEPTION in refills_lms_c2s_summary_proc';
			      aov_sqlerrMsgForLog:=SQLERRM;
				 RAISE EXCEPTION  using errcode = 'ERR01';
            END;
       
            --  monthly table update for ambigues settled case
         --   RAISE notice 'I AM HERE8';
            UPDATE PROCESS_STATUS SET executed_upto=n_date_for_mis, executed_on=CURRENT_TIMESTAMP WHERE PROCESS_ID='C2SMIS';
            mis_sno := mis_sno+1;
            INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'C2S MIS Successfully Executed for the date',n_date_for_mis,CURRENT_TIMESTAMP);
            /* COMMIT; */

            aov_message :='SUCCESS';
            aov_messageForLog :='PreTUPS C2S MIS successfully executed, Date Time:'||CURRENT_TIMESTAMP;
            aov_sqlerrMsgForLog :=' ';

             /*WHEN TOO_MANY_ROWS THEN
             DBMS_OUTPUT.PUT_LINE('Underprocess or ambigous transaction found. PreTUPS C2S MIS cannot continue, Date:' || n_date_for_mis);
             aov_messageForLog:='Underprocess or ambigous transaction found. PreTUPS C2S MIS cannot continue, Date:' || n_date_for_mis;
             aov_sqlerrMsgForLog:=' ';
             flag:=1;

             WHEN OTHERS THEN
                DBMS_OUTPUT.PUT_LINE('OTHERS Error when checking for underprocess or ambigous transactions'||SQLERRM);
                aov_messageForLog:='Error when checking for underprocess or ambigous transactions, Date:'|| n_date_for_mis;
                 aov_sqlerrMsgForLog:=SQLERRM;
                RAISE mainException;
          END;*/


         --WHEN alreadyDoneException THEN
         END IF;
          EXCEPTION
         --RAISE notice 'I AM HERE7';
         
         when sqlstate 'ERR02' then--exception handled in case MIS already executed
          aov_sqlerrMsgForLog:=SQLERRM;
          RAISE notice 'I AM HERE 33';
          RAISE EXCEPTION using errcode = 'ERR01';

         WHEN OTHERS THEN
          RAISE NOTICE '%','OTHERS Error when checking if MIS process has already been executed'||SQLERRM;
          aov_messageForLog:='OTHERS Error when checking if MIS process has already been executed, Date:'|| n_date_for_mis;
          aov_sqlerrMsgForLog:=SQLERRM;
          RAISE notice 'I AM HERE 34';
          RAISE EXCEPTION  using errcode = 'ERR01';
	
    END;
--	RAISE notice 'I AM HERE 35';
	IF flag = 1 THEN
	n_date_for_mis := ld_to_date; ---If Underprocess or Anbigous transaction found then stop the MIS process for further execution of other dates
	RAISE EXCEPTION  using errcode = 'ERR01';
	ELSE
	n_date_for_mis:=n_date_for_mis+interval '1 day' ;
	END IF;

END LOOP;

EXCEPTION --Exception Handling of main procedure
 when sqlstate 'ERR01' then-- WHEN mainException THEN
  RAISE NOTICE '%','mainException Caught='||SQLERRM;
  aov_message :='FAILED';
  
  
  WHEN OTHERS THEN 
  RAISE NOTICE '%','OTHERS ERROR in Main procedure:='||SQLERRM;
  aov_message :='FAILED';
  
	
END;
$$;


ALTER FUNCTION pretupsdatabase.mis_sp_get_mis_data_dtrange(aiv_fromdate character varying, aiv_todate character varying, OUT aov_message character varying, OUT aov_messageforlog character varying, OUT aov_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_sp_get_mis_mon_data_dtrange(character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_get_mis_mon_data_dtrange(aiv_fromdate character varying, aiv_todate character varying, OUT aov_message character varying, OUT aov_messageforlog character varying, OUT aov_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE 
n_date_for_mis         PROCESS_STATUS.executed_upto%TYPE;
gd_createdon           DATE;
v_message              VARCHAR(500);
   v_messageforlog        VARCHAR(500);
   v_sqlerrmsgforlog      VARCHAR(500);
  ld_from_date TIMESTAMP(0);
  ld_to_date TIMESTAMP(0);
  ld_created_on TIMESTAMP(0);
  flag SMALLINT;
  mis_sno SMALLINT;
  status SMALLINT;
  mis_already_executed SMALLINT;
  sql_stmt1        VARCHAR (2000);

BEGIN

  ld_from_date   :=TO_DATE(aiv_fromDate,'dd/mm/yy');
  ld_to_date     :=TO_DATE(aiv_toDate,'dd/mm/yy');
  n_date_for_mis :=ld_from_date;
  flag           :=0;
  ld_created_on  :=CURRENT_TIMESTAMP;  -- Initailaize Created On date
  gd_createdon := ld_created_on;
  mis_already_executed :=0;
  sql_stmt1 := '';

WHILE n_date_for_mis <= ld_to_date ---run the MIS process for each date less than the To Date
   LOOP
    RAISE NOTICE '%','EXCEUTING FOR ::::::::'||n_date_for_mis;
    BEGIN
         ---Check if MIS process has already run for the date
         mis_sno := 19;
         mis_already_executed=(SELECT 1
         FROM PROCESS_STATUS
         WHERE PROCESS_ID='C2SMISMON' AND EXECUTED_UPTO>=n_date_for_mis);
         if mis_already_executed is not null then
         RAISE NOTICE '%','PreTUPS C2S MIS Monthly Data already Executed, Date:' || n_date_for_mis;
         aov_message :='FAILED';
         aov_messageForLog:='PreTUPS C2S MIS Monthly Data already Executed, Date:' || n_date_for_mis;
         aov_sqlerrMsgForLog:=' ';
         --RAISE alreadyDoneException;
         RAISE EXCEPTION  using errcode ='ERR02';
         else
        
             BEGIN
             ---Check if Underprocess or Ambigous transactions are found in the Transaction table for the date
             mis_already_executed=(SELECT 1 
            FROM PROCESS_STATUS
            WHERE PROCESS_ID='C2SMIS' AND EXECUTED_UPTO<n_date_for_mis);
            if mis_already_executed is not null then
              RAISE NOTICE '%','PreTUPS C2S MIS for Monthly Data cannot continue, first execute C2S MIS Daily Data process, Date:' || n_date_for_mis;
              aov_messageForLog:='PreTUPS C2S MIS for Monthly Data cannot continue, first execute C2S MIS Daily Data process, Date:' || n_date_for_mis;
              aov_sqlerrMsgForLog:=' ';
             flag:=1;
             else
          

                BEGIN
                   mis_sno := mis_sno+1;
                   INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before Sp_Update_Monthly_Data1',n_date_for_mis,CURRENT_TIMESTAMP);
                   select * into v_messageforlog,v_sqlerrmsgforlog from mis_sp_update_monthly_data1 (n_date_for_mis);
                EXCEPTION

                   WHEN OTHERS
                   THEN
                      RAISE NOTICE '%',   'OTHERS EXCEPTION in SP_UPDATE_MONTHLY_DATA1:' || SQLERRM;
                 aov_messageForLog:='EXCEPTION in SP_UPDATE_MONTHLY_DATA1';
                 aov_sqlerrMsgForLog:=SQLERRM;
                       RAISE EXCEPTION  using errcode = 'ERR01';
                END;

                BEGIN
                   mis_sno := mis_sno+1;
                   INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before Sp_Update_Monthly_Data2',n_date_for_mis,CURRENT_TIMESTAMP);
                   select * into v_messageforlog,v_sqlerrmsgforlog from mis_sp_update_monthly_data2 (n_date_for_mis);
                EXCEPTION
                   
                   WHEN OTHERS
                   THEN
                      RAISE NOTICE '%',   'OTHERS EXCEPTION in SP_UPDATE_MONTHLY_DATA2:' || SQLERRM;
                 aov_messageForLog:='EXCEPTION in SP_UPDATE_MONTHLY_DATA2';
                 aov_sqlerrMsgForLog:=SQLERRM;
                      RAISE EXCEPTION  using errcode = 'ERR01';
                END;

                BEGIN
                   mis_sno := mis_sno+1;
                   INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before Sp_Update_Monthly_Data3',n_date_for_mis,CURRENT_TIMESTAMP);
                   select * into v_messageforlog,v_sqlerrmsgforlog from mis_sp_update_monthly_data3 (n_date_for_mis);
                EXCEPTION
                   
                   WHEN OTHERS
                   THEN
                      RAISE NOTICE '%',   'OTHERS EXCEPTION in SP_UPDATE_MONTHLY_DATA3:' || SQLERRM;
                 aov_messageForLog:='EXCEPTION in SP_UPDATE_MONTHLY_DATA3';
                 aov_sqlerrMsgForLog:=SQLERRM;
                      RAISE EXCEPTION  using errcode = 'ERR01';
                END;

                BEGIN
                   mis_sno := mis_sno+1;
                   INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before Sp_Update_Mon_AmbSettData1',n_date_for_mis,CURRENT_TIMESTAMP);
                   select * into v_messageforlog,v_sqlerrmsgforlog from mis_sp_update_mon_ambsettdata1 (n_date_for_mis);
                EXCEPTION
                  
                   WHEN OTHERS
                   THEN
                      RAISE NOTICE '%',   'OTHERS EXCEPTION in Sp_Update_Mon_AmbSettData1:' || SQLERRM;
                 aov_messageForLog:='EXCEPTION in Sp_Update_Mon_AmbSettData1';
                 aov_sqlerrMsgForLog:=SQLERRM;
                      RAISE EXCEPTION  using errcode = 'ERR01';
                END;
                
                BEGIN
                   mis_sno := mis_sno+1;
                   INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'before Sp_Update_Mon_AmbSettData3',n_date_for_mis,CURRENT_TIMESTAMP);
                   --Sp_Update_Mon_AmbSettData3 (n_date_for_mis);
                EXCEPTION
                
                   WHEN OTHERS
                   THEN
                      RAISE NOTICE '%',   'OTHERS EXCEPTION in Sp_Update_Mon_AmbSettData3:' || SQLERRM;
                 aov_messageForLog:='EXCEPTION in Sp_Update_Mon_AmbSettData3';
                 aov_sqlerrMsgForLog:=SQLERRM;
                      RAISE EXCEPTION  using errcode = 'ERR01';
                END;

                
                
                 UPDATE PROCESS_STATUS SET executed_upto=n_date_for_mis, executed_on=CURRENT_TIMESTAMP WHERE PROCESS_ID='C2SMISMON';
                 mis_sno := mis_sno+1;
                 INSERT INTO C2SMIS_LOGS VALUES (mis_sno, 'C2S MIS For Monthly Data Successfully Executed for the date',n_date_for_mis,CURRENT_TIMESTAMP);

                 /* COMMIT; */

                  aov_message :='SUCCESS';
                  aov_messageForLog :='PreTUPS C2S MIS for Monthly Data successfully executed, Date Time:'||CURRENT_TIMESTAMP;
                  aov_sqlerrMsgForLog :=' ';
                  END IF;
         EXCEPTION

             WHEN OTHERS THEN
                 RAISE NOTICE '%','OTHERS Error when checking for underprocess or ambigous transactions'||SQLERRM;
                 aov_messageForLog:='Error when checking for underprocess or ambigous transactions, Date:'|| n_date_for_mis;
                 aov_sqlerrMsgForLog:=SQLERRM;
                 RAISE EXCEPTION  using errcode = 'ERR01';
          END;
	END IF;
	EXCEPTION
          when sqlstate 'ERR02' then--exception handled in case MIS already executed
           aov_sqlerrMsgForLog:=SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';

          WHEN OTHERS THEN
          RAISE NOTICE '%','OTHERS Error when checking if MIS process has already been executed'||SQLERRM;
          aov_messageForLog:='OTHERS Error when checking if MIS process has already been executed, Date:'|| n_date_for_mis;
           aov_sqlerrMsgForLog:=SQLERRM;
          RAISE EXCEPTION  using errcode = 'ERR01';

  END;

IF flag = 1 THEN
n_date_for_mis := ld_to_date;
RAISE EXCEPTION  using errcode = 'ERR01';
ELSE
n_date_for_mis:=n_date_for_mis+interval '1 day';
END IF;
END LOOP;

EXCEPTION
  when sqlstate 'ERR01' then

  RAISE NOTICE '%','mainException Caught='||SQLERRM;
  aov_message :='FAILED';

  WHEN OTHERS THEN

  RAISE NOTICE '%','OTHERS ERROR in Main procedure:='||SQLERRM;
  aov_message :='FAILED';

END;
$$;


ALTER FUNCTION pretupsdatabase.mis_sp_get_mis_mon_data_dtrange(aiv_fromdate character varying, aiv_todate character varying, OUT aov_message character varying, OUT aov_messageforlog character varying, OUT aov_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_sp_insert_opening_bal(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_insert_opening_bal(p_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
   DECLARE
      n_previousdate     TEMP_DAILY_CHNL_TRANS_MAIN.trans_date%TYPE;
      n_user_id    TEMP_DAILY_CHNL_TRANS_MAIN.user_id%TYPE;
      n_opning_stock    TEMP_DAILY_CHNL_TRANS_MAIN.opening_balance%TYPE;
      n_closing_stock    TEMP_DAILY_CHNL_TRANS_MAIN.closing_balance%TYPE;
      n_network_code    TEMP_DAILY_CHNL_TRANS_MAIN.network_code%TYPE;
      n_network_code_for    TEMP_DAILY_CHNL_TRANS_MAIN.network_code_for%TYPE;
      n_product_code        TEMP_DAILY_CHNL_TRANS_MAIN.product_code%TYPE;
      n_grph_domain_code     TEMP_DAILY_CHNL_TRANS_MAIN.grph_domain_code%TYPE;
      rcd_count          INT;
      n_max_balance_date TEMP_DAILY_CHNL_TRANS_MAIN.trans_date%TYPE;


  declare  msisdn_usage_cursor CURSOR (p_date TIMESTAMP(0))
  FOR
   SELECT user_id, opening_balance, closing_balance, network_code, network_code_for, product_code, grph_domain_code
   FROM TEMP_DAILY_CHNL_TRANS_MAIN
                WHERE trans_date = p_date;

   BEGIN
 RAISE notice 'inside  mis_sp_insert_opening_bal';
   FOR transaction_record IN msisdn_usage_cursor  (p_date)
   LOOP
        n_user_id := transaction_record.user_id;
        n_network_code := transaction_record.network_code;
        n_network_code_for := transaction_record.network_code_for;
        n_product_code := transaction_record.product_code;
        n_grph_domain_code := transaction_record.grph_domain_code;

         BEGIN
		-- RAISE notice 'SELECT FROM USER_DAILY_BALANCES';
               n_closing_stock=(SELECT /*+ INDEX(USER_DAILY_BALANCES IND_UDB_DT_ID_PROD) */ balance 
               FROM USER_DAILY_BALANCES
               WHERE user_id = n_user_id
               AND network_code_for = n_network_code_for
               AND balance_date = p_date - interval '1 day'
               AND product_code = n_product_code
               AND network_code = n_network_code);
		raise notice 'n_closing_stock:%',n_closing_stock;
		if n_closing_stock is null then
		raise notice 'setting rcd count 0';
		rcd_count := 0;
		end if;
        
   
             EXCEPTION   
            WHEN OTHERS
            THEN
               RAISE NOTICE '%', 'OTHERS Exception in SP_INSERT_OPENING_BAL 1, User:' || n_user_id || SQLERRM;
               v_messageforlog := 'OTHERS Exception in SP_INSERT_OPENING_BAL 1, User' || n_user_id || ' Date:' || p_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION  using errcode = 'ERR07';
         END;

         IF (rcd_count = 0)
         THEN
            BEGIN

                n_closing_stock=(SELECT /*+ INDEX(USER_DAILY_BALANCES IND_UDB_DT_ID_PROD) */ balance 
                FROM USER_DAILY_BALANCES
                    WHERE user_id = n_user_id
                    AND network_code_for = n_network_code_for
                    AND product_code = n_product_code
                    AND network_code = n_network_code
                    AND balance_date = (SELECT MAX(balance_date) FROM USER_DAILY_BALANCES
                        WHERE user_id = n_user_id
                        AND network_code_for = n_network_code_for
                        AND product_code = n_product_code
                        AND network_code = n_network_code
                        AND balance_date < p_date));
		raise notice 'n_closing_stock:%',n_closing_stock;
		if n_closing_stock is null then
		raise notice 'setting n_closing_stock 0';
		 n_closing_stock := 0;
		end if;
               
            EXCEPTION
                                 
               WHEN OTHERS
               THEN
                  RAISE NOTICE '%', 'OTHERS Exception in SP_INSERT_OPENING_BAL 2, User:' || n_user_id || SQLERRM;
                  v_messageforlog := 'OTHERS Exception in SP_INSERT_OPENING_BAL 2, User:' || n_user_id || ' Date:' || p_date;
                  v_sqlerrmsgforlog := SQLERRM;
                  RAISE EXCEPTION  using errcode = 'ERR07';
            END;
        END IF;

        BEGIN
	--RAISE notice 'UPDATEing TEMP_DAILY_CHNL_TRANS_MAIN';
         UPDATE TEMP_DAILY_CHNL_TRANS_MAIN
           SET opening_balance = n_closing_stock
         WHERE user_id = n_user_id
           AND trans_date = p_date
           AND product_code = n_product_code
           AND network_code = n_network_code
           AND network_code_for = n_network_code_for;
	--RAISE notice 'UPDATED TEMP_DAILY_CHNL_TRANS_MAIN';
        EXCEPTION
           WHEN OTHERS
           THEN
              RAISE NOTICE '%', 'Exception in SP_INSERT_OPENING_BAL 3, User:' || n_user_id || SQLERRM;
              v_messageforlog := 'Exception in SP_INSERT_OPENING_BAL 3, User:' || n_user_id || ' Date:' || p_date;
              v_sqlerrmsgforlog := SQLERRM;
              RAISE EXCEPTION  using errcode = 'ERR07';
        END;

    END LOOP;
     raise notice 'mis_sp_insert_opening_bal SUCCESS';
	   v_messageforlog := 'mis_sp_insert_opening_bal SUCCESS';
	v_sqlerrmsgforlog := 'mis_sp_insert_opening_bal NO error ';  
    
EXCEPTION
   when sqlstate 'ERR07' then
         RAISE NOTICE 'SQLexception in mis_sp_insert_opening_bal 3';
         RAISE EXCEPTION  using errcode = 'ERR01';
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION in SP_INSERT_OPENING_BAL';
          RAISE EXCEPTION  using errcode = 'ERR01';
END;
  $$;


ALTER FUNCTION pretupsdatabase.mis_sp_insert_opening_bal(p_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_sp_split_bonus_details(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_split_bonus_details(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$

DECLARE 

    sp_transferID           C2S_TRANSFERS.TRANSFER_ID%TYPE;
    sp_bundleType     C2S_BONUSES_MISTMP.ACCOUNT_TYPE%TYPE;
    sp_amount            C2S_BONUSES_MISTMP.BALANCE%TYPE;
    sp_bundleID          C2S_BONUSES_MISTMP.ACCOUNT_ID%TYPE;
    bonus_details VARCHAR(2000);
    bon_det_separated_by_pipe  varchar(50) ARRAY[400];
    bon_det_separated_by_colon  varchar(50) ARRAY[400];
    colon_separated_string VARCHAR(50);
    i	INT;

    --------Declaration For The Cursor

    declare split_bonus_cursor CURSOR(in_date DATE)
    for
    SELECT ct.transfer_date,ct.BONUS_DETAILS, ct.TRANSFER_ID FROM C2S_TRANSFERS_MISTMP ct
    WHERE ct.TRANSFER_STATUS='200'; --AND ct.TRANSFER_DATE=in_date;
    BEGIN
    RAISE notice 'inside  mis_sp_split_bonus_details';
        FOR split_bonus_record IN split_bonus_cursor (aiv_date)
        LOOP
            bonus_details := split_bonus_record.BONUS_DETAILS;
            sp_transferID   :=split_bonus_record.TRANSFER_ID;
            BEGIN       
               bon_det_separated_by_pipe := string_to_array(bonus_details, '|'); 
              IF array_length(bon_det_separated_by_pipe,1) IS NOT NULL THEN
              for i in array_lower(bon_det_separated_by_pipe, 1)..array_upper(bon_det_separated_by_pipe, 1) 
                LOOP
                    colon_separated_string := bon_det_separated_by_pipe[i];
                    bon_det_separated_by_colon := string_to_array(colon_separated_string,':');
                    sp_bundleID := RTRIM(bon_det_separated_by_colon[1],':');
                 
                    sp_bundleType := RTRIM(bon_det_separated_by_colon[2],':');
                  
                    sp_amount := TO_NUMBER(RTRIM(bon_det_separated_by_colon[3],':'),'9999999999.99');
                    raise notice 'sp_bundleID: % ', sp_bundleID;
                    raise notice 'sp_amount:  %', sp_amount;
                    raise notice 'sp_bundleType: % ', sp_bundleType;
                    BEGIN
                        INSERT INTO C2S_BONUSES_MISTMP (transfer_id,account_id,account_type,balance) VALUES
                            (sp_transferID,(select bundle_id from bonus_bundle_master where bundle_code=sp_bundleID),sp_bundleType,sp_amount);
                        EXCEPTION
                        
                        WHEN OTHERS
                        THEN
                            RAISE NOTICE '%', 'OTHERS EXCEPTION in SP_SPLIT_BONUS_DETAILS 1'|| SQLERRM;
                            v_messageforlog := 'OTHERS EXCEPTION in SP_SPLIT_BONUS_DETAILS 1';
                            v_sqlerrmsgforlog := SQLERRM;
                           RAISE EXCEPTION  using errcode = 'ERR05';
                    END;
                END LOOP;
                END IF;
            END;
        END LOOP;
         raise notice 'SP_UPDATE_DAILY_CHNL_TRANS_DET SUCCESS';
	   v_messageforlog := 'SP_UPDATE_DAILY_CHNL_TRANS_DET SUCCESS';
	v_sqlerrmsgforlog := 'SP_UPDATE_DAILY_CHNL_TRANS_DET NO error ';  
EXCEPTION
when sqlstate 'ERR05' then
         RAISE NOTICE 'procexception in SP_UPDATE_DAILY_CHNL_TRANS_DET 3:%',SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
WHEN OTHERS
    THEN
        RAISE NOTICE '%', 'OTHERS EXCEPTION in SP_SPLIT_BONUS_DETAILS 2:' || SQLERRM;
        RAISE EXCEPTION  using errcode = 'ERR01';
END ;
$$;


ALTER FUNCTION pretupsdatabase.mis_sp_split_bonus_details(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_sp_update_c2s_bonuses(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_update_c2s_bonuses(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE 
v_message              VARCHAR (500);
       
    in_transferDate        C2S_TRANSFERS.TRANSFER_DATE%TYPE;
    in_serviceType         C2S_TRANSFERS.SERVICE_TYPE%TYPE;
    in_sub_service         C2S_TRANSFERS.SUB_SERVICE%TYPE;
    in_bundleID            C2S_BONUSES_MISTMP.ACCOUNT_ID%TYPE;
    in_bundleType          C2S_BONUSES_MISTMP.ACCOUNT_TYPE%TYPE;
    in_amount              C2S_BONUSES_MISTMP.BALANCE%TYPE;
    in_trans_count         DAILY_C2S_BONUSES.trans_count%TYPE;
    in_servClassID         C2S_TRANSFERS.SERVICE_CLASS_ID%TYPE;
    in_servClassCode         C2S_TRANSFERS.SERVICE_CLASS_CODE%TYPE;

--------Declaration For The Cursor
    declare c2s_bonus_cursor CURSOR(aiv_date TIMESTAMP(0))
    IS
    SELECT ct.TRANSFER_DATE, ct.SERVICE_TYPE,ct.sub_service, ct.SERVICE_CLASS_ID, ct.SERVICE_CLASS_CODE,
    cb.ACCOUNT_ID,cb.ACCOUNT_TYPE, COUNT(1) trans_count, SUM(cb.BALANCE) balance
    FROM C2S_TRANSFERS_MISTMP ct, C2S_BONUSES_MISTMP cb
    WHERE ct.TRANSFER_STATUS='200'
    AND ct.TRANSFER_ID=cb.TRANSFER_ID
    --AND ct.TRANSFER_DATE=aiv_date
    GROUP BY ct.TRANSFER_DATE, ct.SERVICE_TYPE,ct.sub_service, ct.SERVICE_CLASS_ID,
    ct.SERVICE_CLASS_CODE, cb.ACCOUNT_ID, cb.ACCOUNT_TYPE;

BEGIN

   FOR c2s_bonus_record IN c2s_bonus_cursor (aiv_date)
   LOOP
   in_transferDate :=c2s_bonus_record.TRANSFER_DATE;
   --in_transferDate :=aiv_date;
   in_serviceType :=c2s_bonus_record.SERVICE_TYPE;
   in_sub_service := c2s_bonus_record.SUB_SERVICE;
   in_bundleID :=c2s_bonus_record.ACCOUNT_ID;
   in_bundleType :=c2s_bonus_record.ACCOUNT_TYPE;
   in_amount :=c2s_bonus_record.balance;
   in_trans_count := c2s_bonus_record.trans_count;
   in_servClassID :=c2s_bonus_record.SERVICE_CLASS_ID;
   in_servClassCode :=c2s_bonus_record.SERVICE_CLASS_CODE;

          BEGIN
            INSERT INTO DAILY_C2S_BONUSES
            (
              TRANS_DATE,SERVICE_TYPE,SUB_SERVICE,BUNDLE_ID,BUNDLE_TYPE,TRANS_AMOUNT,
              TRANS_COUNT,SERVICE_CLASS_ID,SERVICE_CLASS_CODE
            )
            VALUES
            (
              in_transferDate,in_serviceType,in_sub_service,in_bundleID,in_bundleType,in_amount,in_trans_count,in_servClassID,   in_servClassCode
            );
          EXCEPTION
            WHEN OTHERS
            THEN
                RAISE NOTICE '%','OTHERS EXCEPTION in sp_update_c2s_bonuses 1'|| SQLERRM;
                v_messageforlog :='OTHERS Error in sp_update_c2s_bonuses 1:'||in_transferDate;
                v_sqlerrmsgforlog := SQLERRM;
           RAISE EXCEPTION  using errcode = 'ERR05';
          END;
   END LOOP;
   raise notice 'sp_update_c2s_bonuses SUCCESS';
	   v_messageforlog := 'sp_update_c2s_bonuses SUCCESS';
	v_sqlerrmsgforlog := 'sp_update_c2s_bonuses NO error ';  
EXCEPTION
when sqlstate 'ERR05' then
         RAISE NOTICE 'procexception in sp_update_c2s_bonuses 3:%',SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';

      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION =' || SQLERRM;
         v_messageforlog :='OTHERS Exception in sp_update_c2s_bonuses 2, Date:'|| in_transferDate;
         v_sqlerrmsgforlog := SQLERRM;
          RAISE EXCEPTION  using errcode = 'ERR01';
END;
$$;


ALTER FUNCTION pretupsdatabase.mis_sp_update_c2s_bonuses(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_sp_update_c2s_msisdn_usage(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_update_c2s_msisdn_usage(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE 

  iv_msisdn                           MSISDN_USAGE_SUMMARY.msisdn%TYPE;
  iv_prefix_id                    MSISDN_USAGE_SUMMARY.prefix_id%TYPE;
  iv_network_code                MSISDN_USAGE_SUMMARY.network_code%TYPE;
  in_success_count                MSISDN_USAGE_SUMMARY.success_count%TYPE;
  in_success_amount                MSISDN_USAGE_SUMMARY.success_amount%TYPE;
  in_fail_count                    MSISDN_USAGE_SUMMARY.fail_count%TYPE;
  in_fail_amount                MSISDN_USAGE_SUMMARY.fail_amount%TYPE;
  iv_service_type                MSISDN_USAGE_SUMMARY.SERVICE_TYPE%TYPE;
    iv_sub_service                MSISDN_USAGE_SUMMARY.SUB_SERVICE%TYPE;
  in_receiver_access_fee        MSISDN_USAGE_SUMMARY.receiver_access_fee%TYPE;
  in_receiver_credit_amount        MSISDN_USAGE_SUMMARY.receiver_credit_amount%TYPE;
  in_receiver_bonus                MSISDN_USAGE_SUMMARY.receiver_bonus%TYPE;
  in_tax1                        MSISDN_USAGE_SUMMARY.tax1%TYPE;
  in_tax2                        MSISDN_USAGE_SUMMARY.tax2%TYPE;
  id_trans_date                      C2S_TRANSFERS_MISTMP.transfer_date%TYPE;
  iv_transfer_status            C2S_TRANSFERS_MISTMP.transfer_status%TYPE;

   ---Cursor Declaration
   declare msisdn_usage_cursor CURSOR (aiv_date TIMESTAMP(0))
   FOR
   SELECT ct.receiver_msisdn,ct.prefix_id,ct.transfer_date,
   ct.network_code,ct.SERVICE_TYPE,ct.sub_service,ct.transfer_status,
   SUM(ct.transfer_value) transfer_value,
   SUM(ct.receiver_access_fee) receiver_access_fee,
   SUM(ct.receiver_bonus_value) receiver_bonus_value,
   SUM(ct.receiver_transfer_value) receiver_transfer_value,
   SUM(ct.receiver_tax1_value) receiver_tax1_value,
   SUM(ct.receiver_tax2_value) receiver_tax2_value,
   COUNT(ct.transfer_date) COUNT
   FROM C2S_TRANSFERS_MISTMP ct
   --WHERE ct.transfer_date=aiv_date
   GROUP BY ct.transfer_date,ct.receiver_msisdn,ct.prefix_id,
   ct.network_code,ct.SERVICE_TYPE,ct.sub_service,ct.transfer_status;


BEGIN
RAISE notice 'inside  mis_sp_update_c2s_msisdn_usage';
   FOR transaction_record IN msisdn_usage_cursor  (aiv_date)
   LOOP
  in_success_count := 0;
  in_success_amount := 0;
  in_fail_count := 0;
  in_fail_amount := 0;
  in_receiver_access_fee := transaction_record.receiver_access_fee;
  in_receiver_credit_amount := transaction_record.receiver_transfer_value;
  in_receiver_bonus := transaction_record.receiver_bonus_value;
  in_tax1 := transaction_record.receiver_tax1_value;
  in_tax2 := transaction_record.receiver_tax2_value;

  iv_msisdn :=transaction_record.receiver_msisdn;
  iv_prefix_id :=transaction_record.prefix_id;
  iv_network_code :=transaction_record.network_code;
  iv_service_type :=transaction_record.SERVICE_TYPE;
  iv_sub_service :=transaction_record.SUB_SERVICE;
  id_trans_date :=transaction_record.transfer_date;
  --id_trans_date :=aiv_date;
  iv_transfer_status := transaction_record.transfer_status;

      IF iv_transfer_status = '200'
      THEN
         in_success_count := transaction_record.COUNT;
         in_success_amount := transaction_record.transfer_value;
      END IF;

      IF iv_transfer_status = '206'
      THEN
         in_fail_count := transaction_record.COUNT;
         in_fail_amount := transaction_record.transfer_value;
      END IF;


      BEGIN

         UPDATE MSISDN_USAGE_SUMMARY
            SET success_count = success_count + in_success_count,
                fail_count = fail_count + in_fail_count,
                success_amount = success_amount + in_success_amount,
                fail_amount = fail_amount + in_fail_amount,
                receiver_access_fee = receiver_access_fee + in_receiver_access_fee,
                tax1 = tax1 + in_tax1,
                tax2 = tax2 + in_tax2,
                receiver_credit_amount = receiver_credit_amount + in_receiver_credit_amount,
                receiver_bonus = receiver_bonus + in_receiver_bonus
        WHERE month_year = TO_DATE('01-'||TO_CHAR(id_trans_date,'mm-yy'),'dd-mm-yy')
        AND MONTH = TO_CHAR(id_trans_date,'mm')::bigint
        AND MODULE = 'C2S'
        AND network_code = iv_network_code
        AND msisdn = iv_msisdn
        AND prefix_id = iv_prefix_id::bigint
        AND SERVICE_TYPE = iv_service_type
    AND SUB_SERVICE=iv_sub_service;
raise notice 'UPDATE MSISDN_USAGE_SUMMARY1';
         IF NOT FOUND 
         THEN
         raise notice 'INSERT MSISDN_USAGE_SUMMARY';
            INSERT INTO MSISDN_USAGE_SUMMARY(msisdn, prefix_id, MONTH,
                 network_code, success_count, success_amount,
                 fail_count, fail_amount, SERVICE_TYPE,SUB_SERVICE,
                 receiver_access_fee, receiver_credit_amount,
                 receiver_bonus, tax1, tax2, tax3, MODULE, month_year)
                 values (iv_msisdn,iv_prefix_id,TO_CHAR(id_trans_date,'mm')::bigint,
                 iv_network_code,in_success_count,in_success_amount,
                 in_fail_count,in_fail_amount,iv_service_type,iv_sub_service,
                 in_receiver_access_fee,in_receiver_credit_amount,
                   in_receiver_bonus,in_tax1,in_tax2,0,'C2S',
                 TO_DATE('01-'||TO_CHAR(id_trans_date,'mm-yy'),'dd-mm-yy'));
         END IF;

      EXCEPTION
         WHEN OTHERS
         THEN
            RAISE NOTICE '%','OTHERS EXCEPTION in SP_UPDATE_C2S_MSISDN_USAGE 1, '|| SQLERRM;
            v_messageforlog :='OTHERS Error in SP_UPDATE_C2S_MSISDN_USAGE 1, Date:'|| id_trans_date;
            v_sqlerrmsgforlog := SQLERRM;
      RAISE EXCEPTION  using errcode = 'ERR05';
      END;
   END LOOP;
     raise notice 'SP_UPDATE_C2S_MSISDN_USAGE SUCCESS';
	v_messageforlog := 'SP_UPDATE_C2S_MSISDN_USAGE SUCCESS';
	v_sqlerrmsgforlog := 'SP_UPDATE_C2S_MSISDN_USAGE NO error ';  
EXCEPTION
 when sqlstate 'ERR05' then
         RAISE NOTICE 'procexception in SP_UPDATE_C2S_MSISDN_USAGE 3';
         RAISE EXCEPTION  using errcode = 'ERR01';
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION =' || SQLERRM;
         v_messageforlog :='OTHERS Exception in SP_UPDATE_C2S_MSISDN_USAGE 2, Date:'|| id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
END ;
$$;


ALTER FUNCTION pretupsdatabase.mis_sp_update_c2s_msisdn_usage(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_sp_update_c2s_sub_denom(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_update_c2s_sub_denom(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE 

   iv_service_class_code   C2S_SUB_DENOM_DETAILS.service_class_code%TYPE;
   iv_service_class_id     C2S_SUB_DENOM_DETAILS.service_class_id%TYPE;
   iv_interface_id         C2S_SUB_DENOM_DETAILS.interface_id%TYPE;
   iv_denomination_slab    C2S_SUB_DENOM_DETAILS.denomination_slab%TYPE;
   iv_service_type           C2S_SUB_DENOM_DETAILS.SERVICE_TYPE%TYPE;
   iv_sub_service           C2S_SUB_DENOM_DETAILS.SUB_SERVICE%TYPE;
   in_transfer_amount      C2S_SUB_DENOM_DETAILS.transfer_amount%TYPE;
   in_requested_amount     C2S_SUB_DENOM_DETAILS.requested_amount%TYPE;
   in_transfer_count       C2S_SUB_DENOM_DETAILS.transfer_count%TYPE;
   in_access_fee           C2S_SUB_DENOM_DETAILS.access_fee%TYPE;
   id_transfer_date        C2S_SUB_DENOM_DETAILS.transfer_date%TYPE;
   iv_network_code         C2S_SUB_DENOM_DETAILS.network_code%TYPE;


   --Cursor Declaration
   declare c2s_sub_denom_details_cursor CURSOR (aiv_date TIMESTAMP(0))
  FOR

   SELECT   ct.service_class_id,ct.service_class_code,
               ct.interface_id, sm.slab_id,ct.SERVICE_TYPE, ct.sub_service,
               ct.transfer_date, ct.receiver_network_code,
               SUM(ct.receiver_access_fee) receiver_access_fee,
               COALESCE (SUM (ct.transfer_value), 0) amount,
                  COALESCE(SUM(ct.transfer_value), 0) requested_amount,
               COUNT (ct.transfer_date) COUNT
          FROM C2S_TRANSFERS_MISTMP ct, SLAB_MASTER sm
         WHERE --ct.transfer_date = aiv_date AND 
            ct.transfer_status='200'
           AND    ct.transfer_value >= sm.from_range
         AND    ct.transfer_value <= sm.to_range
           AND sm.slab_date=(SELECT MAX(slab_date) FROM SLAB_MASTER WHERE
                                          slab_date<=CURRENT_TIMESTAMP AND
                                    ct.SERVICE_TYPE=sm.SERVICE_TYPE)
      GROUP BY ct.service_class_code,
               ct.service_class_id,
               ct.interface_id,
               sm.slab_id,
               ct.transfer_date,
               ct.receiver_network_code,
               ct.SERVICE_TYPE,ct.sub_service;


BEGIN
RAISE notice 'inside  mis_sp_update_c2s_sub_denom';
   FOR transaction_record IN c2s_sub_denom_details_cursor (aiv_date)
   LOOP
      iv_service_class_code := transaction_record.service_class_code;
      iv_service_class_id := transaction_record.service_class_id;
      iv_interface_id := transaction_record.interface_id;
      iv_denomination_slab := transaction_record.slab_id;
      iv_service_type := transaction_record.SERVICE_TYPE;
      iv_sub_service := transaction_record.SUB_SERVICE;
      in_transfer_amount := transaction_record.amount;
      in_requested_amount := transaction_record.requested_amount;
      in_transfer_count := transaction_record.COUNT;
      in_access_fee := transaction_record.receiver_access_fee;
      --id_transfer_date := transaction_record.transfer_date;
      id_transfer_date := aiv_date;
      iv_network_code := transaction_record.receiver_network_code;

      BEGIN
      RAISE notice 'UPDATE C2S_SUB_DENOM_DETAILS';
         UPDATE C2S_SUB_DENOM_DETAILS
            SET transfer_amount = transfer_amount + in_transfer_amount,
                transfer_count = transfer_count + in_transfer_count,
                access_fee = access_fee + in_access_fee,
                requested_amount=requested_amount+in_requested_amount
          WHERE service_class_code = iv_service_class_code
            AND service_class_id = iv_service_class_id
            AND denomination_slab = iv_denomination_slab
            AND interface_id = iv_interface_id
            AND transfer_date = id_transfer_date
            AND network_code = iv_network_code
            AND SERVICE_TYPE = iv_service_type
        AND SUB_SERVICE = iv_sub_service ;

         IF NOT FOUND 
         THEN
          RAISE notice 'INSERT C2S_SUB_DENOM_DETAILS';
            INSERT INTO C2S_SUB_DENOM_DETAILS
                           (service_class_code, service_class_id,
                         interface_id, denomination_slab,
                         transfer_amount, transfer_count,
                         access_fee, transfer_date, network_code,
                         SERVICE_TYPE,SUB_SERVICE,requested_amount)
                 values (iv_service_class_code, iv_service_class_id,
                         iv_interface_id, iv_denomination_slab,
                         in_transfer_amount, in_transfer_count,
                         in_access_fee, id_transfer_date, iv_network_code,
                         iv_service_type,iv_sub_service, in_requested_amount);
         END IF;
      EXCEPTION
         WHEN OTHERS
         THEN
            RAISE NOTICE '%',   'OTHERS EXCEPTION in sp_update_c2s_sub_denom 1:' || SQLERRM;
            v_messageforlog := 'OTHERS Error in sp_update_c2s_sub_denom 1, Date:'|| aiv_date;
            v_sqlerrmsgforlog := SQLERRM;
             RAISE EXCEPTION  using errcode = 'ERR05';
      END;
   END LOOP;
         raise notice 'mis_sp_update_c2s_sub_denom SUCCESS';
	   v_messageforlog := 'mis_sp_update_c2s_sub_denom SUCCESS';
	v_sqlerrmsgforlog := 'mis_sp_update_c2s_sub_denom NO error '; 
EXCEPTION
	when sqlstate 'ERR05' then
         RAISE NOTICE 'procexception in mis_sp_update_c2s_sub_denom 2';
         RAISE EXCEPTION  using errcode = 'ERR01';
   WHEN OTHERS
   THEN
      RAISE NOTICE '%', 'OTHERS EXCEPTION =' || SQLERRM;
      v_messageforlog := 'OTHERS Exception in mis_sp_update_c2s_sub_denom 2, Date:' || aiv_date;
      v_sqlerrmsgforlog := SQLERRM;
END;
   $$;


ALTER FUNCTION pretupsdatabase.mis_sp_update_c2s_sub_denom(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_sp_update_c2s_success_failure(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_update_c2s_success_failure(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE 
v_nullvalue               VARCHAR(5):='N.A.';


  id_trans_date                       C2S_DAILY_FAILURE_DETAILS.trans_date%TYPE;
  iv_service_type                C2S_DAILY_FAILURE_DETAILS.SERVICE_TYPE%TYPE;
  iv_sub_service_type            C2S_DAILY_FAILURE_DETAILS.sub_service_type%TYPE;
  iv_error_code                    C2S_DAILY_FAILURE_DETAILS.error_code%TYPE;
  in_count                        C2S_DAILY_FAILURE_DETAILS.COUNT%TYPE;
  in_amount                        C2S_DAILY_FAILURE_DETAILS.amount%TYPE;
  iv_sender_network_code        C2S_DAILY_FAILURE_DETAILS.sender_network_code%TYPE;
  iv_receiver_network_code       C2S_DAILY_FAILURE_DETAILS.receiver_network_code%TYPE;

  in_trans_count                C2S_SUMMARY_DAILY.total_trans_count%TYPE;
  in_trans_amount               C2S_SUMMARY_DAILY.total_trans_amount%TYPE;
  in_fail_count                 C2S_SUMMARY_DAILY.fail_count%TYPE;
  in_fail_amount                C2S_SUMMARY_DAILY.fail_amount%TYPE;
  iv_transfer_status            C2S_TRANSFERS_MISTMP.transfer_status%TYPE;
  in_ambg_count                 C2S_SUMMARY_DAILY.fail_count%TYPE;
  in_ambg_amount                C2S_SUMMARY_DAILY.fail_amount%TYPE;


   --Cursor Declaration
   declare SUCCESS_FAILURE_CURSOR CURSOR (aiv_date TIMESTAMP(0))
   FOR
        SELECT transfer_date,network_code,receiver_network_code,
     SERVICE_TYPE,sub_service,transfer_status,COALESCE(error_code,v_nullvalue) error_code,
     SUM(transfer_value) amount,
     COUNT(transfer_date) COUNT
     FROM C2S_TRANSFERS_MISTMP
     --WHERE transfer_date=aiv_date
     GROUP BY transfer_date,network_code,receiver_network_code,
     SERVICE_TYPE,sub_service,transfer_status,error_code;
BEGIN
   RAISE notice 'inside  mis_sp_update_c2s_success_failure';
   FOR TRANSACTION_RECORD IN SUCCESS_FAILURE_CURSOR (aiv_date)
   LOOP

  id_trans_date            :=TRANSACTION_RECORD.transfer_date;
  --id_trans_date            :=aiv_date;
  iv_service_type          :=TRANSACTION_RECORD.SERVICE_TYPE;
  iv_sub_service_type      :=TRANSACTION_RECORD.sub_service;
  iv_error_code            :=TRANSACTION_RECORD.error_code;
  iv_transfer_status       :=TRANSACTION_RECORD.transfer_status;
  iv_sender_network_code   :=TRANSACTION_RECORD.network_code;
  iv_receiver_network_code :=TRANSACTION_RECORD.receiver_network_code;
  in_count:=0;
  in_amount:=0;
  in_trans_count:=0;
  in_trans_amount:=0;
  in_fail_count:=0;
  in_fail_amount:=0;
  in_ambg_count:=0;
  in_ambg_amount:=0;

      IF iv_transfer_status = '200' THEN
           in_trans_count := TRANSACTION_RECORD.COUNT;
           in_trans_amount := TRANSACTION_RECORD.amount;
      ELSIF iv_transfer_status = '206' THEN
         in_fail_count := TRANSACTION_RECORD.COUNT;
         in_fail_amount := TRANSACTION_RECORD.amount;
      END IF;

      BEGIN
       RAISE notice 'UPDATE C2S_SUMMARY_DAILY SET';
             UPDATE C2S_SUMMARY_DAILY SET
           TOTAL_TRANS_COUNT = TOTAL_TRANS_COUNT + in_trans_count,
           TOTAL_TRANS_AMOUNT = TOTAL_TRANS_AMOUNT + in_trans_amount,
           FAIL_COUNT = FAIL_COUNT + in_fail_count,
           FAIL_AMOUNT = FAIL_AMOUNT + in_fail_amount
           WHERE TRANS_DATE=id_trans_date
           AND   SENDER_NETWORK_CODE=iv_sender_network_code
           AND     RECEIVER_NETWORK_CODE=iv_receiver_network_code
           AND   SERVICE_TYPE=iv_service_type
           AND   SUB_SERVICE_TYPE=iv_sub_service_type;


         IF NOT FOUND  THEN
         RAISE notice 'INSERT C2S_SUMMARY_DAILY SET';
            INSERT INTO C2S_SUMMARY_DAILY
            (trans_date, sender_network_code,
                 receiver_network_code,SERVICE_TYPE,
                 sub_service_type, total_trans_count,
                 total_trans_amount,fail_count,fail_amount)
            values (id_trans_date, iv_sender_network_code,
                 iv_receiver_network_code,iv_service_type,
                 iv_sub_service_type, in_trans_count,
                 in_trans_amount,in_fail_count,in_fail_amount);
         END IF;
      EXCEPTION
         WHEN OTHERS
         THEN
            RAISE NOTICE '%','OTHERS EXCEPTION in SP_UPDATE_C2S_SUCCESS_FAILURE 1:'|| SQLERRM;
            v_messageforlog :='OTHERS Error in SP_UPDATE_C2S_SUCCESS_FAILURE 1, Date:'|| id_trans_date;
            v_sqlerrmsgforlog := SQLERRM;
      RAISE EXCEPTION  using errcode = 'ERR05';
      END;

      IF iv_transfer_status='206' THEN
           in_count:=TRANSACTION_RECORD.COUNT;
         in_amount:=TRANSACTION_RECORD.amount;
      BEGIN
       UPDATE C2S_DAILY_FAILURE_DETAILS SET
                COUNT=COUNT+in_count,
             amount=amount+in_amount
             WHERE trans_date=id_trans_date
             AND SERVICE_TYPE=iv_service_type
             AND sub_service_type=iv_sub_service_type
             AND error_code=iv_error_code
             AND sender_network_code=iv_sender_network_code
             AND receiver_network_code=iv_receiver_network_code;


         IF NOT FOUND 
         THEN
            INSERT INTO C2S_DAILY_FAILURE_DETAILS
            (trans_date,SERVICE_TYPE,sub_service_type,
                 error_code,COUNT,amount,sender_network_code,
                 receiver_network_code)
            values (id_trans_date,iv_service_type,iv_sub_service_type,
                 iv_error_code,in_count,in_amount,iv_sender_network_code,
                 iv_receiver_network_code);
         END IF;
      EXCEPTION
         WHEN OTHERS
         THEN
            RAISE NOTICE '%','OTHERS EXCEPTION in SP_UPDATE_C2S_SUCCESS_FAILURE 2:'|| SQLERRM;
            v_messageforlog :='OTHERS Error in SP_UPDATE_C2S_SUCCESS_FAILURE 2, Date:'|| id_trans_date;
            v_sqlerrmsgforlog := SQLERRM;
            RAISE EXCEPTION  using errcode = 'ERR05';
      END;
      END IF;
   END LOOP;
   raise notice 'SP_UPDATE_C2S_SUCCESS_FAILURE SUCCESS';
	   v_messageforlog := 'SP_UPDATE_C2S_SUCCESS_FAILURE SUCCESS';
	v_sqlerrmsgforlog := 'SP_UPDATE_C2S_SUCCESS_FAILURE NO error '; 
EXCEPTION
  when sqlstate 'ERR05' then
         RAISE NOTICE 'procexception in SP_UPDATE_C2S_SUCCESS_FAILURE';
         RAISE EXCEPTION  using errcode = 'ERR01';
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION =' || SQLERRM;
         v_messageforlog :='OTHERS Exception in SP_UPDATE_C2S_SUCCESS_FAILURE 3, Date:'|| id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE EXCEPTION 'Exception in SP_UPDATE_C2S_SUCCESS_FAILURE 3';
END ;
 $$;


ALTER FUNCTION pretupsdatabase.mis_sp_update_c2s_success_failure(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_sp_update_daily_chnl_trans_det(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_update_daily_chnl_trans_det(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
   DECLARE 
    gd_createdon           DATE;
      iv_user_id                  DAILY_CHNL_TRANS_DETAILS.user_id%TYPE;
      id_trans_date               DAILY_CHNL_TRANS_DETAILS.trans_date%TYPE;
      iv_receiver_category_code   DAILY_CHNL_TRANS_DETAILS.receiver_category_code%TYPE;
      iv_product_code             DAILY_CHNL_TRANS_DETAILS.product_code%TYPE;
      iv_type                     DAILY_CHNL_TRANS_DETAILS.TYPE%TYPE;
      in_total_tax1               DAILY_CHNL_TRANS_DETAILS.total_tax1_in%TYPE;
      in_total_tax2               DAILY_CHNL_TRANS_DETAILS.total_tax2_in%TYPE;
      in_total_tax3               DAILY_CHNL_TRANS_DETAILS.total_tax3_in%TYPE;
      iv_transfer_category        DAILY_CHNL_TRANS_DETAILS.transfer_category%TYPE;
      iv_transfer_type            DAILY_CHNL_TRANS_DETAILS.transfer_type%TYPE;
      iv_transfer_sub_type        DAILY_CHNL_TRANS_DETAILS.transfer_sub_type%TYPE;
      in_trans_count              DAILY_CHNL_TRANS_DETAILS.trans_in_count%TYPE;
      in_trans_amount             DAILY_CHNL_TRANS_DETAILS.trans_in_amount%TYPE;

      ---Cursor Declaration
      declare  daily_chnl_trans_det1_cursor CURSOR (aiv_date TIMESTAMP(0))
      for
         SELECT   ct.from_user_id, DATE_TRUNC('day',ct.close_date::TIMESTAMP) transfer_date, cti.product_code,
                  ct.receiver_category_code, ct.TYPE, ct.transfer_category,
                  ct.transfer_type, ct.transfer_sub_type,
                  SUM (COALESCE(cti.tax1_value,0)) tax1_value,
                  SUM (COALESCE(cti.tax2_value,0)) tax2_value,
                  SUM (COALESCE(cti.tax3_value,0)) tax3_value,
                  SUM (COALESCE(cti.approved_quantity,0)) amount, COUNT (ct.transfer_id) COUNT
             FROM CHANNEL_TRANSFERS ct, CHANNEL_TRANSFERS_ITEMS cti
            WHERE ct.transfer_id = cti.transfer_id
              AND DATE_TRUNC ('day',ct.close_date::TIMESTAMP) = aiv_date
              AND ct.status = 'CLOSE'
              AND ct.from_user_id <> 'OPT'
         GROUP BY ct.from_user_id,
                  date_trunc('day',ct.close_date::TIMESTAMP),
                  cti.product_code,
                  ct.receiver_category_code,
                  ct.TYPE,
                  ct.transfer_category,
                  ct.transfer_type,
                  ct.transfer_sub_type;

      declare  daily_chnl_trans_det2_cursor CURSOR (aiv_date TIMESTAMP(0))
      FOR
         SELECT   ct.to_user_id, DATE_TRUNC('day',ct.close_date::TIMESTAMP) transfer_date,
                  cti.product_code, ct.sender_category_code, ct.TYPE,
                  ct.transfer_category, ct.transfer_type,
                  ct.transfer_sub_type, SUM (COALESCE(cti.tax1_value,0)) tax1_value,
                  SUM (COALESCE(cti.tax2_value,0)) tax2_value,
                  SUM (COALESCE(cti.tax3_value,0)) tax3_value,
                  SUM (COALESCE(cti.approved_quantity,0)) amount, COUNT (ct.transfer_id) COUNT
             FROM CHANNEL_TRANSFERS ct, CHANNEL_TRANSFERS_ITEMS cti
            WHERE ct.transfer_id = cti.transfer_id
              AND date_trunc('day',ct.close_date::TIMESTAMP) = aiv_date
              AND ct.status = 'CLOSE'
              AND ct.to_user_id <> 'OPT'
         GROUP BY ct.to_user_id,
                  date_trunc('day',ct.close_date::TIMESTAMP),
                  cti.product_code,
                  ct.sender_category_code,
                  ct.TYPE,
                  ct.transfer_category,
                  ct.transfer_type,
                  ct.transfer_sub_type;
   BEGIN
   RAISE notice 'inside  mis_sp_update_daily_chnl_trans_det';
	 gd_createdon := current_timestamp;  
      FOR transaction_record IN daily_chnl_trans_det1_cursor (aiv_date)
      LOOP
         in_total_tax1 := 0;
         in_total_tax2 := 0;
         in_total_tax3 := 0;
         in_trans_count := 0;
         in_trans_amount := 0;
         iv_user_id := transaction_record.from_user_id;
         id_trans_date := transaction_record.transfer_date;
         iv_receiver_category_code :=
                                    transaction_record.receiver_category_code;
         iv_product_code := transaction_record.product_code;
         iv_type := transaction_record.TYPE;
         iv_transfer_category := transaction_record.transfer_category;
         iv_transfer_type := transaction_record.transfer_type;
         iv_transfer_sub_type := transaction_record.transfer_sub_type;
         in_total_tax1 := transaction_record.tax1_value;
         in_total_tax2 := transaction_record.tax2_value;
         in_total_tax3 := transaction_record.tax3_value;
         in_trans_count := transaction_record.COUNT;
         in_trans_amount := transaction_record.amount;

         BEGIN
          RAISE notice 'updating DAILY_CHNL_TRANS_DETAILS';
            UPDATE DAILY_CHNL_TRANS_DETAILS
               SET total_tax1_out = total_tax1_out + in_total_tax1,
                   total_tax2_out = total_tax2_out + in_total_tax2,
                   total_tax3_out = total_tax3_out + in_total_tax3,
                   trans_out_count = trans_out_count + in_trans_count,
                   trans_out_amount = trans_out_amount + in_trans_amount
             WHERE user_id = iv_user_id
               AND trans_date = id_trans_date
               AND receiver_category_code = iv_receiver_category_code
               AND product_code = iv_product_code
               AND TYPE = iv_type
               AND transfer_category = iv_transfer_category
               AND transfer_type = iv_transfer_type
               AND transfer_sub_type = iv_transfer_sub_type;

            IF NOT FOUND
            THEN
             RAISE notice 'inserting DAILY_CHNL_TRANS_DETAILS';
               INSERT INTO DAILY_CHNL_TRANS_DETAILS
                                (user_id, trans_date,
                            receiver_category_code, product_code,
                            type, total_tax1_in, total_tax2_in,
                            total_tax3_in,total_tax1_out, total_tax2_out,
                            total_tax3_out, transfer_category,
                            transfer_type, transfer_sub_type, created_on,
                            trans_in_count, trans_in_amount, trans_out_count, trans_out_amount)
                    VALUES (iv_user_id, id_trans_date,
                            iv_receiver_category_code, iv_product_code,
                            iv_type, 0, 0, 0, in_total_tax1, in_total_tax2,
                            in_total_tax3, iv_transfer_category,
                            iv_transfer_type, iv_transfer_sub_type, gd_createdon,
                            0, 0, in_trans_count, in_trans_amount);
            END IF;
         EXCEPTION
            WHEN OTHERS
            THEN
               RAISE NOTICE 'EXCEPTION in SP_UPDATE_DAILY_CHNL_TRANS_DET 1, User:%,Error:%',iv_user_id,SQLERRM;
               v_messageforlog := 'Exception in SP_UPDATE_DAILY_CHNL_TRANS_DET 1, User:'|| iv_user_id || id_trans_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION  using errcode = 'ERR05';
         END;
      END LOOP;

      FOR transaction_record IN daily_chnl_trans_det2_cursor (aiv_date)
      LOOP
         in_total_tax1 := 0;
         in_total_tax2 := 0;
         in_total_tax3 := 0;
         in_trans_count := 0;
         in_trans_amount := 0;
         iv_user_id := transaction_record.to_user_id;
         id_trans_date := transaction_record.transfer_date;
         iv_receiver_category_code :=
                                    transaction_record.sender_category_code;
         iv_product_code := transaction_record.product_code;
         iv_type := transaction_record.TYPE;
         iv_transfer_category := transaction_record.transfer_category;
         iv_transfer_type := transaction_record.transfer_type;
         iv_transfer_sub_type := transaction_record.transfer_sub_type;
         in_total_tax1 := transaction_record.tax1_value;
         in_total_tax2 := transaction_record.tax2_value;
         in_total_tax3 := transaction_record.tax3_value;
         in_trans_count := transaction_record.COUNT;
         in_trans_amount := transaction_record.amount;

         BEGIN
            UPDATE DAILY_CHNL_TRANS_DETAILS
               SET total_tax1_in = total_tax1_in + in_total_tax1,
                   total_tax2_in = total_tax2_in + in_total_tax2,
                   total_tax3_in = total_tax3_in + in_total_tax3,
                   trans_in_count = trans_in_count + in_trans_count,
                   trans_in_amount = trans_in_amount + in_trans_amount
             WHERE user_id = iv_user_id
               AND trans_date = id_trans_date
               AND receiver_category_code = iv_receiver_category_code
               AND product_code = iv_product_code
               AND TYPE = iv_type
               AND transfer_category = iv_transfer_category
               AND transfer_type = iv_transfer_type
               AND transfer_sub_type = iv_transfer_sub_type;

            IF NOT FOUND
            THEN
               INSERT INTO DAILY_CHNL_TRANS_DETAILS
                                (user_id, trans_date,
                            receiver_category_code, product_code,
                            TYPE, total_tax1_in, total_tax2_in,
                            total_tax3_in,total_tax1_out, total_tax2_out,
                            total_tax3_out, transfer_category,
                            transfer_type, transfer_sub_type, created_on,
                            trans_in_count, trans_in_amount, trans_out_count, trans_out_amount)
                    VALUES (iv_user_id, id_trans_date,
                            iv_receiver_category_code, iv_product_code,
                            iv_type, in_total_tax1, in_total_tax2,
                            in_total_tax3, 0, 0, 0, iv_transfer_category,
                            iv_transfer_type, iv_transfer_sub_type, gd_createdon,
                            in_trans_count, in_trans_amount, 0, 0);
            END IF;
         EXCEPTION
            WHEN OTHERS
            THEN
               RAISE NOTICE '%',   'EXCEPTION in SP_UPDATE_DAILY_CHNL_TRANS_DET 2, User:' || iv_user_id || SQLERRM;
               v_messageforlog := 'Error in SP_UPDATE_DAILY_CHNL_TRANS_DET 2,User:' || iv_user_id|| ', Date :' || id_trans_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION  using errcode = 'ERR05';
         --Raise procHException;
         END;
      END LOOP;
      raise notice 'mis_sp_update_daily_chnl_trans_det SUCCESS';
	   v_messageforlog := 'mis_sp_update_daily_chnl_trans_det SUCCESS';
	v_sqlerrmsgforlog := 'mis_sp_update_daily_chnl_trans_det NO error ';  
   EXCEPTION
   when sqlstate 'ERR05' then
         RAISE NOTICE 'procexception in SP_UPDATE_DAILY_CHNL_TRANS_DET 3';
         RAISE EXCEPTION  using errcode = 'ERR01';
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'EXCEPTION:' || SQLERRM;
         v_messageforlog := 'Exception in SP_UPDATE_DAILY_CHNL_TRANS_DET 3, Date:' || id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
   END ;
 $$;


ALTER FUNCTION pretupsdatabase.mis_sp_update_daily_chnl_trans_det(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_sp_update_daily_trn_summary(timestamp without time zone, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_update_daily_trn_summary(aiv_date timestamp without time zone, v_choice character varying, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE
v_nullvalue               VARCHAR(5):='N.A.';
        
   id_trans_date                    C2S_DAILY_TRANSACTIONS.trans_date%TYPE;
   iv_network_code                  C2S_DAILY_TRANSACTIONS.network_code%TYPE;
   iv_receiver_network_code         C2S_DAILY_TRANSACTIONS.receiver_network_code%TYPE;
   iv_service_type                  C2S_DAILY_TRANSACTIONS.SERVICE_TYPE%TYPE;
   iv_sub_service_type              C2S_DAILY_TRANSACTIONS.sub_service_type%TYPE;
   iv_sender_category               C2S_DAILY_TRANSACTIONS.sender_category%TYPE;
   iv_receiver_service_class_id     C2S_DAILY_TRANSACTIONS.receiver_service_class_id%TYPE;
   iv_receiver_service_class_code   C2S_DAILY_TRANSACTIONS.receiver_service_class_code%TYPE;
   iv_transfer_status               C2S_TRANSFERS_MISTMP.transfer_status%TYPE;
   in_receiver_access_fee           C2S_DAILY_TRANSACTIONS.receiver_access_fee%TYPE;
   in_receiver_tax1_value           C2S_DAILY_TRANSACTIONS.receiver_tax1_value%TYPE;
   in_receiver_tax2_value           C2S_DAILY_TRANSACTIONS.receiver_tax2_value%TYPE;
   in_receiver_tax3_value           C2S_DAILY_TRANSACTIONS.receiver_tax3_value%TYPE;
   in_success_count                 C2S_DAILY_TRANSACTIONS.success_count%TYPE;
   in_success_amount                C2S_DAILY_TRANSACTIONS.success_amount%TYPE;
   in_failure_count                 C2S_DAILY_TRANSACTIONS.failure_count%TYPE;
   in_failure_amount                C2S_DAILY_TRANSACTIONS.failure_amount%TYPE;
   in_bonus_amount                    C2S_DAILY_TRANSACTIONS.bonus_amount%TYPE;
   in_validity                           C2S_DAILY_TRANSACTIONS.validity%TYPE;
   in_bonus_validity                C2S_DAILY_TRANSACTIONS.bonus_validity%TYPE;
   in_penalty                         C2S_DAILY_TRANSACTIONS.penalty%TYPE;
   in_owner_penalty                 C2S_DAILY_TRANSACTIONS.owner_penalty%TYPE;
   in_penalty_count                 C2S_DAILY_TRANSACTIONS.penalty_count%TYPE;

   ---Cursor Declaration
   declare daily_transaction_cursor CURSOR (aiv_date TIMESTAMP(0))
   FOR
      SELECT   ct.transfer_date, ct.network_code, ct.receiver_network_code,
               ct.SERVICE_TYPE,  ct.sender_category,
               COALESCE(ct.sub_service,v_nullvalue) sub_service,
               COALESCE(ct.service_class_id,v_nullvalue) service_class_id,
               COALESCE(ct.service_class_code,v_nullvalue) service_class_code,
               ct.transfer_status,
               SUM (ct.receiver_access_fee) receiver_access_fee,
               SUM (ct.receiver_tax1_value) tax1,
               SUM (ct.receiver_tax2_value) tax2,
               SUM (ct.transfer_value) transfer_value,
               COUNT (ct.transfer_date) COUNT,
               SUM (ct.receiver_bonus_value    ) bonus_amount,
               SUM (ct.receiver_validity) validity,
               SUM (ct.receiver_bonus_validity) bonus_validity,
               SUM(ct.penalty) penalty,
               SUM(ct.owner_penalty) owner_penalty,
               COALESCE(SUM(case when COALESCE(PENALTY,0)>0 then 1 else 0 end),0) PENALTY_COUNT
          FROM C2S_TRANSFERS_MISTMP ct 
         --WHERE ct.Transfer_date=aiv_date
      GROUP BY ct.transfer_date,
               ct.network_code,
               ct.receiver_network_code,
               ct.SERVICE_TYPE,
               ct.sub_service,
               ct.sender_category,
               ct.service_class_id,
               ct.service_class_code,
               ct.transfer_status;

BEGIN
RAISE notice 'inside  mis_sp_update_daily_trn_summary';
   FOR transaction_record IN daily_transaction_cursor  (aiv_date)
   LOOP
      in_success_count := 0;
      in_failure_count := 0;
      in_success_amount := 0;
      in_failure_amount := 0;
      in_receiver_access_fee := 0;
      in_receiver_tax1_value := 0;
      in_receiver_tax2_value := 0;
      in_receiver_tax3_value := 0;
      in_bonus_amount        :=0;
      in_validity            :=0;
         in_bonus_validity      :=0;
      in_penalty:=0;
      in_owner_penalty:=0;
      in_penalty_count:=0;


      in_receiver_access_fee := transaction_record.receiver_access_fee;
      in_receiver_tax1_value := transaction_record.tax1;
      in_receiver_tax2_value := transaction_record.tax2;
      id_trans_date := transaction_record.transfer_date;
      --id_trans_date := aiv_date;
      iv_transfer_status := transaction_record.transfer_status;
      iv_network_code := transaction_record.network_code;
      iv_receiver_network_code := transaction_record.receiver_network_code;
      iv_service_type := transaction_record.SERVICE_TYPE;
      iv_sub_service_type := transaction_record.sub_service;
      iv_sender_category := transaction_record.sender_category;
      iv_receiver_service_class_id := transaction_record.service_class_id;
      iv_receiver_service_class_code := transaction_record.service_class_code;

      IF iv_transfer_status = '200'
      THEN
         in_success_count := transaction_record.COUNT;
         in_success_amount := transaction_record.transfer_value;
         in_bonus_amount        :=transaction_record.bonus_amount;
         in_validity            :=transaction_record.validity;
              in_bonus_validity      :=transaction_record.bonus_validity;
        in_penalty:=transaction_record.penalty;
        in_owner_penalty:=transaction_record.owner_penalty;
        in_penalty_count:=transaction_record.penalty_count;
      END IF;

      IF iv_transfer_status = '206'
      THEN
         in_failure_count := transaction_record.COUNT;
         in_failure_amount := transaction_record.transfer_value;
      END IF;

      IF v_choice = 'Y'
      THEN
         iv_receiver_service_class_id := 'ALL';
      END IF;
      BEGIN
      RAISE notice 'UPDATE C2S_DAILY_TRANSACTIONS';
         UPDATE C2S_DAILY_TRANSACTIONS
            SET success_count = success_count + in_success_count,
                failure_count = failure_count + in_failure_count,
                success_amount = success_amount + in_success_amount,
                failure_amount = failure_amount + in_failure_amount,
                receiver_access_fee = receiver_access_fee + in_receiver_access_fee,
                receiver_tax1_value = receiver_tax1_value + in_receiver_tax1_value,
                receiver_tax2_value = receiver_tax2_value + in_receiver_tax2_value,
                receiver_tax3_value = receiver_tax3_value + in_receiver_tax3_value,
                bonus_amount = bonus_amount+         in_bonus_amount ,
                validity = validity + in_validity ,
                     bonus_validity = bonus_validity + in_bonus_validity,
                penalty=penalty+in_penalty,
                owner_penalty= owner_penalty+in_owner_penalty,
                penalty_count=penalty_count+in_penalty_count
          WHERE trans_date = id_trans_date
            AND network_code = iv_network_code
            AND receiver_network_code = iv_receiver_network_code
            AND SERVICE_TYPE = iv_service_type
            AND sub_service_type = iv_sub_service_type
            AND sender_category = iv_sender_category
            AND receiver_service_class_id = iv_receiver_service_class_id
            AND receiver_service_class_code = iv_receiver_service_class_code;

         IF NOT FOUND 
         THEN
         RAISE notice 'INSERT C2S_DAILY_TRANSACTIONS';
            INSERT INTO C2S_DAILY_TRANSACTIONS
            (trans_date, network_code,
                         receiver_network_code, SERVICE_TYPE,
                         sub_service_type, sender_category,
                         receiver_service_class_id,
                         receiver_service_class_code,
                         receiver_access_fee, receiver_tax1_value,
                         receiver_tax2_value, receiver_tax3_value,
                         success_count, success_amount,
                         failure_count, failure_amount, created_on,
                         bonus_amount,validity,bonus_validity,penalty,owner_penalty,penalty_count)
            values (id_trans_date, iv_network_code,
                         iv_receiver_network_code, iv_service_type,
                         iv_sub_service_type, iv_sender_category,
                         iv_receiver_service_class_id,
                         iv_receiver_service_class_code,
                         in_receiver_access_fee, in_receiver_tax1_value,
                         in_receiver_tax2_value, in_receiver_tax3_value,
                         in_success_count, in_success_amount,
                         in_failure_count, in_failure_amount, CURRENT_TIMESTAMP,
                          in_bonus_amount,in_validity,in_bonus_validity,in_penalty,in_owner_penalty,in_penalty_count);
         END IF;
      EXCEPTION
         WHEN OTHERS
         THEN
            RAISE NOTICE '%','OTHERS EXCEPTION in sp_update_daily_trn_summary 1, '|| SQLERRM;
            v_messageforlog :='OTHERS Error in sp_update_daily_trn_summary 1, Date:'|| id_trans_date;
            v_sqlerrmsgforlog := SQLERRM;
      RAISE EXCEPTION  using errcode = 'ERR05';
      END;
   END LOOP;
    raise notice 'sp_update_daily_trn_summary SUCCESS';
	   v_messageforlog := 'sp_update_daily_trn_summary SUCCESS';
	v_sqlerrmsgforlog := 'sp_update_daily_trn_summary NO error ';  
EXCEPTION
 when sqlstate 'ERR05' then
         RAISE NOTICE 'procexception in sp_update_daily_trn_summary 3';
         RAISE EXCEPTION  using errcode = 'ERR01';
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION =' || SQLERRM;
         v_messageforlog :='OTHERS Exception in sp_update_daily_trn_summary 2, Date:'|| id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
END;
$$;


ALTER FUNCTION pretupsdatabase.mis_sp_update_daily_trn_summary(aiv_date timestamp without time zone, v_choice character varying, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_sp_update_mon_ambsettdata1(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_update_mon_ambsettdata1(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
   DECLARE 

    id_trans_date                       DAILY_CHNL_TRANS_DETAILS.trans_date%TYPE;
      ln_roam_c2s_transfer_out_amt           MONTHLY_CHNL_TRANS_MAIN.roam_c2s_transfer_out_amount%TYPE;
      ln_c2s_transfer_out_count          MONTHLY_CHNL_TRANS_MAIN.c2s_transfer_out_count%TYPE;
      ln_c2s_transfer_out_amount        MONTHLY_CHNL_TRANS_MAIN.c2s_transfer_out_amount%TYPE;
      ln_differential                      MONTHLY_CHNL_TRANS_MAIN.differential%TYPE;
      ln_adjustment_in                     MONTHLY_CHNL_TRANS_MAIN.adjustment_in%TYPE;
      ln_adjustment_out                  MONTHLY_CHNL_TRANS_MAIN.adjustment_out%TYPE;
      ln_C2S_TRANSFER_IN_AMOUNT           MONTHLY_CHNL_TRANS_MAIN.C2S_TRANSFER_IN_AMOUNT%TYPE;
      ln_C2S_TRANSFER_IN_COUNT            MONTHLY_CHNL_TRANS_MAIN.C2S_TRANSFER_IN_COUNT%TYPE;
      ln_REV_DIFFERENTIAL               MONTHLY_CHNL_TRANS_MAIN.REV_DIFFERENTIAL%TYPE;
     
  --Cursor Declaration
   declare monthly_chnl_main CURSOR (aiv_date TIMESTAMP(0))
   FOR
      SELECT   Mast.transfer_date,Mast.sender_id, Mast.network_code,Mast.service_class_id,
                  Mast.receiver_network_code, Mast.product_code,
                  Mast.unit_value,Mast.grph_domain_code, Mast.category_code,
                  Mast.domain_code, Mast.SERVICE_TYPE,Mast.sub_service,
                  COUNT (Mast.transfer_id) c2s_count,
                  SUM(Mast.transfer_value) c2s_amount,
                  COALESCE(SUM(Mast.receiver_transfer_value),0) c2s_rectrans,
                  COALESCE(SUM(Mast.receiver_access_fee),0) c2s_recfee,
                  COALESCE(SUM(Mast.receiver_tax1_value),0) c2s_rectax1,
                  COALESCE(SUM(Mast.receiver_tax2_value),0) c2s_rectax2,
                  COALESCE(SUM(Mast.sender_transfer_value),0) c2s_sender_value,
                  COALESCE(SUM(Mast.receiver_bonus_value),0) c2s_rec_bonus,
                  COALESCE(SUM(adj.tax1_value),0) diff_tax1,
                  COALESCE(SUM(adj.tax2_value),0) diff_tax2,
                  COALESCE(SUM(adj.tax3_value),0) diff_tax3,
                  COALESCE(SUM(adj.transfer_value),0) diff_amount,
                  SUM(CASE  WHEN COALESCE(adj.transfer_value,0) <>0 THEN 1 ELSE 0 END) diff_count,
                  COALESCE(SUM(Mast.receiver_validity),0) validity,
                  COALESCE(SUM(Mast.receiver_bonus_validity),0) bonus_validity,
                  COALESCE(SUM(Mast.PENALTY),0)PENALTY,
                  COALESCE(SUM(Mast.OWNER_PENALTY),0)OWNER_PENALTY
          FROM (SELECT
                  c2strans.transfer_date,c2strans.sender_id, c2strans.network_code,c2strans.service_class_id,
                  c2strans.receiver_network_code, p.product_code,
                  p.unit_value,ug.grph_domain_code, cat.category_code,
                  cat.domain_code, c2strans.SERVICE_TYPE,c2strans.sub_service,
                  c2strans.transfer_id,c2strans.transfer_value,c2strans.receiver_transfer_value,
                  c2strans.receiver_access_fee,c2strans.receiver_tax1_value,c2strans.receiver_tax2_value,
                  c2strans.sender_transfer_value,c2strans.receiver_bonus_value,c2strans.receiver_validity,
                  c2strans.receiver_bonus_validity,
                  c2strans.PENALTY,
                  c2strans.owner_penalty
        FROM   C2S_TRANSFERS c2strans,PRODUCTS P,
        CATEGORIES cat,USER_GEOGRAPHIES ug
        WHERE (c2strans.transfer_date != aiv_date and c2strans.reconciliation_date = aiv_date) AND
          c2strans.transfer_status = '200'
        AND  c2strans.transfer_type = 'TXN'
        AND   p.product_code=c2strans.product_code
        AND   cat.category_code=c2strans.sender_category
        AND ug.user_id=c2strans.sender_id
        ) Mast left join ADJUSTMENTS adj
        on ( Mast.transfer_id =adj.reference_id
        AND    Mast.sender_id   = adj.user_id)
        GROUP BY Mast.transfer_date,Mast.sender_id, Mast.network_code, Mast.receiver_network_code,Mast.product_code,Mast.grph_domain_code,
             Mast.category_code,Mast.domain_code,Mast.SERVICE_TYPE,Mast.sub_service,Mast.service_class_id,Mast.unit_value;

BEGIN

   id_trans_date:=aiv_date;
 
   BEGIN
      FOR t_r IN monthly_chnl_main (aiv_date)
      LOOP
        id_trans_date:=t_r.transfer_date;
      ln_roam_c2s_transfer_out_amt:=0;
      ln_c2s_transfer_out_count :=0;
        ln_c2s_transfer_out_amount :=0;
        ln_adjustment_out   :=0;
        ln_differential  :=0;
        ln_C2S_TRANSFER_IN_COUNT  :=0;
        ln_C2S_TRANSFER_IN_AMOUNT  :=0;
        ln_adjustment_in  :=0;
        ln_REV_DIFFERENTIAL   :=0;

              
       IF (t_r.network_code <> t_r.receiver_network_code)
      THEN
            ln_roam_c2s_transfer_out_amt:=t_r.c2s_amount;
      END IF;
      IF (t_r.SERVICE_TYPE <> 'RCREV')
      THEN
        ln_c2s_transfer_out_count :=t_r.c2s_count;
        ln_c2s_transfer_out_amount :=t_r.c2s_amount;
        ln_adjustment_out   :=t_r.diff_count;
        ln_differential  :=t_r.diff_amount;
      ELSE
        ln_C2S_TRANSFER_IN_COUNT  :=t_r.c2s_count;
        ln_C2S_TRANSFER_IN_AMOUNT  :=t_r.c2s_amount;
        ln_adjustment_in  :=t_r.diff_count;
        ln_REV_DIFFERENTIAL   :=t_r.diff_amount;
      END IF;

         BEGIN
            UPDATE MONTHLY_CHNL_TRANS_MAIN
               SET roam_c2s_transfer_out_amount = roam_c2s_transfer_out_amount + ln_roam_c2s_transfer_out_amt,
                   c2s_transfer_out_count = c2s_transfer_out_count + ln_c2s_transfer_out_count,
                   c2s_transfer_out_amount = c2s_transfer_out_amount + ln_c2s_transfer_out_amount,
                   differential = differential + ln_differential,
                   adjustment_in = adjustment_in + ln_adjustment_in,
                   adjustment_out = adjustment_out + ln_adjustment_out,
                   C2S_TRANSFER_IN_AMOUNT  = C2S_TRANSFER_IN_AMOUNT + ln_C2S_TRANSFER_IN_AMOUNT,
                   C2S_TRANSFER_IN_COUNT = C2S_TRANSFER_IN_COUNT + ln_C2S_TRANSFER_IN_COUNT,
                   REV_DIFFERENTIAL  = REV_DIFFERENTIAL +  ln_REV_DIFFERENTIAL

             WHERE user_id = t_r.sender_id
               AND TO_CHAR(trans_date,'mm-yy') = TO_CHAR(id_trans_date,'mm-yy')
               AND product_code = t_r.product_code
               AND category_code = t_r.category_code
               AND network_code = t_r.network_code
               AND network_code_for = t_r.receiver_network_code
               AND sender_domain_code = t_r.domain_code
               AND grph_domain_code = t_r.grph_domain_code;

            IF NOT FOUND
            THEN
               INSERT INTO MONTHLY_CHNL_TRANS_MAIN
                                (user_id,trans_date,
                            product_code, category_code,
                            network_code, network_code_for,
                            sender_domain_code,
                            roam_c2s_transfer_out_amount,
                            c2s_transfer_out_count,
                            c2s_transfer_out_amount,
                            o2c_transfer_in_count,
                            o2c_transfer_in_amount,
                            o2c_return_out_count,
                            o2c_return_out_amount,
                            o2c_withdraw_out_count,
                            o2c_withdraw_out_amount,
                            c2c_transfer_in_count,
                            c2c_transfer_in_amount,
                            c2c_transfer_out_count,
                            c2c_transfer_out_amount,
                            c2c_return_in_count,
                            c2c_return_in_amount,
                            c2c_return_out_count,
                            c2c_return_out_amount,
                            c2c_withdraw_in_count,
                            c2c_withdraw_in_amount,
                            c2c_withdraw_out_count,
                            c2c_withdraw_out_amount, differential,
                            adjustment_in, adjustment_out, created_on,
                            grph_domain_code,
                            c2c_reverse_in_count,
                            c2c_reverse_in_amount,
                            c2c_reverse_out_count,
                            c2c_reverse_out_amount,
                            o2c_reverse_in_count,
                            o2c_reverse_in_amount,
                            o2c_reverse_out_count,
                            o2c_reverse_out_amount,
                            C2S_TRANSFER_IN_AMOUNT ,
                            C2S_TRANSFER_IN_COUNT ,
                            REV_DIFFERENTIAL
                    )
                    values (t_r.sender_id,
                            TO_DATE('01-'||TO_CHAR (id_trans_date,'mm-yy'),'dd-mm-yy'),
                            t_r.product_code, t_r.category_code,
                            t_r.network_code, t_r.receiver_network_code,
                            t_r.domain_code,
                            ln_roam_c2s_transfer_out_amt,
                            ln_c2s_transfer_out_count,
                            ln_c2s_transfer_out_amount,
                            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, ln_differential,
                            ln_adjustment_in, ln_adjustment_out, CURRENT_TIMESTAMP,
                            t_r.grph_domain_code,0,0,0,0,0,0,0,0,
                            ln_C2S_TRANSFER_IN_AMOUNT ,
                            ln_C2S_TRANSFER_IN_COUNT ,
                            ln_REV_DIFFERENTIAL
                );
            END IF;
          EXCEPTION
            WHEN OTHERS
            THEN
               RAISE NOTICE '%',   'OTHERS EXCEPTION in Sp_Update_Mon_AmbSettData1 1, User:'  || t_r.sender_id || SQLERRM;
               v_messageforlog := 'OTHERS EXCEPTION in Sp_Update_Mon_AmbSettData1 1, User:' || t_r.sender_id || ', Date:' || id_trans_date;
               v_sqlerrmsgforlog := SQLERRM;
              RAISE EXCEPTION  using errcode = 'ERR05';

          END;
          
          -- 3 start here 
          BEGIN
            UPDATE MONTHLY_C2S_TRANS_DETAILS
               SET total_tax1 = total_tax1 + t_r.c2s_rectax1,
                   total_tax2 = total_tax2 + t_r.c2s_rectax2,
                   --total_tax3 = total_tax3 + t_r.total_tax3,
                   sender_transfer_amount = sender_transfer_amount + t_r.c2s_sender_value,
                   receiver_credit_amount = receiver_credit_amount + t_r.c2s_rectrans,
                   receiver_access_fee = receiver_access_fee + t_r.c2s_recfee,
                   differential_adjustment_tax1 = differential_adjustment_tax1 + t_r.diff_tax1,
                   differential_adjustment_tax2 = differential_adjustment_tax2 + t_r.diff_tax2,
                   differential_adjustment_tax3 = differential_adjustment_tax3 + t_r.diff_tax3,
                   receiver_bonus = receiver_bonus + t_r.c2s_rec_bonus,
                   transaction_count=transaction_count+t_r.c2s_count,
                   transaction_amount=transaction_amount+t_r.c2s_amount,
                   differential_amount=differential_amount+t_r.diff_amount,
                    --SENDER_REVERSE_AMOUNT = SENDER_REVERSE_AMOUNT + t_r.SENDER_REVERSE_AMOUNT,
                    --RECEIVER_DEBIT_AMOUNT = RECEIVER_DEBIT_AMOUNT + t_r.RECEIVER_DEBIT_AMOUNT,
                    --REVERSE_AMOUNT = REVERSE_AMOUNT + t_r.REVERSE_AMOUNT,
                    --REVERSE_COUNT = REVERSE_COUNT + t_r.REVERSE_COUNT,
                    --REVERSE_DIFF_AMOUNT = REVERSE_DIFF_AMOUNT + t_r.REVERSE_DIFF_AMOUNT,
                    --REVERSE_DIFF_COUNT = REVERSE_DIFF_COUNT + t_r.REVERSE_DIFF_COUNT,
                    PENALTY=PENALTY+t_r.PENALTY,
                    OWNER_PENALTY=OWNER_PENALTY+t_r.OWNER_PENALTY
                    --roam_amount=roam_amount+t_r.roam_amount
             WHERE user_id = t_r.sender_id
               AND TO_CHAR(trans_date,'mm-yy') = TO_CHAR(id_trans_date,'mm-yy')
               AND receiver_network_code = t_r.receiver_network_code
               AND sender_category_code = t_r.category_code
               AND receiver_service_class_id = t_r.service_class_id
               AND SERVICE_TYPE = t_r.SERVICE_TYPE
                AND SUB_SERVICE = t_r.SUB_SERVICE;

            IF NOT FOUND
            THEN
               INSERT INTO MONTHLY_C2S_TRANS_DETAILS
                              (user_id,
                            trans_date,
                            sender_category_code,
                            receiver_service_class_id,
                            receiver_network_code, SERVICE_TYPE,sub_service,
                            total_tax1, total_tax2, total_tax3,
                            sender_transfer_amount,
                            receiver_credit_amount,
                            receiver_access_fee,
                            differential_adjustment_tax1,
                            differential_adjustment_tax2,
                            differential_adjustment_tax3,
                            receiver_bonus, created_on,
                            receiver_validity,
                            receiver_bonus_validity,
                            transaction_amount,
                            transaction_count,
                            differential_amount,
                            SENDER_REVERSE_AMOUNT ,
                            RECEIVER_DEBIT_AMOUNT ,
                            REVERSE_AMOUNT ,
                            REVERSE_COUNT ,
                            REVERSE_DIFF_AMOUNT,
                            REVERSE_DIFF_COUNT,
                            PENALTY,
                            OWNER_PENALTY,
                            roam_amount
                            )
                    values (t_r.sender_id,
                            TO_DATE('01-'||TO_CHAR (id_trans_date, 'mm-yy'),'dd-mm-yy'),
                            t_r.category_code,
                            t_r.service_class_id,
                            t_r.receiver_network_code, t_r.SERVICE_TYPE,t_r.sub_service,
                            t_r.c2s_rectax1, t_r.c2s_rectax2, 0,
                            t_r.c2s_sender_value,
                            t_r.c2s_rectrans,
                            t_r.c2s_recfee,
                            t_r.diff_tax1,
                            t_r.diff_tax2,
                            t_r.diff_tax3,
                            t_r.c2s_rec_bonus, CURRENT_TIMESTAMP,
                            0,0,
                            t_r.c2s_amount,t_r.c2s_count,
                            t_r.diff_amount,
                            0 ,0 ,0 ,0 ,0,0,
                            t_r.PENALTY,
                            t_r.OWNER_PENALTY,
                            0 
                            );
            END IF;

            EXCEPTION
            WHEN OTHERS
            THEN
               RAISE NOTICE '%',   'OTHERS EXCEPTION in Sp_Update_Mon_AmbSettData1 5, User:'|| t_r.sender_id || SQLERRM;
               v_messageforlog := 'OTHERS Error in Sp_Update_Mon_AmbSettData1 5, User:'|| t_r.sender_id;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION  using errcode = 'ERR05';
          END;
  
      END LOOP;
   EXCEPTION
       when sqlstate 'ERR05' then
         raise notice 'procexception in Sp_Update_Mon_AmbSettData1:%',SQLERRM;
        RAISE EXCEPTION  using errcode = 'ERR01';
         WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION in Sp_Update_Mon_AmbSettData1:' || SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
   END;
 EXCEPTION
        when sqlstate 'ERR05' then
         raise notice 'procexception in Sp_Update_Mon_AmbSettData1:%',SQLERRM;
        RAISE EXCEPTION  using errcode = 'ERR01';
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION =' || SQLERRM;
         v_messageforlog := 'OTHERS Exception in Sp_Update_Mon_AmbSettData1 8, Date:' || id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
END;
$$;


ALTER FUNCTION pretupsdatabase.mis_sp_update_mon_ambsettdata1(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_sp_update_monthly_data1(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_update_monthly_data1(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
   DECLARE 
   
   id_trans_date               DAILY_CHNL_TRANS_DETAILS.trans_date%TYPE;
 
   --Cursor Declaration
   declare monthly_chnl_main  CURSOR (aiv_date TIMESTAMP(0))
   IS
      SELECT   user_id, trans_date,
               product_code, category_code, network_code, network_code_for,
               sender_domain_code, grph_domain_code,
               roam_c2s_transfer_out_amount,
               c2s_transfer_out_count,c2s_transfer_out_amount,
               o2c_transfer_in_count,o2c_transfer_in_amount,
               o2c_return_out_count,o2c_return_out_amount,
               o2c_withdraw_out_count,o2c_withdraw_out_amount,
               c2c_transfer_in_count,c2c_transfer_in_amount,
               c2c_transfer_out_count,c2c_transfer_out_amount,
               c2c_return_in_count,c2c_return_in_amount,
               c2c_return_out_count,c2c_return_out_amount,
               c2c_withdraw_in_count,c2c_withdraw_in_amount,
               c2c_withdraw_out_count,c2c_withdraw_out_amount,
               differential,adjustment_in,adjustment_out,
               c2c_reverse_in_count,c2c_reverse_in_amount,
               c2c_reverse_out_count,c2c_reverse_out_amount,
               o2c_reverse_in_count,o2c_reverse_in_amount,
               o2c_reverse_out_count,o2c_reverse_out_amount,
               C2S_TRANSFER_IN_AMOUNT ,C2S_TRANSFER_IN_COUNT ,REV_DIFFERENTIAL
          FROM DAILY_CHNL_TRANS_MAIN
          WHERE trans_date = aiv_date;

BEGIN

   id_trans_date:=aiv_date;
   BEGIN
      FOR t_r IN monthly_chnl_main (aiv_date)
      LOOP
         BEGIN
            UPDATE MONTHLY_CHNL_TRANS_MAIN
               SET roam_c2s_transfer_out_amount = roam_c2s_transfer_out_amount + t_r.roam_c2s_transfer_out_amount,
                   c2s_transfer_out_count = c2s_transfer_out_count + t_r.c2s_transfer_out_count,
                   c2s_transfer_out_amount = c2s_transfer_out_amount + t_r.c2s_transfer_out_amount,
                   o2c_transfer_in_count = o2c_transfer_in_count + t_r.o2c_transfer_in_count,
                   o2c_transfer_in_amount = o2c_transfer_in_amount + t_r.o2c_transfer_in_amount,
                   o2c_return_out_count = o2c_return_out_count + t_r.o2c_return_out_count,
                   o2c_return_out_amount = o2c_return_out_amount + t_r.o2c_return_out_amount,
                   o2c_withdraw_out_count = o2c_withdraw_out_count + t_r.o2c_withdraw_out_count,
                   o2c_withdraw_out_amount = o2c_withdraw_out_amount + t_r.o2c_withdraw_out_amount,
                   c2c_transfer_in_count = c2c_transfer_in_count + t_r.c2c_transfer_in_count,
                   c2c_transfer_in_amount = c2c_transfer_in_amount + t_r.c2c_transfer_in_amount,
                   c2c_transfer_out_count = c2c_transfer_out_count + t_r.c2c_transfer_out_count,
                   c2c_transfer_out_amount = c2c_transfer_out_amount + t_r.c2c_transfer_out_amount,
                   c2c_return_in_count = c2c_return_in_count + t_r.c2c_return_in_count,
                   c2c_return_in_amount = c2c_return_in_amount + t_r.c2c_return_in_amount,
                   c2c_return_out_count = c2c_return_out_count + t_r.c2c_return_out_count,
                   c2c_return_out_amount = c2c_return_out_amount + t_r.c2c_return_out_amount,
                   c2c_withdraw_in_count = c2c_withdraw_in_count + t_r.c2c_withdraw_in_count,
                   c2c_withdraw_in_amount = c2c_withdraw_in_amount + t_r.c2c_withdraw_in_amount,
                   c2c_withdraw_out_count = c2c_withdraw_out_count + t_r.c2c_withdraw_out_count,
                   c2c_withdraw_out_amount = c2c_withdraw_out_amount + t_r.c2c_withdraw_out_amount,
                   differential = differential + t_r.differential,
                   adjustment_in = adjustment_in + t_r.adjustment_in,
                   adjustment_out = adjustment_out + t_r.adjustment_out,
                   c2c_reverse_in_count = c2c_reverse_in_count + t_r.c2c_reverse_in_count,
                   c2c_reverse_in_amount = c2c_reverse_in_amount + t_r.c2c_reverse_in_amount,
                   c2c_reverse_out_count = c2c_reverse_out_count + t_r.c2c_reverse_out_count,
                   c2c_reverse_out_amount = c2c_reverse_out_amount + t_r.c2c_reverse_out_amount,
                   o2c_reverse_in_count = o2c_reverse_in_count + t_r.o2c_reverse_in_count,
                   o2c_reverse_in_amount = o2c_reverse_in_amount + t_r.o2c_reverse_in_amount,
                   o2c_reverse_out_count = o2c_reverse_out_count + t_r.o2c_reverse_out_count,
                   o2c_reverse_out_amount = o2c_reverse_out_amount + t_r.o2c_reverse_out_amount,
                   C2S_TRANSFER_IN_AMOUNT  = C2S_TRANSFER_IN_AMOUNT + t_r.C2S_TRANSFER_IN_AMOUNT,
                   C2S_TRANSFER_IN_COUNT = C2S_TRANSFER_IN_COUNT + t_r.C2S_TRANSFER_IN_COUNT,
                   REV_DIFFERENTIAL  = REV_DIFFERENTIAL +  t_r.REV_DIFFERENTIAL

             WHERE user_id = t_r.user_id
               AND TO_CHAR(trans_date,'mm-yy') = TO_CHAR(t_r.trans_date,'mm-yy')
               AND product_code = t_r.product_code
               AND category_code = t_r.category_code
               AND network_code = t_r.network_code
               AND network_code_for = t_r.network_code_for
               AND sender_domain_code = t_r.sender_domain_code
               AND grph_domain_code = t_r.grph_domain_code;
IF NOT FOUND
            THEN
               INSERT INTO MONTHLY_CHNL_TRANS_MAIN
                                (user_id,trans_date,
                            product_code, category_code,
                            network_code, network_code_for,
                            sender_domain_code,
                            roam_c2s_transfer_out_amount,
                            c2s_transfer_out_count,
                            c2s_transfer_out_amount,
                            o2c_transfer_in_count,
                            o2c_transfer_in_amount,
                            o2c_return_out_count,
                            o2c_return_out_amount,
                            o2c_withdraw_out_count,
                            o2c_withdraw_out_amount,
                            c2c_transfer_in_count,
                            c2c_transfer_in_amount,
                            c2c_transfer_out_count,
                            c2c_transfer_out_amount,
                            c2c_return_in_count,
                            c2c_return_in_amount,
                            c2c_return_out_count,
                            c2c_return_out_amount,
                            c2c_withdraw_in_count,
                            c2c_withdraw_in_amount,
                            c2c_withdraw_out_count,
                            c2c_withdraw_out_amount, differential,
                            adjustment_in, adjustment_out, created_on,
                            grph_domain_code,
                            c2c_reverse_in_count,
                            c2c_reverse_in_amount,
                            c2c_reverse_out_count,
                            c2c_reverse_out_amount,
                            o2c_reverse_in_count,
                            o2c_reverse_in_amount,
                            o2c_reverse_out_count,
                            o2c_reverse_out_amount,
                            C2S_TRANSFER_IN_AMOUNT ,
                            C2S_TRANSFER_IN_COUNT ,
                            REV_DIFFERENTIAL
            )
                    values (t_r.user_id,
                            TO_DATE('01-'||TO_CHAR (t_r.trans_date,'mm-yy'),'dd-mm-yy'),
                            t_r.product_code, t_r.category_code,
                            t_r.network_code, t_r.network_code_for,
                            t_r.sender_domain_code,
                            t_r.roam_c2s_transfer_out_amount,
                            t_r.c2s_transfer_out_count,
                            t_r.c2s_transfer_out_amount,
                            t_r.o2c_transfer_in_count,
                            t_r.o2c_transfer_in_amount,
                            t_r.o2c_return_out_count,
                            t_r.o2c_return_out_amount,
                            t_r.o2c_withdraw_out_count,
                            t_r.o2c_withdraw_out_amount,
                            t_r.c2c_transfer_in_count,
                            t_r.c2c_transfer_in_amount,
                            t_r.c2c_transfer_out_count,
                            t_r.c2c_transfer_out_amount,
                            t_r.c2c_return_in_count,
                            t_r.c2c_return_in_amount,
                            t_r.c2c_return_out_count,
                            t_r.c2c_return_out_amount,
                            t_r.c2c_withdraw_in_count,
                            t_r.c2c_withdraw_in_amount,
                            t_r.c2c_withdraw_out_count,
                            t_r.c2c_withdraw_out_amount, t_r.differential,
                            t_r.adjustment_in, t_r.adjustment_out, CURRENT_TIMESTAMP,
                            t_r.grph_domain_code,
                            t_r.c2c_reverse_in_count,
                            t_r.c2c_reverse_in_amount,
                            t_r.c2c_reverse_out_count,
                            t_r.c2c_reverse_out_amount,
                            t_r.o2c_reverse_in_count,
                            t_r.o2c_reverse_in_amount,
                            t_r.o2c_reverse_out_count,
                            t_r.o2c_reverse_out_amount,
                            t_r.C2S_TRANSFER_IN_AMOUNT ,
                            t_r.C2S_TRANSFER_IN_COUNT ,
                            t_r.REV_DIFFERENTIAL
                );
            END IF;
          EXCEPTION
            WHEN OTHERS
            THEN
               RAISE NOTICE '%',   'OTHERS EXCEPTION in Sp_Update_Monthly_Data1 1, User:'  || t_r.user_id || SQLERRM;
               v_messageforlog := 'OTHERS EXCEPTION in Sp_Update_Monthly_Data1 1, User:' || t_r.user_id || ', Date:' || id_trans_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION  using errcode = 'ERR05';
          END;
      END LOOP;
   EXCEPTION
	when sqlstate 'ERR05' then
         raise notice 'procexception in Sp_Update_Monthly_Data1:%',SQLERRM;
        RAISE EXCEPTION  using errcode = 'ERR01';
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION:' || SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
   END;
 EXCEPTION
      
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION =' || SQLERRM;
         v_messageforlog := 'OTHERS Exception in Sp_Update_Monthly_Data1 8, Date:' || id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
        RAISE EXCEPTION  using errcode = 'ERR01';
END;
$$;


ALTER FUNCTION pretupsdatabase.mis_sp_update_monthly_data1(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_sp_update_monthly_data2(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_update_monthly_data2(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
   DECLARE 
   id_trans_date               DAILY_CHNL_TRANS_DETAILS.trans_date%TYPE;
   --Cursor Declaration

   declare monthly_chnl_details CURSOR (aiv_date TIMESTAMP(0))
   FOR
      SELECT   user_id, trans_date,
               receiver_category_code, product_code, TYPE, transfer_category,
               transfer_type, transfer_sub_type,
               total_tax1_in,total_tax2_in,total_tax3_in,
               total_tax1_out,total_tax2_out,total_tax3_out,
               trans_in_count,trans_in_amount,
               trans_out_count,trans_out_amount
          FROM DAILY_CHNL_TRANS_DETAILS
         WHERE trans_date=aiv_date;

BEGIN

   id_trans_date:=aiv_date;
   BEGIN
      FOR t_r IN monthly_chnl_details (aiv_date)
      LOOP
         BEGIN
            UPDATE MONTHLY_CHNL_TRANS_DETAILS
               SET total_tax1_in = total_tax1_in + t_r.total_tax1_in,
                   total_tax2_in = total_tax2_in + t_r.total_tax2_in,
                   total_tax3_in = total_tax3_in + t_r.total_tax3_in,
                   total_tax1_out = total_tax1_out + t_r.total_tax1_out,
                   total_tax2_out = total_tax2_out + t_r.total_tax2_out,
                   total_tax3_out = total_tax3_out + t_r.total_tax3_out,
                   trans_in_count = trans_in_count + t_r.trans_in_count,
                   trans_in_amount = trans_in_amount + t_r.trans_in_amount,
                   trans_out_count = trans_out_count + t_r.trans_out_count,
                   trans_out_amount = trans_out_amount + t_r.trans_out_amount
             WHERE user_id = t_r.user_id
               AND TO_CHAR(trans_date,'mm-yy') = TO_CHAR(t_r.trans_date,'mm-yy')
               AND receiver_category_code = t_r.receiver_category_code
               AND product_code = t_r.product_code
               AND TYPE = t_r.TYPE
               AND transfer_category = t_r.transfer_category
               AND transfer_type = t_r.transfer_type
               AND transfer_sub_type = t_r.transfer_sub_type;

            IF NOT FOUND
            THEN
               INSERT INTO MONTHLY_CHNL_TRANS_DETAILS
                                (user_id,
                            trans_date,
                            receiver_category_code, product_code,
                            TYPE, total_tax1_in, total_tax2_in,
                            total_tax3_in, total_tax1_out,
                            total_tax2_out, total_tax3_out,
                            transfer_category, transfer_type,
                            transfer_sub_type, created_on,
                            trans_in_count, trans_in_amount,
                            trans_out_count, trans_out_amount)
                    values (t_r.user_id,
                            TO_DATE('01-'||TO_CHAR (t_r.trans_date, 'mm-yy'),'dd-mm-yy'),
                            t_r.receiver_category_code, t_r.product_code,
                            t_r.TYPE, t_r.total_tax1_in, t_r.total_tax2_in,
                            t_r.total_tax3_in, t_r.total_tax1_out,
                            t_r.total_tax2_out, t_r.total_tax3_out,
                            t_r.transfer_category, t_r.transfer_type,
                            t_r.transfer_sub_type, CURRENT_TIMESTAMP,
                            t_r.trans_in_count, t_r.trans_in_amount,
                            t_r.trans_out_count, t_r.trans_out_amount);
            END IF;
         EXCEPTION
            WHEN OTHERS
            THEN
               RAISE NOTICE '%',   'OTHERS EXCEPTION in Sp_Update_Monthly_Data2 3,User:' || t_r.user_id || SQLERRM;
               v_messageforlog := 'OTHERS Error in Sp_Update_Monthly_Data2 3,User:'|| t_r.user_id || ', Date:' ||id_trans_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION  using errcode = 'ERR05';
          END;
      END LOOP;
   EXCEPTION
   when sqlstate 'ERR05' then
         raise notice 'procexception in Sp_Update_Monthly_Data2:%',SQLERRM;
        RAISE EXCEPTION  using errcode = 'ERR01';
   
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION:' || SQLERRM;
         RAISE EXCEPTION 'procexception in Sp_Update_Monthly_Data2';
   END;

 EXCEPTION
      
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION =' || SQLERRM;
         v_messageforlog := 'OTHERS Exception in Sp_Update_Monthly_Data2 8, Date:' || id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE EXCEPTION 'Exception in Sp_Update_Monthly_Data2 8';
END;
$$;


ALTER FUNCTION pretupsdatabase.mis_sp_update_monthly_data2(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_sp_update_monthly_data3(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_update_monthly_data3(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
   DECLARE 

   id_trans_date               DAILY_CHNL_TRANS_DETAILS.trans_date%TYPE;
   --Cursor Declaration

   -----this procedure has been modified on 12/09


   declare monthly_c2s_details CURSOR (aiv_date TIMESTAMP(0))
   FOR
      SELECT   user_id, trans_date,
               receiver_network_code, SERVICE_TYPE, Sub_Service,
               sender_category_code,receiver_service_class_id,
               total_tax1,total_tax2,total_tax3,
               sender_transfer_amount,receiver_credit_amount,
               receiver_access_fee,
               differential_adjustment_tax1,
               differential_adjustment_tax2,
               differential_adjustment_tax3,
               receiver_bonus,
               transaction_amount,
               transaction_count,
               differential_amount,
               receiver_validity,
               receiver_bonus_validity,
               SENDER_REVERSE_AMOUNT ,
               RECEIVER_DEBIT_AMOUNT ,
               REVERSE_AMOUNT ,
               REVERSE_COUNT ,
               REVERSE_DIFF_AMOUNT,
               REVERSE_DIFF_COUNT, 
               PENALTY,
               OWNER_PENALTY,
               roam_amount,
               penalty_count
          FROM DAILY_C2S_TRANS_DETAILS
         WHERE trans_date=aiv_date;

BEGIN

   id_trans_date:=aiv_date;
   BEGIN
      FOR t_r IN monthly_c2s_details (aiv_date)
      LOOP
         BEGIN
            UPDATE MONTHLY_C2S_TRANS_DETAILS
               SET total_tax1 = total_tax1 + t_r.total_tax1,
                   total_tax2 = total_tax2 + t_r.total_tax2,
                   total_tax3 = total_tax3 + t_r.total_tax3,
                   sender_transfer_amount = sender_transfer_amount + t_r.sender_transfer_amount,
                   receiver_credit_amount = receiver_credit_amount + t_r.receiver_credit_amount,
                   receiver_access_fee = receiver_access_fee + t_r.receiver_access_fee,
                   differential_adjustment_tax1 = differential_adjustment_tax1 + t_r.differential_adjustment_tax1,
                   differential_adjustment_tax2 = differential_adjustment_tax2 + t_r.differential_adjustment_tax2,
                   differential_adjustment_tax3 = differential_adjustment_tax3 + t_r.differential_adjustment_tax3,
                   receiver_bonus = receiver_bonus + t_r.receiver_bonus,
                   receiver_validity = receiver_validity + t_r.receiver_validity,
                   receiver_bonus_validity = receiver_bonus_validity + t_r.receiver_bonus_validity,
                   transaction_count=transaction_count+t_r.transaction_count,
                   transaction_amount=transaction_amount+t_r.transaction_amount,
                   differential_amount=differential_amount+t_r.differential_amount,
                    SENDER_REVERSE_AMOUNT = SENDER_REVERSE_AMOUNT + t_r.SENDER_REVERSE_AMOUNT,
                    RECEIVER_DEBIT_AMOUNT = RECEIVER_DEBIT_AMOUNT + t_r.RECEIVER_DEBIT_AMOUNT,
                    REVERSE_AMOUNT = REVERSE_AMOUNT + t_r.REVERSE_AMOUNT,
                    REVERSE_COUNT = REVERSE_COUNT + t_r.REVERSE_COUNT,
                    REVERSE_DIFF_AMOUNT = REVERSE_DIFF_AMOUNT + t_r.REVERSE_DIFF_AMOUNT,
                    REVERSE_DIFF_COUNT = REVERSE_DIFF_COUNT + t_r.REVERSE_DIFF_COUNT,
                    PENALTY=PENALTY+t_r.PENALTY,
                    OWNER_PENALTY=OWNER_PENALTY+t_r.OWNER_PENALTY,
                    roam_amount=roam_amount+t_r.roam_amount,
                    PENALTY_COUNT=PENALTY_COUNT+t_r.PENALTY_COUNT
             WHERE user_id = t_r.user_id
               AND TO_CHAR(trans_date,'mm-yy') = TO_CHAR(t_r.trans_date,'mm-yy')
               AND receiver_network_code = t_r.receiver_network_code
               AND sender_category_code = t_r.sender_category_code
               AND receiver_service_class_id = t_r.receiver_service_class_id
               AND SERVICE_TYPE = t_r.SERVICE_TYPE
           AND SUB_SERVICE = t_r.SUB_SERVICE;

            IF NOT FOUND
            THEN
               INSERT INTO MONTHLY_C2S_TRANS_DETAILS
                              (user_id,
                            trans_date,
                            sender_category_code,
                            receiver_service_class_id,
                            receiver_network_code, SERVICE_TYPE,sub_service,
                            total_tax1, total_tax2, total_tax3,
                            sender_transfer_amount,
                            receiver_credit_amount,
                            receiver_access_fee,
                            differential_adjustment_tax1,
                            differential_adjustment_tax2,
                            differential_adjustment_tax3,
                            receiver_bonus, created_on,
                            receiver_validity,
                            receiver_bonus_validity,
                            transaction_amount,
                            transaction_count,
                            differential_amount,
                            SENDER_REVERSE_AMOUNT ,
                            RECEIVER_DEBIT_AMOUNT ,
                            REVERSE_AMOUNT ,
                            REVERSE_COUNT ,
                            REVERSE_DIFF_AMOUNT,
                            REVERSE_DIFF_COUNT,
                            PENALTY,
                            OWNER_PENALTY,
                            roam_amount,
                            PENALTY_COUNT
                            )
                    values (t_r.user_id,
                            TO_DATE('01-'||TO_CHAR (t_r.trans_date, 'mm-yy'),'dd-mm-yy'),
                            t_r.sender_category_code,
                            t_r.receiver_service_class_id,
                            t_r.receiver_network_code, t_r.SERVICE_TYPE,t_r.sub_service,
                            t_r.total_tax1, t_r.total_tax2, t_r.total_tax3,
                            t_r.sender_transfer_amount,
                            t_r.receiver_credit_amount,
                            t_r.receiver_access_fee,
                            t_r.differential_adjustment_tax1,
                            t_r.differential_adjustment_tax2,
                            t_r.differential_adjustment_tax3,
                            t_r.receiver_bonus, CURRENT_TIMESTAMP,
                            t_r.receiver_validity,t_r.receiver_bonus_validity,
                            t_r.transaction_amount,t_r.transaction_count,
                            t_r.differential_amount,
                            t_r.SENDER_REVERSE_AMOUNT ,
                            t_r.RECEIVER_DEBIT_AMOUNT ,
                            t_r.REVERSE_AMOUNT ,
                            t_r.REVERSE_COUNT ,
                            t_r.REVERSE_DIFF_AMOUNT,
                            t_r.REVERSE_DIFF_COUNT,
                            t_r.PENALTY,
                            t_r.OWNER_PENALTY,
                            t_r.roam_amount,
                            t_r.PENALTY_COUNT
                            );
            END IF;

            EXCEPTION
            WHEN OTHERS
            THEN
               RAISE NOTICE '%',   'OTHERS EXCEPTION in Sp_Update_Monthly_Data3 5, User:'|| t_r.user_id || SQLERRM;
               v_messageforlog := 'OTHERS Error in Sp_Update_Monthly_Data3 5, User:'|| t_r.user_id;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION  using errcode = 'ERR05';
          END;
        END LOOP;
   EXCEPTION
     when sqlstate 'ERR05' then
         raise notice 'procexception in Sp_Update_Monthly_Data3:%',SQLERRM;
        RAISE EXCEPTION  using errcode = 'ERR01';
      WHEN OTHERS
      THEN
      RAISE NOTICE '%', 'procexception in Sp_Update_Monthly_Data3 6';
         RAISE NOTICE '%', 'OTHERS EXCEPTION:' || SQLERRM;
     RAISE EXCEPTION  using errcode = 'ERR01';
   END;
 EXCEPTION
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION =' || SQLERRM;
         v_messageforlog := 'OTHERS Exception in Sp_Update_Monthly_Data3 8, Date:' || id_trans_date;
         v_sqlerrmsgforlog := SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';
END;
$$;


ALTER FUNCTION pretupsdatabase.mis_sp_update_monthly_data3(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_sp_update_roam_c2s_trans(character varying, timestamp without time zone, character varying, character varying, character varying, character varying, character varying, integer, character varying, integer); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_sp_update_roam_c2s_trans(p_user_id character varying, p_date timestamp without time zone, p_productid character varying, p_categorycode character varying, p_homelocation character varying, p_targetlocation character varying, p_domaincode character varying, p_productmrp integer, p_geodomaincode character varying, p_trans_amt integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
   DECLARE
   v_message              VARCHAR (500);
     v_messageforlog        VARCHAR (500);
        v_sqlerrmsgforlog      VARCHAR (500);
        user_rcd_count INT;
   BEGIN
   
      BEGIN
         BEGIN
            --SELECT 1 INTO user_rcd_count
            --FROM TEMP_DAILY_CHNL_TRANS_MAIN
           -- WHERE user_id = gv_userid
            --AND network_code = gv_networkcode
           -- AND network_code_for = gv_networkcodefor
            --AND product_code = gv_productcode
           -- AND grph_domain_code = gv_grphdomaincode
            --AND trans_date = gd_transaction_date;
           
	user_rcd_count=(SELECT 1
		FROM TEMP_DAILY_CHNL_TRANS_MAIN
           WHERE user_id = gv_userid
           AND network_code = gv_networkcode
           AND network_code_for = gv_networkcodefor
            AND product_code = gv_productcode
            AND grph_domain_code = gv_grphdomaincode
           AND trans_date = gd_transaction_date);

            IF NOT FOUND
            THEN
               v_messageforlog := 'SQL%NOTFOUND in SP_UPDATE_ROAM_C2S_TRANS 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION 'EXCEPTION in SP_UPDATE_ROAM_C2S_TRANS 1';
            END IF;
         EXCEPTION
            WHEN NO_DATA_FOUND
            THEN                           --when no row returned for the user
               user_rcd_count := 0;
            WHEN OTHERS
            THEN
               v_messageforlog := 'OTHERS SQL Exception in SP_UPDATE_ROAM_C2S_TRANS 1, User:' || gv_userid || ' Date:' || gd_transaction_date;
               v_sqlerrmsgforlog := SQLERRM;
               RAISE EXCEPTION 'SQL Exception in SP_UPDATE_ROAM_C2S_TRANS 1';
         END;

         IF user_rcd_count = 0
         THEN
            INSERT INTO TEMP_DAILY_CHNL_TRANS_MAIN
                        (user_id, trans_date, product_code, category_code,
                         network_code, network_code_for, sender_domain_code,
                         created_on, product_mrp,grph_domain_code,
                         roam_c2s_transfer_out_amount
                        )
                 VALUES (p_user_id, p_date, p_productid, p_categorycode,
                         p_homelocation, p_homelocation, p_domaincode,
                         gd_createdon, p_productmrp,p_geodomaincode,
                         p_trans_amt
                        );
         ELSE
            UPDATE TEMP_DAILY_CHNL_TRANS_MAIN
               SET roam_c2s_transfer_out_amount =
                                    roam_c2s_transfer_out_amount + p_trans_amt
             WHERE user_id = p_user_id
               AND trans_date = p_date
               AND product_code = p_productid
               AND network_code = p_homelocation
               AND network_code_for = p_homelocation;
         END IF;
      EXCEPTION
        
         WHEN OTHERS
         THEN
            RAISE NOTICE '%',   'OTHERS EXCEPTION in SP_UPDATE_ROAM_C2S_TRANS 2, User:' || gv_userid || SQLERRM;
            v_messageforlog := 'OTHERS Exception in SP_UPDATE_ROAM_C2S_TRANS 2, User:' || gv_userid || ' Date:' || gd_transaction_date;
            v_sqlerrmsgforlog := SQLERRM;
            RAISE EXCEPTION 'EXCEPTION in SP_UPDATE_ROAM_C2S_TRANS 2';
      END;
                              --end of distributor insertion block
   EXCEPTION
    
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION IN SP_UPDATE_ROAM_C2S_TRANS 3:' || SQLERRM;
         RAISE EXCEPTION 'EXCEPTION IN SP_UPDATE_ROAM_C2S_TRANS 3';
   END;
    $$;


ALTER FUNCTION pretupsdatabase.mis_sp_update_roam_c2s_trans(p_user_id character varying, p_date timestamp without time zone, p_productid character varying, p_categorycode character varying, p_homelocation character varying, p_targetlocation character varying, p_domaincode character varying, p_productmrp integer, p_geodomaincode character varying, p_trans_amt integer) OWNER TO pgdb;

--
-- Name: mis_summary_report_main_sp_get_mis_data_dtrange(character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_summary_report_main_sp_get_mis_data_dtrange(aiv_fromdate character varying, aiv_todate character varying, OUT aov_message character varying, OUT aov_messageforlog character varying, OUT aov_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE
  ld_from_date TIMESTAMP(0);
  ld_to_date TIMESTAMP(0);
  ld_created_on TIMESTAMP(0);
  flag SMALLINT;
  status SMALLINT;
  ld_mis_date TIMESTAMP(0);
  mis_already_executed SMALLINT;
  n_date_for_mis TIMESTAMP(0);
  v_nullvalue character varying(5);
BEGIN

  ld_from_date   :=TO_DATE(aiv_fromDate,'dd/mm/yy');
  ld_to_date     :=TO_DATE(aiv_toDate,'dd/mm/yy');
  n_date_for_mis :=ld_from_date;
  flag           :=0;
  ld_created_on  :=CURRENT_TIMESTAMP;  -- Initailaize Created On date
  mis_already_executed :=0;
  v_nullvalue := 'N.A.';
  RAISE NOTICE 'MIS summary report Started ::::::::';
  
WHILE n_date_for_mis <= ld_to_date ---run the MIS process for each date less than the To Date
   LOOP
	RAISE NOTICE '%','EXCEUTING FOR MIS SUMMARY REPOR ::::::::'||n_date_for_mis;
	BEGIN
		 ---Check if MIS process has already run for the date
		 mis_already_executed=(SELECT 1 
		 FROM PROCESS_STATUS
    	 WHERE PROCESS_ID='P2PMIS' AND EXECUTED_UPTO>=n_date_for_mis);

		if mis_already_executed is not null then
		  RAISE NOTICE '%', 'HELLO!!!!!!!!  ' || mis_already_executed;
		 RAISE NOTICE '%','PreTUPS P2P MIS new already Executed, Date:' || n_date_for_mis ;
		 aov_message :='FAILED';
		 aov_messageForLog:='PreTUPS P2P MIS new already Executed, Date:' || n_date_for_mis;
		 aov_sqlerrMsgForLog:=' ';
		 RAISE EXCEPTION  using errcode = 'ERR05';
		else
		 --EXCEPTION
	     --WHEN NO_DATA_FOUND THEN
		 BEGIN
		     ---Check if Underprocess or Ambigous transactions are found in the Transaction table for the date
 		     status=(SELECT 1 FROM SUBSCRIBER_TRANSFERS ST
		     WHERE ST.TRANSFER_DATE = n_date_for_mis AND
		     ST.TRANSFER_STATUS IN ('205','250'));
		      if status is not null then
	     	 RAISE NOTICE '%','Underprocess or Ambigous transaction found. PreTUPS P2P MIS cannot continue, Date:' || n_date_for_mis;
		 	 aov_messageForLog:='Underprocess or Ambigous transaction found. PreTUPS P2P MIS cannot continue, Date:' || n_date_for_mis;
		 	 aov_sqlerrMsgForLog:=' ';
			 flag:=1;

			 else
	      --EXCEPTION
	     	-- WHEN NO_DATA_FOUND THEN ----If MIS not executed for the date and no Underprocess or Ambigous transactions found --then update all the MIS tables
			 	 --- Call of Update Hourly Transaction Summary procedure
	 	  	     BEGIN
	 	  	      RAISE NOTICE '%','BEFORE BEGIN:'||v_nullvalue;
	 	  	  select * into aov_messageForLog,aov_sqlerrMsgForLog from MIS_SUMMARY_REPORT_SP_UPDATE_HOURLY_TRN_SUMMARY (n_date_for_mis, v_nullvalue); 
	      		 --perform MIS_SUMMARY_REPORT_SP_UPDATE_HOURLY_TRN_SUMMARY(n_date_for_mis);
	    		 EXCEPTION
	       		 when sqlstate 'ERR05' then
	       		 RAISE NOTICE '%','Exception in SP_UPDATE_HOURLY_TRN_SUMMARY:'||SQLERRM;
		   		 aov_messageForLog:='Exception in Procedure SP_UPDATE_HOURLY_TRN_SUMMARY, Date:' || n_date_for_mis;
	 	   		 aov_sqlerrMsgForLog:=SQLERRM;
		   		 RAISE EXCEPTION  using errcode = 'ERR01';

	       		 WHEN OTHERS THEN
	       		 RAISE NOTICE '%','Error in Procedure SP_UPDATE_HOURLY_TRN_SUMMARY:'||SQLERRM;
		   		 aov_messageForLog:='Error in Procedure SP_UPDATE_HOURLY_TRN_SUMMARY, Date:' || n_date_for_mis;
	 	   		 aov_sqlerrMsgForLog:=SQLERRM;
		   		RAISE EXCEPTION  using errcode = 'ERR01';
	    		 END;

	    		 --- Call of Update Daily Transaction Summary procedure
	    		 BEGIN
	    		 select * into aov_messageForLog,aov_sqlerrMsgForLog from MIS_SUMMARY_REPORT_SP_UPDATE_DAILY_TRN_SUMMARY (n_date_for_mis, v_nullvalue); 
	       		 --perform MIS_SUMMARY_REPORT_SP_UPDATE_DAILY_TRN_SUMMARY(n_date_for_mis);
	    		 EXCEPTION
	       		 when sqlstate 'ERR05' then
	       		 RAISE NOTICE '%','Exception in SP_UPDATE_DAILY_TRN_SUMMARY:'||SQLERRM;
	       		 aov_messageForLog:='Exception in Procedure SP_UPDATE_DAILY_TRN_SUMMARY, Date:' || n_date_for_mis;
	 	   		 aov_sqlerrMsgForLog:=SQLERRM;
	       		 RAISE EXCEPTION  using errcode = 'ERR01';
	       		 WHEN OTHERS THEN
	       		 RAISE NOTICE '%','Error in Procedure SP_UPDATE_DAILY_TRN_SUMMARY:'||SQLERRM;
		   		 aov_messageForLog:='Error in Procedure SP_UPDATE_DAILY_TRN_SUMMARY, Date:' || n_date_for_mis;
	 	   		 aov_sqlerrMsgForLog:=SQLERRM;
	       		 RAISE EXCEPTION  using errcode = 'ERR01';
	    		 END;

	    		 --- Call of Update Monthly Transaction Summary procedure
	    		 BEGIN
	    		 select * into aov_messageForLog,aov_sqlerrMsgForLog from MIS_SUMMARY_REPORT_SP_UPDATE_MONTHLY_TRN_SUMMARY (n_date_for_mis, v_nullvalue); 
	      		 --perform MIS_SUMMARY_REPORT_SP_UPDATE_MONTHLY_TRN_SUMMARY(n_date_for_mis);
	    		 EXCEPTION
	       		 when sqlstate 'ERR05' then
	       		 RAISE NOTICE '%','Exception in SP_UPDATE_MONTHLY_TRN_SUMMARY: '||SQLERRM;
		   		 aov_messageForLog:='Exception in Procedure SP_UPDATE_MONTHLY_TRN_SUMMARY, Date:' || n_date_for_mis;
	 	   		 aov_sqlerrMsgForLog:=SQLERRM;
	       		 RAISE EXCEPTION  using errcode = 'ERR01';

	       		 WHEN  OTHERS THEN
	       		 RAISE NOTICE '%','Error in Procedure SP_UPDATE_MONTHLY_TRN_SUMMARY: '||SQLERRM;
		   		 aov_messageForLog:='Error in Procedure SP_UPDATE_MONTHLY_TRN_SUMMARY, Date:' || n_date_for_mis;
	 	   		 aov_sqlerrMsgForLog:=SQLERRM;
	       		 RAISE EXCEPTION  using errcode = 'ERR01';
	    		 END;

	    		 --- Call of Update P2P Subscriber Summary procedure
	    		 BEGIN
	    		 select * into aov_messageForLog,aov_sqlerrMsgForLog from MIS_SUMMARY_REPORT_SP_UPDATE_P2P_SUB_SUMMARY (n_date_for_mis, v_nullvalue);
	       		 --perform MIS_SUMMARY_REPORT_SP_UPDATE_P2P_SUB_SUMMARY(n_date_for_mis);
	    		 EXCEPTION
	       		  when sqlstate 'ERR05' then
	       		 RAISE NOTICE '%','Exception in SP_UPDATE_P2P_SUB_SUMMARY: '||SQLERRM;
		   		 aov_messageForLog:='Exception in Procedure SP_UPDATE_P2P_SUB_SUMMARY, Date:' || n_date_for_mis;
	 	   		 aov_sqlerrMsgForLog:=SQLERRM;
		   		 RAISE EXCEPTION  using errcode = 'ERR01';

	       		 WHEN  OTHERS THEN
	       		 RAISE NOTICE '%','Error in Procedure SP_UPDATE_P2P_SUB_SUMMARY: '||SQLERRM;
		   		 aov_messageForLog:='Error in Procedure SP_UPDATE_P2P_SUB_SUMMARY, Date:' || n_date_for_mis;
	 	   		 aov_sqlerrMsgForLog:=SQLERRM;
	       		 RAISE EXCEPTION  using errcode = 'ERR01';
	    		 END;

	    		 --- Call of Update success failure procedure
	    		 BEGIN
	    		 select * into aov_messageForLog,aov_sqlerrMsgForLog from MIS_SUMMARY_REPORT_SP_UPDATE_P2P_SUCCESS_FAILURE (n_date_for_mis);
	       		 --perform MIS_SUMMARY_REPORT_SP_UPDATE_P2P_SUCCESS_FAILURE(n_date_for_mis);
	    		 EXCEPTION
	       		 when sqlstate 'ERR05' then
	       		 RAISE NOTICE '%','Exception in SP_UPDATE_P2P_SUCCESS_FAILURE: '||SQLERRM;
		   		 aov_messageForLog:='Exception in Procedure SP_UPDATE_P2P_SUCCESS_FAILURE, Date:' || n_date_for_mis;
	 	   		 aov_sqlerrMsgForLog:=SQLERRM;
		   		 RAISE EXCEPTION  using errcode = 'ERR01';
	       		 WHEN  OTHERS THEN
	       		 RAISE NOTICE '%','Error in Procedure SP_UPDATE_P2P_SUCCESS_FAILURE:'||SQLERRM;
		   		 aov_messageForLog:='Error in Procedure SP_UPDATE_P2P_SUCCESS_FAILURE, Date:' || n_date_for_mis;
	 	   		 aov_sqlerrMsgForLog:=SQLERRM;
	       		RAISE EXCEPTION  using errcode = 'ERR01';
	       		
	    		 END;
			END IF;
				 UPDATE PROCESS_STATUS SET executed_upto=n_date_for_mis, executed_on=current_timestamp WHERE PROCESS_ID='P2PMIS';			 
	     		 /* COMMIT; */
		 		 aov_message :='SUCCESS';
		 		 aov_messageForLog :='PreTUPS P2P MIS successfully executed, Date Time:'||current_timestamp;
				 aov_sqlerrMsgForLog :=' ';

	         /*WHEN TOO_MANY_ROWS THEN
		     RAISE NOTICE '%','Underprocess or Ambigous transaction found. PreTUPS P2P MIS cannot continue, Date:' || n_date_for_mis;
		     aov_messageForLog:='Underprocess or Ambigous transaction found. PreTUPS P2P MIS cannot continue, Date:' || n_date_for_mis;
	         aov_sqlerrMsgForLog:=' ';
			 flag:=1;

			 WHEN OTHERS THEN
	       	 RAISE NOTICE '%','Error when checking for Underprocess or Ambigous transactions'||SQLERRM;
		   	 aov_messageForLog:='Error when checking for Underprocess or Ambigous transactions, Date:'|| n_date_for_mis;
	 	   	 aov_sqlerrMsgForLog:=SQLERRM;
		   	 RAISE EXCEPTION  using errcode = 'ERR01';*/
		  END;

END IF;

EXCEPTION
		  when sqlstate 'ERR05' then--exception handled in case MIS already executed
	 	  aov_sqlerrMsgForLog:=SQLERRM;
		  RAISE EXCEPTION  using errcode = 'ERR01';
		  WHEN OTHERS THEN
	      RAISE NOTICE '%','Error when checking if MIS process has already been executed'||SQLERRM;
		  aov_messageForLog:='Error when checking if MIS process has already been executed, Date:'|| n_date_for_mis;
	 	  aov_sqlerrMsgForLog:=SQLERRM;
		  RAISE EXCEPTION  using errcode = 'ERR01';

  END;

IF flag = 1 THEN
n_date_for_mis := ld_to_date; ---If Underprocess or Anbigous transaction found then stop the MIS process for further execution of other dates
RAISE EXCEPTION  using errcode = 'ERR01';
ELSE
n_date_for_mis:=n_date_for_mis+ interval '1' day;
END IF;
END LOOP;

EXCEPTION --Exception Handling of main procedure
  when sqlstate 'ERR01' then
  /* ROLLBACK; */
  RAISE NOTICE '%','EXCEPTION Caught='||SQLERRM;
  aov_message :='FAILED';

  WHEN OTHERS THEN
  /* ROLLBACK; */
  RAISE NOTICE '%','ERROR in Main procedure:='||SQLERRM;
  aov_message :='FAILED';

END ;	
	
$$;


ALTER FUNCTION pretupsdatabase.mis_summary_report_main_sp_get_mis_data_dtrange(aiv_fromdate character varying, aiv_todate character varying, OUT aov_message character varying, OUT aov_messageforlog character varying, OUT aov_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_summary_report_sp_update_daily_trn_summary(timestamp without time zone, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_summary_report_sp_update_daily_trn_summary(aiv_date timestamp without time zone, v_nullvalue character varying, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE 
	ln_success_count		    DAILY_TRANSACTION_SUMMARY.SUCCESS_COUNT%TYPE=0;
	ln_failure_count		    DAILY_TRANSACTION_SUMMARY.FAILURE_COUNT%TYPE=0;
	ln_success_amount		    DAILY_TRANSACTION_SUMMARY.SUCCESS_AMT%TYPE=0;
	ln_failure_amount			DAILY_TRANSACTION_SUMMARY.FAILURE_AMT%TYPE=0;
	ln_sender_processing_fee	DAILY_TRANSACTION_SUMMARY.SENDER_PROCESSING_FEE%TYPE=0;
	ln_receiver_processing_fee	DAILY_TRANSACTION_SUMMARY.RECEIVER_PROCESSING_FEE%TYPE=0;
	ln_sender_tax1_amount		DAILY_TRANSACTION_SUMMARY.SENDER_TAX1_AMOUNT%TYPE=0;
	ln_sender_tax2_amount		DAILY_TRANSACTION_SUMMARY.SENDER_TAX2_AMOUNT%TYPE=0;
	ln_receiver_tax1_amount		DAILY_TRANSACTION_SUMMARY.RECEIVER_TAX1_AMOUNT%TYPE=0;
	ln_receiver_tax2_amount		DAILY_TRANSACTION_SUMMARY.RECEIVER_TAX2_AMOUNT%TYPE=0;
	ln_sender_debit_amount		DAILY_TRANSACTION_SUMMARY.SENDER_DEBIT_AMOUNT%TYPE=0;
	ln_receiver_credit_amount	DAILY_TRANSACTION_SUMMARY.RECEIVER_CREDIT_AMOUNT%TYPE=0;

	ln_receiver_bonus_amount	DAILY_TRANSACTION_SUMMARY.bonus_amount%TYPE=0;
	ln_receiver_validity		DAILY_TRANSACTION_SUMMARY.validity%TYPE=0;
	ln_receiver_bonus_validity	DAILY_TRANSACTION_SUMMARY.bonus_validity%TYPE=0;

	---Cursor Declaration

	declare DAILY_TRANSACTION_CURSOR CURSOR(aiv_Date Timestamp(0)) IS
	SELECT /*+ INDEX(st) INDEX(T1) INDEX(T2)*/
	COALESCE(T1.SERVICE_CLASS_ID,v_nullvalue) SENDER_SERVICE_CLASS,
	COALESCE(T2.SERVICE_CLASS_ID,v_nullvalue) RECEIVER_SERVICE_CLASS,
	ST.TRANSFER_DATE TRANS_DATE,
	ST.TRANSFER_STATUS STATUS,
	COUNT(ST.TRANSFER_DATE) COUNT,
	COALESCE(ST.SERVICE_TYPE,v_nullvalue) SERVICE,
	ST.SUB_SERVICE,
	COALESCE(ST.NETWORK_CODE,v_nullvalue) NETWORK_CODE,
	COALESCE(ST.RECEIVER_NETWORK_CODE,NETWORK_CODE) RECEIVER_NETWORK_CODE,
	SUM(COALESCE(ST.TRANSFER_VALUE,0)) AMOUNT,
	SUM(COALESCE(ST.SENDER_ACCESS_FEE,0)) SENDER_PROCESSING_FEE,
	SUM(COALESCE(ST.RECEIVER_ACCESS_FEE,0)) RECEIVER_PROCESSING_FEE,
	SUM(COALESCE(ST.SENDER_TAX1_VALUE,0)) SENDER_TAX1_AMOUNT,
	SUM(COALESCE(ST.SENDER_TAX2_VALUE,0)) SENDER_TAX2_AMOUNT,
	SUM(COALESCE(ST.RECEIVER_TAX1_VALUE,0)) RECEIVER_TAX1_AMOUNT,
	SUM(COALESCE(ST.RECEIVER_TAX2_VALUE,0)) RECEIVER_TAX2_AMOUNT,
	SUM(COALESCE(ST.SENDER_TRANSFER_VALUE,0)) SENDER_DEBIT_AMOUNT,
	SUM(COALESCE(ST.RECEIVER_TRANSFER_VALUE,0)) RECEIVER_CREDIT_AMOUNT,
	SUM(COALESCE(ST.RECEIVER_BONUS_VALUE,0)) RECEIVER_BONUS_AMOUNT,
	SUM(COALESCE(ST.RECEIVER_VALIDITY,0)) RECEIVER_VALIDITY,
	SUM(COALESCE(ST.RECEIVER_BONUS_VALIDITY,0)) RECEIVER_BONUS_VALIDITY,
	ST.TRANSFER_CATEGORY
	FROM TRANSFER_ITEMS T1, TRANSFER_ITEMS T2, SUBSCRIBER_TRANSFERS ST
	WHERE
	ST.TRANSFER_DATE = AIV_DATE AND
	ST.TRANSFER_STATUS IN('200','206') AND
	ST.TRANSFER_ID = T1.TRANSFER_ID
	AND ST.TRANSFER_ID = T2.TRANSFER_ID
	AND T1.USER_TYPE = 'SENDER'
	AND T2.USER_TYPE = 'RECEIVER'
	GROUP BY
	T1.SERVICE_CLASS_ID,
	T2.SERVICE_CLASS_ID,
	ST.TRANSFER_DATE,
	ST.TRANSFER_STATUS,
	ST.SERVICE_TYPE,
	ST.SUB_SERVICE,
	ST.NETWORK_CODE,
	ST.RECEIVER_NETWORK_CODE,
	ST.transfer_category;

BEGIN

	FOR dtr IN DAILY_TRANSACTION_CURSOR(aiv_Date) LOOP
	ln_success_count		   :=0;
	ln_failure_count	       :=0;
	ln_success_amount		   :=0;
	ln_failure_amount	 	   :=0;
	ln_sender_processing_fee   :=0;
	ln_receiver_processing_fee :=0;
	ln_sender_tax1_amount	   :=0;
	ln_sender_tax2_amount	   :=0;
	ln_receiver_tax1_amount	   :=0;
	ln_receiver_tax2_amount	   :=0;
	ln_sender_debit_amount	   :=0;
	ln_receiver_credit_amount  :=0;
	ln_receiver_bonus_amount	:=0;
	ln_receiver_validity		:=0;
	ln_receiver_bonus_validity	:=0;


	IF dtr.status = '200' THEN
		ln_success_count	       :=dtr.COUNT;
		ln_success_amount	       :=dtr.AMOUNT;
		ln_sender_processing_fee   :=dtr.SENDER_PROCESSING_FEE;
		ln_receiver_processing_fee :=dtr.RECEIVER_PROCESSING_FEE;
		ln_sender_tax1_amount	   :=dtr.SENDER_TAX1_AMOUNT;
		ln_sender_tax2_amount	   :=dtr.SENDER_TAX2_AMOUNT;
		ln_receiver_tax1_amount	   :=dtr.RECEIVER_TAX1_AMOUNT;
		ln_receiver_tax2_amount	   :=dtr.RECEIVER_TAX2_AMOUNT;
		ln_sender_debit_amount	   :=dtr.SENDER_DEBIT_AMOUNT;
		ln_receiver_credit_amount  :=dtr.RECEIVER_CREDIT_AMOUNT;
		ln_receiver_bonus_amount	:=dtr.RECEIVER_BONUS_AMOUNT;
		ln_receiver_validity		:=dtr.RECEIVER_VALIDITY;
		ln_receiver_bonus_validity	:=dtr.RECEIVER_BONUS_VALIDITY;
	ELSE
		ln_failure_count   := dtr.COUNT;
		ln_failure_amount  := dtr.AMOUNT;
	END IF;
	BEGIN
	UPDATE DAILY_TRANSACTION_SUMMARY
	SET
		SUCCESS_COUNT			=SUCCESS_COUNT + ln_success_count,
		FAILURE_COUNT			=FAILURE_COUNT + ln_failure_count,
		SUCCESS_AMT				=SUCCESS_AMT + ln_success_amount,
		FAILURE_AMT				=FAILURE_AMT + ln_failure_amount,
		SENDER_PROCESSING_FEE	=SENDER_PROCESSING_FEE + ln_sender_processing_fee,
		RECEIVER_PROCESSING_FEE	=RECEIVER_PROCESSING_FEE + ln_receiver_processing_fee,
		SENDER_TAX1_AMOUNT		=SENDER_TAX1_AMOUNT	+ ln_sender_tax1_amount,
		SENDER_TAX2_AMOUNT		=SENDER_TAX2_AMOUNT	+ ln_sender_tax2_amount,
		RECEIVER_TAX1_AMOUNT	=RECEIVER_TAX1_AMOUNT + ln_receiver_tax1_amount,
		RECEIVER_TAX2_AMOUNT	=RECEIVER_TAX2_AMOUNT + ln_receiver_tax2_amount,
		SENDER_DEBIT_AMOUNT		=SENDER_DEBIT_AMOUNT + ln_sender_debit_amount,
		RECEIVER_CREDIT_AMOUNT	=RECEIVER_CREDIT_AMOUNT	+ ln_receiver_credit_amount,
		BONUS_AMOUNT			=BONUS_AMOUNT	+ ln_receiver_bonus_amount,
		VALIDITY				=VALIDITY	+ ln_receiver_validity,
		BONUS_VALIDITY			=BONUS_VALIDITY	+ ln_receiver_bonus_validity
		WHERE
		TRANS_DATE				  = dtr.trans_date AND
		SENDER_SERVICE_CLASS      = dtr.sender_service_class AND
		RECEIVER_SERVICE_CLASS	  = dtr.receiver_service_class AND
		SERVICE				      = dtr.service AND
		SUB_SERVICE				  = dtr.sub_service AND
		SENDER_NETWORK_CODE		  =	dtr.network_code AND
		RECEIVER_NETWORK_CODE	  =	dtr.receiver_network_code and
		TRANSFER_CATEGORY		  = dtr.transfer_category;
	if not found  THEN
	INSERT INTO DAILY_TRANSACTION_SUMMARY
	    (trans_date,
		sender_service_class,
		receiver_service_class,
		service,
		success_count,
		failure_count,
		success_amt,
		failure_amt,
		sender_processing_fee,
		receiver_processing_fee,
		sender_tax1_amount,
		sender_tax2_amount,
		receiver_tax1_amount,
		receiver_tax2_amount,
		sender_debit_amount,
		receiver_credit_amount,
		sender_network_code,
		receiver_network_code,
		created_on,
		sub_service,
		bonus_amount,
		validity,
		bonus_validity,
		transfer_category)
	values (dtr.trans_date,
		dtr.sender_service_class,
		dtr.receiver_service_class,
		dtr.service,
		ln_success_count,
		ln_failure_count,
		ln_success_amount,
		ln_failure_amount,
		ln_sender_processing_fee,
		ln_receiver_processing_fee,
		ln_sender_tax1_amount,
		ln_sender_tax2_amount,
		ln_receiver_tax1_amount,
		ln_receiver_tax2_amount,
		ln_sender_debit_amount,
		ln_receiver_credit_amount,
		dtr.network_code,
		dtr.receiver_network_code,
		date_trunc('day',current_timestamp::timestamp),
		dtr.sub_service,
		ln_receiver_bonus_amount,
		ln_receiver_validity,
		ln_receiver_bonus_validity,
		dtr.transfer_category);

	END if;
	EXCEPTION
			 WHEN OTHERS THEN
			 	  RAISE NOTICE '%','EXCEPTION while Updating/Inserting in DAILY_TRANSACTION_SUMMARY ='||sqlerrm;
			   	  v_messageForLog:='Error while Updating/Inserting record in DAILY_TRANSACTION_SUMMARY, Date:'||dtr.trans_date;
		       	  v_sqlerrMsgForLog:=sqlerrm;
			  	  RAISE EXCEPTION  using errcode = 'ERR05';
	END;
    END LOOP;

EXCEPTION
	when sqlstate 'ERR05' then
	    --IF DAILY_TRANSACTION_CURSOR%ISOPEN THEN
           --CLOSE DAILY_TRANSACTION_CURSOR;
		--END IF;
		RAISE NOTICE '%','EXCEPTION while Updating/Inserting in DAILY_TRANSACTION_SUMMARY';
		RAISE EXCEPTION  using errcode = 'ERR05';
	WHEN OTHERS THEN
	    --IF DAILY_TRANSACTION_CURSOR%ISOPEN THEN
           --CLOSE DAILY_TRANSACTION_CURSOR;
		--END IF;
		RAISE NOTICE '%','OTHERS EXCEPTION ='||sqlerrm;
		v_messageForLog:='Exception while inserting/updating record in DAILY_TRANSACTION_SUMMARY';
		v_sqlerrMsgForLog:=sqlerrm;
		RAISE EXCEPTION  using errcode = 'ERR05';
END;

$$;


ALTER FUNCTION pretupsdatabase.mis_summary_report_sp_update_daily_trn_summary(aiv_date timestamp without time zone, v_nullvalue character varying, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_summary_report_sp_update_hourly_trn_summary(timestamp without time zone, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_summary_report_sp_update_hourly_trn_summary(aiv_date timestamp without time zone, v_nullvalue character varying, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE 
	ln_success_count				   HOURLY_TRANSACTION_SUMMARY.SUCCESS_COUNT%TYPE=0;
	ln_failure_count				   HOURLY_TRANSACTION_SUMMARY.FAILURE_COUNT%TYPE=0;
	ln_success_amount				   HOURLY_TRANSACTION_SUMMARY.SUCCESS_AMT%TYPE=0;
	ln_failure_amount				   HOURLY_TRANSACTION_SUMMARY.FAILURE_AMT%TYPE=0;
	ln_sender_debit_amount			   HOURLY_TRANSACTION_SUMMARY.SENDER_DEBIT_AMOUNT%TYPE=0;
	ln_receiver_credit_amount		   HOURLY_TRANSACTION_SUMMARY.RECEIVER_CREDIT_AMOUNT%TYPE=0;
	---Cursor Declaration

	declare HOURLY_TRANSACTION_CURSOR CURSOR(aiv_Date Timestamp(0)) IS
	SELECT /*+ INDEX(st) INDEX(T1) INDEX(T2)*/
	COALESCE(T1.SERVICE_CLASS_ID,v_nullvalue) SENDER_SERVICE_CLASS,
	COALESCE(T2.SERVICE_CLASS_ID,v_nullvalue) RECEIVER_SERVICE_CLASS,
	ST.TRANSFER_DATE TRANS_DATE,
	TO_CHAR(ST.TRANSFER_DATE_TIME,'HH24')::integer TRANS_HOUR,
	ST.TRANSFER_STATUS STATUS,
	COUNT(ST.TRANSFER_DATE) COUNT,
	COALESCE(ST.SERVICE_TYPE,v_nullvalue) SERVICE,
	ST.SUB_SERVICE,
	SUM(COALESCE(ST.TRANSFER_VALUE,0)) AMOUNT,
	SUM(COALESCE(ST.SENDER_TRANSFER_VALUE,0)) SENDER_DEBIT_AMOUNT,
	SUM(COALESCE(ST.RECEIVER_TRANSFER_VALUE,0)) RECEIVER_CREDIT_AMOUNT,
	ST.transfer_category
	FROM TRANSFER_ITEMS T1, TRANSFER_ITEMS T2, SUBSCRIBER_TRANSFERS ST
	WHERE
	ST.TRANSFER_DATE = AIV_DATE
	AND ST.TRANSFER_STATUS IN('200','206')
	AND	ST.TRANSFER_ID = T1.TRANSFER_ID
	AND ST.TRANSFER_ID = T2.TRANSFER_ID
	AND T1.USER_TYPE = 'SENDER'
	AND T2.USER_TYPE = 'RECEIVER'
	GROUP BY
	T1.SERVICE_CLASS_ID,
	T2.SERVICE_CLASS_ID,
	ST.TRANSFER_DATE,
	TO_CHAR(ST.TRANSFER_DATE_TIME,'HH24')::integer,
	ST.TRANSFER_STATUS,
	ST.SERVICE_TYPE,
	ST.SUB_SERVICE,
	ST.transfer_category;

BEGIN

	FOR htr IN HOURLY_TRANSACTION_CURSOR(aiv_Date) LOOP
	ln_success_count	       :=0;
	ln_failure_count	   	   :=0;
	ln_success_amount	 	   :=0;
	ln_failure_amount	   	   :=0;
	ln_sender_debit_amount	   :=0;
	ln_receiver_credit_amount  :=0;

	IF htr.STATUS = '200' THEN
		ln_success_count	      := htr.COUNT;
		ln_success_amount	      := htr.AMOUNT;
		ln_sender_debit_amount	  := htr.SENDER_DEBIT_AMOUNT;
		ln_receiver_credit_amount := htr.RECEIVER_CREDIT_AMOUNT;
	ELSE
		ln_failure_count	      := htr.COUNT;
		ln_failure_amount	  	  := htr.AMOUNT;
	END IF;

	BEGIN
	UPDATE HOURLY_TRANSACTION_SUMMARY
	SET
		SUCCESS_COUNT			 =SUCCESS_COUNT +ln_success_count,
		FAILURE_COUNT			 =FAILURE_COUNT	+ ln_failure_count,
		SUCCESS_AMT				 =SUCCESS_AMT+ln_success_amount,
		FAILURE_AMT				 =FAILURE_AMT +ln_failure_amount,
		SENDER_DEBIT_AMOUNT		 =SENDER_DEBIT_AMOUNT + ln_sender_debit_amount,
		RECEIVER_CREDIT_AMOUNT	 =RECEIVER_CREDIT_AMOUNT + ln_receiver_credit_amount
		WHERE TRANS_DATE 					   = htr.TRANS_DATE AND
		      TRANS_HOUR		       		   = htr.TRANS_HOUR AND
		      SENDER_SERVICE_CLASS             = htr.SENDER_SERVICE_CLASS AND
		      RECEIVER_SERVICE_CLASS           = htr.RECEIVER_SERVICE_CLASS AND
		      SERVICE                          = htr.SERVICE AND
  		      SUB_SERVICE                      = htr.SUB_SERVICE AND
			  TRANSFER_CATEGORY				   = htr.transfer_category;
	if not found  THEN
	RAISE NOTICE '%','Isert ino hurly_transaction_summary';
		INSERT INTO HOURLY_TRANSACTION_SUMMARY
			(trans_date,
			trans_hour,
			sender_service_class,
			receiver_service_class,
			service,
			success_count,
			failure_count,
			success_amt,
			failure_amt,
			sender_debit_amount,
			receiver_credit_amount,
			sub_service,
			transfer_category)
		values (htr.trans_date,
			htr.trans_hour,
			htr.sender_service_class,
			htr.receiver_service_class,
			htr.service,
			ln_success_count,
			ln_failure_count,
			ln_success_amount,
			ln_failure_amount,
			ln_sender_debit_amount,
			ln_receiver_credit_amount,
			htr.sub_service,
			htr.transfer_category);

	END if;
	EXCEPTION
			 WHEN OTHERS THEN
			 	  RAISE NOTICE '%','EXCEPTION while Updating/Inserting in HOURLY_TRANSACTION_SUMMARY ='||sqlerrm;
			   	  v_messageForLog:='Error while Updating/Inserting record in HOURLY_TRANSACTION_SUMMARY, Date:'||htr.trans_date||' Hour:'||htr.trans_hour;
		       	  v_sqlerrMsgForLog:=sqlerrm;
			  	 RAISE EXCEPTION  using errcode = 'ERR05';
	END;
    END LOOP;

EXCEPTION
	when sqlstate 'ERR05' then
		 --IF HOURLY_TRANSACTION_CURSOR%ISOPEN THEN
      	 	 --CLOSE HOURLY_TRANSACTION_CURSOR;
		 --END IF;
		 RAISE NOTICE '%','EXCEPTION while Updating/Inserting in HOURLY_TRANSACTION_SUMMARY';
		RAISE EXCEPTION  using errcode = 'ERR05';
		
	WHEN OTHERS THEN
		--IF HOURLY_TRANSACTION_CURSOR%ISOPEN THEN
		--CLOSE HOURLY_TRANSACTION_CURSOR;
		--END IF;
		RAISE NOTICE '%','OTHERS EXCEPTION ='||sqlerrm;
		v_messageForLog:='Exception while inserting/updating record in HOURLY_TRANSACTION_SUMMARY';
		v_sqlerrMsgForLog:=sqlerrm;
		RAISE EXCEPTION  using errcode = 'ERR05';
		
END ;	
	
$$;


ALTER FUNCTION pretupsdatabase.mis_summary_report_sp_update_hourly_trn_summary(aiv_date timestamp without time zone, v_nullvalue character varying, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_summary_report_sp_update_monthly_trn_summary(timestamp without time zone, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_summary_report_sp_update_monthly_trn_summary(aiv_date timestamp without time zone, v_nullvalue character varying, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE
	ln_success_count			MONTHLY_TRANSACTION_SUMMARY.SUCCESS_COUNT%TYPE=0;
	ln_failure_count			MONTHLY_TRANSACTION_SUMMARY.FAILURE_COUNT%TYPE=0;
	ln_success_amount			MONTHLY_TRANSACTION_SUMMARY.SUCCESS_AMT%TYPE=0;
	ln_failure_amount			MONTHLY_TRANSACTION_SUMMARY.FAILURE_AMT%TYPE=0;
	ln_sender_processing_fee	MONTHLY_TRANSACTION_SUMMARY.SENDER_PROCESSING_FEE%TYPE=0;
	ln_receiver_processing_fee	MONTHLY_TRANSACTION_SUMMARY.RECEIVER_PROCESSING_FEE%TYPE=0;
	ln_sender_tax1_amount		MONTHLY_TRANSACTION_SUMMARY.SENDER_TAX1_AMOUNT%TYPE=0;
	ln_sender_tax2_amount		MONTHLY_TRANSACTION_SUMMARY.SENDER_TAX2_AMOUNT%TYPE=0;
	ln_receiver_tax1_amount		MONTHLY_TRANSACTION_SUMMARY.RECEIVER_TAX1_AMOUNT%TYPE=0;
	ln_receiver_tax2_amount		MONTHLY_TRANSACTION_SUMMARY.RECEIVER_TAX2_AMOUNT%TYPE=0;
	ln_sender_debit_amount		MONTHLY_TRANSACTION_SUMMARY.SENDER_DEBIT_AMOUNT%TYPE=0;
	ln_receiver_credit_amount	MONTHLY_TRANSACTION_SUMMARY.RECEIVER_CREDIT_AMOUNT%TYPE=0;
	ln_receiver_bonus_amount	MONTHLY_TRANSACTION_SUMMARY.bonus_amount%TYPE=0;
	ln_receiver_validity		MONTHLY_TRANSACTION_SUMMARY.validity%TYPE=0;
	ln_receiver_bonus_validity	MONTHLY_TRANSACTION_SUMMARY.bonus_validity%TYPE=0;
	
	---Cursor Declaration
	declare MONTHLY_TRANSACTION_CURSOR CURSOR(aiv_Date Timestamp(0)) IS
	SELECT
	TO_CHAR(TRANS_DATE, 'MM')::integer AS MONTH,
	TO_DATE(TO_CHAR(TRANS_DATE, 'MM/YYYY'), 'MM/YYYY') MONTH_YEAR,
	SENDER_SERVICE_CLASS,
	RECEIVER_SERVICE_CLASS,
	SERVICE,
	SUB_SERVICE,
	SENDER_NETWORK_CODE,
	RECEIVER_NETWORK_CODE,
	SUCCESS_COUNT,
	FAILURE_COUNT,
	SUCCESS_AMT,
	FAILURE_AMT,
	SENDER_PROCESSING_FEE,
	RECEIVER_PROCESSING_FEE,
	SENDER_TAX1_AMOUNT,
	SENDER_TAX2_AMOUNT,
	RECEIVER_TAX1_AMOUNT,
	RECEIVER_TAX2_AMOUNT,
	SENDER_DEBIT_AMOUNT,
	RECEIVER_CREDIT_AMOUNT,
	BONUS_AMOUNT,
	VALIDITY,
	BONUS_VALIDITY,
	TRANSFER_CATEGORY
	FROM DAILY_TRANSACTION_SUMMARY
	WHERE TRANS_DATE = aiv_Date;


BEGIN

	FOR mtr IN MONTHLY_TRANSACTION_CURSOR(aiv_Date) LOOP
	ln_success_count	       :=0;
	ln_failure_count	       :=0;
	ln_success_amount	       :=0;
	ln_failure_amount	       :=0;
	ln_sender_processing_fee   :=0;
	ln_receiver_processing_fee :=0;
	ln_sender_tax1_amount	   :=0;
	ln_sender_tax2_amount	   :=0;
	ln_receiver_tax1_amount	   :=0;
	ln_receiver_tax2_amount	   :=0;
	ln_sender_debit_amount	   :=0;
	ln_receiver_credit_amount  :=0;
	ln_receiver_bonus_amount	:=0;
	ln_receiver_validity		:=0;
	ln_receiver_bonus_validity	:=0;


	ln_success_count		    :=mtr.SUCCESS_COUNT;
	ln_success_amount		    :=mtr.SUCCESS_AMT;
	ln_sender_processing_fee	:=mtr.SENDER_PROCESSING_FEE;
	ln_receiver_processing_fee	:=mtr.RECEIVER_PROCESSING_FEE;
	ln_sender_tax1_amount		:=mtr.SENDER_TAX1_AMOUNT;
	ln_sender_tax2_amount		:=mtr.SENDER_TAX2_AMOUNT;
	ln_receiver_tax1_amount		:=mtr.RECEIVER_TAX1_AMOUNT;
	ln_receiver_tax2_amount		:=mtr.RECEIVER_TAX2_AMOUNT;
	ln_sender_debit_amount		:=mtr.SENDER_DEBIT_AMOUNT;
	ln_receiver_credit_amount	:=mtr.RECEIVER_CREDIT_AMOUNT;
	ln_failure_count		    :=mtr.FAILURE_COUNT;
	ln_failure_amount		    :=mtr.FAILURE_AMT;
	ln_receiver_bonus_amount	:=mtr.BONUS_AMOUNT;
	ln_receiver_validity		:=mtr.VALIDITY;
	ln_receiver_bonus_validity	:=mtr.BONUS_VALIDITY;
	
	BEGIN
	UPDATE MONTHLY_TRANSACTION_SUMMARY
	SET
		SUCCESS_COUNT		    =SUCCESS_COUNT + ln_success_count,
		FAILURE_COUNT			=FAILURE_COUNT + ln_failure_count,
		SUCCESS_AMT				=SUCCESS_AMT + ln_success_amount,
		FAILURE_AMT				=FAILURE_AMT + ln_failure_amount,
		SENDER_PROCESSING_FEE	=SENDER_PROCESSING_FEE	+ ln_sender_processing_fee,
		RECEIVER_PROCESSING_FEE	=RECEIVER_PROCESSING_FEE + ln_receiver_processing_fee,
		SENDER_TAX1_AMOUNT		=SENDER_TAX1_AMOUNT	+ ln_sender_tax1_amount,
		SENDER_TAX2_AMOUNT		=SENDER_TAX2_AMOUNT	+ ln_sender_tax2_amount,
		RECEIVER_TAX1_AMOUNT	=RECEIVER_TAX1_AMOUNT + ln_receiver_tax1_amount,
		RECEIVER_TAX2_AMOUNT	=RECEIVER_TAX2_AMOUNT + ln_receiver_tax2_amount,
		SENDER_DEBIT_AMOUNT		=SENDER_DEBIT_AMOUNT + ln_sender_debit_amount,
		RECEIVER_CREDIT_AMOUNT	=RECEIVER_CREDIT_AMOUNT	+ ln_receiver_credit_amount,
		BONUS_AMOUNT			=BONUS_AMOUNT + ln_receiver_bonus_amount,
		VALIDITY				=VALIDITY + ln_receiver_validity,
		BONUS_VALIDITY			=BONUS_VALIDITY + ln_receiver_bonus_validity
		WHERE
		TRANS_MONTH				= mtr.month AND
		TRANS_MONTH_YEAR		= mtr.month_year AND
		SENDER_SERVICE_CLASS	= mtr.sender_service_class AND
		RECEIVER_SERVICE_CLASS	= mtr.receiver_service_class AND
		SERVICE                 = mtr.service AND
		SUB_SERVICE             = mtr.sub_service AND
		SENDER_NETWORK_CODE     = mtr.sender_network_code AND
		RECEIVER_NETWORK_CODE   = mtr.receiver_network_code and
		TRANSFER_CATEGORY		= mtr.transfer_category;
	if NOT FOUND THEN
	INSERT INTO MONTHLY_TRANSACTION_SUMMARY
		(trans_month,
	    trans_month_year,
		sender_service_class,
		receiver_service_class,
		service,
		success_count,
		failure_count,
		success_amt,
		failure_amt,
		sender_processing_fee,
		receiver_processing_fee,
		sender_tax1_amount,
		sender_tax2_amount,
		receiver_tax1_amount,
		receiver_tax2_amount,
		sender_debit_amount,
		receiver_credit_amount,
		sender_network_code,
		receiver_network_code,
		created_on,
		sub_service,
		bonus_amount,
		validity,
		bonus_validity,
		transfer_category)
	values (mtr.month,
	    mtr.month_year,
		mtr.sender_service_class,
		mtr.receiver_service_class,
		mtr.service,
		ln_success_count,
		ln_failure_count,
		ln_success_amount,
		ln_failure_amount,
		ln_sender_processing_fee,
		ln_receiver_processing_fee,
		ln_sender_tax1_amount,
		ln_sender_tax2_amount,
		ln_receiver_tax1_amount,
		ln_receiver_tax2_amount,
		ln_sender_debit_amount,
		ln_receiver_credit_amount,
		mtr.sender_network_code,
		mtr.receiver_network_code,
		date_trunc('day',current_timestamp::timestamp),
		mtr.sub_service,
		ln_receiver_bonus_amount,
		ln_receiver_validity,
		ln_receiver_bonus_validity,
		mtr.transfer_category);
	END if;
	EXCEPTION
			 WHEN OTHERS THEN
			 	  RAISE NOTICE '%','EXCEPTION while Updating/Inserting in MONTHLY_TRANSACTION_SUMMARY ='||sqlerrm;
			   	  v_messageForLog:='Error while Updating/Inserting record in MONTHLY_TRANSACTION_SUMMARY, Month-Year:'||mtr.month_year;
		       	  v_sqlerrMsgForLog:=sqlerrm;
			  	  RAISE EXCEPTION  using errcode = 'ERR05';
	END;
	END LOOP;

EXCEPTION
	when sqlstate 'ERR05' then
		--IF MONTHLY_TRANSACTION_CURSOR%ISOPEN THEN
      	   --CLOSE MONTHLY_TRANSACTION_CURSOR;
		--END IF;
		RAISE NOTICE '%','EXCEPTION while Updating/Inserting in MONTHLY_TRANSACTION_SUMMARY';
		RAISE EXCEPTION  using errcode = 'ERR05';
	WHEN OTHERS THEN
		--IF MONTHLY_TRANSACTION_CURSOR%ISOPEN THEN
      	   --CLOSE MONTHLY_TRANSACTION_CURSOR;
		--END IF;
		RAISE NOTICE '%','OTHERS EXCEPTION ='||sqlerrm;
		v_messageForLog:='Exception while Updating/Inserting record in MONTHLY_TRANSACTION_SUMMARY';
		v_sqlerrMsgForLog:=sqlerrm;
		RAISE EXCEPTION  using errcode = 'ERR05';

END ;	
	
$$;


ALTER FUNCTION pretupsdatabase.mis_summary_report_sp_update_monthly_trn_summary(aiv_date timestamp without time zone, v_nullvalue character varying, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_summary_report_sp_update_p2p_sub_summary(timestamp without time zone, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_summary_report_sp_update_p2p_sub_summary(aiv_date timestamp without time zone, v_nullvalue character varying, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE
	ln_success_count		    P2P_SUBSCRIBER_SUMMARY.SUCCESS_COUNT%TYPE=0;
	ln_failure_count		    P2P_SUBSCRIBER_SUMMARY.FAILURE_COUNT%TYPE=0;
	ln_success_amount		    P2P_SUBSCRIBER_SUMMARY.SUCCESS_AMT%TYPE=0;
	ln_failure_amount		    P2P_SUBSCRIBER_SUMMARY.FAILURE_AMT%TYPE=0;
	ln_sender_processing_fee	P2P_SUBSCRIBER_SUMMARY.SENDER_PROCESSING_FEE%TYPE=0;
	ln_receiver_processing_fee	P2P_SUBSCRIBER_SUMMARY.RECEIVER_PROCESSING_FEE%TYPE=0;
	ln_sender_tax1_amount		P2P_SUBSCRIBER_SUMMARY.SENDER_TAX1_AMOUNT%TYPE=0;
	ln_sender_tax2_amount		P2P_SUBSCRIBER_SUMMARY.SENDER_TAX2_AMOUNT%TYPE=0;
	ln_receiver_tax1_amount		P2P_SUBSCRIBER_SUMMARY.RECEIVER_TAX1_AMOUNT%TYPE=0;
	ln_receiver_tax2_amount		P2P_SUBSCRIBER_SUMMARY.RECEIVER_TAX2_AMOUNT%TYPE=0;
	ln_sender_debit_amount		P2P_SUBSCRIBER_SUMMARY.SENDER_DEBIT_AMOUNT%TYPE=0;
	ln_receiver_credit_amount	P2P_SUBSCRIBER_SUMMARY.RECEIVER_CREDIT_AMOUNT%TYPE=0;
	ln_receiver_bonus_amount	P2P_SUBSCRIBER_SUMMARY.bonus_amount%TYPE=0;
	ln_receiver_validity		P2P_SUBSCRIBER_SUMMARY.validity%TYPE=0;
	ln_receiver_bonus_validity	P2P_SUBSCRIBER_SUMMARY.bonus_validity%TYPE=0;

        
	---Cursor Declaration
	declare P2P_SUBSCRIBER_CURSOR CURSOR(aiv_Date Timestamp(0)) IS
	SELECT /*+ INDEX(T1) INDEX(ST)*/ ST.SENDER_ID,
	COALESCE(T1.SERVICE_CLASS_ID,v_nullvalue) SENDER_SERVICE_CLASS,
	T1.SUBSCRIBER_TYPE,
	TO_CHAR(ST.TRANSFER_DATE, 'MM')::integer AS MONTH,
	TO_DATE(TO_CHAR(ST.TRANSFER_DATE, 'MM/YYYY'), 'MM/YYYY') MONTH_YEAR,
	ST.TRANSFER_STATUS STATUS,
	COUNT(ST.TRANSFER_DATE) COUNT,
	ST.SERVICE_TYPE SERVICE,
	ST.SUB_SERVICE,
	ST.SENDER_MSISDN,
	ST.NETWORK_CODE,
	SUM(COALESCE(ST.TRANSFER_VALUE,0)) AMOUNT,
	SUM(COALESCE(ST.SENDER_ACCESS_FEE,0)) SENDER_PROCESSING_FEE,
	SUM(COALESCE(ST.RECEIVER_ACCESS_FEE,0)) RECEIVER_PROCESSING_FEE,
	SUM(COALESCE(ST.SENDER_TAX1_VALUE,0)) SENDER_TAX1_AMOUNT,
	SUM(COALESCE(ST.SENDER_TAX2_VALUE,0)) SENDER_TAX2_AMOUNT,
	SUM(COALESCE(ST.RECEIVER_TAX1_VALUE,0)) RECEIVER_TAX1_AMOUNT,
	SUM(COALESCE(ST.RECEIVER_TAX2_VALUE,0)) RECEIVER_TAX2_AMOUNT,
	SUM(COALESCE(ST.SENDER_TRANSFER_VALUE,0)) SENDER_DEBIT_AMOUNT,
	SUM(COALESCE(ST.RECEIVER_TRANSFER_VALUE,0)) RECEIVER_CREDIT_AMOUNT,
	SUM(COALESCE(ST.RECEIVER_BONUS_VALUE,0)) RECEIVER_BONUS_AMOUNT,
	SUM(COALESCE(ST.RECEIVER_VALIDITY,0)) RECEIVER_VALIDITY,
	SUM(COALESCE(ST.RECEIVER_BONUS_VALIDITY,0)) RECEIVER_BONUS_VALIDITY,
	ST.TRANSFER_CATEGORY
	FROM TRANSFER_ITEMS T1, SUBSCRIBER_TRANSFERS ST
	WHERE
	ST.TRANSFER_DATE = aiv_Date AND
	ST.TRANSFER_STATUS IN('200','206') AND
	ST.TRANSFER_ID = T1.TRANSFER_ID AND
	T1.USER_TYPE = 'SENDER'
	GROUP BY
	ST.SENDER_ID,
	T1.SERVICE_CLASS_ID,
	T1.SUBSCRIBER_TYPE,
	ST.TRANSFER_DATE,
	ST.TRANSFER_STATUS,
	ST.SERVICE_TYPE,
	ST.SUB_SERVICE,
	ST.NETWORK_CODE,
	ST.SENDER_MSISDN,
	ST.TRANSFER_CATEGORY;


BEGIN

	FOR str IN P2P_SUBSCRIBER_CURSOR(aiv_date) LOOP
	ln_success_count	       :=0;
	ln_failure_count	       :=0;
	ln_success_amount	       :=0;
	ln_failure_amount	       :=0;
	ln_sender_processing_fee   :=0;
	ln_receiver_processing_fee :=0;
	ln_sender_tax1_amount	   :=0;
	ln_sender_tax2_amount	   :=0;
	ln_receiver_tax1_amount	   :=0;
	ln_receiver_tax2_amount	   :=0;
	ln_sender_debit_amount	   :=0;
	ln_receiver_credit_amount  :=0;
	ln_receiver_bonus_amount	:=0;
	ln_receiver_validity		:=0;
	ln_receiver_bonus_validity	:=0;


	IF str.status = '200' THEN
		ln_success_count	       :=str.COUNT;
		ln_success_amount	       :=str.AMOUNT;
		ln_sender_processing_fee   :=str.SENDER_PROCESSING_FEE;
		ln_receiver_processing_fee :=str.RECEIVER_PROCESSING_FEE;
		ln_sender_tax1_amount	   :=str.SENDER_TAX1_AMOUNT;
		ln_sender_tax2_amount	   :=str.SENDER_TAX2_AMOUNT;
		ln_receiver_tax1_amount	   :=str.RECEIVER_TAX1_AMOUNT;
		ln_receiver_tax2_amount	   :=str.RECEIVER_TAX2_AMOUNT;
		ln_sender_debit_amount	   :=str.SENDER_DEBIT_AMOUNT;
		ln_receiver_credit_amount  :=str.RECEIVER_CREDIT_AMOUNT;
		ln_receiver_bonus_amount	:=str.RECEIVER_BONUS_AMOUNT;
		ln_receiver_validity		:=str.RECEIVER_VALIDITY;
		ln_receiver_bonus_validity	:=str.RECEIVER_BONUS_VALIDITY;

	ELSE
		ln_failure_count   := str.COUNT;
		ln_failure_amount  := str.AMOUNT;
	END IF;

	BEGIN
	UPDATE P2P_SUBSCRIBER_SUMMARY
	SET
		SUCCESS_COUNT		    =SUCCESS_COUNT + ln_success_count,
		FAILURE_COUNT		    =FAILURE_COUNT + ln_failure_count,
		SUCCESS_AMT		        =SUCCESS_AMT + ln_success_amount,
		FAILURE_AMT		        =FAILURE_AMT + ln_failure_amount,
		SENDER_PROCESSING_FEE	=SENDER_PROCESSING_FEE	+ ln_sender_processing_fee,
		RECEIVER_PROCESSING_FEE	=RECEIVER_PROCESSING_FEE + ln_receiver_processing_fee,
		SENDER_TAX1_AMOUNT	    =SENDER_TAX1_AMOUNT	+ ln_sender_tax1_amount,
		SENDER_TAX2_AMOUNT	    =SENDER_TAX2_AMOUNT	+ ln_sender_tax2_amount,
		RECEIVER_TAX1_AMOUNT	=RECEIVER_TAX1_AMOUNT + ln_receiver_tax1_amount,
		RECEIVER_TAX2_AMOUNT	=RECEIVER_TAX2_AMOUNT + ln_receiver_tax2_amount,
		SENDER_DEBIT_AMOUNT	=SENDER_DEBIT_AMOUNT + ln_sender_debit_amount,
		RECEIVER_CREDIT_AMOUNT	=RECEIVER_CREDIT_AMOUNT	+ ln_receiver_credit_amount,
		BONUS_AMOUNT			=BONUS_AMOUNT	+ ln_receiver_bonus_amount,
		VALIDITY				=VALIDITY	+ ln_receiver_validity,
		BONUS_VALIDITY			=BONUS_VALIDITY	+ ln_receiver_bonus_validity
		WHERE
		MONTH			        = str.month AND
		MONTH_YEAR		        = str.month_year AND
		USER_ID	          		= str.sender_id AND
		SUBSCRIBER_TYPE		    = str.subscriber_type AND
		SENDER_SERVICE_CLASS	= str.sender_service_class AND
		SERVICE                 = str.service AND
		SUB_SERVICE             = str.sub_service AND
		SENDER_MSISDN           = str.sender_msisdn AND
		SENDER_NETWORK_CODE     = str.network_code AND
		TRANSFER_CATEGORY		= str.transfer_category;
if NOT FOUND THEN
	INSERT INTO P2P_SUBSCRIBER_SUMMARY
	    (user_id,
		month,
		month_year,
		subscriber_type,
		sender_service_class,
		service,
		success_count,
		failure_count,
		success_amt,
		failure_amt,
		sender_processing_fee,
		receiver_processing_fee,
		sender_tax1_amount,
		sender_tax2_amount,
		receiver_tax1_amount,
		receiver_tax2_amount,
		sender_debit_amount,
		receiver_credit_amount,
		sender_network_code,
		created_on,
		sender_msisdn,
		sub_service,
		bonus_amount,
		validity,
		bonus_validity,
		transfer_category)
	values (str.sender_id,
		str.month,
		str.month_year,
		str.subscriber_type,
		str.sender_service_class,
		str.service,
		ln_success_count,
		ln_failure_count,
		ln_success_amount,
		ln_failure_amount,
		ln_sender_processing_fee,
		ln_receiver_processing_fee,
		ln_sender_tax1_amount,
		ln_sender_tax2_amount,
		ln_receiver_tax1_amount,
		ln_receiver_tax2_amount,
		ln_sender_debit_amount,
		ln_receiver_credit_amount,
		str.network_code,
		date_trunc('day',current_timestamp::timestamp),
		str.sender_msisdn,
		str.sub_service,
		ln_receiver_bonus_amount,
		ln_receiver_validity,
		ln_receiver_bonus_validity,
		str.transfer_category);
	END if;
	EXCEPTION
			 WHEN OTHERS THEN
			 	  RAISE NOTICE '%','EXCEPTION while Updating/Inserting in P2P_SUBSCRIBER_SUMMARY ='||sqlerrm;
			   	  v_messageForLog:='Error while Updating/Inserting record in P2P_SUBSCRIBER_SUMMARY, User: '||str.sender_id || ' Month/Year: ' ||str.month_year;
		       	  v_sqlerrMsgForLog:=sqlerrm;
			  	  RAISE EXCEPTION  using errcode = 'ERR05';
	END;
	END LOOP;

EXCEPTION
	when sqlstate 'ERR05' then
		--IF P2P_SUBSCRIBER_CURSOR%ISOPEN THEN
      	   --CLOSE P2P_SUBSCRIBER_CURSOR;
		--END IF;
		RAISE NOTICE '%','EXCEPTION while Updating/Inserting in P2P_SUBSCRIBER_SUMMARY';
		RAISE EXCEPTION  using errcode = 'ERR05';
	WHEN OTHERS THEN
		--IF P2P_SUBSCRIBER_CURSOR%ISOPEN THEN
      	   --CLOSE P2P_SUBSCRIBER_CURSOR;
		--END IF;
		RAISE NOTICE '%','OTHERS EXCEPTION ='||sqlerrm;
		v_messageForLog:='Exception while Updating/Inserting record in P2P_SUBSCRIBER_SUMMARY';
		v_sqlerrMsgForLog:=sqlerrm;
		RAISE EXCEPTION  using errcode = 'ERR05';
END ;	
	
$$;


ALTER FUNCTION pretupsdatabase.mis_summary_report_sp_update_p2p_sub_summary(aiv_date timestamp without time zone, v_nullvalue character varying, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: mis_summary_report_sp_update_p2p_success_failure(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION mis_summary_report_sp_update_p2p_success_failure(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE
    in_count				   P2P_DAILY_FAILURE_DETAILS.count%TYPE;
    in_amount				   P2P_DAILY_FAILURE_DETAILS.amount%TYPE;
    in_trans_count             P2P_SUMMARY_DAILY.total_trans_count%TYPE;
    in_trans_amount            P2P_SUMMARY_DAILY.total_trans_amount%TYPE;
    in_fail_count              P2P_SUMMARY_DAILY.fail_count%TYPE;
    in_fail_amount             P2P_SUMMARY_DAILY.fail_amount%TYPE;
   --Cursor Declaration
   declare SUCCESS_FAILURE_CURSOR CURSOR(aiv_date Timestamp(0)) IS
   	 SELECT transfer_date,network_code,receiver_network_code,
	 service_type,sub_service,transfer_status,error_code,
	 SUM(transfer_value) amount,
	 COUNT(transfer_date) count,
	 transfer_category
	 FROM subscriber_transfers
	 WHERE transfer_date=aiv_date
	 GROUP BY transfer_date,network_code,receiver_network_code,
	 service_type,sub_service,transfer_status,error_code,transfer_category;

BEGIN

   FOR tr IN SUCCESS_FAILURE_CURSOR (aiv_date)
   LOOP

    in_count:=0;
    in_amount:=0;
    in_trans_count:=0;
    in_trans_amount:=0;
    in_fail_count:=0;
    in_fail_amount:=0;

      IF tr.transfer_status = '200' THEN
	  	 in_trans_count := tr.count;
      	 in_trans_amount := tr.amount;
      ELSIF tr.transfer_status = '206' THEN
         in_fail_count := tr.count;
         in_fail_amount := tr.amount;
      END IF;

      BEGIN
	  	   UPDATE p2p_summary_daily SET
		   TOTAL_TRANS_COUNT = TOTAL_TRANS_COUNT + in_trans_count,
		   TOTAL_TRANS_AMOUNT = TOTAL_TRANS_AMOUNT + in_trans_amount,
		   FAIL_COUNT = FAIL_COUNT + in_fail_count,
		   FAIL_AMOUNT = FAIL_AMOUNT + in_fail_amount
		   WHERE TRANS_DATE			   		=tr.transfer_date
		   AND   SENDER_NETWORK_CODE   		=tr.network_code
		   AND	 RECEIVER_NETWORK_CODE 		=tr.receiver_network_code
		   AND   SERVICE_TYPE 		   		=tr.service_type
		   AND   SUB_SERVICE_TYPE 	   		=tr.sub_service
		   AND TRANSFER_CATEGORY			=tr.transfer_category;


         if NOT FOUND THEN
            INSERT INTO P2P_SUMMARY_DAILY
				 (trans_date, sender_network_code,
				 receiver_network_code,service_type,
				 sub_service_type, total_trans_count,
				 total_trans_amount,fail_count,fail_amount,transfer_category)
                 values (tr.transfer_date, tr.network_code,
				 tr.receiver_network_code,tr.service_type,
				 tr.sub_service, in_trans_count,
				 in_trans_amount,in_fail_count,in_fail_amount,tr.transfer_category);
         END if;
      EXCEPTION
         WHEN OTHERS
         THEN
            RAISE NOTICE '%','EXCEPTION while Updating/Inserting in P2P_SUMMARY_DAILY ='|| SQLERRM;
            v_messageforlog :='Error while Updating/Inserting record in P2P_SUMMARY_DAILY, Date:'|| tr.transfer_date;
            v_sqlerrmsgforlog := SQLERRM;
      RAISE EXCEPTION  using errcode = 'ERR05';
      END;

	  IF tr.transfer_status='206' THEN
	  	 in_count:=tr.count;
		 in_amount:=tr.amount;
      BEGIN
       UPDATE P2P_DAILY_FAILURE_DETAILS SET
	   		 count=count+in_count,
			 amount=amount+in_amount
			 WHERE trans_date			   =tr.transfer_date
			 AND service_type			   =tr.service_type
			 AND sub_service_type		   =tr.sub_service
			 AND error_code				   =tr.error_code
			 AND sender_network_code	   =tr.network_code
			 AND receiver_network_code	   =tr.receiver_network_code
			 AND transfer_category		   =tr.transfer_category;


		 if NOT FOUND
         THEN
            INSERT INTO P2P_DAILY_FAILURE_DETAILS
			     (trans_date,service_type,sub_service_type,
				 error_code,count,amount,sender_network_code,
				 receiver_network_code,transfer_category)
                 values (tr.transfer_date,tr.service_type,tr.sub_service,
				 tr.error_code,in_count,in_amount,tr.network_code,
				 tr.receiver_network_code,tr.transfer_category);
         END if;
      EXCEPTION
         WHEN OTHERS
         THEN
            RAISE NOTICE '%','EXCEPTION  while Updating/Inserting in P2P_DAILY_FAILURE_DETAILS ='|| SQLERRM;
            v_messageforlog :='Error while Updating/Inserting record in P2P_DAILY_FAILURE_DETAILS, Date:'|| tr.transfer_date;
            v_sqlerrmsgforlog := SQLERRM;
			RAISE EXCEPTION  using errcode = 'ERR05';
      END;
	  END IF;
   END LOOP;
EXCEPTION
     when sqlstate 'ERR05' then
         RAISE NOTICE '%','EXCEPTION IN p2p daily failure details';
         RAISE EXCEPTION  using errcode = 'ERR01';
      WHEN OTHERS
      THEN
         RAISE NOTICE '%', 'OTHERS EXCEPTION =' || SQLERRM;
         v_messageforlog :='Exception while inserting/updating record for failure data';
         v_sqlerrmsgforlog := SQLERRM;
         RAISE EXCEPTION  using errcode = 'ERR01';

--END PKG_MIS_SUMMARY_REPORTS;
END ;	
	
$$;


ALTER FUNCTION pretupsdatabase.mis_summary_report_sp_update_p2p_success_failure(aiv_date timestamp without time zone, OUT v_messageforlog character varying, OUT v_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: movenetworkdailystocksdata(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION movenetworkdailystocksdata() RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
 stockData_cur CURSOR FOR SELECT * FROM network_daily_stocks;
 oldTable_row network_daily_stocks%ROWTYPE;    
 newTable_row network_daily_stocks_new%ROWTYPE;

BEGIN
 
       
  FOR oldTable_row in stockData_cur
    LOOP
       newTable_row.network_code := oldTable_row.network_code;
       newTable_row.network_code_for := oldTable_row.network_code_for;
       newTable_row.product_code := oldTable_row.product_code;
       newTable_row.wallet_date := oldTable_row.wallet_date;
       newTable_row.created_on := oldTable_row.created_on;
       newTable_row.creation_type := oldTable_row.creation_type;
       newTable_row.wallet_type := 'SAL';
       newTable_row.wallet_created := oldTable_row.wallet_created;
       newTable_row.wallet_returned := oldTable_row.wallet_returned;
       newTable_row.wallet_sold := oldTable_row.wallet_sold;
       newTable_row.wallet_balance := oldTable_row.wallet_balance;
       newTable_row.last_txn_no := oldTable_row.last_txn_no;
       newTable_row.last_txn_balance := oldTable_row.last_txn_balance;
       newTable_row.last_txn_type := oldTable_row.last_txn_type;
       newTable_row.previous_balance := oldTable_row.previous_balance;
         BEGIN
            insert into network_daily_stocks_new values(newTable_row.wallet_date,newTable_row.wallet_type,newTable_row.network_code,newTable_row.network_code_for,newTable_row.product_code,newTable_row.wallet_created,newTable_row.wallet_returned,newTable_row.wallet_balance,newTable_row.wallet_sold,newTable_row.last_txn_no,newTable_row.last_txn_type,newTable_row.last_txn_balance,newTable_row.previous_balance,newTable_row.created_on,newTable_row.creation_type);         
         END;         
           newTable_row.network_code := oldTable_row.network_code;
       newTable_row.network_code_for := oldTable_row.network_code_for;
       newTable_row.product_code := oldTable_row.product_code;
       newTable_row.wallet_date := oldTable_row.wallet_date;
       newTable_row.created_on := oldTable_row.created_on;
       newTable_row.creation_type := oldTable_row.creation_type;
       newTable_row.wallet_type := 'FOC';
       newTable_row.wallet_created := oldTable_row.wallet_created;
       newTable_row.wallet_returned := oldTable_row.wallet_returned;
       newTable_row.wallet_sold := oldTable_row.wallet_sold;
       newTable_row.wallet_balance := oldTable_row.wallet_balance;
       newTable_row.last_txn_no := oldTable_row.last_txn_no;
       newTable_row.last_txn_balance := oldTable_row.last_txn_balance;
       newTable_row.last_txn_type := oldTable_row.last_txn_type;
       newTable_row.previous_balance := oldTable_row.previous_balance;
         BEGIN
            insert into network_daily_stocks_new values(newTable_row.wallet_date,newTable_row.wallet_type,newTable_row.network_code,newTable_row.network_code_for,newTable_row.product_code,newTable_row.wallet_created,newTable_row.wallet_returned,newTable_row.wallet_balance,newTable_row.wallet_sold,newTable_row.last_txn_no,newTable_row.last_txn_type,newTable_row.last_txn_balance,newTable_row.previous_balance,newTable_row.created_on,newTable_row.creation_type);         
         END;
         newTable_row.network_code := oldTable_row.network_code;
       newTable_row.network_code_for := oldTable_row.network_code_for;
       newTable_row.product_code := oldTable_row.product_code;
       newTable_row.wallet_date := oldTable_row.wallet_date;
       newTable_row.created_on := oldTable_row.created_on;
       newTable_row.creation_type := oldTable_row.creation_type;
       newTable_row.wallet_type := 'INC';
       newTable_row.wallet_created := oldTable_row.wallet_created;
       newTable_row.wallet_returned := oldTable_row.wallet_returned;
       newTable_row.wallet_sold := oldTable_row.wallet_sold;
       newTable_row.wallet_balance := oldTable_row.wallet_balance;
       newTable_row.last_txn_no := oldTable_row.last_txn_no;
       newTable_row.last_txn_balance := oldTable_row.last_txn_balance;
       newTable_row.last_txn_type := oldTable_row.last_txn_type;
       newTable_row.previous_balance := oldTable_row.previous_balance;
         BEGIN
            insert into network_daily_stocks_new values(newTable_row.wallet_date,newTable_row.wallet_type,newTable_row.network_code,newTable_row.network_code_for,newTable_row.product_code,newTable_row.wallet_created,newTable_row.wallet_returned,newTable_row.wallet_balance,newTable_row.wallet_sold,newTable_row.last_txn_no,newTable_row.last_txn_type,newTable_row.last_txn_balance,newTable_row.previous_balance,newTable_row.created_on,newTable_row.creation_type);         
         END;
       
    END LOOP;
  /* commit; */
END;
$$;


ALTER FUNCTION pretupsdatabase.movenetworkdailystocksdata() OWNER TO pgdb;

--
-- Name: movenetworkstocksdata(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION movenetworkstocksdata() RETURNS void
    LANGUAGE plpgsql
    AS $$
declare 
 declare stockdata_cur cursor FOR  select * from network_stocks;
 oldtable_row network_stocks%rowtype;    
 newtable_row network_stocks_new%rowtype;

begin
 
       
  for oldtable_row in stockdata_cur
    loop
       newtable_row.network_code := oldtable_row.network_code;
           newtable_row.network_code_for := oldtable_row.network_code_for;
       newtable_row.product_code := oldtable_row.product_code;
           newtable_row.daily_stock_updated_on := oldtable_row.daily_stock_updated_on;
       newtable_row.created_on := oldtable_row.created_on;
       newtable_row.created_by := oldtable_row.created_by;
       newtable_row.modified_on := oldtable_row.modified_on;
       newtable_row.modified_by := oldtable_row.modified_by;       
       newtable_row.wallet_type := 'sal';
       newtable_row.wallet_created := oldtable_row.wallet_created;
       newtable_row.wallet_returned := oldtable_row.wallet_returned;
       newtable_row.wallet_sold := oldtable_row.wallet_sold;
       newtable_row.wallet_balance := oldtable_row.wallet_balance;
       newtable_row.last_txn_no := oldtable_row.last_txn_no;
       newtable_row.last_txn_balance := oldtable_row.last_txn_balance;
       newtable_row.last_txn_type := oldtable_row.last_txn_type;
       newtable_row.previous_balance := oldtable_row.previous_balance;
         begin
            insert into network_stocks_new values(newtable_row.network_code,newtable_row.network_code_for,newtable_row.product_code,newtable_row.wallet_type,newtable_row.wallet_created,newtable_row.wallet_returned,newtable_row.wallet_balance,newtable_row.wallet_sold,newtable_row.last_txn_no,newtable_row.last_txn_type,newtable_row.last_txn_balance,newtable_row.previous_balance,newtable_row.modified_by,newtable_row.modified_on,newtable_row.created_on,newtable_row.created_by,newtable_row.daily_stock_updated_on);         
         end;         
       newtable_row.wallet_type := 'foc';
       newtable_row.wallet_created := oldtable_row.wallet_created;
       newtable_row.wallet_returned := oldtable_row.wallet_returned;
       newtable_row.wallet_sold := oldtable_row.wallet_sold;
       newtable_row.wallet_balance := oldtable_row.wallet_balance;
       newtable_row.last_txn_no := oldtable_row.last_txn_no;
       newtable_row.last_txn_balance := oldtable_row.last_txn_balance;
       newtable_row.last_txn_type := oldtable_row.last_txn_type;
       newtable_row.previous_balance := oldtable_row.previous_balance;
          begin
            insert into network_stocks_new values(newtable_row.network_code,newtable_row.network_code_for,newtable_row.product_code,newtable_row.wallet_type,newtable_row.wallet_created,newtable_row.wallet_returned,newtable_row.wallet_balance,newtable_row.wallet_sold,newtable_row.last_txn_no,newtable_row.last_txn_type,newtable_row.last_txn_balance,newtable_row.previous_balance,newtable_row.modified_by,newtable_row.modified_on,newtable_row.created_on,newtable_row.created_by,newtable_row.daily_stock_updated_on);         
         end;
        newtable_row.wallet_type := 'inc';
       newtable_row.wallet_created := oldtable_row.wallet_created;
       newtable_row.wallet_returned := oldtable_row.wallet_returned;
       newtable_row.wallet_sold := oldtable_row.wallet_sold;
       newtable_row.wallet_balance := oldtable_row.wallet_balance;
       newtable_row.last_txn_no := oldtable_row.last_txn_no;
       newtable_row.last_txn_balance := oldtable_row.last_txn_balance;
       newtable_row.last_txn_type := oldtable_row.last_txn_type;
       newtable_row.previous_balance := oldtable_row.previous_balance;
          begin
            insert into network_stocks_new values(newtable_row.network_code,newtable_row.network_code_for,newtable_row.product_code,newtable_row.wallet_type,newtable_row.wallet_created,newtable_row.wallet_returned,newtable_row.wallet_balance,newtable_row.wallet_sold,newtable_row.last_txn_no,newtable_row.last_txn_type,newtable_row.last_txn_balance,newtable_row.previous_balance,newtable_row.modified_by,newtable_row.modified_on,newtable_row.created_on,newtable_row.created_by,newtable_row.daily_stock_updated_on);         
         end;
       
    end loop;
  /* commit; */
end;
$$;


ALTER FUNCTION pretupsdatabase.movenetworkstocksdata() OWNER TO pgdb;

--
-- Name: network_daily_closing_stock(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION network_daily_closing_stock(OUT rtn_message character varying, OUT rtn_messageforlog character varying, OUT rtn_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
declare 
/* ############## new procedure for network daily closing stock#####################
create table network_stock_temp
(
  process     varchar2(40 byte),
  start_time  date,
  end_time    date,
  status_log  varchar2(100 byte)
);   */
p_network_code network_stocks.network_code%type;
p_network_code_for network_stocks.network_code_for%type;
p_product_code network_stocks.product_code%type;

p_wallet_created NETWORK_STOCKS.WALLET_CREATED%TYPE;
p_wallet_returned NETWORK_STOCKS.WALLET_RETURNED%TYPE;
p_wallet NETWORK_STOCKS.wallet_balance%TYPE;
p_wallet_sold NETWORK_STOCKS.wallet_sold%TYPE;
p_last_txn_no NETWORK_STOCKS.last_txn_no%TYPE;
p_last_txn_type NETWORK_STOCKS.last_txn_type%TYPE;
p_last_txn_stock NETWORK_STOCKS.last_txn_balance%TYPE;
p_previous_stock NETWORK_STOCKS.previous_balance%TYPE;
p_wallet_type NETWORK_STOCKS.wallet_type%TYPE;
p_daily_stock_updated_on DATE;


q_created_on timestamp(0);
daydifference int = 0;
startcount smallint;
datecounter timestamp(0);




  declare network_stock_list_cur cursor for
    SELECT network_code,network_code_for,product_code,wallet_created,
        wallet_returned,wallet_balance,wallet_sold,last_txn_no,last_txn_type,last_txn_balance,previous_balance,
        wallet_type,daily_stock_updated_on
        FROM NETWORK_STOCKS
        WHERE date_trunc('day',daily_stock_updated_on::timestamp)<>date_trunc('day',current_timestamp::timestamp) FOR UPDATE;
  
       

begin



                        for network_stock_records in network_stock_list_cur
                         loop
                    p_network_code:=network_stock_records.network_code;
                    p_network_code_for:=network_stock_records.network_code_for;
                    p_product_code:=network_stock_records.product_code;
                    p_wallet_created:=network_stock_records.wallet_created;
                    p_wallet_returned:=network_stock_records.wallet_returned;
                    p_wallet:=network_stock_records.wallet_balance;
                    p_wallet_sold:=network_stock_records.wallet_sold;
                    p_last_txn_no:=network_stock_records.last_txn_no;
                    p_last_txn_type:=network_stock_records.last_txn_type;
                    p_last_txn_stock:=network_stock_records.last_txn_balance;
                    p_previous_stock:=network_stock_records.previous_balance;
                    p_wallet_type:=network_stock_records.wallet_type;
                    p_daily_stock_updated_on:=network_stock_records.daily_stock_updated_on;

                         begin
raise notice 'Inside begin....................!';
                                  q_created_on  :=current_timestamp;
                                  startcount := 1;
                                  datecounter:= p_daily_stock_updated_on;
                                 
                                 daydifference = (select date_part('day',q_created_on::timestamp - p_daily_stock_updated_on::timestamp));

                                  raise notice '%',' no of daydifference::'||daydifference;


                            for xyz in startcount .. daydifference
                            loop

                                     begin
				raise notice 'Inside Loop....................!';
                                          insert into network_daily_stocks
                                                (wallet_date,product_code,network_code,network_code_for,wallet_created,wallet_returned,wallet_balance,
                        wallet_sold,last_txn_no,last_txn_type,last_txn_balance,previous_balance,wallet_type,created_on)
                      VALUES(dateCounter,p_product_code,p_network_code,p_network_code_for,p_wallet_created,
                      p_wallet_returned,p_wallet,p_wallet_sold,p_last_txn_no,p_last_txn_type,p_last_txn_stock,
                      p_previous_stock,p_wallet_type,q_created_on
                                                );
                                                raise notice 'after insert....................!';
                                          exception
                                    when others
                                    then
                                       raise notice '%', 'exception others in network_daily_closing_stock insert sql, product:' || p_product_code || sqlerrm;
                                                   rtn_messageforlog := 'exception others in network_daily_closing_stock insert sql, product:' || p_product_code ;
                                   rtn_sqlerrmsgforlog := sqlerrm;
                                   raise exception 'exception others in network_daily_closing_stock insert sql';
                                    end;-- end of insert sql

                                    begin

                                                update network_stocks set
                                                                daily_stock_updated_on=q_created_on
                                            where product_code=p_product_code
                                                and network_code=p_network_code
                                                and network_code_for=p_network_code_for;

                                                exception
                                                   when others
                                                   then
                                                          raise notice '%', 'exception in network_daily_closing_stock update sql, product:' || p_product_code || sqlerrm;
                                                          rtn_messageforlog := 'exception in network_daily_closing_stock update sql, product:' || p_product_code ;
                                          rtn_sqlerrmsgforlog := sqlerrm;
                                                          raise exception 'exception in network_daily_closing_stock update sql';

                                        end;-- end of update sql

                                startcount:= startcount+1;
                                datecounter:= datecounter+ interval '1' day ;

                                end loop;--end of daydiffrence loop


                         end;--end of outer begin


                         end loop;--end of outer for loop

                             rtn_message:='success';
                                 rtn_messageforlog :='pretups network_daily_closing_stock mis successfully executed, date time:'||current_timestamp;
                                 rtn_sqlerrmsgforlog :=' ';

                                -- update network_stock_temp set end_time=sysdate,status_log=rtn_message where process='network_daily_closing_stock_process' and trunc(start_time)=trunc(sysdate);
                 /* commit; */

                 exception --exception handling of main procedure

                 when others then
                          /* rollback; */
                  raise notice '%','others error in network_daily_closing_stock procedure:='||sqlerrm;
                      rtn_message :='failed';
                          raise exception 'others error in network_daily_closing_stock procedure';



end;
$$;


ALTER FUNCTION pretupsdatabase.network_daily_closing_stock(OUT rtn_message character varying, OUT rtn_messageforlog character varying, OUT rtn_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: p2pdwhtempprc(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION p2pdwhtempprc(p_date timestamp without time zone, OUT p_mastercnt integer, OUT p_transcnt integer, OUT p_message character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE 

v_srno 	 INT;
v_data	VARCHAR (1000);



  DECLARE P2P_MASTER CURSOR  FOR
			SELECT PS.USER_ID||','||PS.MSISDN||','||PS.SUBSCRIBER_TYPE||','||REPLACE(LK.LOOKUP_NAME,',',' ')||','||PS.NETWORK_CODE||','||

	case when TO_CHAR(PS.LAST_TRANSFER_ON,'DD/MM/YYYY HH12:MI:SS') is null then COALESCE(to_char(PS.LAST_TRANSFER_ON, 'DD/MM/YYYY HH12:MI:SS'), '') else TO_CHAR(PS.LAST_TRANSFER_ON,'DD/MM/YYYY HH12:MI:SS') end ||','||
	--case when PS.LAST_TRANSFER_ON is null then '' else PS.LAST_TRANSFER_ON end ||','||

	REPLACE(case when  KV.VALUE is null then '' else  KV.VALUE end,',',' ')||','||PS.TOTAL_TRANSFERS||','||PS.TOTAL_TRANSFER_AMOUNT||','||
         case when PS.CREDIT_LIMIT is null then '' else PS.CREDIT_LIMIT::varchar(10) end||','||
        PS.REGISTERED_ON||','||case when  PS.LAST_TRANSFER_ID is null then '' else PS.LAST_TRANSFER_ID end||','
       ||  case when PS.LAST_TRANSFER_MSISDN is null then '' else PS.LAST_TRANSFER_MSISDN end||','
        ||PS.LANGUAGE||','||case when PS.COUNTRY is null then '' else PS.COUNTRY end||','
        ||REPLACE(case when PS.USER_NAME is null then '' else PS.USER_NAME end,',',' ')||','
        FROM KEY_VALUES KV right outer join  P2P_SUBSCRIBERS PS on PS.LAST_TRANSFER_STATUS=KV.KEY AND 'P2P_STATUS'=KV.TYPE ,LOOKUPS LK  
        WHERE  date_trunc('day',PS.ACTIVATED_ON::TIMESTAMP) < p_date AND LK.LOOKUP_CODE = PS.STATUS AND LK.LOOKUP_TYPE = 'SSTAT'  AND  PS.STATUS IN('Y','S') ;

 DECLARE P2P_TRANS CURSOR FOR
		SELECT STR.transfer_id||','||STR.transfer_date||','||STR.transfer_date_time||','||TRI1.msisdn||','||TRI2.msisdn||','
		||STR.transfer_value||','||STR.product_code||','||TRI1.previous_balance||','||TRI2.previous_balance||','
		||TRI1.post_balance||','||TRI2.post_balance||','||TRI1.transfer_value||','||TRI2.transfer_value||','||REPLACE(KV1.VALUE,',',' ')||','
		||REPLACE(KV2.VALUE,',',' ')||','||TRI1.subscriber_type||','||TRI2.subscriber_type||','||TRI1.service_class_id||','||TRI2.service_class_id||','
		||STR.sender_tax1_value||','||STR.receiver_tax1_value||','||STR.sender_tax2_value||','||STR.receiver_tax2_value||','
		||STR.sender_access_fee||','||STR.receiver_access_fee||','||STR.receiver_validity||','||STR.receiver_bonus_value||','
		||STR.receiver_bonus_validity||','||STR.receiver_grace_period||','||STR.sub_service||','||REPLACE(KV.VALUE,',',' ')||','
		FROM    KEY_VALUES KV1 right outer join TRANSFER_ITEMS TRI1 on TRI1.transfer_status= KV1.KEY AND KV1.TYPE = 'P2P_STATUS' , 
        KEY_VALUES KV2 right outer join TRANSFER_ITEMS TRI2 on TRI2.transfer_status=KV2.KEY AND KV2.TYPE = 'P2P_STATUS', 
        KEY_VALUES KV right outer join SUBSCRIBER_TRANSFERS STR on STR.transfer_status= KV.KEY AND KV.TYPE = 'P2P_STATUS' 
		WHERE STR.transfer_id = TRI1.transfer_id AND STR.transfer_id = TRI2.transfer_id AND TRI1.sno = 1
		AND TRI2.sno = 2 AND STR.transfer_date = p_date;

		BEGIN
			
	   		RAISE NOTICE '%','Start P2P DWH PROC1';
	   		
			v_srno := 0;
			v_data	:= NULL;

RAISE NOTICE '%','Start P2P DWH PROC..............100';
			DELETE from temp_p2p_dwh_master;
			DELETE from temp_p2p_dwh_trans;
	
			RAISE NOTICE '%','Start P2P DWH PROC..............1';

		   OPEN P2P_MASTER;
		   LOOP
		   RAISE NOTICE '%','Start P2P DWH PROC..............inside loop 1';
			FETCH  P2P_MASTER INTO v_data;
			    IF NOT FOUND THEN EXIT;
                            END IF;
			 RAISE NOTICE '%','Start P2P DWH PROC..............outside if 1';
			v_srno := v_srno+1; 
			 RAISE NOTICE '%','Start P2P DWH PROC..............before inseert 1';
			INSERT INTO TEMP_P2P_DWH_MASTER ( SRNO, DATA )
			VALUES (v_srno, v_data);
			raise notice '%','i am here:6=';

			IF (MOD(v_srno , 10000) = 0)
			THEN  COMMIT; 
			END IF;
			
		  END LOOP;
		  CLOSE P2P_MASTER;

			p_masterCnt := v_srno;
			RAISE NOTICE '%','p_masterCnt = '||p_masterCnt;
			v_srno := 0;
			v_data	:= NULL;

		   OPEN P2P_TRANS;
		   LOOP
		   RAISE NOTICE '%','Start P2P DWH PROC..............inside loop 2';
			FETCH P2P_TRANS INTO v_data;
			    IF NOT FOUND THEN EXIT;
                            END IF;
			
			v_srno := v_srno+1;
			INSERT INTO TEMP_P2P_DWH_TRANS ( SRNO, DATA )
			VALUES (v_srno, v_data);

			END LOOP;
			CLOSE P2P_TRANS;

			p_transCnt :=v_srno;
			RAISE NOTICE '%','p_transCnt = '||p_transCnt;

		/* COMMIT; */
		RAISE NOTICE '%','P2P DWH PROC Completed';
		p_message:='SUCCESS';

		EXCEPTION
		WHEN OTHERS THEN
		
			 p_message:='Not able to migrate data, Exception occoured';
	RAISE EXCEPTION 'Not able to migrate data, Exception occoured';


END;
$$;


ALTER FUNCTION pretupsdatabase.p2pdwhtempprc(p_date timestamp without time zone, OUT p_mastercnt integer, OUT p_transcnt integer, OUT p_message character varying) OWNER TO pgdb;

--
-- Name: p_changevoucherstatus(character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, character varying, integer, character varying, character varying, character varying, integer, character varying, character varying, integer, character varying, character varying, character varying, character varying, integer); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION p_changevoucherstatus(p_batchno character varying, p_batchtype character varying, p_fromserialno character varying, p_toserialno character varying, p_batchenablestat character varying, p_batchgenstat character varying, p_batchonholdstat character varying, p_batchstolenstat character varying, p_batchsoldstat character varying, p_batchdamagestat character varying, p_batchreconcilestat character varying, p_batchprintstat character varying, p_warehousestat character varying, p_preactivestat character varying, p_suspendstat character varying, p_createdon character varying, p_maxerrorallowed integer, p_modifiedby character varying, p_noofvouchers integer, p_successprocessstatus character varying, p_errorprocessstatus character varying, p_batchconstat character varying, p_processscreen integer, p_modifiedtime character varying, p_referenceno character varying, p_rcadminmaxdateallowed integer, p_enableprocess character varying, p_changeprocess character varying, p_reconcileprocess character varying, p_networkcode character varying, p_seqid integer, OUT p_returnmessage character varying, OUT p_returnlogmessage character varying, OUT p_sqlerrormessage character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE
-- Variables Declaration
v_batchEnableStat  voms_batches.BATCH_TYPE%type;
v_batchGenStat  voms_batches.BATCH_TYPE%type;
v_batchOnHoldStat  voms_batches.BATCH_TYPE%type;
v_batchStolenStat  voms_batches.BATCH_TYPE%type;
v_batchSoldStat  voms_batches.BATCH_TYPE%type;
v_batchDamageStat  voms_batches.BATCH_TYPE%type;
v_batchReconcileStat  voms_batches.BATCH_TYPE%type;
v_batchConStat  voms_batches.BATCH_TYPE%type;
v_batchPrintStat voms_batches.BATCH_TYPE%type;
v_wareHouseStat voms_batches.BATCH_TYPE%type;
v_preActiveStat voms_batches.BATCH_TYPE%type;
v_suspendStat voms_batches.BATCH_TYPE%type;
v_batchNo voms_batches.BATCH_NO%type;
v_batchType voms_batches.BATCH_TYPE%type;
v_vouchStat voms_vouchers.STATUS%type;
v_voucCurrStat voms_vouchers.CURRENT_STATUS%type;
v_expDate voms_vouchers.EXPIRY_DATE%type;
v_createdOn TIMESTAMP without time zone;
v_productID voms_vouchers.PRODUCT_ID%type;
v_modifiedBy voms_voucher_audit.MODIFIED_BY%type;
rcd_count INT;
v_networkCode voms_batches.NETWORK_CODE%type;
v_succFailFlag varchar(32767);
v_message voms_voucher_audit.MESSAGE%type;
v_errorCount Integer;
v_processScreen INT;
v_modifiedTime TIMESTAMP without time zone;
v_processStatus voms_voucher_audit.PROCESS_STATUS%type;
v_row_id INT;
v_insertRowId voms_voucher_audit.ROW_ID%type;
v_serialStart voms_batches.FROM_SERIAL_NO%type;
v_referenceNo voms_batches.REFERENCE_NO%type;
v_EnableProcess voms_batches.PROCESS%type;
v_ChangeProcess voms_batches.PROCESS%type;
v_ReconcileProcess voms_batches.PROCESS%type;
v_returnMessage  varchar(32767);
v_returnLogMessage  varchar(32767);
v_sqlErrorMessage varchar(32767);
v_enableCount Integer;
v_DamageStolenCount Integer;
v_DamageStolenAfterEnCount Integer;
v_onHoldCount Integer;
v_counsumedCount INT;
v_voucherNotFoundCount Integer;
v_preActiveCount INT;
v_voucherNotFoundFlag boolean;
v_maxErrorFlag boolean;
v_LastRequestAttemptNo voms_vouchers.LAST_REQUEST_ATTEMPT_NO%type;
v_LastAttemptValue voms_vouchers.LAST_ATTEMPT_VALUE%type;
v_RCAdminMaxdateallowed INT;
v_serialNoLength INT;
v_seqId  INT;
-- Declaration of Variables Ends --
BEGIN
     v_batchEnableStat:=p_batchEnableStat;
     v_batchGenStat:=p_batchGenStat;
     v_batchOnHoldStat:=p_batchOnHoldStat;
     v_batchStolenStat:=p_batchStolenStat;
     v_batchSoldStat:= p_batchSoldStat;
     v_batchDamageStat:=p_batchDamageStat;
     v_batchReconcileStat:=p_batchReconcileStat;
     v_batchPrintStat:=p_batchPrintStat;
     v_wareHouseStat:=p_wareHouseStat;
     v_preActiveStat:=p_preActiveStat;
     v_suspendStat:=p_suspendStat;
     v_batchConStat:=p_batchConStat;
  
     v_modifiedBy:=p_modifiedBy;
     v_errorCount:=0;
     v_serialStart:=p_fromSerialNo;
     v_batchNo :=p_batchNo;
     v_batchType:=p_batchType;
     v_processScreen:=p_processScreen;
     v_enableCount  :=0;
     v_DamageStolenCount :=0;
   --  v_DamageStolenAfterEnCount :=0;
     v_onHoldCount :=0;
     v_counsumedCount:=0;
     v_preActiveCount:=0;
     v_referenceNo:=p_referenceNo;
     v_RCAdminMaxdateallowed:=p_RCAdminMaxdateallowed;
     v_EnableProcess :=p_EnableProcess;
     v_ChangeProcess :=p_ChangeProcess;
     v_ReconcileProcess :=p_ReconcileProcess;
     v_networkCode:=p_networkCode;
     v_voucherNotFoundCount:=0;
     v_returnMessage:='';
     v_sqlErrorMessage:='';
     v_serialNoLength:=LENGTH(p_fromSerialNo);
	v_seqId:=p_seqId;
	v_LastRequestAttemptNo := 0.0;
	v_LastAttemptValue :=0.0;
     -- Start the Loop --
     WHILE(v_serialStart<=p_toSerialNo) LOOP
      BEGIN
       select to_timestamp(p_createdOn ,'YYYY-MM-DD HH24:MI:SS')::timestamp without time zone into v_createdOn ;
       select to_timestamp(p_modifiedTime ,'YYYY-MM-DD HH24:MI:SS')::timestamp without time zone into v_modifiedTime ;
       
       v_succFailFlag:='FAILED';
       v_maxErrorFlag:=FALSE;
       v_voucherNotFoundFlag:=FALSE;
       rcd_count:=0;
       v_message:='';
       v_returnLogMessage:='';
           /* Check that the total invalid vouchers are less
       than the max error entries allowed */
       IF(v_errorCount<= p_maxErrorAllowed) THEN
            --Block for checking which vouchers are valid for the incoming new voucher status
         BEGIN
            RAISE NOTICE 'Before check_change_valid_proc  %', v_createdOn ;
           
RAISE NOTICE 'Before check_change_valid_proc  %', v_seqId ;
	SELECT
	*  
	INTO 
		v_DamageStolenAfterEnCount ,
		v_vouchStat , 
		v_voucCurrStat  ,
		v_productID ,
		v_LastRequestAttemptNo  ,
		v_LastAttemptValue ,
		v_succFailFlag  ,
		v_enableCount  ,
		v_errorCount  ,
		v_message ,
		v_DamageStolenCount ,
		v_onHoldCount,
		v_voucherNotFoundCount ,
		v_returnMessage ,
		v_returnLogMessage 
		FROM 
	 VSC_CHECK_CHANGE_VALID_PROC(
	v_serialStart, v_voucherNotFoundFlag, 
	v_processScreen ,v_createdOn ,
	v_batchType, v_batchEnableStat ,
	v_batchGenStat, v_EnableProcess , 
	v_batchStolenStat , v_batchOnHoldStat, 
	v_preActiveStat, v_suspendStat, 
	v_ChangeProcess , v_wareHouseStat , 
	v_batchPrintStat , v_batchReconcileStat ,  
	v_modifiedTime , v_RCAdminMaxdateallowed , 
	v_ReconcileProcess, v_batchConStat,v_batchDamageStat, v_seqId
		 ) ;
		 

		RAISE NOTICE 'AFTER check_change_valid_proc %,%, % ',v_vouchStat, v_voucCurrStat , v_productID       ;
		
         EXCEPTION
           when sqlstate 'EXITE'  then
              RAISE NOTICE 'EXCEPTION while checking if voucher is valid  =   % ', SQLERRM ;
              v_returnMessage:='FAILED';
              RAISE EXCEPTION 'SQL Exception for updating records   ' USING ERRCODE = 'EXITE';
          WHEN OTHERS THEN
               RAISE NOTICE 'others EXCEPTION while checking if voucher is valid  =   % ', SQLERRM ;
              v_returnMessage:='FAILED';
               RAISE EXCEPTION 'SQL Exception for updating records   ' USING ERRCODE = 'EXITE';
         END;
           RAISE NOTICE 'Iv_succFailFlag  =   % ', v_succFailFlag ;

          -- If vouchers are valid then perform these steps
          IF(v_succFailFlag='SUCCESS') THEN
          /* If vouchers are valid for change status and the new
          voucher status is of enable type then
          1. Update voucher Table */

          IF(p_batchType=p_batchEnableStat AND v_processScreen=1) THEN
          BEGIN
         RAISE NOTICE 'vsc_update_voucher_enable before %, % , %, %, %, %, %, %', v_vouchstat,
    v_batchreconcilestat ,
    v_batchno,
   v_batchtype,
    v_modifiedby,
   v_modifiedtime,
    v_serialstart, v_seqId ;
    
	select * into v_returnlogmessage, v_returnmessage, v_message from vsc_update_voucher_enable(
    v_vouchstat,
    v_batchreconcilestat ,
    v_batchno,
   v_batchtype,
    v_modifiedby,
   v_modifiedtime,
    v_serialstart, v_seqId );
     RAISE NOTICE 'vsc_update_voucher_enable after ';
	
         EXCEPTION
         when sqlstate 'EXITE'  then
          RAISE NOTICE 'EXCEPTION while updating vouchers for Enable type  =  % ', SQLERRM ;
              v_returnMessage:='FAILED';
               RAISE EXCEPTION 'SQL Exception for updating records   ' USING ERRCODE = 'EXITE';
          WHEN OTHERS THEN
               RAISE NOTICE 'others EXCEPTION while updating vouchers for Enable type  =  % ', SQLERRM ;
              v_returnMessage:='FAILED';
               RAISE EXCEPTION 'SQL Exception for updating records   ' USING ERRCODE = 'EXITE';
         END;
         ELSIF(p_batchType=p_batchEnableStat AND (v_processScreen=2)) THEN
          BEGIN
             RAISE NOTICE 'VSC_UPDATE_VOUCHER_ENABLE_OTHER before ';
	SELECT * into v_returnlogmessage, v_returnmessage, v_message from  VSC_UPDATE_VOUCHER_ENABLE_OTHER(
	v_batchno,
	  v_batchtype,
	    v_modifiedby ,
	   v_modifiedtime ,
	  v_vouchStat ,
	  v_batchReconcileStat  ,
	v_voucCurrStat  ,
	v_serialStart ,
	v_seqId
	)
	;
	   RAISE NOTICE 'VSC_UPDATE_VOUCHER_ENABLE_OTHER after ';
         EXCEPTION
           when sqlstate 'EXITE'  then
                v_returnMessage:='FAILED';
                RAISE NOTICE 'EXCEPTION while updating vouchers for Enable type  =  % ', SQLERRM ;
              RAISE EXCEPTION 'SQL Exception for updating records   ' USING ERRCODE = 'EXITE';
          WHEN OTHERS THEN
              v_returnMessage:='FAILED';
                RAISE NOTICE 'others EXCEPTION while updating vouchers for Enable type  =  % ', SQLERRM ;
               RAISE EXCEPTION 'SQL Exception for updating records   ' USING ERRCODE = 'EXITE';
         END;
         /*code changed by kamini .
         elsif(p_batchType=p_batchEnableStat AND (v_processScreen=2 OR v_processScreen=3)) then*/

         ELSIF(p_batchType=p_batchEnableStat AND v_processScreen=3) THEN
          BEGIN
          RAISE NOTICE 'VSC_UPDATE_VOUCHER_ENABLE_OTHER before ';
             SELECT * into v_returnlogmessage, v_returnmessage, v_message from  VSC_UPDATE_VOUCHER_ENABLE_OTHER(
	v_batchno,
	  v_batchtype,
	    v_modifiedby ,
	   v_modifiedtime ,
	  v_vouchStat ,
	  v_batchReconcileStat  ,
	v_voucCurrStat  ,
	v_serialStart ,
	v_seqId
	)
	;
	     RAISE NOTICE 'VSC_UPDATE_VOUCHER_ENABLE_OTHER after ';
         EXCEPTION
           when sqlstate 'EXITE'  then
                v_returnMessage:='FAILED';
                 RAISE NOTICE 'EXCEPTION while updating vouchers for Enable type  =  % ', SQLERRM ;
               RAISE EXCEPTION 'EXCEPTION while updating vouchers for Enable type   ' USING ERRCODE = 'EXITE';
          WHEN OTHERS THEN
              v_returnMessage:='FAILED';
               RAISE NOTICE 'others EXCEPTION while updating vouchers for Enable type  =  % ', SQLERRM ;
               RAISE EXCEPTION 'EXCEPTION while updating vouchers for Enable type   ' USING ERRCODE = 'EXITE';
         END;

         /* If new voucher status is other than enable
         then perform these steps
         1. Update Vouchers. */
          ELSE
          BEGIN
             RAISE NOTICE 'VSC_UPDATE_VOUCHERS before '; 
	select * into v_returnlogmessage, v_returnmessage, v_message from   VSC_UPDATE_VOUCHERS(
	v_vouchStat,
	   v_batchreconcilestat,
	   v_batchno ,
	 v_batchtype ,
	  v_modifiedby ,
	  v_modifiedtime,
	v_serialstart ,
	v_voucCurrStat	,
	  v_LastRequestAttemptNo,
	   v_LastAttemptValue ,
	  v_seqId
	)
	 ;
	  RAISE NOTICE 'VSC_UPDATE_VOUCHERS after '; 
          EXCEPTION
           when sqlstate 'EXITE'  then
                RAISE NOTICE 'EXCEPTION while updating vouchers    =  % ', SQLERRM ;
              v_returnMessage:='FAILED';
               RAISE EXCEPTION 'EXCEPTION while updating vouchers for Enable type   ' USING ERRCODE = 'EXITE';
          WHEN OTHERS THEN
                RAISE NOTICE 'others EXCEPTION while updating vouchers  =  % ', SQLERRM ;
              v_returnMessage:='FAILED';
               RAISE EXCEPTION 'others EXCEPTION while updating vouchers  ' USING ERRCODE = 'EXITE';
          END;
          END IF;  --   en d of if(p_batchType=p_batchEnableStat)
      END IF;  --end of if(SUCCESS)

       IF(v_succFailFlag='SUCCESS') THEN
             v_processStatus:=p_successProcessStatus;  --store SU in status of VA table in case of success
             v_message:='Success';
            ELSE
                v_processStatus:=p_errorProcessStatus; --store ER in status of VA table in case of error
       END IF;

       /* For all voucher status change log entry of each serial no
       in voucher udit table . Block for insertion in VA table */
        -- if condition added on 13/02/04 so that if voucher not found then
        -- that entry is not made in VA table
        IF(v_voucherNotFoundFlag=FALSE) THEN
        BEGIN
        RAISE NOTICE 'VSC_INSERT_IN_AUDIT_PROC before '; 
	select  * into v_returnMessage, v_returnLogMessage, v_sqlErrorMessage from VSC_INSERT_IN_AUDIT_PROC(
	 v_insertRowId,
	  v_serialStart,
	  v_batchType,
	  v_vouchStat,
	 v_modifiedBy,
	 v_modifiedTime,
	 v_batchNo,
	 v_message,
	 v_processStatus,
	 v_row_id
	)
	;
	  RAISE NOTICE 'VSC_INSERT_IN_AUDIT_PROC after %, %, %', v_returnMessage, v_returnLogMessage, v_sqlErrorMessage ; 
        EXCEPTION
          when sqlstate 'EXITE'  then
                v_returnMessage:='FAILED';
                RAISE NOTICE 'EXCEPTION while inserting in VA table = % ', SQLERRM ;
               RAISE EXCEPTION 'EXCEPTION while inserting in VA table = ' USING ERRCODE = 'EXITE';
          WHEN OTHERS THEN
                v_returnMessage:='FAILED';
               RAISE NOTICE 'others EXCEPTION while inserting in VA table  = % ', SQLERRM ;
               RAISE EXCEPTION 'others EXCEPTION while inserting in VA table ' USING ERRCODE = 'EXITE';
        END;  -- end of inserting record in voucher_audit table
        END IF; -- end of  if(v_voucherNotFoundFlag=false)

      ELSE  -- Else of exceeding the max error allowed
         v_succFailFlag:='FAILED';
         v_returnMessage:='FAILED';
        v_maxErrorFlag:=TRUE;
         v_message:='Exceeded the max error '|| p_maxErrorAllowed ||' entries allowed ';
         v_returnLogMessage:='Exceeded the max error '|| p_maxErrorAllowed ||' entries allowed ';
          RAISE EXCEPTION 'Exceeded the max error ' USING ERRCODE = 'EXITE';
      END IF;
      v_serialStart:=v_serialStart::bigint+1; -- incrementing from serial no by 1
      v_serialStart:=LPAD(v_serialStart,v_serialNoLength,'0');
	RAISE NOTICE 'v_serialStart after incrementing = % ', v_serialStart ;
      /* catch the Exception of type EXITEXCEPTION thrown above */
      EXCEPTION
        when sqlstate 'EXITE'  then
      v_returnMessage:='FAILED';
         RAISE NOTICE 'EXCEPTION in while loop % ', SQLERRM ;
        RAISE EXCEPTION 'FAILED ' USING ERRCODE = 'EXITE';
      WHEN OTHERS THEN
      v_returnMessage:='FAILED';
      RAISE NOTICE 'EXCEPTION other in while loop % ', SQLERRM ;
        RAISE EXCEPTION 'FAILED ' USING ERRCODE = 'EXITE';
      END;
      END LOOP;  -- end of while loop
      RAISE NOTICE 'v_serialStart after loop =% ', v_serialStart ;
      /*    Update the Voucher batch and voucher summary  Table */
     BEGIN
        	
	RAISE NOTICE 'VSC_INSERT_IN_SUMMARY_PROC before '; 
	SELECT * into  v_returnlogmessage  , v_returnmessage from  VSC_INSERT_IN_SUMMARY_PROC(
	v_enableCount,
	v_counsumedCount,
	v_DamageStolenCount ,
	v_DamageStolenAfterEnCount,
	v_referenceNo,
	v_createdOn,
	v_productID,
	v_networkCode,
	v_onHoldCount,
	rcd_count,
	p_fromSerialNo
	)
	   ;
	 RAISE NOTICE 'VSC_INSERT_IN_SUMMARY_PROC after '; 
        EXCEPTION
        when sqlstate 'EXITE'  then
            v_returnMessage:='FAILED';
          RAISE NOTICE 'EXCEPTION while inserting in summary table  =% ', SQLERRM ;
           RAISE EXCEPTION 'EXCEPTION while inserting in summary table' USING ERRCODE = 'EXITE';
      WHEN OTHERS THEN
            v_returnMessage:='FAILED';
           RAISE NOTICE 'others EXCEPTION while inserting in summary table  =% ', SQLERRM ;
           RAISE EXCEPTION 'others EXCEPTION while inserting in summary table ' USING ERRCODE = 'EXITE';
     END;

     --COMMIT;  --final commit

      /* If all the entries are inavlid for status change then
      return FAILED else return SUCCESS . Also return the message that
      needs to be written in the log file. */
      IF(p_noOfVouchers=v_errorCount) THEN
        p_returnMessage:='FAILED';
    p_returnLogMessage:='Vouchers is in '||v_vouchStat||' and cannot be made to '||v_batchType||'';
      ELSIF(v_maxErrorFlag=TRUE) THEN
     p_returnMessage:='FAILED';
        p_returnLogMessage:='Exceeded the max error '|| p_maxErrorAllowed ||' entries allowed ';
      ELSIF(v_returnMessage='FAILED') THEN
        p_returnMessage:='FAILED';
        p_returnLogMessage:='Not able to update the vouchers status to '||v_batchType;
      ELSE
        p_returnMessage:='SUCCESS';
        IF(v_voucherNotFoundCount>0) THEN
        p_returnLogMessage:='Successfully changed status with '||v_voucherNotFoundCount||' vouchers not found';
    ELSIF(v_errorCount> 0) THEN
    p_returnLogMessage:='Not able to update the status to '||v_batchType||' of  '|| v_errorCount ||' vouchers';
    ELSE
        p_returnLogMessage:='Successfully changed status of '||p_noOfVouchers||' vouchers';
        END IF;
      END IF;
EXCEPTION
 when sqlstate 'EXITE'  then
p_returnMessage:='FAILED';
--p_returnLogMessage:='Not able to update the vouchers status to '||v_batchType;
p_returnLogMessage:='Not able to update the vouchers status to '||v_batchType;
p_sqlErrorMessage:=v_sqlErrorMessage;
  RAISE NOTICE 'Procedure Exiting% ', SQLERRM ;
WHEN OTHERS THEN
p_returnMessage:='FAILED';
--p_returnLogMessage:='Not able to update the vouchers status to '||v_batchType;
p_returnLogMessage:='Not able to update the vouchers status to '||v_batchType;
p_sqlErrorMessage:=v_sqlErrorMessage;
  RAISE NOTICE 'Procedure Exiting% ', SQLERRM ;
--ROLLBACK;
END; --Rollback in case of Exception
$$;


ALTER FUNCTION pretupsdatabase.p_changevoucherstatus(p_batchno character varying, p_batchtype character varying, p_fromserialno character varying, p_toserialno character varying, p_batchenablestat character varying, p_batchgenstat character varying, p_batchonholdstat character varying, p_batchstolenstat character varying, p_batchsoldstat character varying, p_batchdamagestat character varying, p_batchreconcilestat character varying, p_batchprintstat character varying, p_warehousestat character varying, p_preactivestat character varying, p_suspendstat character varying, p_createdon character varying, p_maxerrorallowed integer, p_modifiedby character varying, p_noofvouchers integer, p_successprocessstatus character varying, p_errorprocessstatus character varying, p_batchconstat character varying, p_processscreen integer, p_modifiedtime character varying, p_referenceno character varying, p_rcadminmaxdateallowed integer, p_enableprocess character varying, p_changeprocess character varying, p_reconcileprocess character varying, p_networkcode character varying, p_seqid integer, OUT p_returnmessage character varying, OUT p_returnlogmessage character varying, OUT p_sqlerrormessage character varying) OWNER TO pgdb;

--
-- Name: p_chnlserviceproc(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION p_chnlserviceproc(OUT p_returnmessage character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE 

V_USERID 		 	 USER_SERVICES.USER_ID%TYPE;
V_RETURNMESSAGE 			  VARCHAR(255);


 DECLARE USER_ID_CUR CURSOR IS
                                SELECT DISTINCT  USER_ID FROM USER_SERVICES;

BEGIN

               OPEN  USER_ID_CUR;
                            LOOP
				FETCH USER_ID_CUR INTO V_USERID ;
				EXIT WHEN NOT FOUND;
                                 BEGIN

                                       INSERT INTO USER_SERVICES (USER_ID,SERVICE_TYPE,STATUS)
                                         VALUES (V_USERID,'IR', 'Y');
                                 EXCEPTION
                                        WHEN OTHERS THEN
                                             RAISE NOTICE '%','EXCEPTION IN INSERTING NEW RECORD FOR %'||V_USERID ||sqlerrm;
                                             V_RETURNMESSAGE:='EXCEPTION IN INSERTING NEW RECORD FOR'||V_USERID;
                                              RAISE EXCEPTION 'EXCEPTION IN INSERTING NEW RECORD FOR ';
                                   END;
			END LOOP;
                      P_RETURNMESSAGE:=V_RETURNMESSAGE;
END ;
$$;


ALTER FUNCTION pretupsdatabase.p_chnlserviceproc(OUT p_returnmessage character varying) OWNER TO pgdb;

--
-- Name: p_updaterechargetinfo(character varying, timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION p_updaterechargetinfo(p_batchconstat character varying, p_modifieddate timestamp without time zone, OUT p_returnmessage character varying, OUT p_message character varying, OUT p_messagetosend character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
	DECLARE 
	v_createdOn voms_vouchers.EXPIRY_DATE%type;
	v_generationBatchNo voms_vouchers.GENERATION_BATCH_NO%type;
	v_userNetworkCode voms_vouchers.USER_NETWORK_CODE%type;
	v_productionNetworkCode voms_vouchers.PRODUCTION_NETWORK_CODE%type;
	v_productID voms_vouchers.PRODUCT_ID%type;
	rcd_count INT;
	v_recharge_count INT;

	v_ErrMessage varchar(200);
	v_voucherUpdateCount INT;

	v_tempCount INT=0;


	DECLARE consumeVouch cursor  FOR select date_trunc('day',v.FIRST_CONSUMED_ON::TIMESTAMP) createdOn,v.PRODUCT_ID PRODID,count(v.STATUS) cot,
	v.GENERATION_BATCH_NO GENNO,v.USER_NETWORK_CODE ULCODE ,v.PRODUCTION_NETWORK_CODE PRODLOCCODE
	From voms_vouchers v where status=p_batchConStat
	AND v.CON_SUMMARY_UPDATE='N'
	group by  date_trunc('day',v.FIRST_CONSUMED_ON::TIMESTAMP),v.PRODUCT_ID,v.GENERATION_BATCH_NO,v.USER_NETWORK_CODE,v.PRODUCTION_NETWORK_CODE
	order by date_trunc('day',v.FIRST_CONSUMED_ON::TIMESTAMP),v.PRODUCT_ID,v.PRODUCTION_NETWORK_CODE;
	BEGIN
		v_voucherUpdateCount:=0;
		FOR VOUCH_CUR IN consumeVouch
		 LOOP
	     BEGIN
		 exit when NOT FOUND;
		  v_tempCount:=0;
		  v_productID:=VOUCH_CUR.PRODID;
		  v_recharge_count:=VOUCH_CUR.cot;
		  v_generationBatchNo:=VOUCH_CUR.GENNO;
		  v_userNetworkCode:=VOUCH_CUR.ULCODE;
		  v_productionNetworkCode:=VOUCH_CUR.PRODLOCCODE;
		  v_createdOn:=VOUCH_CUR.createdOn;
		  RAISE NOTICE '%','v_productID:='||v_productID;
		  RAISE NOTICE '%','v_recharge_count:='||v_recharge_count;
		  RAISE NOTICE '%','v_createdOn:='||v_createdOn;

		  BEGIN
				UPDATE voms_vouchers set CON_SUMMARY_UPDATE='Y',MODIFIED_BY='RECHARGESCH',
				MODIFIED_ON=p_modifiedDate
				WHERE  PRODUCT_ID=v_productID
				 and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
				 and USER_NETWORK_CODE=v_userNetworkCode
				 and generation_batch_no=v_generationBatchNo and CON_SUMMARY_UPDATE='N'
				 and status=p_batchConStat and date_trunc('day',FIRST_CONSUMED_ON::TIMESTAMP)=v_createdOn;

				IF NOT FOUND then
			RAISE NOTICE '%','SQL EXCEPTION while updating  voucher status  ='||sqlerrm;
				RAISE EXCEPTION 'SQL EXCEPTION while updating  voucher status';
				end if;  -- end of if SQL%NOTFOUND
			EXCEPTION
			
			when others then
			 v_ErrMessage:='Not able to update vouchers for PROD. LOC:='||v_productionNetworkCode||' USER_NETWORK_CODE='||v_userNetworkCode||'PRODUCT_ID='||v_productID;
			p_returnMessage:='FAILED';
			RAISE NOTICE '%','SQL EXCEPTION while updating  voucher status  ='||sqlerrm;
			RAISE EXCEPTION 'Not able to update vouchers for PROD';
		 END;

		  BEGIN
				UPDATE VOMS_VOUCHER_BATCH_SUMMARY set TOTAL_RECHARGED=TOTAL_RECHARGED+v_tempCount
				WHERE  BATCH_NO =v_generationBatchNo;
				RAISE NOTICE '%','v_tempCount for voucherBatchsummary '||v_productionNetworkCode ||' v_userNetworkCode '||v_userNetworkCode||'is '||v_tempCount;

				IF NOT FOUND then
			RAISE NOTICE '%','SQL EXCEPTION while checking updating batch summary  ='||sqlerrm;
				RAISE EXCEPTION 'SQL EXCEPTION while checking updating batch summary';
				end if;  -- end of if SQL%NOTFOUND
			EXCEPTION
			
			when others then
			 v_ErrMessage:='Not able to update batch for Batch No:='||v_generationBatchNo;
			 RAISE NOTICE '%','SQL EXCEPTION while updating  voucher status  ='||sqlerrm;
			 RAISE EXCEPTION 'Not able to update batch for Batch No';
			END;


		  BEGIN      --block for insertion/updation in voucher_summary
			 begin  --block checking if record exist in voucher_summary
			 select '1' INTO rcd_count
			 where exists (select 1 from voms_voucher_summary
						 where SUMMARY_DATE=v_createdOn
						 and PRODUCT_ID=v_productID
						 and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
						 and USER_NETWORK_CODE=v_userNetworkCode);

			EXCEPTION
			WHEN NO_DATA_FOUND THEN  --when no row returned for the distributor
				 --DBMS_OUTPUT.PUT_LINE('No Record found ');
				 rcd_count := 0;
			WHEN OTHERS THEN
				 RAISE NOTICE '%','Exception in Record check ';
				RAISE EXCEPTION 'Exception in Record check';
		   end;
		  if rcd_count = 0 then
			INSERT INTO VOMS_VOUCHER_SUMMARY(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE,USER_NETWORK_CODE,
					TOTAL_RECHARGED) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_tempCount);
					
					RAISE NOTICE '%','Insert-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount;
		  else
			UPDATE VOMS_VOUCHER_SUMMARY
			SET TOTAL_RECHARGED=TOTAL_RECHARGED+v_tempCount
						 where SUMMARY_DATE=v_createdOn
						 and PRODUCT_ID=v_productID
						 and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
						 and USER_NETWORK_CODE=v_userNetworkCode;
						 RAISE NOTICE '%','update-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount;
		  end if;
		  exception
		  
			when others then
			  RAISE NOTICE '%','EXCEPTION CAUGHT while Record test in VOUCHER_SUMMARY='||sqlerrm;
				RAISE EXCEPTION 'EXCEPTION CAUGHT while Record test in VOUCHER_SUMMARY';
		  END;

		  RAISE NOTICE '%','v_voucherUpdateCount===='||v_voucherUpdateCount;

		  EXCEPTION
		
		  WHEN OTHERS then
		  RAISE NOTICE '%','Not able to update records '||sqlerrm;
		 RAISE EXCEPTION 'Not able to update records';

		  END;

		  end loop;
		  RAISE NOTICE '%','Voucher Count='||v_voucherUpdateCount;
		  p_returnMessage:='SUCCESS';
		  p_message :='Successfully Completed updation of '||v_voucherUpdateCount||' vouchers';
		  p_messageToSend :='';
		

	EXCEPTION
	
	/* ROLLBACK; */
	WHEN OTHERS THEN
	p_message :='Not able to update Vouchers';
	p_messageToSend :=v_ErrMessage;
	if consumeVouch%ISOPEN then
		  close consumeVouch;
	end if;
	RAISE NOTICE '%','Procedure Exiting'||sqlerrm;
	p_returnMessage:='FAILED';
	/* ROLLBACK; */
	END;
	$$;


ALTER FUNCTION pretupsdatabase.p_updaterechargetinfo(p_batchconstat character varying, p_modifieddate timestamp without time zone, OUT p_returnmessage character varying, OUT p_message character varying, OUT p_messagetosend character varying) OWNER TO pgdb;

--
-- Name: p_updatesummaryinfo(character varying, character varying, character varying, timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION p_updatesummaryinfo(p_batchconstat character varying, p_batchdastat character varying, p_batchreconcilestat character varying, p_modifieddate timestamp without time zone, OUT p_returnmessage character varying, OUT p_message character varying, OUT p_messagetosend character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE 
	v_createdOn voms_vouchers.EXPIRY_DATE%type;
	v_generationBatchNo voms_vouchers.GENERATION_BATCH_NO%type;
	v_userNetworkCode voms_vouchers.USER_NETWORK_CODE%type;
	v_productionNetworkCode voms_vouchers.PRODUCTION_NETWORK_CODE%type;
	v_productID voms_vouchers.PRODUCT_ID%type;
	v_voucherStatus voms_vouchers.CURRENT_STATUS%type;
	v_vouchPreviousStatus voms_vouchers.PREVIOUS_STATUS%type;
	rcd_count INT;
	v_recharge_count INT;

	v_ErrMessage varchar(200);
	v_voucherUpdateCount INT;

	v_tempCount INT=0;

	DECLARE consumeVouch cursor FOR 
	SELECT current_status,previous_status,date_trunc('day',v.MODIFIED_ON::TIMESTAMP) createdOn,v.PRODUCT_ID PRODID,count(v.STATUS) cot,
	v.GENERATION_BATCH_NO GENNO,v.USER_NETWORK_CODE ULCODE ,v.PRODUCTION_NETWORK_CODE PRODLOCCODE
	From voms_vouchers v where (current_status=p_batchConStat OR current_status=p_batchDaStat OR current_status=p_batchReconcileStat)
	AND v.CON_SUMMARY_UPDATE='N' AND FIRST_CONSUMED_BY IS NOT NULL
	group by  date_trunc('day',v.MODIFIED_ON::TIMESTAMP),v.PRODUCT_ID,v.GENERATION_BATCH_NO,v.USER_NETWORK_CODE,v.PRODUCTION_NETWORK_CODE,current_status,previous_status
	order by date_trunc('day',v.MODIFIED_ON::TIMESTAMP),v.PRODUCT_ID,v.PRODUCTION_NETWORK_CODE,current_status,previous_status;
	BEGIN
--v.GENERATION_BATCH_NO;

		v_voucherUpdateCount:=0;
		FOR VOUCH_CUR IN consumeVouch
		 LOOP
	     BEGIN
		 exit when NOT FOUND;
		  v_tempCount:=0;
		  v_productID:=VOUCH_CUR.PRODID;
		  v_recharge_count:=VOUCH_CUR.cot;
		  v_generationBatchNo:=VOUCH_CUR.GENNO;
		  v_userNetworkCode:=VOUCH_CUR.ULCODE;
		  v_productionNetworkCode:=VOUCH_CUR.PRODLOCCODE;
		  v_createdOn:=VOUCH_CUR.createdOn;
		  v_voucherStatus:=VOUCH_CUR.current_status;
		  v_vouchPreviousStatus:=VOUCH_CUR.previous_status;

		  RAISE NOTICE '%','v_productID:='||v_productID;
		  RAISE NOTICE '%','v_recharge_count:='||v_recharge_count;
		  RAISE NOTICE '%','v_createdOn:='||v_createdOn;
		  RAISE NOTICE '%','v_voucherStatus:='||v_voucherStatus;
		  RAISE NOTICE '%','v_vouchPreviousStatus:='||v_vouchPreviousStatus;

		  BEGIN

				if(v_voucherStatus=p_batchConStat) THEN
				UPDATE voms_vouchers set CON_SUMMARY_UPDATE='Y',MODIFIED_BY='RECHARGESCH',
				MODIFIED_ON=p_modifiedDate
				WHERE  PRODUCT_ID=v_productID
				 and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
				 and USER_NETWORK_CODE=v_userNetworkCode AND FIRST_CONSUMED_BY IS NOT NULL
				 and generation_batch_no=v_generationBatchNo and CON_SUMMARY_UPDATE='N'
				 and current_status=p_batchConStat and date_trunc('day',MODIFIED_ON::TIMESTAMP)=v_createdOn;

				IF NOT FOUND then
				RAISE NOTICE '%','SQL EXCEPTION while updating  voucher status  ='||sqlerrm;
				RAISE EXCEPTION 'SQL EXCEPTION while updating  voucher status';
				end if;  -- end of if SQL%NOTFOUND

				ELSIF(v_voucherStatus=p_batchReconcileStat) THEN
				UPDATE voms_vouchers set CON_SUMMARY_UPDATE='Y',MODIFIED_BY='RECHARGESCH',
				MODIFIED_ON=p_modifiedDate
				WHERE  PRODUCT_ID=v_productID
				 and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
				 and USER_NETWORK_CODE=v_userNetworkCode AND FIRST_CONSUMED_BY IS NOT NULL
				 and generation_batch_no=v_generationBatchNo and CON_SUMMARY_UPDATE='N'
				 and current_status=p_batchReconcileStat and date_trunc('day',MODIFIED_ON::TIMESTAMP)=v_createdOn;

				IF NOT FOUND then
				RAISE NOTICE '%','SQL EXCEPTION while updating  voucher status  ='||sqlerrm;
				RAISE EXCEPTION 'SQL EXCEPTION while updating  voucher status';
				end if;  -- end of if SQL%NOTFOUND

				ELSE
				UPDATE voms_vouchers set CON_SUMMARY_UPDATE='Y',MODIFIED_BY='RECHARGESCH',
				MODIFIED_ON=p_modifiedDate
				WHERE  PRODUCT_ID=v_productID
				 and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
				 and USER_NETWORK_CODE=v_userNetworkCode AND FIRST_CONSUMED_BY IS NOT NULL
				 and generation_batch_no=v_generationBatchNo and CON_SUMMARY_UPDATE='N'
				 and current_status=p_batchDaStat and date_trunc('day',MODIFIED_ON::TIMESTAMP)=v_createdOn;

				IF NOT FOUND then
			RAISE NOTICE '%','SQL EXCEPTION while updating  voucher status  ='||sqlerrm;
				RAISE EXCEPTION 'SQL EXCEPTION while updating  voucher status';
				end if;  -- end of if SQL%NOTFOUND

				END IF;
				
			EXCEPTION

			when others then
			 v_ErrMessage:='Not able to update vouchers for PROD. LOC:='||v_productionNetworkCode||' USER_NETWORK_CODE='||v_userNetworkCode||'PRODUCT_ID='||v_productID||'v_voucherStatus='||v_voucherStatus;
			p_returnMessage:='FAILED';
			 RAISE EXCEPTION 'Not able to update vouchers for PROD';
		 END;

		  BEGIN

				if(v_voucherStatus=p_batchConStat) THEN
				--UPDATE VOUCHER_BATCH_SUMMARY set TOTAL_RECHARGED=TOTAL_RECHARGED+v_recharge_count
				UPDATE VOMS_VOUCHER_BATCH_SUMMARY set TOTAL_RECHARGED=TOTAL_RECHARGED+v_tempCount
				WHERE  BATCH_NO =v_generationBatchNo;
				RAISE NOTICE '%','v_tempCount for voucherBatchsummary '||v_productionNetworkCode ||' v_userNetworkCode '||v_userNetworkCode||'is '||v_tempCount||'v_voucherStatus='||v_voucherStatus;

				ELSIF(v_voucherStatus=p_batchDaStat) THEN
				UPDATE VOMS_VOUCHER_BATCH_SUMMARY set TOTAL_STOLEN_DMG_AFTER_EN =TOTAL_STOLEN_DMG_AFTER_EN+v_tempCount
				WHERE  BATCH_NO =v_generationBatchNo;
				RAISE NOTICE '%','v_tempCount for voucherBatchsummary '||v_productionNetworkCode ||' v_userNetworkCode '||v_userNetworkCode||'is '||v_tempCount||'v_voucherStatus='||v_voucherStatus;
				END IF;

				IF NOT FOUND then
			RAISE NOTICE '%','SQL EXCEPTION while checking updating batch summary  ='||sqlerrm;
				RAISE EXCEPTION 'SQL EXCEPTION while checking updating batch summary';
				end if;  -- end of if SQL%NOTFOUND
			EXCEPTION
			
			when others then
			 v_ErrMessage:='Not able to update batch for Batch No:='||v_generationBatchNo||'v_voucherStatus='||v_voucherStatus;
			 RAISE EXCEPTION 'Not able to update batch for Batch No';
			END;


		--block for insertion/updation in voucher_summary
		  
			BEGIN
			 select '1' INTO rcd_count from dual
			 where exists (select 1 from voms_voucher_summary
						 where SUMMARY_DATE=v_createdOn
						 and PRODUCT_ID=v_productID
						 and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
						 and USER_NETWORK_CODE=v_userNetworkCode);

			EXCEPTION
			WHEN NO_DATA_FOUND THEN  --when no row returned for the distributor
				 --DBMS_OUTPUT.PUT_LINE('No Record found ');
				 rcd_count := 0;
			WHEN OTHERS THEN
				 RAISE NOTICE '%','Exception in Record check ';
				 RAISE EXCEPTION 'Exception in Record check';
		   END;
		    BEGIN 
		  if(v_voucherStatus=p_batchConStat) THEN
				if rcd_count = 0 then
				   IF (v_vouchPreviousStatus=p_batchReconcileStat) THEN
						INSERT INTO VOMS_VOUCHER_SUMMARY(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE,USER_NETWORK_CODE,
						TOTAL_RECHARGED,TOTAL_RECONCILED_CHANGED) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_tempCount,v_tempCount);
					    --TOTAL_RECHARGED) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_recharge_count);
					    RAISE NOTICE '%','Insert-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount;
					ELSE
						INSERT INTO VOMS_VOUCHER_SUMMARY(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE,USER_NETWORK_CODE,
						TOTAL_RECHARGED) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_tempCount);
					    --TOTAL_RECHARGED) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_recharge_count);
					    RAISE NOTICE '%','Insert-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount;
					END IF;
		   else
				   IF (v_vouchPreviousStatus=p_batchReconcileStat) THEN
				UPDATE VOMS_VOUCHER_SUMMARY
						SET TOTAL_RECHARGED=TOTAL_RECHARGED+v_tempCount,
						TOTAL_RECONCILED_CHANGED=TOTAL_RECONCILED_CHANGED+v_tempCount
						where SUMMARY_DATE=v_createdOn
						and PRODUCT_ID=v_productID
						and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
						and USER_NETWORK_CODE=v_userNetworkCode;
						RAISE NOTICE '%','update-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount;
					ELSE
				UPDATE VOMS_VOUCHER_SUMMARY
						SET TOTAL_RECHARGED=TOTAL_RECHARGED+v_tempCount
						where SUMMARY_DATE=v_createdOn
						and PRODUCT_ID=v_productID
						and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
						and USER_NETWORK_CODE=v_userNetworkCode;
						RAISE NOTICE '%','update-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount;
					END IF;
				end if;

			ELSIF(v_voucherStatus=p_batchReconcileStat) THEN
				if rcd_count = 0 then
						INSERT INTO VOMS_VOUCHER_SUMMARY(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE,USER_NETWORK_CODE,
						TOTAL_RECONCILED) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_tempCount);
					    RAISE NOTICE '%','Insert-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount;
		   else
				UPDATE VOMS_VOUCHER_SUMMARY
						SET TOTAL_RECONCILED=TOTAL_RECONCILED+v_tempCount
						where SUMMARY_DATE=v_createdOn
						and PRODUCT_ID=v_productID
						and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
						and USER_NETWORK_CODE=v_userNetworkCode;
						RAISE NOTICE '%','update-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount;
				end if;

			ELSE
				if rcd_count = 0 then
				   IF (v_vouchPreviousStatus=p_batchReconcileStat) THEN
						INSERT INTO VOMS_VOUCHER_SUMMARY(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE,USER_NETWORK_CODE,
						TOTAL_STOLEN_DMG_AFTER_EN,TOTAL_RECONCILED_CHANGED) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_tempCount,v_tempCount);
					    RAISE NOTICE '%','Insert-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount;
					ELSE
						INSERT INTO VOMS_VOUCHER_SUMMARY(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE,USER_NETWORK_CODE,
						TOTAL_STOLEN_DMG_AFTER_EN) VALUES(v_createdOn,v_productID,v_productionNetworkCode,v_userNetworkCode,v_tempCount);
					    RAISE NOTICE '%','Insert-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount;
					END IF;
		   else
				   IF (v_vouchPreviousStatus=p_batchReconcileStat) THEN
				UPDATE VOMS_VOUCHER_SUMMARY
						SET TOTAL_STOLEN_DMG_AFTER_EN=TOTAL_STOLEN_DMG_AFTER_EN+v_tempCount,
						TOTAL_RECONCILED_CHANGED=TOTAL_RECONCILED_CHANGED+v_tempCount
						where SUMMARY_DATE=v_createdOn
						and PRODUCT_ID=v_productID
						and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
						and USER_NETWORK_CODE=v_userNetworkCode;
						RAISE NOTICE '%','update-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount;
					ELSE
				UPDATE VOMS_VOUCHER_SUMMARY
						SET TOTAL_STOLEN_DMG_AFTER_EN=TOTAL_STOLEN_DMG_AFTER_EN+v_tempCount
						where SUMMARY_DATE=v_createdOn
						and PRODUCT_ID=v_productID
						and PRODUCTION_NETWORK_CODE=v_productionNetworkCode
						and USER_NETWORK_CODE=v_userNetworkCode;
						RAISE NOTICE '%','update-v_tempCount for vouchersummary '||v_productionNetworkCode ||' v_userNetworkCode'||v_userNetworkCode||'is '||v_tempCount;
					END IF;
				end if;

		  end if;
		  exception
		   
			when others then
			  RAISE NOTICE '%','EXCEPTION CAUGHT while Record test in VOUCHER_SUMMARY='||sqlerrm;
				   RAISE EXCEPTION 'EXCEPTION CAUGHT while Record test in VOUCHER_SUMMARY';
		  END;

		  RAISE NOTICE '%','v_voucherUpdateCount===='||v_voucherUpdateCount;

		  EXCEPTION
		 
		  WHEN OTHERS then
		  RAISE NOTICE '%','Not able to update records '||sqlerrm;
		  RAISE EXCEPTION 'EXCEPTION Not able to update records';

		  END;
		  end loop;
		  RAISE NOTICE '%','Voucher Count='||v_voucherUpdateCount;
		  p_returnMessage:='SUCCESS';
		  p_message :='Successfully Completed updation of '||v_voucherUpdateCount||' vouchers';
		  p_messageToSend :='';
		--COMMIT;  --final commit

	EXCEPTION
	
	WHEN OTHERS THEN
	p_message :='Not able to update Vouchers';
	p_messageToSend :=v_ErrMessage;
	

	RAISE NOTICE '%','Procedure Exiting'||sqlerrm;
	p_returnMessage:='FAILED';
	/* ROLLBACK; */
	END;
$$;


ALTER FUNCTION pretupsdatabase.p_updatesummaryinfo(p_batchconstat character varying, p_batchdastat character varying, p_batchreconcilestat character varying, p_modifieddate timestamp without time zone, OUT p_returnmessage character varying, OUT p_message character varying, OUT p_messagetosend character varying) OWNER TO pgdb;

--
-- Name: p_whitelistdatamgt(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION p_whitelistdatamgt(OUT p_errorcode character varying, OUT p_returnmessage character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
declare 
v_returnmessage varchar(255);
v_errorcode varchar(255);

begin

        begin
		raise notice '%','1 ';
                execute 'alter table white_list rename to white_list_old';
                
        exception
        when others then
                
            raise notice '%','exception 1 '||sqlerrm;
                p_errorcode :=sqlerrm;
                p_returnmessage:='not able to rename table to back up, getting exception='||sqlerrm;
                raise exception 'not able to rename table to back up, getting exception ';
        end;
         
        begin
                 raise notice '%','2 ';
                execute 'alter table white_list_bak rename to white_list';
                
        exception
        when others then
                /* rollback; */
            raise notice '%','exception 2 '||sqlerrm;
                p_errorcode :=sqlerrm;
                p_returnmessage:='not able to rename table white_list_bak to original one white_list , getting exception='||sqlerrm;
                begin
                        raise notice '%','3 ';
                        execute 'alter table white_list_old rename to white_list';
                       
                exception
                when others then
                        /* rollback; */
                    raise notice '%','exception 3 '||sqlerrm;
                                p_errorcode :=sqlerrm;
                                p_returnmessage:='not able to rename back up table to original one, getting exception='||sqlerrm;
                        raise exception 'not able to rename back up table to original one ';
                end;     
                
        end;

        begin
        raise notice '%','4 ';
                 execute  'create table white_list_bak (network_code varchar(2) not null, msisdn  varchar(15) not null, account_id varchar(20) not null,  entry_date timestamp(0)  not null,  account_status varchar(20) not null,  service_class  varchar(20) not null,  credit_limit  decimal(20) not null, interface_id  varchar(15)  not null,  external_interface_code  varchar(15)    not null,   created_on  timestamp(0)   not null,  created_by varchar(20) not null, modified_on   timestamp(0) not null,  modified_by  varchar(20) not null, status    varchar(2) not null,activated_on timestamp(0) not null,activated_by  varchar(20) not null, movement_code  varchar(20) not null, language   varchar(2) not null, country  varchar(2) not null, imsi   varchar(20), 
                 CONSTRAINT PK_WHITE_LIST PRIMARY KEY (MSISDN) ) ';

        exception
        when others then
                /* rollback; */
            raise notice '%','exception  '||sqlerrm;
                p_errorcode :=sqlerrm;
                p_returnmessage:='not able to create table white_list_bak, getting exception='||sqlerrm;
                begin
                raise notice '%','5 ';
                                execute  'drop table white_list_old';
                exception
                        when others then
                                /* rollback; */
                            raise notice '%','exception  '||sqlerrm;
                                p_errorcode :=sqlerrm;
                                p_returnmessage:='not able to drop backup table, getting exception='||sqlerrm;
                                raise exception 'not able to drop backup table ';
                end;     
        end;
 
        begin
         raise notice '%','6 ';
                execute 'drop table white_list_old';
               
        exception
        when others then
                /* rollback; */
            raise notice '%','exception  '||sqlerrm;
                p_errorcode :=sqlerrm;
                p_returnmessage:='not able to drop backup table, getting exception='||sqlerrm;
                raise exception 'not able to drop backup table ';
        end;
        p_errorcode :=v_errorcode;
        p_returnmessage:=v_returnmessage;

        exception
        when others then
                /* rollback; */
            raise notice '%','exception  '||sqlerrm;
                p_errorcode :=v_errorcode;
                p_returnmessage:=v_returnmessage;
end;
$$;


ALTER FUNCTION pretupsdatabase.p_whitelistdatamgt(OUT p_errorcode character varying, OUT p_returnmessage character varying) OWNER TO pgdb;

--
-- Name: p_yearendprocess(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION p_yearendprocess(OUT p_returnmessage character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE 
/* The p_yearEndProcess make entries in the IDS table for the new year
 and can be executed only on the first or last day of the financial year.
 It finds records for the last year and updates for the new year.
 The procedure cannot be executed two times for one financial year.
*/
v_prevYear ids.id_year%TYPE;
v_year int;
v_maxYear int;
v_newYear int;
v_idType ids.id_type%TYPE;
v_idsNetworkCode ids.network_code%type;
currentDay varchar(2);
currentMonth varchar(2);
currentYear varchar(4);
v_currentDay int;
v_currentMonth int;
v_currentYear int;
v_returnMessage varchar(255);
DECLARE c_idTypes CURSOR FOR
		SELECT id_type, network_code,frequency FROM ids
			WHERE id_year=(SELECT max(id_year) FROM ids WHERE id_year!='ALL');

BEGIN

	   SELECT to_char(current_timestamp,'dd') INTO  currentDay;
	   SELECT to_char(current_timestamp,'mm') INTO  currentMonth;
	   SELECT to_char(current_timestamp,'yyyy') INTO  currentYear;
	   v_currentDay :=TO_NUMBER(currentDay,'99');
	   v_currentMonth :=TO_NUMBER(currentMonth,'99');
	   v_currentYear:=TO_NUMBER(currentYear,'9999');
	   RAISE NOTICE '%','v_currentYear='||v_currentYear||' v_currentMonth='||v_currentMonth||' v_currentDay='||v_currentDay;
	   SELECT max(id_year) INTO v_prevYear FROM ids WHERE id_year<>'ALL';
	   RAISE NOTICE '%','STARTED'||TO_NUMBER(v_prevYear,'9999');
	   v_year :=TO_NUMBER(v_prevYear,'9999');

	   v_newYear:=v_year+1;
	   RAISE NOTICE '%','STARTED v_newYear= '||v_newYear;
	   --update queries for ids table


	 IF ((v_currentDay=31 AND v_currentMonth=3) OR (v_currentday=1 AND v_currentMonth=4)) THEN

	   BEGIN
		   Begin
				SELECT max(id_year) INTO v_maxYear FROM ids WHERE id_year<>'ALL';
			    IF (v_currentDay=31 AND v_currentMonth=3 AND v_maxYear<=v_currentYear) THEN
					FOR ids_type_cur IN c_idTypes
						LOOP
						  BEGIN
							v_idType :=ids_type_cur.id_type;
							v_idsNetworkCode :=ids_type_cur.network_code;
							RAISE NOTICE '%','ID_TYPE='||v_idType||'   NETWORK_CODE='||v_idsNetworkCode||'   FREQUENCY='||ids_type_cur.frequency;
							INSERT INTO ids(id_year, id_type, network_code, last_no,frequency)
							VALUES (v_newYear,v_idType,v_idsNetworkCode,0,ids_type_cur.frequency);
							
						  EXCEPTION
					 	  	WHEN others THEN
					  	 	RAISE NOTICE '%','EXCEPTION IN INSERTING NEW RECORD FOR '||v_idType;
						 	v_returnMessage:='EXCEPTION IN INSERTING NEW RECORD FOR '||v_idType;
						 	RAISE EXCEPTION 'IDtype % SQL Insert Exception ',v_idType;
						  END;
						END LOOP;
				ELSIF(v_currentDay=1 AND v_currentMonth=4 AND v_maxYear<v_currentYear) THEN
					FOR ids_type_cur IN c_idTypes
						LOOP
						  BEGIN
							v_idType :=ids_type_cur.id_type;
							v_idsNetworkCode :=ids_type_cur.network_code;
							RAISE NOTICE '%','ID_TYPE='||v_idType||'   NETWORK_CODE='||v_idsNetworkCode||'   FREQUENCY='||ids_type_cur.frequency;
							INSERT INTO ids(id_year, id_type, network_code, last_no,frequency)
							VALUES (v_newYear,v_idType,v_idsNetworkCode,0,ids_type_cur.frequency);
						  EXCEPTION
					 	  	WHEN others THEN
					  	 	RAISE NOTICE '%','EXCEPTION IN INSERTING NEW RECORD FOR '||v_idType;
						 	v_returnMessage:='EXCEPTION IN INSERTING NEW RECORD FOR '||v_idType;
						 	RAISE EXCEPTION 'IDtype % SQL Insert Exception. ',v_idType;
						  END;
						END LOOP;
				END IF;
		   EXCEPTION

		     WHEN others THEN
		      RAISE NOTICE '%','EXCEPTION  '||sqlerrm;
 			  v_returnMessage:='Not able to update the id series entries';
			 RAISE EXCEPTION 'IDtype % SQL Insert Exception.. ',v_idType;
			END;



		v_returnMessage:='Records successfully updated for new financial year';
		/* COMMIT; */

	   EXCEPTION
	     WHEN others THEN
	      RAISE NOTICE '%','EXCEPTION  '||sqlerrm;
		 RAISE EXCEPTION 'SQL  Exception ';
	  END;

	  ELSE
	  	  v_returnMessage:='Program can run only on the First day or the Last day of the financial year';


	  END IF;

	p_returnMessage:=v_returnMessage;

	EXCEPTION

	WHEN others THEN
	   	/* ROLLBACK; */
	    RAISE NOTICE '%','EXCEPTION  '||sqlerrm;
		p_returnMessage:=v_returnMessage;
END;
$$;


ALTER FUNCTION pretupsdatabase.p_yearendprocess(OUT p_returnmessage character varying) OWNER TO pgdb;

--
-- Name: partition_table_insert_trigger(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION partition_table_insert_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
DECLARE
sql TEXT;
table_name TEXT;
partition_key TEXT;
new_partition_key timestamp without time zone;

BEGIN
table_name := TG_ARGV[0];
partition_key := TG_ARGV[1];

 EXECUTE format('SELECT ($1).%s', partition_key)
   USING NEW
   INTO  new_partition_key;
   
    IF ( new_partition_key >= DATE '2017-05-01' AND
         new_partition_key < DATE '2017-05-16' ) THEN
        sql := 'INSERT INTO '|| table_name ||'_may1 VALUES ($1.*);';        
    ELSIF(new_partition_key >= DATE '2017-05-16' AND
         new_partition_key < DATE '2017-06-01' ) THEN
        sql := 'INSERT INTO '|| table_name ||'_may2 VALUES ($1.*);';  

ELSIF ( new_partition_key >= DATE '2017-06-01' AND
         new_partition_key < DATE '2017-06-16' ) THEN
        sql := 'INSERT INTO '|| table_name ||'_jun1 VALUES ($1.*);';  
    ELSIF ( new_partition_key >= DATE '2017-06-16' AND
         new_partition_key < DATE '2017-07-01' ) THEN
        sql := 'INSERT INTO '|| table_name ||'_jun2 VALUES ($1.*);';
        
    ELSIF ( new_partition_key >= DATE '2017-07-01' AND
         new_partition_key < DATE '2017-07-16' ) THEN
        sql := 'INSERT INTO '|| table_name ||'_jul1 VALUES ($1.*);';  
    ELSIF ( new_partition_key >= DATE '2017-07-16' AND
         new_partition_key < DATE '2017-08-01' ) THEN
        sql := 'INSERT INTO '|| table_name ||'_jul2 VALUES ($1.*);';  
    ELSIF ( new_partition_key >= DATE '2017-08-01' AND
         new_partition_key < DATE '2017-08-16' ) THEN
        sql := 'INSERT INTO '|| table_name ||'_aug1 VALUES ($1.*);';  
    ELSIF ( new_partition_key >= DATE '2017-08-16' AND
         new_partition_key < DATE '2017-09-01' ) THEN
        sql := 'INSERT INTO '|| table_name ||'_aug2 VALUES ($1.*);';
     ELSIF ( new_partition_key >= DATE '2017-09-01' AND
         new_partition_key < DATE '2017-09-16' ) THEN
        sql := 'INSERT INTO '|| table_name ||'_sep1 VALUES ($1.*);';  
    ELSIF ( new_partition_key >= DATE '2017-09-16' AND
         new_partition_key < DATE '2017-10-01' ) THEN
        sql := 'INSERT INTO '|| table_name ||'_sep2 VALUES ($1.*);'; 
    ELSIF ( new_partition_key >= DATE '2017-10-01' AND
         new_partition_key < DATE '2017-10-16' ) THEN
        sql := 'INSERT INTO '|| table_name ||'_oct11 VALUES ($1.*);';  
    ELSIF ( new_partition_key >= DATE '2017-10-16' AND
         new_partition_key < DATE '2017-11-01' ) THEN
        sql := 'INSERT INTO '|| table_name ||'_oct2 VALUES ($1.*);';   
    ELSE
        RAISE EXCEPTION 'Date out of range.  Fix the Partition_table_insert_trigger() function!';
    END IF;
    Execute sql USING NEW;
    RETURN NULL;
END;
$_$;


ALTER FUNCTION pretupsdatabase.partition_table_insert_trigger() OWNER TO pgdb;

--
-- Name: partition_table_update_trigger(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION partition_table_update_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
DECLARE
sql TEXT;
table_name TEXT;
partition_name TEXT;
partition_key TEXT;
primary_key TEXT;
BEGIN

partition_name := TG_ARGV[0];
table_name := TG_ARGV[1];
partition_key := TG_ARGV[2];
primary_key:=TG_ARGV[3];

 IF ( 'NEW.'||partition_key != 'OLD.'||partition_key ) THEN
	sql := 'DELETE FROM '||partition_name||'
	WHERE $1.'||partition_key||'='||partition_key||' AND $1.'||primary_key||'='||primary_key||';';
	EXECUTE sql USING OLD;
        sql:='INSERT INTO '||table_name||' VALUES ($1.*);';
        EXECUTE sql USING NEW;
        RETURN NULL;
    ELSE
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$_$;


ALTER FUNCTION pretupsdatabase.partition_table_update_trigger() OWNER TO pgdb;

--
-- Name: remove_duplicate_indexes(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION remove_duplicate_indexes(OUT return_message character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
declare
duplicate_index_list  TEXT[];
duplicate_index TEXT;

--cursor for finding indexes columnwise
duplicate_indexed_column_list  CURSOR
FOR SELECT array_agg(indexrelid::regclass) AS Indexes
FROM pg_index  GROUP BY indrelid ,indkey  HAVING COUNT(*) > 1;

counter INTEGER := 0 ; 
DROP_INDEX_QRY TEXT;

BEGIN
--looping columns
for duplicate_indexed_column in duplicate_indexed_column_list
loop
		counter := 0; 
		duplicate_index_list  := duplicate_indexed_column.Indexes;
		RAISE NOTICE  'Duplicate indexes found: %', duplicate_index_list;
		--looping each indexes of column 
		--Excluding 1st index removing other indexes
		--But if any exception comes incase of primary key, it should not be deleted so in that case rather than pk_index, 1st index should delete.
		
		FOREACH duplicate_index IN ARRAY duplicate_index_list
		LOOP
			IF (counter > 0) THEN
			BEGIN
			DROP_INDEX_QRY := 'DROP INDEX '|| duplicate_index;
			RAISE NOTICE  'Droping index: %', DROP_INDEX_QRY;
			EXECUTE DROP_INDEX_QRY;
			EXCEPTION
			WHEN OTHERS THEN
			RAISE NOTICE 'EXCEPTION while deleting duplicate Index: %, %' , SQLERRM, DROP_INDEX_QRY;    

				BEGIN
				RAISE NOTICE 'oth index %' ,  duplicate_index_list[1]  ; 
				DROP_INDEX_QRY := 'DROP INDEX '|| duplicate_index_list[1] ;
				RAISE NOTICE  'Droping index: %', DROP_INDEX_QRY;
				EXECUTE DROP_INDEX_QRY;
				EXCEPTION
				WHEN OTHERS THEN
				RAISE NOTICE 'EXCEPTION while deleting oth index: %, %' , SQLERRM, DROP_INDEX_QRY;    
				END;
			
			END;
			END IF; 
			counter := counter + 1 ; 
		END LOOP;
END LOOP;

RAISE NOTICE  'Duplicate Indexes in Pretups deleted successfully';
RETURN_MESSAGE :=  'Duplicate Indexes in Pretups deleted successfully';
EXCEPTION
WHEN OTHERS THEN
RAISE NOTICE 'EXCEPTION while deleting duplicate Indexes in Pretups: %' , SQLERRM;    
RETURN_MESSAGE :='EXCEPTION: '||SQLERRM;

END;
$$;


ALTER FUNCTION pretupsdatabase.remove_duplicate_indexes(OUT return_message character varying) OWNER TO pgdb;

--
-- Name: replace_msisdn_from_table(character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION replace_msisdn_from_table(p_tablename character varying, p_partitionname character varying DEFAULT 'XX'::character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$
 declare
cmt_frqncy varchar(5);
update_str varchar(400);
execute_stmt varchar(800);
column_names cursor  is select column_names from migrate_msisdn where table_name=p_tablename;

begin


cmt_frqncy:=10000;
update_str:='';
--open column_names;
for clmn in column_names
	loop
		update_str:=update_str||clmn.column_names||'='||'evaluate_new_msisdn(old_'||clmn.column_names||'), ';
	end loop;
update_str:=update_str||' msisdn_modified=:1';

if p_partitionname='XX' then
	execute_stmt:='update '|| p_tablename || ' set ' ||update_str||
	' where msisdn_modified=:2 and rownum<=' || cmt_frqncy;
else
	execute_stmt:='update '|| p_tablename || ' partition (' || p_partitionname || ')'||' set '||
	update_str||' where msisdn_modified=:2 and rownum<=' || cmt_frqncy;
end if;

RAISE NOTICE '%','executing stmt: '||execute_stmt;

loop
	execute immediate execute_stmt using 'Y';
	exit when sql%rowcount=0;
	/* commit; */
end loop;

/* commit; */

end;
$$;


ALTER FUNCTION pretupsdatabase.replace_msisdn_from_table(p_tablename character varying, p_partitionname character varying) OWNER TO pgdb;

--
-- Name: rp2pdwhtempprc(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION rp2pdwhtempprc(p_date timestamp without time zone, OUT p_mastercnt integer, OUT p_chtranscnt integer, OUT p_c2stranscnt integer, OUT p_message character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE 


BEGIN

        RAISE NOTICE '%','START RP2P DWH PROC1';
	RAISE NOTICE '%','START RP2P DWH PROC P_MASTERCNT 1' ;
        TRUNCATE  TEMP_RP2P_DWH_MASTER;
        TRUNCATE TABLE TEMP_RP2P_DWH_CHTRANS;
	TRUNCATE TABLE TEMP_RP2P_DWH_C2STRANS;

 RAISE NOTICE '%','START RP2P DWH PROC...........P_MASTERCNT.................2' ;


     INSERT INTO TEMP_RP2P_DWH_MASTER ( SRNO, DATA )
    (SELECT row_number() over() as a ,(U.USER_ID||','||PARENT_ID||','||OWNER_ID||','||USER_TYPE||','||case when EXTERNAL_CODE is null then '' else EXTERNAL_CODE end
||','||case when MSISDN is null then '' else MSISDN end
    ||','||REPLACE(L.LOOKUP_NAME,',',' ')||','||REPLACE(case when LOGIN_ID is null then '' else LOGIN_ID end,',',' ')||','||U.CATEGORY_CODE||','||CAT.CATEGORY_NAME||','||
    UG.GRPH_DOMAIN_CODE||','||REPLACE(GD.GRPH_DOMAIN_NAME,',',' ')||','||
    REPLACE(USER_NAME,',',' ')||','||REPLACE(CITY,',',' ')||','||REPLACE(STATE,',',' ')||','||REPLACE(COUNTRY,',',' ')||',' ||',' ) DATA1 FROM USERS U, 

CATEGORIES CAT,USER_GEOGRAPHIES UG,GEOGRAPHICAL_DOMAINS GD,LOOKUPS L, LOOKUP_TYPES LT
    WHERE U.USER_ID=UG.USER_ID AND U.CATEGORY_CODE=CAT.CATEGORY_CODE AND U.STATUS<>'C'
    AND UG.GRPH_DOMAIN_CODE=GD.GRPH_DOMAIN_CODE AND L.LOOKUP_CODE=U.STATUS
    AND LT.LOOKUP_TYPE='URTYP' AND LT.LOOKUP_TYPE=L.LOOKUP_TYPE AND date_trunc('day',u.created_on::TIMESTAMP)<=p_date
    AND USER_TYPE='CHANNEL');
    
    P_MASTERCNT=(SELECT MAX(SRNO) FROM TEMP_RP2P_DWH_MASTER);
 RAISE NOTICE '%','START RP2P DWH PROC...........P_MASTERCNT.......' || P_MASTERCNT;


 RAISE NOTICE '%','START RP2P DWH PROC...........P_MASTERCNT.................3' ;

  INSERT INTO TEMP_RP2P_DWH_CHTRANS ( SRNO, DATA )
    (SELECT row_number() over() as a,DATA1 FROM (SELECT(CT.TRANSFER_ID
||','||case when REQUEST_GATEWAY_TYPE is null then '' else REQUEST_GATEWAY_TYPE end||','||TO_CHAR(CT.TRANSFER_DATE,'DD/MM/YYYY')
  ||','||TO_CHAR(CT.CREATED_ON,'DD/MM/YYYY HH12:MI:SS PM')||','||CT.NETWORK_CODE
 ||','||CT.TRANSFER_TYPE||','||CT.TRANSFER_SUB_TYPE||','||CT.TRANSFER_CATEGORY
   ||','||CT.TYPE||','||CT.FROM_USER_ID||','||CT.TO_USER_ID||','||case when CT.MSISDN is null then '' else CT.MSISDN::varchar(40) end ||','||case when CT.TO_MSISDN is null then '' else CT.TO_MSISDN::varchar(40) end 
   ||','||CT.SENDER_CATEGORY_CODE||','||CT.RECEIVER_CATEGORY_CODE
  ||','||  CTI.SENDER_DEBIT_QUANTITY||','||CTI.RECEIVER_CREDIT_QUANTITY||','||CT.TRANSFER_MRP
  ||','||CTI.MRP||','||CTI.PAYABLE_AMOUNT||','||CTI.NET_PAYABLE_AMOUNT||','||0
  ||','||CTI.TAX1_VALUE||','||CTI.TAX2_VALUE||','||CTI.TAX3_VALUE||','||CTI.COMMISSION_VALUE
  ||','||','||','||case when CT.EXT_TXN_NO is null then '' else CT.EXT_TXN_NO end||','||case when TO_CHAR(CT.EXT_TXN_DATE,'DD/MM/YYYY') is null then COALESCE(to_char(CT.EXT_TXN_DATE, 'DD/MM/YYYY'), '') else TO_CHAR(CT.EXT_TXN_DATE,'DD/MM/YYYY') end||','||','||CTI.PRODUCT_CODE||','||','
  || CASE CT.STATUS  WHEN 'CLOSE' THEN '200' ELSE '240' END ||','||','||','||','||','||','||','||','||','
   ||','||case when CT.CELL_ID is null then '' else CT.CELL_ID end
   ||','||CTI.SENDER_POST_STOCK||','||CTI.SENDER_PREVIOUS_STOCK||','||CTI.RECEIVER_POST_STOCK||','||CTI.RECEIVER_PREVIOUS_STOCK||','||','
   ||','||case when CT.SOS_STATUS is null then '' else CT.SOS_STATUS end
  ||','|| case when to_char(CT.SOS_SETTLEMENT_DATE,'DD/MM/YYYY') is null then COALESCE(to_char(CT.SOS_SETTLEMENT_DATE, 'DD/MM/YYYY'), '') else to_char(CT.SOS_SETTLEMENT_DATE, 'DD/MM/YYYY') end
  ||','|| case when CTI.otf_type is null then '' else CTI.otf_type end ||','|| case when CTI.otf_rate is null then '' else CTI.otf_rate end ||','|| case when CTI.otf_amount is null then '' else CTI.otf_amount end 
) DATA1    

            FROM CHANNEL_TRANSFERS CT LEFT OUTER JOIN   CHANNEL_TRANSFERS_ITEMS CTI ON CT.TRANSFER_ID=CTI.TRANSFER_ID
    WHERE 
     CT.STATUS IN('CLOSE','CNCL') AND date_trunc('day',ct.close_date::TIMESTAMP)=p_date
    ORDER BY CT.MODIFIED_ON,CT.TYPE) as d1);
    

    P_CHTRANSCNT= (SELECT MAX(SRNO)  FROM TEMP_RP2P_DWH_CHTRANS);
RAISE NOTICE '%','START RP2P DWH PROC...........P_CHTRANSCNT' || P_CHTRANSCNT;

 RAISE NOTICE '%','START RP2P DWH PROC...........P_MASTERCNT.................4' ;

   


   	 INSERT INTO TEMP_RP2P_DWH_C2STRANS ( SRNO, DATA,TRANSFER_STATUS)
    (SELECT row_number() over() as a,DATA1,TRANSFER_STATUS FROM (SELECT (CT.TRANSFER_ID||','||REQUEST_GATEWAY_TYPE||','||TO_CHAR
(CT.TRANSFER_DATE,'DD/MM/YYYY')
    ||','||TO_CHAR(CT.TRANSFER_DATE_TIME,'DD/MM/YYYY HH12:MI:SS PM')||','||CT.NETWORK_CODE||','||CT.SERVICE_TYPE||','||','|| 'SALE'||','||'C2S'||','||
CT.SENDER_ID||','||','||CT.SENDER_MSISDN||','||CT.RECEIVER_MSISDN||','||
    CT.SENDER_CATEGORY||','||','||CT.SENDER_TRANSFER_VALUE||','||CT.RECEIVER_TRANSFER_VALUE||','
  ||CT.TRANSFER_VALUE||','||CT.QUANTITY||','||','||','|| CT.RECEIVER_ACCESS_FEE||','
   || CT.RECEIVER_TAX1_VALUE||','||CT.RECEIVER_TAX2_VALUE||','||0||','||','||case when CT.DIFFERENTIAL_APPLICABLE is null then '' else CT.DIFFERENTIAL_APPLICABLE  end||','
    ||case when CT.DIFFERENTIAL_GIVEN is null then '' else CT.DIFFERENTIAL_GIVEN end||','||','||','||','||CT.PRODUCT_CODE||','||case when CT.CREDIT_BACK_STATUS is null then '' else CT.CREDIT_BACK_STATUS end ||','||CT.TRANSFER_STATUS
    ||','||CT.RECEIVER_BONUS_VALUE||','||CT.RECEIVER_VALIDITY||','||CT.RECEIVER_BONUS_VALIDITY||','
    ||case when CT.SERVICE_CLASS_CODE is null then '' else CT.SERVICE_CLASS_CODE end||','||case when CT.INTERFACE_ID is null then '' else CT.INTERFACE_ID end||','||case when CT.CARD_GROUP_CODE is null then '' else CT.CARD_GROUP_CODE end
   ||','||REPLACE(case when KV.VALUE is null then '' else KV.VALUE end,',',' ')
  ||','|| case when CT.SERIAL_NUMBER is null then '' else CT.SERIAL_NUMBER end
  ||','||case when CT.INTERFACE_REFERENCE_ID is null then '' else CT.INTERFACE_REFERENCE_ID end||','
   || case when CT.CELL_ID is null then '' else CT.CELL_ID end ||','
   || case when CT.SENDER_POST_BALANCE is null then '' else CT.SENDER_POST_BALANCE::varchar(20) end||','||case when CT.SENDER_PREVIOUS_BALANCE is null then '' else CT.SENDER_PREVIOUS_BALANCE::varchar(20) end
   ||','||case when CT.RECEIVER_POST_BALANCE is null then '' else CT.RECEIVER_POST_BALANCE::varchar(20) end||','||case when CT.RECEIVER_PREVIOUS_BALANCE is null then '' else CT.RECEIVER_PREVIOUS_BALANCE::varchar(20) end||','
   ||case when CT.REVERSAL_ID is null then '' else CT.REVERSAL_ID end||','
   || case when CT.SUB_SERVICE is null then '' else CT.SUB_SERVICE end ||','
   ) 
   DATA1,CT.TRANSFER_STATUS TRANSFER_STATUS
    FROM KEY_VALUES KV right outer join C2S_TRANSFERS CT on (KV.KEY=CT.ERROR_CODE AND KV.TYPE='C2S_ERR_CD'),Service_Type_Selector_Mapping STSM WHERE CT.TRANSFER_DATE=p_date  AND stsm.SELECTOR_CODE=CT.SUB_SERVICE AND stsm.SERVICE_TYPE=CT.SERVICE_TYPE
     ORDER BY CT.TRANSFER_DATE_TIME
	)as d2);

    P_C2STRANSCNT=(SELECT MAX(SRNO)  FROM TEMP_RP2P_DWH_C2STRANS);
RAISE NOTICE '%','START RP2P DWH PROC...........P_C2STRANSCNT' || P_C2STRANSCNT;

    RAISE NOTICE '%','RP2P DWH PROC COMPLETED';
    P_MESSAGE:='SUCCESS';

    EXCEPTION
                
                 WHEN OTHERS THEN
                        P_MESSAGE:='NOT ABLE TO MIGRATE DATA, EXCEPTION OCCOURED';
                       

END;
$$;


ALTER FUNCTION pretupsdatabase.rp2pdwhtempprc(p_date timestamp without time zone, OUT p_mastercnt integer, OUT p_chtranscnt integer, OUT p_c2stranscnt integer, OUT p_message character varying) OWNER TO pgdb;

--
-- Name: sp_chnl_users_bal_mismatch(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION sp_chnl_users_bal_mismatch(OUT v_errorcode character varying, OUT v_message character varying, v_processed_upto timestamp without time zone, OUT v_amount integer) RETURNS record
    LANGUAGE plpgsql
    AS $$
    DECLARE 
       cursor1 CURSOR
      IS
         	 SELECT user_id, product_code, balance FROM user_balances;
	        
	       mismatchstring   character varying (200);
	       userid           user_balances.user_id%TYPE;
	       productcode      user_balances.product_code%TYPE;
	       tmpbalance       user_balances.balance%TYPE;
	       currentbalance   user_balances.balance%TYPE;
	       closingbalance   user_balances.balance%TYPE;
	       channelinamt     user_balances.balance%TYPE;
	       channeloutamt    user_balances.balance%TYPE;
	       c2soutamt        user_balances.balance%TYPE;
	       diffdramt        user_balances.balance%TYPE;
	       diffamt          user_balances.balance%TYPE;
	       reconamt         user_balances.balance%TYPE;
	       v_from_date      TIMESTAMP(0);
	       --mainexception    EXCEPTION;
           
		   BEGIN

           		SET TRANSACTION READ ONLY;
          		FOR t_r IN cursor1
       				LOOP
          				userid := t_r.user_id;
						productcode := t_r.product_code;
						currentbalance := -t_r.balance;
          --(Total IN transactions-Total OUT transactions-balance) should be zero
          		   	  	RAISE NOTICE '%', 'Initial userid ='|| userid|| ' currentbalance='|| currentbalance||' productcode='||productcode;
           				
						BEGIN
						--getting max date for which balance of all users is available
								  SELECT MIN (balance_date) INTO v_from_date 
								  FROM (SELECT   ub.user_id, MAX (ub.balance_date) balance_date FROM user_daily_balances ub, users u
                        		  	   			 WHERE u.user_id = ub.user_id AND u.status = 'Y' OR (u.status = 'N' AND date_trunc('day',u.modified_on::TIMESTAMP) >= v_processed_upto )
                     			  GROUP BY ub.user_id) AS A;
              					  RAISE NOTICE '%', 'v_from_date=' || v_from_date;
          			   EXCEPTION
             		   WHEN OTHERS THEN
                	   		v_errorcode := '3575';
                			v_message :='Balance mismatch process executed for user balance. Exception in getting max from date.' || SQLERRM;
                			RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
                			RAISE EXCEPTION  using errcode = 'ERR05';
          			   END;
                       
					   BEGIN
             --getting current balances of the users
		 		            SELECT COALESCE (SUM (CASE WHEN u.status = 'Y' THEN balance WHEN (u.status = 'N' AND date_trunc('day',u.modified_on::TIMESTAMP) >= v_processed_upto ) THEN balance ELSE 0 END),0) INTO currentbalance
               				FROM user_balances ub, users u  WHERE ub.user_id = u.user_id
							 AND u.user_id = userid AND ub.product_code = productcode;
 							 RAISE NOTICE '%',   'userid='|| userid|| ' currentbalance='|| currentbalance|| ' productcode='|| productcode;
          			  EXCEPTION
					  WHEN OTHERS THEN
					  	   v_errorcode := '3576';
                		   v_message :='Balance mismatch process executed for user balance. Exception in getting current balance of the users.'|| SQLERRM;
						   RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
						   RAISE EXCEPTION  using errcode = 'ERR05';
          		 END; 
                 
				 BEGIN
             --getting prev balance
              		   SELECT COALESCE (SUM (CASE WHEN u.status = 'Y' THEN balance WHEN u.status = 'N' AND date_trunc('day',u.modified_on::TIMESTAMP) >= v_processed_upto THEN balance ELSE 0 END), 0) INTO closingbalance
               		   FROM user_daily_balances ub, users u
					   WHERE ub.user_id = u.user_id AND ub.balance_date = v_from_date
					   AND u.user_id = userid AND ub.product_code = productcode;
  					   RAISE NOTICE '%', 'closingbalance=' || closingbalance;
				EXCEPTION
				WHEN OTHERS THEN
					 v_errorcode := '3572';
					 v_message :='Balance mismatch process executed for user balance. Exception in getting Closing(prev) balance.'|| SQLERRM;
					 RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
					 RAISE EXCEPTION  using errcode = 'ERR05';
          	  END;
 			  
			  BEGIN
	              	SELECT /*+ INDEX ( CTI UK_CHNL_TRANSFER_ITEMS )*/
	                COALESCE (SUM (CASE  WHEN u.status = 'Y' THEN cti.approved_quantity WHEN u.status = 'N' AND date_trunc('day',u.modified_on::TIMESTAMP) >= v_processed_upto  THEN cti.approved_quantity ELSE 0   END ),  0 )  INTO channelinamt
					FROM channel_transfers ct, channel_transfers_items cti, users u
					WHERE ct.status = 'CLOSE'
					AND ct.transfer_id = cti.transfer_id
					AND ct.to_user_id = u.user_id
	                  --close date instead of transfer date is being used as transfer_date is not being updated at approval time
	                -- 1 is added as we need to consider the transaction from the next day for which closing balance was found
					AND date_trunc('day',ct.close_date::TIMESTAMP) >= v_from_date + interval '1' day
	                AND u.user_id = userid
	                AND cti.product_code = productcode;
	 				RAISE NOTICE '%', 'channelInAmt=' || channelinamt;
             EXCEPTION
             WHEN OTHERS THEN
					 v_errorcode := '3560';
	                 v_message :='Balance mismatch process executed for user balance. Exception in getting Channle In transactions.'|| SQLERRM;
					 RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
					 RAISE EXCEPTION  using errcode = 'ERR05';
             END;
 		  
		  	 BEGIN
	             	  SELECT /*+ INDEX ( CTI UK_CHNL_TRANSFER_ITEMS )*/
	                  COALESCE(SUM(CASE WHEN u.status = 'Y' THEN cti.approved_quantity WHEN u.status = 'N' AND date_trunc('day',u.modified_on::TIMESTAMP) >= v_processed_upto THEN cti.approved_quantity ELSE 0 END),0) INTO channeloutamt               FROM channel_transfers ct, channel_transfers_items cti, users u
					  WHERE ct.status = 'CLOSE'  AND ct.transfer_id = cti.transfer_id AND ct.from_user_id = u.user_id
	                --close date instead of transfer date is being used as transfer_date is not being updated at approval time
	                -- 1 is added as we need to consider the transaction from the next day for which closing balance was found
	                AND date_trunc('day',ct.close_date::TIMESTAMP) >= v_from_date + interval '1' day
	                AND u.user_id = userid AND cti.product_code = productcode;
	  				RAISE NOTICE '%', 'channelOutAmt=' || channeloutamt;
          	EXCEPTION
            WHEN OTHERS THEN
				 	v_errorcode := '3561';
					v_message :='Balance mismatch process executed for user balance. Exception in getting Channel Out transactions.'|| SQLERRM;
	                RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
					RAISE EXCEPTION  using errcode = 'ERR05';
			END;
 
 			BEGIN
				 	  SELECT COALESCE(SUM(CASE WHEN (cs.transfer_status = '250') THEN (CASE WHEN (credit_back_status != '200') THEN (CASE WHEN u.status = 'N' AND date_trunc('day',u.modified_on::TIMESTAMP) >= v_processed_upto THEN cs.quantity WHEN u.status <> 'N' THEN cs.quantity ELSE 0 END) ELSE 0 END) ELSE cs.transfer_value END),0) INTO c2soutamt
					  FROM c2s_transfers cs, users u
		              WHERE cs.sender_id = u.user_id AND cs.transfer_status <> '206' AND cs.transfer_date >= v_from_date + interval '1' day
					  AND u.user_id = userid AND cs.product_code = productcode;
  					  RAISE NOTICE '%', 'c2sOutAmt=' || c2soutamt;
			EXCEPTION
            WHEN OTHERS THEN
				 		v_errorcode := '3562';
						v_message :='Balance mismatch process executed for user balance. Exception in getting C2S Out transactions.'|| SQLERRM;
						RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
						RAISE EXCEPTION  using errcode = 'ERR05';
          	END;
 
          BEGIN
		  	   		SELECT COALESCE (SUM (CASE WHEN u.status = 'N' AND date_trunc('day',u.modified_on::TIMESTAMP) >= v_processed_upto THEN cs.quantity WHEN u.status <> 'N' THEN cs.quantity ELSE 0 END), 0) INTO reconamt
					FROM c2s_transfers cs, users u
					WHERE cs.sender_id = u.user_id
	                AND cs.reconciliation_flag = 'Y'
	                AND cs.reconciliation_date >= v_from_date + interval '1' day
	                AND cs.transfer_status = '200'
	                AND cs.transfer_date < cs.reconciliation_date
	                AND u.user_id = userid AND cs.product_code = productcode;
 					RAISE NOTICE '%', 'reconAmt=' || reconamt;
          EXCEPTION
		  		   WHEN OTHERS THEN
				   			   v_errorcode := '3577';
							   v_message :='Balance mismatch process executed for user balance. Exception in getting reconciliation transactions.'|| SQLERRM;
							   RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
							   RAISE EXCEPTION  using errcode = 'ERR05';
		   END;
 
           BEGIN
             --getting differential transactions of a particular channel user and a particular product
             		   SELECT COALESCE(SUM((CASE WHEN (stock_updated = 'Y') THEN (CASE WHEN u.status = 'Y' THEN d.transfer_value WHEN u.status = 'N' AND date_trunc('day',u.modified_on::TIMESTAMP) >= v_processed_upto
 					   THEN d.transfer_value ELSE 0 END ) ELSE 0 END ) ), 0 ) INTO diffamt FROM adjustments d, users u WHERE d.user_id = u.user_id
 					   AND d.entry_type = 'CR'
 					   AND d.adjustment_date >= v_from_date + interval '1' day
		               AND u.user_id = userid
		               AND d.product_code = productcode;
 					   RAISE NOTICE '%', 'diffAmt=' || diffamt;
             EXCEPTION
             WHEN OTHERS THEN
			 	  		 v_errorcode := '3574';
						 v_message :='Balance mismatch process executed for user balance. Exception in getting Differential transactions.'|| SQLERRM;
						 RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
						 RAISE EXCEPTION  using errcode = 'ERR05';
			END;
 
            BEGIN
             --getting differential transactions of a particular channel user and a particular product
		             SELECT COALESCE(SUM((CASE WHEN (stock_updated = 'Y') THEN (CASE WHEN u.status = 'Y' THEN d.transfer_value WHEN u.status = 'N' AND date_trunc('day',u.modified_on::TIMESTAMP) >= v_processed_upto
					 THEN d.transfer_value ELSE 0 END ) ELSE 0 END )), 0) INTO diffdramt FROM adjustments d, users u WHERE d.user_id = u.user_id
		              AND d.entry_type = 'DR'
		              AND d.adjustment_date >= v_from_date + interval '1' day
		              AND u.user_id = userid
		              AND d.product_code = productcode;
		              RAISE NOTICE '%', 'diffDrAmt=' || diffdramt;
			 EXCEPTION
             WHEN OTHERS THEN
             	  		 v_errorcode := '3574';
						 v_message :=  'Balance mismatch process executed for user balance. Exception in getting Differential transactions.'|| SQLERRM;
						 RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
						 RAISE EXCEPTION  using errcode = 'ERR05';
			END;
		 			v_amount := closingbalance + channelinamt - channeloutamt - c2soutamt - reconamt  - currentbalance + diffamt - diffdramt;
					RAISE NOTICE '%', 'balance=' || v_amount;
 			
			IF v_amount = 0 THEN
			   RAISE NOTICE '%', 'When V_AMOUNT IS ZERO.......' || v_amount;
			   			v_errorcode := '3516';
						v_message := 'Balance mismatch process executed successfully for user balance. No mismatch found.';
			ELSE
			 RAISE NOTICE '%', 'When V_AMOUNT IS NOT ZERO.......' || v_amount;
			 
			  
             			mismatchstring :=mismatchstring || userid || ':' || t_r.balance || ',';

  RAISE NOTICE '%',mismatchstring;
    RAISE NOTICE '%','NOT ZERO..................';
             			
             			v_errorcode := '3517';
             			v_message := 'Balance mismatch process executed successfully for user balance. Mismatch found for Amount:'|| v_amount;
			END IF;
			
			IF LENGTH (mismatchstring) >=100 THEN
			     RAISE EXCEPTION  using errcode = 'ERR05';				 
         	END IF;
       END LOOP;
	    
			   IF LENGTH (mismatchstring) > 0  THEN
		             v_errorcode := '3517';
		             v_message :='Balance mismatch process executed successfully for user balance. Mismatch found.' || mismatchstring;
			    ELSE
			     RAISE NOTICE '%', 'LASTTTTTT..................' ;
			     RAISE NOTICE '%', mismatchstring;
		             v_errorcode := '3516';
		             v_message :=  'Balance mismatch process executed successfully for user balance.No mismatch found.';
		        END IF;
				
	   			RAISE NOTICE '%', 'v_message=' || v_message;
	   			RAISE NOTICE '%', 'mismatchstring=' || mismatchstring;
	   			/* ROLLBACK; */
								
EXCEPTION
WHEN sqlstate 'ERR05' THEN
          /* ROLLBACK; */
		  IF LENGTH (mismatchstring) > 0  THEN
	             v_errorcode := '3517';
				  v_message :='Balance mismatch process executed successfully for user balance. Mismatch found.' || mismatchstring;
	      END IF;
		  RAISE NOTICE '%', 'v_message=' || v_message;
          RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
 WHEN OTHERS THEN
          /* ROLLBACK; */
		  IF LENGTH (mismatchstring) > 0  THEN
	             v_errorcode := '3517';
	             v_message :='Balance mismatch process executed successfully for user balance. Mismatch found.' || mismatchstring;
	    	END IF;
		RAISE NOTICE '%', 'v_message=' || v_message;
        RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
    END;
$$;


ALTER FUNCTION pretupsdatabase.sp_chnl_users_bal_mismatch(OUT v_errorcode character varying, OUT v_message character varying, v_processed_upto timestamp without time zone, OUT v_amount integer) OWNER TO pgdb;

--
-- Name: sp_chnl_users_balance_mismatch(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION sp_chnl_users_balance_mismatch(OUT v_errorcode character varying, OUT v_message character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
    DECLARE 
 /*  The logic of calculation is add all the IN channel transactions of the channel user and
    subtract all OUT channel transactions, C2S recharges and available balance.
     The values of errror code will be as follows
 */
 
       --cursor for getting balance of all the channel users
       cursor1 CURSOR
       IS
          SELECT user_id, product_code, balance
            FROM user_balances;
 
       mismatchstring    character varying (200);
       userid           user_balances.user_id%TYPE;
       productcode      user_balances.product_code%TYPE;
       tmpbalance       user_balances.balance%TYPE;
       currentbalance   user_balances.balance%TYPE;
    BEGIN

       SET TRANSACTION READ ONLY;
       v_errorcode := '3563';
       v_message :=
             'Balance mismatch process executed for user balance. Exception while executing the 
   process.'
          || mismatchstring;
       mismatchstring := '';
 
       FOR t_r IN cursor1
       LOOP
          BEGIN
             userid := t_r.user_id;
             productcode := t_r.product_code;
             currentbalance := -t_r.balance;
             --(Total IN transactions-Total OUT transactions-balance) should be zero
             RAISE NOTICE '%',   'Initial userid ='
                                   || userid
                                   || ' currentbalance='
                                   || currentbalance
;
 
             BEGIN
                --getting O2C and C2C IN transactions of a particular channel user and a particular product
                SELECT COALESCE (SUM (cti.approved_quantity), 0)
                  INTO tmpbalance
                  FROM channel_transfers ct, channel_transfers_items cti
                 WHERE ct.status = 'CLOSE'
                   AND ct.transfer_id = cti.transfer_id
                   AND ct.to_user_id = userid
                   AND cti.product_code = productcode;
 
                -- DBMS_OUTPUT.put_line ('C2C IN  tmpbalance='||tmpbalance);
                currentbalance := currentbalance + tmpbalance;
             EXCEPTION
                WHEN OTHERS
                THEN
                   v_errorcode := '3560';
                   v_message :=
                         'Balance mismatch process executed for user balance. Exception in getting IN channel transactions for userid='
                      || userid
                      || ' '
                      || SQLERRM
                      || ' '
                      || mismatchstring;
                   RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
                   RAISE EXCEPTION  using errcode = 'ERR05';
             END;
 
             BEGIN
                --getting O2C and C2C OUT transaction of a channel user and a particular product
                SELECT COALESCE (SUM (cti.approved_quantity), 0)
                  INTO tmpbalance
                  FROM channel_transfers ct, channel_transfers_items cti
                 WHERE ct.status = 'CLOSE'
                   AND ct.transfer_id = cti.transfer_id
                   AND ct.from_user_id = userid
                   AND cti.product_code = productcode;
 
                -- DBMS_OUTPUT.put_line ('C2C OUT tmpbalance='||tmpbalance);
                currentbalance := currentbalance - tmpbalance;
             EXCEPTION
                WHEN OTHERS
                THEN
                   v_errorcode := '3561';
                   v_message :=
                         'Balance mismatch process executed for user balance. Exception in getting OUT channel transactions for userid='
                      || userid
                      || ' '
                      || SQLERRM
                      || ' '
                      || mismatchstring;
                   RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
                   RAISE EXCEPTION  using errcode = 'ERR05';
             END;
 
             BEGIN
                --getting total C2S OUT transactions of a channel user and a particular product
                SELECT COALESCE
                          (SUM
                              (CASE
                                  WHEN (cs.transfer_status = '250')
                                     THEN (CASE
                                              WHEN (credit_back_status = '200'
                                                   )
                                                 THEN 0
                                              ELSE (cs.quantity)
                                           END
                                          )
                                  ELSE cs.quantity
                               END
                              ),
                           0
                          )
                  INTO tmpbalance
                  FROM c2s_transfers cs
                 WHERE cs.transfer_status <> '206'
                   AND cs.sender_id = userid
                   AND cs.product_code = productcode;
 
                -- DBMS_OUTPUT.put_line ('C2S tmpbalance='||tmpbalance);
 
                /*    SELECT NVL(SUM(transfer_value),0) into tmpbalance
                   FROM c2s_transfers
                   WHERE (transfer_status='200' or transfer_status='205')
                   AND sender_id =userid
                   AND product_code=productcode;
                */
                currentbalance := currentbalance - tmpbalance;
             EXCEPTION
                WHEN OTHERS
                THEN
                   v_errorcode := '3562';
                   v_message :=
                         'Balance mismatch process executed for user balance. Exception in getting recharge transactions for userid='
                      || userid
                      || ' '
                      || SQLERRM
                      || ' '
                      || mismatchstring;
                   RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
                   RAISE EXCEPTION  using errcode = 'ERR05';
             END;
 
             BEGIN
                --getting differential transactions of a particular channel user and a particular product
                SELECT COALESCE (SUM ((CASE
                                     WHEN (stock_updated = 'Y')
                                        THEN d.transfer_value
                                     ELSE 0
                                  END
                                 )
                                ),
                            0
                           )
                  INTO tmpbalance
                  FROM adjustments d
                 WHERE d.entry_type = 'CR'
                   AND d.user_id = userid
                   AND d.product_code = productcode;
 
                -- DBMS_OUTPUT.PUT_LINE('diffAmt='||tmpbalance);
                currentbalance := currentbalance + tmpbalance;
             EXCEPTION
                WHEN OTHERS
                THEN
                   v_errorcode := '3574';
                   v_message :=
                         'Balance mismatch process executed for user balance. Exception in getting Differential transactions for userid='
                      || userid
                      || ' '
                      || SQLERRM
                      || ' '
                      || mismatchstring;
                   RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
                   RAISE EXCEPTION  using errcode = 'ERR05';
             END;
 
             BEGIN
                --getting differential transactions of a particular channel user and a particular product
                SELECT COALESCE (SUM ((CASE
                                     WHEN (stock_updated = 'Y')
                                        THEN d.transfer_value
                                     ELSE 0
                                  END
                                 )
                                ),
                            0
                           )
                  INTO tmpbalance
                  FROM adjustments d
                 WHERE d.entry_type = 'DR'
                   AND d.user_id = userid
                   AND d.product_code = productcode;
 
                -- DBMS_OUTPUT.PUT_LINE('diffDrAmt='||tmpbalance);
                currentbalance := currentbalance - tmpbalance;
             EXCEPTION
                WHEN OTHERS
                THEN
                   v_errorcode := '3574';
                   v_message :=
                         'Balance mismatch process executed for user balance. Exception in getting Differential transactions for userid='
                      || userid
                      || ' '
                      || SQLERRM
                      || ' '
                      || mismatchstring;
                   RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
                   RAISE EXCEPTION  using errcode = 'ERR05';
             END;
 
             RAISE NOTICE '%',   'Final userid='
                                   || userid
                                   || ' currentbalance='
                                   || currentbalance
;
 
             IF currentbalance <> 0
             THEN
                mismatchstring :=
                      mismatchstring
                   || userid
                   || ':'
                   || productcode
                   || ':'
                   || t_r.balance
                   || ',';
             END IF;
          EXCEPTION
             WHEN sqlstate 'ERR05'
             THEN
                RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
                RAISE EXCEPTION  using errcode = 'ERR05';
             WHEN OTHERS
             THEN
                RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
                RAISE EXCEPTION  using errcode = 'ERR05';
          END;
       END LOOP;
 
       IF LENGTH (mismatchstring) > 0
       THEN
          v_errorcode := '3517';
          v_message :=
                'Balance mismatch process executed successfully for user balance. Mismatch found.'
             || mismatchstring;
       ELSE
          v_errorcode := '3516';
          v_message :=
             'Balance mismatch process executed successfully for user balance.No mismatch found.';
       END IF;
 
       /* ROLLBACK; */
    EXCEPTION
       WHEN sqlstate 'ERR05'
       THEN
          /* ROLLBACK; */
          RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
       WHEN OTHERS
       THEN
          /* ROLLBACK; */
          RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
    END;
$$;


ALTER FUNCTION pretupsdatabase.sp_chnl_users_balance_mismatch(OUT v_errorcode character varying, OUT v_message character varying) OWNER TO pgdb;

--
-- Name: sp_net_stocks_balance_mismatch(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION sp_net_stocks_balance_mismatch(OUT v_errorcode character varying, OUT v_message character varying, INOUT v_from_date timestamp without time zone) RETURNS record
    LANGUAGE plpgsql
    AS $$
    DECLARE 
    
 /*  The logic of calculation is add all the IN stock transactions of the network and
    subtract all OUT network transactions of the network.
 */
 
       --cursor for getting all the OUT stock transactions from the network


        cur_current_stock CURSOR
       IS
          SELECT   SUM (WALLET_BALANCE) WALLET_BALANCE, product_code, network_code,
                   network_code_for
              FROM network_stocks
          GROUP BY product_code
	,network_code, network_code_for;

 
        productcode      network_stocks.product_code%TYPE;
       networkcode      network_stocks.network_code%TYPE;
       networkcodefor   network_stocks.network_code_for%TYPE;
       amount           network_stocks.WALLET_BALANCE%TYPE;
       current_stock    network_stocks.WALLET_BALANCE%TYPE;
       close_stock      network_stocks.WALLET_BALANCE%TYPE;
       txn_amount       network_stocks.WALLET_BALANCE%TYPE;
       errstring         character varying (300);
       tempdate         DATE;
       from_date        DATE;
      
    BEGIN
       SET TRANSACTION READ ONLY;
       errstring := '';
 
       FOR t_r IN cur_current_stock
       LOOP
          BEGIN
             from_date := v_from_date;
             current_stock := t_r.WALLET_BALANCE;
             productcode := t_r.product_code;
             networkcode := t_r.network_code;
             networkcodefor := t_r.network_code_for;
 
             BEGIN
                --getting closing stock for the network for a particular product
                SELECT SUM (COALESCE (WALLET_BALANCE, 0))
                  INTO close_stock
                  FROM network_daily_stocks
                 WHERE WALLET_DATE = from_date - interval '1' day
                   AND product_code = productcode
                   AND network_code = networkcode
                   AND network_code_for = networkcodefor;
             EXCEPTION
                WHEN NO_DATA_FOUND
                THEN
                   BEGIN
                      SELECT SUM (WALLET_BALANCE), WALLET_DATE
                        INTO close_stock, tempdate
                        FROM network_daily_stocks
                       WHERE WALLET_DATE =
                                (SELECT MAX (WALLET_DATE)
                                   FROM network_daily_stocks
                                  WHERE product_code = productcode
                                    AND network_code = networkcode
                                    AND network_code_for = networkcodefor)
                         AND product_code = productcode
                         AND network_code = networkcode
                         AND network_code_for = networkcodefor;
 
                      from_date := tempdate + interval '1' day;
                   EXCEPTION
                      WHEN OTHERS
                      THEN
                         v_errorcode := '3571';
                         v_message :=
                               'Balance mismatch process executed for network stock. Exception in getting Closing stock transactions for max date.'
                            || SQLERRM;
                         RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
                         RAISE EXCEPTION using errcode = 'ERR05';
                   END;
                WHEN OTHERS
                THEN
                   v_errorcode := '3571';
                   v_message :=
                         'Balance mismatch process executed for network stock. Exception in getting Closing stock transactions.'
                      || SQLERRM;
                   RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
                   RAISE EXCEPTION using errcode = 'ERR05';
             END;
 
             BEGIN
                --getting current stock for the network for a particular product
                SELECT (  COALESCE
                             (SUM
                                 (CASE
                                     WHEN (nst.entry_type = 'CREATION')
                                        THEN nsti.approved_quantity
                                  END
                                 ),
                              0
                             )
                        + COALESCE
                             (SUM
                                 (CASE
                                     WHEN (    nst.entry_type <> 'CREATION'
                                           AND nst.entry_type <> 'TRANSFER'
                                          )
                                        THEN nsti.approved_quantity
                                  END
                                 ),
                              0
                             )
                        - COALESCE
                             (SUM
                                 (CASE
                                     WHEN (nst.entry_type = 'TRANSFER')
                                        THEN nsti.approved_quantity
                                  END
                                 ),
                              0
                             )
                       )
                  INTO txn_amount
                  FROM network_stock_transactions nst,
                       network_stock_trans_items nsti
                 WHERE nst.txn_no = nsti.txn_no
                   AND nst.txn_status = 'CLOSE'
                   AND date_trunc('day',modified_on::TIMESTAMP) >= from_date
                   AND nsti.product_code = productcode
                   AND nst.network_code = networkcode
                   AND nst.network_code_for = networkcodefor;
             EXCEPTION
                WHEN OTHERS
                THEN
                   v_errorcode := '3573';
                   v_message :=
                         'Balance mismatch process executed for network stock. Exception in getting current stock transactions.'
                      || SQLERRM;
                   RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
                   RAISE EXCEPTION using errcode = 'ERR05';
             END;
 
             RAISE NOTICE '%',   'Product Code= '
                                   || productcode
                                   || ' and Network Code= '
                                   || networkcode
                                   || ' and Network Code For= '
                                   || networkcodefor
                                   || ' and Mismatched amount='
                                   || amount
;
             RAISE NOTICE '%',   'Current Stock= '
                                   || current_stock
                                   || ' and Close Stock= '
                                   || close_stock
                                   || ' and Txn Amount= '
                                   || txn_amount
;
             amount := close_stock + txn_amount - current_stock;
 
             IF amount <> 0
             THEN
                errstring :=
                      errstring
                   || 'Product Code= '
                   || productcode
                   || ' and Network Code= '
                   || networkcode
                   || ' and Network Code For= '
                   || networkcodefor
                   || ' and Amount='
                   || amount
                   || ',';
             END IF;
          EXCEPTION
             WHEN OTHERS
             THEN
                v_errorcode := '3573';
                v_message :=
                   'Balance mismatch process executed for network stock. Exception while executing the loop.';
                RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
                RAISE EXCEPTION using errcode = 'ERR05';
          END;
       END LOOP;
 
       IF (errstring <> NULL OR LENGTH (TRIM (errstring)) > 0)
       THEN
          v_errorcode := '3519';
          v_message :=
             'Balance mismatch process executed successfully for network stock. Mismatch found.';
          v_message := v_message || errstring;
           
       ELSE
          v_errorcode := '3518';
          v_message :=
             'Balance mismatch process executed successfully for network stock.No mismatch found.';
       END IF;
 
       /* ROLLBACK; */
    EXCEPTION
       WHEN sqlstate 'ERR05'
       THEN
          RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
          /* ROLLBACK; */
       WHEN OTHERS
       THEN
          v_errorcode := '3565';
          v_message :=
             'Balance mismatch process executed for network stock. Exception while executing the  main process.';
          RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
          /* ROLLBACK; */

    END;
  $$;


ALTER FUNCTION pretupsdatabase.sp_net_stocks_balance_mismatch(OUT v_errorcode character varying, OUT v_message character varying, INOUT v_from_date timestamp without time zone) OWNER TO pgdb;

--
-- Name: sp_system_total_bal_mismatch(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION sp_system_total_bal_mismatch(OUT v_errorcode character varying, OUT v_message character varying, v_processed_upto timestamp without time zone, OUT v_amount integer) RETURNS record
    LANGUAGE plpgsql
    AS $$
    DECLARE 
       currentbalance   user_balances.balance%TYPE;
       closingbalance   user_balances.balance%TYPE;
       channelinamt     user_balances.balance%TYPE;
       channeloutamt    user_balances.balance%TYPE;
       c2soutamt        user_balances.balance%TYPE;
       diffdramt        user_balances.balance%TYPE;
       diffamt          user_balances.balance%TYPE;
       reconamt         user_balances.balance%TYPE;
       errstring        VARCHAR (300);
       v_from_date      TIMESTAMP(0);
      -- mainexception    EXCEPTION;
    BEGIN

       SET TRANSACTION READ ONLY;
 
       BEGIN
          --getting current balances of the users
          SELECT COALESCE (SUM (balance), 0)
            INTO currentbalance
            FROM user_balances ub;
 
          RAISE NOTICE '%', 'currentbalance=' || currentbalance;
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3576';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting current balance of the users.'
                || SQLERRM;
             RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
              RAISE EXCEPTION  using errcode = 'ERR05' ;
       END;
 
       BEGIN
          SELECT /*+ INDEX ( CTI UK_CHNL_TRANSFER_ITEMS )*/
                 COALESCE (SUM (cti.approved_quantity), 0)
            INTO channelinamt
            FROM channel_transfers ct, channel_transfers_items cti, users u
           WHERE ct.status = 'CLOSE'
             AND ct.transfer_id = cti.transfer_id
             AND ct.to_user_id = u.user_id;
 
          RAISE NOTICE '%', 'channelInAmt=' || channelinamt;
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3560';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Channle In transactions.'
                || SQLERRM;
             RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
              RAISE EXCEPTION  using errcode = 'ERR05' ;
       END;
 
       BEGIN
          SELECT /*+ INDEX ( CTI UK_CHNL_TRANSFER_ITEMS )*/
                 COALESCE (SUM (cti.approved_quantity), 0)
            INTO channeloutamt
            FROM channel_transfers ct, channel_transfers_items cti, users u
           WHERE ct.status = 'CLOSE'
             AND ct.transfer_id = cti.transfer_id
             AND ct.from_user_id = u.user_id;
 
          RAISE NOTICE '%', 'channelOutAmt=' || channeloutamt;
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3561';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Channel Out transactions.'
                || SQLERRM;
             RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
             RAISE EXCEPTION  using errcode = 'ERR05' ;
       END;
 
       BEGIN
          SELECT COALESCE
                    (SUM
                        (CASE
                            WHEN (cs.transfer_status = '250')
                               THEN (CASE
                                        WHEN (credit_back_status = '200')
                                           THEN 0
                                        ELSE (cs.quantity)
                                     END
                                    )
                            ELSE cs.quantity
                         END
                        ),
                     0
                    )
            INTO c2soutamt
            FROM c2s_transfers cs
           WHERE cs.transfer_status <> '206';
 
          RAISE NOTICE '%', 'c2sOutAmt=' || c2soutamt;
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3562';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting C2S Out transactions.'
                || SQLERRM;
             RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
             RAISE EXCEPTION  using errcode = 'ERR05' ;
       END;
 
       BEGIN
          --getting differential transactions of a particular channel user and a particular product
          SELECT COALESCE (SUM ((CASE
                               WHEN (stock_updated = 'Y')
                                  THEN d.transfer_value
                               ELSE 0
                            END
                           )
                          ),
                      0
                     )
            INTO diffamt
            FROM adjustments d
           WHERE d.entry_type = 'CR';
 
          RAISE NOTICE '%', 'diffAmt=' || diffamt;
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3574';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Differential transactions.'
                || SQLERRM;
             RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
             RAISE EXCEPTION  using errcode = 'ERR05' ;
       END;
 
       BEGIN
          --getting differential transactions of a particular channel user and a particular product
          SELECT COALESCE (SUM ((CASE
                               WHEN (stock_updated = 'Y')
                                  THEN d.transfer_value
                               ELSE 0
                            END
                           )
                          ),
                      0
                     )
            INTO diffdramt
            FROM adjustments d
           WHERE d.entry_type = 'DR';
 
          RAISE NOTICE '%', 'diffDrAmt=' || diffdramt;
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3574';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Differential transactions.'
                || SQLERRM;
             RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
             RAISE EXCEPTION  using errcode = 'ERR05' ;
       END;
 
       v_amount :=
            channelinamt
          - channeloutamt
          - c2soutamt
          - currentbalance
          + diffamt
          - diffdramt;
       RAISE NOTICE '%', 'balance=' || v_amount;
 
       IF v_amount = 0
       THEN
          v_errorcode := '3516';
          v_message :=
             'Balance mismatch process executed successfully for user balance. No mismatch found.';
       ELSE
          v_errorcode := '3517';
          v_message :=
                'Balance mismatch process executed successfully for user balance. Mismatch found for Amount:'
             || v_amount;
       END IF;
 
       /* ROLLBACK; */
    EXCEPTION
       WHEN  sqlstate 'ERR05'
       THEN
          /* ROLLBACK; */
          RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
       WHEN OTHERS
       THEN
          /* ROLLBACK; */
          v_errorcode := '3563';
          v_message :=
             'Balance mismatch process executed for user balance. Exception while executing the main process.';
          RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
    END;
 $$;


ALTER FUNCTION pretupsdatabase.sp_system_total_bal_mismatch(OUT v_errorcode character varying, OUT v_message character varying, v_processed_upto timestamp without time zone, OUT v_amount integer) OWNER TO pgdb;

--
-- Name: sp_system_usr_bal_mismatch(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION sp_system_usr_bal_mismatch(OUT v_errorcode character varying, OUT v_message character varying, v_processed_upto timestamp without time zone, OUT v_amount integer) RETURNS record
    LANGUAGE plpgsql
    AS $$
    DECLARE 
       currentbalance   user_balances.balance%TYPE;
       closingbalance   user_balances.balance%TYPE;
       channelinamt     user_balances.balance%TYPE;
       channeloutamt    user_balances.balance%TYPE;
       c2soutamt        user_balances.balance%TYPE;
       diffdramt        user_balances.balance%TYPE;
       diffamt          user_balances.balance%TYPE;
       reconamt         user_balances.balance%TYPE;
       errstring        VARCHAR (300);
       v_from_date      TIMESTAMP(0);
       --mainexception    EXCEPTION;
    BEGIN

       SET TRANSACTION READ ONLY;
 
       BEGIN
          --getting max date for which balance of all users is available
          SELECT MIN (balance_date)
            INTO v_from_date
            FROM (SELECT   ub.user_id, MAX (ub.balance_date) balance_date
                      FROM user_daily_balances ub, users u
                     WHERE u.user_id = ub.user_id AND u.status = 'Y'
                        OR (    u.status = 'N'
                            AND date_trunc('day',u.modified_on::TIMESTAMP) >= v_processed_upto
                           )
                  GROUP BY ub.user_id) AS A;
 
          RAISE NOTICE '%', 'v_from_date=' || v_from_date;
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3575';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting max from date.'
                || SQLERRM;
             RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
             RAISE EXCEPTION  using errcode = 'ERR05' ;
       END;
 
       BEGIN
          --getting current balances of the users
          SELECT COALESCE (SUM (CASE
                              WHEN u.status = 'Y'
                                 THEN balance
                              WHEN (    u.status = 'N'
                                    AND date_trunc('day',u.modified_on::TIMESTAMP) >=
                                                               v_processed_upto
                                   )
                                 THEN balance
                              ELSE 0
                           END
                          ),
                      0
                     )
            INTO currentbalance
            FROM user_balances ub, users u
           WHERE ub.user_id = u.user_id;
 
          RAISE NOTICE '%', 'currentbalance=' || currentbalance;
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3576';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting current balance of the users.'
                || SQLERRM;
             RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
              RAISE EXCEPTION  using errcode = 'ERR05' ;
       END;
 
       BEGIN
          --getting prev balance
          SELECT COALESCE (SUM (CASE
                              WHEN u.status = 'Y'
                                 THEN balance
                              WHEN u.status = 'N'
                              AND date_trunc('day',u.modified_on::TIMESTAMP) >= v_processed_upto
                                 THEN balance
                              ELSE 0
                           END
                          ),
                      0
                     )
            INTO closingbalance
            FROM user_daily_balances ub, users u
           WHERE ub.user_id = u.user_id AND ub.balance_date = v_from_date;
 
          RAISE NOTICE '%', 'closingbalance=' || closingbalance;
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3572';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Closing(prev) balance.'
                || SQLERRM;
             RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
             RAISE EXCEPTION  using errcode = 'ERR05' ;
       END;
 
       BEGIN
          SELECT /*+ INDEX ( CTI UK_CHNL_TRANSFER_ITEMS )*/
                 COALESCE
                    (SUM
                        (CASE
                            WHEN u.status = 'Y'
                               THEN cti.approved_quantity
                            WHEN u.status = 'N'
                            AND date_trunc('day',u.modified_on::TIMESTAMP) >= v_processed_upto
                               THEN cti.approved_quantity
                            ELSE 0
                         END
                        ),
                     0
                    )
            INTO channelinamt
            FROM channel_transfers ct, channel_transfers_items cti, users u
           WHERE ct.status = 'CLOSE'
             AND ct.transfer_id = cti.transfer_id
             AND ct.to_user_id = u.user_id
             --close date instead of transfer date is being used as transfer_date is not being updated at approval time
             -- 1 is added as we need to consider the transaction from the next day for which closing balance was found
             AND date_trunc('day',ct.close_date::TIMESTAMP) >= v_from_date + interval '1' day;
 
          RAISE NOTICE '%', 'channelInAmt=' || channelinamt;
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3560';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Channle In transactions.'
                || SQLERRM;
             RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
              RAISE EXCEPTION  using errcode = 'ERR05' ;
       END;
 
       BEGIN
          SELECT /*+ INDEX ( CTI UK_CHNL_TRANSFER_ITEMS )*/
                 COALESCE
                    (SUM
                        (CASE
                            WHEN u.status = 'Y'
                               THEN cti.approved_quantity
                            WHEN u.status = 'N'
                            AND date_trunc('day',u.modified_on::TIMESTAMP) >= v_processed_upto
                               THEN cti.approved_quantity
                            ELSE 0
                         END
                        ),
                     0
                    )
            INTO channeloutamt
            FROM channel_transfers ct, channel_transfers_items cti, users u
           WHERE ct.status = 'CLOSE'
             AND ct.transfer_id = cti.transfer_id
             AND ct.from_user_id = u.user_id
             --close date instead of transfer date is being used as transfer_date is not being updated at approval time
             -- 1 is added as we need to consider the transaction from the next day for which closing balance was found
             AND date_trunc('day',ct.close_date::TIMESTAMP) >= v_from_date +  interval '1' day;
 
          RAISE NOTICE '%', 'channelOutAmt=' || channeloutamt;
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3561';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Channel Out transactions.'
                || SQLERRM;
             RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
              RAISE EXCEPTION  using errcode = 'ERR05' ;
       END;
 
       BEGIN
          SELECT COALESCE
                    (SUM
                        (CASE
                            WHEN (cs.transfer_status = '250')
                               THEN (CASE
                                        WHEN (credit_back_status != '200')
                                           THEN (CASE
                                                    WHEN u.status = 'N'
                                                    AND date_trunc('day',u.modified_on::TIMESTAMP) >=
                                                               v_processed_upto
                                                       THEN cs.quantity
                                                    WHEN u.status <> 'N'
                                                       THEN cs.quantity
                                                    ELSE 0
                                                 END
                                                )
                                        ELSE 0
                                     END
                                    )
                            ELSE cs.transfer_value
                         END
                        ),
                     0
                    )
            INTO c2soutamt
            FROM c2s_transfers cs, users u
           WHERE cs.sender_id = u.user_id
             AND cs.transfer_status <> '206'
             AND cs.transfer_date >= v_from_date +  interval '1' day;
 
          RAISE NOTICE '%', 'c2sOutAmt=' || c2soutamt;
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3562';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting C2S Out transactions.'
                || SQLERRM;
             RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
              RAISE EXCEPTION  using errcode = 'ERR05' ;
       END;
 
       BEGIN
          SELECT COALESCE (SUM (CASE
                              WHEN u.status = 'N'
                              AND date_trunc('day',u.modified_on::TIMESTAMP) >= v_processed_upto
                                 THEN cs.quantity
                              WHEN u.status <> 'N'
                                 THEN cs.quantity
                              ELSE 0
                           END
                          ),
                      0
                     )
            INTO reconamt
            FROM c2s_transfers cs, users u
           WHERE cs.sender_id = u.user_id
             AND cs.reconciliation_flag = 'Y'
             AND cs.reconciliation_date >= v_from_date +  interval '1' day
             AND cs.transfer_status = '200'
             AND cs.transfer_date < cs.reconciliation_date;
 
          RAISE NOTICE '%', 'reconAmt=' || reconamt;
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3577';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting reconciliation transactions.'
                || SQLERRM;
             RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
              RAISE EXCEPTION  using errcode = 'ERR05' ;
       END;
 
       BEGIN
          --getting differential transactions of a particular channel user and a particular product
          SELECT COALESCE
                    (SUM
                        ((CASE
                             WHEN (stock_updated = 'Y')
                                THEN (CASE
                                         WHEN u.status = 'Y'
                                            THEN d.transfer_value
                                         WHEN u.status = 'N'
                                         AND date_trunc('day',u.modified_on::TIMESTAMP) >=
                                                               v_processed_upto
                                            THEN d.transfer_value
                                         ELSE 0
                                      END
                                     )
                             ELSE 0
                          END
                         )
                        ),
                     0
                    )
            INTO diffamt
            FROM adjustments d, users u
           WHERE d.user_id = u.user_id
             AND d.entry_type = 'CR'
             AND d.adjustment_date >= v_from_date +  interval '1' day;
 
          RAISE NOTICE '%', 'diffAmt=' || diffamt;
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3574';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Differential transactions.'
                || SQLERRM;
             RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
              RAISE EXCEPTION  using errcode = 'ERR05' ;
       END;
 
       BEGIN
          --getting differential transactions of a particular channel user and a particular product
          SELECT COALESCE
                    (SUM
                        ((CASE
                             WHEN (stock_updated = 'Y')
                                THEN (CASE
                                         WHEN u.status = 'Y'
                                            THEN d.transfer_value
                                         WHEN u.status = 'N'
                                         AND date_trunc('day',u.modified_on::TIMESTAMP) >=
                                                               v_processed_upto
                                            THEN d.transfer_value
                                         ELSE 0
                                      END
                                     )
                             ELSE 0
                          END
                         )
                        ),
                     0
                    )
            INTO diffdramt
            FROM adjustments d, users u
           WHERE d.user_id = u.user_id
             AND d.entry_type = 'DR'
             AND d.adjustment_date >= v_from_date +  interval '1' day;
 
          RAISE NOTICE '%', 'diffDrAmt=' || diffdramt;
       EXCEPTION
          WHEN OTHERS
          THEN
             v_errorcode := '3574';
             v_message :=
                   'Balance mismatch process executed for user balance. Exception in getting Differential transactions.'
                || SQLERRM;
             RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
              RAISE EXCEPTION  using errcode = 'ERR05' ;
       END;
 
       v_amount :=
            closingbalance
          + channelinamt
          - channeloutamt
          - c2soutamt
          - reconamt
          - currentbalance
          + diffamt
          - diffdramt;
       RAISE NOTICE '%', 'balance=' || v_amount;
 
       IF v_amount = 0
       THEN
          v_errorcode := '3516';
          v_message :=
             'Balance mismatch process executed successfully for user balance. No mismatch found.';
       ELSE
          v_errorcode := '3517';
          v_message :=
                'Balance mismatch process executed successfully for user balance. Mismatch found for Amount:'
             || v_amount;
       END IF;
 
       /* ROLLBACK; */
    EXCEPTION
       WHEN sqlstate 'ERR05'
       THEN
          /* ROLLBACK; */
          RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
       WHEN OTHERS
       THEN
          /* ROLLBACK; */
          v_errorcode := '3563';
          v_message :=
             'Balance mismatch process executed for user balance. Exception while executing the main process.';
          RAISE NOTICE '%', 'EXCEPTION =' || SQLERRM;
    END;
$$;


ALTER FUNCTION pretupsdatabase.sp_system_usr_bal_mismatch(OUT v_errorcode character varying, OUT v_message character varying, v_processed_upto timestamp without time zone, OUT v_amount integer) OWNER TO pgdb;

--
-- Name: sp_update_hourly_trn_summary(timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION sp_update_hourly_trn_summary(aiv_date timestamp without time zone) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE 
	ln_success_count				   HOURLY_TRANSACTION_SUMMARY.SUCCESS_COUNT%TYPE=0;
	ln_failure_count				   HOURLY_TRANSACTION_SUMMARY.FAILURE_COUNT%TYPE=0;
	ln_success_amount				   HOURLY_TRANSACTION_SUMMARY.SUCCESS_AMT%TYPE=0;
	ln_failure_amount				   HOURLY_TRANSACTION_SUMMARY.FAILURE_AMT%TYPE=0;
	ln_sender_debit_amount			   HOURLY_TRANSACTION_SUMMARY.SENDER_DEBIT_AMOUNT%TYPE=0;
	ln_receiver_credit_amount		   HOURLY_TRANSACTION_SUMMARY.RECEIVER_CREDIT_AMOUNT%TYPE=0;
        v_messageForLog VARCHAR(100);
        v_sqlerrMsgForLog VARCHAR(100);
	---Cursor Declaration

	declare HOURLY_TRANSACTION_CURSOR CURSOR(aiv_Date Timestamp(0)) IS
	SELECT /*+ INDEX(st) INDEX(T1) INDEX(T2)*/
	COALESCE(T1.SERVICE_CLASS_ID,v_nullvalue) SENDER_SERVICE_CLASS,
	COALESCE(T2.SERVICE_CLASS_ID,v_nullvalue) RECEIVER_SERVICE_CLASS,
	ST.TRANSFER_DATE TRANS_DATE,
	TO_NUMBER(TO_CHAR(ST.TRANSFER_DATE_TIME,'HH24')) TRANS_HOUR,
	ST.TRANSFER_STATUS STATUS,
	COUNT(ST.TRANSFER_DATE) COUNT,
	COALESCE(ST.SERVICE_TYPE,v_nullvalue) SERVICE,
	ST.SUB_SERVICE,
	SUM(COALESCE(ST.TRANSFER_VALUE,0)) AMOUNT,
	SUM(COALESCE(ST.SENDER_TRANSFER_VALUE,0)) SENDER_DEBIT_AMOUNT,
	SUM(COALESCE(ST.RECEIVER_TRANSFER_VALUE,0)) RECEIVER_CREDIT_AMOUNT,
	ST.transfer_category
	FROM TRANSFER_ITEMS T1, TRANSFER_ITEMS T2, SUBSCRIBER_TRANSFERS ST
	WHERE
	ST.TRANSFER_DATE = AIV_DATE
	AND ST.TRANSFER_STATUS IN('200','206')
	AND	ST.TRANSFER_ID = T1.TRANSFER_ID
	AND ST.TRANSFER_ID = T2.TRANSFER_ID
	AND T1.USER_TYPE = 'SENDER'
	AND T2.USER_TYPE = 'RECEIVER'
	GROUP BY
	T1.SERVICE_CLASS_ID,
	T2.SERVICE_CLASS_ID,
	ST.TRANSFER_DATE,
	TO_NUMBER(TO_CHAR(ST.TRANSFER_DATE_TIME,'HH24')),
	ST.TRANSFER_STATUS,
	ST.SERVICE_TYPE,
	ST.SUB_SERVICE,
	ST.transfer_category;

BEGIN

	FOR htr IN HOURLY_TRANSACTION_CURSOR(n_date_for_mis) LOOP
	ln_success_count	       :=0;
	ln_failure_count	   	   :=0;
	ln_success_amount	 	   :=0;
	ln_failure_amount	   	   :=0;
	ln_sender_debit_amount	   :=0;
	ln_receiver_credit_amount  :=0;

	IF htr.STATUS = '200' THEN
		ln_success_count	      := htr.COUNT;
		ln_success_amount	      := htr.AMOUNT;
		ln_sender_debit_amount	  := htr.SENDER_DEBIT_AMOUNT;
		ln_receiver_credit_amount := htr.RECEIVER_CREDIT_AMOUNT;
	ELSE
		ln_failure_count	      := htr.COUNT;
		ln_failure_amount	  	  := htr.AMOUNT;
	END IF;

	BEGIN
	UPDATE HOURLY_TRANSACTION_SUMMARY
	SET
		SUCCESS_COUNT			 =SUCCESS_COUNT +ln_success_count,
		FAILURE_COUNT			 =FAILURE_COUNT	+ ln_failure_count,
		SUCCESS_AMT				 =SUCCESS_AMT+ln_success_amount,
		FAILURE_AMT				 =FAILURE_AMT +ln_failure_amount,
		SENDER_DEBIT_AMOUNT		 =SENDER_DEBIT_AMOUNT + ln_sender_debit_amount,
		RECEIVER_CREDIT_AMOUNT	 =RECEIVER_CREDIT_AMOUNT + ln_receiver_credit_amount
		WHERE TRANS_DATE 					   = htr.TRANS_DATE AND
		      TRANS_HOUR		       		   = htr.TRANS_HOUR AND
		      SENDER_SERVICE_CLASS             = htr.SENDER_SERVICE_CLASS AND
		      RECEIVER_SERVICE_CLASS           = htr.RECEIVER_SERVICE_CLASS AND
		      SERVICE                          = htr.SERVICE AND
  		      SUB_SERVICE                      = htr.SUB_SERVICE AND
			  TRANSFER_CATEGORY				   = htr.transfer_category;
	CASE WHEN SQL%NOTFOUND THEN
		INSERT INTO HOURLY_TRANSACTION_SUMMARY
			(trans_date,
			trans_hour,
			sender_service_class,
			receiver_service_class,
			service,
			success_count,
			failure_count,
			success_amt,
			failure_amt,
			sender_debit_amount,
			receiver_credit_amount,
			sub_service,
			transfer_category)
		SELECT (htr.trans_date,
			htr.trans_hour,
			htr.sender_service_class,
			htr.receiver_service_class,
			htr.service,
			ln_success_count,
			ln_failure_count,
			ln_success_amount,
			ln_failure_amount,
			ln_sender_debit_amount,
			ln_receiver_credit_amount,
			htr.sub_service,
			htr.transfer_category);

	END CASE;
	EXCEPTION
			 WHEN OTHERS THEN
			 	  RAISE NOTICE '%','EXCEPTION while Updating/Inserting in HOURLY_TRANSACTION_SUMMARY ='||sqlerrm;
			   	  v_messageForLog:='Error while Updating/Inserting record in HOURLY_TRANSACTION_SUMMARY, Date:'||htr.trans_date||' Hour:'||htr.trans_hour;
		       	  v_sqlerrMsgForLog:=sqlerrm;
			  	 RAISE EXCEPTION  using errcode = 'ERR05';
	END;
    END LOOP;

EXCEPTION
	when sqlstate 'ERR05' then
		 IF HOURLY_TRANSACTION_CURSOR%ISOPEN THEN
      	 	CLOSE HOURLY_TRANSACTION_CURSOR;
		 END IF;
		 RAISE NOTICE '%','EXCEPTION while Updating/Inserting in HOURLY_TRANSACTION_SUMMARY';
		RAISE EXCEPTION  using errcode = 'ERR05';
	WHEN OTHERS THEN
		IF HOURLY_TRANSACTION_CURSOR%ISOPEN THEN
           CLOSE HOURLY_TRANSACTION_CURSOR;
		END IF;
		RAISE NOTICE '%','OTHERS EXCEPTION ='||sqlerrm;
		v_messageForLog:='Exception while inserting/updating record in HOURLY_TRANSACTION_SUMMARY';
		v_sqlerrMsgForLog:=sqlerrm;
		RAISE EXCEPTION  using errcode = 'ERR05';
		
END ;	
	
$$;


ALTER FUNCTION pretupsdatabase.sp_update_hourly_trn_summary(aiv_date timestamp without time zone) OWNER TO pgdb;

--
-- Name: status_change(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION status_change() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN  
	IF (OLD.status IN ('CU','ST','DA') AND OLD.status <> NEW.status ) THEN  
	RAISE EXCEPTION 'Invalid_status_change';  
	END IF;  
	RETURN NULL;  
END;
$$;


ALTER FUNCTION pretupsdatabase.status_change() OWNER TO pgdb;

--
-- Name: svc_setor_intfc_mapping_insert(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION svc_setor_intfc_mapping_insert() RETURNS void
    LANGUAGE plpgsql
    AS $$
    DECLARE 
    v_service_type            SVC_SETOR_INTFC_MAPPING.SERVICE_TYPE%TYPE;                        
    v_selector_code            SVC_SETOR_INTFC_MAPPING.SELECTOR_CODE%TYPE;                       
    v_network_code            SVC_SETOR_INTFC_MAPPING.NETWORK_CODE%TYPE;                        
    v_interface_id            SVC_SETOR_INTFC_MAPPING.INTERFACE_ID%TYPE;                        
    v_prefix_id                SVC_SETOR_INTFC_MAPPING.PREFIX_ID%TYPE;                           
    v_action                SVC_SETOR_INTFC_MAPPING.ACTION%TYPE;                              
    v_method_type            SVC_SETOR_INTFC_MAPPING.METHOD_TYPE%TYPE;                         
    v_count                    BIGINT;     

    --SQLException EXCEPTION;
   -- EXITEXCEPTION EXCEPTION;

    DECLARE  serise_cur CURSOR FOR select st.service_type,stm.selector_code, ns.sender_network, ss.interface_id,ss.prefix_id,ss.action,st.type  
            from service_type st, network_services ns, service_type_selector_mapping stm, intf_ntwrk_prfx_mapping ss
            where st.external_interface = 'Y'
            and st.status = 'Y'
            and ns.status='Y'
            and ns.service_type=st.service_type
            and st.type in('PRE','POST')
            and stm.service_type=st.service_type
            and stm.status='Y'
            and ss.METHOD_TYPE=st.TYPE
            and ss.NETWORK_CODE=ns.SENDER_NETWORK
            order by st.module,st.service_type,stm.selector_code, ss.prefix_id,ss.action;

    BEGIN

                RAISE NOTICE '%','Start SVC_SETOR_INTFC_MAPPING_INSERT';
                v_count:=1;

               OPEN serise_cur;
               LOOP
                RAISE NOTICE '%','Inside Loop';
                            FETCH serise_cur INTO v_service_type, v_selector_code, v_network_code, v_interface_id, v_prefix_id, v_action, v_method_type;
                            IF NOT FOUND THEN EXIT;
                            END IF;
                            --EXIT WHEN serise_cur%NOTFOUND;

                             RAISE NOTICE '%','Inside Loop..insertion';
                            INSERT INTO SVC_SETOR_INTFC_MAPPING(SERVICE_TYPE, SELECTOR_CODE, NETWORK_CODE, INTERFACE_ID, PREFIX_ID, ACTION, METHOD_TYPE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SRV_SELECTOR_INTERFACE_ID)
                            VALUES (v_service_type,v_selector_code,v_network_code,v_interface_id, v_prefix_id, v_action, v_method_type, current_timestamp,'SYSTEM',current_timestamp,'SYSTEM',v_count);                         
                             v_count:=v_count+1;

                            END LOOP;
                            CLOSE serise_cur;
                            
                            EXCEPTION WHEN OTHERS
			       THEN
                                RAISE NOTICE '%','SQL EXCEPTION while inserting into SVC_SETOR_INTFC_MAPPING, Error is ' || SQLERRM;
                               
                                RAISE EXCEPTION ' SQL EXCEPTION while inserting into SVC_SETOR_INTFC_MAPPING'  ;
                           
 

            --COMMIT;
            RAISE NOTICE '%','End SVC_SETOR_INTFC_MAPPING_INSERT  v_count='||v_count;
    
END;
$$;


ALTER FUNCTION pretupsdatabase.svc_setor_intfc_mapping_insert() OWNER TO pgdb;

--
-- Name: test(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION test() RETURNS integer
    LANGUAGE plpgsql
    AS $$
begin
begin
	savepoint s;
	end;
    raise exception using errcode = 50001;
    
 exception
  when sqlstate '50001' then
  begin
    rollback to savepoint s;
    end;
        return sqlstate;
end $$;


ALTER FUNCTION pretupsdatabase.test() OWNER TO pgdb;

--
-- Name: trig_barred_msisdn_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_barred_msisdn_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO BARRED_MSISDN_HISTORY(module,network_code,msisdn,name,user_type,
barred_type,created_on,created_by,modified_on,modified_by,barred_reason,created_date,
entry_date,operation_performed)
VALUES(NEW.module,NEW.network_code,NEW.msisdn,NEW.name,NEW.user_type,
NEW.barred_type,NEW.created_on,NEW.created_by,NEW.created_on,
NEW.created_by,NEW.barred_reason,NEW.created_date,current_timestamp,'I');
ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO BARRED_MSISDN_HISTORY(module,network_code,msisdn,name,user_type,
barred_type,created_on,created_by,modified_on,modified_by,barred_reason,created_date,
entry_date,operation_performed)
VALUES(NEW.module,NEW.network_code,NEW.msisdn,NEW.name,NEW.user_type,
NEW.barred_type,NEW.created_on,NEW.created_by,NEW.modified_on,
NEW.modified_by,NEW.barred_reason,NEW.created_date,current_timestamp,'U');
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO BARRED_MSISDN_HISTORY(module,network_code,msisdn,name,user_type,
barred_type,created_on,created_by,modified_on,modified_by,barred_reason,created_date,
entry_date,operation_performed)
VALUES(OLD.module,OLD.network_code,OLD.msisdn,OLD.name,OLD.user_type,
OLD.barred_type,OLD.created_on,OLD.created_by,OLD.modified_on,
OLD.modified_by,OLD.barred_reason,OLD.created_date,current_timestamp,'D');
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_barred_msisdn_history() OWNER TO pgdb;

--
-- Name: trig_bonus_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_bonus_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN

IF (TG_OP = 'INSERT') THEN
INSERT INTO BONUS_HISTORY(PROFILE_TYPE,USER_ID_OR_MSISDN,POINTS,
BUCKET_CODE,PRODUCT_CODE,POINTS_DATE,LAST_REDEMPTION_ID,LAST_REDEMPTION_ON,
LAST_ALLOCATION_TYPE,LAST_ALLOCATED_ON,CREATED_ON,CREATED_BY,
MODIFIED_ON,MODIFIED_BY,OPERATION_PERFORMED,ENTRY_DATE,TRANSFER_ID,PROFILE_ID,ACCUMULATED_POINTS,VERSION)
VALUES(NEW.PROFILE_TYPE,NEW.USER_ID_OR_MSISDN,NEW.POINTS,NEW.BUCKET_CODE,
NEW.PRODUCT_CODE,NEW.POINTS_DATE,NEW.LAST_REDEMPTION_ID,NEW.LAST_REDEMPTION_ON,
NEW.LAST_ALLOCATION_TYPE,NEW.LAST_ALLOCATED_ON,NEW.CREATED_ON,NEW.CREATED_BY,
NEW.MODIFIED_ON,NEW.MODIFIED_BY,'I',current_timestamp,NEW.TRANSFER_ID,NEW.PROFILE_ID,NEW.ACCUMULATED_POINTS,NEW.VERSION);

ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO BONUS_HISTORY(PROFILE_TYPE,USER_ID_OR_MSISDN,POINTS,
BUCKET_CODE,PRODUCT_CODE,POINTS_DATE,LAST_REDEMPTION_ID,LAST_REDEMPTION_ON,
LAST_ALLOCATION_TYPE,LAST_ALLOCATED_ON,CREATED_ON,CREATED_BY,
MODIFIED_ON,MODIFIED_BY,OPERATION_PERFORMED,ENTRY_DATE,TRANSFER_ID,PROFILE_ID,ACCUMULATED_POINTS,VERSION)
VALUES(NEW.PROFILE_TYPE,NEW.USER_ID_OR_MSISDN,NEW.POINTS,NEW.BUCKET_CODE,
NEW.PRODUCT_CODE,NEW.POINTS_DATE,NEW.LAST_REDEMPTION_ID,NEW.LAST_REDEMPTION_ON,
NEW.LAST_ALLOCATION_TYPE,NEW.LAST_ALLOCATED_ON,NEW.CREATED_ON,NEW.CREATED_BY,
NEW.MODIFIED_ON,NEW.MODIFIED_BY,'U',current_timestamp,NEW.TRANSFER_ID,NEW.PROFILE_ID,NEW.ACCUMULATED_POINTS,NEW.VERSION);

ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO BONUS_HISTORY(PROFILE_TYPE,USER_ID_OR_MSISDN,POINTS,
BUCKET_CODE,PRODUCT_CODE,POINTS_DATE,LAST_REDEMPTION_ID,LAST_REDEMPTION_ON,
LAST_ALLOCATION_TYPE,LAST_ALLOCATED_ON,CREATED_ON,CREATED_BY,
MODIFIED_ON,MODIFIED_BY,OPERATION_PERFORMED,ENTRY_DATE,TRANSFER_ID,PROFILE_ID,ACCUMULATED_POINTS,VERSION)
VALUES(OLD.PROFILE_TYPE,OLD.USER_ID_OR_MSISDN,OLD.POINTS,OLD.BUCKET_CODE,
OLD.PRODUCT_CODE,OLD.POINTS_DATE,OLD.LAST_REDEMPTION_ID,OLD.LAST_REDEMPTION_ON,
OLD.LAST_ALLOCATION_TYPE,OLD.LAST_ALLOCATED_ON,OLD.CREATED_ON,OLD.CREATED_BY,
OLD.MODIFIED_ON,OLD.MODIFIED_BY,'D',current_timestamp,OLD.TRANSFER_ID,NEW.PROFILE_ID,NEW.ACCUMULATED_POINTS,NEW.VERSION);
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_bonus_history() OWNER TO pgdb;

--
-- Name: trig_channel_users_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_channel_users_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO CHANNEL_USERS_HISTORY (USER_ID, USER_GRADE, CONTACT_PERSON, TRANSFER_PROFILE_ID, COMM_PROFILE_SET_ID,
    IN_SUSPEND, OUT_SUSPEND, OUTLET_CODE, SUBOUTLET_CODE, ACTIVATED_ON,
    APPLICATION_ID, MPAY_PROFILE_ID, USER_PROFILE_ID, IS_PRIMARY, MCOMMERCE_SERVICE_ALLOW,
    LOW_BAL_ALERT_ALLOW, MCATEGORY_CODE, ALERT_MSISDN, ALERT_TYPE, ALERT_EMAIL,
    VOMS_DECRYP_KEY, TRF_RULE_TYPE, AUTO_O2C_ALLOW, AUTO_FOC_ALLOW, LMS_PROFILE_UPDATED_ON,
    LMS_PROFILE, REF_BASED, ASSOCIATED_MSISDN, ASSOCIATED_MSISDN_TYPE, ASSOCIATED_MSISDN_CDATE,
    ASSOCIATED_MSISDN_MDATE, AUTO_C2C_ALLOW, AUTO_C2C_QUANTITY, OPT_IN_OUT_STATUS, OPT_IN_OUT_NOTIFY_DATE,
    OPT_IN_OUT_RESPONSE_DATE, CONTROL_GROUP,ENTRY_DATE,OPERATION_PERFORMED)
VALUES(NEW.USER_ID, NEW.USER_GRADE, NEW.CONTACT_PERSON, NEW.TRANSFER_PROFILE_ID, NEW.COMM_PROFILE_SET_ID,
    NEW.IN_SUSPEND, NEW.OUT_SUSPEND, NEW.OUTLET_CODE, NEW.SUBOUTLET_CODE, NEW.ACTIVATED_ON, NEW.APPLICATION_ID, NEW.MPAY_PROFILE_ID, NEW.USER_PROFILE_ID, NEW.IS_PRIMARY, NEW.MCOMMERCE_SERVICE_ALLOW, NEW.LOW_BAL_ALERT_ALLOW, NEW.MCATEGORY_CODE, NEW.ALERT_MSISDN, NEW.ALERT_TYPE, NEW.ALERT_EMAIL, NEW.VOMS_DECRYP_KEY, NEW.TRF_RULE_TYPE, NEW.AUTO_O2C_ALLOW, NEW.AUTO_FOC_ALLOW, NEW.LMS_PROFILE_UPDATED_ON, NEW.LMS_PROFILE, NEW.REF_BASED, NEW.ASSOCIATED_MSISDN, NEW.ASSOCIATED_MSISDN_TYPE, NEW.ASSOCIATED_MSISDN_CDATE, NEW.ASSOCIATED_MSISDN_MDATE, NEW.AUTO_C2C_ALLOW, NEW.AUTO_C2C_QUANTITY, NEW.OPT_IN_OUT_STATUS, NEW.OPT_IN_OUT_NOTIFY_DATE, NEW.OPT_IN_OUT_RESPONSE_DATE, NEW.CONTROL_GROUP,sysdate,'I');
ELSIF (TG_OP = 'UPDATE') AND (nvl(NEW.LMS_PROFILE,'XYZ') <> nvl(OLD.LMS_PROFILE,'XYZ')) then
INSERT INTO CHANNEL_USERS_HISTORY (USER_ID, USER_GRADE, CONTACT_PERSON, TRANSFER_PROFILE_ID, COMM_PROFILE_SET_ID,
    IN_SUSPEND, OUT_SUSPEND, OUTLET_CODE, SUBOUTLET_CODE, ACTIVATED_ON,
    APPLICATION_ID, MPAY_PROFILE_ID, USER_PROFILE_ID, IS_PRIMARY, MCOMMERCE_SERVICE_ALLOW,
    LOW_BAL_ALERT_ALLOW, MCATEGORY_CODE, ALERT_MSISDN, ALERT_TYPE, ALERT_EMAIL,
    VOMS_DECRYP_KEY, TRF_RULE_TYPE, AUTO_O2C_ALLOW, AUTO_FOC_ALLOW, LMS_PROFILE_UPDATED_ON,
    LMS_PROFILE, REF_BASED, ASSOCIATED_MSISDN, ASSOCIATED_MSISDN_TYPE, ASSOCIATED_MSISDN_CDATE,
    ASSOCIATED_MSISDN_MDATE, AUTO_C2C_ALLOW, AUTO_C2C_QUANTITY, OPT_IN_OUT_STATUS, OPT_IN_OUT_NOTIFY_DATE,
    OPT_IN_OUT_RESPONSE_DATE, CONTROL_GROUP,ENTRY_DATE,OPERATION_PERFORMED)
VALUES(OLD.USER_ID,NVL( OLD.USER_GRADE, NEW.USER_GRADE), OLD.CONTACT_PERSON,NVL( OLD.TRANSFER_PROFILE_ID, NEW.TRANSFER_PROFILE_ID), NVL(OLD.COMM_PROFILE_SET_ID, NEW.COMM_PROFILE_SET_ID),OLD.IN_SUSPEND, OLD.OUT_SUSPEND, OLD.OUTLET_CODE, OLD.SUBOUTLET_CODE, OLD.ACTIVATED_ON, OLD.APPLICATION_ID, OLD.MPAY_PROFILE_ID, NVL(OLD.USER_PROFILE_ID,NEW.USER_PROFILE_ID), OLD.IS_PRIMARY, OLD.MCOMMERCE_SERVICE_ALLOW, OLD.LOW_BAL_ALERT_ALLOW, OLD.MCATEGORY_CODE, OLD.ALERT_MSISDN, OLD.ALERT_TYPE, OLD.ALERT_EMAIL, OLD.VOMS_DECRYP_KEY, OLD.TRF_RULE_TYPE, OLD.AUTO_O2C_ALLOW, OLD.AUTO_FOC_ALLOW, NEW.LMS_PROFILE_UPDATED_ON, NEW.LMS_PROFILE, OLD.REF_BASED, OLD.ASSOCIATED_MSISDN, OLD.ASSOCIATED_MSISDN_TYPE, OLD.ASSOCIATED_MSISDN_CDATE, OLD.ASSOCIATED_MSISDN_MDATE, OLD.AUTO_C2C_ALLOW, OLD.AUTO_C2C_QUANTITY, NEW.OPT_IN_OUT_STATUS, NEW.OPT_IN_OUT_NOTIFY_DATE, NEW.OPT_IN_OUT_RESPONSE_DATE, NEW.CONTROL_GROUP,sysdate,'U');
ELSIF (TG_OP = 'UPDATE') AND (nvl(NEW.OPT_IN_OUT_STATUS,'XYZ') <> nvl(OLD.OPT_IN_OUT_STATUS,'XYZ')) then
INSERT INTO CHANNEL_USERS_HISTORY (USER_ID, USER_GRADE, CONTACT_PERSON, TRANSFER_PROFILE_ID, COMM_PROFILE_SET_ID,
    IN_SUSPEND, OUT_SUSPEND, OUTLET_CODE, SUBOUTLET_CODE, ACTIVATED_ON,
    APPLICATION_ID, MPAY_PROFILE_ID, USER_PROFILE_ID, IS_PRIMARY, MCOMMERCE_SERVICE_ALLOW,
    LOW_BAL_ALERT_ALLOW, MCATEGORY_CODE, ALERT_MSISDN, ALERT_TYPE, ALERT_EMAIL,
    VOMS_DECRYP_KEY, TRF_RULE_TYPE, AUTO_O2C_ALLOW, AUTO_FOC_ALLOW, LMS_PROFILE_UPDATED_ON,
    LMS_PROFILE, REF_BASED, ASSOCIATED_MSISDN, ASSOCIATED_MSISDN_TYPE, ASSOCIATED_MSISDN_CDATE,
    ASSOCIATED_MSISDN_MDATE, AUTO_C2C_ALLOW, AUTO_C2C_QUANTITY, OPT_IN_OUT_STATUS, OPT_IN_OUT_NOTIFY_DATE,
    OPT_IN_OUT_RESPONSE_DATE, CONTROL_GROUP,ENTRY_DATE,OPERATION_PERFORMED)
VALUES(OLD.USER_ID,NVL( OLD.USER_GRADE, NEW.USER_GRADE), OLD.CONTACT_PERSON,NVL( OLD.TRANSFER_PROFILE_ID, NEW.TRANSFER_PROFILE_ID), NVL(OLD.COMM_PROFILE_SET_ID, NEW.COMM_PROFILE_SET_ID),OLD.IN_SUSPEND, OLD.OUT_SUSPEND, OLD.OUTLET_CODE, OLD.SUBOUTLET_CODE, OLD.ACTIVATED_ON, OLD.APPLICATION_ID, OLD.MPAY_PROFILE_ID, NVL(OLD.USER_PROFILE_ID,NEW.USER_PROFILE_ID), OLD.IS_PRIMARY, OLD.MCOMMERCE_SERVICE_ALLOW, OLD.LOW_BAL_ALERT_ALLOW, OLD.MCATEGORY_CODE, OLD.ALERT_MSISDN, OLD.ALERT_TYPE, OLD.ALERT_EMAIL, OLD.VOMS_DECRYP_KEY, OLD.TRF_RULE_TYPE, OLD.AUTO_O2C_ALLOW, OLD.AUTO_FOC_ALLOW, NEW.LMS_PROFILE_UPDATED_ON, NEW.LMS_PROFILE, OLD.REF_BASED, OLD.ASSOCIATED_MSISDN, OLD.ASSOCIATED_MSISDN_TYPE, OLD.ASSOCIATED_MSISDN_CDATE, OLD.ASSOCIATED_MSISDN_MDATE, OLD.AUTO_C2C_ALLOW, OLD.AUTO_C2C_QUANTITY, NEW.OPT_IN_OUT_STATUS, NEW.OPT_IN_OUT_NOTIFY_DATE, NEW.OPT_IN_OUT_RESPONSE_DATE, NEW.CONTROL_GROUP,sysdate,'U');
ELSIF (TG_OP = 'UPDATE') AND (nvl(NEW.CONTROL_GROUP,'XYZ') <> nvl(OLD.CONTROL_GROUP,'XYZ')) then
INSERT INTO CHANNEL_USERS_HISTORY (USER_ID, USER_GRADE, CONTACT_PERSON, TRANSFER_PROFILE_ID, COMM_PROFILE_SET_ID,
    IN_SUSPEND, OUT_SUSPEND, OUTLET_CODE, SUBOUTLET_CODE, ACTIVATED_ON,
    APPLICATION_ID, MPAY_PROFILE_ID, USER_PROFILE_ID, IS_PRIMARY, MCOMMERCE_SERVICE_ALLOW,
    LOW_BAL_ALERT_ALLOW, MCATEGORY_CODE, ALERT_MSISDN, ALERT_TYPE, ALERT_EMAIL,
    VOMS_DECRYP_KEY, TRF_RULE_TYPE, AUTO_O2C_ALLOW, AUTO_FOC_ALLOW, LMS_PROFILE_UPDATED_ON,
    LMS_PROFILE, REF_BASED, ASSOCIATED_MSISDN, ASSOCIATED_MSISDN_TYPE, ASSOCIATED_MSISDN_CDATE,
    ASSOCIATED_MSISDN_MDATE, AUTO_C2C_ALLOW, AUTO_C2C_QUANTITY, OPT_IN_OUT_STATUS, OPT_IN_OUT_NOTIFY_DATE,
    OPT_IN_OUT_RESPONSE_DATE, CONTROL_GROUP,ENTRY_DATE,OPERATION_PERFORMED)
VALUES(OLD.USER_ID,NVL( OLD.USER_GRADE, NEW.USER_GRADE), OLD.CONTACT_PERSON,NVL( OLD.TRANSFER_PROFILE_ID, NEW.TRANSFER_PROFILE_ID), NVL(OLD.COMM_PROFILE_SET_ID, NEW.COMM_PROFILE_SET_ID),OLD.IN_SUSPEND, OLD.OUT_SUSPEND, OLD.OUTLET_CODE, OLD.SUBOUTLET_CODE, OLD.ACTIVATED_ON, OLD.APPLICATION_ID, OLD.MPAY_PROFILE_ID, NVL(OLD.USER_PROFILE_ID,NEW.USER_PROFILE_ID), OLD.IS_PRIMARY, OLD.MCOMMERCE_SERVICE_ALLOW, OLD.LOW_BAL_ALERT_ALLOW, OLD.MCATEGORY_CODE, OLD.ALERT_MSISDN, OLD.ALERT_TYPE, OLD.ALERT_EMAIL, OLD.VOMS_DECRYP_KEY, OLD.TRF_RULE_TYPE, OLD.AUTO_O2C_ALLOW, OLD.AUTO_FOC_ALLOW, NEW.LMS_PROFILE_UPDATED_ON, NEW.LMS_PROFILE, OLD.REF_BASED, OLD.ASSOCIATED_MSISDN, OLD.ASSOCIATED_MSISDN_TYPE, OLD.ASSOCIATED_MSISDN_CDATE, OLD.ASSOCIATED_MSISDN_MDATE, OLD.AUTO_C2C_ALLOW, OLD.AUTO_C2C_QUANTITY, NEW.OPT_IN_OUT_STATUS, NEW.OPT_IN_OUT_NOTIFY_DATE, NEW.OPT_IN_OUT_RESPONSE_DATE, NEW.CONTROL_GROUP,sysdate,'U');
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO CHANNEL_USERS_HISTORY (USER_ID, USER_GRADE, CONTACT_PERSON, TRANSFER_PROFILE_ID, COMM_PROFILE_SET_ID,
    IN_SUSPEND, OUT_SUSPEND, OUTLET_CODE, SUBOUTLET_CODE, ACTIVATED_ON,
    APPLICATION_ID, MPAY_PROFILE_ID, USER_PROFILE_ID, IS_PRIMARY, MCOMMERCE_SERVICE_ALLOW,
    LOW_BAL_ALERT_ALLOW, MCATEGORY_CODE, ALERT_MSISDN, ALERT_TYPE, ALERT_EMAIL,
    VOMS_DECRYP_KEY, TRF_RULE_TYPE, AUTO_O2C_ALLOW, AUTO_FOC_ALLOW, LMS_PROFILE_UPDATED_ON,
    LMS_PROFILE, REF_BASED, ASSOCIATED_MSISDN, ASSOCIATED_MSISDN_TYPE, ASSOCIATED_MSISDN_CDATE,
    ASSOCIATED_MSISDN_MDATE, AUTO_C2C_ALLOW, AUTO_C2C_QUANTITY, OPT_IN_OUT_STATUS, OPT_IN_OUT_NOTIFY_DATE,
    OPT_IN_OUT_RESPONSE_DATE, CONTROL_GROUP,ENTRY_DATE,OPERATION_PERFORMED)
VALUES(OLD.USER_ID, OLD.USER_GRADE, OLD.CONTACT_PERSON, OLD.TRANSFER_PROFILE_ID, OLD.COMM_PROFILE_SET_ID,OLD.IN_SUSPEND, OLD.OUT_SUSPEND, OLD.OUTLET_CODE, OLD.SUBOUTLET_CODE, OLD.ACTIVATED_ON, OLD.APPLICATION_ID, OLD.MPAY_PROFILE_ID, OLD.USER_PROFILE_ID, OLD.IS_PRIMARY, OLD.MCOMMERCE_SERVICE_ALLOW, OLD.LOW_BAL_ALERT_ALLOW, OLD.MCATEGORY_CODE, OLD.ALERT_MSISDN, OLD.ALERT_TYPE, OLD.ALERT_EMAIL, OLD.VOMS_DECRYP_KEY, OLD.TRF_RULE_TYPE, OLD.AUTO_O2C_ALLOW, OLD.AUTO_FOC_ALLOW, OLD.LMS_PROFILE_UPDATED_ON, OLD.LMS_PROFILE, OLD.REF_BASED, OLD.ASSOCIATED_MSISDN, OLD.ASSOCIATED_MSISDN_TYPE, OLD.ASSOCIATED_MSISDN_CDATE, OLD.ASSOCIATED_MSISDN_MDATE, OLD.AUTO_C2C_ALLOW, OLD.AUTO_C2C_QUANTITY, OLD.OPT_IN_OUT_STATUS, OLD.OPT_IN_OUT_NOTIFY_DATE, OLD.OPT_IN_OUT_RESPONSE_DATE, OLD.CONTROL_GROUP,sysdate,'D');
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_channel_users_history() OWNER TO pgdb;

--
-- Name: trig_chnl_trf_rules_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_chnl_trf_rules_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO CHNL_TRANSFER_RULES_HISTORY(transfer_rule_id, domain_code,
network_code, from_category, to_category, transfer_chnl_bypass_allowed,
withdraw_allowed, withdraw_chnl_bypass_allowed, return_allowed,
return_chnl_bypass_allowed, approval_required, first_approval_limit,
second_approval_limit, created_by, created_on, modified_by, modified_on,
status, entry_date, operation_performed, transfer_type, parent_association_allowed,
direct_transfer_allowed, transfer_allowed, foc_transfer_type, foc_allowed,
type, uncntrl_transfer_allowed,restricted_msisdn_access,
to_domain_code, uncntrl_transfer_level, cntrl_transfer_level,
fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed,
uncntrl_return_level, cntrl_return_level, fixed_return_level,
fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level,
cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category,parent_assocation_allowed,previous_status,direct_payout_allowed)
VALUES(NEW.transfer_rule_id, NEW.domain_code,
NEW.network_code, NEW.from_category, NEW.to_category, NEW.transfer_chnl_bypass_allowed,
NEW.withdraw_allowed, NEW.withdraw_chnl_bypass_allowed, NEW.return_allowed,
NEW.return_chnl_bypass_allowed, NEW.approval_required, NEW.first_approval_limit,
NEW.second_approval_limit, NEW.created_by, NEW.created_on, NEW.modified_by, NEW.modified_on,
NEW.status, current_timestamp, 'I', NEW.transfer_type, NEW.parent_association_allowed,
NEW.direct_transfer_allowed, NEW.transfer_allowed, NEW.foc_transfer_type, NEW.foc_allowed,
NEW.type, NEW.uncntrl_transfer_allowed,NEW.restricted_msisdn_access,
NEW.to_domain_code, NEW.uncntrl_transfer_level, NEW.cntrl_transfer_level,
NEW.fixed_transfer_level, NEW.fixed_transfer_category, NEW.uncntrl_return_allowed,
NEW.uncntrl_return_level, NEW.cntrl_return_level, NEW.fixed_return_level,
NEW.fixed_return_category, NEW.uncntrl_withdraw_allowed, NEW.uncntrl_withdraw_level,
NEW.cntrl_withdraw_level, NEW.fixed_withdraw_level, NEW.fixed_withdraw_category,NEW.parent_assocation_allowed,NEW.previous_status,NEW.direct_payout_allowed);
ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO CHNL_TRANSFER_RULES_HISTORY(transfer_rule_id, domain_code,
network_code, from_category, to_category, transfer_chnl_bypass_allowed,
withdraw_allowed, withdraw_chnl_bypass_allowed, return_allowed,
return_chnl_bypass_allowed, approval_required, first_approval_limit,
second_approval_limit, created_by, created_on, modified_by, modified_on,
status, entry_date, operation_performed, transfer_type, parent_association_allowed,
direct_transfer_allowed, transfer_allowed, foc_transfer_type, foc_allowed,
type, uncntrl_transfer_allowed,restricted_msisdn_access,
to_domain_code, uncntrl_transfer_level, cntrl_transfer_level,
fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed,
uncntrl_return_level, cntrl_return_level, fixed_return_level,
fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level,
cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category,parent_assocation_allowed,previous_status,direct_payout_allowed)
VALUES(NEW.transfer_rule_id, NEW.domain_code,
NEW.network_code, NEW.from_category, NEW.to_category, NEW.transfer_chnl_bypass_allowed,
NEW.withdraw_allowed, NEW.withdraw_chnl_bypass_allowed, NEW.return_allowed,
NEW.return_chnl_bypass_allowed, NEW.approval_required, NEW.first_approval_limit,
NEW.second_approval_limit, NEW.created_by, NEW.created_on, NEW.modified_by, NEW.modified_on,
NEW.status, current_timestamp, 'U', NEW.transfer_type, NEW.parent_association_allowed,
NEW.direct_transfer_allowed, NEW.transfer_allowed, NEW.foc_transfer_type, NEW.foc_allowed,
NEW.type, NEW.uncntrl_transfer_allowed,NEW.restricted_msisdn_access,
NEW.to_domain_code, NEW.uncntrl_transfer_level, NEW.cntrl_transfer_level,
NEW.fixed_transfer_level, NEW.fixed_transfer_category, NEW.uncntrl_return_allowed,
NEW.uncntrl_return_level, NEW.cntrl_return_level, NEW.fixed_return_level,
NEW.fixed_return_category, NEW.uncntrl_withdraw_allowed, NEW.uncntrl_withdraw_level,
NEW.cntrl_withdraw_level, NEW.fixed_withdraw_level, NEW.fixed_withdraw_category,NEW.parent_assocation_allowed,NEW.previous_status,NEW.direct_payout_allowed);
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO CHNL_TRANSFER_RULES_HISTORY(transfer_rule_id, domain_code,
network_code, from_category, to_category, transfer_chnl_bypass_allowed,
withdraw_allowed, withdraw_chnl_bypass_allowed, return_allowed,
return_chnl_bypass_allowed, approval_required, first_approval_limit,
second_approval_limit, created_by, created_on, modified_by, modified_on,
status, entry_date, operation_performed, transfer_type, parent_association_allowed,
direct_transfer_allowed, transfer_allowed, foc_transfer_type, foc_allowed,
type, uncntrl_transfer_allowed,restricted_msisdn_access,
to_domain_code, uncntrl_transfer_level, cntrl_transfer_level,
fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed,
uncntrl_return_level, cntrl_return_level, fixed_return_level,
fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level,
cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category,parent_assocation_allowed,previous_status,direct_payout_allowed)
VALUES(OLD.transfer_rule_id, OLD.domain_code,
OLD.network_code, OLD.from_category, OLD.to_category, OLD.transfer_chnl_bypass_allowed,
OLD.withdraw_allowed, OLD.withdraw_chnl_bypass_allowed, OLD.return_allowed,
OLD.return_chnl_bypass_allowed, OLD.approval_required, OLD.first_approval_limit,
OLD.second_approval_limit, OLD.created_by, OLD.created_on, OLD.modified_by, OLD.modified_on,
OLD.status, current_timestamp, 'D', OLD.transfer_type, OLD.parent_association_allowed,
OLD.direct_transfer_allowed, OLD.transfer_allowed, OLD.foc_transfer_type, OLD.foc_allowed,
OLD.type, OLD.uncntrl_transfer_allowed,OLD.restricted_msisdn_access,
OLD.to_domain_code, OLD.uncntrl_transfer_level, OLD.cntrl_transfer_level,
OLD.fixed_transfer_level, OLD.fixed_transfer_category, OLD.uncntrl_return_allowed,
OLD.uncntrl_return_level, OLD.cntrl_return_level, OLD.fixed_return_level,
OLD.fixed_return_category, OLD.uncntrl_withdraw_allowed, OLD.uncntrl_withdraw_level,
OLD.cntrl_withdraw_level, OLD.fixed_withdraw_level, OLD.fixed_withdraw_category,OLD.parent_assocation_allowed,OLD.previous_status,OLD.direct_payout_allowed);
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_chnl_trf_rules_history() OWNER TO pgdb;

--
-- Name: trig_configurations_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_configurations_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN

IF (TG_OP = 'INSERT') THEN
INSERT INTO CONFIGURATIONS_HISTORY(INSTANCE_ID, INTERFACE_ID,
TYPE, KEY, VALUE, DESCRIPTION, MODIFIED_ALLOWED,
DISPLAY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, entry_date, operation_performed)
VALUES(NEW.INSTANCE_ID, NEW.INTERFACE_ID,
NEW.TYPE, NEW.KEY, NEW.VALUE, NEW.DESCRIPTION, NEW.MODIFIED_ALLOWED,
NEW.DISPLAY_ALLOWED, NEW.CREATED_ON, NEW.CREATED_BY, NEW.MODIFIED_ON, NEW.MODIFIED_BY, CURRENT_TIMESTAMP,'I');

ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO CONFIGURATIONS_HISTORY(INSTANCE_ID, INTERFACE_ID,
TYPE, KEY, VALUE, DESCRIPTION, MODIFIED_ALLOWED,
DISPLAY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, entry_date, operation_performed)
VALUES(NEW.INSTANCE_ID, NEW.INTERFACE_ID,
NEW.TYPE, NEW.KEY, NEW.VALUE, NEW.DESCRIPTION, NEW.MODIFIED_ALLOWED,
NEW.DISPLAY_ALLOWED, NEW.CREATED_ON, NEW.CREATED_BY, NEW.MODIFIED_ON, NEW.MODIFIED_BY, CURRENT_TIMESTAMP,'U');

ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO CONFIGURATIONS_HISTORY(INSTANCE_ID, INTERFACE_ID,
TYPE, KEY, VALUE, DESCRIPTION, MODIFIED_ALLOWED,
DISPLAY_ALLOWED, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, entry_date, operation_performed)
VALUES(OLD.INSTANCE_ID, OLD.INTERFACE_ID,
OLD.TYPE, OLD.KEY, OLD.VALUE, OLD.DESCRIPTION, OLD.MODIFIED_ALLOWED,
OLD.DISPLAY_ALLOWED, OLD.CREATED_ON, OLD.CREATED_BY, OLD.MODIFIED_ON, OLD.MODIFIED_BY, CURRENT_TIMESTAMP,'D');
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_configurations_history() OWNER TO pgdb;

--
-- Name: trig_control_prf_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_control_prf_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO CONTROL_PRF_HISTORY(control_code, network_code,
preference_code, value, created_on, created_by, modified_on,
modified_by, entry_date, operation_performed,type)
VALUES(NEW.control_code, NEW.network_code,
NEW.preference_code, NEW.value,NEW.created_on, NEW.created_by, NEW.modified_on,
NEW.modified_by,current_timestamp,'I',NEW.type);
ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO CONTROL_PRF_HISTORY(control_code, network_code,
preference_code, value, created_on, created_by, modified_on,
modified_by, entry_date, operation_performed,type)
VALUES(NEW.control_code, NEW.network_code,
NEW.preference_code, NEW.value,NEW.created_on, NEW.created_by, NEW.modified_on,
NEW.modified_by,current_timestamp,'U',NEW.type);
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO CONTROL_PRF_HISTORY(control_code, network_code,
preference_code, value, created_on, created_by, modified_on,
modified_by, entry_date, operation_performed,type)
VALUES(OLD.control_code, OLD.network_code,
OLD.preference_code, OLD.value,OLD.created_on, OLD.created_by, OLD.modified_on,
OLD.modified_by,current_timestamp,'D',OLD.type);
END IF;

RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_control_prf_history() OWNER TO pgdb;

--
-- Name: trig_network_prf_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_network_prf_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO NETWORK_PRF_HISTORY(network_code, preference_code,
value, created_on, created_by, modified_on, modified_by,
entry_date, operation_performed)
VALUES(NEW.network_code, NEW.preference_code,
NEW.value, NEW.created_on, NEW.created_by, NEW.modified_on, NEW.modified_by,current_timestamp,'I');
ELSIF (TG_OP = 'UPDATE')  THEN
INSERT INTO NETWORK_PRF_HISTORY(network_code, preference_code,
value, created_on, created_by, modified_on, modified_by,
entry_date, operation_performed)
VALUES(NEW.network_code, NEW.preference_code,
NEW.value, NEW.created_on, NEW.created_by, NEW.modified_on, NEW.modified_by,current_timestamp,'U');
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO NETWORK_PRF_HISTORY(network_code, preference_code,
value, created_on, created_by, modified_on, modified_by,
entry_date, operation_performed)
VALUES(OLD.network_code, OLD.preference_code,
OLD.value, OLD.created_on, OLD.created_by, OLD.modified_on, OLD.modified_by,current_timestamp,'D');
END IF;

RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_network_prf_history() OWNER TO pgdb;

--
-- Name: trig_network_stocks_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_network_stocks_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO NETWORK_STOCKS_HISTORY (network_code,network_code_for,product_code,wallet_type,
wallet_created,wallet_returned,wallet_balance,wallet_sold,last_txn_no,last_txn_type,
last_txn_balance,previous_balance,modified_by,modified_on,created_on,created_by,entry_date,operation_performed,daily_stock_updated_on)
VALUES (NEW.network_code,NEW.network_code_for,NEW.product_code,NEW.wallet_type,
NEW.wallet_created,NEW.wallet_returned,NEW.wallet_balance,NEW.wallet_sold,NEW.last_txn_no,NEW.last_txn_type,
NEW.last_txn_balance,NEW.previous_balance,NEW.modified_by,NEW.modified_on,NEW.created_on,NEW.created_by,current_timestamp,'I',current_timestamp);
ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO NETWORK_STOCKS_HISTORY (network_code,network_code_for,product_code,wallet_type,
wallet_created,wallet_returned,wallet_balance,wallet_sold,last_txn_no,last_txn_type,
last_txn_balance,previous_balance,modified_by,modified_on,created_on,created_by,entry_date,operation_performed,daily_stock_updated_on)
VALUES (NEW.network_code,NEW.network_code_for,NEW.product_code,NEW.wallet_type,
NEW.wallet_created,NEW.wallet_returned,NEW.wallet_balance,NEW.wallet_sold,
NEW.last_txn_no,NEW.last_txn_type,NEW.last_txn_balance,OLD.wallet_balance,
NEW.modified_by,NEW.modified_on,NEW.created_on,NEW.created_by,current_timestamp,'U',NEW.daily_stock_updated_on);
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO NETWORK_STOCKS_HISTORY (network_code,network_code_for,product_code,wallet_type,
wallet_created,wallet_returned,wallet_balance,wallet_sold,last_txn_no,last_txn_type,
last_txn_balance,previous_balance,modified_by,modified_on,created_on,created_by,entry_date,operation_performed,daily_stock_updated_on)
VALUES (OLD.network_code,OLD.network_code_for,OLD.product_code,OLD.wallet_type,
OLD.wallet_created,OLD.wallet_returned,OLD.wallet_balance,OLD.wallet_sold,
OLD.last_txn_no,OLD.last_txn_type,OLD.last_txn_balance,OLD.wallet_balance,
OLD.modified_by,OLD.modified_on,OLD.created_on,OLD.created_by,current_timestamp,'D',OLD.daily_stock_updated_on);
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_network_stocks_history() OWNER TO pgdb;

--
-- Name: trig_networks_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_networks_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO NETWORKS_HISTORY(network_code, network_name, network_short_name,
company_name, report_header_name, erp_network_code, address1, address2,
city, state, zip_code, country, network_type, status, remarks, language_1_message,
language_2_message, text_1_value, text_2_value, country_prefix_code, mis_done_date,
created_on, created_by, modified_on, modified_by, service_set_id, entry_date,
operation_performed)
VALUES (NEW.network_code,NEW.network_name,NEW.network_short_name,
NEW.company_name,NEW.report_header_name,NEW.erp_network_code,NEW.address1,NEW.address2,
NEW.city,NEW.state,NEW.zip_code, NEW.country, NEW.network_type, NEW.status, NEW.remarks, NEW.language_1_message,
NEW.language_2_message, NEW.text_1_value, NEW.text_2_value, NEW.country_prefix_code, NEW.mis_done_date,
NEW.created_on,NEW.created_by,NEW.created_on,NEW.created_by,NEW.service_set_id,current_timestamp,
'I');
ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO NETWORKS_HISTORY(network_code, network_name, network_short_name,
company_name, report_header_name, erp_network_code, address1, address2,
city, state, zip_code, country, network_type, status, remarks, language_1_message,
language_2_message, text_1_value, text_2_value, country_prefix_code, mis_done_date,
created_on, created_by, modified_on, modified_by, service_set_id, entry_date,
operation_performed)
VALUES(NEW.network_code,NEW.network_name,NEW.network_short_name,
NEW.company_name,NEW.report_header_name,NEW.erp_network_code,NEW.address1,NEW.address2,
NEW.city,NEW.state,NEW.zip_code, NEW.country, NEW.network_type, NEW.status, NEW.remarks, NEW.language_1_message,
NEW.language_2_message, NEW.text_1_value, NEW.text_2_value, NEW.country_prefix_code, NEW.mis_done_date,
NEW.created_on,NEW.created_by,NEW.modified_on,NEW.modified_by,NEW.service_set_id,current_timestamp,
'U');
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO NETWORKS_HISTORY(network_code, network_name, network_short_name,
company_name, report_header_name, erp_network_code, address1, address2,
city, state, zip_code, country, network_type, status, remarks, language_1_message,
language_2_message, text_1_value, text_2_value, country_prefix_code, mis_done_date,
created_on, created_by, modified_on, modified_by, service_set_id, entry_date,
operation_performed)
VALUES(OLD.network_code,OLD.network_name,OLD.network_short_name,
OLD.company_name,OLD.report_header_name,OLD.erp_network_code,OLD.address1,OLD.address2,
OLD.city,OLD.state,OLD.zip_code, OLD.country, OLD.network_type, OLD.status, OLD.remarks, OLD.language_1_message,
OLD.language_2_message, OLD.text_1_value, OLD.text_2_value, OLD.country_prefix_code, OLD.mis_done_date,
OLD.created_on,OLD.created_by,OLD.modified_on,OLD.modified_by,OLD.service_set_id,current_timestamp,
'D');
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_networks_history() OWNER TO pgdb;

--
-- Name: trig_ota_adm_txn_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_ota_adm_txn_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO OTA_ADM_TRANSACTION_HISTORY(msisdn,transaction_id,operation,
response,created_by,created_on,lock_time,entry_date,operation_performed)
VALUES(NEW.msisdn,NEW.transaction_id,NEW.operation,NEW.response,
NEW.created_by,NEW.created_on,NEW.lock_time,current_timestamp,'I');
ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO OTA_ADM_TRANSACTION_HISTORY(msisdn,transaction_id,operation,
response,created_by,created_on,lock_time,entry_date,operation_performed)
VALUES(NEW.msisdn,NEW.transaction_id,NEW.operation,NEW.response,
NEW.created_by,NEW.created_on,NEW.lock_time,current_timestamp,'U');
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO OTA_ADM_TRANSACTION_HISTORY(msisdn,transaction_id,operation,
response,created_by,created_on,lock_time,entry_date,operation_performed)
VALUES(OLD.msisdn,OLD.transaction_id,OLD.operation,OLD.response,
OLD.created_by,OLD.created_on,OLD.lock_time,current_timestamp,'D');
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_ota_adm_txn_history() OWNER TO pgdb;

--
-- Name: trig_p2p_buddies_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_p2p_buddies_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN

IF (TG_OP = 'INSERT') THEN
INSERT INTO P2P_BUDDIES_HISTORY(buddy_msisdn,parent_id,buddy_seq_num,
buddy_name,status,buddy_last_transfer_id,buddy_last_transfer_on,
buddy_last_transfer_type,buddy_total_transfer,buddy_total_transfer_amt,
created_on,created_by,modified_on,modified_by,preferred_amount,
last_transfer_amount,prefix_id)
VALUES (NEW.buddy_msisdn,NEW.parent_id,NEW.buddy_seq_num,
NEW.buddy_name,NEW.status,null,null,null,null,null,
NEW.created_on,NEW.created_by,current_timestamp,'SYSTEM',NEW.preferred_amount,
null,NEW.prefix_id);
ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO P2P_BUDDIES_HISTORY(buddy_msisdn,parent_id,buddy_seq_num,
buddy_name,status,buddy_last_transfer_id,buddy_last_transfer_on,
buddy_last_transfer_type,buddy_total_transfer,buddy_total_transfer_amt,
created_on,created_by,modified_on,modified_by,preferred_amount,
last_transfer_amount,prefix_id)
VALUES (OLD.buddy_msisdn,OLD.parent_id,OLD.buddy_seq_num,
OLD.buddy_name,NEW.status,OLD.buddy_last_transfer_id,OLD.buddy_last_transfer_on,
OLD.buddy_last_transfer_type,OLD.buddy_total_transfer,OLD.buddy_total_transfer_amt,
OLD.created_on,OLD.created_by,current_timestamp,'SYSTEM',NEW.preferred_amount,
OLD.last_transfer_amount,OLD.prefix_id);
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO P2P_BUDDIES_HISTORY(buddy_msisdn,parent_id,buddy_seq_num,
buddy_name,status,buddy_last_transfer_id,buddy_last_transfer_on,
buddy_last_transfer_type,buddy_total_transfer,buddy_total_transfer_amt,
created_on,created_by,modified_on,modified_by,preferred_amount,
last_transfer_amount,prefix_id)
VALUES (OLD.buddy_msisdn,OLD.parent_id,OLD.buddy_seq_num,
OLD.buddy_name,OLD.status,OLD.buddy_last_transfer_id,OLD.buddy_last_transfer_on,
OLD.buddy_last_transfer_type,OLD.buddy_total_transfer,OLD.buddy_total_transfer_amt,
OLD.created_on,OLD.created_by,current_timestamp,'SYSTEM',OLD.preferred_amount,
OLD.last_transfer_amount,OLD.prefix_id);
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_p2p_buddies_history() OWNER TO pgdb;

--
-- Name: trig_pos_key_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_pos_key_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
 IF (OLD.msisdn is not null AND OLD.modified_on is not null) THEN
      INSERT INTO POS_KEY_HISTORY (icc_id,msisdn,modified_by,modified_on,
	  created_on,created_by,network_code,sim_profile_id)
      VALUES  (OLD.icc_id,OLD.msisdn,OLD.modified_by,OLD.modified_on,
	  OLD.created_on,OLD.created_by,OLD.network_code,OLD.sim_profile_id);
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_pos_key_history() OWNER TO pgdb;

--
-- Name: trig_post_pay_cust_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_post_pay_cust_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO POSTPAID_CUST_PAY_HISTORY
(queue_id, network_code, msisdn, account_id, 
amount, transfer_id, status, entry_date, 
description, process_id, process_date, other_info, 
service_type, entry_type, process_status, module_code, 
sender_id, created_on, source_type, interface_id, 
external_id, service_class, product_code, tax_amount, 
access_fee_amount, entry_for, bonus_amount, sender_msisdn, 
cdr_file_name, gateway_code, interface_amount, imsi, 
operation_performed, record_entry_date,receiver_msisdn,type)
VALUES(NEW.queue_id, NEW.network_code, NEW.msisdn, NEW.account_id, 
NEW.amount, NEW.transfer_id, NEW.status, NEW.entry_date, 
NEW.description, NEW.process_id, NEW.process_date, NEW.other_info, 
NEW.service_type, NEW.entry_type, NEW.process_status, NEW.module_code, 
NEW.sender_id, NEW.created_on, NEW.source_type, NEW.interface_id, 
NEW.external_id, NEW.service_class, NEW.product_code, NEW.tax_amount, 
NEW.access_fee_amount, NEW.entry_for, NEW.bonus_amount, NEW.sender_msisdn, 
NEW.cdr_file_name, NEW.gateway_code, NEW.interface_amount, NEW.imsi, 
'I',current_timestamp,NEW.receiver_msisdn,NEW.type );
ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO POSTPAID_CUST_PAY_HISTORY
(queue_id, network_code, msisdn, account_id, 
amount, transfer_id, status, entry_date, 
description, process_id, process_date, other_info, 
service_type, entry_type, process_status, module_code, 
sender_id, created_on, source_type, interface_id, 
external_id, service_class, product_code, tax_amount, 
access_fee_amount, entry_for, bonus_amount, sender_msisdn, 
cdr_file_name, gateway_code, interface_amount, imsi, 
operation_performed, record_entry_date,receiver_msisdn,type)
VALUES(NEW.queue_id, NEW.network_code, NEW.msisdn, NEW.account_id, 
NEW.amount, NEW.transfer_id, NEW.status, NEW.entry_date, 
NEW.description, NEW.process_id, NEW.process_date, NEW.other_info, 
NEW.service_type, NEW.entry_type, NEW.process_status, NEW.module_code, 
NEW.sender_id, NEW.created_on, NEW.source_type, NEW.interface_id, 
NEW.external_id, NEW.service_class, NEW.product_code, NEW.tax_amount, 
NEW.access_fee_amount, NEW.entry_for, NEW.bonus_amount, NEW.sender_msisdn, 
NEW.cdr_file_name, NEW.gateway_code, NEW.interface_amount, NEW.imsi, 
'U',current_timestamp,NEW.receiver_msisdn,NEW.type);
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO POSTPAID_CUST_PAY_HISTORY
(queue_id, network_code, msisdn, account_id, 
amount, transfer_id, status, entry_date, 
description, process_id, process_date, other_info, 
service_type, entry_type, process_status, module_code, 
sender_id, created_on, source_type, interface_id, 
external_id, service_class, product_code, tax_amount, 
access_fee_amount, entry_for, bonus_amount, sender_msisdn, 
cdr_file_name, gateway_code, interface_amount, imsi, 
operation_performed, record_entry_date,receiver_msisdn,type)
VALUES(OLD.queue_id, OLD.network_code, OLD.msisdn, OLD.account_id, 
OLD.amount, OLD.transfer_id, OLD.status, OLD.entry_date, 
OLD.description, OLD.process_id, OLD.process_date, OLD.other_info, 
OLD.service_type, OLD.entry_type, OLD.process_status, OLD.module_code, 
OLD.sender_id, OLD.created_on, OLD.source_type, OLD.interface_id, 
OLD.external_id, OLD.service_class, OLD.product_code, OLD.tax_amount, 
OLD.access_fee_amount, OLD.entry_for, OLD.bonus_amount, OLD.sender_msisdn, 
OLD.cdr_file_name, OLD.gateway_code, OLD.interface_amount, OLD.imsi, 
'D',current_timestamp,OLD.receiver_msisdn,OLD.type);
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_post_pay_cust_history() OWNER TO pgdb;

--
-- Name: trig_reg_info_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_reg_info_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO REG_INFO_HISTORY(msisdn, transaction_id, operation,
created_by, created_on, entry_date, operation_performed)
VALUES (NEW.msisdn, NEW.transaction_id, NEW.operation,
NEW.created_by, NEW.created_on, current_timestamp, 'I');
ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO REG_INFO_HISTORY(msisdn, transaction_id, operation,
created_by, created_on, entry_date, operation_performed)
VALUES(NEW.msisdn, NEW.transaction_id, NEW.operation,
NEW.created_by, NEW.created_on, current_timestamp, 'U');
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO REG_INFO_HISTORY(msisdn, transaction_id, operation,
created_by, created_on, entry_date, operation_performed)
VALUES(OLD.msisdn, OLD.transaction_id, OLD.operation,
OLD.created_by, OLD.created_on, current_timestamp,'D');
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_reg_info_history() OWNER TO pgdb;

--
-- Name: trig_restricted_msisdn_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_restricted_msisdn_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'UPDATE') THEN
INSERT INTO RESTRICTED_MSISDNS_HISTORY (msisdn,
subscriber_id, channel_user_id, channel_user_category,
owner_id, employee_code, employee_name, network_code,
monthly_limit, min_txn_amount, max_txn_amount, total_txn_count,
total_txn_amount, black_list_status, remark, approved_by,
approved_on, associated_by, old_status, new_status, association_date, created_on,
created_by, modified_on, modified_by, entry_date,
operation_performed, language, country)
VALUES(NEW.msisdn,NEW.subscriber_id, NEW.channel_user_id, NEW.channel_user_category,
NEW.owner_id, NEW.employee_code, NEW.employee_name, NEW.network_code,
NEW.monthly_limit, NEW.min_txn_amount, NEW.max_txn_amount, NEW.total_txn_count,
NEW.total_txn_amount, NEW.black_list_status, NEW.remark, NEW.approved_by,
NEW.approved_on, NEW.associated_by, OLD.status,NEW.status, NEW.association_date, NEW.created_on,
NEW.created_by, NEW.modified_on, NEW.modified_by, current_timestamp,
'U', NEW.language, NEW.country);
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO RESTRICTED_MSISDNS_HISTORY (msisdn,
subscriber_id, channel_user_id, channel_user_category,
owner_id, employee_code, employee_name, network_code,
monthly_limit, min_txn_amount, max_txn_amount, total_txn_count,
total_txn_amount, black_list_status, remark, approved_by,
approved_on, associated_by, old_status, new_status, association_date, created_on,
created_by, modified_on, modified_by, entry_date,
operation_performed, language, country)
VALUES(OLD.msisdn,OLD.subscriber_id, OLD.channel_user_id, OLD.channel_user_category,
OLD.owner_id, OLD.employee_code, OLD.employee_name, OLD.network_code,
OLD.monthly_limit, OLD.min_txn_amount, OLD.max_txn_amount, OLD.total_txn_count,
OLD.total_txn_amount, OLD.black_list_status, OLD.remark, OLD.approved_by,
OLD.approved_on, OLD.associated_by, OLD.status, 'D', OLD.association_date, OLD.created_on,
OLD.created_by, OLD.modified_on, OLD.modified_by, current_timestamp,
'D', OLD.language, OLD.country);
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_restricted_msisdn_history() OWNER TO pgdb;

--
-- Name: trig_sch_batch_detail_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_sch_batch_detail_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO SCHEDULED_BATCH_DETAIL_HISTORY (batch_id, subscriber_id,
msisdn, amount, processed_on, status, transfer_id, transfer_status,error_code,
created_on, created_by, modified_on, modified_by,entry_date,operation_performed,sub_service)
VALUES(NEW.batch_id, NEW.subscriber_id,
NEW.msisdn, NEW.amount, NEW.processed_on, NEW.status, NEW.transfer_id, NEW.transfer_status,NEW.error_code,
NEW.created_on, NEW.created_by, NEW.modified_on, NEW.modified_by,current_timestamp,'I',NEW.sub_service);
ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO SCHEDULED_BATCH_DETAIL_HISTORY (batch_id, subscriber_id,
msisdn, amount, processed_on, status, transfer_id, transfer_status,error_code,
created_on, created_by, modified_on, modified_by,entry_date,operation_performed,sub_service)
VALUES(NEW.batch_id, NEW.subscriber_id,
NEW.msisdn, NEW.amount, NEW.processed_on, NEW.status, NEW.transfer_id, NEW.transfer_status,NEW.error_code,
NEW.created_on, NEW.created_by, NEW.modified_on, NEW.modified_by,current_timestamp,'U',NEW.sub_service);
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO SCHEDULED_BATCH_DETAIL_HISTORY (batch_id, subscriber_id,
msisdn, amount, processed_on, status, transfer_id, transfer_status,error_code,
created_on, created_by, modified_on, modified_by,entry_date,operation_performed,sub_service)
VALUES(OLD.batch_id, OLD.subscriber_id,
OLD.msisdn, OLD.amount, OLD.processed_on, OLD.status, OLD.transfer_id, OLD.transfer_status,OLD.error_code,
OLD.created_on, OLD.created_by, OLD.modified_on, OLD.modified_by,current_timestamp,'D',OLD.sub_service);
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_sch_batch_detail_history() OWNER TO pgdb;

--
-- Name: trig_sch_batch_master_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_sch_batch_master_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO SCHEDULED_BATCH_MASTER_HISTORY (batch_id,
status, network_code, total_count, successful_count,
upload_failed_count, process_failed_count, cancelled_count,
scheduled_date, parent_id, owner_id, parent_category,
parent_domain, SERVICE_TYPE, created_on, created_by,
modified_on, modified_by, initiated_by, entry_date,
operation_performed,ref_batch_id,active_user_id)
VALUES(NEW.batch_id,
NEW.status, NEW.network_code, NEW.total_count, NEW.successful_count,
NEW.upload_failed_count, NEW.process_failed_count, NEW.cancelled_count,
NEW.scheduled_date, NEW.parent_id, NEW.owner_id, NEW.parent_category,
NEW.parent_domain, NEW.SERVICE_TYPE, NEW.created_on, NEW.created_by,
NEW.modified_on, NEW.modified_by, NEW.initiated_by, current_timestamp, 'I',NEW.ref_batch_id,NEW.active_user_id);
ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO SCHEDULED_BATCH_MASTER_HISTORY (batch_id,
status, network_code, total_count, successful_count,
upload_failed_count, process_failed_count, cancelled_count,
scheduled_date, parent_id, owner_id, parent_category,
parent_domain, SERVICE_TYPE, created_on, created_by,
modified_on, modified_by, initiated_by, entry_date,
operation_performed,ref_batch_id,active_user_id)
VALUES(NEW.batch_id,
NEW.status, NEW.network_code, NEW.total_count, NEW.successful_count,
NEW.upload_failed_count, NEW.process_failed_count, NEW.cancelled_count,
NEW.scheduled_date, NEW.parent_id, NEW.owner_id, NEW.parent_category,
NEW.parent_domain, NEW.SERVICE_TYPE, NEW.created_on, NEW.created_by,
NEW.modified_on, NEW.modified_by, NEW.initiated_by, current_timestamp, 'U',NEW.ref_batch_id,NEW.active_user_id);
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO SCHEDULED_BATCH_MASTER_HISTORY (batch_id,
status, network_code, total_count, successful_count,
upload_failed_count, process_failed_count, cancelled_count,
scheduled_date, parent_id, owner_id, parent_category,
parent_domain, SERVICE_TYPE, created_on, created_by,
modified_on, modified_by, initiated_by, entry_date,
operation_performed,ref_batch_id,active_user_id)
VALUES(OLD.batch_id,
OLD.status, OLD.network_code, OLD.total_count, OLD.successful_count,
OLD.upload_failed_count, OLD.process_failed_count, OLD.cancelled_count,
OLD.scheduled_date, OLD.parent_id, OLD.owner_id, OLD.parent_category,
OLD.parent_domain, OLD.SERVICE_TYPE, OLD.created_on, OLD.created_by,
OLD.modified_on, OLD.modified_by, OLD.initiated_by, current_timestamp, 'D',OLD.ref_batch_id,NEW.active_user_id);
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_sch_batch_master_history() OWNER TO pgdb;

--
-- Name: trig_service_class_prf_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_service_class_prf_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO SERVICE_CLASS_PRF_HISTORY(module, network_code,
service_class_id, preference_code, value, created_on, created_by,
modified_on, modified_by, entry_date, operation_performed)
VALUES(NEW.module, NEW.network_code,
NEW.service_class_id, NEW.preference_code, NEW.value, NEW.created_on, NEW.created_by,
NEW.modified_on, NEW.modified_by,current_timestamp,'I');
ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO SERVICE_CLASS_PRF_HISTORY(module, network_code,
service_class_id, preference_code, value, created_on, created_by,
modified_on, modified_by, entry_date, operation_performed)
VALUES(NEW.module, NEW.network_code,
NEW.service_class_id, NEW.preference_code, NEW.value, NEW.created_on, NEW.created_by,
NEW.modified_on, NEW.modified_by,current_timestamp,'U');
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO SERVICE_CLASS_PRF_HISTORY(module, network_code,
service_class_id, preference_code, value, created_on, created_by,
modified_on, modified_by, entry_date, operation_performed)
VALUES(OLD.module, OLD.network_code,
OLD.service_class_id, OLD.preference_code, OLD.value, OLD.created_on, OLD.created_by,
OLD.modified_on, OLD.modified_by,current_timestamp,'D');
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_service_class_prf_history() OWNER TO pgdb;

--
-- Name: trig_subs_routing_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_subs_routing_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
IF (TG_OP = 'UPDATE')  THEN
INSERT INTO subscriber_routing_history(msisdn,interface_id,subscriber_type,
external_interface_id,status,created_by,created_on,modified_by,
modified_on,text1,text2,entry_date,operation_performed)
VALUES(NEW.msisdn,NEW.interface_id,NEW.subscriber_type,NEW.external_interface_id,
NEW.status,NEW.created_by,NEW.created_on,NEW.modified_by,NEW.modified_on,
NEW.text1,NEW.text2,current_timestamp,'U');
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO subscriber_routing_history(msisdn,interface_id,subscriber_type,
external_interface_id,status,created_by,created_on,modified_by,
modified_on,text1,text2,entry_date,operation_performed)
VALUES(OLD.msisdn,OLD.interface_id,OLD.subscriber_type,OLD.external_interface_id,
OLD.status,OLD.created_by,OLD.created_on,OLD.modified_by,OLD.modified_on,
OLD.text1,OLD.text2,current_timestamp,'D');
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_subs_routing_history() OWNER TO pgdb;

--
-- Name: trig_subscriber_msisdn_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_subscriber_msisdn_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
  IF (TG_OP = 'INSERT') THEN
    INSERT INTO SUBSCRIBER_MSISDN_HISTORY (MSISDN,USER_SID,CREATED_ON,CREATED_BY,MODIFY_ON,MODIFY_BY,USER_NAME,REQUEST_GATEWAY_CODE,REQUEST_GATEWAY_TYPE)
	values (NEW.MSISDN,NEW.USER_SID,NEW.CREATED_ON,NEW.CREATED_BY,NEW.MODIFIED_ON,NEW.MODIFIED_BY,NEW.USER_NAME,NEW.REQUEST_GATEWAY_CODE,NEW.REQUEST_GATEWAY_TYPE) ;

  ELSE IF (TG_OP = 'UPDATE') THEN
    INSERT INTO SUBSCRIBER_MSISDN_HISTORY (MSISDN,USER_SID,CREATED_ON,CREATED_BY,MODIFY_ON,MODIFY_BY,USER_NAME,REQUEST_GATEWAY_CODE,REQUEST_GATEWAY_TYPE)
	values (NEW.MSISDN,NEW.USER_SID,NEW.CREATED_ON,NEW.CREATED_BY,NEW.MODIFIED_ON,NEW.MODIFIED_BY,NEW.USER_NAME,NEW.REQUEST_GATEWAY_CODE,NEW.REQUEST_GATEWAY_TYPE) ;

  ELSE IF (TG_OP = 'DELETE') THEN
    INSERT INTO SUBSCRIBER_MSISDN_HISTORY (MSISDN,USER_SID,CREATED_ON,CREATED_BY,MODIFY_ON,MODIFY_BY,USER_NAME,REQUEST_GATEWAY_CODE,REQUEST_GATEWAY_TYPE)
	values (OLD.MSISDN,OLD.USER_SID,OLD.CREATED_ON,OLD.CREATED_BY,OLD.MODIFIED_ON,OLD.MODIFIED_BY,OLD.USER_NAME,OLD.REQUEST_GATEWAY_CODE,OLD.REQUEST_GATEWAY_TYPE) ;
END IF;
END IF;
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_subscriber_msisdn_history() OWNER TO pgdb;

--
-- Name: trig_system_prf_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_system_prf_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO SYSTEM_PRF_HISTORY(preference_code, name,
type, value_type, default_value, min_value, max_value,
max_size, description, modified_allowed, display, module,
remarks, created_on, created_by, modified_on, modified_by,
allowed_values, fixed_value, entry_date, operation_performed)
VALUES(NEW.preference_code, NEW.name,
NEW.type, NEW.value_type, NEW.default_value, NEW.min_value, NEW.max_value,
NEW.max_size, NEW.description, NEW.modified_allowed, NEW.display, NEW.module,
NEW.remarks, NEW.created_on, NEW.created_by, NEW.modified_on, NEW.modified_by,
NEW.allowed_values, NEW.fixed_value,current_timestamp,'I');
ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO SYSTEM_PRF_HISTORY(preference_code, name,
type, value_type, default_value, min_value, max_value,
max_size, description, modified_allowed, display, module,
remarks, created_on, created_by, modified_on, modified_by,
allowed_values, fixed_value, entry_date, operation_performed)
VALUES(NEW.preference_code, NEW.name,
NEW.type, NEW.value_type, NEW.default_value, NEW.min_value, NEW.max_value,
NEW.max_size, NEW.description, NEW.modified_allowed, NEW.display, NEW.module,
NEW.remarks, NEW.created_on, NEW.created_by, NEW.modified_on, NEW.modified_by,
NEW.allowed_values, NEW.fixed_value,current_timestamp,'U');
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO SYSTEM_PRF_HISTORY(preference_code, name,
type, value_type, default_value, min_value, max_value,
max_size, description, modified_allowed, display, module,
remarks, created_on, created_by, modified_on, modified_by,
allowed_values, fixed_value, entry_date, operation_performed)
VALUES(OLD.preference_code, OLD.name,
OLD.type, OLD.value_type, OLD.default_value, OLD.min_value, OLD.max_value,
OLD.max_size, OLD.description, OLD.modified_allowed, OLD.display, OLD.module,
OLD.remarks, OLD.created_on, OLD.created_by, OLD.modified_on, OLD.modified_by,
OLD.allowed_values, OLD.fixed_value,current_timestamp,'D');
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_system_prf_history() OWNER TO pgdb;

--
-- Name: trig_test(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_test() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF NEW.APPLICATION_ID = 1 THEN
IF (TG_OP = 'INSERT') THEN
INSERT INTO TEST(ROLE_CODE,PAGE_CODE,APPLICATION_ID)
VALUES(NEW.ROLE_CODE,NEW.PAGE_CODE,'2');
ELSIF (TG_OP = 'UPDATE') THEN
UPDATE TEST SET ROLE_CODE=NEW.ROLE_CODE, PAGE_CODE=NEW.PAGE_CODE, APPLICATION_ID='2' 
WHERE  ROLE_CODE=OLD.ROLE_CODE AND PAGE_CODE=OLD.PAGE_CODE;
END IF;
ELSIF (TG_OP = 'DELETE') THEN
DELETE FROM TEST WHERE  ROLE_CODE=OLD.ROLE_CODE AND PAGE_CODE=OLD.PAGE_CODE;
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_test() OWNER TO pgdb;

--
-- Name: trig_transfer_rules_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_transfer_rules_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO TRANSFER_RULES_HISTORY(MODULE, network_code,
sender_subscriber_type, receiver_subscriber_type, sender_service_class_id,
receiver_service_class_id, created_on, created_by, modified_on, modified_by,
card_group_set_id, status, operation_performed, entry_date, sub_service,allowed_days,allowed_series,denied_series,gateway_code,category_code,grade_code)
VALUES(NEW.MODULE, NEW.network_code,NEW.sender_subscriber_type, NEW.receiver_subscriber_type,
NEW.sender_service_class_id,NEW.receiver_service_class_id,NEW.created_on,NEW.created_by,
NEW.modified_on,NEW.modified_by,NEW.card_group_set_id,NEW.status,'I',current_timestamp,NEW.sub_service,NEW.allowed_days,NEW.allowed_series,NEW.denied_series,NEW.gateway_code,NEW.category_code,NEW.grade_code);
ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO TRANSFER_RULES_HISTORY(MODULE, network_code,
sender_subscriber_type, receiver_subscriber_type, sender_service_class_id,
receiver_service_class_id, created_on, created_by, modified_on, modified_by,
card_group_set_id, status, operation_performed, entry_date, sub_service,allowed_days,allowed_series,denied_series,gateway_code,category_code,grade_code)
VALUES(NEW.MODULE, NEW.network_code,NEW.sender_subscriber_type, NEW.receiver_subscriber_type,
NEW.sender_service_class_id,NEW.receiver_service_class_id,NEW.created_on,NEW.created_by,
NEW.modified_on,NEW.modified_by,NEW.card_group_set_id,NEW.status,'U',current_timestamp,NEW.sub_service,NEW.allowed_days,NEW.allowed_series,NEW.denied_series,NEW.gateway_code,NEW.category_code,NEW.grade_code);
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO TRANSFER_RULES_HISTORY(MODULE, network_code,
sender_subscriber_type, receiver_subscriber_type, sender_service_class_id,
receiver_service_class_id, created_on, created_by, modified_on, modified_by,
card_group_set_id, status, operation_performed, entry_date, sub_service,allowed_days,allowed_series,denied_series,gateway_code,category_code,grade_code)
VALUES(OLD.MODULE, OLD.network_code,OLD.sender_subscriber_type, OLD.receiver_subscriber_type,
OLD.sender_service_class_id,OLD.receiver_service_class_id, OLD.created_on, OLD.created_by,
OLD.modified_on, OLD.modified_by,OLD.card_group_set_id, OLD.status, 'D',current_timestamp,OLD.sub_service,OLD.allowed_days,OLD.allowed_series,OLD.denied_series,OLD.gateway_code,OLD.category_code,OLD.grade_code);
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_transfer_rules_history() OWNER TO pgdb;

--
-- Name: trig_user_balances_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_user_balances_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF (TG_OP = 'INSERT') THEN
INSERT INTO USER_BALANCES_HISTORY(user_id, network_code, network_code_for,
product_code, prev_balance, balance,last_transfer_type, last_transfer_no,
last_transfer_on,operation_performed,entry_date,daily_balance_updated_on,balance_type)
VALUES(NEW.user_id,NEW.network_code,NEW.network_code_for,NEW.product_code,
0,NEW.balance,NEW.last_transfer_type, NEW.last_transfer_no,NEW.last_transfer_on,'I',current_timestamp,current_timestamp,NEW.balance_type);
ELSIF (TG_OP = 'UPDATE') THEN
INSERT INTO USER_BALANCES_HISTORY(user_id, network_code, network_code_for,
product_code, prev_balance, balance,last_transfer_type, last_transfer_no,
last_transfer_on,operation_performed,entry_date,daily_balance_updated_on,balance_type)
VALUES(NEW.user_id,NEW.network_code,NEW.network_code_for,NEW.product_code,
OLD.balance,NEW.balance,NEW.last_transfer_type,NEW.last_transfer_no,
NEW.last_transfer_on,'U',current_timestamp,NEW.daily_balance_updated_on,NEW.balance_type);
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO USER_BALANCES_HISTORY(user_id, network_code, network_code_for,
product_code, prev_balance, balance,last_transfer_type, last_transfer_no,
last_transfer_on, operation_performed,entry_date,daily_balance_updated_on,balance_type)
VALUES(OLD.user_id,OLD.network_code,OLD.network_code_for,OLD.product_code,
OLD.balance,NEW.balance,NEW.last_transfer_type,NEW.last_transfer_no,
NEW.last_transfer_on,'D',current_timestamp,OLD.daily_balance_updated_on,OLD.balance_type);
END IF;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_user_balances_history() OWNER TO pgdb;

--
-- Name: trig_user_phones_pin_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_user_phones_pin_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
DECLARE

vstatus VARCHAR (2);
   vapprovaldate  TIMESTAMP(0) ;
   vapprovaltwodate  TIMESTAMP(0) ;
   vappvalue VARCHAR (10);
   vusertype VARCHAR(10);

BEGIN

	 SELECT STATUS,LEVEL1_APPROVED_ON,LEVEL2_APPROVED_ON,USER_TYPE  INTO vstatus,vapprovaldate,vapprovaltwodate,vusertype   FROM USERS WHERE USER_ID= NEW.user_id;

	 SELECT DEFAULT_VALUE  INTO vappvalue FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE='USRLEVELAPPROVAL';

   BEGIN
  IF (TG_OP = 'INSERT') THEN
    IF   vstatus='Y' THEN
 	INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
	  	VALUES(NEW.user_id, NEW.msisdn, NEW.sms_pin, vusertype, NEW.phone_profile, date_trunc('day',NEW.modified_on::TIMESTAMP), NEW.modified_on, NEW.modified_by, 'PIN');
 		END IF;
   END IF;

  IF (TG_OP = 'UPDATE') THEN


  	 IF NEW.SMS_PIN <> OLD.SMS_PIN AND vstatus='Y' THEN
   	  INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
	  	VALUES(NEW.user_id, NEW.msisdn, NEW.sms_pin, vusertype, NEW.phone_profile, date_trunc('day',NEW.modified_on::TIMESTAMP), NEW.modified_on, NEW.modified_by, 'PIN');

	END IF;


	 IF  vstatus='Y' AND NEW.PIN_MODIFIED_ON = vapprovaldate AND vappvalue ='1' THEN
	   	  INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
		  	VALUES(NEW.user_id, NEW.msisdn, NEW.sms_pin, vusertype, NEW.phone_profile, date_trunc('day',NEW.modified_on::TIMESTAMP), NEW.modified_on, NEW.modified_by, 'PIN');
	END IF;

	IF  vstatus='Y' AND NEW.PIN_MODIFIED_ON = vapprovaltwodate AND vappvalue ='2' THEN
	   	  INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid,pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
		  	VALUES(NEW.user_id, NEW.msisdn, NEW.sms_pin,vusertype, NEW.phone_profile, date_trunc('day',NEW.modified_on::TIMESTAMP), NEW.modified_on, NEW.modified_by, 'PIN');

	END IF;


END IF;
END;
RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_user_phones_pin_history() OWNER TO pgdb;

--
-- Name: trig_users_password_history(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION trig_users_password_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ 
DECLARE
   vappvalue VARCHAR (10);
   vpin VARCHAR (50);
   vphoneprofile VARCHAR (10);
BEGIN

     SELECT DEFAULT_VALUE  INTO vappvalue FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE='USRLEVELAPPROVAL';


BEGIN
 IF NEW.login_id IS NOT NULL THEN --condition for web,gateway etc user
IF (TG_OP = 'INSERT') THEN
   IF  (NEW.USER_TYPE='OPERATOR' OR NEW.USER_TYPE='STAFF' OR vappvalue='0') THEN
     INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
        VALUES(NEW.user_id, NEW.login_id, NEW.PASSWORD, NEW.user_type, NEW.category_code, date_trunc('day',NEW.modified_on::TIMESTAMP) , NEW.modified_on, NEW.modified_by, 'PWD');
   END IF;
ELSIF (TG_OP = 'UPDATE') THEN

     IF NEW.MSISDN <> OLD.MSISDN AND NEW.STATUS ='Y' AND NEW.USER_TYPE='CHANNEL' THEN

        SELECT sms_pin,PHONE_PROFILE  INTO vpin,vphoneprofile FROM USER_PHONES WHERE  user_id=OLD.user_id AND PRIMARY_NUMBER='Y';

         INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
          VALUES(NEW.user_id,OLD.MSISDN,vpin, 'CHANNEL', vphoneprofile, date_trunc('day',NEW.modified_on::TIMESTAMP), NEW.modified_on, NEW.modified_by, 'MSN');

    END IF;

    IF (NEW.USER_TYPE='OPERATOR' OR NEW.USER_TYPE='STAFF' OR vappvalue='0') AND NEW.PASSWORD <> OLD.PASSWORD THEN
      INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
           VALUES(NEW.user_id, NEW.login_id, NEW.PASSWORD, NEW.user_type, NEW.category_code, date_trunc('day',NEW.modified_on::TIMESTAMP), NEW.modified_on, NEW.modified_by, 'PWD');

    ELSIF NEW.USER_TYPE='CHANNEL' AND vappvalue <> '0' THEN  --change password or modify password case

        IF NEW.PASSWORD <> OLD.PASSWORD AND NEW.LEVEL1_APPROVED_BY IS NOT NULL AND (NEW.STATUS ='Y' or NEW.STATUS ='PA') THEN
          INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
              VALUES(NEW.user_id, NEW.login_id, NEW.PASSWORD, NEW.user_type, NEW.category_code, date_trunc('day',NEW.modified_on::TIMESTAMP), NEW.modified_on, NEW.modified_by, 'PWD');

        ELSE IF NEW.PSWD_MODIFIED_ON IS NULL AND NEW.STATUS ='Y'  THEN  --first time user creation without change password
             INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
              VALUES(NEW.user_id, NEW.login_id, NEW.PASSWORD, NEW.user_type, NEW.category_code, date_trunc('day',NEW.modified_on::TIMESTAMP), NEW.modified_on, NEW.modified_by, 'PWD');
        END IF;
        END IF;



    END IF;
END IF;
END IF;
    END;
    RETURN NULL;
END;
$$;


ALTER FUNCTION pretupsdatabase.trig_users_password_history() OWNER TO pgdb;

--
-- Name: update_accpnt_dly_c2s_lms_smry(character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION update_accpnt_dly_c2s_lms_smry(aiv_date character varying, OUT rtn_message character varying, OUT rtn_messageforlog character varying, OUT rtn_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE 
p_trans_date            DAILY_C2S_LMS_SUMMARY.trans_date%type;
p_user_id                DAILY_C2S_LMS_SUMMARY.user_id%type;
p_product_code            DAILY_C2S_LMS_SUMMARY.product_code%type;
p_lms_profile            DAILY_C2S_LMS_SUMMARY.lms_profile%type;
p_accumulated_points    DAILY_C2S_LMS_SUMMARY.accumulated_points%type;
p_count INT;
--sqlexception EXCEPTION;-- Handles SQL or other Exception while checking records Exist
  DECLARE update_cur CURSOR FOR
        SELECT USER_ID_OR_MSISDN, PRODUCT_CODE, PROFILE_ID, ACCUMULATED_POINTS,POINTS_DATE from  BONUS where PROFILE_TYPE='LMS' and POINTS_DATE=to_date(aiv_Date,'dd/mm/yy');
    BEGIN
    
      p_count:=0;
      FOR user_records IN update_cur
             LOOP
                     p_user_id:=user_records.USER_ID_OR_MSISDN;
                    p_product_code:=user_records.product_code;
                    p_lms_profile:=user_records.PROFILE_ID;
                    p_accumulated_points:=user_records.accumulated_points;
                 BEGIN
			
                    p_count:=p_count+1;
                    --SAVEPOINT my_savepoint;
                    UPDATE DAILY_C2S_LMS_SUMMARY SET  accumulated_points=p_accumulated_points
                    WHERE user_id=p_user_id
                    AND product_code=p_product_code
                    --AND trans_date=to_date(aiv_Date,'dd/mm/yy')
                    AND lms_profile=p_lms_profile;
                    
                    RAISE NOTICE '%',' output '||p_user_id ||p_product_code || to_date(aiv_Date,'dd/mm/yy') ||p_lms_profile;

                    IF NOT FOUND 
			THEN
			RAISE NOTICE '%', 'Exception in update_accpnt_dly_c2s_lms_smry, User:' || p_user_id || SQLERRM;
			rtn_messageforlog:='Exception in update_accpnt_dly_c2s_lms_smry, User:' || p_user_id ;
			rtn_sqlerrmsgforlog:=SQLERRM;
				rtn_message :='FAILED';
			RAISE EXCEPTION 'user %  detail not found',p_user_id;
						
			END IF;
                    EXCEPTION
                        WHEN OTHERS THEN
				--ROLLBACK TO my_savepoint;
                                  RAISE NOTICE '%', 'Exception in update_acc_pnt_daily_c2s_lms_summary Update SQL, User:' || p_user_id ||' DATE:'||p_trans_date||' Profile:'||p_lms_profile|| SQLERRM;
                                  rtn_messageforlog := 'Exception in update_acc_pnt_daily_c2s_lms_summary Update SQL, User:' || p_user_id||' DATE:'||p_trans_date||' Profile:'||p_lms_profile;
                                  rtn_sqlerrmsgforlog := SQLERRM;
                                  --RAISE sqlexception;
                                  RAISE EXCEPTION 'user % Exception in update_acc_pnt_daily_c2s_lms_summary Update SQL',p_user_id;
			END;
			RAISE NOTICE '%',' i am here '||p_user_id;
		END LOOP;
		RAISE NOTICE '%',' i am here 2'||p_user_id;
                rtn_message:='SUCCESS';
                rtn_messageForLog :='PreTUPS update_acc_pnt_daily_c2s_lms_summary successfully executed, Number of updates:'||p_count;
                rtn_sqlerrMsgForLog :=' ';
                
        EXCEPTION
         WHEN OTHERS THEN
         RAISE NOTICE '%',' i am here3 '||p_user_id;
             -- ROLLBACK TO my_savepoint;
              --ROLLBACK to savepoint my_savepoint;
               RAISE NOTICE '%',' i am here4 '||p_user_id;
               RAISE NOTICE '%','OTHERS ERROR in update_acc_pnt_daily_c2s_lms_summary procedure:='||SQLERRM;
              rtn_message :='FAILED';
              rtn_sqlerrmsgforlog := SQLERRM;
              rtn_messageforlog := 'Exception in update_acc_pnt_daily_c2s_lms_summary Update SQL';
              RAISE EXCEPTION ' % Exception in Update SQL',SQLERRM;
    END;
$$;


ALTER FUNCTION pretupsdatabase.update_accpnt_dly_c2s_lms_smry(aiv_date character varying, OUT rtn_message character varying, OUT rtn_messageforlog character varying, OUT rtn_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: update_created_date(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION update_created_date() RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE 

V_SERIAL_NO VOMS_VOUCHERS.SERIAL_NO%TYPE;
V_CREATED_DATE VOMS_VOUCHERS.CREATED_DATE%TYPE;
V_MODIFY_COUNT  DECIMAL (20);

DECLARE  VOUCHER_LIST_CUR CURSOR FOR SELECT  SERIAL_NO,  date_trunc('day',CREATED_ON::TIMESTAMP) FROM VOMS_VOUCHERS WHERE CREATED_DATE IS NULL  ;

BEGIN

	 	 OPEN VOUCHER_LIST_CUR;
		 LOOP
		 RAISE NOTICE '%','Inside Loop';
		 FETCH VOUCHER_LIST_CUR INTO V_SERIAL_NO, V_CREATED_DATE;
		 IF NOT FOUND THEN EXIT;
		 END IF;
		 RAISE NOTICE '%','Update Query';
		 UPDATE VOMS_VOUCHERS  SET CREATED_DATE =  V_CREATED_DATE  WHERE SERIAL_NO = V_SERIAL_NO;
		 --V_MODIFY_COUNT :=     V_MODIFY_COUNT  + SQL%ROWCOUNT;
		 --IF  MOD( V_MODIFY_COUNT  ,  1000   ) = 0 THEN
		 /* COMMIT; */
		 --RAISE NOTICE '%','Committed On ' || V_MODIFY_COUNT;
		-- END IF;

		 END LOOP;
		  RAISE NOTICE '%','Outside Loop';
		 CLOSE VOUCHER_LIST_CUR;
		 EXCEPTION
		 WHEN OTHERS THEN
		 	  ROLLBACK;
				RAISE NOTICE '%','OTHERS ERROR in update_created_date procedure:='||SQLERRM;
				RAISE EXCEPTION 'user % RROR in update_created_date procedure',user_id;
		/* COMMIT; */
END;
$$;


ALTER FUNCTION pretupsdatabase.update_created_date() OWNER TO pgdb;

--
-- Name: update_opening_closing_balance(timestamp without time zone, timestamp without time zone); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION update_opening_closing_balance(p_fromdate timestamp without time zone, p_todate timestamp without time zone, OUT v_messageforlog character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE 
    gv_userid user_daily_balances.user_id%TYPE;
    gv_balance user_daily_balances.balance%TYPE;
    gv_networkcode user_daily_balances.network_code%TYPE;
    gv_networkcodefor user_daily_balances.network_code_for%TYPE;
    gv_productcode user_daily_balances.product_code%TYPE;
   -- p_fromdate user_daily_balances.balance_date%TYPE;
   -- p_todate user_daily_balances.balance_date%TYPE;
    p_date user_daily_balances.balance_date%TYPE;
    gd_transaction_date user_daily_balances.balance_date%TYPE;
    gv_pre_balance user_daily_balances.balance%TYPE;
    v_modify_count    DECIMAL (20);
    V_SQLERRMSGFORLOG varchar(200);
    USER_BAL_CUR record;

USER_BALANCE_CUR CURSOR (p_fromdate user_daily_balances.balance_date%TYPE,p_todate user_daily_balances.balance_date%TYPE)  FOR
        SELECT user_id, BALANCE, NETWORK_CODE, NETWORK_CODE_FOR, PRODUCT_CODE,BALANCE_DATE FROM USER_DAILY_BALANCES WHERE  date_trunc('day',BALANCE_DATE::TIMESTAMP) >= date_trunc('day',p_fromdate::TIMESTAMP) and date_trunc('day',BALANCE_DATE::TIMESTAMP) <= date_trunc('day',p_todate::TIMESTAMP) order by BALANCE_DATE;
	
BEGIN

    gv_userid := '';
    gv_balance := 0;
    gv_networkcode := '';
    gv_networkcodefor := '';
    gv_productcode := '';
    v_modify_count := 0;
    
   
    FOR USER_BAL_CUR IN USER_BALANCE_CUR(p_fromdate,p_todate)
     LOOP
        gv_userid := USER_BAL_CUR.user_id;
        gv_balance := USER_BAL_CUR.balance;
        gv_networkcode := USER_BAL_CUR.network_code;
        gv_networkcodefor := USER_BAL_CUR.network_code_for;
        gv_productcode := USER_BAL_CUR.product_code;
        p_date := USER_BAL_CUR.BALANCE_DATE;
        gd_transaction_date := USER_BAL_CUR.BALANCE_DATE-INTERVAL '1 day';
         
        
        gv_pre_balance := 0;
        BEGIN
        
            SELECT balance INTO gv_pre_balance
            FROM USER_DAILY_BALANCES
            WHERE user_id = gv_userid
            AND network_code = gv_networkcode
            AND network_code_for = gv_networkcodefor
            AND product_code = gv_productcode
            AND date_trunc('day',balance_date::TIMESTAMP) >= date_trunc('day',gd_transaction_date::TIMESTAMP);

            IF NOT FOUND 
	    THEN
              v_messageforlog :='SQL Exception in , User '|| gv_userid || ' Date:' || gd_transaction_date;
              
              RAISE NOTICE '%; SQLSTATE: %', SQLERRM, SQLSTATE;
		RAISE EXCEPTION 'user % daily balance detail not found',user_id;
               END IF;
		EXCEPTION
               WHEN NO_DATA_FOUND
               THEN                       
              
              RAISE NOTICE '%', 'No Record found for user_id,date,' ||gv_userid||gd_transaction_date || SQLERRM;
              gv_pre_balance := 0;
              WHEN OTHERS
               THEN
              v_messageforlog := 'SQL Exception in , User ' || gv_userid || ' Date:' || gd_transaction_date;
              
              RAISE NOTICE '%; SQLSTATE: %', SQLERRM, SQLSTATE;
              RAISE EXCEPTION 'user % SQL Exception ',user_id;
            END;
	BEGIN
	RAISE NOTICE '%', ' UPDATE  is::' || 'gv_pre_balance:' || gv_pre_balance || 'gv_balance' || gv_balance || 'p_date' || p_date || 'gv_userid' || gv_userid || 'gv_networkcode' || gv_networkcode || 'gv_productcode' || gv_productcode ;
         UPDATE DAILY_CHNL_TRANS_MAIN  SET OPENING_BALANCE = gv_pre_balance,  CLOSING_BALANCE= gv_balance  WHERE TRANS_DATE = p_date and USER_ID=gv_userid and
         NETWORK_CODE=gv_networkcode and NETWORK_CODE_FOR=gv_networkcodefor and PRODUCT_CODE=gv_productcode;
	 v_messageforlog := 'Successfully executed the update sql';
	 IF NOT FOUND 
	    THEN
              v_messageforlog :='Error in update SQL';              
              RAISE NOTICE '%; SQLSTATE: %', SQLERRM, SQLSTATE;
               END IF;
	 EXCEPTION
		WHEN OTHERS
		THEN
		 RAISE NOTICE '%', 'Exception in Update, User:' || gv_userid ;
		v_messageforlog := 'Exception in Update, User:' || gv_userid ;										 
		RAISE EXCEPTION 'user % balaces update sql fail',gv_userid;
	END;-- End of update SQL
	

    END LOOP;
   v_messageforlog := 'Successfully executed the update sql' ;
       IF NOT FOUND 
	    THEN
              v_messageforlog :='SQL Exception here';              
              RAISE NOTICE '%; SQLSTATE: %', SQLERRM, SQLSTATE;
               END IF;
    EXCEPTION
	WHEN OTHERS
		THEN
		RAISE NOTICE '%; SQLSTATE: %', SQLERRM, SQLSTATE;
		RAISE NOTICE '%', 'Exception in UPDATE DAILY_CHNL_TRANS_MAIN SQL' ;
		v_messageforlog := 'Exception in UPDATE DAILY_CHNL_TRANS_MAIN SQL';							 
		RAISE EXCEPTION 'UPDATE DAILY_CHNL_TRANS_MAIN SQL  fail';
	
END;
$$;


ALTER FUNCTION pretupsdatabase.update_opening_closing_balance(p_fromdate timestamp without time zone, p_todate timestamp without time zone, OUT v_messageforlog character varying) OWNER TO pgdb;

--
-- Name: update_voucher_enable(character varying, character varying, character varying, character varying, character varying, date, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION update_voucher_enable(v_vouchstat character varying, v_batchreconcilestat character varying, v_batchno character varying, v_batchtype character varying, v_modifiedby character varying, v_modifiedtime date, v_serialstart character varying, OUT v_returnlogmessage character varying, OUT v_returnmessage character varying, OUT v_message character varying) RETURNS SETOF record
    LANGUAGE plpgsql
    AS $$
BEGIN

/* If previous voucher status is of Reconcile then update */
	RAISE NOTICE '%','v_vouchStat & v_batchReconcileStat  ='||v_vouchStat||' & '||v_batchReconcileStat;
            IF(v_vouchStat=v_batchReconcileStat) THEN
		RAISE NOTICE '%',' v_vouchStat & v_batchReconcileStat same. v_serialStart = '|| v_serialStart;
            /*************************
            Code modified by kamini
            UPDATE VOMS_VOUCHERS set RECHARGE_SOURCE=null, CONSUMED_BY=null, CONSUMED_ON=null,
            TRANSACTION_ID=null, RECHARGE_PARTNER_ID=null, REQUEST_SOURCE =null,
            REQUEST_PARTNER_ID=null, TALK_TIME=null, VALIDUPTO=null, GRACE_PERIOD=null,
            TAX_RATE=null, TAX_AMOUNT=null, PARTNER_PRODUCT_ID=null, USER_NETWORK_CODE=null,
            ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;
            ************************/
		BEGIN
            UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=v_batchNo,STATUS=v_batchType,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;
	 /*
            IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND */

              EXCEPTION
	      WHEN OTHERS THEN
	       RAISE NOTICE '%','SQL EXCEPTION while update voucher table  ='||SQLERRM;
	       RAISE EXCEPTION 'SQL EXCEPTION while update voucher table' USING ERRCODE = 'SQLEX';

	       END;

            /* If previous voucher status other than Reconcile then update */
            ELSE
		RAISE NOTICE '%',' v_vouchStat & v_batchReconcileStat not match. v_serialStart = '|| v_serialStart;
            /*************************
            Code modified by kamini
            UPDATE VOMS_VOUCHERS set ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart;

            ********************/
	  BEGIN
            UPDATE VOMS_VOUCHERS SET ENABLE_BATCH_NO=v_batchNo,STATUS=v_batchType,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;

            /*IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND */


             EXCEPTION
	      WHEN OTHERS THEN
	       RAISE NOTICE '%','SQL EXCEPTION while update voucher table  ='||SQLERRM;
	       RAISE EXCEPTION 'SQL EXCEPTION while update voucher table' USING ERRCODE = 'SQLEX';

	       END;

           END IF;  -- end of if(v_vouchStat=p_batchReconcileStat)
EXCEPTION
--WHEN SQLException THEN
 when sqlstate 'SQLEX'  then
     v_returnMessage:='FAILED';
      v_message:='Not able to update voucher table'||v_serialStart;
      v_returnLogMessage:='Not able to update voucher table'||v_serialStart;
       RAISE NOTICE 'Not able to update voucher in vouchers table %',v_serialStart;
        --RAISE EXITEXCEPTION;
        RAISE EXCEPTION 'Not able to update voucher table' USING ERRCODE = 'EXITE';
    

WHEN OTHERS THEN
     v_returnMessage:='FAILED';
        RAISE NOTICE 'Exception while updating records %',v_serialStart;
      v_returnLogMessage:='Exception while updating voucher table'||v_serialStart;
      --RAISE EXITEXCEPTION;
       RAISE EXCEPTION ' EXCEPTION while update voucher table' USING ERRCODE = 'EXITE';
END;
$$;


ALTER FUNCTION pretupsdatabase.update_voucher_enable(v_vouchstat character varying, v_batchreconcilestat character varying, v_batchno character varying, v_batchtype character varying, v_modifiedby character varying, v_modifiedtime date, v_serialstart character varying, OUT v_returnlogmessage character varying, OUT v_returnmessage character varying, OUT v_message character varying) OWNER TO pgdb;

--
-- Name: update_voucher_enable_other(character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION update_voucher_enable_other(v_vouchstat character varying, v_batchreconcilestat character varying, v_vouccurrstat character varying, v_serialstart character varying, OUT v_returnlogmessage character varying, OUT v_returnmessage character varying, OUT v_message character varying) RETURNS SETOF record
    LANGUAGE plpgsql
    AS $$
BEGIN

            /* If previous voucher status and current status both is in Reconcile state then update current status and status both*/
            IF((v_vouchStat=v_batchReconcileStat) AND (v_voucCurrStat=v_batchReconcileStat)) THEN

            /*************************
            Code modified by kamini

            UPDATE VOMS_VOUCHERS set RECHARGE_SOURCE=null, CONSUMED_BY=null, CONSUMED_ON=null,
            TRANSACTION_ID=null, RECHARGE_PARTNER_ID=null, REQUEST_SOURCE =null,
            REQUEST_PARTNER_ID=null, TALK_TIME=null, VALIDUPTO=null, GRACE_PERIOD=null,
            TAX_RATE=null, TAX_AMOUNT=null, PARTNER_PRODUCT_ID=null, USER_NETWORK_CODE=null,
            ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;
            ************************/
	    BEGIN
            UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=v_batchNo,STATUS=v_batchType,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;

           /* IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND
	   */
            EXCEPTION
              WHEN OTHERS THEN
               RAISE NOTICE '%','SQL EXCEPTION while update voucher table  ='||SQLERRM;
	       RAISE EXCEPTION 'SQL EXCEPTION while update voucher table' USING ERRCODE = 'SQLEX';
	    END;
            

            /* If previous voucher status is consumed and current status is in Reconcile state then update only current status*/
            ELSIF ((v_vouchStat=v_batchConStat) AND (v_voucCurrStat=v_batchReconcileStat)) THEN
            BEGIN
            UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;

            /*IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND
            */
            
             EXCEPTION
              WHEN OTHERS THEN
               RAISE NOTICE '%','SQL EXCEPTION while update voucher table  ='||SQLERRM;
	       RAISE EXCEPTION 'SQL EXCEPTION while update voucher table' USING ERRCODE = 'SQLEX';
	    END;

            /*************************
            Code modified by kamini
            UPDATE VOMS_VOUCHERS set ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart;

            ********************/

            ELSE --Added By Gurjeet on 11/10/2004 because this was missing
            BEGIN
            UPDATE VOMS_VOUCHERS SET ENABLE_BATCH_NO=v_batchNo,STATUS=v_batchType,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart;
	/*
            IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND
            */

             EXCEPTION
              WHEN OTHERS THEN
               RAISE NOTICE '%','SQL EXCEPTION while update voucher table  ='||SQLERRM;
	       RAISE EXCEPTION 'SQL EXCEPTION while update voucher table' USING ERRCODE = 'SQLEX';
	    END;

           END IF;  -- end of if(v_vouchStat=p_batchReconcileStat)
EXCEPTION
 when sqlstate 'SQLEX'  then
--WHEN SQLException THEN
     v_returnMessage:='FAILED';
      v_message:='Not able to update voucher table'||v_serialStart;
      v_returnLogMessage:='Not able to update voucher table'||v_serialStart;
     -- DBMS_OUTPUT.PUT_LINE('Not able to update voucher in vouchers table'||v_serialStart);
    --  RAISE EXITEXCEPTION;
       RAISE NOTICE '%','Not able to update voucher in vouchers table'||SQLERRM;
        RAISE EXCEPTION 'Not able to update voucher in vouchers table' USING ERRCODE = 'SQLEX';

WHEN OTHERS THEN
      v_returnMessage:='FAILED';
    --  DBMS_OUTPUT.PUT_LINE('Exception while updating records '||v_serialStart);
      v_returnLogMessage:='Exception while updating voucher table'||v_serialStart;
      --RAISE EXITEXCEPTION;
        RAISE NOTICE '%','Exception while updating records'||SQLERRM;
        RAISE EXCEPTION 'Exception while updating records' USING ERRCODE = 'EXITE';
END;
$$;


ALTER FUNCTION pretupsdatabase.update_voucher_enable_other(v_vouchstat character varying, v_batchreconcilestat character varying, v_vouccurrstat character varying, v_serialstart character varying, OUT v_returnlogmessage character varying, OUT v_returnmessage character varying, OUT v_message character varying) OWNER TO pgdb;

--
-- Name: update_voucher_enable_other(character varying, character varying, character varying, date, character varying, character varying, character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION update_voucher_enable_other(v_batchno character varying, v_batchtype character varying, v_modifiedby character varying, v_modifiedtime date, v_vouchstat character varying, v_batchreconcilestat character varying, v_vouccurrstat character varying, v_serialstart character varying, OUT v_returnlogmessage character varying, OUT v_returnmessage character varying, OUT v_message character varying) RETURNS SETOF record
    LANGUAGE plpgsql
    AS $$
BEGIN
    RAISE NOTICE 'v_vouchStat= % v_batchReconcileStat= % v_voucCurrStat % ',v_vouchStat,v_batchReconcileStat, v_voucCurrStat ;
            /* If previous voucher status and current status both is in Reconcile state then update current status and status both*/
            IF((v_vouchStat=v_batchReconcileStat) AND (v_voucCurrStat=v_batchReconcileStat)) THEN
		RAISE NOTICE 'inside if ' ;
            /*************************
            Code modified by kamini

            UPDATE VOMS_VOUCHERS set RECHARGE_SOURCE=null, CONSUMED_BY=null, CONSUMED_ON=null,
            TRANSACTION_ID=null, RECHARGE_PARTNER_ID=null, REQUEST_SOURCE =null,
            REQUEST_PARTNER_ID=null, TALK_TIME=null, VALIDUPTO=null, GRACE_PERIOD=null,
            TAX_RATE=null, TAX_AMOUNT=null, PARTNER_PRODUCT_ID=null, USER_NETWORK_CODE=null,
            ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;
            ************************/
	    BEGIN
            UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=v_batchNo,STATUS=v_batchType,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;

           /* IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND
	   */
            EXCEPTION
              WHEN OTHERS THEN
               RAISE NOTICE '%','SQL EXCEPTION while update voucher table  = '||SQLERRM;
	       RAISE EXCEPTION 'SQL EXCEPTION while update voucher table' USING ERRCODE = 'SQLEX';
	    END;
            

            /* If previous voucher status is consumed and current status is in Reconcile state then update only current status*/
            ELSIF ((v_vouchStat=v_batchConStat) AND (v_voucCurrStat=v_batchReconcileStat)) THEN
            BEGIN
            UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;

            /*IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND
            */
            
             EXCEPTION
              WHEN OTHERS THEN
               RAISE NOTICE '%','SQL EXCEPTION while update voucher table  ='||SQLERRM;
	       RAISE EXCEPTION 'SQL EXCEPTION while update voucher table' USING ERRCODE = 'SQLEX';
	    END;

            /*************************
            Code modified by kamini
            UPDATE VOMS_VOUCHERS set ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart;

            ********************/

            ELSE --Added By Gurjeet on 11/10/2004 because this was missing
            BEGIN
            UPDATE VOMS_VOUCHERS SET ENABLE_BATCH_NO=v_batchNo,STATUS=v_batchType,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart;
	/*
            IF SQL%NOTFOUND THEN
              DBMS_OUTPUT.PUT_LINE('SQL EXCEPTION while update voucher table  ='||SQLERRM);
            RAISE SQLException;
            END IF;  -- end of if SQL%NOTFOUND
            */

             EXCEPTION
              WHEN OTHERS THEN
               RAISE NOTICE '%','SQL EXCEPTION while update voucher table  ='||SQLERRM;
	       RAISE EXCEPTION 'SQL EXCEPTION while update voucher table' USING ERRCODE = 'SQLEX';
	    END;

           END IF;  -- end of if(v_vouchStat=p_batchReconcileStat)
EXCEPTION
 when sqlstate 'SQLEX'  then
--WHEN SQLException THEN
     v_returnMessage:='FAILED';
      v_message:='Not able to update voucher table'||v_serialStart;
      v_returnLogMessage:='Not able to update voucher table'||v_serialStart;
     -- DBMS_OUTPUT.PUT_LINE('Not able to update voucher in vouchers table'||v_serialStart);
    --  RAISE EXITEXCEPTION;
       RAISE NOTICE '%','Not able to update voucher in vouchers table'||SQLERRM;
        RAISE EXCEPTION 'Not able to update voucher in vouchers table' USING ERRCODE = 'EXITE';

WHEN OTHERS THEN
      v_returnMessage:='FAILED';
    --  DBMS_OUTPUT.PUT_LINE('Exception while updating records '||v_serialStart);
      v_returnLogMessage:='Exception while updating voucher table'||v_serialStart;
      --RAISE EXITEXCEPTION;
        RAISE NOTICE '%','Exception while updating records'||SQLERRM;
        RAISE EXCEPTION 'Exception while updating records' USING ERRCODE = 'EXITE';
END;
$$;


ALTER FUNCTION pretupsdatabase.update_voucher_enable_other(v_batchno character varying, v_batchtype character varying, v_modifiedby character varying, v_modifiedtime date, v_vouchstat character varying, v_batchreconcilestat character varying, v_vouccurrstat character varying, v_serialstart character varying, OUT v_returnlogmessage character varying, OUT v_returnmessage character varying, OUT v_message character varying) OWNER TO pgdb;

--
-- Name: updt_crtd_clmns_in_tbl(character varying, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION updt_crtd_clmns_in_tbl(p_tablename character varying, p_partitionname character varying DEFAULT 'XX'::character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$ declare 
cmt_frqncy varchar(5);
update_str varchar(400);
execute_stmt varchar(800);
column_names cursor  is select column_names from migrate_msisdn where table_name=p_tablename;

begin

cmt_frqncy:=10000;
update_str:='';
--open column_names;
for clmun in column_names
	loop
		update_str:=update_str||'old_'||clmun.column_names||'='||clmun.column_names||', ';
	end loop;
update_str:=update_str||'msisdn_modified=:1';

RAISE NOTICE '%','***'||'p_partitionname='||p_partitionname||'***';
if p_partitionname='XX' then
	execute_stmt:='update '|| p_tablename || ' set ' ||update_str||
	' where msisdn_modified is null and rownum<=' || cmt_frqncy;
else
	execute_stmt:='update '|| p_tablename || ' partition (' || p_partitionname || ')'||' set '||
	update_str||' where msisdn_modified is null and rownum<=' || cmt_frqncy;
end if;

RAISE NOTICE '%','executing stmt: '||execute_stmt;

loop
	execute immediate execute_stmt using 'N';
	exit when sql%rowcount=0;
	/* commit; */
end loop;

/* commit; */

end;
$$;


ALTER FUNCTION pretupsdatabase.updt_crtd_clmns_in_tbl(p_tablename character varying, p_partitionname character varying) OWNER TO pgdb;

--
-- Name: user__new(character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION user__new(userid character varying) RETURNS SETOF character varying
    LANGUAGE plpgsql
    AS $$

 begin
	return query select  user_name as gv_user_name from users where status='Y' and user_id = userid;
	--return gv_user_name;
	 
 end;
$$;


ALTER FUNCTION pretupsdatabase.user__new(userid character varying) OWNER TO pgdb;

--
-- Name: user_balances_history_update_trigger(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION user_balances_history_update_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
DECLARE
sql TEXT;
table_name TEXT;
partition_name TEXT;
partition_key TEXT;
BEGIN

partition_name := TG_ARGV[0];
table_name := TG_ARGV[1];
partition_key := TG_ARGV[2];

 IF ( 'NEW.'||partition_key != 'OLD.'||partition_key ) THEN
	sql := 'DELETE FROM '||partition_name||'
	WHERE $1.'||partition_key||'='||partition_key||' AND OLD.user_id=user_id;';
	EXECUTE sql USING OLD;
        sql:='INSERT INTO '||table_name||' VALUES ($1.*);';
        EXECUTE sql USING NEW;
        RETURN NULL;
    ELSE
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$_$;


ALTER FUNCTION pretupsdatabase.user_balances_history_update_trigger() OWNER TO pgdb;

--
-- Name: user_daily_balances_update_trigger(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION user_daily_balances_update_trigger() RETURNS trigger
    LANGUAGE plpgsql
    AS $_$
DECLARE
sql TEXT;
table_name TEXT;
partition_name TEXT;
partition_key TEXT;
BEGIN

partition_name := TG_ARGV[0];
table_name := TG_ARGV[1];
partition_key := TG_ARGV[2];

 IF ( 'NEW.'||partition_key != 'OLD.'||partition_key ) THEN
	sql := 'DELETE FROM '||partition_name||'
	WHERE $1.'||partition_key||'='||partition_key||' AND OLD.user_id=user_id;';
	EXECUTE sql USING OLD;
        sql:='INSERT INTO '||table_name||' VALUES ($1.*);';
        EXECUTE sql USING NEW;
        RETURN NULL;
    ELSE
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$_$;


ALTER FUNCTION pretupsdatabase.user_daily_balances_update_trigger() OWNER TO pgdb;

--
-- Name: user_daily_closing_balance(); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION user_daily_closing_balance(OUT rtn_message character varying, OUT rtn_messageforlog character varying, OUT rtn_sqlerrmsgforlog character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
 
DECLARE
p_user_id  USER_BALANCES.user_id%TYPE;
p_product_code USER_BALANCES.product_code%TYPE;
p_network_code USER_BALANCES.network_code%TYPE;
p_network_code_for USER_BALANCES.network_code_for%TYPE;

q_user_id USER_DAILY_BALANCES.user_id%TYPE ;
q_network_code USER_DAILY_BALANCES.network_code%TYPE;
q_network_code_for USER_DAILY_BALANCES.network_code_for%TYPE;
q_product_code USER_DAILY_BALANCES.product_code%TYPE;
q_balance USER_DAILY_BALANCES.balance%TYPE;
q_prev_balance USER_DAILY_BALANCES.prev_balance%TYPE;
q_last_transfer_type USER_DAILY_BALANCES.last_transfer_type%TYPE;
q_last_transfer_no USER_DAILY_BALANCES.last_transfer_no%TYPE;
q_last_transfer_on USER_DAILY_BALANCES.last_transfer_on%TYPE;


q_daily_balance_updated_on TIMESTAMP(0);
q_created_on TIMESTAMP(0);
dayDifference INT = 0;
startCount SMALLINT;
dateCounter TIMESTAMP(0);

--sqlexception EXCEPTION;-- Handles SQL or other Exception while checking records Exist

 DECLARE user_list_cur CURSOR  FOR
    	SELECT ub.user_id,ub.product_code,ub.network_code,ub.network_code_for
		FROM USER_BALANCES ub, USERS u
		WHERE ub.USER_ID=u.USER_ID
		AND date_trunc('day',daily_balance_updated_on::TIMESTAMP) <>date_trunc('day',CURRENT_TIMESTAMP::TIMESTAMP)
		AND date_trunc('day',u.modified_on::TIMESTAMP) >= CASE WHEN (u.status='N') THEN (CURRENT_TIMESTAMP-INTERVAL '366 days') WHEN (u.status='C') THEN (CURRENT_TIMESTAMP-INTERVAL '366 days') ELSE date_trunc('day',u.modified_on::TIMESTAMP) END;		
		

BEGIN

	 		FOR user_records IN user_list_cur
			 LOOP
					p_user_id:=user_records.user_id;
					p_product_code:=user_records.product_code;
					p_network_code:=user_records.network_code;
					p_network_code_for:=user_records.network_code_for;
			 BEGIN
			 	   	SELECT  user_id into q_user_id
			 	   	FROM USER_BALANCES
					WHERE user_id=p_user_id
					AND network_code=p_network_code
					AND network_code_for=p_network_code_for
					and product_code=p_product_code
					FOR UPDATE;

					SELECT network_code INTO q_network_code
					FROM USER_BALANCES
					WHERE user_id=p_user_id
					AND network_code=p_network_code
					AND network_code_for=p_network_code_for
					and product_code=p_product_code
					FOR UPDATE;
					
					SELECT network_code_for INTO q_network_code_for
					 FROM USER_BALANCES
					WHERE user_id=p_user_id
					AND network_code=p_network_code
					AND network_code_for=p_network_code_for
					and product_code=p_product_code
					FOR UPDATE;
					
					SELECT product_code INTO q_product_code 
					FROM USER_BALANCES
					WHERE user_id=p_user_id
					AND network_code=p_network_code
					AND network_code_for=p_network_code_for
					and product_code=p_product_code
					FOR UPDATE;
					
					SELECT balance INTO q_balance
					FROM USER_BALANCES
					WHERE user_id=p_user_id
					AND network_code=p_network_code
					AND network_code_for=p_network_code_for
					and product_code=p_product_code
					FOR UPDATE;
					
					SELECT prev_balance INTO  q_prev_balance
					FROM USER_BALANCES
					WHERE user_id=p_user_id
					AND network_code=p_network_code
					AND network_code_for=p_network_code_for
					and product_code=p_product_code
					FOR UPDATE;
					
					SELECT last_transfer_type INTO q_last_transfer_type
					FROM USER_BALANCES
					WHERE user_id=p_user_id
					AND network_code=p_network_code
					AND network_code_for=p_network_code_for
					and product_code=p_product_code
					FOR UPDATE;
					
					SELECT last_transfer_no INTO q_last_transfer_no
					FROM USER_BALANCES
					WHERE user_id=p_user_id
					AND network_code=p_network_code
					AND network_code_for=p_network_code_for
					and product_code=p_product_code
					FOR UPDATE;
					
					SELECT last_transfer_on INTO q_last_transfer_on
					FROM USER_BALANCES
					WHERE user_id=p_user_id
					AND network_code=p_network_code
					AND network_code_for=p_network_code_for
					and product_code=p_product_code
					FOR UPDATE;
					
					SELECT date_trunc('day',daily_balance_updated_on::TIMESTAMP) q_daily_balance_updated_on INTO q_daily_balance_updated_on
					FROM USER_BALANCES
					WHERE user_id=p_user_id
					AND network_code=p_network_code
					AND network_code_for=p_network_code_for
					and product_code=p_product_code
					FOR UPDATE;
					
					IF NOT FOUND 
					THEN
					 	 RAISE NOTICE '%', 'Exception SQL%NOTFOUND in USER_DAILY_CLOSING_BALANCE Select SQL, User:' || p_user_id || SQLERRM;
						 rtn_messageforlog:='Exception SQL%NOTFOUND in USER_DAILY_CLOSING_BALANCE 2, User:' || p_user_id ;
						 rtn_sqlerrmsgforlog:=SQLERRM;
						  rtn_message :='FAILED';
						RAISE NOTICE '%; SQLSTATE: %', SQLERRM, SQLSTATE;
						RAISE EXCEPTION 'user % balance detail not found',user_id;
						
					END IF;

					EXCEPTION
					WHEN NO_DATA_FOUND
					THEN
					      RAISE NOTICE '%', 'Exception NO_DATA_FOUND in USER_DAILY_CLOSING_BALANCE Select SQL, User:' || p_user_id || SQLERRM;

					WHEN OTHERS
					THEN
					      RAISE NOTICE '%', 'OTHERS Exception in USER_DAILY_CLOSING_BALANCE 2, User:' || p_user_id || SQLERRM;
						rtn_messageforlog := 'OTHERS Exception in USER_DAILY_CLOSING_BALANCE 2, User:' || p_user_id ;
						rtn_sqlerrmsgforlog := SQLERRM;
						rtn_message :='FAILED';
						
						RAISE NOTICE '%; SQLSTATE: %', SQLERRM, SQLSTATE;
						RAISE EXCEPTION 'user % balance detail not found',user_id;

			 END;
			
			 BEGIN
				
		 	  	  q_created_on  :=CURRENT_TIMESTAMP;
		 	  	  
				  startCount := 1;
				  
				  dateCounter:= q_daily_balance_updated_on;
				  
				  dayDifference:= DATE_PART('day', date_trunc('day',q_created_on::TIMESTAMP) - q_daily_balance_updated_on);
				  RAISE NOTICE '%',' No Of dayDifference::'||dayDifference;


			    FOR xyz IN startCount .. dayDifference
			    LOOP


				     BEGIN


				 	  INSERT INTO USER_DAILY_BALANCES
					              (balance_date,user_id,network_code,network_code_for,
					  			  product_code,balance,prev_balance,last_transfer_type,
								  last_transfer_no,last_transfer_on,created_on
								  )
					  	    VALUES(dateCounter,q_user_id,q_network_code,
								   q_network_code_for,q_product_code,q_balance,q_prev_balance,
								   q_last_transfer_type,q_last_transfer_no,q_last_transfer_on,
								   q_created_on
								  );
						
					  EXCEPTION
			            WHEN OTHERS
			            THEN
			               RAISE NOTICE '%', 'Exception OTHERS in USER_DAILY_CLOSING_BALANCE Insert SQL, User:' || p_user_id || SQLERRM;
						   rtn_messageforlog := 'Exception OTHERS in USER_DAILY_CLOSING_BALANCE Insert SQL, User:' || p_user_id ;
						   rtn_sqlerrmsgforlog := SQLERRM;
						    rtn_message :='FAILED';
		                       
		                       RAISE NOTICE '%; SQLSTATE: %', SQLERRM, SQLSTATE;
		                       RAISE EXCEPTION 'user % daily balances insert sql fail',user_id;



				    END;-- End of insert SQL

				    BEGIN

						UPDATE USER_BALANCES SET
							   daily_balance_updated_on=q_created_on
					    WHERE user_id=p_user_id
						AND product_code=p_product_code
						AND network_code=p_network_code
						AND network_code_for=p_network_code_for;
						EXCEPTION
						   WHEN OTHERS
						   THEN
							  RAISE NOTICE '%', 'Exception in USER_DAILY_CLOSING_BALANCE Update SQL, User:' || p_user_id || SQLERRM;
							  rtn_messageforlog := 'Exception in USER_DAILY_CLOSING_BALANCE Update SQL, User:' || p_user_id ;
							rtn_sqlerrmsgforlog := SQLERRM;
							 rtn_message :='FAILED';
							 
							  RAISE EXCEPTION 'user % balaces update sql fail',user_id;

			 	   END;-- End of update SQL

					startCount:= startCount+1;
					dateCounter:= dateCounter+ interval '1 day';
				END LOOP;--End of daydiffrence loop
			RAISE NOTICE '%', 'I am here AGAIN::'|| p_user_id;

			--COMMIT;
			RAISE NOTICE '%', 'RECORDS COMMITED::'||p_user_id;

			EXCEPTION
			WHEN OTHERS
			THEN
			
			 RAISE NOTICE '%', 'Exception in Update SQL, User:' || p_user_id || SQLERRM;
			rtn_messageforlog := 'Exception in Update SQL, User:' || p_user_id ;
		        rtn_sqlerrmsgforlog := SQLERRM;
		         rtn_message :='FAILED';
			RAISE EXCEPTION 'user % Exception in Update SQL',user_id;
			
			
							 
		END;--End oF Outer begin

			--COMMIT; 


	 END LOOP;--End of outer for loop
 

			     rtn_message:='SUCCESS';
				 rtn_messageForLog :='PreTUPS USER_DAILY_CLOSING_BALANCE MIS successfully executed, Date Time:'||CURRENT_TIMESTAMP;
				 rtn_sqlerrMsgForLog :=' ';

				 --COMMIT;

		 EXCEPTION --Exception Handling of main procedure
			-- WHEN SQLSTATE '22P02' THEN
			--	  ROLLBACK;
			--	  RAISE NOTICE '%','sqlException Caught='||SQLERRM;
			--	  rtn_message :='FAILED';
		
			--	  RAISE NOTICE '%; SQLSTATE: %', SQLERRM, SQLSTATE;

		 WHEN OTHERS THEN
		 	  ROLLBACK;
				RAISE NOTICE '%','OTHERS ERROR in USER_DAILY_CLOSING_BALANCE procedure:='||SQLERRM;
				rtn_message :='FAILED';
				rtn_sqlerrmsgforlog := SQLERRM;
				RAISE EXCEPTION 'user % Exception in SQL',user_id;
			  



END;
  $$;


ALTER FUNCTION pretupsdatabase.user_daily_closing_balance(OUT rtn_message character varying, OUT rtn_messageforlog character varying, OUT rtn_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: userclosingbalance(character varying, date, date, integer, integer); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION userclosingbalance(p_userid character varying, p_startdate date, p_enddate date, p_startamt integer, p_endamt integer) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
declare 
p_userCloBalDateWise VARCHAR(4000) DEFAULT '' ; balDate DATE; balance integer ; productCode VARCHAR(10);
c_userCloBal CURSOR(p_userId VARCHAR,p_startDate DATE,p_endDate DATE,p_startAmt integer,p_endAmt integer) IS
	   SELECT  UDB.user_id user_id,UDB.balance_date balance_date,UDB.balance balance,UDB.PRODUCT_CODE
                        FROM    USER_DAILY_BALANCES UDB
                        WHERE UDB.user_id=p_userId
                        AND UDB.balance_date >=p_startDate
                        AND UDB.balance_date <=p_endDate
                        AND UDB.balance >=p_startAmt
                        AND UDB.balance <=p_endAmt ORDER BY balance_date ASC   ;
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
$$;


ALTER FUNCTION pretupsdatabase.userclosingbalance(p_userid character varying, p_startdate date, p_enddate date, p_startamt integer, p_endamt integer) OWNER TO pgdb;

--
-- Name: vsc_check_change_valid_proc(character varying, boolean, integer, timestamp without time zone, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, timestamp without time zone, integer, character varying, character varying, character varying, integer); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION vsc_check_change_valid_proc(v_serialstart character varying, v_vouchernotfoundflag boolean, v_processscreen integer, v_createdon timestamp without time zone, v_batchtype character varying, v_batchenablestat character varying, v_batchgenstat character varying, v_enableprocess character varying, v_batchstolenstat character varying, v_batchonholdstat character varying, v_preactivestat character varying, v_suspendstat character varying, v_changeprocess character varying, v_warehousestat character varying, v_batchprintstat character varying, v_batchreconcilestat character varying, v_modifiedtime timestamp without time zone, v_rcadminmaxdateallowed integer, v_reconcileprocess character varying, v_batchconstat character varying, v_batchdamagestat character varying, v_seqid integer, OUT v_damagestolenafterencount integer, OUT v_vouchstat character varying, OUT v_vouccurrstat character varying, OUT v_productid character varying, OUT v_lastrequestattemptno double precision, OUT v_lastattemptvalue numeric, OUT v_succfailflag character varying, OUT v_enablecount integer, OUT v_errorcount integer, OUT v_message character varying, OUT v_damagestolencount integer, OUT v_onholdcount integer, OUT v_vouchernotfoundcount integer, OUT v_returnmessage character varying, OUT v_returnlogmessage character varying) RETURNS record
    LANGUAGE plpgsql
    AS $_$
DECLARE
v_expDate voms_vouchers.EXPIRY_DATE%type;
v_generationBatchNo voms_vouchers.GENERATION_BATCH_NO%type;
v_prodNetworkCode voms_vouchers.PRODUCTION_NETWORK_CODE%type;
v_userNetworkCode voms_vouchers.USER_NETWORK_CODE%type;
v_PreviousStatus voms_vouchers.PREVIOUS_STATUS%type;
v_LastConsumedOn voms_vouchers.LAST_CONSUMED_ON%type;
v_wareHouseCount INT;
v_suspendCount INT;
v_modifieddate date;
v_lastModifieddate date;
v_daysDifflastconCurrDate INT;


QUERY TEXT := '';

QRY_SELECT_VOMS_VOUCHERS TEXT :=' SELECT STATUS,CURRENT_STATUS,EXPIRY_DATE,GENERATION_BATCH_NO, PRODUCTION_NETWORK_CODE
         ,PRODUCT_ID,PREVIOUS_STATUS,LAST_CONSUMED_ON,LAST_REQUEST_ATTEMPT_NO,LAST_ATTEMPT_VALUE  
        FROM VOMS_VOUCHERS WHERE SERIAL_NO=$1 ';
         
QRY_SEQ_ID TEXT :=' AND SEQUENCE_ID=$2 ';

QRY_FOR_UPDATE TEXT :=  ' FOR UPDATE  ';

BEGIN

v_enableCount  :=0;
v_errorCount:=0;
v_DamageStolenCount :=0;
v_onHoldCount :=0;
v_wareHouseCount:=0;
v_suspendCount:=0;
v_daysDifflastconCurrDate := 0;
v_voucherNotFoundCount:=0;
v_returnMessage:='';
v_userNetworkCode:='';
v_DamageStolenAfterEnCount  :=0;
v_LastRequestAttemptNo  :=0.0;
v_LastAttemptValue  :=0.0 ;
v_message :='';
v_returnLogMessage :='';

 RAISE NOTICE 'Function : VSC_CHECK_CHANGE_VALID_PROC started ' ;
 
         /* Get the voucher status abd then check whether that voucher
         is valid for status change or not */
	   IF(v_seqId = 0 ) THEN
		QUERY = QRY_SELECT_VOMS_VOUCHERS || QRY_FOR_UPDATE;
	   ELSE
		QUERY = QRY_SELECT_VOMS_VOUCHERS || QRY_SEQ_ID || QRY_FOR_UPDATE;
           END IF; 

		 RAISE NOTICE 'Function : VSC_CHECK_CHANGE_VALID_PROC started, QUERY =  % ', QUERY ;

		RAISE NOTICE 'Before Exceute query =  % ', v_serialStart;
		RAISE NOTICE 'Before Exceute query =  % ', v_seqId ;

		EXECUTE  QUERY USING v_serialStart,v_seqId into
		v_vouchStat,v_voucCurrStat,v_expDate,v_generationBatchNo,
		v_prodNetworkCode,v_productID,v_PreviousStatus,v_LastConsumedOn,v_LastRequestAttemptNo,v_LastAttemptValue ;
         
		RAISE NOTICE 'Function : VSC_CHECK_CHANGE_VALID_PROC started, Query Executed v_generationBatchNo %',v_generationBatchNo ;

		RAISE NOTICE '%,%,%,%,%',v_vouchStat,v_voucCurrStat,v_expDate,v_generationBatchNo,v_prodNetworkCode;

		RAISE NOTICE '%,%,%,%,%',
		v_productID,v_PreviousStatus,v_LastConsumedOn,v_LastRequestAttemptNo,v_LastAttemptValue ;

		/*initialization required in order to return value , value can not be null*/
		if(v_LastRequestAttemptNo is null) then
			v_LastRequestAttemptNo:=0;
		end if;
		if(v_LastAttemptValue is null) then 
			v_LastAttemptValue:=0;
		end if;
		
         /* Check whether that voucher has expired or not . If not then
        perform voucher valid for change status checking.*/
        BEGIN           -- Begin of batch type checking
         IF(v_expDate>=v_createdOn) THEN
             RAISE NOTICE 'Function : VSC_CHECK_CHANGE_VALID_PROC , if v_expDate greater than v_createdOn ' ;
              
         -- If user is coming from 1 screen ie Enable Screen
         IF(v_processScreen = 1) THEN
            RAISE NOTICE 'Function : VSC_CHECK_CHANGE_VALID_PROC ,  if  v_processScreen = 1  ' ;
         /*Condition for Enable voucher status , The status should be GE*/
         IF(v_batchType = v_batchEnableStat) THEN
           IF(v_vouchStat = v_batchGenStat) THEN
              v_succFailFlag:='SUCCESS';
              v_enableCount:=v_enableCount+1;
           ELSE
              v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              RAISE NOTICE 'logging : 5 %', v_errorCount ;
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_EnableProcess||' Screen';  -- Message to be written in VA
           END IF; -- of Enable type checking

           /*Condition for stolen voucher status, The status should be GE,Enable,
         Sold, On Hold status*/
           RAISE NOTICE 'v_processScreen = 1 , v_batchType % v_batchStolenStat %  ',v_batchType, v_batchStolenStat ;
         ELSIF(v_batchType = v_batchStolenStat) THEN
           IF(v_vouchStat = v_batchGenStat) THEN
			v_succFailFlag:='SUCCESS';
			v_DamageStolenCount:=v_DamageStolenCount+1;
           ELSE
			v_errorCount:=v_errorCount+1;
			v_succFailFlag:='FAILED';
			v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_EnableProcess||' Screen'; -- Message to be written in VA
           END IF; -- of stolen type checking

           /*Condition for damage voucher status, The status should be GE,Enable,
         Sold, On Hold status*/
            RAISE NOTICE ' v_processScreen = 1 , v_batchType % v_batchDamageStat %  ',v_batchType, v_batchDamageStat ;
         ELSIF(v_batchType = v_batchDamageStat) THEN
           IF(v_vouchStat = v_batchGenStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
		v_succFailFlag:='FAILED';
		v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_EnableProcess||' Screen';-- Message to be written in VA
           END IF; -- of damage type checking
           END IF; -- END OF batch type checking for 1

          -- If user is coming from 2 screen ie Change Voucher Screen
         ELSIF(v_processScreen = 2) THEN
          RAISE NOTICE 'Function : VSC_CHECK_CHANGE_VALID_PROC ,  if  v_processScreen = 2  ' ;
         /*Condition for Enable voucher status , The status should be GE,
         On hold, Reconcile*/

         IF(v_batchType = v_batchEnableStat) THEN
           IF(v_vouchStat = v_batchOnHoldStat) THEN
              v_succFailFlag:='SUCCESS';

           ELSIF(v_vouchStat = v_preActiveStat) THEN ---added for new preactive state---
              v_succFailFlag:='SUCCESS';
           ELSIF(v_vouchStat = v_suspendStat) THEN ---added for new suspend state---
              v_succFailFlag:='SUCCESS';
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen';  -- Message to be written in VA
           END IF; -- of Enable type checking


           /*Condition for On Hold voucher status, The status should be Enable,
         status*/
         RAISE NOTICE ' v_processScreen = 2 , v_batchType % v_batchOnHoldStat % v_vouchStat % ',v_batchType, v_batchOnHoldStat, v_vouchStat ;
         ELSIF(v_batchType = v_batchOnHoldStat) THEN
           IF(v_vouchStat = v_batchEnableStat) THEN
              v_succFailFlag:='SUCCESS';
              v_onHoldCount:=v_onHoldCount+1;
        RAISE NOTICE ' v_processScreen = 2 , v_batchType % v_suspendStat %  ',v_batchType, v_suspendStat ;
        ELSIF(v_vouchStat = v_suspendStat) THEN ---added for new suspend state---
              v_succFailFlag:='SUCCESS';
              v_onHoldCount:=v_onHoldCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen'; -- Message to be written in VA
           END IF; -- of On Hold type checking

           /*Condition for stolen voucher status, The status should be GE,
         Enable status*/
          RAISE NOTICE ' v_processScreen = 2 , v_batchType % v_batchStolenStat %  ',v_batchType, v_batchStolenStat ;
         ELSIF(v_batchType = v_batchStolenStat) THEN
          IF(v_vouchStat = v_batchEnableStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
           ELSIF(v_vouchStat = v_batchOnHoldStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
         ELSIF(v_vouchStat = v_suspendStat) THEN ---added for new suspend state---
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
           ELSE
                    v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen'; -- Message to be written in VA
           END IF; -- of stolen type checking

           /*Condition for damage voucher status, The status should be GE,Enable,
         status*/
          RAISE NOTICE ' v_processScreen = 2 , v_batchType % v_batchDamageStat %  ',v_batchType, v_batchDamageStat ;
         ELSIF(v_batchType = v_batchDamageStat) THEN
           IF(v_vouchStat = v_batchEnableStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
                v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
           ELSIF(v_vouchStat = v_batchOnHoldStat) THEN
              v_succFailFlag:='SUCCESS';
              v_DamageStolenCount:=v_DamageStolenCount+1;
              v_DamageStolenAfterEnCount:=v_DamageStolenAfterEnCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen';-- Message to be written in VA
           END IF; -- of damage type checking


           /*Condition for warehouse voucher status, The status should be PE
         status*/
	RAISE NOTICE ' v_processScreen = 2 , v_batchType % v_wareHouseStat %  ',v_batchType, v_wareHouseStat ;
           ELSIF(v_batchType = v_wareHouseStat) THEN ---added for new state warehouse--
           IF(v_vouchStat = v_batchPrintStat) THEN
              v_succFailFlag:='SUCCESS';
              v_wareHouseCount:=v_wareHouseCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen';-- Message to be written in VA
           END IF; -- of warehouse type checking

            /*Condition for warehouse voucher status, The status should be PE
         status*/
	RAISE NOTICE ' v_processScreen = 2 , v_batchType % v_suspendStat %  ',v_batchType, v_suspendStat ;
           ELSIF(v_batchType = v_suspendStat) THEN ---added for new state SUSPEND--
           IF(v_vouchStat = v_batchEnableStat) THEN
              v_succFailFlag:='SUCCESS';
              v_suspendCount:=v_suspendCount+1;
            ELSIF(v_vouchStat = v_wareHouseStat) THEN
            v_succFailFlag:='SUCCESS';
              v_suspendCount:=v_suspendCount+1;
            ELSIF(v_vouchStat = v_preActiveStat) THEN
            v_succFailFlag:='SUCCESS';
              v_suspendCount:=v_suspendCount+1;
            ELSIF(v_vouchStat = v_batchOnHoldStat) THEN
              v_succFailFlag:='SUCCESS';
              v_suspendCount:=v_suspendCount+1;
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ChangeProcess||' Screen';-- Message to be written in VA
           END IF; -- of SUSPEND type checking

          END IF; -- END OF batch type checking for 2

         -- Condition if user is coming from 3 Screen ie Change Reconcile Status
         ELSIF(v_processScreen = 3) THEN
		RAISE NOTICE 'Function : VSC_CHECK_CHANGE_VALID_PROC ,  if  v_processScreen = 3  ' ;
         /*Condition for Enable voucher status , The status should be Reconcile.If request is to enable the voucher
         two condition is chacked
         a)the current_status is 'RC'
         b) The days difference between last consumed on and current date should be greater less than or equal to RCAdmindaysdiffallowed.*/
         IF(v_batchType = v_batchEnableStat) THEN
           IF(v_vouchStat = v_batchReconcileStat) THEN ----Check1
           v_modifieddate:=(DATE_TRUNC('day',v_modifiedTime::timestamp));
           v_lastModifieddate:=(DATE_TRUNC('day',v_LastConsumedOn::timestamp));
           v_daysDifflastconCurrDate:=(v_modifieddate-v_lastModifieddate);
               IF v_daysDifflastconCurrDate<=v_RCAdminMaxdateallowed THEN
              v_succFailFlag:='SUCCESS';
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ReconcileProcess||' Screen';  -- Message to be written in VA
           END IF; -- of Time Checking
           END IF;----Check1

           /*Condition for consume voucher status, The status should be
         Reconcile only*/
         ELSIF(v_batchType = v_batchConStat)  THEN
           IF(v_vouchStat = v_batchReconcileStat) THEN
             v_succFailFlag:='SUCCESS';
           ELSE
                 v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='Voucher is '||v_vouchStat||' and cannot be made to '||v_batchType||' from '||v_ReconcileProcess||' Screen'; -- Message to be written in VA
           END IF; -- of consume type checking

        
         END IF; -- END OF batch type checking for 2

         END IF; -- end of voucher valid type process wise

          ELSE  -- Else of if voucher has expired
              v_errorCount:=v_errorCount+1;
           v_succFailFlag:='FAILED';
           v_message:='Voucher Has Expired';
         END IF;      -- end of  if(v_expDat>=p_createdOn)

       END;  -- end of batch type checking
v_returnMessage:='Function : VSC_CHECK_CHANGE_VALID_PROC executed successfully ';
v_returnLogMessage :=v_returnMessage;
  RAISE NOTICE '%',v_returnMessage;
EXCEPTION
           WHEN NO_DATA_FOUND THEN
              v_errorCount:=v_errorCount+1;
              v_succFailFlag:='FAILED';
              v_message:='NO Record found for voucher in vouchers table';
              v_voucherNotFoundCount:=v_voucherNotFoundCount+1;
              v_voucherNotFoundFlag:=TRUE;
              RAISE NOTICE '%','NO Record found for voucher in vouchers table'||v_serialStart;
         WHEN OTHERS THEN
               RAISE NOTICE '%','SQL Exception for updating records '||SQLERRM;
              v_returnMessage:='FAILED';
              v_returnLogMessage:='Exception while checking for voucher status in vouchers table'||v_serialStart||SQLERRM;
               RAISE EXCEPTION 'Exception while checking for voucher status in vouchers table' USING ERRCODE = 'EXITE';
END;
$_$;


ALTER FUNCTION pretupsdatabase.vsc_check_change_valid_proc(v_serialstart character varying, v_vouchernotfoundflag boolean, v_processscreen integer, v_createdon timestamp without time zone, v_batchtype character varying, v_batchenablestat character varying, v_batchgenstat character varying, v_enableprocess character varying, v_batchstolenstat character varying, v_batchonholdstat character varying, v_preactivestat character varying, v_suspendstat character varying, v_changeprocess character varying, v_warehousestat character varying, v_batchprintstat character varying, v_batchreconcilestat character varying, v_modifiedtime timestamp without time zone, v_rcadminmaxdateallowed integer, v_reconcileprocess character varying, v_batchconstat character varying, v_batchdamagestat character varying, v_seqid integer, OUT v_damagestolenafterencount integer, OUT v_vouchstat character varying, OUT v_vouccurrstat character varying, OUT v_productid character varying, OUT v_lastrequestattemptno double precision, OUT v_lastattemptvalue numeric, OUT v_succfailflag character varying, OUT v_enablecount integer, OUT v_errorcount integer, OUT v_message character varying, OUT v_damagestolencount integer, OUT v_onholdcount integer, OUT v_vouchernotfoundcount integer, OUT v_returnmessage character varying, OUT v_returnlogmessage character varying) OWNER TO pgdb;

--
-- Name: vsc_insert_in_audit_proc(character varying, character varying, character varying, character varying, character varying, timestamp without time zone, character varying, character varying, character varying, integer); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION vsc_insert_in_audit_proc(v_insertrowid character varying, v_serialstart character varying, v_batchtype character varying, v_vouchstat character varying, v_modifiedby character varying, v_modifiedtime timestamp without time zone, v_batchno character varying, v_message character varying, v_processstatus character varying, v_row_id integer, OUT v_returnmessage character varying, OUT v_returnlogmessage character varying, OUT v_sqlerrormessage character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
BEGIN
v_returnLogMessage :='';
v_sqlErrorMessage :='';
 RAISE NOTICE 'Function : VSC_INSERT_IN_AUDIT_PROC started' ;
            BEGIN -- block for getting next row ID
              SELECT nextval('voucher_audit_id') INTO v_row_id ;
              v_insertRowId:=TO_CHAR(v_row_id,'FM999999999999999999');
		RAISE NOTICE 'Function : VSC_INSERT_IN_AUDIT_PROC , v_insertRowId  = %',v_insertRowId ;
              EXCEPTION
              WHEN NO_DATA_FOUND THEN
              v_returnLogMessage:='Function : VSC_INSERT_IN_AUDIT_PROC , Exception while getting next row no for VA '||v_serialStart;
              v_returnMessage:='FAILED';
                  RAISE EXCEPTION 'Function : VSC_INSERT_IN_AUDIT_PROC , Exception while getting next row no for VA ' USING ERRCODE = 'EXITE';
              WHEN OTHERS THEN
              v_returnLogMessage:='Exception while getting next row no for VA '||v_serialStart;
              v_returnMessage:='FAILED';
                RAISE EXCEPTION 'Function : VSC_INSERT_IN_AUDIT_PROC , Exception while getting next row no for VA ' USING ERRCODE = 'EXITE';
            END;  -- end of getting next row id block

            BEGIN -- Block for inserting record in voucher_audit table
              INSERT INTO VOMS_VOUCHER_AUDIT(ROW_ID, SERIAL_NO, CURRENT_STATUS, PREVIOUS_STATUS,
              MODIFIED_BY, MODIFIED_ON, STATUS_CHANGE_SOURCE, STATUS_CHANGE_PARTNER_ID,
              BATCH_NO, MESSAGE, PROCESS_STATUS)
              VALUES(v_insertRowId,v_serialStart,v_batchType,v_vouchStat,v_modifiedBy,v_modifiedTime,
              'WEB','',v_batchNo,v_message,v_processStatus);
               RAISE NOTICE 'Function : VSC_INSERT_IN_AUDIT_PROC , INSERT query executed successfully' ;
               
              EXCEPTION
              WHEN OTHERS THEN
            RAISE NOTICE 'Function : VSC_INSERT_IN_AUDIT_PROC , others EXCEPTION while inserting next row no  = %',SQLERRM ;
              v_returnMessage:='FAILED';
              v_returnLogMessage:='Function : VSC_INSERT_IN_AUDIT_PROC , Exception while inserting in VA table '||v_serialStart||SQLERRM;
               RAISE EXCEPTION 'Function : VSC_INSERT_IN_AUDIT_PROC , Exception while inserting in VA table  ' USING ERRCODE = 'NINEX';

            END;  -- end of inserting record in voucher_audit table
v_returnMessage:='Function : VSC_INSERT_IN_AUDIT_PROC  executed successfully';
 RAISE NOTICE '%',v_returnMessage ;
EXCEPTION
when sqlstate 'SQLEX'  then
     v_returnMessage:='FAILED';
     v_sqlErrorMessage:=SQLERRM;
       RAISE NOTICE 'Function : VSC_INSERT_IN_AUDIT_PROC ,  SQL Exception for inserting in VA table  = %',v_serialStart ;
       RAISE EXCEPTION 'Function : VSC_INSERT_IN_AUDIT_PROC ,  Exception while inserting in VA table  ' USING ERRCODE = 'EXITE';
when sqlstate 'NINEX'  then
     v_returnMessage:='FAILED';
      v_sqlErrorMessage:=SQLERRM;
       RAISE NOTICE 'Function : VSC_INSERT_IN_AUDIT_PROC ,  SQL Exception for inserting in VA table  = %',v_serialStart ;
       RAISE EXCEPTION 'Function : VSC_INSERT_IN_AUDIT_PROC ,  Exception while inserting in VA table  ' USING ERRCODE = 'EXITE';
when sqlstate 'NINEX'  then
     v_returnMessage:='FAILED';
      v_sqlErrorMessage:=SQLERRM;
       RAISE EXCEPTION 'Function : VSC_INSERT_IN_AUDIT_PROC ,  Exception while inserting in VA table  ' USING ERRCODE = 'EXITE';
WHEN OTHERS THEN
v_returnMessage:='FAILED';
v_returnLogMessage:='Function : VSC_INSERT_IN_AUDIT_PROC ,  Exception while inserting in VA table '||v_serialStart||SQLERRM;
v_sqlErrorMessage:=SQLERRM;
 RAISE EXCEPTION 'Function : VSC_INSERT_IN_AUDIT_PROC ,  Exception while inserting in VA table  ' USING ERRCODE = 'EXITE';
END;
$$;


ALTER FUNCTION pretupsdatabase.vsc_insert_in_audit_proc(v_insertrowid character varying, v_serialstart character varying, v_batchtype character varying, v_vouchstat character varying, v_modifiedby character varying, v_modifiedtime timestamp without time zone, v_batchno character varying, v_message character varying, v_processstatus character varying, v_row_id integer, OUT v_returnmessage character varying, OUT v_returnlogmessage character varying, OUT v_sqlerrormessage character varying) OWNER TO pgdb;

--
-- Name: vsc_insert_in_summary_proc(integer, integer, integer, integer, character varying, timestamp without time zone, character varying, character varying, integer, integer, character varying); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION vsc_insert_in_summary_proc(v_enablecount integer, v_counsumedcount integer, v_damagestolencount integer, v_damagestolenafterencount integer, v_referenceno character varying, v_createdon timestamp without time zone, v_productid character varying, v_networkcode character varying, v_onholdcount integer, rcd_count integer, p_fromserialno character varying, OUT v_returnlogmessage character varying, OUT v_returnmessage character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
BEGIN
  RAISE NOTICE ' Function : VSC_INSERT_IN_SUMMARY_PROC  started';
	    BEGIN
	      RAISE NOTICE ' Function : VSC_INSERT_IN_SUMMARY_PROC  qry UPDATE VOMS_VOUCHER_BATCH_SUMMARY before execution v_referenceNo =% ',v_referenceNo;
		UPDATE VOMS_VOUCHER_BATCH_SUMMARY SET
		TOTAL_ENABLED=TOTAL_ENABLED+v_enableCount,
		TOTAL_RECHARGED=TOTAL_RECHARGED+v_counsumedCount,
		TOTAL_STOLEN_DMG=TOTAL_STOLEN_DMG+(v_DamageStolenCount-v_DamageStolenAfterEnCount),
		TOTAL_STOLEN_DMG_AFTER_EN=TOTAL_STOLEN_DMG_AFTER_EN+v_DamageStolenAfterEnCount
		WHERE  BATCH_NO =v_referenceNo;
            RAISE NOTICE ' Function : VSC_INSERT_IN_SUMMARY_PROC qry UPDATE VOMS_VOUCHER_BATCH_SUMMARY executed';
  
            EXCEPTION
             WHEN OTHERS THEN
               RAISE NOTICE '%','Function : VSC_INSERT_IN_SUMMARY_PROC , SQL EXCEPTION while qry UPDATE VOMS_VOUCHER_BATCH_SUMMARY  = '||SQLERRM;
	       RAISE EXCEPTION 'Function : VSC_INSERT_IN_SUMMARY_PROC , SQL EXCEPTION while qry UPDATE VOMS_VOUCHER_BATCH_SUMMARY ' USING ERRCODE = 'SQLEX';  

            END;

      BEGIN      --block for insertion/updation in voucher_summary
         BEGIN  --block checking if record exist in voucher_summary
          RAISE NOTICE 'Function : VSC_INSERT_IN_SUMMARY_PROC , before getting rcd_count, v_productID = %', v_productID;
         SELECT '1' INTO rcd_count
         WHERE EXISTS (SELECT 1 FROM VOMS_VOUCHER_SUMMARY
                       WHERE SUMMARY_DATE=v_createdOn
                     AND PRODUCT_ID=v_productID
                     AND PRODUCTION_NETWORK_CODE=v_networkCode
                     AND USER_NETWORK_CODE=v_networkCode);

        RAISE NOTICE 'Function : VSC_INSERT_IN_SUMMARY_PROC ,after getting rcd_count= %',rcd_count;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN  --when no row returned for the distributor
              RAISE NOTICE 'Function : VSC_INSERT_IN_SUMMARY_PROC, No Record found in voucher summary table';
             rcd_count := 0;
        when sqlstate 'SQLEX'  then
              RAISE NOTICE 'Function : VSC_INSERT_IN_SUMMARY_PROC, SQL EXCEPTION while checking for voucher_summary  = %',SQLERRM ;
              v_returnMessage:='FAILED';
              v_returnLogMessage:='Function : VSC_INSERT_IN_SUMMARY_PROC, Exception while checking is record exist in summary table ';
               RAISE EXCEPTION 'Function : VSC_INSERT_IN_SUMMARY_PROC, SQL EXCEPTION while update voucher table' USING ERRCODE = 'SQLEX';
        WHEN OTHERS THEN
              RAISE NOTICE 'Function : VSC_INSERT_IN_SUMMARY_PROC , Exception while checking is record exist' ;
             v_returnMessage:='FAILED';
             v_returnLogMessage:='Function : VSC_INSERT_IN_SUMMARY_PROC , Exception while checking is record exist in summary table ';
              RAISE EXCEPTION 'Function : VSC_INSERT_IN_SUMMARY_PROC  , SQL EXCEPTION while update voucher table' USING ERRCODE = 'SQLEX';
       END;
      IF rcd_count = 0 THEN
        INSERT INTO VOMS_VOUCHER_SUMMARY(SUMMARY_DATE, PRODUCT_ID, PRODUCTION_NETWORK_CODE,
        USER_NETWORK_CODE,TOTAL_ENABLED, TOTAL_STOLEN_DMG,TOTAL_STOLEN_DMG_AFTER_EN, TOTAL_ON_HOLD)
        VALUES(v_createdOn,v_productID,v_networkCode,v_networkCode,v_enableCount,
            (v_DamageStolenCount-v_DamageStolenAfterEnCount),v_DamageStolenAfterEnCount,v_onHoldCount);
      ELSE
           UPDATE VOMS_VOUCHER_SUMMARY
        SET TOTAL_ENABLED=TOTAL_ENABLED+v_enableCount,
        TOTAL_STOLEN_DMG=TOTAL_STOLEN_DMG+(v_DamageStolenCount-v_DamageStolenAfterEnCount),
        TOTAL_STOLEN_DMG_AFTER_EN=TOTAL_STOLEN_DMG_AFTER_EN+v_DamageStolenAfterEnCount,
        TOTAL_ON_HOLD=TOTAL_ON_HOLD+v_onHoldCount
                       WHERE SUMMARY_DATE=v_createdOn
                     AND PRODUCT_ID=v_productID
                     AND PRODUCTION_NETWORK_CODE=v_networkCode
                     AND USER_NETWORK_CODE=v_networkCode;
      END IF;
      EXCEPTION
          when sqlstate 'SQLEX'  then
              v_returnMessage:='FAILED';
               RAISE NOTICE 'Function : VSC_INSERT_IN_SUMMARY_PROC , SQL/OTHERS EXCEPTION CAUGHT while Record exist in summary % ', SQLERRM ;
               RAISE EXCEPTION 'Function : VSC_INSERT_IN_SUMMARY_PROC , SQL EXCEPTION while update voucher table' USING ERRCODE = 'SQLEX';
        WHEN OTHERS THEN
               RAISE NOTICE 'Function : VSC_INSERT_IN_SUMMARY_PROC , EXCEPTION CAUGHT while Record exist in summary= % ', SQLERRM ;
              v_returnMessage:='FAILED';
              v_returnLogMessage:='Function : VSC_INSERT_IN_SUMMARY_PROC , Exception while insertin/updating summary table ';
               RAISE EXCEPTION 'Function : VSC_INSERT_IN_SUMMARY_PROC , Exception while insertin/updating summary table ' USING ERRCODE = 'NINEX';
       END;  --end of voucher_audit insertion block
   v_returnMessage:=  'Function : VSC_INSERT_IN_SUMMARY_PROC  executed successfully';   
  RAISE NOTICE '%',v_returnMessage;
EXCEPTION
	when sqlstate 'SQLEX'  then
		v_returnMessage:='FAILED';
		RAISE NOTICE 'Function : VSC_INSERT_IN_SUMMARY_PROC , SQL Exception for updating records   % ', p_fromSerialNo ;
		RAISE EXCEPTION ' Function : VSC_INSERT_IN_SUMMARY_PROC , SQL Exception for updating records   ' USING ERRCODE = 'EXITE';
	when sqlstate 'NINEX'  then
		v_returnMessage:='FAILED';
		RAISE NOTICE 'Function : VSC_INSERT_IN_SUMMARY_PROC , Not able to insert record in voucher_audit    % ', p_fromSerialNo ;
		RAISE EXCEPTION 'Function : VSC_INSERT_IN_SUMMARY_PROC , Not able to insert record in voucher_audit ' USING ERRCODE = 'EXITE';
	WHEN OTHERS THEN
		v_returnMessage:='FAILED';
		v_returnLogMessage:='Function : VSC_INSERT_IN_SUMMARY_PROC , Exception while inserting/updating summary table ';
		RAISE EXCEPTION 'Function : VSC_INSERT_IN_SUMMARY_PROC , Exception while inserting/updating summary table ' USING ERRCODE = 'EXITE';
END;
$$;


ALTER FUNCTION pretupsdatabase.vsc_insert_in_summary_proc(v_enablecount integer, v_counsumedcount integer, v_damagestolencount integer, v_damagestolenafterencount integer, v_referenceno character varying, v_createdon timestamp without time zone, v_productid character varying, v_networkcode character varying, v_onholdcount integer, rcd_count integer, p_fromserialno character varying, OUT v_returnlogmessage character varying, OUT v_returnmessage character varying) OWNER TO pgdb;

--
-- Name: vsc_update_voucher_enable(character varying, character varying, character varying, character varying, character varying, timestamp without time zone, character varying, integer); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION vsc_update_voucher_enable(v_vouchstat character varying, v_batchreconcilestat character varying, v_batchno character varying, v_batchtype character varying, v_modifiedby character varying, v_modifiedtime timestamp without time zone, v_serialstart character varying, v_seqid integer, OUT v_returnlogmessage character varying, OUT v_returnmessage character varying, OUT v_message character varying) RETURNS record
    LANGUAGE plpgsql
    AS $_$
DECLARE
QUERY TEXT := '';
QRY_UPDATE_VOMS_VOUCHERS_IF_STATUS_RECON TEXT :=' UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=$1 , STATUS = $2 ,CURRENT_STATUS =$3 ,LAST_BATCH_NO=$4
            ,MODIFIED_BY=$5,MODIFIED_ON=$6
            ,PREVIOUS_STATUS=$7 WHERE serial_no=$8';
            
QRY_UPDATE_VOMS_VOUCHERS_IF_STATUS_OTHER TEXT :=' UPDATE VOMS_VOUCHERS SET ENABLE_BATCH_NO=$1,STATUS=$2,CURRENT_STATUS=$3,MODIFIED_BY=$4
,MODIFIED_ON=$5,LAST_BATCH_NO=$6,PREVIOUS_STATUS=$7  WHERE serial_no=$8 ';
            
QRY_SEQ_ID TEXT :=' AND SEQUENCE_ID=$9';

BEGIN


RAISE NOTICE 'Function : vsc_update_voucher_enable started' ;
	RAISE NOTICE '%','v_vouchStat & v_batchReconcileStat  ='||v_vouchStat||' & '||v_batchReconcileStat;
	/* If previous voucher status is of Reconcile then update */
            IF(v_vouchStat=v_batchReconcileStat) THEN
	RAISE NOTICE 'Frst IF' ;
		    IF(v_seqId  = 0 ) THEN
			QUERY = QRY_UPDATE_VOMS_VOUCHERS_IF_STATUS_RECON ;
		    ELSE
			QUERY = QRY_UPDATE_VOMS_VOUCHERS_IF_STATUS_RECON || QRY_SEQ_ID ;
		    END IF; 
           
		RAISE NOTICE 'Function : vsc_update_voucher_enable,QUERY : %',QUERY ;
            /*************************
            Code modified by kamini
            UPDATE VOMS_VOUCHERS set RECHARGE_SOURCE=null, CONSUMED_BY=null, CONSUMED_ON=null,
            TRANSACTION_ID=null, RECHARGE_PARTNER_ID=null, REQUEST_SOURCE =null,
            REQUEST_PARTNER_ID=null, TALK_TIME=null, VALIDUPTO=null, GRACE_PERIOD=null,
            TAX_RATE=null, TAX_AMOUNT=null, PARTNER_PRODUCT_ID=null, USER_NETWORK_CODE=null,
            ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;
            ************************/
		BEGIN
		EXECUTE QUERY using v_batchNo, v_batchType, v_batchType, v_batchNo, v_modifiedBy, v_modifiedTime, v_vouchStat, v_serialStart,v_seqId;            

              EXCEPTION
	      WHEN OTHERS THEN
	       RAISE NOTICE '%','Function : vsc_update_voucher_enable, SQL EXCEPTION while update voucher table  ='||SQLERRM;
	       RAISE EXCEPTION 'Function : vsc_update_voucher_enable, SQL EXCEPTION while update voucher table' USING ERRCODE = 'SQLEX';

	       END;

            /* If previous voucher status other than Reconcile then update */
            ELSE
		RAISE NOTICE '%',' Function : vsc_update_voucher_enable, v_vouchStat & v_batchReconcileStat not match. v_serialStart = '|| v_serialStart;

		 IF(v_seqId = 0) THEN
			QUERY = QRY_UPDATE_VOMS_VOUCHERS_IF_STATUS_OTHER ;
		 ELSE
			QUERY = QRY_UPDATE_VOMS_VOUCHERS_IF_STATUS_OTHER || QRY_SEQ_ID ;
		 END IF; 
		    
            /*************************
            Code modified by kamini
            UPDATE VOMS_VOUCHERS set ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart;

            ********************/
	  BEGIN
            EXECUTE QUERY using v_batchNo,v_batchType,v_batchType,v_modifiedBy,v_modifiedTime,v_batchNo,v_vouchStat,v_serialStart,v_seqId;
       
             EXCEPTION
	      WHEN OTHERS THEN
	       RAISE NOTICE '%','Function : vsc_update_voucher_enable, SQL EXCEPTION while update voucher table  ='||SQLERRM;
	       RAISE EXCEPTION 'Function : vsc_update_voucher_enable, SQL EXCEPTION while update voucher table' USING ERRCODE = 'SQLEX';

	       END;

           END IF;  -- end of if(v_vouchStat=p_batchReconcileStat)
v_returnmessage := 'Function : vsc_update_voucher_enable executed successfully';
 RAISE NOTICE '%', v_returnmessage ;
EXCEPTION
 when sqlstate 'SQLEX'  then
     v_returnMessage:='FAILED';
      v_message:='Not able to update voucher table'||v_serialStart;
      v_returnLogMessage:='Not able to update voucher table'||v_serialStart;
       RAISE NOTICE 'Function : vsc_update_voucher_enable, Not able to update voucher in vouchers table %',v_serialStart;
        RAISE EXCEPTION 'Function : vsc_update_voucher_enable, Not able to update voucher table' USING ERRCODE = 'EXITE';
    

WHEN OTHERS THEN
     v_returnMessage:='FAILED';
        RAISE NOTICE 'Function : vsc_update_voucher_enable, Exception while updating records %',v_serialStart;
      v_returnLogMessage:='Exception while updating voucher table'||v_serialStart;
       RAISE EXCEPTION 'Function : vsc_update_voucher_enable,  EXCEPTION while update voucher table' USING ERRCODE = 'EXITE';
END;
$_$;


ALTER FUNCTION pretupsdatabase.vsc_update_voucher_enable(v_vouchstat character varying, v_batchreconcilestat character varying, v_batchno character varying, v_batchtype character varying, v_modifiedby character varying, v_modifiedtime timestamp without time zone, v_serialstart character varying, v_seqid integer, OUT v_returnlogmessage character varying, OUT v_returnmessage character varying, OUT v_message character varying) OWNER TO pgdb;

--
-- Name: vsc_update_voucher_enable_other(character varying, character varying, character varying, timestamp without time zone, character varying, character varying, character varying, character varying, integer); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION vsc_update_voucher_enable_other(v_batchno character varying, v_batchtype character varying, v_modifiedby character varying, v_modifiedtime timestamp without time zone, v_vouchstat character varying, v_batchreconcilestat character varying, v_vouccurrstat character varying, v_serialstart character varying, v_seqid integer, OUT v_returnlogmessage character varying, OUT v_returnmessage character varying, OUT v_message character varying) RETURNS record
    LANGUAGE plpgsql
    AS $_$
DECLARE
FUNC_NAME TEXT :='VSC_UPDATE_VOUCHER_ENABLE_OTHER';
QRY_WHEN_STATUS_RECON TEXT :=' UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=$1,STATUS=$2,CURRENT_STATUS=$3,LAST_BATCH_NO=$4,
            MODIFIED_BY=$5,MODIFIED_ON=$6,
            PREVIOUS_STATUS=$7
            WHERE serial_no=$8 ';
          
QRY_WHEN_STATUS_CON TEXT :=' UPDATE VOMS_VOUCHERS SET USER_NETWORK_CODE=NULL,
            ENABLE_BATCH_NO=$1,CURRENT_STATUS=$2,LAST_BATCH_NO=$3,
            MODIFIED_BY=$4,MODIFIED_ON=$5,
            PREVIOUS_STATUS=$6
            WHERE serial_no=$7 ';
			
QRY_WHEN_STATUS_OTHER TEXT :=' UPDATE VOMS_VOUCHERS SET ENABLE_BATCH_NO=$1,STATUS=$2,CURRENT_STATUS=$3,
            MODIFIED_BY=$4,MODIFIED_ON=$5,LAST_BATCH_NO=$6,PREVIOUS_STATUS= $7
            WHERE serial_no=$8 ';		
        

BEGIN
RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER staterd ' ;

	   IF(v_seqId != 0 ) THEN
		QRY_WHEN_STATUS_RECON = QRY_WHEN_STATUS_RECON || ' AND SEQUENCE_ID= $9' ;
		QRY_WHEN_STATUS_CON = QRY_WHEN_STATUS_CON || ' AND SEQUENCE_ID= $8'  ;
		QRY_WHEN_STATUS_OTHER = QRY_WHEN_STATUS_OTHER || ' AND SEQUENCE_ID= $9'  ;
           END IF; 
           
	RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER, v_vouchStat= % v_batchReconcileStat= % v_voucCurrStat % ',v_vouchStat,v_batchReconcileStat, v_voucCurrStat ;

            BEGIN

           /* If previous voucher status and current status both is in Reconcile state then update current status and status both*/
            IF((v_vouchStat=v_batchReconcileStat) AND (v_voucCurrStat=v_batchReconcileStat)) THEN
		RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  status Reconcile ' ;
            /*************************
            Code modified by kamini

            UPDATE VOMS_VOUCHERS set RECHARGE_SOURCE=null, CONSUMED_BY=null, CONSUMED_ON=null,
            TRANSACTION_ID=null, RECHARGE_PARTNER_ID=null, REQUEST_SOURCE =null,
            REQUEST_PARTNER_ID=null, TALK_TIME=null, VALIDUPTO=null, GRACE_PERIOD=null,
            TAX_RATE=null, TAX_AMOUNT=null, PARTNER_PRODUCT_ID=null, USER_NETWORK_CODE=null,
            ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            PREVIOUS_STATUS=v_vouchStat
            WHERE serial_no=v_serialStart;
            ************************/
	    RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  QRY_WHEN_STATUS_RECON before execution, Query=%', QRY_WHEN_STATUS_RECON ; 
            EXECUTE QRY_WHEN_STATUS_RECON using v_batchNo,v_batchType, v_batchType, v_batchNo, v_modifiedBy, v_modifiedTime, v_vouchStat, v_serialStart, v_seqId;
           RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  QRY_WHEN_STATUS_RECON after execution' ;
           
      

            /* If previous voucher status is consumed and current status is in Reconcile state then update only current status*/
            ELSIF ((v_vouchStat=v_batchConStat) AND (v_voucCurrStat=v_batchReconcileStat)) THEN
	   RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  QRY_WHEN_STATUS_consumed before execution, Query=%', QRY_WHEN_STATUS_RECON ; 
            EXECUTE QRY_WHEN_STATUS_CON USING v_batchNo,v_batchType,v_batchNo,v_modifiedBy,v_modifiedTime,v_vouchStat,v_serialStart, v_seqId;
	   RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  QRY_WHEN_STATUS_consumed after execution' ;
  
            /*************************
            Code modified by kamini
            UPDATE VOMS_VOUCHERS set ENABLE_BATCH_NO=v_batchNo,CURRENT_STATUS=v_batchType,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            LAST_BATCH_NO=v_batchNo,PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart;

            ********************/

            ELSE --Added By Gurjeet on 11/10/2004 because this was missing
	   RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  QRY_WHEN_STATUS_other before execution, Query=%', QRY_WHEN_STATUS_RECON ; 
            EXECUTE QRY_WHEN_STATUS_OTHER USING v_batchNo, v_batchType, v_batchType, v_modifiedBy, v_modifiedTime, v_batchNo, v_vouchStat, v_serialStart, v_seqId;
	   RAISE NOTICE 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  QRY_WHEN_STATUS_other after execution' ;

           END IF;  -- end of if(v_vouchStat=p_batchReconcileStat)
           EXCEPTION
		WHEN OTHERS THEN
		 RAISE NOTICE '%','Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER, SQL Exception for updating records '||SQLERRM;
              v_returnMessage:='FAILED';
              v_returnLogMessage:='Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Exception while UPDATING for voucher status in vouchers table'||SQLERRM;
               RAISE EXCEPTION 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Exception while checking for voucher status in vouchers table' USING ERRCODE = 'SQLEX';
           
           END;
           v_message:='Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Succesfully executed';
            RAISE NOTICE '%',v_message;
EXCEPTION
 when sqlstate 'SQLEX'  then
     v_returnMessage:='FAILED';
      v_message:='Not able to update voucher table'||v_serialStart;
      v_returnLogMessage:='Not able to update voucher table'||v_serialStart;
       RAISE NOTICE '%','Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Not able to update voucher in vouchers table'||SQLERRM;
        RAISE EXCEPTION 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Not able to update voucher in vouchers table' USING ERRCODE = 'SQLEX';

WHEN OTHERS THEN
      v_returnMessage:='FAILED';
      v_returnLogMessage:='Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Exception while updating voucher table'||v_serialStart;
        RAISE NOTICE '%','Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Exception while updating records'||SQLERRM;
        RAISE EXCEPTION 'Function :  VSC_UPDATE_VOUCHER_ENABLE_OTHER,  Exception while updating records' USING ERRCODE = 'EXITE';
END;
$_$;


ALTER FUNCTION pretupsdatabase.vsc_update_voucher_enable_other(v_batchno character varying, v_batchtype character varying, v_modifiedby character varying, v_modifiedtime timestamp without time zone, v_vouchstat character varying, v_batchreconcilestat character varying, v_vouccurrstat character varying, v_serialstart character varying, v_seqid integer, OUT v_returnlogmessage character varying, OUT v_returnmessage character varying, OUT v_message character varying) OWNER TO pgdb;

--
-- Name: vsc_update_vouchers(character varying, character varying, character varying, character varying, character varying, timestamp without time zone, character varying, character varying, double precision, numeric, integer); Type: FUNCTION; Schema: pretupsdatabase; Owner: pgdb
--

CREATE FUNCTION vsc_update_vouchers(v_vouchstat character varying, v_batchreconcilestat character varying, v_batchno character varying, v_batchtype character varying, v_modifiedby character varying, v_modifiedtime timestamp without time zone, v_serialstart character varying, v_vouccurrstat character varying, v_lastrequestattemptno double precision, v_lastattemptvalue numeric, v_seqid integer, OUT v_returnlogmessage character varying, OUT v_returnmessage character varying, OUT v_message character varying) RETURNS record
    LANGUAGE plpgsql
    AS $_$
DECLARE

QRY_WHEN_STATUS_RECON TEXT := ' UPDATE VOMS_VOUCHERS SET STATUS=$1,CURRENT_STATUS=$2,LAST_BATCH_NO=$3,
                MODIFIED_BY=$4,MODIFIED_ON=$5,PREVIOUS_STATUS=$6,
                LAST_Attempt_NO=$7,ATTEMPT_USED=ATTEMPT_USED+1,
                TOTAL_VALUE_USED=(TOTAL_VALUE_USED+$8)
                WHERE serial_no=$9';
               
QRY_WHEN_STATUS_OTHER TEXT := 'UPDATE VOMS_VOUCHERS SET STATUS=$1,CURRENT_STATUS=$2,LAST_BATCH_NO=$3,
                MODIFIED_BY=$4,MODIFIED_ON=$5,PREVIOUS_STATUS=$6 WHERE serial_no=$7';           

BEGIN
		
		RAISE NOTICE 'Function : VSC_UPDATE_VOUCHERS, v_batchType= % v_batchNo= % ',v_batchType,v_batchNo ;

		IF(v_seqId != 0) THEN
			RAISE NOTICE 'Function : VSC_UPDATE_VOUCHERS, v_seqId= % ',v_seqId ;
			QRY_WHEN_STATUS_RECON = QRY_WHEN_STATUS_RECON || ' AND SEQUENCE_ID= $10' ;
			QRY_WHEN_STATUS_OTHER = QRY_WHEN_STATUS_OTHER || ' AND SEQUENCE_ID= $8'  ;
		END IF; 
           
       
            /*************************
            Code modified by kamini

            UPDATE VOMS_VOUCHERS set CURRENT_STATUS=v_batchType,LAST_BATCH_NO=v_batchNo,
            MODIFIED_BY=v_modifiedBy,MODIFIED_ON=v_modifiedTime,MODIFIED_DATE=v_createdOn,
            PREVIOUS_STATUS=v_vouchStat WHERE serial_no=v_serialStart;LAST_ATTEMPT_NO=LAST_ATTEMPT_NO+1
            **************************/
            BEGIN
            IF(v_voucCurrStat=v_batchReconcileStat) THEN
		RAISE NOTICE 'Function : VSC_UPDATE_VOUCHERS, status reconciliation , Query = % ' , QRY_WHEN_STATUS_RECON;
		EXECUTE  QRY_WHEN_STATUS_RECON  using  v_batchType, v_batchType, v_batchNo, v_modifiedBy, v_modifiedTime,v_vouchStat
                v_LastRequestAttemptNo, v_LastAttemptValue, v_serialStart , v_seqId;
                RAISE NOTICE 'Function : VSC_UPDATE_VOUCHERS, status reconciliation , Query executed succesfully ' ;

            ELSE
		RAISE NOTICE 'Function : VSC_UPDATE_VOUCHERS, status other, Query = % ' , QRY_WHEN_STATUS_OTHER;
		EXECUTE  QRY_WHEN_STATUS_OTHER   using v_batchType, v_batchType, v_batchNo, v_modifiedBy, v_modifiedTime, v_vouchStat, v_serialStart , v_seqId ;
		RAISE NOTICE 'Function : VSC_UPDATE_VOUCHERS, status other, Query executed succesfully ' ;
            END IF;

            EXCEPTION
		WHEN OTHERS THEN
		RAISE NOTICE '%','Function : VSC_UPDATE_VOUCHERS, SQL EXCEPTION while update voucher table  = '||SQLERRM;
		RAISE EXCEPTION 'Function : VSC_UPDATE_VOUCHERS, SQL EXCEPTION while update voucher table' USING ERRCODE = 'SQLEX';
	    END;
v_message:='VSC_UPDATE_VOUCHERS, Successfully executed';	    
EXCEPTION
 when sqlstate 'SQLEX'  then
     v_returnMessage:='FAILED';
      v_message:='Function :VSC_UPDATE_VOUCHERS,Not able to update voucher table'||v_serialStart;
      v_returnLogMessage:='Function :VSC_UPDATE_VOUCHERS,Not able to update voucher table'||v_serialStart;
       RAISE NOTICE '%','Function :VSC_UPDATE_VOUCHERS,VSC_UPDATE_VOUCHERS,Not able to update voucher in vouchers table='||SQLERRM;
        RAISE EXCEPTION 'Function :VSC_UPDATE_VOUCHERS,Not able to update voucher in vouchers table' USING ERRCODE = 'EXITE';

WHEN OTHERS THEN
     v_returnMessage:='FAILED';
      v_returnLogMessage:='Function :VSC_UPDATE_VOUCHERS, Exception while updating voucher table'||v_serialStart;
       RAISE NOTICE '%','Function :VSC_UPDATE_VOUCHERS, Exception while updating records'||SQLERRM;
       RAISE EXCEPTION 'Function :VSC_UPDATE_VOUCHERS, Exception while updating records' USING ERRCODE = 'EXITE';
END;
$_$;


ALTER FUNCTION pretupsdatabase.vsc_update_vouchers(v_vouchstat character varying, v_batchreconcilestat character varying, v_batchno character varying, v_batchtype character varying, v_modifiedby character varying, v_modifiedtime timestamp without time zone, v_serialstart character varying, v_vouccurrstat character varying, v_lastrequestattemptno double precision, v_lastattemptvalue numeric, v_seqid integer, OUT v_returnlogmessage character varying, OUT v_returnmessage character varying, OUT v_message character varying) OWNER TO pgdb;

SET search_path = public, pg_catalog;

--
-- Name: enable_voucher_count(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION enable_voucher_count() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
      
DECLARE

V_RECORD_EXISTS SMALLINT;
BEGIN  
     IF (OLD.CURRENT_STATUS = 'EN' AND NEW.CURRENT_STATUS <> 'CU' AND NEW.CURRENT_STATUS <> 'UP' AND OLD.CURRENT_STATUS <> NEW.CURRENT_STATUS  ) THEN

              UPDATE VOMS_ENABLE_SUMMARY SET VOUCHER_COUNT = VOUCHER_COUNT - 1 WHERE PRODUCT_ID = OLD.PRODUCT_ID
                               AND CREATED_DATE  =  OLD.CREATED_DATE AND EXPIRY_DATE =  OLD.EXPIRY_DATE;
      END IF;

     IF (NEW.CURRENT_STATUS = 'EN' AND OLD.CURRENT_STATUS <> NEW.CURRENT_STATUS  AND OLD.CURRENT_STATUS<> 'CU' AND OLD.CURRENT_STATUS<>'UP' ) THEN
         SELECT COUNT(1) INTO V_RECORD_EXISTS FROM VOMS_ENABLE_SUMMARY WHERE PRODUCT_ID = OLD.PRODUCT_ID
                               AND CREATED_DATE  =  OLD.CREATED_DATE AND EXPIRY_DATE =  OLD.EXPIRY_DATE;
         IF V_RECORD_EXISTS = 1 THEN
                  UPDATE VOMS_ENABLE_SUMMARY SET VOUCHER_COUNT = VOUCHER_COUNT + 1 WHERE PRODUCT_ID = OLD.PRODUCT_ID
                              AND CREATED_DATE  =  OLD.CREATED_DATE  AND EXPIRY_DATE =  OLD.EXPIRY_DATE;
         ELSE
               INSERT INTO VOMS_ENABLE_SUMMARY (
              PRODUCT_ID, CREATED_DATE, VOUCHER_COUNT,
              EXPIRY_DATE) VALUES ( OLD.PRODUCT_ID, OLD.CREATED_DATE , 1 , OLD.EXPIRY_DATE);
         END IF;

     END IF;

END;
$$;


ALTER FUNCTION public.enable_voucher_count() OWNER TO pgdb;

--
-- Name: status_change(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION status_change() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN  
	IF (OLD.status IN ('CU','ST','DA') AND OLD.status <> NEW.status ) THEN  
	RAISE EXCEPTION 'Invalid_status_change';  
	END IF;    
END;
$$;


ALTER FUNCTION public.status_change() OWNER TO pgdb;

--
-- Name: trig_barred_msisdn_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_barred_msisdn_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO BARRED_MSISDN_HISTORY(module,network_code,msisdn,name,user_type,
barred_type,created_on,created_by,modified_on,modified_by,barred_reason,created_date,
entry_date,operation_performed)
VALUES(NEW.module,NEW.network_code,NEW.msisdn,NEW.name,NEW.user_type,
NEW.barred_type,NEW.created_on,NEW.created_by,NEW.created_on,
NEW.created_by,NEW.barred_reason,NEW.created_date,sysdate,'I');
ELSIF UPDATING THEN
INSERT INTO BARRED_MSISDN_HISTORY(module,network_code,msisdn,name,user_type,
barred_type,created_on,created_by,modified_on,modified_by,barred_reason,created_date,
entry_date,operation_performed)
VALUES(NEW.module,NEW.network_code,NEW.msisdn,NEW.name,NEW.user_type,
NEW.barred_type,NEW.created_on,NEW.created_by,NEW.modified_on,
NEW.modified_by,NEW.barred_reason,NEW.created_date,sysdate,'U');
ELSIF DELETING THEN
INSERT INTO BARRED_MSISDN_HISTORY(module,network_code,msisdn,name,user_type,
barred_type,created_on,created_by,modified_on,modified_by,barred_reason,created_date,
entry_date,operation_performed)
VALUES(OLD.module,OLD.network_code,OLD.msisdn,OLD.name,OLD.user_type,
OLD.barred_type,OLD.created_on,OLD.created_by,OLD.modified_on,
OLD.modified_by,OLD.barred_reason,OLD.created_date,sysdate,'D');
END IF;
END;
$$;


ALTER FUNCTION public.trig_barred_msisdn_history() OWNER TO pgdb;

--
-- Name: trig_bonus_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_bonus_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN

IF INSERTING THEN
INSERT INTO BONUS_HISTORY(PROFILE_TYPE,USER_ID_OR_MSISDN,POINTS,
BUCKET_CODE,PRODUCT_CODE,POINTS_DATE,LAST_REDEMPTION_ID,LAST_REDEMPTION_ON,
LAST_ALLOCATION_TYPE,LAST_ALLOCATED_ON,CREATED_ON,CREATED_BY,
MODIFIED_ON,MODIFIED_BY,OPERATION_PERFORMED,ENTRY_DATE,TRANSFER_ID,PROFILE_ID,ACCUMULATED_POINTS,VERSION)
VALUES(NEW.PROFILE_TYPE,NEW.USER_ID_OR_MSISDN,NEW.POINTS,NEW.BUCKET_CODE,
NEW.PRODUCT_CODE,NEW.POINTS_DATE,NEW.LAST_REDEMPTION_ID,NEW.LAST_REDEMPTION_ON,
NEW.LAST_ALLOCATION_TYPE,NEW.LAST_ALLOCATED_ON,NEW.CREATED_ON,NEW.CREATED_BY,
NEW.MODIFIED_ON,NEW.MODIFIED_BY,'I',SYSDATE,NEW.TRANSFER_ID,NEW.PROFILE_ID,NEW.ACCUMULATED_POINTS,NEW.VERSION);

ELSIF UPDATING THEN
INSERT INTO BONUS_HISTORY(PROFILE_TYPE,USER_ID_OR_MSISDN,POINTS,
BUCKET_CODE,PRODUCT_CODE,POINTS_DATE,LAST_REDEMPTION_ID,LAST_REDEMPTION_ON,
LAST_ALLOCATION_TYPE,LAST_ALLOCATED_ON,CREATED_ON,CREATED_BY,
MODIFIED_ON,MODIFIED_BY,OPERATION_PERFORMED,ENTRY_DATE,TRANSFER_ID,PROFILE_ID,ACCUMULATED_POINTS,VERSION)
VALUES(NEW.PROFILE_TYPE,NEW.USER_ID_OR_MSISDN,NEW.POINTS,NEW.BUCKET_CODE,
NEW.PRODUCT_CODE,NEW.POINTS_DATE,NEW.LAST_REDEMPTION_ID,NEW.LAST_REDEMPTION_ON,
NEW.LAST_ALLOCATION_TYPE,NEW.LAST_ALLOCATED_ON,NEW.CREATED_ON,NEW.CREATED_BY,
NEW.MODIFIED_ON,NEW.MODIFIED_BY,'U',SYSDATE,NEW.TRANSFER_ID,NEW.PROFILE_ID,NEW.ACCUMULATED_POINTS,NEW.VERSION);

ELSIF DELETING THEN
INSERT INTO BONUS_HISTORY(PROFILE_TYPE,USER_ID_OR_MSISDN,POINTS,
BUCKET_CODE,PRODUCT_CODE,POINTS_DATE,LAST_REDEMPTION_ID,LAST_REDEMPTION_ON,
LAST_ALLOCATION_TYPE,LAST_ALLOCATED_ON,CREATED_ON,CREATED_BY,
MODIFIED_ON,MODIFIED_BY,OPERATION_PERFORMED,ENTRY_DATE,TRANSFER_ID,PROFILE_ID,ACCUMULATED_POINTS,VERSION)
VALUES(OLD.PROFILE_TYPE,OLD.USER_ID_OR_MSISDN,OLD.POINTS,OLD.BUCKET_CODE,
OLD.PRODUCT_CODE,OLD.POINTS_DATE,OLD.LAST_REDEMPTION_ID,OLD.LAST_REDEMPTION_ON,
OLD.LAST_ALLOCATION_TYPE,OLD.LAST_ALLOCATED_ON,OLD.CREATED_ON,OLD.CREATED_BY,
OLD.MODIFIED_ON,OLD.MODIFIED_BY,'D',SYSDATE,OLD.TRANSFER_ID,NEW.PROFILE_ID,NEW.ACCUMULATED_POINTS,NEW.VERSION);
END IF;
END;
$$;


ALTER FUNCTION public.trig_bonus_history() OWNER TO pgdb;

--
-- Name: trig_channel_users_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_channel_users_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO CHANNEL_USERS_HISTORY (USER_ID, USER_GRADE, CONTACT_PERSON, TRANSFER_PROFILE_ID, COMM_PROFILE_SET_ID,
    IN_SUSPEND, OUT_SUSPEND, OUTLET_CODE, SUBOUTLET_CODE, ACTIVATED_ON,
    APPLICATION_ID, MPAY_PROFILE_ID, USER_PROFILE_ID, IS_PRIMARY, MCOMMERCE_SERVICE_ALLOW,
    LOW_BAL_ALERT_ALLOW, MCATEGORY_CODE, ALERT_MSISDN, ALERT_TYPE, ALERT_EMAIL,
    VOMS_DECRYP_KEY, TRF_RULE_TYPE, AUTO_O2C_ALLOW, AUTO_FOC_ALLOW, LMS_PROFILE_UPDATED_ON,
    LMS_PROFILE, REF_BASED, ASSOCIATED_MSISDN, ASSOCIATED_MSISDN_TYPE, ASSOCIATED_MSISDN_CDATE,
    ASSOCIATED_MSISDN_MDATE, AUTO_C2C_ALLOW, AUTO_C2C_QUANTITY, OPT_IN_OUT_STATUS, OPT_IN_OUT_NOTIFY_DATE,
    OPT_IN_OUT_RESPONSE_DATE, CONTROL_GROUP,ENTRY_DATE,OPERATION_PERFORMED)
VALUES(NEW.USER_ID, NEW.USER_GRADE, NEW.CONTACT_PERSON, NEW.TRANSFER_PROFILE_ID, NEW.COMM_PROFILE_SET_ID,
    NEW.IN_SUSPEND, NEW.OUT_SUSPEND, NEW.OUTLET_CODE, NEW.SUBOUTLET_CODE, NEW.ACTIVATED_ON, NEW.APPLICATION_ID, NEW.MPAY_PROFILE_ID, NEW.USER_PROFILE_ID, NEW.IS_PRIMARY, NEW.MCOMMERCE_SERVICE_ALLOW, NEW.LOW_BAL_ALERT_ALLOW, NEW.MCATEGORY_CODE, NEW.ALERT_MSISDN, NEW.ALERT_TYPE, NEW.ALERT_EMAIL, NEW.VOMS_DECRYP_KEY, NEW.TRF_RULE_TYPE, NEW.AUTO_O2C_ALLOW, NEW.AUTO_FOC_ALLOW, NEW.LMS_PROFILE_UPDATED_ON, NEW.LMS_PROFILE, NEW.REF_BASED, NEW.ASSOCIATED_MSISDN, NEW.ASSOCIATED_MSISDN_TYPE, NEW.ASSOCIATED_MSISDN_CDATE, NEW.ASSOCIATED_MSISDN_MDATE, NEW.AUTO_C2C_ALLOW, NEW.AUTO_C2C_QUANTITY, NEW.OPT_IN_OUT_STATUS, NEW.OPT_IN_OUT_NOTIFY_DATE, NEW.OPT_IN_OUT_RESPONSE_DATE, NEW.CONTROL_GROUP,sysdate,'I');
ELSIF UPDATING AND (nvl(NEW.LMS_PROFILE,'XYZ') <> nvl(OLD.LMS_PROFILE,'XYZ')) then
INSERT INTO CHANNEL_USERS_HISTORY (USER_ID, USER_GRADE, CONTACT_PERSON, TRANSFER_PROFILE_ID, COMM_PROFILE_SET_ID,
    IN_SUSPEND, OUT_SUSPEND, OUTLET_CODE, SUBOUTLET_CODE, ACTIVATED_ON,
    APPLICATION_ID, MPAY_PROFILE_ID, USER_PROFILE_ID, IS_PRIMARY, MCOMMERCE_SERVICE_ALLOW,
    LOW_BAL_ALERT_ALLOW, MCATEGORY_CODE, ALERT_MSISDN, ALERT_TYPE, ALERT_EMAIL,
    VOMS_DECRYP_KEY, TRF_RULE_TYPE, AUTO_O2C_ALLOW, AUTO_FOC_ALLOW, LMS_PROFILE_UPDATED_ON,
    LMS_PROFILE, REF_BASED, ASSOCIATED_MSISDN, ASSOCIATED_MSISDN_TYPE, ASSOCIATED_MSISDN_CDATE,
    ASSOCIATED_MSISDN_MDATE, AUTO_C2C_ALLOW, AUTO_C2C_QUANTITY, OPT_IN_OUT_STATUS, OPT_IN_OUT_NOTIFY_DATE,
    OPT_IN_OUT_RESPONSE_DATE, CONTROL_GROUP,ENTRY_DATE,OPERATION_PERFORMED)
VALUES(OLD.USER_ID,NVL( OLD.USER_GRADE, NEW.USER_GRADE), OLD.CONTACT_PERSON,NVL( OLD.TRANSFER_PROFILE_ID, NEW.TRANSFER_PROFILE_ID), NVL(OLD.COMM_PROFILE_SET_ID, NEW.COMM_PROFILE_SET_ID),OLD.IN_SUSPEND, OLD.OUT_SUSPEND, OLD.OUTLET_CODE, OLD.SUBOUTLET_CODE, OLD.ACTIVATED_ON, OLD.APPLICATION_ID, OLD.MPAY_PROFILE_ID, NVL(OLD.USER_PROFILE_ID,NEW.USER_PROFILE_ID), OLD.IS_PRIMARY, OLD.MCOMMERCE_SERVICE_ALLOW, OLD.LOW_BAL_ALERT_ALLOW, OLD.MCATEGORY_CODE, OLD.ALERT_MSISDN, OLD.ALERT_TYPE, OLD.ALERT_EMAIL, OLD.VOMS_DECRYP_KEY, OLD.TRF_RULE_TYPE, OLD.AUTO_O2C_ALLOW, OLD.AUTO_FOC_ALLOW, NEW.LMS_PROFILE_UPDATED_ON, NEW.LMS_PROFILE, OLD.REF_BASED, OLD.ASSOCIATED_MSISDN, OLD.ASSOCIATED_MSISDN_TYPE, OLD.ASSOCIATED_MSISDN_CDATE, OLD.ASSOCIATED_MSISDN_MDATE, OLD.AUTO_C2C_ALLOW, OLD.AUTO_C2C_QUANTITY, NEW.OPT_IN_OUT_STATUS, NEW.OPT_IN_OUT_NOTIFY_DATE, NEW.OPT_IN_OUT_RESPONSE_DATE, NEW.CONTROL_GROUP,sysdate,'U');
ELSIF UPDATING AND (nvl(NEW.OPT_IN_OUT_STATUS,'XYZ') <> nvl(OLD.OPT_IN_OUT_STATUS,'XYZ')) then
INSERT INTO CHANNEL_USERS_HISTORY (USER_ID, USER_GRADE, CONTACT_PERSON, TRANSFER_PROFILE_ID, COMM_PROFILE_SET_ID,
    IN_SUSPEND, OUT_SUSPEND, OUTLET_CODE, SUBOUTLET_CODE, ACTIVATED_ON,
    APPLICATION_ID, MPAY_PROFILE_ID, USER_PROFILE_ID, IS_PRIMARY, MCOMMERCE_SERVICE_ALLOW,
    LOW_BAL_ALERT_ALLOW, MCATEGORY_CODE, ALERT_MSISDN, ALERT_TYPE, ALERT_EMAIL,
    VOMS_DECRYP_KEY, TRF_RULE_TYPE, AUTO_O2C_ALLOW, AUTO_FOC_ALLOW, LMS_PROFILE_UPDATED_ON,
    LMS_PROFILE, REF_BASED, ASSOCIATED_MSISDN, ASSOCIATED_MSISDN_TYPE, ASSOCIATED_MSISDN_CDATE,
    ASSOCIATED_MSISDN_MDATE, AUTO_C2C_ALLOW, AUTO_C2C_QUANTITY, OPT_IN_OUT_STATUS, OPT_IN_OUT_NOTIFY_DATE,
    OPT_IN_OUT_RESPONSE_DATE, CONTROL_GROUP,ENTRY_DATE,OPERATION_PERFORMED)
VALUES(OLD.USER_ID,NVL( OLD.USER_GRADE, NEW.USER_GRADE), OLD.CONTACT_PERSON,NVL( OLD.TRANSFER_PROFILE_ID, NEW.TRANSFER_PROFILE_ID), NVL(OLD.COMM_PROFILE_SET_ID, NEW.COMM_PROFILE_SET_ID),OLD.IN_SUSPEND, OLD.OUT_SUSPEND, OLD.OUTLET_CODE, OLD.SUBOUTLET_CODE, OLD.ACTIVATED_ON, OLD.APPLICATION_ID, OLD.MPAY_PROFILE_ID, NVL(OLD.USER_PROFILE_ID,NEW.USER_PROFILE_ID), OLD.IS_PRIMARY, OLD.MCOMMERCE_SERVICE_ALLOW, OLD.LOW_BAL_ALERT_ALLOW, OLD.MCATEGORY_CODE, OLD.ALERT_MSISDN, OLD.ALERT_TYPE, OLD.ALERT_EMAIL, OLD.VOMS_DECRYP_KEY, OLD.TRF_RULE_TYPE, OLD.AUTO_O2C_ALLOW, OLD.AUTO_FOC_ALLOW, NEW.LMS_PROFILE_UPDATED_ON, NEW.LMS_PROFILE, OLD.REF_BASED, OLD.ASSOCIATED_MSISDN, OLD.ASSOCIATED_MSISDN_TYPE, OLD.ASSOCIATED_MSISDN_CDATE, OLD.ASSOCIATED_MSISDN_MDATE, OLD.AUTO_C2C_ALLOW, OLD.AUTO_C2C_QUANTITY, NEW.OPT_IN_OUT_STATUS, NEW.OPT_IN_OUT_NOTIFY_DATE, NEW.OPT_IN_OUT_RESPONSE_DATE, NEW.CONTROL_GROUP,sysdate,'U');
ELSIF UPDATING AND (nvl(NEW.CONTROL_GROUP,'XYZ') <> nvl(OLD.CONTROL_GROUP,'XYZ')) then
INSERT INTO CHANNEL_USERS_HISTORY (USER_ID, USER_GRADE, CONTACT_PERSON, TRANSFER_PROFILE_ID, COMM_PROFILE_SET_ID,
    IN_SUSPEND, OUT_SUSPEND, OUTLET_CODE, SUBOUTLET_CODE, ACTIVATED_ON,
    APPLICATION_ID, MPAY_PROFILE_ID, USER_PROFILE_ID, IS_PRIMARY, MCOMMERCE_SERVICE_ALLOW,
    LOW_BAL_ALERT_ALLOW, MCATEGORY_CODE, ALERT_MSISDN, ALERT_TYPE, ALERT_EMAIL,
    VOMS_DECRYP_KEY, TRF_RULE_TYPE, AUTO_O2C_ALLOW, AUTO_FOC_ALLOW, LMS_PROFILE_UPDATED_ON,
    LMS_PROFILE, REF_BASED, ASSOCIATED_MSISDN, ASSOCIATED_MSISDN_TYPE, ASSOCIATED_MSISDN_CDATE,
    ASSOCIATED_MSISDN_MDATE, AUTO_C2C_ALLOW, AUTO_C2C_QUANTITY, OPT_IN_OUT_STATUS, OPT_IN_OUT_NOTIFY_DATE,
    OPT_IN_OUT_RESPONSE_DATE, CONTROL_GROUP,ENTRY_DATE,OPERATION_PERFORMED)
VALUES(OLD.USER_ID,NVL( OLD.USER_GRADE, NEW.USER_GRADE), OLD.CONTACT_PERSON,NVL( OLD.TRANSFER_PROFILE_ID, NEW.TRANSFER_PROFILE_ID), NVL(OLD.COMM_PROFILE_SET_ID, NEW.COMM_PROFILE_SET_ID),OLD.IN_SUSPEND, OLD.OUT_SUSPEND, OLD.OUTLET_CODE, OLD.SUBOUTLET_CODE, OLD.ACTIVATED_ON, OLD.APPLICATION_ID, OLD.MPAY_PROFILE_ID, NVL(OLD.USER_PROFILE_ID,NEW.USER_PROFILE_ID), OLD.IS_PRIMARY, OLD.MCOMMERCE_SERVICE_ALLOW, OLD.LOW_BAL_ALERT_ALLOW, OLD.MCATEGORY_CODE, OLD.ALERT_MSISDN, OLD.ALERT_TYPE, OLD.ALERT_EMAIL, OLD.VOMS_DECRYP_KEY, OLD.TRF_RULE_TYPE, OLD.AUTO_O2C_ALLOW, OLD.AUTO_FOC_ALLOW, NEW.LMS_PROFILE_UPDATED_ON, NEW.LMS_PROFILE, OLD.REF_BASED, OLD.ASSOCIATED_MSISDN, OLD.ASSOCIATED_MSISDN_TYPE, OLD.ASSOCIATED_MSISDN_CDATE, OLD.ASSOCIATED_MSISDN_MDATE, OLD.AUTO_C2C_ALLOW, OLD.AUTO_C2C_QUANTITY, NEW.OPT_IN_OUT_STATUS, NEW.OPT_IN_OUT_NOTIFY_DATE, NEW.OPT_IN_OUT_RESPONSE_DATE, NEW.CONTROL_GROUP,sysdate,'U');
ELSIF DELETING THEN
INSERT INTO CHANNEL_USERS_HISTORY (USER_ID, USER_GRADE, CONTACT_PERSON, TRANSFER_PROFILE_ID, COMM_PROFILE_SET_ID,
    IN_SUSPEND, OUT_SUSPEND, OUTLET_CODE, SUBOUTLET_CODE, ACTIVATED_ON,
    APPLICATION_ID, MPAY_PROFILE_ID, USER_PROFILE_ID, IS_PRIMARY, MCOMMERCE_SERVICE_ALLOW,
    LOW_BAL_ALERT_ALLOW, MCATEGORY_CODE, ALERT_MSISDN, ALERT_TYPE, ALERT_EMAIL,
    VOMS_DECRYP_KEY, TRF_RULE_TYPE, AUTO_O2C_ALLOW, AUTO_FOC_ALLOW, LMS_PROFILE_UPDATED_ON,
    LMS_PROFILE, REF_BASED, ASSOCIATED_MSISDN, ASSOCIATED_MSISDN_TYPE, ASSOCIATED_MSISDN_CDATE,
    ASSOCIATED_MSISDN_MDATE, AUTO_C2C_ALLOW, AUTO_C2C_QUANTITY, OPT_IN_OUT_STATUS, OPT_IN_OUT_NOTIFY_DATE,
    OPT_IN_OUT_RESPONSE_DATE, CONTROL_GROUP,ENTRY_DATE,OPERATION_PERFORMED)
VALUES(OLD.USER_ID, OLD.USER_GRADE, OLD.CONTACT_PERSON, OLD.TRANSFER_PROFILE_ID, OLD.COMM_PROFILE_SET_ID,OLD.IN_SUSPEND, OLD.OUT_SUSPEND, OLD.OUTLET_CODE, OLD.SUBOUTLET_CODE, OLD.ACTIVATED_ON, OLD.APPLICATION_ID, OLD.MPAY_PROFILE_ID, OLD.USER_PROFILE_ID, OLD.IS_PRIMARY, OLD.MCOMMERCE_SERVICE_ALLOW, OLD.LOW_BAL_ALERT_ALLOW, OLD.MCATEGORY_CODE, OLD.ALERT_MSISDN, OLD.ALERT_TYPE, OLD.ALERT_EMAIL, OLD.VOMS_DECRYP_KEY, OLD.TRF_RULE_TYPE, OLD.AUTO_O2C_ALLOW, OLD.AUTO_FOC_ALLOW, OLD.LMS_PROFILE_UPDATED_ON, OLD.LMS_PROFILE, OLD.REF_BASED, OLD.ASSOCIATED_MSISDN, OLD.ASSOCIATED_MSISDN_TYPE, OLD.ASSOCIATED_MSISDN_CDATE, OLD.ASSOCIATED_MSISDN_MDATE, OLD.AUTO_C2C_ALLOW, OLD.AUTO_C2C_QUANTITY, OLD.OPT_IN_OUT_STATUS, OLD.OPT_IN_OUT_NOTIFY_DATE, OLD.OPT_IN_OUT_RESPONSE_DATE, OLD.CONTROL_GROUP,sysdate,'D');
END IF;
END;
$$;


ALTER FUNCTION public.trig_channel_users_history() OWNER TO pgdb;

--
-- Name: trig_chnl_trf_rules_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_chnl_trf_rules_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO CHNL_TRANSFER_RULES_HISTORY(transfer_rule_id, domain_code,
network_code, from_category, to_category, transfer_chnl_bypass_allowed,
withdraw_allowed, withdraw_chnl_bypass_allowed, return_allowed,
return_chnl_bypass_allowed, approval_required, first_approval_limit,
second_approval_limit, created_by, created_on, modified_by, modified_on,
status, entry_date, operation_performed, transfer_type, parent_association_allowed,
direct_transfer_allowed, transfer_allowed, foc_transfer_type, foc_allowed,
type, uncntrl_transfer_allowed,restricted_msisdn_access,
to_domain_code, uncntrl_transfer_level, cntrl_transfer_level,
fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed,
uncntrl_return_level, cntrl_return_level, fixed_return_level,
fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level,
cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category,parent_assocation_allowed,previous_status,direct_payout_allowed)
VALUES(NEW.transfer_rule_id, NEW.domain_code,
NEW.network_code, NEW.from_category, NEW.to_category, NEW.transfer_chnl_bypass_allowed,
NEW.withdraw_allowed, NEW.withdraw_chnl_bypass_allowed, NEW.return_allowed,
NEW.return_chnl_bypass_allowed, NEW.approval_required, NEW.first_approval_limit,
NEW.second_approval_limit, NEW.created_by, NEW.created_on, NEW.modified_by, NEW.modified_on,
NEW.status, sysdate, 'I', NEW.transfer_type, NEW.parent_association_allowed,
NEW.direct_transfer_allowed, NEW.transfer_allowed, NEW.foc_transfer_type, NEW.foc_allowed,
NEW.type, NEW.uncntrl_transfer_allowed,NEW.restricted_msisdn_access,
NEW.to_domain_code, NEW.uncntrl_transfer_level, NEW.cntrl_transfer_level,
NEW.fixed_transfer_level, NEW.fixed_transfer_category, NEW.uncntrl_return_allowed,
NEW.uncntrl_return_level, NEW.cntrl_return_level, NEW.fixed_return_level,
NEW.fixed_return_category, NEW.uncntrl_withdraw_allowed, NEW.uncntrl_withdraw_level,
NEW.cntrl_withdraw_level, NEW.fixed_withdraw_level, NEW.fixed_withdraw_category,NEW.parent_assocation_allowed,NEW.previous_status,NEW.direct_payout_allowed);
ELSIF UPDATING THEN
INSERT INTO CHNL_TRANSFER_RULES_HISTORY(transfer_rule_id, domain_code,
network_code, from_category, to_category, transfer_chnl_bypass_allowed,
withdraw_allowed, withdraw_chnl_bypass_allowed, return_allowed,
return_chnl_bypass_allowed, approval_required, first_approval_limit,
second_approval_limit, created_by, created_on, modified_by, modified_on,
status, entry_date, operation_performed, transfer_type, parent_association_allowed,
direct_transfer_allowed, transfer_allowed, foc_transfer_type, foc_allowed,
type, uncntrl_transfer_allowed,restricted_msisdn_access,
to_domain_code, uncntrl_transfer_level, cntrl_transfer_level,
fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed,
uncntrl_return_level, cntrl_return_level, fixed_return_level,
fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level,
cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category,parent_assocation_allowed,previous_status,direct_payout_allowed)
VALUES(NEW.transfer_rule_id, NEW.domain_code,
NEW.network_code, NEW.from_category, NEW.to_category, NEW.transfer_chnl_bypass_allowed,
NEW.withdraw_allowed, NEW.withdraw_chnl_bypass_allowed, NEW.return_allowed,
NEW.return_chnl_bypass_allowed, NEW.approval_required, NEW.first_approval_limit,
NEW.second_approval_limit, NEW.created_by, NEW.created_on, NEW.modified_by, NEW.modified_on,
NEW.status, sysdate, 'U', NEW.transfer_type, NEW.parent_association_allowed,
NEW.direct_transfer_allowed, NEW.transfer_allowed, NEW.foc_transfer_type, NEW.foc_allowed,
NEW.type, NEW.uncntrl_transfer_allowed,NEW.restricted_msisdn_access,
NEW.to_domain_code, NEW.uncntrl_transfer_level, NEW.cntrl_transfer_level,
NEW.fixed_transfer_level, NEW.fixed_transfer_category, NEW.uncntrl_return_allowed,
NEW.uncntrl_return_level, NEW.cntrl_return_level, NEW.fixed_return_level,
NEW.fixed_return_category, NEW.uncntrl_withdraw_allowed, NEW.uncntrl_withdraw_level,
NEW.cntrl_withdraw_level, NEW.fixed_withdraw_level, NEW.fixed_withdraw_category,NEW.parent_assocation_allowed,NEW.previous_status,NEW.direct_payout_allowed);
ELSIF DELETING THEN
INSERT INTO CHNL_TRANSFER_RULES_HISTORY(transfer_rule_id, domain_code,
network_code, from_category, to_category, transfer_chnl_bypass_allowed,
withdraw_allowed, withdraw_chnl_bypass_allowed, return_allowed,
return_chnl_bypass_allowed, approval_required, first_approval_limit,
second_approval_limit, created_by, created_on, modified_by, modified_on,
status, entry_date, operation_performed, transfer_type, parent_association_allowed,
direct_transfer_allowed, transfer_allowed, foc_transfer_type, foc_allowed,
type, uncntrl_transfer_allowed,restricted_msisdn_access,
to_domain_code, uncntrl_transfer_level, cntrl_transfer_level,
fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed,
uncntrl_return_level, cntrl_return_level, fixed_return_level,
fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level,
cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category,parent_assocation_allowed,previous_status,direct_payout_allowed)
VALUES(OLD.transfer_rule_id, OLD.domain_code,
OLD.network_code, OLD.from_category, OLD.to_category, OLD.transfer_chnl_bypass_allowed,
OLD.withdraw_allowed, OLD.withdraw_chnl_bypass_allowed, OLD.return_allowed,
OLD.return_chnl_bypass_allowed, OLD.approval_required, OLD.first_approval_limit,
OLD.second_approval_limit, OLD.created_by, OLD.created_on, OLD.modified_by, OLD.modified_on,
OLD.status, sysdate, 'D', OLD.transfer_type, OLD.parent_association_allowed,
OLD.direct_transfer_allowed, OLD.transfer_allowed, OLD.foc_transfer_type, OLD.foc_allowed,
OLD.type, OLD.uncntrl_transfer_allowed,OLD.restricted_msisdn_access,
OLD.to_domain_code, OLD.uncntrl_transfer_level, OLD.cntrl_transfer_level,
OLD.fixed_transfer_level, OLD.fixed_transfer_category, OLD.uncntrl_return_allowed,
OLD.uncntrl_return_level, OLD.cntrl_return_level, OLD.fixed_return_level,
OLD.fixed_return_category, OLD.uncntrl_withdraw_allowed, OLD.uncntrl_withdraw_level,
OLD.cntrl_withdraw_level, OLD.fixed_withdraw_level, OLD.fixed_withdraw_category,OLD.parent_assocation_allowed,NEW.previous_status,NEW.direct_payout_allowed);
END IF;
END;
$$;


ALTER FUNCTION public.trig_chnl_trf_rules_history() OWNER TO pgdb;

--
-- Name: trig_control_prf_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_control_prf_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO CONTROL_PRF_HISTORY(control_code, network_code,
preference_code, value, created_on, created_by, modified_on,
modified_by, entry_date, operation_performed,type)
VALUES(OLD.control_code, OLD.network_code,
OLD.preference_code, OLD.value,OLD.created_on, OLD.created_by, OLD.modified_on,
OLD.modified_by,sysdate,'I',OLD.type);
ELSIF UPDATING THEN
INSERT INTO CONTROL_PRF_HISTORY(control_code, network_code,
preference_code, value, created_on, created_by, modified_on,
modified_by, entry_date, operation_performed,type)
VALUES(OLD.control_code, OLD.network_code,
OLD.preference_code, OLD.value,OLD.created_on, OLD.created_by, OLD.modified_on,
OLD.modified_by,sysdate,'U',OLD.type);
ELSIF DELETING THEN
INSERT INTO CONTROL_PRF_HISTORY(control_code, network_code,
preference_code, value, created_on, created_by, modified_on,
modified_by, entry_date, operation_performed,type)
VALUES(OLD.control_code, OLD.network_code,
OLD.preference_code, OLD.value,OLD.created_on, OLD.created_by, OLD.modified_on,
OLD.modified_by,sysdate,'D',OLD.type);
END IF;


END;
$$;


ALTER FUNCTION public.trig_control_prf_history() OWNER TO pgdb;

--
-- Name: trig_network_prf_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_network_prf_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO NETWORK_PRF_HISTORY(network_code, preference_code,
value, created_on, created_by, modified_on, modified_by,
entry_date, operation_performed)
VALUES(NEW.network_code, NEW.preference_code,
NEW.value, NEW.created_on, NEW.created_by, NEW.modified_on, NEW.modified_by,sysdate,'I');
ELSIF UPDATING THEN
INSERT INTO NETWORK_PRF_HISTORY(network_code, preference_code,
value, created_on, created_by, modified_on, modified_by,
entry_date, operation_performed)
VALUES(NEW.network_code, NEW.preference_code,
NEW.value, NEW.created_on, NEW.created_by, NEW.modified_on, NEW.modified_by,sysdate,'U');
ELSIF UPDATING THEN
INSERT INTO NETWORK_PRF_HISTORY(network_code, preference_code,
value, created_on, created_by, modified_on, modified_by,
entry_date, operation_performed)
VALUES(OLD.network_code, OLD.preference_code,
OLD.value, OLD.created_on, OLD.created_by, OLD.modified_on, OLD.modified_by,sysdate,'D');
END IF;


END;
$$;


ALTER FUNCTION public.trig_network_prf_history() OWNER TO pgdb;

--
-- Name: trig_network_stocks_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_network_stocks_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO NETWORK_STOCKS_HISTORY (network_code,network_code_for,product_code,wallet_type,
wallet_created,wallet_returned,wallet_balance,wallet_sold,last_txn_no,last_txn_type,
last_txn_balance,previous_balance,modified_by,modified_on,created_on,created_by,entry_date,operation_performed,daily_stock_updated_on)
VALUES (NEW.network_code,NEW.network_code_for,NEW.product_code,NEW.wallet_type,
NEW.wallet_created,NEW.wallet_returned,NEW.wallet_balance,NEW.wallet_sold,NEW.last_txn_no,NEW.last_txn_type,
NEW.last_txn_balance,NEW.previous_balance,NEW.modified_by,NEW.modified_on,NEW.created_on,NEW.created_by,sysdate,'I',sysdate);
ELSIF UPDATING THEN
INSERT INTO NETWORK_STOCKS_HISTORY (network_code,network_code_for,product_code,wallet_type,
wallet_created,wallet_returned,wallet_balance,wallet_sold,last_txn_no,last_txn_type,
last_txn_balance,previous_balance,modified_by,modified_on,created_on,created_by,entry_date,operation_performed,daily_stock_updated_on)
VALUES (NEW.network_code,NEW.network_code_for,NEW.product_code,NEW.wallet_type,
NEW.wallet_created,NEW.wallet_returned,NEW.wallet_balance,NEW.wallet_sold,
NEW.last_txn_no,NEW.last_txn_type,NEW.last_txn_balance,OLD.wallet_balance,
NEW.modified_by,NEW.modified_on,NEW.created_on,NEW.created_by,sysdate,'U',NEW.daily_stock_updated_on);
ELSIF DELETING THEN
INSERT INTO NETWORK_STOCKS_HISTORY (network_code,network_code_for,product_code,wallet_type,
wallet_created,wallet_returned,wallet_balance,wallet_sold,last_txn_no,last_txn_type,
last_txn_balance,previous_balance,modified_by,modified_on,created_on,created_by,entry_date,operation_performed,daily_stock_updated_on)
VALUES (OLD.network_code,OLD.network_code_for,OLD.product_code,OLD.wallet_type,
OLD.wallet_created,OLD.wallet_returned,NEW.wallet_balance,OLD.wallet_sold,
OLD.last_txn_no,OLD.last_txn_type,OLD.last_txn_balance,OLD.wallet_balance,
OLD.modified_by,OLD.modified_on,OLD.created_on,OLD.created_by,sysdate,'D',OLD.daily_stock_updated_on);
END IF;
END;
$$;


ALTER FUNCTION public.trig_network_stocks_history() OWNER TO pgdb;

--
-- Name: trig_networks_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_networks_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO NETWORKS_HISTORY(network_code, network_name, network_short_name,
company_name, report_header_name, erp_network_code, address1, address2,
city, state, zip_code, country, network_type, status, remarks, language_1_message,
language_2_message, text_1_value, text_2_value, country_prefix_code, mis_done_date,
created_on, created_by, modified_on, modified_by, service_set_id, entry_date,
operation_performed)
VALUES (NEW.network_code,NEW.network_name,NEW.network_short_name,
NEW.company_name,NEW.report_header_name,NEW.erp_network_code,NEW.address1,NEW.address2,
NEW.city,NEW.state,NEW.zip_code, NEW.country, NEW.network_type, NEW.status, NEW.remarks, NEW.language_1_message,
NEW.language_2_message, NEW.text_1_value, NEW.text_2_value, NEW.country_prefix_code, NEW.mis_done_date,
NEW.created_on,NEW.created_by,NEW.created_on,NEW.created_by,NEW.service_set_id,sysdate,
'I');
ELSIF UPDATING THEN
INSERT INTO NETWORKS_HISTORY(network_code, network_name, network_short_name,
company_name, report_header_name, erp_network_code, address1, address2,
city, state, zip_code, country, network_type, status, remarks, language_1_message,
language_2_message, text_1_value, text_2_value, country_prefix_code, mis_done_date,
created_on, created_by, modified_on, modified_by, service_set_id, entry_date,
operation_performed)
VALUES(NEW.network_code,NEW.network_name,NEW.network_short_name,
NEW.company_name,NEW.report_header_name,NEW.erp_network_code,NEW.address1,NEW.address2,
NEW.city,NEW.state,NEW.zip_code, NEW.country, NEW.network_type, NEW.status, NEW.remarks, NEW.language_1_message,
NEW.language_2_message, NEW.text_1_value, NEW.text_2_value, NEW.country_prefix_code, NEW.mis_done_date,
NEW.created_on,NEW.created_by,NEW.modified_on,NEW.modified_by,NEW.service_set_id,sysdate,
'U');
ELSIF DELETING THEN
INSERT INTO NETWORKS_HISTORY(network_code, network_name, network_short_name,
company_name, report_header_name, erp_network_code, address1, address2,
city, state, zip_code, country, network_type, status, remarks, language_1_message,
language_2_message, text_1_value, text_2_value, country_prefix_code, mis_done_date,
created_on, created_by, modified_on, modified_by, service_set_id, entry_date,
operation_performed)
VALUES(OLD.network_code,OLD.network_name,OLD.network_short_name,
OLD.company_name,OLD.report_header_name,OLD.erp_network_code,OLD.address1,OLD.address2,
OLD.city,OLD.state,OLD.zip_code, OLD.country, OLD.network_type, OLD.status, OLD.remarks, OLD.language_1_message,
OLD.language_2_message, OLD.text_1_value, OLD.text_2_value, OLD.country_prefix_code, OLD.mis_done_date,
OLD.created_on,OLD.created_by,OLD.modified_on,OLD.modified_by,OLD.service_set_id,sysdate,
'D');
END IF;
END;
$$;


ALTER FUNCTION public.trig_networks_history() OWNER TO pgdb;

--
-- Name: trig_ota_adm_txn_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_ota_adm_txn_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO OTA_ADM_TRANSACTION_HISTORY(msisdn,transaction_id,operation,
response,created_by,created_on,lock_time,entry_date,operation_performed)
VALUES(NEW.msisdn,NEW.transaction_id,NEW.operation,NEW.response,
NEW.created_by,NEW.created_on,NEW.lock_time,sysdate,'I');
ELSIF UPDATING THEN
INSERT INTO OTA_ADM_TRANSACTION_HISTORY(msisdn,transaction_id,operation,
response,created_by,created_on,lock_time,entry_date,operation_performed)
VALUES(NEW.msisdn,NEW.transaction_id,NEW.operation,NEW.response,
NEW.created_by,NEW.created_on,NEW.lock_time,sysdate,'U');
ELSIF DELETING THEN
INSERT INTO OTA_ADM_TRANSACTION_HISTORY(msisdn,transaction_id,operation,
response,created_by,created_on,lock_time,entry_date,operation_performed)
VALUES(OLD.msisdn,OLD.transaction_id,OLD.operation,OLD.response,
OLD.created_by,OLD.created_on,OLD.lock_time,sysdate,'D');
END IF;
END;
$$;


ALTER FUNCTION public.trig_ota_adm_txn_history() OWNER TO pgdb;

--
-- Name: trig_p2p_buddies_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_p2p_buddies_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN

IF INSERTING THEN
INSERT INTO P2P_BUDDIES_HISTORY(buddy_msisdn,parent_id,buddy_seq_num,
buddy_name,status,buddy_last_transfer_id,buddy_last_transfer_on,
buddy_last_transfer_type,buddy_total_transfer,buddy_total_transfer_amt,
created_on,created_by,modified_on,modified_by,preferred_amount,
last_transfer_amount,prefix_id)
VALUES (NEW.buddy_msisdn,NEW.parent_id,NEW.buddy_seq_num,
NEW.buddy_name,NEW.status,null,null,null,null,null,
NEW.created_on,NEW.created_by,sysdate,'SYSTEM',NEW.preferred_amount,
null,NEW.prefix_id);
ELSIF UPDATING THEN
INSERT INTO P2P_BUDDIES_HISTORY(buddy_msisdn,parent_id,buddy_seq_num,
buddy_name,status,buddy_last_transfer_id,buddy_last_transfer_on,
buddy_last_transfer_type,buddy_total_transfer,buddy_total_transfer_amt,
created_on,created_by,modified_on,modified_by,preferred_amount,
last_transfer_amount,prefix_id)
VALUES (OLD.buddy_msisdn,OLD.parent_id,OLD.buddy_seq_num,
OLD.buddy_name,NEW.status,OLD.buddy_last_transfer_id,OLD.buddy_last_transfer_on,
OLD.buddy_last_transfer_type,OLD.buddy_total_transfer,OLD.buddy_total_transfer_amt,
OLD.created_on,OLD.created_by,sysdate,'SYSTEM',NEW.preferred_amount,
OLD.last_transfer_amount,OLD.prefix_id);
ELSIF DELETING THEN
INSERT INTO P2P_BUDDIES_HISTORY(buddy_msisdn,parent_id,buddy_seq_num,
buddy_name,status,buddy_last_transfer_id,buddy_last_transfer_on,
buddy_last_transfer_type,buddy_total_transfer,buddy_total_transfer_amt,
created_on,created_by,modified_on,modified_by,preferred_amount,
last_transfer_amount,prefix_id)
VALUES (OLD.buddy_msisdn,OLD.parent_id,OLD.buddy_seq_num,
OLD.buddy_name,OLD.status,OLD.buddy_last_transfer_id,OLD.buddy_last_transfer_on,
OLD.buddy_last_transfer_type,OLD.buddy_total_transfer,OLD.buddy_total_transfer_amt,
OLD.created_on,OLD.created_by,sysdate,'SYSTEM',OLD.preferred_amount,
OLD.last_transfer_amount,OLD.prefix_id);
END IF;
END;
$$;


ALTER FUNCTION public.trig_p2p_buddies_history() OWNER TO pgdb;

--
-- Name: trig_pos_key_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_pos_key_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
 IF (OLD.msisdn is not null AND OLD.modified_on is not null) THEN
      INSERT INTO POS_KEY_HISTORY (icc_id,msisdn,modified_by,modified_on,
	  created_on,created_by,network_code,sim_profile_id)
      VALUES  (OLD.icc_id,OLD.msisdn,OLD.modified_by,OLD.modified_on,
	  OLD.created_on,OLD.created_by,OLD.network_code,OLD.sim_profile_id);
END IF;
END;
$$;


ALTER FUNCTION public.trig_pos_key_history() OWNER TO pgdb;

--
-- Name: trig_post_pay_cust_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_post_pay_cust_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO POSTPAID_CUST_PAY_HISTORY
(queue_id, network_code, msisdn, account_id, 
amount, transfer_id, status, entry_date, 
description, process_id, process_date, other_info, 
service_type, entry_type, process_status, module_code, 
sender_id, created_on, source_type, interface_id, 
external_id, service_class, product_code, tax_amount, 
access_fee_amount, entry_for, bonus_amount, sender_msisdn, 
cdr_file_name, gateway_code, interface_amount, imsi, 
operation_performed, record_entry_date,receiver_msisdn,type)
VALUES(NEW.queue_id, NEW.network_code, NEW.msisdn, NEW.account_id, 
NEW.amount, NEW.transfer_id, NEW.status, NEW.entry_date, 
NEW.description, NEW.process_id, NEW.process_date, NEW.other_info, 
NEW.service_type, NEW.entry_type, NEW.process_status, NEW.module_code, 
NEW.sender_id, NEW.created_on, NEW.source_type, NEW.interface_id, 
NEW.external_id, NEW.service_class, NEW.product_code, NEW.tax_amount, 
NEW.access_fee_amount, NEW.entry_for, NEW.bonus_amount, NEW.sender_msisdn, 
NEW.cdr_file_name, NEW.gateway_code, NEW.interface_amount, NEW.imsi, 
'I',sysdate,NEW.receiver_msisdn,NEW.type );
ELSIF UPDATING THEN
INSERT INTO POSTPAID_CUST_PAY_HISTORY
(queue_id, network_code, msisdn, account_id, 
amount, transfer_id, status, entry_date, 
description, process_id, process_date, other_info, 
service_type, entry_type, process_status, module_code, 
sender_id, created_on, source_type, interface_id, 
external_id, service_class, product_code, tax_amount, 
access_fee_amount, entry_for, bonus_amount, sender_msisdn, 
cdr_file_name, gateway_code, interface_amount, imsi, 
operation_performed, record_entry_date,receiver_msisdn,type)
VALUES(NEW.queue_id, NEW.network_code, NEW.msisdn, NEW.account_id, 
NEW.amount, NEW.transfer_id, NEW.status, NEW.entry_date, 
NEW.description, NEW.process_id, NEW.process_date, NEW.other_info, 
NEW.service_type, NEW.entry_type, NEW.process_status, NEW.module_code, 
NEW.sender_id, NEW.created_on, NEW.source_type, NEW.interface_id, 
NEW.external_id, NEW.service_class, NEW.product_code, NEW.tax_amount, 
NEW.access_fee_amount, NEW.entry_for, NEW.bonus_amount, NEW.sender_msisdn, 
NEW.cdr_file_name, NEW.gateway_code, NEW.interface_amount, NEW.imsi, 
'U',sysdate,NEW.receiver_msisdn,NEW.type);
ELSIF DELETING THEN
INSERT INTO POSTPAID_CUST_PAY_HISTORY
(queue_id, network_code, msisdn, account_id, 
amount, transfer_id, status, entry_date, 
description, process_id, process_date, other_info, 
service_type, entry_type, process_status, module_code, 
sender_id, created_on, source_type, interface_id, 
external_id, service_class, product_code, tax_amount, 
access_fee_amount, entry_for, bonus_amount, sender_msisdn, 
cdr_file_name, gateway_code, interface_amount, imsi, 
operation_performed, record_entry_date,receiver_msisdn,type)
VALUES(OLD.queue_id, OLD.network_code, OLD.msisdn, OLD.account_id, 
OLD.amount, OLD.transfer_id, OLD.status, OLD.entry_date, 
OLD.description, OLD.process_id, OLD.process_date, OLD.other_info, 
OLD.service_type, OLD.entry_type, OLD.process_status, OLD.module_code, 
OLD.sender_id, OLD.created_on, OLD.source_type, OLD.interface_id, 
OLD.external_id, OLD.service_class, OLD.product_code, OLD.tax_amount, 
OLD.access_fee_amount, OLD.entry_for, OLD.bonus_amount, OLD.sender_msisdn, 
OLD.cdr_file_name, OLD.gateway_code, OLD.interface_amount, OLD.imsi, 
'D',sysdate,OLD.receiver_msisdn,OLD.type);
END IF;
END;
$$;


ALTER FUNCTION public.trig_post_pay_cust_history() OWNER TO pgdb;

--
-- Name: trig_reg_info_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_reg_info_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO REG_INFO_HISTORY(msisdn, transaction_id, operation,
created_by, created_on, entry_date, operation_performed)
VALUES (NEW.msisdn, NEW.transaction_id, NEW.operation,
NEW.created_by, NEW.created_on, sysdate, 'I');
ELSIF UPDATING THEN
INSERT INTO REG_INFO_HISTORY(msisdn, transaction_id, operation,
created_by, created_on, entry_date, operation_performed)
VALUES(NEW.msisdn, NEW.transaction_id, NEW.operation,
NEW.created_by, NEW.created_on, sysdate, 'U');
ELSIF DELETING THEN
INSERT INTO REG_INFO_HISTORY(msisdn, transaction_id, operation,
created_by, created_on, entry_date, operation_performed)
VALUES(OLD.msisdn, OLD.transaction_id, OLD.operation,
OLD.created_by, OLD.created_on, sysdate,'D');
END IF;
END;
$$;


ALTER FUNCTION public.trig_reg_info_history() OWNER TO pgdb;

--
-- Name: trig_restricted_msisdn_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_restricted_msisdn_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF UPDATING THEN
INSERT INTO RESTRICTED_MSISDNS_HISTORY (msisdn,
subscriber_id, channel_user_id, channel_user_category,
owner_id, employee_code, employee_name, network_code,
monthly_limit, min_txn_amount, max_txn_amount, total_txn_count,
total_txn_amount, black_list_status, remark, approved_by,
approved_on, associated_by, old_status, new_status, association_date, created_on,
created_by, modified_on, modified_by, entry_date,
operation_performed, language, country)
VALUES(NEW.msisdn,NEW.subscriber_id, NEW.channel_user_id, NEW.channel_user_category,
NEW.owner_id, NEW.employee_code, NEW.employee_name, NEW.network_code,
NEW.monthly_limit, NEW.min_txn_amount, NEW.max_txn_amount, NEW.total_txn_count,
NEW.total_txn_amount, NEW.black_list_status, NEW.remark, NEW.approved_by,
NEW.approved_on, NEW.associated_by, OLD.status,NEW.status, NEW.association_date, NEW.created_on,
NEW.created_by, NEW.modified_on, NEW.modified_by, sysdate,
'U', NEW.language, NEW.country);
ELSIF DELETING THEN
INSERT INTO RESTRICTED_MSISDNS_HISTORY (msisdn,
subscriber_id, channel_user_id, channel_user_category,
owner_id, employee_code, employee_name, network_code,
monthly_limit, min_txn_amount, max_txn_amount, total_txn_count,
total_txn_amount, black_list_status, remark, approved_by,
approved_on, associated_by, old_status, new_status, association_date, created_on,
created_by, modified_on, modified_by, entry_date,
operation_performed, language, country)
VALUES(OLD.msisdn,OLD.subscriber_id, OLD.channel_user_id, OLD.channel_user_category,
OLD.owner_id, OLD.employee_code, OLD.employee_name, OLD.network_code,
OLD.monthly_limit, OLD.min_txn_amount, OLD.max_txn_amount, OLD.total_txn_count,
OLD.total_txn_amount, OLD.black_list_status, OLD.remark, OLD.approved_by,
OLD.approved_on, OLD.associated_by, OLD.status, 'D', OLD.association_date, OLD.created_on,
OLD.created_by, OLD.modified_on, OLD.modified_by, sysdate,
'D', OLD.language, OLD.country);
END IF;
END;
$$;


ALTER FUNCTION public.trig_restricted_msisdn_history() OWNER TO pgdb;

--
-- Name: trig_sch_batch_detail_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_sch_batch_detail_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO SCHEDULED_BATCH_DETAIL_HISTORY (batch_id, subscriber_id,
msisdn, amount, processed_on, status, transfer_id, transfer_status,error_code,
created_on, created_by, modified_on, modified_by,entry_date,operation_performed,sub_service)
VALUES(NEW.batch_id, NEW.subscriber_id,
NEW.msisdn, NEW.amount, NEW.processed_on, NEW.status, NEW.transfer_id, NEW.transfer_status,NEW.error_code,
NEW.created_on, NEW.created_by, NEW.modified_on, NEW.modified_by,sysdate,'I',NEW.sub_service);
ELSIF UPDATING THEN
INSERT INTO SCHEDULED_BATCH_DETAIL_HISTORY (batch_id, subscriber_id,
msisdn, amount, processed_on, status, transfer_id, transfer_status,error_code,
created_on, created_by, modified_on, modified_by,entry_date,operation_performed,sub_service)
VALUES(NEW.batch_id, NEW.subscriber_id,
NEW.msisdn, NEW.amount, NEW.processed_on, NEW.status, NEW.transfer_id, NEW.transfer_status,NEW.error_code,
NEW.created_on, NEW.created_by, NEW.modified_on, NEW.modified_by,sysdate,'U',NEW.sub_service);
ELSIF DELETING THEN
INSERT INTO SCHEDULED_BATCH_DETAIL_HISTORY (batch_id, subscriber_id,
msisdn, amount, processed_on, status, transfer_id, transfer_status,error_code,
created_on, created_by, modified_on, modified_by,entry_date,operation_performed,sub_service)
VALUES(OLD.batch_id, OLD.subscriber_id,
OLD.msisdn, OLD.amount, OLD.processed_on, OLD.status, OLD.transfer_id, OLD.transfer_status,OLD.error_code,
OLD.created_on, OLD.created_by, OLD.modified_on, OLD.modified_by,sysdate,'D',OLD.sub_service);
END IF;
END;
$$;


ALTER FUNCTION public.trig_sch_batch_detail_history() OWNER TO pgdb;

--
-- Name: trig_sch_batch_master_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_sch_batch_master_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO SCHEDULED_BATCH_MASTER_HISTORY (batch_id,
status, network_code, total_count, successful_count,
upload_failed_count, process_failed_count, cancelled_count,
scheduled_date, parent_id, owner_id, parent_category,
parent_domain, SERVICE_TYPE, created_on, created_by,
modified_on, modified_by, initiated_by, entry_date,
operation_performed,ref_batch_id,active_user_id)
VALUES(NEW.batch_id,
NEW.status, NEW.network_code, NEW.total_count, NEW.successful_count,
NEW.upload_failed_count, NEW.process_failed_count, NEW.cancelled_count,
NEW.scheduled_date, NEW.parent_id, NEW.owner_id, NEW.parent_category,
NEW.parent_domain, NEW.SERVICE_TYPE, NEW.created_on, NEW.created_by,
NEW.modified_on, NEW.modified_by, NEW.initiated_by, SYSDATE, 'I',NEW.ref_batch_id,NEW.active_user_id);
ELSIF UPDATING THEN
INSERT INTO SCHEDULED_BATCH_MASTER_HISTORY (batch_id,
status, network_code, total_count, successful_count,
upload_failed_count, process_failed_count, cancelled_count,
scheduled_date, parent_id, owner_id, parent_category,
parent_domain, SERVICE_TYPE, created_on, created_by,
modified_on, modified_by, initiated_by, entry_date,
operation_performed,ref_batch_id,active_user_id)
VALUES(NEW.batch_id,
NEW.status, NEW.network_code, NEW.total_count, NEW.successful_count,
NEW.upload_failed_count, NEW.process_failed_count, NEW.cancelled_count,
NEW.scheduled_date, NEW.parent_id, NEW.owner_id, NEW.parent_category,
NEW.parent_domain, NEW.SERVICE_TYPE, NEW.created_on, NEW.created_by,
NEW.modified_on, NEW.modified_by, NEW.initiated_by, SYSDATE, 'U',NEW.ref_batch_id,NEW.active_user_id);
ELSIF DELETING THEN
INSERT INTO SCHEDULED_BATCH_MASTER_HISTORY (batch_id,
status, network_code, total_count, successful_count,
upload_failed_count, process_failed_count, cancelled_count,
scheduled_date, parent_id, owner_id, parent_category,
parent_domain, SERVICE_TYPE, created_on, created_by,
modified_on, modified_by, initiated_by, entry_date,
operation_performed,ref_batch_id,active_user_id)
VALUES(OLD.batch_id,
OLD.status, OLD.network_code, OLD.total_count, OLD.successful_count,
OLD.upload_failed_count, OLD.process_failed_count, OLD.cancelled_count,
OLD.scheduled_date, OLD.parent_id, OLD.owner_id, OLD.parent_category,
OLD.parent_domain, OLD.SERVICE_TYPE, OLD.created_on, OLD.created_by,
OLD.modified_on, OLD.modified_by, OLD.initiated_by, SYSDATE, 'D',OLD.ref_batch_id,NEW.active_user_id);
END IF;
END;
$$;


ALTER FUNCTION public.trig_sch_batch_master_history() OWNER TO pgdb;

--
-- Name: trig_service_class_prf_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_service_class_prf_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO SERVICE_CLASS_PRF_HISTORY(module, network_code,
service_class_id, preference_code, value, created_on, created_by,
modified_on, modified_by, entry_date, operation_performed)
VALUES(NEW.module, NEW.network_code,
NEW.service_class_id, NEW.preference_code, NEW.value, NEW.created_on, NEW.created_by,
NEW.modified_on, NEW.modified_by,sysdate,'I');
ELSIF UPDATING THEN
INSERT INTO SERVICE_CLASS_PRF_HISTORY(module, network_code,
service_class_id, preference_code, value, created_on, created_by,
modified_on, modified_by, entry_date, operation_performed)
VALUES(NEW.module, NEW.network_code,
NEW.service_class_id, NEW.preference_code, NEW.value, NEW.created_on, NEW.created_by,
NEW.modified_on, NEW.modified_by,sysdate,'U');
ELSIF DELETING THEN
INSERT INTO SERVICE_CLASS_PRF_HISTORY(module, network_code,
service_class_id, preference_code, value, created_on, created_by,
modified_on, modified_by, entry_date, operation_performed)
VALUES(OLD.module, OLD.network_code,
OLD.service_class_id, OLD.preference_code, OLD.value, OLD.created_on, OLD.created_by,
OLD.modified_on, OLD.modified_by,sysdate,'D');
END IF;
END;
$$;


ALTER FUNCTION public.trig_service_class_prf_history() OWNER TO pgdb;

--
-- Name: trig_subs_routing_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_subs_routing_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF UPDATING THEN
INSERT INTO SUBSCRIBER_ROUTING_HISTORY(msisdn,interface_id,subscriber_type,
external_interface_id,status,created_by,created_on,modified_by,
modified_on,text1,text2,entry_date,operation_performed)
VALUES(NEW.msisdn,NEW.interface_id,NEW.subscriber_type,NEW.external_interface_id,
NEW.status,NEW.created_by,NEW.created_on,NEW.modified_by,NEW.modified_on,
NEW.text1,NEW.text2,sysdate,'U');
ELSIF DELETING THEN
INSERT INTO SUBSCRIBER_ROUTING_HISTORY(msisdn,interface_id,subscriber_type,
external_interface_id,status,created_by,created_on,modified_by,
modified_on,text1,text2,entry_date,operation_performed)
VALUES(OLD.msisdn,OLD.interface_id,OLD.subscriber_type,OLD.external_interface_id,
OLD.status,OLD.created_by,OLD.created_on,OLD.modified_by,OLD.modified_on,
OLD.text1,OLD.text2,sysdate,'D');
END IF;
END;
$$;


ALTER FUNCTION public.trig_subs_routing_history() OWNER TO pgdb;

--
-- Name: trig_subscriber_msisdn_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_subscriber_msisdn_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
  IF INSERTING THEN
    INSERT INTO SUBSCRIBER_MSISDN_HISTORY (MSISDN,USER_SID,CREATED_ON,CREATED_BY,MODIFY_ON,MODIFY_BY,USER_NAME,REQUEST_GATEWAY_CODE,REQUEST_GATEWAY_TYPE)
	values (NEW.MSISDN,NEW.USER_SID,NEW.CREATED_ON,NEW.CREATED_BY,NEW.MODIFIED_ON,NEW.MODIFIED_BY,NEW.USER_NAME,NEW.REQUEST_GATEWAY_CODE,NEW.REQUEST_GATEWAY_TYPE) ;

  ELSE IF UPDATING THEN
    INSERT INTO SUBSCRIBER_MSISDN_HISTORY (MSISDN,USER_SID,CREATED_ON,CREATED_BY,MODIFY_ON,MODIFY_BY,USER_NAME,REQUEST_GATEWAY_CODE,REQUEST_GATEWAY_TYPE)
	values (NEW.MSISDN,NEW.USER_SID,NEW.CREATED_ON,NEW.CREATED_BY,NEW.MODIFIED_ON,NEW.MODIFIED_BY,NEW.USER_NAME,NEW.REQUEST_GATEWAY_CODE,NEW.REQUEST_GATEWAY_TYPE) ;

  ELSE IF DELETING THEN
    INSERT INTO SUBSCRIBER_MSISDN_HISTORY (MSISDN,USER_SID,CREATED_ON,CREATED_BY,MODIFY_ON,MODIFY_BY,USER_NAME,REQUEST_GATEWAY_CODE,REQUEST_GATEWAY_TYPE)
	values (OLD.MSISDN,OLD.USER_SID,OLD.CREATED_ON,OLD.CREATED_BY,OLD.MODIFIED_ON,OLD.MODIFIED_BY,OLD.USER_NAME,OLD.REQUEST_GATEWAY_CODE,OLD.REQUEST_GATEWAY_TYPE) ;
END IF;
END IF;
END IF;
END;
$$;


ALTER FUNCTION public.trig_subscriber_msisdn_history() OWNER TO pgdb;

--
-- Name: trig_system_prf_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_system_prf_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO SYSTEM_PRF_HISTORY(preference_code, name,
type, value_type, default_value, min_value, max_value,
max_size, description, modified_allowed, display, module,
remarks, created_on, created_by, modified_on, modified_by,
allowed_values, fixed_value, entry_date, operation_performed)
VALUES(NEW.preference_code, NEW.name,
NEW.type, NEW.value_type, NEW.default_value, NEW.min_value, NEW.max_value,
NEW.max_size, NEW.description, NEW.modified_allowed, NEW.display, NEW.module,
NEW.remarks, NEW.created_on, NEW.created_by, NEW.modified_on, NEW.modified_by,
NEW.allowed_values, NEW.fixed_value,sysdate,'I');
ELSIF UPDATING THEN
INSERT INTO SYSTEM_PRF_HISTORY(preference_code, name,
type, value_type, default_value, min_value, max_value,
max_size, description, modified_allowed, display, module,
remarks, created_on, created_by, modified_on, modified_by,
allowed_values, fixed_value, entry_date, operation_performed)
VALUES(NEW.preference_code, NEW.name,
NEW.type, NEW.value_type, NEW.default_value, NEW.min_value, NEW.max_value,
NEW.max_size, NEW.description, NEW.modified_allowed, NEW.display, NEW.module,
NEW.remarks, NEW.created_on, NEW.created_by, NEW.modified_on, NEW.modified_by,
NEW.allowed_values, NEW.fixed_value,sysdate,'U');
ELSIF DELETING THEN
INSERT INTO SYSTEM_PRF_HISTORY(preference_code, name,
type, value_type, default_value, min_value, max_value,
max_size, description, modified_allowed, display, module,
remarks, created_on, created_by, modified_on, modified_by,
allowed_values, fixed_value, entry_date, operation_performed)
VALUES(OLD.preference_code, OLD.name,
OLD.type, OLD.value_type, OLD.default_value, OLD.min_value, OLD.max_value,
OLD.max_size, OLD.description, OLD.modified_allowed, OLD.display, OLD.module,
OLD.remarks, OLD.created_on, OLD.created_by, OLD.modified_on, OLD.modified_by,
OLD.allowed_values, OLD.fixed_value,sysdate,'D');
END IF;
END;
$$;


ALTER FUNCTION public.trig_system_prf_history() OWNER TO pgdb;

--
-- Name: trig_test(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_test() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF NEW.APPLICATION_ID = 1 THEN
IF INSERTING THEN
INSERT INTO TEST(ROLE_CODE,PAGE_CODE,APPLICATION_ID)
VALUES(NEW.ROLE_CODE,NEW.PAGE_CODE,'2');
ELSIF UPDATING THEN
UPDATE TEST SET ROLE_CODE=NEW.ROLE_CODE, PAGE_CODE=NEW.PAGE_CODE, APPLICATION_ID='2' 
WHERE  ROLE_CODE=OLD.ROLE_CODE AND PAGE_CODE=OLD.PAGE_CODE;
END IF;
ELSIF DELETING THEN
DELETE FROM TEST WHERE  ROLE_CODE=OLD.ROLE_CODE AND PAGE_CODE=OLD.PAGE_CODE;
END IF;
END;
$$;


ALTER FUNCTION public.trig_test() OWNER TO pgdb;

--
-- Name: trig_transfer_rules_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_transfer_rules_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO TRANSFER_RULES_HISTORY(MODULE, network_code,
sender_subscriber_type, receiver_subscriber_type, sender_service_class_id,
receiver_service_class_id, created_on, created_by, modified_on, modified_by,
card_group_set_id, status, operation_performed, entry_date, sub_service,allowed_days,allowed_series,denied_series,gateway_code,category_code,grade_code)
VALUES(NEW.MODULE, NEW.network_code,NEW.sender_subscriber_type, NEW.receiver_subscriber_type,
NEW.sender_service_class_id,NEW.receiver_service_class_id,NEW.created_on,NEW.created_by,
NEW.modified_on,NEW.modified_by,NEW.card_group_set_id,NEW.status,'I',SYSDATE,NEW.sub_service,NEW.allowed_days,NEW.allowed_series,NEW.denied_series,NEW.gateway_code,NEW.category_code,NEW.grade_code);
ELSIF UPDATING THEN
INSERT INTO TRANSFER_RULES_HISTORY(MODULE, network_code,
sender_subscriber_type, receiver_subscriber_type, sender_service_class_id,
receiver_service_class_id, created_on, created_by, modified_on, modified_by,
card_group_set_id, status, operation_performed, entry_date, sub_service,allowed_days,allowed_series,denied_series,gateway_code,category_code,grade_code)
VALUES(NEW.MODULE, NEW.network_code,NEW.sender_subscriber_type, NEW.receiver_subscriber_type,
NEW.sender_service_class_id,NEW.receiver_service_class_id,NEW.created_on,NEW.created_by,
NEW.modified_on,NEW.modified_by,NEW.card_group_set_id,NEW.status,'U',SYSDATE,NEW.sub_service,NEW.allowed_days,NEW.allowed_series,NEW.denied_series,NEW.gateway_code,NEW.category_code,NEW.grade_code);
ELSIF DELETING THEN
INSERT INTO TRANSFER_RULES_HISTORY(MODULE, network_code,
sender_subscriber_type, receiver_subscriber_type, sender_service_class_id,
receiver_service_class_id, created_on, created_by, modified_on, modified_by,
card_group_set_id, status, operation_performed, entry_date, sub_service,allowed_days,allowed_series,denied_series,gateway_code,category_code,grade_code)
VALUES(OLD.MODULE, OLD.network_code,OLD.sender_subscriber_type, OLD.receiver_subscriber_type,
OLD.sender_service_class_id,OLD.receiver_service_class_id, OLD.created_on, OLD.created_by,
OLD.modified_on, OLD.modified_by,OLD.card_group_set_id, OLD.status, 'D',SYSDATE,OLD.sub_service,OLD.allowed_days,OLD.allowed_series,OLD.denied_series,OLD.gateway_code,OLD.category_code,OLD.grade_code);
END IF;
END;
$$;


ALTER FUNCTION public.trig_transfer_rules_history() OWNER TO pgdb;

--
-- Name: trig_user_balances_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_user_balances_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
BEGIN
IF INSERTING THEN
INSERT INTO USER_BALANCES_HISTORY(user_id, network_code, network_code_for,
product_code, prev_balance, balance,last_transfer_type, last_transfer_no,
last_transfer_on,operation_performed,entry_date,daily_balance_updated_on,balance_type)
VALUES(NEW.user_id,NEW.network_code,NEW.network_code_for,NEW.product_code,
0,NEW.balance,NEW.last_transfer_type, NEW.last_transfer_no,NEW.last_transfer_on,'I',sysdate,sysdate,NEW.balance_type);
ELSIF UPDATING THEN
INSERT INTO USER_BALANCES_HISTORY(user_id, network_code, network_code_for,
product_code, prev_balance, balance,last_transfer_type, last_transfer_no,
last_transfer_on,operation_performed,entry_date,daily_balance_updated_on,balance_type)
VALUES(NEW.user_id,NEW.network_code,NEW.network_code_for,NEW.product_code,
OLD.balance,NEW.balance,NEW.last_transfer_type,NEW.last_transfer_no,
NEW.last_transfer_on,'U',sysdate,NEW.daily_balance_updated_on,NEW.balance_type);
ELSIF DELETING THEN
INSERT INTO USER_BALANCES_HISTORY(user_id, network_code, network_code_for,
product_code, prev_balance, balance,last_transfer_type, last_transfer_no,
last_transfer_on, operation_performed,entry_date,daily_balance_updated_on,balance_type)
VALUES(OLD.user_id,OLD.network_code,OLD.network_code_for,OLD.product_code,
OLD.balance,NEW.balance,NEW.last_transfer_type,NEW.last_transfer_no,
NEW.last_transfer_on,'D',sysdate,OLD.daily_balance_updated_on,OLD.balance_type);
END IF;
END;
$$;


ALTER FUNCTION public.trig_user_balances_history() OWNER TO pgdb;

--
-- Name: trig_user_phones_pin_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_user_phones_pin_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$  
DECLARE

vstatus VARCHAR (2);
   vapprovaldate  TIMESTAMP(0) ;
   vapprovaltwodate  TIMESTAMP(0) ;
   vappvalue VARCHAR (10);
   vusertype VARCHAR(10);

BEGIN

	 SELECT STATUS,LEVEL1_APPROVED_ON,LEVEL2_APPROVED_ON,USER_TYPE  INTO vstatus,vapprovaldate,vapprovaltwodate,vusertype   FROM USERS WHERE USER_ID= NEW.user_id;

	 SELECT DEFAULT_VALUE  INTO vappvalue FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE='USRLEVELAPPROVAL';

   BEGIN
  IF INSERTING THEN
    IF   vstatus='Y' THEN
 	INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
	  	VALUES(NEW.user_id, NEW.msisdn, NEW.sms_pin, vusertype, NEW.phone_profile, TRUNC(NEW.modified_on), NEW.modified_on, NEW.modified_by, 'PIN');
 		END IF;
   END IF;

  IF UPDATING THEN


  	 IF NEW.SMS_PIN <> OLD.SMS_PIN AND vstatus='Y' THEN
   	  INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
	  	VALUES(NEW.user_id, NEW.msisdn, NEW.sms_pin, vusertype, NEW.phone_profile, TRUNC(NEW.modified_on), NEW.modified_on, NEW.modified_by, 'PIN');

	END IF;


	 IF  vstatus='Y' AND NEW.PIN_MODIFIED_ON = vapprovaldate AND vappvalue ='1' THEN
	   	  INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
		  	VALUES(NEW.user_id, NEW.msisdn, NEW.sms_pin, vusertype, NEW.phone_profile, TRUNC(NEW.modified_on), NEW.modified_on, NEW.modified_by, 'PIN');
	END IF;

	IF  vstatus='Y' AND NEW.PIN_MODIFIED_ON = vapprovaltwodate AND vappvalue ='2' THEN
	   	  INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid,pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
		  	VALUES(NEW.user_id, NEW.msisdn, NEW.sms_pin,vusertype, NEW.phone_profile, TRUNC(NEW.modified_on), NEW.modified_on, NEW.modified_by, 'PIN');

	END IF;


END IF;
END;
END;
$$;


ALTER FUNCTION public.trig_user_phones_pin_history() OWNER TO pgdb;

--
-- Name: trig_users_password_history(); Type: FUNCTION; Schema: public; Owner: pgdb
--

CREATE FUNCTION trig_users_password_history() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ 
DECLARE
   vappvalue VARCHAR (10);
   vpin VARCHAR (50);
   vphoneprofile VARCHAR (10);
BEGIN

     SELECT DEFAULT_VALUE  INTO vappvalue FROM SYSTEM_PREFERENCES WHERE PREFERENCE_CODE='USRLEVELAPPROVAL';


BEGIN
 IF NEW.login_id IS NOT NULL THEN --condition for web,gateway etc user
IF INSERTING THEN
   IF  (NEW.USER_TYPE='OPERATOR' OR NEW.USER_TYPE='STAFF' OR vappvalue='0') THEN
     INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
        VALUES(NEW.user_id, NEW.login_id, NEW.PASSWORD, NEW.user_type, NEW.category_code, TRUNC(NEW.modified_on), NEW.modified_on, NEW.modified_by, 'PWD');
   END IF;
ELSIF UPDATING THEN

     IF NEW.MSISDN <> OLD.MSISDN AND NEW.STATUS ='Y' AND NEW.USER_TYPE='CHANNEL' THEN

        SELECT sms_pin,PHONE_PROFILE  INTO vpin,vphoneprofile FROM USER_PHONES WHERE  user_id=OLD.user_id AND PRIMARY_NUMBER='Y';

         INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
          VALUES(NEW.user_id,OLD.MSISDN,vpin, 'CHANNEL', vphoneprofile, TRUNC(NEW.modified_on), NEW.modified_on, NEW.modified_by, 'MSN');

    END IF;

    IF (NEW.USER_TYPE='OPERATOR' OR NEW.USER_TYPE='STAFF' OR vappvalue='0') AND NEW.PASSWORD <> OLD.PASSWORD THEN
      INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
           VALUES(NEW.user_id, NEW.login_id, NEW.PASSWORD, NEW.user_type, NEW.category_code, TRUNC(NEW.modified_on), NEW.modified_on, NEW.modified_by, 'PWD');

    ELSIF NEW.USER_TYPE='CHANNEL' AND vappvalue <> '0' THEN  --change password or modify password case

        IF NEW.PASSWORD <> OLD.PASSWORD AND NEW.LEVEL1_APPROVED_BY IS NOT NULL AND (NEW.STATUS ='Y' or NEW.STATUS ='PA') THEN
          INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
              VALUES(NEW.user_id, NEW.login_id, NEW.PASSWORD, NEW.user_type, NEW.category_code, TRUNC(NEW.modified_on), NEW.modified_on, NEW.modified_by, 'PWD');

        ELSE IF NEW.PSWD_MODIFIED_ON IS NULL AND NEW.STATUS ='Y'  THEN  --first time user creation without change password
             INSERT INTO PIN_PASSWORD_HISTORY( user_id, msisdn_or_loginid, pin_or_password, user_type, user_category, modified_date, modified_on, modified_by, modification_type)
              VALUES(NEW.user_id, NEW.login_id, NEW.PASSWORD, NEW.user_type, NEW.category_code, TRUNC(NEW.modified_on), NEW.modified_on, NEW.modified_by, 'PWD');
        END IF;
        END IF;



    END IF;
END IF;
END IF;
    END;
END;
$$;


ALTER FUNCTION public.trig_users_password_history() OWNER TO pgdb;