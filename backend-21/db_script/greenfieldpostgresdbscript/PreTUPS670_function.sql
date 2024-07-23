--
-- Name: c2s_transfers_details(character varying); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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


ALTER FUNCTION pretupsschema1.c2s_transfers_details(aiv_date character varying) OWNER TO pgdb;

--
-- Name: c2stxnminutswise(character varying); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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


    execute  'truncate table temp_c2s_hrourly_count';

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

                                        select txt_count into v_cnt from  temp_c2s_hrourly_count where to_date(date_minute,'dd-mm-yy hh24:mi')=to_date(v_temp,'dd-mm-yy hh24:mi');

                                        exception
                                         when no_data_found
                                         then
                                         v_cnt := 0;
                    end;
                   -- execute immediate 'select txt_count from  temp_c2s_hrourly_count where to_char(date_minute,''dd-mon-yy hh24:mi'')=:1' into v_cnt using to_char(v_temp,'dd-mon-yy hh24:mi');

                    raise notice '%',v_h||','||v_m||'='||v_cnt;


              end loop;-- minute loop

        end loop; --hours loop

end;
$$;


ALTER FUNCTION pretupsschema1.c2stxnminutswise(p_date character varying) OWNER TO pgdb;

--
-- Name: dump_trans_summary(character varying); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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
		
                             
                IF NOT FOUND 
		THEN
           
              RAISE NOTICE '%; SQLSTATE1:SQL Exception in INSERT ',  SQLSTATE;
		RAISE EXCEPTION 'SQL Exception in INSERT ' ;
               END IF;
            EXCEPTION
               
               WHEN OTHERS
               THEN
              RAISE NOTICE '%; SQLSTATE2:SQL Exception in INSERT ', SQLSTATE;
              RAISE EXCEPTION 'SQL Exception in INSERT';
                             
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


ALTER FUNCTION pretupsschema1.dump_trans_summary(start_date_time character varying) OWNER TO pgdb;

--
-- Name: getaccountinformation(character varying, character varying); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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


ALTER FUNCTION pretupsschema1.getaccountinformation(p_msisdn character varying, p_transactionnumber character varying, OUT p_status character varying, OUT p_transactionnumberout character varying, OUT p_serviceclass character varying, OUT p_accountid character varying, OUT p_accountstatus character varying, OUT p_creditlimit character varying, OUT p_languageid character varying, OUT p_imsi character varying, OUT p_balance character varying) OWNER TO pgdb;

--
-- Name: iatdwhtempprc(timestamp without time zone); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
--

CREATE FUNCTION iatdwhtempprc(p_date timestamp without time zone, OUT p_iattranscnt integer, OUT p_message character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$

begin
        raise notice '%','start iat dwh proc';

        execute 'truncate table temp_iat_dwh_iattrans';


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
        c2s_iat_transfer_items iti where ct.transfer_date=p_date 
        and iti.transfer_id=ct.transfer_id
        and ct.ext_credit_intfce_type='IAT'
        order by ct.transfer_date_time) as d1;
        /* commit; */

       select max(srno) into p_iattranscnt from temp_iat_dwh_iattrans;


        raise notice '%','iat dwh proc completed';
        p_message:='success';

        exception
            when others then
                p_message:='not able to migrate data, exception occoured';
                RAISE EXCEPTION 'not able to migrate data, exception occoured';

end;
$$;


ALTER FUNCTION pretupsschema1.iatdwhtempprc(p_date timestamp without time zone, OUT p_iattranscnt integer, OUT p_message character varying) OWNER TO pgdb;

--
-- Name: insert_cat_roles(); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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
                            
                                                                fetch category_cur into v_category_code ;
                                                               exit when not found;
                                                                                           
                                                               insert into category_roles (category_code,role_code)
                                                              values (v_category_code,'BC2CINITIATE');
                                 
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


ALTER FUNCTION pretupsschema1.insert_cat_roles(OUT p_returnmessage character varying) OWNER TO pgdb;

--
-- Name: insert_dly_no_c2s_lms_smry(character varying); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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
p_count int;

 declare insert_cur cursor FOR
        select distinct cu.user_id,cu.lms_profile, ps.product_code  from channel_users cu,  users u, profile_set_version ps
        where cu.lms_profile is not null and u.user_id=cu.user_id and u.status not in ('n','c') and cu.lms_profile=ps.set_id
        EXCEPT  
        select ds.user_id,ds.lms_profile, ds.product_code from daily_c2s_lms_summary ds where ds.trans_date=to_date('08/12/16','dd/mm/yy');
    begin

      p_count:=0;
      p_trans_date:=to_date(aiv_date,'dd/mm/yy');    
      p_txn_amount:=0;
      p_txn_count:=0;
      p_accumulatepoint:=0;
      raise notice '%','i am here' ||p_trans_date ;      
      for user_records in insert_cur
             loop
                     p_user_id:=user_records.user_id;
                    p_product_code:=user_records.product_code;
                    p_lms_profile:=user_records.lms_profile;
                begin
                    p_count:=p_count+1;
                    insert into daily_c2s_lms_summary(trans_date,user_id,product_code,lms_profile,transaction_amount,transaction_count,accumulated_points) values  (p_trans_date,p_user_id,p_product_code,p_lms_profile,p_txn_amount,p_txn_count,p_accumulatepoint);

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


ALTER FUNCTION pretupsschema1.insert_dly_no_c2s_lms_smry(aiv_date character varying, OUT rtn_message character varying, OUT rtn_messageforlog character varying, OUT rtn_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: movenetworkdailystocksdata(); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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


ALTER FUNCTION pretupsschema1.movenetworkdailystocksdata() OWNER TO pgdb;

--
-- Name: movenetworkstocksdata(); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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


ALTER FUNCTION pretupsschema1.movenetworkstocksdata() OWNER TO pgdb;

--
-- Name: network_daily_closing_stock(); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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

p_stock_created network_stocks.wallet_created%type;
p_stock_returned network_stocks.wallet_returned%type;
p_stock network_stocks.wallet_balance%type;
p_stock_sold network_stocks.wallet_sold%type;
p_last_txn_no network_stocks.last_txn_no%type;
p_last_txn_type network_stocks.last_txn_type%type;
p_last_txn_stock network_stocks.last_txn_balance%type;
p_previous_stock network_stocks.previous_balance%type;
p_foc_stock_created network_stocks.wallet_created%type;
p_foc_stock_returned network_stocks.wallet_returned%type;
p_foc_stock network_stocks.wallet_balance%type;
p_foc_stock_sold network_stocks.wallet_sold%type;
p_foc_last_txn_no network_stocks.last_txn_no%type;
p_foc_last_txn_type network_stocks.last_txn_type%type;
p_foc_last_txn_stock network_stocks.last_txn_balance%type;
p_foc_previous_stock network_stocks.previous_balance%type;

p_inc_stock_created network_stocks.wallet_created%type;
p_inc_stock_returned network_stocks.wallet_returned%type;
p_inc_stock network_stocks.wallet_balance%type;
p_inc_stock_sold network_stocks.wallet_sold%type;
p_inc_last_txn_no network_stocks.last_txn_no%type;
p_inc_last_txn_type network_stocks.last_txn_type%type;
p_inc_last_txn_stock network_stocks.last_txn_balance%type;
p_inc_previous_stock network_stocks.previous_balance%type;
p_modified_by network_stocks.modified_by%type;
p_modified_on network_stocks.modified_on%type;
p_created_on network_stocks.created_on%type;
p_created_by network_stocks.created_by%type;
p_daily_stock_updated_on timestamp(0);


q_created_on timestamp(0);
daydifference int = 0;
startcount smallint;
datecounter timestamp(0);




  declare network_stock_list_cur cursor for
        select network_code,network_code_for,product_code,stock_created,
                stock_returned,stock,stock_sold,last_txn_no,last_txn_type,last_txn_stock,previous_stock,
                foc_stock_created, foc_stock_returned, foc_stock, foc_stock_sold,
                foc_last_txn_no, foc_last_txn_type, foc_last_txn_stock, foc_previous_stock,
                inc_stock_created, inc_stock_returned, inc_stock, inc_stock_sold,
                inc_last_txn_no, inc_last_txn_type, inc_last_txn_stock, inc_previous_stock,
                modified_by,modified_on,created_on,created_by,daily_stock_updated_on
                from network_stocks
                where date_trunc('day',daily_stock_updated_on::timestamp)<>date_trunc('day',current_timestamp::timestamp) for update;

begin



                        for network_stock_records in network_stock_list_cur
                         loop
                                        p_network_code:=network_stock_records.network_code;
                                        p_network_code_for:=network_stock_records.network_code_for;
                                        p_product_code:=network_stock_records.product_code;
                                        p_stock_created:=network_stock_records.stock_created;
                                        p_stock_returned:=network_stock_records.stock_returned;
                                        p_stock:=network_stock_records.stock;
                                        p_stock_sold:=network_stock_records.stock_sold;
                                        p_last_txn_no:=network_stock_records.last_txn_no;
                                        p_last_txn_type:=network_stock_records.last_txn_type;
                                        p_last_txn_stock:=network_stock_records.last_txn_stock;
                                        p_previous_stock:=network_stock_records.previous_stock;
                                        p_foc_stock_created:=network_stock_records.foc_stock_created;
                                        p_foc_stock_returned:=network_stock_records.foc_stock_returned;
                                        p_foc_stock:=network_stock_records.foc_stock;
                                        p_foc_stock_sold:=network_stock_records.foc_stock_sold;
                                        p_foc_last_txn_no:=network_stock_records.foc_last_txn_no;
                                        p_foc_last_txn_type:=network_stock_records.foc_last_txn_type;
                                        p_foc_last_txn_stock:=network_stock_records.foc_last_txn_stock;
                                        p_foc_previous_stock:=network_stock_records.foc_previous_stock;
                                        p_inc_stock_created:=network_stock_records.inc_stock_created;
                                        p_inc_stock_returned:=network_stock_records.inc_stock_returned;
                                        p_inc_stock:=network_stock_records.inc_stock;
                                        p_inc_stock_sold:=network_stock_records.inc_stock_sold;
                                        p_inc_last_txn_no:=network_stock_records.inc_last_txn_no;
                                        p_inc_last_txn_type:=network_stock_records.inc_last_txn_type;
                                        p_inc_last_txn_stock:=network_stock_records.inc_last_txn_stock;
                                        p_inc_previous_stock:=network_stock_records.inc_previous_stock;
                                        p_modified_by:=network_stock_records.modified_by;
                                        p_modified_on:=network_stock_records.modified_on;
                                        p_created_on:=network_stock_records.created_on;
                                        p_created_by:=network_stock_records.created_by;
                                        p_daily_stock_updated_on:=network_stock_records.daily_stock_updated_on;

                         begin

                                  q_created_on  :=current_timestamp;
                                  startcount := 1;
                                  datecounter:= p_daily_stock_updated_on;
                                  daydifference:= date_trunc('day',q_created_on::timestamp) - p_daily_stock_updated_on;

                                  raise notice '%',' no of daydifference::'||daydifference;


                            for xyz in startcount .. daydifference
                            loop

                                     begin

                                          insert into network_daily_stocks
                                                (stock_date,product_code,network_code,network_code_for,stock_created,stock_returned,stock,
                                                stock_sold,last_txn_no,last_txn_type,last_txn_stock,previous_stock,foc_stock_created, foc_stock_returned, foc_stock, foc_stock_sold, foc_last_txn_no, foc_last_txn_type, foc_last_txn_stock, foc_previous_stock,inc_stock_created,inc_stock_returned, inc_stock, inc_stock_sold,inc_last_txn_no, inc_last_txn_type,inc_last_txn_stock, inc_previous_stock,created_on
                                                )
                                          values(datecounter,p_product_code,p_network_code,p_network_code_for,p_stock_created,
                                          p_stock_returned,p_stock,p_stock_sold,p_last_txn_no,p_last_txn_type,p_last_txn_stock,
                                          p_previous_stock,p_foc_stock_created,p_foc_stock_returned,p_foc_stock,p_foc_stock_sold,
                                          p_foc_last_txn_no,p_foc_last_txn_type,p_foc_last_txn_stock ,p_foc_previous_stock, p_inc_stock_created,p_inc_stock_returned,p_inc_stock,p_inc_stock_sold,p_inc_last_txn_no,
                                          p_inc_last_txn_type,p_inc_last_txn_stock,p_inc_previous_stock,q_created_on);
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
                                datecounter:= datecounter+1;

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


ALTER FUNCTION pretupsschema1.network_daily_closing_stock(OUT rtn_message character varying, OUT rtn_messageforlog character varying, OUT rtn_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: p2pdwhtempprc(timestamp without time zone); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
--

CREATE FUNCTION p2pdwhtempprc(p_date timestamp without time zone, OUT p_mastercnt integer, OUT p_transcnt integer, OUT p_message character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE 

v_srno 	 INT;
v_data	VARCHAR (1000);



  DECLARE P2P_MASTER CURSOR  FOR
			SELECT PS.USER_ID||','||PS.MSISDN||','||PS.SUBSCRIBER_TYPE||','||REPLACE(LK.LOOKUP_NAME,',',' ')||','||PS.NETWORK_CODE||','||PS.LAST_TRANSFER_ON
        ||','||REPLACE(KV.VALUE,',',' ')||','||PS.TOTAL_TRANSFERS||','||PS.TOTAL_TRANSFER_AMOUNT||','||PS.CREDIT_LIMIT||','||
        PS.REGISTERED_ON||','||PS.LAST_TRANSFER_ID||','||PS.LAST_TRANSFER_MSISDN||','||PS.LANGUAGE||','||PS.COUNTRY||','||REPLACE(PS.USER_NAME,',',' ')||','
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
			
	   		RAISE NOTICE '%','Start P2P DWH PROC';
	   		
			v_srno := 0;
			v_data	:= NULL;

			execute 'drop table temp_p2p_dwh_master';
			execute 'drop table temp_p2p_dwh_trans';
	


		   OPEN P2P_MASTER;
		   LOOP
			FETCH P2P_MASTER INTO v_data;
			EXIT WHEN  NOT FOUND;
			
			v_srno := v_srno+1;
			INSERT INTO TEMP_P2P_DWH_MASTER ( SRNO, DATA )
			VALUES (v_srno, v_data);
			raise notice '%','i am here:6=';

			--IF (MOD(v_srno , 10000) = 0)
			--THEN /* COMMIT; */
			--END IF;

			END LOOP;
			CLOSE P2P_MASTER;

			p_masterCnt := v_srno;
			RAISE NOTICE '%','p_masterCnt = '||p_masterCnt;
			v_srno := 0;
			v_data	:= NULL;

		   OPEN P2P_TRANS;
		   LOOP
			FETCH P2P_TRANS INTO v_data;
			EXIT WHEN NOT FOUND;
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


ALTER FUNCTION pretupsschema1.p2pdwhtempprc(p_date timestamp without time zone, OUT p_mastercnt integer, OUT p_transcnt integer, OUT p_message character varying) OWNER TO pgdb;

--
-- Name: p_chnlserviceproc(); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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


ALTER FUNCTION pretupsschema1.p_chnlserviceproc(OUT p_returnmessage character varying) OWNER TO pgdb;

--
-- Name: p_updaterechargetinfo(character varying, timestamp without time zone); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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


ALTER FUNCTION pretupsschema1.p_updaterechargetinfo(p_batchconstat character varying, p_modifieddate timestamp without time zone, OUT p_returnmessage character varying, OUT p_message character varying, OUT p_messagetosend character varying) OWNER TO pgdb;

--
-- Name: p_updatesummaryinfo(character varying, character varying, character varying, timestamp without time zone); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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


ALTER FUNCTION pretupsschema1.p_updatesummaryinfo(p_batchconstat character varying, p_batchdastat character varying, p_batchreconcilestat character varying, p_modifieddate timestamp without time zone, OUT p_returnmessage character varying, OUT p_message character varying, OUT p_messagetosend character varying) OWNER TO pgdb;

--
-- Name: p_whitelistdatamgt(); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
--

CREATE FUNCTION p_whitelistdatamgt(OUT p_errorcode character varying, OUT p_returnmessage character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$
declare 
v_returnmessage varchar(255);
v_errorcode varchar(255);

begin

        begin
                execute 'alter table white_list rename to white_list_old';
                
        exception
        when others then
                
            raise notice '%','exception 1 '||sqlerrm;
                p_errorcode :=sqlerrm;
                p_returnmessage:='not able to rename table to back up, getting exception='||sqlerrm;
                raise exception 'not able to rename table to back up, getting exception ';
        end;
         
        begin
                
                execute 'alter table white_list_bak rename to white_list';
                
        exception
        when others then
                /* rollback; */
            raise notice '%','exception 2 '||sqlerrm;
                p_errorcode :=sqlerrm;
                p_returnmessage:='not able to rename table white_list_bak to original one white_list , getting exception='||sqlerrm;
                begin
                       
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
                 execute  'create table white_list_bak (network_code varchar(2) not null, msisdn  varchar(15) not null, account_id varchar(20) not null,  entry_date timestamp(0)  not null,  account_status varchar(20) not null,  service_class  varchar(20) not null,  credit_limit  decimal(20) not null, interface_id  varchar(15)  not null,  external_interface_code  varchar(15)    not null,   created_on  timestamp(0)   not null,  created_by varchar(20) not null, modified_on   timestamp(0) not null,  modified_by  varchar(20) not null, status    varchar(2) not null,activated_on timestamp(0) not null,activated_by  varchar(20) not null, movement_code  varchar(20) not null, language   varchar(2) not null, country  varchar(2) not null, imsi   varchar(20) ) ';

        exception
        when others then
                /* rollback; */
            raise notice '%','exception  '||sqlerrm;
                p_errorcode :=sqlerrm;
                p_returnmessage:='not able to create table white_list_bak, getting exception='||sqlerrm;
                begin
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


ALTER FUNCTION pretupsschema1.p_whitelistdatamgt(OUT p_errorcode character varying, OUT p_returnmessage character varying) OWNER TO pgdb;

--
-- Name: p_yearendprocess(); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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


ALTER FUNCTION pretupsschema1.p_yearendprocess(OUT p_returnmessage character varying) OWNER TO pgdb;

--
-- Name: rp2pdwhtempprc(timestamp without time zone); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
--

CREATE FUNCTION rp2pdwhtempprc(p_date timestamp without time zone, OUT p_mastercnt integer, OUT p_chtranscnt integer, OUT p_c2stranscnt integer, OUT p_message character varying) RETURNS record
    LANGUAGE plpgsql
    AS $$

begin
        raise notice '%','start rp2p dwh proc';

        execute immediate 'truncate table temp_rp2p_dwh_master';
        execute immediate 'truncate table temp_rp2p_dwh_chtrans';
        execute immediate 'truncate table temp_rp2p_dwh_c2strans';


    insert into temp_rp2p_dwh_master ( srno, data1 )
    select rownum,(u.user_id||','||parent_id||','||owner_id||','||user_type||','||external_code||','||msisdn
    ||','||replace(l.lookup_name,',',' ')||','||replace(login_id,',',' ')||','||u.category_code||','||cat.category_name||','||
    ug.grph_domain_code||','||replace(gd.grph_domain_name,',',' ')||','||
    replace(user_name,',',' ')||','||replace(city,',',' ')||','||replace(state,',',' ')||','||replace(country,',',' ')||',' ||',' ) as data1 from users u, categories cat,user_geographies ug,geographical_domains gd,lookups l, lookup_types lt
    where u.user_id=ug.user_id and u.category_code=cat.category_code and u.status<>'c'
    and ug.grph_domain_code=gd.grph_domain_code and l.lookup_code=u.status
    and lt.lookup_type='urtyp' and lt.lookup_type=l.lookup_type and date_trunc('day',u.created_on::TIMESTAMP)<=p_date
    and user_type='channel';
    commit;
    select max(srno) into p_mastercnt from temp_rp2p_dwh_master;



    insert into temp_rp2p_dwh_chtrans ( srno, data1 )
    select rownum,data1 from (select (ct.transfer_id||','||request_gateway_type||','||to_char(ct.transfer_date,'dd/mm/yyyy')
    ||','||to_char(ct.created_on,'dd/mm/yyyy hh12:mi:ss pm')||','||ct.network_code
    ||','||ct.transfer_type||','||ct.transfer_sub_type||','||ct.transfer_category
    ||','||ct.type||','||ct.from_user_id||','||ct.to_user_id||','||ct.msisdn||','||ct.to_msisdn
    ||','||ct.sender_category_code||','||ct.receiver_category_code||','||ct.cell_id||','||ct.switch_id
    ||','||cti.required_quantity||','||cti.required_quantity||','||cti.required_quantity
    ||','||cti.mrp||','||cti.payable_amount||','||cti.net_payable_amount||','||0
    ||','||cti.tax1_value||','||cti.tax2_value||','||cti.tax3_value||','||cti.commission_value
    ||','||','||','||ct.ext_txn_no||','||to_char(ct.ext_txn_date,'dd/mm/yyyy')||','||','||cti.product_code||','||','
    || case ct.status  when 'close' then '200' else '240' end ||','||','||','||','||','||','||','||','||','
    ||ct.transfer_initiated_by||','||ct.first_approved_by||','||ct.second_approved_by||','||ct.third_approved_by||','||',') as data1 from channel_transfers ct left join channel_transfers_items cti on cti.transfer_id=ct.transfer_id  
    where ct.status in('close','cncl') and date_trunc('day',ct.close_date::TIMESTAMP)=p_date
    order by ct.modified_on,ct.type)as d1;
    commit;
    select max(srno) into p_chtranscnt from temp_rp2p_dwh_chtrans;



    insert into temp_rp2p_dwh_c2strans ( srno, data1,transfer_status)
    select rownum,data1,transfer_status from (select (ct.transfer_id||','||request_gateway_type||','||to_char(ct.transfer_date,'dd/mm/yyyy')
    ||','||to_char(ct.transfer_date_time,'dd/mm/yyyy hh12:mi:ss pm')||','||ct.network_code||','||ct.service_type||','||','||                                                                                                                                                                                                                  'sale'||','||'c2s'||','||ct.sender_id||','||','||ct.sender_msisdn||','||ct.receiver_msisdn||','||
    ct.sender_category||','||','||ct.sender_transfer_value||','||ct.receiver_transfer_value||','||
    ct.transfer_value||','||ct.quantity||','||','||','|| ct.receiver_access_fee||','||
    ct.receiver_tax1_value||','||ct.receiver_tax2_value||','||0||','||','||ct.differential_applicable||','||
    ct.differential_given||','||','||','||','||ct.product_code||','||ct.credit_back_status||','||ct.transfer_status
    ||','||ct.receiver_bonus_value||','||ct.receiver_validity||','||ct.receiver_bonus_validity||','
    ||ct.service_class_code||','||ct.interface_id||','||ct.card_group_code||','||ct.cell_id||','||ct.switch_id
    ||','||replace(kv.value,',',' ')||','||ct.serial_number||','
    ||','||','||','||','||ct.reversal_id ||',') data1,ct.transfer_status transfer_status
    from  key_values kv right outer join c2s_transfers ct on kv.key=ct.error_code and kv.type='c2s_err_cd' where ct.transfer_date=p_date  
    order by ct.transfer_date_time)as d1;
    commit;

    select max(srno) into p_c2stranscnt from temp_rp2p_dwh_c2strans;


    raise notice '%','rp2p dwh proc completed';
    p_message:='success';

    exception

                 when others then
                        p_message:='not able to migrate data, exception occoured';
                        RAISE EXCEPTION 'not able to migrate data, exception occoured';

end;
$$;


ALTER FUNCTION pretupsschema1.rp2pdwhtempprc(p_date timestamp without time zone, OUT p_mastercnt integer, OUT p_chtranscnt integer, OUT p_c2stranscnt integer, OUT p_message character varying) OWNER TO pgdb;

--
-- Name: svc_setor_intfc_mapping_insert(); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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
                            FETCH serise_cur INTO v_service_type, v_selector_code, v_network_code, v_interface_id, v_prefix_id, v_action, v_method_type;
                            EXIT WHEN serise_cur%NOTFOUND;
                            
                            INSERT INTO SVC_SETOR_INTFC_MAPPING(SERVICE_TYPE, SELECTOR_CODE, NETWORK_CODE, INTERFACE_ID, PREFIX_ID, ACTION, METHOD_TYPE, CREATED_ON, CREATED_BY, MODIFIED_ON, MODIFIED_BY, SRV_SELECTOR_INTERFACE_ID)
                            VALUES (v_service_type,v_selector_code,v_network_code,v_interface_id, v_prefix_id, v_action, v_method_type, current_timestamp,'SYSTEM',current_timestamp,'SYSTEM',v_count);                         
                             v_count:=v_count+1;

                            END LOOP;
                            CLOSE serise_cur;
                            
                            IF NOT FOUND
                            THEN
                                RAISE NOTICE '%','SQL EXCEPTION while inserting into SVC_SETOR_INTFC_MAPPING, Error is '||SQLERRM;
                                RAISE EXCEPTION 'user % SQL EXCEPTION while inserting into SVC_SETOR_INTFC_MAPPING',p_user_id;
                            END IF;
 

            --COMMIT;
            RAISE NOTICE '%','End SVC_SETOR_INTFC_MAPPING_INSERT  v_count='||v_count;
    
END;
$$;


ALTER FUNCTION pretupsschema1.svc_setor_intfc_mapping_insert() OWNER TO pgdb;

--
-- Name: update_accpnt_dly_c2s_lms_smry(character varying); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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


ALTER FUNCTION pretupsschema1.update_accpnt_dly_c2s_lms_smry(aiv_date character varying, OUT rtn_message character varying, OUT rtn_messageforlog character varying, OUT rtn_sqlerrmsgforlog character varying) OWNER TO pgdb;

--
-- Name: update_created_date(); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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
		 FETCH VOUCHER_LIST_CUR INTO V_SERIAL_NO, V_CREATED_DATE;
		 EXIT WHEN NOT FOUND;
		 UPDATE VOMS_VOUCHERS  SET CREATED_DATE =  V_CREATED_DATE  WHERE SERIAL_NO = V_SERIAL_NO;
		 --V_MODIFY_COUNT :=     V_MODIFY_COUNT  + SQL%ROWCOUNT;
		 --IF  MOD( V_MODIFY_COUNT  ,  1000   ) = 0 THEN
		 /* COMMIT; */
		 --RAISE NOTICE '%','Committed On ' || V_MODIFY_COUNT;
		-- END IF;

		 END LOOP;
		 CLOSE VOUCHER_LIST_CUR;
		 EXCEPTION
		 WHEN OTHERS THEN
		 	  ROLLBACK;
				RAISE NOTICE '%','OTHERS ERROR in update_created_date procedure:='||SQLERRM;
				RAISE EXCEPTION 'user % RROR in update_created_date procedure',user_id;
		/* COMMIT; */
END;
$$;


ALTER FUNCTION pretupsschema1.update_created_date() OWNER TO pgdb;

--
-- Name: update_opening_closing_balance(timestamp without time zone, timestamp without time zone); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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


ALTER FUNCTION pretupsschema1.update_opening_closing_balance(p_fromdate timestamp without time zone, p_todate timestamp without time zone, OUT v_messageforlog character varying) OWNER TO pgdb;

--
-- Name: user_daily_closing_balance(); Type: FUNCTION; Schema: pretupsschema1; Owner: pgdb
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


ALTER FUNCTION pretupsschema1.user_daily_closing_balance(OUT rtn_message character varying, OUT rtn_messageforlog character varying, OUT rtn_sqlerrmsgforlog character varying) OWNER TO pgdb;


--function USERCLOSINGBALANCE 

CREATE OR REPLACE FUNCTION USERCLOSINGBALANCE (p_userId  VARCHAR,p_startDate DATE,p_endDate DATE,p_startAmt integer,p_endAmt integer)
RETURNS VARCHAR
AS $p_userCloBalDateWise$
declare 
p_userCloBalDateWise VARCHAR(4000) DEFAULT '' ; balDate DATE; balance integer ; productCode VARCHAR(10);
c_userCloBal CURSOR(p_userId VARCHAR,p_startDate DATE,p_endDate DATE,p_startAmt integer,p_endAmt integer) IS
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
$p_userCloBalDateWise$ LANGUAGE PLPGSQL;

--function GETUSERROLES used in report Internaluserrolereport.rpt
CREATE OR REPLACE FUNCTION GETUSERROLES(p_userId  varchar,p_roleName varchar) 
RETURNS VARCHAR
AS $p_userRoles$
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
         p_userRoles:=SUBSTR(p_userRoles,0,LENGTH(p_userRoles)-2);
      END IF;
      --p_userRoles:='<div align="left" style="white-space: 0; letter-spacing: 0; " >'||p_userRoles||'</div>';

      RETURN p_userRoles;
END;
$p_userRoles$ LANGUAGE PLPGSQL;

--function GETUSERROLESTYPE used in report Internaluserrolereport.rpt

CREATE OR REPLACE FUNCTION GETUSERROLESTYPE
(p_userId  VARCHAR,p_nameorcode VARCHAR) 
RETURNS VARCHAR
AS $p_userRolesType$
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
$p_userRolesType$ LANGUAGE PLPGSQL;


