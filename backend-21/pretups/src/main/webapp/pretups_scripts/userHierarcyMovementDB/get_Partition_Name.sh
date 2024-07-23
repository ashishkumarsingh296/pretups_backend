#/oracle/userHierarcyMovementDB/get_Partition_Name

#SCRIPT TO GET THE LATEST PARTITION NAME FROM CHANNEL_TRANSFER AND CHANNEL_TRANSFER_ITEMS

sqlplus $1/$2  <<ENDOFSQL 
create or replace function get_part_name_for_ChnlTrf(p_date in date)
  return varchar2 is
  d date;
  retp varchar2(30);
  mind date:=to_date('4444-01-01','yyyy-mm-dd');
  str varchar2(32000);
  cursor c is
  select high_value, partition_name p
  from user_tab_partitions
  where table_name='CHANNEL_TRANSFERS';
  begin
   for r in c loop
       str := r.high_value;
       execute immediate 'select '||str||' from dual' into d;     
       if p_date<d and d<mind then
          retp:=r.p;
           mind:=d;
        end if;
     end loop;
    return retp;
  end;
   / 
create or replace function get_part_name_for_ChnlTrf_itm(p_date in date)
  return varchar2 is
  d date;
  retp varchar2(30);
  mind date:=to_date('4444-01-01','yyyy-mm-dd');
  str varchar2(32000);
  cursor c is
  select high_value, partition_name p
  from user_tab_partitions
  where table_name='CHANNEL_TRANSFERS_ITEMS';
  begin
   for r in c loop
       str := r.high_value;
       execute immediate 'select '||str||' from dual' into d;
       if p_date<d and d<mind then
          retp:=r.p;
           mind:=d;
        end if;
     end loop;
    return retp;
  end;
   /

CLEAR SCREEN;
PROMPT LATEST PARTITION OF THE CHANNEL_TRANSFER TABLE:-;

select get_part_name_for_ChnlTrf(sysdate) from dual;

PROMPT LATEST PARTITION OF THE CHANNEL_TRANSFER_ITEMS:-;

select get_part_name_for_ChnlTrf_itm(sysdate) from dual;
ENDOFSQL
