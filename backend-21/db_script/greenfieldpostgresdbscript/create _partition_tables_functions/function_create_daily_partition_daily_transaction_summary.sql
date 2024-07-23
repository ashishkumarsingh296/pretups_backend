

CREATE OR REPLACE FUNCTION function_create_daily_partition_daily_transaction_summary(
    table_name character varying,
    partition_key character varying,
    table_space character varying,
    date_from character varying, 
    date_to character varying, 
    table_ext character varying)
  RETURNS void AS
$BODY$
declare

seq integer;


BEGIN
	
	EXECUTE 'CREATE TRIGGER '||table_name||'_daily_partition_trigger BEFORE INSERT  ON '||table_name||' FOR EACH ROW EXECUTE PROCEDURE Partition_table_insert_trigger_daily('||table_name||','||partition_key||');';

		EXECUTE 'CREATE TABLE ' || table_name||'_' || table_ext ||  '(LIKE ' || table_name||'   INCLUDING ALL) INHERITS('|| table_name||') tablespace '||table_space||';';

		EXECUTE 'ALTER TABLE '|| table_name||'_' || table_ext || ' ADD CONSTRAINT ' || table_name||'_' || table_ext || ' CHECK ( '||partition_key||' >=  DATE '||quote_literal(date_from)||' AND '||partition_key||' < DATE '||quote_literal(date_to)||');';

		EXECUTE 'CREATE TRIGGER '||table_name||'_updatepartition_trigger BEFORE UPDATE ON ' ||  table_name||'_' || table_ext || ' FOR EACH ROW EXECUTE PROCEDURE daily_transaction_summary_update_trigger('||table_name||'_' || table_ext ||','||table_name||','||partition_key||');';
	
	

END
$BODY$
  LANGUAGE plpgsql VOLATILE;


 select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-01', '2017-08-02', 'aug1' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-02', '2017-08-03', 'aug2' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-03', '2017-08-04', 'aug3' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-04', '2017-08-05', 'aug4' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-05', '2017-08-06', 'aug5' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-06', '2017-08-07', 'aug6' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-07', '2017-08-08', 'aug7' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-08', '2017-08-09', 'aug8' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-09', '2017-08-10', 'aug9' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-10', '2017-08-11', 'aug10' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-11', '2017-08-12', 'aug11' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-12', '2017-08-13', 'aug12' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-13', '2017-08-14', 'aug13' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-14', '2017-08-15', 'aug14' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-15', '2017-08-16', 'aug15' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-16', '2017-08-17', 'aug16' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-17', '2017-08-18', 'aug17' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-18', '2017-08-19', 'aug18' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-19', '2017-08-20', 'aug19' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-20', '2017-08-21', 'aug20' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-21', '2017-08-22', 'aug21' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-22', '2017-08-23', 'aug22' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-23', '2017-08-24', 'aug23' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-24', '2017-08-25', 'aug24' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-25', '2017-08-26', 'aug25' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-26', '2017-08-27', 'aug26' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-27', '2017-08-28', 'aug27' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-28', '2017-08-29', 'aug28' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-29', '2017-08-30', 'aug29' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-30', '2017-08-31', 'aug30' );

select function_create_daily_partition_daily_transaction_summary('daily_transaction_summary', 'trans_date', 'pg_default', '2017-08-31', '2017-09-01', 'aug31' );
  


